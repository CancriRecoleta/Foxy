package com.github.foxy.common.world;

import com.github.foxy.common.Logger;
import com.github.foxy.common.util.MemoryBuffer;
import it.unimi.dsi.fastutil.longs.Long2ShortOpenHashMap;
import org.lwjgl.system.MemoryUtil;

/**
 * Serializer / deserializer for {@link WorldSection} payloads.
 *
 * <h2>On-disk layout</h2>
 * <pre>
 *   offset  | bytes              | meaning
 *   --------+--------------------+----------------------------------------------------
 *      0    | 8                  | section key (lvl/x/y/z packed; see WorldEngine)
 *      8    | 8                  | metadata (LUT size in low 16 bits, nonEmptyChildren
 *           |                    |   in next 8 bits, format version in next 8 bits,
 *           |                    |   32 reserved bits)
 *     16    | 32^3 * 2 = 65536   | per-voxel u16 LUT index, linear y-major order
 *  65552    | LUT_size * 8       | dense LUT of unique mapping ids (longs)
 * </pre>
 *
 * <h2>Cleanroom note</h2>
 * Upstream Foxy stores voxels in a Morton (Z-order) interleaving for spatial-locality on
 * disk. That implementation relies on {@link Integer#compress}/{@link Integer#expand},
 * which are Java 19+ intrinsics and unavailable on the project's Java 17 target. The
 * cleanroom port stores in linear y-major order; on-disk size is unchanged because the
 * LUT compaction does the heavy lifting either way, but the on-disk format is therefore
 * <em>not</em> binary-compatible with upstream voxy data.
 */
public final class SaveLoadSystemV1 {
    private SaveLoadSystemV1() {}

    /** Bumped whenever the on-disk layout changes incompatibly. */
    public static final int STORAGE_VERSION = 1;

    /** Worst-case serialized size: header + per-voxel indices + at-most-one LUT entry per voxel. */
    public static final int MAX_SERIALIZED_SECTION_SIZE = 16
            + WorldSection.SECTION_VOLUME * 2
            + WorldSection.SECTION_VOLUME * 8;

    private static final ThreadLocal<Long2ShortOpenHashMap> LUT_CACHE = ThreadLocal.withInitial(() -> {
        var m = new Long2ShortOpenHashMap(1024);
        m.defaultReturnValue((short) -1);
        return m;
    });

    /**
     * Serializes {@code section} into a freshly-allocated {@link MemoryBuffer}. The caller
     * owns the returned buffer and is responsible for freeing it.
     */
    public static MemoryBuffer serialize(WorldSection section) {
        var data = section.data;
        var lut = LUT_CACHE.get();
        lut.clear();

        var buffer = new MemoryBuffer(MAX_SERIALIZED_SECTION_SIZE);
        long base = buffer.address;
        long ptr = base;

        MemoryUtil.memPutLong(ptr, section.key); ptr += 8;
        long metadataPtr = ptr; ptr += 8;

        long indexPtr = ptr; ptr += WorldSection.SECTION_VOLUME * 2L;
        long lutPtr = ptr;

        // Walk voxels in linear order, populating the LUT lazily.
        for (long block : data) {
            short mapping = lut.get(block);
            if (mapping == -1) {
                mapping = (short) lut.size();
                lut.put(block, mapping);
                MemoryUtil.memPutLong(lutPtr, block);
                lutPtr += 8;
            }
            MemoryUtil.memPutShort(indexPtr, mapping);
            indexPtr += 2;
        }
        if (lut.size() >= (1 << 16)) {
            buffer.free();
            throw new IllegalStateException("Section " + section.key + " has too many distinct mapping ids");
        }

        long metadata = 0L;
        metadata |= Integer.toUnsignedLong(lut.size()) & 0xFFFFL;          // bits 0..15
        metadata |= Byte.toUnsignedLong(section.getNonEmptyChildren()) << 16; // bits 16..23
        metadata |= ((long) STORAGE_VERSION & 0xFFL) << 24;                   // bits 24..31
        MemoryUtil.memPutLong(metadataPtr, metadata);

        long total = lutPtr - base;
        // Allocate a tight copy and free the over-sized scratch.
        var packed = new MemoryBuffer(total);
        MemoryUtil.memCopy(base, packed.address, total);
        buffer.free();
        return packed;
    }

    /**
     * Restores {@code into}'s data array from {@code payload}. Returns {@code false} if
     * the payload is malformed; the caller should then treat the section as air.
     */
    public static boolean deserialize(WorldSection into, MemoryBuffer payload) {
        if (payload.size < 16 + WorldSection.SECTION_VOLUME * 2L) {
            Logger.warn("Section payload too small (" + payload.size + " bytes)");
            return false;
        }
        long base = payload.address;
        long key = MemoryUtil.memGetLong(base);
        if (key != into.key) {
            Logger.warn("Section payload key mismatch: got " + key + " expected " + into.key);
            return false;
        }
        long metadata = MemoryUtil.memGetLong(base + 8);
        int lutSize = (int) (metadata & 0xFFFFL);
        byte nonEmptyChildren = (byte) ((metadata >>> 16) & 0xFFL);
        int version = (int) ((metadata >>> 24) & 0xFFL);
        if (version != STORAGE_VERSION) {
            Logger.warn("Unsupported section storage version " + version);
            return false;
        }
        long indexPtr = base + 16;
        long lutPtr = indexPtr + WorldSection.SECTION_VOLUME * 2L;
        long expectedSize = (lutPtr - base) + lutSize * 8L;
        if (payload.size < expectedSize) {
            Logger.warn("Section payload truncated: " + payload.size + " < " + expectedSize);
            return false;
        }

        // Materialise the LUT into a flat long[] for one-load-per-voxel.
        long[] lut = new long[lutSize];
        for (int i = 0; i < lutSize; i++) {
            lut[i] = MemoryUtil.memGetLong(lutPtr + i * 8L);
        }
        long[] dst = into._unsafeGetRawDataArray();
        for (int i = 0; i < WorldSection.SECTION_VOLUME; i++) {
            int idx = MemoryUtil.memGetShort(indexPtr + i * 2L) & 0xFFFF;
            if (idx >= lutSize) {
                Logger.warn("Section payload has out-of-range LUT index " + idx + " >= " + lutSize);
                return false;
            }
            dst[i] = lut[idx];
        }
        into.metadata = metadata;
        into._unsafeSetNonEmptyChildren(nonEmptyChildren);
        return true;
    }
}

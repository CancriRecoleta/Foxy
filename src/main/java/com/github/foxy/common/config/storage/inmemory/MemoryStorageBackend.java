package com.github.foxy.common.config.storage.inmemory;

import com.github.foxy.common.config.storage.StorageBackend;
import com.github.foxy.common.util.MemoryBuffer;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.function.LongConsumer;

/**
 * Volatile in-memory {@link StorageBackend}.
 *
 * <p>Section payloads are sharded across {@code 2^slicesBits} {@link Long2ObjectMap} buckets
 * keyed by the section id, each guarded by its own monitor; this lets concurrent
 * voxelization threads write disjoint regions without contending on a single map.</p>
 *
 * <p>Sharding key: low bits of the section id (which already encodes spatial coords) are
 * sufficient because section coordinates are dense in practice.</p>
 *
 * <p>Intended for development/import dry-runs and as the L1 cache half of a layered backend.
 * Persistent storage is the disk-backed sibling implementation (TODO).</p>
 */
public final class MemoryStorageBackend extends StorageBackend {

    private final Long2ObjectMap<MemoryBuffer>[] shards;
    private final Int2ObjectOpenHashMap<ByteBuffer> idMappings = new Int2ObjectOpenHashMap<>();

    /** 16-shard backend; reasonable default for a single-player import. */
    public MemoryStorageBackend() {
        this(4);
    }

    /** Backend with {@code 1<<slicesBits} shards. */
    @SuppressWarnings("unchecked")
    public MemoryStorageBackend(int slicesBits) {
        this.shards = new Long2ObjectMap[1 << slicesBits];
        for (int i = 0; i < this.shards.length; i++) {
            this.shards[i] = new Long2ObjectOpenHashMap<>();
        }
    }

    private Long2ObjectMap<MemoryBuffer> shardFor(long key) {
        // Mix the low and high halves so spatially-close keys land in different shards.
        long h = key ^ (key >>> 32);
        h *= 0xff51afd7ed558ccdL;
        h ^= (h >>> 33);
        return this.shards[(int) (h & (this.shards.length - 1))];
    }

    @Override
    public void iteratePositions(int level, LongConsumer callback) {
        LongConsumer filtered = callback;
        if (level != -1) {
            // Section ids encode (level, x, y, z); upper 4 bits are the level.
            final int wantLevel = level;
            filtered = key -> {
                if ((int) ((key >>> 60) & 0xF) == wantLevel) callback.accept(key);
            };
        }
        for (var shard : this.shards) {
            synchronized (shard) {
                shard.keySet().forEach(filtered);
            }
        }
    }

    @Override
    public MemoryBuffer getSectionData(long key, MemoryBuffer scratch) {
        var shard = shardFor(key);
        synchronized (shard) {
            var data = shard.get(key);
            if (data == null) return null;
            if (data.size > scratch.size) {
                throw new IllegalArgumentException("Scratch buffer too small (" + scratch.size + " < " + data.size + ")");
            }
            data.copyTo(scratch.address);
            // We can't return scratch with a smaller "size" without a sub-buffer wrapper;
            // the caller is expected to consult the returned buffer's size for the payload.
            return MemoryBuffer.wrap(scratch.address, data.size);
        }
    }

    @Override
    public void setSectionData(long key, MemoryBuffer data) {
        var shard = shardFor(key);
        var snapshot = data.copy();
        MemoryBuffer prev;
        synchronized (shard) {
            prev = shard.put(key, snapshot);
        }
        if (prev != null) prev.free();
    }

    @Override
    public void deleteSectionData(long key) {
        var shard = shardFor(key);
        MemoryBuffer prev;
        synchronized (shard) {
            prev = shard.remove(key);
        }
        if (prev != null) prev.free();
    }

    @Override
    public void putIdMapping(int id, ByteBuffer data) {
        synchronized (this.idMappings) {
            var copy = MemoryUtil.memAlloc(data.remaining());
            int origPos = data.position();
            MemoryUtil.memCopy(data, copy);
            data.position(origPos);
            var prev = this.idMappings.put(id, copy);
            if (prev != null) MemoryUtil.memFree(prev);
        }
    }

    @Override
    public Int2ObjectOpenHashMap<byte[]> getIdMappingsData() {
        var out = new Int2ObjectOpenHashMap<byte[]>();
        synchronized (this.idMappings) {
            for (var entry : this.idMappings.int2ObjectEntrySet()) {
                var src = entry.getValue();
                int origPos = src.position();
                byte[] heap = new byte[src.remaining()];
                src.get(heap);
                src.position(origPos);
                out.put(entry.getIntKey(), heap);
            }
        }
        return out;
    }

    @Override public void flush() { /* no-op */ }

    @Override
    public void close() {
        for (var shard : this.shards) {
            synchronized (shard) {
                for (var buf : shard.values()) buf.free();
                shard.clear();
            }
        }
        synchronized (this.idMappings) {
            for (var buf : this.idMappings.values()) MemoryUtil.memFree(buf);
            this.idMappings.clear();
        }
    }
}

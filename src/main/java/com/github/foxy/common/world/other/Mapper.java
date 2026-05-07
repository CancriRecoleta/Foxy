package com.github.foxy.common.world.other;

import com.github.foxy.common.Logger;
import com.github.foxy.common.config.IMappingStorage;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.lwjgl.system.MemoryUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * Bidirectional registries for {@link BlockState} &harr; {@code int} ids and biome
 * {@code ResourceLocation} &harr; {@code int} ids, plus the bit layout that packs
 * (light, blockId, biomeId) into a single {@code long} per voxel.
 *
 * <h2>Bit layout of the packed mapping id</h2>
 * <pre>
 *   bit  63                                                                 0
 *        +--------+----------+--------------------+------------------------+
 *        | light8 | biomeId9 |       blockId20    |   reserved (27 bits)   |
 *        +--------+----------+--------------------+------------------------+
 *   bits  56..63    47..55         27..46                  0..26
 * </pre>
 * The 27 low bits are reserved for renderer-side state; voxelization writes them as zero.
 *
 * <h2>Persistence</h2>
 * Every newly-allocated id is serialized through {@link IMappingStorage#putIdMapping}; the
 * registry rehydrates from {@link IMappingStorage#getIdMappingsData()} at construction time.
 * Block-state entries store the full {@code BlockState} via Mojang's codec so registry
 * renames between MC versions can be repaired by the data-fixer (see {@link StateEntry#deserialize}).
 *
 * <h2>Concurrency</h2>
 * Lookups go through a {@link ConcurrentHashMap}; only the rare allocate-new-id path takes
 * a per-namespace {@link ReentrantLock}. This keeps the hot voxelization path lock-free.
 */
public class Mapper {
    private static final int BLOCK_STATE_TYPE = 1;
    private static final int BIOME_TYPE = 2;

    /** Sentinel for "this id has never been observed". */
    public static final long UNKNOWN_MAPPING = -1L;

    /** Reserved id 0 is air. */
    public static final long AIR = 0L;

    private final IMappingStorage storage;

    private final ReentrantLock blockLock = new ReentrantLock();
    private final ConcurrentHashMap<BlockState, StateEntry> block2stateEntry = new ConcurrentHashMap<>(2048, 0.75f, 16);
    private final ObjectArrayList<StateEntry> blockId2stateEntry = new ObjectArrayList<>();

    private final ReentrantLock biomeLock = new ReentrantLock();
    private final ConcurrentHashMap<String, BiomeEntry> biome2biomeEntry = new ConcurrentHashMap<>(256, 0.75f, 16);
    private final ObjectArrayList<BiomeEntry> biomeId2biomeEntry = new ObjectArrayList<>();

    private Consumer<StateEntry> newStateCallback;
    private Consumer<BiomeEntry> newBiomeCallback;

    /** Builds a mapper backed by {@code storage}, hydrating from persisted entries. */
    public Mapper(IMappingStorage storage) {
        this.storage = storage;
        // Slot 0 is hard-coded to air so getIdForBlockState returns 0 for any air variant
        // without touching the lock or the storage backend.
        var airEntry = new StateEntry(0, Blocks.AIR.defaultBlockState());
        this.block2stateEntry.put(airEntry.state, airEntry);
        this.blockId2stateEntry.add(airEntry);
        loadFromStorage();
    }

    // ---- packed-id bit ops ---------------------------------------------------------------

    /** {@code true} when the packed id has no block content (i.e. blockId == 0 == air). */
    public static boolean isAir(long id) {
        return (id & (((1L << 20) - 1) << 27)) == 0L;
    }

    /** Extracts the 20-bit block id. */
    public static int getBlockId(long id) { return (int) ((id >> 27) & ((1L << 20) - 1)); }

    /** Extracts the 9-bit biome id. */
    public static int getBiomeId(long id) { return (int) ((id >> 47) & 0x1FF); }

    /** Extracts the 8-bit packed light value (sky/block nibbles). */
    public static int getLightId(long id) { return (int) ((id >> 56) & 0xFF); }

    /** Returns {@code id} with its light byte replaced. */
    public static long withLight(long id, int light) {
        return (id & ~(0xFFL << 56)) | (Integer.toUnsignedLong(light & 0xFF) << 56);
    }

    /** Returns {@code id} with block + biome ids replaced (preserves light). */
    public static long withBlockBiome(long id, int block, int biome) {
        return (id & (0xFFL << 56)) | (Integer.toUnsignedLong(block) << 27) | (Integer.toUnsignedLong(biome) << 47);
    }

    /** Convenience: pack (light, AIR, biome=0) into a packed id. */
    public static long airWithLight(int light) {
        return Integer.toUnsignedLong(light & 0xFF) << 56;
    }

    /** Composes (light, blockId, biomeId) into a packed mapping id. */
    public static long composeMappingId(byte light, int blockId, int biomeId) {
        if (blockId == AIR) {
            // Biome is meaningless for air; skip writing it so air voxels collapse to a
            // single value regardless of biome. Saves a few bits of storage entropy.
            return Byte.toUnsignedLong(light) << 56;
        }
        return (Byte.toUnsignedLong(light) << 56)
                | (Integer.toUnsignedLong(biomeId) << 47)
                | (Integer.toUnsignedLong(blockId) << 27);
    }

    // ---- callbacks -----------------------------------------------------------------------

    /** Called once per newly-registered block state; useful for renderer cache invalidation. */
    public void setStateCallback(Consumer<StateEntry> cb) { this.newStateCallback = cb; }

    /** Called once per newly-registered biome. */
    public void setBiomeCallback(Consumer<BiomeEntry> cb) { this.newBiomeCallback = cb; }

    // ---- registry queries ----------------------------------------------------------------

    /** Number of registered block states (including air at id 0). */
    public final int getBlockStateCount() { return this.blockId2stateEntry.size(); }

    /** Builds a packed mapping id for (light, blockState, biome). */
    public long getBaseId(byte light, BlockState state, Holder<Biome> biome) {
        if (state.isAir()) return Byte.toUnsignedLong(light) << 56;
        return composeMappingId(light, getIdForBlockState(state), getIdForBiome(biome));
    }

    /** Looks up the {@link BlockState} previously assigned id {@code blockId}. */
    public BlockState getBlockStateFromBlockId(int blockId) {
        return this.blockId2stateEntry.get(blockId).state;
    }

    /** Returns the int id for {@code state}, allocating a new one if needed. */
    public int getIdForBlockState(BlockState state) {
        if (state.isAir()) return 0;
        var entry = this.block2stateEntry.get(state);
        if (entry == null) entry = registerNewBlockState(state);
        return entry.id;
    }

    /** Returns the int id for the biome held by {@code biome}, allocating if needed. */
    public int getIdForBiome(Holder<Biome> biome) {
        // ResourceKey.location() is the 1.20.1 spelling of upstream's identifier().
        String biomeId = biome.unwrapKey().orElseThrow().location().toString();
        var entry = this.biome2biomeEntry.get(biomeId);
        if (entry == null) entry = registerNewBiome(biomeId);
        return entry.id;
    }

    /** Static opacity (light-block value) of the block state with the given id. */
    public int getBlockStateOpacity(int blockId) {
        return this.blockId2stateEntry.get(blockId).opacity;
    }

    /** Static opacity helper that takes the packed mapping id. */
    public int getBlockStateOpacity(long mappingId) {
        return getBlockStateOpacity(getBlockId(mappingId));
    }

    // ---- registry mutation (rare path) ---------------------------------------------------

    private StateEntry registerNewBlockState(BlockState state) {
        this.blockLock.lock();
        try {
            // Re-check: another thread may have raced us to the lock and registered it.
            var entry = this.block2stateEntry.get(state);
            if (entry != null) return entry;
            entry = new StateEntry(this.blockId2stateEntry.size(), state);
            this.blockId2stateEntry.add(entry);
            this.block2stateEntry.put(state, entry);
            persist(entry.id | (BLOCK_STATE_TYPE << 30), entry.serialize());
            if (this.newStateCallback != null) this.newStateCallback.accept(entry);
            return entry;
        } finally {
            this.blockLock.unlock();
        }
    }

    private BiomeEntry registerNewBiome(String biome) {
        this.biomeLock.lock();
        try {
            var entry = this.biome2biomeEntry.get(biome);
            if (entry != null) return entry;
            entry = new BiomeEntry(this.biomeId2biomeEntry.size(), biome);
            this.biomeId2biomeEntry.add(entry);
            this.biome2biomeEntry.put(biome, entry);
            persist(entry.id | (BIOME_TYPE << 30), entry.serialize());
            if (this.newBiomeCallback != null) this.newBiomeCallback.accept(entry);
            return entry;
        } finally {
            this.biomeLock.unlock();
        }
    }

    private void persist(int taggedId, byte[] payload) {
        ByteBuffer buf = MemoryUtil.memAlloc(payload.length);
        try {
            buf.put(payload);
            buf.rewind();
            this.storage.putIdMapping(taggedId, buf);
        } finally {
            MemoryUtil.memFree(buf);
        }
    }

    // ---- bulk load / resave -------------------------------------------------------------

    private void loadFromStorage() {
        var mappings = this.storage.getIdMappingsData();
        var blockEntries = new ArrayList<StateEntry>();
        var biomeEntries = new ArrayList<BiomeEntry>();
        boolean[] forceResave = new boolean[1];

        for (var entry : mappings.int2ObjectEntrySet()) {
            int kind = entry.getIntKey() >>> 30;
            int id = entry.getIntKey() & ((1 << 30) - 1);
            switch (kind) {
                case BLOCK_STATE_TYPE -> {
                    var sentry = StateEntry.deserialize(id, entry.getValue(), forceResave);
                    if (sentry.state.isAir()) {
                        Logger.error("Stored mapping " + id + " deserialized to air; dropping");
                        forceResave[0] = true;
                        continue;
                    }
                    blockEntries.add(sentry);
                    if (this.block2stateEntry.putIfAbsent(sentry.state, sentry) != null) {
                        Logger.warn("Duplicate stored mapping for block state " + sentry.state);
                    }
                }
                case BIOME_TYPE -> {
                    var bentry = BiomeEntry.deserialize(id, entry.getValue());
                    biomeEntries.add(bentry);
                    if (this.biome2biomeEntry.put(bentry.biome, bentry) != null) {
                        throw new IllegalStateException("Duplicate stored mapping for biome " + bentry.biome);
                    }
                }
                default -> throw new IllegalStateException("Unknown mapping kind " + kind);
            }
        }

        // Insert into the dense id-indexed arrays in id order so blockId2stateEntry[i].id == i.
        blockEntries.sort(Comparator.comparingInt(a -> a.id));
        for (var e : blockEntries) {
            if (this.blockId2stateEntry.size() != e.id) {
                throw new IllegalStateException("Block id " + e.id + " skipped (have " + this.blockId2stateEntry.size() + ")");
            }
            this.blockId2stateEntry.add(e);
        }
        biomeEntries.sort(Comparator.comparingInt(a -> a.id));
        for (var e : biomeEntries) {
            if (this.biomeId2biomeEntry.size() != e.id) {
                throw new IllegalStateException("Biome id " + e.id + " skipped (have " + this.biomeId2biomeEntry.size() + ")");
            }
            this.biomeId2biomeEntry.add(e);
        }

        if (forceResave[0]) {
            Logger.warn("Mapper triggered force resave (data-fixed entries)");
            forceResaveStates();
        }
    }

    /** Snapshot of the dense block-state id table; index == id. */
    public StateEntry[] getStateEntries() {
        this.blockLock.lock();
        try {
            return this.blockId2stateEntry.toArray(new StateEntry[0]);
        } finally {
            this.blockLock.unlock();
        }
    }

    /** Snapshot of the dense biome id table; index == id. */
    public BiomeEntry[] getBiomeEntries() {
        this.biomeLock.lock();
        try {
            return this.biomeId2biomeEntry.toArray(new BiomeEntry[0]);
        } finally {
            this.biomeLock.unlock();
        }
    }

    /** Re-serializes every entry to storage; used after a data-fixer-driven repair. */
    public void forceResaveStates() {
        for (var entry : new ArrayList<>(this.block2stateEntry.values())) {
            if (entry.state.isAir() && entry.id == 0) continue;
            persist(entry.id | (BLOCK_STATE_TYPE << 30), entry.serialize());
        }
        for (var entry : new ArrayList<>(this.biome2biomeEntry.values())) {
            persist(entry.id | (BIOME_TYPE << 30), entry.serialize());
        }
        this.storage.flush();
    }

    /** Releases backend resources; the storage backend itself is the caller's to close. */
    public void close() { /* no-op */ }

    // ---- serialized entry types ---------------------------------------------------------

    /** A registered (id, BlockState) pair plus its precomputed light-block opacity. */
    public static final class StateEntry {
        public final int id;
        public final BlockState state;
        public final int opacity;

        public StateEntry(int id, BlockState state) {
            this.id = id;
            this.state = state;
            // Leaves are technically translucent in vanilla but we treat them as solid for
            // the LOD mip selector, otherwise distant trees become see-through holes.
            if (state.getBlock() instanceof LeavesBlock) {
                this.opacity = 15;
            } else {
                // 1.20.1 BlockBehaviour.getLightBlock(BlockGetter, BlockPos) 鈥?call with a
                // sentinel level/pos because the implementations we care about don't read
                // either parameter.
                this.opacity = state.getLightBlock(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
            }
        }

        /** Mojang-codec-driven NBT serialization (gz-compressed). */
        public byte[] serialize() {
            try {
                var tag = new CompoundTag();
                tag.putInt("id", this.id);
                var encoded = BlockState.CODEC.encodeStart(NbtOps.INSTANCE, this.state)
                        .result()
                        .orElseThrow(() -> new IllegalStateException("BlockState codec failed for " + this.state));
                tag.put("block_state", encoded);
                var out = new ByteArrayOutputStream();
                NbtIo.writeCompressed(tag, out);
                return out.toByteArray();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * Deserialize an entry, attempting a data-fixer round-trip if the codec rejects the
         * stored payload (e.g. block was renamed in a vanilla update). Sets
         * {@code forceResave[0]} when a fix was applied so the caller can persist the
         * patched entry back to storage.
         */
        public static StateEntry deserialize(int id, byte[] payload, boolean[] forceResave) {
            try {
                var tag = NbtIo.readCompressed(new ByteArrayInputStream(payload));
                if (tag.getInt("id") != id) {
                    throw new IllegalStateException("Encoded id mismatch (" + tag.getInt("id") + " vs " + id + ")");
                }
                var stateTag = tag.getCompound("block_state");
                DataResult<BlockState> parsed = BlockState.CODEC.parse(NbtOps.INSTANCE, stateTag);
                if (parsed.error().isPresent()) {
                    Logger.info("BlockState " + id + " failed to parse, attempting datafix: "
                            + parsed.error().get().message());
                    int curVersion = SharedConstants.getCurrentVersion().getDataVersion().getVersion();
                    var fixed = (CompoundTag) DataFixers.getDataFixer().update(
                                    References.BLOCK_STATE,
                                    new Dynamic<>(NbtOps.INSTANCE, stateTag),
                                    0,
                                    curVersion)
                            .getValue();
                    parsed = BlockState.CODEC.parse(NbtOps.INSTANCE, fixed);
                    if (parsed.error().isPresent()) {
                        Logger.error("Datafix failed for BlockState " + id
                                + ", substituting air. Error: " + parsed.error().get().message());
                        return new StateEntry(id, Blocks.AIR.defaultBlockState());
                    }
                    forceResave[0] = true;
                    return new StateEntry(id, parsed.result().orElseThrow());
                }
                return new StateEntry(id, parsed.result().orElseThrow());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /** A registered (id, biomeResourceLocation) pair. */
    public static final class BiomeEntry {
        public final int id;
        public final String biome;

        public BiomeEntry(int id, String biome) {
            this.id = id;
            this.biome = biome;
        }

        /** NBT serialization of the biome id mapping (gz-compressed). */
        public byte[] serialize() {
            try {
                var tag = new CompoundTag();
                tag.putInt("id", this.id);
                tag.putString("biome_id", this.biome);
                var out = new ByteArrayOutputStream();
                NbtIo.writeCompressed(tag, out);
                return out.toByteArray();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public static BiomeEntry deserialize(int id, byte[] payload) {
            try {
                var tag = NbtIo.readCompressed(new ByteArrayInputStream(payload));
                if (tag.getInt("id") != id) {
                    throw new IllegalStateException("Encoded id mismatch (" + tag.getInt("id") + " vs " + id + ")");
                }
                return new BiomeEntry(id, tag.getString("biome_id"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}

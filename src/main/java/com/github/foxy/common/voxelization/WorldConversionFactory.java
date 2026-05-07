package com.github.foxy.common.voxelization;

import com.github.foxy.common.world.other.Mapper;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.util.SimpleBitStorage;
import net.minecraft.util.ZeroBitStorage;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.GlobalPalette;
import net.minecraft.world.level.chunk.HashMapPalette;
import net.minecraft.world.level.chunk.LinearPalette;
import net.minecraft.world.level.chunk.Palette;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.PalettedContainerRO;
import net.minecraft.world.level.chunk.SingleValuePalette;

import java.util.WeakHashMap;

/**
 * Converts a vanilla {@link PalettedContainer}{@code <BlockState>} plus its sibling biome
 * container and a per-voxel light supplier into a {@link VoxelizedSection}'s level-0 slice.
 *
 * <h2>Strategy</h2>
 * Vanilla stores each section's blocks in a {@link PalettedContainer} backed by a
 * {@link Palette} (id&rarr;state lookup) and a {@link net.minecraft.util.BitStorage}
 * (per-voxel palette index). Reading every voxel through the public
 * {@code container.get(x,y,z)} API would re-hash the BlockState 4096 times; instead we
 * walk the palette once to build a local {@code paletteIndex -> mapperBlockId} table and
 * then do a tight loop over the bit-storage longs directly.
 *
 * <p>This requires reflective access to the {@code .data} field of {@code PalettedContainer}
 * and the {@code .palette} / {@code .storage} fields of the inner {@code Data} record;
 * those are exposed via the project's access transformer
 * ({@code META-INF/accesstransformer.cfg}).</p>
 *
 * <h2>Lithium fast path</h2>
 * Upstream Voxy has a Lithium-aware branch for {@code LithiumHashPalette}. Lithium has no
 * Forge port for 1.20.1, so that branch is omitted; an unrecognised palette implementation
 * throws.
 *
 * <h2>Threading</h2>
 * Cache scratch buffers are kept in a {@link ThreadLocal} so concurrent voxelizers don't
 * fight over allocations. The per-mapper local table is a {@link WeakHashMap} keyed by
 * {@link Mapper}, so caches die naturally when a mapper is discarded.
 */
public final class WorldConversionFactory {
    private WorldConversionFactory() {}

    /** Per-thread scratch state to keep the hot path allocation-free. */
    private static final class Cache {
        final int[] biomeCache = new int[4 * 4 * 4];
        final WeakHashMap<Mapper, Reference2IntOpenHashMap<BlockState>> localMapping = new WeakHashMap<>();
        int[] paletteCache = new int[1024];

        Reference2IntOpenHashMap<BlockState> getLocalMapping(Mapper mapper) {
            return this.localMapping.computeIfAbsent(mapper, m -> new Reference2IntOpenHashMap<>());
        }

        int[] getPaletteCache(int size) {
            if (this.paletteCache.length < size) this.paletteCache = new int[size];
            return this.paletteCache;
        }
    }

    private static final ThreadLocal<Cache> THREAD_LOCAL = ThreadLocal.withInitial(Cache::new);

    /**
     * Walks the supplied palette in one pass, populating {@code paletteCache[i]} with the
     * Mapper-side block id for the BlockState at palette index {@code i}.
     *
     * <p>{@code blockStateCache} memoizes lookups across palettes belonging to the same
     * Mapper because a single import typically sees the same BlockState many times.</p>
     *
     * @return number of palette entries consumed (== {@code palette.getSize()})
     */
    private static int setupLocalPalette(Palette<BlockState> palette,
                                          Reference2IntOpenHashMap<BlockState> blockStateCache,
                                          Mapper mapper,
                                          int[] paletteCache) {
        int size = palette.getSize();
        if (palette instanceof LinearPalette<BlockState>
                || palette instanceof HashMapPalette<BlockState>
                || palette instanceof SingleValuePalette<BlockState>) {
            // All three vanilla palette kinds expose valueFor(int); SingleValuePalette only
            // serves index 0 but the loop terminates correctly because size == 1.
            for (int i = 0; i < size; i++) {
                BlockState state = null;
                int blockId = -1;
                try { state = palette.valueFor(i); }
                catch (Exception ignored) { /* corrupt palette slots fall through to air */ }
                if (state != null) {
                    blockId = blockStateCache.getOrDefault(state, -1);
                    if (blockId == -1) {
                        blockId = mapper.getIdForBlockState(state);
                        blockStateCache.put(state, blockId);
                    }
                }
                paletteCache[i] = blockId;
            }
            return size;
        }
        throw new IllegalStateException("Unknown palette implementation: " + palette.getClass().getName());
    }

    /** Convenience overload without zoom (the common case). */
    public static VoxelizedSection convert(VoxelizedSection out,
                                           Mapper mapper,
                                           PalettedContainer<BlockState> blocks,
                                           PalettedContainerRO<Holder<Biome>> biomes,
                                           ILightingSupplier light) {
        return convert(out, mapper, blocks, biomes, light, false, 0L);
    }

    /**
     * Populate {@code out}'s level-0 slice from vanilla containers and a light supplier.
     *
     * @param out          target section; level-0 slice is overwritten, mip pyramid is not
     *                     touched (call {@link WorldVoxilizedSectionMipper} after)
     * @param mapper       block-state / biome id allocator
     * @param blocks       vanilla block container for the section
     * @param biomes       vanilla biome container for the section (read-only)
     * @param light        per-voxel packed light nibble supplier
     * @param shouldZoom   if {@code true} and the section spans biome boundaries, smooth
     *                     biome ids per-voxel using the {@code zoomSeed} (unimplemented in
     *                     this port; the flag is preserved for parity with upstream)
     * @param zoomSeed     deterministic seed for the zoom function when enabled
     * @return {@code out}, with {@code lvl0NonAirCount} updated
     */
    public static VoxelizedSection convert(VoxelizedSection out,
                                           Mapper mapper,
                                           PalettedContainer<BlockState> blocks,
                                           PalettedContainerRO<Holder<Biome>> biomes,
                                           ILightingSupplier light,
                                           boolean shouldZoom,
                                           long zoomSeed) {
        var cache = THREAD_LOCAL.get();
        var blockStateCache = cache.getLocalMapping(mapper);
        var biomeIds = cache.biomeCache;
        var data = out.section;

        // Reach inside the vanilla container; .data is exposed via the project access
        // transformer. Data is a record, so palette() and storage() are public accessors.
        var palette = blocks.data.palette();
        var paletteCache = cache.getPaletteCache(palette.getSize());

        GlobalPalette<BlockState> globalPalette = null;
        int paletteCount;
        if (palette instanceof GlobalPalette<BlockState> gp) {
            // The global palette is too big to materialise fully (~22k entries), so we
            // resolve each storage entry through it on demand instead.
            globalPalette = gp;
            paletteCount = gp.getSize();
        } else {
            paletteCount = Math.max(0, setupLocalPalette(palette, blockStateCache, mapper, paletteCache) - 1);
        }

        // Biomes live on a 4x4x4 sub-grid per section; resolve them once up front.
        {
            int dst = 0;
            int initial = -1;
            for (int by = 0; by < 4; by++)
                for (int bz = 0; bz < 4; bz++)
                    for (int bx = 0; bx < 4; bx++) {
                        int id = mapper.getIdForBiome(biomes.get(bx, by, bz));
                        biomeIds[dst++] = id;
                        if (initial == -1) initial = id;
                        // Same trick as upstream: only zoom when the section actually
                        // spans more than one biome.
                        shouldZoom &= (initial == id);
                    }
            // The actual per-voxel biome zoom function from upstream is not yet ported;
            // see the parameter doc for what {@code shouldZoom} would do.
        }

        int nonAirCount = 0;
        var storage = blocks.data.storage();
        if (storage instanceof SimpleBitStorage simple) {
            // SimpleBitStorage packs N entries per long with bit-width = simple.getBits().
            long[] raw = simple.getRaw();
            int bits = simple.getBits();
            int mask = (1 << bits) - 1;
            int entriesPerLong = 64 / bits;
            int entriesLeftInWord = 0;
            long word = 0;
            int wordIdx = 0;

            for (int i = 0; i <= 0xFFF; i++) {
                if (entriesLeftInWord == 0) {
                    word = raw[wordIdx++];
                    entriesLeftInWord = entriesPerLong;
                }
                int paletteIdx = (int) (word & mask);
                word >>>= bits;
                entriesLeftInWord--;

                int blockId;
                if (globalPalette == null) {
                    // Defensive clamp guards against malformed sections that reference
                    // palette indices outside the actual palette size.
                    blockId = paletteCache[Math.min(paletteIdx, paletteCount)];
                } else {
                    blockId = mapper.getIdForBlockState(globalPalette.valueFor(paletteIdx));
                }

                byte lightNibble = light.supply(i & 0xF, (i >> 8) & 0xF, (i >> 4) & 0xF);
                if (blockId != 0) nonAirCount++;
                data[i] = Mapper.composeMappingId(lightNibble, blockId, biomeIds[biomeIndex(i)]);
            }
        } else if (storage instanceof ZeroBitStorage) {
            // Whole section is one block (the SingleValuePalette case).
            int blockId = paletteCache[0];
            if (blockId == 0) {
                for (int i = 0; i <= 0xFFF; i++) {
                    data[i] = Mapper.airWithLight(light.supply(i & 0xF, (i >> 8) & 0xF, (i >> 4) & 0xF));
                }
            } else {
                nonAirCount = 4096;
                for (int i = 0; i <= 0xFFF; i++) {
                    byte lightNibble = light.supply(i & 0xF, (i >> 8) & 0xF, (i >> 4) & 0xF);
                    data[i] = Mapper.composeMappingId(lightNibble, blockId, biomeIds[biomeIndex(i)]);
                }
            }
        } else {
            throw new IllegalStateException("Unsupported BitStorage: " + storage.getClass().getName());
        }

        out.lvl0NonAirCount = nonAirCount;
        return out;
    }

    /**
     * Maps a level-0 voxel index {@code i = (y<<8)|(z<<4)|x} (with x,y,z in {@code [0,16)})
     * to the index into the 4&times;4&times;4 biome cache that covers it.
     *
     * <p>Each biome cell spans a 4&times;4&times;4 block region, so the biome cache index
     * is {@code (y>>2, z>>2, x>>2)} packed as {@code (by<<4)|(bz<<2)|bx}. Equivalent to
     * {@code Integer.compress(i, 0b1100_1100_1100)} on Java 19+, but inlined here so the
     * code compiles on Java 17.</p>
     */
    private static int biomeIndex(int i) {
        int bx = (i >> 2) & 0x3;
        int bz = (i >> 6) & 0x3;
        int by = (i >> 10) & 0x3;
        return (by << 4) | (bz << 2) | bx;
    }
}

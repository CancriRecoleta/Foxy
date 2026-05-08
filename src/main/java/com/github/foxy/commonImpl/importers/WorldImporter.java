package com.github.foxy.commonImpl.importers;

import com.github.foxy.common.Logger;
import com.github.foxy.common.voxelization.LightNibbleReader;
import com.github.foxy.common.voxelization.SectionDataPacker;
import com.github.foxy.common.voxelization.VoxelizedSection;
import com.github.foxy.common.voxelization.WorldConversionFactory;
import com.github.foxy.common.voxelization.WorldVoxilizedSectionMipper;
import com.github.foxy.common.world.WorldEngine;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.PalettedContainerRO;
import net.minecraft.world.level.chunk.storage.RegionFile;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * One-shot import of a vanilla anvil region directory (containing
 * {@code r.<x>.<z>.mca} files) into a {@link WorldEngine}.
 *
 * <p>Cleanroom implementation targeting Minecraft 1.20.1 / Forge 47.4 chunk NBT layout:
 * each {@code sections[]} entry carries {@code block_states}, {@code biomes},
 * {@code BlockLight} and {@code SkyLight}. Vanilla's own
 * {@link PalettedContainer#codecRW codecRW} / {@link PalettedContainer#codecRO codecRO}
 * are used to decode the palettes; the result is fed to
 * {@link WorldConversionFactory}.</p>
 *
 * <h2>Pipeline</h2>
 * <ol>
 *   <li>Enumerate all {@code r.X.Z.mca} files in the supplied directory and parse the
 *       region coordinates from each filename.</li>
 *   <li>For each region, iterate its 32&times;32 chunk slots and read the NBT of every
 *       chunk that exists.</li>
 *   <li>For each chunk-section, voxelize into a 16&sup3; {@link VoxelizedSection}.</li>
 *   <li>Pack 8 adjacent voxelized sections (a 2&times;2&times;2 cube of chunk-sections)
 *       into one 32&sup3; LOD-0 {@link com.github.foxy.common.world.WorldSection
 *       WorldSection} via {@link SectionDataPacker}.</li>
 *   <li>Persist synchronously through {@link WorldEngine#saveSection saveSection} and
 *       release the section reference.</li>
 * </ol>
 *
 * <p>The import writes LOD 0 synchronously. The owning {@link
 * com.github.foxy.commonImpl.ImportManager} starts the asynchronous mip rebuild after
 * completion so LOD 1..4 become renderable without a manual follow-up command.</p>
 *
 * <h2>Threading</h2>
 * <p>{@link #runImport} returns immediately and runs work on a dedicated daemon worker
 * thread. The worker drives both the progress and completion callbacks; callers can
 * request a stop via {@link #shutdown()}.</p>
 */
public final class WorldImporter implements IDataImporter {

    private static final Pattern REGION_FILE_PATTERN = Pattern.compile("r\\.(-?\\d+)\\.(-?\\d+)\\.mca");
    /** A region file can hold up to 32&times;32 chunk slots (not all need be present). */
    private static final int CHUNKS_PER_REGION_AXIS = 32;

    private final WorldEngine engine;
    private final Path regionDirectory;
    private final Codec_BlockStateContainer blockCodec;
    private final Codec_BiomeContainer biomeCodec;

    private final AtomicBoolean cancelled = new AtomicBoolean(false);
    private final AtomicBoolean running = new AtomicBoolean(false);
    private volatile Thread workerThread;

    /**
     * @param engine          target engine; held via {@link WorldEngine#acquireRef()}
     *                        for the duration of the import
     * @param regionDirectory typically {@code <world>/region/}
     * @param biomeRegistry   biome registry used to decode the biome palette; on the
     *                        client this can be obtained via
     *                        {@code Minecraft.getInstance().level.registryAccess()
     *                        .registryOrThrow(Registries.BIOME)}
     */
    public WorldImporter(WorldEngine engine, Path regionDirectory, Registry<Biome> biomeRegistry) {
        this.engine = engine;
        this.regionDirectory = regionDirectory;
        this.blockCodec = new Codec_BlockStateContainer();
        this.biomeCodec = new Codec_BiomeContainer(biomeRegistry);
    }

    @Override public WorldEngine getEngine() { return this.engine; }
    @Override public boolean isRunning() { return this.running.get(); }

    @Override
    public void shutdown() {
        this.cancelled.set(true);
        var t = this.workerThread;
        if (t != null) t.interrupt();
    }

    @Override
    public void runImport(IUpdateCallback updateCallback, ICompletionCallback completionCallback) {
        if (!this.running.compareAndSet(false, true)) {
            throw new IllegalStateException("Importer already running");
        }
        this.engine.acquireRef();
        Thread t = new Thread(() -> {
            int processed = 0;
            try {
                processed = doImport(updateCallback);
            } catch (Throwable th) {
                Logger.error("WorldImporter worker crashed", th);
            } finally {
                this.running.set(false);
                this.engine.releaseRef();
                try {
                    completionCallback.onCompletion(processed);
                } catch (Throwable th) {
                    Logger.error("WorldImporter completion callback threw", th);
                }
            }
        }, "foxy-world-importer");
        t.setDaemon(true);
        this.workerThread = t;
        t.start();
    }

    /** Worker entry point; returns the number of chunks successfully imported. */
    private int doImport(IUpdateCallback updateCallback) {
        List<RegionFileRef> regionFiles = enumerateRegionFiles();
        if (regionFiles.isEmpty()) {
            Logger.warn("WorldImporter: no region files found in " + this.regionDirectory);
            return 0;
        }
        int totalEstimate = regionFiles.size() * CHUNKS_PER_REGION_AXIS * CHUNKS_PER_REGION_AXIS;
        var done = new AtomicInteger(0);

        // Reuse a single VoxelizedSection scratch buffer: the voxelizer doesn't retain
        // a reference once convert() returns, so zeroing-and-reusing avoids ~37 KiB of
        // long[] allocations per chunk-section.
        VoxelizedSection scratch = VoxelizedSection.createEmpty();

        int imported = 0;
        for (RegionFileRef ref : regionFiles) {
            if (this.cancelled.get()) break;
            try (var rf = new RegionFile(ref.path, this.regionDirectory, true)) {
                imported += processRegion(rf, ref, scratch, done, totalEstimate, updateCallback);
            } catch (IOException e) {
                Logger.error("WorldImporter: failed to open region " + ref.path, e);
            }
        }
        // Force a final flush so an in-process death after this point won't lose data.
        try { this.engine.storage.flush(); } catch (Throwable t) { Logger.error("Flush failed", t); }
        return imported;
    }

    /** Walks the region directory and parses {@code r.X.Z.mca} into refs. */
    private List<RegionFileRef> enumerateRegionFiles() {
        var refs = new ArrayList<RegionFileRef>();
        if (!Files.isDirectory(this.regionDirectory)) {
            Logger.error("WorldImporter: region path is not a directory: " + this.regionDirectory);
            return refs;
        }
        try (var stream = Files.list(this.regionDirectory)) {
            stream.forEach(p -> {
                String name = p.getFileName().toString();
                Matcher m = REGION_FILE_PATTERN.matcher(name);
                if (m.matches()) {
                    int rx = Integer.parseInt(m.group(1));
                    int rz = Integer.parseInt(m.group(2));
                    refs.add(new RegionFileRef(p, rx, rz));
                }
            });
        } catch (IOException e) {
            Logger.error("WorldImporter: directory listing failed", e);
        }
        return refs;
    }

    private int processRegion(RegionFile rf, RegionFileRef ref, VoxelizedSection scratch,
                              AtomicInteger done, int totalEstimate, IUpdateCallback updateCallback) {
        int imported = 0;
        for (int dz = 0; dz < CHUNKS_PER_REGION_AXIS; dz++) {
            for (int dx = 0; dx < CHUNKS_PER_REGION_AXIS; dx++) {
                if (this.cancelled.get()) return imported;
                int chunkX = ref.regionX * CHUNKS_PER_REGION_AXIS + dx;
                int chunkZ = ref.regionZ * CHUNKS_PER_REGION_AXIS + dz;
                ChunkPos cp = new ChunkPos(chunkX, chunkZ);
                if (!rf.doesChunkExist(cp)) {
                    int d = done.incrementAndGet();
                    if ((d & 0x3F) == 0) updateCallback.onUpdate(d, totalEstimate);
                    continue;
                }
                CompoundTag chunkTag = readChunkTag(rf, cp);
                if (chunkTag == null) {
                    int d = done.incrementAndGet();
                    if ((d & 0x3F) == 0) updateCallback.onUpdate(d, totalEstimate);
                    continue;
                }
                try {
                    importChunk(chunkX, chunkZ, chunkTag, scratch);
                    imported++;
                } catch (Throwable t) {
                    Logger.error("WorldImporter: chunk " + chunkX + "," + chunkZ + " failed", t);
                }
                int d = done.incrementAndGet();
                updateCallback.onUpdate(d, totalEstimate);
            }
        }
        return imported;
    }

    /** Reads a chunk's CompoundTag from a region file; returns null on read failure. */
    private static CompoundTag readChunkTag(RegionFile rf, ChunkPos cp) {
        try (DataInputStream in = rf.getChunkDataInputStream(cp)) {
            if (in == null) return null;
            return NbtIo.read(in);
        } catch (IOException e) {
            Logger.warn("WorldImporter: failed to read chunk " + cp + ": " + e.getMessage());
            return null;
        }
    }

    /** Voxelizes one chunk's sections and persists them as LOD-0 WorldSections. */
    private void importChunk(int chunkX, int chunkZ, CompoundTag chunkTag, VoxelizedSection scratch) {
        if (!chunkTag.contains("sections", 9 /* TAG_LIST */)) return;
        var sectionsTag = chunkTag.getList("sections", 10 /* TAG_COMPOUND */);

        for (int i = 0; i < sectionsTag.size(); i++) {
            if (this.cancelled.get()) return;
            CompoundTag sectionTag = sectionsTag.getCompound(i);
            if (!sectionTag.contains("Y", 99 /* TAG_NUMERIC */)) continue;
            int sectionY = sectionTag.getByte("Y");

            // Skip light-only sections (above/below the actual block range).
            if (!sectionTag.contains("block_states", 10)) continue;

            scratch.zero();
            scratch.setPosition(chunkX, sectionY, chunkZ);

            PalettedContainer<BlockState> blocks = this.blockCodec.parse(sectionTag.getCompound("block_states"));
            if (blocks == null) continue;
            PalettedContainerRO<Holder<Biome>> biomes = sectionTag.contains("biomes", 10)
                    ? this.biomeCodec.parse(sectionTag.getCompound("biomes"))
                    : null;
            if (biomes == null) {
                // Some non-overworld chunks lack biome data; fall back to uniform plains.
                biomes = this.biomeCodec.uniform();
            }
            byte[] blockLight = sectionTag.contains("BlockLight", 7) ? sectionTag.getByteArray("BlockLight") : null;
            byte[] skyLight   = sectionTag.contains("SkyLight",   7) ? sectionTag.getByteArray("SkyLight")   : null;

            var light = new LightNibbleReader(blockLight, skyLight);
            WorldConversionFactory.convert(scratch, this.engine.getMapper(), blocks, biomes, light);

            // Map a 2x2x2 chunk-section bundle into one WorldSection: the world-space
            // section coords are chunk_coords / 2; (chunk_x & 1, sectionY & 1, chunk_z & 1)
            // selects which of the 8 octants this chunk-section occupies inside it.
            int wsx = chunkX >> 1;
            int wsy = sectionY >> 1;
            int wsz = chunkZ >> 1;
            int ox = chunkX & 1;
            int oy = sectionY & 1;
            int oz = chunkZ & 1;

            var dst = this.engine.acquire(0, wsx, wsy, wsz);
            try {
                SectionDataPacker.packLvl0Octant(scratch, dst, ox, oy, oz);
                dst.updateLvl0State();
                this.engine.markDirty(dst);
                this.engine.saveSection(dst);
                // The import path writes LOD 0 only; ImportManager schedules mipAll()
                // once the whole run completes so LOD 1..4 are rebuilt in coherent
                // parent batches instead of racing every individual chunk write.
                @SuppressWarnings("unused") var unused = WorldVoxilizedSectionMipper.class;
            } finally {
                dst.release();
            }
        }
    }

    // ---- region file ref / codec wrappers ---------------------------------------------

    private record RegionFileRef(Path path, int regionX, int regionZ) {}

    /**
     * Wraps {@link PalettedContainer#codecRW} as a NBT-to-container parser; the codec
     * itself is registry-access-free, so a single instance can be reused across all
     * chunks of one importer run.
     */
    private static final class Codec_BlockStateContainer {
        private final com.mojang.serialization.Codec<PalettedContainer<BlockState>> codec;

        Codec_BlockStateContainer() {
            this.codec = PalettedContainer.codecRW(
                    Block.BLOCK_STATE_REGISTRY,
                    BlockState.CODEC,
                    PalettedContainer.Strategy.SECTION_STATES,
                    Blocks.AIR.defaultBlockState());
        }

        PalettedContainer<BlockState> parse(CompoundTag tag) {
            var result = this.codec.parse(NbtOps.INSTANCE, tag);
            if (result.error().isPresent()) {
                Logger.warn("Block PalettedContainer decode failed: " + result.error().get().message());
                return null;
            }
            return result.result().orElse(null);
        }
    }

    /** Wrapper around {@link PalettedContainer#codecRO} for biomes. */
    private static final class Codec_BiomeContainer {
        private final com.mojang.serialization.Codec<PalettedContainerRO<Holder<Biome>>> codec;
        private final Holder<Biome> defaultBiome;
        private final PalettedContainer.Strategy strategy = PalettedContainer.Strategy.SECTION_BIOMES;
        private final Registry<Biome> registry;

        Codec_BiomeContainer(Registry<Biome> biomeRegistry) {
            this.registry = biomeRegistry;
            this.defaultBiome = biomeRegistry.getHolderOrThrow(Biomes.PLAINS);
            this.codec = PalettedContainer.codecRO(
                    biomeRegistry.asHolderIdMap(),
                    biomeRegistry.holderByNameCodec(),
                    this.strategy,
                    this.defaultBiome);
        }

        PalettedContainerRO<Holder<Biome>> parse(CompoundTag tag) {
            var result = this.codec.parse(NbtOps.INSTANCE, tag);
            if (result.error().isPresent()) {
                Logger.warn("Biome PalettedContainer decode failed: " + result.error().get().message());
                return null;
            }
            return result.result().orElse(null);
        }

        /** Synthesises a uniform single-value biome container, used when NBT omits biomes. */
        PalettedContainerRO<Holder<Biome>> uniform() {
            return new PalettedContainer<>(
                    this.registry.asHolderIdMap(),
                    this.defaultBiome,
                    this.strategy);
        }
    }
}

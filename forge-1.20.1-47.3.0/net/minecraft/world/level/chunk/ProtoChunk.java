//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.chunk;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.BelowZeroRetrogen;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.ticks.LevelChunkTicks;
import net.minecraft.world.ticks.ProtoChunkTicks;
import net.minecraft.world.ticks.TickContainerAccess;

public class ProtoChunk extends ChunkAccess {
    @Nullable
    private volatile LevelLightEngine lightEngine;
    private volatile ChunkStatus status;
    private final List<CompoundTag> entities;
    private final Map<GenerationStep.Carving, CarvingMask> carvingMasks;
    @Nullable
    private BelowZeroRetrogen belowZeroRetrogen;
    private final ProtoChunkTicks<Block> blockTicks;
    private final ProtoChunkTicks<Fluid> fluidTicks;

    public ProtoChunk(ChunkPos p_188167_, UpgradeData p_188168_, LevelHeightAccessor p_188169_, Registry<Biome> p_188170_, @Nullable BlendingData p_188171_) {
        this(p_188167_, p_188168_, (LevelChunkSection[])null, new ProtoChunkTicks(), new ProtoChunkTicks(), p_188169_, p_188170_, p_188171_);
    }

    public ProtoChunk(ChunkPos p_188173_, UpgradeData p_188174_, @Nullable LevelChunkSection[] p_188175_, ProtoChunkTicks<Block> p_188176_, ProtoChunkTicks<Fluid> p_188177_, LevelHeightAccessor p_188178_, Registry<Biome> p_188179_, @Nullable BlendingData p_188180_) {
        super(p_188173_, p_188174_, p_188178_, p_188179_, 0L, p_188175_, p_188180_);
        this.status = ChunkStatus.EMPTY;
        this.entities = Lists.newArrayList();
        this.carvingMasks = new Object2ObjectArrayMap();
        this.blockTicks = p_188176_;
        this.fluidTicks = p_188177_;
    }

    public TickContainerAccess<Block> getBlockTicks() {
        return this.blockTicks;
    }

    public TickContainerAccess<Fluid> getFluidTicks() {
        return this.fluidTicks;
    }

    public ChunkAccess.TicksToSave getTicksForSerialization() {
        return new ChunkAccess.TicksToSave(this.blockTicks, this.fluidTicks);
    }

    public BlockState getBlockState(BlockPos p_63264_) {
        int $$1 = p_63264_.getY();
        if (this.isOutsideBuildHeight($$1)) {
            return Blocks.VOID_AIR.defaultBlockState();
        } else {
            LevelChunkSection $$2 = this.getSection(this.getSectionIndex($$1));
            return $$2.hasOnlyAir() ? Blocks.AIR.defaultBlockState() : $$2.getBlockState(p_63264_.getX() & 15, $$1 & 15, p_63264_.getZ() & 15);
        }
    }

    public FluidState getFluidState(BlockPos p_63239_) {
        int $$1 = p_63239_.getY();
        if (this.isOutsideBuildHeight($$1)) {
            return Fluids.EMPTY.defaultFluidState();
        } else {
            LevelChunkSection $$2 = this.getSection(this.getSectionIndex($$1));
            return $$2.hasOnlyAir() ? Fluids.EMPTY.defaultFluidState() : $$2.getFluidState(p_63239_.getX() & 15, $$1 & 15, p_63239_.getZ() & 15);
        }
    }

    @Nullable
    public BlockState setBlockState(BlockPos p_63217_, BlockState p_63218_, boolean p_63219_) {
        int $$3 = p_63217_.getX();
        int $$4 = p_63217_.getY();
        int $$5 = p_63217_.getZ();
        if ($$4 >= this.getMinBuildHeight() && $$4 < this.getMaxBuildHeight()) {
            int $$6 = this.getSectionIndex($$4);
            LevelChunkSection $$7 = this.getSection($$6);
            boolean $$8 = $$7.hasOnlyAir();
            if ($$8 && p_63218_.is(Blocks.AIR)) {
                return p_63218_;
            } else {
                int $$9 = SectionPos.sectionRelative($$3);
                int $$10 = SectionPos.sectionRelative($$4);
                int $$11 = SectionPos.sectionRelative($$5);
                BlockState $$12 = $$7.setBlockState($$9, $$10, $$11, p_63218_);
                if (this.status.isOrAfter(ChunkStatus.INITIALIZE_LIGHT)) {
                    boolean $$13 = $$7.hasOnlyAir();
                    if ($$13 != $$8) {
                        this.lightEngine.updateSectionStatus(p_63217_, $$13);
                    }

                    if (LightEngine.hasDifferentLightProperties(this, p_63217_, $$12, p_63218_)) {
                        this.skyLightSources.update(this, $$9, $$4, $$11);
                        this.lightEngine.checkBlock(p_63217_);
                    }
                }

                EnumSet<Heightmap.Types> $$14 = this.getStatus().heightmapsAfter();
                EnumSet<Heightmap.Types> $$15 = null;
                Iterator var16 = $$14.iterator();

                Heightmap.Types $$18;
                while(var16.hasNext()) {
                    $$18 = (Heightmap.Types)var16.next();
                    Heightmap $$17 = (Heightmap)this.heightmaps.get($$18);
                    if ($$17 == null) {
                        if ($$15 == null) {
                            $$15 = EnumSet.noneOf(Heightmap.Types.class);
                        }

                        $$15.add($$18);
                    }
                }

                if ($$15 != null) {
                    Heightmap.primeHeightmaps(this, $$15);
                }

                var16 = $$14.iterator();

                while(var16.hasNext()) {
                    $$18 = (Heightmap.Types)var16.next();
                    ((Heightmap)this.heightmaps.get($$18)).update($$9, $$4, $$11, p_63218_);
                }

                return $$12;
            }
        } else {
            return Blocks.VOID_AIR.defaultBlockState();
        }
    }

    public void setBlockEntity(BlockEntity p_156488_) {
        this.blockEntities.put(p_156488_.getBlockPos(), p_156488_);
    }

    @Nullable
    public BlockEntity getBlockEntity(BlockPos p_63257_) {
        return (BlockEntity)this.blockEntities.get(p_63257_);
    }

    public Map<BlockPos, BlockEntity> getBlockEntities() {
        return this.blockEntities;
    }

    public void addEntity(CompoundTag p_63243_) {
        this.entities.add(p_63243_);
    }

    public void addEntity(Entity p_63183_) {
        if (!p_63183_.isPassenger()) {
            CompoundTag $$1 = new CompoundTag();
            p_63183_.save($$1);
            this.addEntity($$1);
        }
    }

    public void setStartForStructure(Structure p_223432_, StructureStart p_223433_) {
        BelowZeroRetrogen $$2 = this.getBelowZeroRetrogen();
        if ($$2 != null && p_223433_.isValid()) {
            BoundingBox $$3 = p_223433_.getBoundingBox();
            LevelHeightAccessor $$4 = this.getHeightAccessorForGeneration();
            if ($$3.minY() < $$4.getMinBuildHeight() || $$3.maxY() >= $$4.getMaxBuildHeight()) {
                return;
            }
        }

        super.setStartForStructure(p_223432_, p_223433_);
    }

    public List<CompoundTag> getEntities() {
        return this.entities;
    }

    public ChunkStatus getStatus() {
        return this.status;
    }

    public void setStatus(ChunkStatus p_63187_) {
        this.status = p_63187_;
        if (this.belowZeroRetrogen != null && p_63187_.isOrAfter(this.belowZeroRetrogen.targetStatus())) {
            this.setBelowZeroRetrogen((BelowZeroRetrogen)null);
        }

        this.setUnsaved(true);
    }

    public Holder<Biome> getNoiseBiome(int p_204450_, int p_204451_, int p_204452_) {
        if (this.getHighestGeneratedStatus().isOrAfter(ChunkStatus.BIOMES)) {
            return super.getNoiseBiome(p_204450_, p_204451_, p_204452_);
        } else {
            throw new IllegalStateException("Asking for biomes before we have biomes");
        }
    }

    public static short packOffsetCoordinates(BlockPos p_63281_) {
        int $$1 = p_63281_.getX();
        int $$2 = p_63281_.getY();
        int $$3 = p_63281_.getZ();
        int $$4 = $$1 & 15;
        int $$5 = $$2 & 15;
        int $$6 = $$3 & 15;
        return (short)($$4 | $$5 << 4 | $$6 << 8);
    }

    public static BlockPos unpackOffsetCoordinates(short p_63228_, int p_63229_, ChunkPos p_63230_) {
        int $$3 = SectionPos.sectionToBlockCoord(p_63230_.x, p_63228_ & 15);
        int $$4 = SectionPos.sectionToBlockCoord(p_63229_, p_63228_ >>> 4 & 15);
        int $$5 = SectionPos.sectionToBlockCoord(p_63230_.z, p_63228_ >>> 8 & 15);
        return new BlockPos($$3, $$4, $$5);
    }

    public void markPosForPostprocessing(BlockPos p_63266_) {
        if (!this.isOutsideBuildHeight(p_63266_)) {
            ChunkAccess.getOrCreateOffsetList(this.postProcessing, this.getSectionIndex(p_63266_.getY())).add(packOffsetCoordinates(p_63266_));
        }

    }

    public void addPackedPostProcess(short p_63225_, int p_63226_) {
        ChunkAccess.getOrCreateOffsetList(this.postProcessing, p_63226_).add(p_63225_);
    }

    public Map<BlockPos, CompoundTag> getBlockEntityNbts() {
        return Collections.unmodifiableMap(this.pendingBlockEntities);
    }

    @Nullable
    public CompoundTag getBlockEntityNbtForSaving(BlockPos p_63275_) {
        BlockEntity $$1 = this.getBlockEntity(p_63275_);
        return $$1 != null ? $$1.saveWithFullMetadata() : (CompoundTag)this.pendingBlockEntities.get(p_63275_);
    }

    public void removeBlockEntity(BlockPos p_63262_) {
        this.blockEntities.remove(p_63262_);
        this.pendingBlockEntities.remove(p_63262_);
    }

    @Nullable
    public CarvingMask getCarvingMask(GenerationStep.Carving p_188185_) {
        return (CarvingMask)this.carvingMasks.get(p_188185_);
    }

    public CarvingMask getOrCreateCarvingMask(GenerationStep.Carving p_188191_) {
        return (CarvingMask)this.carvingMasks.computeIfAbsent(p_188191_, (p_289528_) -> {
            return new CarvingMask(this.getHeight(), this.getMinBuildHeight());
        });
    }

    public void setCarvingMask(GenerationStep.Carving p_188187_, CarvingMask p_188188_) {
        this.carvingMasks.put(p_188187_, p_188188_);
    }

    public void setLightEngine(LevelLightEngine p_63210_) {
        this.lightEngine = p_63210_;
    }

    public void setBelowZeroRetrogen(@Nullable BelowZeroRetrogen p_188184_) {
        this.belowZeroRetrogen = p_188184_;
    }

    @Nullable
    public BelowZeroRetrogen getBelowZeroRetrogen() {
        return this.belowZeroRetrogen;
    }

    private static <T> LevelChunkTicks<T> unpackTicks(ProtoChunkTicks<T> p_188190_) {
        return new LevelChunkTicks(p_188190_.scheduledTicks());
    }

    public LevelChunkTicks<Block> unpackBlockTicks() {
        return unpackTicks(this.blockTicks);
    }

    public LevelChunkTicks<Fluid> unpackFluidTicks() {
        return unpackTicks(this.fluidTicks);
    }

    public LevelHeightAccessor getHeightAccessorForGeneration() {
        return (LevelHeightAccessor)(this.isUpgrading() ? BelowZeroRetrogen.UPGRADE_HEIGHT_ACCESSOR : this);
    }
}

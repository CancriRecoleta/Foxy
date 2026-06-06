//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.blending;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction8;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Direction.Plane;
import net.minecraft.data.worldgen.NoiseData;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.GenerationStep.Carving;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.material.FluidState;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableObject;

public class Blender {
    private static final Blender EMPTY = new Blender(new Long2ObjectOpenHashMap(), new Long2ObjectOpenHashMap()) {
        public BlendingOutput blendOffsetAndFactor(int p_209724_, int p_209725_) {
            return new BlendingOutput(1.0, 0.0);
        }

        public double blendDensity(DensityFunction.FunctionContext p_209727_, double p_209728_) {
            return p_209728_;
        }

        public BiomeResolver getBiomeResolver(BiomeResolver p_190232_) {
            return p_190232_;
        }
    };
    private static final NormalNoise SHIFT_NOISE;
    private static final int HEIGHT_BLENDING_RANGE_CELLS;
    private static final int HEIGHT_BLENDING_RANGE_CHUNKS;
    private static final int DENSITY_BLENDING_RANGE_CELLS = 2;
    private static final int DENSITY_BLENDING_RANGE_CHUNKS;
    private static final double OLD_CHUNK_XZ_RADIUS = 8.0;
    private final Long2ObjectOpenHashMap<BlendingData> heightAndBiomeBlendingData;
    private final Long2ObjectOpenHashMap<BlendingData> densityBlendingData;

    public static Blender empty() {
        return EMPTY;
    }

    public static Blender of(@Nullable WorldGenRegion p_190203_) {
        if (p_190203_ == null) {
            return EMPTY;
        } else {
            ChunkPos $$1 = p_190203_.getCenter();
            if (!p_190203_.isOldChunkAround($$1, HEIGHT_BLENDING_RANGE_CHUNKS)) {
                return EMPTY;
            } else {
                Long2ObjectOpenHashMap<BlendingData> $$2 = new Long2ObjectOpenHashMap();
                Long2ObjectOpenHashMap<BlendingData> $$3 = new Long2ObjectOpenHashMap();
                int $$4 = Mth.square(HEIGHT_BLENDING_RANGE_CHUNKS + 1);

                for(int $$5 = -HEIGHT_BLENDING_RANGE_CHUNKS; $$5 <= HEIGHT_BLENDING_RANGE_CHUNKS; ++$$5) {
                    for(int $$6 = -HEIGHT_BLENDING_RANGE_CHUNKS; $$6 <= HEIGHT_BLENDING_RANGE_CHUNKS; ++$$6) {
                        if ($$5 * $$5 + $$6 * $$6 <= $$4) {
                            int $$7 = $$1.x + $$5;
                            int $$8 = $$1.z + $$6;
                            BlendingData $$9 = BlendingData.getOrUpdateBlendingData(p_190203_, $$7, $$8);
                            if ($$9 != null) {
                                $$2.put(ChunkPos.asLong($$7, $$8), $$9);
                                if ($$5 >= -DENSITY_BLENDING_RANGE_CHUNKS && $$5 <= DENSITY_BLENDING_RANGE_CHUNKS && $$6 >= -DENSITY_BLENDING_RANGE_CHUNKS && $$6 <= DENSITY_BLENDING_RANGE_CHUNKS) {
                                    $$3.put(ChunkPos.asLong($$7, $$8), $$9);
                                }
                            }
                        }
                    }
                }

                if ($$2.isEmpty() && $$3.isEmpty()) {
                    return EMPTY;
                } else {
                    return new Blender($$2, $$3);
                }
            }
        }
    }

    Blender(Long2ObjectOpenHashMap<BlendingData> p_202197_, Long2ObjectOpenHashMap<BlendingData> p_202198_) {
        this.heightAndBiomeBlendingData = p_202197_;
        this.densityBlendingData = p_202198_;
    }

    public BlendingOutput blendOffsetAndFactor(int p_209719_, int p_209720_) {
        int $$2 = QuartPos.fromBlock(p_209719_);
        int $$3 = QuartPos.fromBlock(p_209720_);
        double $$4 = this.getBlendingDataValue($$2, 0, $$3, BlendingData::getHeight);
        if ($$4 != Double.MAX_VALUE) {
            return new BlendingOutput(0.0, heightToOffset($$4));
        } else {
            MutableDouble $$5 = new MutableDouble(0.0);
            MutableDouble $$6 = new MutableDouble(0.0);
            MutableDouble $$7 = new MutableDouble(Double.POSITIVE_INFINITY);
            this.heightAndBiomeBlendingData.forEach((p_202249_, p_202250_) -> {
                p_202250_.iterateHeights(QuartPos.fromSection(ChunkPos.getX(p_202249_)), QuartPos.fromSection(ChunkPos.getZ(p_202249_)), (p_190199_, p_190200_, p_190201_) -> {
                    double $$8 = Mth.length((double)($$2 - p_190199_), (double)($$3 - p_190200_));
                    if (!($$8 > (double)HEIGHT_BLENDING_RANGE_CELLS)) {
                        if ($$8 < $$7.doubleValue()) {
                            $$7.setValue($$8);
                        }

                        double $$9 = 1.0 / ($$8 * $$8 * $$8 * $$8);
                        $$6.add(p_190201_ * $$9);
                        $$5.add($$9);
                    }
                });
            });
            if ($$7.doubleValue() == Double.POSITIVE_INFINITY) {
                return new BlendingOutput(1.0, 0.0);
            } else {
                double $$8 = $$6.doubleValue() / $$5.doubleValue();
                double $$9 = Mth.clamp($$7.doubleValue() / (double)(HEIGHT_BLENDING_RANGE_CELLS + 1), 0.0, 1.0);
                $$9 = 3.0 * $$9 * $$9 - 2.0 * $$9 * $$9 * $$9;
                return new BlendingOutput($$9, heightToOffset($$8));
            }
        }
    }

    private static double heightToOffset(double p_190155_) {
        double $$1 = 1.0;
        double $$2 = p_190155_ + 0.5;
        double $$3 = Mth.positiveModulo($$2, 8.0);
        return 1.0 * (32.0 * ($$2 - 128.0) - 3.0 * ($$2 - 120.0) * $$3 + 3.0 * $$3 * $$3) / (128.0 * (32.0 - 3.0 * $$3));
    }

    public double blendDensity(DensityFunction.FunctionContext p_209721_, double p_209722_) {
        int $$2 = QuartPos.fromBlock(p_209721_.blockX());
        int $$3 = p_209721_.blockY() / 8;
        int $$4 = QuartPos.fromBlock(p_209721_.blockZ());
        double $$5 = this.getBlendingDataValue($$2, $$3, $$4, BlendingData::getDensity);
        if ($$5 != Double.MAX_VALUE) {
            return $$5;
        } else {
            MutableDouble $$6 = new MutableDouble(0.0);
            MutableDouble $$7 = new MutableDouble(0.0);
            MutableDouble $$8 = new MutableDouble(Double.POSITIVE_INFINITY);
            this.densityBlendingData.forEach((p_202241_, p_202242_) -> {
                p_202242_.iterateDensities(QuartPos.fromSection(ChunkPos.getX(p_202241_)), QuartPos.fromSection(ChunkPos.getZ(p_202241_)), $$3 - 1, $$3 + 1, (p_202230_, p_202231_, p_202232_, p_202233_) -> {
                    double $$10 = Mth.length((double)($$2 - p_202230_), (double)(($$3 - p_202231_) * 2), (double)($$4 - p_202232_));
                    if (!($$10 > 2.0)) {
                        if ($$10 < $$8.doubleValue()) {
                            $$8.setValue($$10);
                        }

                        double $$11 = 1.0 / ($$10 * $$10 * $$10 * $$10);
                        $$7.add(p_202233_ * $$11);
                        $$6.add($$11);
                    }
                });
            });
            if ($$8.doubleValue() == Double.POSITIVE_INFINITY) {
                return p_209722_;
            } else {
                double $$9 = $$7.doubleValue() / $$6.doubleValue();
                double $$10 = Mth.clamp($$8.doubleValue() / 3.0, 0.0, 1.0);
                return Mth.lerp($$10, $$9, p_209722_);
            }
        }
    }

    private double getBlendingDataValue(int p_190175_, int p_190176_, int p_190177_, CellValueGetter p_190178_) {
        int $$4 = QuartPos.toSection(p_190175_);
        int $$5 = QuartPos.toSection(p_190177_);
        boolean $$6 = (p_190175_ & 3) == 0;
        boolean $$7 = (p_190177_ & 3) == 0;
        double $$8 = this.getBlendingDataValue(p_190178_, $$4, $$5, p_190175_, p_190176_, p_190177_);
        if ($$8 == Double.MAX_VALUE) {
            if ($$6 && $$7) {
                $$8 = this.getBlendingDataValue(p_190178_, $$4 - 1, $$5 - 1, p_190175_, p_190176_, p_190177_);
            }

            if ($$8 == Double.MAX_VALUE) {
                if ($$6) {
                    $$8 = this.getBlendingDataValue(p_190178_, $$4 - 1, $$5, p_190175_, p_190176_, p_190177_);
                }

                if ($$8 == Double.MAX_VALUE && $$7) {
                    $$8 = this.getBlendingDataValue(p_190178_, $$4, $$5 - 1, p_190175_, p_190176_, p_190177_);
                }
            }
        }

        return $$8;
    }

    private double getBlendingDataValue(CellValueGetter p_190212_, int p_190213_, int p_190214_, int p_190215_, int p_190216_, int p_190217_) {
        BlendingData $$6 = (BlendingData)this.heightAndBiomeBlendingData.get(ChunkPos.asLong(p_190213_, p_190214_));
        return $$6 != null ? p_190212_.get($$6, p_190215_ - QuartPos.fromSection(p_190213_), p_190216_, p_190217_ - QuartPos.fromSection(p_190214_)) : Double.MAX_VALUE;
    }

    public BiomeResolver getBiomeResolver(BiomeResolver p_190204_) {
        return (p_204669_, p_204670_, p_204671_, p_204672_) -> {
            Holder<Biome> $$5 = this.blendBiome(p_204669_, p_204670_, p_204671_);
            return $$5 == null ? p_190204_.getNoiseBiome(p_204669_, p_204670_, p_204671_, p_204672_) : $$5;
        };
    }

    @Nullable
    private Holder<Biome> blendBiome(int p_224707_, int p_224708_, int p_224709_) {
        MutableDouble $$3 = new MutableDouble(Double.POSITIVE_INFINITY);
        MutableObject<Holder<Biome>> $$4 = new MutableObject();
        this.heightAndBiomeBlendingData.forEach((p_224716_, p_224717_) -> {
            p_224717_.iterateBiomes(QuartPos.fromSection(ChunkPos.getX(p_224716_)), p_224708_, QuartPos.fromSection(ChunkPos.getZ(p_224716_)), (p_224723_, p_224724_, p_224725_) -> {
                double $$7 = Mth.length((double)(p_224707_ - p_224723_), (double)(p_224709_ - p_224724_));
                if (!($$7 > (double)HEIGHT_BLENDING_RANGE_CELLS)) {
                    if ($$7 < $$3.doubleValue()) {
                        $$4.setValue(p_224725_);
                        $$3.setValue($$7);
                    }

                }
            });
        });
        if ($$3.doubleValue() == Double.POSITIVE_INFINITY) {
            return null;
        } else {
            double $$5 = SHIFT_NOISE.getValue((double)p_224707_, 0.0, (double)p_224709_) * 12.0;
            double $$6 = Mth.clamp(($$3.doubleValue() + $$5) / (double)(HEIGHT_BLENDING_RANGE_CELLS + 1), 0.0, 1.0);
            return $$6 > 0.5 ? null : (Holder)$$4.getValue();
        }
    }

    public static void generateBorderTicks(WorldGenRegion p_197032_, ChunkAccess p_197033_) {
        ChunkPos $$2 = p_197033_.getPos();
        boolean $$3 = p_197033_.isOldNoiseGeneration();
        BlockPos.MutableBlockPos $$4 = new BlockPos.MutableBlockPos();
        BlockPos $$5 = new BlockPos($$2.getMinBlockX(), 0, $$2.getMinBlockZ());
        BlendingData $$6 = p_197033_.getBlendingData();
        if ($$6 != null) {
            int $$7 = $$6.getAreaWithOldGeneration().getMinBuildHeight();
            int $$8 = $$6.getAreaWithOldGeneration().getMaxBuildHeight() - 1;
            if ($$3) {
                for(int $$9 = 0; $$9 < 16; ++$$9) {
                    for(int $$10 = 0; $$10 < 16; ++$$10) {
                        generateBorderTick(p_197033_, $$4.setWithOffset($$5, $$9, $$7 - 1, $$10));
                        generateBorderTick(p_197033_, $$4.setWithOffset($$5, $$9, $$7, $$10));
                        generateBorderTick(p_197033_, $$4.setWithOffset($$5, $$9, $$8, $$10));
                        generateBorderTick(p_197033_, $$4.setWithOffset($$5, $$9, $$8 + 1, $$10));
                    }
                }
            }

            Iterator var19 = Plane.HORIZONTAL.iterator();

            while(true) {
                Direction $$11;
                do {
                    if (!var19.hasNext()) {
                        return;
                    }

                    $$11 = (Direction)var19.next();
                } while(p_197032_.getChunk($$2.x + $$11.getStepX(), $$2.z + $$11.getStepZ()).isOldNoiseGeneration() == $$3);

                int $$12 = $$11 == Direction.EAST ? 15 : 0;
                int $$13 = $$11 == Direction.WEST ? 0 : 15;
                int $$14 = $$11 == Direction.SOUTH ? 15 : 0;
                int $$15 = $$11 == Direction.NORTH ? 0 : 15;

                for(int $$16 = $$12; $$16 <= $$13; ++$$16) {
                    for(int $$17 = $$14; $$17 <= $$15; ++$$17) {
                        int $$18 = Math.min($$8, p_197033_.getHeight(Types.MOTION_BLOCKING, $$16, $$17)) + 1;

                        for(int $$19 = $$7; $$19 < $$18; ++$$19) {
                            generateBorderTick(p_197033_, $$4.setWithOffset($$5, $$16, $$19, $$17));
                        }
                    }
                }
            }
        }
    }

    private static void generateBorderTick(ChunkAccess p_197041_, BlockPos p_197042_) {
        BlockState $$2 = p_197041_.getBlockState(p_197042_);
        if ($$2.is(BlockTags.LEAVES)) {
            p_197041_.markPosForPostprocessing(p_197042_);
        }

        FluidState $$3 = p_197041_.getFluidState(p_197042_);
        if (!$$3.isEmpty()) {
            p_197041_.markPosForPostprocessing(p_197042_);
        }

    }

    public static void addAroundOldChunksCarvingMaskFilter(WorldGenLevel p_197035_, ProtoChunk p_197036_) {
        ChunkPos $$2 = p_197036_.getPos();
        ImmutableMap.Builder<Direction8, BlendingData> $$3 = ImmutableMap.builder();
        Direction8[] var4 = Direction8.values();
        int var5 = var4.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            Direction8 $$4 = var4[var6];
            int $$5 = $$2.x + $$4.getStepX();
            int $$6 = $$2.z + $$4.getStepZ();
            BlendingData $$7 = p_197035_.getChunk($$5, $$6).getBlendingData();
            if ($$7 != null) {
                $$3.put($$4, $$7);
            }
        }

        ImmutableMap<Direction8, BlendingData> $$8 = $$3.build();
        if (p_197036_.isOldNoiseGeneration() || !$$8.isEmpty()) {
            DistanceGetter $$9 = makeOldChunkDistanceGetter(p_197036_.getBlendingData(), $$8);
            CarvingMask.Mask $$10 = (p_202262_, p_202263_, p_202264_) -> {
                double $$4 = (double)p_202262_ + 0.5 + SHIFT_NOISE.getValue((double)p_202262_, (double)p_202263_, (double)p_202264_) * 4.0;
                double $$5 = (double)p_202263_ + 0.5 + SHIFT_NOISE.getValue((double)p_202263_, (double)p_202264_, (double)p_202262_) * 4.0;
                double $$6 = (double)p_202264_ + 0.5 + SHIFT_NOISE.getValue((double)p_202264_, (double)p_202262_, (double)p_202263_) * 4.0;
                return $$9.getDistance($$4, $$5, $$6) < 4.0;
            };
            Stream var10000 = Stream.of(Carving.values());
            Objects.requireNonNull(p_197036_);
            var10000.map(p_197036_::getOrCreateCarvingMask).forEach((p_202259_) -> {
                p_202259_.setAdditionalMask($$10);
            });
        }
    }

    public static DistanceGetter makeOldChunkDistanceGetter(@Nullable BlendingData p_224727_, Map<Direction8, BlendingData> p_224728_) {
        List<DistanceGetter> $$2 = Lists.newArrayList();
        if (p_224727_ != null) {
            $$2.add(makeOffsetOldChunkDistanceGetter((Direction8)null, p_224727_));
        }

        p_224728_.forEach((p_224734_, p_224735_) -> {
            $$2.add(makeOffsetOldChunkDistanceGetter(p_224734_, p_224735_));
        });
        return (p_202267_, p_202268_, p_202269_) -> {
            double $$4 = Double.POSITIVE_INFINITY;
            Iterator var9 = $$2.iterator();

            while(var9.hasNext()) {
                DistanceGetter $$5 = (DistanceGetter)var9.next();
                double $$6 = $$5.getDistance(p_202267_, p_202268_, p_202269_);
                if ($$6 < $$4) {
                    $$4 = $$6;
                }
            }

            return $$4;
        };
    }

    private static DistanceGetter makeOffsetOldChunkDistanceGetter(@Nullable Direction8 p_224730_, BlendingData p_224731_) {
        double $$2 = 0.0;
        double $$3 = 0.0;
        Direction $$4;
        if (p_224730_ != null) {
            for(Iterator var6 = p_224730_.getDirections().iterator(); var6.hasNext(); $$3 += (double)($$4.getStepZ() * 16)) {
                $$4 = (Direction)var6.next();
                $$2 += (double)($$4.getStepX() * 16);
            }
        }

        double $$5 = $$2;
        double $$6 = $$3;
        double $$7 = (double)p_224731_.getAreaWithOldGeneration().getHeight() / 2.0;
        double $$8 = (double)p_224731_.getAreaWithOldGeneration().getMinBuildHeight() + $$7;
        return (p_224703_, p_224704_, p_224705_) -> {
            return distanceToCube(p_224703_ - 8.0 - $$5, p_224704_ - $$8, p_224705_ - 8.0 - $$6, 8.0, $$7, 8.0);
        };
    }

    private static double distanceToCube(double p_197025_, double p_197026_, double p_197027_, double p_197028_, double p_197029_, double p_197030_) {
        double $$6 = Math.abs(p_197025_) - p_197028_;
        double $$7 = Math.abs(p_197026_) - p_197029_;
        double $$8 = Math.abs(p_197027_) - p_197030_;
        return Mth.length(Math.max(0.0, $$6), Math.max(0.0, $$7), Math.max(0.0, $$8));
    }

    static {
        SHIFT_NOISE = NormalNoise.create(new XoroshiroRandomSource(42L), NoiseData.DEFAULT_SHIFT);
        HEIGHT_BLENDING_RANGE_CELLS = QuartPos.fromSection(7) - 1;
        HEIGHT_BLENDING_RANGE_CHUNKS = QuartPos.toSection(HEIGHT_BLENDING_RANGE_CELLS + 3);
        DENSITY_BLENDING_RANGE_CHUNKS = QuartPos.toSection(5);
    }

    interface CellValueGetter {
        double get(BlendingData var1, int var2, int var3, int var4);
    }

    public static record BlendingOutput(double alpha, double blendingOffset) {
        public BlendingOutput(double alpha, double blendingOffset) {
            this.alpha = alpha;
            this.blendingOffset = blendingOffset;
        }

        public double alpha() {
            return this.alpha;
        }

        public double blendingOffset() {
            return this.blendingOffset;
        }
    }

    public interface DistanceGetter {
        double getDistance(double var1, double var3, double var5);
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.blending;

import com.google.common.primitives.Doubles;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.DoubleStream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction8;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap.Types;

public class BlendingData {
    private static final double BLENDING_DENSITY_FACTOR = 0.1;
    protected static final int CELL_WIDTH = 4;
    protected static final int CELL_HEIGHT = 8;
    protected static final int CELL_RATIO = 2;
    private static final double SOLID_DENSITY = 1.0;
    private static final double AIR_DENSITY = -1.0;
    private static final int CELLS_PER_SECTION_Y = 2;
    private static final int QUARTS_PER_SECTION = QuartPos.fromBlock(16);
    private static final int CELL_HORIZONTAL_MAX_INDEX_INSIDE;
    private static final int CELL_HORIZONTAL_MAX_INDEX_OUTSIDE;
    private static final int CELL_COLUMN_INSIDE_COUNT;
    private static final int CELL_COLUMN_OUTSIDE_COUNT;
    private static final int CELL_COLUMN_COUNT;
    private final LevelHeightAccessor areaWithOldGeneration;
    private static final List<Block> SURFACE_BLOCKS;
    protected static final double NO_VALUE = Double.MAX_VALUE;
    private boolean hasCalculatedData;
    private final double[] heights;
    private final List<List<Holder<Biome>>> biomes;
    private final transient double[][] densities;
    private static final Codec<double[]> DOUBLE_ARRAY_CODEC;
    public static final Codec<BlendingData> CODEC;

    private static DataResult<BlendingData> validateArraySize(BlendingData p_190321_) {
        return p_190321_.heights.length != CELL_COLUMN_COUNT ? DataResult.error(() -> {
            return "heights has to be of length " + CELL_COLUMN_COUNT;
        }) : DataResult.success(p_190321_);
    }

    private BlendingData(int p_224740_, int p_224741_, Optional<double[]> p_224742_) {
        this.heights = (double[])p_224742_.orElse((double[])Util.make(new double[CELL_COLUMN_COUNT], (p_224756_) -> {
            Arrays.fill(p_224756_, Double.MAX_VALUE);
        }));
        this.densities = new double[CELL_COLUMN_COUNT][];
        ObjectArrayList<List<Holder<Biome>>> $$3 = new ObjectArrayList(CELL_COLUMN_COUNT);
        $$3.size(CELL_COLUMN_COUNT);
        this.biomes = $$3;
        int $$4 = SectionPos.sectionToBlockCoord(p_224740_);
        int $$5 = SectionPos.sectionToBlockCoord(p_224741_) - $$4;
        this.areaWithOldGeneration = LevelHeightAccessor.create($$4, $$5);
    }

    @Nullable
    public static BlendingData getOrUpdateBlendingData(WorldGenRegion p_190305_, int p_190306_, int p_190307_) {
        ChunkAccess $$3 = p_190305_.getChunk(p_190306_, p_190307_);
        BlendingData $$4 = $$3.getBlendingData();
        if ($$4 != null && $$3.getHighestGeneratedStatus().isOrAfter(ChunkStatus.BIOMES)) {
            $$4.calculateData($$3, sideByGenerationAge(p_190305_, p_190306_, p_190307_, false));
            return $$4;
        } else {
            return null;
        }
    }

    public static Set<Direction8> sideByGenerationAge(WorldGenLevel p_197066_, int p_197067_, int p_197068_, boolean p_197069_) {
        Set<Direction8> $$4 = EnumSet.noneOf(Direction8.class);
        Direction8[] var5 = Direction8.values();
        int var6 = var5.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            Direction8 $$5 = var5[var7];
            int $$6 = p_197067_ + $$5.getStepX();
            int $$7 = p_197068_ + $$5.getStepZ();
            if (p_197066_.getChunk($$6, $$7).isOldNoiseGeneration() == p_197069_) {
                $$4.add($$5);
            }
        }

        return $$4;
    }

    private void calculateData(ChunkAccess p_190318_, Set<Direction8> p_190319_) {
        if (!this.hasCalculatedData) {
            if (p_190319_.contains(Direction8.NORTH) || p_190319_.contains(Direction8.WEST) || p_190319_.contains(Direction8.NORTH_WEST)) {
                this.addValuesForColumn(getInsideIndex(0, 0), p_190318_, 0, 0);
            }

            int $$5;
            if (p_190319_.contains(Direction8.NORTH)) {
                for($$5 = 1; $$5 < QUARTS_PER_SECTION; ++$$5) {
                    this.addValuesForColumn(getInsideIndex($$5, 0), p_190318_, 4 * $$5, 0);
                }
            }

            if (p_190319_.contains(Direction8.WEST)) {
                for($$5 = 1; $$5 < QUARTS_PER_SECTION; ++$$5) {
                    this.addValuesForColumn(getInsideIndex(0, $$5), p_190318_, 0, 4 * $$5);
                }
            }

            if (p_190319_.contains(Direction8.EAST)) {
                for($$5 = 1; $$5 < QUARTS_PER_SECTION; ++$$5) {
                    this.addValuesForColumn(getOutsideIndex(CELL_HORIZONTAL_MAX_INDEX_OUTSIDE, $$5), p_190318_, 15, 4 * $$5);
                }
            }

            if (p_190319_.contains(Direction8.SOUTH)) {
                for($$5 = 0; $$5 < QUARTS_PER_SECTION; ++$$5) {
                    this.addValuesForColumn(getOutsideIndex($$5, CELL_HORIZONTAL_MAX_INDEX_OUTSIDE), p_190318_, 4 * $$5, 15);
                }
            }

            if (p_190319_.contains(Direction8.EAST) && p_190319_.contains(Direction8.NORTH_EAST)) {
                this.addValuesForColumn(getOutsideIndex(CELL_HORIZONTAL_MAX_INDEX_OUTSIDE, 0), p_190318_, 15, 0);
            }

            if (p_190319_.contains(Direction8.EAST) && p_190319_.contains(Direction8.SOUTH) && p_190319_.contains(Direction8.SOUTH_EAST)) {
                this.addValuesForColumn(getOutsideIndex(CELL_HORIZONTAL_MAX_INDEX_OUTSIDE, CELL_HORIZONTAL_MAX_INDEX_OUTSIDE), p_190318_, 15, 15);
            }

            this.hasCalculatedData = true;
        }
    }

    private void addValuesForColumn(int p_190300_, ChunkAccess p_190301_, int p_190302_, int p_190303_) {
        if (this.heights[p_190300_] == Double.MAX_VALUE) {
            this.heights[p_190300_] = (double)this.getHeightAtXZ(p_190301_, p_190302_, p_190303_);
        }

        this.densities[p_190300_] = this.getDensityColumn(p_190301_, p_190302_, p_190303_, Mth.floor(this.heights[p_190300_]));
        this.biomes.set(p_190300_, this.getBiomeColumn(p_190301_, p_190302_, p_190303_));
    }

    private int getHeightAtXZ(ChunkAccess p_190311_, int p_190312_, int p_190313_) {
        int $$4;
        if (p_190311_.hasPrimedHeightmap(Types.WORLD_SURFACE_WG)) {
            $$4 = Math.min(p_190311_.getHeight(Types.WORLD_SURFACE_WG, p_190312_, p_190313_) + 1, this.areaWithOldGeneration.getMaxBuildHeight());
        } else {
            $$4 = this.areaWithOldGeneration.getMaxBuildHeight();
        }

        int $$5 = this.areaWithOldGeneration.getMinBuildHeight();
        BlockPos.MutableBlockPos $$6 = new BlockPos.MutableBlockPos(p_190312_, $$4, p_190313_);

        do {
            if ($$6.getY() <= $$5) {
                return $$5;
            }

            $$6.move(Direction.DOWN);
        } while(!SURFACE_BLOCKS.contains(p_190311_.getBlockState($$6).getBlock()));

        return $$6.getY();
    }

    private static double read1(ChunkAccess p_198298_, BlockPos.MutableBlockPos p_198299_) {
        return isGround(p_198298_, p_198299_.move(Direction.DOWN)) ? 1.0 : -1.0;
    }

    private static double read7(ChunkAccess p_198301_, BlockPos.MutableBlockPos p_198302_) {
        double $$2 = 0.0;

        for(int $$3 = 0; $$3 < 7; ++$$3) {
            $$2 += read1(p_198301_, p_198302_);
        }

        return $$2;
    }

    private double[] getDensityColumn(ChunkAccess p_198293_, int p_198294_, int p_198295_, int p_198296_) {
        double[] $$4 = new double[this.cellCountPerColumn()];
        Arrays.fill($$4, -1.0);
        BlockPos.MutableBlockPos $$5 = new BlockPos.MutableBlockPos(p_198294_, this.areaWithOldGeneration.getMaxBuildHeight(), p_198295_);
        double $$6 = read7(p_198293_, $$5);

        int $$10;
        double $$11;
        double $$12;
        for($$10 = $$4.length - 2; $$10 >= 0; --$$10) {
            $$11 = read1(p_198293_, $$5);
            $$12 = read7(p_198293_, $$5);
            $$4[$$10] = ($$6 + $$11 + $$12) / 15.0;
            $$6 = $$12;
        }

        $$10 = this.getCellYIndex(Mth.floorDiv(p_198296_, 8));
        if ($$10 >= 0 && $$10 < $$4.length - 1) {
            $$11 = ((double)p_198296_ + 0.5) % 8.0 / 8.0;
            $$12 = (1.0 - $$11) / $$11;
            double $$13 = Math.max($$12, 1.0) * 0.25;
            $$4[$$10 + 1] = -$$12 / $$13;
            $$4[$$10] = 1.0 / $$13;
        }

        return $$4;
    }

    private List<Holder<Biome>> getBiomeColumn(ChunkAccess p_224758_, int p_224759_, int p_224760_) {
        ObjectArrayList<Holder<Biome>> $$3 = new ObjectArrayList(this.quartCountPerColumn());
        $$3.size(this.quartCountPerColumn());

        for(int $$4 = 0; $$4 < $$3.size(); ++$$4) {
            int $$5 = $$4 + QuartPos.fromBlock(this.areaWithOldGeneration.getMinBuildHeight());
            $$3.set($$4, p_224758_.getNoiseBiome(QuartPos.fromBlock(p_224759_), $$5, QuartPos.fromBlock(p_224760_)));
        }

        return $$3;
    }

    private static boolean isGround(ChunkAccess p_190315_, BlockPos p_190316_) {
        BlockState $$2 = p_190315_.getBlockState(p_190316_);
        if ($$2.isAir()) {
            return false;
        } else if ($$2.is(BlockTags.LEAVES)) {
            return false;
        } else if ($$2.is(BlockTags.LOGS)) {
            return false;
        } else if (!$$2.is(Blocks.BROWN_MUSHROOM_BLOCK) && !$$2.is(Blocks.RED_MUSHROOM_BLOCK)) {
            return !$$2.getCollisionShape(p_190315_, p_190316_).isEmpty();
        } else {
            return false;
        }
    }

    protected double getHeight(int p_190286_, int p_190287_, int p_190288_) {
        if (p_190286_ != CELL_HORIZONTAL_MAX_INDEX_OUTSIDE && p_190288_ != CELL_HORIZONTAL_MAX_INDEX_OUTSIDE) {
            return p_190286_ != 0 && p_190288_ != 0 ? Double.MAX_VALUE : this.heights[getInsideIndex(p_190286_, p_190288_)];
        } else {
            return this.heights[getOutsideIndex(p_190286_, p_190288_)];
        }
    }

    private double getDensity(@Nullable double[] p_190325_, int p_190326_) {
        if (p_190325_ == null) {
            return Double.MAX_VALUE;
        } else {
            int $$2 = this.getCellYIndex(p_190326_);
            return $$2 >= 0 && $$2 < p_190325_.length ? p_190325_[$$2] * 0.1 : Double.MAX_VALUE;
        }
    }

    protected double getDensity(int p_190334_, int p_190335_, int p_190336_) {
        if (p_190335_ == this.getMinY()) {
            return 0.1;
        } else if (p_190334_ != CELL_HORIZONTAL_MAX_INDEX_OUTSIDE && p_190336_ != CELL_HORIZONTAL_MAX_INDEX_OUTSIDE) {
            return p_190334_ != 0 && p_190336_ != 0 ? Double.MAX_VALUE : this.getDensity(this.densities[getInsideIndex(p_190334_, p_190336_)], p_190335_);
        } else {
            return this.getDensity(this.densities[getOutsideIndex(p_190334_, p_190336_)], p_190335_);
        }
    }

    protected void iterateBiomes(int p_224749_, int p_224750_, int p_224751_, BiomeConsumer p_224752_) {
        if (p_224750_ >= QuartPos.fromBlock(this.areaWithOldGeneration.getMinBuildHeight()) && p_224750_ < QuartPos.fromBlock(this.areaWithOldGeneration.getMaxBuildHeight())) {
            int $$4 = p_224750_ - QuartPos.fromBlock(this.areaWithOldGeneration.getMinBuildHeight());

            for(int $$5 = 0; $$5 < this.biomes.size(); ++$$5) {
                if (this.biomes.get($$5) != null) {
                    Holder<Biome> $$6 = (Holder)((List)this.biomes.get($$5)).get($$4);
                    if ($$6 != null) {
                        p_224752_.consume(p_224749_ + getX($$5), p_224751_ + getZ($$5), $$6);
                    }
                }
            }

        }
    }

    protected void iterateHeights(int p_190296_, int p_190297_, HeightConsumer p_190298_) {
        for(int $$3 = 0; $$3 < this.heights.length; ++$$3) {
            double $$4 = this.heights[$$3];
            if ($$4 != Double.MAX_VALUE) {
                p_190298_.consume(p_190296_ + getX($$3), p_190297_ + getZ($$3), $$4);
            }
        }

    }

    protected void iterateDensities(int p_190290_, int p_190291_, int p_190292_, int p_190293_, DensityConsumer p_190294_) {
        int $$5 = this.getColumnMinY();
        int $$6 = Math.max(0, p_190292_ - $$5);
        int $$7 = Math.min(this.cellCountPerColumn(), p_190293_ - $$5);

        for(int $$8 = 0; $$8 < this.densities.length; ++$$8) {
            double[] $$9 = this.densities[$$8];
            if ($$9 != null) {
                int $$10 = p_190290_ + getX($$8);
                int $$11 = p_190291_ + getZ($$8);

                for(int $$12 = $$6; $$12 < $$7; ++$$12) {
                    p_190294_.consume($$10, $$12 + $$5, $$11, $$9[$$12] * 0.1);
                }
            }
        }

    }

    private int cellCountPerColumn() {
        return this.areaWithOldGeneration.getSectionsCount() * 2;
    }

    private int quartCountPerColumn() {
        return QuartPos.fromSection(this.areaWithOldGeneration.getSectionsCount());
    }

    private int getColumnMinY() {
        return this.getMinY() + 1;
    }

    private int getMinY() {
        return this.areaWithOldGeneration.getMinSection() * 2;
    }

    private int getCellYIndex(int p_224747_) {
        return p_224747_ - this.getColumnMinY();
    }

    private static int getInsideIndex(int p_190331_, int p_190332_) {
        return CELL_HORIZONTAL_MAX_INDEX_INSIDE - p_190331_ + p_190332_;
    }

    private static int getOutsideIndex(int p_190351_, int p_190352_) {
        return CELL_COLUMN_INSIDE_COUNT + p_190351_ + CELL_HORIZONTAL_MAX_INDEX_OUTSIDE - p_190352_;
    }

    private static int getX(int p_190349_) {
        if (p_190349_ < CELL_COLUMN_INSIDE_COUNT) {
            return zeroIfNegative(CELL_HORIZONTAL_MAX_INDEX_INSIDE - p_190349_);
        } else {
            int $$1 = p_190349_ - CELL_COLUMN_INSIDE_COUNT;
            return CELL_HORIZONTAL_MAX_INDEX_OUTSIDE - zeroIfNegative(CELL_HORIZONTAL_MAX_INDEX_OUTSIDE - $$1);
        }
    }

    private static int getZ(int p_190355_) {
        if (p_190355_ < CELL_COLUMN_INSIDE_COUNT) {
            return zeroIfNegative(p_190355_ - CELL_HORIZONTAL_MAX_INDEX_INSIDE);
        } else {
            int $$1 = p_190355_ - CELL_COLUMN_INSIDE_COUNT;
            return CELL_HORIZONTAL_MAX_INDEX_OUTSIDE - zeroIfNegative($$1 - CELL_HORIZONTAL_MAX_INDEX_OUTSIDE);
        }
    }

    private static int zeroIfNegative(int p_190357_) {
        return p_190357_ & ~(p_190357_ >> 31);
    }

    public LevelHeightAccessor getAreaWithOldGeneration() {
        return this.areaWithOldGeneration;
    }

    static {
        CELL_HORIZONTAL_MAX_INDEX_INSIDE = QUARTS_PER_SECTION - 1;
        CELL_HORIZONTAL_MAX_INDEX_OUTSIDE = QUARTS_PER_SECTION;
        CELL_COLUMN_INSIDE_COUNT = 2 * CELL_HORIZONTAL_MAX_INDEX_INSIDE + 1;
        CELL_COLUMN_OUTSIDE_COUNT = 2 * CELL_HORIZONTAL_MAX_INDEX_OUTSIDE + 1;
        CELL_COLUMN_COUNT = CELL_COLUMN_INSIDE_COUNT + CELL_COLUMN_OUTSIDE_COUNT;
        SURFACE_BLOCKS = List.of(Blocks.PODZOL, Blocks.GRAVEL, Blocks.GRASS_BLOCK, Blocks.STONE, Blocks.COARSE_DIRT, Blocks.SAND, Blocks.RED_SAND, Blocks.MYCELIUM, Blocks.SNOW_BLOCK, Blocks.TERRACOTTA, Blocks.DIRT);
        DOUBLE_ARRAY_CODEC = Codec.DOUBLE.listOf().xmap(Doubles::toArray, Doubles::asList);
        CODEC = RecordCodecBuilder.create((p_224754_) -> {
            return p_224754_.group(Codec.INT.fieldOf("min_section").forGetter((p_224767_) -> {
                return p_224767_.areaWithOldGeneration.getMinSection();
            }), Codec.INT.fieldOf("max_section").forGetter((p_224765_) -> {
                return p_224765_.areaWithOldGeneration.getMaxSection();
            }), DOUBLE_ARRAY_CODEC.optionalFieldOf("heights").forGetter((p_224762_) -> {
                return DoubleStream.of(p_224762_.heights).anyMatch((p_224745_) -> {
                    return p_224745_ != Double.MAX_VALUE;
                }) ? Optional.of(p_224762_.heights) : Optional.empty();
            })).apply(p_224754_, BlendingData::new);
        }).comapFlatMap(BlendingData::validateArraySize, Function.identity());
    }

    protected interface BiomeConsumer {
        void consume(int var1, int var2, Holder<Biome> var3);
    }

    protected interface HeightConsumer {
        void consume(int var1, int var2, double var3);
    }

    protected interface DensityConsumer {
        void consume(int var1, int var2, int var3, double var4);
    }
}

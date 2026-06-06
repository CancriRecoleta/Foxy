//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.feature;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.feature.configurations.SpikeConfiguration;
import net.minecraft.world.phys.AABB;

public class SpikeFeature extends Feature<SpikeConfiguration> {
    public static final int NUMBER_OF_SPIKES = 10;
    private static final int SPIKE_DISTANCE = 42;
    private static final LoadingCache<Long, List<EndSpike>> SPIKE_CACHE;

    public SpikeFeature(Codec<SpikeConfiguration> p_66852_) {
        super(p_66852_);
    }

    public static List<EndSpike> getSpikesForLevel(WorldGenLevel p_66859_) {
        RandomSource $$1 = RandomSource.create(p_66859_.getSeed());
        long $$2 = $$1.nextLong() & 65535L;
        return (List)SPIKE_CACHE.getUnchecked($$2);
    }

    public boolean place(FeaturePlaceContext<SpikeConfiguration> p_160372_) {
        SpikeConfiguration $$1 = (SpikeConfiguration)p_160372_.config();
        WorldGenLevel $$2 = p_160372_.level();
        RandomSource $$3 = p_160372_.random();
        BlockPos $$4 = p_160372_.origin();
        List<EndSpike> $$5 = $$1.getSpikes();
        if ($$5.isEmpty()) {
            $$5 = getSpikesForLevel($$2);
        }

        Iterator var7 = $$5.iterator();

        while(var7.hasNext()) {
            EndSpike $$6 = (EndSpike)var7.next();
            if ($$6.isCenterWithinChunk($$4)) {
                this.placeSpike($$2, $$3, $$1, $$6);
            }
        }

        return true;
    }

    private void placeSpike(ServerLevelAccessor p_225247_, RandomSource p_225248_, SpikeConfiguration p_225249_, EndSpike p_225250_) {
        int $$4 = p_225250_.getRadius();
        Iterator var6 = BlockPos.betweenClosed(new BlockPos(p_225250_.getCenterX() - $$4, p_225247_.getMinBuildHeight(), p_225250_.getCenterZ() - $$4), new BlockPos(p_225250_.getCenterX() + $$4, p_225250_.getHeight() + 10, p_225250_.getCenterZ() + $$4)).iterator();

        while(true) {
            while(var6.hasNext()) {
                BlockPos $$5 = (BlockPos)var6.next();
                if ($$5.distToLowCornerSqr((double)p_225250_.getCenterX(), (double)$$5.getY(), (double)p_225250_.getCenterZ()) <= (double)($$4 * $$4 + 1) && $$5.getY() < p_225250_.getHeight()) {
                    this.setBlock(p_225247_, $$5, Blocks.OBSIDIAN.defaultBlockState());
                } else if ($$5.getY() > 65) {
                    this.setBlock(p_225247_, $$5, Blocks.AIR.defaultBlockState());
                }
            }

            if (p_225250_.isGuarded()) {
                int $$6 = true;
                int $$7 = true;
                int $$8 = true;
                BlockPos.MutableBlockPos $$9 = new BlockPos.MutableBlockPos();

                for(int $$10 = -2; $$10 <= 2; ++$$10) {
                    for(int $$11 = -2; $$11 <= 2; ++$$11) {
                        for(int $$12 = 0; $$12 <= 3; ++$$12) {
                            boolean $$13 = Mth.abs($$10) == 2;
                            boolean $$14 = Mth.abs($$11) == 2;
                            boolean $$15 = $$12 == 3;
                            if ($$13 || $$14 || $$15) {
                                boolean $$16 = $$10 == -2 || $$10 == 2 || $$15;
                                boolean $$17 = $$11 == -2 || $$11 == 2 || $$15;
                                BlockState $$18 = (BlockState)((BlockState)((BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.NORTH, $$16 && $$11 != -2)).setValue(IronBarsBlock.SOUTH, $$16 && $$11 != 2)).setValue(IronBarsBlock.WEST, $$17 && $$10 != -2)).setValue(IronBarsBlock.EAST, $$17 && $$10 != 2);
                                this.setBlock(p_225247_, $$9.set(p_225250_.getCenterX() + $$10, p_225250_.getHeight() + $$12, p_225250_.getCenterZ() + $$11), $$18);
                            }
                        }
                    }
                }
            }

            EndCrystal $$19 = (EndCrystal)EntityType.END_CRYSTAL.create(p_225247_.getLevel());
            if ($$19 != null) {
                $$19.setBeamTarget(p_225249_.getCrystalBeamTarget());
                $$19.setInvulnerable(p_225249_.isCrystalInvulnerable());
                $$19.moveTo((double)p_225250_.getCenterX() + 0.5, (double)(p_225250_.getHeight() + 1), (double)p_225250_.getCenterZ() + 0.5, p_225248_.nextFloat() * 360.0F, 0.0F);
                p_225247_.addFreshEntity($$19);
                this.setBlock(p_225247_, new BlockPos(p_225250_.getCenterX(), p_225250_.getHeight(), p_225250_.getCenterZ()), Blocks.BEDROCK.defaultBlockState());
            }

            return;
        }
    }

    static {
        SPIKE_CACHE = CacheBuilder.newBuilder().expireAfterWrite(5L, TimeUnit.MINUTES).build(new SpikeCacheLoader());
    }

    public static class EndSpike {
        public static final Codec<EndSpike> CODEC = RecordCodecBuilder.create((p_66890_) -> {
            return p_66890_.group(Codec.INT.fieldOf("centerX").orElse(0).forGetter((p_160382_) -> {
                return p_160382_.centerX;
            }), Codec.INT.fieldOf("centerZ").orElse(0).forGetter((p_160380_) -> {
                return p_160380_.centerZ;
            }), Codec.INT.fieldOf("radius").orElse(0).forGetter((p_160378_) -> {
                return p_160378_.radius;
            }), Codec.INT.fieldOf("height").orElse(0).forGetter((p_160376_) -> {
                return p_160376_.height;
            }), Codec.BOOL.fieldOf("guarded").orElse(false).forGetter((p_160374_) -> {
                return p_160374_.guarded;
            })).apply(p_66890_, EndSpike::new);
        });
        private final int centerX;
        private final int centerZ;
        private final int radius;
        private final int height;
        private final boolean guarded;
        private final AABB topBoundingBox;

        public EndSpike(int p_66881_, int p_66882_, int p_66883_, int p_66884_, boolean p_66885_) {
            this.centerX = p_66881_;
            this.centerZ = p_66882_;
            this.radius = p_66883_;
            this.height = p_66884_;
            this.guarded = p_66885_;
            this.topBoundingBox = new AABB((double)(p_66881_ - p_66883_), (double)DimensionType.MIN_Y, (double)(p_66882_ - p_66883_), (double)(p_66881_ + p_66883_), (double)DimensionType.MAX_Y, (double)(p_66882_ + p_66883_));
        }

        public boolean isCenterWithinChunk(BlockPos p_66892_) {
            return SectionPos.blockToSectionCoord(p_66892_.getX()) == SectionPos.blockToSectionCoord(this.centerX) && SectionPos.blockToSectionCoord(p_66892_.getZ()) == SectionPos.blockToSectionCoord(this.centerZ);
        }

        public int getCenterX() {
            return this.centerX;
        }

        public int getCenterZ() {
            return this.centerZ;
        }

        public int getRadius() {
            return this.radius;
        }

        public int getHeight() {
            return this.height;
        }

        public boolean isGuarded() {
            return this.guarded;
        }

        public AABB getTopBoundingBox() {
            return this.topBoundingBox;
        }
    }

    static class SpikeCacheLoader extends CacheLoader<Long, List<EndSpike>> {
        SpikeCacheLoader() {
        }

        public List<EndSpike> load(Long p_66910_) {
            IntArrayList $$1 = Util.toShuffledList(IntStream.range(0, 10), RandomSource.create(p_66910_));
            List<EndSpike> $$2 = Lists.newArrayList();

            for(int $$3 = 0; $$3 < 10; ++$$3) {
                int $$4 = Mth.floor(42.0 * Math.cos(2.0 * (-3.141592653589793 + 0.3141592653589793 * (double)$$3)));
                int $$5 = Mth.floor(42.0 * Math.sin(2.0 * (-3.141592653589793 + 0.3141592653589793 * (double)$$3)));
                int $$6 = $$1.get($$3);
                int $$7 = 2 + $$6 / 3;
                int $$8 = 76 + $$6 * 3;
                boolean $$9 = $$6 == 1 || $$6 == 2;
                $$2.add(new EndSpike($$4, $$5, $$7, $$8, $$9));
            }

            return $$2;
        }
    }
}

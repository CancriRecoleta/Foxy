//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Column;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.feature.configurations.LargeDripstoneConfiguration;
import net.minecraft.world.phys.Vec3;

public class LargeDripstoneFeature extends Feature<LargeDripstoneConfiguration> {
    public LargeDripstoneFeature(Codec<LargeDripstoneConfiguration> p_159960_) {
        super(p_159960_);
    }

    public boolean place(FeaturePlaceContext<LargeDripstoneConfiguration> p_159967_) {
        WorldGenLevel $$1 = p_159967_.level();
        BlockPos $$2 = p_159967_.origin();
        LargeDripstoneConfiguration $$3 = (LargeDripstoneConfiguration)p_159967_.config();
        RandomSource $$4 = p_159967_.random();
        if (!DripstoneUtils.isEmptyOrWater($$1, $$2)) {
            return false;
        } else {
            Optional<Column> $$5 = Column.scan($$1, $$2, $$3.floorToCeilingSearchRange, DripstoneUtils::isEmptyOrWater, DripstoneUtils::isDripstoneBaseOrLava);
            if ($$5.isPresent() && $$5.get() instanceof Column.Range) {
                Column.Range $$6 = (Column.Range)$$5.get();
                if ($$6.height() < 4) {
                    return false;
                } else {
                    int $$7 = (int)((float)$$6.height() * $$3.maxColumnRadiusToCaveHeightRatio);
                    int $$8 = Mth.clamp($$7, $$3.columnRadius.getMinValue(), $$3.columnRadius.getMaxValue());
                    int $$9 = Mth.randomBetweenInclusive($$4, $$3.columnRadius.getMinValue(), $$8);
                    LargeDripstone $$10 = makeDripstone($$2.atY($$6.ceiling() - 1), false, $$4, $$9, $$3.stalactiteBluntness, $$3.heightScale);
                    LargeDripstone $$11 = makeDripstone($$2.atY($$6.floor() + 1), true, $$4, $$9, $$3.stalagmiteBluntness, $$3.heightScale);
                    WindOffsetter $$13;
                    if ($$10.isSuitableForWind($$3) && $$11.isSuitableForWind($$3)) {
                        $$13 = new WindOffsetter($$2.getY(), $$4, $$3.windSpeed);
                    } else {
                        $$13 = net.minecraft.world.level.levelgen.feature.LargeDripstoneFeature.WindOffsetter.noWind();
                    }

                    boolean $$14 = $$10.moveBackUntilBaseIsInsideStoneAndShrinkRadiusIfNecessary($$1, $$13);
                    boolean $$15 = $$11.moveBackUntilBaseIsInsideStoneAndShrinkRadiusIfNecessary($$1, $$13);
                    if ($$14) {
                        $$10.placeBlocks($$1, $$4, $$13);
                    }

                    if ($$15) {
                        $$11.placeBlocks($$1, $$4, $$13);
                    }

                    return true;
                }
            } else {
                return false;
            }
        }
    }

    private static LargeDripstone makeDripstone(BlockPos p_225139_, boolean p_225140_, RandomSource p_225141_, int p_225142_, FloatProvider p_225143_, FloatProvider p_225144_) {
        return new LargeDripstone(p_225139_, p_225140_, p_225142_, (double)p_225143_.sample(p_225141_), (double)p_225144_.sample(p_225141_));
    }

    private void placeDebugMarkers(WorldGenLevel p_159962_, BlockPos p_159963_, Column.Range p_159964_, WindOffsetter p_159965_) {
        p_159962_.setBlock(p_159965_.offset(p_159963_.atY(p_159964_.ceiling() - 1)), Blocks.DIAMOND_BLOCK.defaultBlockState(), 2);
        p_159962_.setBlock(p_159965_.offset(p_159963_.atY(p_159964_.floor() + 1)), Blocks.GOLD_BLOCK.defaultBlockState(), 2);

        for(BlockPos.MutableBlockPos $$4 = p_159963_.atY(p_159964_.floor() + 2).mutable(); $$4.getY() < p_159964_.ceiling() - 1; $$4.move(Direction.UP)) {
            BlockPos $$5 = p_159965_.offset($$4);
            if (DripstoneUtils.isEmptyOrWater(p_159962_, $$5) || p_159962_.getBlockState($$5).is(Blocks.DRIPSTONE_BLOCK)) {
                p_159962_.setBlock($$5, Blocks.CREEPER_HEAD.defaultBlockState(), 2);
            }
        }

    }

    private static final class LargeDripstone {
        private BlockPos root;
        private final boolean pointingUp;
        private int radius;
        private final double bluntness;
        private final double scale;

        LargeDripstone(BlockPos p_197116_, boolean p_197117_, int p_197118_, double p_197119_, double p_197120_) {
            this.root = p_197116_;
            this.pointingUp = p_197117_;
            this.radius = p_197118_;
            this.bluntness = p_197119_;
            this.scale = p_197120_;
        }

        private int getHeight() {
            return this.getHeightAtRadius(0.0F);
        }

        private int getMinY() {
            return this.pointingUp ? this.root.getY() : this.root.getY() - this.getHeight();
        }

        private int getMaxY() {
            return !this.pointingUp ? this.root.getY() : this.root.getY() + this.getHeight();
        }

        boolean moveBackUntilBaseIsInsideStoneAndShrinkRadiusIfNecessary(WorldGenLevel p_159990_, WindOffsetter p_159991_) {
            while(this.radius > 1) {
                BlockPos.MutableBlockPos $$2 = this.root.mutable();
                int $$3 = Math.min(10, this.getHeight());

                for(int $$4 = 0; $$4 < $$3; ++$$4) {
                    if (p_159990_.getBlockState($$2).is(Blocks.LAVA)) {
                        return false;
                    }

                    if (DripstoneUtils.isCircleMostlyEmbeddedInStone(p_159990_, p_159991_.offset($$2), this.radius)) {
                        this.root = $$2;
                        return true;
                    }

                    $$2.move(this.pointingUp ? Direction.DOWN : Direction.UP);
                }

                this.radius /= 2;
            }

            return false;
        }

        private int getHeightAtRadius(float p_159988_) {
            return (int)DripstoneUtils.getDripstoneHeight((double)p_159988_, (double)this.radius, this.scale, this.bluntness);
        }

        void placeBlocks(WorldGenLevel p_225146_, RandomSource p_225147_, WindOffsetter p_225148_) {
            for(int $$3 = -this.radius; $$3 <= this.radius; ++$$3) {
                for(int $$4 = -this.radius; $$4 <= this.radius; ++$$4) {
                    float $$5 = Mth.sqrt((float)($$3 * $$3 + $$4 * $$4));
                    if (!($$5 > (float)this.radius)) {
                        int $$6 = this.getHeightAtRadius($$5);
                        if ($$6 > 0) {
                            if ((double)p_225147_.nextFloat() < 0.2) {
                                $$6 = (int)((float)$$6 * Mth.randomBetween(p_225147_, 0.8F, 1.0F));
                            }

                            BlockPos.MutableBlockPos $$7 = this.root.offset($$3, 0, $$4).mutable();
                            boolean $$8 = false;
                            int $$9 = this.pointingUp ? p_225146_.getHeight(Types.WORLD_SURFACE_WG, $$7.getX(), $$7.getZ()) : Integer.MAX_VALUE;

                            for(int $$10 = 0; $$10 < $$6 && $$7.getY() < $$9; ++$$10) {
                                BlockPos $$11 = p_225148_.offset($$7);
                                if (DripstoneUtils.isEmptyOrWaterOrLava(p_225146_, $$11)) {
                                    $$8 = true;
                                    Block $$12 = Blocks.DRIPSTONE_BLOCK;
                                    p_225146_.setBlock($$11, $$12.defaultBlockState(), 2);
                                } else if ($$8 && p_225146_.getBlockState($$11).is(BlockTags.BASE_STONE_OVERWORLD)) {
                                    break;
                                }

                                $$7.move(this.pointingUp ? Direction.UP : Direction.DOWN);
                            }
                        }
                    }
                }
            }

        }

        boolean isSuitableForWind(LargeDripstoneConfiguration p_159997_) {
            return this.radius >= p_159997_.minRadiusForWind && this.bluntness >= (double)p_159997_.minBluntnessForWind;
        }
    }

    private static final class WindOffsetter {
        private final int originY;
        @Nullable
        private final Vec3 windSpeed;

        WindOffsetter(int p_225150_, RandomSource p_225151_, FloatProvider p_225152_) {
            this.originY = p_225150_;
            float $$3 = p_225152_.sample(p_225151_);
            float $$4 = Mth.randomBetween(p_225151_, 0.0F, 3.1415927F);
            this.windSpeed = new Vec3((double)(Mth.cos($$4) * $$3), 0.0, (double)(Mth.sin($$4) * $$3));
        }

        private WindOffsetter() {
            this.originY = 0;
            this.windSpeed = null;
        }

        static WindOffsetter noWind() {
            return new WindOffsetter();
        }

        BlockPos offset(BlockPos p_160009_) {
            if (this.windSpeed == null) {
                return p_160009_;
            } else {
                int $$1 = this.originY - p_160009_.getY();
                Vec3 $$2 = this.windSpeed.scale((double)$$1);
                return p_160009_.offset(Mth.floor($$2.x), 0, Mth.floor($$2.z));
            }
        }
    }
}

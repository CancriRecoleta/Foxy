//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class BlueIceFeature extends Feature<NoneFeatureConfiguration> {
    public BlueIceFeature(Codec<NoneFeatureConfiguration> p_65285_) {
        super(p_65285_);
    }

    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> p_159475_) {
        BlockPos $$1 = p_159475_.origin();
        WorldGenLevel $$2 = p_159475_.level();
        RandomSource $$3 = p_159475_.random();
        if ($$1.getY() > $$2.getSeaLevel() - 1) {
            return false;
        } else if (!$$2.getBlockState($$1).is(Blocks.WATER) && !$$2.getBlockState($$1.below()).is(Blocks.WATER)) {
            return false;
        } else {
            boolean $$4 = false;
            Direction[] var6 = Direction.values();
            int $$7 = var6.length;

            int $$8;
            for($$8 = 0; $$8 < $$7; ++$$8) {
                Direction $$5 = var6[$$8];
                if ($$5 != Direction.DOWN && $$2.getBlockState($$1.relative($$5)).is(Blocks.PACKED_ICE)) {
                    $$4 = true;
                    break;
                }
            }

            if (!$$4) {
                return false;
            } else {
                $$2.setBlock($$1, Blocks.BLUE_ICE.defaultBlockState(), 2);

                for(int $$6 = 0; $$6 < 200; ++$$6) {
                    $$7 = $$3.nextInt(5) - $$3.nextInt(6);
                    $$8 = 3;
                    if ($$7 < 2) {
                        $$8 += $$7 / 2;
                    }

                    if ($$8 >= 1) {
                        BlockPos $$9 = $$1.offset($$3.nextInt($$8) - $$3.nextInt($$8), $$7, $$3.nextInt($$8) - $$3.nextInt($$8));
                        BlockState $$10 = $$2.getBlockState($$9);
                        if ($$10.isAir() || $$10.is(Blocks.WATER) || $$10.is(Blocks.PACKED_ICE) || $$10.is(Blocks.ICE)) {
                            Direction[] var11 = Direction.values();
                            int var12 = var11.length;

                            for(int var13 = 0; var13 < var12; ++var13) {
                                Direction $$11 = var11[var13];
                                BlockState $$12 = $$2.getBlockState($$9.relative($$11));
                                if ($$12.is(Blocks.BLUE_ICE)) {
                                    $$2.setBlock($$9, Blocks.BLUE_ICE.defaultBlockState(), 2);
                                    break;
                                }
                            }
                        }
                    }
                }

                return true;
            }
        }
    }
}

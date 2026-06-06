//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.MultifaceGrowthConfiguration;

public class MultifaceGrowthFeature extends Feature<MultifaceGrowthConfiguration> {
    public MultifaceGrowthFeature(Codec<MultifaceGrowthConfiguration> p_225156_) {
        super(p_225156_);
    }

    public boolean place(FeaturePlaceContext<MultifaceGrowthConfiguration> p_225165_) {
        WorldGenLevel $$1 = p_225165_.level();
        BlockPos $$2 = p_225165_.origin();
        RandomSource $$3 = p_225165_.random();
        MultifaceGrowthConfiguration $$4 = (MultifaceGrowthConfiguration)p_225165_.config();
        if (!isAirOrWater($$1.getBlockState($$2))) {
            return false;
        } else {
            List<Direction> $$5 = $$4.getShuffledDirections($$3);
            if (placeGrowthIfPossible($$1, $$2, $$1.getBlockState($$2), $$4, $$3, $$5)) {
                return true;
            } else {
                BlockPos.MutableBlockPos $$6 = $$2.mutable();
                Iterator var8 = $$5.iterator();

                while(var8.hasNext()) {
                    Direction $$7 = (Direction)var8.next();
                    $$6.set($$2);
                    List<Direction> $$8 = $$4.getShuffledDirectionsExcept($$3, $$7.getOpposite());

                    for(int $$9 = 0; $$9 < $$4.searchRange; ++$$9) {
                        $$6.setWithOffset($$2, (Direction)$$7);
                        BlockState $$10 = $$1.getBlockState($$6);
                        if (!isAirOrWater($$10) && !$$10.is($$4.placeBlock)) {
                            break;
                        }

                        if (placeGrowthIfPossible($$1, $$6, $$10, $$4, $$3, $$8)) {
                            return true;
                        }
                    }
                }

                return false;
            }
        }
    }

    public static boolean placeGrowthIfPossible(WorldGenLevel p_225158_, BlockPos p_225159_, BlockState p_225160_, MultifaceGrowthConfiguration p_225161_, RandomSource p_225162_, List<Direction> p_225163_) {
        BlockPos.MutableBlockPos $$6 = p_225159_.mutable();
        Iterator var7 = p_225163_.iterator();

        Direction $$7;
        BlockState $$8;
        do {
            if (!var7.hasNext()) {
                return false;
            }

            $$7 = (Direction)var7.next();
            $$8 = p_225158_.getBlockState($$6.setWithOffset(p_225159_, (Direction)$$7));
        } while(!$$8.is(p_225161_.canBePlacedOn));

        BlockState $$9 = p_225161_.placeBlock.getStateForPlacement(p_225160_, p_225158_, p_225159_, $$7);
        if ($$9 == null) {
            return false;
        } else {
            p_225158_.setBlock(p_225159_, $$9, 3);
            p_225158_.getChunk(p_225159_).markPosForPostprocessing(p_225159_);
            if (p_225162_.nextFloat() < p_225161_.chanceOfSpreading) {
                p_225161_.placeBlock.getSpreader().spreadFromFaceTowardRandomDirection($$9, p_225158_, p_225159_, $$7, p_225162_, true);
            }

            return true;
        }
    }

    private static boolean isAirOrWater(BlockState p_225167_) {
        return p_225167_.isAir() || p_225167_.is(Blocks.WATER);
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class CoralClawFeature extends CoralFeature {
    public CoralClawFeature(Codec<NoneFeatureConfiguration> p_65422_) {
        super(p_65422_);
    }

    protected boolean placeFeature(LevelAccessor p_224959_, RandomSource p_224960_, BlockPos p_224961_, BlockState p_224962_) {
        if (!this.placeCoralBlock(p_224959_, p_224960_, p_224961_, p_224962_)) {
            return false;
        } else {
            Direction $$4 = Plane.HORIZONTAL.getRandomDirection(p_224960_);
            int $$5 = p_224960_.nextInt(2) + 2;
            List<Direction> $$6 = Util.toShuffledList(Stream.of($$4, $$4.getClockWise(), $$4.getCounterClockWise()), p_224960_);
            List<Direction> $$7 = $$6.subList(0, $$5);
            Iterator var9 = $$7.iterator();

            while(var9.hasNext()) {
                Direction $$8 = (Direction)var9.next();
                BlockPos.MutableBlockPos $$9 = p_224961_.mutable();
                int $$10 = p_224960_.nextInt(2) + 1;
                $$9.move($$8);
                int $$15;
                Direction $$14;
                if ($$8 == $$4) {
                    $$14 = $$4;
                    $$15 = p_224960_.nextInt(3) + 2;
                } else {
                    $$9.move(Direction.UP);
                    Direction[] $$13 = new Direction[]{$$8, Direction.UP};
                    $$14 = (Direction)Util.getRandom((Object[])$$13, p_224960_);
                    $$15 = p_224960_.nextInt(3) + 3;
                }

                int $$17;
                for($$17 = 0; $$17 < $$10 && this.placeCoralBlock(p_224959_, p_224960_, $$9, p_224962_); ++$$17) {
                    $$9.move($$14);
                }

                $$9.move($$14.getOpposite());
                $$9.move(Direction.UP);

                for($$17 = 0; $$17 < $$15; ++$$17) {
                    $$9.move($$4);
                    if (!this.placeCoralBlock(p_224959_, p_224960_, $$9, p_224962_)) {
                        break;
                    }

                    if (p_224960_.nextFloat() < 0.25F) {
                        $$9.move(Direction.UP);
                    }
                }
            }

            return true;
        }
    }
}

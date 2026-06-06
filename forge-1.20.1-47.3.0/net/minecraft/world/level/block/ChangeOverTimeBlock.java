//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block;

import java.util.Iterator;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public interface ChangeOverTimeBlock<T extends Enum<T>> {
    int SCAN_DISTANCE = 4;

    Optional<BlockState> getNext(BlockState var1);

    float getChanceModifier();

    default void onRandomTick(BlockState p_220948_, ServerLevel p_220949_, BlockPos p_220950_, RandomSource p_220951_) {
        float $$4 = 0.05688889F;
        if (p_220951_.nextFloat() < 0.05688889F) {
            this.applyChangeOverTime(p_220948_, p_220949_, p_220950_, p_220951_);
        }

    }

    T getAge();

    default void applyChangeOverTime(BlockState p_220953_, ServerLevel p_220954_, BlockPos p_220955_, RandomSource p_220956_) {
        int $$4 = this.getAge().ordinal();
        int $$5 = 0;
        int $$6 = 0;
        Iterator var8 = BlockPos.withinManhattan(p_220955_, 4, 4, 4).iterator();

        while(var8.hasNext()) {
            BlockPos $$7 = (BlockPos)var8.next();
            int $$8 = $$7.distManhattan(p_220955_);
            if ($$8 > 4) {
                break;
            }

            if (!$$7.equals(p_220955_)) {
                BlockState $$9 = p_220954_.getBlockState($$7);
                Block $$10 = $$9.getBlock();
                if ($$10 instanceof ChangeOverTimeBlock) {
                    Enum<?> $$11 = ((ChangeOverTimeBlock)$$10).getAge();
                    if (this.getAge().getClass() == $$11.getClass()) {
                        int $$12 = $$11.ordinal();
                        if ($$12 < $$4) {
                            return;
                        }

                        if ($$12 > $$4) {
                            ++$$6;
                        } else {
                            ++$$5;
                        }
                    }
                }
            }
        }

        float $$13 = (float)($$6 + 1) / (float)($$6 + $$5 + 1);
        float $$14 = $$13 * $$13 * this.getChanceModifier();
        if (p_220956_.nextFloat() < $$14) {
            this.getNext(p_220953_).ifPresent((p_153039_) -> {
                p_220954_.setBlockAndUpdate(p_220955_, p_153039_);
            });
        }

    }
}

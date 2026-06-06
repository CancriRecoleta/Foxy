//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.entity.ai.behavior;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import org.apache.commons.lang3.mutable.MutableInt;

public class SetHiddenState {
    private static final int HIDE_TIMEOUT = 300;

    public SetHiddenState() {
    }

    public static BehaviorControl<LivingEntity> create(int p_259244_, int p_260263_) {
        int $$2 = p_259244_ * 20;
        MutableInt $$3 = new MutableInt(0);
        return BehaviorBuilder.create((p_259055_) -> {
            return p_259055_.group(p_259055_.present(MemoryModuleType.HIDING_PLACE), p_259055_.present(MemoryModuleType.HEARD_BELL_TIME)).apply(p_259055_, (p_260296_, p_260145_) -> {
                return (p_288844_, p_288845_, p_288846_) -> {
                    long $$9 = (Long)p_259055_.get(p_260145_);
                    boolean $$10 = $$9 + 300L <= p_288846_;
                    if ($$3.getValue() <= $$2 && !$$10) {
                        BlockPos $$11 = ((GlobalPos)p_259055_.get(p_260296_)).pos();
                        if ($$11.closerThan(p_288845_.blockPosition(), (double)p_260263_)) {
                            $$3.increment();
                        }

                        return true;
                    } else {
                        p_260145_.erase();
                        p_260296_.erase();
                        p_288845_.getBrain().updateActivityFromSchedule(p_288844_.getDayTime(), p_288844_.getGameTime());
                        $$3.setValue(0);
                        return true;
                    }
                };
            });
        });
    }
}

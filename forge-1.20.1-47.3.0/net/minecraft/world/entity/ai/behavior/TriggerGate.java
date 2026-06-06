//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.entity.ai.behavior;

import com.mojang.datafixers.util.Pair;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.GateBehavior.OrderPolicy;
import net.minecraft.world.entity.ai.behavior.GateBehavior.RunningPolicy;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.Trigger;

public class TriggerGate {
    public TriggerGate() {
    }

    public static <E extends LivingEntity> OneShot<E> triggerOneShuffled(List<Pair<? extends Trigger<? super E>, Integer>> p_259551_) {
        return triggerGate(p_259551_, OrderPolicy.SHUFFLED, RunningPolicy.RUN_ONE);
    }

    public static <E extends LivingEntity> OneShot<E> triggerGate(List<Pair<? extends Trigger<? super E>, Integer>> p_259442_, GateBehavior.OrderPolicy p_259823_, GateBehavior.RunningPolicy p_259632_) {
        ShufflingList<Trigger<? super E>> $$3 = new ShufflingList();
        p_259442_.forEach((p_260333_) -> {
            $$3.add((Trigger)p_260333_.getFirst(), (Integer)p_260333_.getSecond());
        });
        return BehaviorBuilder.create((p_259457_) -> {
            return p_259457_.point((p_260107_, p_259505_, p_259999_) -> {
                if (p_259823_ == OrderPolicy.SHUFFLED) {
                    $$3.shuffle();
                }

                Iterator var7 = $$3.iterator();

                while(var7.hasNext()) {
                    Trigger<? super E> $$6 = (Trigger)var7.next();
                    if ($$6.trigger(p_260107_, p_259505_, p_259999_) && p_259632_ == RunningPolicy.RUN_ONE) {
                        break;
                    }
                }

                return true;
            });
        });
    }
}

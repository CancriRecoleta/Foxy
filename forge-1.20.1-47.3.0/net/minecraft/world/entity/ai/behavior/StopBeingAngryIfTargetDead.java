//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import java.util.UUID;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.GameRules;

public class StopBeingAngryIfTargetDead {
    public StopBeingAngryIfTargetDead() {
    }

    public static BehaviorControl<LivingEntity> create() {
        return BehaviorBuilder.create((p_258814_) -> {
            return p_258814_.group(p_258814_.present(MemoryModuleType.ANGRY_AT)).apply(p_258814_, (p_258813_) -> {
                return (p_258807_, p_258808_, p_258809_) -> {
                    Optional.ofNullable(p_258807_.getEntity((UUID)p_258814_.get(p_258813_))).map((p_258802_) -> {
                        LivingEntity var10000;
                        if (p_258802_ instanceof LivingEntity $$1) {
                            var10000 = $$1;
                        } else {
                            var10000 = null;
                        }

                        return var10000;
                    }).filter(LivingEntity::isDeadOrDying).filter((p_289388_) -> {
                        return p_289388_.getType() != EntityType.PLAYER || p_258807_.getGameRules().getBoolean(GameRules.RULE_FORGIVE_DEAD_PLAYERS);
                    }).ifPresent((p_258811_) -> {
                        p_258813_.erase();
                    });
                    return true;
                };
            });
        });
    }
}

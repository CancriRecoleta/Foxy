//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class HurtBySensor extends Sensor<LivingEntity> {
    public HurtBySensor() {
    }

    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY);
    }

    protected void doTick(ServerLevel p_26670_, LivingEntity p_26671_) {
        Brain<?> $$2 = p_26671_.getBrain();
        DamageSource $$3 = p_26671_.getLastDamageSource();
        if ($$3 != null) {
            $$2.setMemory(MemoryModuleType.HURT_BY, (Object)p_26671_.getLastDamageSource());
            Entity $$4 = $$3.getEntity();
            if ($$4 instanceof LivingEntity) {
                $$2.setMemory(MemoryModuleType.HURT_BY_ENTITY, (Object)((LivingEntity)$$4));
            }
        } else {
            $$2.eraseMemory(MemoryModuleType.HURT_BY);
        }

        $$2.getMemory(MemoryModuleType.HURT_BY_ENTITY).ifPresent((p_289407_) -> {
            if (!p_289407_.isAlive() || p_289407_.level() != p_26670_) {
                $$2.eraseMemory(MemoryModuleType.HURT_BY_ENTITY);
            }

        });
    }
}

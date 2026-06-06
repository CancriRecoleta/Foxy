//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;

public class AdultSensor extends Sensor<AgeableMob> {
    public AdultSensor() {
    }

    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
    }

    protected void doTick(ServerLevel p_148248_, AgeableMob p_148249_) {
        p_148249_.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).ifPresent((p_186145_) -> {
            this.setNearestVisibleAdult(p_148249_, p_186145_);
        });
    }

    private void setNearestVisibleAdult(AgeableMob p_186141_, NearestVisibleLivingEntities p_186142_) {
        Optional var10000 = p_186142_.findClosest((p_289403_) -> {
            return p_289403_.getType() == p_186141_.getType() && !p_289403_.isBaby();
        });
        Objects.requireNonNull(AgeableMob.class);
        Optional<AgeableMob> $$2 = var10000.map(AgeableMob.class::cast);
        p_186141_.getBrain().setMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT, $$2);
    }
}

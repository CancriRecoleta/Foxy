//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;

public class PiglinBruteSpecificSensor extends Sensor<LivingEntity> {
    public PiglinBruteSpecificSensor() {
    }

    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_NEMESIS, MemoryModuleType.NEARBY_ADULT_PIGLINS);
    }

    protected void doTick(ServerLevel p_26721_, LivingEntity p_26722_) {
        Brain<?> $$2 = p_26722_.getBrain();
        List<AbstractPiglin> $$3 = Lists.newArrayList();
        NearestVisibleLivingEntities $$4 = (NearestVisibleLivingEntities)$$2.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).orElse(NearestVisibleLivingEntities.empty());
        Optional var10000 = $$4.findClosest((p_186155_) -> {
            return p_186155_ instanceof WitherSkeleton || p_186155_ instanceof WitherBoss;
        });
        Objects.requireNonNull(Mob.class);
        Optional<Mob> $$5 = var10000.map(Mob.class::cast);
        List<LivingEntity> $$6 = (List)$$2.getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES).orElse(ImmutableList.of());
        Iterator var8 = $$6.iterator();

        while(var8.hasNext()) {
            LivingEntity $$7 = (LivingEntity)var8.next();
            if ($$7 instanceof AbstractPiglin && ((AbstractPiglin)$$7).isAdult()) {
                $$3.add((AbstractPiglin)$$7);
            }
        }

        $$2.setMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS, $$5);
        $$2.setMemory(MemoryModuleType.NEARBY_ADULT_PIGLINS, (Object)$$3);
    }
}

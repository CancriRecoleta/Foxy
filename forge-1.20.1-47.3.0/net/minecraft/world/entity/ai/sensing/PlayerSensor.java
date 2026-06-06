//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.player.Player;

public class PlayerSensor extends Sensor<LivingEntity> {
    public PlayerSensor() {
    }

    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_PLAYERS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER);
    }

    protected void doTick(ServerLevel p_26740_, LivingEntity p_26741_) {
        Stream var10000 = p_26740_.players().stream().filter(EntitySelector.NO_SPECTATORS).filter((p_26744_) -> {
            return p_26741_.closerThan(p_26744_, 16.0);
        });
        Objects.requireNonNull(p_26741_);
        List<Player> $$2 = (List)var10000.sorted(Comparator.comparingDouble(p_26741_::distanceToSqr)).collect(Collectors.toList());
        Brain<?> $$3 = p_26741_.getBrain();
        $$3.setMemory(MemoryModuleType.NEAREST_PLAYERS, (Object)$$2);
        List<Player> $$4 = (List)$$2.stream().filter((p_26747_) -> {
            return isEntityTargetable(p_26741_, p_26747_);
        }).collect(Collectors.toList());
        $$3.setMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER, (Object)($$4.isEmpty() ? null : (Player)$$4.get(0)));
        Optional<Player> $$5 = $$4.stream().filter((p_148304_) -> {
            return isEntityAttackable(p_26741_, p_148304_);
        }).findFirst();
        $$3.setMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, $$5);
    }
}

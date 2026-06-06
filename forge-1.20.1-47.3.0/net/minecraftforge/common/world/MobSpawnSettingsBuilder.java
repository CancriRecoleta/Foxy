//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common.world;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.jetbrains.annotations.Nullable;

public class MobSpawnSettingsBuilder extends MobSpawnSettings.Builder {
    private final Set<MobCategory> typesView;
    private final Set<EntityType<?>> costView;

    public MobSpawnSettingsBuilder(MobSpawnSettings orig) {
        this.typesView = Collections.unmodifiableSet(this.spawners.keySet());
        this.costView = Collections.unmodifiableSet(this.mobSpawnCosts.keySet());
        orig.getSpawnerTypes().forEach((k) -> {
            ((List)this.spawners.get(k)).clear();
            ((List)this.spawners.get(k)).addAll(orig.getMobs(k).unwrap());
        });
        orig.getEntityTypes().forEach((k) -> {
            this.mobSpawnCosts.put(k, orig.getMobSpawnCost(k));
        });
        this.creatureGenerationProbability = orig.getCreatureProbability();
    }

    public Set<MobCategory> getSpawnerTypes() {
        return this.typesView;
    }

    public List<MobSpawnSettings.SpawnerData> getSpawner(MobCategory type) {
        return (List)this.spawners.get(type);
    }

    public Set<EntityType<?>> getEntityTypes() {
        return this.costView;
    }

    @Nullable
    public MobSpawnSettings.@Nullable MobSpawnCost getCost(EntityType<?> type) {
        return (MobSpawnSettings.MobSpawnCost)this.mobSpawnCosts.get(type);
    }

    public float getProbability() {
        return this.creatureGenerationProbability;
    }

    public MobSpawnSettingsBuilder disablePlayerSpawn() {
        return this;
    }
}

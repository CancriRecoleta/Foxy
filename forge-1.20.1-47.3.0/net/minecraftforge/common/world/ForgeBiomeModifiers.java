//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common.world;

import com.mojang.serialization.Codec;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.world.BiomeModifier.Phase;
import net.minecraftforge.registries.ForgeRegistries;

public final class ForgeBiomeModifiers {
    private ForgeBiomeModifiers() {
    }

    public static record RemoveSpawnsBiomeModifier(HolderSet<Biome> biomes, HolderSet<EntityType<?>> entityTypes) implements BiomeModifier {
        public RemoveSpawnsBiomeModifier(HolderSet<Biome> biomes, HolderSet<EntityType<?>> entityTypes) {
            this.biomes = biomes;
            this.entityTypes = entityTypes;
        }

        public void modify(Holder<Biome> biome, BiomeModifier.Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
            if (phase == Phase.REMOVE && this.biomes.contains(biome)) {
                MobSpawnSettingsBuilder spawnBuilder = builder.getMobSpawnSettings();
                MobCategory[] var5 = MobCategory.values();
                int var6 = var5.length;

                for(int var7 = 0; var7 < var6; ++var7) {
                    MobCategory category = var5[var7];
                    List<MobSpawnSettings.SpawnerData> spawns = spawnBuilder.getSpawner(category);
                    spawns.removeIf((spawnerData) -> {
                        return this.entityTypes.contains((Holder)ForgeRegistries.ENTITY_TYPES.getHolder((Object)spawnerData.type).get());
                    });
                }
            }

        }

        public Codec<? extends BiomeModifier> codec() {
            return (Codec)ForgeMod.REMOVE_SPAWNS_BIOME_MODIFIER_TYPE.get();
        }

        public HolderSet<Biome> biomes() {
            return this.biomes;
        }

        public HolderSet<EntityType<?>> entityTypes() {
            return this.entityTypes;
        }
    }

    public static record AddSpawnsBiomeModifier(HolderSet<Biome> biomes, List<MobSpawnSettings.SpawnerData> spawners) implements BiomeModifier {
        public AddSpawnsBiomeModifier(HolderSet<Biome> biomes, List<MobSpawnSettings.SpawnerData> spawners) {
            this.biomes = biomes;
            this.spawners = spawners;
        }

        public static AddSpawnsBiomeModifier singleSpawn(HolderSet<Biome> biomes, MobSpawnSettings.SpawnerData spawner) {
            return new AddSpawnsBiomeModifier(biomes, List.of(spawner));
        }

        public void modify(Holder<Biome> biome, BiomeModifier.Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
            if (phase == Phase.ADD && this.biomes.contains(biome)) {
                MobSpawnSettingsBuilder spawns = builder.getMobSpawnSettings();
                Iterator var5 = this.spawners.iterator();

                while(var5.hasNext()) {
                    MobSpawnSettings.SpawnerData spawner = (MobSpawnSettings.SpawnerData)var5.next();
                    EntityType<?> type = spawner.type;
                    spawns.addSpawn(type.getCategory(), spawner);
                }
            }

        }

        public Codec<? extends BiomeModifier> codec() {
            return (Codec)ForgeMod.ADD_SPAWNS_BIOME_MODIFIER_TYPE.get();
        }

        public HolderSet<Biome> biomes() {
            return this.biomes;
        }

        public List<MobSpawnSettings.SpawnerData> spawners() {
            return this.spawners;
        }
    }

    public static record RemoveFeaturesBiomeModifier(HolderSet<Biome> biomes, HolderSet<PlacedFeature> features, Set<GenerationStep.Decoration> steps) implements BiomeModifier {
        public RemoveFeaturesBiomeModifier(HolderSet<Biome> biomes, HolderSet<PlacedFeature> features, Set<GenerationStep.Decoration> steps) {
            this.biomes = biomes;
            this.features = features;
            this.steps = steps;
        }

        public static RemoveFeaturesBiomeModifier allSteps(HolderSet<Biome> biomes, HolderSet<PlacedFeature> features) {
            return new RemoveFeaturesBiomeModifier(biomes, features, EnumSet.allOf(GenerationStep.Decoration.class));
        }

        public void modify(Holder<Biome> biome, BiomeModifier.Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
            if (phase == Phase.REMOVE && this.biomes.contains(biome)) {
                BiomeGenerationSettingsBuilder generationSettings = builder.getGenerationSettings();
                Iterator var5 = this.steps.iterator();

                while(var5.hasNext()) {
                    GenerationStep.Decoration step = (GenerationStep.Decoration)var5.next();
                    List var10000 = generationSettings.getFeatures(step);
                    HolderSet var10001 = this.features;
                    Objects.requireNonNull(var10001);
                    var10000.removeIf(var10001::contains);
                }
            }

        }

        public Codec<? extends BiomeModifier> codec() {
            return (Codec)ForgeMod.REMOVE_FEATURES_BIOME_MODIFIER_TYPE.get();
        }

        public HolderSet<Biome> biomes() {
            return this.biomes;
        }

        public HolderSet<PlacedFeature> features() {
            return this.features;
        }

        public Set<GenerationStep.Decoration> steps() {
            return this.steps;
        }
    }

    public static record AddFeaturesBiomeModifier(HolderSet<Biome> biomes, HolderSet<PlacedFeature> features, GenerationStep.Decoration step) implements BiomeModifier {
        public AddFeaturesBiomeModifier(HolderSet<Biome> biomes, HolderSet<PlacedFeature> features, GenerationStep.Decoration step) {
            this.biomes = biomes;
            this.features = features;
            this.step = step;
        }

        public void modify(Holder<Biome> biome, BiomeModifier.Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
            if (phase == Phase.ADD && this.biomes.contains(biome)) {
                BiomeGenerationSettingsBuilder generationSettings = builder.getGenerationSettings();
                this.features.forEach((holder) -> {
                    generationSettings.addFeature(this.step, holder);
                });
            }

        }

        public Codec<? extends BiomeModifier> codec() {
            return (Codec)ForgeMod.ADD_FEATURES_BIOME_MODIFIER_TYPE.get();
        }

        public HolderSet<Biome> biomes() {
            return this.biomes;
        }

        public HolderSet<PlacedFeature> features() {
            return this.features;
        }

        public GenerationStep.Decoration step() {
            return this.step;
        }
    }
}

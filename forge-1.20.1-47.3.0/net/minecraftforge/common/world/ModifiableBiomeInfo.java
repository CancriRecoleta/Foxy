//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common.world;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.world.BiomeModifier.Phase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public class ModifiableBiomeInfo {
    @NotNull
    private final @NotNull BiomeInfo originalBiomeInfo;
    @Nullable
    private @Nullable BiomeInfo modifiedBiomeInfo = null;

    public ModifiableBiomeInfo(@NotNull @NotNull BiomeInfo originalBiomeInfo) {
        this.originalBiomeInfo = originalBiomeInfo;
    }

    @NotNull
    public @NotNull BiomeInfo get() {
        return this.modifiedBiomeInfo == null ? this.originalBiomeInfo : this.modifiedBiomeInfo;
    }

    @NotNull
    public @NotNull BiomeInfo getOriginalBiomeInfo() {
        return this.originalBiomeInfo;
    }

    @Nullable
    public @Nullable BiomeInfo getModifiedBiomeInfo() {
        return this.modifiedBiomeInfo;
    }

    @Internal
    public void applyBiomeModifiers(Holder<Biome> biome, List<BiomeModifier> biomeModifiers) {
        if (this.modifiedBiomeInfo != null) {
            throw new IllegalStateException(String.format(Locale.ENGLISH, "Biome %s already modified", biome));
        } else {
            BiomeInfo original = this.getOriginalBiomeInfo();
            BiomeInfo.Builder builder = net.minecraftforge.common.world.ModifiableBiomeInfo.BiomeInfo.Builder.copyOf(original);
            BiomeModifier.Phase[] var5 = Phase.values();
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                BiomeModifier.Phase phase = var5[var7];
                Iterator var9 = biomeModifiers.iterator();

                while(var9.hasNext()) {
                    BiomeModifier modifier = (BiomeModifier)var9.next();
                    modifier.modify(biome, phase, builder);
                }
            }

            this.modifiedBiomeInfo = builder.build();
        }
    }

    public static record BiomeInfo(Biome.ClimateSettings climateSettings, BiomeSpecialEffects effects, BiomeGenerationSettings generationSettings, MobSpawnSettings mobSpawnSettings) {
        public BiomeInfo(Biome.ClimateSettings climateSettings, BiomeSpecialEffects effects, BiomeGenerationSettings generationSettings, MobSpawnSettings mobSpawnSettings) {
            this.climateSettings = climateSettings;
            this.effects = effects;
            this.generationSettings = generationSettings;
            this.mobSpawnSettings = mobSpawnSettings;
        }

        public Biome.ClimateSettings climateSettings() {
            return this.climateSettings;
        }

        public BiomeSpecialEffects effects() {
            return this.effects;
        }

        public BiomeGenerationSettings generationSettings() {
            return this.generationSettings;
        }

        public MobSpawnSettings mobSpawnSettings() {
            return this.mobSpawnSettings;
        }

        public static class Builder {
            private ClimateSettingsBuilder climateSettings;
            private BiomeSpecialEffectsBuilder effects;
            private BiomeGenerationSettingsBuilder generationSettings;
            private MobSpawnSettingsBuilder mobSpawnSettings;

            public static Builder copyOf(BiomeInfo original) {
                ClimateSettingsBuilder climateBuilder = ClimateSettingsBuilder.copyOf(original.climateSettings());
                BiomeSpecialEffectsBuilder effectsBuilder = BiomeSpecialEffectsBuilder.copyOf(original.effects());
                BiomeGenerationSettingsBuilder generationBuilder = new BiomeGenerationSettingsBuilder(original.generationSettings());
                MobSpawnSettingsBuilder mobSpawnBuilder = new MobSpawnSettingsBuilder(original.mobSpawnSettings());
                return new Builder(climateBuilder, effectsBuilder, generationBuilder, mobSpawnBuilder);
            }

            private Builder(ClimateSettingsBuilder climateSettings, BiomeSpecialEffectsBuilder effects, BiomeGenerationSettingsBuilder generationSettings, MobSpawnSettingsBuilder mobSpawnSettings) {
                this.climateSettings = climateSettings;
                this.effects = effects;
                this.generationSettings = generationSettings;
                this.mobSpawnSettings = mobSpawnSettings;
            }

            public BiomeInfo build() {
                return new BiomeInfo(this.climateSettings.build(), this.effects.build(), this.generationSettings.build(), this.mobSpawnSettings.build());
            }

            public ClimateSettingsBuilder getClimateSettings() {
                return this.climateSettings;
            }

            public BiomeSpecialEffectsBuilder getSpecialEffects() {
                return this.effects;
            }

            public BiomeGenerationSettingsBuilder getGenerationSettings() {
                return this.generationSettings;
            }

            public MobSpawnSettingsBuilder getMobSpawnSettings() {
                return this.mobSpawnSettings;
            }
        }
    }
}

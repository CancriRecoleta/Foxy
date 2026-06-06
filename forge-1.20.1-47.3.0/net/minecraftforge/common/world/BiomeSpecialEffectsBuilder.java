//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common.world;

import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.biome.AmbientAdditionsSettings;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;

public class BiomeSpecialEffectsBuilder extends BiomeSpecialEffects.Builder {
    public static BiomeSpecialEffectsBuilder copyOf(BiomeSpecialEffects baseEffects) {
        BiomeSpecialEffectsBuilder builder = create(baseEffects.getFogColor(), baseEffects.getWaterColor(), baseEffects.getWaterFogColor(), baseEffects.getSkyColor());
        builder.grassColorModifier = baseEffects.getGrassColorModifier();
        Optional var10000 = baseEffects.getFoliageColorOverride();
        Objects.requireNonNull(builder);
        var10000.ifPresent(builder::foliageColorOverride);
        var10000 = baseEffects.getGrassColorOverride();
        Objects.requireNonNull(builder);
        var10000.ifPresent(builder::grassColorOverride);
        var10000 = baseEffects.getAmbientParticleSettings();
        Objects.requireNonNull(builder);
        var10000.ifPresent(builder::ambientParticle);
        var10000 = baseEffects.getAmbientLoopSoundEvent();
        Objects.requireNonNull(builder);
        var10000.ifPresent(builder::ambientLoopSound);
        var10000 = baseEffects.getAmbientMoodSettings();
        Objects.requireNonNull(builder);
        var10000.ifPresent(builder::ambientMoodSound);
        var10000 = baseEffects.getAmbientAdditionsSettings();
        Objects.requireNonNull(builder);
        var10000.ifPresent(builder::ambientAdditionsSound);
        var10000 = baseEffects.getBackgroundMusic();
        Objects.requireNonNull(builder);
        var10000.ifPresent(builder::backgroundMusic);
        return builder;
    }

    public static BiomeSpecialEffectsBuilder create(int fogColor, int waterColor, int waterFogColor, int skyColor) {
        return new BiomeSpecialEffectsBuilder(fogColor, waterColor, waterFogColor, skyColor);
    }

    protected BiomeSpecialEffectsBuilder(int fogColor, int waterColor, int waterFogColor, int skyColor) {
        this.fogColor(fogColor);
        this.waterColor(waterColor);
        this.waterFogColor(waterFogColor);
        this.skyColor(skyColor);
    }

    public int getFogColor() {
        return this.fogColor.getAsInt();
    }

    public int waterColor() {
        return this.waterColor.getAsInt();
    }

    public int getWaterFogColor() {
        return this.waterFogColor.getAsInt();
    }

    public int getSkyColor() {
        return this.skyColor.getAsInt();
    }

    public BiomeSpecialEffects.GrassColorModifier getGrassColorModifier() {
        return this.grassColorModifier;
    }

    public Optional<Integer> getFoliageColorOverride() {
        return this.foliageColorOverride;
    }

    public Optional<Integer> getGrassColorOverride() {
        return this.grassColorOverride;
    }

    public Optional<AmbientParticleSettings> getAmbientParticle() {
        return this.ambientParticle;
    }

    public Optional<Holder<SoundEvent>> getAmbientLoopSound() {
        return this.ambientLoopSoundEvent;
    }

    public Optional<AmbientMoodSettings> getAmbientMoodSound() {
        return this.ambientMoodSettings;
    }

    public Optional<AmbientAdditionsSettings> getAmbientAdditionsSound() {
        return this.ambientAdditionsSettings;
    }

    public Optional<Music> getBackgroundMusic() {
        return this.backgroundMusic;
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common.util;

import java.util.function.Supplier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;
import org.jetbrains.annotations.NotNull;

public class ForgeSoundType extends SoundType {
    private final Supplier<SoundEvent> breakSound;
    private final Supplier<SoundEvent> stepSound;
    private final Supplier<SoundEvent> placeSound;
    private final Supplier<SoundEvent> hitSound;
    private final Supplier<SoundEvent> fallSound;

    public ForgeSoundType(float volumeIn, float pitchIn, Supplier<SoundEvent> breakSoundIn, Supplier<SoundEvent> stepSoundIn, Supplier<SoundEvent> placeSoundIn, Supplier<SoundEvent> hitSoundIn, Supplier<SoundEvent> fallSoundIn) {
        super(volumeIn, pitchIn, (SoundEvent)null, (SoundEvent)null, (SoundEvent)null, (SoundEvent)null, (SoundEvent)null);
        this.breakSound = breakSoundIn;
        this.stepSound = stepSoundIn;
        this.placeSound = placeSoundIn;
        this.hitSound = hitSoundIn;
        this.fallSound = fallSoundIn;
    }

    public @NotNull SoundEvent getBreakSound() {
        return (SoundEvent)this.breakSound.get();
    }

    public @NotNull SoundEvent getStepSound() {
        return (SoundEvent)this.stepSound.get();
    }

    public @NotNull SoundEvent getPlaceSound() {
        return (SoundEvent)this.placeSound.get();
    }

    public @NotNull SoundEvent getHitSound() {
        return (SoundEvent)this.hitSound.get();
    }

    public @NotNull SoundEvent getFallSound() {
        return (SoundEvent)this.fallSound.get();
    }
}

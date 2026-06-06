//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.resources.sounds;

import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractSoundInstance implements SoundInstance {
    protected Sound sound;
    protected final SoundSource source;
    protected final ResourceLocation location;
    protected float volume;
    protected float pitch;
    protected double x;
    protected double y;
    protected double z;
    protected boolean looping;
    protected int delay;
    protected SoundInstance.Attenuation attenuation;
    protected boolean relative;
    protected RandomSource random;

    protected AbstractSoundInstance(SoundEvent p_235072_, SoundSource p_235073_, RandomSource p_235074_) {
        this(p_235072_.getLocation(), p_235073_, p_235074_);
    }

    protected AbstractSoundInstance(ResourceLocation p_235068_, SoundSource p_235069_, RandomSource p_235070_) {
        this.volume = 1.0F;
        this.pitch = 1.0F;
        this.attenuation = net.minecraft.client.resources.sounds.SoundInstance.Attenuation.LINEAR;
        this.location = p_235068_;
        this.source = p_235069_;
        this.random = p_235070_;
    }

    public ResourceLocation getLocation() {
        return this.location;
    }

    public WeighedSoundEvents resolve(SoundManager p_119591_) {
        if (this.location.equals(SoundManager.INTENTIONALLY_EMPTY_SOUND_LOCATION)) {
            this.sound = SoundManager.INTENTIONALLY_EMPTY_SOUND;
            return SoundManager.INTENTIONALLY_EMPTY_SOUND_EVENT;
        } else {
            WeighedSoundEvents $$1 = p_119591_.getSoundEvent(this.location);
            if ($$1 == null) {
                this.sound = SoundManager.EMPTY_SOUND;
            } else {
                this.sound = $$1.getSound(this.random);
            }

            return $$1;
        }
    }

    public Sound getSound() {
        return this.sound;
    }

    public SoundSource getSource() {
        return this.source;
    }

    public boolean isLooping() {
        return this.looping;
    }

    public int getDelay() {
        return this.delay;
    }

    public float getVolume() {
        return this.volume * this.sound.getVolume().sample(this.random);
    }

    public float getPitch() {
        return this.pitch * this.sound.getPitch().sample(this.random);
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public SoundInstance.Attenuation getAttenuation() {
        return this.attenuation;
    }

    public boolean isRelative() {
        return this.relative;
    }

    public String toString() {
        return "SoundInstance[" + this.location + "]";
    }
}

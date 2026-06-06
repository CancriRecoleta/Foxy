//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.client.event.sound;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public class PlaySoundEvent extends SoundEvent {
    private final String name;
    private final SoundInstance originalSound;
    private @Nullable SoundInstance sound;

    @Internal
    public PlaySoundEvent(SoundEngine manager, SoundInstance sound) {
        super(manager);
        this.originalSound = sound;
        this.name = sound.getLocation().getPath();
        this.setSound(sound);
    }

    public String getName() {
        return this.name;
    }

    public SoundInstance getOriginalSound() {
        return this.originalSound;
    }

    public @Nullable SoundInstance getSound() {
        return this.sound;
    }

    public void setSound(@Nullable SoundInstance newSound) {
        this.sound = newSound;
    }
}

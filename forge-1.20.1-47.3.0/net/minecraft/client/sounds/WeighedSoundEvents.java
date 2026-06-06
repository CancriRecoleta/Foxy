//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.sounds;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WeighedSoundEvents implements Weighted<Sound> {
    private final List<Weighted<Sound>> list = Lists.newArrayList();
    @Nullable
    private final Component subtitle;

    public WeighedSoundEvents(ResourceLocation p_120446_, @Nullable String p_120447_) {
        this.subtitle = p_120447_ == null ? null : Component.translatable(p_120447_);
    }

    public int getWeight() {
        int $$0 = 0;

        Weighted $$1;
        for(Iterator var2 = this.list.iterator(); var2.hasNext(); $$0 += $$1.getWeight()) {
            $$1 = (Weighted)var2.next();
        }

        return $$0;
    }

    public Sound getSound(RandomSource p_235265_) {
        int $$1 = this.getWeight();
        if (!this.list.isEmpty() && $$1 != 0) {
            int $$2 = p_235265_.nextInt($$1);
            Iterator var4 = this.list.iterator();

            Weighted $$3;
            do {
                if (!var4.hasNext()) {
                    return SoundManager.EMPTY_SOUND;
                }

                $$3 = (Weighted)var4.next();
                $$2 -= $$3.getWeight();
            } while($$2 >= 0);

            return (Sound)$$3.getSound(p_235265_);
        } else {
            return SoundManager.EMPTY_SOUND;
        }
    }

    public void addSound(Weighted<Sound> p_120452_) {
        this.list.add(p_120452_);
    }

    @Nullable
    public Component getSubtitle() {
        return this.subtitle;
    }

    public void preloadIfRequired(SoundEngine p_120450_) {
        Iterator var2 = this.list.iterator();

        while(var2.hasNext()) {
            Weighted<Sound> $$1 = (Weighted)var2.next();
            $$1.preloadIfRequired(p_120450_);
        }

    }
}

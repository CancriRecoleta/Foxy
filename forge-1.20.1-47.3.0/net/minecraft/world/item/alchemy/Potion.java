//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.item.alchemy;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.common.extensions.IForgePotion;

public class Potion implements IForgePotion {
    @Nullable
    private final String name;
    private final ImmutableList<MobEffectInstance> effects;

    public static Potion byName(String p_43490_) {
        return (Potion)BuiltInRegistries.POTION.get(ResourceLocation.tryParse(p_43490_));
    }

    public Potion(MobEffectInstance... p_43487_) {
        this((String)null, p_43487_);
    }

    public Potion(@Nullable String p_43484_, MobEffectInstance... p_43485_) {
        this.name = p_43484_;
        this.effects = ImmutableList.copyOf(p_43485_);
    }

    public String getName(String p_43493_) {
        return p_43493_ + (this.name == null ? BuiltInRegistries.POTION.getKey(this).getPath() : this.name);
    }

    public List<MobEffectInstance> getEffects() {
        return this.effects;
    }

    public boolean hasInstantEffects() {
        if (!this.effects.isEmpty()) {
            UnmodifiableIterator var1 = this.effects.iterator();

            while(var1.hasNext()) {
                MobEffectInstance mobeffectinstance = (MobEffectInstance)var1.next();
                if (mobeffectinstance.getEffect().isInstantenous()) {
                    return true;
                }
            }
        }

        return false;
    }
}

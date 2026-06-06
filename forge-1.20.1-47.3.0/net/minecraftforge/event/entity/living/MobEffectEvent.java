//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.event.entity.living;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event.HasResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MobEffectEvent extends LivingEvent {
    protected final @Nullable MobEffectInstance effectInstance;

    public MobEffectEvent(LivingEntity living, MobEffectInstance effectInstance) {
        super(living);
        this.effectInstance = effectInstance;
    }

    public @Nullable MobEffectInstance getEffectInstance() {
        return this.effectInstance;
    }

    public static class Expired extends MobEffectEvent {
        public Expired(LivingEntity living, MobEffectInstance effectInstance) {
            super(living, effectInstance);
        }
    }

    public static class Added extends MobEffectEvent {
        private final MobEffectInstance oldEffectInstance;
        private final Entity source;

        public Added(LivingEntity living, MobEffectInstance oldEffectInstance, MobEffectInstance newEffectInstance, Entity source) {
            super(living, newEffectInstance);
            this.oldEffectInstance = oldEffectInstance;
            this.source = source;
        }

        public @NotNull MobEffectInstance getEffectInstance() {
            return super.getEffectInstance();
        }

        public @Nullable MobEffectInstance getOldEffectInstance() {
            return this.oldEffectInstance;
        }

        public @Nullable Entity getEffectSource() {
            return this.source;
        }
    }

    @HasResult
    public static class Applicable extends MobEffectEvent {
        public Applicable(LivingEntity living, @NotNull MobEffectInstance effectInstance) {
            super(living, effectInstance);
        }

        public @NotNull MobEffectInstance getEffectInstance() {
            return super.getEffectInstance();
        }
    }

    @Cancelable
    public static class Remove extends MobEffectEvent {
        private final MobEffect effect;

        public Remove(LivingEntity living, MobEffect effect) {
            super(living, living.getEffect(effect));
            this.effect = effect;
        }

        public Remove(LivingEntity living, MobEffectInstance effectInstance) {
            super(living, effectInstance);
            this.effect = effectInstance.getEffect();
        }

        public MobEffect getEffect() {
            return this.effect;
        }

        public @Nullable MobEffectInstance getEffectInstance() {
            return super.getEffectInstance();
        }
    }
}

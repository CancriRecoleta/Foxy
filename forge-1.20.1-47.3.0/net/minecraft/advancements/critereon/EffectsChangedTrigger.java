//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;

public class EffectsChangedTrigger extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("effects_changed");

    public EffectsChangedTrigger() {
    }

    public ResourceLocation getId() {
        return ID;
    }

    public TriggerInstance createInstance(JsonObject p_286892_, ContextAwarePredicate p_286547_, DeserializationContext p_286271_) {
        MobEffectsPredicate $$3 = MobEffectsPredicate.fromJson(p_286892_.get("effects"));
        ContextAwarePredicate $$4 = EntityPredicate.fromJson(p_286892_, "source", p_286271_);
        return new TriggerInstance(p_286547_, $$3, $$4);
    }

    public void trigger(ServerPlayer p_149263_, @Nullable Entity p_149264_) {
        LootContext $$2 = p_149264_ != null ? EntityPredicate.createContext(p_149263_, p_149264_) : null;
        this.trigger(p_149263_, (p_149268_) -> {
            return p_149268_.matches(p_149263_, $$2);
        });
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final MobEffectsPredicate effects;
        private final ContextAwarePredicate source;

        public TriggerInstance(ContextAwarePredicate p_286580_, MobEffectsPredicate p_286820_, ContextAwarePredicate p_286703_) {
            super(EffectsChangedTrigger.ID, p_286580_);
            this.effects = p_286820_;
            this.source = p_286703_;
        }

        public static TriggerInstance hasEffects(MobEffectsPredicate p_26781_) {
            return new TriggerInstance(ContextAwarePredicate.ANY, p_26781_, ContextAwarePredicate.ANY);
        }

        public static TriggerInstance gotEffectsFrom(EntityPredicate p_149278_) {
            return new TriggerInstance(ContextAwarePredicate.ANY, MobEffectsPredicate.ANY, EntityPredicate.wrap(p_149278_));
        }

        public boolean matches(ServerPlayer p_149275_, @Nullable LootContext p_149276_) {
            if (!this.effects.matches((LivingEntity)p_149275_)) {
                return false;
            } else {
                return this.source == ContextAwarePredicate.ANY || p_149276_ != null && this.source.matches(p_149276_);
            }
        }

        public JsonObject serializeToJson(SerializationContext p_26783_) {
            JsonObject $$1 = super.serializeToJson(p_26783_);
            $$1.add("effects", this.effects.serializeToJson());
            $$1.add("source", this.source.toJson(p_26783_));
            return $$1;
        }
    }
}

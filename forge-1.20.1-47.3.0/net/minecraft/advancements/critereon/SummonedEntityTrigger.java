//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;

public class SummonedEntityTrigger extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("summoned_entity");

    public SummonedEntityTrigger() {
    }

    public ResourceLocation getId() {
        return ID;
    }

    public TriggerInstance createInstance(JsonObject p_286669_, ContextAwarePredicate p_286745_, DeserializationContext p_286637_) {
        ContextAwarePredicate $$3 = EntityPredicate.fromJson(p_286669_, "entity", p_286637_);
        return new TriggerInstance(p_286745_, $$3);
    }

    public void trigger(ServerPlayer p_68257_, Entity p_68258_) {
        LootContext $$2 = EntityPredicate.createContext(p_68257_, p_68258_);
        this.trigger(p_68257_, (p_68265_) -> {
            return p_68265_.matches($$2);
        });
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final ContextAwarePredicate entity;

        public TriggerInstance(ContextAwarePredicate p_286853_, ContextAwarePredicate p_286838_) {
            super(SummonedEntityTrigger.ID, p_286853_);
            this.entity = p_286838_;
        }

        public static TriggerInstance summonedEntity(EntityPredicate.Builder p_68276_) {
            return new TriggerInstance(ContextAwarePredicate.ANY, EntityPredicate.wrap(p_68276_.build()));
        }

        public boolean matches(LootContext p_68280_) {
            return this.entity.matches(p_68280_);
        }

        public JsonObject serializeToJson(SerializationContext p_68278_) {
            JsonObject $$1 = super.serializeToJson(p_68278_);
            $$1.add("entity", this.entity.toJson(p_68278_));
            return $$1;
        }
    }
}

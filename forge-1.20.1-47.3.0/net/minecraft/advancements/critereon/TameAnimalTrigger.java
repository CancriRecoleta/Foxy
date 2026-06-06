//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.storage.loot.LootContext;

public class TameAnimalTrigger extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("tame_animal");

    public TameAnimalTrigger() {
    }

    public ResourceLocation getId() {
        return ID;
    }

    public TriggerInstance createInstance(JsonObject p_286910_, ContextAwarePredicate p_286765_, DeserializationContext p_286732_) {
        ContextAwarePredicate $$3 = EntityPredicate.fromJson(p_286910_, "entity", p_286732_);
        return new TriggerInstance(p_286765_, $$3);
    }

    public void trigger(ServerPlayer p_68830_, Animal p_68831_) {
        LootContext $$2 = EntityPredicate.createContext(p_68830_, p_68831_);
        this.trigger(p_68830_, (p_68838_) -> {
            return p_68838_.matches($$2);
        });
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final ContextAwarePredicate entity;

        public TriggerInstance(ContextAwarePredicate p_286593_, ContextAwarePredicate p_286484_) {
            super(TameAnimalTrigger.ID, p_286593_);
            this.entity = p_286484_;
        }

        public static TriggerInstance tamedAnimal() {
            return new TriggerInstance(ContextAwarePredicate.ANY, ContextAwarePredicate.ANY);
        }

        public static TriggerInstance tamedAnimal(EntityPredicate p_68849_) {
            return new TriggerInstance(ContextAwarePredicate.ANY, EntityPredicate.wrap(p_68849_));
        }

        public boolean matches(LootContext p_68853_) {
            return this.entity.matches(p_68853_);
        }

        public JsonObject serializeToJson(SerializationContext p_68851_) {
            JsonObject $$1 = super.serializeToJson(p_68851_);
            $$1.add("entity", this.entity.toJson(p_68851_));
            return $$1;
        }
    }
}

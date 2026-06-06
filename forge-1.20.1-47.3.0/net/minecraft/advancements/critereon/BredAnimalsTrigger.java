//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.storage.loot.LootContext;

public class BredAnimalsTrigger extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("bred_animals");

    public BredAnimalsTrigger() {
    }

    public ResourceLocation getId() {
        return ID;
    }

    public TriggerInstance createInstance(JsonObject p_286457_, ContextAwarePredicate p_286507_, DeserializationContext p_286849_) {
        ContextAwarePredicate $$3 = EntityPredicate.fromJson(p_286457_, "parent", p_286849_);
        ContextAwarePredicate $$4 = EntityPredicate.fromJson(p_286457_, "partner", p_286849_);
        ContextAwarePredicate $$5 = EntityPredicate.fromJson(p_286457_, "child", p_286849_);
        return new TriggerInstance(p_286507_, $$3, $$4, $$5);
    }

    public void trigger(ServerPlayer p_147279_, Animal p_147280_, Animal p_147281_, @Nullable AgeableMob p_147282_) {
        LootContext $$4 = EntityPredicate.createContext(p_147279_, p_147280_);
        LootContext $$5 = EntityPredicate.createContext(p_147279_, p_147281_);
        LootContext $$6 = p_147282_ != null ? EntityPredicate.createContext(p_147279_, p_147282_) : null;
        this.trigger(p_147279_, (p_18653_) -> {
            return p_18653_.matches($$4, $$5, $$6);
        });
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final ContextAwarePredicate parent;
        private final ContextAwarePredicate partner;
        private final ContextAwarePredicate child;

        public TriggerInstance(ContextAwarePredicate p_286459_, ContextAwarePredicate p_286695_, ContextAwarePredicate p_286476_, ContextAwarePredicate p_286433_) {
            super(BredAnimalsTrigger.ID, p_286459_);
            this.parent = p_286695_;
            this.partner = p_286476_;
            this.child = p_286433_;
        }

        public static TriggerInstance bredAnimals() {
            return new TriggerInstance(ContextAwarePredicate.ANY, ContextAwarePredicate.ANY, ContextAwarePredicate.ANY, ContextAwarePredicate.ANY);
        }

        public static TriggerInstance bredAnimals(EntityPredicate.Builder p_18668_) {
            return new TriggerInstance(ContextAwarePredicate.ANY, ContextAwarePredicate.ANY, ContextAwarePredicate.ANY, EntityPredicate.wrap(p_18668_.build()));
        }

        public static TriggerInstance bredAnimals(EntityPredicate p_18670_, EntityPredicate p_18671_, EntityPredicate p_18672_) {
            return new TriggerInstance(ContextAwarePredicate.ANY, EntityPredicate.wrap(p_18670_), EntityPredicate.wrap(p_18671_), EntityPredicate.wrap(p_18672_));
        }

        public boolean matches(LootContext p_18676_, LootContext p_18677_, @Nullable LootContext p_18678_) {
            if (this.child == ContextAwarePredicate.ANY || p_18678_ != null && this.child.matches(p_18678_)) {
                return this.parent.matches(p_18676_) && this.partner.matches(p_18677_) || this.parent.matches(p_18677_) && this.partner.matches(p_18676_);
            } else {
                return false;
            }
        }

        public JsonObject serializeToJson(SerializationContext p_18674_) {
            JsonObject $$1 = super.serializeToJson(p_18674_);
            $$1.add("parent", this.parent.toJson(p_18674_));
            $$1.add("partner", this.partner.toJson(p_18674_));
            $$1.add("child", this.child.toJson(p_18674_));
            return $$1;
        }
    }
}

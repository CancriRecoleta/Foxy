//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.MinMaxBounds.Ints;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.Vec3;

public class TargetBlockTrigger extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("target_hit");

    public TargetBlockTrigger() {
    }

    public ResourceLocation getId() {
        return ID;
    }

    public TriggerInstance createInstance(JsonObject p_286400_, ContextAwarePredicate p_286802_, DeserializationContext p_286826_) {
        MinMaxBounds.Ints $$3 = Ints.fromJson(p_286400_.get("signal_strength"));
        ContextAwarePredicate $$4 = EntityPredicate.fromJson(p_286400_, "projectile", p_286826_);
        return new TriggerInstance(p_286802_, $$3, $$4);
    }

    public void trigger(ServerPlayer p_70212_, Entity p_70213_, Vec3 p_70214_, int p_70215_) {
        LootContext $$4 = EntityPredicate.createContext(p_70212_, p_70213_);
        this.trigger(p_70212_, (p_70224_) -> {
            return p_70224_.matches($$4, p_70214_, p_70215_);
        });
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final MinMaxBounds.Ints signalStrength;
        private final ContextAwarePredicate projectile;

        public TriggerInstance(ContextAwarePredicate p_286385_, MinMaxBounds.Ints p_286505_, ContextAwarePredicate p_286608_) {
            super(TargetBlockTrigger.ID, p_286385_);
            this.signalStrength = p_286505_;
            this.projectile = p_286608_;
        }

        public static TriggerInstance targetHit(MinMaxBounds.Ints p_286700_, ContextAwarePredicate p_286883_) {
            return new TriggerInstance(ContextAwarePredicate.ANY, p_286700_, p_286883_);
        }

        public JsonObject serializeToJson(SerializationContext p_70240_) {
            JsonObject $$1 = super.serializeToJson(p_70240_);
            $$1.add("signal_strength", this.signalStrength.serializeToJson());
            $$1.add("projectile", this.projectile.toJson(p_70240_));
            return $$1;
        }

        public boolean matches(LootContext p_70242_, Vec3 p_70243_, int p_70244_) {
            if (!this.signalStrength.matches(p_70244_)) {
                return false;
            } else {
                return this.projectile.matches(p_70242_);
            }
        }
    }
}

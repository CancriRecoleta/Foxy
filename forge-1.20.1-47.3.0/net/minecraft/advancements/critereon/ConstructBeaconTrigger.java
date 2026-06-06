//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.MinMaxBounds.Ints;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class ConstructBeaconTrigger extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("construct_beacon");

    public ConstructBeaconTrigger() {
    }

    public ResourceLocation getId() {
        return ID;
    }

    public TriggerInstance createInstance(JsonObject p_286465_, ContextAwarePredicate p_286914_, DeserializationContext p_286803_) {
        MinMaxBounds.Ints $$3 = Ints.fromJson(p_286465_.get("level"));
        return new TriggerInstance(p_286914_, $$3);
    }

    public void trigger(ServerPlayer p_148030_, int p_148031_) {
        this.trigger(p_148030_, (p_148028_) -> {
            return p_148028_.matches(p_148031_);
        });
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final MinMaxBounds.Ints level;

        public TriggerInstance(ContextAwarePredicate p_286868_, MinMaxBounds.Ints p_286272_) {
            super(ConstructBeaconTrigger.ID, p_286868_);
            this.level = p_286272_;
        }

        public static TriggerInstance constructedBeacon() {
            return new TriggerInstance(ContextAwarePredicate.ANY, Ints.ANY);
        }

        public static TriggerInstance constructedBeacon(MinMaxBounds.Ints p_22766_) {
            return new TriggerInstance(ContextAwarePredicate.ANY, p_22766_);
        }

        public boolean matches(int p_148033_) {
            return this.level.matches(p_148033_);
        }

        public JsonObject serializeToJson(SerializationContext p_22770_) {
            JsonObject $$1 = super.serializeToJson(p_22770_);
            $$1.add("level", this.level.serializeToJson());
            return $$1;
        }
    }
}

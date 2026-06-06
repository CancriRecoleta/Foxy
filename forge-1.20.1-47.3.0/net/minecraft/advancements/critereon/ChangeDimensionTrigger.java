//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;

public class ChangeDimensionTrigger extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("changed_dimension");

    public ChangeDimensionTrigger() {
    }

    public ResourceLocation getId() {
        return ID;
    }

    public TriggerInstance createInstance(JsonObject p_19762_, ContextAwarePredicate p_286295_, DeserializationContext p_19764_) {
        ResourceKey<Level> $$3 = p_19762_.has("from") ? ResourceKey.create(Registries.DIMENSION, new ResourceLocation(GsonHelper.getAsString(p_19762_, "from"))) : null;
        ResourceKey<Level> $$4 = p_19762_.has("to") ? ResourceKey.create(Registries.DIMENSION, new ResourceLocation(GsonHelper.getAsString(p_19762_, "to"))) : null;
        return new TriggerInstance(p_286295_, $$3, $$4);
    }

    public void trigger(ServerPlayer p_19758_, ResourceKey<Level> p_19759_, ResourceKey<Level> p_19760_) {
        this.trigger(p_19758_, (p_19768_) -> {
            return p_19768_.matches(p_19759_, p_19760_);
        });
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        @Nullable
        private final ResourceKey<Level> from;
        @Nullable
        private final ResourceKey<Level> to;

        public TriggerInstance(ContextAwarePredicate p_286423_, @Nullable ResourceKey<Level> p_286585_, @Nullable ResourceKey<Level> p_286666_) {
            super(ChangeDimensionTrigger.ID, p_286423_);
            this.from = p_286585_;
            this.to = p_286666_;
        }

        public static TriggerInstance changedDimension() {
            return new TriggerInstance(ContextAwarePredicate.ANY, (ResourceKey)null, (ResourceKey)null);
        }

        public static TriggerInstance changedDimension(ResourceKey<Level> p_147561_, ResourceKey<Level> p_147562_) {
            return new TriggerInstance(ContextAwarePredicate.ANY, p_147561_, p_147562_);
        }

        public static TriggerInstance changedDimensionTo(ResourceKey<Level> p_19783_) {
            return new TriggerInstance(ContextAwarePredicate.ANY, (ResourceKey)null, p_19783_);
        }

        public static TriggerInstance changedDimensionFrom(ResourceKey<Level> p_147564_) {
            return new TriggerInstance(ContextAwarePredicate.ANY, p_147564_, (ResourceKey)null);
        }

        public boolean matches(ResourceKey<Level> p_19785_, ResourceKey<Level> p_19786_) {
            if (this.from != null && this.from != p_19785_) {
                return false;
            } else {
                return this.to == null || this.to == p_19786_;
            }
        }

        public JsonObject serializeToJson(SerializationContext p_19781_) {
            JsonObject $$1 = super.serializeToJson(p_19781_);
            if (this.from != null) {
                $$1.addProperty("from", this.from.location().toString());
            }

            if (this.to != null) {
                $$1.addProperty("to", this.to.location().toString());
            }

            return $$1;
        }
    }
}

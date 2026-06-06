//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.alchemy.Potion;

public class BrewedPotionTrigger extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("brewed_potion");

    public BrewedPotionTrigger() {
    }

    public ResourceLocation getId() {
        return ID;
    }

    public TriggerInstance createInstance(JsonObject p_286606_, ContextAwarePredicate p_286420_, DeserializationContext p_286605_) {
        Potion $$3 = null;
        if (p_286606_.has("potion")) {
            ResourceLocation $$4 = new ResourceLocation(GsonHelper.getAsString(p_286606_, "potion"));
            $$3 = (Potion)BuiltInRegistries.POTION.getOptional($$4).orElseThrow(() -> {
                return new JsonSyntaxException("Unknown potion '" + $$4 + "'");
            });
        }

        return new TriggerInstance(p_286420_, $$3);
    }

    public void trigger(ServerPlayer p_19121_, Potion p_19122_) {
        this.trigger(p_19121_, (p_19125_) -> {
            return p_19125_.matches(p_19122_);
        });
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        @Nullable
        private final Potion potion;

        public TriggerInstance(ContextAwarePredicate p_286312_, @Nullable Potion p_286830_) {
            super(BrewedPotionTrigger.ID, p_286312_);
            this.potion = p_286830_;
        }

        public static TriggerInstance brewedPotion() {
            return new TriggerInstance(ContextAwarePredicate.ANY, (Potion)null);
        }

        public boolean matches(Potion p_19142_) {
            return this.potion == null || this.potion == p_19142_;
        }

        public JsonObject serializeToJson(SerializationContext p_19144_) {
            JsonObject $$1 = super.serializeToJson(p_19144_);
            if (this.potion != null) {
                $$1.addProperty("potion", BuiltInRegistries.POTION.getKey(this.potion).toString());
            }

            return $$1;
        }
    }
}

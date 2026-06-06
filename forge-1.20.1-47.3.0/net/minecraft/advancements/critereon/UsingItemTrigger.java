//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class UsingItemTrigger extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("using_item");

    public UsingItemTrigger() {
    }

    public ResourceLocation getId() {
        return ID;
    }

    public TriggerInstance createInstance(JsonObject p_286642_, ContextAwarePredicate p_286670_, DeserializationContext p_286897_) {
        ItemPredicate $$3 = ItemPredicate.fromJson(p_286642_.get("item"));
        return new TriggerInstance(p_286670_, $$3);
    }

    public void trigger(ServerPlayer p_163866_, ItemStack p_163867_) {
        this.trigger(p_163866_, (p_163870_) -> {
            return p_163870_.matches(p_163867_);
        });
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final ItemPredicate item;

        public TriggerInstance(ContextAwarePredicate p_286652_, ItemPredicate p_286296_) {
            super(UsingItemTrigger.ID, p_286652_);
            this.item = p_286296_;
        }

        public static TriggerInstance lookingAt(EntityPredicate.Builder p_163884_, ItemPredicate.Builder p_163885_) {
            return new TriggerInstance(EntityPredicate.wrap(p_163884_.build()), p_163885_.build());
        }

        public boolean matches(ItemStack p_163887_) {
            return this.item.matches(p_163887_);
        }

        public JsonObject serializeToJson(SerializationContext p_163889_) {
            JsonObject $$1 = super.serializeToJson(p_163889_);
            $$1.add("item", this.item.serializeToJson());
            return $$1;
        }
    }
}

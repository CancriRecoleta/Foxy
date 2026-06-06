//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.ItemPredicate.Builder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class UsedTotemTrigger extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("used_totem");

    public UsedTotemTrigger() {
    }

    public ResourceLocation getId() {
        return ID;
    }

    public TriggerInstance createInstance(JsonObject p_286841_, ContextAwarePredicate p_286597_, DeserializationContext p_286414_) {
        ItemPredicate $$3 = ItemPredicate.fromJson(p_286841_.get("item"));
        return new TriggerInstance(p_286597_, $$3);
    }

    public void trigger(ServerPlayer p_74432_, ItemStack p_74433_) {
        this.trigger(p_74432_, (p_74436_) -> {
            return p_74436_.matches(p_74433_);
        });
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final ItemPredicate item;

        public TriggerInstance(ContextAwarePredicate p_286406_, ItemPredicate p_286462_) {
            super(UsedTotemTrigger.ID, p_286406_);
            this.item = p_286462_;
        }

        public static TriggerInstance usedTotem(ItemPredicate p_163725_) {
            return new TriggerInstance(ContextAwarePredicate.ANY, p_163725_);
        }

        public static TriggerInstance usedTotem(ItemLike p_74453_) {
            return new TriggerInstance(ContextAwarePredicate.ANY, Builder.item().of(p_74453_).build());
        }

        public boolean matches(ItemStack p_74451_) {
            return this.item.matches(p_74451_);
        }

        public JsonObject serializeToJson(SerializationContext p_74455_) {
            JsonObject $$1 = super.serializeToJson(p_74455_);
            $$1.add("item", this.item.serializeToJson());
            return $$1;
        }
    }
}

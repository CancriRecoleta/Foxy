//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.MinMaxBounds.Ints;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class ItemDurabilityTrigger extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("item_durability_changed");

    public ItemDurabilityTrigger() {
    }

    public ResourceLocation getId() {
        return ID;
    }

    public TriggerInstance createInstance(JsonObject p_286693_, ContextAwarePredicate p_286383_, DeserializationContext p_286352_) {
        ItemPredicate $$3 = ItemPredicate.fromJson(p_286693_.get("item"));
        MinMaxBounds.Ints $$4 = Ints.fromJson(p_286693_.get("durability"));
        MinMaxBounds.Ints $$5 = Ints.fromJson(p_286693_.get("delta"));
        return new TriggerInstance(p_286383_, $$3, $$4, $$5);
    }

    public void trigger(ServerPlayer p_43670_, ItemStack p_43671_, int p_43672_) {
        this.trigger(p_43670_, (p_43676_) -> {
            return p_43676_.matches(p_43671_, p_43672_);
        });
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final ItemPredicate item;
        private final MinMaxBounds.Ints durability;
        private final MinMaxBounds.Ints delta;

        public TriggerInstance(ContextAwarePredicate p_286731_, ItemPredicate p_286447_, MinMaxBounds.Ints p_286431_, MinMaxBounds.Ints p_286460_) {
            super(ItemDurabilityTrigger.ID, p_286731_);
            this.item = p_286447_;
            this.durability = p_286431_;
            this.delta = p_286460_;
        }

        public static TriggerInstance changedDurability(ItemPredicate p_151287_, MinMaxBounds.Ints p_151288_) {
            return changedDurability(ContextAwarePredicate.ANY, p_151287_, p_151288_);
        }

        public static TriggerInstance changedDurability(ContextAwarePredicate p_286720_, ItemPredicate p_286288_, MinMaxBounds.Ints p_286730_) {
            return new TriggerInstance(p_286720_, p_286288_, p_286730_, Ints.ANY);
        }

        public boolean matches(ItemStack p_43699_, int p_43700_) {
            if (!this.item.matches(p_43699_)) {
                return false;
            } else if (!this.durability.matches(p_43699_.getMaxDamage() - p_43700_)) {
                return false;
            } else {
                return this.delta.matches(p_43699_.getDamageValue() - p_43700_);
            }
        }

        public JsonObject serializeToJson(SerializationContext p_43702_) {
            JsonObject $$1 = super.serializeToJson(p_43702_);
            $$1.add("item", this.item.serializeToJson());
            $$1.add("durability", this.durability.serializeToJson());
            $$1.add("delta", this.delta.serializeToJson());
            return $$1;
        }
    }
}

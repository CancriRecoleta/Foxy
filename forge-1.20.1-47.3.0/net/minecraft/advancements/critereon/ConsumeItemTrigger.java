//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements.critereon;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.MinMaxBounds.Ints;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.ItemLike;

public class ConsumeItemTrigger extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("consume_item");

    public ConsumeItemTrigger() {
    }

    public ResourceLocation getId() {
        return ID;
    }

    public TriggerInstance createInstance(JsonObject p_286724_, ContextAwarePredicate p_286492_, DeserializationContext p_286887_) {
        return new TriggerInstance(p_286492_, ItemPredicate.fromJson(p_286724_.get("item")));
    }

    public void trigger(ServerPlayer p_23683_, ItemStack p_23684_) {
        this.trigger(p_23683_, (p_23687_) -> {
            return p_23687_.matches(p_23684_);
        });
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final ItemPredicate item;

        public TriggerInstance(ContextAwarePredicate p_286663_, ItemPredicate p_286533_) {
            super(ConsumeItemTrigger.ID, p_286663_);
            this.item = p_286533_;
        }

        public static TriggerInstance usedItem() {
            return new TriggerInstance(ContextAwarePredicate.ANY, ItemPredicate.ANY);
        }

        public static TriggerInstance usedItem(ItemPredicate p_148082_) {
            return new TriggerInstance(ContextAwarePredicate.ANY, p_148082_);
        }

        public static TriggerInstance usedItem(ItemLike p_23704_) {
            return new TriggerInstance(ContextAwarePredicate.ANY, new ItemPredicate((TagKey)null, ImmutableSet.of(p_23704_.asItem()), Ints.ANY, Ints.ANY, EnchantmentPredicate.NONE, EnchantmentPredicate.NONE, (Potion)null, NbtPredicate.ANY));
        }

        public boolean matches(ItemStack p_23702_) {
            return this.item.matches(p_23702_);
        }

        public JsonObject serializeToJson(SerializationContext p_23706_) {
            JsonObject $$1 = super.serializeToJson(p_23706_);
            $$1.add("item", this.item.serializeToJson());
            return $$1;
        }
    }
}

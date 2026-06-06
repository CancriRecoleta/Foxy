//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements.critereon;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.minecraft.advancements.critereon.MinMaxBounds.Ints;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.ItemLike;

public class InventoryChangeTrigger extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("inventory_changed");

    public InventoryChangeTrigger() {
    }

    public ResourceLocation getId() {
        return ID;
    }

    public TriggerInstance createInstance(JsonObject p_286555_, ContextAwarePredicate p_286704_, DeserializationContext p_286270_) {
        JsonObject $$3 = GsonHelper.getAsJsonObject(p_286555_, "slots", new JsonObject());
        MinMaxBounds.Ints $$4 = Ints.fromJson($$3.get("occupied"));
        MinMaxBounds.Ints $$5 = Ints.fromJson($$3.get("full"));
        MinMaxBounds.Ints $$6 = Ints.fromJson($$3.get("empty"));
        ItemPredicate[] $$7 = ItemPredicate.fromJsonArray(p_286555_.get("items"));
        return new TriggerInstance(p_286704_, $$4, $$5, $$6, $$7);
    }

    public void trigger(ServerPlayer p_43150_, Inventory p_43151_, ItemStack p_43152_) {
        int $$3 = 0;
        int $$4 = 0;
        int $$5 = 0;

        for(int $$6 = 0; $$6 < p_43151_.getContainerSize(); ++$$6) {
            ItemStack $$7 = p_43151_.getItem($$6);
            if ($$7.isEmpty()) {
                ++$$4;
            } else {
                ++$$5;
                if ($$7.getCount() >= $$7.getMaxStackSize()) {
                    ++$$3;
                }
            }
        }

        this.trigger(p_43150_, p_43151_, p_43152_, $$3, $$4, $$5);
    }

    private void trigger(ServerPlayer p_43154_, Inventory p_43155_, ItemStack p_43156_, int p_43157_, int p_43158_, int p_43159_) {
        this.trigger(p_43154_, (p_43166_) -> {
            return p_43166_.matches(p_43155_, p_43156_, p_43157_, p_43158_, p_43159_);
        });
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final MinMaxBounds.Ints slotsOccupied;
        private final MinMaxBounds.Ints slotsFull;
        private final MinMaxBounds.Ints slotsEmpty;
        private final ItemPredicate[] predicates;

        public TriggerInstance(ContextAwarePredicate p_286286_, MinMaxBounds.Ints p_286313_, MinMaxBounds.Ints p_286767_, MinMaxBounds.Ints p_286601_, ItemPredicate[] p_286380_) {
            super(InventoryChangeTrigger.ID, p_286286_);
            this.slotsOccupied = p_286313_;
            this.slotsFull = p_286767_;
            this.slotsEmpty = p_286601_;
            this.predicates = p_286380_;
        }

        public static TriggerInstance hasItems(ItemPredicate... p_43198_) {
            return new TriggerInstance(ContextAwarePredicate.ANY, Ints.ANY, Ints.ANY, Ints.ANY, p_43198_);
        }

        public static TriggerInstance hasItems(ItemLike... p_43200_) {
            ItemPredicate[] $$1 = new ItemPredicate[p_43200_.length];

            for(int $$2 = 0; $$2 < p_43200_.length; ++$$2) {
                $$1[$$2] = new ItemPredicate((TagKey)null, ImmutableSet.of(p_43200_[$$2].asItem()), Ints.ANY, Ints.ANY, EnchantmentPredicate.NONE, EnchantmentPredicate.NONE, (Potion)null, NbtPredicate.ANY);
            }

            return hasItems($$1);
        }

        public JsonObject serializeToJson(SerializationContext p_43196_) {
            JsonObject $$1 = super.serializeToJson(p_43196_);
            if (!this.slotsOccupied.isAny() || !this.slotsFull.isAny() || !this.slotsEmpty.isAny()) {
                JsonObject $$2 = new JsonObject();
                $$2.add("occupied", this.slotsOccupied.serializeToJson());
                $$2.add("full", this.slotsFull.serializeToJson());
                $$2.add("empty", this.slotsEmpty.serializeToJson());
                $$1.add("slots", $$2);
            }

            if (this.predicates.length > 0) {
                JsonArray $$3 = new JsonArray();
                ItemPredicate[] var4 = this.predicates;
                int var5 = var4.length;

                for(int var6 = 0; var6 < var5; ++var6) {
                    ItemPredicate $$4 = var4[var6];
                    $$3.add($$4.serializeToJson());
                }

                $$1.add("items", $$3);
            }

            return $$1;
        }

        public boolean matches(Inventory p_43187_, ItemStack p_43188_, int p_43189_, int p_43190_, int p_43191_) {
            if (!this.slotsFull.matches(p_43189_)) {
                return false;
            } else if (!this.slotsEmpty.matches(p_43190_)) {
                return false;
            } else if (!this.slotsOccupied.matches(p_43191_)) {
                return false;
            } else {
                int $$5 = this.predicates.length;
                if ($$5 == 0) {
                    return true;
                } else if ($$5 != 1) {
                    List<ItemPredicate> $$6 = new ObjectArrayList(this.predicates);
                    int $$7 = p_43187_.getContainerSize();

                    for(int $$8 = 0; $$8 < $$7; ++$$8) {
                        if ($$6.isEmpty()) {
                            return true;
                        }

                        ItemStack $$9 = p_43187_.getItem($$8);
                        if (!$$9.isEmpty()) {
                            $$6.removeIf((p_43194_) -> {
                                return p_43194_.matches($$9);
                            });
                        }
                    }

                    return $$6.isEmpty();
                } else {
                    return !p_43188_.isEmpty() && this.predicates[0].matches(p_43188_);
                }
            }
        }
    }
}

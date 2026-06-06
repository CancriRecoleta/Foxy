//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class FishingRodHookedTrigger extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("fishing_rod_hooked");

    public FishingRodHookedTrigger() {
    }

    public ResourceLocation getId() {
        return ID;
    }

    public TriggerInstance createInstance(JsonObject p_286350_, ContextAwarePredicate p_286888_, DeserializationContext p_286756_) {
        ItemPredicate $$3 = ItemPredicate.fromJson(p_286350_.get("rod"));
        ContextAwarePredicate $$4 = EntityPredicate.fromJson(p_286350_, "entity", p_286756_);
        ItemPredicate $$5 = ItemPredicate.fromJson(p_286350_.get("item"));
        return new TriggerInstance(p_286888_, $$3, $$4, $$5);
    }

    public void trigger(ServerPlayer p_40417_, ItemStack p_40418_, FishingHook p_40419_, Collection<ItemStack> p_40420_) {
        LootContext $$4 = EntityPredicate.createContext(p_40417_, (Entity)(p_40419_.getHookedIn() != null ? p_40419_.getHookedIn() : p_40419_));
        this.trigger(p_40417_, (p_40425_) -> {
            return p_40425_.matches(p_40418_, $$4, p_40420_);
        });
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final ItemPredicate rod;
        private final ContextAwarePredicate entity;
        private final ItemPredicate item;

        public TriggerInstance(ContextAwarePredicate p_286346_, ItemPredicate p_286539_, ContextAwarePredicate p_286253_, ItemPredicate p_286372_) {
            super(FishingRodHookedTrigger.ID, p_286346_);
            this.rod = p_286539_;
            this.entity = p_286253_;
            this.item = p_286372_;
        }

        public static TriggerInstance fishedItem(ItemPredicate p_40448_, EntityPredicate p_40449_, ItemPredicate p_40450_) {
            return new TriggerInstance(ContextAwarePredicate.ANY, p_40448_, EntityPredicate.wrap(p_40449_), p_40450_);
        }

        public boolean matches(ItemStack p_40444_, LootContext p_40445_, Collection<ItemStack> p_40446_) {
            if (!this.rod.matches(p_40444_)) {
                return false;
            } else if (!this.entity.matches(p_40445_)) {
                return false;
            } else {
                if (this.item != ItemPredicate.ANY) {
                    boolean $$3 = false;
                    Entity $$4 = (Entity)p_40445_.getParamOrNull(LootContextParams.THIS_ENTITY);
                    if ($$4 instanceof ItemEntity) {
                        ItemEntity $$5 = (ItemEntity)$$4;
                        if (this.item.matches($$5.getItem())) {
                            $$3 = true;
                        }
                    }

                    Iterator var8 = p_40446_.iterator();

                    while(var8.hasNext()) {
                        ItemStack $$6 = (ItemStack)var8.next();
                        if (this.item.matches($$6)) {
                            $$3 = true;
                            break;
                        }
                    }

                    if (!$$3) {
                        return false;
                    }
                }

                return true;
            }
        }

        public JsonObject serializeToJson(SerializationContext p_40452_) {
            JsonObject $$1 = super.serializeToJson(p_40452_);
            $$1.add("rod", this.rod.serializeToJson());
            $$1.add("entity", this.entity.toJson(p_40452_));
            $$1.add("item", this.item.serializeToJson());
            return $$1;
        }
    }
}

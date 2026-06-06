//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

public class PickedUpItemTrigger extends SimpleCriterionTrigger<TriggerInstance> {
    private final ResourceLocation id;

    public PickedUpItemTrigger(ResourceLocation p_221296_) {
        this.id = p_221296_;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    protected TriggerInstance createInstance(JsonObject p_286475_, ContextAwarePredicate p_286683_, DeserializationContext p_286255_) {
        ItemPredicate $$3 = ItemPredicate.fromJson(p_286475_.get("item"));
        ContextAwarePredicate $$4 = EntityPredicate.fromJson(p_286475_, "entity", p_286255_);
        return new TriggerInstance(this.id, p_286683_, $$3, $$4);
    }

    public void trigger(ServerPlayer p_221299_, ItemStack p_221300_, @Nullable Entity p_221301_) {
        LootContext $$3 = EntityPredicate.createContext(p_221299_, p_221301_);
        this.trigger(p_221299_, (p_221306_) -> {
            return p_221306_.matches(p_221299_, p_221300_, $$3);
        });
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final ItemPredicate item;
        private final ContextAwarePredicate entity;

        public TriggerInstance(ResourceLocation p_286249_, ContextAwarePredicate p_286258_, ItemPredicate p_286761_, ContextAwarePredicate p_286491_) {
            super(p_286249_, p_286258_);
            this.item = p_286761_;
            this.entity = p_286491_;
        }

        public static TriggerInstance thrownItemPickedUpByEntity(ContextAwarePredicate p_286865_, ItemPredicate p_286788_, ContextAwarePredicate p_286327_) {
            return new TriggerInstance(CriteriaTriggers.THROWN_ITEM_PICKED_UP_BY_ENTITY.getId(), p_286865_, p_286788_, p_286327_);
        }

        public static TriggerInstance thrownItemPickedUpByPlayer(ContextAwarePredicate p_286405_, ItemPredicate p_286518_, ContextAwarePredicate p_286381_) {
            return new TriggerInstance(CriteriaTriggers.THROWN_ITEM_PICKED_UP_BY_PLAYER.getId(), p_286405_, p_286518_, p_286381_);
        }

        public boolean matches(ServerPlayer p_221323_, ItemStack p_221324_, LootContext p_221325_) {
            if (!this.item.matches(p_221324_)) {
                return false;
            } else {
                return this.entity.matches(p_221325_);
            }
        }

        public JsonObject serializeToJson(SerializationContext p_221331_) {
            JsonObject $$1 = super.serializeToJson(p_221331_);
            $$1.add("item", this.item.serializeToJson());
            $$1.add("entity", this.entity.toJson(p_221331_));
            return $$1;
        }
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

public class PlayerInteractTrigger extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("player_interacted_with_entity");

    public PlayerInteractTrigger() {
    }

    public ResourceLocation getId() {
        return ID;
    }

    protected TriggerInstance createInstance(JsonObject p_286617_, ContextAwarePredicate p_286504_, DeserializationContext p_286558_) {
        ItemPredicate $$3 = ItemPredicate.fromJson(p_286617_.get("item"));
        ContextAwarePredicate $$4 = EntityPredicate.fromJson(p_286617_, "entity", p_286558_);
        return new TriggerInstance(p_286504_, $$3, $$4);
    }

    public void trigger(ServerPlayer p_61495_, ItemStack p_61496_, Entity p_61497_) {
        LootContext $$3 = EntityPredicate.createContext(p_61495_, p_61497_);
        this.trigger(p_61495_, (p_61501_) -> {
            return p_61501_.matches(p_61496_, $$3);
        });
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final ItemPredicate item;
        private final ContextAwarePredicate entity;

        public TriggerInstance(ContextAwarePredicate p_286824_, ItemPredicate p_286719_, ContextAwarePredicate p_286219_) {
            super(PlayerInteractTrigger.ID, p_286824_);
            this.item = p_286719_;
            this.entity = p_286219_;
        }

        public static TriggerInstance itemUsedOnEntity(ContextAwarePredicate p_286452_, ItemPredicate.Builder p_286289_, ContextAwarePredicate p_286370_) {
            return new TriggerInstance(p_286452_, p_286289_.build(), p_286370_);
        }

        public static TriggerInstance itemUsedOnEntity(ItemPredicate.Builder p_286235_, ContextAwarePredicate p_286667_) {
            return itemUsedOnEntity(ContextAwarePredicate.ANY, p_286235_, p_286667_);
        }

        public boolean matches(ItemStack p_61522_, LootContext p_61523_) {
            return !this.item.matches(p_61522_) ? false : this.entity.matches(p_61523_);
        }

        public JsonObject serializeToJson(SerializationContext p_61525_) {
            JsonObject $$1 = super.serializeToJson(p_61525_);
            $$1.add("item", this.item.serializeToJson());
            $$1.add("entity", this.entity.toJson(p_61525_));
            return $$1;
        }
    }
}

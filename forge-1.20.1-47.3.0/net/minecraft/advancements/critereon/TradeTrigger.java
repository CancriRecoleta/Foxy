//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

public class TradeTrigger extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("villager_trade");

    public TradeTrigger() {
    }

    public ResourceLocation getId() {
        return ID;
    }

    public TriggerInstance createInstance(JsonObject p_286654_, ContextAwarePredicate p_286835_, DeserializationContext p_286772_) {
        ContextAwarePredicate $$3 = EntityPredicate.fromJson(p_286654_, "villager", p_286772_);
        ItemPredicate $$4 = ItemPredicate.fromJson(p_286654_.get("item"));
        return new TriggerInstance(p_286835_, $$3, $$4);
    }

    public void trigger(ServerPlayer p_70960_, AbstractVillager p_70961_, ItemStack p_70962_) {
        LootContext $$3 = EntityPredicate.createContext(p_70960_, p_70961_);
        this.trigger(p_70960_, (p_70970_) -> {
            return p_70970_.matches($$3, p_70962_);
        });
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final ContextAwarePredicate villager;
        private final ItemPredicate item;

        public TriggerInstance(ContextAwarePredicate p_286523_, ContextAwarePredicate p_286395_, ItemPredicate p_286263_) {
            super(TradeTrigger.ID, p_286523_);
            this.villager = p_286395_;
            this.item = p_286263_;
        }

        public static TriggerInstance tradedWithVillager() {
            return new TriggerInstance(ContextAwarePredicate.ANY, ContextAwarePredicate.ANY, ItemPredicate.ANY);
        }

        public static TriggerInstance tradedWithVillager(EntityPredicate.Builder p_191437_) {
            return new TriggerInstance(EntityPredicate.wrap(p_191437_.build()), ContextAwarePredicate.ANY, ItemPredicate.ANY);
        }

        public boolean matches(LootContext p_70985_, ItemStack p_70986_) {
            if (!this.villager.matches(p_70985_)) {
                return false;
            } else {
                return this.item.matches(p_70986_);
            }
        }

        public JsonObject serializeToJson(SerializationContext p_70983_) {
            JsonObject $$1 = super.serializeToJson(p_70983_);
            $$1.add("item", this.item.serializeToJson());
            $$1.add("villager", this.villager.toJson(p_70983_));
            return $$1;
        }
    }
}

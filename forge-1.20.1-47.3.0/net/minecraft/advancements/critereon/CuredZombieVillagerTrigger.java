//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.storage.loot.LootContext;

public class CuredZombieVillagerTrigger extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("cured_zombie_villager");

    public CuredZombieVillagerTrigger() {
    }

    public ResourceLocation getId() {
        return ID;
    }

    public TriggerInstance createInstance(JsonObject p_286832_, ContextAwarePredicate p_286917_, DeserializationContext p_286335_) {
        ContextAwarePredicate $$3 = EntityPredicate.fromJson(p_286832_, "zombie", p_286335_);
        ContextAwarePredicate $$4 = EntityPredicate.fromJson(p_286832_, "villager", p_286335_);
        return new TriggerInstance(p_286917_, $$3, $$4);
    }

    public void trigger(ServerPlayer p_24275_, Zombie p_24276_, Villager p_24277_) {
        LootContext $$3 = EntityPredicate.createContext(p_24275_, p_24276_);
        LootContext $$4 = EntityPredicate.createContext(p_24275_, p_24277_);
        this.trigger(p_24275_, (p_24285_) -> {
            return p_24285_.matches($$3, $$4);
        });
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final ContextAwarePredicate zombie;
        private final ContextAwarePredicate villager;

        public TriggerInstance(ContextAwarePredicate p_286338_, ContextAwarePredicate p_286686_, ContextAwarePredicate p_286773_) {
            super(CuredZombieVillagerTrigger.ID, p_286338_);
            this.zombie = p_286686_;
            this.villager = p_286773_;
        }

        public static TriggerInstance curedZombieVillager() {
            return new TriggerInstance(ContextAwarePredicate.ANY, ContextAwarePredicate.ANY, ContextAwarePredicate.ANY);
        }

        public boolean matches(LootContext p_24300_, LootContext p_24301_) {
            if (!this.zombie.matches(p_24300_)) {
                return false;
            } else {
                return this.villager.matches(p_24301_);
            }
        }

        public JsonObject serializeToJson(SerializationContext p_24298_) {
            JsonObject $$1 = super.serializeToJson(p_24298_);
            $$1.add("zombie", this.zombie.toJson(p_24298_));
            $$1.add("villager", this.villager.toJson(p_24298_));
            return $$1;
        }
    }
}

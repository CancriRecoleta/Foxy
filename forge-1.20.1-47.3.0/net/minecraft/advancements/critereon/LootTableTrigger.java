//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;

public class LootTableTrigger extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("player_generates_container_loot");

    public LootTableTrigger() {
    }

    public ResourceLocation getId() {
        return ID;
    }

    protected TriggerInstance createInstance(JsonObject p_286915_, ContextAwarePredicate p_286259_, DeserializationContext p_286891_) {
        ResourceLocation $$3 = new ResourceLocation(GsonHelper.getAsString(p_286915_, "loot_table"));
        return new TriggerInstance(p_286259_, $$3);
    }

    public void trigger(ServerPlayer p_54598_, ResourceLocation p_54599_) {
        this.trigger(p_54598_, (p_54606_) -> {
            return p_54606_.matches(p_54599_);
        });
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final ResourceLocation lootTable;

        public TriggerInstance(ContextAwarePredicate p_286834_, ResourceLocation p_286434_) {
            super(LootTableTrigger.ID, p_286834_);
            this.lootTable = p_286434_;
        }

        public static TriggerInstance lootTableUsed(ResourceLocation p_54619_) {
            return new TriggerInstance(ContextAwarePredicate.ANY, p_54619_);
        }

        public boolean matches(ResourceLocation p_54621_) {
            return this.lootTable.equals(p_54621_);
        }

        public JsonObject serializeToJson(SerializationContext p_54617_) {
            JsonObject $$1 = super.serializeToJson(p_54617_);
            $$1.addProperty("loot_table", this.lootTable.toString());
            return $$1;
        }
    }
}

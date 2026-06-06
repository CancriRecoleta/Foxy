//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common.loot;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.common.ToolAction;
import org.jetbrains.annotations.NotNull;

public class CanToolPerformAction implements LootItemCondition {
    public static final LootItemConditionType LOOT_CONDITION_TYPE = new LootItemConditionType(new Serializer());
    final ToolAction action;

    public CanToolPerformAction(ToolAction action) {
        this.action = action;
    }

    public @NotNull LootItemConditionType getType() {
        return LOOT_CONDITION_TYPE;
    }

    public @NotNull Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParams.TOOL);
    }

    public boolean test(LootContext lootContext) {
        ItemStack itemstack = (ItemStack)lootContext.getParamOrNull(LootContextParams.TOOL);
        return itemstack != null && itemstack.canPerformAction(this.action);
    }

    public static LootItemCondition.Builder canToolPerformAction(ToolAction action) {
        return () -> {
            return new CanToolPerformAction(action);
        };
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<CanToolPerformAction> {
        public Serializer() {
        }

        public void serialize(JsonObject json, CanToolPerformAction itemCondition, @NotNull JsonSerializationContext context) {
            json.addProperty("action", itemCondition.action.name());
        }

        public @NotNull CanToolPerformAction deserialize(JsonObject json, @NotNull JsonDeserializationContext context) {
            return new CanToolPerformAction(ToolAction.get(json.get("action").getAsString()));
        }
    }
}

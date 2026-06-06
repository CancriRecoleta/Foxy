//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.Arrays;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;

public class ItemUsedOnLocationTrigger extends SimpleCriterionTrigger<TriggerInstance> {
    final ResourceLocation id;

    public ItemUsedOnLocationTrigger(ResourceLocation p_286779_) {
        this.id = p_286779_;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public TriggerInstance createInstance(JsonObject p_286301_, ContextAwarePredicate p_286748_, DeserializationContext p_286322_) {
        ContextAwarePredicate $$3 = ContextAwarePredicate.fromElement("location", p_286322_, p_286301_.get("location"), LootContextParamSets.ADVANCEMENT_LOCATION);
        if ($$3 == null) {
            throw new JsonParseException("Failed to parse 'location' field");
        } else {
            return new TriggerInstance(this.id, p_286748_, $$3);
        }
    }

    public void trigger(ServerPlayer p_286813_, BlockPos p_286625_, ItemStack p_286620_) {
        ServerLevel $$3 = p_286813_.serverLevel();
        BlockState $$4 = $$3.getBlockState(p_286625_);
        LootParams $$5 = (new LootParams.Builder($$3)).withParameter(LootContextParams.ORIGIN, p_286625_.getCenter()).withParameter(LootContextParams.THIS_ENTITY, p_286813_).withParameter(LootContextParams.BLOCK_STATE, $$4).withParameter(LootContextParams.TOOL, p_286620_).create(LootContextParamSets.ADVANCEMENT_LOCATION);
        LootContext $$6 = (new LootContext.Builder($$5)).create((ResourceLocation)null);
        this.trigger(p_286813_, (p_286596_) -> {
            return p_286596_.matches($$6);
        });
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final ContextAwarePredicate location;

        public TriggerInstance(ResourceLocation p_286265_, ContextAwarePredicate p_286333_, ContextAwarePredicate p_286319_) {
            super(p_286265_, p_286333_);
            this.location = p_286319_;
        }

        public static TriggerInstance placedBlock(Block p_286530_) {
            ContextAwarePredicate $$1 = ContextAwarePredicate.create(LootItemBlockStatePropertyCondition.hasBlockStateProperties(p_286530_).build());
            return new TriggerInstance(CriteriaTriggers.PLACED_BLOCK.id, ContextAwarePredicate.ANY, $$1);
        }

        public static TriggerInstance placedBlock(LootItemCondition.Builder... p_286365_) {
            ContextAwarePredicate $$1 = ContextAwarePredicate.create((LootItemCondition[])Arrays.stream(p_286365_).map(LootItemCondition.Builder::build).toArray((p_286827_) -> {
                return new LootItemCondition[p_286827_];
            }));
            return new TriggerInstance(CriteriaTriggers.PLACED_BLOCK.id, ContextAwarePredicate.ANY, $$1);
        }

        private static TriggerInstance itemUsedOnLocation(LocationPredicate.Builder p_286740_, ItemPredicate.Builder p_286777_, ResourceLocation p_286742_) {
            ContextAwarePredicate $$3 = ContextAwarePredicate.create(LocationCheck.checkLocation(p_286740_).build(), MatchTool.toolMatches(p_286777_).build());
            return new TriggerInstance(p_286742_, ContextAwarePredicate.ANY, $$3);
        }

        public static TriggerInstance itemUsedOnBlock(LocationPredicate.Builder p_286808_, ItemPredicate.Builder p_286486_) {
            return itemUsedOnLocation(p_286808_, p_286486_, CriteriaTriggers.ITEM_USED_ON_BLOCK.id);
        }

        public static TriggerInstance allayDropItemOnBlock(LocationPredicate.Builder p_286325_, ItemPredicate.Builder p_286531_) {
            return itemUsedOnLocation(p_286325_, p_286531_, CriteriaTriggers.ALLAY_DROP_ITEM_ON_BLOCK.id);
        }

        public boolean matches(LootContext p_286800_) {
            return this.location.matches(p_286800_);
        }

        public JsonObject serializeToJson(SerializationContext p_286870_) {
            JsonObject $$1 = super.serializeToJson(p_286870_);
            $$1.add("location", this.location.toJson(p_286870_));
            return $$1;
        }
    }
}

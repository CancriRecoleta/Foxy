//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.Set;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class LootItemBlockStatePropertyCondition implements LootItemCondition {
    final Block block;
    final StatePropertiesPredicate properties;

    LootItemBlockStatePropertyCondition(Block p_81762_, StatePropertiesPredicate p_81763_) {
        this.block = p_81762_;
        this.properties = p_81763_;
    }

    public LootItemConditionType getType() {
        return LootItemConditions.BLOCK_STATE_PROPERTY;
    }

    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParams.BLOCK_STATE);
    }

    public boolean test(LootContext p_81772_) {
        BlockState $$1 = (BlockState)p_81772_.getParamOrNull(LootContextParams.BLOCK_STATE);
        return $$1 != null && $$1.is(this.block) && this.properties.matches($$1);
    }

    public static Builder hasBlockStateProperties(Block p_81770_) {
        return new Builder(p_81770_);
    }

    public static class Builder implements LootItemCondition.Builder {
        private final Block block;
        private StatePropertiesPredicate properties;

        public Builder(Block p_81783_) {
            this.properties = StatePropertiesPredicate.ANY;
            this.block = p_81783_;
        }

        public Builder setProperties(StatePropertiesPredicate.Builder p_81785_) {
            this.properties = p_81785_.build();
            return this;
        }

        public LootItemCondition build() {
            return new LootItemBlockStatePropertyCondition(this.block, this.properties);
        }
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<LootItemBlockStatePropertyCondition> {
        public Serializer() {
        }

        public void serialize(JsonObject p_81795_, LootItemBlockStatePropertyCondition p_81796_, JsonSerializationContext p_81797_) {
            p_81795_.addProperty("block", BuiltInRegistries.BLOCK.getKey(p_81796_.block).toString());
            p_81795_.add("properties", p_81796_.properties.serializeToJson());
        }

        public LootItemBlockStatePropertyCondition deserialize(JsonObject p_81805_, JsonDeserializationContext p_81806_) {
            ResourceLocation $$2 = new ResourceLocation(GsonHelper.getAsString(p_81805_, "block"));
            Block $$3 = (Block)BuiltInRegistries.BLOCK.getOptional($$2).orElseThrow(() -> {
                return new IllegalArgumentException("Can't find block " + $$2);
            });
            StatePropertiesPredicate $$4 = StatePropertiesPredicate.fromJson(p_81805_.get("properties"));
            $$4.checkState($$3.getStateDefinition(), (p_81790_) -> {
                throw new JsonSyntaxException("Block " + $$3 + " has no property " + p_81790_);
            });
            return new LootItemBlockStatePropertyCondition($$3, $$4);
        }
    }
}

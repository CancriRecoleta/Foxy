//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class CopyBlockState extends LootItemConditionalFunction {
    final Block block;
    final Set<Property<?>> properties;

    CopyBlockState(LootItemCondition[] p_80050_, Block p_80051_, Set<Property<?>> p_80052_) {
        super(p_80050_);
        this.block = p_80051_;
        this.properties = p_80052_;
    }

    public LootItemFunctionType getType() {
        return LootItemFunctions.COPY_STATE;
    }

    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParams.BLOCK_STATE);
    }

    protected ItemStack run(ItemStack p_80060_, LootContext p_80061_) {
        BlockState $$2 = (BlockState)p_80061_.getParamOrNull(LootContextParams.BLOCK_STATE);
        if ($$2 != null) {
            CompoundTag $$3 = p_80060_.getOrCreateTag();
            CompoundTag $$5;
            if ($$3.contains("BlockStateTag", 10)) {
                $$5 = $$3.getCompound("BlockStateTag");
            } else {
                $$5 = new CompoundTag();
                $$3.put("BlockStateTag", $$5);
            }

            Stream var10000 = this.properties.stream();
            Objects.requireNonNull($$2);
            var10000.filter($$2::hasProperty).forEach((p_80072_) -> {
                $$5.putString(p_80072_.getName(), serialize($$2, p_80072_));
            });
        }

        return p_80060_;
    }

    public static Builder copyState(Block p_80063_) {
        return new Builder(p_80063_);
    }

    private static <T extends Comparable<T>> String serialize(BlockState p_80065_, Property<T> p_80066_) {
        T $$2 = p_80065_.getValue(p_80066_);
        return p_80066_.getName($$2);
    }

    public static class Builder extends LootItemConditionalFunction.Builder<Builder> {
        private final Block block;
        private final Set<Property<?>> properties = Sets.newHashSet();

        Builder(Block p_80079_) {
            this.block = p_80079_;
        }

        public Builder copy(Property<?> p_80085_) {
            if (!this.block.getStateDefinition().getProperties().contains(p_80085_)) {
                throw new IllegalStateException("Property " + p_80085_ + " is not present on block " + this.block);
            } else {
                this.properties.add(p_80085_);
                return this;
            }
        }

        protected Builder getThis() {
            return this;
        }

        public LootItemFunction build() {
            return new CopyBlockState(this.getConditions(), this.block, this.properties);
        }
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<CopyBlockState> {
        public Serializer() {
        }

        public void serialize(JsonObject p_80097_, CopyBlockState p_80098_, JsonSerializationContext p_80099_) {
            super.serialize(p_80097_, (LootItemConditionalFunction)p_80098_, p_80099_);
            p_80097_.addProperty("block", BuiltInRegistries.BLOCK.getKey(p_80098_.block).toString());
            JsonArray $$3 = new JsonArray();
            p_80098_.properties.forEach((p_80091_) -> {
                $$3.add(p_80091_.getName());
            });
            p_80097_.add("properties", $$3);
        }

        public CopyBlockState deserialize(JsonObject p_80093_, JsonDeserializationContext p_80094_, LootItemCondition[] p_80095_) {
            ResourceLocation $$3 = new ResourceLocation(GsonHelper.getAsString(p_80093_, "block"));
            Block $$4 = (Block)BuiltInRegistries.BLOCK.getOptional($$3).orElseThrow(() -> {
                return new IllegalArgumentException("Can't find block " + $$3);
            });
            StateDefinition<Block, BlockState> $$5 = $$4.getStateDefinition();
            Set<Property<?>> $$6 = Sets.newHashSet();
            JsonArray $$7 = GsonHelper.getAsJsonArray(p_80093_, "properties", (JsonArray)null);
            if ($$7 != null) {
                $$7.forEach((p_80111_) -> {
                    $$6.add($$5.getProperty(GsonHelper.convertToString(p_80111_, "property")));
                });
            }

            return new CopyBlockState(p_80095_, $$4, $$6);
        }
    }
}

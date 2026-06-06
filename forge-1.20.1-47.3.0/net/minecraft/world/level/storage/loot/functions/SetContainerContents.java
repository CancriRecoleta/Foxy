//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetContainerContents extends LootItemConditionalFunction {
    final List<LootPoolEntryContainer> entries;
    final BlockEntityType<?> type;

    SetContainerContents(LootItemCondition[] p_193033_, BlockEntityType<?> p_193034_, List<LootPoolEntryContainer> p_193035_) {
        super(p_193033_);
        this.type = p_193034_;
        this.entries = ImmutableList.copyOf(p_193035_);
    }

    public LootItemFunctionType getType() {
        return LootItemFunctions.SET_CONTENTS;
    }

    public ItemStack run(ItemStack p_80911_, LootContext p_80912_) {
        if (p_80911_.isEmpty()) {
            return p_80911_;
        } else {
            NonNullList<ItemStack> $$2 = NonNullList.create();
            this.entries.forEach((p_80916_) -> {
                p_80916_.expand(p_80912_, (p_287573_) -> {
                    ServerLevel var10001 = p_80912_.getLevel();
                    Objects.requireNonNull($$2);
                    p_287573_.createItemStack(LootTable.createStackSplitter(var10001, $$2::add), p_80912_);
                });
            });
            CompoundTag $$3 = new CompoundTag();
            ContainerHelper.saveAllItems($$3, $$2);
            CompoundTag $$4 = BlockItem.getBlockEntityData(p_80911_);
            if ($$4 == null) {
                $$4 = $$3;
            } else {
                $$4.merge($$3);
            }

            BlockItem.setBlockEntityData(p_80911_, this.type, $$4);
            return p_80911_;
        }
    }

    public void validate(ValidationContext p_80918_) {
        super.validate(p_80918_);

        for(int $$1 = 0; $$1 < this.entries.size(); ++$$1) {
            ((LootPoolEntryContainer)this.entries.get($$1)).validate(p_80918_.forChild(".entry[" + $$1 + "]"));
        }

    }

    public static Builder setContents(BlockEntityType<?> p_193037_) {
        return new Builder(p_193037_);
    }

    public static class Builder extends LootItemConditionalFunction.Builder<Builder> {
        private final List<LootPoolEntryContainer> entries = Lists.newArrayList();
        private final BlockEntityType<?> type;

        public Builder(BlockEntityType<?> p_193040_) {
            this.type = p_193040_;
        }

        protected Builder getThis() {
            return this;
        }

        public Builder withEntry(LootPoolEntryContainer.Builder<?> p_80931_) {
            this.entries.add(p_80931_.build());
            return this;
        }

        public LootItemFunction build() {
            return new SetContainerContents(this.getConditions(), this.type, this.entries);
        }
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<SetContainerContents> {
        public Serializer() {
        }

        public void serialize(JsonObject p_80944_, SetContainerContents p_80945_, JsonSerializationContext p_80946_) {
            super.serialize(p_80944_, (LootItemConditionalFunction)p_80945_, p_80946_);
            p_80944_.addProperty("type", BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(p_80945_.type).toString());
            p_80944_.add("entries", p_80946_.serialize(p_80945_.entries));
        }

        public SetContainerContents deserialize(JsonObject p_80936_, JsonDeserializationContext p_80937_, LootItemCondition[] p_80938_) {
            LootPoolEntryContainer[] $$3 = (LootPoolEntryContainer[])GsonHelper.getAsObject(p_80936_, "entries", p_80937_, LootPoolEntryContainer[].class);
            ResourceLocation $$4 = new ResourceLocation(GsonHelper.getAsString(p_80936_, "type"));
            BlockEntityType<?> $$5 = (BlockEntityType)BuiltInRegistries.BLOCK_ENTITY_TYPE.getOptional($$4).orElseThrow(() -> {
                return new JsonSyntaxException("Unknown block entity type id '" + $$4 + "'");
            });
            return new SetContainerContents(p_80938_, $$5, Arrays.asList($$3));
        }
    }
}

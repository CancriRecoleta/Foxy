//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetLoreFunction extends LootItemConditionalFunction {
    final boolean replace;
    final List<Component> lore;
    @Nullable
    final LootContext.EntityTarget resolutionContext;

    public SetLoreFunction(LootItemCondition[] p_81083_, boolean p_81084_, List<Component> p_81085_, @Nullable LootContext.EntityTarget p_81086_) {
        super(p_81083_);
        this.replace = p_81084_;
        this.lore = ImmutableList.copyOf(p_81085_);
        this.resolutionContext = p_81086_;
    }

    public LootItemFunctionType getType() {
        return LootItemFunctions.SET_LORE;
    }

    public Set<LootContextParam<?>> getReferencedContextParams() {
        return this.resolutionContext != null ? ImmutableSet.of(this.resolutionContext.getParam()) : ImmutableSet.of();
    }

    public ItemStack run(ItemStack p_81089_, LootContext p_81090_) {
        ListTag $$2 = this.getLoreTag(p_81089_, !this.lore.isEmpty());
        if ($$2 != null) {
            if (this.replace) {
                $$2.clear();
            }

            UnaryOperator<Component> $$3 = SetNameFunction.createResolver(p_81090_, this.resolutionContext);
            Stream var10000 = this.lore.stream().map($$3).map(Component.Serializer::toJson).map(StringTag::valueOf);
            Objects.requireNonNull($$2);
            var10000.forEach($$2::add);
        }

        return p_81089_;
    }

    @Nullable
    private ListTag getLoreTag(ItemStack p_81092_, boolean p_81093_) {
        CompoundTag $$4;
        if (p_81092_.hasTag()) {
            $$4 = p_81092_.getTag();
        } else {
            if (!p_81093_) {
                return null;
            }

            $$4 = new CompoundTag();
            p_81092_.setTag($$4);
        }

        CompoundTag $$7;
        if ($$4.contains("display", 10)) {
            $$7 = $$4.getCompound("display");
        } else {
            if (!p_81093_) {
                return null;
            }

            $$7 = new CompoundTag();
            $$4.put("display", $$7);
        }

        if ($$7.contains("Lore", 9)) {
            return $$7.getList("Lore", 8);
        } else if (p_81093_) {
            ListTag $$8 = new ListTag();
            $$7.put("Lore", $$8);
            return $$8;
        } else {
            return null;
        }
    }

    public static Builder setLore() {
        return new Builder();
    }

    public static class Builder extends LootItemConditionalFunction.Builder<Builder> {
        private boolean replace;
        private LootContext.EntityTarget resolutionContext;
        private final List<Component> lore = Lists.newArrayList();

        public Builder() {
        }

        public Builder setReplace(boolean p_165454_) {
            this.replace = p_165454_;
            return this;
        }

        public Builder setResolutionContext(LootContext.EntityTarget p_165450_) {
            this.resolutionContext = p_165450_;
            return this;
        }

        public Builder addLine(Component p_165452_) {
            this.lore.add(p_165452_);
            return this;
        }

        protected Builder getThis() {
            return this;
        }

        public LootItemFunction build() {
            return new SetLoreFunction(this.getConditions(), this.replace, this.lore, this.resolutionContext);
        }
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<SetLoreFunction> {
        public Serializer() {
        }

        public void serialize(JsonObject p_81111_, SetLoreFunction p_81112_, JsonSerializationContext p_81113_) {
            super.serialize(p_81111_, (LootItemConditionalFunction)p_81112_, p_81113_);
            p_81111_.addProperty("replace", p_81112_.replace);
            JsonArray $$3 = new JsonArray();
            Iterator var5 = p_81112_.lore.iterator();

            while(var5.hasNext()) {
                Component $$4 = (Component)var5.next();
                $$3.add(net.minecraft.network.chat.Component.Serializer.toJsonTree($$4));
            }

            p_81111_.add("lore", $$3);
            if (p_81112_.resolutionContext != null) {
                p_81111_.add("entity", p_81113_.serialize(p_81112_.resolutionContext));
            }

        }

        public SetLoreFunction deserialize(JsonObject p_81103_, JsonDeserializationContext p_81104_, LootItemCondition[] p_81105_) {
            boolean $$3 = GsonHelper.getAsBoolean(p_81103_, "replace", false);
            List<Component> $$4 = (List)Streams.stream(GsonHelper.getAsJsonArray(p_81103_, "lore")).map(Component.Serializer::fromJson).collect(ImmutableList.toImmutableList());
            LootContext.EntityTarget $$5 = (LootContext.EntityTarget)GsonHelper.getAsObject(p_81103_, "entity", (Object)null, p_81104_, LootContext.EntityTarget.class);
            return new SetLoreFunction(p_81105_, $$3, $$4, $$5);
        }
    }
}

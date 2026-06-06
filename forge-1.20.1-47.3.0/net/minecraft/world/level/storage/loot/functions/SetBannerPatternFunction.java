//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetBannerPatternFunction extends LootItemConditionalFunction {
    final List<Pair<Holder<BannerPattern>, DyeColor>> patterns;
    final boolean append;

    SetBannerPatternFunction(LootItemCondition[] p_165275_, List<Pair<Holder<BannerPattern>, DyeColor>> p_165276_, boolean p_165277_) {
        super(p_165275_);
        this.patterns = p_165276_;
        this.append = p_165277_;
    }

    protected ItemStack run(ItemStack p_165280_, LootContext p_165281_) {
        CompoundTag $$2 = BlockItem.getBlockEntityData(p_165280_);
        if ($$2 == null) {
            $$2 = new CompoundTag();
        }

        BannerPattern.Builder $$3 = new BannerPattern.Builder();
        List var10000 = this.patterns;
        Objects.requireNonNull($$3);
        var10000.forEach($$3::addPattern);
        ListTag $$4 = $$3.toListTag();
        ListTag $$6;
        if (this.append) {
            $$6 = $$2.getList("Patterns", 10).copy();
            $$6.addAll($$4);
        } else {
            $$6 = $$4;
        }

        $$2.put("Patterns", $$6);
        BlockItem.setBlockEntityData(p_165280_, BlockEntityType.BANNER, $$2);
        return p_165280_;
    }

    public LootItemFunctionType getType() {
        return LootItemFunctions.SET_BANNER_PATTERN;
    }

    public static Builder setBannerPattern(boolean p_165283_) {
        return new Builder(p_165283_);
    }

    public static class Builder extends LootItemConditionalFunction.Builder<Builder> {
        private final ImmutableList.Builder<Pair<Holder<BannerPattern>, DyeColor>> patterns = ImmutableList.builder();
        private final boolean append;

        Builder(boolean p_165287_) {
            this.append = p_165287_;
        }

        protected Builder getThis() {
            return this;
        }

        public LootItemFunction build() {
            return new SetBannerPatternFunction(this.getConditions(), this.patterns.build(), this.append);
        }

        public Builder addPattern(ResourceKey<BannerPattern> p_230996_, DyeColor p_230997_) {
            return this.addPattern((Holder)BuiltInRegistries.BANNER_PATTERN.getHolderOrThrow(p_230996_), p_230997_);
        }

        public Builder addPattern(Holder<BannerPattern> p_230999_, DyeColor p_231000_) {
            this.patterns.add(Pair.of(p_230999_, p_231000_));
            return this;
        }
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<SetBannerPatternFunction> {
        public Serializer() {
        }

        public void serialize(JsonObject p_165307_, SetBannerPatternFunction p_165308_, JsonSerializationContext p_165309_) {
            super.serialize(p_165307_, (LootItemConditionalFunction)p_165308_, p_165309_);
            JsonArray $$3 = new JsonArray();
            p_165308_.patterns.forEach((p_231003_) -> {
                JsonObject $$2 = new JsonObject();
                $$2.addProperty("pattern", ((ResourceKey)((Holder)p_231003_.getFirst()).unwrapKey().orElseThrow(() -> {
                    return new JsonSyntaxException("Unknown pattern: " + p_231003_.getFirst());
                })).location().toString());
                $$2.addProperty("color", ((DyeColor)p_231003_.getSecond()).getName());
                $$3.add($$2);
            });
            p_165307_.add("patterns", $$3);
            p_165307_.addProperty("append", p_165308_.append);
        }

        public SetBannerPatternFunction deserialize(JsonObject p_165299_, JsonDeserializationContext p_165300_, LootItemCondition[] p_165301_) {
            ImmutableList.Builder<Pair<Holder<BannerPattern>, DyeColor>> $$3 = ImmutableList.builder();
            JsonArray $$4 = GsonHelper.getAsJsonArray(p_165299_, "patterns");

            for(int $$5 = 0; $$5 < $$4.size(); ++$$5) {
                JsonObject $$6 = GsonHelper.convertToJsonObject($$4.get($$5), "pattern[" + $$5 + "]");
                String $$7 = GsonHelper.getAsString($$6, "pattern");
                Optional<? extends Holder<BannerPattern>> $$8 = BuiltInRegistries.BANNER_PATTERN.getHolder(ResourceKey.create(Registries.BANNER_PATTERN, new ResourceLocation($$7)));
                if ($$8.isEmpty()) {
                    throw new JsonSyntaxException("Unknown pattern: " + $$7);
                }

                String $$9 = GsonHelper.getAsString($$6, "color");
                DyeColor $$10 = DyeColor.byName($$9, (DyeColor)null);
                if ($$10 == null) {
                    throw new JsonSyntaxException("Unknown color: " + $$9);
                }

                $$3.add(Pair.of((Holder)$$8.get(), $$10));
            }

            boolean $$11 = GsonHelper.getAsBoolean(p_165299_, "append");
            return new SetBannerPatternFunction(p_165301_, $$3.build(), $$11);
        }
    }
}

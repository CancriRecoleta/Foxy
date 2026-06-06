//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements.critereon;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.MinMaxBounds.Ints;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ItemLike;

public class ItemPredicate {
    private static final Map<ResourceLocation, Function<JsonObject, ItemPredicate>> custom_predicates = new HashMap();
    private static final Map<ResourceLocation, Function<JsonObject, ItemPredicate>> unmod_predicates;
    public static final ItemPredicate ANY;
    @Nullable
    private final TagKey<Item> tag;
    @Nullable
    private final Set<Item> items;
    private final MinMaxBounds.Ints count;
    private final MinMaxBounds.Ints durability;
    private final EnchantmentPredicate[] enchantments;
    private final EnchantmentPredicate[] storedEnchantments;
    @Nullable
    private final Potion potion;
    private final NbtPredicate nbt;

    public ItemPredicate() {
        this.tag = null;
        this.items = null;
        this.potion = null;
        this.count = Ints.ANY;
        this.durability = Ints.ANY;
        this.enchantments = EnchantmentPredicate.NONE;
        this.storedEnchantments = EnchantmentPredicate.NONE;
        this.nbt = NbtPredicate.ANY;
    }

    public ItemPredicate(@Nullable TagKey<Item> p_204137_, @Nullable Set<Item> p_204138_, MinMaxBounds.Ints p_204139_, MinMaxBounds.Ints p_204140_, EnchantmentPredicate[] p_204141_, EnchantmentPredicate[] p_204142_, @Nullable Potion p_204143_, NbtPredicate p_204144_) {
        this.tag = p_204137_;
        this.items = p_204138_;
        this.count = p_204139_;
        this.durability = p_204140_;
        this.enchantments = p_204141_;
        this.storedEnchantments = p_204142_;
        this.potion = p_204143_;
        this.nbt = p_204144_;
    }

    public boolean matches(ItemStack p_45050_) {
        if (this == ANY) {
            return true;
        } else if (this.tag != null && !p_45050_.is(this.tag)) {
            return false;
        } else if (this.items != null && !this.items.contains(p_45050_.getItem())) {
            return false;
        } else if (!this.count.matches(p_45050_.getCount())) {
            return false;
        } else if (!this.durability.isAny() && !p_45050_.isDamageableItem()) {
            return false;
        } else if (!this.durability.matches(p_45050_.getMaxDamage() - p_45050_.getDamageValue())) {
            return false;
        } else if (!this.nbt.matches(p_45050_)) {
            return false;
        } else {
            Map map1;
            EnchantmentPredicate[] var3;
            int var4;
            int var5;
            EnchantmentPredicate enchantmentpredicate1;
            if (this.enchantments.length > 0) {
                map1 = p_45050_.getAllEnchantments();
                var3 = this.enchantments;
                var4 = var3.length;

                for(var5 = 0; var5 < var4; ++var5) {
                    enchantmentpredicate1 = var3[var5];
                    if (!enchantmentpredicate1.containedIn(map1)) {
                        return false;
                    }
                }
            }

            if (this.storedEnchantments.length > 0) {
                map1 = EnchantmentHelper.deserializeEnchantments(EnchantedBookItem.getEnchantments(p_45050_));
                var3 = this.storedEnchantments;
                var4 = var3.length;

                for(var5 = 0; var5 < var4; ++var5) {
                    enchantmentpredicate1 = var3[var5];
                    if (!enchantmentpredicate1.containedIn(map1)) {
                        return false;
                    }
                }
            }

            Potion potion = PotionUtils.getPotion(p_45050_);
            return this.potion == null || this.potion == potion;
        }
    }

    public static ItemPredicate fromJson(@Nullable JsonElement p_45052_) {
        if (p_45052_ != null && !p_45052_.isJsonNull()) {
            JsonObject jsonobject = GsonHelper.convertToJsonObject(p_45052_, "item");
            if (jsonobject.has("type")) {
                ResourceLocation rl = new ResourceLocation(GsonHelper.getAsString(jsonobject, "type"));
                if (custom_predicates.containsKey(rl)) {
                    return (ItemPredicate)((Function)custom_predicates.get(rl)).apply(jsonobject);
                } else {
                    throw new JsonSyntaxException("There is no ItemPredicate of type " + rl);
                }
            } else {
                MinMaxBounds.Ints minmaxbounds$ints = Ints.fromJson(jsonobject.get("count"));
                MinMaxBounds.Ints minmaxbounds$ints1 = Ints.fromJson(jsonobject.get("durability"));
                if (jsonobject.has("data")) {
                    throw new JsonParseException("Disallowed data tag found");
                } else {
                    NbtPredicate nbtpredicate = NbtPredicate.fromJson(jsonobject.get("nbt"));
                    Set<Item> set = null;
                    JsonArray jsonarray = GsonHelper.getAsJsonArray(jsonobject, "items", (JsonArray)null);
                    if (jsonarray != null) {
                        ImmutableSet.Builder<Item> builder = ImmutableSet.builder();
                        Iterator var8 = jsonarray.iterator();

                        while(var8.hasNext()) {
                            JsonElement jsonelement = (JsonElement)var8.next();
                            ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.convertToString(jsonelement, "item"));
                            builder.add((Item)BuiltInRegistries.ITEM.getOptional(resourcelocation).orElseThrow(() -> {
                                return new JsonSyntaxException("Unknown item id '" + resourcelocation + "'");
                            }));
                        }

                        set = builder.build();
                    }

                    TagKey<Item> tagkey = null;
                    if (jsonobject.has("tag")) {
                        ResourceLocation resourcelocation1 = new ResourceLocation(GsonHelper.getAsString(jsonobject, "tag"));
                        tagkey = TagKey.create(Registries.ITEM, resourcelocation1);
                    }

                    Potion potion = null;
                    if (jsonobject.has("potion")) {
                        ResourceLocation resourcelocation2 = new ResourceLocation(GsonHelper.getAsString(jsonobject, "potion"));
                        potion = (Potion)BuiltInRegistries.POTION.getOptional(resourcelocation2).orElseThrow(() -> {
                            return new JsonSyntaxException("Unknown potion '" + resourcelocation2 + "'");
                        });
                    }

                    EnchantmentPredicate[] aenchantmentpredicate = EnchantmentPredicate.fromJsonArray(jsonobject.get("enchantments"));
                    EnchantmentPredicate[] aenchantmentpredicate1 = EnchantmentPredicate.fromJsonArray(jsonobject.get("stored_enchantments"));
                    return new ItemPredicate(tagkey, set, minmaxbounds$ints, minmaxbounds$ints1, aenchantmentpredicate, aenchantmentpredicate1, potion, nbtpredicate);
                }
            }
        } else {
            return ANY;
        }
    }

    public JsonElement serializeToJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject jsonobject = new JsonObject();
            JsonArray jsonarray2;
            if (this.items != null) {
                jsonarray2 = new JsonArray();
                Iterator var3 = this.items.iterator();

                while(var3.hasNext()) {
                    Item item = (Item)var3.next();
                    jsonarray2.add(BuiltInRegistries.ITEM.getKey(item).toString());
                }

                jsonobject.add("items", jsonarray2);
            }

            if (this.tag != null) {
                jsonobject.addProperty("tag", this.tag.location().toString());
            }

            jsonobject.add("count", this.count.serializeToJson());
            jsonobject.add("durability", this.durability.serializeToJson());
            jsonobject.add("nbt", this.nbt.serializeToJson());
            int var5;
            EnchantmentPredicate enchantmentpredicate1;
            EnchantmentPredicate[] var7;
            int var8;
            if (this.enchantments.length > 0) {
                jsonarray2 = new JsonArray();
                var7 = this.enchantments;
                var8 = var7.length;

                for(var5 = 0; var5 < var8; ++var5) {
                    enchantmentpredicate1 = var7[var5];
                    jsonarray2.add(enchantmentpredicate1.serializeToJson());
                }

                jsonobject.add("enchantments", jsonarray2);
            }

            if (this.storedEnchantments.length > 0) {
                jsonarray2 = new JsonArray();
                var7 = this.storedEnchantments;
                var8 = var7.length;

                for(var5 = 0; var5 < var8; ++var5) {
                    enchantmentpredicate1 = var7[var5];
                    jsonarray2.add(enchantmentpredicate1.serializeToJson());
                }

                jsonobject.add("stored_enchantments", jsonarray2);
            }

            if (this.potion != null) {
                jsonobject.addProperty("potion", BuiltInRegistries.POTION.getKey(this.potion).toString());
            }

            return jsonobject;
        }
    }

    public static ItemPredicate[] fromJsonArray(@Nullable JsonElement p_45056_) {
        if (p_45056_ != null && !p_45056_.isJsonNull()) {
            JsonArray jsonarray = GsonHelper.convertToJsonArray(p_45056_, "items");
            ItemPredicate[] aitempredicate = new ItemPredicate[jsonarray.size()];

            for(int i = 0; i < aitempredicate.length; ++i) {
                aitempredicate[i] = fromJson(jsonarray.get(i));
            }

            return aitempredicate;
        } else {
            return new ItemPredicate[0];
        }
    }

    public static void register(ResourceLocation name, Function<JsonObject, ItemPredicate> deserializer) {
        custom_predicates.put(name, deserializer);
    }

    public static Map<ResourceLocation, Function<JsonObject, ItemPredicate>> getPredicates() {
        return unmod_predicates;
    }

    static {
        unmod_predicates = Collections.unmodifiableMap(custom_predicates);
        ANY = new ItemPredicate();
    }

    public static class Builder {
        private final List<EnchantmentPredicate> enchantments = Lists.newArrayList();
        private final List<EnchantmentPredicate> storedEnchantments = Lists.newArrayList();
        @Nullable
        private Set<Item> items;
        @Nullable
        private TagKey<Item> tag;
        private MinMaxBounds.Ints count;
        private MinMaxBounds.Ints durability;
        @Nullable
        private Potion potion;
        private NbtPredicate nbt;

        private Builder() {
            this.count = Ints.ANY;
            this.durability = Ints.ANY;
            this.nbt = NbtPredicate.ANY;
        }

        public static Builder item() {
            return new Builder();
        }

        public Builder of(ItemLike... p_151446_) {
            this.items = (Set)Stream.of(p_151446_).map(ItemLike::asItem).collect(ImmutableSet.toImmutableSet());
            return this;
        }

        public Builder of(TagKey<Item> p_204146_) {
            this.tag = p_204146_;
            return this;
        }

        public Builder withCount(MinMaxBounds.Ints p_151444_) {
            this.count = p_151444_;
            return this;
        }

        public Builder hasDurability(MinMaxBounds.Ints p_151450_) {
            this.durability = p_151450_;
            return this;
        }

        public Builder isPotion(Potion p_151442_) {
            this.potion = p_151442_;
            return this;
        }

        public Builder hasNbt(CompoundTag p_45076_) {
            this.nbt = new NbtPredicate(p_45076_);
            return this;
        }

        public Builder hasEnchantment(EnchantmentPredicate p_45072_) {
            this.enchantments.add(p_45072_);
            return this;
        }

        public Builder hasStoredEnchantment(EnchantmentPredicate p_151448_) {
            this.storedEnchantments.add(p_151448_);
            return this;
        }

        public ItemPredicate build() {
            return new ItemPredicate(this.tag, this.items, this.count, this.durability, (EnchantmentPredicate[])this.enchantments.toArray(EnchantmentPredicate.NONE), (EnchantmentPredicate[])this.storedEnchantments.toArray(EnchantmentPredicate.NONE), this.potion, this.nbt);
        }
    }
}

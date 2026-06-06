//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class SetEnchantmentsFunction extends LootItemConditionalFunction {
    final Map<Enchantment, NumberProvider> enchantments;
    final boolean add;

    SetEnchantmentsFunction(LootItemCondition[] p_165337_, Map<Enchantment, NumberProvider> p_165338_, boolean p_165339_) {
        super(p_165337_);
        this.enchantments = ImmutableMap.copyOf(p_165338_);
        this.add = p_165339_;
    }

    public LootItemFunctionType getType() {
        return LootItemFunctions.SET_ENCHANTMENTS;
    }

    public Set<LootContextParam<?>> getReferencedContextParams() {
        return (Set)this.enchantments.values().stream().flatMap((p_279081_) -> {
            return p_279081_.getReferencedContextParams().stream();
        }).collect(ImmutableSet.toImmutableSet());
    }

    public ItemStack run(ItemStack p_165346_, LootContext p_165347_) {
        Object2IntMap<Enchantment> $$2 = new Object2IntOpenHashMap();
        this.enchantments.forEach((p_165353_, p_165354_) -> {
            $$2.put(p_165353_, p_165354_.getInt(p_165347_));
        });
        if (p_165346_.getItem() == Items.BOOK) {
            ItemStack $$3 = new ItemStack(Items.ENCHANTED_BOOK);
            $$2.forEach((p_165343_, p_165344_) -> {
                EnchantedBookItem.addEnchantment($$3, new EnchantmentInstance(p_165343_, p_165344_));
            });
            return $$3;
        } else {
            Map<Enchantment, Integer> $$4 = EnchantmentHelper.getEnchantments(p_165346_);
            if (this.add) {
                $$2.forEach((p_165366_, p_165367_) -> {
                    updateEnchantment($$4, p_165366_, Math.max((Integer)$$4.getOrDefault(p_165366_, 0) + p_165367_, 0));
                });
            } else {
                $$2.forEach((p_165361_, p_165362_) -> {
                    updateEnchantment($$4, p_165361_, Math.max(p_165362_, 0));
                });
            }

            EnchantmentHelper.setEnchantments($$4, p_165346_);
            return p_165346_;
        }
    }

    private static void updateEnchantment(Map<Enchantment, Integer> p_165356_, Enchantment p_165357_, int p_165358_) {
        if (p_165358_ == 0) {
            p_165356_.remove(p_165357_);
        } else {
            p_165356_.put(p_165357_, p_165358_);
        }

    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<SetEnchantmentsFunction> {
        public Serializer() {
        }

        public void serialize(JsonObject p_165394_, SetEnchantmentsFunction p_165395_, JsonSerializationContext p_165396_) {
            super.serialize(p_165394_, (LootItemConditionalFunction)p_165395_, p_165396_);
            JsonObject $$3 = new JsonObject();
            p_165395_.enchantments.forEach((p_259023_, p_259024_) -> {
                ResourceLocation $$4 = BuiltInRegistries.ENCHANTMENT.getKey(p_259023_);
                if ($$4 == null) {
                    throw new IllegalArgumentException("Don't know how to serialize enchantment " + p_259023_);
                } else {
                    $$3.add($$4.toString(), p_165396_.serialize(p_259024_));
                }
            });
            p_165394_.add("enchantments", $$3);
            p_165394_.addProperty("add", p_165395_.add);
        }

        public SetEnchantmentsFunction deserialize(JsonObject p_165381_, JsonDeserializationContext p_165382_, LootItemCondition[] p_165383_) {
            Map<Enchantment, NumberProvider> $$3 = Maps.newHashMap();
            if (p_165381_.has("enchantments")) {
                JsonObject $$4 = GsonHelper.getAsJsonObject(p_165381_, "enchantments");
                Iterator var6 = $$4.entrySet().iterator();

                while(var6.hasNext()) {
                    Map.Entry<String, JsonElement> $$5 = (Map.Entry)var6.next();
                    String $$6 = (String)$$5.getKey();
                    JsonElement $$7 = (JsonElement)$$5.getValue();
                    Enchantment $$8 = (Enchantment)BuiltInRegistries.ENCHANTMENT.getOptional(new ResourceLocation($$6)).orElseThrow(() -> {
                        return new JsonSyntaxException("Unknown enchantment '" + $$6 + "'");
                    });
                    NumberProvider $$9 = (NumberProvider)p_165382_.deserialize($$7, NumberProvider.class);
                    $$3.put($$8, $$9);
                }
            }

            boolean $$10 = GsonHelper.getAsBoolean(p_165381_, "add", false);
            return new SetEnchantmentsFunction(p_165383_, $$3, $$10);
        }
    }

    public static class Builder extends LootItemConditionalFunction.Builder<Builder> {
        private final Map<Enchantment, NumberProvider> enchantments;
        private final boolean add;

        public Builder() {
            this(false);
        }

        public Builder(boolean p_165372_) {
            this.enchantments = Maps.newHashMap();
            this.add = p_165372_;
        }

        protected Builder getThis() {
            return this;
        }

        public Builder withEnchantment(Enchantment p_165375_, NumberProvider p_165376_) {
            this.enchantments.put(p_165375_, p_165376_);
            return this;
        }

        public LootItemFunction build() {
            return new SetEnchantmentsFunction(this.getConditions(), this.enchantments, this.add);
        }
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.slf4j.Logger;

public class EnchantRandomlyFunction extends LootItemConditionalFunction {
    private static final Logger LOGGER = LogUtils.getLogger();
    final List<Enchantment> enchantments;

    EnchantRandomlyFunction(LootItemCondition[] p_80418_, Collection<Enchantment> p_80419_) {
        super(p_80418_);
        this.enchantments = ImmutableList.copyOf(p_80419_);
    }

    public LootItemFunctionType getType() {
        return LootItemFunctions.ENCHANT_RANDOMLY;
    }

    public ItemStack run(ItemStack p_80429_, LootContext p_80430_) {
        RandomSource $$2 = p_80430_.getRandom();
        Enchantment $$6;
        if (this.enchantments.isEmpty()) {
            boolean $$3 = p_80429_.is(Items.BOOK);
            List<Enchantment> $$4 = (List)BuiltInRegistries.ENCHANTMENT.stream().filter(Enchantment::isDiscoverable).filter((p_80436_) -> {
                return $$3 || p_80436_.canEnchant(p_80429_);
            }).collect(Collectors.toList());
            if ($$4.isEmpty()) {
                LOGGER.warn("Couldn't find a compatible enchantment for {}", p_80429_);
                return p_80429_;
            }

            $$6 = (Enchantment)$$4.get($$2.nextInt($$4.size()));
        } else {
            $$6 = (Enchantment)this.enchantments.get($$2.nextInt(this.enchantments.size()));
        }

        return enchantItem(p_80429_, $$6, $$2);
    }

    private static ItemStack enchantItem(ItemStack p_230980_, Enchantment p_230981_, RandomSource p_230982_) {
        int $$3 = Mth.nextInt(p_230982_, p_230981_.getMinLevel(), p_230981_.getMaxLevel());
        if (p_230980_.is(Items.BOOK)) {
            p_230980_ = new ItemStack(Items.ENCHANTED_BOOK);
            EnchantedBookItem.addEnchantment(p_230980_, new EnchantmentInstance(p_230981_, $$3));
        } else {
            p_230980_.enchant(p_230981_, $$3);
        }

        return p_230980_;
    }

    public static Builder randomEnchantment() {
        return new Builder();
    }

    public static LootItemConditionalFunction.Builder<?> randomApplicableEnchantment() {
        return simpleBuilder((p_80438_) -> {
            return new EnchantRandomlyFunction(p_80438_, ImmutableList.of());
        });
    }

    public static class Builder extends LootItemConditionalFunction.Builder<Builder> {
        private final Set<Enchantment> enchantments = Sets.newHashSet();

        public Builder() {
        }

        protected Builder getThis() {
            return this;
        }

        public Builder withEnchantment(Enchantment p_80445_) {
            this.enchantments.add(p_80445_);
            return this;
        }

        public LootItemFunction build() {
            return new EnchantRandomlyFunction(this.getConditions(), this.enchantments);
        }
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<EnchantRandomlyFunction> {
        public Serializer() {
        }

        public void serialize(JsonObject p_80454_, EnchantRandomlyFunction p_80455_, JsonSerializationContext p_80456_) {
            super.serialize(p_80454_, (LootItemConditionalFunction)p_80455_, p_80456_);
            if (!p_80455_.enchantments.isEmpty()) {
                JsonArray $$3 = new JsonArray();
                Iterator var5 = p_80455_.enchantments.iterator();

                while(var5.hasNext()) {
                    Enchantment $$4 = (Enchantment)var5.next();
                    ResourceLocation $$5 = BuiltInRegistries.ENCHANTMENT.getKey($$4);
                    if ($$5 == null) {
                        throw new IllegalArgumentException("Don't know how to serialize enchantment " + $$4);
                    }

                    $$3.add(new JsonPrimitive($$5.toString()));
                }

                p_80454_.add("enchantments", $$3);
            }

        }

        public EnchantRandomlyFunction deserialize(JsonObject p_80450_, JsonDeserializationContext p_80451_, LootItemCondition[] p_80452_) {
            List<Enchantment> $$3 = Lists.newArrayList();
            if (p_80450_.has("enchantments")) {
                JsonArray $$4 = GsonHelper.getAsJsonArray(p_80450_, "enchantments");
                Iterator var6 = $$4.iterator();

                while(var6.hasNext()) {
                    JsonElement $$5 = (JsonElement)var6.next();
                    String $$6 = GsonHelper.convertToString($$5, "enchantment");
                    Enchantment $$7 = (Enchantment)BuiltInRegistries.ENCHANTMENT.getOptional(new ResourceLocation($$6)).orElseThrow(() -> {
                        return new JsonSyntaxException("Unknown enchantment '" + $$6 + "'");
                    });
                    $$3.add($$7);
                }
            }

            return new EnchantRandomlyFunction(p_80452_, $$3);
        }
    }
}

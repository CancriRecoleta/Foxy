//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class BonusLevelTableCondition implements LootItemCondition {
    final Enchantment enchantment;
    final float[] values;

    BonusLevelTableCondition(Enchantment p_81510_, float[] p_81511_) {
        this.enchantment = p_81510_;
        this.values = p_81511_;
    }

    public LootItemConditionType getType() {
        return LootItemConditions.TABLE_BONUS;
    }

    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParams.TOOL);
    }

    public boolean test(LootContext p_81521_) {
        ItemStack $$1 = (ItemStack)p_81521_.getParamOrNull(LootContextParams.TOOL);
        int $$2 = $$1 != null ? EnchantmentHelper.getItemEnchantmentLevel(this.enchantment, $$1) : 0;
        float $$3 = this.values[Math.min($$2, this.values.length - 1)];
        return p_81521_.getRandom().nextFloat() < $$3;
    }

    public static LootItemCondition.Builder bonusLevelFlatChance(Enchantment p_81518_, float... p_81519_) {
        return () -> {
            return new BonusLevelTableCondition(p_81518_, p_81519_);
        };
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<BonusLevelTableCondition> {
        public Serializer() {
        }

        public void serialize(JsonObject p_81537_, BonusLevelTableCondition p_81538_, JsonSerializationContext p_81539_) {
            p_81537_.addProperty("enchantment", BuiltInRegistries.ENCHANTMENT.getKey(p_81538_.enchantment).toString());
            p_81537_.add("chances", p_81539_.serialize(p_81538_.values));
        }

        public BonusLevelTableCondition deserialize(JsonObject p_81547_, JsonDeserializationContext p_81548_) {
            ResourceLocation $$2 = new ResourceLocation(GsonHelper.getAsString(p_81547_, "enchantment"));
            Enchantment $$3 = (Enchantment)BuiltInRegistries.ENCHANTMENT.getOptional($$2).orElseThrow(() -> {
                return new JsonParseException("Invalid enchantment id: " + $$2);
            });
            float[] $$4 = (float[])GsonHelper.getAsObject(p_81547_, "chances", p_81548_, float[].class);
            return new BonusLevelTableCondition($$3, $$4);
        }
    }
}

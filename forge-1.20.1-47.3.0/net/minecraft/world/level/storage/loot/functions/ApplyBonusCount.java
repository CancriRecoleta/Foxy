//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ApplyBonusCount extends LootItemConditionalFunction {
    static final Map<ResourceLocation, FormulaDeserializer> FORMULAS = Maps.newHashMap();
    final Enchantment enchantment;
    final Formula formula;

    ApplyBonusCount(LootItemCondition[] p_79903_, Enchantment p_79904_, Formula p_79905_) {
        super(p_79903_);
        this.enchantment = p_79904_;
        this.formula = p_79905_;
    }

    public LootItemFunctionType getType() {
        return LootItemFunctions.APPLY_BONUS;
    }

    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParams.TOOL);
    }

    public ItemStack run(ItemStack p_79913_, LootContext p_79914_) {
        ItemStack $$2 = (ItemStack)p_79914_.getParamOrNull(LootContextParams.TOOL);
        if ($$2 != null) {
            int $$3 = EnchantmentHelper.getItemEnchantmentLevel(this.enchantment, $$2);
            int $$4 = this.formula.calculateNewCount(p_79914_.getRandom(), p_79913_.getCount(), $$3);
            p_79913_.setCount($$4);
        }

        return p_79913_;
    }

    public static LootItemConditionalFunction.Builder<?> addBonusBinomialDistributionCount(Enchantment p_79918_, float p_79919_, int p_79920_) {
        return simpleBuilder((p_79928_) -> {
            return new ApplyBonusCount(p_79928_, p_79918_, new BinomialWithBonusCount(p_79920_, p_79919_));
        });
    }

    public static LootItemConditionalFunction.Builder<?> addOreBonusCount(Enchantment p_79916_) {
        return simpleBuilder((p_79943_) -> {
            return new ApplyBonusCount(p_79943_, p_79916_, new OreDrops());
        });
    }

    public static LootItemConditionalFunction.Builder<?> addUniformBonusCount(Enchantment p_79940_) {
        return simpleBuilder((p_79935_) -> {
            return new ApplyBonusCount(p_79935_, p_79940_, new UniformBonusCount(1));
        });
    }

    public static LootItemConditionalFunction.Builder<?> addUniformBonusCount(Enchantment p_79922_, int p_79923_) {
        return simpleBuilder((p_79932_) -> {
            return new ApplyBonusCount(p_79932_, p_79922_, new UniformBonusCount(p_79923_));
        });
    }

    static {
        FORMULAS.put(net.minecraft.world.level.storage.loot.functions.ApplyBonusCount.BinomialWithBonusCount.TYPE, BinomialWithBonusCount::deserialize);
        FORMULAS.put(net.minecraft.world.level.storage.loot.functions.ApplyBonusCount.OreDrops.TYPE, OreDrops::deserialize);
        FORMULAS.put(net.minecraft.world.level.storage.loot.functions.ApplyBonusCount.UniformBonusCount.TYPE, UniformBonusCount::deserialize);
    }

    private interface Formula {
        int calculateNewCount(RandomSource var1, int var2, int var3);

        void serializeParams(JsonObject var1, JsonSerializationContext var2);

        ResourceLocation getType();
    }

    static final class UniformBonusCount implements Formula {
        public static final ResourceLocation TYPE = new ResourceLocation("uniform_bonus_count");
        private final int bonusMultiplier;

        public UniformBonusCount(int p_80016_) {
            this.bonusMultiplier = p_80016_;
        }

        public int calculateNewCount(RandomSource p_230976_, int p_230977_, int p_230978_) {
            return p_230977_ + p_230976_.nextInt(this.bonusMultiplier * p_230978_ + 1);
        }

        public void serializeParams(JsonObject p_80022_, JsonSerializationContext p_80023_) {
            p_80022_.addProperty("bonusMultiplier", this.bonusMultiplier);
        }

        public static Formula deserialize(JsonObject p_80019_, JsonDeserializationContext p_80020_) {
            int $$2 = GsonHelper.getAsInt(p_80019_, "bonusMultiplier");
            return new UniformBonusCount($$2);
        }

        public ResourceLocation getType() {
            return TYPE;
        }
    }

    private static final class OreDrops implements Formula {
        public static final ResourceLocation TYPE = new ResourceLocation("ore_drops");

        OreDrops() {
        }

        public int calculateNewCount(RandomSource p_230972_, int p_230973_, int p_230974_) {
            if (p_230974_ > 0) {
                int $$3 = p_230972_.nextInt(p_230974_ + 2) - 1;
                if ($$3 < 0) {
                    $$3 = 0;
                }

                return p_230973_ * ($$3 + 1);
            } else {
                return p_230973_;
            }
        }

        public void serializeParams(JsonObject p_79983_, JsonSerializationContext p_79984_) {
        }

        public static Formula deserialize(JsonObject p_79980_, JsonDeserializationContext p_79981_) {
            return new OreDrops();
        }

        public ResourceLocation getType() {
            return TYPE;
        }
    }

    private static final class BinomialWithBonusCount implements Formula {
        public static final ResourceLocation TYPE = new ResourceLocation("binomial_with_bonus_count");
        private final int extraRounds;
        private final float probability;

        public BinomialWithBonusCount(int p_79952_, float p_79953_) {
            this.extraRounds = p_79952_;
            this.probability = p_79953_;
        }

        public int calculateNewCount(RandomSource p_230965_, int p_230966_, int p_230967_) {
            for(int $$3 = 0; $$3 < p_230967_ + this.extraRounds; ++$$3) {
                if (p_230965_.nextFloat() < this.probability) {
                    ++p_230966_;
                }
            }

            return p_230966_;
        }

        public void serializeParams(JsonObject p_79959_, JsonSerializationContext p_79960_) {
            p_79959_.addProperty("extra", this.extraRounds);
            p_79959_.addProperty("probability", this.probability);
        }

        public static Formula deserialize(JsonObject p_79956_, JsonDeserializationContext p_79957_) {
            int $$2 = GsonHelper.getAsInt(p_79956_, "extra");
            float $$3 = GsonHelper.getAsFloat(p_79956_, "probability");
            return new BinomialWithBonusCount($$2, $$3);
        }

        public ResourceLocation getType() {
            return TYPE;
        }
    }

    interface FormulaDeserializer {
        Formula deserialize(JsonObject var1, JsonDeserializationContext var2);
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<ApplyBonusCount> {
        public Serializer() {
        }

        public void serialize(JsonObject p_79995_, ApplyBonusCount p_79996_, JsonSerializationContext p_79997_) {
            super.serialize(p_79995_, (LootItemConditionalFunction)p_79996_, p_79997_);
            p_79995_.addProperty("enchantment", BuiltInRegistries.ENCHANTMENT.getKey(p_79996_.enchantment).toString());
            p_79995_.addProperty("formula", p_79996_.formula.getType().toString());
            JsonObject $$3 = new JsonObject();
            p_79996_.formula.serializeParams($$3, p_79997_);
            if ($$3.size() > 0) {
                p_79995_.add("parameters", $$3);
            }

        }

        public ApplyBonusCount deserialize(JsonObject p_79991_, JsonDeserializationContext p_79992_, LootItemCondition[] p_79993_) {
            ResourceLocation $$3 = new ResourceLocation(GsonHelper.getAsString(p_79991_, "enchantment"));
            Enchantment $$4 = (Enchantment)BuiltInRegistries.ENCHANTMENT.getOptional($$3).orElseThrow(() -> {
                return new JsonParseException("Invalid enchantment id: " + $$3);
            });
            ResourceLocation $$5 = new ResourceLocation(GsonHelper.getAsString(p_79991_, "formula"));
            FormulaDeserializer $$6 = (FormulaDeserializer)ApplyBonusCount.FORMULAS.get($$5);
            if ($$6 == null) {
                throw new JsonParseException("Invalid formula id: " + $$5);
            } else {
                Formula $$8;
                if (p_79991_.has("parameters")) {
                    $$8 = $$6.deserialize(GsonHelper.getAsJsonObject(p_79991_, "parameters"), p_79992_);
                } else {
                    $$8 = $$6.deserialize(new JsonObject(), p_79992_);
                }

                return new ApplyBonusCount(p_79993_, $$4, $$8);
            }
        }
    }
}

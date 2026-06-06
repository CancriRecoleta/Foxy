//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ApplyExplosionDecay extends LootItemConditionalFunction {
    ApplyExplosionDecay(LootItemCondition[] p_80029_) {
        super(p_80029_);
    }

    public LootItemFunctionType getType() {
        return LootItemFunctions.EXPLOSION_DECAY;
    }

    public ItemStack run(ItemStack p_80034_, LootContext p_80035_) {
        Float $$2 = (Float)p_80035_.getParamOrNull(LootContextParams.EXPLOSION_RADIUS);
        if ($$2 != null) {
            RandomSource $$3 = p_80035_.getRandom();
            float $$4 = 1.0F / $$2;
            int $$5 = p_80034_.getCount();
            int $$6 = 0;

            for(int $$7 = 0; $$7 < $$5; ++$$7) {
                if ($$3.nextFloat() <= $$4) {
                    ++$$6;
                }
            }

            p_80034_.setCount($$6);
        }

        return p_80034_;
    }

    public static LootItemConditionalFunction.Builder<?> explosionDecay() {
        return simpleBuilder(ApplyExplosionDecay::new);
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<ApplyExplosionDecay> {
        public Serializer() {
        }

        public ApplyExplosionDecay deserialize(JsonObject p_80040_, JsonDeserializationContext p_80041_, LootItemCondition[] p_80042_) {
            return new ApplyExplosionDecay(p_80042_);
        }
    }
}

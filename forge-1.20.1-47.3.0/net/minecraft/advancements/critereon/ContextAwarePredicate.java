//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements.critereon;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;

public class ContextAwarePredicate {
    public static final ContextAwarePredicate ANY = new ContextAwarePredicate(new LootItemCondition[0]);
    private final LootItemCondition[] conditions;
    private final Predicate<LootContext> compositePredicates;

    ContextAwarePredicate(LootItemCondition[] p_286308_) {
        this.conditions = p_286308_;
        this.compositePredicates = LootItemConditions.andConditions(p_286308_);
    }

    public static ContextAwarePredicate create(LootItemCondition... p_286844_) {
        return new ContextAwarePredicate(p_286844_);
    }

    @Nullable
    public static ContextAwarePredicate fromElement(String p_286647_, DeserializationContext p_286323_, @Nullable JsonElement p_286520_, LootContextParamSet p_286912_) {
        if (p_286520_ != null && p_286520_.isJsonArray()) {
            LootItemCondition[] $$4 = p_286323_.deserializeConditions(p_286520_.getAsJsonArray(), p_286323_.getAdvancementId() + "/" + p_286647_, p_286912_);
            return new ContextAwarePredicate($$4);
        } else {
            return null;
        }
    }

    public boolean matches(LootContext p_286260_) {
        return this.compositePredicates.test(p_286260_);
    }

    public JsonElement toJson(SerializationContext p_286222_) {
        return (JsonElement)(this.conditions.length == 0 ? JsonNull.INSTANCE : p_286222_.serializeConditions(this.conditions));
    }

    public static JsonElement toJson(ContextAwarePredicate[] p_286611_, SerializationContext p_286638_) {
        if (p_286611_.length == 0) {
            return JsonNull.INSTANCE;
        } else {
            JsonArray $$2 = new JsonArray();
            ContextAwarePredicate[] var3 = p_286611_;
            int var4 = p_286611_.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                ContextAwarePredicate $$3 = var3[var5];
                $$2.add($$3.toJson(p_286638_));
            }

            return $$2;
        }
    }
}

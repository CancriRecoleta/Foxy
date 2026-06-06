//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import org.apache.commons.lang3.ArrayUtils;

public abstract class LootItemConditionalFunction implements LootItemFunction {
    protected final LootItemCondition[] predicates;
    private final Predicate<LootContext> compositePredicates;

    protected LootItemConditionalFunction(LootItemCondition[] p_80678_) {
        this.predicates = p_80678_;
        this.compositePredicates = LootItemConditions.andConditions(p_80678_);
    }

    public final ItemStack apply(ItemStack p_80689_, LootContext p_80690_) {
        return this.compositePredicates.test(p_80690_) ? this.run(p_80689_, p_80690_) : p_80689_;
    }

    protected abstract ItemStack run(ItemStack var1, LootContext var2);

    public void validate(ValidationContext p_80682_) {
        LootItemFunction.super.validate(p_80682_);

        for(int $$1 = 0; $$1 < this.predicates.length; ++$$1) {
            this.predicates[$$1].validate(p_80682_.forChild(".conditions[" + $$1 + "]"));
        }

    }

    protected static Builder<?> simpleBuilder(Function<LootItemCondition[], LootItemFunction> p_80684_) {
        return new DummyBuilder(p_80684_);
    }

    static final class DummyBuilder extends Builder<DummyBuilder> {
        private final Function<LootItemCondition[], LootItemFunction> constructor;

        public DummyBuilder(Function<LootItemCondition[], LootItemFunction> p_80702_) {
            this.constructor = p_80702_;
        }

        protected DummyBuilder getThis() {
            return this;
        }

        public LootItemFunction build() {
            return (LootItemFunction)this.constructor.apply(this.getConditions());
        }
    }

    public abstract static class Serializer<T extends LootItemConditionalFunction> implements net.minecraft.world.level.storage.loot.Serializer<T> {
        public Serializer() {
        }

        public void serialize(JsonObject p_80711_, T p_80712_, JsonSerializationContext p_80713_) {
            if (!ArrayUtils.isEmpty(p_80712_.predicates)) {
                p_80711_.add("conditions", p_80713_.serialize(p_80712_.predicates));
            }

        }

        public final T deserialize(JsonObject p_80719_, JsonDeserializationContext p_80720_) {
            LootItemCondition[] $$2 = (LootItemCondition[])GsonHelper.getAsObject(p_80719_, "conditions", new LootItemCondition[0], p_80720_, LootItemCondition[].class);
            return this.deserialize(p_80719_, p_80720_, $$2);
        }

        public abstract T deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3);
    }

    public abstract static class Builder<T extends Builder<T>> implements LootItemFunction.Builder, ConditionUserBuilder<T> {
        private final List<LootItemCondition> conditions = Lists.newArrayList();

        public Builder() {
        }

        public T when(LootItemCondition.Builder p_80694_) {
            this.conditions.add(p_80694_.build());
            return this.getThis();
        }

        public final T unwrap() {
            return this.getThis();
        }

        protected abstract T getThis();

        protected LootItemCondition[] getConditions() {
            return (LootItemCondition[])this.conditions.toArray(new LootItemCondition[0]);
        }
    }
}

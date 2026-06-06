//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.storage.loot.entries;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import org.apache.commons.lang3.ArrayUtils;

public abstract class LootPoolEntryContainer implements ComposableEntryContainer {
    protected final LootItemCondition[] conditions;
    private final Predicate<LootContext> compositeCondition;

    protected LootPoolEntryContainer(LootItemCondition[] p_79638_) {
        this.conditions = p_79638_;
        this.compositeCondition = LootItemConditions.andConditions(p_79638_);
    }

    public void validate(ValidationContext p_79641_) {
        for(int $$1 = 0; $$1 < this.conditions.length; ++$$1) {
            this.conditions[$$1].validate(p_79641_.forChild(".condition[" + $$1 + "]"));
        }

    }

    protected final boolean canRun(LootContext p_79640_) {
        return this.compositeCondition.test(p_79640_);
    }

    public abstract LootPoolEntryType getType();

    public abstract static class Serializer<T extends LootPoolEntryContainer> implements net.minecraft.world.level.storage.loot.Serializer<T> {
        public Serializer() {
        }

        public final void serialize(JsonObject p_79670_, T p_79671_, JsonSerializationContext p_79672_) {
            if (!ArrayUtils.isEmpty(p_79671_.conditions)) {
                p_79670_.add("conditions", p_79672_.serialize(p_79671_.conditions));
            }

            this.serializeCustom(p_79670_, p_79671_, p_79672_);
        }

        public final T deserialize(JsonObject p_79664_, JsonDeserializationContext p_79665_) {
            LootItemCondition[] $$2 = (LootItemCondition[])GsonHelper.getAsObject(p_79664_, "conditions", new LootItemCondition[0], p_79665_, LootItemCondition[].class);
            return this.deserializeCustom(p_79664_, p_79665_, $$2);
        }

        public abstract void serializeCustom(JsonObject var1, T var2, JsonSerializationContext var3);

        public abstract T deserializeCustom(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3);
    }

    public abstract static class Builder<T extends Builder<T>> implements ConditionUserBuilder<T> {
        private final List<LootItemCondition> conditions = Lists.newArrayList();

        public Builder() {
        }

        protected abstract T getThis();

        public T when(LootItemCondition.Builder p_79646_) {
            this.conditions.add(p_79646_.build());
            return this.getThis();
        }

        public final T unwrap() {
            return this.getThis();
        }

        protected LootItemCondition[] getConditions() {
            return (LootItemCondition[])this.conditions.toArray(new LootItemCondition[0]);
        }

        public AlternativesEntry.Builder otherwise(Builder<?> p_79644_) {
            return new AlternativesEntry.Builder(new Builder[]{this, p_79644_});
        }

        public EntryGroup.Builder append(Builder<?> p_165148_) {
            return new EntryGroup.Builder(new Builder[]{this, p_165148_});
        }

        public SequentialEntry.Builder then(Builder<?> p_165149_) {
            return new SequentialEntry.Builder(new Builder[]{this, p_165149_});
        }

        public abstract LootPoolEntryContainer build();
    }
}

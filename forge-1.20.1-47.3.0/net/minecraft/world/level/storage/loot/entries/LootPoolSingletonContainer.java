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
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.functions.FunctionUserBuilder;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.apache.commons.lang3.ArrayUtils;

public abstract class LootPoolSingletonContainer extends LootPoolEntryContainer {
    public static final int DEFAULT_WEIGHT = 1;
    public static final int DEFAULT_QUALITY = 0;
    protected final int weight;
    protected final int quality;
    protected final LootItemFunction[] functions;
    final BiFunction<ItemStack, LootContext, ItemStack> compositeFunction;
    private final LootPoolEntry entry = new EntryBase() {
        public void createItemStack(Consumer<ItemStack> p_79700_, LootContext p_79701_) {
            LootPoolSingletonContainer.this.createItemStack(LootItemFunction.decorate(LootPoolSingletonContainer.this.compositeFunction, p_79700_, p_79701_), p_79701_);
        }
    };

    protected LootPoolSingletonContainer(int p_79681_, int p_79682_, LootItemCondition[] p_79683_, LootItemFunction[] p_79684_) {
        super(p_79683_);
        this.weight = p_79681_;
        this.quality = p_79682_;
        this.functions = p_79684_;
        this.compositeFunction = LootItemFunctions.compose(p_79684_);
    }

    public void validate(ValidationContext p_79686_) {
        super.validate(p_79686_);

        for(int $$1 = 0; $$1 < this.functions.length; ++$$1) {
            this.functions[$$1].validate(p_79686_.forChild(".functions[" + $$1 + "]"));
        }

    }

    protected abstract void createItemStack(Consumer<ItemStack> var1, LootContext var2);

    public boolean expand(LootContext p_79694_, Consumer<LootPoolEntry> p_79695_) {
        if (this.canRun(p_79694_)) {
            p_79695_.accept(this.entry);
            return true;
        } else {
            return false;
        }
    }

    public static Builder<?> simpleBuilder(EntryConstructor p_79688_) {
        return new DummyBuilder(p_79688_);
    }

    static class DummyBuilder extends Builder<DummyBuilder> {
        private final EntryConstructor constructor;

        public DummyBuilder(EntryConstructor p_79717_) {
            this.constructor = p_79717_;
        }

        protected DummyBuilder getThis() {
            return this;
        }

        public LootPoolEntryContainer build() {
            return this.constructor.build(this.weight, this.quality, this.getConditions(), this.getFunctions());
        }
    }

    @FunctionalInterface
    protected interface EntryConstructor {
        LootPoolSingletonContainer build(int var1, int var2, LootItemCondition[] var3, LootItemFunction[] var4);
    }

    public abstract static class Serializer<T extends LootPoolSingletonContainer> extends LootPoolEntryContainer.Serializer<T> {
        public Serializer() {
        }

        public void serializeCustom(JsonObject p_79741_, T p_79742_, JsonSerializationContext p_79743_) {
            if (p_79742_.weight != 1) {
                p_79741_.addProperty("weight", p_79742_.weight);
            }

            if (p_79742_.quality != 0) {
                p_79741_.addProperty("quality", p_79742_.quality);
            }

            if (!ArrayUtils.isEmpty(p_79742_.functions)) {
                p_79741_.add("functions", p_79743_.serialize(p_79742_.functions));
            }

        }

        public final T deserializeCustom(JsonObject p_79733_, JsonDeserializationContext p_79734_, LootItemCondition[] p_79735_) {
            int $$3 = GsonHelper.getAsInt(p_79733_, "weight", 1);
            int $$4 = GsonHelper.getAsInt(p_79733_, "quality", 0);
            LootItemFunction[] $$5 = (LootItemFunction[])GsonHelper.getAsObject(p_79733_, "functions", new LootItemFunction[0], p_79734_, LootItemFunction[].class);
            return this.deserialize(p_79733_, p_79734_, $$3, $$4, p_79735_, $$5);
        }

        protected abstract T deserialize(JsonObject var1, JsonDeserializationContext var2, int var3, int var4, LootItemCondition[] var5, LootItemFunction[] var6);
    }

    public abstract static class Builder<T extends Builder<T>> extends LootPoolEntryContainer.Builder<T> implements FunctionUserBuilder<T> {
        protected int weight = 1;
        protected int quality = 0;
        private final List<LootItemFunction> functions = Lists.newArrayList();

        public Builder() {
        }

        public T apply(LootItemFunction.Builder p_79710_) {
            this.functions.add(p_79710_.build());
            return (Builder)this.getThis();
        }

        protected LootItemFunction[] getFunctions() {
            return (LootItemFunction[])this.functions.toArray(new LootItemFunction[0]);
        }

        public T setWeight(int p_79708_) {
            this.weight = p_79708_;
            return (Builder)this.getThis();
        }

        public T setQuality(int p_79712_) {
            this.quality = p_79712_;
            return (Builder)this.getThis();
        }
    }

    protected abstract class EntryBase implements LootPoolEntry {
        protected EntryBase() {
        }

        public int getWeight(float p_79725_) {
            return Math.max(Mth.floor((float)LootPoolSingletonContainer.this.weight + (float)LootPoolSingletonContainer.this.quality * p_79725_), 0);
        }
    }
}

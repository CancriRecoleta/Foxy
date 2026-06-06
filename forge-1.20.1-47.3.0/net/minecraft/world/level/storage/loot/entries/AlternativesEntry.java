//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.storage.loot.entries;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.apache.commons.lang3.ArrayUtils;

public class AlternativesEntry extends CompositeEntryBase {
    AlternativesEntry(LootPoolEntryContainer[] p_79384_, LootItemCondition[] p_79385_) {
        super(p_79384_, p_79385_);
    }

    public LootPoolEntryType getType() {
        return LootPoolEntries.ALTERNATIVES;
    }

    protected ComposableEntryContainer compose(ComposableEntryContainer[] p_79390_) {
        switch (p_79390_.length) {
            case 0 -> return ALWAYS_FALSE;
            case 1 -> return p_79390_[0];
            case 2 -> return p_79390_[0].or(p_79390_[1]);
            default -> return (p_79393_, p_79394_) -> {
    ComposableEntryContainer[] var3 = p_79390_;
    int var4 = p_79390_.length;

    for(int var5 = 0; var5 < var4; ++var5) {
        ComposableEntryContainer $$3 = var3[var5];
        if ($$3.expand(p_79393_, p_79394_)) {
            return true;
        }
    }

    return false;
};
        }
    }

    public void validate(ValidationContext p_79388_) {
        super.validate(p_79388_);

        for(int $$1 = 0; $$1 < this.children.length - 1; ++$$1) {
            if (ArrayUtils.isEmpty(this.children[$$1].conditions)) {
                p_79388_.reportProblem("Unreachable entry!");
            }
        }

    }

    public static Builder alternatives(LootPoolEntryContainer.Builder<?>... p_79396_) {
        return new Builder(p_79396_);
    }

    public static <E> Builder alternatives(Collection<E> p_230934_, Function<E, LootPoolEntryContainer.Builder<?>> p_230935_) {
        Stream var10002 = p_230934_.stream();
        Objects.requireNonNull(p_230935_);
        return new Builder((LootPoolEntryContainer.Builder[])var10002.map(p_230935_::apply).toArray((p_230932_) -> {
            return new LootPoolEntryContainer.Builder[p_230932_];
        }));
    }

    public static class Builder extends LootPoolEntryContainer.Builder<Builder> {
        private final List<LootPoolEntryContainer> entries = Lists.newArrayList();

        public Builder(LootPoolEntryContainer.Builder<?>... p_79399_) {
            LootPoolEntryContainer.Builder[] var2 = p_79399_;
            int var3 = p_79399_.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                LootPoolEntryContainer.Builder<?> $$1 = var2[var4];
                this.entries.add($$1.build());
            }

        }

        protected Builder getThis() {
            return this;
        }

        public Builder otherwise(LootPoolEntryContainer.Builder<?> p_79402_) {
            this.entries.add(p_79402_.build());
            return this;
        }

        public LootPoolEntryContainer build() {
            return new AlternativesEntry((LootPoolEntryContainer[])this.entries.toArray(new LootPoolEntryContainer[0]), this.getConditions());
        }
    }
}

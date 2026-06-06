//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.storage.loot.entries;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SequentialEntry extends CompositeEntryBase {
    SequentialEntry(LootPoolEntryContainer[] p_79812_, LootItemCondition[] p_79813_) {
        super(p_79812_, p_79813_);
    }

    public LootPoolEntryType getType() {
        return LootPoolEntries.SEQUENCE;
    }

    protected ComposableEntryContainer compose(ComposableEntryContainer[] p_79816_) {
        switch (p_79816_.length) {
            case 0 -> return ALWAYS_TRUE;
            case 1 -> return p_79816_[0];
            case 2 -> return p_79816_[0].and(p_79816_[1]);
            default -> return (p_79819_, p_79820_) -> {
    ComposableEntryContainer[] var3 = p_79816_;
    int var4 = p_79816_.length;

    for(int var5 = 0; var5 < var4; ++var5) {
        ComposableEntryContainer $$3 = var3[var5];
        if (!$$3.expand(p_79819_, p_79820_)) {
            return false;
        }
    }

    return true;
};
        }
    }

    public static Builder sequential(LootPoolEntryContainer.Builder<?>... p_165153_) {
        return new Builder(p_165153_);
    }

    public static class Builder extends LootPoolEntryContainer.Builder<Builder> {
        private final List<LootPoolEntryContainer> entries = Lists.newArrayList();

        public Builder(LootPoolEntryContainer.Builder<?>... p_165156_) {
            LootPoolEntryContainer.Builder[] var2 = p_165156_;
            int var3 = p_165156_.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                LootPoolEntryContainer.Builder<?> $$1 = var2[var4];
                this.entries.add($$1.build());
            }

        }

        protected Builder getThis() {
            return this;
        }

        public Builder then(LootPoolEntryContainer.Builder<?> p_165160_) {
            this.entries.add(p_165160_.build());
            return this;
        }

        public LootPoolEntryContainer build() {
            return new SequentialEntry((LootPoolEntryContainer[])this.entries.toArray(new LootPoolEntryContainer[0]), this.getConditions());
        }
    }
}

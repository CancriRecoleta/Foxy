//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.storage.loot.entries;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class EntryGroup extends CompositeEntryBase {
    EntryGroup(LootPoolEntryContainer[] p_79550_, LootItemCondition[] p_79551_) {
        super(p_79550_, p_79551_);
    }

    public LootPoolEntryType getType() {
        return LootPoolEntries.GROUP;
    }

    protected ComposableEntryContainer compose(ComposableEntryContainer[] p_79559_) {
        switch (p_79559_.length) {
            case 0:
                return ALWAYS_TRUE;
            case 1:
                return p_79559_[0];
            case 2:
                ComposableEntryContainer $$1 = p_79559_[0];
                ComposableEntryContainer $$2 = p_79559_[1];
                return (p_79556_, p_79557_) -> {
                    $$1.expand(p_79556_, p_79557_);
                    $$2.expand(p_79556_, p_79557_);
                    return true;
                };
            default:
                return (p_79562_, p_79563_) -> {
                    ComposableEntryContainer[] var3 = p_79559_;
                    int var4 = p_79559_.length;

                    for(int var5 = 0; var5 < var4; ++var5) {
                        ComposableEntryContainer $$3 = var3[var5];
                        $$3.expand(p_79562_, p_79563_);
                    }

                    return true;
                };
        }
    }

    public static Builder list(LootPoolEntryContainer.Builder<?>... p_165138_) {
        return new Builder(p_165138_);
    }

    public static class Builder extends LootPoolEntryContainer.Builder<Builder> {
        private final List<LootPoolEntryContainer> entries = Lists.newArrayList();

        public Builder(LootPoolEntryContainer.Builder<?>... p_165141_) {
            LootPoolEntryContainer.Builder[] var2 = p_165141_;
            int var3 = p_165141_.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                LootPoolEntryContainer.Builder<?> $$1 = var2[var4];
                this.entries.add($$1.build());
            }

        }

        protected Builder getThis() {
            return this;
        }

        public Builder append(LootPoolEntryContainer.Builder<?> p_165145_) {
            this.entries.add(p_165145_.build());
            return this;
        }

        public LootPoolEntryContainer build() {
            return new EntryGroup((LootPoolEntryContainer[])this.entries.toArray(new LootPoolEntryContainer[0]), this.getConditions());
        }
    }
}

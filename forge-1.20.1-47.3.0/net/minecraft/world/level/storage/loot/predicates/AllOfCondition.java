//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.storage.loot.predicates;

public class AllOfCondition extends CompositeLootItemCondition {
    AllOfCondition(LootItemCondition[] p_286723_) {
        super(p_286723_, LootItemConditions.andConditions(p_286723_));
    }

    public LootItemConditionType getType() {
        return LootItemConditions.ALL_OF;
    }

    public static Builder allOf(LootItemCondition.Builder... p_286873_) {
        return new Builder(p_286873_);
    }

    public static class Builder extends CompositeLootItemCondition.Builder {
        public Builder(LootItemCondition.Builder... p_286842_) {
            super(p_286842_);
        }

        public Builder and(LootItemCondition.Builder p_286760_) {
            this.addTerm(p_286760_);
            return this;
        }

        protected LootItemCondition create(LootItemCondition[] p_286816_) {
            return new AllOfCondition(p_286816_);
        }
    }

    public static class Serializer extends CompositeLootItemCondition.Serializer<AllOfCondition> {
        public Serializer() {
        }

        protected AllOfCondition create(LootItemCondition[] p_286223_) {
            return new AllOfCondition(p_286223_);
        }
    }
}

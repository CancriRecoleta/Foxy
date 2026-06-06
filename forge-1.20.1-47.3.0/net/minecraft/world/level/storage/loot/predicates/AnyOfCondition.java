//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.storage.loot.predicates;

public class AnyOfCondition extends CompositeLootItemCondition {
    AnyOfCondition(LootItemCondition[] p_286532_) {
        super(p_286532_, LootItemConditions.orConditions(p_286532_));
    }

    public LootItemConditionType getType() {
        return LootItemConditions.ANY_OF;
    }

    public static Builder anyOf(LootItemCondition.Builder... p_286239_) {
        return new Builder(p_286239_);
    }

    public static class Builder extends CompositeLootItemCondition.Builder {
        public Builder(LootItemCondition.Builder... p_286497_) {
            super(p_286497_);
        }

        public Builder or(LootItemCondition.Builder p_286344_) {
            this.addTerm(p_286344_);
            return this;
        }

        protected LootItemCondition create(LootItemCondition[] p_286715_) {
            return new AnyOfCondition(p_286715_);
        }
    }

    public static class Serializer extends CompositeLootItemCondition.Serializer<AnyOfCondition> {
        public Serializer() {
        }

        protected AnyOfCondition create(LootItemCondition[] p_286467_) {
            return new AnyOfCondition(p_286467_);
        }
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.storage.loot.predicates;

import java.util.Iterator;
import java.util.function.Function;

public interface ConditionUserBuilder<T extends ConditionUserBuilder<T>> {
    T when(LootItemCondition.Builder var1);

    default <E> T when(Iterable<E> p_231041_, Function<E, LootItemCondition.Builder> p_231042_) {
        T $$2 = this.unwrap();

        Object $$3;
        for(Iterator var4 = p_231041_.iterator(); var4.hasNext(); $$2 = $$2.when((LootItemCondition.Builder)p_231042_.apply($$3))) {
            $$3 = var4.next();
        }

        return $$2;
    }

    T unwrap();
}

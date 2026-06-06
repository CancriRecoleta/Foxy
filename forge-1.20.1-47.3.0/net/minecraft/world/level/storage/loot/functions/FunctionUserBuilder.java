//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.storage.loot.functions;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;

public interface FunctionUserBuilder<T extends FunctionUserBuilder<T>> {
    T apply(LootItemFunction.Builder var1);

    default <E> T apply(Iterable<E> p_230985_, Function<E, LootItemFunction.Builder> p_230986_) {
        T $$2 = this.unwrap();

        Object $$3;
        for(Iterator var4 = p_230985_.iterator(); var4.hasNext(); $$2 = $$2.apply((LootItemFunction.Builder)p_230986_.apply($$3))) {
            $$3 = var4.next();
        }

        return $$2;
    }

    default <E> T apply(E[] p_230988_, Function<E, LootItemFunction.Builder> p_230989_) {
        return this.apply((Iterable)Arrays.asList(p_230988_), p_230989_);
    }

    T unwrap();
}

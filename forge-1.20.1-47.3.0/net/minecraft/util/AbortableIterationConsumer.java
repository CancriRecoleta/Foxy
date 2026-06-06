//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util;

import java.util.function.Consumer;

@FunctionalInterface
public interface AbortableIterationConsumer<T> {
    Continuation accept(T var1);

    static <T> AbortableIterationConsumer<T> forConsumer(Consumer<T> p_261477_) {
        return (p_261916_) -> {
            p_261477_.accept(p_261916_);
            return net.minecraft.util.AbortableIterationConsumer.Continuation.CONTINUE;
        };
    }

    public static enum Continuation {
        CONTINUE,
        ABORT;

        private Continuation() {
        }

        public boolean shouldAbort() {
            return this == ABORT;
        }
    }
}

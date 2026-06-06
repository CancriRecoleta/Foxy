//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util;

import com.google.common.collect.ImmutableSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public final class Graph {
    private Graph() {
    }

    public static <T> boolean depthFirstSearch(Map<T, Set<T>> p_184557_, Set<T> p_184558_, Set<T> p_184559_, Consumer<T> p_184560_, T p_184561_) {
        if (p_184558_.contains(p_184561_)) {
            return false;
        } else if (p_184559_.contains(p_184561_)) {
            return true;
        } else {
            p_184559_.add(p_184561_);
            Iterator var5 = ((Set)p_184557_.getOrDefault(p_184561_, ImmutableSet.of())).iterator();

            Object $$5;
            do {
                if (!var5.hasNext()) {
                    p_184559_.remove(p_184561_);
                    p_184558_.add(p_184561_);
                    p_184560_.accept(p_184561_);
                    return false;
                }

                $$5 = var5.next();
            } while(!depthFirstSearch(p_184557_, p_184558_, p_184559_, p_184560_, $$5));

            return true;
        }
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClassInstanceMultiMap<T> extends AbstractCollection<T> {
    private final Map<Class<?>, List<T>> byClass = Maps.newHashMap();
    private final Class<T> baseClass;
    private final List<T> allInstances = Lists.newArrayList();

    public ClassInstanceMultiMap(Class<T> p_13531_) {
        this.baseClass = p_13531_;
        this.byClass.put(p_13531_, this.allInstances);
    }

    public boolean add(T p_13536_) {
        boolean $$1 = false;
        Iterator var3 = this.byClass.entrySet().iterator();

        while(var3.hasNext()) {
            Map.Entry<Class<?>, List<T>> $$2 = (Map.Entry)var3.next();
            if (((Class)$$2.getKey()).isInstance(p_13536_)) {
                $$1 |= ((List)$$2.getValue()).add(p_13536_);
            }
        }

        return $$1;
    }

    public boolean remove(Object p_13543_) {
        boolean $$1 = false;
        Iterator var3 = this.byClass.entrySet().iterator();

        while(var3.hasNext()) {
            Map.Entry<Class<?>, List<T>> $$2 = (Map.Entry)var3.next();
            if (((Class)$$2.getKey()).isInstance(p_13543_)) {
                List<T> $$3 = (List)$$2.getValue();
                $$1 |= $$3.remove(p_13543_);
            }
        }

        return $$1;
    }

    public boolean contains(Object p_13540_) {
        return this.find(p_13540_.getClass()).contains(p_13540_);
    }

    public <S> Collection<S> find(Class<S> p_13534_) {
        if (!this.baseClass.isAssignableFrom(p_13534_)) {
            throw new IllegalArgumentException("Don't know how to search for " + p_13534_);
        } else {
            List<? extends T> $$1 = (List)this.byClass.computeIfAbsent(p_13534_, (p_13538_) -> {
                Stream var10000 = this.allInstances.stream();
                Objects.requireNonNull(p_13538_);
                return (List)var10000.filter(p_13538_::isInstance).collect(Collectors.toList());
            });
            return Collections.unmodifiableCollection($$1);
        }
    }

    public Iterator<T> iterator() {
        return (Iterator)(this.allInstances.isEmpty() ? Collections.emptyIterator() : Iterators.unmodifiableIterator(this.allInstances.iterator()));
    }

    public List<T> getAllInstances() {
        return ImmutableList.copyOf(this.allInstances);
    }

    public int size() {
        return this.allInstances.size();
    }
}

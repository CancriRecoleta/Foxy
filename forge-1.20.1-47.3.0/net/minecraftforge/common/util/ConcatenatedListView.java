//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common.util;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

public class ConcatenatedListView<T> implements List<T> {
    private final List<? extends List<? extends T>> lists;

    @SafeVarargs
    public static <T> ConcatenatedListView<T> of(List<T>... lists) {
        return new ConcatenatedListView(List.of(lists));
    }

    public static <T> List<T> of(List<? extends List<? extends T>> members) {
        Object var10000;
        switch (members.size()) {
            case 0 -> var10000 = List.of();
            case 1 -> var10000 = Collections.unmodifiableList((List)members.get(0));
            default -> var10000 = new ConcatenatedListView(members);
        }

        return (List)var10000;
    }

    private ConcatenatedListView(List<? extends List<? extends T>> lists) {
        this.lists = lists;
    }

    public int size() {
        int size = 0;

        List list;
        for(Iterator var2 = this.lists.iterator(); var2.hasNext(); size += list.size()) {
            list = (List)var2.next();
        }

        return size;
    }

    public boolean isEmpty() {
        Iterator var1 = this.lists.iterator();

        List list;
        do {
            if (!var1.hasNext()) {
                return true;
            }

            list = (List)var1.next();
        } while(list.isEmpty());

        return false;
    }

    public boolean contains(Object o) {
        Iterator var2 = this.lists.iterator();

        List list;
        do {
            if (!var2.hasNext()) {
                return false;
            }

            list = (List)var2.next();
        } while(!list.contains(o));

        return true;
    }

    public T get(int index) {
        int size;
        for(Iterator var2 = this.lists.iterator(); var2.hasNext(); index -= size) {
            List<? extends T> list = (List)var2.next();
            size = list.size();
            if (index < size) {
                return list.get(index);
            }
        }

        throw new IndexOutOfBoundsException(index);
    }

    public int indexOf(Object o) {
        int offset = 0;

        List list;
        for(Iterator var3 = this.lists.iterator(); var3.hasNext(); offset += list.size()) {
            list = (List)var3.next();
            int foundIndex = list.indexOf(o);
            if (foundIndex >= 0) {
                return offset + foundIndex;
            }
        }

        return -1;
    }

    public int lastIndexOf(Object o) {
        int offset = 0;

        List list;
        for(Iterator var3 = Lists.reverse(this.lists).iterator(); var3.hasNext(); offset += list.size()) {
            list = (List)var3.next();
            int foundIndex = list.lastIndexOf(o);
            if (foundIndex >= 0) {
                return offset + foundIndex;
            }
        }

        return -1;
    }

    public @NotNull Iterator<T> iterator() {
        return Iterables.unmodifiableIterable(Iterables.concat(this.lists)).iterator();
    }

    public Spliterator<T> spliterator() {
        return Iterables.unmodifiableIterable(Iterables.concat(this.lists)).spliterator();
    }

    private <C extends Collection<T>> C concatenate(Supplier<C> collectionFactory) {
        C concat = (Collection)collectionFactory.get();
        Iterator var3 = this.lists.iterator();

        while(var3.hasNext()) {
            List<? extends T> list = (List)var3.next();
            concat.addAll(list);
        }

        return concat;
    }

    public @NotNull Object[] toArray() {
        return ((ArrayList)this.concatenate(ArrayList::new)).toArray();
    }

    public <T1> @NotNull T1[] toArray(@NotNull T1[] a) {
        return ((ArrayList)this.concatenate(ArrayList::new)).toArray(a);
    }

    public boolean containsAll(@NotNull Collection<?> c) {
        return ((HashSet)this.concatenate(HashSet::new)).containsAll(c);
    }

    public boolean add(T t) {
        throw new UnsupportedOperationException();
    }

    public void add(int index, T element) {
        throw new UnsupportedOperationException();
    }

    public T set(int index, T element) {
        throw new UnsupportedOperationException();
    }

    public boolean addAll(@NotNull Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    public boolean addAll(int index, @NotNull Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    public T remove(int index) {
        throw new UnsupportedOperationException();
    }

    public boolean removeAll(@NotNull Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    public boolean retainAll(@NotNull Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public @NotNull ListIterator<T> listIterator() {
        throw new UnsupportedOperationException();
    }

    public @NotNull ListIterator<T> listIterator(int index) {
        throw new UnsupportedOperationException();
    }

    public @NotNull List<T> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }
}

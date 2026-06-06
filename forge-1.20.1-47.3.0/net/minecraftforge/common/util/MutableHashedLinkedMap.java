//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common.util;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;

public class MutableHashedLinkedMap<K, V> implements Iterable<Map.Entry<K, V>> {
    public static final Hash.Strategy<? super Object> BASIC = new BasicStrategy();
    public static final Hash.Strategy<? super Object> IDENTITY = new IdentityStrategy();
    private final Hash.Strategy<? super K> strategy;
    private final Map<K, MutableHashedLinkedMap<K, V>.Entry> entries;
    private final MergeFunction<K, V> merge;
    private MutableHashedLinkedMap<K, V>.Entry head;
    private MutableHashedLinkedMap<K, V>.Entry last;
    private transient int changes;

    public MutableHashedLinkedMap() {
        this(BASIC);
    }

    public MutableHashedLinkedMap(Hash.Strategy<? super K> strategy) {
        this(strategy, (k, v1, v2) -> {
            return v2;
        });
    }

    public MutableHashedLinkedMap(Hash.Strategy<? super K> strategy, MergeFunction<K, V> merge) {
        this.head = null;
        this.last = null;
        this.changes = 0;
        this.strategy = strategy;
        this.entries = new Object2ObjectOpenCustomHashMap(strategy);
        this.merge = merge;
    }

    public @Nullable V put(K key, V value) {
        MutableHashedLinkedMap<K, V>.Entry old = (Entry)this.entries.get(key);
        if (old != null) {
            V ret = old.value;
            old.value = this.merge.apply(key, ret, value);
            return ret;
        } else {
            ++this.changes;
            MutableHashedLinkedMap<K, V>.Entry self = new Entry(key, value);
            MutableHashedLinkedMap<K, V>.Entry l = this.last;
            self.previous = l;
            if (l == null) {
                this.head = self;
            } else {
                l.next = self;
            }

            this.last = self;
            this.entries.put(key, self);
            return null;
        }
    }

    public boolean contains(K key) {
        return this.entries.containsKey(key);
    }

    public boolean isEmpty() {
        return this.entries.isEmpty();
    }

    public @Nullable V remove(K key) {
        MutableHashedLinkedMap<K, V>.Entry ret = (Entry)this.entries.remove(key);
        if (ret == null) {
            return null;
        } else {
            this.remove(ret);
            return ret.getValue();
        }
    }

    public @Nullable V get(K key) {
        MutableHashedLinkedMap<K, V>.Entry entry = (Entry)this.entries.get(key);
        return entry == null ? null : entry.getValue();
    }

    public Iterator<Map.Entry<K, V>> iterator() {
        return new Iterator<Map.Entry<K, V>>() {
            private MutableHashedLinkedMap<K, V>.Entry current;
            private MutableHashedLinkedMap<K, V>.Entry last;
            private int expectedChanges;

            {
                this.current = MutableHashedLinkedMap.this.head;
                this.last = null;
                this.expectedChanges = MutableHashedLinkedMap.this.changes;
            }

            public boolean hasNext() {
                return this.current != null;
            }

            public Map.Entry<K, V> next() {
                if (MutableHashedLinkedMap.this.changes != this.expectedChanges) {
                    throw new ConcurrentModificationException();
                } else if (!this.hasNext()) {
                    throw new NoSuchElementException();
                } else {
                    this.last = this.current;
                    this.current = this.current.next;
                    return this.last;
                }
            }

            public void remove() {
                if (this.last == null) {
                    throw new IllegalStateException("Invalid remove() call, must call next() first");
                } else if (MutableHashedLinkedMap.this.changes != this.expectedChanges) {
                    throw new ConcurrentModificationException();
                } else {
                    MutableHashedLinkedMap<K, V>.Entry removed = (Entry)MutableHashedLinkedMap.this.entries.remove(this.last.getKey());
                    if (removed != this.last) {
                        throw new ConcurrentModificationException();
                    } else {
                        ++this.expectedChanges;
                        MutableHashedLinkedMap.this.remove(this.last);
                        this.last = null;
                    }
                }
            }
        };
    }

    public @Nullable V putFirst(K key, V value) {
        return this.head != null ? this.putBefore(this.head.getKey(), key, value) : this.put(key, value);
    }

    public @Nullable V putAfter(K after, K key, V value) {
        MutableHashedLinkedMap<K, V>.Entry target = (Entry)this.entries.get(after);
        if (target == null) {
            return this.put(key, value);
        } else {
            V ret = null;
            MutableHashedLinkedMap<K, V>.Entry entry = (Entry)this.entries.get(key);
            if (entry != null) {
                ret = entry.value;
                entry.value = this.merge.apply(key, ret, value);
                this.remove(entry);
            } else {
                entry = new Entry(key, value);
                this.entries.put(key, entry);
            }

            ++this.changes;
            entry.previous = target;
            if (target.next == null) {
                this.last = target;
            } else {
                target.next.previous = entry;
            }

            entry.next = target.next;
            target.next = entry;
            return ret;
        }
    }

    public @Nullable V putBefore(K before, K key, V value) {
        MutableHashedLinkedMap<K, V>.Entry target = (Entry)this.entries.get(before);
        if (target == null) {
            return this.put(key, value);
        } else {
            V ret = null;
            MutableHashedLinkedMap<K, V>.Entry entry = (Entry)this.entries.get(key);
            if (entry != null) {
                ret = entry.value;
                entry.value = this.merge.apply(key, ret, value);
                this.remove(entry);
            } else {
                entry = new Entry(key, value);
                this.entries.put(key, entry);
            }

            ++this.changes;
            entry.previous = target.previous;
            if (target.previous == null) {
                this.head = entry;
            } else {
                target.previous.next = entry;
            }

            entry.next = target;
            target.previous = entry;
            return ret;
        }
    }

    private void remove(MutableHashedLinkedMap<K, V>.Entry e) {
        ++this.changes;
        MutableHashedLinkedMap<K, V>.Entry previous = e.previous;
        if (this.head == e) {
            this.head = e.next;
        } else if (e.previous != null) {
            e.previous.next = e.next;
            e.previous = null;
        }

        if (this.last == e) {
            this.last = previous;
        } else if (e.next != null) {
            e.next.previous = previous;
            e.next = null;
        }

    }

    public interface MergeFunction<Key, Value> {
        Value apply(Key var1, Value var2, Value var3);
    }

    private class Entry implements Map.Entry<K, V> {
        private final K key;
        private V value;
        private MutableHashedLinkedMap<K, V>.Entry previous;
        private MutableHashedLinkedMap<K, V>.Entry next;

        private Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return this.key;
        }

        public V getValue() {
            return this.value;
        }

        public V setValue(V value) {
            V old = this.value;
            this.value = value;
            return old;
        }

        public String toString() {
            return "Entry[" + this.key + ", " + this.value + "]";
        }

        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry<?, ?> e)) {
                return false;
            } else {
                boolean var10000;
                label38: {
                    label27: {
                        if (this.key == null) {
                            if (e.getKey() != null) {
                                break label27;
                            }
                        } else if (!this.key.equals(e.getKey())) {
                            break label27;
                        }

                        if (this.value == null) {
                            if (e.getValue() == null) {
                                break label38;
                            }
                        } else if (this.value.equals(e.getValue())) {
                            break label38;
                        }
                    }

                    var10000 = false;
                    return var10000;
                }

                var10000 = true;
                return var10000;
            }
        }

        public int hashCode() {
            return (this.key == null ? 0 : MutableHashedLinkedMap.this.strategy.hashCode(this.key)) ^ (this.value == null ? 0 : this.value.hashCode());
        }
    }

    private static class BasicStrategy implements Hash.Strategy<Object> {
        private BasicStrategy() {
        }

        public int hashCode(Object o) {
            return Objects.hashCode(o);
        }

        public boolean equals(Object a, Object b) {
            return Objects.equals(a, b);
        }
    }

    private static class IdentityStrategy implements Hash.Strategy<Object> {
        private IdentityStrategy() {
        }

        public int hashCode(Object o) {
            return System.identityHashCode(o);
        }

        public boolean equals(Object a, Object b) {
            return a == b;
        }
    }
}

package com.github.foxy.client.core.util;

import com.github.foxy.common.util.HierarchicalBitSet;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;

/**
 * Sparse handle table: hands out stable {@code int} ids backed by a
 * {@link HierarchicalBitSet}, with the actual {@code T} values living in a
 * geometrically-grown {@code T[]}.
 *
 * <h2>Why this shape</h2>
 * <p>Render-side subsystems often want to track a few thousand live objects with O(1)
 * lookup, dense iteration, and stable ids that survive {@link #release(int)}/{@link
 * #put(Object)} cycles. The hierarchical bitset gives O(log<sub>64</sub>n) "first
 * free" lookup; the parallel {@code T[]} gives O(1) get-by-id. Calls to
 * {@link Int2ObjectFunction#apply} only happen on growth, so the array generator can
 * use the typed {@code new T[n]} idiom without reflection or class literals scattered
 * through call sites.</p>
 *
 * <h2>Thread safety</h2>
 * <p>None. Callers serialise externally; this matches the upstream contract.</p>
 *
 * <h2>Cleanroom note</h2>
 * <p>Same algorithm as upstream Voxy with English javadoc, an explicit
 * {@link #INITIAL_CAPACITY} constant, and the growth math expressed as
 * {@code length + ceil(length * GROWTH_FACTOR)} with a doc comment instead of an
 * inline cast soup.</p>
 */
public class ExpandingObjectAllocationList<T> {

    /** Geometric growth factor: each resize multiplies capacity by ~1.75x. */
    private static final float GROWTH_FACTOR = 0.75f;

    /** Initial capacity; matches upstream's choice. */
    private static final int INITIAL_CAPACITY = 16;

    private final Int2ObjectFunction<T[]> arrayGenerator;
    private final HierarchicalBitSet bitSet;
    private T[] objects;

    /** Convenience constructor with no upper bound on allocated ids. */
    public ExpandingObjectAllocationList(Int2ObjectFunction<T[]> arrayGenerator) {
        this(arrayGenerator, -1);
    }

    /**
     * @param arrayGenerator typed-array constructor; called once at startup and on each
     *                       grow. Must return an array of the requested length.
     * @param limit          hard cap on simultaneously-allocated ids; {@code -1} for the
     *                       bitset's default 64<sup>4</sup> capacity
     */
    public ExpandingObjectAllocationList(Int2ObjectFunction<T[]> arrayGenerator, int limit) {
        this.arrayGenerator = arrayGenerator;
        this.objects = arrayGenerator.apply(INITIAL_CAPACITY);
        this.bitSet = limit == -1 ? new HierarchicalBitSet() : new HierarchicalBitSet(limit);
    }

    /**
     * Stores {@code obj} under a fresh id; grows the array when the new id won't fit.
     * Returns the id so callers can later {@link #get(int)} or {@link #release(int)}.
     */
    public int put(T obj) {
        int id = this.bitSet.allocateNext();
        if (id < 0) {
            throw new IllegalStateException("ExpandingObjectAllocationList exceeded its limit");
        }
        if (this.objects.length <= id) {
            int newLength = this.objects.length + (int) Math.ceil(this.objects.length * GROWTH_FACTOR);
            T[] grown = this.arrayGenerator.apply(newLength);
            System.arraycopy(this.objects, 0, grown, 0, this.objects.length);
            this.objects = grown;
        }
        this.objects[id] = obj;
        return id;
    }

    /** Releases the id at {@code id}; throws if it wasn't allocated. */
    public void release(int id) {
        if (!this.bitSet.free(id)) {
            throw new IllegalArgumentException("Index " + id + " was not allocated");
        }
        this.objects[id] = null;
    }

    /**
     * Returns the value at {@code index}. The bitset check is intentionally on every
     * call to surface use-after-release errors; callers that profile this hot can
     * read {@code _unsafeGetArray()} directly (not exposed by this rewrite).
     */
    public T get(int index) {
        if (!this.bitSet.isSet(index)) {
            throw new IllegalArgumentException("Index " + index + " is not allocated");
        }
        return this.objects[index];
    }

    /** Number of currently-allocated ids. */
    public int count() {
        return this.bitSet.getCount();
    }
}

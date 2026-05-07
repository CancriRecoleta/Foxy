package com.github.foxy.common.thread;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

/**
 * Concurrent map keyed by weakly-held {@link LongSupplier} keys, with a deterministic
 * cleanup callback fired on each value when its key is GC'd.
 *
 * <h2>Why this shape</h2>
 * <p>Used by {@link PerThreadContextExecutor} to attach per-thread context state to a
 * sentinel {@code ThreadObj} that lives in a thread-local. When the worker thread dies
 * its sentinel becomes unreachable, the {@link WeakReference} enqueues, and the next
 * {@link #cleanup()} sweep runs the configured cleanup consumer on the orphaned value
 * (typically a teardown of GL state or a flush of pending writes).</p>
 *
 * <h2>Sharded value table</h2>
 * <p>Values are sharded across {@link #SHARD_COUNT} buckets keyed by a hashed copy of
 * the supplier's {@code getAsLong()} id. Each bucket has its own {@link ReentrantLock},
 * so concurrent {@code computeIfAbsent} on different threads only contend when their
 * keys hash into the same shard.</p>
 *
 * <p>The key&rarr;id map ({@code weakKeyToId}) uses a separate global lock; it's only
 * touched during {@code computeIfAbsent} (insert) and {@link #cleanup()} (drain
 * reference queue), which are infrequent compared with bucket lookups.</p>
 *
 * <p>Cleanroom note: same algorithmic structure as upstream; the cleanroom rewrite
 * extracts the shard count to a named constant, replaces the array initialiser blocks
 * with explicit constructor loops, and adds full English javadoc.</p>
 */
public class WeakConcurrentCleanableHashMap<K extends LongSupplier, V> {

    /** Number of value buckets; must be a power of two. */
    private static final int SHARD_COUNT = 1 << 4;
    private static final int SHARD_MASK = SHARD_COUNT - 1;

    private final Consumer<V> valueCleaner;
    private final ReferenceQueue<K> referenceQueue = new ReferenceQueue<>();

    /** Global lock for the WeakReference&lt;K&gt; → id map. */
    private final ReentrantLock keyMapLock = new ReentrantLock();

    /** WeakReference&lt;K&gt; → key id; default {@code -1} for "absent". */
    private final Object2LongOpenHashMap<WeakReference<K>> weakKeyToId = new Object2LongOpenHashMap<>();

    /** Per-shard {@code id → V} maps. */
    private final Long2ObjectOpenHashMap<V>[] shardValues;

    /** Per-shard locks (one for each entry in {@link #shardValues}). */
    private final ReentrantLock[] shardLocks;

    private final AtomicInteger size = new AtomicInteger();

    {
        this.weakKeyToId.defaultReturnValue(-1L);
    }

    /**
     * @param cleanupConsumer invoked once per value whose key has been GC'd; called from
     *                        {@link #cleanup()}, which can run on any thread
     */
    @SuppressWarnings("unchecked")
    public WeakConcurrentCleanableHashMap(Consumer<V> cleanupConsumer) {
        this.valueCleaner = cleanupConsumer;
        this.shardValues = (Long2ObjectOpenHashMap<V>[]) new Long2ObjectOpenHashMap<?>[SHARD_COUNT];
        this.shardLocks = new ReentrantLock[SHARD_COUNT];
        for (int i = 0; i < SHARD_COUNT; i++) {
            this.shardValues[i] = new Long2ObjectOpenHashMap<>();
            this.shardLocks[i] = new ReentrantLock();
        }
    }

    /** Picks the shard index for a given key id. */
    private static int shardFor(long id) {
        return HashCommon.mix((int) id) & SHARD_MASK;
    }

    /**
     * Returns the value mapped to {@code key}, computing it via {@code valueOnAbsent}
     * the first time. Drains the cleanup queue first, so GC-orphaned entries don't
     * accumulate.
     */
    public V computeIfAbsent(K key, Supplier<V> valueOnAbsent) {
        cleanup();

        long id = key.getAsLong();
        int bucket = shardFor(id);
        var shard = this.shardValues[bucket];
        var lock = this.shardLocks[bucket];

        lock.lock();
        try {
            if (shard.containsKey(id)) {
                return shard.get(id);
            }
            V fresh = valueOnAbsent.get();
            shard.put(id, fresh);
            // Insert the WeakReference under the key-map lock so cleanup() sees a
            // consistent (weakKeyToId, shard) pair when the key is later collected.
            this.keyMapLock.lock();
            try {
                this.weakKeyToId.put(new WeakReference<>(key, this.referenceQueue), id);
            } finally {
                this.keyMapLock.unlock();
            }
            this.size.incrementAndGet();
            return fresh;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Drains the {@link ReferenceQueue} and runs {@link #valueCleaner} on every value
     * whose key has been collected. Cheap to call repeatedly: returns immediately when
     * the queue is empty.
     */
    @SuppressWarnings("unchecked")
    public void cleanup() {
        WeakReference<K> ref = (WeakReference<K>) this.referenceQueue.poll();
        if (ref == null) return;

        // Drain everything queued under one key-map lock acquisition to amortise locking.
        var orphanIds = new LongArrayFIFOQueue();
        this.keyMapLock.lock();
        try {
            do {
                long id = this.weakKeyToId.removeLong(ref);
                if (id >= 0) orphanIds.enqueue(id);
            } while ((ref = (WeakReference<K>) this.referenceQueue.poll()) != null);
        } finally {
            this.keyMapLock.unlock();
        }
        if (orphanIds.isEmpty()) return;

        int evicted = orphanIds.size();
        while (!orphanIds.isEmpty()) {
            long id = orphanIds.dequeueLong();
            int bucket = shardFor(id);
            var lock = this.shardLocks[bucket];
            V value;
            lock.lock();
            try {
                value = this.shardValues[bucket].remove(id);
            } finally {
                lock.unlock();
            }
            if (value != null) {
                this.valueCleaner.accept(value);
            } else {
                evicted--; // Already removed by clear(); don't double-count.
            }
        }
        if (this.size.addAndGet(-evicted) < 0) {
            throw new IllegalStateException("WeakConcurrentCleanableHashMap size went negative");
        }
    }

    /**
     * Removes every entry, returning the values in arbitrary order so the caller can
     * run cleanup on them all. Drains the reference queue first.
     */
    public List<V> clear() {
        cleanup();
        var values = new ArrayList<V>(size());
        // Acquire every shard lock plus the key-map lock so observers see an atomic
        // empty state; this is rarely called and not a contended path.
        for (var lock : this.shardLocks) lock.lock();
        this.keyMapLock.lock();
        try {
            this.weakKeyToId.clear();
            for (var shard : this.shardValues) {
                values.addAll(shard.values());
                shard.clear();
            }
            this.size.set(0);
        } finally {
            this.keyMapLock.unlock();
            for (var lock : this.shardLocks) lock.unlock();
        }
        return values;
    }

    /** Number of currently-live entries (excludes pending cleanup). */
    public int size() { return this.size.get(); }
}

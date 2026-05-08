package com.github.foxy.common.world;

import com.github.foxy.common.Logger;
import com.github.foxy.common.world.other.Mapper;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.StampedLock;

/**
 * Concurrent cache + LRU coordinator for {@link WorldSection} objects.
 *
 * <h2>Two-tier cache</h2>
 * <ol>
 *   <li><b>Live cache</b> &mdash; sharded {@code Long2Object} map of currently-acquired
 *       sections (refCount &ge; 0). Sharding is by Stafford-13 hash of the section key,
 *       so spatially-clustered keys spread across shards. Each shard has its own
 *       {@link StampedLock}.</li>
 *   <li><b>LRU secondary cache</b> &mdash; bounded {@link Long2ObjectLinkedOpenHashMap}
 *       holding sections whose ref count just reached zero. Lookups on a key in the LRU
 *       resurrect the section (skipping the storage round-trip).</li>
 * </ol>
 *
 * <h2>Concurrent loading</h2>
 * Upstream Voxy uses a hand-rolled busy-spin handshake between loader and waiters. The
 * cleanroom port uses a {@link CompletableFuture}-per-key as the rendezvous: the first
 * thread to {@code computeIfAbsent} a key is the loader; everyone else gets the same
 * future and blocks on {@link CompletableFuture#join()}. Each waiter calls
 * {@link WorldSection#acquire()} on the result, so the section's ref count tracks the
 * number of live callers regardless of who loaded it.
 */
public final class ActiveSectionTracker {

    /** Loader callback: deserializes section data into the supplied target. */
    @FunctionalInterface
    public interface SectionLoader {
        /** Returns the same tri-state as {@link com.github.foxy.common.config.section.SectionStorage#loadSection}. */
        int load(WorldSection section);
    }

    private final AtomicInteger loadedSections = new AtomicInteger();
    private final Long2ObjectOpenHashMap<CompletableFuture<WorldSection>>[] liveCache;
    private final StampedLock[] shardLocks;
    private final SectionLoader loader;

    private final int lruCapacity;
    private final StampedLock lruLock = new StampedLock();
    private final Long2ObjectLinkedOpenHashMap<WorldSection> lruCache;

    @Nullable public final WorldEngine engine;

    /** Convenience overload without an engine link (e.g. unit tests). */
    public ActiveSectionTracker(int shardBits, SectionLoader loader, int lruCapacity) {
        this(shardBits, loader, lruCapacity, null);
    }

    /**
     * @param shardBits   live cache is sharded into {@code 1 << shardBits} buckets
     * @param loader      callback to populate a freshly-allocated section
     * @param lruCapacity max number of unloaded sections held in the secondary cache
     * @param engine      owning engine (may be {@code null}); receives last-active updates
     */
    @SuppressWarnings("unchecked")
    public ActiveSectionTracker(int shardBits, SectionLoader loader, int lruCapacity, @Nullable WorldEngine engine) {
        this.engine = engine;
        this.loader = loader;
        this.liveCache = new Long2ObjectOpenHashMap[1 << shardBits];
        this.shardLocks = new StampedLock[1 << shardBits];
        for (int i = 0; i < this.liveCache.length; i++) {
            this.liveCache[i] = new Long2ObjectOpenHashMap<>(1024);
            this.shardLocks[i] = new StampedLock();
        }
        this.lruCapacity = lruCapacity;
        this.lruCache = new Long2ObjectLinkedOpenHashMap<>(lruCapacity);
    }

    /** Acquire-by-coordinates convenience. */
    public WorldSection acquire(int lvl, int x, int y, int z, boolean nullOnEmpty) {
        return acquire(WorldEngine.getWorldSectionId(lvl, x, y, z), nullOnEmpty);
    }

    /**
     * Returns a section for {@code key}, loading it from storage if necessary. Each call
     * adds one to the section's ref count; the caller must {@link WorldSection#release()}
     * when done.
     *
     * <p>{@code nullOnEmpty}: if {@code true} and the loader signals that no payload was
     * stored for this key (the section would be all-air), the caller gets {@code null}
     * instead of an empty section. The acquired ref is released automatically in that
     * case.</p>
     */
    public WorldSection acquire(long key, boolean nullOnEmpty) {
        if (this.engine != null) this.engine.markActive();

        int shardIdx = shardFor(key);
        var shard = this.liveCache[shardIdx];
        var lock = this.shardLocks[shardIdx];

        // Fast path: section is already live.
        long readStamp = lock.readLock();
        try {
            var existing = shard.get(key);
            if (existing != null && existing.isDone() && !existing.isCompletedExceptionally()) {
                var section = existing.getNow(null);
                if (section != null && section.tryAcquire()) {
                    return section;
                }
                // Section was evicted between the get and the tryAcquire; fall through.
            }
        } finally {
            lock.unlockRead(readStamp);
        }

        // Slow path: become the loader, or attach as a waiter to an in-flight future.
        CompletableFuture<WorldSection> future;
        boolean isLoader;
        long writeStamp = lock.writeLock();
        try {
            var existing = shard.get(key);
            if (existing != null) {
                if (existing.isDone() && !existing.isCompletedExceptionally()) {
                    var section = existing.getNow(null);
                    if (section != null && section.tryAcquire()) {
                        return section;
                    }
                    shard.remove(key);
                } else {
                    future = existing;
                    isLoader = false;
                    return waitForLoadedSection(future, key);
                }
            }
            {
                future = new CompletableFuture<>();
                shard.put(key, future);
                isLoader = true;
            }
        } finally {
            lock.unlockWrite(writeStamp);
        }

        if (isLoader) {
            return loaderPath(key, future, nullOnEmpty, shardIdx);
        }

        // Waiter path: block on the loader's future, then bump the ref count.
        return waitForLoadedSection(future, key);
    }

    private WorldSection waitForLoadedSection(CompletableFuture<WorldSection> future, long key) {
        WorldSection section = future.join();
        if (section == null) return null; // loader returned a "no payload + nullOnEmpty" miss
        if (!section.tryAcquire()) {
            return acquire(key, false);
        }
        return section;
    }

    private WorldSection loaderPath(long key, CompletableFuture<WorldSection> future,
                                    boolean nullOnEmpty, int shardIdx) {
        WorldSection section = null;
        int status = 0;

        // Try the LRU first 鈥?resurrecting an evicted section is far cheaper than reloading.
        long lruStamp = this.lruLock.writeLock();
        WorldSection evicted = null;
        try {
            section = this.lruCache.remove(key);
            // Opportunistically trim the LRU when it's significantly over capacity, so
            // a long acquire-then-release run doesn't grow the secondary cache without bound.
            if (section == null && !this.lruCache.isEmpty()
                    && this.lruCapacity + 100 < this.lruCache.size() + this.loadedSections.get()) {
                evicted = this.lruCache.removeFirst();
            }
        } finally {
            this.lruLock.unlockWrite(lruStamp);
        }
        if (evicted != null) evicted._unsafeGetRawDataArray(); // referenced just to keep field hot

        if (section != null) {
            section.primeForReuse();
        } else {
            section = new WorldSection(WorldEngine.getLevel(key),
                    WorldEngine.getX(key),
                    WorldEngine.getY(key),
                    WorldEngine.getZ(key),
                    this);
            try {
                status = this.loader.load(section);
            } catch (Throwable t) {
                Logger.error("Loader threw on " + WorldEngine.pprintPos(key), t);
                status = -1;
            }
            if (status < 0) {
                Logger.error("Loader rejected " + WorldEngine.pprintPos(key) + "; treating as air");
                status = 1;
            }
            if (status == 1) {
                // No payload 鈥?fill with air so the section is in a defined state.
                int sky = 15, block = 0;
                Arrays.fill(section.data, Mapper.composeMappingId((byte) (sky | (block << 4)), 0, 0));
            }
        }

        // Acquire on behalf of this caller before publishing, so a concurrent eviction
        // racing on getRefCount() == 0 will see at least one outstanding reference.
        section.acquire(1);
        this.loadedSections.incrementAndGet();
        future.complete(section);

        if (nullOnEmpty && status == 1) {
            section.release();
            // Do not remove the future from the cache here: subsequent acquires of the
            // same key should also see the air section so they can decide to null it.
            return null;
        }
        return section;
    }

    /**
     * Drops {@code section} from the live cache and either parks it in the LRU or releases
     * its array entirely. Called by {@link WorldSection#release(int)} when the ref count
     * reaches zero. The {@code hints} bitmask currently only carries
     * {@link WorldSection#RELEASE_HINT_POSSIBLE_REUSE}, which is informational.
     */
    void tryUnload(WorldSection section, int hints) {
        if (this.engine != null) this.engine.markActive();

        // Section may need a save sweep before eviction. Try once outside the lock to
        // give the save service a chance.
        if (section.shouldSave() && this.engine != null) {
            if (section.tryAcquire()) {
                if (section.shouldSave() && !this.engine.saveSection(section, true, true)) {
                    // The save service wouldn't take it; drop our temp acquire and let
                    // the eviction path below decide.
                    section.release(false, hints);
                } else if (!section.shouldSave()) {
                    section.release(false, hints);
                }
            }
        }
        if (section.getRefCount() != 0) return;
        if (section.isDirty || section.inSaveQueue) return;

        int shardIdx = shardFor(section.key);
        var shard = this.liveCache[shardIdx];
        var lock = this.shardLocks[shardIdx];

        WorldSection evicted = null;
        long writeStamp = lock.writeLock();
        try {
            // Re-check under the shard lock: another thread may have grabbed a ref.
            if (section.getRefCount() != 0) return;
            if (section.isDirty || section.inSaveQueue) return;
            if (!section.trySetFreed()) return;
            shard.remove(section.key);

            long lruStamp = this.lruLock.writeLock();
            try {
                if (this.lruCache.put(section.key, section) != null) {
                    throw new IllegalStateException("Duplicate LRU entry for " + WorldEngine.pprintPos(section.key));
                }
                if (this.lruCapacity < this.lruCache.size()) {
                    evicted = this.lruCache.removeFirst();
                }
            } finally {
                this.lruLock.unlockWrite(lruStamp);
            }
        } finally {
            lock.unlockWrite(writeStamp);
        }

        if (evicted != null) {
            // Drop the backing array reference for the GC. We deliberately do NOT pool
            // arrays in this cleanroom port 鈥?see WorldSection's class javadoc.
            evicted.data = null;
        }
        this.loadedSections.decrementAndGet();
    }

    private int shardFor(long key) {
        return (int) (mixStafford13(key) & (this.liveCache.length - 1));
    }

    private static long mixStafford13(long seed) {
        seed = (seed ^ (seed >>> 30)) * -4658895280553007687L;
        seed = (seed ^ (seed >>> 27)) * -7723592293110705685L;
        return seed ^ (seed >>> 31);
    }

    /** Number of currently-loaded (acquired or live) sections. */
    public int getLoadedCacheCount() { return this.loadedSections.get(); }

    /** Snapshot size of the LRU secondary cache. */
    public int getSecondaryCacheSize() {
        long stamp = this.lruLock.tryOptimisticRead();
        int size = this.lruCache.size();
        if (!this.lruLock.validate(stamp)) {
            stamp = this.lruLock.readLock();
            try { size = this.lruCache.size(); }
            finally { this.lruLock.unlockRead(stamp); }
        }
        return size;
    }
}

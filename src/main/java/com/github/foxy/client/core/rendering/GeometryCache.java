package com.github.foxy.client.core.rendering;

import com.github.foxy.client.core.rendering.building.BuiltSection;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;

import java.util.concurrent.locks.ReentrantLock;

/**
 * CPU-side LRU cache for built section geometry, keyed by packed section id.
 *
 * <h2>Eviction policy</h2>
 * <p>Insertion-order LRU: when the total bytes held by the cache exceeds
 * {@link #maxCombinedSize}, oldest entries are evicted (and their off-heap geometry
 * buffers freed) until the budget fits again.</p>
 *
 * <h2>Thread safety</h2>
 * <p>Single global {@link ReentrantLock}. Every public mutation runs under it; the
 * lock is released before any {@link BuiltSection#free()} call so the freed
 * buffer's off-heap teardown doesn't keep the cache locked.</p>
 *
 * <p>Cleanroom note: same shape as upstream Voxy with English javadoc and
 * try/finally lock discipline so an unexpected exception inside the eviction loop
 * cannot leave the cache locked.</p>
 */
public class GeometryCache {

    private final ReentrantLock lock = new ReentrantLock();
    private final Long2ObjectLinkedOpenHashMap<BuiltSection> cache = new Long2ObjectLinkedOpenHashMap<>();

    private long maxCombinedSize;
    private long currentSize;

    public GeometryCache(long maxSize) {
        setMaxTotalSize(maxSize);
    }

    /** Updates the eviction budget; the next {@link #put} will trim if currently over. */
    public void setMaxTotalSize(long size) {
        this.maxCombinedSize = size;
    }

    /**
     * Inserts {@code section} (replacing any prior entry under its key) and evicts
     * oldest entries until the cache fits in {@link #maxCombinedSize}. The replaced
     * prior entry, if any, is freed after the lock is released.
     */
    public void put(BuiltSection section) {
        BuiltSection prev;
        this.lock.lock();
        try {
            prev = this.cache.put(section.position, section);
            this.currentSize += section.geometryBuffer.size;
            if (prev != null) {
                this.currentSize -= prev.geometryBuffer.size;
            }
            while (this.maxCombinedSize <= this.currentSize) {
                var oldest = this.cache.removeFirst();
                this.currentSize -= oldest.geometryBuffer.size;
                oldest.free();
            }
        } finally {
            this.lock.unlock();
        }
        if (prev != null) prev.free();
    }

    /** Removes the entry for {@code position}; returns it without freeing. */
    public BuiltSection remove(long position) {
        this.lock.lock();
        try {
            var section = this.cache.remove(position);
            if (section != null) {
                this.currentSize -= section.geometryBuffer.size;
            }
            return section;
        } finally {
            this.lock.unlock();
        }
    }

    /** Removes and frees the entry for {@code position}, if any. */
    public void clear(long position) {
        var section = remove(position);
        if (section != null) section.free();
    }

    /** Frees every cached section; the cache is empty afterwards. */
    public void free() {
        this.lock.lock();
        try {
            this.cache.values().forEach(BuiltSection::free);
            this.cache.clear();
            this.currentSize = 0L;
        } finally {
            this.lock.unlock();
        }
    }
}

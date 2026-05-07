package com.github.foxy.common.util;

import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Best-fit free-list allocator over a single linear address space.
 *
 * <h2>What it does</h2>
 * <p>The arena hands out non-overlapping {@code [addr, addr + size)} ranges and tracks
 * which are currently allocated. Successive {@link #alloc(long)} calls fit into the
 * smallest free hole large enough; {@link #free(long)} returns the range to the free
 * list and immediately merges with adjacent free neighbours. {@link #expand(long, long)}
 * grows an existing allocation in place when the next byte is free.</p>
 *
 * <h2>Backing data structures</h2>
 * The implementation keeps two synchronized indices over the free list:
 * <ul>
 *   <li>{@link #freeByAddr} &mdash; addr &rarr; size, ordered by address. Used for
 *       merge-on-free lookups and for {@link #expand}.</li>
 *   <li>{@link #freeBySize} &mdash; size &rarr; sorted set of addrs. Used for best-fit
 *       lookups in {@link #alloc}: pick the smallest size &ge; request.</li>
 * </ul>
 * Plus a flat {@link #takenByAddr} that maps addr &rarr; size for taken ranges, so
 * {@link #free} and {@link #expand} can validate their input.
 *
 * <h2>Cleanroom note</h2>
 * Upstream Voxy hand-packs (addr, size) into single longs and walks one
 * {@code LongRBTreeSet}. That is more cache-friendly but limits per-allocation size to
 * 2&sup3;&sup0; bytes and is harder to audit. The Foxy port trades a few extra heap
 * objects per free block for a straightforward TreeMap-of-TreeSet design; we can swap in
 * the bit-packed variant later if profiling shows it matters.
 */
public final class AllocationArena {
    /** Returned by {@link #alloc(long)} when the request would exceed {@link #setLimit}. */
    public static final long SIZE_LIMIT = -1L;

    /** addr → size, ordered by address; merge-on-free uses {@link TreeMap#floorEntry}. */
    private final TreeMap<Long, Long> freeByAddr = new TreeMap<>();

    /** size → set of addrs sharing that size; best-fit uses {@link TreeMap#ceilingKey}. */
    private final TreeMap<Long, NavigableSet<Long>> freeBySize = new TreeMap<>();

    /** addr → size for currently-allocated ranges. */
    private final TreeMap<Long, Long> takenByAddr = new TreeMap<>();

    private long sizeLimit = Long.MAX_VALUE;
    private long totalSize;
    private boolean resized;

    /** Drops every allocation, taken or free, and resets the size limit to unbounded. */
    public void reset() {
        this.freeByAddr.clear();
        this.freeBySize.clear();
        this.takenByAddr.clear();
        this.sizeLimit = Long.MAX_VALUE;
        this.totalSize = 0L;
        this.resized = false;
    }

    /** Reads and clears the {@link #resized} flag. */
    public boolean getResetResized() {
        boolean r = this.resized;
        this.resized = false;
        return r;
    }

    /** Highest address ever handed out (i.e. peak watermark + free-block tail). */
    public long getSize() { return this.totalSize; }

    /** Sets a hard ceiling on {@link #getSize()}; allocations past this return {@link #SIZE_LIMIT}. */
    public void setLimit(long size) {
        if (size < this.totalSize) {
            throw new IllegalStateException("New size limit smaller than current totalSize");
        }
        this.sizeLimit = size;
    }

    /** Current size limit. */
    public long getLimit() { return this.sizeLimit; }

    /** Number of distinct free regions (i.e. external fragmentation count). */
    public int numFreeBlocks() { return this.freeByAddr.size(); }

    /**
     * Allocates {@code size} bytes; returns the address or {@link #SIZE_LIMIT} when no
     * fitting free block exists and the size cap would be exceeded by growing.
     */
    public long alloc(long size) {
        if (size <= 0L) throw new IllegalArgumentException("size must be > 0");

        // Best-fit search across the size index: smallest free block ≥ requested size.
        Long bestSize = this.freeBySize.ceilingKey(size);
        if (bestSize != null) {
            NavigableSet<Long> addrs = this.freeBySize.get(bestSize);
            long addr = addrs.first();
            removeFreeBlock(addr, bestSize);
            if (bestSize > size) {
                // Carve off a smaller free remainder.
                addFreeBlock(addr + size, bestSize - size);
            }
            this.takenByAddr.put(addr, size);
            return addr;
        }

        // No fit in the free list: grow at the tail if room remains under the cap.
        if (this.totalSize + size > this.sizeLimit) return SIZE_LIMIT;
        long addr = this.totalSize;
        this.totalSize += size;
        this.takenByAddr.put(addr, size);
        this.resized = true;
        return addr;
    }

    /**
     * Frees the allocation at {@code addr}, merging with adjacent free neighbours.
     * Returns the size of the freed range.
     */
    public long free(long addr) {
        Long size = this.takenByAddr.remove(addr);
        if (size == null) {
            throw new IllegalArgumentException("free() called on unknown address " + addr);
        }
        long blockAddr = addr;
        long blockSize = size;

        // Merge with the predecessor if it ends exactly where we start.
        var prev = this.freeByAddr.floorEntry(blockAddr - 1);
        if (prev != null && prev.getKey() + prev.getValue() == blockAddr) {
            removeFreeBlock(prev.getKey(), prev.getValue());
            blockAddr = prev.getKey();
            blockSize += prev.getValue();
        }

        // Merge with the successor if it starts exactly where we end.
        var next = this.freeByAddr.ceilingEntry(addr + size);
        if (next != null && next.getKey() == blockAddr + blockSize) {
            removeFreeBlock(next.getKey(), next.getValue());
            blockSize += next.getValue();
        }

        // If the merged block runs to the tail of the arena, shrink instead of recording a free.
        if (blockAddr + blockSize == this.totalSize) {
            this.totalSize = blockAddr;
            this.resized = true;
        } else {
            addFreeBlock(blockAddr, blockSize);
        }
        return size;
    }

    /**
     * Tries to grow the allocation at {@code addr} by {@code extra} bytes in place.
     * Returns {@code true} on success; {@code false} when the immediately following
     * range isn't a free block large enough to absorb the extension.
     */
    public boolean expand(long addr, long extra) {
        if (extra <= 0L) throw new IllegalArgumentException("extra must be > 0");
        Long size = this.takenByAddr.get(addr);
        if (size == null) {
            throw new IllegalArgumentException("expand() called on unknown address " + addr);
        }
        long endAddr = addr + size;

        // Case 1: there is a free block starting exactly at endAddr — try to consume it.
        Long nextSize = this.freeByAddr.get(endAddr);
        if (nextSize != null) {
            if (nextSize < extra) return false;
            removeFreeBlock(endAddr, nextSize);
            if (nextSize > extra) {
                addFreeBlock(endAddr + extra, nextSize - extra);
            }
            this.takenByAddr.put(addr, size + extra);
            return true;
        }

        // Case 2: this is the last allocation; grow the arena tail if the cap allows.
        if (endAddr == this.totalSize) {
            if (this.totalSize + extra > this.sizeLimit) return false;
            this.totalSize += extra;
            this.takenByAddr.put(addr, size + extra);
            this.resized = true;
            return true;
        }

        // Otherwise: another taken allocation sits right after this one. Cannot extend.
        return false;
    }

    /** Returns the size of the allocation at {@code addr}; throws if it isn't taken. */
    public long getSize(long addr) {
        Long size = this.takenByAddr.get(addr);
        if (size == null) {
            throw new IllegalArgumentException("Unknown allocation at " + addr);
        }
        return size;
    }

    // ---- free-list index maintenance ---------------------------------------------------

    private void addFreeBlock(long addr, long size) {
        this.freeByAddr.put(addr, size);
        this.freeBySize.computeIfAbsent(size, k -> new TreeSet<>()).add(addr);
    }

    private void removeFreeBlock(long addr, long size) {
        this.freeByAddr.remove(addr);
        NavigableSet<Long> bucket = this.freeBySize.get(size);
        if (bucket != null) {
            bucket.remove(addr);
            if (bucket.isEmpty()) this.freeBySize.remove(size);
        }
    }
}

package com.github.foxy.common.util;

/**
 * Sparse bit-set with first-free / first-free-run lookup in O(log<sub>64</sub>n).
 *
 * <h2>Layout</h2>
 * <p>Four levels of {@code long}s, each level holding 64 entries per parent bit:</p>
 * <pre>
 *   level A:                          1 long  =  64 bits
 *   level B:        64 longs * 64 bits           = 4096 bits
 *   level C:     64*64 longs * 64 bits           = 262144 bits
 *   level D:  64*64*64 longs * 64 bits           = 16777216 bits   (capacity)
 * </pre>
 * <p>A bit is set in {@code A[i]} iff <em>every</em> bit in the corresponding 64-bit
 * group at level B is set; the same is true for B&rarr;C and C&rarr;D. This lets
 * {@link #allocateNext()} skip a fully-occupied 64-bit group with a single 64-way
 * lookup at each level.</p>
 *
 * <h2>API</h2>
 * <ul>
 *   <li>{@link #allocateNext()} &mdash; reserve and return the lowest free index, or
 *       {@code -1} when the set is full.</li>
 *   <li>{@link #allocateNextConsecutiveCounted(int)} &mdash; reserve {@code count}
 *       contiguous indices ({@code 1 <= count <= 64}); returns the base index, or
 *       {@code -1}/{@code -2} on capacity / limit failure.</li>
 *   <li>{@link #free(int)} &mdash; release one index.</li>
 *   <li>{@link #isSet(int)}, {@link #getCount()}, {@link #getMaxIndex()} &mdash;
 *       introspection.</li>
 * </ul>
 *
 * <h2>Usage</h2>
 * <p>Used by the renderer's section trackers and MDIC slot allocators where a dense
 * set of integer ids needs first-free / first-free-run allocation behaviour.</p>
 *
 * <p>Cleanroom note: same algorithm as upstream Voxy, with English javadoc, the three
 * {@code main()}/{@code main2()}/{@code main3()} test harnesses dropped (they belong
 * in unit tests rather than production source), and the assertion-style commented-out
 * checks removed.</p>
 */
public class HierarchicalBitSet {
    /** Sentinel returned when the entire 64-bit set group is full. */
    public static final int SET_FULL = -1;

    private final int limit;
    private int count;

    /** Top-level summary: bit i = 1 iff B[i] is fully set. */
    private long A;
    private final long[] B = new long[64];
    private final long[] C = new long[64 * 64];
    private final long[] D = new long[64 * 64 * 64];

    /** Highest index ever set; tracked incrementally to support {@link #getMaxIndex()}. */
    private int maxId = -1;

    /** Default capacity 64<sup>4</sup> = 16 777 216 bits. */
    public HierarchicalBitSet() {
        this(1 << (6 * 4));
    }

    /**
     * @param limit upper bound on simultaneously-set bits; capped at 64<sup>4</sup>
     */
    public HierarchicalBitSet(int limit) {
        if (limit > (1 << (6 * 4))) {
            throw new IllegalArgumentException("limit " + limit + " exceeds capacity 64^4");
        }
        this.limit = limit;
    }

    /**
     * Reserves the lowest free index. Returns {@code -1} when the limit is reached or
     * the entire set is full.
     */
    public int allocateNext() {
        if (this.A == -1L) return -1;
        if (this.count + 1 > this.limit) return -1;

        int idx = Long.numberOfTrailingZeros(~this.A);
        long bp = this.B[idx];
        idx = Long.numberOfTrailingZeros(~bp) + 64 * idx;
        long cp = this.C[idx];
        idx = Long.numberOfTrailingZeros(~cp) + 64 * idx;
        long dp = this.D[idx];
        idx = Long.numberOfTrailingZeros(~dp) + 64 * idx;
        int reserved = idx;

        // Set the bit at level D, then propagate the "fully set" summary up A/B/C as
        // each level becomes saturated.
        dp |= 1L << (idx & 0x3F);
        this.D[idx >> 6] = dp;
        if (dp == -1L) {
            idx >>= 6;
            cp |= 1L << (idx & 0x3F);
            this.C[idx >> 6] = cp;
            if (cp == -1L) {
                idx >>= 6;
                bp |= 1L << (idx & 0x3F);
                this.B[idx >> 6] = bp;
                if (bp == -1L) {
                    idx >>= 6;
                    this.A |= 1L << (idx & 0x3F);
                }
            }
        }
        this.count++;
        if (reserved == this.maxId + 1) this.maxId++;
        return reserved;
    }

    /** Internal: set the bit at {@code idx} and propagate the saturation summary up. */
    private void set(int idx) {
        if (idx == this.maxId + 1) this.maxId++;
        long dp = this.D[idx >> 6] |= 1L << (idx & 0x3F);
        if (dp == -1L) {
            idx >>= 6;
            long cp = (this.C[idx >> 6] |= 1L << (idx & 0x3F));
            if (cp == -1L) {
                idx >>= 6;
                long bp = this.B[idx >> 6] |= 1L << (idx & 0x3F);
                if (bp == -1L) {
                    idx >>= 6;
                    this.A |= 1L << (idx & 0x3F);
                }
            }
        }
        this.count++;
    }

    /**
     * Returns the smallest free index &ge; {@code idx}. Walks the hierarchy
     * level-by-level, retrying when a level reports the search must skip ahead.
     */
    private int findNextFree(int idx) {
        int pos;
        do {
            pos = Long.numberOfTrailingZeros((~this.A) & -(1L << (idx >> 18)));
            idx = Math.max(pos << 18, idx);

            pos = Long.numberOfTrailingZeros((~this.B[idx >> 18]) & -(1L << ((idx >> 12) & 0x3F)));
            idx = Math.max((pos + ((idx >> 18) << 6)) << 12, idx);
            if (pos == 64) continue;

            pos = Long.numberOfTrailingZeros((~this.C[idx >> 12]) & -(1L << ((idx >> 6) & 0x3F)));
            idx = Math.max((pos + ((idx >> 12) << 6)) << 6, idx);
            if (pos == 64) continue;

            pos = Long.numberOfTrailingZeros(((~this.D[idx >> 6]) & -(1L << (idx & 0x3F))));
            idx = Math.max(pos + ((idx >> 6) << 6), idx);
        } while (pos == 64);
        return idx;
    }

    /**
     * Reserves {@code count} consecutive indices starting at the lowest base index
     * where the run fits.
     *
     * @param count length of the run, in {@code [1, 64]}
     * @return base index of the run, {@code -1} if the set is full, or {@code -2} if
     *         the limit would be exceeded
     */
    public int allocateNextConsecutiveCounted(int count) {
        if (count > 64) {
            throw new IllegalStateException("count > 64 not supported (no slow path implemented)");
        }
        if (this.A == -1L) return -1;
        if (this.count + count >= this.limit) return -2;

        long checkMask = (1L << count) - 1L;
        int i = findNextFree(0);
        while (true) {
            // Fuse two adjacent D[]s so a run can straddle a 64-bit boundary.
            long fused = this.D[i >> 6] >>> (i & 63);
            if (64 - (i & 63) < count) {
                fused |= this.D[(i >> 6) + 1] << (64 - (i & 63));
            }
            if ((fused & checkMask) != 0L) {
                // The first occupied bit in the window forces the search past it.
                i += Long.numberOfTrailingZeros(fused);
                i = findNextFree(i);
                continue;
            }
            for (int j = 0; j < count; j++) {
                set(j + i);
            }
            return i;
        }
    }

    /**
     * Releases the bit at {@code idx}. Returns {@code true} if it was previously set.
     * Updates {@link #getMaxIndex()} when the freed bit was the current max.
     */
    public boolean free(int idx) {
        long v = this.D[idx >> 6];
        boolean wasSet = (v & (1L << (idx & 0x3F))) != 0L;
        if (wasSet) this.count--;

        if (wasSet && idx == this.maxId) {
            // Walk back to the previous set bit so getMaxIndex stays accurate.
            for (this.maxId--; this.maxId >= 0 && !isSet(this.maxId); this.maxId--) {
                // body intentionally empty
            }
        }

        // Clearing a bit at level D propagates "no longer fully set" up the summary.
        this.D[idx >> 6] = v & ~(1L << (idx & 0x3F));
        idx >>= 6;
        this.C[idx >> 6] &= ~(1L << (idx & 0x3F));
        idx >>= 6;
        this.B[idx >> 6] &= ~(1L << (idx & 0x3F));
        idx >>= 6;
        this.A &= ~(1L << (idx & 0x3F));

        return wasSet;
    }

    /** Number of currently-set bits. */
    public int getCount() { return this.count; }

    /** Configured upper bound. */
    public int getLimit() { return this.limit; }

    /** Whether the bit at {@code idx} is set. */
    public boolean isSet(int idx) {
        return (this.D[idx >> 6] & (1L << (idx & 0x3F))) != 0L;
    }

    /** Highest set index, or {@code -1} when the set is empty. */
    public int getMaxIndex() { return this.maxId; }
}

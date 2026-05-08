package com.github.foxy.common.world;

import com.github.foxy.commonImpl.FoxyCommon;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * One reference-counted, mutable LOD slice of a {@link WorldEngine}'s world.
 *
 * <h2>Geometry</h2>
 * Each section is a fixed {@code 32&times;32&times;32 = 32768}-voxel cube of packed
 * mapping ids (see {@link com.github.foxy.common.world.other.Mapper Mapper}'s bit layout).
 * At LOD level {@code lvl}, one voxel covers a {@code 2^lvl} block edge, so a single
 * section spans {@code 32 * 2^lvl} blocks per axis.
 *
 * <h2>Reference counting</h2>
 * The {@link #atomicState} word combines a "loaded" bit (bit 0) with a 31-bit ref count
 * (bits 1..31). Acquire / release are CAS loops that fail fast if the section has been
 * unloaded under the caller. The owning {@link ActiveSectionTracker} is notified when the
 * count drops to zero so it can decide whether to evict the section.
 *
 * <h2>Dirty &amp; save-queue flags</h2>
 * {@link #isDirty} is set by mutators and cleared by the saving service. {@link #inSaveQueue}
 * marks a section that has been handed off to the save queue but not yet persisted; the two
 * are coordinated so a section is never both freed and dirty at the same time.
 *
 * <h2>Cleanroom note</h2>
 * Upstream Voxy uses {@link java.lang.invoke.VarHandle VarHandle} reflection plus a
 * recycled {@code long[]} pool driven by a {@link java.util.concurrent.ConcurrentLinkedDeque
 * ConcurrentLinkedDeque}. The cleanroom port substitutes {@link AtomicIntegerFieldUpdater}
 * (less ceremony, identical visibility semantics) and lets the GC handle backing arrays;
 * the array pool can be reintroduced if profiling shows it pays off on Forge 1.20.1.
 */
public final class WorldSection {
    /** Voxel count per section ({@code 32^3}). */
    public static final int SECTION_VOLUME = 32 * 32 * 32;

    /** {@code -Dfoxy.verifyWorldSectionExecution=true} enables defensive bounds + state checks. */
    public static final boolean VERIFY_WORLD_SECTION_EXECUTION =
            FoxyCommon.isVerificationFlagOn("verifyWorldSectionExecution");

    private static final AtomicIntegerFieldUpdater<WorldSection> STATE =
            AtomicIntegerFieldUpdater.newUpdater(WorldSection.class, "atomicState");
    private static final AtomicIntegerFieldUpdater<WorldSection> NON_EMPTY_BLOCK =
            AtomicIntegerFieldUpdater.newUpdater(WorldSection.class, "nonEmptyBlockCount");

    /** LOD level (0 = native blocks, increasing levels are coarser). */
    public final int lvl;
    /** Section x in section units. */
    public final int x;
    /** Section y in section units. */
    public final int y;
    /** Section z in section units. */
    public final int z;
    /** Packed (lvl, x, y, z) section id; matches {@link WorldEngine#getWorldSectionId}. */
    public final long key;

    /** Backing voxel storage; never reallocated for the lifetime of the section. */
    long[] data;

    /** Opaque metadata field used by the save / load layer (e.g. LUT size + flags). */
    long metadata;

    /** Population counter; only meaningful at LOD level 0. */
    @SuppressWarnings("unused") // updated via NON_EMPTY_BLOCK
    volatile int nonEmptyBlockCount;

    /** Bitmask of which 2&times;2&times;2 child sections are non-empty (renderer hint). */
    volatile byte nonEmptyChildren;

    final ActiveSectionTracker tracker;

    /** Set by mutators; cleared by the save service. */
    volatile boolean inSaveQueue;
    /** Set when {@code true}, cleared by {@link #setNotDirty()} on the save path. */
    volatile boolean isDirty;

    /**
     * Combined load-flag + ref-count word.
     *
     * <p>Bit 0: 1 when the section is loaded, 0 once it's been freed and is awaiting
     * eviction. Bits 1..31: ref count (each acquire adds 2, each release subtracts 2).</p>
     *
     * <p>Initial value 1 = loaded, refCount = 0.</p>
     */
    @SuppressWarnings("unused") // updated via STATE
    private volatile int atomicState = 1;

    WorldSection(int lvl, int x, int y, int z, ActiveSectionTracker tracker) {
        this.lvl = lvl;
        this.x = x;
        this.y = y;
        this.z = z;
        this.key = WorldEngine.getWorldSectionId(lvl, x, y, z);
        this.tracker = tracker;
        this.data = new long[SECTION_VOLUME];
    }

    /**
     * Resets the loaded flag back to {@code true} on a section that is being plucked from
     * the LRU cache for reuse. The ref count is reset to zero implicitly.
     */
    void primeForReuse() {
        STATE.set(this, 1);
    }

    /** Direct backing-array access; do not retain the reference past a release. */
    public long[] _unsafeGetRawDataArray() {
        return this.data;
    }

    @Override
    public int hashCode() {
        // (x * P) + y, then (路) * Q + z, then (路) * R + lvl: standard avalanche-y mix.
        return ((x * 1235641 + y) * 8127451 + z) * 918267913 + lvl;
    }

    /**
     * Bumps the ref count by one if the section is still loaded; returns {@code false}
     * (without modifying state) if it has already been freed.
     */
    public boolean tryAcquire() {
        int prev, next;
        do {
            prev = STATE.get(this);
            if ((prev & 1) == 0) return false; // freed
            next = prev + 2;
        } while (!STATE.compareAndSet(this, prev, next));
        return true;
    }

    /** Bumps the ref count by one; throws if the section was already freed. */
    public int acquire() { return acquire(1); }

    /**
     * Bumps the ref count by {@code count}; throws if the section was already freed.
     * Returns the new ref count (post-increment).
     */
    public int acquire(int count) {
        int state = STATE.getAndAdd(this, count << 1) + (count << 1);
        if ((state & 1) == 0) {
            throw new IllegalStateException("Tried to acquire unloaded section: " + WorldEngine.pprintPos(this.key));
        }
        return state >> 1;
    }

    /** Current ref count (excludes the load-flag bit). */
    public int getRefCount() { return STATE.get(this) >> 1; }

    /** Decrements the ref count by one; if it reaches zero, asks the tracker to evict. */
    public int release() { return release(true, 0); }

    /** Hint to {@link #release(int)} that the freed array is likely to be reused soon. */
    public static final int RELEASE_HINT_POSSIBLE_REUSE = 1;

    /** Variant of {@link #release()} that forwards an eviction hint to the tracker. */
    public int release(int hints) { return release(true, hints); }

    int release(boolean unload, int hints) {
        int state = STATE.getAndAdd(this, -2) - 2;
        if (state < 1) {
            STATE.compareAndSet(this, state, state + 2);
            return 0;
        }
        if ((state & 1) == 0) throw new IllegalStateException("Tried releasing a freed section");
        if ((state >> 1) == 0 && unload) {
            if (this.tracker != null) {
                this.tracker.tryUnload(this, hints);
            } else if (trySetFreed()) {
                // Untracked section: drop the array reference for the GC.
                this.data = null;
            }
        }
        return state >> 1;
    }

    /**
     * Atomically transitions the section from {@code (loaded, refCount=0)} to
     * {@code (freed, refCount=0)}. Returns {@code true} on success.
     *
     * <p>{@link AtomicIntegerFieldUpdater} exposes {@link AtomicIntegerFieldUpdater#compareAndSet
     * compareAndSet} (boolean) but not {@code compareAndExchange} (returns the witnessed
     * prior value), so this method reads the prior state explicitly to validate the
     * "freed but has refs" invariant before attempting the CAS.</p>
     */
    boolean trySetFreed() {
        int prev = STATE.get(this);
        if ((prev & 1) == 0) {
            // Already freed; the refCount component must be zero in that case.
            if (prev != 0) {
                throw new IllegalStateException("Section marked as free but has refs");
            }
            return false;
        }
        // prev has the loaded bit set; only state == 1 (refCount == 0) is freeable.
        if (prev != 1) return false;
        if (this.isDirty || this.inSaveQueue) return false;
        return STATE.compareAndSet(this, 1, 0);
    }

    /** Computes the linear index of voxel (x, y, z) within {@link #data}. */
    public static int getIndex(int x, int y, int z) {
        final int M = (1 << 5) - 1;
        if (VERIFY_WORLD_SECTION_EXECUTION) {
            if ((x | y | z) < 0 || (x | y | z) > M) {
                throw new IllegalArgumentException("Out of bounds: " + x + ", " + y + ", " + z);
            }
        }
        return ((y & M) << 10) | ((z & M) << 5) | (x & M);
    }

    /** Writes {@code id} at (x, y, z) and returns the previous value. */
    public long set(int x, int y, int z, long id) {
        // The block-count delta is intentionally not maintained here 鈥?upstream defers
        // that to the importer / mip service which know the previous value semantically.
        int idx = getIndex(x, y, z);
        long old = this.data[idx];
        this.data[idx] = id;
        return old;
    }

    /** Snapshot copy of the entire section data array. */
    public long[] copyData() {
        assertNotFreed();
        return Arrays.copyOf(this.data, this.data.length);
    }

    /** Copies {@link #data} into {@code dst[0..SECTION_VOLUME)}. */
    public void copyDataTo(long[] dst) { copyDataTo(dst, 0); }

    /** Copies {@link #data} into {@code dst[dstOffset..dstOffset+SECTION_VOLUME)}. */
    public void copyDataTo(long[] dst, int dstOffset) {
        assertNotFreed();
        if ((dst.length - dstOffset) < this.data.length) {
            throw new IllegalArgumentException("Destination too small");
        }
        System.arraycopy(this.data, 0, dst, dstOffset, this.data.length);
    }

    /** Index of an immediate child section in the 2&times;2&times;2 child grid. */
    public static int getChildIndex(int x, int y, int z) {
        return (x & 1) | ((y & 1) << 2) | ((z & 1) << 1);
    }

    /** 8-bit mask: bit {@code i} = 1 when child slot {@code i} has any non-air content. */
    public byte getNonEmptyChildren() { return this.nonEmptyChildren; }

    /**
     * Recomputes one bit of {@link #nonEmptyChildren} based on {@code child}'s current
     * non-empty mask. Returns:
     * <ul>
     *   <li>{@code 0} if no change,</li>
     *   <li>{@code 1} if the bit changed but the overall (any-non-empty) state did not,</li>
     *   <li>{@code 2} if the section transitioned between fully-empty and non-empty.</li>
     * </ul>
     */
    public synchronized int updateEmptyChildState(WorldSection child) {
        int childIdx = getChildIndex(child.x, child.y, child.z);
        byte msk = (byte) (1 << childIdx);
        byte prev = this.nonEmptyChildren;
        byte next = (byte) ((prev & ~msk) | (child.getNonEmptyChildren() != 0 ? msk : 0));
        this.nonEmptyChildren = next;
        return ((prev != 0) ^ (next != 0)) ? 2 : (prev != next ? 1 : 0);
    }

    /** Population counter (level-0 only). */
    public int getNonEmptyBlockCount() { return NON_EMPTY_BLOCK.get(this); }

    /** Atomically adjusts the population counter by {@code delta}; returns the new value. */
    public int addNonEmptyBlockCount(int delta) {
        int count = NON_EMPTY_BLOCK.addAndGet(this, delta);
        if (VERIFY_WORLD_SECTION_EXECUTION && count < 0) {
            throw new IllegalStateException("nonEmptyBlockCount went negative");
        }
        return count;
    }

    /**
     * Forces {@link #nonEmptyChildren} to {@code 0xFF} when the population is non-zero,
     * and {@code 0} otherwise. Used by the importer to bootstrap the mask after an
     * initial bulk fill at LOD 0. Returns {@code true} if the mask actually changed.
     */
    public synchronized boolean updateLvl0State() {
        if (VERIFY_WORLD_SECTION_EXECUTION && this.lvl != 0) {
            throw new IllegalStateException("updateLvl0State called on lvl " + this.lvl);
        }
        byte prev = this.nonEmptyChildren;
        byte next = (byte) (NON_EMPTY_BLOCK.get(this) == 0 ? 0 : 0xFF);
        this.nonEmptyChildren = next;
        return prev != next;
    }

    /** Direct setter for the deserializer; do not call from gameplay code. */
    public void _unsafeSetNonEmptyChildren(byte mask) { this.nonEmptyChildren = mask; }

    /** Allocates a section without a tracker; useful for unit tests. */
    public static WorldSection _createRawUntrackedUnsafeSection(int lvl, int x, int y, int z) {
        return new WorldSection(lvl, x, y, z, null);
    }

    /** Marks the section dirty, so the save service picks it up on the next sweep. */
    public void markDirty() { this.isDirty = true; }

    /**
     * Atomically swap {@link #inSaveQueue} from {@code !state} to {@code state}; returns
     * {@code true} when the transition succeeded. Used by the save service to claim
     * exclusive ownership before serializing.
     */
    public synchronized boolean exchangeIsInSaveQueue(boolean state) {
        if (this.inSaveQueue == !state) { this.inSaveQueue = state; return true; }
        return false;
    }

    /** Clears the dirty flag; returns the prior value. */
    public synchronized boolean setNotDirty() {
        boolean prev = this.isDirty;
        this.isDirty = false;
        return prev;
    }

    /** {@code true} when there is unsaved work and no save service has claimed it yet. */
    public boolean shouldSave() { return this.isDirty && !this.inSaveQueue; }

    /** {@code true} once {@link #trySetFreed()} has succeeded. */
    public boolean isFreed() { return (STATE.get(this) & 1) == 0; }

    public void assertNotFree() {
        if (VERIFY_WORLD_SECTION_EXECUTION && isFreed()) {
            throw new IllegalStateException("Use-after-free on " + WorldEngine.pprintPos(this.key));
        }
    }

    private void assertNotFreed() {
        assertNotFree();
    }
}

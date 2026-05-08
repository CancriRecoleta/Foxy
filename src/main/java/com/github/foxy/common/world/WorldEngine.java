package com.github.foxy.common.world;

import com.github.foxy.common.Logger;
import com.github.foxy.common.config.section.SectionStorage;
import com.github.foxy.common.world.other.Mapper;
import com.github.foxy.commonImpl.FoxyInstance;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.VarHandle;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Top-level handle for one Foxy-managed world.
 *
 * <p>Owns three things:</p>
 * <ul>
 *   <li>{@link SectionStorage} &mdash; durable section + mapping store</li>
 *   <li>{@link Mapper} &mdash; BlockState/biome registry, lazily populated and persisted
 *       through the storage</li>
 *   <li>{@link ActiveSectionTracker} &mdash; live + LRU section caches</li>
 * </ul>
 *
 * <h2>Section ids</h2>
 * Sections are identified by a 64-bit packed key:
 * <pre>
 *   bit  63       60 59          52 51          28 27           4 3   0
 *        +----------+---------------+---------------+--------------+----+
 *        |  level4  |    y (8)      |    z (24)     |    x (24)    | rs |
 *        +----------+---------------+---------------+--------------+----+
 * </pre>
 * Four reserved bits at the bottom can be repurposed by callers (renderer flags etc.).
 *
 * <h2>Lifecycle</h2>
 * Hand-off ownership: the engine takes ownership of the {@code SectionStorage} passed in
 * and closes it during {@link #free()}. Foreign code must {@link #acquireRef()} for as
 * long as it intends to keep using the engine; {@link #releaseRef()} when done. The
 * engine is &quot;idle&quot; when no refs are outstanding, no sections are live, and a
 * timeout has elapsed since the last {@link #markActive() activity tick}.
 */
public final class WorldEngine {
    /** Maximum LOD level supported by the storage key encoding. */
    public static final int MAX_LOD_LAYER = 4;

    /** Bit set in the {@code changeState} arg of {@link #markDirty(WorldSection, int, int)}. */
    public static final int UPDATE_TYPE_BLOCK_BIT = 1;
    /** Bit set in the {@code changeState} arg of {@link #markDirty(WorldSection, int, int)}. */
    public static final int UPDATE_TYPE_CHILD_EXISTENCE_BIT = 2;
    /** Bit set to suppress save-queue enqueuing on a dirty marker. */
    public static final int UPDATE_TYPE_DONT_SAVE = 4;
    /** Default mask: {@link #UPDATE_TYPE_BLOCK_BIT} | {@link #UPDATE_TYPE_CHILD_EXISTENCE_BIT}. */
    public static final int DEFAULT_UPDATE_FLAGS = UPDATE_TYPE_BLOCK_BIT | UPDATE_TYPE_CHILD_EXISTENCE_BIT;

    /** Section-mutation observer; registered by the renderer to invalidate built geometry. */
    @FunctionalInterface
    public interface ISectionChangeCallback {
        void accept(WorldSection section, int updateFlags, int neighborMsk);
    }

    /** Save observer; registered by the saving service to handle deferred persistence. */
    @FunctionalInterface
    public interface ISectionSaveCallback {
        boolean save(WorldEngine engine, WorldSection section, boolean nonBlocking, boolean sectionAlreadyAcquired);
    }

    /** Storage facade; owned by the engine, closed on {@link #free()}. */
    public final SectionStorage storage;

    private final Mapper mapper;
    private final ActiveSectionTracker sectionTracker;
    private ISectionChangeCallback dirtyCallback;
    private ISectionSaveCallback saveCallback;

    /** Set to {@code false} by {@link #free()}; every public method asserts this is {@code true}. */
    private volatile boolean isLive = true;

    /** Tracks foreign refs so {@link #isWorldIdle()} can decide when to expire. */
    private final AtomicInteger refCount = new AtomicInteger();

    /** Last wall-clock time (ms) the engine handled traffic. */
    volatile long lastActiveTime = System.currentTimeMillis();

    /** Idle timeout in milliseconds; matches upstream's 10-second default. */
    private static final long IDLE_TIMEOUT_MILLIS = 10_000L;

    /** Single-storage convenience constructor. */
    public WorldEngine(SectionStorage storage) {
        this(storage, null);
    }

    public WorldEngine(SectionStorage storage, @Nullable FoxyInstance instance) {
        this.instanceIn = instance;
        this.storage = storage;
        this.mapper = new Mapper(this.storage);
        // 6 shard bits 鈫?64 shards; LRU sized by available heap (rough heuristic from upstream).
        int lruCapacity = (Runtime.getRuntime().maxMemory() >= (1L << 32) - (200L << 20)) ? 2048 : 1024;
        this.sectionTracker = new ActiveSectionTracker(6, this.storage::loadSection, lruCapacity, this);
    }

    // ---- callbacks ---------------------------------------------------------------------

    /** Registers the renderer-side change observer. May be called once. */
    public void setDirtyCallback(ISectionChangeCallback callback) { this.dirtyCallback = callback; }

    /** Registers the save service. May be called once. */
    public void setSaveCallback(ISectionSaveCallback callback) { this.saveCallback = callback; }

    /** {@link Mapper} accessor; the engine owns its lifecycle. */
    public Mapper getMapper() { return this.mapper; }

    /** {@code true} until {@link #free()} runs. */
    public boolean isLive() { return this.isLive; }

    // ---- section acquire ---------------------------------------------------------------

    /** Variant of {@link #acquire(int, int, int, int)} that returns {@code null} for empty sections. */
    public WorldSection acquireIfExists(int lvl, int x, int y, int z) {
        ensureLive();
        return this.sectionTracker.acquire(lvl, x, y, z, true);
    }

    /** Loads (or finds in cache) the section at {@code (lvl, x, y, z)} and bumps its ref count. */
    public WorldSection acquire(int lvl, int x, int y, int z) {
        ensureLive();
        return this.sectionTracker.acquire(lvl, x, y, z, false);
    }

    /** Acquire by packed section id. */
    public WorldSection acquire(long key) {
        ensureLive();
        return this.sectionTracker.acquire(key, false);
    }

    /** Acquire-if-exists by packed section id. */
    public WorldSection acquireIfExists(long key) {
        ensureLive();
        return this.sectionTracker.acquire(key, true);
    }

    // ---- key codec ---------------------------------------------------------------------

    /**
     * Increment when {@link #getWorldSectionId} changes shape; persisted alongside saved
     * sections so old data can be detected and refused.
     */
    public static final int POS_FORMAT_VERSION = 1;

    /** Packs a (level, x, y, z) tuple into a section id. See class javadoc for layout. */
    public static long getWorldSectionId(int lvl, int x, int y, int z) {
        return ((long) lvl << 60)
                | ((long) (y & 0xFF) << 52)
                | ((long) (z & ((1 << 24) - 1)) << 28)
                | ((long) (x & ((1 << 24) - 1)) << 4);
    }

    /** Extracts the LOD level from a section id. */
    public static int getLevel(long id) { return (int) ((id >> 60) & 0xF); }

    /** Extracts the sign-extended 24-bit x coordinate from a section id. */
    public static int getX(long id) { return (int) ((id << 36) >> 40); }

    /** Extracts the sign-extended 8-bit y coordinate from a section id. */
    public static int getY(long id) { return (int) ((id << 4) >> 56); }

    /** Extracts the sign-extended 24-bit z coordinate from a section id. */
    public static int getZ(long id) { return (int) ((id << 12) >> 40); }

    /** Pretty-print a section id for log lines. */
    public static String pprintPos(long pos) {
        return getLevel(pos) + "@[" + getX(pos) + ", " + getY(pos) + ", " + getZ(pos) + "]";
    }

    // ---- dirty / save plumbing ---------------------------------------------------------

    /** Equivalent to {@code markDirty(section, DEFAULT_UPDATE_FLAGS, 0)}. */
    public void markDirty(WorldSection section) {
        markDirty(section, DEFAULT_UPDATE_FLAGS, 0);
    }

    /**
     * Notifies the dirty callback (if any) and queues the section for save unless
     * {@link #UPDATE_TYPE_DONT_SAVE} is set in {@code changeState}.
     *
     * @param section       the section that changed; must come from this engine's tracker
     * @param changeState   bitmask of {@code UPDATE_TYPE_*} flags
     * @param neighborMsk   bitmask passed verbatim to the renderer callback
     */
    public void markDirty(WorldSection section, int changeState, int neighborMsk) {
        ensureLive();
        if (section.tracker != this.sectionTracker) {
            throw new IllegalStateException("Section is not from this engine");
        }
        if (this.dirtyCallback != null) {
            this.dirtyCallback.accept(section, changeState, neighborMsk);
        }
        if ((changeState & UPDATE_TYPE_DONT_SAVE) == 0) {
            section.markDirty();
        }
    }

    /** Forwards to the registered save callback, defaulting to a blocking save. */
    public boolean saveSection(WorldSection section) {
        return saveSection(section, false, false);
    }

    /**
     * Forwards to the registered save callback. When no callback is registered the engine
     * falls back to a synchronous {@link SectionStorage#saveSection} call.
     */
    public boolean saveSection(WorldSection section, boolean nonBlocking, boolean sectionAlreadyAcquired) {
        if (this.saveCallback != null) {
            return this.saveCallback.save(this, section, nonBlocking, sectionAlreadyAcquired);
        }
        // Fallback: persist synchronously. Caller guarantees a live ref via tryAcquire upstream.
        try {
            this.storage.saveSection(section);
            section.setNotDirty();
            return true;
        } catch (Throwable t) {
            Logger.error("Synchronous save failed for " + pprintPos(section.key), t);
            return false;
        }
    }

    // ---- lifecycle ---------------------------------------------------------------------

    /**
     * Returns active-section / LRU counts for the {@code F3} debug overlay.
     * Format mirrors upstream: {@code "ACC/SCC: <live>/<lru>"}.
     */
    public void addDebugData(List<String> debug) {
        debug.add("ACC/SCC: " + this.sectionTracker.getLoadedCacheCount()
                + "/" + this.sectionTracker.getSecondaryCacheSize());
    }

    /** Total number of currently-loaded sections. */
    public int getActiveSectionCount() { return this.sectionTracker.getLoadedCacheCount(); }

    /** {@code true} when there are outstanding refs or live sections. */
    public boolean isWorldUsed() {
        ensureLive();
        return this.refCount.get() != 0 || this.sectionTracker.getLoadedCacheCount() != 0;
    }

    /** {@code true} once the engine has been idle for {@link #IDLE_TIMEOUT_MILLIS}. */
    public boolean isWorldIdle() {
        if (isWorldUsed()) {
            this.lastActiveTime = System.currentTimeMillis();
            VarHandle.fullFence();
            return false;
        }
        return IDLE_TIMEOUT_MILLIS < (System.currentTimeMillis() - this.lastActiveTime);
    }

    /** Resets the idle timer to "now". */
    public void markActive() {
        // Don't ensureLive here: the tracker calls this from many internal paths and a
        // race between free() and a tracker eviction shouldn't crash.
        this.lastActiveTime = System.currentTimeMillis();
    }

    /** Adds a foreign ref and resets the idle timer. */
    public void acquireRef() {
        ensureLive();
        this.refCount.incrementAndGet();
        markActive();
    }

    /** Drops a foreign ref. */
    public void releaseRef() {
        ensureLive();
        if (this.refCount.decrementAndGet() < 0) {
            throw new IllegalStateException("ref count went negative");
        }
        markActive();
    }

    /** Tears the engine down; storage and mapper are closed and further calls fail. */
    public void free() {
        ensureLive();
        this.isLive = false;
        VarHandle.fullFence();
        if (this.sectionTracker.getLoadedCacheCount() != 0) {
            Logger.warn("WorldEngine free() called with "
                    + this.sectionTracker.getLoadedCacheCount()
                    + " sections still loaded; flushing storage and closing anyway");
        }
        try { this.mapper.close(); } catch (Throwable t) { Logger.error("Mapper close failed", t); }
        try { this.storage.flush(); } catch (Throwable t) { Logger.error("Storage flush failed", t); }
        try { this.storage.close(); } catch (Throwable t) { Logger.error("Storage close failed", t); }
    }

    private void ensureLive() {
        if (!this.isLive) throw new IllegalStateException("World engine is not live");
    }

    /** May be {@code null}: kept for source-compat with upstream's {@code instanceIn} field. */
    @Nullable public final FoxyInstance instanceIn;
}

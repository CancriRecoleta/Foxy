package com.github.foxy.common.world.service;

import com.github.foxy.common.Logger;
import com.github.foxy.common.world.WorldEngine;
import com.github.foxy.common.world.WorldSection;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Asynchronous worker that rebuilds the parent layers of a {@link WorldEngine}'s LOD
 * pyramid by repeatedly invoking {@link SectionMipper#mipOne}.
 *
 * <h2>Queue model</h2>
 * <p>One coalescing FIFO queue per service: {@link #pending} is a {@link Set} guard so
 * the same parent key never sits twice in the queue, and {@link #queue} is the actual
 * deque the worker pulls from. Adding a key already in {@link #pending} is a no-op,
 * so {@link #scheduleParentOf} can be called liberally without risking unbounded growth.</p>
 *
 * <h2>Propagation</h2>
 * <p>After every successful {@link SectionMipper#mipOne} the worker enqueues the
 * grandparent ({@code lvl + 1} of the section just written), up to
 * {@link WorldEngine#MAX_LOD_LAYER}. This makes one initial enqueue at LOD 1 climb to
 * LOD 4 automatically without the caller spelling out each layer.</p>
 *
 * <h2>Bulk rebuild</h2>
 * <p>{@link #mipAll} walks every LOD-0 key currently held by the engine's storage and
 * enqueues its parent at LOD 1; chain propagation does the rest. Used as the
 * post-import finishing step.</p>
 *
 * <h2>Lifecycle</h2>
 * <p>One daemon worker thread per service; {@link #shutdown(boolean)} can either drain
 * the queue first or abort immediately. The service is single-use: once shut down it
 * cannot be restarted (caller spawns a fresh instance).</p>
 */
public final class MipService implements AutoCloseable {

    /** Time the worker waits for new jobs before parking; tuned for low idle CPU. */
    private static final long IDLE_POLL_MILLIS = 100L;

    private final WorldEngine engine;
    private final Thread worker;
    private final LinkedBlockingDeque<Long> queue = new LinkedBlockingDeque<>();
    private final Set<Long> pending = ConcurrentHashMap.newKeySet();

    private volatile boolean running = true;
    private volatile boolean drainOnShutdown = false;
    private final AtomicInteger inFlight = new AtomicInteger();
    private final AtomicLong totalMipped = new AtomicLong();

    /**
     * Constructs and starts a service bound to {@code engine}. The engine must outlive
     * the service; the service holds a regular reference, no acquireRef.
     */
    public MipService(WorldEngine engine) {
        this.engine = engine;
        this.worker = new Thread(this::workerLoop, "foxy-mip-service");
        this.worker.setDaemon(true);
        this.worker.start();
    }

    // ---- public scheduling --------------------------------------------------------------

    /**
     * Schedules a mip pass for the parent of the section at {@code (childLvl, cx, cy, cz)}.
     * If {@code childLvl} is already at {@link WorldEngine#MAX_LOD_LAYER}, this is a no-op
     * (no further parent exists to remip).
     */
    public void scheduleParentOf(int childLvl, int cx, int cy, int cz) {
        if (childLvl >= WorldEngine.MAX_LOD_LAYER) return;
        scheduleMip(childLvl + 1, cx >> 1, cy >> 1, cz >> 1);
    }

    /**
     * Schedules a mip pass at {@code (lvl, x, y, z)}. If a job for the same key is
     * already queued, the call is a no-op.
     *
     * @return {@code true} if the job was actually enqueued
     */
    public boolean scheduleMip(int lvl, int x, int y, int z) {
        if (!this.running) return false;
        if (lvl < 1 || lvl > WorldEngine.MAX_LOD_LAYER) return false;
        long key = WorldEngine.getWorldSectionId(lvl, x, y, z);
        if (this.pending.add(key)) {
            this.queue.offer(key);
            return true;
        }
        return false;
    }

    /**
     * Walks every LOD-0 section key in the engine's storage and enqueues its parent at
     * LOD 1 for mipping. The chain-propagation step inside the worker then climbs to
     * LOD 2, 3, 4 automatically.
     *
     * @return number of distinct LOD-1 parent keys enqueued
     */
    public int mipAll() {
        if (!this.running) return 0;
        // Use a local set first so we don't hammer the lock-free pending set with
        // duplicates while iterating storage.
        var parentKeys = new HashSet<Long>();
        this.engine.storage.iteratePositions(0, key -> {
            int x = WorldEngine.getX(key);
            int y = WorldEngine.getY(key);
            int z = WorldEngine.getZ(key);
            parentKeys.add(WorldEngine.getWorldSectionId(1, x >> 1, y >> 1, z >> 1));
        });
        int enqueued = 0;
        for (long pk : parentKeys) {
            if (this.pending.add(pk)) {
                this.queue.offer(pk);
                enqueued++;
            }
        }
        return enqueued;
    }

    // ---- worker -----------------------------------------------------------------------

    private void workerLoop() {
        try {
            while (this.running || (this.drainOnShutdown && !this.queue.isEmpty())) {
                Long key;
                try {
                    key = this.queue.poll(IDLE_POLL_MILLIS, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                if (key == null) continue;
                this.pending.remove(key);
                this.inFlight.incrementAndGet();
                try {
                    processOne(key);
                } catch (Throwable t) {
                    Logger.error("MipService: failed to mip " + WorldEngine.pprintPos(key), t);
                } finally {
                    this.inFlight.decrementAndGet();
                }
            }
        } finally {
            // Worker exit invariant: drain the queue if requested.
            if (this.drainOnShutdown) {
                drainTail();
            }
        }
    }

    /** Runs the mipper on one key and enqueues the grandparent unless we're at the top. */
    private void processOne(long key) {
        int lvl = WorldEngine.getLevel(key);
        int x = WorldEngine.getX(key);
        int y = WorldEngine.getY(key);
        int z = WorldEngine.getZ(key);
        boolean nonEmpty = SectionMipper.mipOne(this.engine, lvl, x, y, z);
        this.totalMipped.incrementAndGet();
        // Chain propagation: only continue climbing while we have actual non-air content
        // to feed the grandparent. An all-air parent contributes nothing different from
        // an absent one, so dropping the chain here saves a pass higher up.
        if (nonEmpty && lvl < WorldEngine.MAX_LOD_LAYER) {
            scheduleMip(lvl + 1, x >> 1, y >> 1, z >> 1);
        }
    }

    /** Best-effort: drain whatever is left after the worker loop has exited. */
    private void drainTail() {
        Long key;
        while ((key = this.queue.poll()) != null) {
            this.pending.remove(key);
            try {
                processOne(key);
            } catch (Throwable t) {
                Logger.error("MipService drain: " + WorldEngine.pprintPos(key) + " failed", t);
            }
        }
    }

    // ---- introspection / lifecycle ------------------------------------------------------

    /** Number of jobs currently waiting in the queue. */
    public int pendingCount() { return this.queue.size(); }

    /** Number of jobs currently being processed by the worker. */
    public int inFlightCount() { return this.inFlight.get(); }

    /** Total number of {@link SectionMipper#mipOne} calls completed since startup. */
    public long totalMipped() { return this.totalMipped.get(); }

    /**
     * Stops the worker. When {@code drain} is {@code true} the worker finishes any
     * already-queued jobs before exiting; otherwise it abandons them.
     *
     * <p>This call blocks for at most {@link WorldSection#VERIFY_WORLD_SECTION_EXECUTION
     * a few seconds} for the worker to actually exit.</p>
     */
    public void shutdown(boolean drain) {
        this.drainOnShutdown = drain;
        this.running = false;
        if (!drain) {
            this.worker.interrupt();
        }
        while (this.worker.isAlive()) {
            try {
                this.worker.join(1_000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    @Override public void close() { shutdown(true); }
}

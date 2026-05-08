package com.github.foxy.client.core.model;

import com.github.foxy.common.Logger;
import com.github.foxy.common.world.other.Mapper;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;

import java.util.List;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Async block-model bakery: takes block-state ids from the renderer, bakes their face
 * textures + metadata, and uploads the result into the GPU-side {@link ModelStore}.
 *
 * <h2>Threading</h2>
 * <p>One worker thread parks on {@link LockSupport#park()} until a request arrives;
 * each request is enqueued via {@link #requestBlockBake(int)} and unparks the worker.
 * The worker drains the entire pending queue per wake-up via
 * {@link ModelFactory#processAllThings()}.</p>
 *
 * <h2>Per-frame upload</h2>
 * <p>{@link #tick(long)} is called from the render thread; it pumps any completed
 * bakes onto the GPU through {@link ModelFactory#processUploads()}. The
 * {@code totalBudget} parameter is reserved for future use (it currently isn't
 * forwarded to the factory).</p>
 *
 * <h2>De-duplication</h2>
 * <p>{@link #seenIds} is a guard against re-enqueuing the same block-state id while
 * its first bake is still in flight; the lock pair around the set / queue keeps the
 * "check then add then enqueue" sequence atomic across multiple renderer threads.</p>
 *
 * <h2>Cleanroom note</h2>
 * <p>Same shape as upstream Voxy. The cleanroom rewrite tightens the worker thread's
 * exception path (preserves the cause, narrows visibility), explicit names for the
 * two locks, and full English javadoc.</p>
 */
public class ModelBakerySubsystem {

    private final ModelStore storage = new ModelStore();
    public final ModelFactory factory;
    private final Mapper mapper;

    private final Thread workerThread;
    private volatile boolean running = true;
    private volatile Throwable workerException;

    /** Coordinates the read-then-add to {@link #seenIds}. */
    private final ReentrantLock seenIdsLock = new ReentrantLock();
    /** Coordinates calls to {@link ModelFactory#addEntry}, which is not thread-safe. */
    private final ReentrantLock enqueueLock = new ReentrantLock();
    /** Block-state ids already enqueued or completed; sized for a typical Vanilla state-set. */
    private final IntOpenHashSet seenIds = new IntOpenHashSet(6000);

    public ModelBakerySubsystem(Mapper mapper) {
        this.mapper = mapper;
        this.factory = new ModelFactory(mapper, this.storage);
        this.workerThread = new Thread(this::workerLoop, "foxy-model-bakery");
        this.workerThread.setDaemon(true);
        this.workerThread.setUncaughtExceptionHandler((t, e) -> {
            this.running = false;
            this.workerException = e == null ? new RuntimeException("Unknown bakery worker exception") : e;
        });
        this.workerThread.start();
    }

    private void workerLoop() {
        while (this.running) {
            this.factory.processAllThings();
            LockSupport.park();
        }
    }

    /**
     * Pumps any completed bakes onto the GPU. Called from the render thread once per
     * frame. Throws if the worker thread previously crashed.
     */
    public void tick(long totalBudget) {
        Throwable t = this.workerException;
        if (t != null) {
            throw new RuntimeException("Model bakery worker threw", t);
        }
        this.factory.processUploads();
    }

    /** Stops the worker, joins it, and frees the GPU resources. */
    public void shutdown() {
        this.running = false;
        LockSupport.unpark(this.workerThread);
        try {
            this.workerThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while joining model bakery worker", e);
        }
        this.factory.free();
        this.storage.free();
    }

    /**
     * Requests a bake for {@code blockId} if it hasn't been baked or queued already.
     * Wakes the worker when the request is accepted.
     */
    public void requestBlockBake(int blockId) {
        if (this.mapper.getBlockStateCount() <= blockId) {
            Logger.error("ModelBakerySubsystem: out-of-range block-state id "
                    + blockId + " (max " + this.mapper.getBlockStateCount() + ")");
            return;
        }
        // Two-lock dance: the seenIds set is hit on every request; the heavier
        // factory.addEntry call is only run when this is genuinely a new id.
        this.seenIdsLock.lock();
        try {
            if (!this.seenIds.add(blockId)) return;
        } finally {
            this.seenIdsLock.unlock();
        }
        this.enqueueLock.lock();
        try {
            this.factory.addEntry(blockId);
        } finally {
            this.enqueueLock.unlock();
        }
        LockSupport.unpark(this.workerThread);
    }

    /** Forwards a biome registration to the factory and wakes the worker. */
    public void addBiome(Mapper.BiomeEntry biomeEntry) {
        this.factory.addBiome(biomeEntry);
        LockSupport.unpark(this.workerThread);
    }

    /** Appends one F3-overlay status line summarising in-flight / baked counts. */
    public void addDebugData(List<String> debug) {
        debug.add(String.format("IF/MC: %03d, %04d",
                this.factory.getInflightCount(),
                this.factory.getBakedCount()));
    }

    /** GPU-side model registry; shared with the renderer. */
    public ModelStore getStore() { return this.storage; }

    /** {@code true} when no bakes are currently in flight. */
    public boolean areQueuesEmpty() { return this.factory.getInflightCount() == 0; }

    /** Number of bakes still waiting on the worker. */
    public int getProcessingCount() { return this.factory.getInflightCount(); }
}

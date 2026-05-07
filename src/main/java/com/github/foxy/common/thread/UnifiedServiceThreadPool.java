package com.github.foxy.common.thread;

import com.github.foxy.common.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Worker thread pool that drives a {@link ServiceManager}'s registered {@link Service}s.
 *
 * <h2>Topology</h2>
 * <p>Three coupled pieces:</p>
 * <ul>
 *   <li>{@link ServiceManager} &mdash; owns the list of services, runs a weighted
 *       random scheduler in {@link ServiceManager#tryRunAJob}.</li>
 *   <li>{@link MultiThreadPrioritySemaphore} &mdash; gates worker thread wakeups so
 *       they only spin when there's work or a shutdown signal.</li>
 *   <li>The worker {@link Thread}s themselves &mdash; each acquires from the
 *       semaphore in a loop and asks the manager to run one job per acquisition.</li>
 * </ul>
 *
 * <p>{@link ServiceManager}'s {@code execute} hook releases a permit on the semaphore
 * via {@link #release}; the {@link MultiThreadPrioritySemaphore.Block} held by this
 * pool wakes up exactly one worker per permit.</p>
 *
 * <h2>Sizing</h2>
 * <p>{@link #setNumThreads(int)} can grow or shrink the pool live. Shrinking releases
 * extra permits on the worker block so currently-blocked workers wake up, see they
 * are surplus, and exit. Growing spawns new workers immediately. The call blocks
 * until the actual thread count matches the requested count.</p>
 *
 * <h2>Shutdown</h2>
 * <p>{@link #shutdown()} stops the manager (rejecting new jobs), releases enough
 * permits to wake every worker for a final cycle, and waits for the thread list to
 * drain. Workers always remove themselves on exit.</p>
 *
 * <p>Cleanroom note: identical contract to upstream Voxy's {@code UnifiedServiceThreadPool};
 * the cleanroom rewrite drops the legacy {@code main()} demo, narrows visibility on
 * the worker {@code Thread} list, and adds full English javadoc.</p>
 */
public class UnifiedServiceThreadPool {
    /** Worker thread priority; intentionally below the default to keep the render thread responsive. */
    private static final int WORKER_THREAD_PRIORITY = 3;

    /** Polling interval used while waiting for the worker count to settle. */
    private static final long SETTLE_POLL_MILLIS = 50L;

    /** Permit count released on shutdown to ensure every blocked worker wakes once more. */
    private static final int SHUTDOWN_RELEASE_COUNT = 10_000;

    /** Public so callers can register their own services. */
    public final ServiceManager serviceManager;

    /** Public so callers can attach extra blocks (rarely needed). */
    public final MultiThreadPrioritySemaphore groupSemaphore;

    private final MultiThreadPrioritySemaphore.Block selfBlock;
    private final ThreadGroup threadGroup;
    private final List<Thread> threads = new ArrayList<>();
    private int nextThreadId;

    public UnifiedServiceThreadPool() {
        this.threadGroup = new ThreadGroup("foxy-service-pool");
        this.serviceManager = new ServiceManager(this::release);
        this.groupSemaphore = new MultiThreadPrioritySemaphore(this.serviceManager::tryRunAJob);
        this.selfBlock = this.groupSemaphore.createBlock();
    }

    /** {@link ServiceManager} pumps job-released counts here so workers wake up. */
    private void release(int permits) {
        this.groupSemaphore.pooledRelease(permits);
    }

    /**
     * Resize the worker pool to {@code threads}. Returns {@code false} if the pool
     * already had that many workers (no-op); otherwise spawns or reaps as needed and
     * blocks until the count actually settles.
     */
    public boolean setNumThreads(int targetCount) {
        if (targetCount < 0) {
            throw new IllegalArgumentException("targetCount must be >= 0, got " + targetCount);
        }
        synchronized (this.threads) {
            int diff = targetCount - this.threads.size();
            if (diff == 0) return false;
            if (diff < 0) {
                // Shrink: extra releases wake up surplus workers, which see size > target
                // on the next iteration and exit voluntarily.
                this.selfBlock.release(-diff);
            } else {
                // Grow: spawn new daemon workers.
                for (int i = 0; i < diff; i++) {
                    Thread t = new Thread(this.threadGroup, this::workerLoop,
                            "foxy-worker-#" + (this.nextThreadId++));
                    t.setPriority(WORKER_THREAD_PRIORITY);
                    t.setDaemon(true);
                    this.threads.add(t);
                    t.start();
                }
            }
        }
        // Wait for the actual count to match; needed for the shrink case so callers
        // don't see lingering threads after this method returns.
        while (true) {
            synchronized (this.threads) {
                if (this.threads.size() == targetCount) return true;
            }
            sleepUninterruptibly(SETTLE_POLL_MILLIS);
        }
    }

    private void workerLoop() {
        // Block until either there's work or the pool is being shrunk (extra permits).
        // Upstream comment: "this is stupid but it works" — the trick is that the
        // semaphore's run-callback pumps the manager's scheduler, so workers spend
        // their lifetime alternating between blocking and running.
        this.selfBlock.acquire();

        synchronized (this.threads) {
            this.threads.remove(Thread.currentThread());
        }
    }

    /**
     * Stops the manager, drains every queued job, and waits for every worker to
     * exit. Safe to call from any thread; idempotency-checked by the underlying
     * {@link ServiceManager#shutdown()}.
     */
    public void shutdown() {
        this.serviceManager.shutdown();
        // Release more permits than we have workers so each one is guaranteed to wake.
        this.selfBlock.release(SHUTDOWN_RELEASE_COUNT);
        while (true) {
            synchronized (this.threads) {
                if (this.threads.isEmpty()) break;
            }
            sleepUninterruptibly(2 * SETTLE_POLL_MILLIS);
        }
        this.selfBlock.free();
    }

    private static void sleepUninterruptibly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Logger.error("UnifiedServiceThreadPool wait interrupted", e);
        }
    }
}

package com.github.foxy.common.thread;

import com.github.foxy.common.Logger;
import com.github.foxy.common.util.Pair;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * One unit of background work managed by a {@link ServiceManager}.
 *
 * <h2>Job model</h2>
 * <p>Callers push jobs in via {@link #execute()}, which increments an internal semaphore.
 * The owning {@link ServiceManager} elects worker threads to drain that semaphore by
 * calling {@link #runJob()}; each successful drain runs the service's per-thread
 * {@code execute} callback exactly once.</p>
 *
 * <h2>Per-thread context</h2>
 * <p>The {@link PerThreadContextExecutor} held by this service handles thread-local
 * setup/teardown of context (e.g. opening a per-worker GL context, allocating per-worker
 * scratch buffers). Callers wire that up by passing a {@code Supplier<Pair<execute, cleanup>>}
 * to {@link ServiceManager#createService}.</p>
 *
 * <h2>Limiter</h2>
 * <p>An optional {@link BooleanSupplier} can throttle execution: when it returns
 * {@code false} the manager skips this service during scheduling, leaving its jobs
 * queued. Useful for back-pressuring async jobs whose downstream targets aren't ready.</p>
 *
 * <h2>Lifecycle</h2>
 * <p>{@link #shutdown()} is one-way: it deregisters the service from its manager,
 * waits for in-flight work to finish, drains queued jobs, and tears down per-thread
 * contexts. Subsequent {@link #execute()} calls log an error and no-op.</p>
 *
 * <p>Cleanroom note: same public API as upstream Voxy's {@code Service}; the cleanroom
 * rewrite tightens the {@code isLive}/{@code isStopping} state model with a single
 * {@link AtomicBoolean} stopping gate and adds full English javadoc.</p>
 */
public class Service {
    private final PerThreadContextExecutor executor;
    private final ServiceManager manager;
    final long weight;
    final String name;
    final BooleanSupplier limiter;

    /** Counts queued jobs; {@link #execute} releases, {@link #runJob} acquires. */
    private final Semaphore tasks = new Semaphore(0);

    /** Set once {@link #shutdown()} has begun; idempotency-checked CAS. */
    private final AtomicBoolean stopping = new AtomicBoolean(false);

    /** Cleared when {@link #shutdown} finishes draining in-flight work. */
    private volatile boolean live = true;

    Service(Supplier<Pair<Runnable, Runnable>> ctxSupplier,
            ServiceManager manager,
            long weight,
            String name,
            BooleanSupplier limiter) {
        this.manager = manager;
        this.weight = weight;
        this.name = name;
        this.limiter = limiter;
        this.executor = new PerThreadContextExecutor(ctxSupplier, e -> manager.handleException(this, e));
    }

    /**
     * Queues one job for execution by a worker thread. Logs and returns silently if the
     * service is already shutting down (callers shouldn't be racing shutdown but doing
     * so is benign).
     */
    public void execute() {
        if (this.stopping.get()) {
            Logger.error("Service '" + this.name + "' received execute() after shutdown; ignored");
            return;
        }
        this.tasks.release();
        this.manager.execute(this);
    }

    /**
     * Worker-side entry: claims one queued job and runs it via the per-thread context.
     * Returns {@code false} when the service is no longer live or no job was available
     * (e.g. another worker drained it under us).
     */
    boolean runJob() {
        if (this.stopping.get() || !this.live) return false;
        if (!this.tasks.tryAcquire()) return false;
        if (!this.executor.run()) {
            throw new IllegalStateException("Per-thread executor refused to run for service " + this.name);
        }
        return true;
    }

    /** {@code true} until {@link #shutdown} has fully drained. */
    public boolean isLive() { return this.live && !this.stopping.get(); }

    /** Number of jobs currently queued (in-flight excluded). */
    public int numJobs() { return this.tasks.availablePermits(); }

    /**
     * Spin-waits until the queue empties. Polls every 10 ms; returns immediately once
     * the service has been shut down.
     */
    public void blockTillEmpty() {
        while (isLive() && numJobs() != 0) {
            Thread.yield();
            try { Thread.sleep(10L); }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while waiting for service '" + this.name + "' to drain", e);
            }
        }
    }

    /**
     * Stops the service. Returns the number of queued jobs that were dropped without
     * running. Idempotency: throws on second call.
     */
    public int shutdown() {
        if (!this.stopping.compareAndSet(false, true)) {
            throw new IllegalStateException("Service '" + this.name + "' shutdown twice");
        }
        // Order matters here:
        //   1. mark stopping so new jobs are rejected
        //   2. deregister from the manager so the scheduler skips us
        //   3. drain in-flight executor work
        //   4. drop queued jobs and clear isLive
        this.manager.removeService(this);
        this.executor.shutdown();
        int remaining = this.tasks.drainPermits();
        this.live = false;
        this.manager.remJobs(remaining);
        return remaining;
    }

    /**
     * Worker-stealing entry point: tries to claim one queued job without running it.
     * Used by the manager when balancing work across worker threads.
     */
    public boolean steal() {
        if (!this.tasks.tryAcquire()) return false;
        this.manager.remJobs(1);
        return true;
    }

    /** Drops every queued job from this service; returns the count discarded. */
    public int drain() {
        int dropped = this.tasks.drainPermits();
        if (dropped != 0) {
            this.manager.remJobs(dropped);
        }
        return dropped;
    }
}

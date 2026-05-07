package com.github.foxy.common.thread;

import com.github.foxy.common.Logger;
import com.github.foxy.common.util.Pair;
import it.unimi.dsi.fastutil.HashCommon;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.IntConsumer;
import java.util.function.Supplier;

/**
 * Multi-service scheduler shared by every {@link Service} in a worker pool.
 *
 * <h2>Job model</h2>
 * <p>The manager owns a copy-on-write array of {@link Service}s. Each service tracks
 * its own queued-job count (see {@link Service#numJobs()}); the manager keeps the
 * grand total in {@link #totalJobs} so workers can fast-exit on idle. The
 * {@link #jobRelease} callback is invoked once per queued job so the worker pool's
 * semaphore wakes the right number of workers.</p>
 *
 * <h2>Scheduling</h2>
 * <p>{@link #tryRunAJob()} picks one service via weighted random selection across
 * the set of services that have queued jobs and pass their {@link Service#limiter}
 * (if any). The weight is {@code service.weight * service.numJobs()}, which biases
 * the scheduler toward services with both a high configured priority and a long
 * backlog. The starting position rotates each call (via {@link ThreadCtx#shiftFactor})
 * to avoid bias toward the lower-indexed services on tied weights.</p>
 *
 * <h2>Return codes</h2>
 * <p>{@link #tryRunAJob()} returns:</p>
 * <ul>
 *   <li>{@code 0} &mdash; one job ran successfully.</li>
 *   <li>{@code 1} &mdash; the manager has no services or no jobs at all.</li>
 *   <li>{@code 2} &mdash; jobs exist but every candidate service failed selection
 *       (rare; happens when services churn between probes).</li>
 *   <li>{@code 3} &mdash; jobs exist but every candidate service was filtered out
 *       by its {@link Service#limiter}; caller should back off and retry.</li>
 * </ul>
 *
 * <p>Cleanroom note: same algorithmic shape as upstream Voxy with cleaner state
 * naming, English javadoc, and a {@code skipMask} that's now {@code long} explicitly
 * (so up to 64 services can be tracked; upstream silently capped at 64 too via
 * {@code skipMsk |= 1L<<i}).</p>
 */
public class ServiceManager {

    /** Hard cap on services per manager — bounded by {@code skipMask} bit width. */
    private static final int MAX_SERVICES_PER_MANAGER = 64;

    /** Per-thread scratch state used to randomise service selection without contention. */
    private static final class ThreadCtx {
        int shiftFactor;
        long seed;

        ThreadCtx() {
            this.seed = HashCommon.murmurHash3(System.nanoTime() ^ System.identityHashCode(this));
        }

        long rand(long upperExclusive) {
            this.seed = HashCommon.mix(this.seed);
            // murmur seeds can be negative; mask to the positive range first.
            long positive = this.seed & Long.MAX_VALUE;
            return positive % upperExclusive;
        }
    }

    private final IntConsumer jobRelease;
    private final ThreadLocal<ThreadCtx> threadCtx = ThreadLocal.withInitial(ThreadCtx::new);
    private final AtomicInteger totalJobs = new AtomicInteger();

    /** Copy-on-write services array; reads are unlocked, writes are synchronized. */
    private volatile Service[] services = new Service[0];

    private volatile boolean shutdown;

    public ServiceManager(IntConsumer jobRelease) {
        this.jobRelease = jobRelease;
    }

    // ---- service registration ---------------------------------------------------------

    /** Convenience: services with no per-thread cleanup. */
    public Service createServiceNoCleanup(Supplier<Runnable> ctxFactory, long weight) {
        return createServiceNoCleanup(ctxFactory, weight, "");
    }

    /** Convenience: services with no per-thread cleanup, named for diagnostics. */
    public Service createServiceNoCleanup(Supplier<Runnable> ctxFactory, long weight, String name) {
        return createService(() -> new Pair<>(ctxFactory.get(), () -> {}), weight, name);
    }

    /** Service with per-thread (execute, cleanup) context, no limiter. */
    public Service createService(Supplier<Pair<Runnable, Runnable>> ctxFactory, long weight) {
        return createService(ctxFactory, weight, "");
    }

    /** Named service with per-thread (execute, cleanup) context, no limiter. */
    public Service createService(Supplier<Pair<Runnable, Runnable>> ctxFactory, long weight, String name) {
        return createService(ctxFactory, weight, name, null);
    }

    /**
     * Full-control entry point.
     *
     * @param ctxFactory per-thread context supplier; each worker thread sees one
     *                   {@code (execute, cleanup)} pair via {@link PerThreadContextExecutor}
     * @param weight     scheduling weight; larger = more likely to be picked when its
     *                   queue has work
     * @param name       diagnostic name (used in error logs)
     * @param limiter    optional gate; when present and returning {@code false}, the
     *                   service is skipped during scheduling
     */
    public synchronized Service createService(Supplier<Pair<Runnable, Runnable>> ctxFactory,
                                              long weight,
                                              String name,
                                              BooleanSupplier limiter) {
        if (this.services.length >= MAX_SERVICES_PER_MANAGER) {
            throw new IllegalStateException("ServiceManager hit max services (" + MAX_SERVICES_PER_MANAGER + ")");
        }
        Service service = new Service(ctxFactory, this, weight, name, limiter);
        Service[] grown = Arrays.copyOf(this.services, this.services.length + 1);
        grown[grown.length - 1] = service;
        this.services = grown;
        return service;
    }

    // ---- scheduler -------------------------------------------------------------------

    /** Worker entry; see class javadoc for return codes. */
    public int tryRunAJob() {
        if (this.services.length == 0 || this.totalJobs.get() == 0) return 1;
        return runOneJob();
    }

    private int runOneJob() {
        ThreadCtx ctx = this.threadCtx.get();
        // Outer loop: retry if the services array changes underneath us (a Service
        // shutting down between probe and selection is the typical cause).
        outer:
        while (true) {
            Service[] services = this.services;
            if (services.length == 0) return 1;
            if (this.totalJobs.get() == 0) return 1;

            long skipMask = 0L;
            int shiftFactor = (ctx.shiftFactor++) & Integer.MAX_VALUE;
            int countdown = shiftFactor;
            long totalWeight = 0L;
            Service tieBreakChoice = null;

            for (int i = 0; i < services.length; i++) {
                Service service = services[i];
                if (!service.isLive()) {
                    // The array changed under us; refetch on next loop.
                    Thread.yield();
                    continue outer;
                }
                boolean reachedShift = countdown-- <= 0;
                if (service.limiter != null && !service.limiter.getAsBoolean()) {
                    skipMask |= 1L << i;
                    continue;
                }
                long jc = service.numJobs();
                if (reachedShift && jc != 0 && tieBreakChoice == null) {
                    tieBreakChoice = service;
                }
                totalWeight += jc * service.weight;
            }
            if (totalWeight == 0L) {
                return skipMask != 0L ? 3 : 2;
            }

            long sample = ctx.rand(totalWeight);
            Service selected = null;
            for (int i = 0; i < services.length; i++) {
                Service service = services[(i + shiftFactor) % services.length];
                if (service.limiter != null
                        && (((skipMask & (1L << i)) != 0L) || !service.limiter.getAsBoolean())) {
                    skipMask |= 1L << i;
                    continue;
                }
                sample -= service.numJobs() * service.weight;
                if (sample <= 0L) {
                    selected = service;
                    break;
                }
            }
            if (selected == null) selected = tieBreakChoice;
            if (selected == null) {
                return skipMask != 0L ? 3 : 2;
            }
            if (!selected.isLive()) {
                continue; // Live state changed; retry.
            }
            if (!selected.runJob()) {
                continue; // Race with another worker draining the queue; retry.
            }
            if (this.totalJobs.decrementAndGet() < 0) {
                throw new IllegalStateException("ServiceManager.totalJobs went negative");
            }
            return 0;
        }
    }

    // ---- shutdown --------------------------------------------------------------------

    /**
     * Stops accepting new services and blocks until every existing service has
     * called {@link Service#shutdown()} on its own. Caller is responsible for
     * triggering each service's shutdown; this just waits for them all to drain.
     */
    public void shutdown() {
        if (this.shutdown) {
            throw new IllegalStateException("ServiceManager shutdown twice");
        }
        this.shutdown = true;
        while (this.services.length != 0) {
            Thread.yield();
            synchronized (this) {
                for (Service s : this.services) {
                    if (s.isLive()) {
                        throw new IllegalStateException(
                                "Service '" + s.name + "' still live during ServiceManager shutdown");
                    }
                }
            }
        }
        while (this.totalJobs.get() != 0) {
            Thread.yield();
        }
    }

    // ---- internal API used by Service ------------------------------------------------

    synchronized void removeService(Service service) {
        Service[] services = this.services;
        Service[] shrunk = new Service[services.length - 1];
        int j = 0;
        for (Service s : services) {
            if (s != service) shrunk[j++] = s;
        }
        if (j != shrunk.length) {
            throw new IllegalStateException("removeService called with unknown service '" + service.name + "'");
        }
        this.services = shrunk;
    }

    void execute(Service service) {
        this.totalJobs.incrementAndGet();
        this.jobRelease.accept(1);
    }

    void remJobs(int count) {
        // The pool's semaphore can be over-credited; the worker side just sees one
        // extra wake-up that runOneJob() returns 1 (no jobs) on, which is benign.
        if (this.totalJobs.addAndGet(-count) < 0) {
            throw new IllegalStateException("ServiceManager.totalJobs went negative on remJobs(" + count + ")");
        }
    }

    void handleException(Service service, Exception exception) {
        Logger.error("Service '" + service.name + "' on thread '" + Thread.currentThread().getName()
                + "' threw an exception", exception);
    }
}

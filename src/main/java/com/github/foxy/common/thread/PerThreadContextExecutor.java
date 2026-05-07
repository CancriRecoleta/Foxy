package com.github.foxy.common.thread;

import com.github.foxy.common.Logger;
import com.github.foxy.common.util.Pair;
import com.github.foxy.common.util.TrackedObject;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

/**
 * Runnable dispatcher that materialises a fresh execution context the first time each
 * worker thread enters it.
 *
 * <h2>Per-thread context</h2>
 * <p>Owned by a {@link Service}; on first {@link #run()} from a given thread the
 * supplied {@code contextFactory} produces a {@link Pair} of {@code (execute, cleanup)}
 * runnables. {@code execute} is invoked for every {@link #run()} on that thread;
 * {@code cleanup} runs once when the thread is GC'd or the executor shuts down. This
 * lets a service set up GL contexts, scratch buffers, native handles, etc. exactly once
 * per worker without the caller having to thread-locally manage them.</p>
 *
 * <h2>Weak-keyed cleanup</h2>
 * <p>Per-thread contexts are stored in a {@link WeakConcurrentCleanableHashMap} keyed
 * by a {@link ThreadObj} sentinel that lives in a {@link ThreadLocal}. When the worker
 * thread dies and its sentinel becomes unreachable, the map's cleanup callback runs the
 * context's {@code cleanup} runnable. Polling for cleanup happens implicitly on every
 * {@link #run()} (via {@link WeakConcurrentCleanableHashMap#computeIfAbsent}'s built-in
 * drain) so dead-thread tear-down is timely without needing a dedicated reaper thread.</p>
 *
 * <h2>Shutdown</h2>
 * <p>{@link #shutdown()} flips the executor to non-live, spins until every in-flight
 * {@link #run()} returns, then runs the {@code cleanup} hook on every still-attached
 * context and finally invokes {@link TrackedObject#free0() free0} so leak tracking sees
 * the object as released.</p>
 *
 * <p>Cleanroom note: identical contract to upstream Voxy's {@code PerThreadContextExecutor};
 * the cleanroom rewrite drops the legacy {@code main()} demo, narrows visibility on
 * helper types, and adds full English javadoc.</p>
 */
public class PerThreadContextExecutor extends TrackedObject {

    /** Caller-supplied (execute, cleanup) pair packaged for storage. */
    private static final class ThreadContext {
        final Runnable execute;
        final Runnable cleanup;
        ThreadContext(Pair<Runnable, Runnable> pair) {
            this(pair.left(), pair.right());
        }
        ThreadContext(Runnable execute, Runnable cleanup) {
            this.execute = execute;
            this.cleanup = cleanup;
        }
    }

    /**
     * Sentinel that anchors the per-thread context. Every worker has exactly one
     * {@link ThreadObj} via {@link #THREAD_CTX}; the executor's weak map keys off this
     * sentinel rather than the {@link Thread} itself so the JVM can collect a dead
     * thread's slot without us holding a strong reference to the thread object.
     */
    private record ThreadObj(long id) implements LongSupplier {
        private static final AtomicLong NEXT_ID = new AtomicLong();
        ThreadObj() { this(NEXT_ID.getAndIncrement()); }
        @Override public long getAsLong() { return this.id; }
    }

    private static final ThreadLocal<ThreadObj> THREAD_CTX = ThreadLocal.withInitial(ThreadObj::new);

    private final WeakConcurrentCleanableHashMap<ThreadObj, ThreadContext> contexts =
            new WeakConcurrentCleanableHashMap<>(this::cleanContext);
    private final Supplier<ThreadContext> contextFactory;
    private final Consumer<Exception> exceptionHandler;

    /** Counts threads currently inside {@link #run()}; used by {@link #shutdown()}. */
    private final AtomicInteger inFlight = new AtomicInteger();

    /** Cleared by {@link #shutdown()}; reads of this gate the {@link #run()} body. */
    private volatile boolean live = true;

    /** Convenience constructor that defaults to logging exceptions at ERROR. */
    PerThreadContextExecutor(Supplier<Pair<Runnable, Runnable>> ctxFactory) {
        this(ctxFactory, e -> Logger.error("PerThreadContextExecutor caught exception", e));
    }

    /**
     * @param ctxFactory       called once per worker thread to produce its
     *                         {@code (execute, cleanup)} pair
     * @param exceptionHandler invoked when {@code execute} or {@code cleanup} throws;
     *                         exceptions are caught so a misbehaving service can't
     *                         take down a worker thread
     */
    PerThreadContextExecutor(Supplier<Pair<Runnable, Runnable>> ctxFactory, Consumer<Exception> exceptionHandler) {
        this.contextFactory = () -> new ThreadContext(ctxFactory.get());
        this.exceptionHandler = exceptionHandler;
    }

    private void cleanContext(ThreadContext ctx) {
        try {
            ctx.cleanup.run();
        } catch (Exception e) {
            this.exceptionHandler.accept(e);
        }
    }

    /**
     * Runs the per-thread {@code execute} runnable; lazily allocates the context on
     * first invocation from this thread. Returns {@code false} when the executor has
     * been shut down (in which case the runnable was skipped).
     */
    boolean run() {
        this.inFlight.incrementAndGet();
        if (!this.live) {
            this.inFlight.decrementAndGet();
            this.exceptionHandler.accept(new IllegalStateException("PerThreadContextExecutor used after shutdown"));
            return false;
        }
        try {
            ThreadContext ctx = this.contexts.computeIfAbsent(THREAD_CTX.get(), this.contextFactory);
            try {
                ctx.execute.run();
            } catch (Exception e) {
                this.exceptionHandler.accept(e);
            }
            return true;
        } finally {
            this.inFlight.decrementAndGet();
        }
    }

    /**
     * Stops accepting work and tears down every still-attached context. Spins until
     * {@link #inFlight} drains; safe but blocking.
     */
    public void shutdown() {
        if (!this.live) {
            throw new IllegalStateException("PerThreadContextExecutor shutdown twice");
        }
        this.live = false;
        // Wait for every in-flight run() to observe live=false and exit. The window is
        // tiny in practice — a single execute callback's runtime — so a busy-yield loop
        // beats a CountDownLatch + per-run synchronization on the hot path.
        while (this.inFlight.get() != 0) {
            Thread.onSpinWait();
        }
        for (ThreadContext ctx : this.contexts.clear()) {
            cleanContext(ctx);
        }
        free0();
    }

    /** Mirrors {@link TrackedObject#free()} onto {@link #shutdown()}. */
    @Override
    public void free() {
        shutdown();
    }

    /** {@code true} until {@link #shutdown()} has run. */
    public boolean isLive() { return this.live; }
}

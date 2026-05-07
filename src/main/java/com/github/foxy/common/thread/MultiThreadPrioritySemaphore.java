package com.github.foxy.common.thread;

import com.github.foxy.common.util.TrackedObject;

import java.util.Arrays;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.IntSupplier;

/**
 * Priority-aware multi-semaphore: lets several thread pools share a common job stream
 * while still letting each pool prioritise its own private queue.
 *
 * <h2>Why this exists</h2>
 * <p>The renderer wants two scheduling modes simultaneously:</p>
 * <ul>
 *   <li>workers from the dedicated Foxy pool should pick up Foxy jobs first;</li>
 *   <li>any thread that happens to wander in (e.g. a Minecraft worker) can opportunistically
 *       drain the same queue when nothing else is pressing.</li>
 * </ul>
 * <p>The {@link Block} abstraction holds two semaphores — a {@code localSemaphore} for
 * the dedicated pool's permits and a {@code blockSemaphore} that mirrors the global
 * pooled count. {@link Block#acquire(boolean)} is a state machine over those two: take
 * the local one first when present, otherwise try to run a pooled job, and only sleep
 * when both are empty.</p>
 *
 * <h2>Pool integration</h2>
 * <p>{@link #pooledRelease(int)} releases {@code n} permits both into the global pool
 * and into every block's mirror semaphore so all blocks compete on the same job
 * stream. The {@link IntSupplier} {@code executor} returns one of the four status
 * codes documented on {@link ServiceManager#tryRunAJob}.</p>
 *
 * <p>Cleanroom note: same algorithmic shape as upstream Voxy. The cleanroom rewrite
 * fixes a one-past-the-end bug in {@code freeBlock} (upstream's loop ran
 * {@code i <= blocks.length} which reads off the source array's tail), tightens the
 * doc on the dual-semaphore protocol, and removes the upstream `// absolutely no idea
 * if this works` running commentary now that the protocol is documented.</p>
 */
public class MultiThreadPrioritySemaphore {

    /** One subscriber to the shared pool. */
    public static final class Block extends TrackedObject {
        private final Semaphore blockSemaphore = new Semaphore(0);
        private final Semaphore localSemaphore = new Semaphore(0);
        private final MultiThreadPrioritySemaphore owner;

        Block(MultiThreadPrioritySemaphore owner) {
            this.owner = owner;
        }

        /**
         * Releases {@code permits} permits to local consumers, and mirrors the same
         * count into the block-side global counter so {@code acquire(true)} sees them.
         *
         * <p>Order matters: local first, then block, so a concurrent {@link #acquire}
         * that wakes on the block side and races to {@code tryAcquire(local)} sees the
         * local permit.</p>
         */
        public void release(int permits) {
            this.localSemaphore.release(permits);
            this.blockSemaphore.release(permits);
        }

        /** {@code acquire(true)}: pump pooled jobs while waiting for a local one. */
        public void acquire() {
            acquire(true);
        }

        /**
         * Block until a local permit is acquired. When {@code runJob} is true, the
         * caller's CPU is willing to drain pooled jobs while it waits; when false, it
         * just sleeps on the local semaphore.
         */
        public void acquire(boolean runJob) {
            while (true) {
                if (runJob) {
                    this.blockSemaphore.acquireUninterruptibly();
                    if (this.localSemaphore.tryAcquire()) {
                        return;
                    }
                    if (this.owner.tryRun(this)) {
                        return;
                    }
                } else {
                    this.localSemaphore.acquireUninterruptibly();
                    // Mirror the local acquire onto the block-side counter so the
                    // accounting stays balanced. If the block side is empty, we drop
                    // the failed attempt; the global pool can be over-credited safely.
                    this.blockSemaphore.tryAcquire();
                    return;
                }
            }
        }

        /** Tears down the block, deregistering it from the owning semaphore. */
        public void free() {
            this.owner.freeBlock(this);
            free0();
        }

        /** Outstanding local permits. */
        public int availablePermits() {
            return this.localSemaphore.availablePermits();
        }

        /**
         * Non-blocking version of {@link #acquire(boolean) acquire(false)}. Returns
         * {@code true} when a local permit was acquired; {@code false} when no permits
         * were available. Releases its block-side mirror permit on the failure path.
         */
        public boolean tryAcquire() {
            if (this.localSemaphore.availablePermits() == 0) return false;
            if (!this.blockSemaphore.tryAcquire()) return false;
            if (this.localSemaphore.tryAcquire()) {
                return true;
            }
            this.blockSemaphore.release(1);
            return false;
        }
    }

    /** Pooled job slot count, drained by {@link #tryRun}. */
    private final Semaphore pooledSemaphore = new Semaphore(0);

    /** Drains one pooled job; returns {@link ServiceManager#tryRunAJob}-style status. */
    private final IntSupplier executor;

    /** Copy-on-write list of subscribers; reads are unlocked, writes synchronized. */
    private volatile Block[] blocks = new Block[0];

    public MultiThreadPrioritySemaphore(IntSupplier executor) {
        this.executor = executor;
    }

    /** Registers a new subscriber. */
    public synchronized Block createBlock() {
        Block block = new Block(this);
        Block[] grown = Arrays.copyOf(this.blocks, this.blocks.length + 1);
        grown[grown.length - 1] = block;
        this.blocks = grown;
        return block;
    }

    private synchronized void freeBlock(Block block) {
        Block[] old = this.blocks;
        Block[] shrunk = new Block[old.length - 1];
        int j = 0;
        // Cleanroom fix: upstream's loop ran i <= old.length (one past the end). Use
        // a normal half-open range here.
        for (int i = 0; i < old.length; i++) {
            if (old[i] != block) shrunk[j++] = old[i];
        }
        if (j != shrunk.length) {
            throw new IllegalStateException("freeBlock called with unregistered block");
        }
        this.blocks = shrunk;
    }

    /** Mirrors {@code permits} pooled jobs into every block's block-side counter. */
    public void pooledRelease(int permits) {
        this.pooledSemaphore.release(permits);
        for (Block block : this.blocks) {
            block.blockSemaphore.release(permits);
        }
    }

    /**
     * Drains one pooled job on behalf of {@code block}. Returns {@code true} when it
     * also captured a local permit for {@code block} during the wait (i.e. the caller
     * can return from {@link Block#acquire(boolean)}); {@code false} when it should
     * loop back and try again.
     */
    private boolean tryRun(Block block) {
        if (!this.pooledSemaphore.tryAcquire()) {
            return false;
        }
        while (true) {
            int status = this.executor.getAsInt();
            if (status == 0 || status == 1) {
                // 0 = ran one job, 1 = no work — either way we're done with this turn.
                return false;
            }
            if (status >= 2) {
                // 2 / 3 = throttle / limiter blocked. Wait briefly for a local permit;
                // if one shows up, hand the pooled credit back and resume locally.
                try {
                    if (block.localSemaphore.tryAcquire(10L, TimeUnit.MILLISECONDS)) {
                        block.blockSemaphore.tryAcquire();
                        pooledRelease(1);
                        return true;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("MultiThreadPrioritySemaphore.tryRun interrupted", e);
                }
            }
        }
    }
}

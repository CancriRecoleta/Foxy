package com.github.foxy.client.core.rendering;

import com.github.foxy.common.world.WorldEngine;
import com.github.foxy.common.world.WorldSection;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;

import java.util.concurrent.locks.StampedLock;
import java.util.function.LongConsumer;

import static com.github.foxy.common.world.WorldEngine.UPDATE_TYPE_BLOCK_BIT;
import static com.github.foxy.common.world.WorldEngine.UPDATE_TYPE_CHILD_EXISTENCE_BIT;

/**
 * Sharded watch-set + dispatcher: routes per-section world events to render-side
 * mesh-rebuild and child-update hooks based on a per-position bitmask.
 *
 * <h2>Sharding</h2>
 * <p>The {@link Long2ByteOpenHashMap} watch sets are split into {@link #SLICES}
 * shards by a Stafford-13 hash of the section position; each shard has its own
 * {@link StampedLock}. This keeps {@link #watch} / {@link #unwatch} scalable when
 * many threads register interest concurrently (one per worker during cold-load).</p>
 *
 * <h2>StampedLock optimistic upgrade</h2>
 * <p>{@link #watch} starts under a read lock, computes whether the call would
 * actually modify the set, and only upgrades to a write lock when there's a
 * change. The upgrade goes through {@link StampedLock#tryConvertToWriteLock} to
 * skip a release-then-acquire cycle when no other thread is waiting; on a failed
 * conversion we drop the read, take the write, and recompute (the world-state
 * could have shifted under us between the two stamps).</p>
 *
 * <h2>Event types</h2>
 * <p>{@link WorldEngine#UPDATE_TYPE_BLOCK_BIT} fires
 * {@link #renderMeshGen} and (on the very first watch) {@link #initialRenderMeshGen}.
 * {@link WorldEngine#UPDATE_TYPE_CHILD_EXISTENCE_BIT} fires
 * {@link #childUpdateCallback} with the modified section.</p>
 *
 * <p>Cleanroom note: identical algorithm to upstream Voxy with English javadoc, a
 * named {@code SLICES_MASK} for the shard index reduction, try/finally lock
 * discipline, and the explicit re-check when the optimistic write upgrade fails.</p>
 */
public class SectionUpdateRouter implements ISectionWatcher {

    /** Number of shards; must be a power of two. */
    private static final int SLICES = 1 << 4;
    private static final int SLICES_MASK = SLICES - 1;

    /** Receives section-level events that altered child-existence flags. */
    public interface IChildUpdate {
        void accept(WorldSection section);
    }

    private final Long2ByteOpenHashMap[] slices = new Long2ByteOpenHashMap[SLICES];
    private final StampedLock[] locks = new StampedLock[SLICES];
    {
        for (int i = 0; i < this.slices.length; i++) {
            this.slices[i] = new Long2ByteOpenHashMap();
            this.locks[i] = new StampedLock();
        }
    }

    private LongConsumer initialRenderMeshGen;
    private LongConsumer renderMeshGen;
    private IChildUpdate childUpdateCallback;

    /**
     * Wires up the dispatch callbacks. May only be called once; subsequent calls
     * throw to surface the configuration error.
     */
    public void setCallbacks(LongConsumer initialRenderMeshGen,
                              LongConsumer renderMeshGen,
                              IChildUpdate childUpdateCallback) {
        if (this.renderMeshGen != null) {
            throw new IllegalStateException("SectionUpdateRouter.setCallbacks called twice");
        }
        this.initialRenderMeshGen = initialRenderMeshGen;
        this.renderMeshGen = renderMeshGen;
        this.childUpdateCallback = childUpdateCallback;
    }

    @Override
    public boolean watch(int lvl, int x, int y, int z, int types) {
        return watch(WorldEngine.getWorldSectionId(lvl, x, y, z), types);
    }

    @Override
    public boolean watch(long position, int types) {
        int idx = sliceIndex(position);
        var set = this.slices[idx];
        var lock = this.locks[idx];
        byte delta;
        long stamp = lock.readLock();
        try {
            byte current = set.getOrDefault(position, (byte) 0);
            byte newBits = (byte) types;
            // 'delta' is the subset of types not already set.
            delta = (byte) ((current & newBits) ^ newBits);
            if (delta != 0) {
                long ws = lock.tryConvertToWriteLock(stamp);
                if (ws == 0L) {
                    // Could not upgrade in place; release read, acquire write, re-check.
                    lock.unlockRead(stamp);
                    stamp = lock.writeLock();
                    current = set.getOrDefault(position, (byte) 0);
                    delta = (byte) ((current & newBits) ^ newBits);
                    if (delta != 0) {
                        set.put(position, (byte) (current | newBits));
                    }
                } else {
                    stamp = ws;
                    set.put(position, (byte) (current | newBits));
                }
            }
        } finally {
            lock.unlock(stamp);
        }
        if (((delta & types) & UPDATE_TYPE_BLOCK_BIT) != 0) {
            this.initialRenderMeshGen.accept(position);
        }
        return delta != 0;
    }

    @Override
    public boolean unwatch(int lvl, int x, int y, int z, int types) {
        return unwatch(WorldEngine.getWorldSectionId(lvl, x, y, z), types);
    }

    @Override
    public boolean unwatch(long position, int types) {
        int idx = sliceIndex(position);
        var set = this.slices[idx];
        var lock = this.locks[idx];

        long stamp = lock.readLock();
        boolean removed = false;
        try {
            byte current = set.getOrDefault(position, (byte) 0);
            if (current == 0) {
                throw new IllegalStateException("Section not watched: " + WorldEngine.pprintPos(position));
            }
            if ((current & types) != 0) {
                long ws = lock.tryConvertToWriteLock(stamp);
                if (ws == 0L) {
                    lock.unlockRead(stamp);
                    stamp = lock.writeLock();
                    current = set.getOrDefault(position, (byte) 0);
                    if (current == 0) {
                        throw new IllegalStateException("Section not watched: " + WorldEngine.pprintPos(position));
                    }
                } else {
                    stamp = ws;
                }
                if ((current & types) != 0) {
                    current &= (byte) ~types;
                    if (current == 0) {
                        set.remove(position);
                        removed = true;
                    } else {
                        set.put(position, current);
                    }
                }
            }
        } finally {
            lock.unlock(stamp);
        }
        return removed;
    }

    @Override
    public int get(long position) {
        int idx = sliceIndex(position);
        var set = this.slices[idx];
        var lock = this.locks[idx];
        long stamp = lock.readLock();
        try {
            return set.getOrDefault(position, (byte) 0);
        } finally {
            lock.unlockRead(stamp);
        }
    }

    /**
     * Forwards a world-engine event to the registered callbacks for any watch type
     * the subscriber registered. Quiet no-op when the section isn't watched for
     * any of the supplied types.
     */
    public void forwardEvent(WorldSection section, int type) {
        long position = section.key;
        int idx = sliceIndex(position);
        var set = this.slices[idx];
        var lock = this.locks[idx];

        byte types;
        long stamp = lock.readLock();
        try {
            types = (byte) (set.getOrDefault(position, (byte) 0) & type);
        } finally {
            lock.unlockRead(stamp);
        }
        if (types == 0) return;
        if ((types & UPDATE_TYPE_CHILD_EXISTENCE_BIT) != 0) {
            this.childUpdateCallback.accept(section);
        }
        if ((types & UPDATE_TYPE_BLOCK_BIT) != 0) {
            this.renderMeshGen.accept(position);
        }
    }

    /**
     * External hook to force a remesh on a watched section (e.g. when an
     * out-of-band data path mutated its content).
     */
    public void triggerRemesh(long position) {
        int idx = sliceIndex(position);
        var set = this.slices[idx];
        var lock = this.locks[idx];
        byte types;
        long stamp = lock.readLock();
        try {
            types = set.getOrDefault(position, (byte) 0);
        } finally {
            lock.unlockRead(stamp);
        }
        if ((types & UPDATE_TYPE_BLOCK_BIT) != 0) {
            this.renderMeshGen.accept(position);
        }
    }

    /** Stafford-13 mix mod {@link #SLICES}. */
    private static int sliceIndex(long value) {
        value = (value ^ (value >>> 30)) * -4658895280553007687L;
        value = (value ^ (value >>> 27)) * -7723592293110705685L;
        return (int) ((value ^ (value >>> 31)) & SLICES_MASK);
    }
}

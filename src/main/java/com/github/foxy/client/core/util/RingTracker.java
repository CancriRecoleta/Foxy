package com.github.foxy.client.core.util;

import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;

/**
 * Tracks a circular ring of (x, z) cells around a moving centre and emits
 * load / unload events as the centre slides.
 *
 * <h2>Operation queue</h2>
 * <p>{@link #operations} is a sparse map from packed {@code (x, z)} coordinate to a
 * signed pending-action count. {@code +1} means "load", {@code -1} means "unload".
 * Symmetric movements (load then unload at the same cell within the same window)
 * cancel out as the counters add to zero, so {@link #process} can drop the entry
 * without ever calling the consumer.</p>
 *
 * <h2>Centre movement strategy</h2>
 * <ul>
 *   <li>{@link #moveCenter(int, int)} dispatches to a fast path for unit deltas (the
 *       common case when chunks-per-second is bounded by the player's walk speed)
 *       and a general path for larger jumps.</li>
 *   <li>If the new centre is more than {@code radius + 1} away on either axis, the
 *       old ring has no cells in common with the new one, so we full-unload and
 *       full-load instead of computing the per-axis deltas.</li>
 *   <li>Per-axis movement uses {@link #boundDist}, a precomputed half-circle bounding
 *       distance for each row/column index, so the per-step work is O(2 * radius).</li>
 * </ul>
 *
 * <h2>Cleanroom note</h2>
 * <p>Same algorithm as upstream Voxy. The cleanroom rewrite drops the legacy
 * {@code main()} demo, removes the unused {@code Logger} / {@code Random} imports,
 * narrows the per-axis movement helpers' visibility, and adds full English javadoc.</p>
 */
public class RingTracker {

    private final Long2ByteOpenHashMap operations = new Long2ByteOpenHashMap(1 << 13);
    private final int[] boundDist;
    private final int radius;
    private int centerX;
    private int centerZ;

    public RingTracker(int radius, int centerX, int centerZ, boolean fill) {
        this(null, radius, centerX, centerZ, fill);
    }

    /**
     * @param stealFrom optional source to inherit pending operations from (used by
     *                  {@code RenderDistanceTracker.setRenderDistance} so cells already
     *                  queued for load aren't requeued under the new ring)
     * @param radius    ring radius in cells
     * @param centerX   initial centre x
     * @param centerZ   initial centre z
     * @param fill      whether to seed the ring with load events for every cell
     */
    public RingTracker(RingTracker stealFrom, int radius, int centerX, int centerZ, boolean fill) {
        this.centerX = centerX;
        this.centerZ = centerZ;
        this.radius = radius;
        this.boundDist = generateBoundingHalfCircleDistance(radius);
        if (stealFrom != null) {
            this.operations.putAll(stealFrom.operations);
            stealFrom.operations.clear();
        }
        if (fill) fillRing(true);
    }

    /** Packs (x, z) into a single 64-bit key. */
    private static long pack(int x, int z) {
        return Integer.toUnsignedLong(x) | (Integer.toUnsignedLong(z) << 32);
    }

    private void fillRing(boolean load) {
        for (int i = 0; i <= this.radius * 2; i++) {
            int x = this.centerX + i - this.radius;
            int d = this.boundDist[i];
            for (int z = this.centerZ - d; z <= this.centerZ + d; z++) {
                int res = this.operations.addTo(pack(x, z), (byte) (load ? 1 : -1));
                if ((load && 0 < res) || (!load && res < 0)) {
                    throw new IllegalStateException("RingTracker fillRing inconsistency at "
                            + x + "," + z + " res=" + res);
                }
            }
        }
    }

    /** Marks every current cell for unload. */
    public void unload() {
        fillRing(false);
    }

    /**
     * Slides the ring's centre to {@code (x, z)}. When the jump exceeds the ring's
     * span the old ring is fully unloaded and a fresh one loaded; otherwise the
     * per-axis fast paths run.
     */
    public void moveCenter(int x, int z) {
        if (this.radius + 1 < Math.abs(x - this.centerX) || this.radius + 1 < Math.abs(z - this.centerZ)) {
            fillRing(false);
            this.centerX = x;
            this.centerZ = z;
            fillRing(true);
            return;
        }
        if (x != this.centerX) moveX(x - this.centerX);
        if (z != this.centerZ) moveZ(z - this.centerZ);
    }

    private void moveZ(int delta) {
        if (delta == 0) return;
        if (delta == 1 || delta == -1) {
            for (int i = 0; i <= this.radius * 2; i++) {
                int x = this.centerX + i - this.radius;
                int d = this.boundDist[i] * delta;
                int loadZ = this.centerZ + d + delta;
                int unloadZ = this.centerZ - d;
                if (0 < this.operations.addTo(pack(x, loadZ), (byte) 1)) {
                    throw new IllegalStateException("RingTracker.moveZ load conflict at " + x + "," + loadZ);
                }
                if (this.operations.addTo(pack(x, unloadZ), (byte) -1) < 0) {
                    throw new IllegalStateException("RingTracker.moveZ unload conflict at " + x + "," + unloadZ);
                }
            }
            this.centerZ += delta;
            return;
        }
        // General case: |delta| > 1.
        int sign = Integer.signum(delta);
        for (int i = 0; i <= this.radius * 2; i++) {
            int x = this.centerX + i - this.radius;
            int d = this.boundDist[i] * sign;
            int loadAnchor = this.centerZ + d;
            for (int z = loadAnchor + (sign < 0 ? delta : 1); z <= loadAnchor + (sign < 0 ? -1 : delta); z++) {
                if (0 < this.operations.addTo(pack(x, z), (byte) 1)) {
                    throw new IllegalStateException("RingTracker.moveZ load conflict at " + x + "," + z);
                }
            }
            int unloadAnchor = this.centerZ - d;
            for (int z = unloadAnchor + (sign < 0 ? (delta + 1) : 0); z < unloadAnchor + (sign < 0 ? 1 : delta); z++) {
                if (this.operations.addTo(pack(x, z), (byte) -1) < 0) {
                    throw new IllegalStateException("RingTracker.moveZ unload conflict at " + x + "," + z);
                }
            }
        }
        this.centerZ += delta;
    }

    private void moveX(int delta) {
        if (delta == 0) return;
        if (delta == 1 || delta == -1) {
            for (int i = 0; i <= this.radius * 2; i++) {
                int z = this.centerZ + i - this.radius;
                int d = this.boundDist[i] * delta;
                int loadX = this.centerX + d + delta;
                int unloadX = this.centerX - d;
                if (0 < this.operations.addTo(pack(loadX, z), (byte) 1)) {
                    throw new IllegalStateException("RingTracker.moveX load conflict at " + loadX + "," + z);
                }
                if (this.operations.addTo(pack(unloadX, z), (byte) -1) < 0) {
                    throw new IllegalStateException("RingTracker.moveX unload conflict at " + unloadX + "," + z);
                }
            }
            this.centerX += delta;
            return;
        }
        int sign = Integer.signum(delta);
        for (int i = 0; i <= this.radius * 2; i++) {
            int z = this.centerZ + i - this.radius;
            int d = this.boundDist[i] * sign;
            int loadAnchor = this.centerX + d;
            for (int x = loadAnchor + (sign < 0 ? delta : 1); x <= loadAnchor + (sign < 0 ? -1 : delta); x++) {
                if (0 < this.operations.addTo(pack(x, z), (byte) 1)) {
                    throw new IllegalStateException("RingTracker.moveX load conflict at " + x + "," + z);
                }
            }
            int unloadAnchor = this.centerX - d;
            for (int x = unloadAnchor + (sign < 0 ? (delta + 1) : 0); x < unloadAnchor + (sign < 0 ? 1 : delta); x++) {
                if (this.operations.addTo(pack(x, z), (byte) -1) < 0) {
                    throw new IllegalStateException("RingTracker.moveX unload conflict at " + x + "," + z);
                }
            }
        }
        this.centerX += delta;
    }

    /** Per-event consumer: invoked once per (x, z) cell when {@link #process} drains. */
    public interface IUpdateConsumer {
        void accept(int x, int z);
    }

    /**
     * Drains up to {@code n} pending operations, dispatching to {@code onAdd} or
     * {@code onRemove}. Cells whose net pending count is zero (added and removed
     * within the same window) are silently dropped.
     *
     * @return number of consumer-side dispatches that actually fired
     */
    public int process(int n, IUpdateConsumer onAdd, IUpdateConsumer onRemove) {
        if (this.operations.isEmpty()) return 0;
        var iter = this.operations.long2ByteEntrySet().fastIterator();
        int dispatched = 0;
        while (iter.hasNext() && n > 0) {
            var entry = iter.next();
            byte op = entry.getByteValue();
            if (op == 0) {
                iter.remove();
                continue;
            }
            n--;
            if (op != 1 && op != -1) {
                throw new IllegalStateException("RingTracker pending op outside {-1, +1}: " + op);
            }
            long packed = entry.getLongKey();
            int x = (int) (packed & 0xFFFFFFFFL);
            int z = (int) ((packed >>> 32) & 0xFFFFFFFFL);
            if (op == 1) onAdd.accept(x, z);
            else onRemove.accept(x, z);
            iter.remove();
            dispatched++;
        }
        return dispatched;
    }

    /** Precomputes the bounding half-circle distance for each row index in {@code [-r, +r]}. */
    private static int[] generateBoundingHalfCircleDistance(int radius) {
        int[] out = new int[radius * 2 + 1];
        for (int i = -radius; i <= radius; i++) {
            out[i + radius] = (int) Math.sqrt(radius * radius - i * i);
        }
        return out;
    }
}

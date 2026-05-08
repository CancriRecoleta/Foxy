package com.github.foxy.client.core.rendering;

import com.github.foxy.client.core.util.RingTracker;
import com.github.foxy.common.world.WorldEngine;

import java.util.function.LongConsumer;

/**
 * Maintains the set of LOD-4 top-level nodes the renderer cares about, based on
 * distance from the camera.
 *
 * <h2>Centring policy</h2>
 * <p>The tracker only re-centres its underlying {@link RingTracker} after the
 * camera has drifted at least {@link #CHECK_DISTANCE_BLOCKS} blocks from the last
 * recorded centre. This avoids re-walking the ring every frame for tiny camera
 * jitters and lets the move-rate be dominated by per-frame
 * {@link RingTracker#process} calls instead.</p>
 *
 * <h2>Coord conversion</h2>
 * <p>Camera coords are in blocks; the ring works in LOD-4 sections (each
 * 32 &times; 2<sup>4</sup> = 512 blocks per axis), so {@code >> 9} converts.</p>
 *
 * <h2>Y range</h2>
 * <p>The tracker emits add / remove for every section between {@code minSec}
 * (inclusive) and {@code maxSec} (inclusive) on each (x, z) cell, since LOD-4
 * data spans the full vertical world range and is keyed only by horizontal
 * distance.</p>
 *
 * <p>Cleanroom note: same algorithm as upstream Voxy with English javadoc and a
 * named {@link #CHECK_DISTANCE_BLOCKS} constant explaining the centre-update
 * heuristic.</p>
 */
public class RenderDistanceTracker {

    /** Distance the camera must move before the ring is re-centred. */
    private static final int CHECK_DISTANCE_BLOCKS = 128;

    /** Bit shift: blocks → LOD-4 section coords (32-block sections × 2^4 LOD = 512). */
    private static final int BLOCK_TO_LVL4_SHIFT = 9;

    private final LongConsumer addTopLevelNode;
    private final LongConsumer removeTopLevelNode;
    private final int processRate;
    private final int minSec;
    private final int maxSec;

    private RingTracker tracker;
    private int renderDistance;
    private double posX;
    private double posZ;

    /**
     * @param rate              max number of add/remove events processed per
     *                          {@link #setCenterAndProcess} call
     * @param minSec            inclusive minimum LOD-4 Y section
     * @param maxSec            inclusive maximum LOD-4 Y section
     * @param addTopLevelNode   invoked once per LOD-4 section that enters the ring
     * @param removeTopLevelNode invoked once per LOD-4 section that leaves the ring
     */
    public RenderDistanceTracker(int rate, int minSec, int maxSec,
                                  LongConsumer addTopLevelNode,
                                  LongConsumer removeTopLevelNode) {
        this.addTopLevelNode = addTopLevelNode;
        this.removeTopLevelNode = removeTopLevelNode;
        this.renderDistance = 2;
        this.tracker = new RingTracker(this.renderDistance, 0, 0, true);
        this.processRate = rate;
        this.minSec = minSec;
        this.maxSec = maxSec;
    }

    /**
     * Updates the ring radius. Existing cells are marked for unload; the new ring is
     * built around the current camera position so cells that overlap the old and new
     * extents are kept rather than removed-and-readded.
     */
    public void setRenderDistance(int renderDistance) {
        if (renderDistance == this.renderDistance) return;
        this.renderDistance = renderDistance;
        this.tracker.unload();
        int cx = ((int) this.posX) >> BLOCK_TO_LVL4_SHIFT;
        int cz = ((int) this.posZ) >> BLOCK_TO_LVL4_SHIFT;
        this.tracker = new RingTracker(this.tracker, renderDistance, cx, cz, true);
    }

    /**
     * Tells the tracker the camera is now at {@code (x, z)} (block coords). When
     * the camera has drifted &ge; {@link #CHECK_DISTANCE_BLOCKS} from the last
     * centre, re-centres the ring; then drains up to {@link #processRate}
     * add/remove events.
     *
     * @return {@code true} when at least one event was dispatched
     */
    public boolean setCenterAndProcess(double x, double z) {
        double dx = this.posX - x;
        double dz = this.posZ - z;
        if (CHECK_DISTANCE_BLOCKS * CHECK_DISTANCE_BLOCKS < dx * dx + dz * dz) {
            this.posX = x;
            this.posZ = z;
            this.tracker.moveCenter(((int) x) >> BLOCK_TO_LVL4_SHIFT, ((int) z) >> BLOCK_TO_LVL4_SHIFT);
        }
        return this.tracker.process(this.processRate, this::add, this::rem) != 0;
    }

    private void add(int x, int z) {
        for (int y = this.minSec; y <= this.maxSec; y++) {
            this.addTopLevelNode.accept(WorldEngine.getWorldSectionId(4, x, y, z));
        }
    }

    private void rem(int x, int z) {
        for (int y = this.minSec; y <= this.maxSec; y++) {
            this.removeTopLevelNode.accept(WorldEngine.getWorldSectionId(4, x, y, z));
        }
    }
}

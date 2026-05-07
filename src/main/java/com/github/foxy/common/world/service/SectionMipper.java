package com.github.foxy.common.world.service;

import com.github.foxy.common.world.WorldEngine;
import com.github.foxy.common.world.WorldSection;
import com.github.foxy.common.world.other.Mapper;
import com.github.foxy.common.world.other.Mipper;

import java.util.Arrays;

/**
 * Synchronous primitive that folds eight child {@link WorldSection}s at LOD {@code N-1}
 * into the corresponding octants of one parent section at LOD {@code N}.
 *
 * <h2>Math</h2>
 * <p>A parent section at LOD {@code N} covers a world-space cube of side
 * {@code 32 * 2^N} blocks; the eight children at LOD {@code N-1} tile that cube as a
 * {@code 2x2x2} grid. In voxel-resolution terms each child has {@code 32^3} voxels at
 * the child's resolution, contributing {@code 16^3} parent voxels (one parent voxel
 * for every {@code 2^3} cube of child voxels).</p>
 *
 * <p>For child octant {@code (ox, oy, oz)} (each in {@code [0, 2)}) the parent voxel at
 * local coords {@code (lx, ly, lz)} (each in {@code [0, 16)}) is the {@link Mipper#mip
 * Mipper.mip} fold of child voxels at coords {@code (2*lx + dx, 2*ly + dy, 2*lz + dz)}
 * for {@code dx, dy, dz} in {@code [0, 2)}. The destination index in the parent's 32&sup3;
 * array is {@code ((16*oy + ly) << 10) | ((16*oz + lz) << 5) | (16*ox + lx)}.</p>
 *
 * <h2>Sparse worlds</h2>
 * <p>If a child section doesn't exist (sparse imports, unloaded LOD-0 regions), the
 * corresponding parent octant is filled with air {@link Mapper#airWithLight 0-light air}
 * voxels &mdash; the same value the importer falls back to on a cache-miss section.</p>
 *
 * <h2>Concurrency</h2>
 * <p>This class doesn't acquire its own locks; reference counting on
 * {@link WorldSection} provides the necessary serialisation. Callers are responsible for
 * ensuring that no other writer is mutating any of the eight children while a mip pass
 * is reading them &mdash; in practice this means running mipping after the importer's
 * own per-chunk write has released its WorldSection ref.</p>
 */
public final class SectionMipper {
    private SectionMipper() {}

    /** Stride in the WorldSection 32&sup3; index when stepping +1 along x. */
    private static final int CHILD_STRIDE_X = 1;
    /** Stride in the WorldSection 32&sup3; index when stepping +1 along z. */
    private static final int CHILD_STRIDE_Z = 1 << 5;
    /** Stride in the WorldSection 32&sup3; index when stepping +1 along y. */
    private static final int CHILD_STRIDE_Y = 1 << 10;

    /**
     * Rebuilds the parent section at {@code (parentLvl, parentX, parentY, parentZ)}
     * from its eight children at LOD {@code parentLvl - 1}.
     *
     * <p>Acquires the parent (creating it if necessary) and each existing child via the
     * engine, performs the fold, marks the parent dirty and persists it synchronously,
     * and releases all references. {@code parentLvl} must be in {@code [1, MAX_LOD_LAYER]}.</p>
     *
     * @return {@code true} when the mip wrote a non-empty parent (i.e. at least one
     *         child contributed any non-air voxel), {@code false} when the parent ended
     *         up entirely air.
     */
    public static boolean mipOne(WorldEngine engine, int parentLvl, int parentX, int parentY, int parentZ) {
        if (parentLvl < 1 || parentLvl > WorldEngine.MAX_LOD_LAYER) {
            throw new IllegalArgumentException("parentLvl must be in [1, MAX_LOD_LAYER], got " + parentLvl);
        }
        WorldSection parent = engine.acquire(parentLvl, parentX, parentY, parentZ);
        try {
            long[] dst = parent._unsafeGetRawDataArray();
            // Start from a clean slate; missing children leave their octants as air.
            Arrays.fill(dst, Mapper.AIR);

            int childLvl = parentLvl - 1;
            int totalNonAir = 0;
            byte childMask = 0;

            for (int oy = 0; oy < 2; oy++) {
                for (int oz = 0; oz < 2; oz++) {
                    for (int ox = 0; ox < 2; ox++) {
                        int cx = (parentX << 1) | ox;
                        int cy = (parentY << 1) | oy;
                        int cz = (parentZ << 1) | oz;
                        WorldSection child = engine.acquireIfExists(childLvl, cx, cy, cz);
                        if (child == null) continue;
                        try {
                            int delta = foldChildIntoOctant(child, dst, ox, oy, oz, engine.getMapper());
                            totalNonAir += delta;
                            if (delta > 0) {
                                int childIdx = WorldSection.getChildIndex(cx, cy, cz);
                                childMask |= (byte) (1 << childIdx);
                            }
                        } finally {
                            child.release();
                        }
                    }
                }
            }

            parent._unsafeSetNonEmptyChildren(childMask);
            // The parent section's nonEmptyBlockCount is only meaningful at LOD 0 by
            // upstream convention, so we don't try to maintain it here.
            engine.markDirty(parent);
            engine.saveSection(parent);
            return totalNonAir > 0;
        } finally {
            parent.release();
        }
    }

    /**
     * Folds one child section (32&sup3;) into the {@code (ox, oy, oz)} octant of the
     * parent's 32&sup3; array. Returns the number of non-air parent voxels written.
     */
    private static int foldChildIntoOctant(WorldSection child, long[] parentData,
                                           int ox, int oy, int oz, Mapper mapper) {
        long[] src = child._unsafeGetRawDataArray();
        int parentBaseX = ox << 4;
        int parentBaseY = oy << 4;
        int parentBaseZ = oz << 4;
        int nonAir = 0;

        for (int ly = 0; ly < 16; ly++) {
            int parentY = parentBaseY + ly;
            int parentRowBase = parentY << 10;
            int childRowBase = (ly << 1) << 10;
            for (int lz = 0; lz < 16; lz++) {
                int parentZ = parentBaseZ + lz;
                int parentZBase = parentRowBase | (parentZ << 5);
                int childZBase = childRowBase | ((lz << 1) << 5);
                for (int lx = 0; lx < 16; lx++) {
                    int parentX = parentBaseX + lx;
                    int parentIdx = parentZBase | parentX;
                    int childIdxBase = childZBase | (lx << 1);

                    long I000 = src[childIdxBase];
                    long I100 = src[childIdxBase + CHILD_STRIDE_X];
                    long I001 = src[childIdxBase + CHILD_STRIDE_Z];
                    long I101 = src[childIdxBase + CHILD_STRIDE_X + CHILD_STRIDE_Z];
                    long I010 = src[childIdxBase + CHILD_STRIDE_Y];
                    long I110 = src[childIdxBase + CHILD_STRIDE_Y + CHILD_STRIDE_X];
                    long I011 = src[childIdxBase + CHILD_STRIDE_Y + CHILD_STRIDE_Z];
                    long I111 = src[childIdxBase + CHILD_STRIDE_Y + CHILD_STRIDE_X + CHILD_STRIDE_Z];

                    long folded = Mipper.mip(I000, I100, I001, I101, I010, I110, I011, I111, mapper);
                    parentData[parentIdx] = folded;
                    if (!Mapper.isAir(folded)) nonAir++;
                }
            }
        }
        return nonAir;
    }
}

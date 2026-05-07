package com.github.foxy.common.voxelization;

import com.github.foxy.common.world.WorldSection;
import com.github.foxy.common.world.other.Mapper;

/**
 * 16&sup3; &rarr; 32&sup3; bridge: assembles up to eight {@link VoxelizedSection} buffers
 * into one LOD-0 {@link WorldSection}.
 *
 * <p>{@link VoxelizedSection} is the voxelizer's output and covers a single vanilla
 * chunk-section (16&sup3; blocks); {@link WorldSection} is the engine's storage unit and
 * covers 32&sup3; LOD-0 voxels &mdash; exactly a 2&times;2&times;2 cube of chunk-sections.
 * The packer writes directly into the destination {@code WorldSection}'s raw array so it
 * can be reused across many calls inside an importer's main loop without allocating.</p>
 *
 * <p><b>Octant convention:</b> {@code octantX/Y/Z} are in {@code [0, 2)}, selecting the
 * {@code [0,16)} or {@code [16,32)} sub-range on the corresponding axis.</p>
 */
public final class SectionDataPacker {
    private SectionDataPacker() {}

    /**
     * Writes {@code src} (a 16&sup3; LOD-0 slice) into the {@code (octantX, octantY, octantZ)}
     * octant of {@code dst}. Also bumps {@code dst.addNonEmptyBlockCount} by the count of
     * non-air voxels copied in.
     *
     * <p>Caller is responsible for having acquired {@code dst} and ensuring its
     * {@code lvl == 0}; both are unchecked on the hot path and held by the importer
     * loop's own invariants.</p>
     */
    public static void packLvl0Octant(VoxelizedSection src,
                                      WorldSection dst,
                                      int octantX, int octantY, int octantZ) {
        if (WorldSection.VERIFY_WORLD_SECTION_EXECUTION) {
            if ((octantX | octantY | octantZ) < 0 || octantX > 1 || octantY > 1 || octantZ > 1) {
                throw new IllegalArgumentException("Octant coords must be in [0,2): "
                        + octantX + ", " + octantY + ", " + octantZ);
            }
            if (dst.lvl != 0) {
                throw new IllegalStateException("packLvl0Octant only valid on lvl 0 (got " + dst.lvl + ")");
            }
        }
        long[] dstData = dst._unsafeGetRawDataArray();
        long[] srcData = src.section;
        int baseX = octantX << 4;
        int baseY = octantY << 4;
        int baseZ = octantZ << 4;

        int delta = 0;
        // VoxelizedSection LOD-0 index: (y << 8) | (z << 4) | x; WorldSection index:
        // (y << 10) | (z << 5) | x. Both are y-major but their strides differ, so the
        // copy needs an explicit triple loop rather than a single arraycopy.
        for (int y = 0; y < 16; y++) {
            int dstYBase = (baseY + y) << 10;
            int srcYBase = y << 8;
            for (int z = 0; z < 16; z++) {
                int dstZBase = dstYBase | ((baseZ + z) << 5);
                int srcZBase = srcYBase | (z << 4);
                for (int x = 0; x < 16; x++) {
                    long voxel = srcData[srcZBase | x];
                    dstData[dstZBase | (baseX + x)] = voxel;
                    if (!Mapper.isAir(voxel)) delta++;
                }
            }
        }
        dst.addNonEmptyBlockCount(delta);
    }

    /**
     * Inverse of {@link #packLvl0Octant}: extracts a 16&sup3; octant out of a
     * {@link WorldSection} into a freshly-zeroed {@link VoxelizedSection}'s LOD-0 slice
     * and updates {@code dst.lvl0NonAirCount}.
     *
     * <p>Useful for the &quot;read a 32&sup3; section back from storage and re-mip a
     * sub-range&quot; flow. Only the LOD-0 slice is populated; callers that want the
     * mip pyramid must run {@link WorldVoxilizedSectionMipper} themselves.</p>
     */
    public static void extractLvl0Octant(WorldSection src,
                                         int octantX, int octantY, int octantZ,
                                         VoxelizedSection dst) {
        if (WorldSection.VERIFY_WORLD_SECTION_EXECUTION) {
            if ((octantX | octantY | octantZ) < 0 || octantX > 1 || octantY > 1 || octantZ > 1) {
                throw new IllegalArgumentException("Octant coords must be in [0,2)");
            }
        }
        long[] srcData = src._unsafeGetRawDataArray();
        long[] dstData = dst.section;
        int baseX = octantX << 4;
        int baseY = octantY << 4;
        int baseZ = octantZ << 4;

        int nonAir = 0;
        for (int y = 0; y < 16; y++) {
            int srcYBase = (baseY + y) << 10;
            int dstYBase = y << 8;
            for (int z = 0; z < 16; z++) {
                int srcZBase = srcYBase | ((baseZ + z) << 5);
                int dstZBase = dstYBase | (z << 4);
                for (int x = 0; x < 16; x++) {
                    long voxel = srcData[srcZBase | (baseX + x)];
                    dstData[dstZBase | x] = voxel;
                    if (!Mapper.isAir(voxel)) nonAir++;
                }
            }
        }
        dst.lvl0NonAirCount = nonAir;
    }
}

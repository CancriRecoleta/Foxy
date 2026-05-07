package com.github.foxy.common.voxelization;

import com.github.foxy.common.world.other.Mapper;
import com.github.foxy.common.world.other.Mipper;

/**
 * Builds the LOD-1..LOD-4 mip pyramid of a {@link VoxelizedSection} from its level-0
 * voxels by repeated 8&rarr;1 folding through {@link Mipper#mip}.
 *
 * <p>Layout assumed: see {@link VoxelizedSection}'s class javadoc. The four loops below
 * each consume the previous level and write the next; coordinates use the local index
 * helpers {@link #G}/{@link #H}/{@link #I}/{@link #J} so the reads stay row-major and
 * cache-friendly.</p>
 */
public final class WorldVoxilizedSectionMipper {
    private WorldVoxilizedSectionMipper() {}

    /** Index of voxel (x,y,z) in the level-0 16^3 slice. */
    private static int G(int x, int y, int z) { return (y << 8) | (z << 4) | x; }

    /** Index of voxel (x,y,z) in the level-1 8^3 slice. */
    private static int H(int x, int y, int z) { return ((y << 6) | (z << 3) | x) + 16 * 16 * 16; }

    /** Index of voxel (x,y,z) in the level-2 4^3 slice. */
    private static int I(int x, int y, int z) { return ((y << 4) | (z << 2) | x) + 8 * 8 * 8 + 16 * 16 * 16; }

    /** Index of voxel (x,y,z) in the level-3 2^3 slice. */
    private static int J(int x, int y, int z) { return ((y << 2) | (z << 1) | x) + 4 * 4 * 4 + 8 * 8 * 8 + 16 * 16 * 16; }

    /**
     * Computes levels 1..4 of {@code section} from level 0. Caller is responsible for
     * having populated the level-0 slice (e.g. via {@link WorldConversionFactory}).
     */
    public static void mipSection(VoxelizedSection section, Mapper mapper) {
        var data = section.section;

        // Level 1: stride through the 16^3 slice in 2^3 cubes. The bit-twiddle on q walks
        // it without nested loops by keeping x, y, z in disjoint bit positions.
        int outIdx = 0;
        final int MSK = 0b1110_1110_1110;
        final int iMSK1 = (~MSK) + 1;
        int q = 0;
        while (true) {
            data[16 * 16 * 16 + outIdx++] = Mipper.mip(
                    data[q | G(0, 0, 0)], data[q | G(1, 0, 0)], data[q | G(0, 0, 1)], data[q | G(1, 0, 1)],
                    data[q | G(0, 1, 0)], data[q | G(1, 1, 0)], data[q | G(0, 1, 1)], data[q | G(1, 1, 1)],
                    mapper);
            if (q == MSK) break;
            q = (q + iMSK1) & MSK;
        }

        // Levels 2..3: small enough to use plain triple loops.
        outIdx = 0;
        for (int y = 0; y < 8; y += 2)
            for (int z = 0; z < 8; z += 2)
                for (int x = 0; x < 8; x += 2) {
                    data[16 * 16 * 16 + 8 * 8 * 8 + outIdx++] = Mipper.mip(
                            data[H(x, y, z)],         data[H(x + 1, y, z)],         data[H(x, y, z + 1)],         data[H(x + 1, y, z + 1)],
                            data[H(x, y + 1, z)],     data[H(x + 1, y + 1, z)],     data[H(x, y + 1, z + 1)],     data[H(x + 1, y + 1, z + 1)],
                            mapper);
                }

        outIdx = 0;
        for (int y = 0; y < 4; y += 2)
            for (int z = 0; z < 4; z += 2)
                for (int x = 0; x < 4; x += 2) {
                    data[16 * 16 * 16 + 8 * 8 * 8 + 4 * 4 * 4 + outIdx++] = Mipper.mip(
                            data[I(x, y, z)],         data[I(x + 1, y, z)],         data[I(x, y, z + 1)],         data[I(x + 1, y, z + 1)],
                            data[I(x, y + 1, z)],     data[I(x + 1, y + 1, z)],     data[I(x, y + 1, z + 1)],     data[I(x + 1, y + 1, z + 1)],
                            mapper);
                }

        // Level 4: a single fold of the eight level-3 voxels.
        data[16 * 16 * 16 + 8 * 8 * 8 + 4 * 4 * 4 + 2 * 2 * 2] = Mipper.mip(
                data[J(0, 0, 0)], data[J(1, 0, 0)], data[J(0, 0, 1)], data[J(1, 0, 1)],
                data[J(0, 1, 0)], data[J(1, 1, 0)], data[J(0, 1, 1)], data[J(1, 1, 1)],
                mapper);
    }
}

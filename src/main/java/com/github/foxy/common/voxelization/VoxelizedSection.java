package com.github.foxy.common.voxelization;

import java.util.Arrays;

/**
 * Packed voxel data for a single 16&times;16&times;16 chunk section, plus its precomputed
 * mip pyramid down to a single 1&times;1&times;1 sample.
 *
 * <h2>Layout of {@link #section}</h2>
 * The backing array is a single {@code long[]} laid out level-after-level so renderer code
 * can compute level-base offsets with a few shifts:
 * <pre>
 *   index range                        | level | resolution     | element count
 *   -----------------------------------+-------+----------------+---------------
 *   [0, 4096)                          |   0   | 16x16x16       |   4096
 *   [4096, 4608)                       |   1   |  8x 8x 8       |    512
 *   [4608, 4672)                       |   2   |  4x 4x 4       |     64
 *   [4672, 4680)                       |   3   |  2x 2x 2       |      8
 *   [4680, 4681)                       |   4   |  1x 1x 1       |      1
 * </pre>
 *
 * <p>Each element is a packed mapping id produced by
 * {@link com.github.foxy.common.world.other.Mapper#composeMappingId(byte, int, int) Mapper.composeMappingId}.</p>
 *
 * <p>The mip pyramid is populated by
 * {@link WorldVoxilizedSectionMipper#mipSection(VoxelizedSection, com.github.foxy.common.world.other.Mapper)
 * WorldVoxilizedSectionMipper}; level 0 is populated by
 * {@link WorldConversionFactory#convert WorldConversionFactory.convert}.</p>
 *
 * <p>{@link #x}/{@link #y}/{@link #z} are 1-section units (i.e. block coords divided by 16)
 * and identify where this section lives in world space; the conversion factory does not
 * write them.</p>
 */
public final class VoxelizedSection {
    /** Section x in section units (block_x &gt;&gt; 4). */
    public int x;
    /** Section y in section units (block_y &gt;&gt; 4). */
    public int y;
    /** Section z in section units (block_z &gt;&gt; 4). */
    public int z;

    /** Number of non-air level-0 voxels; used by callers to skip empty sections. */
    public int lvl0NonAirCount;

    /** Packed voxel storage; see class javadoc for layout. */
    public final long[] section;

    /** Wraps an already-allocated section buffer (must have the right length). */
    public VoxelizedSection(long[] section) {
        this.section = section;
    }

    /**
     * Returns the index of the first element of {@code lvl}'s slice. Callers can compute
     * a per-voxel index inside that slice and add the result.
     *
     * <p>Constants are pre-OR'd to avoid four conditional adds at runtime.</p>
     */
    public static int getBaseIndexForLevel(int lvl) {
        int offset = 0;
        if (lvl >= 1) offset |= (1 << 12);                     // skip 16^3
        if (lvl >= 2) offset |= (1 <<  9);                     // skip  8^3
        if (lvl >= 3) offset |= (1 <<  6);                     // skip  4^3
        if (lvl >= 4) offset |= (1 <<  3);                     // skip  2^3
        return offset;
    }

    /** Sets the section coordinates and returns {@code this} for chaining. */
    public VoxelizedSection setPosition(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    /**
     * Random-access read of voxel (x, y, z) at LOD {@code lvl}. Coordinates are clamped
     * by mask &mdash; callers must pass values in range {@code [0, 16 &gt;&gt; lvl)}.
     */
    public long get(int lvl, int x, int y, int z) {
        int sizeBits = 4 - lvl;
        int M = (1 << sizeBits) - 1;
        x &= M; y &= M; z &= M;
        int local = (y << (sizeBits << 1)) | (z << sizeBits) | x;
        return this.section[local + getBaseIndexForLevel(lvl)];
    }

    /** Allocates an empty section with the full 4681-long backing array. */
    public static VoxelizedSection createEmpty() {
        return new VoxelizedSection(new long[16 * 16 * 16 + 8 * 8 * 8 + 4 * 4 * 4 + 2 * 2 * 2 + 1]);
    }

    /** Resets the section to all-air; safe to call on a recycled instance. */
    public VoxelizedSection zero() {
        this.lvl0NonAirCount = 0;
        Arrays.fill(this.section, 0L);
        return this;
    }
}

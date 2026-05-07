package com.github.foxy.client.core.model;

/**
 * Read-only bit predicates over a 64-bit per-block-state metadata word.
 *
 * <h2>Layout of the metadata word</h2>
 * <p>The 64 bits are split into 8 byte-sized fields, one per face plus one for
 * whole-block flags:</p>
 * <pre>
 *   bit  0..7    face -X       8..15  face +X
 *   bit 16..23   face -Y      24..31  face +Y
 *   bit 32..39   face -Z      40..47  face +Z
 *   bit 48..55   global flags (occluded / fluid / opaque / ...)
 *   bit 56..63   light emission (0..15) and reserved high bits
 * </pre>
 *
 * <h2>Per-face byte layout (bits 0..7 within each byte)</h2>
 * <pre>
 *   bit 0  face occludes adjacent
 *   bit 1  face is fully opaque (depth-test friendly)
 *   bit 2  face can itself be occluded
 *   bit 3  face uses self-lighting
 *   bit 7  face exists (a face byte with all bits zero except this one means "blank
 *           but present"; a byte with all bits set to 1 means "no face at all")
 * </pre>
 *
 * <p>Cleanroom note: same bit semantics as upstream Voxy. The cleanroom rewrite adds
 * full English javadoc explaining the layout, names the {@code 1L}-returning
 * variants by intent (they're the GLSL-friendly versions that return the literal bit
 * for branchless shader use), and removes the dead {@code //NOTE: this might need
 * to be moved to per face} TODO that no longer matches the renderer's plans.</p>
 */
public abstract class ModelQueries {
    private ModelQueries() {}

    /** Byte offset in the metadata word where the global-flags byte lives. */
    private static final int GLOBAL_FLAGS_BIT_OFFSET = 8 * 6;

    /** {@code true} when the face exists at all (the byte isn't all-1s). */
    public static boolean faceExists(long metadata, int face) {
        return ((metadata >> (8 * face)) & 0xFF) != 0xFF;
    }

    /** {@code true} when the face can itself be occluded by neighbours. */
    public static boolean faceCanBeOccluded(long metadata, int face) {
        return ((metadata >> (8 * face)) & 0b100) == 0b100;
    }

    /** {@code true} when the face occludes the adjacent block's matching face. */
    public static boolean faceOccludes(long metadata, int face) {
        return faceExists(metadata, face) && ((metadata >> (8 * face)) & 0b1) == 0b1;
    }

    /** {@code true} when the face uses its own emissive lighting (e.g. magma blocks). */
    public static boolean faceUsesSelfLighting(long metadata, int face) {
        return ((metadata >> (8 * face)) & 0b1000) != 0;
    }

    // ---- whole-block flags -----------------------------------------------------------

    /** {@code true} when the model renders both sides (e.g. plants / leaves). */
    public static boolean isDoubleSided(long metadata) {
        return ((metadata >> GLOBAL_FLAGS_BIT_OFFSET) & 4) != 0;
    }

    /** Bit form of {@link #isDoubleSided}; returns 0 or 1 for branchless shader use. */
    public static long _isDoubleSided(long metadata) {
        return ((metadata >> (GLOBAL_FLAGS_BIT_OFFSET + 2)) & 1L);
    }

    /** {@code true} when the model has any translucent face. */
    public static boolean isTranslucent(long metadata) {
        return ((metadata >> GLOBAL_FLAGS_BIT_OFFSET) & 2) != 0;
    }

    /** Bit form of {@link #isTranslucent}. */
    public static long _isTranslucent(long metadata) {
        return ((metadata >> (GLOBAL_FLAGS_BIT_OFFSET + 1)) & 1L);
    }

    /** {@code true} when the block contains a fluid (e.g. waterlogged stairs). */
    public static boolean containsFluid(long metadata) {
        return ((metadata >> GLOBAL_FLAGS_BIT_OFFSET) & 8) != 0;
    }

    /** Bit form of {@link #containsFluid}. */
    public static long _containsFluid(long metadata) {
        return ((metadata >> (GLOBAL_FLAGS_BIT_OFFSET + 3)) & 1L);
    }

    /** {@code true} when the block <em>is</em> a fluid (water / lava). */
    public static boolean isFluid(long metadata) {
        return ((metadata >> GLOBAL_FLAGS_BIT_OFFSET) & 16) != 0;
    }

    /** Bit form of {@link #isFluid}. */
    public static long _isFluid(long metadata) {
        return ((metadata >> (GLOBAL_FLAGS_BIT_OFFSET + 4)) & 1L);
    }

    /** {@code true} when the block tints with the biome colour (grass / leaves / water). */
    public static boolean isBiomeColoured(long metadata) {
        return ((metadata >> GLOBAL_FLAGS_BIT_OFFSET) & 1L) != 0;
    }

    /** Bit form of {@link #isBiomeColoured}. */
    public static long _isBiomeColoured(long metadata) {
        return ((metadata >> GLOBAL_FLAGS_BIT_OFFSET) & 1L);
    }

    /** Bit-not form: 1 if not biome-coloured, 0 if biome-coloured. */
    public static long _notIsBiomeColoured(long metadata) {
        return (((~metadata) >> GLOBAL_FLAGS_BIT_OFFSET) & 1L);
    }

    /** {@code true} when the block culls neighbouring same-state faces. */
    public static boolean cullsSame(long metadata) {
        return ((metadata >> GLOBAL_FLAGS_BIT_OFFSET) & 32) != 0;
    }

    /** {@code true} when every face is fully opaque (cheap depth-prepass eligible). */
    public static boolean isFullyOpaque(long metadata) {
        return ((metadata >> GLOBAL_FLAGS_BIT_OFFSET) & 64) != 0;
    }

    /** Bit form of {@link #isFullyOpaque}. */
    public static long _isFullyOpaque(long metadata) {
        return ((metadata >> (GLOBAL_FLAGS_BIT_OFFSET + 6)) & 1L);
    }

    /** Light-emission level in {@code [0, 15]}; non-zero blocks contribute to block light. */
    public static long lightEmission(long metadata) {
        return (metadata >> (GLOBAL_FLAGS_BIT_OFFSET + 7)) & 0xFL;
    }
}

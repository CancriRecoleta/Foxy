package com.github.foxy.common.util;

/**
 * Generic bit gather / scatter and clamp helpers.
 *
 * <h2>Why this exists</h2>
 * <p>{@link Integer#expand} / {@link Integer#compress} (and their long siblings) are
 * Java 19+ intrinsics; Foxy targets Java 17, so this class provides a portable
 * software fallback. The hot paths that use bit gather/scatter (Z-order curve
 * encoding in serialization, 4&times;4&times;4 biome index folding in voxelization)
 * either inline their own bit math or call {@link #expand} / {@link #compress}
 * here.</p>
 *
 * <h2>Semantics</h2>
 * <p>{@code expand(value, mask)} scatters the low bits of {@code value} into the bit
 * positions set in {@code mask}. {@code compress(value, mask)} is the inverse: it
 * gathers the bits at the positions set in {@code mask} into the low bits of the
 * result. Both behave the same as the JDK 19+ intrinsics.</p>
 *
 * <p>Cleanroom note: same algorithm as upstream Voxy, with English javadoc and a
 * fully unsigned-aware long loop guard ({@link Long#compareUnsigned}). The clamp
 * helpers exist because {@link Math#clamp(float, float, float)} is Java 21+.</p>
 */
public final class BitOps {
    private BitOps() {}

    /**
     * Scatters the low set bits of {@code value} into the bit positions where
     * {@code mask} has 1s. Equivalent to {@code Integer.expand(value, mask)} on
     * Java 19+.
     */
    public static int expand(int value, int mask) {
        int out = 0;
        int sourceBit = 1;
        for (int target = 1; target != 0 && target <= mask; target <<= 1) {
            if ((mask & target) != 0) {
                if ((value & sourceBit) != 0) out |= target;
                sourceBit <<= 1;
            }
        }
        return out;
    }

    /** Long variant of {@link #expand(int, int)}. */
    public static long expand(long value, long mask) {
        long out = 0L;
        long sourceBit = 1L;
        // Walk every set bit position in the mask; use unsigned comparison so a
        // mask spanning the sign bit doesn't terminate the loop early.
        for (long target = 1L; target != 0L && Long.compareUnsigned(target, mask) <= 0; target <<= 1) {
            if ((mask & target) != 0L) {
                if ((value & sourceBit) != 0L) out |= target;
                sourceBit <<= 1;
            }
        }
        return out;
    }

    /**
     * Gathers the bits of {@code value} at positions set in {@code mask} into the
     * low bits of the result, preserving order. Equivalent to
     * {@code Integer.compress(value, mask)} on Java 19+.
     */
    public static int compress(int value, int mask) {
        int out = 0;
        int destBit = 1;
        for (int source = 1; source != 0 && source <= mask; source <<= 1) {
            if ((mask & source) != 0) {
                if ((value & source) != 0) out |= destBit;
                destBit <<= 1;
            }
        }
        return out;
    }

    /** {@link Math#max}/{@link Math#min} clamp; equivalent to Java 21's {@code Math.clamp}. */
    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    /** Integer clamp helper. */
    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}

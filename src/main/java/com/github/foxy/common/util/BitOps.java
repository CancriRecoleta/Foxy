package com.github.foxy.common.util;

// Java 17 has no Integer.compress/expand or Long.compress/expand (those were added in Java 19).
// Foxy targets Java 17 (Forge 1.20.1), so these portable bit-by-bit implementations stand in for
// the JDK intrinsics with identical PEXT (compress) / PDEP (expand) semantics. Correctness is
// prioritised over throughput here because they feed block-id packing where a subtle bug would
// silently corrupt meshes.
public final class BitOps {
    private BitOps() {}

    // Parallel bits extract (PEXT): gather the bits of value selected by mask into the low bits of the result.
    public static int compress(int value, int mask) {
        int result = 0;
        int outPos = 0;
        for (int bit = 0; bit < 32; bit++) {
            if ((mask & (1 << bit)) != 0) {
                if ((value & (1 << bit)) != 0) {
                    result |= (1 << outPos);
                }
                outPos++;
            }
        }
        return result;
    }

    public static long compress(long value, long mask) {
        long result = 0;
        int outPos = 0;
        for (int bit = 0; bit < 64; bit++) {
            if ((mask & (1L << bit)) != 0) {
                if ((value & (1L << bit)) != 0) {
                    result |= (1L << outPos);
                }
                outPos++;
            }
        }
        return result;
    }

    // Parallel bits deposit (PDEP): scatter the low bits of value into the bit positions selected by mask.
    public static int expand(int value, int mask) {
        int result = 0;
        int inPos = 0;
        for (int bit = 0; bit < 32; bit++) {
            if ((mask & (1 << bit)) != 0) {
                if ((value & (1 << inPos)) != 0) {
                    result |= (1 << bit);
                }
                inPos++;
            }
        }
        return result;
    }

    public static long expand(long value, long mask) {
        long result = 0;
        int inPos = 0;
        for (int bit = 0; bit < 64; bit++) {
            if ((mask & (1L << bit)) != 0) {
                if ((value & (1L << inPos)) != 0) {
                    result |= (1L << bit);
                }
                inPos++;
            }
        }
        return result;
    }
}

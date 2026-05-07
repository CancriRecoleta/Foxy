package com.github.foxy.common.util;

public final class BitOps {
    private BitOps() {}

    public static int expand(int value, int mask) {
        int out = 0;
        int bit = 1;
        for (int target = 1; target != 0 && target <= mask; target <<= 1) {
            if ((mask & target) != 0) {
                if ((value & bit) != 0) out |= target;
                bit <<= 1;
            }
        }
        return out;
    }

    public static long expand(long value, long mask) {
        long out = 0;
        long bit = 1;
        for (long target = 1; target != 0 && Long.compareUnsigned(target, mask) <= 0; target <<= 1) {
            if ((mask & target) != 0) {
                if ((value & bit) != 0) out |= target;
                bit <<= 1;
            }
        }
        return out;
    }

    public static int compress(int value, int mask) {
        int out = 0;
        int bit = 1;
        for (int source = 1; source != 0 && source <= mask; source <<= 1) {
            if ((mask & source) != 0) {
                if ((value & source) != 0) out |= bit;
                bit <<= 1;
            }
        }
        return out;
    }

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen;

import net.minecraft.util.RandomSource;

public interface BitRandomSource extends RandomSource {
    float FLOAT_MULTIPLIER = 5.9604645E-8F;
    double DOUBLE_MULTIPLIER = 1.1102230246251565E-16;

    int next(int var1);

    default int nextInt() {
        return this.next(32);
    }

    default int nextInt(int p_188504_) {
        if (p_188504_ <= 0) {
            throw new IllegalArgumentException("Bound must be positive");
        } else if ((p_188504_ & p_188504_ - 1) == 0) {
            return (int)((long)p_188504_ * (long)this.next(31) >> 31);
        } else {
            int $$1;
            int $$2;
            do {
                $$1 = this.next(31);
                $$2 = $$1 % p_188504_;
            } while($$1 - $$2 + (p_188504_ - 1) < 0);

            return $$2;
        }
    }

    default long nextLong() {
        int $$0 = this.next(32);
        int $$1 = this.next(32);
        long $$2 = (long)$$0 << 32;
        return $$2 + (long)$$1;
    }

    default boolean nextBoolean() {
        return this.next(1) != 0;
    }

    default float nextFloat() {
        return (float)this.next(24) * 5.9604645E-8F;
    }

    default double nextDouble() {
        int $$0 = this.next(26);
        int $$1 = this.next(27);
        long $$2 = ((long)$$0 << 27) + (long)$$1;
        return (double)$$2 * 1.1102230246251565E-16;
    }
}

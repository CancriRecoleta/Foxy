//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen;

import com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.ThreadingDetector;

public class LegacyRandomSource implements BitRandomSource {
    private static final int MODULUS_BITS = 48;
    private static final long MODULUS_MASK = 281474976710655L;
    private static final long MULTIPLIER = 25214903917L;
    private static final long INCREMENT = 11L;
    private final AtomicLong seed = new AtomicLong();
    private final MarsagliaPolarGaussian gaussianSource = new MarsagliaPolarGaussian(this);

    public LegacyRandomSource(long p_188578_) {
        this.setSeed(p_188578_);
    }

    public RandomSource fork() {
        return new LegacyRandomSource(this.nextLong());
    }

    public PositionalRandomFactory forkPositional() {
        return new LegacyPositionalRandomFactory(this.nextLong());
    }

    public void setSeed(long p_188585_) {
        if (!this.seed.compareAndSet(this.seed.get(), (p_188585_ ^ 25214903917L) & 281474976710655L)) {
            throw ThreadingDetector.makeThreadingException("LegacyRandomSource", (Thread)null);
        } else {
            this.gaussianSource.reset();
        }
    }

    public int next(int p_188581_) {
        long $$1 = this.seed.get();
        long $$2 = $$1 * 25214903917L + 11L & 281474976710655L;
        if (!this.seed.compareAndSet($$1, $$2)) {
            throw ThreadingDetector.makeThreadingException("LegacyRandomSource", (Thread)null);
        } else {
            return (int)($$2 >> 48 - p_188581_);
        }
    }

    public double nextGaussian() {
        return this.gaussianSource.nextGaussian();
    }

    public static class LegacyPositionalRandomFactory implements PositionalRandomFactory {
        private final long seed;

        public LegacyPositionalRandomFactory(long p_188588_) {
            this.seed = p_188588_;
        }

        public RandomSource at(int p_224198_, int p_224199_, int p_224200_) {
            long $$3 = Mth.getSeed(p_224198_, p_224199_, p_224200_);
            long $$4 = $$3 ^ this.seed;
            return new LegacyRandomSource($$4);
        }

        public RandomSource fromHashOf(String p_224202_) {
            int $$1 = p_224202_.hashCode();
            return new LegacyRandomSource((long)$$1 ^ this.seed);
        }

        @VisibleForTesting
        public void parityConfigString(StringBuilder p_188596_) {
            p_188596_.append("LegacyPositionalRandomFactory{").append(this.seed).append("}");
        }
    }
}

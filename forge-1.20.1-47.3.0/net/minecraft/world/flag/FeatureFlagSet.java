//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.flag;

import it.unimi.dsi.fastutil.HashCommon;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import javax.annotation.Nullable;

public final class FeatureFlagSet {
    private static final FeatureFlagSet EMPTY = new FeatureFlagSet((FeatureFlagUniverse)null, 0L);
    public static final int MAX_CONTAINER_SIZE = 64;
    @Nullable
    private final FeatureFlagUniverse universe;
    private final long mask;

    private FeatureFlagSet(@Nullable FeatureFlagUniverse p_250433_, long p_251523_) {
        this.universe = p_250433_;
        this.mask = p_251523_;
    }

    static FeatureFlagSet create(FeatureFlagUniverse p_251573_, Collection<FeatureFlag> p_251037_) {
        if (p_251037_.isEmpty()) {
            return EMPTY;
        } else {
            long $$2 = computeMask(p_251573_, 0L, p_251037_);
            return new FeatureFlagSet(p_251573_, $$2);
        }
    }

    public static FeatureFlagSet of() {
        return EMPTY;
    }

    public static FeatureFlagSet of(FeatureFlag p_252331_) {
        return new FeatureFlagSet(p_252331_.universe, p_252331_.mask);
    }

    public static FeatureFlagSet of(FeatureFlag p_251008_, FeatureFlag... p_249805_) {
        long $$2 = p_249805_.length == 0 ? p_251008_.mask : computeMask(p_251008_.universe, p_251008_.mask, Arrays.asList(p_249805_));
        return new FeatureFlagSet(p_251008_.universe, $$2);
    }

    private static long computeMask(FeatureFlagUniverse p_249684_, long p_250982_, Iterable<FeatureFlag> p_251734_) {
        FeatureFlag $$3;
        for(Iterator var4 = p_251734_.iterator(); var4.hasNext(); p_250982_ |= $$3.mask) {
            $$3 = (FeatureFlag)var4.next();
            if (p_249684_ != $$3.universe) {
                throw new IllegalStateException("Mismatched feature universe, expected '" + p_249684_ + "', but got '" + $$3.universe + "'");
            }
        }

        return p_250982_;
    }

    public boolean contains(FeatureFlag p_249521_) {
        if (this.universe != p_249521_.universe) {
            return false;
        } else {
            return (this.mask & p_249521_.mask) != 0L;
        }
    }

    public boolean isSubsetOf(FeatureFlagSet p_249164_) {
        if (this.universe == null) {
            return true;
        } else if (this.universe != p_249164_.universe) {
            return false;
        } else {
            return (this.mask & ~p_249164_.mask) == 0L;
        }
    }

    public FeatureFlagSet join(FeatureFlagSet p_251527_) {
        if (this.universe == null) {
            return p_251527_;
        } else if (p_251527_.universe == null) {
            return this;
        } else if (this.universe != p_251527_.universe) {
            throw new IllegalArgumentException("Mismatched set elements: '" + this.universe + "' != '" + p_251527_.universe + "'");
        } else {
            return new FeatureFlagSet(this.universe, this.mask | p_251527_.mask);
        }
    }

    public boolean equals(Object p_248691_) {
        if (this == p_248691_) {
            return true;
        } else {
            boolean var10000;
            if (p_248691_ instanceof FeatureFlagSet) {
                FeatureFlagSet $$1 = (FeatureFlagSet)p_248691_;
                if (this.universe == $$1.universe && this.mask == $$1.mask) {
                    var10000 = true;
                    return var10000;
                }
            }

            var10000 = false;
            return var10000;
        }
    }

    public int hashCode() {
        return (int)HashCommon.mix(this.mask);
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util;

import java.util.concurrent.TimeUnit;
import net.minecraft.util.valueproviders.UniformInt;

public class TimeUtil {
    public static final long NANOSECONDS_PER_SECOND;
    public static final long NANOSECONDS_PER_MILLISECOND;

    public TimeUtil() {
    }

    public static UniformInt rangeOfSeconds(int p_145021_, int p_145022_) {
        return UniformInt.of(p_145021_ * 20, p_145022_ * 20);
    }

    static {
        NANOSECONDS_PER_SECOND = TimeUnit.SECONDS.toNanos(1L);
        NANOSECONDS_PER_MILLISECOND = TimeUnit.MILLISECONDS.toNanos(1L);
    }
}

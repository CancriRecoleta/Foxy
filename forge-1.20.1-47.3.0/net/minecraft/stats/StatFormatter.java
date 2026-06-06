//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.stats;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;
import net.minecraft.Util;

public interface StatFormatter {
    DecimalFormat DECIMAL_FORMAT = (DecimalFormat)Util.make(new DecimalFormat("########0.00"), (p_12881_) -> {
        p_12881_.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
    });
    StatFormatter DEFAULT;
    StatFormatter DIVIDE_BY_TEN;
    StatFormatter DISTANCE;
    StatFormatter TIME;

    String format(int var1);

    static {
        NumberFormat var10000 = NumberFormat.getIntegerInstance(Locale.US);
        Objects.requireNonNull(var10000);
        DEFAULT = var10000::format;
        DIVIDE_BY_TEN = (p_12885_) -> {
            return DECIMAL_FORMAT.format((double)p_12885_ * 0.1);
        };
        DISTANCE = (p_12883_) -> {
            double $$1 = (double)p_12883_ / 100.0;
            double $$2 = $$1 / 1000.0;
            if ($$2 > 0.5) {
                return DECIMAL_FORMAT.format($$2) + " km";
            } else {
                return $$1 > 0.5 ? DECIMAL_FORMAT.format($$1) + " m" : "" + p_12883_ + " cm";
            }
        };
        TIME = (p_12879_) -> {
            double $$1 = (double)p_12879_ / 20.0;
            double $$2 = $$1 / 60.0;
            double $$3 = $$2 / 60.0;
            double $$4 = $$3 / 24.0;
            double $$5 = $$4 / 365.0;
            if ($$5 > 0.5) {
                return DECIMAL_FORMAT.format($$5) + " y";
            } else if ($$4 > 0.5) {
                return DECIMAL_FORMAT.format($$4) + " d";
            } else if ($$3 > 0.5) {
                return DECIMAL_FORMAT.format($$3) + " h";
            } else {
                return $$2 > 0.5 ? DECIMAL_FORMAT.format($$2) + " m" : "" + $$1 + " s";
            }
        };
    }
}

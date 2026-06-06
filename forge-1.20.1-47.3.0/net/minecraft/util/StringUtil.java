//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public class StringUtil {
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");
    private static final Pattern LINE_PATTERN = Pattern.compile("\\r\\n|\\v");
    private static final Pattern LINE_END_PATTERN = Pattern.compile("(?:\\r\\n|\\v)$");

    public StringUtil() {
    }

    public static String formatTickDuration(int p_14405_) {
        int $$1 = p_14405_ / 20;
        int $$2 = $$1 / 60;
        $$1 %= 60;
        int $$3 = $$2 / 60;
        $$2 %= 60;
        return $$3 > 0 ? String.format(Locale.ROOT, "%02d:%02d:%02d", $$3, $$2, $$1) : String.format(Locale.ROOT, "%02d:%02d", $$2, $$1);
    }

    public static String stripColor(String p_14407_) {
        return STRIP_COLOR_PATTERN.matcher(p_14407_).replaceAll("");
    }

    public static boolean isNullOrEmpty(@Nullable String p_14409_) {
        return StringUtils.isEmpty(p_14409_);
    }

    public static String truncateStringIfNecessary(String p_144999_, int p_145000_, boolean p_145001_) {
        if (p_144999_.length() <= p_145000_) {
            return p_144999_;
        } else if (p_145001_ && p_145000_ > 3) {
            String var10000 = p_144999_.substring(0, p_145000_ - 3);
            return var10000 + "...";
        } else {
            return p_144999_.substring(0, p_145000_);
        }
    }

    public static int lineCount(String p_145003_) {
        if (p_145003_.isEmpty()) {
            return 0;
        } else {
            Matcher $$1 = LINE_PATTERN.matcher(p_145003_);

            int $$2;
            for($$2 = 1; $$1.find(); ++$$2) {
            }

            return $$2;
        }
    }

    public static boolean endsWithNewLine(String p_145005_) {
        return LINE_END_PATTERN.matcher(p_145005_).find();
    }

    public static String trimChatMessage(String p_216470_) {
        return truncateStringIfNecessary(p_216470_, 256, false);
    }
}

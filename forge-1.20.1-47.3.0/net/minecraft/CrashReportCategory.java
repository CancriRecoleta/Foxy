//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.logging.CrashReportExtender;

public class CrashReportCategory {
    private final String title;
    private final List<Entry> entries = Lists.newArrayList();
    private StackTraceElement[] stackTrace = new StackTraceElement[0];

    public CrashReportCategory(String p_178936_) {
        this.title = p_178936_;
    }

    public static String formatLocation(LevelHeightAccessor p_178938_, double p_178939_, double p_178940_, double p_178941_) {
        return String.format(Locale.ROOT, "%.2f,%.2f,%.2f - %s", p_178939_, p_178940_, p_178941_, formatLocation(p_178938_, BlockPos.containing(p_178939_, p_178940_, p_178941_)));
    }

    public static String formatLocation(LevelHeightAccessor p_178948_, BlockPos p_178949_) {
        return formatLocation(p_178948_, p_178949_.getX(), p_178949_.getY(), p_178949_.getZ());
    }

    public static String formatLocation(LevelHeightAccessor p_178943_, int p_178944_, int p_178945_, int p_178946_) {
        StringBuilder stringbuilder = new StringBuilder();

        try {
            stringbuilder.append(String.format(Locale.ROOT, "World: (%d,%d,%d)", p_178944_, p_178945_, p_178946_));
        } catch (Throwable var19) {
            stringbuilder.append("(Error finding world loc)");
        }

        stringbuilder.append(", ");

        int i3;
        int j3;
        int k3;
        int l3;
        int i4;
        int j4;
        int k4;
        int l4;
        int i5;
        int j5;
        int k5;
        int l5;
        try {
            i3 = SectionPos.blockToSectionCoord(p_178944_);
            j3 = SectionPos.blockToSectionCoord(p_178945_);
            k3 = SectionPos.blockToSectionCoord(p_178946_);
            l3 = p_178944_ & 15;
            i4 = p_178945_ & 15;
            j4 = p_178946_ & 15;
            k4 = SectionPos.sectionToBlockCoord(i3);
            l4 = p_178943_.getMinBuildHeight();
            i5 = SectionPos.sectionToBlockCoord(k3);
            j5 = SectionPos.sectionToBlockCoord(i3 + 1) - 1;
            k5 = p_178943_.getMaxBuildHeight() - 1;
            l5 = SectionPos.sectionToBlockCoord(k3 + 1) - 1;
            stringbuilder.append(String.format(Locale.ROOT, "Section: (at %d,%d,%d in %d,%d,%d; chunk contains blocks %d,%d,%d to %d,%d,%d)", l3, i4, j4, i3, j3, k3, k4, l4, i5, j5, k5, l5));
        } catch (Throwable var18) {
            stringbuilder.append("(Error finding chunk loc)");
        }

        stringbuilder.append(", ");

        try {
            i3 = p_178944_ >> 9;
            j3 = p_178946_ >> 9;
            k3 = i3 << 5;
            l3 = j3 << 5;
            i4 = (i3 + 1 << 5) - 1;
            j4 = (j3 + 1 << 5) - 1;
            k4 = i3 << 9;
            l4 = p_178943_.getMinBuildHeight();
            i5 = j3 << 9;
            j5 = (i3 + 1 << 9) - 1;
            k5 = p_178943_.getMaxBuildHeight() - 1;
            l5 = (j3 + 1 << 9) - 1;
            stringbuilder.append(String.format(Locale.ROOT, "Region: (%d,%d; contains chunks %d,%d to %d,%d, blocks %d,%d,%d to %d,%d,%d)", i3, j3, k3, l3, i4, j4, k4, l4, i5, j5, k5, l5));
        } catch (Throwable var17) {
            stringbuilder.append("(Error finding world loc)");
        }

        return stringbuilder.toString();
    }

    public CrashReportCategory setDetail(String p_128166_, CrashReportDetail<String> p_128167_) {
        try {
            this.setDetail(p_128166_, p_128167_.call());
        } catch (Throwable var4) {
            Throwable throwable = var4;
            this.setDetailError(p_128166_, throwable);
        }

        return this;
    }

    public CrashReportCategory setDetail(String p_128160_, Object p_128161_) {
        this.entries.add(new Entry(p_128160_, p_128161_));
        return this;
    }

    public void setDetailError(String p_128163_, Throwable p_128164_) {
        this.setDetail(p_128163_, (Object)p_128164_);
    }

    public int fillInStackTrace(int p_128149_) {
        StackTraceElement[] astacktraceelement = Thread.currentThread().getStackTrace();
        if (astacktraceelement.length <= 0) {
            return 0;
        } else {
            int len = astacktraceelement.length - 3 - p_128149_;
            if (len <= 0) {
                len = astacktraceelement.length;
            }

            this.stackTrace = new StackTraceElement[len];
            System.arraycopy(astacktraceelement, astacktraceelement.length - len, this.stackTrace, 0, this.stackTrace.length);
            return this.stackTrace.length;
        }
    }

    public boolean validateStackTrace(StackTraceElement p_128157_, StackTraceElement p_128158_) {
        if (this.stackTrace.length != 0 && p_128157_ != null) {
            StackTraceElement stacktraceelement = this.stackTrace[0];
            if (stacktraceelement.isNativeMethod() == p_128157_.isNativeMethod() && stacktraceelement.getClassName().equals(p_128157_.getClassName()) && stacktraceelement.getFileName().equals(p_128157_.getFileName()) && stacktraceelement.getMethodName().equals(p_128157_.getMethodName())) {
                if (p_128158_ != null != this.stackTrace.length > 1) {
                    return false;
                } else if (p_128158_ != null && !this.stackTrace[1].equals(p_128158_)) {
                    return false;
                } else {
                    this.stackTrace[0] = p_128157_;
                    return true;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void trimStacktrace(int p_128175_) {
        StackTraceElement[] astacktraceelement = new StackTraceElement[this.stackTrace.length - p_128175_];
        System.arraycopy(this.stackTrace, 0, astacktraceelement, 0, astacktraceelement.length);
        this.stackTrace = astacktraceelement;
    }

    public void getDetails(StringBuilder p_128169_) {
        p_128169_.append("-- ").append(this.title).append(" --\n");
        p_128169_.append("Details:");
        Iterator var2 = this.entries.iterator();

        while(var2.hasNext()) {
            Entry crashreportcategory$entry = (Entry)var2.next();
            p_128169_.append("\n\t");
            p_128169_.append(crashreportcategory$entry.getKey());
            p_128169_.append(": ");
            p_128169_.append(crashreportcategory$entry.getValue());
        }

        if (this.stackTrace != null && this.stackTrace.length > 0) {
            p_128169_.append("\nStacktrace:");
            p_128169_.append(CrashReportExtender.generateEnhancedStackTrace(this.stackTrace));
        }

    }

    public StackTraceElement[] getStacktrace() {
        return this.stackTrace;
    }

    public void applyStackTrace(Throwable t) {
        this.stackTrace = t.getStackTrace();
    }

    public static void populateBlockDetails(CrashReportCategory p_178951_, LevelHeightAccessor p_178952_, BlockPos p_178953_, @Nullable BlockState p_178954_) {
        if (p_178954_ != null) {
            Objects.requireNonNull(p_178954_);
            p_178951_.setDetail("Block", p_178954_::toString);
        }

        p_178951_.setDetail("Block location", () -> {
            return formatLocation(p_178952_, p_178953_);
        });
    }

    static class Entry {
        private final String key;
        private final String value;

        public Entry(String p_128181_, @Nullable Object p_128182_) {
            this.key = p_128181_;
            if (p_128182_ == null) {
                this.value = "~~NULL~~";
            } else if (p_128182_ instanceof Throwable) {
                Throwable throwable = (Throwable)p_128182_;
                String var10001 = throwable.getClass().getSimpleName();
                this.value = "~~ERROR~~ " + var10001 + ": " + throwable.getMessage();
            } else {
                this.value = p_128182_.toString();
            }

        }

        public String getKey() {
            return this.key;
        }

        public String getValue() {
            return this.value;
        }
    }
}

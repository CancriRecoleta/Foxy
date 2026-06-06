//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util;

public class FrameTimer {
    public static final int LOGGING_LENGTH = 240;
    private final long[] loggedTimes = new long[240];
    private int logStart;
    private int logLength;
    private int logEnd;

    public FrameTimer() {
    }

    public void logFrameDuration(long p_13756_) {
        this.loggedTimes[this.logEnd] = p_13756_;
        ++this.logEnd;
        if (this.logEnd == 240) {
            this.logEnd = 0;
        }

        if (this.logLength < 240) {
            this.logStart = 0;
            ++this.logLength;
        } else {
            this.logStart = this.wrapIndex(this.logEnd + 1);
        }

    }

    public long getAverageDuration(int p_144733_) {
        int $$1 = (this.logStart + p_144733_) % 240;
        int $$2 = this.logStart;

        long $$3;
        for($$3 = 0L; $$2 != $$1; ++$$2) {
            $$3 += this.loggedTimes[$$2];
        }

        return $$3 / (long)p_144733_;
    }

    public int scaleAverageDurationTo(int p_144735_, int p_144736_) {
        return this.scaleSampleTo(this.getAverageDuration(p_144735_), p_144736_, 60);
    }

    public int scaleSampleTo(long p_13758_, int p_13759_, int p_13760_) {
        double $$3 = (double)p_13758_ / (double)(1000000000L / (long)p_13760_);
        return (int)($$3 * (double)p_13759_);
    }

    public int getLogStart() {
        return this.logStart;
    }

    public int getLogEnd() {
        return this.logEnd;
    }

    public int wrapIndex(int p_13763_) {
        return p_13763_ % 240;
    }

    public long[] getLog() {
        return this.loggedTimes;
    }
}

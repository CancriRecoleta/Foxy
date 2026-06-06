//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util.profiling.jfr.stats;

import java.time.Duration;
import java.time.Instant;
import jdk.jfr.consumer.RecordedEvent;

public record TickTimeStat(Instant timestamp, Duration currentAverage) {
    public TickTimeStat(Instant timestamp, Duration currentAverage) {
        this.timestamp = timestamp;
        this.currentAverage = currentAverage;
    }

    public static TickTimeStat from(RecordedEvent p_185826_) {
        return new TickTimeStat(p_185826_.getStartTime(), p_185826_.getDuration("averageTickDuration"));
    }

    public Instant timestamp() {
        return this.timestamp;
    }

    public Duration currentAverage() {
        return this.currentAverage;
    }
}

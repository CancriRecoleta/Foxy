//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util.profiling.jfr.stats;

import com.mojang.datafixers.util.Pair;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public record FileIOStat(Duration duration, @Nullable String path, long bytes) {
    public FileIOStat(Duration duration, @Nullable String path, long bytes) {
        this.duration = duration;
        this.path = path;
        this.bytes = bytes;
    }

    public static Summary summary(Duration p_185641_, List<FileIOStat> p_185642_) {
        long $$2 = p_185642_.stream().mapToLong((p_185652_) -> {
            return p_185652_.bytes;
        }).sum();
        return new Summary($$2, (double)$$2 / (double)p_185641_.getSeconds(), (long)p_185642_.size(), (double)p_185642_.size() / (double)p_185641_.getSeconds(), (Duration)p_185642_.stream().map(FileIOStat::duration).reduce(Duration.ZERO, Duration::plus), ((Map)p_185642_.stream().filter((p_185650_) -> {
            return p_185650_.path != null;
        }).collect(Collectors.groupingBy((p_185647_) -> {
            return p_185647_.path;
        }, Collectors.summingLong((p_185639_) -> {
            return p_185639_.bytes;
        })))).entrySet().stream().sorted(Entry.comparingByValue().reversed()).map((p_185644_) -> {
            return Pair.of((String)p_185644_.getKey(), (Long)p_185644_.getValue());
        }).limit(10L).toList());
    }

    public Duration duration() {
        return this.duration;
    }

    @Nullable
    public String path() {
        return this.path;
    }

    public long bytes() {
        return this.bytes;
    }

    public static record Summary(long totalBytes, double bytesPerSecond, long counts, double countsPerSecond, Duration timeSpentInIO, List<Pair<String, Long>> topTenContributorsByTotalBytes) {
        public Summary(long totalBytes, double bytesPerSecond, long counts, double countsPerSecond, Duration timeSpentInIO, List<Pair<String, Long>> topTenContributorsByTotalBytes) {
            this.totalBytes = totalBytes;
            this.bytesPerSecond = bytesPerSecond;
            this.counts = counts;
            this.countsPerSecond = countsPerSecond;
            this.timeSpentInIO = timeSpentInIO;
            this.topTenContributorsByTotalBytes = topTenContributorsByTotalBytes;
        }

        public long totalBytes() {
            return this.totalBytes;
        }

        public double bytesPerSecond() {
            return this.bytesPerSecond;
        }

        public long counts() {
            return this.counts;
        }

        public double countsPerSecond() {
            return this.countsPerSecond;
        }

        public Duration timeSpentInIO() {
            return this.timeSpentInIO;
        }

        public List<Pair<String, Long>> topTenContributorsByTotalBytes() {
            return this.topTenContributorsByTotalBytes;
        }
    }
}

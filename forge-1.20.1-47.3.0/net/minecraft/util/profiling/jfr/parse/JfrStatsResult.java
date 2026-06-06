//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util.profiling.jfr.parse;

import com.mojang.datafixers.util.Pair;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.profiling.jfr.serialize.JfrResultJsonSerializer;
import net.minecraft.util.profiling.jfr.stats.ChunkGenStat;
import net.minecraft.util.profiling.jfr.stats.CpuLoadStat;
import net.minecraft.util.profiling.jfr.stats.FileIOStat;
import net.minecraft.util.profiling.jfr.stats.GcHeapStat;
import net.minecraft.util.profiling.jfr.stats.NetworkPacketSummary;
import net.minecraft.util.profiling.jfr.stats.ThreadAllocationStat;
import net.minecraft.util.profiling.jfr.stats.TickTimeStat;
import net.minecraft.util.profiling.jfr.stats.TimedStatSummary;
import net.minecraft.world.level.chunk.ChunkStatus;

public record JfrStatsResult(Instant recordingStarted, Instant recordingEnded, Duration recordingDuration, @Nullable Duration worldCreationDuration, List<TickTimeStat> tickTimes, List<CpuLoadStat> cpuLoadStats, GcHeapStat.Summary heapSummary, ThreadAllocationStat.Summary threadAllocationSummary, NetworkPacketSummary receivedPacketsSummary, NetworkPacketSummary sentPacketsSummary, FileIOStat.Summary fileWrites, FileIOStat.Summary fileReads, List<ChunkGenStat> chunkGenStats) {
    public JfrStatsResult(Instant recordingStarted, Instant recordingEnded, Duration recordingDuration, @Nullable Duration worldCreationDuration, List<TickTimeStat> tickTimes, List<CpuLoadStat> cpuLoadStats, GcHeapStat.Summary heapSummary, ThreadAllocationStat.Summary threadAllocationSummary, NetworkPacketSummary receivedPacketsSummary, NetworkPacketSummary sentPacketsSummary, FileIOStat.Summary fileWrites, FileIOStat.Summary fileReads, List<ChunkGenStat> chunkGenStats) {
        this.recordingStarted = recordingStarted;
        this.recordingEnded = recordingEnded;
        this.recordingDuration = recordingDuration;
        this.worldCreationDuration = worldCreationDuration;
        this.tickTimes = tickTimes;
        this.cpuLoadStats = cpuLoadStats;
        this.heapSummary = heapSummary;
        this.threadAllocationSummary = threadAllocationSummary;
        this.receivedPacketsSummary = receivedPacketsSummary;
        this.sentPacketsSummary = sentPacketsSummary;
        this.fileWrites = fileWrites;
        this.fileReads = fileReads;
        this.chunkGenStats = chunkGenStats;
    }

    public List<Pair<ChunkStatus, TimedStatSummary<ChunkGenStat>>> chunkGenSummary() {
        Map<ChunkStatus, List<ChunkGenStat>> $$0 = (Map)this.chunkGenStats.stream().collect(Collectors.groupingBy(ChunkGenStat::status));
        return $$0.entrySet().stream().map((p_185509_) -> {
            return Pair.of((ChunkStatus)p_185509_.getKey(), TimedStatSummary.summary((List)p_185509_.getValue()));
        }).sorted(Comparator.comparing((p_185507_) -> {
            return ((TimedStatSummary)p_185507_.getSecond()).totalDuration();
        }).reversed()).toList();
    }

    public String asJson() {
        return (new JfrResultJsonSerializer()).format(this);
    }

    public Instant recordingStarted() {
        return this.recordingStarted;
    }

    public Instant recordingEnded() {
        return this.recordingEnded;
    }

    public Duration recordingDuration() {
        return this.recordingDuration;
    }

    @Nullable
    public Duration worldCreationDuration() {
        return this.worldCreationDuration;
    }

    public List<TickTimeStat> tickTimes() {
        return this.tickTimes;
    }

    public List<CpuLoadStat> cpuLoadStats() {
        return this.cpuLoadStats;
    }

    public GcHeapStat.Summary heapSummary() {
        return this.heapSummary;
    }

    public ThreadAllocationStat.Summary threadAllocationSummary() {
        return this.threadAllocationSummary;
    }

    public NetworkPacketSummary receivedPacketsSummary() {
        return this.receivedPacketsSummary;
    }

    public NetworkPacketSummary sentPacketsSummary() {
        return this.sentPacketsSummary;
    }

    public FileIOStat.Summary fileWrites() {
        return this.fileWrites;
    }

    public FileIOStat.Summary fileReads() {
        return this.fileReads;
    }

    public List<ChunkGenStat> chunkGenStats() {
        return this.chunkGenStats;
    }
}

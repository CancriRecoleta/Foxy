//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util.profiling.jfr.parse;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordingFile;
import net.minecraft.util.profiling.jfr.stats.ChunkGenStat;
import net.minecraft.util.profiling.jfr.stats.CpuLoadStat;
import net.minecraft.util.profiling.jfr.stats.FileIOStat;
import net.minecraft.util.profiling.jfr.stats.GcHeapStat;
import net.minecraft.util.profiling.jfr.stats.NetworkPacketSummary;
import net.minecraft.util.profiling.jfr.stats.ThreadAllocationStat;
import net.minecraft.util.profiling.jfr.stats.TickTimeStat;
import net.minecraft.util.profiling.jfr.stats.NetworkPacketSummary.PacketIdentification;

public class JfrStatsParser {
    private Instant recordingStarted;
    private Instant recordingEnded;
    private final List<ChunkGenStat> chunkGenStats;
    private final List<CpuLoadStat> cpuLoadStat;
    private final Map<NetworkPacketSummary.PacketIdentification, MutableCountAndSize> receivedPackets;
    private final Map<NetworkPacketSummary.PacketIdentification, MutableCountAndSize> sentPackets;
    private final List<FileIOStat> fileWrites;
    private final List<FileIOStat> fileReads;
    private int garbageCollections;
    private Duration gcTotalDuration;
    private final List<GcHeapStat> gcHeapStats;
    private final List<ThreadAllocationStat> threadAllocationStats;
    private final List<TickTimeStat> tickTimes;
    @Nullable
    private Duration worldCreationDuration;

    private JfrStatsParser(Stream<RecordedEvent> p_185443_) {
        this.recordingStarted = Instant.EPOCH;
        this.recordingEnded = Instant.EPOCH;
        this.chunkGenStats = Lists.newArrayList();
        this.cpuLoadStat = Lists.newArrayList();
        this.receivedPackets = Maps.newHashMap();
        this.sentPackets = Maps.newHashMap();
        this.fileWrites = Lists.newArrayList();
        this.fileReads = Lists.newArrayList();
        this.gcTotalDuration = Duration.ZERO;
        this.gcHeapStats = Lists.newArrayList();
        this.threadAllocationStats = Lists.newArrayList();
        this.tickTimes = Lists.newArrayList();
        this.worldCreationDuration = null;
        this.capture(p_185443_);
    }

    public static JfrStatsResult parse(Path p_185448_) {
        try {
            final RecordingFile $$1 = new RecordingFile(p_185448_);

            JfrStatsResult var4;
            try {
                Iterator<RecordedEvent> $$2 = new Iterator<RecordedEvent>() {
                    public boolean hasNext() {
                        return $$1.hasMoreEvents();
                    }

                    public RecordedEvent next() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        } else {
                            try {
                                return $$1.readEvent();
                            } catch (IOException var2) {
                                IOException $$0 = var2;
                                throw new UncheckedIOException($$0);
                            }
                        }
                    }
                };
                Stream<RecordedEvent> $$3 = StreamSupport.stream(Spliterators.spliteratorUnknownSize($$2, 1297), false);
                var4 = (new JfrStatsParser($$3)).results();
            } catch (Throwable var6) {
                try {
                    $$1.close();
                } catch (Throwable var5) {
                    var6.addSuppressed(var5);
                }

                throw var6;
            }

            $$1.close();
            return var4;
        } catch (IOException var7) {
            IOException $$4 = var7;
            throw new UncheckedIOException($$4);
        }
    }

    private JfrStatsResult results() {
        Duration $$0 = Duration.between(this.recordingStarted, this.recordingEnded);
        return new JfrStatsResult(this.recordingStarted, this.recordingEnded, $$0, this.worldCreationDuration, this.tickTimes, this.cpuLoadStat, GcHeapStat.summary($$0, this.gcHeapStats, this.gcTotalDuration, this.garbageCollections), ThreadAllocationStat.summary(this.threadAllocationStats), collectPacketStats($$0, this.receivedPackets), collectPacketStats($$0, this.sentPackets), FileIOStat.summary($$0, this.fileWrites), FileIOStat.summary($$0, this.fileReads), this.chunkGenStats);
    }

    private void capture(Stream<RecordedEvent> p_185455_) {
        p_185455_.forEach((p_185457_) -> {
            if (p_185457_.getEndTime().isAfter(this.recordingEnded) || this.recordingEnded.equals(Instant.EPOCH)) {
                this.recordingEnded = p_185457_.getEndTime();
            }

            if (p_185457_.getStartTime().isBefore(this.recordingStarted) || this.recordingStarted.equals(Instant.EPOCH)) {
                this.recordingStarted = p_185457_.getStartTime();
            }

            switch (p_185457_.getEventType().getName()) {
                case "minecraft.ChunkGeneration":
                    this.chunkGenStats.add(ChunkGenStat.from(p_185457_));
                    break;
                case "minecraft.LoadWorld":
                    this.worldCreationDuration = p_185457_.getDuration();
                    break;
                case "minecraft.ServerTickTime":
                    this.tickTimes.add(TickTimeStat.from(p_185457_));
                    break;
                case "minecraft.PacketReceived":
                    this.incrementPacket(p_185457_, p_185457_.getInt("bytes"), this.receivedPackets);
                    break;
                case "minecraft.PacketSent":
                    this.incrementPacket(p_185457_, p_185457_.getInt("bytes"), this.sentPackets);
                    break;
                case "jdk.ThreadAllocationStatistics":
                    this.threadAllocationStats.add(ThreadAllocationStat.from(p_185457_));
                    break;
                case "jdk.GCHeapSummary":
                    this.gcHeapStats.add(GcHeapStat.from(p_185457_));
                    break;
                case "jdk.CPULoad":
                    this.cpuLoadStat.add(CpuLoadStat.from(p_185457_));
                    break;
                case "jdk.FileWrite":
                    this.appendFileIO(p_185457_, this.fileWrites, "bytesWritten");
                    break;
                case "jdk.FileRead":
                    this.appendFileIO(p_185457_, this.fileReads, "bytesRead");
                    break;
                case "jdk.GarbageCollection":
                    ++this.garbageCollections;
                    this.gcTotalDuration = this.gcTotalDuration.plus(p_185457_.getDuration());
            }

        });
    }

    private void incrementPacket(RecordedEvent p_185459_, int p_185460_, Map<NetworkPacketSummary.PacketIdentification, MutableCountAndSize> p_185461_) {
        ((MutableCountAndSize)p_185461_.computeIfAbsent(PacketIdentification.from(p_185459_), (p_185446_) -> {
            return new MutableCountAndSize();
        })).increment(p_185460_);
    }

    private void appendFileIO(RecordedEvent p_185463_, List<FileIOStat> p_185464_, String p_185465_) {
        p_185464_.add(new FileIOStat(p_185463_.getDuration(), p_185463_.getString("path"), p_185463_.getLong(p_185465_)));
    }

    private static NetworkPacketSummary collectPacketStats(Duration p_185450_, Map<NetworkPacketSummary.PacketIdentification, MutableCountAndSize> p_185451_) {
        List<Pair<NetworkPacketSummary.PacketIdentification, NetworkPacketSummary.PacketCountAndSize>> $$2 = p_185451_.entrySet().stream().map((p_185453_) -> {
            return Pair.of((NetworkPacketSummary.PacketIdentification)p_185453_.getKey(), ((MutableCountAndSize)p_185453_.getValue()).toCountAndSize());
        }).toList();
        return new NetworkPacketSummary(p_185450_, $$2);
    }

    public static final class MutableCountAndSize {
        private long count;
        private long totalSize;

        public MutableCountAndSize() {
        }

        public void increment(int p_185477_) {
            this.totalSize += (long)p_185477_;
            ++this.count;
        }

        public NetworkPacketSummary.PacketCountAndSize toCountAndSize() {
            return new NetworkPacketSummary.PacketCountAndSize(this.count, this.totalSize);
        }
    }
}

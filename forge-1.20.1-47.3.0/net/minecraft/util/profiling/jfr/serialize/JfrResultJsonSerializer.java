//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util.profiling.jfr.serialize;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.LongSerializationPolicy;
import com.mojang.datafixers.util.Pair;
import java.time.Duration;
import java.util.DoubleSummaryStatistics;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.DoubleStream;
import net.minecraft.Util;
import net.minecraft.util.profiling.jfr.Percentiles;
import net.minecraft.util.profiling.jfr.parse.JfrStatsResult;
import net.minecraft.util.profiling.jfr.stats.ChunkGenStat;
import net.minecraft.util.profiling.jfr.stats.CpuLoadStat;
import net.minecraft.util.profiling.jfr.stats.FileIOStat;
import net.minecraft.util.profiling.jfr.stats.GcHeapStat;
import net.minecraft.util.profiling.jfr.stats.NetworkPacketSummary;
import net.minecraft.util.profiling.jfr.stats.ThreadAllocationStat;
import net.minecraft.util.profiling.jfr.stats.TickTimeStat;
import net.minecraft.util.profiling.jfr.stats.TimedStatSummary;
import net.minecraft.world.level.chunk.ChunkStatus;

public class JfrResultJsonSerializer {
    private static final String BYTES_PER_SECOND = "bytesPerSecond";
    private static final String COUNT = "count";
    private static final String DURATION_NANOS_TOTAL = "durationNanosTotal";
    private static final String TOTAL_BYTES = "totalBytes";
    private static final String COUNT_PER_SECOND = "countPerSecond";
    final Gson gson;

    public JfrResultJsonSerializer() {
        this.gson = (new GsonBuilder()).setPrettyPrinting().setLongSerializationPolicy(LongSerializationPolicy.DEFAULT).create();
    }

    public String format(JfrStatsResult p_185536_) {
        JsonObject $$1 = new JsonObject();
        $$1.addProperty("startedEpoch", p_185536_.recordingStarted().toEpochMilli());
        $$1.addProperty("endedEpoch", p_185536_.recordingEnded().toEpochMilli());
        $$1.addProperty("durationMs", p_185536_.recordingDuration().toMillis());
        Duration $$2 = p_185536_.worldCreationDuration();
        if ($$2 != null) {
            $$1.addProperty("worldGenDurationMs", $$2.toMillis());
        }

        $$1.add("heap", this.heap(p_185536_.heapSummary()));
        $$1.add("cpuPercent", this.cpu(p_185536_.cpuLoadStats()));
        $$1.add("network", this.network(p_185536_));
        $$1.add("fileIO", this.fileIO(p_185536_));
        $$1.add("serverTick", this.serverTicks(p_185536_.tickTimes()));
        $$1.add("threadAllocation", this.threadAllocations(p_185536_.threadAllocationSummary()));
        $$1.add("chunkGen", this.chunkGen(p_185536_.chunkGenSummary()));
        return this.gson.toJson($$1);
    }

    private JsonElement heap(GcHeapStat.Summary p_185542_) {
        JsonObject $$1 = new JsonObject();
        $$1.addProperty("allocationRateBytesPerSecond", p_185542_.allocationRateBytesPerSecond());
        $$1.addProperty("gcCount", p_185542_.totalGCs());
        $$1.addProperty("gcOverHeadPercent", p_185542_.gcOverHead());
        $$1.addProperty("gcTotalDurationMs", p_185542_.gcTotalDuration().toMillis());
        return $$1;
    }

    private JsonElement chunkGen(List<Pair<ChunkStatus, TimedStatSummary<ChunkGenStat>>> p_185573_) {
        JsonObject $$1 = new JsonObject();
        $$1.addProperty("durationNanosTotal", p_185573_.stream().mapToDouble((p_185567_) -> {
            return (double)((TimedStatSummary)p_185567_.getSecond()).totalDuration().toNanos();
        }).sum());
        JsonArray $$2 = (JsonArray)Util.make(new JsonArray(), (p_185558_) -> {
            $$1.add("status", p_185558_);
        });
        Iterator var4 = p_185573_.iterator();

        while(var4.hasNext()) {
            Pair<ChunkStatus, TimedStatSummary<ChunkGenStat>> $$3 = (Pair)var4.next();
            TimedStatSummary<ChunkGenStat> $$4 = (TimedStatSummary)$$3.getSecond();
            JsonObject var10000 = new JsonObject();
            Objects.requireNonNull($$2);
            JsonObject $$5 = (JsonObject)Util.make(var10000, $$2::add);
            $$5.addProperty("state", ((ChunkStatus)$$3.getFirst()).toString());
            $$5.addProperty("count", $$4.count());
            $$5.addProperty("durationNanosTotal", $$4.totalDuration().toNanos());
            $$5.addProperty("durationNanosAvg", $$4.totalDuration().toNanos() / (long)$$4.count());
            JsonObject $$6 = (JsonObject)Util.make(new JsonObject(), (p_185561_) -> {
                $$5.add("durationNanosPercentiles", p_185561_);
            });
            $$4.percentilesNanos().forEach((p_185584_, p_185585_) -> {
                $$6.addProperty("p" + p_185584_, p_185585_);
            });
            Function<ChunkGenStat, JsonElement> $$7 = (p_185538_) -> {
                JsonObject $$1 = new JsonObject();
                $$1.addProperty("durationNanos", p_185538_.duration().toNanos());
                $$1.addProperty("level", p_185538_.level());
                $$1.addProperty("chunkPosX", p_185538_.chunkPos().x);
                $$1.addProperty("chunkPosZ", p_185538_.chunkPos().z);
                $$1.addProperty("worldPosX", p_185538_.worldPos().x());
                $$1.addProperty("worldPosZ", p_185538_.worldPos().z());
                return $$1;
            };
            $$5.add("fastest", (JsonElement)$$7.apply((ChunkGenStat)$$4.fastest()));
            $$5.add("slowest", (JsonElement)$$7.apply((ChunkGenStat)$$4.slowest()));
            $$5.add("secondSlowest", (JsonElement)($$4.secondSlowest() != null ? (JsonElement)$$7.apply((ChunkGenStat)$$4.secondSlowest()) : JsonNull.INSTANCE));
        }

        return $$1;
    }

    private JsonElement threadAllocations(ThreadAllocationStat.Summary p_185546_) {
        JsonArray $$1 = new JsonArray();
        p_185546_.allocationsPerSecondByThread().forEach((p_185554_, p_185555_) -> {
            $$1.add((JsonElement)Util.make(new JsonObject(), (p_185571_) -> {
                p_185571_.addProperty("thread", p_185554_);
                p_185571_.addProperty("bytesPerSecond", p_185555_);
            }));
        });
        return $$1;
    }

    private JsonElement serverTicks(List<TickTimeStat> p_185587_) {
        if (p_185587_.isEmpty()) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject $$1 = new JsonObject();
            double[] $$2 = p_185587_.stream().mapToDouble((p_185548_) -> {
                return (double)p_185548_.currentAverage().toNanos() / 1000000.0;
            }).toArray();
            DoubleSummaryStatistics $$3 = DoubleStream.of($$2).summaryStatistics();
            $$1.addProperty("minMs", $$3.getMin());
            $$1.addProperty("averageMs", $$3.getAverage());
            $$1.addProperty("maxMs", $$3.getMax());
            Map<Integer, Double> $$4 = Percentiles.evaluate($$2);
            $$4.forEach((p_185564_, p_185565_) -> {
                $$1.addProperty("p" + p_185564_, p_185565_);
            });
            return $$1;
        }
    }

    private JsonElement fileIO(JfrStatsResult p_185578_) {
        JsonObject $$1 = new JsonObject();
        $$1.add("write", this.fileIoSummary(p_185578_.fileWrites()));
        $$1.add("read", this.fileIoSummary(p_185578_.fileReads()));
        return $$1;
    }

    private JsonElement fileIoSummary(FileIOStat.Summary p_185540_) {
        JsonObject $$1 = new JsonObject();
        $$1.addProperty("totalBytes", p_185540_.totalBytes());
        $$1.addProperty("count", p_185540_.counts());
        $$1.addProperty("bytesPerSecond", p_185540_.bytesPerSecond());
        $$1.addProperty("countPerSecond", p_185540_.countsPerSecond());
        JsonArray $$2 = new JsonArray();
        $$1.add("topContributors", $$2);
        p_185540_.topTenContributorsByTotalBytes().forEach((p_185581_) -> {
            JsonObject $$2x = new JsonObject();
            $$2.add($$2x);
            $$2x.addProperty("path", (String)p_185581_.getFirst());
            $$2x.addProperty("totalBytes", (Number)p_185581_.getSecond());
        });
        return $$1;
    }

    private JsonElement network(JfrStatsResult p_185589_) {
        JsonObject $$1 = new JsonObject();
        $$1.add("sent", this.packets(p_185589_.sentPacketsSummary()));
        $$1.add("received", this.packets(p_185589_.receivedPacketsSummary()));
        return $$1;
    }

    private JsonElement packets(NetworkPacketSummary p_185544_) {
        JsonObject $$1 = new JsonObject();
        $$1.addProperty("totalBytes", p_185544_.getTotalSize());
        $$1.addProperty("count", p_185544_.getTotalCount());
        $$1.addProperty("bytesPerSecond", p_185544_.getSizePerSecond());
        $$1.addProperty("countPerSecond", p_185544_.getCountsPerSecond());
        JsonArray $$2 = new JsonArray();
        $$1.add("topContributors", $$2);
        p_185544_.largestSizeContributors().forEach((p_185551_) -> {
            JsonObject $$2x = new JsonObject();
            $$2.add($$2x);
            NetworkPacketSummary.PacketIdentification $$3 = (NetworkPacketSummary.PacketIdentification)p_185551_.getFirst();
            NetworkPacketSummary.PacketCountAndSize $$4 = (NetworkPacketSummary.PacketCountAndSize)p_185551_.getSecond();
            $$2x.addProperty("protocolId", $$3.protocolId());
            $$2x.addProperty("packetId", $$3.packetId());
            $$2x.addProperty("packetName", $$3.packetName());
            $$2x.addProperty("totalBytes", $$4.totalSize());
            $$2x.addProperty("count", $$4.totalCount());
        });
        return $$1;
    }

    private JsonElement cpu(List<CpuLoadStat> p_185591_) {
        JsonObject $$1 = new JsonObject();
        BiFunction<List<CpuLoadStat>, ToDoubleFunction<CpuLoadStat>, JsonObject> $$2 = (p_185575_, p_185576_) -> {
            JsonObject $$2 = new JsonObject();
            DoubleSummaryStatistics $$3 = p_185575_.stream().mapToDouble(p_185576_).summaryStatistics();
            $$2.addProperty("min", $$3.getMin());
            $$2.addProperty("average", $$3.getAverage());
            $$2.addProperty("max", $$3.getMax());
            return $$2;
        };
        $$1.add("jvm", (JsonElement)$$2.apply(p_185591_, CpuLoadStat::jvm));
        $$1.add("userJvm", (JsonElement)$$2.apply(p_185591_, CpuLoadStat::userJvm));
        $$1.add("system", (JsonElement)$$2.apply(p_185591_, CpuLoadStat::system));
        return $$1;
    }
}

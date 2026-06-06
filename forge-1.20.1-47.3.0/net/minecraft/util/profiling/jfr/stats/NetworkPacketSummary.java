//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util.profiling.jfr.stats;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import jdk.jfr.consumer.RecordedEvent;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;

public final class NetworkPacketSummary {
    private final PacketCountAndSize totalPacketCountAndSize;
    private final List<Pair<PacketIdentification, PacketCountAndSize>> largestSizeContributors;
    private final Duration recordingDuration;

    public NetworkPacketSummary(Duration p_185738_, List<Pair<PacketIdentification, PacketCountAndSize>> p_185739_) {
        this.recordingDuration = p_185738_;
        this.totalPacketCountAndSize = (PacketCountAndSize)p_185739_.stream().map(Pair::getSecond).reduce(PacketCountAndSize::add).orElseGet(() -> {
            return new PacketCountAndSize(0L, 0L);
        });
        this.largestSizeContributors = p_185739_.stream().sorted(Comparator.comparing(Pair::getSecond, net.minecraft.util.profiling.jfr.stats.NetworkPacketSummary.PacketCountAndSize.SIZE_THEN_COUNT)).limit(10L).toList();
    }

    public double getCountsPerSecond() {
        return (double)this.totalPacketCountAndSize.totalCount / (double)this.recordingDuration.getSeconds();
    }

    public double getSizePerSecond() {
        return (double)this.totalPacketCountAndSize.totalSize / (double)this.recordingDuration.getSeconds();
    }

    public long getTotalCount() {
        return this.totalPacketCountAndSize.totalCount;
    }

    public long getTotalSize() {
        return this.totalPacketCountAndSize.totalSize;
    }

    public List<Pair<PacketIdentification, PacketCountAndSize>> largestSizeContributors() {
        return this.largestSizeContributors;
    }

    public static record PacketCountAndSize(long totalCount, long totalSize) {
        static final Comparator<PacketCountAndSize> SIZE_THEN_COUNT = Comparator.comparing(PacketCountAndSize::totalSize).thenComparing(PacketCountAndSize::totalCount).reversed();

        public PacketCountAndSize(long totalCount, long totalSize) {
            this.totalCount = totalCount;
            this.totalSize = totalSize;
        }

        PacketCountAndSize add(PacketCountAndSize p_185755_) {
            return new PacketCountAndSize(this.totalCount + p_185755_.totalCount, this.totalSize + p_185755_.totalSize);
        }

        public long totalCount() {
            return this.totalCount;
        }

        public long totalSize() {
            return this.totalSize;
        }
    }

    public static record PacketIdentification(PacketFlow direction, int protocolId, int packetId) {
        private static final Map<PacketIdentification, String> PACKET_NAME_BY_ID;

        public PacketIdentification(PacketFlow direction, int protocolId, int packetId) {
            this.direction = direction;
            this.protocolId = protocolId;
            this.packetId = packetId;
        }

        public String packetName() {
            return (String)PACKET_NAME_BY_ID.getOrDefault(this, "unknown");
        }

        public static PacketIdentification from(RecordedEvent p_185778_) {
            return new PacketIdentification(p_185778_.getEventType().getName().equals("minecraft.PacketSent") ? PacketFlow.CLIENTBOUND : PacketFlow.SERVERBOUND, p_185778_.getInt("protocolId"), p_185778_.getInt("packetId"));
        }

        public PacketFlow direction() {
            return this.direction;
        }

        public int protocolId() {
            return this.protocolId;
        }

        public int packetId() {
            return this.packetId;
        }

        static {
            ImmutableMap.Builder<PacketIdentification, String> $$0 = ImmutableMap.builder();
            ConnectionProtocol[] var1 = ConnectionProtocol.values();
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                ConnectionProtocol $$1 = var1[var3];
                PacketFlow[] var5 = PacketFlow.values();
                int var6 = var5.length;

                for(int var7 = 0; var7 < var6; ++var7) {
                    PacketFlow $$2 = var5[var7];
                    Int2ObjectMap<Class<? extends Packet<?>>> $$3 = $$1.getPacketsByIds($$2);
                    $$3.forEach((p_185775_, p_185776_) -> {
                        $$0.put(new PacketIdentification($$2, $$1.getId(), p_185775_), p_185776_.getSimpleName());
                    });
                }
            }

            PACKET_NAME_BY_ID = $$0.build();
        }
    }
}

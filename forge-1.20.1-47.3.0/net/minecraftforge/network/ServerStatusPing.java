//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModList;

public record ServerStatusPing(Map<ResourceLocation, ChannelData> channels, Map<String, String> mods, int fmlNetworkVer, boolean truncated) {
    private static final Codec<ByteBuf> BYTE_BUF_CODEC;
    public static final Codec<ServerStatusPing> CODEC;
    private static final int VERSION_FLAG_IGNORESERVERONLY = 1;

    public ServerStatusPing() {
        this(NetworkRegistry.buildChannelVersionsForListPing(), (Map)Util.make(new HashMap(), (map) -> {
            ModList.get().forEachModContainer((modid, mc) -> {
                map.put(modid, (String)mc.getCustomExtension(IExtensionPoint.DisplayTest.class).map(IExtensionPoint.DisplayTest::suppliedVersion).map(Supplier::get).orElse("OHNOES\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31"));
            });
        }), 3, false);
    }

    public ServerStatusPing(Map<ResourceLocation, ChannelData> channels, Map<String, String> mods, int fmlNetworkVer, boolean truncated) {
        this.channels = channels;
        this.mods = mods;
        this.fmlNetworkVer = fmlNetworkVer;
        this.truncated = truncated;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof ServerStatusPing)) {
            return false;
        } else {
            ServerStatusPing that = (ServerStatusPing)o;
            return this.fmlNetworkVer == that.fmlNetworkVer && this.channels.equals(that.channels) && this.mods.equals(that.mods);
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.channels, this.mods, this.fmlNetworkVer});
    }

    private List<Map.Entry<ResourceLocation, ChannelData>> getChannelsForMod(String modId) {
        return this.channels.entrySet().stream().filter((c) -> {
            return ((ResourceLocation)c.getKey()).getNamespace().equals(modId);
        }).toList();
    }

    private List<Map.Entry<ResourceLocation, ChannelData>> getNonModChannels() {
        return this.channels.entrySet().stream().filter((c) -> {
            return !this.mods.containsKey(((ResourceLocation)c.getKey()).getNamespace());
        }).toList();
    }

    public ByteBuf toBuf() {
        boolean reachedSizeLimit = false;
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeBoolean(false);
        buf.writeShort(this.mods.size());
        int writtenCount = 0;
        Iterator var4 = this.mods.entrySet().iterator();

        while(var4.hasNext()) {
            Map.Entry<String, String> modEntry = (Map.Entry)var4.next();
            boolean isIgnoreServerOnly = ((String)modEntry.getValue()).equals("OHNOES\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31");
            List<Map.Entry<ResourceLocation, ChannelData>> channelsForMod = this.getChannelsForMod((String)modEntry.getKey());
            int channelSizeAndVersionFlag = channelsForMod.size() << 1;
            if (isIgnoreServerOnly) {
                channelSizeAndVersionFlag |= 1;
            }

            buf.writeVarInt(channelSizeAndVersionFlag);
            buf.writeUtf((String)modEntry.getKey());
            if (!isIgnoreServerOnly) {
                buf.writeUtf((String)modEntry.getValue());
            }

            Iterator var9 = channelsForMod.iterator();

            while(var9.hasNext()) {
                Map.Entry<ResourceLocation, ChannelData> entry = (Map.Entry)var9.next();
                buf.writeUtf(((ResourceLocation)entry.getKey()).getPath());
                buf.writeUtf(((ChannelData)entry.getValue()).version());
                buf.writeBoolean(((ChannelData)entry.getValue()).required());
            }

            ++writtenCount;
            if (buf.readableBytes() >= 60000) {
                reachedSizeLimit = true;
                break;
            }
        }

        if (!reachedSizeLimit) {
            List<Map.Entry<ResourceLocation, ChannelData>> nonModChannels = this.getNonModChannels();
            buf.writeVarInt(nonModChannels.size());
            Iterator var12 = nonModChannels.iterator();

            while(var12.hasNext()) {
                Map.Entry<ResourceLocation, ChannelData> entry = (Map.Entry)var12.next();
                buf.writeResourceLocation((ResourceLocation)entry.getKey());
                buf.writeUtf(((ChannelData)entry.getValue()).version());
                buf.writeBoolean(((ChannelData)entry.getValue()).required());
            }
        } else {
            buf.setShort(1, writtenCount);
            buf.writeVarInt(0);
        }

        buf.setBoolean(0, reachedSizeLimit);
        return buf;
    }

    private static ServerStatusPing deserializeOptimized(int fmlNetworkVersion, ByteBuf bbuf) {
        FriendlyByteBuf buf = new FriendlyByteBuf(bbuf);

        boolean truncated;
        HashMap channels;
        HashMap mods;
        try {
            truncated = buf.readBoolean();
            int modsSize = buf.readUnsignedShort();
            mods = new HashMap();
            channels = new HashMap();

            int nonModChannelCount;
            int i;
            for(nonModChannelCount = 0; nonModChannelCount < modsSize; ++nonModChannelCount) {
                i = buf.readVarInt();
                int channelSize = i >>> 1;
                boolean isIgnoreServerOnly = (i & 1) != 0;
                String modId = buf.readUtf();
                String modVersion = isIgnoreServerOnly ? "OHNOES\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31" : buf.readUtf();

                for(int i1 = 0; i1 < channelSize; ++i1) {
                    String channelName = buf.readUtf();
                    String channelVersion = buf.readUtf();
                    boolean requiredOnClient = buf.readBoolean();
                    ResourceLocation id = new ResourceLocation(modId, channelName);
                    channels.put(id, new ChannelData(id, channelVersion, requiredOnClient));
                }

                mods.put(modId, modVersion);
            }

            nonModChannelCount = buf.readVarInt();

            for(i = 0; i < nonModChannelCount; ++i) {
                ResourceLocation channelName = buf.readResourceLocation();
                String channelVersion = buf.readUtf();
                boolean requiredOnClient = buf.readBoolean();
                channels.put(channelName, new ChannelData(channelName, channelVersion, requiredOnClient));
            }
        } finally {
            buf.release();
        }

        return new ServerStatusPing(channels, mods, fmlNetworkVersion, truncated);
    }

    private static String encodeOptimized(ByteBuf buf) {
        int byteLength = buf.readableBytes();
        StringBuilder sb = new StringBuilder();
        sb.append((char)(byteLength & 32767));
        sb.append((char)(byteLength >>> 15 & 32767));
        int buffer = 0;

        int bitsInBuf;
        for(bitsInBuf = 0; buf.isReadable(); bitsInBuf += 8) {
            short b;
            if (bitsInBuf >= 15) {
                b = (short)((char)(buffer & 32767));
                sb.append((char)b);
                buffer >>>= 15;
                bitsInBuf -= 15;
            }

            b = buf.readUnsignedByte();
            buffer |= b << bitsInBuf;
        }

        buf.release();
        if (bitsInBuf > 0) {
            char c = (char)(buffer & 32767);
            sb.append(c);
        }

        return sb.toString();
    }

    private static ByteBuf decodeOptimized(String s) {
        int size0 = s.charAt(0);
        int size1 = s.charAt(1);
        int size = size0 | size1 << 15;
        ByteBuf buf = Unpooled.buffer(size);
        int stringIndex = 2;
        int buffer = 0;

        int bitsInBuf;
        for(bitsInBuf = 0; stringIndex < s.length(); ++stringIndex) {
            while(bitsInBuf >= 8) {
                buf.writeByte(buffer);
                buffer >>>= 8;
                bitsInBuf -= 8;
            }

            char c = s.charAt(stringIndex);
            buffer |= (c & 32767) << bitsInBuf;
            bitsInBuf += 15;
        }

        while(buf.readableBytes() < size) {
            buf.writeByte(buffer);
            buffer >>>= 8;
            bitsInBuf -= 8;
        }

        return buf;
    }

    public Map<ResourceLocation, ChannelData> getRemoteChannels() {
        return this.channels;
    }

    public Map<String, String> getRemoteModData() {
        return this.mods;
    }

    public int getFMLNetworkVersion() {
        return this.fmlNetworkVer;
    }

    public boolean isTruncated() {
        return this.truncated;
    }

    public Map<ResourceLocation, ChannelData> channels() {
        return this.channels;
    }

    public Map<String, String> mods() {
        return this.mods;
    }

    public int fmlNetworkVer() {
        return this.fmlNetworkVer;
    }

    public boolean truncated() {
        return this.truncated;
    }

    static {
        BYTE_BUF_CODEC = Codec.STRING.xmap(ServerStatusPing::decodeOptimized, ServerStatusPing::encodeOptimized);
        CODEC = RecordCodecBuilder.create((in) -> {
            return in.group(Codec.INT.fieldOf("fmlNetworkVersion").forGetter(ServerStatusPing::getFMLNetworkVersion), BYTE_BUF_CODEC.optionalFieldOf("d").forGetter((ping) -> {
                return Optional.of(ping.toBuf());
            }), net.minecraftforge.network.ServerStatusPing.ChannelData.CODEC.listOf().optionalFieldOf("channels").forGetter((ping) -> {
                return Optional.of(List.of());
            }), net.minecraftforge.network.ServerStatusPing.ModInfo.CODEC.listOf().optionalFieldOf("mods").forGetter((ping) -> {
                return Optional.of(List.of());
            }), Codec.BOOL.optionalFieldOf("truncated").forGetter((ping) -> {
                return Optional.of(ping.isTruncated());
            })).apply(in, (fmlVer, buf, channels, mods, truncated) -> {
                return (ServerStatusPing)buf.map((byteBuf) -> {
                    return deserializeOptimized(fmlVer, byteBuf);
                }).orElseGet(() -> {
                    return new ServerStatusPing((Map)((List)channels.orElseGet(List::of)).stream().collect(Collectors.toMap(ChannelData::res, Function.identity())), (Map)((List)mods.orElseGet(List::of)).stream().collect(Collectors.toMap(ModInfo::modId, ModInfo::modmarker)), fmlVer, (Boolean)truncated.orElse(false));
                });
            });
        });
    }

    public static record ChannelData(ResourceLocation res, String version, boolean required) {
        public static final Codec<ChannelData> CODEC = RecordCodecBuilder.create((in) -> {
            return in.group(ResourceLocation.CODEC.fieldOf("res").forGetter(ChannelData::res), Codec.STRING.fieldOf("version").forGetter(ChannelData::version), Codec.BOOL.fieldOf("required").forGetter(ChannelData::required)).apply(in, ChannelData::new);
        });

        public ChannelData(ResourceLocation res, String version, boolean required) {
            this.res = res;
            this.version = version;
            this.required = required;
        }

        public ResourceLocation res() {
            return this.res;
        }

        public String version() {
            return this.version;
        }

        public boolean required() {
            return this.required;
        }
    }

    public static record ModInfo(String modId, String modmarker) {
        public static final Codec<ModInfo> CODEC = RecordCodecBuilder.create((in) -> {
            return in.group(Codec.STRING.fieldOf("modId").forGetter(ModInfo::modId), Codec.STRING.fieldOf("modmarker").forGetter(ModInfo::modmarker)).apply(in, ModInfo::new);
        });

        public ModInfo(String modId, String modmarker) {
            this.modId = modId;
            this.modmarker = modmarker;
        }

        public String modId() {
            return this.modId;
        }

        public String modmarker() {
            return this.modmarker;
        }
    }
}

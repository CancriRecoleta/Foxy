//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.network;

import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.registries.DataPackRegistriesHooks;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryManager;
import net.minecraftforge.registries.ForgeRegistry.Snapshot;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

public class HandshakeMessages {
    public HandshakeMessages() {
    }

    public static class S2CChannelMismatchData extends LoginIndexedMessage {
        private final Map<ResourceLocation, String> mismatchedChannelData;

        public S2CChannelMismatchData(Map<ResourceLocation, String> mismatchedChannelData) {
            this.mismatchedChannelData = mismatchedChannelData;
        }

        public static S2CChannelMismatchData decode(FriendlyByteBuf input) {
            Map<ResourceLocation, String> mismatchedMods = input.readMap((i) -> {
                return new ResourceLocation(i.readUtf(256));
            }, (i) -> {
                return i.readUtf(256);
            });
            return new S2CChannelMismatchData(mismatchedMods);
        }

        public void encode(FriendlyByteBuf output) {
            output.writeMap(this.mismatchedChannelData, (o, r) -> {
                o.writeUtf(r.toString(), 256);
            }, (o, v) -> {
                o.writeUtf(v, 256);
            });
        }

        public Map<ResourceLocation, String> getMismatchedChannelData() {
            return this.mismatchedChannelData;
        }
    }

    public static class S2CConfigData extends LoginIndexedMessage {
        private final String fileName;
        private final byte[] fileData;

        public S2CConfigData(String configFileName, byte[] configFileData) {
            this.fileName = configFileName;
            this.fileData = configFileData;
        }

        void encode(FriendlyByteBuf buffer) {
            buffer.writeUtf(this.fileName);
            buffer.writeByteArray(this.fileData);
        }

        public static S2CConfigData decode(FriendlyByteBuf buffer) {
            return new S2CConfigData(buffer.readUtf(32767), buffer.readByteArray());
        }

        public String getFileName() {
            return this.fileName;
        }

        public byte[] getBytes() {
            return this.fileData;
        }
    }

    public static class S2CRegistry extends LoginIndexedMessage {
        private ResourceLocation registryName;
        @Nullable
        private ForgeRegistry.@Nullable Snapshot snapshot;

        public S2CRegistry(ResourceLocation name, @Nullable ForgeRegistry.@Nullable Snapshot snapshot) {
            this.registryName = name;
            this.snapshot = snapshot;
        }

        void encode(FriendlyByteBuf buffer) {
            buffer.writeResourceLocation(this.registryName);
            buffer.writeBoolean(this.hasSnapshot());
            if (this.hasSnapshot()) {
                buffer.writeBytes((ByteBuf)this.snapshot.getPacketData());
            }

        }

        public static S2CRegistry decode(FriendlyByteBuf buffer) {
            ResourceLocation name = buffer.readResourceLocation();
            ForgeRegistry.Snapshot snapshot = null;
            if (buffer.readBoolean()) {
                snapshot = Snapshot.read(buffer);
            }

            return new S2CRegistry(name, snapshot);
        }

        public ResourceLocation getRegistryName() {
            return this.registryName;
        }

        public boolean hasSnapshot() {
            return this.snapshot != null;
        }

        @Nullable
        public ForgeRegistry.@Nullable Snapshot getSnapshot() {
            return this.snapshot;
        }
    }

    public static class C2SAcknowledge extends LoginIndexedMessage {
        public C2SAcknowledge() {
        }

        public void encode(FriendlyByteBuf buf) {
        }

        public static C2SAcknowledge decode(FriendlyByteBuf buf) {
            return new C2SAcknowledge();
        }
    }

    public static class C2SModListReply extends LoginIndexedMessage {
        private List<String> mods;
        private Map<ResourceLocation, String> channels;
        private Map<ResourceLocation, String> registries;

        public C2SModListReply() {
            this.mods = (List)ModList.get().getMods().stream().map(IModInfo::getModId).collect(Collectors.toList());
            this.channels = NetworkRegistry.buildChannelVersions();
            this.registries = Maps.newHashMap();
        }

        private C2SModListReply(List<String> mods, Map<ResourceLocation, String> channels, Map<ResourceLocation, String> registries) {
            this.mods = mods;
            this.channels = channels;
            this.registries = registries;
        }

        public static C2SModListReply decode(FriendlyByteBuf input) {
            List<String> mods = new ArrayList();
            int len = input.readVarInt();

            for(int x = 0; x < len; ++x) {
                mods.add(input.readUtf(256));
            }

            Map<ResourceLocation, String> channels = new HashMap();
            len = input.readVarInt();

            for(int x = 0; x < len; ++x) {
                channels.put(input.readResourceLocation(), input.readUtf(256));
            }

            Map<ResourceLocation, String> registries = new HashMap();
            len = input.readVarInt();

            for(int x = 0; x < len; ++x) {
                registries.put(input.readResourceLocation(), input.readUtf(256));
            }

            return new C2SModListReply(mods, channels, registries);
        }

        public void encode(FriendlyByteBuf output) {
            output.writeVarInt(this.mods.size());
            this.mods.forEach((m) -> {
                output.writeUtf(m, 256);
            });
            output.writeVarInt(this.channels.size());
            this.channels.forEach((k, v) -> {
                output.writeResourceLocation(k);
                output.writeUtf(v, 256);
            });
            output.writeVarInt(this.registries.size());
            this.registries.forEach((k, v) -> {
                output.writeResourceLocation(k);
                output.writeUtf(v, 256);
            });
        }

        public List<String> getModList() {
            return this.mods;
        }

        public Map<ResourceLocation, String> getRegistries() {
            return this.registries;
        }

        public Map<ResourceLocation, String> getChannels() {
            return this.channels;
        }
    }

    public static class S2CModData extends LoginIndexedMessage {
        private final Map<String, Pair<String, String>> mods;

        public S2CModData() {
            this.mods = (Map)ModList.get().getMods().stream().collect(Collectors.toMap(IModInfo::getModId, (info) -> {
                return Pair.of(info.getDisplayName(), info.getVersion().toString());
            }));
        }

        private S2CModData(Map<String, Pair<String, String>> mods) {
            this.mods = mods;
        }

        public static S2CModData decode(FriendlyByteBuf input) {
            Map<String, Pair<String, String>> mods = input.readMap((o) -> {
                return o.readUtf(256);
            }, (o) -> {
                return Pair.of(o.readUtf(256), o.readUtf(256));
            });
            return new S2CModData(mods);
        }

        public void encode(FriendlyByteBuf output) {
            output.writeMap(this.mods, (o, s) -> {
                o.writeUtf(s, 256);
            }, (o, p) -> {
                o.writeUtf((String)p.getLeft(), 256);
                o.writeUtf((String)p.getRight(), 256);
            });
        }

        public Map<String, Pair<String, String>> getMods() {
            return this.mods;
        }
    }

    public static class S2CModList extends LoginIndexedMessage {
        private List<String> mods;
        private Map<ResourceLocation, String> channels;
        private List<ResourceLocation> registries;
        private final List<ResourceKey<? extends Registry<?>>> dataPackRegistries;

        public S2CModList() {
            this.mods = (List)ModList.get().getMods().stream().map(IModInfo::getModId).collect(Collectors.toList());
            this.channels = NetworkRegistry.buildChannelVersions();
            this.registries = RegistryManager.getRegistryNamesForSyncToClient();
            this.dataPackRegistries = List.copyOf(DataPackRegistriesHooks.getSyncedCustomRegistries());
        }

        private S2CModList(List<String> mods, Map<ResourceLocation, String> channels, List<ResourceLocation> registries, List<ResourceKey<? extends Registry<?>>> dataPackRegistries) {
            this.mods = mods;
            this.channels = channels;
            this.registries = registries;
            this.dataPackRegistries = dataPackRegistries;
        }

        public static S2CModList decode(FriendlyByteBuf input) {
            List<String> mods = new ArrayList();
            int len = input.readVarInt();

            for(int x = 0; x < len; ++x) {
                mods.add(input.readUtf(256));
            }

            Map<ResourceLocation, String> channels = new HashMap();
            len = input.readVarInt();

            for(int x = 0; x < len; ++x) {
                channels.put(input.readResourceLocation(), input.readUtf(256));
            }

            List<ResourceLocation> registries = new ArrayList();
            len = input.readVarInt();

            for(int x = 0; x < len; ++x) {
                registries.add(input.readResourceLocation());
            }

            List<ResourceKey<? extends Registry<?>>> dataPackRegistries = (List)input.readCollection(ArrayList::new, (buf) -> {
                return ResourceKey.createRegistryKey(buf.readResourceLocation());
            });
            return new S2CModList(mods, channels, registries, dataPackRegistries);
        }

        public void encode(FriendlyByteBuf output) {
            output.writeVarInt(this.mods.size());
            this.mods.forEach((m) -> {
                output.writeUtf(m, 256);
            });
            output.writeVarInt(this.channels.size());
            this.channels.forEach((k, v) -> {
                output.writeResourceLocation(k);
                output.writeUtf(v, 256);
            });
            output.writeVarInt(this.registries.size());
            List var10000 = this.registries;
            Objects.requireNonNull(output);
            var10000.forEach(output::writeResourceLocation);
            Set<ResourceKey<? extends Registry<?>>> dataPackRegistries = DataPackRegistriesHooks.getSyncedCustomRegistries();
            output.writeCollection(dataPackRegistries, (buf, key) -> {
                buf.writeResourceLocation(key.location());
            });
        }

        public List<String> getModList() {
            return this.mods;
        }

        public List<ResourceLocation> getRegistries() {
            return this.registries;
        }

        public Map<ResourceLocation, String> getChannels() {
            return this.channels;
        }

        public List<ResourceKey<? extends Registry<?>>> getCustomDataPackRegistries() {
            return this.dataPackRegistries;
        }
    }

    static class LoginIndexedMessage implements IntSupplier {
        private int loginIndex;

        LoginIndexedMessage() {
        }

        void setLoginIndex(int loginIndex) {
            this.loginIndex = loginIndex;
        }

        int getLoginIndex() {
            return this.loginIndex;
        }

        public int getAsInt() {
            return this.getLoginIndex();
        }
    }
}

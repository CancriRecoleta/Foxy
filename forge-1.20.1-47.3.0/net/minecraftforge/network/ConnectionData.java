//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.network;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import org.apache.commons.lang3.tuple.Pair;

public class ConnectionData {
    private ImmutableMap<String, Pair<String, String>> modData;
    private ImmutableMap<ResourceLocation, String> channels;

    ConnectionData(Map<String, Pair<String, String>> modData, Map<ResourceLocation, String> channels) {
        this.modData = ImmutableMap.copyOf(modData);
        this.channels = ImmutableMap.copyOf(channels);
    }

    public ImmutableList<String> getModList() {
        return this.modData.keySet().asList();
    }

    public ImmutableMap<String, Pair<String, String>> getModData() {
        return this.modData;
    }

    public ImmutableMap<ResourceLocation, String> getChannels() {
        return this.channels;
    }

    public static record ModMismatchData(Map<ResourceLocation, String> mismatchedModData, Map<ResourceLocation, Pair<String, String>> presentModData, boolean mismatchedDataFromServer) {
        public ModMismatchData(Map<ResourceLocation, String> mismatchedModData, Map<ResourceLocation, Pair<String, String>> presentModData, boolean mismatchedDataFromServer) {
            this.mismatchedModData = mismatchedModData;
            this.presentModData = presentModData;
            this.mismatchedDataFromServer = mismatchedDataFromServer;
        }

        public static ModMismatchData channel(Map<ResourceLocation, String> mismatchedChannels, ConnectionData connectionData, boolean mismatchedDataFromServer) {
            Map<ResourceLocation, String> mismatchedChannelData = enhanceWithModVersion(mismatchedChannels, connectionData, mismatchedDataFromServer);
            Map<ResourceLocation, Pair<String, String>> presentChannelData = getPresentChannelData(mismatchedChannels.keySet(), connectionData, mismatchedDataFromServer);
            return new ModMismatchData(mismatchedChannelData, presentChannelData, mismatchedDataFromServer);
        }

        public static ModMismatchData registry(Multimap<ResourceLocation, ResourceLocation> mismatchedRegistryData, ConnectionData connectionData) {
            List<ResourceLocation> mismatchedRegistryMods = mismatchedRegistryData.values().stream().map(ResourceLocation::getNamespace).distinct().map((id) -> {
                return new ResourceLocation(id, "");
            }).toList();
            Map<ResourceLocation, String> mismatchedRegistryModData = (Map)mismatchedRegistryMods.stream().map((id) -> {
                return (Pair)ModList.get().getModContainerById(id.getNamespace()).map((modContainer) -> {
                    return Pair.of(id, modContainer.getModInfo().getVersion().toString());
                }).orElse(Pair.of(id, NetworkRegistry.ABSENT.version()));
            }).collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
            Map<ResourceLocation, Pair<String, String>> presentModData = getServerSidePresentModData(mismatchedRegistryModData.keySet(), connectionData);
            return new ModMismatchData(mismatchedRegistryModData, presentModData, false);
        }

        public boolean containsMismatches() {
            return this.mismatchedModData != null && !this.mismatchedModData.isEmpty();
        }

        private static Map<ResourceLocation, String> enhanceWithModVersion(Map<ResourceLocation, String> mismatchedChannels, ConnectionData connectionData, boolean mismatchedDataFromServer) {
            Map mismatchedModVersions;
            if (mismatchedDataFromServer) {
                mismatchedModVersions = connectionData != null ? (Map)connectionData.getModData().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, (e) -> {
                    return (String)((Pair)e.getValue()).getRight();
                })) : Map.of();
            } else {
                mismatchedModVersions = (Map)ModList.get().getMods().stream().map((info) -> {
                    return Pair.of(info.getModId(), info.getVersion().toString());
                }).collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
            }

            return (Map)mismatchedChannels.keySet().stream().map((channel) -> {
                return Pair.of(channel, ((String)mismatchedChannels.get(channel)).equals(NetworkRegistry.ABSENT.version()) ? NetworkRegistry.ABSENT.version() : (String)mismatchedModVersions.getOrDefault(channel.getNamespace(), NetworkRegistry.ABSENT.version()));
            }).collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
        }

        private static Map<ResourceLocation, Pair<String, String>> getPresentChannelData(Set<ResourceLocation> mismatchedChannelsFilter, ConnectionData connectionData, boolean mismatchedDataFromServer) {
            Object channelData;
            if (mismatchedDataFromServer) {
                channelData = NetworkRegistry.buildChannelVersions();
            } else {
                channelData = connectionData != null ? connectionData.getChannels() : Map.of();
            }

            Stream var10000 = ((Map)channelData).keySet().stream();
            Objects.requireNonNull(mismatchedChannelsFilter);
            return (Map)var10000.filter(mismatchedChannelsFilter::contains).map((id) -> {
                return getPresentModDataFromChannel(id, connectionData, mismatchedDataFromServer);
            }).collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
        }

        private static Pair<ResourceLocation, Pair<String, String>> getPresentModDataFromChannel(ResourceLocation channel, ConnectionData connectionData, boolean mismatchedDataFromServer) {
            if (mismatchedDataFromServer) {
                return (Pair)ModList.get().getModContainerById(channel.getNamespace()).map((modContainer) -> {
                    return Pair.of(channel, Pair.of(modContainer.getModInfo().getDisplayName(), modContainer.getModInfo().getVersion().toString()));
                }).orElse(Pair.of(channel, Pair.of(channel.getNamespace(), "")));
            } else {
                Map<String, Pair<String, String>> modData = connectionData != null ? connectionData.getModData() : Map.of();
                Pair<String, String> modDataFromChannel = (Pair)((Map)modData).getOrDefault(channel.getNamespace(), Pair.of(channel.getNamespace(), ""));
                return Pair.of(channel, ((String)modDataFromChannel.getLeft()).isEmpty() ? Pair.of(channel.getNamespace(), (String)modDataFromChannel.getRight()) : modDataFromChannel);
            }
        }

        private static Map<ResourceLocation, Pair<String, String>> getServerSidePresentModData(Set<ResourceLocation> mismatchedModsFilter, ConnectionData connectionData) {
            Map<String, Pair<String, String>> serverModData = connectionData != null ? connectionData.getModData() : Map.of();
            Set<String> modIdFilter = (Set)mismatchedModsFilter.stream().map(ResourceLocation::getNamespace).collect(Collectors.toSet());
            return (Map)((Map)serverModData).entrySet().stream().filter((e) -> {
                return modIdFilter.contains(e.getKey());
            }).collect(Collectors.toMap((e) -> {
                return new ResourceLocation((String)e.getKey(), "");
            }, Map.Entry::getValue));
        }

        public Map<ResourceLocation, String> mismatchedModData() {
            return this.mismatchedModData;
        }

        public Map<ResourceLocation, Pair<String, String>> presentModData() {
            return this.presentModData;
        }

        public boolean mismatchedDataFromServer() {
            return this.mismatchedDataFromServer;
        }
    }
}

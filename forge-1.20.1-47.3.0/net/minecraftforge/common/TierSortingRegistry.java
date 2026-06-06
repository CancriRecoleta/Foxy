//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.graph.ElementOrder;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.toposort.TopologicalSort;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TierSortingRegistry {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceLocation ITEM_TIER_ORDERING_JSON = new ResourceLocation("forge", "item_tier_ordering.json");
    private static boolean hasCustomTiers = false;
    private static final BiMap<ResourceLocation, Tier> tiers = HashBiMap.create();
    private static final Multimap<ResourceLocation, ResourceLocation> edges = HashMultimap.create();
    private static final Multimap<ResourceLocation, ResourceLocation> vanillaEdges = HashMultimap.create();
    private static final List<Tier> sortedTiers;
    private static final List<Tier> sortedTiersUnmodifiable;
    private static final ResourceLocation CHANNEL_NAME;
    private static final String PROTOCOL_VERSION = "1.0";
    private static final SimpleChannel SYNC_CHANNEL;

    public TierSortingRegistry() {
    }

    public static synchronized Tier registerTier(Tier tier, ResourceLocation name, List<Object> after, List<Object> before) {
        if (tiers.containsKey(name)) {
            throw new IllegalStateException("Duplicate tier name " + name);
        } else {
            processTier(tier, name, after, before);
            hasCustomTiers = true;
            return tier;
        }
    }

    public static List<Tier> getSortedTiers() {
        return sortedTiersUnmodifiable;
    }

    public static @Nullable Tier byName(ResourceLocation name) {
        return (Tier)tiers.get(name);
    }

    public static @Nullable ResourceLocation getName(Tier tier) {
        return (ResourceLocation)tiers.inverse().get(tier);
    }

    public static boolean isTierSorted(Tier tier) {
        return getName(tier) != null;
    }

    public static boolean isCorrectTierForDrops(Tier tier, BlockState state) {
        if (!isTierSorted(tier)) {
            return isCorrectTierVanilla(tier, state);
        } else {
            for(int x = sortedTiers.indexOf(tier) + 1; x < sortedTiers.size(); ++x) {
                TagKey<Block> tag = ((Tier)sortedTiers.get(x)).getTag();
                if (tag != null && state.is(tag)) {
                    return false;
                }
            }

            return true;
        }
    }

    public static List<Tier> getTiersLowerThan(Tier tier) {
        return !isTierSorted(tier) ? List.of() : sortedTiers.stream().takeWhile((t) -> {
            return t != tier;
        }).toList();
    }

    private static boolean isCorrectTierVanilla(Tier tier, BlockState state) {
        int i = tier.getLevel();
        if (i < 3 && state.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
            return false;
        } else if (i < 2 && state.is(BlockTags.NEEDS_IRON_TOOL)) {
            return false;
        } else {
            return i >= 1 || !state.is(BlockTags.NEEDS_STONE_TOOL);
        }
    }

    private static void processTier(Tier tier, ResourceLocation name, List<Object> afters, List<Object> befores) {
        tiers.put(name, tier);
        Iterator var4 = afters.iterator();

        Object before;
        ResourceLocation other;
        while(var4.hasNext()) {
            before = var4.next();
            other = getTierName(before);
            edges.put(other, name);
        }

        var4 = befores.iterator();

        while(var4.hasNext()) {
            before = var4.next();
            other = getTierName(before);
            edges.put(name, other);
        }

    }

    private static ResourceLocation getTierName(Object entry) {
        if (entry instanceof String s) {
            return new ResourceLocation(s);
        } else if (entry instanceof ResourceLocation rl) {
            return rl;
        } else if (entry instanceof Tier t) {
            return (ResourceLocation)Objects.requireNonNull(getName(t), "Can't have sorting dependencies for tiers not registered in the TierSortingRegistry");
        } else {
            throw new IllegalStateException("Invalid object type passed into the tier dependencies " + entry.getClass());
        }
    }

    static boolean allowVanilla() {
        return !hasCustomTiers;
    }

    static void init() {
        SYNC_CHANNEL.registerMessage(0, SyncPacket.class, SyncPacket::encode, TierSortingRegistry::receive, TierSortingRegistry::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        MinecraftForge.EVENT_BUS.addListener(TierSortingRegistry::playerLoggedIn);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            net.minecraftforge.common.TierSortingRegistry.ClientEvents.init();
        }

    }

    static PreparableReloadListener getReloadListener() {
        return new SimplePreparableReloadListener<JsonObject>() {
            final Gson gson = (new GsonBuilder()).create();

            protected @NotNull JsonObject prepare(@NotNull ResourceManager resourceManager, ProfilerFiller p) {
                Optional<Resource> res = resourceManager.getResource(TierSortingRegistry.ITEM_TIER_ORDERING_JSON);
                if (res.isEmpty()) {
                    return new JsonObject();
                } else {
                    try {
                        Reader reader = ((Resource)res.get()).openAsReader();

                        JsonObject var5;
                        try {
                            var5 = (JsonObject)this.gson.fromJson(reader, JsonObject.class);
                        } catch (Throwable var8) {
                            if (reader != null) {
                                try {
                                    reader.close();
                                } catch (Throwable var7) {
                                    var8.addSuppressed(var7);
                                }
                            }

                            throw var8;
                        }

                        if (reader != null) {
                            reader.close();
                        }

                        return var5;
                    } catch (IOException var9) {
                        IOException e = var9;
                        TierSortingRegistry.LOGGER.error("Could not read Tier sorting file " + TierSortingRegistry.ITEM_TIER_ORDERING_JSON, e);
                        return new JsonObject();
                    }
                }
            }

            protected void apply(@NotNull JsonObject data, @NotNull ResourceManager resourceManager, ProfilerFiller p) {
                try {
                    if (data.size() > 0) {
                        JsonArray order = GsonHelper.getAsJsonArray(data, "order");
                        List<Tier> customOrder = new ArrayList();
                        Iterator var6 = order.iterator();

                        while(var6.hasNext()) {
                            JsonElement entry = (JsonElement)var6.next();
                            ResourceLocation id = new ResourceLocation(entry.getAsString());
                            Tier tier = TierSortingRegistry.byName(id);
                            if (tier == null) {
                                throw new IllegalStateException("Tier not found with name " + id);
                            }

                            customOrder.add(tier);
                        }

                        List<Tier> missingTiers = TierSortingRegistry.tiers.values().stream().filter((tierx) -> {
                            return !customOrder.contains(tierx);
                        }).toList();
                        if (missingTiers.size() > 0) {
                            Stream var10002 = missingTiers.stream().map((tierx) -> {
                                return Objects.toString(TierSortingRegistry.getName(tierx));
                            });
                            throw new IllegalStateException("Tiers missing from the ordered list: " + (String)var10002.collect(Collectors.joining(", ")));
                        }

                        TierSortingRegistry.setTierOrder(customOrder);
                        return;
                    }
                } catch (Exception var10) {
                    Exception e = var10;
                    TierSortingRegistry.LOGGER.error("Error parsing Tier sorting file " + TierSortingRegistry.ITEM_TIER_ORDERING_JSON, e);
                }

                TierSortingRegistry.recalculateItemTiers();
            }
        };
    }

    private static void recalculateItemTiers() {
        MutableGraph<Tier> graph = GraphBuilder.directed().nodeOrder(ElementOrder.insertion()).build();
        Iterator var1 = tiers.values().iterator();

        while(var1.hasNext()) {
            Tier tier = (Tier)var1.next();
            graph.addNode(tier);
        }

        edges.forEach((key, value) -> {
            if (tiers.containsKey(key) && tiers.containsKey(value)) {
                graph.putEdge((Tier)tiers.get(key), (Tier)tiers.get(value));
            }

        });
        List<Tier> tierList = TopologicalSort.topologicalSort(graph, (Comparator)null);
        setTierOrder(tierList);
    }

    private static void setTierOrder(List<Tier> tierList) {
        runInServerThreadIfPossible((hasServer) -> {
            sortedTiers.clear();
            sortedTiers.addAll(tierList);
            if (hasServer) {
                syncToAll();
            }

        });
    }

    private static void runInServerThreadIfPossible(BooleanConsumer runnable) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            server.execute(() -> {
                runnable.accept(true);
            });
        } else {
            runnable.accept(false);
        }

    }

    private static void syncToAll() {
        Iterator var0 = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers().iterator();

        while(var0.hasNext()) {
            ServerPlayer serverPlayer = (ServerPlayer)var0.next();
            syncToPlayer(serverPlayer);
        }

    }

    private static void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player var2 = event.getEntity();
        if (var2 instanceof ServerPlayer serverPlayer) {
            syncToPlayer(serverPlayer);
        }

    }

    private static void syncToPlayer(ServerPlayer serverPlayer) {
        if (SYNC_CHANNEL.isRemotePresent(serverPlayer.connection.connection) && !serverPlayer.connection.connection.isMemoryConnection()) {
            SYNC_CHANNEL.send(PacketDistributor.PLAYER.with(() -> {
                return serverPlayer;
            }), new SyncPacket(sortedTiers.stream().map(TierSortingRegistry::getName).toList()));
        }

    }

    private static SyncPacket receive(FriendlyByteBuf buffer) {
        int count = buffer.readVarInt();
        List<ResourceLocation> list = new ArrayList();

        for(int i = 0; i < count; ++i) {
            list.add(buffer.readResourceLocation());
        }

        return new SyncPacket(list);
    }

    private static void handle(SyncPacket packet, Supplier<NetworkEvent.Context> context) {
        setTierOrder(packet.tiers.stream().map(TierSortingRegistry::byName).toList());
        ((NetworkEvent.Context)context.get()).setPacketHandled(true);
    }

    static {
        ResourceLocation wood = new ResourceLocation("wood");
        ResourceLocation stone = new ResourceLocation("stone");
        ResourceLocation iron = new ResourceLocation("iron");
        ResourceLocation diamond = new ResourceLocation("diamond");
        ResourceLocation netherite = new ResourceLocation("netherite");
        ResourceLocation gold = new ResourceLocation("gold");
        processTier(Tiers.WOOD, wood, List.of(), List.of());
        processTier(Tiers.GOLD, gold, List.of(wood), List.of(stone));
        processTier(Tiers.STONE, stone, List.of(wood), List.of(iron));
        processTier(Tiers.IRON, iron, List.of(stone), List.of(diamond));
        processTier(Tiers.DIAMOND, diamond, List.of(iron), List.of(netherite));
        processTier(Tiers.NETHERITE, netherite, List.of(diamond), List.of());
        vanillaEdges.putAll(edges);
        sortedTiers = new ArrayList();
        sortedTiersUnmodifiable = Collections.unmodifiableList(sortedTiers);
        CHANNEL_NAME = new ResourceLocation("forge:tier_sorting");
        SYNC_CHANNEL = NetworkRegistry.newSimpleChannel(CHANNEL_NAME, () -> {
            return "1.0";
        }, (versionFromServer) -> {
            return "1.0".equals(versionFromServer) || allowVanilla() && NetworkRegistry.ACCEPTVANILLA.equals(versionFromServer);
        }, (versionFromClient) -> {
            return "1.0".equals(versionFromClient) || allowVanilla() && NetworkRegistry.ACCEPTVANILLA.equals(versionFromClient);
        });
    }

    private static record SyncPacket(List<ResourceLocation> tiers) {
        private SyncPacket(List<ResourceLocation> tiers) {
            this.tiers = tiers;
        }

        private void encode(FriendlyByteBuf buffer) {
            buffer.writeVarInt(this.tiers.size());
            Iterator var2 = this.tiers.iterator();

            while(var2.hasNext()) {
                ResourceLocation loc = (ResourceLocation)var2.next();
                buffer.writeResourceLocation(loc);
            }

        }

        public List<ResourceLocation> tiers() {
            return this.tiers;
        }
    }

    private static class ClientEvents {
        private ClientEvents() {
        }

        public static void init() {
            MinecraftForge.EVENT_BUS.addListener(ClientEvents::clientLogInToServer);
        }

        private static void clientLogInToServer(ClientPlayerNetworkEvent.LoggingIn event) {
            if (event.getConnection() == null || !event.getConnection().isMemoryConnection()) {
                TierSortingRegistry.recalculateItemTiers();
            }

        }
    }
}

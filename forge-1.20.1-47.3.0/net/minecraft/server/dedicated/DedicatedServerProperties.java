//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.dedicated;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import org.slf4j.Logger;

public class DedicatedServerProperties extends Settings<DedicatedServerProperties> {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final Pattern SHA1 = Pattern.compile("^[a-fA-F0-9]{40}$");
    private static final Splitter COMMA_SPLITTER = Splitter.on(',').trimResults();
    public final boolean onlineMode = this.get("online-mode", true);
    public final boolean preventProxyConnections = this.get("prevent-proxy-connections", false);
    public final String serverIp = this.get("server-ip", "");
    public final boolean spawnAnimals = this.get("spawn-animals", true);
    public final boolean spawnNpcs = this.get("spawn-npcs", true);
    public final boolean pvp = this.get("pvp", true);
    public final boolean allowFlight = this.get("allow-flight", false);
    public final String motd = this.get("motd", "A Minecraft Server");
    public final boolean forceGameMode = this.get("force-gamemode", false);
    public final boolean enforceWhitelist = this.get("enforce-whitelist", false);
    public final Difficulty difficulty;
    public final GameType gamemode;
    public final String levelName;
    public final int serverPort;
    @Nullable
    public final Boolean announcePlayerAchievements;
    public final boolean enableQuery;
    public final int queryPort;
    public final boolean enableRcon;
    public final int rconPort;
    public final String rconPassword;
    public final boolean hardcore;
    public final boolean allowNether;
    public final boolean spawnMonsters;
    public final boolean useNativeTransport;
    public final boolean enableCommandBlock;
    public final int spawnProtection;
    public final int opPermissionLevel;
    public final int functionPermissionLevel;
    public final long maxTickTime;
    public final int maxChainedNeighborUpdates;
    public final int rateLimitPacketsPerSecond;
    public final int viewDistance;
    public final int simulationDistance;
    public final int maxPlayers;
    public final int networkCompressionThreshold;
    public final boolean broadcastRconToOps;
    public final boolean broadcastConsoleToOps;
    public final int maxWorldSize;
    public final boolean syncChunkWrites;
    public final boolean enableJmxMonitoring;
    public final boolean enableStatus;
    public final boolean hideOnlinePlayers;
    public final int entityBroadcastRangePercentage;
    public final String textFilteringConfig;
    public final Optional<MinecraftServer.ServerResourcePackInfo> serverResourcePackInfo;
    public final DataPackConfig initialDataPackConfiguration;
    public final Settings<DedicatedServerProperties>.MutableValue<Integer> playerIdleTimeout;
    public final Settings<DedicatedServerProperties>.MutableValue<Boolean> whiteList;
    public final boolean enforceSecureProfile;
    private final WorldDimensionData worldDimensionData;
    public final WorldOptions worldOptions;

    public DedicatedServerProperties(Properties p_180926_) {
        super(p_180926_);
        this.difficulty = (Difficulty)this.get("difficulty", dispatchNumberOrString(Difficulty::byId, Difficulty::byName), Difficulty::getKey, Difficulty.EASY);
        this.gamemode = (GameType)this.get("gamemode", dispatchNumberOrString(GameType::byId, GameType::byName), GameType::getName, GameType.SURVIVAL);
        this.levelName = this.get("level-name", "world");
        this.serverPort = this.get("server-port", 25565);
        this.announcePlayerAchievements = this.getLegacyBoolean("announce-player-achievements");
        this.enableQuery = this.get("enable-query", false);
        this.queryPort = this.get("query.port", 25565);
        this.enableRcon = this.get("enable-rcon", false);
        this.rconPort = this.get("rcon.port", 25575);
        this.rconPassword = this.get("rcon.password", "");
        this.hardcore = this.get("hardcore", false);
        this.allowNether = this.get("allow-nether", true);
        this.spawnMonsters = this.get("spawn-monsters", true);
        this.useNativeTransport = this.get("use-native-transport", true);
        this.enableCommandBlock = this.get("enable-command-block", false);
        this.spawnProtection = this.get("spawn-protection", 16);
        this.opPermissionLevel = this.get("op-permission-level", 4);
        this.functionPermissionLevel = this.get("function-permission-level", 2);
        this.maxTickTime = this.get("max-tick-time", TimeUnit.MINUTES.toMillis(1L));
        this.maxChainedNeighborUpdates = this.get("max-chained-neighbor-updates", 1000000);
        this.rateLimitPacketsPerSecond = this.get("rate-limit", 0);
        this.viewDistance = this.get("view-distance", 10);
        this.simulationDistance = this.get("simulation-distance", 10);
        this.maxPlayers = this.get("max-players", 20);
        this.networkCompressionThreshold = this.get("network-compression-threshold", 256);
        this.broadcastRconToOps = this.get("broadcast-rcon-to-ops", true);
        this.broadcastConsoleToOps = this.get("broadcast-console-to-ops", true);
        this.maxWorldSize = this.get("max-world-size", (p_139771_) -> {
            return Mth.clamp(p_139771_, 1, 29999984);
        }, 29999984);
        this.syncChunkWrites = this.get("sync-chunk-writes", true);
        this.enableJmxMonitoring = this.get("enable-jmx-monitoring", false);
        this.enableStatus = this.get("enable-status", true);
        this.hideOnlinePlayers = this.get("hide-online-players", false);
        this.entityBroadcastRangePercentage = this.get("entity-broadcast-range-percentage", (p_139769_) -> {
            return Mth.clamp(p_139769_, 10, 1000);
        }, 100);
        this.textFilteringConfig = this.get("text-filtering-config", "");
        this.playerIdleTimeout = this.getMutable("player-idle-timeout", 0);
        this.whiteList = this.getMutable("white-list", false);
        this.enforceSecureProfile = this.get("enforce-secure-profile", true);
        String $$1 = this.get("level-seed", "");
        boolean $$2 = this.get("generate-structures", true);
        long $$3 = WorldOptions.parseSeed($$1).orElse(WorldOptions.randomSeed());
        this.worldOptions = new WorldOptions($$3, $$2, false);
        this.worldDimensionData = new WorldDimensionData((JsonObject)this.get("generator-settings", (p_211543_) -> {
            return GsonHelper.parse(!p_211543_.isEmpty() ? p_211543_ : "{}");
        }, new JsonObject()), (String)this.get("level-type", (p_211541_) -> {
            return p_211541_.toLowerCase(Locale.ROOT);
        }, WorldPresets.NORMAL.location().toString()));
        this.serverResourcePackInfo = getServerPackInfo(this.get("resource-pack", ""), this.get("resource-pack-sha1", ""), this.getLegacyString("resource-pack-hash"), this.get("require-resource-pack", false), this.get("resource-pack-prompt", ""));
        this.initialDataPackConfiguration = getDatapackConfig(this.get("initial-enabled-packs", String.join(",", WorldDataConfiguration.DEFAULT.dataPacks().getEnabled())), this.get("initial-disabled-packs", String.join(",", WorldDataConfiguration.DEFAULT.dataPacks().getDisabled())));
    }

    public static DedicatedServerProperties fromFile(Path p_180930_) {
        return new DedicatedServerProperties(loadFromFile(p_180930_));
    }

    protected DedicatedServerProperties reload(RegistryAccess p_139761_, Properties p_139762_) {
        return new DedicatedServerProperties(p_139762_);
    }

    @Nullable
    private static Component parseResourcePackPrompt(String p_214815_) {
        if (!Strings.isNullOrEmpty(p_214815_)) {
            try {
                return Serializer.fromJson(p_214815_);
            } catch (Exception var2) {
                Exception $$1 = var2;
                LOGGER.warn("Failed to parse resource pack prompt '{}'", p_214815_, $$1);
            }
        }

        return null;
    }

    private static Optional<MinecraftServer.ServerResourcePackInfo> getServerPackInfo(String p_214809_, String p_214810_, @Nullable String p_214811_, boolean p_214812_, String p_214813_) {
        if (p_214809_.isEmpty()) {
            return Optional.empty();
        } else {
            String $$7;
            if (!p_214810_.isEmpty()) {
                $$7 = p_214810_;
                if (!Strings.isNullOrEmpty(p_214811_)) {
                    LOGGER.warn("resource-pack-hash is deprecated and found along side resource-pack-sha1. resource-pack-hash will be ignored.");
                }
            } else if (!Strings.isNullOrEmpty(p_214811_)) {
                LOGGER.warn("resource-pack-hash is deprecated. Please use resource-pack-sha1 instead.");
                $$7 = p_214811_;
            } else {
                $$7 = "";
            }

            if ($$7.isEmpty()) {
                LOGGER.warn("You specified a resource pack without providing a sha1 hash. Pack will be updated on the client only if you change the name of the pack.");
            } else if (!SHA1.matcher($$7).matches()) {
                LOGGER.warn("Invalid sha1 for resource-pack-sha1");
            }

            Component $$8 = parseResourcePackPrompt(p_214813_);
            return Optional.of(new MinecraftServer.ServerResourcePackInfo(p_214809_, $$7, p_214812_, $$8));
        }
    }

    private static DataPackConfig getDatapackConfig(String p_251757_, String p_249979_) {
        List<String> $$2 = COMMA_SPLITTER.splitToList(p_251757_);
        List<String> $$3 = COMMA_SPLITTER.splitToList(p_249979_);
        return new DataPackConfig($$2, $$3);
    }

    private static FeatureFlagSet getFeatures(String p_251025_) {
        return FeatureFlags.REGISTRY.fromNames((Iterable)COMMA_SPLITTER.splitToStream(p_251025_).mapMulti((p_248197_, p_248198_) -> {
            ResourceLocation $$2 = ResourceLocation.tryParse(p_248197_);
            if ($$2 == null) {
                LOGGER.warn("Invalid resource location {}, ignoring", p_248197_);
            } else {
                p_248198_.accept($$2);
            }

        }).collect(Collectors.toList()));
    }

    public WorldDimensions createDimensions(RegistryAccess p_250466_) {
        return this.worldDimensionData.create(p_250466_);
    }

    static record WorldDimensionData(JsonObject generatorSettings, String levelType) {
        private static final Map<String, ResourceKey<WorldPreset>> LEGACY_PRESET_NAMES;

        WorldDimensionData(JsonObject generatorSettings, String levelType) {
            this.generatorSettings = generatorSettings;
            this.levelType = levelType;
        }

        public WorldDimensions create(RegistryAccess p_248812_) {
            Registry<WorldPreset> $$1 = p_248812_.registryOrThrow(Registries.WORLD_PRESET);
            Holder.Reference<WorldPreset> $$2 = (Holder.Reference)$$1.getHolder(WorldPresets.NORMAL).or(() -> {
                return $$1.holders().findAny();
            }).orElseThrow(() -> {
                return new IllegalStateException("Invalid datapack contents: can't find default preset");
            });
            Optional var10000 = Optional.ofNullable(ResourceLocation.tryParse(this.levelType)).map((p_258240_) -> {
                return ResourceKey.create(Registries.WORLD_PRESET, p_258240_);
            }).or(() -> {
                return Optional.ofNullable((ResourceKey)LEGACY_PRESET_NAMES.get(this.levelType));
            });
            Objects.requireNonNull($$1);
            Holder<WorldPreset> $$3 = (Holder)var10000.flatMap($$1::getHolder).orElseGet(() -> {
                DedicatedServerProperties.LOGGER.warn("Failed to parse level-type {}, defaulting to {}", this.levelType, $$2.key().location());
                return $$2;
            });
            WorldDimensions $$4 = ((WorldPreset)$$3.value()).createWorldDimensions();
            if ($$3.is(WorldPresets.FLAT)) {
                RegistryOps<JsonElement> $$5 = RegistryOps.create(JsonOps.INSTANCE, (HolderLookup.Provider)p_248812_);
                DataResult var8 = FlatLevelGeneratorSettings.CODEC.parse(new Dynamic($$5, this.generatorSettings()));
                Logger var10001 = DedicatedServerProperties.LOGGER;
                Objects.requireNonNull(var10001);
                Optional<FlatLevelGeneratorSettings> $$6 = var8.resultOrPartial(var10001::error);
                if ($$6.isPresent()) {
                    return $$4.replaceOverworldGenerator(p_248812_, new FlatLevelSource((FlatLevelGeneratorSettings)$$6.get()));
                }
            }

            return $$4;
        }

        public JsonObject generatorSettings() {
            return this.generatorSettings;
        }

        public String levelType() {
            return this.levelType;
        }

        static {
            LEGACY_PRESET_NAMES = Map.of("default", WorldPresets.NORMAL, "largebiomes", WorldPresets.LARGE_BIOMES);
        }
    }
}

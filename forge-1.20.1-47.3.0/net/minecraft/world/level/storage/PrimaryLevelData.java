//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.storage;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.CrashReportCategory;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.border.WorldBorder.Settings;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.dimension.end.EndDragonFight.Data;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.timers.TimerCallbacks;
import net.minecraft.world.level.timers.TimerQueue;
import net.minecraftforge.common.ForgeHooks;
import org.slf4j.Logger;

public class PrimaryLevelData implements ServerLevelData, WorldData {
    private static final Logger LOGGER = LogUtils.getLogger();
    protected static final String PLAYER = "Player";
    protected static final String WORLD_GEN_SETTINGS = "WorldGenSettings";
    private LevelSettings settings;
    private final WorldOptions worldOptions;
    private final SpecialWorldProperty specialWorldProperty;
    private final Lifecycle worldGenSettingsLifecycle;
    private int xSpawn;
    private int ySpawn;
    private int zSpawn;
    private float spawnAngle;
    private long gameTime;
    private long dayTime;
    @Nullable
    private final DataFixer fixerUpper;
    private final int playerDataVersion;
    private boolean upgradedPlayerTag;
    @Nullable
    private CompoundTag loadedPlayerTag;
    private final int version;
    private int clearWeatherTime;
    private boolean raining;
    private int rainTime;
    private boolean thundering;
    private int thunderTime;
    private boolean initialized;
    private boolean difficultyLocked;
    private WorldBorder.Settings worldBorder;
    private EndDragonFight.Data endDragonFightData;
    @Nullable
    private CompoundTag customBossEvents;
    private int wanderingTraderSpawnDelay;
    private int wanderingTraderSpawnChance;
    @Nullable
    private UUID wanderingTraderId;
    private final Set<String> knownServerBrands;
    private boolean wasModded;
    private final Set<String> removedFeatureFlags;
    private final TimerQueue<MinecraftServer> scheduledEvents;
    private boolean confirmedExperimentalWarning;

    private PrimaryLevelData(@Nullable DataFixer p_277859_, int p_277672_, @Nullable CompoundTag p_277888_, boolean p_278109_, int p_277714_, int p_278088_, int p_278037_, float p_277542_, long p_277414_, long p_277635_, int p_277595_, int p_277794_, int p_278007_, boolean p_277943_, int p_277674_, boolean p_277644_, boolean p_277749_, boolean p_278004_, WorldBorder.Settings p_277729_, int p_277856_, int p_278051_, @Nullable UUID p_277341_, Set<String> p_277989_, Set<String> p_277399_, TimerQueue<MinecraftServer> p_277860_, @Nullable CompoundTag p_277936_, EndDragonFight.Data p_289764_, LevelSettings p_278064_, WorldOptions p_278072_, SpecialWorldProperty p_277548_, Lifecycle p_277915_) {
        this.confirmedExperimentalWarning = false;
        this.fixerUpper = p_277859_;
        this.wasModded = p_278109_;
        this.xSpawn = p_277714_;
        this.ySpawn = p_278088_;
        this.zSpawn = p_278037_;
        this.spawnAngle = p_277542_;
        this.gameTime = p_277414_;
        this.dayTime = p_277635_;
        this.version = p_277595_;
        this.clearWeatherTime = p_277794_;
        this.rainTime = p_278007_;
        this.raining = p_277943_;
        this.thunderTime = p_277674_;
        this.thundering = p_277644_;
        this.initialized = p_277749_;
        this.difficultyLocked = p_278004_;
        this.worldBorder = p_277729_;
        this.wanderingTraderSpawnDelay = p_277856_;
        this.wanderingTraderSpawnChance = p_278051_;
        this.wanderingTraderId = p_277341_;
        this.knownServerBrands = p_277989_;
        this.removedFeatureFlags = p_277399_;
        this.loadedPlayerTag = p_277888_;
        this.playerDataVersion = p_277672_;
        this.scheduledEvents = p_277860_;
        this.customBossEvents = p_277936_;
        this.endDragonFightData = p_289764_;
        this.settings = p_278064_;
        this.worldOptions = p_278072_;
        this.specialWorldProperty = p_277548_;
        this.worldGenSettingsLifecycle = p_277915_;
    }

    public PrimaryLevelData(LevelSettings p_251081_, WorldOptions p_251666_, SpecialWorldProperty p_252268_, Lifecycle p_251714_) {
        this((DataFixer)null, SharedConstants.getCurrentVersion().getDataVersion().getVersion(), (CompoundTag)null, false, 0, 0, 0, 0.0F, 0L, 0L, 19133, 0, 0, false, 0, false, false, false, WorldBorder.DEFAULT_SETTINGS, 0, 0, (UUID)null, Sets.newLinkedHashSet(), new HashSet(), new TimerQueue(TimerCallbacks.SERVER_CALLBACKS), (CompoundTag)null, Data.DEFAULT, p_251081_.copy(), p_251666_, p_252268_, p_251714_);
    }

    public static <T> PrimaryLevelData parse(Dynamic<T> p_78531_, DataFixer p_78532_, int p_78533_, @Nullable CompoundTag p_78534_, LevelSettings p_78535_, LevelVersion p_78536_, SpecialWorldProperty p_250651_, WorldOptions p_251864_, Lifecycle p_78538_) {
        long i = p_78531_.get("Time").asLong(0L);
        boolean var10005 = p_78531_.get("WasModded").asBoolean(false);
        int var10006 = p_78531_.get("SpawnX").asInt(0);
        int var10007 = p_78531_.get("SpawnY").asInt(0);
        int var10008 = p_78531_.get("SpawnZ").asInt(0);
        float var10009 = p_78531_.get("SpawnAngle").asFloat(0.0F);
        long var10011 = p_78531_.get("DayTime").asLong(i);
        int var10012 = p_78536_.levelDataVersion();
        int var10013 = p_78531_.get("clearWeatherTime").asInt(0);
        int var10014 = p_78531_.get("rainTime").asInt(0);
        boolean var10015 = p_78531_.get("raining").asBoolean(false);
        int var10016 = p_78531_.get("thunderTime").asInt(0);
        boolean var10017 = p_78531_.get("thundering").asBoolean(false);
        boolean var10018 = p_78531_.get("initialized").asBoolean(true);
        boolean var10019 = p_78531_.get("DifficultyLocked").asBoolean(false);
        WorldBorder.Settings var10020 = Settings.read(p_78531_, WorldBorder.DEFAULT_SETTINGS);
        int var10021 = p_78531_.get("WanderingTraderSpawnDelay").asInt(0);
        int var10022 = p_78531_.get("WanderingTraderSpawnChance").asInt(0);
        UUID var10023 = (UUID)p_78531_.get("WanderingTraderId").read(UUIDUtil.CODEC).result().orElse((UUID)null);
        Set var10024 = (Set)p_78531_.get("ServerBrands").asStream().flatMap((p_78529_) -> {
            return p_78529_.asString().result().stream();
        }).collect(Collectors.toCollection(Sets::newLinkedHashSet));
        Set var10025 = (Set)p_78531_.get("removed_features").asStream().flatMap((p_277335_) -> {
            return p_277335_.asString().result().stream();
        }).collect(Collectors.toSet());
        TimerQueue var10026 = new TimerQueue(TimerCallbacks.SERVER_CALLBACKS, p_78531_.get("ScheduledEvents").asStream());
        CompoundTag var10027 = (CompoundTag)p_78531_.get("CustomBossEvents").orElseEmptyMap().getValue();
        DataResult var10028 = p_78531_.get("DragonFight").read(Data.CODEC);
        Logger var10029 = LOGGER;
        Objects.requireNonNull(var10029);
        return (new PrimaryLevelData(p_78532_, p_78533_, p_78534_, var10005, var10006, var10007, var10008, var10009, i, var10011, var10012, var10013, var10014, var10015, var10016, var10017, var10018, var10019, var10020, var10021, var10022, var10023, var10024, var10025, var10026, var10027, (EndDragonFight.Data)var10028.resultOrPartial(var10029::error).orElse(Data.DEFAULT), p_78535_, p_251864_, p_250651_, p_78538_)).withConfirmedWarning(p_78538_ != Lifecycle.stable() && p_78531_.get("confirmedExperimentalSettings").asBoolean(false));
    }

    public CompoundTag createTag(RegistryAccess p_78543_, @Nullable CompoundTag p_78544_) {
        this.updatePlayerTag();
        if (p_78544_ == null) {
            p_78544_ = this.loadedPlayerTag;
        }

        CompoundTag compoundtag = new CompoundTag();
        this.setTagData(p_78543_, compoundtag, p_78544_);
        return compoundtag;
    }

    private void setTagData(RegistryAccess p_78546_, CompoundTag p_78547_, @Nullable CompoundTag p_78548_) {
        p_78547_.put("ServerBrands", stringCollectionToTag(this.knownServerBrands));
        p_78547_.putBoolean("WasModded", this.wasModded);
        if (!this.removedFeatureFlags.isEmpty()) {
            p_78547_.put("removed_features", stringCollectionToTag(this.removedFeatureFlags));
        }

        CompoundTag compoundtag = new CompoundTag();
        compoundtag.putString("Name", SharedConstants.getCurrentVersion().getName());
        compoundtag.putInt("Id", SharedConstants.getCurrentVersion().getDataVersion().getVersion());
        compoundtag.putBoolean("Snapshot", !SharedConstants.getCurrentVersion().isStable());
        compoundtag.putString("Series", SharedConstants.getCurrentVersion().getDataVersion().getSeries());
        p_78547_.put("Version", compoundtag);
        NbtUtils.addCurrentDataVersion(p_78547_);
        DynamicOps<Tag> dynamicops = RegistryOps.create(NbtOps.INSTANCE, (HolderLookup.Provider)p_78546_);
        DataResult var10000 = WorldGenSettings.encode(dynamicops, this.worldOptions, (RegistryAccess)p_78546_);
        Logger var10002 = LOGGER;
        Objects.requireNonNull(var10002);
        var10000.resultOrPartial(Util.prefix("WorldGenSettings: ", var10002::error)).ifPresent((p_78574_) -> {
            p_78547_.put("WorldGenSettings", p_78574_);
        });
        p_78547_.putInt("GameType", this.settings.gameType().getId());
        p_78547_.putInt("SpawnX", this.xSpawn);
        p_78547_.putInt("SpawnY", this.ySpawn);
        p_78547_.putInt("SpawnZ", this.zSpawn);
        p_78547_.putFloat("SpawnAngle", this.spawnAngle);
        p_78547_.putLong("Time", this.gameTime);
        p_78547_.putLong("DayTime", this.dayTime);
        p_78547_.putLong("LastPlayed", Util.getEpochMillis());
        p_78547_.putString("LevelName", this.settings.levelName());
        p_78547_.putInt("version", 19133);
        p_78547_.putInt("clearWeatherTime", this.clearWeatherTime);
        p_78547_.putInt("rainTime", this.rainTime);
        p_78547_.putBoolean("raining", this.raining);
        p_78547_.putInt("thunderTime", this.thunderTime);
        p_78547_.putBoolean("thundering", this.thundering);
        p_78547_.putBoolean("hardcore", this.settings.hardcore());
        p_78547_.putBoolean("allowCommands", this.settings.allowCommands());
        p_78547_.putBoolean("initialized", this.initialized);
        this.worldBorder.write(p_78547_);
        p_78547_.putByte("Difficulty", (byte)this.settings.difficulty().getId());
        p_78547_.putBoolean("DifficultyLocked", this.difficultyLocked);
        p_78547_.put("GameRules", this.settings.gameRules().createTag());
        p_78547_.put("DragonFight", (Tag)Util.getOrThrow(Data.CODEC.encodeStart(NbtOps.INSTANCE, this.endDragonFightData), IllegalStateException::new));
        if (p_78548_ != null) {
            p_78547_.put("Player", p_78548_);
        }

        DataResult<Tag> dataresult = WorldDataConfiguration.CODEC.encodeStart(NbtOps.INSTANCE, this.settings.getDataConfiguration());
        dataresult.get().ifLeft((p_248505_) -> {
            p_78547_.merge((CompoundTag)p_248505_);
        }).ifRight((p_248506_) -> {
            LOGGER.warn("Failed to encode configuration {}", p_248506_.message());
        });
        if (this.customBossEvents != null) {
            p_78547_.put("CustomBossEvents", this.customBossEvents);
        }

        p_78547_.put("ScheduledEvents", this.scheduledEvents.store());
        p_78547_.putInt("WanderingTraderSpawnDelay", this.wanderingTraderSpawnDelay);
        p_78547_.putInt("WanderingTraderSpawnChance", this.wanderingTraderSpawnChance);
        if (this.wanderingTraderId != null) {
            p_78547_.putUUID("WanderingTraderId", this.wanderingTraderId);
        }

        p_78547_.putString("forgeLifecycle", ForgeHooks.encodeLifecycle(this.settings.getLifecycle()));
        p_78547_.putBoolean("confirmedExperimentalSettings", this.confirmedExperimentalWarning);
    }

    private static ListTag stringCollectionToTag(Set<String> p_277880_) {
        ListTag listtag = new ListTag();
        Stream var10000 = p_277880_.stream().map(StringTag::valueOf);
        Objects.requireNonNull(listtag);
        var10000.forEach(listtag::add);
        return listtag;
    }

    public int getXSpawn() {
        return this.xSpawn;
    }

    public int getYSpawn() {
        return this.ySpawn;
    }

    public int getZSpawn() {
        return this.zSpawn;
    }

    public float getSpawnAngle() {
        return this.spawnAngle;
    }

    public long getGameTime() {
        return this.gameTime;
    }

    public long getDayTime() {
        return this.dayTime;
    }

    private void updatePlayerTag() {
        if (!this.upgradedPlayerTag && this.loadedPlayerTag != null) {
            if (this.playerDataVersion < SharedConstants.getCurrentVersion().getDataVersion().getVersion()) {
                if (this.fixerUpper == null) {
                    throw (NullPointerException)Util.pauseInIde(new NullPointerException("Fixer Upper not set inside LevelData, and the player tag is not upgraded."));
                }

                this.loadedPlayerTag = DataFixTypes.PLAYER.updateToCurrentVersion(this.fixerUpper, this.loadedPlayerTag, this.playerDataVersion);
            }

            this.upgradedPlayerTag = true;
        }

    }

    public CompoundTag getLoadedPlayerTag() {
        this.updatePlayerTag();
        return this.loadedPlayerTag;
    }

    public void setXSpawn(int p_78565_) {
        this.xSpawn = p_78565_;
    }

    public void setYSpawn(int p_78579_) {
        this.ySpawn = p_78579_;
    }

    public void setZSpawn(int p_78584_) {
        this.zSpawn = p_78584_;
    }

    public void setSpawnAngle(float p_78515_) {
        this.spawnAngle = p_78515_;
    }

    public void setGameTime(long p_78519_) {
        this.gameTime = p_78519_;
    }

    public void setDayTime(long p_78567_) {
        this.dayTime = p_78567_;
    }

    public void setSpawn(BlockPos p_78540_, float p_78541_) {
        this.xSpawn = p_78540_.getX();
        this.ySpawn = p_78540_.getY();
        this.zSpawn = p_78540_.getZ();
        this.spawnAngle = p_78541_;
    }

    public String getLevelName() {
        return this.settings.levelName();
    }

    public int getVersion() {
        return this.version;
    }

    public int getClearWeatherTime() {
        return this.clearWeatherTime;
    }

    public void setClearWeatherTime(int p_78517_) {
        this.clearWeatherTime = p_78517_;
    }

    public boolean isThundering() {
        return this.thundering;
    }

    public void setThundering(boolean p_78562_) {
        this.thundering = p_78562_;
    }

    public int getThunderTime() {
        return this.thunderTime;
    }

    public void setThunderTime(int p_78589_) {
        this.thunderTime = p_78589_;
    }

    public boolean isRaining() {
        return this.raining;
    }

    public void setRaining(boolean p_78576_) {
        this.raining = p_78576_;
    }

    public int getRainTime() {
        return this.rainTime;
    }

    public void setRainTime(int p_78592_) {
        this.rainTime = p_78592_;
    }

    public GameType getGameType() {
        return this.settings.gameType();
    }

    public void setGameType(GameType p_78525_) {
        this.settings = this.settings.withGameType(p_78525_);
    }

    public boolean isHardcore() {
        return this.settings.hardcore();
    }

    public boolean getAllowCommands() {
        return this.settings.allowCommands();
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    public void setInitialized(boolean p_78581_) {
        this.initialized = p_78581_;
    }

    public GameRules getGameRules() {
        return this.settings.gameRules();
    }

    public WorldBorder.Settings getWorldBorder() {
        return this.worldBorder;
    }

    public void setWorldBorder(WorldBorder.Settings p_78527_) {
        this.worldBorder = p_78527_;
    }

    public Difficulty getDifficulty() {
        return this.settings.difficulty();
    }

    public void setDifficulty(Difficulty p_78521_) {
        this.settings = this.settings.withDifficulty(p_78521_);
    }

    public boolean isDifficultyLocked() {
        return this.difficultyLocked;
    }

    public void setDifficultyLocked(boolean p_78586_) {
        this.difficultyLocked = p_78586_;
    }

    public TimerQueue<MinecraftServer> getScheduledEvents() {
        return this.scheduledEvents;
    }

    public void fillCrashReportCategory(CrashReportCategory p_164972_, LevelHeightAccessor p_164973_) {
        ServerLevelData.super.fillCrashReportCategory(p_164972_, p_164973_);
        WorldData.super.fillCrashReportCategory(p_164972_);
    }

    public WorldOptions worldGenOptions() {
        return this.worldOptions;
    }

    public boolean isFlatWorld() {
        return this.specialWorldProperty == net.minecraft.world.level.storage.PrimaryLevelData.SpecialWorldProperty.FLAT;
    }

    public boolean isDebugWorld() {
        return this.specialWorldProperty == net.minecraft.world.level.storage.PrimaryLevelData.SpecialWorldProperty.DEBUG;
    }

    public Lifecycle worldGenSettingsLifecycle() {
        return this.worldGenSettingsLifecycle;
    }

    public EndDragonFight.Data endDragonFightData() {
        return this.endDragonFightData;
    }

    public void setEndDragonFightData(EndDragonFight.Data p_289770_) {
        this.endDragonFightData = p_289770_;
    }

    public WorldDataConfiguration getDataConfiguration() {
        return this.settings.getDataConfiguration();
    }

    public void setDataConfiguration(WorldDataConfiguration p_252328_) {
        this.settings = this.settings.withDataConfiguration(p_252328_);
    }

    @Nullable
    public CompoundTag getCustomBossEvents() {
        return this.customBossEvents;
    }

    public void setCustomBossEvents(@Nullable CompoundTag p_78571_) {
        this.customBossEvents = p_78571_;
    }

    public int getWanderingTraderSpawnDelay() {
        return this.wanderingTraderSpawnDelay;
    }

    public void setWanderingTraderSpawnDelay(int p_78595_) {
        this.wanderingTraderSpawnDelay = p_78595_;
    }

    public int getWanderingTraderSpawnChance() {
        return this.wanderingTraderSpawnChance;
    }

    public void setWanderingTraderSpawnChance(int p_78598_) {
        this.wanderingTraderSpawnChance = p_78598_;
    }

    @Nullable
    public UUID getWanderingTraderId() {
        return this.wanderingTraderId;
    }

    public void setWanderingTraderId(UUID p_78553_) {
        this.wanderingTraderId = p_78553_;
    }

    public void setModdedInfo(String p_78550_, boolean p_78551_) {
        this.knownServerBrands.add(p_78550_);
        this.wasModded |= p_78551_;
    }

    public boolean wasModded() {
        return this.wasModded;
    }

    public Set<String> getKnownServerBrands() {
        return ImmutableSet.copyOf(this.knownServerBrands);
    }

    public Set<String> getRemovedFeatureFlags() {
        return Set.copyOf(this.removedFeatureFlags);
    }

    public ServerLevelData overworldData() {
        return this;
    }

    public LevelSettings getLevelSettings() {
        return this.settings.copy();
    }

    public boolean hasConfirmedExperimentalWarning() {
        return this.confirmedExperimentalWarning;
    }

    public PrimaryLevelData withConfirmedWarning(boolean confirmedWarning) {
        this.confirmedExperimentalWarning = confirmedWarning;
        return this;
    }

    /** @deprecated */
    @Deprecated
    public static enum SpecialWorldProperty {
        NONE,
        FLAT,
        DEBUG;

        private SpecialWorldProperty() {
        }
    }
}

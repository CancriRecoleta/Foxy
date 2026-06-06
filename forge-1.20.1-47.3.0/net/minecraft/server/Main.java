//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.util.PathConverter;
import joptsimple.util.PathProperties;
import net.minecraft.CrashReport;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.commands.Commands.CommandSelection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestBatch;
import net.minecraft.gametest.framework.GameTestRegistry;
import net.minecraft.gametest.framework.GameTestRunner;
import net.minecraft.gametest.framework.GameTestServer;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.server.dedicated.DedicatedServerSettings;
import net.minecraft.server.level.progress.LoggerChunkProgressListener;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.profiling.jfr.Environment;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import net.minecraft.util.worldupdate.WorldUpgrader;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.WorldData;
import net.minecraftforge.gametest.BlockPosValueConverter;
import net.minecraftforge.gametest.ForgeGameTestHooks;
import net.minecraftforge.server.loading.ServerModLoader;
import org.apache.logging.log4j.LogManager;
import org.slf4j.Logger;

public class Main {
    private static final Logger LOGGER = LogUtils.getLogger();

    public Main() {
    }

    @DontObfuscate
    public static void main(String[] p_129699_) {
        SharedConstants.tryDetectVersion();
        OptionParser optionparser = new OptionParser();
        OptionSpec<Void> optionspec = optionparser.accepts("nogui");
        OptionSpec<Void> optionspec1 = optionparser.accepts("initSettings", "Initializes 'server.properties' and 'eula.txt', then quits");
        OptionSpec<Void> optionspec2 = optionparser.accepts("demo");
        OptionSpec<Void> optionspec3 = optionparser.accepts("bonusChest");
        OptionSpec<Void> optionspec4 = optionparser.accepts("forceUpgrade");
        OptionSpec<Void> optionspec5 = optionparser.accepts("eraseCache");
        OptionSpec<Void> optionspec6 = optionparser.accepts("safeMode", "Loads level with vanilla datapack only");
        OptionSpec<Void> optionspec7 = optionparser.accepts("help").forHelp();
        OptionSpec<String> optionspec8 = optionparser.accepts("singleplayer").withRequiredArg();
        OptionSpec<String> optionspec9 = optionparser.accepts("universe").withRequiredArg().defaultsTo(".", new String[0]);
        OptionSpec<String> optionspec10 = optionparser.accepts("world").withRequiredArg();
        OptionSpec<Integer> optionspec11 = optionparser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(-1, new Integer[0]);
        OptionSpec<String> optionspec12 = optionparser.accepts("serverId").withRequiredArg();
        OptionSpec<Void> optionspec13 = optionparser.accepts("jfrProfile");
        OptionSpec<Path> optionspec14 = optionparser.accepts("pidFile").withRequiredArg().withValuesConvertedBy(new PathConverter(new PathProperties[0]));
        OptionSpec<String> optionspec15 = optionparser.nonOptions();
        optionparser.accepts("allowUpdates").withRequiredArg().ofType(Boolean.class).defaultsTo(Boolean.TRUE, new Boolean[0]);
        optionparser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."), new File[0]);
        boolean gametestEnabled = Boolean.getBoolean("forge.gameTestServer");
        ArgumentAcceptingOptionSpec spawnPosOpt;
        if (gametestEnabled) {
            spawnPosOpt = optionparser.accepts("spawnPos").withRequiredArg().withValuesConvertedBy(new BlockPosValueConverter()).defaultsTo(new BlockPos(0, 60, 0), new BlockPos[0]);
        } else {
            spawnPosOpt = null;
        }

        try {
            OptionSet optionset = optionparser.parse(p_129699_);
            if (optionset.has(optionspec7)) {
                optionparser.printHelpOn(System.err);
                return;
            }

            Path path2 = Paths.get("eula.txt");
            Eula eula = new Eula(path2);
            if (!eula.hasAgreedToEULA()) {
                LOGGER.info("You need to agree to the EULA in order to run the server. Go to eula.txt for more info.");
                return;
            }

            Path path = (Path)optionset.valueOf(optionspec14);
            if (path != null) {
                writePidFile(path);
            }

            CrashReport.preload();
            if (optionset.has(optionspec13)) {
                JvmProfiler.INSTANCE.start(Environment.SERVER);
            }

            Bootstrap.bootStrap();
            Bootstrap.validate();
            Util.startTimerHackThread();
            Path path1 = Paths.get("server.properties");
            if (!optionset.has(optionspec1)) {
                ServerModLoader.load();
            }

            DedicatedServerSettings dedicatedserversettings = new DedicatedServerSettings(path1);
            dedicatedserversettings.forceSave();
            if (optionset.has(optionspec1)) {
                LOGGER.info("Initialized '{}' and '{}'", path1.toAbsolutePath(), path2.toAbsolutePath());
                return;
            }

            File file1 = new File((String)optionset.valueOf(optionspec9));
            Services services = Services.create(new YggdrasilAuthenticationService(Proxy.NO_PROXY), file1);
            String s = (String)Optional.ofNullable((String)optionset.valueOf(optionspec10)).orElse(dedicatedserversettings.getProperties().levelName);
            if (s == null || s.isEmpty() || (new File(file1, s)).getAbsolutePath().equals((new File(s)).getAbsolutePath())) {
                LOGGER.error("Invalid world directory specified, must not be null, empty or the same directory as your universe! " + s);
                return;
            }

            LevelStorageSource levelstoragesource = LevelStorageSource.createDefault(file1.toPath());
            LevelStorageSource.LevelStorageAccess levelstoragesource$levelstorageaccess = levelstoragesource.validateAndCreateAccess(s);
            levelstoragesource$levelstorageaccess.readAdditionalLevelSaveData();
            LevelSummary levelsummary = levelstoragesource$levelstorageaccess.getSummary();
            if (levelsummary != null) {
                if (levelsummary.requiresManualConversion()) {
                    LOGGER.info("This world must be opened in an older version (like 1.6.4) to be safely converted");
                    return;
                }

                if (!levelsummary.isCompatible()) {
                    LOGGER.info("This world was created by an incompatible version.");
                    return;
                }
            }

            boolean flag = optionset.has(optionspec6);
            if (flag) {
                LOGGER.warn("Safe mode active, only vanilla datapack will be loaded");
            }

            PackRepository packrepository = ServerPacksSource.createPackRepository(levelstoragesource$levelstorageaccess.getLevelPath(LevelResource.DATAPACK_DIR));

            WorldStem worldstem;
            try {
                WorldLoader.InitConfig worldloader$initconfig = loadOrCreateConfig(dedicatedserversettings.getProperties(), levelstoragesource$levelstorageaccess, flag, packrepository);
                worldstem = (WorldStem)Util.blockUntilDone((p_248086_) -> {
                    return WorldLoader.load(worldloader$initconfig, (p_248079_) -> {
                        Registry<LevelStem> registry = p_248079_.datapackDimensions().registryOrThrow(Registries.LEVEL_STEM);
                        DynamicOps<Tag> dynamicops = RegistryOps.create(NbtOps.INSTANCE, (HolderLookup.Provider)p_248079_.datapackWorldgen());
                        Pair<WorldData, WorldDimensions.Complete> pair = levelstoragesource$levelstorageaccess.getDataTag(dynamicops, p_248079_.dataConfiguration(), registry, p_248079_.datapackWorldgen().allRegistriesLifecycle());
                        if (pair != null) {
                            return new WorldLoader.DataLoadOutput((WorldData)pair.getFirst(), ((WorldDimensions.Complete)pair.getSecond()).dimensionsRegistryAccess());
                        } else {
                            LevelSettings levelsettings;
                            WorldOptions worldoptions;
                            WorldDimensions worlddimensions;
                            if (optionset.has(optionspec2)) {
                                levelsettings = MinecraftServer.DEMO_SETTINGS;
                                worldoptions = WorldOptions.DEMO_OPTIONS;
                                worlddimensions = WorldPresets.createNormalWorldDimensions(p_248079_.datapackWorldgen());
                            } else {
                                DedicatedServerProperties dedicatedserverproperties = dedicatedserversettings.getProperties();
                                levelsettings = new LevelSettings(dedicatedserverproperties.levelName, dedicatedserverproperties.gamemode, dedicatedserverproperties.hardcore, dedicatedserverproperties.difficulty, false, new GameRules(), p_248079_.dataConfiguration());
                                worldoptions = optionset.has(optionspec3) ? dedicatedserverproperties.worldOptions.withBonusChest(true) : dedicatedserverproperties.worldOptions;
                                worlddimensions = dedicatedserverproperties.createDimensions(p_248079_.datapackWorldgen());
                            }

                            DataResult var10000 = WorldDimensions.CODEC.encoder().encodeStart(dynamicops, worlddimensions).flatMap((writtenPayloadWithModdedDimensions) -> {
                                return WorldDimensions.CODEC.decoder().parse(dynamicops, writtenPayloadWithModdedDimensions);
                            });
                            Logger var10001 = LOGGER;
                            Objects.requireNonNull(var10001);
                            worlddimensions = (WorldDimensions)var10000.resultOrPartial(var10001::error).orElse(worlddimensions);
                            WorldDimensions.Complete worlddimensions$complete = worlddimensions.bake(registry);
                            Lifecycle lifecycle = worlddimensions$complete.lifecycle().add(p_248079_.datapackWorldgen().allRegistriesLifecycle());
                            return new WorldLoader.DataLoadOutput(new PrimaryLevelData(levelsettings, worldoptions, worlddimensions$complete.specialWorldProperty(), lifecycle), worlddimensions$complete.dimensionsRegistryAccess());
                        }
                    }, WorldStem::new, Util.backgroundExecutor(), p_248086_);
                }).get();
            } catch (Exception var39) {
                Exception exception = var39;
                LOGGER.warn("Failed to load datapacks, can't proceed with server load. You can either fix your datapacks or reset to vanilla with --safeMode", exception);
                return;
            }

            RegistryAccess.Frozen registryaccess$frozen = worldstem.registries().compositeAccess();
            if (optionset.has(optionspec4)) {
                forceUpgrade(levelstoragesource$levelstorageaccess, DataFixers.getDataFixer(), optionset.has(optionspec5), () -> {
                    return true;
                }, registryaccess$frozen.registryOrThrow(Registries.LEVEL_STEM));
            }

            WorldData worlddata = worldstem.worldData();
            levelstoragesource$levelstorageaccess.saveDataTag(registryaccess$frozen, worlddata);
            final MinecraftServer dedicatedserver = MinecraftServer.spin((p_129697_) -> {
                Object dedicatedserver1;
                if (gametestEnabled) {
                    ForgeGameTestHooks.registerGametests();
                    Collection<GameTestBatch> testBatches = GameTestRunner.groupTestsIntoBatches(GameTestRegistry.getAllTestFunctions());
                    BlockPos spawnPos = (BlockPos)optionset.valueOf(spawnPosOpt);
                    dedicatedserver1 = new GameTestServer(p_129697_, levelstoragesource$levelstorageaccess, packrepository, worldstem, testBatches, spawnPos);
                } else {
                    dedicatedserver1 = new DedicatedServer(p_129697_, levelstoragesource$levelstorageaccess, packrepository, worldstem, dedicatedserversettings, DataFixers.getDataFixer(), services, LoggerChunkProgressListener::new);
                }

                ((MinecraftServer)dedicatedserver1).setSingleplayerProfile(optionset.has(optionspec8) ? new GameProfile((UUID)null, (String)optionset.valueOf(optionspec8)) : null);
                ((MinecraftServer)dedicatedserver1).setPort((Integer)optionset.valueOf(optionspec11));
                ((MinecraftServer)dedicatedserver1).setDemo(optionset.has(optionspec2));
                ((MinecraftServer)dedicatedserver1).setId((String)optionset.valueOf(optionspec12));
                boolean flag1 = !optionset.has(optionspec) && !optionset.valuesOf(optionspec15).contains("nogui");
                if (dedicatedserver1 instanceof DedicatedServer dedicatedServer) {
                    if (flag1 && !GraphicsEnvironment.isHeadless()) {
                        dedicatedServer.showGui();
                    }
                }

                return (MinecraftServer)dedicatedserver1;
            });
            Thread thread = new Thread("Server Shutdown Thread") {
                public void run() {
                    if (!(dedicatedserver instanceof GameTestServer)) {
                        dedicatedserver.halt(true);
                    }

                    LogManager.shutdown();
                }
            };
            thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
            Runtime.getRuntime().addShutdownHook(thread);
        } catch (Exception var40) {
            Exception exception1 = var40;
            LOGGER.error(LogUtils.FATAL_MARKER, "Failed to start the minecraft server", exception1);
        }

    }

    private static void writePidFile(Path p_270192_) {
        try {
            long i = ProcessHandle.current().pid();
            Files.writeString(p_270192_, Long.toString(i));
        } catch (IOException var3) {
            IOException ioexception = var3;
            throw new UncheckedIOException(ioexception);
        }
    }

    private static WorldLoader.InitConfig loadOrCreateConfig(DedicatedServerProperties p_248563_, LevelStorageSource.LevelStorageAccess p_251359_, boolean p_249093_, PackRepository p_251069_) {
        WorldDataConfiguration worlddataconfiguration = p_251359_.getDataConfiguration();
        WorldDataConfiguration worlddataconfiguration1;
        boolean flag;
        if (worlddataconfiguration != null) {
            flag = false;
            worlddataconfiguration1 = worlddataconfiguration;
        } else {
            flag = true;
            worlddataconfiguration1 = new WorldDataConfiguration(p_248563_.initialDataPackConfiguration, FeatureFlags.DEFAULT_FLAGS);
        }

        WorldLoader.PackConfig worldloader$packconfig = new WorldLoader.PackConfig(p_251069_, worlddataconfiguration1, p_249093_, flag);
        return new WorldLoader.InitConfig(worldloader$packconfig, CommandSelection.DEDICATED, p_248563_.functionPermissionLevel);
    }

    private static void forceUpgrade(LevelStorageSource.LevelStorageAccess p_195489_, DataFixer p_195490_, boolean p_195491_, BooleanSupplier p_195492_, Registry<LevelStem> p_250443_) {
        LOGGER.info("Forcing world upgrade!");
        WorldUpgrader worldupgrader = new WorldUpgrader(p_195489_, p_195490_, p_250443_, p_195491_);
        Component component = null;

        while(!worldupgrader.isFinished()) {
            Component component1 = worldupgrader.getStatus();
            if (component != component1) {
                component = component1;
                LOGGER.info(worldupgrader.getStatus().getString());
            }

            int i = worldupgrader.getTotalChunks();
            if (i > 0) {
                int j = worldupgrader.getConverted() + worldupgrader.getSkipped();
                LOGGER.info("{}% completed ({} / {} chunks)...", new Object[]{Mth.floor((float)j / (float)i * 100.0F), j, i});
            }

            if (!p_195492_.getAsBoolean()) {
                worldupgrader.cancel();
            } else {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException var10) {
                }
            }
        }

    }
}

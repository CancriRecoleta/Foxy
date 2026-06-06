//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.commands.Commands;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraftforge.registries.DataPackRegistriesHooks;
import org.slf4j.Logger;

public class WorldLoader {
    private static final Logger LOGGER = LogUtils.getLogger();

    public WorldLoader() {
    }

    public static <D, R> CompletableFuture<R> load(InitConfig p_214363_, WorldDataSupplier<D> p_214364_, ResultFactory<D, R> p_214365_, Executor p_214366_, Executor p_214367_) {
        try {
            Pair<WorldDataConfiguration, CloseableResourceManager> pair = p_214363_.packConfig.createResourceManager();
            CloseableResourceManager closeableresourcemanager = (CloseableResourceManager)pair.getSecond();
            LayeredRegistryAccess<RegistryLayer> layeredregistryaccess = RegistryLayer.createRegistryAccess();
            LayeredRegistryAccess<RegistryLayer> layeredregistryaccess1 = loadAndReplaceLayer(closeableresourcemanager, layeredregistryaccess, RegistryLayer.WORLDGEN, DataPackRegistriesHooks.getDataPackRegistries());
            RegistryAccess.Frozen registryaccess$frozen = layeredregistryaccess1.getAccessForLoading(RegistryLayer.DIMENSIONS);
            RegistryAccess.Frozen registryaccess$frozen1 = RegistryDataLoader.load(closeableresourcemanager, registryaccess$frozen, RegistryDataLoader.DIMENSION_REGISTRIES);
            WorldDataConfiguration worlddataconfiguration = (WorldDataConfiguration)pair.getFirst();
            DataLoadOutput<D> dataloadoutput = p_214364_.get(new DataLoadContext(closeableresourcemanager, worlddataconfiguration, registryaccess$frozen, registryaccess$frozen1));
            LayeredRegistryAccess<RegistryLayer> layeredregistryaccess2 = layeredregistryaccess1.replaceFrom(RegistryLayer.DIMENSIONS, (RegistryAccess.Frozen[])(dataloadoutput.finalDimensions));
            RegistryAccess.Frozen registryaccess$frozen2 = layeredregistryaccess2.getAccessForLoading(RegistryLayer.RELOADABLE);
            return ReloadableServerResources.loadResources(closeableresourcemanager, registryaccess$frozen2, worlddataconfiguration.enabledFeatures(), p_214363_.commandSelection(), p_214363_.functionCompilationLevel(), p_214366_, p_214367_).whenComplete((p_214370_, p_214371_) -> {
                if (p_214371_ != null) {
                    closeableresourcemanager.close();
                }

            }).thenApplyAsync((p_248101_) -> {
                p_248101_.updateRegistryTags(registryaccess$frozen2);
                return p_214365_.create(closeableresourcemanager, p_248101_, layeredregistryaccess2, dataloadoutput.cookie);
            }, p_214367_);
        } catch (Exception var15) {
            Exception exception = var15;
            return CompletableFuture.failedFuture(exception);
        }
    }

    private static RegistryAccess.Frozen loadLayer(ResourceManager p_251529_, LayeredRegistryAccess<RegistryLayer> p_250737_, RegistryLayer p_250790_, List<RegistryDataLoader.RegistryData<?>> p_249516_) {
        RegistryAccess.Frozen registryaccess$frozen = p_250737_.getAccessForLoading(p_250790_);
        return RegistryDataLoader.load(p_251529_, registryaccess$frozen, p_249516_);
    }

    private static LayeredRegistryAccess<RegistryLayer> loadAndReplaceLayer(ResourceManager p_249913_, LayeredRegistryAccess<RegistryLayer> p_252077_, RegistryLayer p_250346_, List<RegistryDataLoader.RegistryData<?>> p_250589_) {
        RegistryAccess.Frozen registryaccess$frozen = loadLayer(p_249913_, p_252077_, p_250346_, p_250589_);
        return p_252077_.replaceFrom(p_250346_, (RegistryAccess.Frozen[])(registryaccess$frozen));
    }

    public static record InitConfig(PackConfig packConfig, Commands.CommandSelection commandSelection, int functionCompilationLevel) {
        public InitConfig(PackConfig packConfig, Commands.CommandSelection commandSelection, int functionCompilationLevel) {
            this.packConfig = packConfig;
            this.commandSelection = commandSelection;
            this.functionCompilationLevel = functionCompilationLevel;
        }

        public PackConfig packConfig() {
            return this.packConfig;
        }

        public Commands.CommandSelection commandSelection() {
            return this.commandSelection;
        }

        public int functionCompilationLevel() {
            return this.functionCompilationLevel;
        }
    }

    public static record PackConfig(PackRepository packRepository, WorldDataConfiguration initialDataConfig, boolean safeMode, boolean initMode) {
        public PackConfig(PackRepository packRepository, WorldDataConfiguration initialDataConfig, boolean safeMode, boolean initMode) {
            this.packRepository = packRepository;
            this.initialDataConfig = initialDataConfig;
            this.safeMode = safeMode;
            this.initMode = initMode;
        }

        public Pair<WorldDataConfiguration, CloseableResourceManager> createResourceManager() {
            FeatureFlagSet featureflagset = this.initMode ? FeatureFlags.REGISTRY.allFlags() : this.initialDataConfig.enabledFeatures();
            WorldDataConfiguration worlddataconfiguration = MinecraftServer.configurePackRepository(this.packRepository, this.initialDataConfig.dataPacks(), this.safeMode, featureflagset);
            if (!this.initMode) {
                worlddataconfiguration = worlddataconfiguration.expandFeatures(this.initialDataConfig.enabledFeatures());
            }

            List<PackResources> list = this.packRepository.openAllSelected();
            CloseableResourceManager closeableresourcemanager = new MultiPackResourceManager(PackType.SERVER_DATA, list);
            return Pair.of(worlddataconfiguration, closeableresourcemanager);
        }

        public PackRepository packRepository() {
            return this.packRepository;
        }

        public WorldDataConfiguration initialDataConfig() {
            return this.initialDataConfig;
        }

        public boolean safeMode() {
            return this.safeMode;
        }

        public boolean initMode() {
            return this.initMode;
        }
    }

    public static record DataLoadContext(ResourceManager resources, WorldDataConfiguration dataConfiguration, RegistryAccess.Frozen datapackWorldgen, RegistryAccess.Frozen datapackDimensions) {
        public DataLoadContext(ResourceManager resources, WorldDataConfiguration dataConfiguration, RegistryAccess.Frozen datapackWorldgen, RegistryAccess.Frozen datapackDimensions) {
            this.resources = resources;
            this.dataConfiguration = dataConfiguration;
            this.datapackWorldgen = datapackWorldgen;
            this.datapackDimensions = datapackDimensions;
        }

        public ResourceManager resources() {
            return this.resources;
        }

        public WorldDataConfiguration dataConfiguration() {
            return this.dataConfiguration;
        }

        public RegistryAccess.Frozen datapackWorldgen() {
            return this.datapackWorldgen;
        }

        public RegistryAccess.Frozen datapackDimensions() {
            return this.datapackDimensions;
        }
    }

    @FunctionalInterface
    public interface WorldDataSupplier<D> {
        DataLoadOutput<D> get(DataLoadContext var1);
    }

    public static record DataLoadOutput<D>(D cookie, RegistryAccess.Frozen finalDimensions) {
        public DataLoadOutput(D cookie, RegistryAccess.Frozen finalDimensions) {
            this.cookie = cookie;
            this.finalDimensions = finalDimensions;
        }

        public D cookie() {
            return this.cookie;
        }

        public RegistryAccess.Frozen finalDimensions() {
            return this.finalDimensions;
        }
    }

    @FunctionalInterface
    public interface ResultFactory<D, R> {
        R create(CloseableResourceManager var1, ReloadableServerResources var2, LayeredRegistryAccess<RegistryLayer> var3, D var4);
    }
}

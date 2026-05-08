package com.github.foxy.commonImpl;

import com.github.foxy.Foxy;
import com.github.foxy.client.config.FoxyConfig;
import com.github.foxy.common.Logger;
import com.github.foxy.common.StorageConfigUtil;
import com.github.foxy.common.config.ConfigBuildCtx;
import com.github.foxy.common.config.section.SectionStorageConfig;
import com.github.foxy.common.config.section.SectionSerializationStorage;
import com.github.foxy.common.config.storage.file.FileStorageBackend;
import com.github.foxy.common.thread.UnifiedServiceThreadPool;
import com.github.foxy.common.thread.ServiceManager;
import com.github.foxy.common.world.WorldEngine;
import com.github.foxy.common.world.service.MipService;
import com.github.foxy.common.world.service.SectionSavingService;
import com.github.foxy.common.world.service.VoxelIngestService;
import net.minecraftforge.fml.loading.FMLPaths;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Per-world runtime state for Foxy: the live {@link WorldEngine}, its
 * {@link StorageBackend}, the import scheduler and the LOD mip service.
 *
 * <h2>Singleton</h2>
 * <p>One instance is active at a time, addressed via {@link #current()}. The client
 * lifecycle wiring (see {@code com.github.foxy.client}) calls {@link #enter(WorldIdentifier)}
 * when joining a world and {@link #leave()} when leaving; {@link #getOrCreateEngine}
 * lazily upgrades the singleton with a freshly-built {@link WorldEngine} on first use.</p>
 *
 * <h2>Storage layout</h2>
 * <pre>
 *   &lt;gamedir&gt;/foxy/&lt;worldId&gt;/   &mdash; one tree per identifier
 *           sections/                  &mdash; section payloads (FileStorageBackend)
 *           mappings/                  &mdash; id mappings   (FileStorageBackend)
 * </pre>
 * Different identifiers therefore can never share state, so loading two saves with the
 * same dimension but different seeds doesn't cross-contaminate.
 */
public final class FoxyInstance {

    private static volatile FoxyInstance current;

    private final WorldIdentifier identifier;
    private final Path basePath;
    private final Config storageConfig;
    private final ImportManager importManager = new ImportManager();
    private final UnifiedServiceThreadPool threadPool = new UnifiedServiceThreadPool();
    private final SectionSavingService savingService = new SectionSavingService(this.threadPool.serviceManager);
    private final VoxelIngestService ingestService = new VoxelIngestService(this.threadPool.serviceManager);
    private final Map<WorldIdentifier, WorldEngine> engines = new HashMap<>();
    private final Map<WorldEngine, MipService> mipServices = new HashMap<>();

    private FoxyInstance(WorldIdentifier identifier, Path basePath) {
        this.identifier = identifier;
        this.basePath = basePath.toAbsolutePath().normalize();
        this.storageConfig = StorageConfigUtil.getCreateStorageConfig(
                Config.class,
                c -> c.version == 1 && c.sectionStorageConfig != null,
                Config::defaultConfig,
                this.basePath);
        this.updateDedicatedThreads();
        Logger.info("Foxy storage base path: " + this.basePath);
    }

    /** Returns the active instance or {@code null} when no world is bound. */
    @Nullable
    public static FoxyInstance current() { return current; }

    /**
     * Marks {@code identifier} as the active world. If a different instance is
     * already active it is shut down first; calling {@code enter} with the same
     * identifier as the current one is a no-op.
     */
    public static synchronized FoxyInstance enter(WorldIdentifier identifier) {
        return enter(identifier, FMLPaths.GAMEDIR.get().resolve(Foxy.MODID).resolve("saves"));
    }

    public static synchronized FoxyInstance enter(WorldIdentifier identifier, Path basePath) {
        var existing = current;
        if (existing != null && existing.identifier.equals(identifier)) {
            return existing;
        }
        if (existing != null) {
            existing.shutdownInternal();
        }
        var fresh = new FoxyInstance(identifier, basePath);
        current = fresh;
        Logger.info("FoxyInstance entered " + identifier);
        return fresh;
    }

    /** Tears down the active instance. Safe to call when nothing is active. */
    public static synchronized void leave() {
        var existing = current;
        if (existing == null) return;
        existing.shutdownInternal();
        current = null;
    }

    /** Identifier this instance is bound to. */
    public WorldIdentifier identifier() { return this.identifier; }

    /** Root directory containing this instance's storage config and per-world stores. */
    public Path basePath() { return this.basePath; }

    /** Shared {@link ImportManager}; owns one importer per engine. */
    public ImportManager importManager() { return this.importManager; }

    public boolean isRunning() { return current == this; }

    public boolean isIngestEnabled(WorldIdentifier worldId) {
        return FoxyConfig.CONFIG.enabled && FoxyConfig.CONFIG.ingestEnabled;
    }

    public VoxelIngestService getIngestService() { return this.ingestService; }

    public ServiceManager getServiceManager() { return this.threadPool.serviceManager; }

    public UnifiedServiceThreadPool getThreadPool() { return this.threadPool; }

    public void updateDedicatedThreads() {
        this.setNumThreads(Math.max(FoxyConfig.CONFIG.serviceThreads, 0));
    }

    private void setNumThreads(int threads) {
        if (this.threadPool.setNumThreads(threads)) {
            Logger.info("Dedicated Foxy thread pool size: " + threads);
        }
    }

    /** Returns the active engine for this instance, building it on first request. */
    public synchronized WorldEngine getOrCreateEngine() {
        return getOrCreateEngine(this.identifier);
    }

    public synchronized WorldEngine getOrCreate(WorldIdentifier id) {
        return getOrCreateEngine(id);
    }

    /**
     * Returns the engine bound to {@code id}, building it (and its storage tree) on
     * first request. Useful when the client briefly wants to access another dimension's
     * engine while the active identifier still points at the current dimension.
     */
    public synchronized WorldEngine getOrCreateEngine(WorldIdentifier id) {
        var engine = this.engines.get(id);
        if (engine != null && engine.isLive()) return engine;
        engine = this.buildEngine(id);
        engine.setSaveCallback(this.savingService::enqueueSave);
        this.engines.put(id, engine);
        return engine;
    }

    /**
     * Returns the engine bound to {@code id} only if it has already been built;
     * otherwise {@code null}.
     */
    @Nullable
    public synchronized WorldEngine getEngine(WorldIdentifier id) {
        var engine = this.engines.get(id);
        return (engine != null && engine.isLive()) ? engine : null;
    }

    /**
     * Returns (and lazily creates) the {@link MipService} for {@code engine}. Each
     * engine gets at most one service for the lifetime of the instance.
     */
    public synchronized MipService getOrCreateMipService(WorldEngine engine) {
        var service = this.mipServices.get(engine);
        if (service != null) return service;
        service = new MipService(engine);
        this.mipServices.put(engine, service);
        return service;
    }

    // ---- internals ---------------------------------------------------------------------

    private WorldEngine buildEngine(WorldIdentifier id) {
        var ctx = new ConfigBuildCtx();
        ctx.setProperty(ConfigBuildCtx.BASE_SAVE_PATH, this.basePath.toString());
        ctx.setProperty(ConfigBuildCtx.WORLD_IDENTIFIER, id.getWorldId());
        ctx.setProperty(ConfigBuildCtx.PLAYER_UUID, "local");
        ctx.pushPath(ConfigBuildCtx.DEFAULT_STORAGE_PATH);
        Logger.info("Foxy: building WorldEngine for " + id + " under " + ctx.substituteString(ctx.resolvePath()));
        var sectionStorage = this.createSectionStorage(ctx);
        return new WorldEngine(sectionStorage, this);
    }

    private com.github.foxy.common.config.section.SectionStorage createSectionStorage(ConfigBuildCtx ctx) {
        try {
            return this.storageConfig.sectionStorageConfig.build(ctx);
        } catch (Throwable t) {
            Logger.error("Foxy storage backend failed; falling back to file storage at "
                    + ctx.substituteString(ctx.resolvePath()), t);
            this.storageConfig.sectionStorageConfig = StorageConfigUtil.createDefaultSerializer();
            this.saveStorageConfig();
            return this.storageConfig.sectionStorageConfig.build(ctx);
        }
    }

    private void saveStorageConfig() {
        try {
            java.nio.file.Files.writeString(this.basePath.resolve("config.json"),
                    com.github.foxy.common.config.Serialization.GSON.toJson(this.storageConfig));
        } catch (Throwable t) {
            Logger.error("Foxy failed to persist fallback storage config", t);
        }
    }

    private void shutdownInternal() {
        Logger.info("Foxy: tearing down instance for " + this.identifier);
        // Shut down mip services first so they stop poking the engines mid-shutdown.
        for (var service : this.mipServices.values()) {
            try { service.close(); } catch (Throwable t) { Logger.error("MipService close failed", t); }
        }
        this.mipServices.clear();

        try { this.ingestService.shutdown(); } catch (Throwable t) { Logger.error("VoxelIngestService close failed", t); }
        try { this.savingService.shutdown(); } catch (Throwable t) { Logger.error("SectionSavingService close failed", t); }

        for (var engine : this.engines.values()) {
            try {
                if (engine.isLive()) engine.free();
            } catch (Throwable t) {
                Logger.error("WorldEngine free failed", t);
            }
        }
        this.engines.clear();
        try { this.threadPool.shutdown(); } catch (Throwable t) { Logger.error("Service thread pool close failed", t); }
    }

    private static final class Config {
        public int version = 1;
        public boolean disabled = false;
        public SectionStorageConfig sectionStorageConfig;

        static Config defaultConfig() {
            var config = new Config();
            config.sectionStorageConfig = StorageConfigUtil.createDefaultSerializer();
            return config;
        }
    }
}

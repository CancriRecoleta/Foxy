package com.github.foxy.commonImpl;

import com.github.foxy.Foxy;
import com.github.foxy.common.Logger;
import com.github.foxy.common.config.section.SectionSerializationStorage;
import com.github.foxy.common.config.storage.StorageBackend;
import com.github.foxy.common.config.storage.file.FileStorageBackend;
import com.github.foxy.common.world.WorldEngine;
import com.github.foxy.common.world.service.MipService;
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
    private final ImportManager importManager = new ImportManager();
    private final Map<WorldIdentifier, WorldEngine> engines = new HashMap<>();
    private final Map<WorldEngine, MipService> mipServices = new HashMap<>();

    private FoxyInstance(WorldIdentifier identifier) {
        this.identifier = identifier;
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
        var existing = current;
        if (existing != null && existing.identifier.equals(identifier)) {
            return existing;
        }
        if (existing != null) {
            existing.shutdownInternal();
        }
        var fresh = new FoxyInstance(identifier);
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

    /** Shared {@link ImportManager}; owns one importer per engine. */
    public ImportManager importManager() { return this.importManager; }

    /** Returns the active engine for this instance, building it on first request. */
    public synchronized WorldEngine getOrCreateEngine() {
        return getOrCreateEngine(this.identifier);
    }

    /**
     * Returns the engine bound to {@code id}, building it (and its storage tree) on
     * first request. Useful when the client briefly wants to access another dimension's
     * engine while the active identifier still points at the current dimension.
     */
    public synchronized WorldEngine getOrCreateEngine(WorldIdentifier id) {
        var engine = this.engines.get(id);
        if (engine != null && engine.isLive()) return engine;
        engine = buildEngine(id);
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

    private static Path storageRootFor(WorldIdentifier id) {
        return FMLPaths.GAMEDIR.get()
                .resolve(Foxy.MODID)
                .resolve(id.getWorldId());
    }

    private static WorldEngine buildEngine(WorldIdentifier id) {
        Path root = storageRootFor(id);
        Logger.info("Foxy: building WorldEngine for " + id + " at " + root);
        StorageBackend backend = new FileStorageBackend(root);
        var sectionStorage = new SectionSerializationStorage(backend);
        return new WorldEngine(sectionStorage);
    }

    private void shutdownInternal() {
        Logger.info("Foxy: tearing down instance for " + this.identifier);
        // Shut down mip services first so they stop poking the engines mid-shutdown.
        for (var service : this.mipServices.values()) {
            try { service.close(); } catch (Throwable t) { Logger.error("MipService close failed", t); }
        }
        this.mipServices.clear();

        for (var engine : this.engines.values()) {
            try {
                if (engine.isLive()) engine.free();
            } catch (Throwable t) {
                Logger.error("WorldEngine free failed", t);
            }
        }
        this.engines.clear();
    }
}

package com.github.foxy.client;

import com.github.foxy.client.config.FoxyConfig;
import com.github.foxy.common.Logger;
import com.github.foxy.common.world.WorldEngine;
import com.github.foxy.commonImpl.FoxyInstance;
import com.github.foxy.commonImpl.WorldIdentifier;
import com.github.foxy.commonImpl.importers.IDataImporter;
import com.github.foxy.commonImpl.importers.WorldImporter;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Starts one low-priority single-player region import per changed world/dimension.
 *
 * <p>Live chunk ingest only sees vanilla-loaded chunks. This bridge backfills existing
 * anvil region files so Foxy has durable LOD data outside vanilla/Embeddium view
 * distance.</p>
 *
 * <p>Triggered every client tick from {@link FoxyClientLifecycle}; a per-session
 * {@code (worldId|regionDir)} key in {@link #CHECKED_THIS_SESSION} suppresses the
 * obvious duplicate calls, and a marker file inside the Foxy storage tree
 * ({@code &lt;basePath&gt;/backfill/&lt;worldId&gt;.marker}) suppresses re-imports
 * across sessions when the {@code region/} directory hasn't changed.</p>
 *
 * <h2>Cross-dimension routing</h2>
 * <p>Each call resolves the {@link WorldEngine} via {@link WorldIdentifier#of(Level)},
 * which combines the active instance's namespace + biomeSeed with the supplied
 * level's {@link Level#dimension()}. That way, walking through a nether portal and
 * tripping the next tick's {@code trySchedule} starts a backfill on the nether
 * engine without disturbing the overworld engine that already has data.</p>
 */
public final class FoxyAutoBackfill {
    private static final Set<String> CHECKED_THIS_SESSION = new HashSet<>();

    private FoxyAutoBackfill() {}

    /**
     * Schedules a region-file backfill for the dimension {@code mc.level} currently
     * points at. Pass the active {@link FoxyInstance}; the right per-dimension
     * {@link WorldEngine} is looked up via {@link WorldIdentifier#of(Level)} so callers
     * never have to remember which engine matches the current dimension.
     */
    public static void trySchedule(Minecraft mc, FoxyInstance instance) {
        if (!FoxyConfig.CONFIG.enabled || !FoxyConfig.CONFIG.ingestEnabled || !FoxyConfig.CONFIG.autoBackfillSingleplayer) {
            return;
        }
        if (!mc.hasSingleplayerServer() || mc.level == null) {
            return;
        }
        var server = mc.getSingleplayerServer();
        if (server == null) {
            return;
        }

        Path worldRoot = server.getWorldPath(LevelResource.ROOT);
        Path regionDir = DimensionType.getStorageFolder(mc.level.dimension(), worldRoot).resolve("region");
        if (!Files.isDirectory(regionDir)) {
            return;
        }

        // Per-dimension identifier; getOrCreate lazy-builds a fresh engine the
        // first time the player visits a new dimension, leaving the existing
        // overworld engine untouched.
        WorldIdentifier id = WorldIdentifier.of(mc.level);
        String sessionKey = id.getWorldId() + "|" + regionDir.toAbsolutePath().normalize();
        synchronized (CHECKED_THIS_SESSION) {
            if (!CHECKED_THIS_SESSION.add(sessionKey)) {
                return;
            }
        }

        try {
            RegionSignature signature = RegionSignature.scan(regionDir);
            if (signature.count == 0) {
                Logger.info("Foxy auto-backfill skipped; no region files in " + regionDir);
                return;
            }

            Path marker = markerPath(instance, id);
            String signatureText = signature.serialize();
            if (Files.exists(marker) && Files.readString(marker).equals(signatureText)) {
                Logger.info("Foxy auto-backfill skipped; existing marker is current for " + regionDir);
                return;
            }

            WorldEngine engine = instance.getOrCreate(id);
            var biomeRegistry = mc.level.registryAccess().registryOrThrow(Registries.BIOME);
            var importer = new MarkerWritingImporter(
                    new WorldImporter(engine, regionDir, biomeRegistry),
                    marker,
                    signatureText);
            if (instance.importManager().tryRunImport(importer)) {
                Logger.info("Foxy auto-backfill started for " + regionDir
                        + " (" + signature.count + " region files)");
            } else {
                Logger.info("Foxy auto-backfill skipped; an import is already active");
            }
        } catch (Throwable t) {
            Logger.error("Foxy auto-backfill failed to start", t);
        }
    }

    private static Path markerPath(FoxyInstance instance, WorldIdentifier id) {
        return instance.basePath().resolve("backfill").resolve(id.getWorldId() + ".marker");
    }

    private record RegionSignature(int count, long latestModifiedMillis, long totalBytes) {
        static RegionSignature scan(Path regionDir) throws IOException {
            int count = 0;
            long latestModifiedMillis = 0;
            long totalBytes = 0;
            try (Stream<Path> stream = Files.list(regionDir)) {
                var iterator = stream.iterator();
                while (iterator.hasNext()) {
                    Path path = iterator.next();
                    if (!Files.isRegularFile(path) || !path.getFileName().toString().endsWith(".mca")) {
                        continue;
                    }
                    count++;
                    latestModifiedMillis = Math.max(latestModifiedMillis, Files.getLastModifiedTime(path).toMillis());
                    totalBytes += Files.size(path);
                }
            }
            return new RegionSignature(count, latestModifiedMillis, totalBytes);
        }

        String serialize() {
            return this.count + "\n" + this.latestModifiedMillis + "\n" + this.totalBytes + "\n";
        }
    }

    private static final class MarkerWritingImporter implements IDataImporter {
        private final IDataImporter delegate;
        private final Path marker;
        private final String signature;
        private volatile boolean cancelled;

        private MarkerWritingImporter(IDataImporter delegate, Path marker, String signature) {
            this.delegate = delegate;
            this.marker = marker;
            this.signature = signature;
        }

        @Override
        public void runImport(IUpdateCallback updateCallback, ICompletionCallback completionCallback) {
            this.delegate.runImport(updateCallback, chunks -> {
                if (!this.cancelled) {
                    try {
                        Files.createDirectories(this.marker.getParent());
                        Files.writeString(this.marker, this.signature);
                    } catch (IOException e) {
                        Logger.error("Foxy auto-backfill failed to write marker " + this.marker, e);
                    }
                }
                completionCallback.onCompletion(chunks);
            });
        }

        @Override public WorldEngine getEngine() { return this.delegate.getEngine(); }
        @Override public boolean isRunning() { return this.delegate.isRunning(); }

        @Override
        public void shutdown() {
            this.cancelled = true;
            this.delegate.shutdown();
        }
    }
}

package com.github.foxy.client;

import com.github.foxy.client.compat.FlashbackCompat;
import com.github.foxy.client.config.FoxyConfig;
import com.github.foxy.client.core.RenderResourceReuse;
import com.github.foxy.client.mixin.sodium.AccessorSodiumWorldRenderer;
import com.github.foxy.common.Logger;
import com.github.foxy.common.StorageConfigUtil;
import com.github.foxy.common.config.ConfigBuildCtx;
import com.github.foxy.common.config.Serialization;
import com.github.foxy.common.config.compressors.ZSTDCompressor;
import com.github.foxy.common.config.section.SectionSerializationStorage;
import com.github.foxy.common.config.section.SectionStorage;
import com.github.foxy.common.config.section.SectionStorageConfig;
import com.github.foxy.common.config.storage.other.CompressionStorageAdaptor;
import com.github.foxy.common.config.storage.rocksdb.RocksDBStorageBackend;
import com.github.foxy.commonImpl.ImportManager;
import com.github.foxy.commonImpl.FoxyInstance;
import com.github.foxy.commonImpl.WorldIdentifier;
import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.storage.LevelResource;
import java.nio.file.Files;
import java.nio.file.Path;

public class FoxyClientInstance extends FoxyInstance {
    private final Config config;
    private final Path basePath;
    private final boolean noIngestOverride;
    public FoxyClientInstance() {
        super();
        var path = FlashbackCompat.getReplayStoragePath();
        this.noIngestOverride = path != null;
        if (path == null) {
            path = getBasePath();
        }
        this.basePath = path.normalize();
        this.config = StorageConfigUtil.getCreateStorageConfig(Config.class, c->c.version==1&&c.sectionStorageConfig!=null, ()->DEFAULT_STORAGE_CONFIG, this.basePath);
        this.updateDedicatedThreads();
    }

    @Override
    public void updateDedicatedThreads() {
        int target = FoxyConfig.CONFIG.serviceThreads;
        if (!FoxyConfig.CONFIG.dontUseSodiumBuilderThreads) {
            var swr = SodiumWorldRenderer.instanceNullable();
            if (swr != null) {
                var rsm = ((AccessorSodiumWorldRenderer) swr).getRenderSectionManager();
                if (rsm != null) {
                    this.setNumThreads(Math.max(1, target - rsm.getBuilder().getTotalThreadCount()));
                    return;
                }
            }
        }
        this.setNumThreads(target);
    }

    @Override
    protected ImportManager createImportManager() {
        return new ClientImportManager();
    }

    @Override
    protected SectionStorage createStorage(WorldIdentifier identifier) {
        var ctx = new ConfigBuildCtx();
        ctx.setProperty(ConfigBuildCtx.BASE_SAVE_PATH, this.basePath.toString());
        ctx.setProperty(ConfigBuildCtx.WORLD_IDENTIFIER, identifier.getWorldId());
        ctx.setProperty(ConfigBuildCtx.PLAYER_UUID, Minecraft.getInstance().getUser().getProfileId().toString().replace(':','-'));
        ctx.pushPath(ConfigBuildCtx.DEFAULT_STORAGE_PATH);
        return this.config.sectionStorageConfig.build(ctx);
    }

    public Path getStorageBasePath() {
        return this.basePath;
    }

    @Override
    public boolean isIngestEnabled(WorldIdentifier worldId) {
        return (!this.noIngestOverride) && FoxyConfig.CONFIG.ingestEnabled;
    }

    @Override
    public void shutdown() {
        super.shutdown();
        //Free the render resources cache since the entire instance is freed
        RenderResourceReuse.clearResources();
    }

    private static class Config {
        public int version = 1;
        public boolean disabled = false;
        public SectionStorageConfig sectionStorageConfig;
    }

    private static final Config DEFAULT_STORAGE_CONFIG;
    static {
        var config = new Config();
        config.sectionStorageConfig = StorageConfigUtil.createDefaultSerializer();
        DEFAULT_STORAGE_CONFIG = config;
    }

    private static Path getBasePath() {
        Path basePath = Minecraft.getInstance().gameDirectory.toPath().resolve(".foxy").resolve("saves");
        var iserver = Minecraft.getInstance().getSingleplayerServer();
        if (iserver != null) {
            basePath = iserver.getWorldPath(LevelResource.ROOT).resolve("foxy");
        } else {
            var netHandle = Minecraft.getInstance().gameMode;
            if (netHandle == null) {
                Logger.error("Network handle null");
                basePath = basePath.resolve("UNKNOWN");
            } else if (net.minecraft.client.Minecraft.getInstance().isConnectedToRealms()) {
                basePath = basePath.resolve("realms");
            } else {
                // Upstream reads gameMode.connection.getServerData(); that field is private on 1.20.1,
                // and this runs during handleLogin before Minecraft.player is assigned, so
                // getConnection() (== player.connection) would NPE. getCurrentServer() is public and
                // player-independent and yields the same ServerData for a normal multiplayer join.
                var info = net.minecraft.client.Minecraft.getInstance().getCurrentServer();
                if (info == null) {
                    Logger.error("Server info null");
                    basePath = basePath.resolve("UNKNOWN");
                } else {
                    basePath = basePath.resolve(info.ip.replace(":", "_"));
                }
            }
        }
        return basePath.toAbsolutePath();
    }
}

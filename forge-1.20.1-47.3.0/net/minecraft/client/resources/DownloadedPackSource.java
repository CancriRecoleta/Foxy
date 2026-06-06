//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.resources;

import com.google.common.hash.Hashing;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.repository.Pack.Position;
import net.minecraft.util.HttpUtil;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class DownloadedPackSource implements RepositorySource {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Pattern SHA1 = Pattern.compile("^[a-fA-F0-9]{40}$");
    private static final int MAX_PACK_SIZE_BYTES = 262144000;
    private static final int MAX_KEPT_PACKS = 10;
    private static final String SERVER_ID = "server";
    private static final Component SERVER_NAME = Component.translatable("resourcePack.server.name");
    private static final Component APPLYING_PACK_TEXT = Component.translatable("multiplayer.applyingPack");
    private final File serverPackDir;
    private final ReentrantLock downloadLock = new ReentrantLock();
    @Nullable
    private CompletableFuture<?> currentDownload;
    @Nullable
    private Pack serverPack;

    public DownloadedPackSource(File p_249798_) {
        this.serverPackDir = p_249798_;
    }

    public void loadPacks(Consumer<Pack> p_251994_) {
        if (this.serverPack != null) {
            p_251994_.accept(this.serverPack);
        }

    }

    private static Map<String, String> getDownloadHeaders() {
        return Map.of("X-Minecraft-Username", Minecraft.getInstance().getUser().getName(), "X-Minecraft-UUID", Minecraft.getInstance().getUser().getUuid(), "X-Minecraft-Version", SharedConstants.getCurrentVersion().getName(), "X-Minecraft-Version-ID", SharedConstants.getCurrentVersion().getId(), "X-Minecraft-Pack-Format", String.valueOf(SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES)), "User-Agent", "Minecraft Java/" + SharedConstants.getCurrentVersion().getName());
    }

    public CompletableFuture<?> downloadAndSelectResourcePack(URL p_249839_, String p_249218_, boolean p_251033_) {
        String $$3 = Hashing.sha1().hashString(p_249839_.toString(), StandardCharsets.UTF_8).toString();
        String $$4 = SHA1.matcher(p_249218_).matches() ? p_249218_ : "";
        this.downloadLock.lock();

        CompletableFuture var14;
        try {
            Minecraft $$5 = Minecraft.getInstance();
            File $$6 = new File(this.serverPackDir, $$3);
            CompletableFuture $$10;
            if ($$6.exists()) {
                $$10 = CompletableFuture.completedFuture("");
            } else {
                ProgressScreen $$8 = new ProgressScreen(p_251033_);
                Map<String, String> $$9 = getDownloadHeaders();
                $$5.executeBlocking(() -> {
                    $$5.setScreen($$8);
                });
                $$10 = HttpUtil.downloadTo($$6, p_249839_, $$9, 262144000, $$8, $$5.getProxy());
            }

            this.currentDownload = $$10.thenCompose((p_251155_) -> {
                if (!this.checkHash($$4, $$6)) {
                    return CompletableFuture.failedFuture(new RuntimeException("Hash check failure for file " + $$6 + ", see log"));
                } else {
                    $$5.execute(() -> {
                        if (!p_251033_) {
                            $$5.setScreen(new GenericDirtMessageScreen(APPLYING_PACK_TEXT));
                        }

                    });
                    return this.setServerPack($$6, PackSource.SERVER);
                }
            }).exceptionallyCompose((p_249744_) -> {
                return this.clearServerPack().thenAcceptAsync((p_251750_) -> {
                    LOGGER.warn("Pack application failed: {}, deleting file {}", p_249744_.getMessage(), $$6);
                    deleteQuietly($$6);
                }, Util.ioPool()).thenAcceptAsync((p_248937_) -> {
                    $$5.setScreen(new ConfirmScreen((p_249339_) -> {
                        if (p_249339_) {
                            $$5.setScreen((Screen)null);
                        } else {
                            ClientPacketListener $$2 = $$5.getConnection();
                            if ($$2 != null) {
                                $$2.getConnection().disconnect(Component.translatable("connect.aborted"));
                            }
                        }

                    }, Component.translatable("multiplayer.texturePrompt.failure.line1"), Component.translatable("multiplayer.texturePrompt.failure.line2"), CommonComponents.GUI_PROCEED, Component.translatable("menu.disconnect")));
                }, $$5);
            }).thenAcceptAsync((p_250279_) -> {
                this.clearOldDownloads();
            }, Util.ioPool());
            var14 = this.currentDownload;
        } finally {
            this.downloadLock.unlock();
        }

        return var14;
    }

    private static void deleteQuietly(File p_251727_) {
        try {
            Files.delete(p_251727_.toPath());
        } catch (IOException var2) {
            IOException $$1 = var2;
            LOGGER.warn("Failed to delete file {}: {}", p_251727_, $$1.getMessage());
        }

    }

    public CompletableFuture<Void> clearServerPack() {
        this.downloadLock.lock();

        CompletableFuture var1;
        try {
            if (this.currentDownload != null) {
                this.currentDownload.cancel(true);
            }

            this.currentDownload = null;
            if (this.serverPack == null) {
                return CompletableFuture.completedFuture((Object)null);
            }

            this.serverPack = null;
            var1 = Minecraft.getInstance().delayTextureReload();
        } finally {
            this.downloadLock.unlock();
        }

        return var1;
    }

    private boolean checkHash(String p_251365_, File p_249356_) {
        try {
            String $$2 = com.google.common.io.Files.asByteSource(p_249356_).hash(Hashing.sha1()).toString();
            if (p_251365_.isEmpty()) {
                LOGGER.info("Found file {} without verification hash", p_249356_);
                return true;
            }

            if ($$2.toLowerCase(Locale.ROOT).equals(p_251365_.toLowerCase(Locale.ROOT))) {
                LOGGER.info("Found file {} matching requested hash {}", p_249356_, p_251365_);
                return true;
            }

            LOGGER.warn("File {} had wrong hash (expected {}, found {}).", new Object[]{p_249356_, p_251365_, $$2});
        } catch (IOException var4) {
            IOException $$3 = var4;
            LOGGER.warn("File {} couldn't be hashed.", p_249356_, $$3);
        }

        return false;
    }

    private void clearOldDownloads() {
        if (this.serverPackDir.isDirectory()) {
            try {
                List<File> $$0 = new ArrayList(FileUtils.listFiles(this.serverPackDir, TrueFileFilter.TRUE, (IOFileFilter)null));
                $$0.sort(LastModifiedFileComparator.LASTMODIFIED_REVERSE);
                int $$1 = 0;
                Iterator var3 = $$0.iterator();

                while(var3.hasNext()) {
                    File $$2 = (File)var3.next();
                    if ($$1++ >= 10) {
                        LOGGER.info("Deleting old server resource pack {}", $$2.getName());
                        FileUtils.deleteQuietly($$2);
                    }
                }
            } catch (Exception var5) {
                Exception $$3 = var5;
                LOGGER.error("Error while deleting old server resource pack : {}", $$3.getMessage());
            }

        }
    }

    public CompletableFuture<Void> setServerPack(File p_249885_, PackSource p_251105_) {
        Pack.ResourcesSupplier $$2 = (p_255464_) -> {
            return new FilePackResources(p_255464_, p_249885_, false);
        };
        Pack.Info $$3 = Pack.readPackInfo("server", $$2);
        if ($$3 == null) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Invalid pack metadata at " + p_249885_));
        } else {
            LOGGER.info("Applying server pack {}", p_249885_);
            this.serverPack = Pack.create("server", SERVER_NAME, true, $$2, $$3, PackType.CLIENT_RESOURCES, Position.TOP, true, p_251105_);
            return Minecraft.getInstance().delayTextureReload();
        }
    }

    public CompletableFuture<Void> loadBundledResourcePack(LevelStorageSource.LevelStorageAccess p_248756_) {
        Path $$1 = p_248756_.getLevelPath(LevelResource.MAP_RESOURCE_FILE);
        return Files.exists($$1, new LinkOption[0]) && !Files.isDirectory($$1, new LinkOption[0]) ? this.setServerPack($$1.toFile(), PackSource.WORLD) : CompletableFuture.completedFuture((Object)null);
    }
}

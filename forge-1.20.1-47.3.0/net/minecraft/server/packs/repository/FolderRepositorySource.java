//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.packs.repository;

import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.linkfs.LinkFileSystem;
import net.minecraft.server.packs.repository.Pack.Position;
import org.slf4j.Logger;

public class FolderRepositorySource implements RepositorySource {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Path folder;
    private final PackType packType;
    private final PackSource packSource;

    public FolderRepositorySource(Path p_251796_, PackType p_251664_, PackSource p_250854_) {
        this.folder = p_251796_;
        this.packType = p_251664_;
        this.packSource = p_250854_;
    }

    private static String nameFromPath(Path p_248745_) {
        return p_248745_.getFileName().toString();
    }

    public void loadPacks(Consumer<Pack> p_250965_) {
        try {
            FileUtil.createDirectoriesSafe(this.folder);
            discoverPacks(this.folder, false, (p_248243_, p_248244_) -> {
                String $$3 = nameFromPath(p_248243_);
                Pack $$4 = Pack.readMetaAndCreate("file/" + $$3, Component.literal($$3), false, p_248244_, this.packType, Position.TOP, this.packSource);
                if ($$4 != null) {
                    p_250965_.accept($$4);
                }

            });
        } catch (IOException var3) {
            IOException $$1 = var3;
            LOGGER.warn("Failed to list packs in {}", this.folder, $$1);
        }

    }

    public static void discoverPacks(Path p_248794_, boolean p_255987_, BiConsumer<Path, Pack.ResourcesSupplier> p_248580_) throws IOException {
        DirectoryStream<Path> $$3 = Files.newDirectoryStream(p_248794_);

        try {
            Iterator var4 = $$3.iterator();

            while(var4.hasNext()) {
                Path $$4 = (Path)var4.next();
                Pack.ResourcesSupplier $$5 = detectPackResources($$4, p_255987_);
                if ($$5 != null) {
                    p_248580_.accept($$4, $$5);
                }
            }
        } catch (Throwable var8) {
            if ($$3 != null) {
                try {
                    $$3.close();
                } catch (Throwable var7) {
                    var8.addSuppressed(var7);
                }
            }

            throw var8;
        }

        if ($$3 != null) {
            $$3.close();
        }

    }

    @Nullable
    public static Pack.ResourcesSupplier detectPackResources(Path p_255665_, boolean p_255971_) {
        BasicFileAttributes $$5;
        try {
            $$5 = Files.readAttributes(p_255665_, BasicFileAttributes.class);
        } catch (NoSuchFileException var5) {
            return null;
        } catch (IOException var6) {
            IOException $$4 = var6;
            LOGGER.warn("Failed to read properties of '{}', ignoring", p_255665_, $$4);
            return null;
        }

        if ($$5.isDirectory() && Files.isRegularFile(p_255665_.resolve("pack.mcmeta"), new LinkOption[0])) {
            return (p_255538_) -> {
                return new PathPackResources(p_255538_, p_255665_, p_255971_);
            };
        } else {
            if ($$5.isRegularFile() && p_255665_.getFileName().toString().endsWith(".zip")) {
                FileSystem $$6 = p_255665_.getFileSystem();
                if ($$6 == FileSystems.getDefault() || $$6 instanceof LinkFileSystem) {
                    File $$7 = p_255665_.toFile();
                    return (p_255541_) -> {
                        return new FilePackResources(p_255541_, $$7, p_255971_);
                    };
                }
            }

            LOGGER.info("Found non-pack entry '{}', ignoring", p_255665_);
            return null;
        }
    }
}

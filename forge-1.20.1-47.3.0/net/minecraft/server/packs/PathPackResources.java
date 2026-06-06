//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.packs;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.IoSupplier;
import org.slf4j.Logger;

public class PathPackResources extends AbstractPackResources {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Joiner PATH_JOINER = Joiner.on("/");
    private final Path root;

    public PathPackResources(String p_255754_, Path p_256025_, boolean p_256260_) {
        super(p_255754_, p_256260_);
        this.root = p_256025_;
    }

    @Nullable
    public IoSupplier<InputStream> getRootResource(String... p_249041_) {
        FileUtil.validatePath(p_249041_);
        Path $$1 = FileUtil.resolvePath(this.root, List.of(p_249041_));
        return Files.exists($$1, new LinkOption[0]) ? IoSupplier.create($$1) : null;
    }

    public static boolean validatePath(Path p_249579_) {
        return true;
    }

    @Nullable
    public IoSupplier<InputStream> getResource(PackType p_249352_, ResourceLocation p_251715_) {
        Path $$2 = this.root.resolve(p_249352_.getDirectory()).resolve(p_251715_.getNamespace());
        return getResource(p_251715_, $$2);
    }

    public static IoSupplier<InputStream> getResource(ResourceLocation p_250145_, Path p_251046_) {
        return (IoSupplier)FileUtil.decomposePath(p_250145_.getPath()).get().map((p_251647_) -> {
            Path $$2 = FileUtil.resolvePath(p_251046_, p_251647_);
            return returnFileIfExists($$2);
        }, (p_248714_) -> {
            LOGGER.error("Invalid path {}: {}", p_250145_, p_248714_.message());
            return null;
        });
    }

    @Nullable
    private static IoSupplier<InputStream> returnFileIfExists(Path p_250506_) {
        return Files.exists(p_250506_, new LinkOption[0]) && validatePath(p_250506_) ? IoSupplier.create(p_250506_) : null;
    }

    public void listResources(PackType p_251452_, String p_249854_, String p_248650_, PackResources.ResourceOutput p_248572_) {
        FileUtil.decomposePath(p_248650_).get().ifLeft((p_250225_) -> {
            Path $$4 = this.root.resolve(p_251452_.getDirectory()).resolve(p_249854_);
            listPath(p_249854_, $$4, p_250225_, p_248572_);
        }).ifRight((p_252338_) -> {
            LOGGER.error("Invalid path {}: {}", p_248650_, p_252338_.message());
        });
    }

    public static void listPath(String p_249455_, Path p_249514_, List<String> p_251918_, PackResources.ResourceOutput p_249964_) {
        Path $$4 = FileUtil.resolvePath(p_249514_, p_251918_);

        try {
            Stream<Path> $$5 = Files.find($$4, Integer.MAX_VALUE, (p_250060_, p_250796_) -> {
                return p_250796_.isRegularFile();
            }, new FileVisitOption[0]);

            try {
                $$5.forEach((p_249092_) -> {
                    String $$4 = PATH_JOINER.join(p_249514_.relativize(p_249092_));
                    ResourceLocation $$5 = ResourceLocation.tryBuild(p_249455_, $$4);
                    if ($$5 == null) {
                        Util.logAndPauseIfInIde(String.format(Locale.ROOT, "Invalid path in pack: %s:%s, ignoring", p_249455_, $$4));
                    } else {
                        p_249964_.accept($$5, IoSupplier.create(p_249092_));
                    }

                });
            } catch (Throwable var9) {
                if ($$5 != null) {
                    try {
                        $$5.close();
                    } catch (Throwable var8) {
                        var9.addSuppressed(var8);
                    }
                }

                throw var9;
            }

            if ($$5 != null) {
                $$5.close();
            }
        } catch (NoSuchFileException var10) {
        } catch (IOException var11) {
            IOException $$6 = var11;
            LOGGER.error("Failed to list path {}", $$4, $$6);
        }

    }

    public Set<String> getNamespaces(PackType p_251896_) {
        Set<String> $$1 = Sets.newHashSet();
        Path $$2 = this.root.resolve(p_251896_.getDirectory());

        try {
            DirectoryStream<Path> $$3 = Files.newDirectoryStream($$2);

            try {
                Iterator var5 = $$3.iterator();

                while(var5.hasNext()) {
                    Path $$4 = (Path)var5.next();
                    String $$5 = $$4.getFileName().toString();
                    if ($$5.equals($$5.toLowerCase(Locale.ROOT))) {
                        $$1.add($$5);
                    } else {
                        LOGGER.warn("Ignored non-lowercase namespace: {} in {}", $$5, this.root);
                    }
                }
            } catch (Throwable var9) {
                if ($$3 != null) {
                    try {
                        $$3.close();
                    } catch (Throwable var8) {
                        var9.addSuppressed(var8);
                    }
                }

                throw var9;
            }

            if ($$3 != null) {
                $$3.close();
            }
        } catch (NoSuchFileException var10) {
        } catch (IOException var11) {
            IOException $$6 = var11;
            LOGGER.error("Failed to list path {}", $$2, $$6);
        }

        return $$1;
    }

    public void close() {
    }
}

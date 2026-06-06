//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.resource;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.FileUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class PathPackResources extends AbstractPackResources {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Path source;

    public PathPackResources(String packId, boolean isBuiltin, Path source) {
        super(packId, isBuiltin);
        this.source = source;
    }

    public Path getSource() {
        return this.source;
    }

    protected Path resolve(String... paths) {
        Path path = this.getSource();
        String[] var3 = paths;
        int var4 = paths.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            String name = var3[var5];
            path = path.resolve(name);
        }

        return path;
    }

    public @Nullable IoSupplier<InputStream> getRootResource(String... paths) {
        Path path = this.resolve(paths);
        return !Files.exists(path, new LinkOption[0]) ? null : IoSupplier.create(path);
    }

    public void listResources(PackType type, String namespace, String path, PackResources.ResourceOutput resourceOutput) {
        FileUtil.decomposePath(path).get().ifLeft((parts) -> {
            net.minecraft.server.packs.PathPackResources.listPath(namespace, this.resolve(type.getDirectory(), namespace).toAbsolutePath(), parts, resourceOutput);
        }).ifRight((dataResult) -> {
            LOGGER.error("Invalid path {}: {}", path, dataResult.message());
        });
    }

    public Set<String> getNamespaces(PackType type) {
        return this.getNamespacesFromDisk(type);
    }

    private @NotNull Set<String> getNamespacesFromDisk(PackType type) {
        try {
            Path root = this.resolve(type.getDirectory());
            Stream<Path> walker = Files.walk(root, 1, new FileVisitOption[0]);

            Set var4;
            try {
                Stream var10000 = walker.filter((x$0) -> {
                    return Files.isDirectory(x$0, new LinkOption[0]);
                });
                Objects.requireNonNull(root);
                var4 = (Set)var10000.map(root::relativize).filter((p) -> {
                    return p.getNameCount() > 0;
                }).map((p) -> {
                    return p.toString().replaceAll("/$", "");
                }).filter((s) -> {
                    return !s.isEmpty();
                }).collect(Collectors.toSet());
            } catch (Throwable var7) {
                if (walker != null) {
                    try {
                        walker.close();
                    } catch (Throwable var6) {
                        var7.addSuppressed(var6);
                    }
                }

                throw var7;
            }

            if (walker != null) {
                walker.close();
            }

            return var4;
        } catch (IOException var8) {
            return type == PackType.SERVER_DATA ? this.getNamespaces(PackType.CLIENT_RESOURCES) : Collections.emptySet();
        }
    }

    public IoSupplier<InputStream> getResource(PackType type, ResourceLocation location) {
        return this.getRootResource(getPathFromLocation(location.getPath().startsWith("lang/") ? PackType.CLIENT_RESOURCES : type, location));
    }

    private static String[] getPathFromLocation(PackType type, ResourceLocation location) {
        String[] parts = location.getPath().split("/");
        String[] result = new String[parts.length + 2];
        result[0] = type.getDirectory();
        result[1] = location.getNamespace();
        System.arraycopy(parts, 0, result, 2, parts.length);
        return result;
    }

    public void close() {
    }

    public String toString() {
        return String.format(Locale.ROOT, "%s: %s (%s)", this.getClass().getName(), this.packId(), this.getSource());
    }
}

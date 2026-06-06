//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.packs;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.Util;
import org.slf4j.Logger;

public class VanillaPackResourcesBuilder {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static Consumer<VanillaPackResourcesBuilder> developmentConfig = (p_251787_) -> {
    };
    private static final Map<PackType, Path> ROOT_DIR_BY_TYPE = (Map)Util.make(() -> {
        Class var0 = VanillaPackResources.class;
        synchronized(VanillaPackResources.class) {
            ImmutableMap.Builder<PackType, Path> $$0 = ImmutableMap.builder();
            PackType[] var2 = PackType.values();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                PackType $$1 = var2[var4];
                String $$2 = "/" + $$1.getDirectory() + "/.mcassetsroot";
                URL $$3 = VanillaPackResources.class.getResource($$2);
                if ($$3 == null) {
                    LOGGER.error("File {} does not exist in classpath", $$2);
                } else {
                    try {
                        URI $$4 = $$3.toURI();
                        String $$5 = $$4.getScheme();
                        if (!"jar".equals($$5) && !"file".equals($$5)) {
                            LOGGER.warn("Assets URL '{}' uses unexpected schema", $$4);
                        }

                        Path $$6 = safeGetPath($$4);
                        $$0.put($$1, $$6.getParent());
                    } catch (Exception var12) {
                        Exception $$7 = var12;
                        LOGGER.error("Couldn't resolve path to vanilla assets", $$7);
                    }
                }
            }

            return $$0.build();
        }
    });
    private final Set<Path> rootPaths = new LinkedHashSet();
    private final Map<PackType, Set<Path>> pathsForType = new EnumMap(PackType.class);
    private BuiltInMetadata metadata = BuiltInMetadata.of();
    private final Set<String> namespaces = new HashSet();

    public VanillaPackResourcesBuilder() {
    }

    private static Path safeGetPath(URI p_248652_) throws IOException {
        try {
            return Paths.get(p_248652_);
        } catch (FileSystemNotFoundException var3) {
        } catch (Throwable var4) {
            Throwable $$1 = var4;
            LOGGER.warn("Unable to get path for: {}", p_248652_, $$1);
        }

        try {
            FileSystems.newFileSystem(p_248652_, Collections.emptyMap());
        } catch (FileSystemAlreadyExistsException var2) {
        }

        return Paths.get(p_248652_);
    }

    private boolean validateDirPath(Path p_249112_) {
        if (!Files.exists(p_249112_, new LinkOption[0])) {
            return false;
        } else if (!Files.isDirectory(p_249112_, new LinkOption[0])) {
            throw new IllegalArgumentException("Path " + p_249112_.toAbsolutePath() + " is not directory");
        } else {
            return true;
        }
    }

    private void pushRootPath(Path p_251084_) {
        if (this.validateDirPath(p_251084_)) {
            this.rootPaths.add(p_251084_);
        }

    }

    private void pushPathForType(PackType p_250073_, Path p_252259_) {
        if (this.validateDirPath(p_252259_)) {
            ((Set)this.pathsForType.computeIfAbsent(p_250073_, (p_250639_) -> {
                return new LinkedHashSet();
            })).add(p_252259_);
        }

    }

    public VanillaPackResourcesBuilder pushJarResources() {
        ROOT_DIR_BY_TYPE.forEach((p_251514_, p_251979_) -> {
            this.pushRootPath(p_251979_.getParent());
            this.pushPathForType(p_251514_, p_251979_);
        });
        return this;
    }

    public VanillaPackResourcesBuilder pushClasspathResources(PackType p_251987_, Class<?> p_249062_) {
        Enumeration<URL> $$2 = null;

        try {
            $$2 = p_249062_.getClassLoader().getResources(p_251987_.getDirectory() + "/");
        } catch (IOException var8) {
        }

        while($$2 != null && $$2.hasMoreElements()) {
            URL $$3 = (URL)$$2.nextElement();

            try {
                URI $$4 = $$3.toURI();
                if ("file".equals($$4.getScheme())) {
                    Path $$5 = Paths.get($$4);
                    this.pushRootPath($$5.getParent());
                    this.pushPathForType(p_251987_, $$5);
                }
            } catch (Exception var7) {
                Exception $$6 = var7;
                LOGGER.error("Failed to extract path from {}", $$3, $$6);
            }
        }

        return this;
    }

    public VanillaPackResourcesBuilder applyDevelopmentConfig() {
        developmentConfig.accept(this);
        return this;
    }

    public VanillaPackResourcesBuilder pushUniversalPath(Path p_249464_) {
        this.pushRootPath(p_249464_);
        PackType[] var2 = PackType.values();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            PackType $$1 = var2[var4];
            this.pushPathForType($$1, p_249464_.resolve($$1.getDirectory()));
        }

        return this;
    }

    public VanillaPackResourcesBuilder pushAssetPath(PackType p_248623_, Path p_250065_) {
        this.pushRootPath(p_250065_);
        this.pushPathForType(p_248623_, p_250065_);
        return this;
    }

    public VanillaPackResourcesBuilder setMetadata(BuiltInMetadata p_249597_) {
        this.metadata = p_249597_;
        return this;
    }

    public VanillaPackResourcesBuilder exposeNamespace(String... p_250838_) {
        this.namespaces.addAll(Arrays.asList(p_250838_));
        return this;
    }

    public VanillaPackResources build() {
        Map<PackType, List<Path>> $$0 = new EnumMap(PackType.class);
        PackType[] var2 = PackType.values();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            PackType $$1 = var2[var4];
            List<Path> $$2 = copyAndReverse((Collection)this.pathsForType.getOrDefault($$1, Set.of()));
            $$0.put($$1, $$2);
        }

        return new VanillaPackResources(this.metadata, Set.copyOf(this.namespaces), copyAndReverse(this.rootPaths), $$0);
    }

    private static List<Path> copyAndReverse(Collection<Path> p_252072_) {
        List<Path> $$1 = new ArrayList(p_252072_);
        Collections.reverse($$1);
        return List.copyOf($$1);
    }
}

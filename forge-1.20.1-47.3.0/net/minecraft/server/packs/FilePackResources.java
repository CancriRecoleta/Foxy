//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.packs;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.IoSupplier;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class FilePackResources extends AbstractPackResources {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Splitter SPLITTER = Splitter.on('/').omitEmptyStrings().limit(3);
    private final File file;
    @Nullable
    private ZipFile zipFile;
    private boolean failedToLoad;

    public FilePackResources(String p_256076_, File p_255707_, boolean p_256556_) {
        super(p_256076_, p_256556_);
        this.file = p_255707_;
    }

    @Nullable
    private ZipFile getOrCreateZipFile() {
        if (this.failedToLoad) {
            return null;
        } else {
            if (this.zipFile == null) {
                try {
                    this.zipFile = new ZipFile(this.file);
                } catch (IOException var2) {
                    IOException $$0 = var2;
                    LOGGER.error("Failed to open pack {}", this.file, $$0);
                    this.failedToLoad = true;
                    return null;
                }
            }

            return this.zipFile;
        }
    }

    private static String getPathFromLocation(PackType p_250585_, ResourceLocation p_251470_) {
        return String.format(Locale.ROOT, "%s/%s/%s", p_250585_.getDirectory(), p_251470_.getNamespace(), p_251470_.getPath());
    }

    @Nullable
    public IoSupplier<InputStream> getRootResource(String... p_248514_) {
        return this.getResource(String.join("/", p_248514_));
    }

    public IoSupplier<InputStream> getResource(PackType p_249605_, ResourceLocation p_252147_) {
        return this.getResource(getPathFromLocation(p_249605_, p_252147_));
    }

    @Nullable
    private IoSupplier<InputStream> getResource(String p_251795_) {
        ZipFile $$1 = this.getOrCreateZipFile();
        if ($$1 == null) {
            return null;
        } else {
            ZipEntry $$2 = $$1.getEntry(p_251795_);
            return $$2 == null ? null : IoSupplier.create($$1, $$2);
        }
    }

    public Set<String> getNamespaces(PackType p_10238_) {
        ZipFile $$1 = this.getOrCreateZipFile();
        if ($$1 == null) {
            return Set.of();
        } else {
            Enumeration<? extends ZipEntry> $$2 = $$1.entries();
            Set<String> $$3 = Sets.newHashSet();

            while($$2.hasMoreElements()) {
                ZipEntry $$4 = (ZipEntry)$$2.nextElement();
                String $$5 = $$4.getName();
                if ($$5.startsWith(p_10238_.getDirectory() + "/")) {
                    List<String> $$6 = Lists.newArrayList(SPLITTER.split($$5));
                    if ($$6.size() > 1) {
                        String $$7 = (String)$$6.get(1);
                        if ($$7.equals($$7.toLowerCase(Locale.ROOT))) {
                            $$3.add($$7);
                        } else {
                            LOGGER.warn("Ignored non-lowercase namespace: {} in {}", $$7, this.file);
                        }
                    }
                }
            }

            return $$3;
        }
    }

    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }

    public void close() {
        if (this.zipFile != null) {
            IOUtils.closeQuietly(this.zipFile);
            this.zipFile = null;
        }

    }

    public void listResources(PackType p_250500_, String p_249598_, String p_251613_, PackResources.ResourceOutput p_250655_) {
        ZipFile $$4 = this.getOrCreateZipFile();
        if ($$4 != null) {
            Enumeration<? extends ZipEntry> $$5 = $$4.entries();
            String var10000 = p_250500_.getDirectory();
            String $$6 = var10000 + "/" + p_249598_ + "/";
            String $$7 = $$6 + p_251613_ + "/";

            while($$5.hasMoreElements()) {
                ZipEntry $$8 = (ZipEntry)$$5.nextElement();
                if (!$$8.isDirectory()) {
                    String $$9 = $$8.getName();
                    if ($$9.startsWith($$7)) {
                        String $$10 = $$9.substring($$6.length());
                        ResourceLocation $$11 = ResourceLocation.tryBuild(p_249598_, $$10);
                        if ($$11 != null) {
                            p_250655_.accept($$11, IoSupplier.create($$4, $$8));
                        } else {
                            LOGGER.warn("Invalid path in datapack: {}:{}, ignoring", p_249598_, $$10);
                        }
                    }
                }
            }

        }
    }
}

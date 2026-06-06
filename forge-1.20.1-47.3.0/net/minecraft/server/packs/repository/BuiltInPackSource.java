//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.packs.repository;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public abstract class BuiltInPackSource implements RepositorySource {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String VANILLA_ID = "vanilla";
    private final PackType packType;
    private final VanillaPackResources vanillaPack;
    private final ResourceLocation packDir;

    public BuiltInPackSource(PackType p_249137_, VanillaPackResources p_250453_, ResourceLocation p_251151_) {
        this.packType = p_249137_;
        this.vanillaPack = p_250453_;
        this.packDir = p_251151_;
    }

    public void loadPacks(Consumer<Pack> p_250708_) {
        Pack $$1 = this.createVanillaPack(this.vanillaPack);
        if ($$1 != null) {
            p_250708_.accept($$1);
        }

        this.listBundledPacks(p_250708_);
    }

    @Nullable
    protected abstract Pack createVanillaPack(PackResources var1);

    protected abstract Component getPackTitle(String var1);

    public VanillaPackResources getVanillaPack() {
        return this.vanillaPack;
    }

    private void listBundledPacks(Consumer<Pack> p_249128_) {
        Map<String, Function<String, Pack>> $$1 = new HashMap();
        Objects.requireNonNull($$1);
        this.populatePackList($$1::put);
        $$1.forEach((p_250371_, p_250946_) -> {
            Pack $$3 = (Pack)p_250946_.apply(p_250371_);
            if ($$3 != null) {
                p_249128_.accept($$3);
            }

        });
    }

    protected void populatePackList(BiConsumer<String, Function<String, Pack>> p_250341_) {
        this.vanillaPack.listRawPaths(this.packType, this.packDir, (p_250248_) -> {
            this.discoverPacksInPath(p_250248_, p_250341_);
        });
    }

    protected void discoverPacksInPath(@Nullable Path p_250013_, BiConsumer<String, Function<String, Pack>> p_249898_) {
        if (p_250013_ != null && Files.isDirectory(p_250013_, new LinkOption[0])) {
            try {
                FolderRepositorySource.discoverPacks(p_250013_, true, (p_252012_, p_249772_) -> {
                    p_249898_.accept(pathToId(p_252012_), (p_250601_) -> {
                        return this.createBuiltinPack(p_250601_, p_249772_, this.getPackTitle(p_250601_));
                    });
                });
            } catch (IOException var4) {
                IOException $$2 = var4;
                LOGGER.warn("Failed to discover packs in {}", p_250013_, $$2);
            }
        }

    }

    private static String pathToId(Path p_252048_) {
        return StringUtils.removeEnd(p_252048_.getFileName().toString(), ".zip");
    }

    @Nullable
    protected abstract Pack createBuiltinPack(String var1, Pack.ResourcesSupplier var2, Component var3);
}

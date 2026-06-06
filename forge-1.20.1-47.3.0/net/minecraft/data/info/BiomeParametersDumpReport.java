//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.data.info;

import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.biome.Climate.ParameterList;
import org.slf4j.Logger;

public class BiomeParametersDumpReport implements DataProvider {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Path topPath;
    private final CompletableFuture<HolderLookup.Provider> registries;
    private static final MapCodec<ResourceKey<Biome>> ENTRY_CODEC;
    private static final Codec<Climate.ParameterList<ResourceKey<Biome>>> CODEC;

    public BiomeParametersDumpReport(PackOutput p_256322_, CompletableFuture<HolderLookup.Provider> p_256222_) {
        this.topPath = p_256322_.getOutputFolder(Target.REPORTS).resolve("biome_parameters");
        this.registries = p_256222_;
    }

    public CompletableFuture<?> run(CachedOutput p_254091_) {
        return this.registries.thenCompose((p_274755_) -> {
            DynamicOps<JsonElement> $$2 = RegistryOps.create(JsonOps.INSTANCE, (HolderLookup.Provider)p_274755_);
            List<CompletableFuture<?>> $$3 = new ArrayList();
            MultiNoiseBiomeSourceParameterList.knownPresets().forEach((p_274759_, p_274760_) -> {
                $$3.add(dumpValue(this.createPath(p_274759_.id()), p_254091_, $$2, CODEC, p_274760_));
            });
            return CompletableFuture.allOf((CompletableFuture[])$$3.toArray((p_253398_) -> {
                return new CompletableFuture[p_253398_];
            }));
        });
    }

    private static <E> CompletableFuture<?> dumpValue(Path p_254407_, CachedOutput p_254093_, DynamicOps<JsonElement> p_253788_, Encoder<E> p_254276_, E p_254073_) {
        Optional<JsonElement> $$5 = p_254276_.encodeStart(p_253788_, p_254073_).resultOrPartial((p_236195_) -> {
            LOGGER.error("Couldn't serialize element {}: {}", p_254407_, p_236195_);
        });
        return $$5.isPresent() ? DataProvider.saveStable(p_254093_, (JsonElement)$$5.get(), p_254407_) : CompletableFuture.completedFuture((Object)null);
    }

    private Path createPath(ResourceLocation p_236179_) {
        return this.topPath.resolve(p_236179_.getNamespace()).resolve(p_236179_.getPath() + ".json");
    }

    public final String getName() {
        return "Biome Parameters";
    }

    static {
        ENTRY_CODEC = ResourceKey.codec(Registries.BIOME).fieldOf("biome");
        CODEC = ParameterList.codec(ENTRY_CODEC).fieldOf("biomes").codec();
    }
}

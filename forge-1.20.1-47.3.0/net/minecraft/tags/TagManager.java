//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.tags;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.common.ForgeHooks;

public class TagManager implements PreparableReloadListener {
    private static final Map<ResourceKey<? extends Registry<?>>, String> CUSTOM_REGISTRY_DIRECTORIES;
    private final RegistryAccess registryAccess;
    private List<LoadResult<?>> results = List.of();

    public TagManager(RegistryAccess p_144572_) {
        this.registryAccess = p_144572_;
    }

    public List<LoadResult<?>> getResult() {
        return this.results;
    }

    public static String getTagDir(ResourceKey<? extends Registry<?>> p_203919_) {
        String s = (String)CUSTOM_REGISTRY_DIRECTORIES.get(p_203919_);
        return s != null ? s : "tags/" + ForgeHooks.prefixNamespace(p_203919_.location());
    }

    public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier p_13482_, ResourceManager p_13483_, ProfilerFiller p_13484_, ProfilerFiller p_13485_, Executor p_13486_, Executor p_13487_) {
        List<? extends CompletableFuture<? extends LoadResult<?>>> list = this.registryAccess.registries().map((p_203927_) -> {
            return this.createLoader(p_13483_, p_13486_, p_203927_);
        }).toList();
        CompletableFuture var10000 = CompletableFuture.allOf((CompletableFuture[])list.toArray((p_203906_) -> {
            return new CompletableFuture[p_203906_];
        }));
        Objects.requireNonNull(p_13482_);
        return var10000.thenCompose(p_13482_::wait).thenAcceptAsync((p_203917_) -> {
            this.results = (List)list.stream().map(CompletableFuture::join).collect(Collectors.toUnmodifiableList());
        }, p_13487_);
    }

    private <T> CompletableFuture<LoadResult<T>> createLoader(ResourceManager p_203908_, Executor p_203909_, RegistryAccess.RegistryEntry<T> p_203910_) {
        ResourceKey<? extends Registry<T>> resourcekey = p_203910_.key();
        Registry<T> registry = p_203910_.value();
        TagLoader<Holder<T>> tagloader = new TagLoader((p_258247_) -> {
            return registry.getHolder(ResourceKey.create(resourcekey, p_258247_));
        }, getTagDir(resourcekey));
        return CompletableFuture.supplyAsync(() -> {
            return new LoadResult(resourcekey, tagloader.loadAndBuild(p_203908_));
        }, p_203909_);
    }

    static {
        CUSTOM_REGISTRY_DIRECTORIES = Map.of(Registries.BLOCK, "tags/blocks", Registries.ENTITY_TYPE, "tags/entity_types", Registries.FLUID, "tags/fluids", Registries.GAME_EVENT, "tags/game_events", Registries.ITEM, "tags/items");
    }

    public static record LoadResult<T>(ResourceKey<? extends Registry<T>> key, Map<ResourceLocation, Collection<Holder<T>>> tags) {
        public LoadResult(ResourceKey<? extends Registry<T>> key, Map<ResourceLocation, Collection<Holder<T>>> tags) {
            this.key = key;
            this.tags = tags;
        }

        public ResourceKey<? extends Registry<T>> key() {
            return this.key;
        }

        public Map<ResourceLocation, Collection<Holder<T>>> tags() {
            return this.tags;
        }
    }
}

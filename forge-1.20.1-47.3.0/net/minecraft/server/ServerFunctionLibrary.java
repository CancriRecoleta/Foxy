//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class ServerFunctionLibrary implements PreparableReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final FileToIdConverter LISTER = new FileToIdConverter("functions", ".mcfunction");
    private volatile Map<ResourceLocation, CommandFunction> functions = ImmutableMap.of();
    private final TagLoader<CommandFunction> tagsLoader = new TagLoader(this::getFunction, "tags/functions");
    private volatile Map<ResourceLocation, Collection<CommandFunction>> tags = Map.of();
    private final int functionCompilationLevel;
    private final CommandDispatcher<CommandSourceStack> dispatcher;

    public Optional<CommandFunction> getFunction(ResourceLocation p_136090_) {
        return Optional.ofNullable((CommandFunction)this.functions.get(p_136090_));
    }

    public Map<ResourceLocation, CommandFunction> getFunctions() {
        return this.functions;
    }

    public Collection<CommandFunction> getTag(ResourceLocation p_214328_) {
        return (Collection)this.tags.getOrDefault(p_214328_, List.of());
    }

    public Iterable<ResourceLocation> getAvailableTags() {
        return this.tags.keySet();
    }

    public ServerFunctionLibrary(int p_136053_, CommandDispatcher<CommandSourceStack> p_136054_) {
        this.functionCompilationLevel = p_136053_;
        this.dispatcher = p_136054_;
    }

    public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier p_136057_, ResourceManager p_136058_, ProfilerFiller p_136059_, ProfilerFiller p_136060_, Executor p_136061_, Executor p_136062_) {
        CompletableFuture<Map<ResourceLocation, List<TagLoader.EntryWithSource>>> $$6 = CompletableFuture.supplyAsync(() -> {
            return this.tagsLoader.load(p_136058_);
        }, p_136061_);
        CompletableFuture<Map<ResourceLocation, CompletableFuture<CommandFunction>>> $$7 = CompletableFuture.supplyAsync(() -> {
            return LISTER.listMatchingResources(p_136058_);
        }, p_136061_).thenCompose((p_248095_) -> {
            Map<ResourceLocation, CompletableFuture<CommandFunction>> $$2 = Maps.newHashMap();
            CommandSourceStack $$3 = new CommandSourceStack(CommandSource.NULL, Vec3.ZERO, Vec2.ZERO, (ServerLevel)null, this.functionCompilationLevel, "", CommonComponents.EMPTY, (MinecraftServer)null, (Entity)null);
            Iterator var5 = p_248095_.entrySet().iterator();

            while(var5.hasNext()) {
                Map.Entry<ResourceLocation, Resource> $$4 = (Map.Entry)var5.next();
                ResourceLocation $$5 = (ResourceLocation)$$4.getKey();
                ResourceLocation $$6 = LISTER.fileToId($$5);
                $$2.put($$6, CompletableFuture.supplyAsync(() -> {
                    List<String> $$3x = readLines((Resource)$$4.getValue());
                    return CommandFunction.fromLines($$6, this.dispatcher, $$3, $$3x);
                }, p_136061_));
            }

            CompletableFuture<?>[] $$7 = (CompletableFuture[])$$2.values().toArray(new CompletableFuture[0]);
            return CompletableFuture.allOf($$7).handle((p_179949_, p_179950_) -> {
                return $$2;
            });
        });
        CompletableFuture var10000 = $$6.thenCombine($$7, Pair::of);
        Objects.requireNonNull(p_136057_);
        return var10000.thenCompose(p_136057_::wait).thenAcceptAsync((p_179944_) -> {
            Map<ResourceLocation, CompletableFuture<CommandFunction>> $$1 = (Map)p_179944_.getSecond();
            ImmutableMap.Builder<ResourceLocation, CommandFunction> $$2 = ImmutableMap.builder();
            $$1.forEach((p_179941_, p_179942_) -> {
                p_179942_.handle((p_179954_, p_179955_) -> {
                    if (p_179955_ != null) {
                        LOGGER.error("Failed to load function {}", p_179941_, p_179955_);
                    } else {
                        $$2.put(p_179941_, p_179954_);
                    }

                    return null;
                }).join();
            });
            this.functions = $$2.build();
            this.tags = this.tagsLoader.build((Map)p_179944_.getFirst());
        }, p_136062_);
    }

    private static List<String> readLines(Resource p_214317_) {
        try {
            BufferedReader $$1 = p_214317_.openAsReader();

            List var2;
            try {
                var2 = $$1.lines().toList();
            } catch (Throwable var5) {
                if ($$1 != null) {
                    try {
                        $$1.close();
                    } catch (Throwable var4) {
                        var5.addSuppressed(var4);
                    }
                }

                throw var5;
            }

            if ($$1 != null) {
                $$1.close();
            }

            return var2;
        } catch (IOException var6) {
            IOException $$2 = var6;
            throw new CompletionException($$2);
        }
    }
}

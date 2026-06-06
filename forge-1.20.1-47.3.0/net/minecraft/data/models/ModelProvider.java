//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.data.models;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.data.models.model.DelegatedModel;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModelProvider implements DataProvider {
    private final PackOutput.PathProvider blockStatePathProvider;
    private final PackOutput.PathProvider modelPathProvider;

    public ModelProvider(PackOutput p_252226_) {
        this.blockStatePathProvider = p_252226_.createPathProvider(Target.RESOURCE_PACK, "blockstates");
        this.modelPathProvider = p_252226_.createPathProvider(Target.RESOURCE_PACK, "models");
    }

    public CompletableFuture<?> run(CachedOutput p_253790_) {
        Map<Block, BlockStateGenerator> $$1 = Maps.newHashMap();
        Consumer<BlockStateGenerator> $$2 = (p_125120_) -> {
            Block $$2 = p_125120_.getBlock();
            BlockStateGenerator $$3 = (BlockStateGenerator)$$1.put($$2, p_125120_);
            if ($$3 != null) {
                throw new IllegalStateException("Duplicate blockstate definition for " + $$2);
            }
        };
        Map<ResourceLocation, Supplier<JsonElement>> $$3 = Maps.newHashMap();
        Set<Item> $$4 = Sets.newHashSet();
        BiConsumer<ResourceLocation, Supplier<JsonElement>> $$5 = (p_125123_, p_125124_) -> {
            Supplier<JsonElement> $$3x = (Supplier)$$3.put(p_125123_, p_125124_);
            if ($$3x != null) {
                throw new IllegalStateException("Duplicate model definition for " + p_125123_);
            }
        };
        Objects.requireNonNull($$4);
        Consumer<Item> $$6 = $$4::add;
        (new BlockModelGenerators($$2, $$5, $$6)).run();
        (new ItemModelGenerators($$5)).run();
        List<Block> $$7 = BuiltInRegistries.BLOCK.stream().filter((p_125117_) -> {
            return !$$1.containsKey(p_125117_);
        }).toList();
        if (!$$7.isEmpty()) {
            throw new IllegalStateException("Missing blockstate definitions for: " + $$7);
        } else {
            BuiltInRegistries.BLOCK.forEach((p_125128_) -> {
                Item $$3x = (Item)Item.BY_BLOCK.get(p_125128_);
                if ($$3x != null) {
                    if ($$4.contains($$3x)) {
                        return;
                    }

                    ResourceLocation $$4x = ModelLocationUtils.getModelLocation($$3x);
                    if (!$$3.containsKey($$4x)) {
                        $$3.put($$4x, new DelegatedModel(ModelLocationUtils.getModelLocation(p_125128_)));
                    }
                }

            });
            CompletableFuture[] var10000 = new CompletableFuture[]{this.saveCollection(p_253790_, $$1, (p_248016_) -> {
                return this.blockStatePathProvider.json(p_248016_.builtInRegistryHolder().key().location());
            }), null};
            PackOutput.PathProvider var10006 = this.modelPathProvider;
            Objects.requireNonNull(var10006);
            var10000[1] = this.saveCollection(p_253790_, $$3, var10006::json);
            return CompletableFuture.allOf(var10000);
        }
    }

    private <T> CompletableFuture<?> saveCollection(CachedOutput p_254549_, Map<T, ? extends Supplier<JsonElement>> p_253779_, Function<T, Path> p_254013_) {
        return CompletableFuture.allOf((CompletableFuture[])p_253779_.entrySet().stream().map((p_253408_) -> {
            Path $$3 = (Path)p_254013_.apply(p_253408_.getKey());
            JsonElement $$4 = (JsonElement)((Supplier)p_253408_.getValue()).get();
            return DataProvider.saveStable(p_254549_, $$4, $$3);
        }).toArray((p_253409_) -> {
            return new CompletableFuture[p_253409_];
        }));
    }

    public final String getName() {
        return "Model Definitions";
    }
}

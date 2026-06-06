//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.font;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.providers.GlyphProviderDefinition;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.DependencySorter;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class FontManager implements PreparableReloadListener, AutoCloseable {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final String FONTS_PATH = "fonts.json";
    public static final ResourceLocation MISSING_FONT = new ResourceLocation("minecraft", "missing");
    private static final FileToIdConverter FONT_DEFINITIONS = FileToIdConverter.json("font");
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    private final FontSet missingFontSet;
    private final List<GlyphProvider> providersToClose = new ArrayList();
    private final Map<ResourceLocation, FontSet> fontSets = new HashMap();
    private final TextureManager textureManager;
    private Map<ResourceLocation, ResourceLocation> renames = ImmutableMap.of();

    public FontManager(TextureManager p_95005_) {
        this.textureManager = p_95005_;
        this.missingFontSet = (FontSet)Util.make(new FontSet(p_95005_, MISSING_FONT), (p_95010_) -> {
            p_95010_.reload(Lists.newArrayList(new GlyphProvider[]{new AllMissingGlyphProvider()}));
        });
    }

    public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier p_285160_, ResourceManager p_285231_, ProfilerFiller p_285232_, ProfilerFiller p_285262_, Executor p_284975_, Executor p_285218_) {
        p_285232_.startTick();
        p_285232_.endTick();
        CompletableFuture var10000 = this.prepare(p_285231_, p_284975_);
        Objects.requireNonNull(p_285160_);
        return var10000.thenCompose(p_285160_::wait).thenAcceptAsync((p_284609_) -> {
            this.apply(p_284609_, p_285262_);
        }, p_285218_);
    }

    private CompletableFuture<Preparation> prepare(ResourceManager p_285252_, Executor p_284969_) {
        List<CompletableFuture<UnresolvedBuilderBundle>> $$2 = new ArrayList();
        Iterator var4 = FONT_DEFINITIONS.listMatchingResourceStacks(p_285252_).entrySet().iterator();

        while(var4.hasNext()) {
            Map.Entry<ResourceLocation, List<Resource>> $$3 = (Map.Entry)var4.next();
            ResourceLocation $$4 = FONT_DEFINITIONS.fileToId((ResourceLocation)$$3.getKey());
            $$2.add(CompletableFuture.supplyAsync(() -> {
                List<Pair<BuilderId, GlyphProviderDefinition>> $$4x = loadResourceStack((List)$$3.getValue(), $$4);
                UnresolvedBuilderBundle $$5 = new UnresolvedBuilderBundle($$4);
                Iterator var7 = $$4x.iterator();

                while(var7.hasNext()) {
                    Pair<BuilderId, GlyphProviderDefinition> $$6 = (Pair)var7.next();
                    BuilderId $$7 = (BuilderId)$$6.getFirst();
                    ((GlyphProviderDefinition)$$6.getSecond()).unpack().ifLeft((p_286126_) -> {
                        CompletableFuture<Optional<GlyphProvider>> $$5x = this.safeLoad($$7, p_286126_, p_285252_, p_284969_);
                        $$5.add($$7, $$5x);
                    }).ifRight((p_286129_) -> {
                        $$5.add($$7, p_286129_);
                    });
                }

                return $$5;
            }, p_284969_));
        }

        return Util.sequence($$2).thenCompose((p_284592_) -> {
            List<CompletableFuture<Optional<GlyphProvider>>> $$2 = (List)p_284592_.stream().flatMap(UnresolvedBuilderBundle::listBuilders).collect(Collectors.toCollection(ArrayList::new));
            GlyphProvider $$3 = new AllMissingGlyphProvider();
            $$2.add(CompletableFuture.completedFuture(Optional.of($$3)));
            return Util.sequence($$2).thenCompose((p_284618_) -> {
                Map<ResourceLocation, List<GlyphProvider>> $$4 = this.resolveProviders(p_284592_);
                CompletableFuture<?>[] $$5 = (CompletableFuture[])$$4.values().stream().map((p_284585_) -> {
                    return CompletableFuture.runAsync(() -> {
                        this.finalizeProviderLoading(p_284585_, $$3);
                    }, p_284969_);
                }).toArray((p_284587_) -> {
                    return new CompletableFuture[p_284587_];
                });
                return CompletableFuture.allOf($$5).thenApply((p_284595_) -> {
                    List<GlyphProvider> $$3 = p_284618_.stream().flatMap(Optional::stream).toList();
                    return new Preparation($$4, $$3);
                });
            });
        });
    }

    private CompletableFuture<Optional<GlyphProvider>> safeLoad(BuilderId p_285113_, GlyphProviderDefinition.Loader p_286561_, ResourceManager p_285424_, Executor p_285371_) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return Optional.of(p_286561_.load(p_285424_));
            } catch (Exception var4) {
                Exception $$3 = var4;
                LOGGER.warn("Failed to load builder {}, rejecting", p_285113_, $$3);
                return Optional.empty();
            }
        }, p_285371_);
    }

    private Map<ResourceLocation, List<GlyphProvider>> resolveProviders(List<UnresolvedBuilderBundle> p_285282_) {
        Map<ResourceLocation, List<GlyphProvider>> $$1 = new HashMap();
        DependencySorter<ResourceLocation, UnresolvedBuilderBundle> $$2 = new DependencySorter();
        p_285282_.forEach((p_284626_) -> {
            $$2.addEntry(p_284626_.fontId, p_284626_);
        });
        $$2.orderByDependencies((p_284620_, p_284621_) -> {
            Objects.requireNonNull($$1);
            p_284621_.resolve($$1::get).ifPresent((p_284590_) -> {
                $$1.put(p_284620_, p_284590_);
            });
        });
        return $$1;
    }

    private void finalizeProviderLoading(List<GlyphProvider> p_285520_, GlyphProvider p_285397_) {
        p_285520_.add(0, p_285397_);
        IntSet $$2 = new IntOpenHashSet();
        Iterator var4 = p_285520_.iterator();

        while(var4.hasNext()) {
            GlyphProvider $$3 = (GlyphProvider)var4.next();
            $$2.addAll($$3.getSupportedGlyphs());
        }

        $$2.forEach((p_284614_) -> {
            if (p_284614_ != 32) {
                Iterator var2 = Lists.reverse(p_285520_).iterator();

                while(var2.hasNext()) {
                    GlyphProvider $$2 = (GlyphProvider)var2.next();
                    if ($$2.getGlyph(p_284614_) != null) {
                        break;
                    }
                }

            }
        });
    }

    private void apply(Preparation p_284939_, ProfilerFiller p_285407_) {
        p_285407_.startTick();
        p_285407_.push("closing");
        this.fontSets.values().forEach(FontSet::close);
        this.fontSets.clear();
        this.providersToClose.forEach(GlyphProvider::close);
        this.providersToClose.clear();
        p_285407_.popPush("reloading");
        p_284939_.providers().forEach((p_284627_, p_284628_) -> {
            FontSet $$2 = new FontSet(this.textureManager, p_284627_);
            $$2.reload(Lists.reverse(p_284628_));
            this.fontSets.put(p_284627_, $$2);
        });
        this.providersToClose.addAll(p_284939_.allProviders);
        p_285407_.pop();
        p_285407_.endTick();
        if (!this.fontSets.containsKey(this.getActualId(Minecraft.DEFAULT_FONT))) {
            throw new IllegalStateException("Default font failed to load");
        }
    }

    private static List<Pair<BuilderId, GlyphProviderDefinition>> loadResourceStack(List<Resource> p_284976_, ResourceLocation p_285272_) {
        List<Pair<BuilderId, GlyphProviderDefinition>> $$2 = new ArrayList();
        Iterator var3 = p_284976_.iterator();

        while(var3.hasNext()) {
            Resource $$3 = (Resource)var3.next();

            try {
                Reader $$4 = $$3.openAsReader();

                try {
                    JsonElement $$5 = (JsonElement)GSON.fromJson($$4, JsonElement.class);
                    FontDefinitionFile $$6 = (FontDefinitionFile)Util.getOrThrow(net.minecraft.client.gui.font.FontManager.FontDefinitionFile.CODEC.parse(JsonOps.INSTANCE, $$5), JsonParseException::new);
                    List<GlyphProviderDefinition> $$7 = $$6.providers;

                    for(int $$8 = $$7.size() - 1; $$8 >= 0; --$$8) {
                        BuilderId $$9 = new BuilderId(p_285272_, $$3.sourcePackId(), $$8);
                        $$2.add(Pair.of($$9, (GlyphProviderDefinition)$$7.get($$8)));
                    }
                } catch (Throwable var12) {
                    if ($$4 != null) {
                        try {
                            $$4.close();
                        } catch (Throwable var11) {
                            var12.addSuppressed(var11);
                        }
                    }

                    throw var12;
                }

                if ($$4 != null) {
                    $$4.close();
                }
            } catch (Exception var13) {
                Exception $$10 = var13;
                LOGGER.warn("Unable to load font '{}' in {} in resourcepack: '{}'", new Object[]{p_285272_, "fonts.json", $$3.sourcePackId(), $$10});
            }
        }

        return $$2;
    }

    public void setRenames(Map<ResourceLocation, ResourceLocation> p_95012_) {
        this.renames = p_95012_;
    }

    private ResourceLocation getActualId(ResourceLocation p_285141_) {
        return (ResourceLocation)this.renames.getOrDefault(p_285141_, p_285141_);
    }

    public Font createFont() {
        return new Font((p_284586_) -> {
            return (FontSet)this.fontSets.getOrDefault(this.getActualId(p_284586_), this.missingFontSet);
        }, false);
    }

    public Font createFontFilterFishy() {
        return new Font((p_284596_) -> {
            return (FontSet)this.fontSets.getOrDefault(this.getActualId(p_284596_), this.missingFontSet);
        }, true);
    }

    public void close() {
        this.fontSets.values().forEach(FontSet::close);
        this.providersToClose.forEach(GlyphProvider::close);
        this.missingFontSet.close();
    }

    @OnlyIn(Dist.CLIENT)
    static record BuilderId(ResourceLocation fontId, String pack, int index) {
        BuilderId(ResourceLocation fontId, String pack, int index) {
            this.fontId = fontId;
            this.pack = pack;
            this.index = index;
        }

        public String toString() {
            return "(" + this.fontId + ": builder #" + this.index + " from pack " + this.pack + ")";
        }

        public ResourceLocation fontId() {
            return this.fontId;
        }

        public String pack() {
            return this.pack;
        }

        public int index() {
            return this.index;
        }
    }

    @OnlyIn(Dist.CLIENT)
    static record Preparation(Map<ResourceLocation, List<GlyphProvider>> providers, List<GlyphProvider> allProviders) {
        Preparation(Map<ResourceLocation, List<GlyphProvider>> providers, List<GlyphProvider> allProviders) {
            this.providers = providers;
            this.allProviders = allProviders;
        }

        public Map<ResourceLocation, List<GlyphProvider>> providers() {
            return this.providers;
        }

        public List<GlyphProvider> allProviders() {
            return this.allProviders;
        }
    }

    @OnlyIn(Dist.CLIENT)
    static record FontDefinitionFile(List<GlyphProviderDefinition> providers) {
        public static final Codec<FontDefinitionFile> CODEC = RecordCodecBuilder.create((p_286425_) -> {
            return p_286425_.group(GlyphProviderDefinition.CODEC.listOf().fieldOf("providers").forGetter(FontDefinitionFile::providers)).apply(p_286425_, FontDefinitionFile::new);
        });

        private FontDefinitionFile(List<GlyphProviderDefinition> providers) {
            this.providers = providers;
        }

        public List<GlyphProviderDefinition> providers() {
            return this.providers;
        }
    }

    @OnlyIn(Dist.CLIENT)
    static record UnresolvedBuilderBundle(ResourceLocation fontId, List<BuilderResult> builders, Set<ResourceLocation> dependencies) implements DependencySorter.Entry<ResourceLocation> {
        public UnresolvedBuilderBundle(ResourceLocation p_284984_) {
            this(p_284984_, new ArrayList(), new HashSet());
        }

        private UnresolvedBuilderBundle(ResourceLocation fontId, List<BuilderResult> builders, Set<ResourceLocation> dependencies) {
            this.fontId = fontId;
            this.builders = builders;
            this.dependencies = dependencies;
        }

        public void add(BuilderId p_286837_, GlyphProviderDefinition.Reference p_286500_) {
            this.builders.add(new BuilderResult(p_286837_, Either.right(p_286500_.id())));
            this.dependencies.add(p_286500_.id());
        }

        public void add(BuilderId p_284935_, CompletableFuture<Optional<GlyphProvider>> p_284966_) {
            this.builders.add(new BuilderResult(p_284935_, Either.left(p_284966_)));
        }

        private Stream<CompletableFuture<Optional<GlyphProvider>>> listBuilders() {
            return this.builders.stream().flatMap((p_285041_) -> {
                return p_285041_.result.left().stream();
            });
        }

        public Optional<List<GlyphProvider>> resolve(Function<ResourceLocation, List<GlyphProvider>> p_285118_) {
            List<GlyphProvider> $$1 = new ArrayList();
            Iterator var3 = this.builders.iterator();

            while(var3.hasNext()) {
                BuilderResult $$2 = (BuilderResult)var3.next();
                Optional<List<GlyphProvider>> $$3 = $$2.resolve(p_285118_);
                if (!$$3.isPresent()) {
                    return Optional.empty();
                }

                $$1.addAll((Collection)$$3.get());
            }

            return Optional.of($$1);
        }

        public void visitRequiredDependencies(Consumer<ResourceLocation> p_285391_) {
            this.dependencies.forEach(p_285391_);
        }

        public void visitOptionalDependencies(Consumer<ResourceLocation> p_285405_) {
        }

        public ResourceLocation fontId() {
            return this.fontId;
        }

        public List<BuilderResult> builders() {
            return this.builders;
        }

        public Set<ResourceLocation> dependencies() {
            return this.dependencies;
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static record BuilderResult(BuilderId id, Either<CompletableFuture<Optional<GlyphProvider>>, ResourceLocation> result) {
        BuilderResult(BuilderId id, Either<CompletableFuture<Optional<GlyphProvider>>, ResourceLocation> result) {
            this.id = id;
            this.result = result;
        }

        public Optional<List<GlyphProvider>> resolve(Function<ResourceLocation, List<GlyphProvider>> p_284942_) {
            return (Optional)this.result.map((p_285332_) -> {
                return ((Optional)p_285332_.join()).map(List::of);
            }, (p_285367_) -> {
                List<GlyphProvider> $$2 = (List)p_284942_.apply(p_285367_);
                if ($$2 == null) {
                    FontManager.LOGGER.warn("Can't find font {} referenced by builder {}, either because it's missing, failed to load or is part of loading cycle", p_285367_, this.id);
                    return Optional.empty();
                } else {
                    return Optional.of($$2);
                }
            });
        }

        public BuilderId id() {
            return this.id;
        }

        public Either<CompletableFuture<Optional<GlyphProvider>>, ResourceLocation> result() {
            return this.result;
        }
    }
}

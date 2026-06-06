//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.data.tags;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagFile;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagManager;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.extensions.IForgeTagAppender;
import net.minecraftforge.registries.RegistryManager;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public abstract class TagsProvider<T> implements DataProvider {
    private static final Logger LOGGER = LogUtils.getLogger();
    protected final PackOutput.PathProvider pathProvider;
    private final CompletableFuture<HolderLookup.Provider> lookupProvider;
    private final CompletableFuture<Void> contentsDone;
    private final CompletableFuture<TagLookup<T>> parentProvider;
    protected final ResourceKey<? extends Registry<T>> registryKey;
    protected final Map<ResourceLocation, TagBuilder> builders;
    protected final String modId;
    protected final @Nullable ExistingFileHelper existingFileHelper;
    private final ExistingFileHelper.IResourceType resourceType;
    private final ExistingFileHelper.IResourceType elementResourceType;

    /** @deprecated */
    protected TagsProvider(PackOutput p_256596_, ResourceKey<? extends Registry<T>> p_255886_, CompletableFuture<HolderLookup.Provider> p_256513_) {
        this(p_256596_, p_255886_, p_256513_, "vanilla", (ExistingFileHelper)null);
    }

    protected TagsProvider(PackOutput p_256596_, ResourceKey<? extends Registry<T>> p_255886_, CompletableFuture<HolderLookup.Provider> p_256513_, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        this(p_256596_, p_255886_, p_256513_, CompletableFuture.completedFuture(net.minecraft.data.tags.TagsProvider.TagLookup.empty()), modId, existingFileHelper);
    }

    /** @deprecated */
    @Deprecated
    protected TagsProvider(PackOutput p_275432_, ResourceKey<? extends Registry<T>> p_275476_, CompletableFuture<HolderLookup.Provider> p_275222_, CompletableFuture<TagLookup<T>> p_275565_) {
        this(p_275432_, p_275476_, p_275222_, p_275565_, "vanilla", (ExistingFileHelper)null);
    }

    protected TagsProvider(PackOutput p_275432_, ResourceKey<? extends Registry<T>> p_275476_, CompletableFuture<HolderLookup.Provider> p_275222_, CompletableFuture<TagLookup<T>> p_275565_, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        this.contentsDone = new CompletableFuture();
        this.builders = Maps.newLinkedHashMap();
        this.pathProvider = p_275432_.createPathProvider(Target.DATA_PACK, TagManager.getTagDir(p_275476_));
        this.registryKey = p_275476_;
        this.parentProvider = p_275565_;
        this.lookupProvider = p_275222_;
        this.modId = modId;
        this.existingFileHelper = existingFileHelper;
        this.resourceType = new ExistingFileHelper.ResourceType(PackType.SERVER_DATA, ".json", TagManager.getTagDir(p_275476_));
        this.elementResourceType = new ExistingFileHelper.ResourceType(PackType.SERVER_DATA, ".json", ForgeHooks.prefixNamespace(p_275476_.location()));
    }

    protected @Nullable Path getPath(ResourceLocation id) {
        return this.pathProvider.json(id);
    }

    public String getName() {
        ResourceLocation var10000 = this.registryKey.location();
        return "Tags for " + var10000 + " mod id " + this.modId;
    }

    protected abstract void addTags(HolderLookup.Provider var1);

    public CompletableFuture<?> run(CachedOutput p_253684_) {
        return this.createContentsProvider().thenApply((p_275895_) -> {
            this.contentsDone.complete((Void)null);
            return p_275895_;
        }).thenCombineAsync(this.parentProvider, (p_274778_, p_274779_) -> {
            record CombinedData<T>(HolderLookup.Provider contents, TagLookup<T> parent) {
                CombinedData(HolderLookup.Provider contents, TagLookup<T> parent) {
                    this.contents = contents;
                    this.parent = parent;
                }

                public HolderLookup.Provider contents() {
                    return this.contents;
                }

                public TagLookup<T> parent() {
                    return this.parent;
                }
            }

            return new CombinedData(p_274778_, p_274779_);
        }).thenCompose((p_274774_) -> {
            HolderLookup.RegistryLookup<T> registrylookup = (HolderLookup.RegistryLookup)p_274774_.contents.lookup(this.registryKey).orElseThrow(() -> {
                return RegistryManager.ACTIVE.getRegistry(this.registryKey) != null ? new IllegalStateException("Forge registry " + this.registryKey.location() + " does not have support for tags") : new IllegalStateException("Registry " + this.registryKey.location() + " not found");
            });
            Predicate<ResourceLocation> predicate = (p_255496_) -> {
                return registrylookup.get(ResourceKey.create(this.registryKey, p_255496_)).isPresent();
            };
            Predicate<ResourceLocation> predicate1 = (p_274776_) -> {
                return this.builders.containsKey(p_274776_) || p_274774_.parent.contains(TagKey.create(this.registryKey, p_274776_));
            };
            return CompletableFuture.allOf((CompletableFuture[])this.builders.entrySet().stream().map((p_255499_) -> {
                ResourceLocation resourcelocation = (ResourceLocation)p_255499_.getKey();
                TagBuilder tagbuilder = (TagBuilder)p_255499_.getValue();
                List<TagEntry> list = tagbuilder.build();
                List<TagEntry> list1 = Stream.concat(list.stream(), tagbuilder.getRemoveEntries()).filter((p_274771_) -> {
                    return !p_274771_.verifyIfPresent(predicate, predicate1);
                }).filter(this::missing).toList();
                if (!list1.isEmpty()) {
                    throw new IllegalArgumentException(String.format(Locale.ROOT, "Couldn't define tag %s as it is missing following references: %s", resourcelocation, list1.stream().map(Objects::toString).collect(Collectors.joining(","))));
                } else {
                    List<TagEntry> removed = tagbuilder.getRemoveEntries().toList();
                    DataResult var10000 = TagFile.CODEC.encodeStart(JsonOps.INSTANCE, new TagFile(list, tagbuilder.isReplace(), removed));
                    Logger var10002 = LOGGER;
                    Objects.requireNonNull(var10002);
                    JsonElement jsonelement = (JsonElement)var10000.getOrThrow(false, var10002::error);
                    Path path = this.getPath(resourcelocation);
                    return path == null ? CompletableFuture.completedFuture((Object)null) : DataProvider.saveStable(p_253684_, jsonelement, path);
                }
            }).toArray((p_253442_) -> {
                return new CompletableFuture[p_253442_];
            }));
        });
    }

    private boolean missing(TagEntry reference) {
        if (!reference.isRequired()) {
            return false;
        } else {
            return this.existingFileHelper == null || !this.existingFileHelper.exists(reference.getId(), reference.isTag() ? this.resourceType : this.elementResourceType);
        }
    }

    protected TagAppender<T> tag(TagKey<T> p_206425_) {
        TagBuilder tagbuilder = this.getOrCreateRawBuilder(p_206425_);
        return new TagAppender(tagbuilder, this.modId);
    }

    protected TagBuilder getOrCreateRawBuilder(TagKey<T> p_236452_) {
        return (TagBuilder)this.builders.computeIfAbsent(p_236452_.location(), (p_236442_) -> {
            if (this.existingFileHelper != null) {
                this.existingFileHelper.trackGenerated(p_236442_, this.resourceType);
            }

            return TagBuilder.create();
        });
    }

    public CompletableFuture<TagLookup<T>> contentsGetter() {
        return this.contentsDone.thenApply((p_276016_) -> {
            return (p_274772_) -> {
                return Optional.ofNullable((TagBuilder)this.builders.get(p_274772_.location()));
            };
        });
    }

    protected CompletableFuture<HolderLookup.Provider> createContentsProvider() {
        return this.lookupProvider.thenApply((p_274768_) -> {
            this.builders.clear();
            this.addTags(p_274768_);
            return p_274768_;
        });
    }

    @FunctionalInterface
    public interface TagLookup<T> extends Function<TagKey<T>, Optional<TagBuilder>> {
        static <T> TagLookup<T> empty() {
            return (p_275247_) -> {
                return Optional.empty();
            };
        }

        default boolean contains(TagKey<T> p_275413_) {
            return ((Optional)this.apply(p_275413_)).isPresent();
        }
    }

    public static class TagAppender<T> implements IForgeTagAppender<T> {
        private final TagBuilder builder;
        private final String modId;

        protected TagAppender(TagBuilder p_236454_, String modId) {
            this.builder = p_236454_;
            this.modId = modId;
        }

        public final TagAppender<T> add(ResourceKey<T> p_256138_) {
            this.builder.addElement(p_256138_.location());
            return this;
        }

        @SafeVarargs
        public final TagAppender<T> add(ResourceKey<T>... p_211102_) {
            ResourceKey[] var2 = p_211102_;
            int var3 = p_211102_.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                ResourceKey<T> resourcekey = var2[var4];
                this.builder.addElement(resourcekey.location());
            }

            return this;
        }

        public TagAppender<T> addOptional(ResourceLocation p_176840_) {
            this.builder.addOptionalElement(p_176840_);
            return this;
        }

        public TagAppender<T> addTag(TagKey<T> p_206429_) {
            this.builder.addTag(p_206429_.location());
            return this;
        }

        public TagAppender<T> addOptionalTag(ResourceLocation p_176842_) {
            this.builder.addOptionalTag(p_176842_);
            return this;
        }

        public TagAppender<T> add(TagEntry tag) {
            this.builder.add(tag);
            return this;
        }

        public TagBuilder getInternalBuilder() {
            return this.builder;
        }

        public String getModID() {
            return this.modId;
        }
    }
}

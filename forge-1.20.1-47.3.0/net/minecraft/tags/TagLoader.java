//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.tags;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.DependencySorter;
import org.slf4j.Logger;

public class TagLoader<T> {
    private static final Logger LOGGER = LogUtils.getLogger();
    final Function<ResourceLocation, Optional<? extends T>> idToValue;
    private final String directory;

    public TagLoader(Function<ResourceLocation, Optional<? extends T>> p_144493_, String p_144494_) {
        this.idToValue = p_144493_;
        this.directory = p_144494_;
    }

    public Map<ResourceLocation, List<EntryWithSource>> load(ResourceManager p_144496_) {
        Map<ResourceLocation, List<EntryWithSource>> map = Maps.newHashMap();
        FileToIdConverter filetoidconverter = FileToIdConverter.json(this.directory);
        Iterator var4 = filetoidconverter.listMatchingResourceStacks(p_144496_).entrySet().iterator();

        while(var4.hasNext()) {
            Map.Entry<ResourceLocation, List<Resource>> entry = (Map.Entry)var4.next();
            ResourceLocation resourcelocation = (ResourceLocation)entry.getKey();
            ResourceLocation resourcelocation1 = filetoidconverter.fileToId(resourcelocation);
            Iterator var8 = ((List)entry.getValue()).iterator();

            while(var8.hasNext()) {
                Resource resource = (Resource)var8.next();

                try {
                    Reader reader = resource.openAsReader();

                    try {
                        JsonElement jsonelement = JsonParser.parseReader(reader);
                        List<EntryWithSource> list = (List)map.computeIfAbsent(resourcelocation1, (p_215974_) -> {
                            return new ArrayList();
                        });
                        DataResult var10000 = TagFile.CODEC.parse(new Dynamic(JsonOps.INSTANCE, jsonelement));
                        Logger var10002 = LOGGER;
                        Objects.requireNonNull(var10002);
                        TagFile tagfile = (TagFile)var10000.getOrThrow(false, var10002::error);
                        if (tagfile.replace()) {
                            list.clear();
                        }

                        String s = resource.sourcePackId();
                        tagfile.entries().forEach((p_215997_) -> {
                            list.add(new EntryWithSource(p_215997_, s));
                        });
                        tagfile.remove().forEach((e) -> {
                            list.add(new EntryWithSource(e, s, true));
                        });
                    } catch (Throwable var16) {
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (Throwable var15) {
                                var16.addSuppressed(var15);
                            }
                        }

                        throw var16;
                    }

                    if (reader != null) {
                        reader.close();
                    }
                } catch (Exception var17) {
                    Exception exception = var17;
                    LOGGER.error("Couldn't read tag list {} from {} in data pack {}", new Object[]{resourcelocation1, resourcelocation, resource.sourcePackId(), exception});
                }
            }
        }

        return map;
    }

    private Either<Collection<EntryWithSource>, Collection<T>> build(TagEntry.Lookup<T> p_215979_, List<EntryWithSource> p_215980_) {
        LinkedHashSet<T> builder = new LinkedHashSet();
        List<EntryWithSource> list = new ArrayList();
        Iterator var5 = p_215980_.iterator();

        while(var5.hasNext()) {
            EntryWithSource tagloader$entrywithsource = (EntryWithSource)var5.next();
            TagEntry var10000 = tagloader$entrywithsource.entry();
            Consumer var10002;
            if (tagloader$entrywithsource.remove()) {
                Objects.requireNonNull(builder);
                var10002 = builder::remove;
            } else {
                Objects.requireNonNull(builder);
                var10002 = builder::add;
            }

            if (!var10000.build(p_215979_, var10002) && !tagloader$entrywithsource.remove()) {
                list.add(tagloader$entrywithsource);
            }
        }

        return list.isEmpty() ? Either.right(List.copyOf(builder)) : Either.left(list);
    }

    public Map<ResourceLocation, Collection<T>> build(Map<ResourceLocation, List<EntryWithSource>> p_203899_) {
        final Map<ResourceLocation, Collection<T>> map = Maps.newHashMap();
        TagEntry.Lookup<T> lookup = new TagEntry.Lookup<T>() {
            @Nullable
            public T element(ResourceLocation p_216039_) {
                return ((Optional)TagLoader.this.idToValue.apply(p_216039_)).orElse((Object)null);
            }

            @Nullable
            public Collection<T> tag(ResourceLocation p_216041_) {
                return (Collection)map.get(p_216041_);
            }
        };
        DependencySorter<ResourceLocation, SortingEntry> dependencysorter = new DependencySorter();
        p_203899_.forEach((p_284685_, p_284686_) -> {
            dependencysorter.addEntry(p_284685_, new SortingEntry(p_284686_));
        });
        dependencysorter.orderByDependencies((p_284682_, p_284683_) -> {
            this.build(lookup, p_284683_.entries).ifLeft((p_215977_) -> {
                LOGGER.error("Couldn't load tag {} as it is missing following references: {}", p_284682_, p_215977_.stream().map(Objects::toString).collect(Collectors.joining(", \n\t")));
            }).ifRight((p_216001_) -> {
                map.put(p_284682_, p_216001_);
            });
        });
        return map;
    }

    public Map<ResourceLocation, Collection<T>> loadAndBuild(ResourceManager p_203901_) {
        return this.build(this.load(p_203901_));
    }

    public static record EntryWithSource(TagEntry entry, String source, boolean remove) {
        public EntryWithSource(TagEntry entry, String source) {
            this(entry, source, false);
        }

        public EntryWithSource(TagEntry entry, String source, boolean remove) {
            this.entry = entry;
            this.source = source;
            this.remove = remove;
        }

        public String toString() {
            return this.entry + " (from " + this.source + ")";
        }

        public TagEntry entry() {
            return this.entry;
        }

        public String source() {
            return this.source;
        }

        public boolean remove() {
            return this.remove;
        }
    }

    static record SortingEntry(List<EntryWithSource> entries) implements DependencySorter.Entry<ResourceLocation> {
        SortingEntry(List<EntryWithSource> entries) {
            this.entries = entries;
        }

        public void visitRequiredDependencies(Consumer<ResourceLocation> p_285529_) {
            this.entries.forEach((p_285236_) -> {
                p_285236_.entry.visitRequiredDependencies(p_285529_);
            });
        }

        public void visitOptionalDependencies(Consumer<ResourceLocation> p_285469_) {
            this.entries.forEach((p_284943_) -> {
                p_284943_.entry.visitOptionalDependencies(p_285469_);
            });
        }

        public List<EntryWithSource> entries() {
            return this.entries;
        }
    }
}

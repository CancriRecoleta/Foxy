//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.packs.resources;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import org.slf4j.Logger;

public class FallbackResourceManager implements ResourceManager {
    static final Logger LOGGER = LogUtils.getLogger();
    public final List<PackEntry> fallbacks = Lists.newArrayList();
    private final PackType type;
    private final String namespace;

    public FallbackResourceManager(PackType p_10605_, String p_10606_) {
        this.type = p_10605_;
        this.namespace = p_10606_;
    }

    public void push(PackResources p_215378_) {
        this.pushInternal(p_215378_.packId(), p_215378_, (Predicate)null);
    }

    public void push(PackResources p_215383_, Predicate<ResourceLocation> p_215384_) {
        this.pushInternal(p_215383_.packId(), p_215383_, p_215384_);
    }

    public void pushFilterOnly(String p_215400_, Predicate<ResourceLocation> p_215401_) {
        this.pushInternal(p_215400_, (PackResources)null, p_215401_);
    }

    private void pushInternal(String p_215396_, @Nullable PackResources p_215397_, @Nullable Predicate<ResourceLocation> p_215398_) {
        this.fallbacks.add(new PackEntry(p_215396_, p_215397_, p_215398_));
    }

    public Set<String> getNamespaces() {
        return ImmutableSet.of(this.namespace);
    }

    public Optional<Resource> getResource(ResourceLocation p_215419_) {
        for(int i = this.fallbacks.size() - 1; i >= 0; --i) {
            PackEntry fallbackresourcemanager$packentry = (PackEntry)this.fallbacks.get(i);
            PackResources packresources = fallbackresourcemanager$packentry.resources;
            if (packresources != null) {
                IoSupplier<InputStream> iosupplier = packresources.getResource(this.type, p_215419_);
                if (iosupplier != null) {
                    IoSupplier<ResourceMetadata> iosupplier1 = this.createStackMetadataFinder(p_215419_, i);
                    return Optional.of(createResource(packresources, p_215419_, iosupplier, iosupplier1));
                }
            }

            if (fallbackresourcemanager$packentry.isFiltered(p_215419_)) {
                LOGGER.warn("Resource {} not found, but was filtered by pack {}", p_215419_, fallbackresourcemanager$packentry.name);
                return Optional.empty();
            }
        }

        return Optional.empty();
    }

    private static Resource createResource(PackResources p_249946_, ResourceLocation p_250632_, IoSupplier<InputStream> p_250514_, IoSupplier<ResourceMetadata> p_251676_) {
        return new Resource(p_249946_, wrapForDebug(p_250632_, p_249946_, p_250514_), p_251676_);
    }

    private static IoSupplier<InputStream> wrapForDebug(ResourceLocation p_248639_, PackResources p_251740_, IoSupplier<InputStream> p_249116_) {
        return LOGGER.isDebugEnabled() ? () -> {
            return new LeakedResourceWarningInputStream((InputStream)p_249116_.get(), p_248639_, p_251740_.packId());
        } : p_249116_;
    }

    public List<Resource> getResourceStack(ResourceLocation p_215367_) {
        ResourceLocation resourcelocation = getMetadataLocation(p_215367_);
        List<Resource> list = new ArrayList();
        boolean flag = false;
        String s = null;

        for(int i = this.fallbacks.size() - 1; i >= 0; --i) {
            PackEntry fallbackresourcemanager$packentry = (PackEntry)this.fallbacks.get(i);
            PackResources pack = fallbackresourcemanager$packentry.resources;
            if (pack != null) {
                Collection<PackResources> children = pack.getChildren();
                Collection<PackResources> packs = children == null ? List.of(pack) : children;
                Iterator var11 = ((Collection)packs).iterator();

                while(var11.hasNext()) {
                    PackResources packresources = (PackResources)var11.next();
                    IoSupplier<InputStream> iosupplier = packresources.getResource(this.type, p_215367_);
                    if (iosupplier != null) {
                        IoSupplier iosupplier1;
                        if (flag) {
                            iosupplier1 = ResourceMetadata.EMPTY_SUPPLIER;
                        } else {
                            iosupplier1 = () -> {
                                IoSupplier<InputStream> iosupplier2 = packresources.getResource(this.type, resourcelocation);
                                return iosupplier2 != null ? parseMetadata(iosupplier2) : ResourceMetadata.EMPTY;
                            };
                        }

                        list.add(new Resource(packresources, iosupplier, iosupplier1));
                    }
                }
            }

            if (fallbackresourcemanager$packentry.isFiltered(p_215367_)) {
                s = fallbackresourcemanager$packentry.name;
                break;
            }

            if (fallbackresourcemanager$packentry.isFiltered(resourcelocation)) {
                flag = true;
            }
        }

        if (list.isEmpty() && s != null) {
            LOGGER.warn("Resource {} not found, but was filtered by pack {}", p_215367_, s);
        }

        return Lists.reverse(list);
    }

    private static boolean isMetadata(ResourceLocation p_249381_) {
        return p_249381_.getPath().endsWith(".mcmeta");
    }

    private static ResourceLocation getResourceLocationFromMetadata(ResourceLocation p_249669_) {
        String s = p_249669_.getPath().substring(0, p_249669_.getPath().length() - ".mcmeta".length());
        return p_249669_.withPath(s);
    }

    static ResourceLocation getMetadataLocation(ResourceLocation p_10625_) {
        return p_10625_.withPath(p_10625_.getPath() + ".mcmeta");
    }

    public Map<ResourceLocation, Resource> listResources(String p_215413_, Predicate<ResourceLocation> p_215414_) {
        Map<ResourceLocation, ResourceWithSourceAndIndex> map = new HashMap();
        Map<ResourceLocation, ResourceWithSourceAndIndex> map1 = new HashMap();
        int i = this.fallbacks.size();

        for(int j = 0; j < i; ++j) {
            PackEntry fallbackresourcemanager$packentry = (PackEntry)this.fallbacks.get(j);
            fallbackresourcemanager$packentry.filterAll(map.keySet());
            fallbackresourcemanager$packentry.filterAll(map1.keySet());
            PackResources packresources = fallbackresourcemanager$packentry.resources;
            if (packresources != null) {
                int k = j;
                packresources.listResources(this.type, this.namespace, p_215413_, (p_248254_, p_248255_) -> {
                    record ResourceWithSourceAndIndex(PackResources packResources, IoSupplier<InputStream> resource, int packIndex) {
                        ResourceWithSourceAndIndex(PackResources packResources, IoSupplier<InputStream> resource, int packIndex) {
                            this.packResources = packResources;
                            this.resource = resource;
                            this.packIndex = packIndex;
                        }

                        public PackResources packResources() {
                            return this.packResources;
                        }

                        public IoSupplier<InputStream> resource() {
                            return this.resource;
                        }

                        public int packIndex() {
                            return this.packIndex;
                        }
                    }

                    if (isMetadata(p_248254_)) {
                        if (p_215414_.test(getResourceLocationFromMetadata(p_248254_))) {
                            map1.put(p_248254_, new ResourceWithSourceAndIndex(packresources, p_248255_, k));
                        }
                    } else if (p_215414_.test(p_248254_)) {
                        map.put(p_248254_, new ResourceWithSourceAndIndex(packresources, p_248255_, k));
                    }

                });
            }
        }

        Map<ResourceLocation, Resource> map2 = Maps.newTreeMap();
        map.forEach((p_248258_, p_248259_) -> {
            ResourceLocation resourcelocation = getMetadataLocation(p_248258_);
            ResourceWithSourceAndIndex fallbackresourcemanager$1resourcewithsourceandindex = (ResourceWithSourceAndIndex)map1.get(resourcelocation);
            IoSupplier iosupplier;
            if (fallbackresourcemanager$1resourcewithsourceandindex != null && fallbackresourcemanager$1resourcewithsourceandindex.packIndex >= p_248259_.packIndex) {
                iosupplier = convertToMetadata(fallbackresourcemanager$1resourcewithsourceandindex.resource);
            } else {
                iosupplier = ResourceMetadata.EMPTY_SUPPLIER;
            }

            map2.put(p_248258_, createResource(p_248259_.packResources, p_248258_, p_248259_.resource, iosupplier));
        });
        return map2;
    }

    private IoSupplier<ResourceMetadata> createStackMetadataFinder(ResourceLocation p_215369_, int p_215370_) {
        return () -> {
            ResourceLocation resourcelocation = getMetadataLocation(p_215369_);

            for(int i = this.fallbacks.size() - 1; i >= p_215370_; --i) {
                PackEntry fallbackresourcemanager$packentry = (PackEntry)this.fallbacks.get(i);
                PackResources packresources = fallbackresourcemanager$packentry.resources;
                if (packresources != null) {
                    IoSupplier<InputStream> iosupplier = packresources.getResource(this.type, resourcelocation);
                    if (iosupplier != null) {
                        return parseMetadata(iosupplier);
                    }
                }

                if (fallbackresourcemanager$packentry.isFiltered(resourcelocation)) {
                    break;
                }
            }

            return ResourceMetadata.EMPTY;
        };
    }

    private static IoSupplier<ResourceMetadata> convertToMetadata(IoSupplier<InputStream> p_250827_) {
        return () -> {
            return parseMetadata(p_250827_);
        };
    }

    private static ResourceMetadata parseMetadata(IoSupplier<InputStream> p_250103_) throws IOException {
        InputStream inputstream = (InputStream)p_250103_.get();

        ResourceMetadata var2;
        try {
            var2 = ResourceMetadata.fromJsonStream(inputstream);
        } catch (Throwable var5) {
            if (inputstream != null) {
                try {
                    inputstream.close();
                } catch (Throwable var4) {
                    var5.addSuppressed(var4);
                }
            }

            throw var5;
        }

        if (inputstream != null) {
            inputstream.close();
        }

        return var2;
    }

    private static void applyPackFiltersToExistingResources(PackEntry p_215393_, Map<ResourceLocation, EntryStack> p_215394_) {
        Iterator var2 = p_215394_.values().iterator();

        while(var2.hasNext()) {
            EntryStack fallbackresourcemanager$entrystack = (EntryStack)var2.next();
            if (p_215393_.isFiltered(fallbackresourcemanager$entrystack.fileLocation)) {
                fallbackresourcemanager$entrystack.fileSources.clear();
            } else if (p_215393_.isFiltered(fallbackresourcemanager$entrystack.metadataLocation())) {
                fallbackresourcemanager$entrystack.metaSources.clear();
            }
        }

    }

    private void listPackResources(PackEntry p_215388_, String p_215389_, Predicate<ResourceLocation> p_215390_, Map<ResourceLocation, EntryStack> p_215391_) {
        PackResources packresources = p_215388_.resources;
        if (packresources != null) {
            packresources.listResources(this.type, this.namespace, p_215389_, (p_248266_, p_248267_) -> {
                if (isMetadata(p_248266_)) {
                    ResourceLocation resourcelocation = getResourceLocationFromMetadata(p_248266_);
                    if (!p_215390_.test(resourcelocation)) {
                        return;
                    }

                    ((EntryStack)p_215391_.computeIfAbsent(resourcelocation, EntryStack::new)).metaSources.put(packresources, p_248267_);
                } else {
                    if (!p_215390_.test(p_248266_)) {
                        return;
                    }

                    ((EntryStack)p_215391_.computeIfAbsent(p_248266_, EntryStack::new)).fileSources.add(new ResourceWithSource(packresources, p_248267_));
                }

            });
        }

    }

    public Map<ResourceLocation, List<Resource>> listResourceStacks(String p_215416_, Predicate<ResourceLocation> p_215417_) {
        Map<ResourceLocation, EntryStack> map = Maps.newHashMap();
        Iterator var4 = this.fallbacks.iterator();

        while(var4.hasNext()) {
            PackEntry fallbackresourcemanager$packentry = (PackEntry)var4.next();
            applyPackFiltersToExistingResources(fallbackresourcemanager$packentry, map);
            this.listPackResources(fallbackresourcemanager$packentry, p_215416_, p_215417_, map);
        }

        TreeMap<ResourceLocation, List<Resource>> treemap = Maps.newTreeMap();
        Iterator var14 = map.values().iterator();

        while(true) {
            EntryStack fallbackresourcemanager$entrystack;
            do {
                if (!var14.hasNext()) {
                    return treemap;
                }

                fallbackresourcemanager$entrystack = (EntryStack)var14.next();
            } while(fallbackresourcemanager$entrystack.fileSources.isEmpty());

            List<Resource> list = new ArrayList();
            Iterator var8 = fallbackresourcemanager$entrystack.fileSources.iterator();

            while(var8.hasNext()) {
                ResourceWithSource fallbackresourcemanager$resourcewithsource = (ResourceWithSource)var8.next();
                PackResources packresources = fallbackresourcemanager$resourcewithsource.source;
                IoSupplier<InputStream> iosupplier = (IoSupplier)fallbackresourcemanager$entrystack.metaSources.get(packresources);
                IoSupplier<ResourceMetadata> iosupplier1 = iosupplier != null ? convertToMetadata(iosupplier) : ResourceMetadata.EMPTY_SUPPLIER;
                list.add(createResource(packresources, fallbackresourcemanager$entrystack.fileLocation, fallbackresourcemanager$resourcewithsource.resource, iosupplier1));
            }

            treemap.put(fallbackresourcemanager$entrystack.fileLocation, list);
        }
    }

    public Stream<PackResources> listPacks() {
        return this.fallbacks.stream().map((p_215386_) -> {
            return p_215386_.resources;
        }).filter(Objects::nonNull);
    }

    static record PackEntry(String name, @Nullable PackResources resources, @Nullable Predicate<ResourceLocation> filter) {
        PackEntry(String name, @Nullable PackResources resources, @Nullable Predicate<ResourceLocation> filter) {
            this.name = name;
            this.resources = resources;
            this.filter = filter;
        }

        public void filterAll(Collection<ResourceLocation> p_215443_) {
            if (this.filter != null) {
                p_215443_.removeIf(this.filter);
            }

        }

        public boolean isFiltered(ResourceLocation p_215441_) {
            return this.filter != null && this.filter.test(p_215441_);
        }

        public String name() {
            return this.name;
        }

        @Nullable
        public PackResources resources() {
            return this.resources;
        }

        @Nullable
        public Predicate<ResourceLocation> filter() {
            return this.filter;
        }
    }

    static record EntryStack(ResourceLocation fileLocation, ResourceLocation metadataLocation, List<ResourceWithSource> fileSources, Map<PackResources, IoSupplier<InputStream>> metaSources) {
        EntryStack(ResourceLocation p_251350_) {
            this(p_251350_, FallbackResourceManager.getMetadataLocation(p_251350_), new ArrayList(), new Object2ObjectArrayMap());
        }

        EntryStack(ResourceLocation fileLocation, ResourceLocation metadataLocation, List<ResourceWithSource> fileSources, Map<PackResources, IoSupplier<InputStream>> metaSources) {
            this.fileLocation = fileLocation;
            this.metadataLocation = metadataLocation;
            this.fileSources = fileSources;
            this.metaSources = metaSources;
        }

        public ResourceLocation fileLocation() {
            return this.fileLocation;
        }

        public ResourceLocation metadataLocation() {
            return this.metadataLocation;
        }

        public List<ResourceWithSource> fileSources() {
            return this.fileSources;
        }

        public Map<PackResources, IoSupplier<InputStream>> metaSources() {
            return this.metaSources;
        }
    }

    static record ResourceWithSource(PackResources source, IoSupplier<InputStream> resource) {
        ResourceWithSource(PackResources source, IoSupplier<InputStream> resource) {
            this.source = source;
            this.resource = resource;
        }

        public PackResources source() {
            return this.source;
        }

        public IoSupplier<InputStream> resource() {
            return this.resource;
        }
    }

    static class LeakedResourceWarningInputStream extends FilterInputStream {
        private final Supplier<String> message;
        private boolean closed;

        public LeakedResourceWarningInputStream(InputStream p_10633_, ResourceLocation p_10634_, String p_10635_) {
            super(p_10633_);
            Exception exception = new Exception("Stacktrace");
            this.message = () -> {
                StringWriter stringwriter = new StringWriter();
                exception.printStackTrace(new PrintWriter(stringwriter));
                return "Leaked resource: '" + p_10634_ + "' loaded from pack: '" + p_10635_ + "'\n" + stringwriter;
            };
        }

        public void close() throws IOException {
            super.close();
            this.closed = true;
        }

        protected void finalize() throws Throwable {
            if (!this.closed) {
                FallbackResourceManager.LOGGER.warn("{}", this.message.get());
            }

            super.finalize();
        }
    }
}

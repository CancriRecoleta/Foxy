//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.packs.resources;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import org.slf4j.Logger;

public class MultiPackResourceManager implements CloseableResourceManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Map<String, FallbackResourceManager> namespacedManagers;
    private final List<PackResources> packs;

    public MultiPackResourceManager(PackType p_203797_, List<PackResources> p_203798_) {
        this.packs = List.copyOf(p_203798_);
        Map<String, FallbackResourceManager> $$2 = new HashMap();
        List<String> $$3 = p_203798_.stream().flatMap((p_215471_) -> {
            return p_215471_.getNamespaces(p_203797_).stream();
        }).distinct().toList();
        Iterator var5 = p_203798_.iterator();

        label57:
        while(var5.hasNext()) {
            PackResources $$4 = (PackResources)var5.next();
            ResourceFilterSection $$5 = this.getPackFilterSection($$4);
            Set<String> $$6 = $$4.getNamespaces(p_203797_);
            Predicate<ResourceLocation> $$7 = $$5 != null ? (p_215474_) -> {
                return $$5.isPathFiltered(p_215474_.getPath());
            } : null;
            Iterator var10 = $$3.iterator();

            while(true) {
                while(true) {
                    String $$8;
                    boolean $$9;
                    boolean $$10;
                    do {
                        if (!var10.hasNext()) {
                            continue label57;
                        }

                        $$8 = (String)var10.next();
                        $$9 = $$6.contains($$8);
                        $$10 = $$5 != null && $$5.isNamespaceFiltered($$8);
                    } while(!$$9 && !$$10);

                    FallbackResourceManager $$11 = (FallbackResourceManager)$$2.get($$8);
                    if ($$11 == null) {
                        $$11 = new FallbackResourceManager(p_203797_, $$8);
                        $$2.put($$8, $$11);
                    }

                    if ($$9 && $$10) {
                        $$11.push($$4, $$7);
                    } else if ($$9) {
                        $$11.push($$4);
                    } else {
                        $$11.pushFilterOnly($$4.packId(), $$7);
                    }
                }
            }
        }

        this.namespacedManagers = $$2;
    }

    @Nullable
    private ResourceFilterSection getPackFilterSection(PackResources p_215468_) {
        try {
            return (ResourceFilterSection)p_215468_.getMetadataSection(ResourceFilterSection.TYPE);
        } catch (IOException var3) {
            LOGGER.error("Failed to get filter section from pack {}", p_215468_.packId());
            return null;
        }
    }

    public Set<String> getNamespaces() {
        return this.namespacedManagers.keySet();
    }

    public Optional<Resource> getResource(ResourceLocation p_215482_) {
        ResourceManager $$1 = (ResourceManager)this.namespacedManagers.get(p_215482_.getNamespace());
        return $$1 != null ? $$1.getResource(p_215482_) : Optional.empty();
    }

    public List<Resource> getResourceStack(ResourceLocation p_215466_) {
        ResourceManager $$1 = (ResourceManager)this.namespacedManagers.get(p_215466_.getNamespace());
        return $$1 != null ? $$1.getResourceStack(p_215466_) : List.of();
    }

    public Map<ResourceLocation, Resource> listResources(String p_215476_, Predicate<ResourceLocation> p_215477_) {
        checkTrailingDirectoryPath(p_215476_);
        Map<ResourceLocation, Resource> $$2 = new TreeMap();
        Iterator var4 = this.namespacedManagers.values().iterator();

        while(var4.hasNext()) {
            FallbackResourceManager $$3 = (FallbackResourceManager)var4.next();
            $$2.putAll($$3.listResources(p_215476_, p_215477_));
        }

        return $$2;
    }

    public Map<ResourceLocation, List<Resource>> listResourceStacks(String p_215479_, Predicate<ResourceLocation> p_215480_) {
        checkTrailingDirectoryPath(p_215479_);
        Map<ResourceLocation, List<Resource>> $$2 = new TreeMap();
        Iterator var4 = this.namespacedManagers.values().iterator();

        while(var4.hasNext()) {
            FallbackResourceManager $$3 = (FallbackResourceManager)var4.next();
            $$2.putAll($$3.listResourceStacks(p_215479_, p_215480_));
        }

        return $$2;
    }

    private static void checkTrailingDirectoryPath(String p_249608_) {
        if (p_249608_.endsWith("/")) {
            throw new IllegalArgumentException("Trailing slash in path " + p_249608_);
        }
    }

    public Stream<PackResources> listPacks() {
        return this.packs.stream();
    }

    public void close() {
        this.packs.forEach(PackResources::close);
    }
}

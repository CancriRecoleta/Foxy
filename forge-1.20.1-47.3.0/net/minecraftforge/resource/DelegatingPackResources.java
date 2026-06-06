//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.resource;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.Nullable;

public class DelegatingPackResources extends AbstractPackResources {
    private final PackMetadataSection packMeta;
    private final List<PackResources> delegates;
    private final Map<String, List<PackResources>> namespacesAssets;
    private final Map<String, List<PackResources>> namespacesData;

    public DelegatingPackResources(String packId, boolean isBuiltin, PackMetadataSection packMeta, List<? extends PackResources> packs) {
        super(packId, isBuiltin);
        this.packMeta = packMeta;
        this.delegates = ImmutableList.copyOf(packs);
        this.namespacesAssets = this.buildNamespaceMap(PackType.CLIENT_RESOURCES, this.delegates);
        this.namespacesData = this.buildNamespaceMap(PackType.SERVER_DATA, this.delegates);
    }

    private Map<String, List<PackResources>> buildNamespaceMap(PackType type, List<PackResources> packList) {
        Map<String, List<PackResources>> map = new HashMap();
        Iterator var4 = packList.iterator();

        while(var4.hasNext()) {
            PackResources pack = (PackResources)var4.next();
            Iterator var6 = pack.getNamespaces(type).iterator();

            while(var6.hasNext()) {
                String namespace = (String)var6.next();
                ((List)map.computeIfAbsent(namespace, (k) -> {
                    return new ArrayList();
                })).add(pack);
            }
        }

        map.replaceAll((k, list) -> {
            return ImmutableList.copyOf(list);
        });
        return ImmutableMap.copyOf(map);
    }

    public <T> @Nullable T getMetadataSection(MetadataSectionSerializer<T> deserializer) throws IOException {
        return deserializer.getMetadataSectionName().equals("pack") ? this.packMeta : null;
    }

    public void listResources(PackType type, String resourceNamespace, String paths, PackResources.ResourceOutput resourceOutput) {
        Iterator var5 = this.delegates.iterator();

        while(var5.hasNext()) {
            PackResources delegate = (PackResources)var5.next();
            delegate.listResources(type, resourceNamespace, paths, resourceOutput);
        }

    }

    public Set<String> getNamespaces(PackType type) {
        return type == PackType.CLIENT_RESOURCES ? this.namespacesAssets.keySet() : this.namespacesData.keySet();
    }

    public void close() {
        Iterator var1 = this.delegates.iterator();

        while(var1.hasNext()) {
            PackResources pack = (PackResources)var1.next();
            pack.close();
        }

    }

    public @Nullable IoSupplier<InputStream> getRootResource(String... paths) {
        return null;
    }

    public @Nullable IoSupplier<InputStream> getResource(PackType type, ResourceLocation location) {
        Iterator var3 = this.getCandidatePacks(type, location).iterator();

        IoSupplier ioSupplier;
        do {
            if (!var3.hasNext()) {
                return null;
            }

            PackResources pack = (PackResources)var3.next();
            ioSupplier = pack.getResource(type, location);
        } while(ioSupplier == null);

        return ioSupplier;
    }

    public @Nullable Collection<PackResources> getChildren() {
        return this.delegates;
    }

    private List<PackResources> getCandidatePacks(PackType type, ResourceLocation location) {
        Map<String, List<PackResources>> map = type == PackType.CLIENT_RESOURCES ? this.namespacesAssets : this.namespacesData;
        List<PackResources> packsWithNamespace = (List)map.get(location.getNamespace());
        return packsWithNamespace == null ? Collections.emptyList() : packsWithNamespace;
    }
}

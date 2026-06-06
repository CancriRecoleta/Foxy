//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common.data;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.client.resources.ClientPackSource;
import net.minecraft.client.resources.IndexedAssetSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.resource.ResourcePackLoader;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

public class ExistingFileHelper {
    private final MultiPackResourceManager clientResources;
    private final MultiPackResourceManager serverData;
    private final boolean enable;
    private final Multimap<PackType, ResourceLocation> generated = HashMultimap.create();

    public ExistingFileHelper(Collection<Path> existingPacks, Set<String> existingMods, boolean enable, @Nullable String assetIndex, @Nullable File assetsDir) {
        List<PackResources> candidateClientResources = new ArrayList();
        List<PackResources> candidateServerResources = new ArrayList();
        if (assetIndex != null && assetsDir != null && assetsDir.exists()) {
            candidateClientResources.add(ClientPackSource.createVanillaPackSource(IndexedAssetSource.createIndexFs(assetsDir.toPath(), assetIndex)));
        }

        candidateServerResources.add(ServerPacksSource.createVanillaPackSource());
        Iterator var8 = existingPacks.iterator();

        while(var8.hasNext()) {
            Path existing = (Path)var8.next();
            File file = existing.toFile();
            PackResources pack = file.isDirectory() ? new PathPackResources(file.getName(), file.toPath(), false) : new FilePackResources(file.getName(), file, false);
            candidateClientResources.add(pack);
            candidateServerResources.add(pack);
        }

        var8 = existingMods.iterator();

        while(var8.hasNext()) {
            String existingMod = (String)var8.next();
            IModFileInfo modFileInfo = ModList.get().getModFileById(existingMod);
            if (modFileInfo != null) {
                PackResources pack = ResourcePackLoader.createPackForMod(modFileInfo);
                candidateClientResources.add(pack);
                candidateServerResources.add(pack);
            }
        }

        this.clientResources = new MultiPackResourceManager(PackType.CLIENT_RESOURCES, candidateClientResources);
        this.serverData = new MultiPackResourceManager(PackType.SERVER_DATA, candidateServerResources);
        this.enable = enable;
    }

    private ResourceManager getManager(PackType packType) {
        return packType == PackType.CLIENT_RESOURCES ? this.clientResources : this.serverData;
    }

    private ResourceLocation getLocation(ResourceLocation base, String suffix, String prefix) {
        return new ResourceLocation(base.getNamespace(), prefix + "/" + base.getPath() + suffix);
    }

    public boolean exists(ResourceLocation loc, PackType packType) {
        if (!this.enable) {
            return true;
        } else {
            return this.generated.get(packType).contains(loc) || this.getManager(packType).getResource(loc).isPresent();
        }
    }

    public boolean exists(ResourceLocation loc, IResourceType type) {
        return this.exists(this.getLocation(loc, type.getSuffix(), type.getPrefix()), type.getPackType());
    }

    public boolean exists(ResourceLocation loc, PackType packType, String pathSuffix, String pathPrefix) {
        return this.exists(this.getLocation(loc, pathSuffix, pathPrefix), packType);
    }

    public void trackGenerated(ResourceLocation loc, IResourceType type) {
        this.generated.put(type.getPackType(), this.getLocation(loc, type.getSuffix(), type.getPrefix()));
    }

    public void trackGenerated(ResourceLocation loc, PackType packType, String pathSuffix, String pathPrefix) {
        this.generated.put(packType, this.getLocation(loc, pathSuffix, pathPrefix));
    }

    @VisibleForTesting
    public Resource getResource(ResourceLocation loc, PackType packType, String pathSuffix, String pathPrefix) throws FileNotFoundException {
        return this.getResource(this.getLocation(loc, pathSuffix, pathPrefix), packType);
    }

    @VisibleForTesting
    public Resource getResource(ResourceLocation loc, PackType packType) throws FileNotFoundException {
        return this.getManager(packType).getResourceOrThrow(loc);
    }

    @VisibleForTesting
    public List<Resource> getResourceStack(ResourceLocation loc, PackType packType) {
        return this.getManager(packType).getResourceStack(loc);
    }

    public boolean isEnabled() {
        return this.enable;
    }

    public interface IResourceType {
        PackType getPackType();

        String getSuffix();

        String getPrefix();
    }

    public static class ResourceType implements IResourceType {
        final PackType packType;
        final String suffix;
        final String prefix;

        public ResourceType(PackType type, String suffix, String prefix) {
            this.packType = type;
            this.suffix = suffix;
            this.prefix = prefix;
        }

        public PackType getPackType() {
            return this.packType;
        }

        public String getSuffix() {
            return this.suffix;
        }

        public String getPrefix() {
            return this.prefix;
        }
    }
}

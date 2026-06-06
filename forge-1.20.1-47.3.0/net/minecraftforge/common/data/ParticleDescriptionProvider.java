//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common.data;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.VisibleForTesting;

public abstract class ParticleDescriptionProvider implements DataProvider {
    private final PackOutput.PathProvider particlesPath;
    @VisibleForTesting
    protected final ExistingFileHelper fileHelper;
    @VisibleForTesting
    protected final Map<ResourceLocation, List<String>> descriptions;

    protected ParticleDescriptionProvider(PackOutput output, ExistingFileHelper fileHelper) {
        this.particlesPath = output.createPathProvider(Target.RESOURCE_PACK, "particles");
        this.fileHelper = fileHelper;
        this.descriptions = new HashMap();
    }

    protected abstract void addDescriptions();

    protected void sprite(ParticleType<?> type, ResourceLocation texture) {
        this.spriteSet(type, texture);
    }

    protected void spriteSet(ParticleType<?> type, ResourceLocation baseName, int numOfTextures, boolean reverse) {
        Preconditions.checkArgument(numOfTextures > 0, "The number of textures to generate must be positive");
        this.spriteSet(type, () -> {
            return new Iterator<ResourceLocation>() {
                private int counter = 0;

                public boolean hasNext() {
                    return this.counter < numOfTextures;
                }

                public ResourceLocation next() {
                    int var10001 = reverse ? numOfTextures - this.counter - 1 : this.counter;
                    ResourceLocation texture = baseName.withSuffix("_" + var10001);
                    ++this.counter;
                    return texture;
                }
            };
        });
    }

    protected void spriteSet(ParticleType<?> type, ResourceLocation texture, ResourceLocation... textures) {
        Stream var10002 = Stream.concat(Stream.of(texture), Arrays.stream(textures));
        Objects.requireNonNull(var10002);
        this.spriteSet(type, var10002::iterator);
    }

    protected void spriteSet(ParticleType<?> type, Iterable<ResourceLocation> textures) {
        ResourceLocation particle = (ResourceLocation)Preconditions.checkNotNull(ForgeRegistries.PARTICLE_TYPES.getKey(type), "The particle type is not registered");
        List<String> desc = new ArrayList();
        Iterator var5 = textures.iterator();

        while(var5.hasNext()) {
            ResourceLocation texture = (ResourceLocation)var5.next();
            Preconditions.checkArgument(this.fileHelper.exists(texture, PackType.CLIENT_RESOURCES, ".png", "textures/particle"), "Texture '%s' does not exist in any known resource pack", texture);
            desc.add(texture.toString());
        }

        Preconditions.checkArgument(desc.size() > 0, "The particle type '%s' must have one texture", particle);
        if (this.descriptions.putIfAbsent(particle, desc) != null) {
            throw new IllegalArgumentException(String.format("The particle type '%s' already has a description associated with it", particle));
        }
    }

    public CompletableFuture<?> run(CachedOutput cache) {
        this.addDescriptions();
        return CompletableFuture.allOf((CompletableFuture[])this.descriptions.entrySet().stream().map((entry) -> {
            JsonArray textures = new JsonArray();
            List var10000 = (List)entry.getValue();
            Objects.requireNonNull(textures);
            var10000.forEach(textures::add);
            return DataProvider.saveStable(cache, (JsonElement)Util.make(new JsonObject(), (obj) -> {
                obj.add("textures", textures);
            }), this.particlesPath.json((ResourceLocation)entry.getKey()));
        }).toArray((x$0) -> {
            return new CompletableFuture[x$0];
        }));
    }

    public String getName() {
        return "Particle Descriptions";
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.resources.model;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AtlasSet implements AutoCloseable {
    private final Map<ResourceLocation, AtlasEntry> atlases;

    public AtlasSet(Map<ResourceLocation, ResourceLocation> p_249969_, TextureManager p_252059_) {
        this.atlases = (Map)p_249969_.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, (p_261403_) -> {
            TextureAtlas $$2 = new TextureAtlas((ResourceLocation)p_261403_.getKey());
            p_252059_.register((ResourceLocation)((ResourceLocation)p_261403_.getKey()), (AbstractTexture)$$2);
            return new AtlasEntry($$2, (ResourceLocation)p_261403_.getValue());
        }));
    }

    public TextureAtlas getAtlas(ResourceLocation p_250828_) {
        return ((AtlasEntry)this.atlases.get(p_250828_)).atlas();
    }

    public void close() {
        this.atlases.values().forEach(AtlasEntry::close);
        this.atlases.clear();
    }

    public Map<ResourceLocation, CompletableFuture<StitchResult>> scheduleLoad(ResourceManager p_249256_, int p_251059_, Executor p_250751_) {
        return (Map)this.atlases.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, (p_261401_) -> {
            AtlasEntry $$4 = (AtlasEntry)p_261401_.getValue();
            return SpriteLoader.create($$4.atlas).loadAndStitch(p_249256_, $$4.atlasInfoLocation, p_251059_, p_250751_).thenApply((p_250418_) -> {
                return new StitchResult($$4.atlas, p_250418_);
            });
        }));
    }

    @OnlyIn(Dist.CLIENT)
    static record AtlasEntry(TextureAtlas atlas, ResourceLocation atlasInfoLocation) implements AutoCloseable {
        AtlasEntry(TextureAtlas atlas, ResourceLocation atlasInfoLocation) {
            this.atlas = atlas;
            this.atlasInfoLocation = atlasInfoLocation;
        }

        public void close() {
            this.atlas.clearTextureData();
        }

        public TextureAtlas atlas() {
            return this.atlas;
        }

        public ResourceLocation atlasInfoLocation() {
            return this.atlasInfoLocation;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class StitchResult {
        private final TextureAtlas atlas;
        private final SpriteLoader.Preparations preparations;

        public StitchResult(TextureAtlas p_250381_, SpriteLoader.Preparations p_251137_) {
            this.atlas = p_250381_;
            this.preparations = p_251137_;
        }

        @Nullable
        public TextureAtlasSprite getSprite(ResourceLocation p_249039_) {
            return (TextureAtlasSprite)this.preparations.regions().get(p_249039_);
        }

        public TextureAtlasSprite missing() {
            return this.preparations.missing();
        }

        public CompletableFuture<Void> readyForUpload() {
            return this.preparations.readyForUpload();
        }

        public void upload() {
            this.atlas.upload(this.preparations);
        }
    }
}

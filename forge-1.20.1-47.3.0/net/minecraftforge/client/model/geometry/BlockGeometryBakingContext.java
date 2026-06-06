//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.client.model.geometry;

import com.mojang.math.Transformation;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public class BlockGeometryBakingContext implements IGeometryBakingContext {
    public final BlockModel owner;
    public final VisibilityData visibilityData = new VisibilityData();
    private @Nullable IUnbakedGeometry<?> customGeometry;
    private @Nullable Transformation rootTransform;
    private @Nullable ResourceLocation renderTypeHint;
    private boolean gui3d = true;

    @Internal
    public BlockGeometryBakingContext(BlockModel owner) {
        this.owner = owner;
    }

    public String getModelName() {
        return this.owner.name;
    }

    public boolean hasCustomGeometry() {
        return this.getCustomGeometry() != null;
    }

    public @Nullable IUnbakedGeometry<?> getCustomGeometry() {
        return this.owner.parent != null && this.customGeometry == null ? this.owner.parent.customData.getCustomGeometry() : this.customGeometry;
    }

    public void setCustomGeometry(IUnbakedGeometry<?> geometry) {
        this.customGeometry = geometry;
    }

    public boolean isComponentVisible(String part, boolean fallback) {
        return this.owner.parent != null && !this.visibilityData.hasCustomVisibility(part) ? this.owner.parent.customData.isComponentVisible(part, fallback) : this.visibilityData.isVisible(part, fallback);
    }

    public boolean hasMaterial(String name) {
        return this.owner.hasTexture(name);
    }

    public Material getMaterial(String name) {
        return this.owner.getMaterial(name);
    }

    public boolean isGui3d() {
        return this.gui3d;
    }

    public boolean useBlockLight() {
        return this.owner.getGuiLight().lightLikeBlock();
    }

    public boolean useAmbientOcclusion() {
        return this.owner.hasAmbientOcclusion();
    }

    public ItemTransforms getTransforms() {
        return this.owner.getTransforms();
    }

    public Transformation getRootTransform() {
        if (this.rootTransform != null) {
            return this.rootTransform;
        } else {
            return this.owner.parent != null ? this.owner.parent.customData.getRootTransform() : Transformation.identity();
        }
    }

    public void setRootTransform(Transformation rootTransform) {
        this.rootTransform = rootTransform;
    }

    public @Nullable ResourceLocation getRenderTypeHint() {
        if (this.renderTypeHint != null) {
            return this.renderTypeHint;
        } else {
            return this.owner.parent != null ? this.owner.parent.customData.getRenderTypeHint() : null;
        }
    }

    public void setRenderTypeHint(ResourceLocation renderTypeHint) {
        this.renderTypeHint = renderTypeHint;
    }

    public void setGui3d(boolean gui3d) {
        this.gui3d = gui3d;
    }

    public void copyFrom(BlockGeometryBakingContext other) {
        this.customGeometry = other.customGeometry;
        this.rootTransform = other.rootTransform;
        this.visibilityData.copyFrom(other.visibilityData);
        this.renderTypeHint = other.renderTypeHint;
        this.gui3d = other.gui3d;
    }

    public BakedModel bake(ModelBaker baker, Function<Material, TextureAtlasSprite> bakedTextureGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {
        IUnbakedGeometry<?> geometry = this.getCustomGeometry();
        if (geometry == null) {
            throw new IllegalStateException("Can not use custom baking without custom geometry");
        } else {
            return geometry.bake(this, baker, bakedTextureGetter, modelTransform, overrides, modelLocation);
        }
    }

    public static class VisibilityData {
        private final Map<String, Boolean> data = new HashMap();

        public VisibilityData() {
        }

        public boolean hasCustomVisibility(String part) {
            return this.data.containsKey(part);
        }

        public boolean isVisible(String part, boolean fallback) {
            return (Boolean)this.data.getOrDefault(part, fallback);
        }

        public void setVisibilityState(String partName, boolean type) {
            this.data.put(partName, type);
        }

        public void copyFrom(VisibilityData visibilityData) {
            this.data.clear();
            this.data.putAll(visibilityData.data);
        }
    }
}

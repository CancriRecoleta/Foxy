//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.resources.model;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class WeightedBakedModel implements IDynamicBakedModel {
    private final int totalWeight;
    private final List<WeightedEntry.Wrapper<BakedModel>> list;
    private final BakedModel wrapped;

    public WeightedBakedModel(List<WeightedEntry.Wrapper<BakedModel>> p_119544_) {
        this.list = p_119544_;
        this.totalWeight = WeightedRandom.getTotalWeight(p_119544_);
        this.wrapped = (BakedModel)((WeightedEntry.Wrapper)p_119544_.get(0)).getData();
    }

    public List<BakedQuad> getQuads(@Nullable BlockState p_235058_, @Nullable Direction p_235059_, RandomSource p_235060_, ModelData modelData, @org.jetbrains.annotations.Nullable RenderType renderType) {
        return (List)WeightedRandom.getWeightedItem(this.list, Math.abs((int)p_235060_.nextLong()) % this.totalWeight).map((p_235065_) -> {
            return ((BakedModel)p_235065_.getData()).getQuads(p_235058_, p_235059_, p_235060_, modelData, renderType);
        }).orElse(Collections.emptyList());
    }

    public boolean useAmbientOcclusion() {
        return this.wrapped.useAmbientOcclusion();
    }

    public boolean useAmbientOcclusion(BlockState state) {
        return this.wrapped.useAmbientOcclusion(state);
    }

    public boolean useAmbientOcclusion(BlockState state, RenderType renderType) {
        return this.wrapped.useAmbientOcclusion(state, renderType);
    }

    public boolean isGui3d() {
        return this.wrapped.isGui3d();
    }

    public boolean usesBlockLight() {
        return this.wrapped.usesBlockLight();
    }

    public boolean isCustomRenderer() {
        return this.wrapped.isCustomRenderer();
    }

    public TextureAtlasSprite getParticleIcon() {
        return this.wrapped.getParticleIcon();
    }

    public TextureAtlasSprite getParticleIcon(ModelData modelData) {
        return this.wrapped.getParticleIcon(modelData);
    }

    public ItemTransforms getTransforms() {
        return this.wrapped.getTransforms();
    }

    public BakedModel applyTransform(ItemDisplayContext transformType, PoseStack poseStack, boolean applyLeftHandTransform) {
        return this.wrapped.applyTransform(transformType, poseStack, applyLeftHandTransform);
    }

    public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data) {
        return (ChunkRenderTypeSet)WeightedRandom.getWeightedItem(this.list, Math.abs((int)rand.nextLong()) % this.totalWeight).map((p_235065_) -> {
            return ((BakedModel)p_235065_.getData()).getRenderTypes(state, rand, data);
        }).orElse(ChunkRenderTypeSet.none());
    }

    public ItemOverrides getOverrides() {
        return this.wrapped.getOverrides();
    }

    @OnlyIn(Dist.CLIENT)
    public static class Builder {
        private final List<WeightedEntry.Wrapper<BakedModel>> list = Lists.newArrayList();

        public Builder() {
        }

        public Builder add(@Nullable BakedModel p_119560_, int p_119561_) {
            if (p_119560_ != null) {
                this.list.add(WeightedEntry.wrap(p_119560_, p_119561_));
            }

            return this;
        }

        @Nullable
        public BakedModel build() {
            if (this.list.isEmpty()) {
                return null;
            } else {
                return (BakedModel)(this.list.size() == 1 ? (BakedModel)((WeightedEntry.Wrapper)this.list.get(0)).getData() : new WeightedBakedModel(this.list));
            }
        }
    }
}

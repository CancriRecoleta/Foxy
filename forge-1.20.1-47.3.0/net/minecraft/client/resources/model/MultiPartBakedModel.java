//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.resources.model;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.MultipartModelData;
import net.minecraftforge.common.util.ConcatenatedListView;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class MultiPartBakedModel implements IDynamicBakedModel {
    private final List<Pair<Predicate<BlockState>, BakedModel>> selectors;
    protected final boolean hasAmbientOcclusion;
    protected final boolean isGui3d;
    protected final boolean usesBlockLight;
    protected final TextureAtlasSprite particleIcon;
    protected final ItemTransforms transforms;
    protected final ItemOverrides overrides;
    private final Map<BlockState, BitSet> selectorCache = new Object2ObjectOpenCustomHashMap(Util.identityStrategy());
    private final BakedModel defaultModel;

    public MultiPartBakedModel(List<Pair<Predicate<BlockState>, BakedModel>> p_119462_) {
        this.selectors = p_119462_;
        BakedModel bakedmodel = (BakedModel)((Pair)p_119462_.iterator().next()).getRight();
        this.defaultModel = bakedmodel;
        this.hasAmbientOcclusion = bakedmodel.useAmbientOcclusion();
        this.isGui3d = bakedmodel.isGui3d();
        this.usesBlockLight = bakedmodel.usesBlockLight();
        this.particleIcon = bakedmodel.getParticleIcon();
        this.transforms = bakedmodel.getTransforms();
        this.overrides = bakedmodel.getOverrides();
    }

    public BitSet getSelectors(@Nullable BlockState p_235050_) {
        BitSet bitset = (BitSet)this.selectorCache.get(p_235050_);
        if (bitset == null) {
            bitset = new BitSet();

            for(int i = 0; i < this.selectors.size(); ++i) {
                Pair<Predicate<BlockState>, BakedModel> pair = (Pair)this.selectors.get(i);
                if (((Predicate)pair.getLeft()).test(p_235050_)) {
                    bitset.set(i);
                }
            }

            this.selectorCache.put(p_235050_, bitset);
        }

        return bitset;
    }

    public List<BakedQuad> getQuads(@Nullable BlockState p_235050_, @Nullable Direction p_235051_, RandomSource p_235052_, ModelData modelData, @org.jetbrains.annotations.Nullable RenderType renderType) {
        if (p_235050_ == null) {
            return Collections.emptyList();
        } else {
            BitSet bitset = this.getSelectors(p_235050_);
            List<List<BakedQuad>> list = Lists.newArrayList();
            long k = p_235052_.nextLong();

            for(int j = 0; j < bitset.length(); ++j) {
                if (bitset.get(j)) {
                    BakedModel model = (BakedModel)((Pair)this.selectors.get(j)).getRight();
                    if (renderType == null || model.getRenderTypes(p_235050_, p_235052_, modelData).contains(renderType)) {
                        list.add(model.getQuads(p_235050_, p_235051_, RandomSource.create(k), MultipartModelData.resolve(modelData, model), renderType));
                    }
                }
            }

            return ConcatenatedListView.of((List)list);
        }
    }

    public boolean useAmbientOcclusion() {
        return this.hasAmbientOcclusion;
    }

    public boolean useAmbientOcclusion(BlockState state) {
        return this.defaultModel.useAmbientOcclusion(state);
    }

    public boolean useAmbientOcclusion(BlockState state, RenderType renderType) {
        return this.defaultModel.useAmbientOcclusion(state, renderType);
    }

    public boolean isGui3d() {
        return this.isGui3d;
    }

    public boolean usesBlockLight() {
        return this.usesBlockLight;
    }

    public boolean isCustomRenderer() {
        return false;
    }

    /** @deprecated */
    @Deprecated
    public TextureAtlasSprite getParticleIcon() {
        return this.particleIcon;
    }

    public TextureAtlasSprite getParticleIcon(ModelData modelData) {
        return this.defaultModel.getParticleIcon(modelData);
    }

    /** @deprecated */
    @Deprecated
    public ItemTransforms getTransforms() {
        return this.transforms;
    }

    public BakedModel applyTransform(ItemDisplayContext transformType, PoseStack poseStack, boolean applyLeftHandTransform) {
        return this.defaultModel.applyTransform(transformType, poseStack, applyLeftHandTransform);
    }

    public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data) {
        LinkedList<ChunkRenderTypeSet> renderTypeSets = new LinkedList();
        BitSet selectors = this.getSelectors(state);

        for(int i = 0; i < selectors.length(); ++i) {
            if (selectors.get(i)) {
                renderTypeSets.add(((BakedModel)((Pair)this.selectors.get(i)).getRight()).getRenderTypes(state, rand, data));
            }
        }

        return ChunkRenderTypeSet.union((Collection)renderTypeSets);
    }

    public ItemOverrides getOverrides() {
        return this.overrides;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Builder {
        private final List<Pair<Predicate<BlockState>, BakedModel>> selectors = Lists.newArrayList();

        public Builder() {
        }

        public void add(Predicate<BlockState> p_119478_, BakedModel p_119479_) {
            this.selectors.add(Pair.of(p_119478_, p_119479_));
        }

        public BakedModel build() {
            return new MultiPartBakedModel(this.selectors);
        }
    }
}

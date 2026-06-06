//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.UnmodifiableIterator;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.math.Transformation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import net.minecraftforge.client.model.geometry.UnbakedGeometryHelper;
import net.minecraftforge.common.util.ConcatenatedListView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CompositeModel implements IUnbakedGeometry<CompositeModel> {
    private final ImmutableMap<String, BlockModel> children;
    private final ImmutableList<String> itemPasses;

    public CompositeModel(ImmutableMap<String, BlockModel> children, ImmutableList<String> itemPasses) {
        this.children = children;
        this.itemPasses = itemPasses;
    }

    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
        Material particleLocation = context.getMaterial("particle");
        TextureAtlasSprite particle = (TextureAtlasSprite)spriteGetter.apply(particleLocation);
        Transformation rootTransform = context.getRootTransform();
        if (!rootTransform.isIdentity()) {
            modelState = UnbakedGeometryHelper.composeRootTransformIntoModelState(modelState, rootTransform);
        }

        ImmutableMap.Builder<String, BakedModel> bakedPartsBuilder = ImmutableMap.builder();
        UnmodifiableIterator var11 = this.children.entrySet().iterator();

        while(var11.hasNext()) {
            Map.Entry<String, BlockModel> entry = (Map.Entry)var11.next();
            String name = (String)entry.getKey();
            if (context.isComponentVisible(name, true)) {
                BlockModel model = (BlockModel)entry.getValue();
                bakedPartsBuilder.put(name, model.bake(baker, model, spriteGetter, modelState, modelLocation, true));
            }
        }

        ImmutableMap<String, BakedModel> bakedParts = bakedPartsBuilder.build();
        ImmutableList.Builder<BakedModel> itemPassesBuilder = ImmutableList.builder();
        UnmodifiableIterator var18 = this.itemPasses.iterator();

        while(var18.hasNext()) {
            String name = (String)var18.next();
            BakedModel model = (BakedModel)bakedParts.get(name);
            if (model == null) {
                throw new IllegalStateException("Specified \"" + name + "\" in \"item_render_order\", but that is not a child of this model.");
            }

            itemPassesBuilder.add(model);
        }

        return new Baked(context.isGui3d(), context.useBlockLight(), context.useAmbientOcclusion(), particle, context.getTransforms(), overrides, bakedParts, itemPassesBuilder.build());
    }

    public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context) {
        this.children.values().forEach((child) -> {
            child.resolveParents(modelGetter);
        });
    }

    public Set<String> getConfigurableComponentNames() {
        return this.children.keySet();
    }

    public static class Baked implements IDynamicBakedModel {
        private final boolean isAmbientOcclusion;
        private final boolean isGui3d;
        private final boolean isSideLit;
        private final TextureAtlasSprite particle;
        private final ItemOverrides overrides;
        private final ItemTransforms transforms;
        private final ImmutableMap<String, BakedModel> children;
        private final ImmutableList<BakedModel> itemPasses;

        public Baked(boolean isGui3d, boolean isSideLit, boolean isAmbientOcclusion, TextureAtlasSprite particle, ItemTransforms transforms, ItemOverrides overrides, ImmutableMap<String, BakedModel> children, ImmutableList<BakedModel> itemPasses) {
            this.children = children;
            this.isAmbientOcclusion = isAmbientOcclusion;
            this.isGui3d = isGui3d;
            this.isSideLit = isSideLit;
            this.particle = particle;
            this.overrides = overrides;
            this.transforms = transforms;
            this.itemPasses = itemPasses;
        }

        public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType) {
            List<List<BakedQuad>> quadLists = new ArrayList();
            UnmodifiableIterator var7 = this.children.entrySet().iterator();

            while(true) {
                Map.Entry entry;
                do {
                    if (!var7.hasNext()) {
                        return ConcatenatedListView.of((List)quadLists);
                    }

                    entry = (Map.Entry)var7.next();
                } while(renderType != null && (state == null || !((BakedModel)entry.getValue()).getRenderTypes(state, rand, data).contains(renderType)));

                quadLists.add(((BakedModel)entry.getValue()).getQuads(state, side, rand, net.minecraftforge.client.model.CompositeModel.Data.resolve(data, (String)entry.getKey()), renderType));
            }
        }

        public @NotNull ModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ModelData modelData) {
            Data.Builder builder = net.minecraftforge.client.model.CompositeModel.Data.builder();
            UnmodifiableIterator var6 = this.children.entrySet().iterator();

            while(var6.hasNext()) {
                Map.Entry<String, BakedModel> entry = (Map.Entry)var6.next();
                builder.with((String)entry.getKey(), ((BakedModel)entry.getValue()).getModelData(level, pos, state, net.minecraftforge.client.model.CompositeModel.Data.resolve(modelData, (String)entry.getKey())));
            }

            return modelData.derive().with(net.minecraftforge.client.model.CompositeModel.Data.PROPERTY, builder.build()).build();
        }

        public boolean useAmbientOcclusion() {
            return this.isAmbientOcclusion;
        }

        public boolean isGui3d() {
            return this.isGui3d;
        }

        public boolean usesBlockLight() {
            return this.isSideLit;
        }

        public boolean isCustomRenderer() {
            return false;
        }

        public TextureAtlasSprite getParticleIcon() {
            return this.particle;
        }

        public ItemOverrides getOverrides() {
            return this.overrides;
        }

        public ItemTransforms getTransforms() {
            return this.transforms;
        }

        public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data) {
            ArrayList<ChunkRenderTypeSet> sets = new ArrayList();
            UnmodifiableIterator var5 = this.children.entrySet().iterator();

            while(var5.hasNext()) {
                Map.Entry<String, BakedModel> entry = (Map.Entry)var5.next();
                sets.add(((BakedModel)entry.getValue()).getRenderTypes(state, rand, net.minecraftforge.client.model.CompositeModel.Data.resolve(data, (String)entry.getKey())));
            }

            return ChunkRenderTypeSet.union((Collection)sets);
        }

        public List<BakedModel> getRenderPasses(ItemStack itemStack, boolean fabulous) {
            return this.itemPasses;
        }

        public @Nullable BakedModel getPart(String name) {
            return (BakedModel)this.children.get(name);
        }

        public static Builder builder(IGeometryBakingContext owner, TextureAtlasSprite particle, ItemOverrides overrides, ItemTransforms cameraTransforms) {
            return builder(owner.useAmbientOcclusion(), owner.isGui3d(), owner.useBlockLight(), particle, overrides, cameraTransforms);
        }

        public static Builder builder(boolean isAmbientOcclusion, boolean isGui3d, boolean isSideLit, TextureAtlasSprite particle, ItemOverrides overrides, ItemTransforms cameraTransforms) {
            return new Builder(isAmbientOcclusion, isGui3d, isSideLit, particle, overrides, cameraTransforms);
        }

        public static class Builder {
            private final boolean isAmbientOcclusion;
            private final boolean isGui3d;
            private final boolean isSideLit;
            private final List<BakedModel> children = new ArrayList();
            private final List<BakedQuad> quads = new ArrayList();
            private final ItemOverrides overrides;
            private final ItemTransforms transforms;
            private TextureAtlasSprite particle;
            private RenderTypeGroup lastRenderTypes;

            private Builder(boolean isAmbientOcclusion, boolean isGui3d, boolean isSideLit, TextureAtlasSprite particle, ItemOverrides overrides, ItemTransforms transforms) {
                this.lastRenderTypes = RenderTypeGroup.EMPTY;
                this.isAmbientOcclusion = isAmbientOcclusion;
                this.isGui3d = isGui3d;
                this.isSideLit = isSideLit;
                this.particle = particle;
                this.overrides = overrides;
                this.transforms = transforms;
            }

            public void addLayer(BakedModel model) {
                this.flushQuads((RenderTypeGroup)null);
                this.children.add(model);
            }

            private void addLayer(RenderTypeGroup renderTypes, List<BakedQuad> quads) {
                IModelBuilder<? extends IModelBuilder<?>> modelBuilder = IModelBuilder.of(this.isAmbientOcclusion, this.isSideLit, this.isGui3d, this.transforms, this.overrides, this.particle, renderTypes);
                Objects.requireNonNull(modelBuilder);
                quads.forEach(modelBuilder::addUnculledFace);
                this.children.add(modelBuilder.build());
            }

            private void flushQuads(RenderTypeGroup renderTypes) {
                if (!Objects.equals(renderTypes, this.lastRenderTypes)) {
                    if (this.quads.size() > 0) {
                        this.addLayer(this.lastRenderTypes, this.quads);
                        this.quads.clear();
                    }

                    this.lastRenderTypes = renderTypes;
                }

            }

            public Builder setParticle(TextureAtlasSprite particleSprite) {
                this.particle = particleSprite;
                return this;
            }

            public Builder addQuads(RenderTypeGroup renderTypes, BakedQuad... quadsToAdd) {
                this.flushQuads(renderTypes);
                Collections.addAll(this.quads, quadsToAdd);
                return this;
            }

            public Builder addQuads(RenderTypeGroup renderTypes, Collection<BakedQuad> quadsToAdd) {
                this.flushQuads(renderTypes);
                this.quads.addAll(quadsToAdd);
                return this;
            }

            public BakedModel build() {
                if (this.quads.size() > 0) {
                    this.addLayer(this.lastRenderTypes, this.quads);
                }

                ImmutableMap.Builder<String, BakedModel> childrenBuilder = ImmutableMap.builder();
                ImmutableList.Builder<BakedModel> itemPassesBuilder = ImmutableList.builder();
                int i = 0;
                Iterator var4 = this.children.iterator();

                while(var4.hasNext()) {
                    BakedModel model = (BakedModel)var4.next();
                    int var10001 = i++;
                    childrenBuilder.put("model_" + var10001, model);
                    itemPassesBuilder.add(model);
                }

                return new Baked(this.isGui3d, this.isSideLit, this.isAmbientOcclusion, this.particle, this.transforms, this.overrides, childrenBuilder.build(), itemPassesBuilder.build());
            }
        }
    }

    public static final class Loader implements IGeometryLoader<CompositeModel> {
        public static final Loader INSTANCE = new Loader();

        private Loader() {
        }

        public CompositeModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) {
            List<String> itemPasses = new ArrayList();
            ImmutableMap.Builder<String, BlockModel> childrenBuilder = ImmutableMap.builder();
            this.readChildren(jsonObject, "children", deserializationContext, childrenBuilder, itemPasses);
            ImmutableMap<String, BlockModel> children = childrenBuilder.build();
            if (children.isEmpty()) {
                throw new JsonParseException("Composite model requires a \"children\" element with at least one element.");
            } else {
                if (jsonObject.has("item_render_order")) {
                    itemPasses.clear();
                    Iterator var6 = jsonObject.getAsJsonArray("item_render_order").iterator();

                    while(var6.hasNext()) {
                        JsonElement element = (JsonElement)var6.next();
                        String name = element.getAsString();
                        if (!children.containsKey(name)) {
                            throw new JsonParseException("Specified \"" + name + "\" in \"item_render_order\", but that is not a child of this model.");
                        }

                        itemPasses.add(name);
                    }
                }

                return new CompositeModel(children, ImmutableList.copyOf(itemPasses));
            }
        }

        private void readChildren(JsonObject jsonObject, String name, JsonDeserializationContext deserializationContext, ImmutableMap.Builder<String, BlockModel> children, List<String> itemPasses) {
            if (jsonObject.has(name)) {
                JsonObject childrenJsonObject = jsonObject.getAsJsonObject(name);
                Iterator var7 = childrenJsonObject.entrySet().iterator();

                while(var7.hasNext()) {
                    Map.Entry<String, JsonElement> entry = (Map.Entry)var7.next();
                    children.put((String)entry.getKey(), (BlockModel)deserializationContext.deserialize((JsonElement)entry.getValue(), BlockModel.class));
                    itemPasses.add((String)entry.getKey());
                }

            }
        }
    }

    public static class Data {
        public static final ModelProperty<Data> PROPERTY = new ModelProperty();
        private final Map<String, ModelData> partData;

        private Data(Map<String, ModelData> partData) {
            this.partData = partData;
        }

        public @Nullable ModelData get(String name) {
            return (ModelData)this.partData.get(name);
        }

        public static ModelData resolve(ModelData modelData, String name) {
            Data compositeData = (Data)modelData.get(PROPERTY);
            if (compositeData == null) {
                return modelData;
            } else {
                ModelData partData = compositeData.get(name);
                return partData != null ? partData : modelData;
            }
        }

        public static Builder builder() {
            return new Builder();
        }

        public static final class Builder {
            private final Map<String, ModelData> partData = new IdentityHashMap();

            public Builder() {
            }

            public Builder with(String name, ModelData data) {
                this.partData.put(name, data);
                return this;
            }

            public Data build() {
                return new Data(this.partData);
            }
        }
    }
}

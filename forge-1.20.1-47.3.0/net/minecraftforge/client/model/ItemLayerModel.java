//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.client.model;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.math.Transformation;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.ForgeRenderTypes;
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.client.model.CompositeModel.Baked;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import net.minecraftforge.client.model.geometry.UnbakedGeometryHelper;
import org.jetbrains.annotations.Nullable;

public class ItemLayerModel implements IUnbakedGeometry<ItemLayerModel> {
    private @Nullable ImmutableList<Material> textures;
    private final Int2ObjectMap<ForgeFaceData> layerData;
    private final Int2ObjectMap<ResourceLocation> renderTypeNames;

    private ItemLayerModel(@Nullable ImmutableList<Material> textures, Int2ObjectMap<ForgeFaceData> layerData, Int2ObjectMap<ResourceLocation> renderTypeNames) {
        this.textures = textures;
        this.layerData = layerData;
        this.renderTypeNames = renderTypeNames;
    }

    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
        if (this.textures == null) {
            ImmutableList.Builder<Material> builder = ImmutableList.builder();

            for(int i = 0; context.hasMaterial("layer" + i); ++i) {
                builder.add(context.getMaterial("layer" + i));
            }

            this.textures = builder.build();
        }

        TextureAtlasSprite particle = (TextureAtlasSprite)spriteGetter.apply(context.hasMaterial("particle") ? context.getMaterial("particle") : (Material)this.textures.get(0));
        Transformation rootTransform = context.getRootTransform();
        if (!rootTransform.isIdentity()) {
            modelState = UnbakedGeometryHelper.composeRootTransformIntoModelState(modelState, rootTransform);
        }

        RenderTypeGroup normalRenderTypes = new RenderTypeGroup(RenderType.translucent(), ForgeRenderTypes.ITEM_UNSORTED_TRANSLUCENT.get());
        CompositeModel.Baked.Builder builder = Baked.builder(context, particle, overrides, context.getTransforms());

        for(int i = 0; i < this.textures.size(); ++i) {
            TextureAtlasSprite sprite = (TextureAtlasSprite)spriteGetter.apply((Material)this.textures.get(i));
            List<BlockElement> unbaked = UnbakedGeometryHelper.createUnbakedItemElements(i, sprite.contents(), (ForgeFaceData)this.layerData.get(i));
            List<BakedQuad> quads = UnbakedGeometryHelper.bakeElements(unbaked, ($) -> {
                return sprite;
            }, modelState, modelLocation);
            ResourceLocation renderTypeName = (ResourceLocation)this.renderTypeNames.get(i);
            RenderTypeGroup renderTypes = renderTypeName != null ? context.getRenderType(renderTypeName) : null;
            builder.addQuads(renderTypes != null ? renderTypes : normalRenderTypes, (Collection)quads);
        }

        return builder.build();
    }

    public static final class Loader implements IGeometryLoader<ItemLayerModel> {
        public static final Loader INSTANCE = new Loader();

        public Loader() {
        }

        public ItemLayerModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) {
            Int2ObjectOpenHashMap<ResourceLocation> renderTypeNames = new Int2ObjectOpenHashMap();
            if (jsonObject.has("render_types")) {
                JsonObject renderTypes = jsonObject.getAsJsonObject("render_types");
                Iterator var5 = renderTypes.entrySet().iterator();

                while(var5.hasNext()) {
                    Map.Entry<String, JsonElement> entry = (Map.Entry)var5.next();
                    ResourceLocation renderType = new ResourceLocation((String)entry.getKey());
                    Iterator var8 = ((JsonElement)entry.getValue()).getAsJsonArray().iterator();

                    while(var8.hasNext()) {
                        JsonElement layer = (JsonElement)var8.next();
                        if (renderTypeNames.put(layer.getAsInt(), renderType) != null) {
                            throw new JsonParseException("Registered duplicate render type for layer " + layer);
                        }
                    }
                }
            }

            Int2ObjectArrayMap<ForgeFaceData> emissiveLayers = new Int2ObjectArrayMap();
            if (jsonObject.has("forge_data")) {
                JsonObject forgeData = jsonObject.get("forge_data").getAsJsonObject();
                this.readLayerData(forgeData, "layers", renderTypeNames, emissiveLayers, false);
            }

            return new ItemLayerModel((ImmutableList)null, emissiveLayers, renderTypeNames);
        }

        protected void readLayerData(JsonObject jsonObject, String name, Int2ObjectOpenHashMap<ResourceLocation> renderTypeNames, Int2ObjectMap<ForgeFaceData> layerData, boolean logWarning) {
            if (jsonObject.has(name)) {
                JsonObject fullbrightLayers = jsonObject.getAsJsonObject(name);
                Iterator var7 = fullbrightLayers.entrySet().iterator();

                while(var7.hasNext()) {
                    Map.Entry<String, JsonElement> entry = (Map.Entry)var7.next();
                    int layer = Integer.parseInt((String)entry.getKey());
                    ForgeFaceData data = ForgeFaceData.read((JsonElement)entry.getValue(), ForgeFaceData.DEFAULT);
                    layerData.put(layer, data);
                }

            }
        }
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.client.model.generators.loaders;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.ForgeFaceData;
import net.minecraftforge.client.model.generators.CustomLoaderBuilder;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemLayerModelBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T> {
    private final Int2ObjectMap<ForgeFaceData> faceData = new Int2ObjectOpenHashMap();
    private final Map<ResourceLocation, IntSet> renderTypes = new LinkedHashMap();
    private final IntSet layersWithRenderTypes = new IntOpenHashSet();

    public static <T extends ModelBuilder<T>> ItemLayerModelBuilder<T> begin(T parent, ExistingFileHelper existingFileHelper) {
        return new ItemLayerModelBuilder(parent, existingFileHelper);
    }

    protected ItemLayerModelBuilder(T parent, ExistingFileHelper existingFileHelper) {
        super(new ResourceLocation("forge:item_layers"), parent, existingFileHelper);
    }

    public ItemLayerModelBuilder<T> emissive(int blockLight, int skyLight, int... layers) {
        Preconditions.checkNotNull(layers, "Layers must not be null");
        Preconditions.checkArgument(layers.length > 0, "At least one layer must be specified");
        Preconditions.checkArgument(Arrays.stream(layers).allMatch((ix) -> {
            return ix >= 0;
        }), "All layers must be >= 0");
        int[] var4 = layers;
        int var5 = layers.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            int i = var4[var6];
            this.faceData.compute(i, (key, value) -> {
                ForgeFaceData fallback = value == null ? ForgeFaceData.DEFAULT : value;
                return new ForgeFaceData(fallback.color(), blockLight, skyLight, fallback.ambientOcclusion(), fallback.calculateNormals());
            });
        }

        return this;
    }

    public ItemLayerModelBuilder<T> color(int color, int... layers) {
        Preconditions.checkNotNull(layers, "Layers must not be null");
        Preconditions.checkArgument(layers.length > 0, "At least one layer must be specified");
        Preconditions.checkArgument(Arrays.stream(layers).allMatch((ix) -> {
            return ix >= 0;
        }), "All layers must be >= 0");
        int[] var3 = layers;
        int var4 = layers.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            int i = var3[var5];
            this.faceData.compute(i, (key, value) -> {
                ForgeFaceData fallback = value == null ? ForgeFaceData.DEFAULT : value;
                return new ForgeFaceData(color, fallback.blockLight(), fallback.skyLight(), fallback.ambientOcclusion(), fallback.calculateNormals());
            });
        }

        return this;
    }

    public ItemLayerModelBuilder<T> renderType(String renderType, int... layers) {
        Preconditions.checkNotNull(renderType, "Render type must not be null");
        ResourceLocation asLoc;
        if (renderType.contains(":")) {
            asLoc = new ResourceLocation(renderType);
        } else {
            asLoc = new ResourceLocation(this.parent.getLocation().getNamespace(), renderType);
        }

        return this.renderType(asLoc, layers);
    }

    public ItemLayerModelBuilder<T> renderType(ResourceLocation renderType, int... layers) {
        Preconditions.checkNotNull(renderType, "Render type must not be null");
        Preconditions.checkNotNull(layers, "Layers must not be null");
        Preconditions.checkArgument(layers.length > 0, "At least one layer must be specified");
        Preconditions.checkArgument(Arrays.stream(layers).allMatch((i) -> {
            return i >= 0;
        }), "All layers must be >= 0");
        IntStream var10000 = Arrays.stream(layers);
        IntSet var10001 = this.layersWithRenderTypes;
        Objects.requireNonNull(var10001);
        int[] alreadyAssigned = var10000.filter(var10001::contains).toArray();
        Preconditions.checkArgument(alreadyAssigned.length == 0, "Attempted to re-assign layer render types: " + Arrays.toString(alreadyAssigned));
        IntSet renderTypeLayers = (IntSet)this.renderTypes.computeIfAbsent(renderType, ($) -> {
            return new IntOpenHashSet();
        });
        Arrays.stream(layers).forEach((layer) -> {
            renderTypeLayers.add(layer);
            this.layersWithRenderTypes.add(layer);
        });
        return this;
    }

    public JsonObject toJson(JsonObject json) {
        json = super.toJson(json);
        JsonObject forgeData = new JsonObject();
        JsonObject layerObj = new JsonObject();
        ObjectIterator var4 = this.faceData.int2ObjectEntrySet().iterator();

        while(var4.hasNext()) {
            Int2ObjectMap.Entry<ForgeFaceData> entry = (Int2ObjectMap.Entry)var4.next();
            layerObj.add(String.valueOf(entry.getIntKey()), (JsonElement)ForgeFaceData.CODEC.encodeStart(JsonOps.INSTANCE, (ForgeFaceData)entry.getValue()).getOrThrow(false, (s) -> {
            }));
        }

        forgeData.add("layers", layerObj);
        json.add("forge_data", forgeData);
        JsonObject renderTypes = new JsonObject();
        this.renderTypes.forEach((renderType, layers) -> {
            JsonArray array = new JsonArray();
            IntStream var10000 = layers.intStream().sorted();
            Objects.requireNonNull(array);
            var10000.forEach(array::add);
            renderTypes.add(renderType.toString(), array);
        });
        json.add("render_types", renderTypes);
        return json;
    }
}

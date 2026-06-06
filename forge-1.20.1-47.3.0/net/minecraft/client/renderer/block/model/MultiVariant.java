//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.resources.model.WeightedBakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MultiVariant implements UnbakedModel {
    private final List<Variant> variants;

    public MultiVariant(List<Variant> p_111847_) {
        this.variants = p_111847_;
    }

    public List<Variant> getVariants() {
        return this.variants;
    }

    public boolean equals(Object p_111862_) {
        if (this == p_111862_) {
            return true;
        } else if (p_111862_ instanceof MultiVariant) {
            MultiVariant multivariant = (MultiVariant)p_111862_;
            return this.variants.equals(multivariant.variants);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.variants.hashCode();
    }

    public Collection<ResourceLocation> getDependencies() {
        return (Collection)this.getVariants().stream().map(Variant::getModelLocation).collect(Collectors.toSet());
    }

    public void resolveParents(Function<ResourceLocation, UnbakedModel> p_249314_) {
        this.getVariants().stream().map(Variant::getModelLocation).distinct().forEach((p_247934_) -> {
            ((UnbakedModel)p_249314_.apply(p_247934_)).resolveParents(p_249314_);
        });
    }

    @Nullable
    public BakedModel bake(ModelBaker p_249016_, Function<Material, TextureAtlasSprite> p_111851_, ModelState p_111852_, ResourceLocation p_111853_) {
        if (this.getVariants().isEmpty()) {
            return null;
        } else {
            WeightedBakedModel.Builder weightedbakedmodel$builder = new WeightedBakedModel.Builder();
            Iterator var6 = this.getVariants().iterator();

            while(var6.hasNext()) {
                Variant variant = (Variant)var6.next();
                BakedModel bakedmodel = p_249016_.bake(variant.getModelLocation(), variant, p_111851_);
                weightedbakedmodel$builder.add(bakedmodel, variant.getWeight());
            }

            return weightedbakedmodel$builder.build();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Deserializer implements JsonDeserializer<MultiVariant> {
        public Deserializer() {
        }

        public MultiVariant deserialize(JsonElement p_111867_, Type p_111868_, JsonDeserializationContext p_111869_) throws JsonParseException {
            List<Variant> list = Lists.newArrayList();
            if (p_111867_.isJsonArray()) {
                JsonArray jsonarray = p_111867_.getAsJsonArray();
                if (jsonarray.size() == 0) {
                    throw new JsonParseException("Empty variant array");
                }

                Iterator var6 = jsonarray.iterator();

                while(var6.hasNext()) {
                    JsonElement jsonelement = (JsonElement)var6.next();
                    list.add((Variant)p_111869_.deserialize(jsonelement, Variant.class));
                }
            } else {
                list.add((Variant)p_111869_.deserialize(p_111867_, Variant.class));
            }

            return new MultiVariant(list);
        }
    }
}

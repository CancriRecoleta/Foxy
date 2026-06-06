//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.math.Transformation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.SimpleUnbakedGeometry;
import net.minecraftforge.client.model.geometry.UnbakedGeometryHelper;

public class ElementsModel extends SimpleUnbakedGeometry<ElementsModel> {
    private final List<BlockElement> elements;

    public ElementsModel(List<BlockElement> elements) {
        this.elements = elements;
    }

    protected void addQuads(IGeometryBakingContext context, IModelBuilder<?> modelBuilder, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ResourceLocation modelLocation) {
        IQuadTransformer postTransform = QuadTransformers.empty();
        Transformation rootTransform = context.getRootTransform();
        if (!rootTransform.isIdentity()) {
            postTransform = UnbakedGeometryHelper.applyRootTransform(modelState, rootTransform);
        }

        Iterator var9 = this.elements.iterator();

        while(var9.hasNext()) {
            BlockElement element = (BlockElement)var9.next();
            Iterator var11 = element.faces.keySet().iterator();

            while(var11.hasNext()) {
                Direction direction = (Direction)var11.next();
                BlockElementFace face = (BlockElementFace)element.faces.get(direction);
                TextureAtlasSprite sprite = (TextureAtlasSprite)spriteGetter.apply(context.getMaterial(face.texture));
                BakedQuad quad = BlockModel.bakeFace(element, face, sprite, direction, modelState, modelLocation);
                postTransform.processInPlace(quad);
                if (face.cullForDirection == null) {
                    modelBuilder.addUnculledFace(quad);
                } else {
                    modelBuilder.addCulledFace(modelState.getRotation().rotateTransform(face.cullForDirection), quad);
                }
            }
        }

    }

    public static final class Loader implements IGeometryLoader<ElementsModel> {
        public static final Loader INSTANCE = new Loader();

        private Loader() {
        }

        public ElementsModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
            if (!jsonObject.has("elements")) {
                throw new JsonParseException("An element model must have an \"elements\" member.");
            } else {
                List<BlockElement> elements = new ArrayList();
                Iterator var4 = GsonHelper.getAsJsonArray(jsonObject, "elements").iterator();

                while(var4.hasNext()) {
                    JsonElement element = (JsonElement)var4.next();
                    elements.add((BlockElement)deserializationContext.deserialize(element, BlockElement.class));
                }

                return new ElementsModel(elements);
            }
        }
    }
}

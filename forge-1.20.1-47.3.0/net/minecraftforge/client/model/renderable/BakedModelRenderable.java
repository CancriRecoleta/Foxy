//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.client.model.renderable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Arrays;
import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector4f;

public class BakedModelRenderable implements IRenderable<Context> {
    private final BakedModel model;

    public static BakedModelRenderable of(ResourceLocation model) {
        return of(Minecraft.getInstance().getModelManager().getModel(model));
    }

    public static BakedModelRenderable of(BakedModel model) {
        return new BakedModelRenderable(model);
    }

    private BakedModelRenderable(BakedModel model) {
        this.model = model;
    }

    public void render(PoseStack poseStack, MultiBufferSource bufferSource, ITextureRenderTypeLookup textureRenderTypeLookup, int lightmap, int overlay, float partialTick, Context context) {
        VertexConsumer buffer = bufferSource.getBuffer(textureRenderTypeLookup.get(InventoryMenu.BLOCK_ATLAS));
        Vector4f tint = context.tint();
        RandomSource randomSource = context.randomSource();
        Direction[] var11 = context.faces();
        int var12 = var11.length;

        for(int var13 = 0; var13 < var12; ++var13) {
            Direction direction = var11[var13];
            randomSource.setSeed(context.seed());
            Iterator var15 = this.model.getQuads(context.state(), direction, randomSource, context.data(), (RenderType)null).iterator();

            while(var15.hasNext()) {
                BakedQuad quad = (BakedQuad)var15.next();
                buffer.putBulkData(poseStack.last(), quad, tint.x(), tint.y(), tint.z(), tint.w(), lightmap, overlay, true);
            }
        }

    }

    public IRenderable<Unit> withContext(ModelData modelData) {
        return this.withContext(new Context(modelData));
    }

    public IRenderable<ModelData> withModelDataContext() {
        return (poseStack, bufferSource, textureRenderTypeLookup, lightmap, overlay, partialTick, context) -> {
            this.render(poseStack, bufferSource, textureRenderTypeLookup, lightmap, overlay, partialTick, new Context(context));
        };
    }

    public static record Context(@Nullable BlockState state, Direction[] faces, RandomSource randomSource, long seed, ModelData data, Vector4f tint) {
        private static final Direction[] ALL_FACES_AND_NULL = (Direction[])Arrays.copyOf(Direction.values(), Direction.values().length + 1);
        private static final Vector4f WHITE = new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);

        public Context(ModelData data) {
            this((BlockState)null, ALL_FACES_AND_NULL, RandomSource.create(), 42L, data, WHITE);
        }

        public Context(@Nullable BlockState state, Direction[] faces, RandomSource randomSource, long seed, ModelData data, Vector4f tint) {
            this.state = state;
            this.faces = faces;
            this.randomSource = randomSource;
            this.seed = seed;
            this.data = data;
            this.tint = tint;
        }

        public @Nullable BlockState state() {
            return this.state;
        }

        public Direction[] faces() {
            return this.faces;
        }

        public RandomSource randomSource() {
            return this.randomSource;
        }

        public long seed() {
            return this.seed;
        }

        public ModelData data() {
            return this.data;
        }

        public Vector4f tint() {
            return this.tint;
        }
    }
}

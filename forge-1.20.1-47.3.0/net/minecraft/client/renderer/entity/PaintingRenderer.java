//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.PaintingTextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class PaintingRenderer extends EntityRenderer<Painting> {
    public PaintingRenderer(EntityRendererProvider.Context p_174332_) {
        super(p_174332_);
    }

    public void render(Painting p_115552_, float p_115553_, float p_115554_, PoseStack p_115555_, MultiBufferSource p_115556_, int p_115557_) {
        p_115555_.pushPose();
        p_115555_.mulPose(Axis.YP.rotationDegrees(180.0F - p_115553_));
        PaintingVariant $$6 = (PaintingVariant)p_115552_.getVariant().value();
        float $$7 = 0.0625F;
        p_115555_.scale(0.0625F, 0.0625F, 0.0625F);
        VertexConsumer $$8 = p_115556_.getBuffer(RenderType.entitySolid(this.getTextureLocation(p_115552_)));
        PaintingTextureManager $$9 = Minecraft.getInstance().getPaintingTextures();
        this.renderPainting(p_115555_, $$8, p_115552_, $$6.getWidth(), $$6.getHeight(), $$9.get($$6), $$9.getBackSprite());
        p_115555_.popPose();
        super.render(p_115552_, p_115553_, p_115554_, p_115555_, p_115556_, p_115557_);
    }

    public ResourceLocation getTextureLocation(Painting p_115550_) {
        return Minecraft.getInstance().getPaintingTextures().getBackSprite().atlasLocation();
    }

    private void renderPainting(PoseStack p_115559_, VertexConsumer p_115560_, Painting p_115561_, int p_115562_, int p_115563_, TextureAtlasSprite p_115564_, TextureAtlasSprite p_115565_) {
        PoseStack.Pose $$7 = p_115559_.last();
        Matrix4f $$8 = $$7.pose();
        Matrix3f $$9 = $$7.normal();
        float $$10 = (float)(-p_115562_) / 2.0F;
        float $$11 = (float)(-p_115563_) / 2.0F;
        float $$12 = 0.5F;
        float $$13 = p_115565_.getU0();
        float $$14 = p_115565_.getU1();
        float $$15 = p_115565_.getV0();
        float $$16 = p_115565_.getV1();
        float $$17 = p_115565_.getU0();
        float $$18 = p_115565_.getU1();
        float $$19 = p_115565_.getV0();
        float $$20 = p_115565_.getV(1.0);
        float $$21 = p_115565_.getU0();
        float $$22 = p_115565_.getU(1.0);
        float $$23 = p_115565_.getV0();
        float $$24 = p_115565_.getV1();
        int $$25 = p_115562_ / 16;
        int $$26 = p_115563_ / 16;
        double $$27 = 16.0 / (double)$$25;
        double $$28 = 16.0 / (double)$$26;

        for(int $$29 = 0; $$29 < $$25; ++$$29) {
            for(int $$30 = 0; $$30 < $$26; ++$$30) {
                float $$31 = $$10 + (float)(($$29 + 1) * 16);
                float $$32 = $$10 + (float)($$29 * 16);
                float $$33 = $$11 + (float)(($$30 + 1) * 16);
                float $$34 = $$11 + (float)($$30 * 16);
                int $$35 = p_115561_.getBlockX();
                int $$36 = Mth.floor(p_115561_.getY() + (double)(($$33 + $$34) / 2.0F / 16.0F));
                int $$37 = p_115561_.getBlockZ();
                Direction $$38 = p_115561_.getDirection();
                if ($$38 == Direction.NORTH) {
                    $$35 = Mth.floor(p_115561_.getX() + (double)(($$31 + $$32) / 2.0F / 16.0F));
                }

                if ($$38 == Direction.WEST) {
                    $$37 = Mth.floor(p_115561_.getZ() - (double)(($$31 + $$32) / 2.0F / 16.0F));
                }

                if ($$38 == Direction.SOUTH) {
                    $$35 = Mth.floor(p_115561_.getX() - (double)(($$31 + $$32) / 2.0F / 16.0F));
                }

                if ($$38 == Direction.EAST) {
                    $$37 = Mth.floor(p_115561_.getZ() + (double)(($$31 + $$32) / 2.0F / 16.0F));
                }

                int $$39 = LevelRenderer.getLightColor(p_115561_.level(), new BlockPos($$35, $$36, $$37));
                float $$40 = p_115564_.getU($$27 * (double)($$25 - $$29));
                float $$41 = p_115564_.getU($$27 * (double)($$25 - ($$29 + 1)));
                float $$42 = p_115564_.getV($$28 * (double)($$26 - $$30));
                float $$43 = p_115564_.getV($$28 * (double)($$26 - ($$30 + 1)));
                this.vertex($$8, $$9, p_115560_, $$31, $$34, $$41, $$42, -0.5F, 0, 0, -1, $$39);
                this.vertex($$8, $$9, p_115560_, $$32, $$34, $$40, $$42, -0.5F, 0, 0, -1, $$39);
                this.vertex($$8, $$9, p_115560_, $$32, $$33, $$40, $$43, -0.5F, 0, 0, -1, $$39);
                this.vertex($$8, $$9, p_115560_, $$31, $$33, $$41, $$43, -0.5F, 0, 0, -1, $$39);
                this.vertex($$8, $$9, p_115560_, $$31, $$33, $$14, $$15, 0.5F, 0, 0, 1, $$39);
                this.vertex($$8, $$9, p_115560_, $$32, $$33, $$13, $$15, 0.5F, 0, 0, 1, $$39);
                this.vertex($$8, $$9, p_115560_, $$32, $$34, $$13, $$16, 0.5F, 0, 0, 1, $$39);
                this.vertex($$8, $$9, p_115560_, $$31, $$34, $$14, $$16, 0.5F, 0, 0, 1, $$39);
                this.vertex($$8, $$9, p_115560_, $$31, $$33, $$17, $$19, -0.5F, 0, 1, 0, $$39);
                this.vertex($$8, $$9, p_115560_, $$32, $$33, $$18, $$19, -0.5F, 0, 1, 0, $$39);
                this.vertex($$8, $$9, p_115560_, $$32, $$33, $$18, $$20, 0.5F, 0, 1, 0, $$39);
                this.vertex($$8, $$9, p_115560_, $$31, $$33, $$17, $$20, 0.5F, 0, 1, 0, $$39);
                this.vertex($$8, $$9, p_115560_, $$31, $$34, $$17, $$19, 0.5F, 0, -1, 0, $$39);
                this.vertex($$8, $$9, p_115560_, $$32, $$34, $$18, $$19, 0.5F, 0, -1, 0, $$39);
                this.vertex($$8, $$9, p_115560_, $$32, $$34, $$18, $$20, -0.5F, 0, -1, 0, $$39);
                this.vertex($$8, $$9, p_115560_, $$31, $$34, $$17, $$20, -0.5F, 0, -1, 0, $$39);
                this.vertex($$8, $$9, p_115560_, $$31, $$33, $$22, $$23, 0.5F, -1, 0, 0, $$39);
                this.vertex($$8, $$9, p_115560_, $$31, $$34, $$22, $$24, 0.5F, -1, 0, 0, $$39);
                this.vertex($$8, $$9, p_115560_, $$31, $$34, $$21, $$24, -0.5F, -1, 0, 0, $$39);
                this.vertex($$8, $$9, p_115560_, $$31, $$33, $$21, $$23, -0.5F, -1, 0, 0, $$39);
                this.vertex($$8, $$9, p_115560_, $$32, $$33, $$22, $$23, -0.5F, 1, 0, 0, $$39);
                this.vertex($$8, $$9, p_115560_, $$32, $$34, $$22, $$24, -0.5F, 1, 0, 0, $$39);
                this.vertex($$8, $$9, p_115560_, $$32, $$34, $$21, $$24, 0.5F, 1, 0, 0, $$39);
                this.vertex($$8, $$9, p_115560_, $$32, $$33, $$21, $$23, 0.5F, 1, 0, 0, $$39);
            }
        }

    }

    private void vertex(Matrix4f p_253885_, Matrix3f p_253799_, VertexConsumer p_254114_, float p_254164_, float p_254459_, float p_254183_, float p_253615_, float p_254448_, int p_253660_, int p_254342_, int p_253757_, int p_254101_) {
        p_254114_.vertex(p_253885_, p_254164_, p_254459_, p_254448_).color(255, 255, 255, 255).uv(p_254183_, p_253615_).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(p_254101_).normal(p_253799_, (float)p_253660_, (float)p_254342_, (float)p_253757_).endVertex();
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Axis;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class CubeMap {
    private static final int SIDES = 6;
    private final ResourceLocation[] images = new ResourceLocation[6];

    public CubeMap(ResourceLocation p_108848_) {
        for(int $$1 = 0; $$1 < 6; ++$$1) {
            ResourceLocation[] var10000 = this.images;
            String var10003 = p_108848_.getPath();
            var10000[$$1] = p_108848_.withPath(var10003 + "_" + $$1 + ".png");
        }

    }

    public void render(Minecraft p_108850_, float p_108851_, float p_108852_, float p_108853_) {
        Tesselator $$4 = Tesselator.getInstance();
        BufferBuilder $$5 = $$4.getBuilder();
        Matrix4f $$6 = (new Matrix4f()).setPerspective(1.4835298F, (float)p_108850_.getWindow().getWidth() / (float)p_108850_.getWindow().getHeight(), 0.05F, 10.0F);
        RenderSystem.backupProjectionMatrix();
        RenderSystem.setProjectionMatrix($$6, VertexSorting.DISTANCE_TO_ORIGIN);
        PoseStack $$7 = RenderSystem.getModelViewStack();
        $$7.pushPose();
        $$7.setIdentity();
        $$7.mulPose(Axis.XP.rotationDegrees(180.0F));
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.enableBlend();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        int $$8 = true;

        for(int $$9 = 0; $$9 < 4; ++$$9) {
            $$7.pushPose();
            float $$10 = ((float)($$9 % 2) / 2.0F - 0.5F) / 256.0F;
            float $$11 = ((float)($$9 / 2) / 2.0F - 0.5F) / 256.0F;
            float $$12 = 0.0F;
            $$7.translate($$10, $$11, 0.0F);
            $$7.mulPose(Axis.XP.rotationDegrees(p_108851_));
            $$7.mulPose(Axis.YP.rotationDegrees(p_108852_));
            RenderSystem.applyModelViewMatrix();

            for(int $$13 = 0; $$13 < 6; ++$$13) {
                RenderSystem.setShaderTexture(0, this.images[$$13]);
                $$5.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
                int $$14 = Math.round(255.0F * p_108853_) / ($$9 + 1);
                if ($$13 == 0) {
                    $$5.vertex(-1.0, -1.0, 1.0).uv(0.0F, 0.0F).color(255, 255, 255, $$14).endVertex();
                    $$5.vertex(-1.0, 1.0, 1.0).uv(0.0F, 1.0F).color(255, 255, 255, $$14).endVertex();
                    $$5.vertex(1.0, 1.0, 1.0).uv(1.0F, 1.0F).color(255, 255, 255, $$14).endVertex();
                    $$5.vertex(1.0, -1.0, 1.0).uv(1.0F, 0.0F).color(255, 255, 255, $$14).endVertex();
                }

                if ($$13 == 1) {
                    $$5.vertex(1.0, -1.0, 1.0).uv(0.0F, 0.0F).color(255, 255, 255, $$14).endVertex();
                    $$5.vertex(1.0, 1.0, 1.0).uv(0.0F, 1.0F).color(255, 255, 255, $$14).endVertex();
                    $$5.vertex(1.0, 1.0, -1.0).uv(1.0F, 1.0F).color(255, 255, 255, $$14).endVertex();
                    $$5.vertex(1.0, -1.0, -1.0).uv(1.0F, 0.0F).color(255, 255, 255, $$14).endVertex();
                }

                if ($$13 == 2) {
                    $$5.vertex(1.0, -1.0, -1.0).uv(0.0F, 0.0F).color(255, 255, 255, $$14).endVertex();
                    $$5.vertex(1.0, 1.0, -1.0).uv(0.0F, 1.0F).color(255, 255, 255, $$14).endVertex();
                    $$5.vertex(-1.0, 1.0, -1.0).uv(1.0F, 1.0F).color(255, 255, 255, $$14).endVertex();
                    $$5.vertex(-1.0, -1.0, -1.0).uv(1.0F, 0.0F).color(255, 255, 255, $$14).endVertex();
                }

                if ($$13 == 3) {
                    $$5.vertex(-1.0, -1.0, -1.0).uv(0.0F, 0.0F).color(255, 255, 255, $$14).endVertex();
                    $$5.vertex(-1.0, 1.0, -1.0).uv(0.0F, 1.0F).color(255, 255, 255, $$14).endVertex();
                    $$5.vertex(-1.0, 1.0, 1.0).uv(1.0F, 1.0F).color(255, 255, 255, $$14).endVertex();
                    $$5.vertex(-1.0, -1.0, 1.0).uv(1.0F, 0.0F).color(255, 255, 255, $$14).endVertex();
                }

                if ($$13 == 4) {
                    $$5.vertex(-1.0, -1.0, -1.0).uv(0.0F, 0.0F).color(255, 255, 255, $$14).endVertex();
                    $$5.vertex(-1.0, -1.0, 1.0).uv(0.0F, 1.0F).color(255, 255, 255, $$14).endVertex();
                    $$5.vertex(1.0, -1.0, 1.0).uv(1.0F, 1.0F).color(255, 255, 255, $$14).endVertex();
                    $$5.vertex(1.0, -1.0, -1.0).uv(1.0F, 0.0F).color(255, 255, 255, $$14).endVertex();
                }

                if ($$13 == 5) {
                    $$5.vertex(-1.0, 1.0, 1.0).uv(0.0F, 0.0F).color(255, 255, 255, $$14).endVertex();
                    $$5.vertex(-1.0, 1.0, -1.0).uv(0.0F, 1.0F).color(255, 255, 255, $$14).endVertex();
                    $$5.vertex(1.0, 1.0, -1.0).uv(1.0F, 1.0F).color(255, 255, 255, $$14).endVertex();
                    $$5.vertex(1.0, 1.0, 1.0).uv(1.0F, 0.0F).color(255, 255, 255, $$14).endVertex();
                }

                $$4.end();
            }

            $$7.popPose();
            RenderSystem.applyModelViewMatrix();
            RenderSystem.colorMask(true, true, true, false);
        }

        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.restoreProjectionMatrix();
        $$7.popPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
    }

    public CompletableFuture<Void> preload(TextureManager p_108855_, Executor p_108856_) {
        CompletableFuture<?>[] $$2 = new CompletableFuture[6];

        for(int $$3 = 0; $$3 < $$2.length; ++$$3) {
            $$2[$$3] = p_108855_.preload(this.images[$$3], p_108856_);
        }

        return CompletableFuture.allOf($$2);
    }
}

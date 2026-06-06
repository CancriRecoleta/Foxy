//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.mojang.math.Transformation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Display.TextDisplay;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

@OnlyIn(Dist.CLIENT)
public abstract class DisplayRenderer<T extends Display, S> extends EntityRenderer<T> {
    private final EntityRenderDispatcher entityRenderDispatcher;

    protected DisplayRenderer(EntityRendererProvider.Context p_270168_) {
        super(p_270168_);
        this.entityRenderDispatcher = p_270168_.getEntityRenderDispatcher();
    }

    public ResourceLocation getTextureLocation(T p_270675_) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    public void render(T p_270405_, float p_270225_, float p_270279_, PoseStack p_270728_, MultiBufferSource p_270209_, int p_270298_) {
        Display.RenderState $$6 = p_270405_.renderState();
        if ($$6 != null) {
            S $$7 = this.getSubState(p_270405_);
            if ($$7 != null) {
                float $$8 = p_270405_.calculateInterpolationProgress(p_270279_);
                this.shadowRadius = $$6.shadowRadius().get($$8);
                this.shadowStrength = $$6.shadowStrength().get($$8);
                int $$9 = $$6.brightnessOverride();
                int $$10 = $$9 != -1 ? $$9 : p_270298_;
                super.render(p_270405_, p_270225_, p_270279_, p_270728_, p_270209_, $$10);
                p_270728_.pushPose();
                p_270728_.mulPose(this.calculateOrientation($$6, p_270405_));
                Transformation $$11 = (Transformation)$$6.transformation().get($$8);
                p_270728_.mulPoseMatrix($$11.getMatrix());
                p_270728_.last().normal().rotate($$11.getLeftRotation()).rotate($$11.getRightRotation());
                this.renderInner(p_270405_, $$7, p_270728_, p_270209_, $$10, $$8);
                p_270728_.popPose();
            }
        }
    }

    private Quaternionf calculateOrientation(Display.RenderState p_277846_, T p_271013_) {
        Camera $$2 = this.entityRenderDispatcher.camera;
        Quaternionf var10000;
        switch (p_277846_.billboardConstraints()) {
            case FIXED -> var10000 = p_271013_.orientation();
            case HORIZONTAL -> var10000 = (new Quaternionf()).rotationYXZ(-0.017453292F * p_271013_.getYRot(), -0.017453292F * $$2.getXRot(), 0.0F);
            case VERTICAL -> var10000 = (new Quaternionf()).rotationYXZ(3.1415927F - 0.017453292F * $$2.getYRot(), 0.017453292F * p_271013_.getXRot(), 0.0F);
            case CENTER -> var10000 = (new Quaternionf()).rotationYXZ(3.1415927F - 0.017453292F * $$2.getYRot(), -0.017453292F * $$2.getXRot(), 0.0F);
            default -> throw new IncompatibleClassChangeError();
        }

        return var10000;
    }

    @Nullable
    protected abstract S getSubState(T var1);

    protected abstract void renderInner(T var1, S var2, PoseStack var3, MultiBufferSource var4, int var5, float var6);

    @OnlyIn(Dist.CLIENT)
    public static class TextDisplayRenderer extends DisplayRenderer<Display.TextDisplay, Display.TextDisplay.TextRenderState> {
        private final Font font;

        protected TextDisplayRenderer(EntityRendererProvider.Context p_271012_) {
            super(p_271012_);
            this.font = p_271012_.getFont();
        }

        private Display.TextDisplay.CachedInfo splitLines(Component p_270823_, int p_270893_) {
            List<FormattedCharSequence> $$2 = this.font.split(p_270823_, p_270893_);
            List<Display.TextDisplay.CachedLine> $$3 = new ArrayList($$2.size());
            int $$4 = 0;
            Iterator var6 = $$2.iterator();

            while(var6.hasNext()) {
                FormattedCharSequence $$5 = (FormattedCharSequence)var6.next();
                int $$6 = this.font.width($$5);
                $$4 = Math.max($$4, $$6);
                $$3.add(new Display.TextDisplay.CachedLine($$5, $$6));
            }

            return new Display.TextDisplay.CachedInfo($$3, $$4);
        }

        @Nullable
        protected Display.TextDisplay.TextRenderState getSubState(Display.TextDisplay p_277947_) {
            return p_277947_.textRenderState();
        }

        public void renderInner(Display.TextDisplay p_277522_, Display.TextDisplay.TextRenderState p_277620_, PoseStack p_277536_, MultiBufferSource p_277845_, int p_278046_, float p_277769_) {
            byte $$6 = p_277620_.flags();
            boolean $$7 = ($$6 & 2) != 0;
            boolean $$8 = ($$6 & 4) != 0;
            boolean $$9 = ($$6 & 1) != 0;
            Display.TextDisplay.Align $$10 = TextDisplay.getAlign($$6);
            byte $$11 = (byte)p_277620_.textOpacity().get(p_277769_);
            int $$14;
            float $$15;
            if ($$8) {
                $$15 = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
                $$14 = (int)($$15 * 255.0F) << 24;
            } else {
                $$14 = p_277620_.backgroundColor().get(p_277769_);
            }

            $$15 = 0.0F;
            Matrix4f $$16 = p_277536_.last().pose();
            $$16.rotate(3.1415927F, 0.0F, 1.0F, 0.0F);
            $$16.scale(-0.025F, -0.025F, -0.025F);
            Display.TextDisplay.CachedInfo $$17 = p_277522_.cacheDisplay(this::splitLines);
            Objects.requireNonNull(this.font);
            int $$18 = 9 + 1;
            int $$19 = $$17.width();
            int $$20 = $$17.lines().size() * $$18;
            $$16.translate(1.0F - (float)$$19 / 2.0F, (float)(-$$20), 0.0F);
            if ($$14 != 0) {
                VertexConsumer $$21 = p_277845_.getBuffer($$7 ? RenderType.textBackgroundSeeThrough() : RenderType.textBackground());
                $$21.vertex($$16, -1.0F, -1.0F, 0.0F).color($$14).uv2(p_278046_).endVertex();
                $$21.vertex($$16, -1.0F, (float)$$20, 0.0F).color($$14).uv2(p_278046_).endVertex();
                $$21.vertex($$16, (float)$$19, (float)$$20, 0.0F).color($$14).uv2(p_278046_).endVertex();
                $$21.vertex($$16, (float)$$19, -1.0F, 0.0F).color($$14).uv2(p_278046_).endVertex();
            }

            for(Iterator var23 = $$17.lines().iterator(); var23.hasNext(); $$15 += (float)$$18) {
                Display.TextDisplay.CachedLine $$22 = (Display.TextDisplay.CachedLine)var23.next();
                float var10000;
                switch ($$10) {
                    case LEFT -> var10000 = 0.0F;
                    case RIGHT -> var10000 = (float)($$19 - $$22.width());
                    case CENTER -> var10000 = (float)$$19 / 2.0F - (float)$$22.width() / 2.0F;
                    default -> throw new IncompatibleClassChangeError();
                }

                float $$23 = var10000;
                this.font.drawInBatch((FormattedCharSequence)$$22.contents(), $$23, $$15, $$11 << 24 | 16777215, $$9, $$16, p_277845_, $$7 ? DisplayMode.SEE_THROUGH : DisplayMode.POLYGON_OFFSET, 0, p_278046_);
            }

        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class ItemDisplayRenderer extends DisplayRenderer<Display.ItemDisplay, Display.ItemDisplay.ItemRenderState> {
        private final ItemRenderer itemRenderer;

        protected ItemDisplayRenderer(EntityRendererProvider.Context p_270110_) {
            super(p_270110_);
            this.itemRenderer = p_270110_.getItemRenderer();
        }

        @Nullable
        protected Display.ItemDisplay.ItemRenderState getSubState(Display.ItemDisplay p_277464_) {
            return p_277464_.itemRenderState();
        }

        public void renderInner(Display.ItemDisplay p_277863_, Display.ItemDisplay.ItemRenderState p_277481_, PoseStack p_277889_, MultiBufferSource p_277509_, int p_277861_, float p_277670_) {
            p_277889_.mulPose(Axis.YP.rotation(3.1415927F));
            this.itemRenderer.renderStatic(p_277481_.itemStack(), p_277481_.itemTransform(), p_277861_, OverlayTexture.NO_OVERLAY, p_277889_, p_277509_, p_277863_.level(), p_277863_.getId());
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class BlockDisplayRenderer extends DisplayRenderer<Display.BlockDisplay, Display.BlockDisplay.BlockRenderState> {
        private final BlockRenderDispatcher blockRenderer;

        protected BlockDisplayRenderer(EntityRendererProvider.Context p_270283_) {
            super(p_270283_);
            this.blockRenderer = p_270283_.getBlockRenderDispatcher();
        }

        @Nullable
        protected Display.BlockDisplay.BlockRenderState getSubState(Display.BlockDisplay p_277721_) {
            return p_277721_.blockRenderState();
        }

        public void renderInner(Display.BlockDisplay p_277939_, Display.BlockDisplay.BlockRenderState p_277885_, PoseStack p_277831_, MultiBufferSource p_277554_, int p_278071_, float p_277847_) {
            this.blockRenderer.renderSingleBlock(p_277885_.blockState(), p_277831_, p_277554_, p_278071_, OverlayTexture.NO_OVERLAY);
        }
    }
}

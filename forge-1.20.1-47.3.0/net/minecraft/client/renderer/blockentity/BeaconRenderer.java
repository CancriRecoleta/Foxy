//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.List;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class BeaconRenderer implements BlockEntityRenderer<BeaconBlockEntity> {
    public static final ResourceLocation BEAM_LOCATION = new ResourceLocation("textures/entity/beacon_beam.png");
    public static final int MAX_RENDER_Y = 1024;

    public BeaconRenderer(BlockEntityRendererProvider.Context p_173529_) {
    }

    public void render(BeaconBlockEntity p_112140_, float p_112141_, PoseStack p_112142_, MultiBufferSource p_112143_, int p_112144_, int p_112145_) {
        long $$6 = p_112140_.getLevel().getGameTime();
        List<BeaconBlockEntity.BeaconBeamSection> $$7 = p_112140_.getBeamSections();
        int $$8 = 0;

        for(int $$9 = 0; $$9 < $$7.size(); ++$$9) {
            BeaconBlockEntity.BeaconBeamSection $$10 = (BeaconBlockEntity.BeaconBeamSection)$$7.get($$9);
            renderBeaconBeam(p_112142_, p_112143_, p_112141_, $$6, $$8, $$9 == $$7.size() - 1 ? 1024 : $$10.getHeight(), $$10.getColor());
            $$8 += $$10.getHeight();
        }

    }

    private static void renderBeaconBeam(PoseStack p_112177_, MultiBufferSource p_112178_, float p_112179_, long p_112180_, int p_112181_, int p_112182_, float[] p_112183_) {
        renderBeaconBeam(p_112177_, p_112178_, BEAM_LOCATION, p_112179_, 1.0F, p_112180_, p_112181_, p_112182_, p_112183_, 0.2F, 0.25F);
    }

    public static void renderBeaconBeam(PoseStack p_112185_, MultiBufferSource p_112186_, ResourceLocation p_112187_, float p_112188_, float p_112189_, long p_112190_, int p_112191_, int p_112192_, float[] p_112193_, float p_112194_, float p_112195_) {
        int $$11 = p_112191_ + p_112192_;
        p_112185_.pushPose();
        p_112185_.translate(0.5, 0.0, 0.5);
        float $$12 = (float)Math.floorMod(p_112190_, 40) + p_112188_;
        float $$13 = p_112192_ < 0 ? $$12 : -$$12;
        float $$14 = Mth.frac($$13 * 0.2F - (float)Mth.floor($$13 * 0.1F));
        float $$15 = p_112193_[0];
        float $$16 = p_112193_[1];
        float $$17 = p_112193_[2];
        p_112185_.pushPose();
        p_112185_.mulPose(Axis.YP.rotationDegrees($$12 * 2.25F - 45.0F));
        float $$30 = 0.0F;
        float $$31 = p_112194_;
        float $$32 = p_112194_;
        float $$33 = 0.0F;
        float $$34 = -p_112194_;
        float $$35 = 0.0F;
        float $$36 = 0.0F;
        float $$37 = -p_112194_;
        float $$38 = 0.0F;
        float $$39 = 1.0F;
        float $$40 = -1.0F + $$14;
        float $$41 = (float)p_112192_ * p_112189_ * (0.5F / p_112194_) + $$40;
        renderPart(p_112185_, p_112186_.getBuffer(RenderType.beaconBeam(p_112187_, false)), $$15, $$16, $$17, 1.0F, p_112191_, $$11, 0.0F, $$31, $$32, 0.0F, $$34, 0.0F, 0.0F, $$37, 0.0F, 1.0F, $$41, $$40);
        p_112185_.popPose();
        $$30 = -p_112195_;
        $$31 = -p_112195_;
        $$32 = p_112195_;
        $$33 = -p_112195_;
        $$34 = -p_112195_;
        $$35 = p_112195_;
        $$36 = p_112195_;
        $$37 = p_112195_;
        $$38 = 0.0F;
        $$39 = 1.0F;
        $$40 = -1.0F + $$14;
        $$41 = (float)p_112192_ * p_112189_ + $$40;
        renderPart(p_112185_, p_112186_.getBuffer(RenderType.beaconBeam(p_112187_, true)), $$15, $$16, $$17, 0.125F, p_112191_, $$11, $$30, $$31, $$32, $$33, $$34, $$35, $$36, $$37, 0.0F, 1.0F, $$41, $$40);
        p_112185_.popPose();
    }

    private static void renderPart(PoseStack p_112156_, VertexConsumer p_112157_, float p_112158_, float p_112159_, float p_112160_, float p_112161_, int p_112162_, int p_112163_, float p_112164_, float p_112165_, float p_112166_, float p_112167_, float p_112168_, float p_112169_, float p_112170_, float p_112171_, float p_112172_, float p_112173_, float p_112174_, float p_112175_) {
        PoseStack.Pose $$20 = p_112156_.last();
        Matrix4f $$21 = $$20.pose();
        Matrix3f $$22 = $$20.normal();
        renderQuad($$21, $$22, p_112157_, p_112158_, p_112159_, p_112160_, p_112161_, p_112162_, p_112163_, p_112164_, p_112165_, p_112166_, p_112167_, p_112172_, p_112173_, p_112174_, p_112175_);
        renderQuad($$21, $$22, p_112157_, p_112158_, p_112159_, p_112160_, p_112161_, p_112162_, p_112163_, p_112170_, p_112171_, p_112168_, p_112169_, p_112172_, p_112173_, p_112174_, p_112175_);
        renderQuad($$21, $$22, p_112157_, p_112158_, p_112159_, p_112160_, p_112161_, p_112162_, p_112163_, p_112166_, p_112167_, p_112170_, p_112171_, p_112172_, p_112173_, p_112174_, p_112175_);
        renderQuad($$21, $$22, p_112157_, p_112158_, p_112159_, p_112160_, p_112161_, p_112162_, p_112163_, p_112168_, p_112169_, p_112164_, p_112165_, p_112172_, p_112173_, p_112174_, p_112175_);
    }

    private static void renderQuad(Matrix4f p_253960_, Matrix3f p_254005_, VertexConsumer p_112122_, float p_112123_, float p_112124_, float p_112125_, float p_112126_, int p_112127_, int p_112128_, float p_112129_, float p_112130_, float p_112131_, float p_112132_, float p_112133_, float p_112134_, float p_112135_, float p_112136_) {
        addVertex(p_253960_, p_254005_, p_112122_, p_112123_, p_112124_, p_112125_, p_112126_, p_112128_, p_112129_, p_112130_, p_112134_, p_112135_);
        addVertex(p_253960_, p_254005_, p_112122_, p_112123_, p_112124_, p_112125_, p_112126_, p_112127_, p_112129_, p_112130_, p_112134_, p_112136_);
        addVertex(p_253960_, p_254005_, p_112122_, p_112123_, p_112124_, p_112125_, p_112126_, p_112127_, p_112131_, p_112132_, p_112133_, p_112136_);
        addVertex(p_253960_, p_254005_, p_112122_, p_112123_, p_112124_, p_112125_, p_112126_, p_112128_, p_112131_, p_112132_, p_112133_, p_112135_);
    }

    private static void addVertex(Matrix4f p_253955_, Matrix3f p_253713_, VertexConsumer p_253894_, float p_253871_, float p_253841_, float p_254568_, float p_254361_, int p_254357_, float p_254451_, float p_254240_, float p_254117_, float p_253698_) {
        p_253894_.vertex(p_253955_, p_254451_, (float)p_254357_, p_254240_).color(p_253871_, p_253841_, p_254568_, p_254361_).uv(p_254117_, p_253698_).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(p_253713_, 0.0F, 1.0F, 0.0F).endVertex();
    }

    public boolean shouldRenderOffScreen(BeaconBlockEntity p_112138_) {
        return true;
    }

    public int getViewDistance() {
        return 256;
    }

    public boolean shouldRender(BeaconBlockEntity p_173531_, Vec3 p_173532_) {
        return Vec3.atCenterOf(p_173531_.getBlockPos()).multiply(1.0, 0.0, 1.0).closerThan(p_173532_.multiply(1.0, 0.0, 1.0), (double)this.getViewDistance());
    }
}

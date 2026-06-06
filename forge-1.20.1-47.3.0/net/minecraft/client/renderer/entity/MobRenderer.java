//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public abstract class MobRenderer<T extends Mob, M extends EntityModel<T>> extends LivingEntityRenderer<T, M> {
    public static final int LEASH_RENDER_STEPS = 24;

    public MobRenderer(EntityRendererProvider.Context p_174304_, M p_174305_, float p_174306_) {
        super(p_174304_, p_174305_, p_174306_);
    }

    protected boolean shouldShowName(T p_115506_) {
        return super.shouldShowName((LivingEntity)p_115506_) && (p_115506_.shouldShowName() || p_115506_.hasCustomName() && p_115506_ == this.entityRenderDispatcher.crosshairPickEntity);
    }

    public boolean shouldRender(T p_115468_, Frustum p_115469_, double p_115470_, double p_115471_, double p_115472_) {
        if (super.shouldRender(p_115468_, p_115469_, p_115470_, p_115471_, p_115472_)) {
            return true;
        } else {
            Entity $$5 = p_115468_.getLeashHolder();
            return $$5 != null ? p_115469_.isVisible($$5.getBoundingBoxForCulling()) : false;
        }
    }

    public void render(T p_115455_, float p_115456_, float p_115457_, PoseStack p_115458_, MultiBufferSource p_115459_, int p_115460_) {
        super.render((LivingEntity)p_115455_, p_115456_, p_115457_, p_115458_, p_115459_, p_115460_);
        Entity $$6 = p_115455_.getLeashHolder();
        if ($$6 != null) {
            this.renderLeash(p_115455_, p_115457_, p_115458_, p_115459_, $$6);
        }
    }

    private <E extends Entity> void renderLeash(T p_115462_, float p_115463_, PoseStack p_115464_, MultiBufferSource p_115465_, E p_115466_) {
        p_115464_.pushPose();
        Vec3 $$5 = p_115466_.getRopeHoldPosition(p_115463_);
        double $$6 = (double)(Mth.lerp(p_115463_, p_115462_.yBodyRotO, p_115462_.yBodyRot) * 0.017453292F) + 1.5707963267948966;
        Vec3 $$7 = p_115462_.getLeashOffset(p_115463_);
        double $$8 = Math.cos($$6) * $$7.z + Math.sin($$6) * $$7.x;
        double $$9 = Math.sin($$6) * $$7.z - Math.cos($$6) * $$7.x;
        double $$10 = Mth.lerp((double)p_115463_, p_115462_.xo, p_115462_.getX()) + $$8;
        double $$11 = Mth.lerp((double)p_115463_, p_115462_.yo, p_115462_.getY()) + $$7.y;
        double $$12 = Mth.lerp((double)p_115463_, p_115462_.zo, p_115462_.getZ()) + $$9;
        p_115464_.translate($$8, $$7.y, $$9);
        float $$13 = (float)($$5.x - $$10);
        float $$14 = (float)($$5.y - $$11);
        float $$15 = (float)($$5.z - $$12);
        float $$16 = 0.025F;
        VertexConsumer $$17 = p_115465_.getBuffer(RenderType.leash());
        Matrix4f $$18 = p_115464_.last().pose();
        float $$19 = Mth.invSqrt($$13 * $$13 + $$15 * $$15) * 0.025F / 2.0F;
        float $$20 = $$15 * $$19;
        float $$21 = $$13 * $$19;
        BlockPos $$22 = BlockPos.containing(p_115462_.getEyePosition(p_115463_));
        BlockPos $$23 = BlockPos.containing(p_115466_.getEyePosition(p_115463_));
        int $$24 = this.getBlockLightLevel(p_115462_, $$22);
        int $$25 = this.entityRenderDispatcher.getRenderer(p_115466_).getBlockLightLevel(p_115466_, $$23);
        int $$26 = p_115462_.level().getBrightness(LightLayer.SKY, $$22);
        int $$27 = p_115462_.level().getBrightness(LightLayer.SKY, $$23);

        int $$29;
        for($$29 = 0; $$29 <= 24; ++$$29) {
            addVertexPair($$17, $$18, $$13, $$14, $$15, $$24, $$25, $$26, $$27, 0.025F, 0.025F, $$20, $$21, $$29, false);
        }

        for($$29 = 24; $$29 >= 0; --$$29) {
            addVertexPair($$17, $$18, $$13, $$14, $$15, $$24, $$25, $$26, $$27, 0.025F, 0.0F, $$20, $$21, $$29, true);
        }

        p_115464_.popPose();
    }

    private static void addVertexPair(VertexConsumer p_174308_, Matrix4f p_254405_, float p_174310_, float p_174311_, float p_174312_, int p_174313_, int p_174314_, int p_174315_, int p_174316_, float p_174317_, float p_174318_, float p_174319_, float p_174320_, int p_174321_, boolean p_174322_) {
        float $$15 = (float)p_174321_ / 24.0F;
        int $$16 = (int)Mth.lerp($$15, (float)p_174313_, (float)p_174314_);
        int $$17 = (int)Mth.lerp($$15, (float)p_174315_, (float)p_174316_);
        int $$18 = LightTexture.pack($$16, $$17);
        float $$19 = p_174321_ % 2 == (p_174322_ ? 1 : 0) ? 0.7F : 1.0F;
        float $$20 = 0.5F * $$19;
        float $$21 = 0.4F * $$19;
        float $$22 = 0.3F * $$19;
        float $$23 = p_174310_ * $$15;
        float $$24 = p_174311_ > 0.0F ? p_174311_ * $$15 * $$15 : p_174311_ - p_174311_ * (1.0F - $$15) * (1.0F - $$15);
        float $$25 = p_174312_ * $$15;
        p_174308_.vertex(p_254405_, $$23 - p_174319_, $$24 + p_174318_, $$25 + p_174320_).color($$20, $$21, $$22, 1.0F).uv2($$18).endVertex();
        p_174308_.vertex(p_254405_, $$23 + p_174319_, $$24 + p_174317_ - p_174318_, $$25 - p_174320_).color($$20, $$21, $$22, 1.0F).uv2($$18).endVertex();
    }
}

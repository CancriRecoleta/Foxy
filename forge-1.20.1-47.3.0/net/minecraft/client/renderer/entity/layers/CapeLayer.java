//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CapeLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    public CapeLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> p_116602_) {
        super(p_116602_);
    }

    public void render(PoseStack p_116615_, MultiBufferSource p_116616_, int p_116617_, AbstractClientPlayer p_116618_, float p_116619_, float p_116620_, float p_116621_, float p_116622_, float p_116623_, float p_116624_) {
        if (p_116618_.isCapeLoaded() && !p_116618_.isInvisible() && p_116618_.isModelPartShown(PlayerModelPart.CAPE) && p_116618_.getCloakTextureLocation() != null) {
            ItemStack $$10 = p_116618_.getItemBySlot(EquipmentSlot.CHEST);
            if (!$$10.is(Items.ELYTRA)) {
                p_116615_.pushPose();
                p_116615_.translate(0.0F, 0.0F, 0.125F);
                double $$11 = Mth.lerp((double)p_116621_, p_116618_.xCloakO, p_116618_.xCloak) - Mth.lerp((double)p_116621_, p_116618_.xo, p_116618_.getX());
                double $$12 = Mth.lerp((double)p_116621_, p_116618_.yCloakO, p_116618_.yCloak) - Mth.lerp((double)p_116621_, p_116618_.yo, p_116618_.getY());
                double $$13 = Mth.lerp((double)p_116621_, p_116618_.zCloakO, p_116618_.zCloak) - Mth.lerp((double)p_116621_, p_116618_.zo, p_116618_.getZ());
                float $$14 = Mth.rotLerp(p_116621_, p_116618_.yBodyRotO, p_116618_.yBodyRot);
                double $$15 = (double)Mth.sin($$14 * 0.017453292F);
                double $$16 = (double)(-Mth.cos($$14 * 0.017453292F));
                float $$17 = (float)$$12 * 10.0F;
                $$17 = Mth.clamp($$17, -6.0F, 32.0F);
                float $$18 = (float)($$11 * $$15 + $$13 * $$16) * 100.0F;
                $$18 = Mth.clamp($$18, 0.0F, 150.0F);
                float $$19 = (float)($$11 * $$16 - $$13 * $$15) * 100.0F;
                $$19 = Mth.clamp($$19, -20.0F, 20.0F);
                if ($$18 < 0.0F) {
                    $$18 = 0.0F;
                }

                float $$20 = Mth.lerp(p_116621_, p_116618_.oBob, p_116618_.bob);
                $$17 += Mth.sin(Mth.lerp(p_116621_, p_116618_.walkDistO, p_116618_.walkDist) * 6.0F) * 32.0F * $$20;
                if (p_116618_.isCrouching()) {
                    $$17 += 25.0F;
                }

                p_116615_.mulPose(Axis.XP.rotationDegrees(6.0F + $$18 / 2.0F + $$17));
                p_116615_.mulPose(Axis.ZP.rotationDegrees($$19 / 2.0F));
                p_116615_.mulPose(Axis.YP.rotationDegrees(180.0F - $$19 / 2.0F));
                VertexConsumer $$21 = p_116616_.getBuffer(RenderType.entitySolid(p_116618_.getCloakTextureLocation()));
                ((PlayerModel)this.getParentModel()).renderCloak(p_116615_, $$21, p_116617_, OverlayTexture.NO_OVERLAY);
                p_116615_.popPose();
            }
        }
    }
}

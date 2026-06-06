//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.model.PandaModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.PandaHoldsItemLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.entity.animal.Panda.Gene;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PandaRenderer extends MobRenderer<Panda, PandaModel<Panda>> {
    private static final Map<Panda.Gene, ResourceLocation> TEXTURES = (Map)Util.make(Maps.newEnumMap(Panda.Gene.class), (p_115647_) -> {
        p_115647_.put(Gene.NORMAL, new ResourceLocation("textures/entity/panda/panda.png"));
        p_115647_.put(Gene.LAZY, new ResourceLocation("textures/entity/panda/lazy_panda.png"));
        p_115647_.put(Gene.WORRIED, new ResourceLocation("textures/entity/panda/worried_panda.png"));
        p_115647_.put(Gene.PLAYFUL, new ResourceLocation("textures/entity/panda/playful_panda.png"));
        p_115647_.put(Gene.BROWN, new ResourceLocation("textures/entity/panda/brown_panda.png"));
        p_115647_.put(Gene.WEAK, new ResourceLocation("textures/entity/panda/weak_panda.png"));
        p_115647_.put(Gene.AGGRESSIVE, new ResourceLocation("textures/entity/panda/aggressive_panda.png"));
    });

    public PandaRenderer(EntityRendererProvider.Context p_174334_) {
        super(p_174334_, new PandaModel(p_174334_.bakeLayer(ModelLayers.PANDA)), 0.9F);
        this.addLayer(new PandaHoldsItemLayer(this, p_174334_.getItemInHandRenderer()));
    }

    public ResourceLocation getTextureLocation(Panda p_115639_) {
        return (ResourceLocation)TEXTURES.getOrDefault(p_115639_.getVariant(), (ResourceLocation)TEXTURES.get(Gene.NORMAL));
    }

    protected void setupRotations(Panda p_115641_, PoseStack p_115642_, float p_115643_, float p_115644_, float p_115645_) {
        super.setupRotations(p_115641_, p_115642_, p_115643_, p_115644_, p_115645_);
        float $$27;
        if (p_115641_.rollCounter > 0) {
            int $$5 = p_115641_.rollCounter;
            int $$6 = $$5 + 1;
            $$27 = 7.0F;
            float $$8 = p_115641_.isBaby() ? 0.3F : 0.8F;
            float $$23;
            float $$20;
            float $$21;
            if ($$5 < 8) {
                $$20 = (float)(90 * $$5) / 7.0F;
                $$21 = (float)(90 * $$6) / 7.0F;
                $$23 = this.getAngle($$20, $$21, $$6, p_115645_, 8.0F);
                p_115642_.translate(0.0F, ($$8 + 0.2F) * ($$23 / 90.0F), 0.0F);
                p_115642_.mulPose(Axis.XP.rotationDegrees(-$$23));
            } else {
                float $$22;
                if ($$5 < 16) {
                    $$20 = ((float)$$5 - 8.0F) / 7.0F;
                    $$21 = 90.0F + 90.0F * $$20;
                    $$22 = 90.0F + 90.0F * ((float)$$6 - 8.0F) / 7.0F;
                    $$23 = this.getAngle($$21, $$22, $$6, p_115645_, 16.0F);
                    p_115642_.translate(0.0F, $$8 + 0.2F + ($$8 - 0.2F) * ($$23 - 90.0F) / 90.0F, 0.0F);
                    p_115642_.mulPose(Axis.XP.rotationDegrees(-$$23));
                } else if ((float)$$5 < 24.0F) {
                    $$20 = ((float)$$5 - 16.0F) / 7.0F;
                    $$21 = 180.0F + 90.0F * $$20;
                    $$22 = 180.0F + 90.0F * ((float)$$6 - 16.0F) / 7.0F;
                    $$23 = this.getAngle($$21, $$22, $$6, p_115645_, 24.0F);
                    p_115642_.translate(0.0F, $$8 + $$8 * (270.0F - $$23) / 90.0F, 0.0F);
                    p_115642_.mulPose(Axis.XP.rotationDegrees(-$$23));
                } else if ($$5 < 32) {
                    $$20 = ((float)$$5 - 24.0F) / 7.0F;
                    $$21 = 270.0F + 90.0F * $$20;
                    $$22 = 270.0F + 90.0F * ((float)$$6 - 24.0F) / 7.0F;
                    $$23 = this.getAngle($$21, $$22, $$6, p_115645_, 32.0F);
                    p_115642_.translate(0.0F, $$8 * ((360.0F - $$23) / 90.0F), 0.0F);
                    p_115642_.mulPose(Axis.XP.rotationDegrees(-$$23));
                }
            }
        }

        float $$24 = p_115641_.getSitAmount(p_115645_);
        float $$26;
        if ($$24 > 0.0F) {
            p_115642_.translate(0.0F, 0.8F * $$24, 0.0F);
            p_115642_.mulPose(Axis.XP.rotationDegrees(Mth.lerp($$24, p_115641_.getXRot(), p_115641_.getXRot() + 90.0F)));
            p_115642_.translate(0.0F, -1.0F * $$24, 0.0F);
            if (p_115641_.isScared()) {
                $$26 = (float)(Math.cos((double)p_115641_.tickCount * 1.25) * Math.PI * 0.05000000074505806);
                p_115642_.mulPose(Axis.YP.rotationDegrees($$26));
                if (p_115641_.isBaby()) {
                    p_115642_.translate(0.0F, 0.8F, 0.55F);
                }
            }
        }

        $$26 = p_115641_.getLieOnBackAmount(p_115645_);
        if ($$26 > 0.0F) {
            $$27 = p_115641_.isBaby() ? 0.5F : 1.3F;
            p_115642_.translate(0.0F, $$27 * $$26, 0.0F);
            p_115642_.mulPose(Axis.XP.rotationDegrees(Mth.lerp($$26, p_115641_.getXRot(), p_115641_.getXRot() + 180.0F)));
        }

    }

    private float getAngle(float p_115625_, float p_115626_, int p_115627_, float p_115628_, float p_115629_) {
        return (float)p_115627_ < p_115629_ ? Mth.lerp(p_115628_, p_115625_, p_115626_) : p_115625_;
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import javax.annotation.Nullable;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class EnderDragonRenderer extends EntityRenderer<EnderDragon> {
    public static final ResourceLocation CRYSTAL_BEAM_LOCATION = new ResourceLocation("textures/entity/end_crystal/end_crystal_beam.png");
    private static final ResourceLocation DRAGON_EXPLODING_LOCATION = new ResourceLocation("textures/entity/enderdragon/dragon_exploding.png");
    private static final ResourceLocation DRAGON_LOCATION = new ResourceLocation("textures/entity/enderdragon/dragon.png");
    private static final ResourceLocation DRAGON_EYES_LOCATION = new ResourceLocation("textures/entity/enderdragon/dragon_eyes.png");
    private static final RenderType RENDER_TYPE;
    private static final RenderType DECAL;
    private static final RenderType EYES;
    private static final RenderType BEAM;
    private static final float HALF_SQRT_3;
    private final DragonModel model;

    public EnderDragonRenderer(EntityRendererProvider.Context p_173973_) {
        super(p_173973_);
        this.shadowRadius = 0.5F;
        this.model = new DragonModel(p_173973_.bakeLayer(ModelLayers.ENDER_DRAGON));
    }

    public void render(EnderDragon p_114208_, float p_114209_, float p_114210_, PoseStack p_114211_, MultiBufferSource p_114212_, int p_114213_) {
        p_114211_.pushPose();
        float $$6 = (float)p_114208_.getLatencyPos(7, p_114210_)[0];
        float $$7 = (float)(p_114208_.getLatencyPos(5, p_114210_)[1] - p_114208_.getLatencyPos(10, p_114210_)[1]);
        p_114211_.mulPose(Axis.YP.rotationDegrees(-$$6));
        p_114211_.mulPose(Axis.XP.rotationDegrees($$7 * 10.0F));
        p_114211_.translate(0.0F, 0.0F, 1.0F);
        p_114211_.scale(-1.0F, -1.0F, 1.0F);
        p_114211_.translate(0.0F, -1.501F, 0.0F);
        boolean $$8 = p_114208_.hurtTime > 0;
        this.model.prepareMobModel(p_114208_, 0.0F, 0.0F, p_114210_);
        VertexConsumer $$13;
        if (p_114208_.dragonDeathTime > 0) {
            float $$9 = (float)p_114208_.dragonDeathTime / 200.0F;
            VertexConsumer $$10 = p_114212_.getBuffer(RenderType.dragonExplosionAlpha(DRAGON_EXPLODING_LOCATION));
            this.model.renderToBuffer(p_114211_, $$10, p_114213_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, $$9);
            VertexConsumer $$11 = p_114212_.getBuffer(DECAL);
            this.model.renderToBuffer(p_114211_, $$11, p_114213_, OverlayTexture.pack(0.0F, $$8), 1.0F, 1.0F, 1.0F, 1.0F);
        } else {
            $$13 = p_114212_.getBuffer(RENDER_TYPE);
            this.model.renderToBuffer(p_114211_, $$13, p_114213_, OverlayTexture.pack(0.0F, $$8), 1.0F, 1.0F, 1.0F, 1.0F);
        }

        $$13 = p_114212_.getBuffer(EYES);
        this.model.renderToBuffer(p_114211_, $$13, p_114213_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        float $$14;
        float $$15;
        if (p_114208_.dragonDeathTime > 0) {
            $$14 = ((float)p_114208_.dragonDeathTime + p_114210_) / 200.0F;
            $$15 = Math.min($$14 > 0.8F ? ($$14 - 0.8F) / 0.2F : 0.0F, 1.0F);
            RandomSource $$16 = RandomSource.create(432L);
            VertexConsumer $$17 = p_114212_.getBuffer(RenderType.lightning());
            p_114211_.pushPose();
            p_114211_.translate(0.0F, -1.0F, -2.0F);

            for(int $$18 = 0; (float)$$18 < ($$14 + $$14 * $$14) / 2.0F * 60.0F; ++$$18) {
                p_114211_.mulPose(Axis.XP.rotationDegrees($$16.nextFloat() * 360.0F));
                p_114211_.mulPose(Axis.YP.rotationDegrees($$16.nextFloat() * 360.0F));
                p_114211_.mulPose(Axis.ZP.rotationDegrees($$16.nextFloat() * 360.0F));
                p_114211_.mulPose(Axis.XP.rotationDegrees($$16.nextFloat() * 360.0F));
                p_114211_.mulPose(Axis.YP.rotationDegrees($$16.nextFloat() * 360.0F));
                p_114211_.mulPose(Axis.ZP.rotationDegrees($$16.nextFloat() * 360.0F + $$14 * 90.0F));
                float $$19 = $$16.nextFloat() * 20.0F + 5.0F + $$15 * 10.0F;
                float $$20 = $$16.nextFloat() * 2.0F + 1.0F + $$15 * 2.0F;
                Matrix4f $$21 = p_114211_.last().pose();
                int $$22 = (int)(255.0F * (1.0F - $$15));
                vertex01($$17, $$21, $$22);
                vertex2($$17, $$21, $$19, $$20);
                vertex3($$17, $$21, $$19, $$20);
                vertex01($$17, $$21, $$22);
                vertex3($$17, $$21, $$19, $$20);
                vertex4($$17, $$21, $$19, $$20);
                vertex01($$17, $$21, $$22);
                vertex4($$17, $$21, $$19, $$20);
                vertex2($$17, $$21, $$19, $$20);
            }

            p_114211_.popPose();
        }

        p_114211_.popPose();
        if (p_114208_.nearestCrystal != null) {
            p_114211_.pushPose();
            $$14 = (float)(p_114208_.nearestCrystal.getX() - Mth.lerp((double)p_114210_, p_114208_.xo, p_114208_.getX()));
            $$15 = (float)(p_114208_.nearestCrystal.getY() - Mth.lerp((double)p_114210_, p_114208_.yo, p_114208_.getY()));
            float $$25 = (float)(p_114208_.nearestCrystal.getZ() - Mth.lerp((double)p_114210_, p_114208_.zo, p_114208_.getZ()));
            renderCrystalBeams($$14, $$15 + EndCrystalRenderer.getY(p_114208_.nearestCrystal, p_114210_), $$25, p_114210_, p_114208_.tickCount, p_114211_, p_114212_, p_114213_);
            p_114211_.popPose();
        }

        super.render(p_114208_, p_114209_, p_114210_, p_114211_, p_114212_, p_114213_);
    }

    private static void vertex01(VertexConsumer p_254498_, Matrix4f p_253891_, int p_254278_) {
        p_254498_.vertex(p_253891_, 0.0F, 0.0F, 0.0F).color(255, 255, 255, p_254278_).endVertex();
    }

    private static void vertex2(VertexConsumer p_253956_, Matrix4f p_254053_, float p_253704_, float p_253701_) {
        p_253956_.vertex(p_254053_, -HALF_SQRT_3 * p_253701_, p_253704_, -0.5F * p_253701_).color(255, 0, 255, 0).endVertex();
    }

    private static void vertex3(VertexConsumer p_253850_, Matrix4f p_254379_, float p_253729_, float p_254030_) {
        p_253850_.vertex(p_254379_, HALF_SQRT_3 * p_254030_, p_253729_, -0.5F * p_254030_).color(255, 0, 255, 0).endVertex();
    }

    private static void vertex4(VertexConsumer p_254184_, Matrix4f p_254082_, float p_253649_, float p_253694_) {
        p_254184_.vertex(p_254082_, 0.0F, p_253649_, 1.0F * p_253694_).color(255, 0, 255, 0).endVertex();
    }

    public static void renderCrystalBeams(float p_114188_, float p_114189_, float p_114190_, float p_114191_, int p_114192_, PoseStack p_114193_, MultiBufferSource p_114194_, int p_114195_) {
        float $$8 = Mth.sqrt(p_114188_ * p_114188_ + p_114190_ * p_114190_);
        float $$9 = Mth.sqrt(p_114188_ * p_114188_ + p_114189_ * p_114189_ + p_114190_ * p_114190_);
        p_114193_.pushPose();
        p_114193_.translate(0.0F, 2.0F, 0.0F);
        p_114193_.mulPose(Axis.YP.rotation((float)(-Math.atan2((double)p_114190_, (double)p_114188_)) - 1.5707964F));
        p_114193_.mulPose(Axis.XP.rotation((float)(-Math.atan2((double)$$8, (double)p_114189_)) - 1.5707964F));
        VertexConsumer $$10 = p_114194_.getBuffer(BEAM);
        float $$11 = 0.0F - ((float)p_114192_ + p_114191_) * 0.01F;
        float $$12 = Mth.sqrt(p_114188_ * p_114188_ + p_114189_ * p_114189_ + p_114190_ * p_114190_) / 32.0F - ((float)p_114192_ + p_114191_) * 0.01F;
        int $$13 = true;
        float $$14 = 0.0F;
        float $$15 = 0.75F;
        float $$16 = 0.0F;
        PoseStack.Pose $$17 = p_114193_.last();
        Matrix4f $$18 = $$17.pose();
        Matrix3f $$19 = $$17.normal();

        for(int $$20 = 1; $$20 <= 8; ++$$20) {
            float $$21 = Mth.sin((float)$$20 * 6.2831855F / 8.0F) * 0.75F;
            float $$22 = Mth.cos((float)$$20 * 6.2831855F / 8.0F) * 0.75F;
            float $$23 = (float)$$20 / 8.0F;
            $$10.vertex($$18, $$14 * 0.2F, $$15 * 0.2F, 0.0F).color(0, 0, 0, 255).uv($$16, $$11).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(p_114195_).normal($$19, 0.0F, -1.0F, 0.0F).endVertex();
            $$10.vertex($$18, $$14, $$15, $$9).color(255, 255, 255, 255).uv($$16, $$12).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(p_114195_).normal($$19, 0.0F, -1.0F, 0.0F).endVertex();
            $$10.vertex($$18, $$21, $$22, $$9).color(255, 255, 255, 255).uv($$23, $$12).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(p_114195_).normal($$19, 0.0F, -1.0F, 0.0F).endVertex();
            $$10.vertex($$18, $$21 * 0.2F, $$22 * 0.2F, 0.0F).color(0, 0, 0, 255).uv($$23, $$11).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(p_114195_).normal($$19, 0.0F, -1.0F, 0.0F).endVertex();
            $$14 = $$21;
            $$15 = $$22;
            $$16 = $$23;
        }

        p_114193_.popPose();
    }

    public ResourceLocation getTextureLocation(EnderDragon p_114206_) {
        return DRAGON_LOCATION;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        float $$2 = -16.0F;
        PartDefinition $$3 = $$1.addOrReplaceChild("head", CubeListBuilder.create().addBox("upperlip", -6.0F, -1.0F, -24.0F, 12, 5, 16, 176, 44).addBox("upperhead", -8.0F, -8.0F, -10.0F, 16, 16, 16, 112, 30).mirror().addBox("scale", -5.0F, -12.0F, -4.0F, 2, 4, 6, 0, 0).addBox("nostril", -5.0F, -3.0F, -22.0F, 2, 2, 4, 112, 0).mirror().addBox("scale", 3.0F, -12.0F, -4.0F, 2, 4, 6, 0, 0).addBox("nostril", 3.0F, -3.0F, -22.0F, 2, 2, 4, 112, 0), PartPose.ZERO);
        $$3.addOrReplaceChild("jaw", CubeListBuilder.create().addBox("jaw", -6.0F, 0.0F, -16.0F, 12, 4, 16, 176, 65), PartPose.offset(0.0F, 4.0F, -8.0F));
        $$1.addOrReplaceChild("neck", CubeListBuilder.create().addBox("box", -5.0F, -5.0F, -5.0F, 10, 10, 10, 192, 104).addBox("scale", -1.0F, -9.0F, -3.0F, 2, 4, 6, 48, 0), PartPose.ZERO);
        $$1.addOrReplaceChild("body", CubeListBuilder.create().addBox("body", -12.0F, 0.0F, -16.0F, 24, 24, 64, 0, 0).addBox("scale", -1.0F, -6.0F, -10.0F, 2, 6, 12, 220, 53).addBox("scale", -1.0F, -6.0F, 10.0F, 2, 6, 12, 220, 53).addBox("scale", -1.0F, -6.0F, 30.0F, 2, 6, 12, 220, 53), PartPose.offset(0.0F, 4.0F, 8.0F));
        PartDefinition $$4 = $$1.addOrReplaceChild("left_wing", CubeListBuilder.create().mirror().addBox("bone", 0.0F, -4.0F, -4.0F, 56, 8, 8, 112, 88).addBox("skin", 0.0F, 0.0F, 2.0F, 56, 0, 56, -56, 88), PartPose.offset(12.0F, 5.0F, 2.0F));
        $$4.addOrReplaceChild("left_wing_tip", CubeListBuilder.create().mirror().addBox("bone", 0.0F, -2.0F, -2.0F, 56, 4, 4, 112, 136).addBox("skin", 0.0F, 0.0F, 2.0F, 56, 0, 56, -56, 144), PartPose.offset(56.0F, 0.0F, 0.0F));
        PartDefinition $$5 = $$1.addOrReplaceChild("left_front_leg", CubeListBuilder.create().addBox("main", -4.0F, -4.0F, -4.0F, 8, 24, 8, 112, 104), PartPose.offset(12.0F, 20.0F, 2.0F));
        PartDefinition $$6 = $$5.addOrReplaceChild("left_front_leg_tip", CubeListBuilder.create().addBox("main", -3.0F, -1.0F, -3.0F, 6, 24, 6, 226, 138), PartPose.offset(0.0F, 20.0F, -1.0F));
        $$6.addOrReplaceChild("left_front_foot", CubeListBuilder.create().addBox("main", -4.0F, 0.0F, -12.0F, 8, 4, 16, 144, 104), PartPose.offset(0.0F, 23.0F, 0.0F));
        PartDefinition $$7 = $$1.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().addBox("main", -8.0F, -4.0F, -8.0F, 16, 32, 16, 0, 0), PartPose.offset(16.0F, 16.0F, 42.0F));
        PartDefinition $$8 = $$7.addOrReplaceChild("left_hind_leg_tip", CubeListBuilder.create().addBox("main", -6.0F, -2.0F, 0.0F, 12, 32, 12, 196, 0), PartPose.offset(0.0F, 32.0F, -4.0F));
        $$8.addOrReplaceChild("left_hind_foot", CubeListBuilder.create().addBox("main", -9.0F, 0.0F, -20.0F, 18, 6, 24, 112, 0), PartPose.offset(0.0F, 31.0F, 4.0F));
        PartDefinition $$9 = $$1.addOrReplaceChild("right_wing", CubeListBuilder.create().addBox("bone", -56.0F, -4.0F, -4.0F, 56, 8, 8, 112, 88).addBox("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56, -56, 88), PartPose.offset(-12.0F, 5.0F, 2.0F));
        $$9.addOrReplaceChild("right_wing_tip", CubeListBuilder.create().addBox("bone", -56.0F, -2.0F, -2.0F, 56, 4, 4, 112, 136).addBox("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56, -56, 144), PartPose.offset(-56.0F, 0.0F, 0.0F));
        PartDefinition $$10 = $$1.addOrReplaceChild("right_front_leg", CubeListBuilder.create().addBox("main", -4.0F, -4.0F, -4.0F, 8, 24, 8, 112, 104), PartPose.offset(-12.0F, 20.0F, 2.0F));
        PartDefinition $$11 = $$10.addOrReplaceChild("right_front_leg_tip", CubeListBuilder.create().addBox("main", -3.0F, -1.0F, -3.0F, 6, 24, 6, 226, 138), PartPose.offset(0.0F, 20.0F, -1.0F));
        $$11.addOrReplaceChild("right_front_foot", CubeListBuilder.create().addBox("main", -4.0F, 0.0F, -12.0F, 8, 4, 16, 144, 104), PartPose.offset(0.0F, 23.0F, 0.0F));
        PartDefinition $$12 = $$1.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().addBox("main", -8.0F, -4.0F, -8.0F, 16, 32, 16, 0, 0), PartPose.offset(-16.0F, 16.0F, 42.0F));
        PartDefinition $$13 = $$12.addOrReplaceChild("right_hind_leg_tip", CubeListBuilder.create().addBox("main", -6.0F, -2.0F, 0.0F, 12, 32, 12, 196, 0), PartPose.offset(0.0F, 32.0F, -4.0F));
        $$13.addOrReplaceChild("right_hind_foot", CubeListBuilder.create().addBox("main", -9.0F, 0.0F, -20.0F, 18, 6, 24, 112, 0), PartPose.offset(0.0F, 31.0F, 4.0F));
        return LayerDefinition.create($$0, 256, 256);
    }

    static {
        RENDER_TYPE = RenderType.entityCutoutNoCull(DRAGON_LOCATION);
        DECAL = RenderType.entityDecal(DRAGON_LOCATION);
        EYES = RenderType.eyes(DRAGON_EYES_LOCATION);
        BEAM = RenderType.entitySmoothCutout(CRYSTAL_BEAM_LOCATION);
        HALF_SQRT_3 = (float)(Math.sqrt(3.0) / 2.0);
    }

    @OnlyIn(Dist.CLIENT)
    public static class DragonModel extends EntityModel<EnderDragon> {
        private final ModelPart head;
        private final ModelPart neck;
        private final ModelPart jaw;
        private final ModelPart body;
        private final ModelPart leftWing;
        private final ModelPart leftWingTip;
        private final ModelPart leftFrontLeg;
        private final ModelPart leftFrontLegTip;
        private final ModelPart leftFrontFoot;
        private final ModelPart leftRearLeg;
        private final ModelPart leftRearLegTip;
        private final ModelPart leftRearFoot;
        private final ModelPart rightWing;
        private final ModelPart rightWingTip;
        private final ModelPart rightFrontLeg;
        private final ModelPart rightFrontLegTip;
        private final ModelPart rightFrontFoot;
        private final ModelPart rightRearLeg;
        private final ModelPart rightRearLegTip;
        private final ModelPart rightRearFoot;
        @Nullable
        private EnderDragon entity;
        private float a;

        public DragonModel(ModelPart p_173976_) {
            this.head = p_173976_.getChild("head");
            this.jaw = this.head.getChild("jaw");
            this.neck = p_173976_.getChild("neck");
            this.body = p_173976_.getChild("body");
            this.leftWing = p_173976_.getChild("left_wing");
            this.leftWingTip = this.leftWing.getChild("left_wing_tip");
            this.leftFrontLeg = p_173976_.getChild("left_front_leg");
            this.leftFrontLegTip = this.leftFrontLeg.getChild("left_front_leg_tip");
            this.leftFrontFoot = this.leftFrontLegTip.getChild("left_front_foot");
            this.leftRearLeg = p_173976_.getChild("left_hind_leg");
            this.leftRearLegTip = this.leftRearLeg.getChild("left_hind_leg_tip");
            this.leftRearFoot = this.leftRearLegTip.getChild("left_hind_foot");
            this.rightWing = p_173976_.getChild("right_wing");
            this.rightWingTip = this.rightWing.getChild("right_wing_tip");
            this.rightFrontLeg = p_173976_.getChild("right_front_leg");
            this.rightFrontLegTip = this.rightFrontLeg.getChild("right_front_leg_tip");
            this.rightFrontFoot = this.rightFrontLegTip.getChild("right_front_foot");
            this.rightRearLeg = p_173976_.getChild("right_hind_leg");
            this.rightRearLegTip = this.rightRearLeg.getChild("right_hind_leg_tip");
            this.rightRearFoot = this.rightRearLegTip.getChild("right_hind_foot");
        }

        public void prepareMobModel(EnderDragon p_114269_, float p_114270_, float p_114271_, float p_114272_) {
            this.entity = p_114269_;
            this.a = p_114272_;
        }

        public void setupAnim(EnderDragon p_114274_, float p_114275_, float p_114276_, float p_114277_, float p_114278_, float p_114279_) {
        }

        public void renderToBuffer(PoseStack p_114281_, VertexConsumer p_114282_, int p_114283_, int p_114284_, float p_114285_, float p_114286_, float p_114287_, float p_114288_) {
            p_114281_.pushPose();
            float $$8 = Mth.lerp(this.a, this.entity.oFlapTime, this.entity.flapTime);
            this.jaw.xRot = (float)(Math.sin((double)($$8 * 6.2831855F)) + 1.0) * 0.2F;
            float $$9 = (float)(Math.sin((double)($$8 * 6.2831855F - 1.0F)) + 1.0);
            $$9 = ($$9 * $$9 + $$9 * 2.0F) * 0.05F;
            p_114281_.translate(0.0F, $$9 - 2.0F, -3.0F);
            p_114281_.mulPose(Axis.XP.rotationDegrees($$9 * 2.0F));
            float $$10 = 0.0F;
            float $$11 = 20.0F;
            float $$12 = -12.0F;
            float $$13 = 1.5F;
            double[] $$14 = this.entity.getLatencyPos(6, this.a);
            float $$15 = Mth.wrapDegrees((float)(this.entity.getLatencyPos(5, this.a)[0] - this.entity.getLatencyPos(10, this.a)[0]));
            float $$16 = Mth.wrapDegrees((float)(this.entity.getLatencyPos(5, this.a)[0] + (double)($$15 / 2.0F)));
            float $$17 = $$8 * 6.2831855F;

            float $$23;
            for(int $$18 = 0; $$18 < 5; ++$$18) {
                double[] $$19 = this.entity.getLatencyPos(5 - $$18, this.a);
                $$23 = (float)Math.cos((double)((float)$$18 * 0.45F + $$17)) * 0.15F;
                this.neck.yRot = Mth.wrapDegrees((float)($$19[0] - $$14[0])) * 0.017453292F * 1.5F;
                this.neck.xRot = $$23 + this.entity.getHeadPartYOffset($$18, $$14, $$19) * 0.017453292F * 1.5F * 5.0F;
                this.neck.zRot = -Mth.wrapDegrees((float)($$19[0] - (double)$$16)) * 0.017453292F * 1.5F;
                this.neck.y = $$11;
                this.neck.z = $$12;
                this.neck.x = $$10;
                $$11 += Mth.sin(this.neck.xRot) * 10.0F;
                $$12 -= Mth.cos(this.neck.yRot) * Mth.cos(this.neck.xRot) * 10.0F;
                $$10 -= Mth.sin(this.neck.yRot) * Mth.cos(this.neck.xRot) * 10.0F;
                this.neck.render(p_114281_, p_114282_, p_114283_, p_114284_, 1.0F, 1.0F, 1.0F, p_114288_);
            }

            this.head.y = $$11;
            this.head.z = $$12;
            this.head.x = $$10;
            double[] $$21 = this.entity.getLatencyPos(0, this.a);
            this.head.yRot = Mth.wrapDegrees((float)($$21[0] - $$14[0])) * 0.017453292F;
            this.head.xRot = Mth.wrapDegrees(this.entity.getHeadPartYOffset(6, $$14, $$21)) * 0.017453292F * 1.5F * 5.0F;
            this.head.zRot = -Mth.wrapDegrees((float)($$21[0] - (double)$$16)) * 0.017453292F;
            this.head.render(p_114281_, p_114282_, p_114283_, p_114284_, 1.0F, 1.0F, 1.0F, p_114288_);
            p_114281_.pushPose();
            p_114281_.translate(0.0F, 1.0F, 0.0F);
            p_114281_.mulPose(Axis.ZP.rotationDegrees(-$$15 * 1.5F));
            p_114281_.translate(0.0F, -1.0F, 0.0F);
            this.body.zRot = 0.0F;
            this.body.render(p_114281_, p_114282_, p_114283_, p_114284_, 1.0F, 1.0F, 1.0F, p_114288_);
            float $$22 = $$8 * 6.2831855F;
            this.leftWing.xRot = 0.125F - (float)Math.cos((double)$$22) * 0.2F;
            this.leftWing.yRot = -0.25F;
            this.leftWing.zRot = -((float)(Math.sin((double)$$22) + 0.125)) * 0.8F;
            this.leftWingTip.zRot = (float)(Math.sin((double)($$22 + 2.0F)) + 0.5) * 0.75F;
            this.rightWing.xRot = this.leftWing.xRot;
            this.rightWing.yRot = -this.leftWing.yRot;
            this.rightWing.zRot = -this.leftWing.zRot;
            this.rightWingTip.zRot = -this.leftWingTip.zRot;
            this.renderSide(p_114281_, p_114282_, p_114283_, p_114284_, $$9, this.leftWing, this.leftFrontLeg, this.leftFrontLegTip, this.leftFrontFoot, this.leftRearLeg, this.leftRearLegTip, this.leftRearFoot, p_114288_);
            this.renderSide(p_114281_, p_114282_, p_114283_, p_114284_, $$9, this.rightWing, this.rightFrontLeg, this.rightFrontLegTip, this.rightFrontFoot, this.rightRearLeg, this.rightRearLegTip, this.rightRearFoot, p_114288_);
            p_114281_.popPose();
            $$23 = -Mth.sin($$8 * 6.2831855F) * 0.0F;
            $$17 = $$8 * 6.2831855F;
            $$11 = 10.0F;
            $$12 = 60.0F;
            $$10 = 0.0F;
            $$14 = this.entity.getLatencyPos(11, this.a);

            for(int $$24 = 0; $$24 < 12; ++$$24) {
                $$21 = this.entity.getLatencyPos(12 + $$24, this.a);
                $$23 += Mth.sin((float)$$24 * 0.45F + $$17) * 0.05F;
                this.neck.yRot = (Mth.wrapDegrees((float)($$21[0] - $$14[0])) * 1.5F + 180.0F) * 0.017453292F;
                this.neck.xRot = $$23 + (float)($$21[1] - $$14[1]) * 0.017453292F * 1.5F * 5.0F;
                this.neck.zRot = Mth.wrapDegrees((float)($$21[0] - (double)$$16)) * 0.017453292F * 1.5F;
                this.neck.y = $$11;
                this.neck.z = $$12;
                this.neck.x = $$10;
                $$11 += Mth.sin(this.neck.xRot) * 10.0F;
                $$12 -= Mth.cos(this.neck.yRot) * Mth.cos(this.neck.xRot) * 10.0F;
                $$10 -= Mth.sin(this.neck.yRot) * Mth.cos(this.neck.xRot) * 10.0F;
                this.neck.render(p_114281_, p_114282_, p_114283_, p_114284_, 1.0F, 1.0F, 1.0F, p_114288_);
            }

            p_114281_.popPose();
        }

        private void renderSide(PoseStack p_173978_, VertexConsumer p_173979_, int p_173980_, int p_173981_, float p_173982_, ModelPart p_173983_, ModelPart p_173984_, ModelPart p_173985_, ModelPart p_173986_, ModelPart p_173987_, ModelPart p_173988_, ModelPart p_173989_, float p_173990_) {
            p_173987_.xRot = 1.0F + p_173982_ * 0.1F;
            p_173988_.xRot = 0.5F + p_173982_ * 0.1F;
            p_173989_.xRot = 0.75F + p_173982_ * 0.1F;
            p_173984_.xRot = 1.3F + p_173982_ * 0.1F;
            p_173985_.xRot = -0.5F - p_173982_ * 0.1F;
            p_173986_.xRot = 0.75F + p_173982_ * 0.1F;
            p_173983_.render(p_173978_, p_173979_, p_173980_, p_173981_, 1.0F, 1.0F, 1.0F, p_173990_);
            p_173984_.render(p_173978_, p_173979_, p_173980_, p_173981_, 1.0F, 1.0F, 1.0F, p_173990_);
            p_173987_.render(p_173978_, p_173979_, p_173980_, p_173981_, 1.0F, 1.0F, 1.0F, p_173990_);
        }
    }
}

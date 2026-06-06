//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpiderModel<T extends Entity> extends HierarchicalModel<T> {
    private static final String BODY_0 = "body0";
    private static final String BODY_1 = "body1";
    private static final String RIGHT_MIDDLE_FRONT_LEG = "right_middle_front_leg";
    private static final String LEFT_MIDDLE_FRONT_LEG = "left_middle_front_leg";
    private static final String RIGHT_MIDDLE_HIND_LEG = "right_middle_hind_leg";
    private static final String LEFT_MIDDLE_HIND_LEG = "left_middle_hind_leg";
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightMiddleHindLeg;
    private final ModelPart leftMiddleHindLeg;
    private final ModelPart rightMiddleFrontLeg;
    private final ModelPart leftMiddleFrontLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;

    public SpiderModel(ModelPart p_170984_) {
        this.root = p_170984_;
        this.head = p_170984_.getChild("head");
        this.rightHindLeg = p_170984_.getChild("right_hind_leg");
        this.leftHindLeg = p_170984_.getChild("left_hind_leg");
        this.rightMiddleHindLeg = p_170984_.getChild("right_middle_hind_leg");
        this.leftMiddleHindLeg = p_170984_.getChild("left_middle_hind_leg");
        this.rightMiddleFrontLeg = p_170984_.getChild("right_middle_front_leg");
        this.leftMiddleFrontLeg = p_170984_.getChild("left_middle_front_leg");
        this.rightFrontLeg = p_170984_.getChild("right_front_leg");
        this.leftFrontLeg = p_170984_.getChild("left_front_leg");
    }

    public static LayerDefinition createSpiderBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        int $$2 = true;
        $$1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(32, 4).addBox(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F), PartPose.offset(0.0F, 15.0F, -3.0F));
        $$1.addOrReplaceChild("body0", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F), PartPose.offset(0.0F, 15.0F, 0.0F));
        $$1.addOrReplaceChild("body1", CubeListBuilder.create().texOffs(0, 12).addBox(-5.0F, -4.0F, -6.0F, 10.0F, 8.0F, 12.0F), PartPose.offset(0.0F, 15.0F, 9.0F));
        CubeListBuilder $$3 = CubeListBuilder.create().texOffs(18, 0).addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F);
        CubeListBuilder $$4 = CubeListBuilder.create().texOffs(18, 0).mirror().addBox(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F);
        $$1.addOrReplaceChild("right_hind_leg", $$3, PartPose.offset(-4.0F, 15.0F, 2.0F));
        $$1.addOrReplaceChild("left_hind_leg", $$4, PartPose.offset(4.0F, 15.0F, 2.0F));
        $$1.addOrReplaceChild("right_middle_hind_leg", $$3, PartPose.offset(-4.0F, 15.0F, 1.0F));
        $$1.addOrReplaceChild("left_middle_hind_leg", $$4, PartPose.offset(4.0F, 15.0F, 1.0F));
        $$1.addOrReplaceChild("right_middle_front_leg", $$3, PartPose.offset(-4.0F, 15.0F, 0.0F));
        $$1.addOrReplaceChild("left_middle_front_leg", $$4, PartPose.offset(4.0F, 15.0F, 0.0F));
        $$1.addOrReplaceChild("right_front_leg", $$3, PartPose.offset(-4.0F, 15.0F, -1.0F));
        $$1.addOrReplaceChild("left_front_leg", $$4, PartPose.offset(4.0F, 15.0F, -1.0F));
        return LayerDefinition.create($$0, 64, 32);
    }

    public ModelPart root() {
        return this.root;
    }

    public void setupAnim(T p_103866_, float p_103867_, float p_103868_, float p_103869_, float p_103870_, float p_103871_) {
        this.head.yRot = p_103870_ * 0.017453292F;
        this.head.xRot = p_103871_ * 0.017453292F;
        float $$6 = 0.7853982F;
        this.rightHindLeg.zRot = -0.7853982F;
        this.leftHindLeg.zRot = 0.7853982F;
        this.rightMiddleHindLeg.zRot = -0.58119464F;
        this.leftMiddleHindLeg.zRot = 0.58119464F;
        this.rightMiddleFrontLeg.zRot = -0.58119464F;
        this.leftMiddleFrontLeg.zRot = 0.58119464F;
        this.rightFrontLeg.zRot = -0.7853982F;
        this.leftFrontLeg.zRot = 0.7853982F;
        float $$7 = -0.0F;
        float $$8 = 0.3926991F;
        this.rightHindLeg.yRot = 0.7853982F;
        this.leftHindLeg.yRot = -0.7853982F;
        this.rightMiddleHindLeg.yRot = 0.3926991F;
        this.leftMiddleHindLeg.yRot = -0.3926991F;
        this.rightMiddleFrontLeg.yRot = -0.3926991F;
        this.leftMiddleFrontLeg.yRot = 0.3926991F;
        this.rightFrontLeg.yRot = -0.7853982F;
        this.leftFrontLeg.yRot = 0.7853982F;
        float $$9 = -(Mth.cos(p_103867_ * 0.6662F * 2.0F + 0.0F) * 0.4F) * p_103868_;
        float $$10 = -(Mth.cos(p_103867_ * 0.6662F * 2.0F + 3.1415927F) * 0.4F) * p_103868_;
        float $$11 = -(Mth.cos(p_103867_ * 0.6662F * 2.0F + 1.5707964F) * 0.4F) * p_103868_;
        float $$12 = -(Mth.cos(p_103867_ * 0.6662F * 2.0F + 4.712389F) * 0.4F) * p_103868_;
        float $$13 = Math.abs(Mth.sin(p_103867_ * 0.6662F + 0.0F) * 0.4F) * p_103868_;
        float $$14 = Math.abs(Mth.sin(p_103867_ * 0.6662F + 3.1415927F) * 0.4F) * p_103868_;
        float $$15 = Math.abs(Mth.sin(p_103867_ * 0.6662F + 1.5707964F) * 0.4F) * p_103868_;
        float $$16 = Math.abs(Mth.sin(p_103867_ * 0.6662F + 4.712389F) * 0.4F) * p_103868_;
        ModelPart var10000 = this.rightHindLeg;
        var10000.yRot += $$9;
        var10000 = this.leftHindLeg;
        var10000.yRot += -$$9;
        var10000 = this.rightMiddleHindLeg;
        var10000.yRot += $$10;
        var10000 = this.leftMiddleHindLeg;
        var10000.yRot += -$$10;
        var10000 = this.rightMiddleFrontLeg;
        var10000.yRot += $$11;
        var10000 = this.leftMiddleFrontLeg;
        var10000.yRot += -$$11;
        var10000 = this.rightFrontLeg;
        var10000.yRot += $$12;
        var10000 = this.leftFrontLeg;
        var10000.yRot += -$$12;
        var10000 = this.rightHindLeg;
        var10000.zRot += $$13;
        var10000 = this.leftHindLeg;
        var10000.zRot += -$$13;
        var10000 = this.rightMiddleHindLeg;
        var10000.zRot += $$14;
        var10000 = this.leftMiddleHindLeg;
        var10000.zRot += -$$14;
        var10000 = this.rightMiddleFrontLeg;
        var10000.zRot += $$15;
        var10000 = this.leftMiddleFrontLeg;
        var10000.zRot += -$$15;
        var10000 = this.rightFrontLeg;
        var10000.zRot += $$16;
        var10000 = this.leftFrontLeg;
        var10000.zRot += -$$16;
    }
}

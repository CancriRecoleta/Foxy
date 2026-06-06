//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ArmorStandArmorModel extends HumanoidModel<ArmorStand> {
    public ArmorStandArmorModel(ModelPart p_170346_) {
        super(p_170346_);
    }

    public static LayerDefinition createBodyLayer(CubeDeformation p_170348_) {
        MeshDefinition $$1 = HumanoidModel.createMesh(p_170348_, 0.0F);
        PartDefinition $$2 = $$1.getRoot();
        $$2.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, p_170348_), PartPose.offset(0.0F, 1.0F, 0.0F));
        $$2.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, p_170348_.extend(0.5F)), PartPose.offset(0.0F, 1.0F, 0.0F));
        $$2.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_170348_.extend(-0.1F)), PartPose.offset(-1.9F, 11.0F, 0.0F));
        $$2.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_170348_.extend(-0.1F)), PartPose.offset(1.9F, 11.0F, 0.0F));
        return LayerDefinition.create($$1, 64, 32);
    }

    public void setupAnim(ArmorStand p_102131_, float p_102132_, float p_102133_, float p_102134_, float p_102135_, float p_102136_) {
        this.head.xRot = 0.017453292F * p_102131_.getHeadPose().getX();
        this.head.yRot = 0.017453292F * p_102131_.getHeadPose().getY();
        this.head.zRot = 0.017453292F * p_102131_.getHeadPose().getZ();
        this.body.xRot = 0.017453292F * p_102131_.getBodyPose().getX();
        this.body.yRot = 0.017453292F * p_102131_.getBodyPose().getY();
        this.body.zRot = 0.017453292F * p_102131_.getBodyPose().getZ();
        this.leftArm.xRot = 0.017453292F * p_102131_.getLeftArmPose().getX();
        this.leftArm.yRot = 0.017453292F * p_102131_.getLeftArmPose().getY();
        this.leftArm.zRot = 0.017453292F * p_102131_.getLeftArmPose().getZ();
        this.rightArm.xRot = 0.017453292F * p_102131_.getRightArmPose().getX();
        this.rightArm.yRot = 0.017453292F * p_102131_.getRightArmPose().getY();
        this.rightArm.zRot = 0.017453292F * p_102131_.getRightArmPose().getZ();
        this.leftLeg.xRot = 0.017453292F * p_102131_.getLeftLegPose().getX();
        this.leftLeg.yRot = 0.017453292F * p_102131_.getLeftLegPose().getY();
        this.leftLeg.zRot = 0.017453292F * p_102131_.getLeftLegPose().getZ();
        this.rightLeg.xRot = 0.017453292F * p_102131_.getRightLegPose().getX();
        this.rightLeg.yRot = 0.017453292F * p_102131_.getRightLegPose().getY();
        this.rightLeg.zRot = 0.017453292F * p_102131_.getRightLegPose().getZ();
        this.hat.copyFrom(this.head);
    }
}

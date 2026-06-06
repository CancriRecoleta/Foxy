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
import net.minecraft.world.entity.monster.Strider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StriderModel<T extends Strider> extends HierarchicalModel<T> {
    private static final String RIGHT_BOTTOM_BRISTLE = "right_bottom_bristle";
    private static final String RIGHT_MIDDLE_BRISTLE = "right_middle_bristle";
    private static final String RIGHT_TOP_BRISTLE = "right_top_bristle";
    private static final String LEFT_TOP_BRISTLE = "left_top_bristle";
    private static final String LEFT_MIDDLE_BRISTLE = "left_middle_bristle";
    private static final String LEFT_BOTTOM_BRISTLE = "left_bottom_bristle";
    private final ModelPart root;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart body;
    private final ModelPart rightBottomBristle;
    private final ModelPart rightMiddleBristle;
    private final ModelPart rightTopBristle;
    private final ModelPart leftTopBristle;
    private final ModelPart leftMiddleBristle;
    private final ModelPart leftBottomBristle;

    public StriderModel(ModelPart p_171011_) {
        this.root = p_171011_;
        this.rightLeg = p_171011_.getChild("right_leg");
        this.leftLeg = p_171011_.getChild("left_leg");
        this.body = p_171011_.getChild("body");
        this.rightBottomBristle = this.body.getChild("right_bottom_bristle");
        this.rightMiddleBristle = this.body.getChild("right_middle_bristle");
        this.rightTopBristle = this.body.getChild("right_top_bristle");
        this.leftTopBristle = this.body.getChild("left_top_bristle");
        this.leftMiddleBristle = this.body.getChild("left_middle_bristle");
        this.leftBottomBristle = this.body.getChild("left_bottom_bristle");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        $$1.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 16.0F, 4.0F), PartPose.offset(-4.0F, 8.0F, 0.0F));
        $$1.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 55).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 16.0F, 4.0F), PartPose.offset(4.0F, 8.0F, 0.0F));
        PartDefinition $$2 = $$1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -6.0F, -8.0F, 16.0F, 14.0F, 16.0F), PartPose.offset(0.0F, 1.0F, 0.0F));
        $$2.addOrReplaceChild("right_bottom_bristle", CubeListBuilder.create().texOffs(16, 65).addBox(-12.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F, true), PartPose.offsetAndRotation(-8.0F, 4.0F, -8.0F, 0.0F, 0.0F, -1.2217305F));
        $$2.addOrReplaceChild("right_middle_bristle", CubeListBuilder.create().texOffs(16, 49).addBox(-12.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F, true), PartPose.offsetAndRotation(-8.0F, -1.0F, -8.0F, 0.0F, 0.0F, -1.134464F));
        $$2.addOrReplaceChild("right_top_bristle", CubeListBuilder.create().texOffs(16, 33).addBox(-12.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F, true), PartPose.offsetAndRotation(-8.0F, -5.0F, -8.0F, 0.0F, 0.0F, -0.87266463F));
        $$2.addOrReplaceChild("left_top_bristle", CubeListBuilder.create().texOffs(16, 33).addBox(0.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F), PartPose.offsetAndRotation(8.0F, -6.0F, -8.0F, 0.0F, 0.0F, 0.87266463F));
        $$2.addOrReplaceChild("left_middle_bristle", CubeListBuilder.create().texOffs(16, 49).addBox(0.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F), PartPose.offsetAndRotation(8.0F, -2.0F, -8.0F, 0.0F, 0.0F, 1.134464F));
        $$2.addOrReplaceChild("left_bottom_bristle", CubeListBuilder.create().texOffs(16, 65).addBox(0.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F), PartPose.offsetAndRotation(8.0F, 3.0F, -8.0F, 0.0F, 0.0F, 1.2217305F));
        return LayerDefinition.create($$0, 64, 128);
    }

    public void setupAnim(Strider p_103903_, float p_103904_, float p_103905_, float p_103906_, float p_103907_, float p_103908_) {
        p_103905_ = Math.min(0.25F, p_103905_);
        if (!p_103903_.isVehicle()) {
            this.body.xRot = p_103908_ * 0.017453292F;
            this.body.yRot = p_103907_ * 0.017453292F;
        } else {
            this.body.xRot = 0.0F;
            this.body.yRot = 0.0F;
        }

        float $$6 = 1.5F;
        this.body.zRot = 0.1F * Mth.sin(p_103904_ * 1.5F) * 4.0F * p_103905_;
        this.body.y = 2.0F;
        ModelPart var10000 = this.body;
        var10000.y -= 2.0F * Mth.cos(p_103904_ * 1.5F) * 2.0F * p_103905_;
        this.leftLeg.xRot = Mth.sin(p_103904_ * 1.5F * 0.5F) * 2.0F * p_103905_;
        this.rightLeg.xRot = Mth.sin(p_103904_ * 1.5F * 0.5F + 3.1415927F) * 2.0F * p_103905_;
        this.leftLeg.zRot = 0.17453292F * Mth.cos(p_103904_ * 1.5F * 0.5F) * p_103905_;
        this.rightLeg.zRot = 0.17453292F * Mth.cos(p_103904_ * 1.5F * 0.5F + 3.1415927F) * p_103905_;
        this.leftLeg.y = 8.0F + 2.0F * Mth.sin(p_103904_ * 1.5F * 0.5F + 3.1415927F) * 2.0F * p_103905_;
        this.rightLeg.y = 8.0F + 2.0F * Mth.sin(p_103904_ * 1.5F * 0.5F) * 2.0F * p_103905_;
        this.rightBottomBristle.zRot = -1.2217305F;
        this.rightMiddleBristle.zRot = -1.134464F;
        this.rightTopBristle.zRot = -0.87266463F;
        this.leftTopBristle.zRot = 0.87266463F;
        this.leftMiddleBristle.zRot = 1.134464F;
        this.leftBottomBristle.zRot = 1.2217305F;
        float $$7 = Mth.cos(p_103904_ * 1.5F + 3.1415927F) * p_103905_;
        var10000 = this.rightBottomBristle;
        var10000.zRot += $$7 * 1.3F;
        var10000 = this.rightMiddleBristle;
        var10000.zRot += $$7 * 1.2F;
        var10000 = this.rightTopBristle;
        var10000.zRot += $$7 * 0.6F;
        var10000 = this.leftTopBristle;
        var10000.zRot += $$7 * 0.6F;
        var10000 = this.leftMiddleBristle;
        var10000.zRot += $$7 * 1.2F;
        var10000 = this.leftBottomBristle;
        var10000.zRot += $$7 * 1.3F;
        float $$8 = 1.0F;
        float $$9 = 1.0F;
        var10000 = this.rightBottomBristle;
        var10000.zRot += 0.05F * Mth.sin(p_103906_ * 1.0F * -0.4F);
        var10000 = this.rightMiddleBristle;
        var10000.zRot += 0.1F * Mth.sin(p_103906_ * 1.0F * 0.2F);
        var10000 = this.rightTopBristle;
        var10000.zRot += 0.1F * Mth.sin(p_103906_ * 1.0F * 0.4F);
        var10000 = this.leftTopBristle;
        var10000.zRot += 0.1F * Mth.sin(p_103906_ * 1.0F * 0.4F);
        var10000 = this.leftMiddleBristle;
        var10000.zRot += 0.1F * Mth.sin(p_103906_ * 1.0F * 0.2F);
        var10000 = this.leftBottomBristle;
        var10000.zRot += 0.05F * Mth.sin(p_103906_ * 1.0F * -0.4F);
    }

    public ModelPart root() {
        return this.root;
    }
}

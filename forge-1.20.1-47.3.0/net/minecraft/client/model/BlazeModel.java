//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.model;

import java.util.Arrays;
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
public class BlazeModel<T extends Entity> extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart[] upperBodyParts;
    private final ModelPart head;

    public BlazeModel(ModelPart p_170443_) {
        this.root = p_170443_;
        this.head = p_170443_.getChild("head");
        this.upperBodyParts = new ModelPart[12];
        Arrays.setAll(this.upperBodyParts, (p_170449_) -> {
            return p_170443_.getChild(getPartName(p_170449_));
        });
    }

    private static String getPartName(int p_170446_) {
        return "part" + p_170446_;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        $$1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
        float $$2 = 0.0F;
        CubeListBuilder $$3 = CubeListBuilder.create().texOffs(0, 16).addBox(0.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F);

        int $$12;
        float $$13;
        float $$14;
        float $$15;
        for($$12 = 0; $$12 < 4; ++$$12) {
            $$13 = Mth.cos($$2) * 9.0F;
            $$14 = -2.0F + Mth.cos((float)($$12 * 2) * 0.25F);
            $$15 = Mth.sin($$2) * 9.0F;
            $$1.addOrReplaceChild(getPartName($$12), $$3, PartPose.offset($$13, $$14, $$15));
            ++$$2;
        }

        $$2 = 0.7853982F;

        for($$12 = 4; $$12 < 8; ++$$12) {
            $$13 = Mth.cos($$2) * 7.0F;
            $$14 = 2.0F + Mth.cos((float)($$12 * 2) * 0.25F);
            $$15 = Mth.sin($$2) * 7.0F;
            $$1.addOrReplaceChild(getPartName($$12), $$3, PartPose.offset($$13, $$14, $$15));
            ++$$2;
        }

        $$2 = 0.47123894F;

        for($$12 = 8; $$12 < 12; ++$$12) {
            $$13 = Mth.cos($$2) * 5.0F;
            $$14 = 11.0F + Mth.cos((float)$$12 * 1.5F * 0.5F);
            $$15 = Mth.sin($$2) * 5.0F;
            $$1.addOrReplaceChild(getPartName($$12), $$3, PartPose.offset($$13, $$14, $$15));
            ++$$2;
        }

        return LayerDefinition.create($$0, 64, 32);
    }

    public ModelPart root() {
        return this.root;
    }

    public void setupAnim(T p_102250_, float p_102251_, float p_102252_, float p_102253_, float p_102254_, float p_102255_) {
        float $$6 = p_102253_ * 3.1415927F * -0.1F;

        int $$9;
        for($$9 = 0; $$9 < 4; ++$$9) {
            this.upperBodyParts[$$9].y = -2.0F + Mth.cos(((float)($$9 * 2) + p_102253_) * 0.25F);
            this.upperBodyParts[$$9].x = Mth.cos($$6) * 9.0F;
            this.upperBodyParts[$$9].z = Mth.sin($$6) * 9.0F;
            ++$$6;
        }

        $$6 = 0.7853982F + p_102253_ * 3.1415927F * 0.03F;

        for($$9 = 4; $$9 < 8; ++$$9) {
            this.upperBodyParts[$$9].y = 2.0F + Mth.cos(((float)($$9 * 2) + p_102253_) * 0.25F);
            this.upperBodyParts[$$9].x = Mth.cos($$6) * 7.0F;
            this.upperBodyParts[$$9].z = Mth.sin($$6) * 7.0F;
            ++$$6;
        }

        $$6 = 0.47123894F + p_102253_ * 3.1415927F * -0.05F;

        for($$9 = 8; $$9 < 12; ++$$9) {
            this.upperBodyParts[$$9].y = 11.0F + Mth.cos(((float)$$9 * 1.5F + p_102253_) * 0.5F);
            this.upperBodyParts[$$9].x = Mth.cos($$6) * 5.0F;
            this.upperBodyParts[$$9].z = Mth.sin($$6) * 5.0F;
            ++$$6;
        }

        this.head.yRot = p_102254_ * 0.017453292F;
        this.head.xRot = p_102255_ * 0.017453292F;
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PiglinHeadModel extends SkullModelBase {
    private final ModelPart head;
    private final ModelPart leftEar;
    private final ModelPart rightEar;

    public PiglinHeadModel(ModelPart p_261926_) {
        this.head = p_261926_.getChild("head");
        this.leftEar = this.head.getChild("left_ear");
        this.rightEar = this.head.getChild("right_ear");
    }

    public static MeshDefinition createHeadModel() {
        MeshDefinition $$0 = new MeshDefinition();
        PiglinModel.addHead(CubeDeformation.NONE, $$0);
        return $$0;
    }

    public void setupAnim(float p_261561_, float p_261750_, float p_261549_) {
        this.head.yRot = p_261750_ * 0.017453292F;
        this.head.xRot = p_261549_ * 0.017453292F;
        float $$3 = 1.2F;
        this.leftEar.zRot = (float)(-(Math.cos((double)(p_261561_ * 3.1415927F * 0.2F * 1.2F)) + 2.5)) * 0.2F;
        this.rightEar.zRot = (float)(Math.cos((double)(p_261561_ * 3.1415927F * 0.2F)) + 2.5) * 0.2F;
    }

    public void renderToBuffer(PoseStack p_261809_, VertexConsumer p_261744_, int p_261711_, int p_262032_, float p_262022_, float p_261949_, float p_261474_, float p_261957_) {
        this.head.render(p_261809_, p_261744_, p_261711_, p_262032_, p_262022_, p_261949_, p_261474_, p_261957_);
    }
}

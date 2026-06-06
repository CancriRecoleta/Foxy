//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class ConduitRenderer implements BlockEntityRenderer<ConduitBlockEntity> {
    public static final Material SHELL_TEXTURE;
    public static final Material ACTIVE_SHELL_TEXTURE;
    public static final Material WIND_TEXTURE;
    public static final Material VERTICAL_WIND_TEXTURE;
    public static final Material OPEN_EYE_TEXTURE;
    public static final Material CLOSED_EYE_TEXTURE;
    private final ModelPart eye;
    private final ModelPart wind;
    private final ModelPart shell;
    private final ModelPart cage;
    private final BlockEntityRenderDispatcher renderer;

    public ConduitRenderer(BlockEntityRendererProvider.Context p_173613_) {
        this.renderer = p_173613_.getBlockEntityRenderDispatcher();
        this.eye = p_173613_.bakeLayer(ModelLayers.CONDUIT_EYE);
        this.wind = p_173613_.bakeLayer(ModelLayers.CONDUIT_WIND);
        this.shell = p_173613_.bakeLayer(ModelLayers.CONDUIT_SHELL);
        this.cage = p_173613_.bakeLayer(ModelLayers.CONDUIT_CAGE);
    }

    public static LayerDefinition createEyeLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        $$1.addOrReplaceChild("eye", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, 0.0F, 8.0F, 8.0F, 0.0F, new CubeDeformation(0.01F)), PartPose.ZERO);
        return LayerDefinition.create($$0, 16, 16);
    }

    public static LayerDefinition createWindLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        $$1.addOrReplaceChild("wind", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F), PartPose.ZERO);
        return LayerDefinition.create($$0, 64, 32);
    }

    public static LayerDefinition createShellLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        $$1.addOrReplaceChild("shell", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F), PartPose.ZERO);
        return LayerDefinition.create($$0, 32, 16);
    }

    public static LayerDefinition createCageLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        $$1.addOrReplaceChild("shell", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
        return LayerDefinition.create($$0, 32, 16);
    }

    public void render(ConduitBlockEntity p_112399_, float p_112400_, PoseStack p_112401_, MultiBufferSource p_112402_, int p_112403_, int p_112404_) {
        float $$6 = (float)p_112399_.tickCount + p_112400_;
        float $$9;
        if (!p_112399_.isActive()) {
            $$9 = p_112399_.getActiveRotation(0.0F);
            VertexConsumer $$8 = SHELL_TEXTURE.buffer(p_112402_, RenderType::entitySolid);
            p_112401_.pushPose();
            p_112401_.translate(0.5F, 0.5F, 0.5F);
            p_112401_.mulPose((new Quaternionf()).rotationY($$9 * 0.017453292F));
            this.shell.render(p_112401_, $$8, p_112403_, p_112404_);
            p_112401_.popPose();
        } else {
            $$9 = p_112399_.getActiveRotation(p_112400_) * 57.295776F;
            float $$10 = Mth.sin($$6 * 0.1F) / 2.0F + 0.5F;
            $$10 += $$10 * $$10;
            p_112401_.pushPose();
            p_112401_.translate(0.5F, 0.3F + $$10 * 0.2F, 0.5F);
            Vector3f $$11 = (new Vector3f(0.5F, 1.0F, 0.5F)).normalize();
            p_112401_.mulPose((new Quaternionf()).rotationAxis($$9 * 0.017453292F, $$11));
            this.cage.render(p_112401_, ACTIVE_SHELL_TEXTURE.buffer(p_112402_, RenderType::entityCutoutNoCull), p_112403_, p_112404_);
            p_112401_.popPose();
            int $$12 = p_112399_.tickCount / 66 % 3;
            p_112401_.pushPose();
            p_112401_.translate(0.5F, 0.5F, 0.5F);
            if ($$12 == 1) {
                p_112401_.mulPose((new Quaternionf()).rotationX(1.5707964F));
            } else if ($$12 == 2) {
                p_112401_.mulPose((new Quaternionf()).rotationZ(1.5707964F));
            }

            VertexConsumer $$13 = ($$12 == 1 ? VERTICAL_WIND_TEXTURE : WIND_TEXTURE).buffer(p_112402_, RenderType::entityCutoutNoCull);
            this.wind.render(p_112401_, $$13, p_112403_, p_112404_);
            p_112401_.popPose();
            p_112401_.pushPose();
            p_112401_.translate(0.5F, 0.5F, 0.5F);
            p_112401_.scale(0.875F, 0.875F, 0.875F);
            p_112401_.mulPose((new Quaternionf()).rotationXYZ(3.1415927F, 0.0F, 3.1415927F));
            this.wind.render(p_112401_, $$13, p_112403_, p_112404_);
            p_112401_.popPose();
            Camera $$14 = this.renderer.camera;
            p_112401_.pushPose();
            p_112401_.translate(0.5F, 0.3F + $$10 * 0.2F, 0.5F);
            p_112401_.scale(0.5F, 0.5F, 0.5F);
            float $$15 = -$$14.getYRot();
            p_112401_.mulPose((new Quaternionf()).rotationYXZ($$15 * 0.017453292F, $$14.getXRot() * 0.017453292F, 3.1415927F));
            float $$16 = 1.3333334F;
            p_112401_.scale(1.3333334F, 1.3333334F, 1.3333334F);
            this.eye.render(p_112401_, (p_112399_.isHunting() ? OPEN_EYE_TEXTURE : CLOSED_EYE_TEXTURE).buffer(p_112402_, RenderType::entityCutoutNoCull), p_112403_, p_112404_);
            p_112401_.popPose();
        }
    }

    static {
        SHELL_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("entity/conduit/base"));
        ACTIVE_SHELL_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("entity/conduit/cage"));
        WIND_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("entity/conduit/wind"));
        VERTICAL_WIND_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("entity/conduit/wind_vertical"));
        OPEN_EYE_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("entity/conduit/open_eye"));
        CLOSED_EYE_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("entity/conduit/closed_eye"));
    }
}

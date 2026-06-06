//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;
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
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.entity.DecoratedPotPatterns;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DecoratedPotRenderer implements BlockEntityRenderer<DecoratedPotBlockEntity> {
    private static final String NECK = "neck";
    private static final String FRONT = "front";
    private static final String BACK = "back";
    private static final String LEFT = "left";
    private static final String RIGHT = "right";
    private static final String TOP = "top";
    private static final String BOTTOM = "bottom";
    private final ModelPart neck;
    private final ModelPart frontSide;
    private final ModelPart backSide;
    private final ModelPart leftSide;
    private final ModelPart rightSide;
    private final ModelPart top;
    private final ModelPart bottom;
    private final Material baseMaterial;

    public DecoratedPotRenderer(BlockEntityRendererProvider.Context p_272872_) {
        this.baseMaterial = (Material)Objects.requireNonNull(Sheets.getDecoratedPotMaterial(DecoratedPotPatterns.BASE));
        ModelPart $$1 = p_272872_.bakeLayer(ModelLayers.DECORATED_POT_BASE);
        this.neck = $$1.getChild("neck");
        this.top = $$1.getChild("top");
        this.bottom = $$1.getChild("bottom");
        ModelPart $$2 = p_272872_.bakeLayer(ModelLayers.DECORATED_POT_SIDES);
        this.frontSide = $$2.getChild("front");
        this.backSide = $$2.getChild("back");
        this.leftSide = $$2.getChild("left");
        this.rightSide = $$2.getChild("right");
    }

    public static LayerDefinition createBaseLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        CubeDeformation $$2 = new CubeDeformation(0.2F);
        CubeDeformation $$3 = new CubeDeformation(-0.1F);
        $$1.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(0, 0).addBox(4.0F, 17.0F, 4.0F, 8.0F, 3.0F, 8.0F, $$3).texOffs(0, 5).addBox(5.0F, 20.0F, 5.0F, 6.0F, 1.0F, 6.0F, $$2), PartPose.offsetAndRotation(0.0F, 37.0F, 16.0F, 3.1415927F, 0.0F, 0.0F));
        CubeListBuilder $$4 = CubeListBuilder.create().texOffs(-14, 13).addBox(0.0F, 0.0F, 0.0F, 14.0F, 0.0F, 14.0F);
        $$1.addOrReplaceChild("top", $$4, PartPose.offsetAndRotation(1.0F, 16.0F, 1.0F, 0.0F, 0.0F, 0.0F));
        $$1.addOrReplaceChild("bottom", $$4, PartPose.offsetAndRotation(1.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F));
        return LayerDefinition.create($$0, 32, 32);
    }

    public static LayerDefinition createSidesLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        CubeListBuilder $$2 = CubeListBuilder.create().texOffs(1, 0).addBox(0.0F, 0.0F, 0.0F, 14.0F, 16.0F, 0.0F, (Set)EnumSet.of(Direction.NORTH));
        $$1.addOrReplaceChild("back", $$2, PartPose.offsetAndRotation(15.0F, 16.0F, 1.0F, 0.0F, 0.0F, 3.1415927F));
        $$1.addOrReplaceChild("left", $$2, PartPose.offsetAndRotation(1.0F, 16.0F, 1.0F, 0.0F, -1.5707964F, 3.1415927F));
        $$1.addOrReplaceChild("right", $$2, PartPose.offsetAndRotation(15.0F, 16.0F, 15.0F, 0.0F, 1.5707964F, 3.1415927F));
        $$1.addOrReplaceChild("front", $$2, PartPose.offsetAndRotation(1.0F, 16.0F, 15.0F, 3.1415927F, 0.0F, 0.0F));
        return LayerDefinition.create($$0, 16, 16);
    }

    @Nullable
    private static Material getMaterial(Item p_272698_) {
        Material $$1 = Sheets.getDecoratedPotMaterial(DecoratedPotPatterns.getResourceKey(p_272698_));
        if ($$1 == null) {
            $$1 = Sheets.getDecoratedPotMaterial(DecoratedPotPatterns.getResourceKey(Items.BRICK));
        }

        return $$1;
    }

    public void render(DecoratedPotBlockEntity p_273776_, float p_273103_, PoseStack p_273455_, MultiBufferSource p_273010_, int p_273407_, int p_273059_) {
        p_273455_.pushPose();
        Direction $$6 = p_273776_.getDirection();
        p_273455_.translate(0.5, 0.0, 0.5);
        p_273455_.mulPose(Axis.YP.rotationDegrees(180.0F - $$6.toYRot()));
        p_273455_.translate(-0.5, 0.0, -0.5);
        VertexConsumer $$7 = this.baseMaterial.buffer(p_273010_, RenderType::entitySolid);
        this.neck.render(p_273455_, $$7, p_273407_, p_273059_);
        this.top.render(p_273455_, $$7, p_273407_, p_273059_);
        this.bottom.render(p_273455_, $$7, p_273407_, p_273059_);
        DecoratedPotBlockEntity.Decorations $$8 = p_273776_.getDecorations();
        this.renderSide(this.frontSide, p_273455_, p_273010_, p_273407_, p_273059_, getMaterial($$8.front()));
        this.renderSide(this.backSide, p_273455_, p_273010_, p_273407_, p_273059_, getMaterial($$8.back()));
        this.renderSide(this.leftSide, p_273455_, p_273010_, p_273407_, p_273059_, getMaterial($$8.left()));
        this.renderSide(this.rightSide, p_273455_, p_273010_, p_273407_, p_273059_, getMaterial($$8.right()));
        p_273455_.popPose();
    }

    private void renderSide(ModelPart p_273495_, PoseStack p_272899_, MultiBufferSource p_273582_, int p_273242_, int p_273108_, @Nullable Material p_273173_) {
        if (p_273173_ == null) {
            p_273173_ = getMaterial(Items.BRICK);
        }

        if (p_273173_ != null) {
            p_273495_.render(p_272899_, p_273173_.buffer(p_273582_, RenderType::entitySolid), p_273242_, p_273108_);
        }

    }
}

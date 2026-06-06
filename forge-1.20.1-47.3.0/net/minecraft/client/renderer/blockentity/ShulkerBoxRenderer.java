//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ShulkerBoxRenderer implements BlockEntityRenderer<ShulkerBoxBlockEntity> {
    private final ShulkerModel<?> model;

    public ShulkerBoxRenderer(BlockEntityRendererProvider.Context p_173626_) {
        this.model = new ShulkerModel(p_173626_.bakeLayer(ModelLayers.SHULKER));
    }

    public void render(ShulkerBoxBlockEntity p_112478_, float p_112479_, PoseStack p_112480_, MultiBufferSource p_112481_, int p_112482_, int p_112483_) {
        Direction $$6 = Direction.UP;
        if (p_112478_.hasLevel()) {
            BlockState $$7 = p_112478_.getLevel().getBlockState(p_112478_.getBlockPos());
            if ($$7.getBlock() instanceof ShulkerBoxBlock) {
                $$6 = (Direction)$$7.getValue(ShulkerBoxBlock.FACING);
            }
        }

        DyeColor $$8 = p_112478_.getColor();
        Material $$10;
        if ($$8 == null) {
            $$10 = Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION;
        } else {
            $$10 = (Material)Sheets.SHULKER_TEXTURE_LOCATION.get($$8.getId());
        }

        p_112480_.pushPose();
        p_112480_.translate(0.5F, 0.5F, 0.5F);
        float $$11 = 0.9995F;
        p_112480_.scale(0.9995F, 0.9995F, 0.9995F);
        p_112480_.mulPose($$6.getRotation());
        p_112480_.scale(1.0F, -1.0F, -1.0F);
        p_112480_.translate(0.0F, -1.0F, 0.0F);
        ModelPart $$12 = this.model.getLid();
        $$12.setPos(0.0F, 24.0F - p_112478_.getProgress(p_112479_) * 0.5F * 16.0F, 0.0F);
        $$12.yRot = 270.0F * p_112478_.getProgress(p_112479_) * 0.017453292F;
        VertexConsumer $$13 = $$10.buffer(p_112481_, RenderType::entityCutoutNoCull);
        this.model.renderToBuffer(p_112480_, $$13, p_112482_, p_112483_, 1.0F, 1.0F, 1.0F, 1.0F);
        p_112480_.popPose();
    }
}

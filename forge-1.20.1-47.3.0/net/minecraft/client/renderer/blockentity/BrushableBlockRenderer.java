//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BrushableBlockRenderer implements BlockEntityRenderer<BrushableBlockEntity> {
    private final ItemRenderer itemRenderer;

    public BrushableBlockRenderer(BlockEntityRendererProvider.Context p_277899_) {
        this.itemRenderer = p_277899_.getItemRenderer();
    }

    public void render(BrushableBlockEntity p_277712_, float p_277981_, PoseStack p_277490_, MultiBufferSource p_278015_, int p_277463_, int p_277346_) {
        if (p_277712_.getLevel() != null) {
            int $$6 = (Integer)p_277712_.getBlockState().getValue(BlockStateProperties.DUSTED);
            if ($$6 > 0) {
                Direction $$7 = p_277712_.getHitDirection();
                if ($$7 != null) {
                    ItemStack $$8 = p_277712_.getItem();
                    if (!$$8.isEmpty()) {
                        p_277490_.pushPose();
                        p_277490_.translate(0.0F, 0.5F, 0.0F);
                        float[] $$9 = this.translations($$7, $$6);
                        p_277490_.translate($$9[0], $$9[1], $$9[2]);
                        p_277490_.mulPose(Axis.YP.rotationDegrees(75.0F));
                        boolean $$10 = $$7 == Direction.EAST || $$7 == Direction.WEST;
                        p_277490_.mulPose(Axis.YP.rotationDegrees((float)(($$10 ? 90 : 0) + 11)));
                        p_277490_.scale(0.5F, 0.5F, 0.5F);
                        int $$11 = LevelRenderer.getLightColor(p_277712_.getLevel(), p_277712_.getBlockState(), p_277712_.getBlockPos().relative($$7));
                        this.itemRenderer.renderStatic($$8, ItemDisplayContext.FIXED, $$11, OverlayTexture.NO_OVERLAY, p_277490_, p_278015_, p_277712_.getLevel(), 0);
                        p_277490_.popPose();
                    }
                }
            }
        }
    }

    private float[] translations(Direction p_278030_, int p_277997_) {
        float[] $$2 = new float[]{0.5F, 0.0F, 0.5F};
        float $$3 = (float)p_277997_ / 10.0F * 0.75F;
        switch (p_278030_) {
            case EAST -> $$2[0] = 0.73F + $$3;
            case WEST -> $$2[0] = 0.25F - $$3;
            case UP -> $$2[1] = 0.25F + $$3;
            case DOWN -> $$2[1] = -0.23F - $$3;
            case NORTH -> $$2[2] = 0.25F - $$3;
            case SOUTH -> $$2[2] = 0.73F + $$3;
        }

        return $$2;
    }
}

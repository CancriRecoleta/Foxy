//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StructureBlockRenderer implements BlockEntityRenderer<StructureBlockEntity> {
    public StructureBlockRenderer(BlockEntityRendererProvider.Context p_173675_) {
    }

    public void render(StructureBlockEntity p_112583_, float p_112584_, PoseStack p_112585_, MultiBufferSource p_112586_, int p_112587_, int p_112588_) {
        if (Minecraft.getInstance().player.canUseGameMasterBlocks() || Minecraft.getInstance().player.isSpectator()) {
            BlockPos $$6 = p_112583_.getStructurePos();
            Vec3i $$7 = p_112583_.getStructureSize();
            if ($$7.getX() >= 1 && $$7.getY() >= 1 && $$7.getZ() >= 1) {
                if (p_112583_.getMode() == StructureMode.SAVE || p_112583_.getMode() == StructureMode.LOAD) {
                    double $$8 = (double)$$6.getX();
                    double $$9 = (double)$$6.getZ();
                    double $$10 = (double)$$6.getY();
                    double $$11 = $$10 + (double)$$7.getY();
                    double $$16;
                    double $$17;
                    switch (p_112583_.getMirror()) {
                        case LEFT_RIGHT:
                            $$16 = (double)$$7.getX();
                            $$17 = (double)(-$$7.getZ());
                            break;
                        case FRONT_BACK:
                            $$16 = (double)(-$$7.getX());
                            $$17 = (double)$$7.getZ();
                            break;
                        default:
                            $$16 = (double)$$7.getX();
                            $$17 = (double)$$7.getZ();
                    }

                    double $$30;
                    double $$31;
                    double $$32;
                    double $$33;
                    switch (p_112583_.getRotation()) {
                        case CLOCKWISE_90:
                            $$30 = $$17 < 0.0 ? $$8 : $$8 + 1.0;
                            $$31 = $$16 < 0.0 ? $$9 + 1.0 : $$9;
                            $$32 = $$30 - $$17;
                            $$33 = $$31 + $$16;
                            break;
                        case CLOCKWISE_180:
                            $$30 = $$16 < 0.0 ? $$8 : $$8 + 1.0;
                            $$31 = $$17 < 0.0 ? $$9 : $$9 + 1.0;
                            $$32 = $$30 - $$16;
                            $$33 = $$31 - $$17;
                            break;
                        case COUNTERCLOCKWISE_90:
                            $$30 = $$17 < 0.0 ? $$8 + 1.0 : $$8;
                            $$31 = $$16 < 0.0 ? $$9 : $$9 + 1.0;
                            $$32 = $$30 + $$17;
                            $$33 = $$31 - $$16;
                            break;
                        default:
                            $$30 = $$16 < 0.0 ? $$8 + 1.0 : $$8;
                            $$31 = $$17 < 0.0 ? $$9 + 1.0 : $$9;
                            $$32 = $$30 + $$16;
                            $$33 = $$31 + $$17;
                    }

                    float $$34 = 1.0F;
                    float $$35 = 0.9F;
                    float $$36 = 0.5F;
                    VertexConsumer $$37 = p_112586_.getBuffer(RenderType.lines());
                    if (p_112583_.getMode() == StructureMode.SAVE || p_112583_.getShowBoundingBox()) {
                        LevelRenderer.renderLineBox(p_112585_, $$37, $$30, $$10, $$31, $$32, $$11, $$33, 0.9F, 0.9F, 0.9F, 1.0F, 0.5F, 0.5F, 0.5F);
                    }

                    if (p_112583_.getMode() == StructureMode.SAVE && p_112583_.getShowAir()) {
                        this.renderInvisibleBlocks(p_112583_, $$37, $$6, p_112585_);
                    }

                }
            }
        }
    }

    private void renderInvisibleBlocks(StructureBlockEntity p_173677_, VertexConsumer p_173678_, BlockPos p_173679_, PoseStack p_173680_) {
        BlockGetter $$4 = p_173677_.getLevel();
        BlockPos $$5 = p_173677_.getBlockPos();
        BlockPos $$6 = $$5.offset(p_173679_);
        Iterator var8 = BlockPos.betweenClosed($$6, $$6.offset(p_173677_.getStructureSize()).offset(-1, -1, -1)).iterator();

        while(true) {
            BlockPos $$7;
            boolean $$9;
            boolean $$10;
            boolean $$11;
            boolean $$12;
            boolean $$13;
            do {
                if (!var8.hasNext()) {
                    return;
                }

                $$7 = (BlockPos)var8.next();
                BlockState $$8 = $$4.getBlockState($$7);
                $$9 = $$8.isAir();
                $$10 = $$8.is(Blocks.STRUCTURE_VOID);
                $$11 = $$8.is(Blocks.BARRIER);
                $$12 = $$8.is(Blocks.LIGHT);
                $$13 = $$10 || $$11 || $$12;
            } while(!$$9 && !$$13);

            float $$14 = $$9 ? 0.05F : 0.0F;
            double $$15 = (double)((float)($$7.getX() - $$5.getX()) + 0.45F - $$14);
            double $$16 = (double)((float)($$7.getY() - $$5.getY()) + 0.45F - $$14);
            double $$17 = (double)((float)($$7.getZ() - $$5.getZ()) + 0.45F - $$14);
            double $$18 = (double)((float)($$7.getX() - $$5.getX()) + 0.55F + $$14);
            double $$19 = (double)((float)($$7.getY() - $$5.getY()) + 0.55F + $$14);
            double $$20 = (double)((float)($$7.getZ() - $$5.getZ()) + 0.55F + $$14);
            if ($$9) {
                LevelRenderer.renderLineBox(p_173680_, p_173678_, $$15, $$16, $$17, $$18, $$19, $$20, 0.5F, 0.5F, 1.0F, 1.0F, 0.5F, 0.5F, 1.0F);
            } else if ($$10) {
                LevelRenderer.renderLineBox(p_173680_, p_173678_, $$15, $$16, $$17, $$18, $$19, $$20, 1.0F, 0.75F, 0.75F, 1.0F, 1.0F, 0.75F, 0.75F);
            } else if ($$11) {
                LevelRenderer.renderLineBox(p_173680_, p_173678_, $$15, $$16, $$17, $$18, $$19, $$20, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F);
            } else if ($$12) {
                LevelRenderer.renderLineBox(p_173680_, p_173678_, $$15, $$16, $$17, $$18, $$19, $$20, 1.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F);
            }
        }
    }

    public boolean shouldRenderOffScreen(StructureBlockEntity p_112581_) {
        return true;
    }

    public int getViewDistance() {
        return 96;
    }
}

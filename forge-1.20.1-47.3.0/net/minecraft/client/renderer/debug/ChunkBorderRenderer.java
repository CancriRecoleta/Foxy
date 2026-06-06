//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class ChunkBorderRenderer implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;
    private static final int CELL_BORDER = ARGB32.color(255, 0, 155, 155);
    private static final int YELLOW = ARGB32.color(255, 255, 255, 0);

    public ChunkBorderRenderer(Minecraft p_113356_) {
        this.minecraft = p_113356_;
    }

    public void render(PoseStack p_113358_, MultiBufferSource p_113359_, double p_113360_, double p_113361_, double p_113362_) {
        Entity $$5 = this.minecraft.gameRenderer.getMainCamera().getEntity();
        float $$6 = (float)((double)this.minecraft.level.getMinBuildHeight() - p_113361_);
        float $$7 = (float)((double)this.minecraft.level.getMaxBuildHeight() - p_113361_);
        ChunkPos $$8 = $$5.chunkPosition();
        float $$9 = (float)((double)$$8.getMinBlockX() - p_113360_);
        float $$10 = (float)((double)$$8.getMinBlockZ() - p_113362_);
        VertexConsumer $$11 = p_113359_.getBuffer(RenderType.debugLineStrip(1.0));
        Matrix4f $$12 = p_113358_.last().pose();

        int $$24;
        int $$23;
        for($$24 = -16; $$24 <= 32; $$24 += 16) {
            for($$23 = -16; $$23 <= 32; $$23 += 16) {
                $$11.vertex($$12, $$9 + (float)$$24, $$6, $$10 + (float)$$23).color(1.0F, 0.0F, 0.0F, 0.0F).endVertex();
                $$11.vertex($$12, $$9 + (float)$$24, $$6, $$10 + (float)$$23).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                $$11.vertex($$12, $$9 + (float)$$24, $$7, $$10 + (float)$$23).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                $$11.vertex($$12, $$9 + (float)$$24, $$7, $$10 + (float)$$23).color(1.0F, 0.0F, 0.0F, 0.0F).endVertex();
            }
        }

        for($$24 = 2; $$24 < 16; $$24 += 2) {
            $$23 = $$24 % 4 == 0 ? CELL_BORDER : YELLOW;
            $$11.vertex($$12, $$9 + (float)$$24, $$6, $$10).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
            $$11.vertex($$12, $$9 + (float)$$24, $$6, $$10).color($$23).endVertex();
            $$11.vertex($$12, $$9 + (float)$$24, $$7, $$10).color($$23).endVertex();
            $$11.vertex($$12, $$9 + (float)$$24, $$7, $$10).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
            $$11.vertex($$12, $$9 + (float)$$24, $$6, $$10 + 16.0F).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
            $$11.vertex($$12, $$9 + (float)$$24, $$6, $$10 + 16.0F).color($$23).endVertex();
            $$11.vertex($$12, $$9 + (float)$$24, $$7, $$10 + 16.0F).color($$23).endVertex();
            $$11.vertex($$12, $$9 + (float)$$24, $$7, $$10 + 16.0F).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
        }

        for($$24 = 2; $$24 < 16; $$24 += 2) {
            $$23 = $$24 % 4 == 0 ? CELL_BORDER : YELLOW;
            $$11.vertex($$12, $$9, $$6, $$10 + (float)$$24).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
            $$11.vertex($$12, $$9, $$6, $$10 + (float)$$24).color($$23).endVertex();
            $$11.vertex($$12, $$9, $$7, $$10 + (float)$$24).color($$23).endVertex();
            $$11.vertex($$12, $$9, $$7, $$10 + (float)$$24).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
            $$11.vertex($$12, $$9 + 16.0F, $$6, $$10 + (float)$$24).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
            $$11.vertex($$12, $$9 + 16.0F, $$6, $$10 + (float)$$24).color($$23).endVertex();
            $$11.vertex($$12, $$9 + 16.0F, $$7, $$10 + (float)$$24).color($$23).endVertex();
            $$11.vertex($$12, $$9 + 16.0F, $$7, $$10 + (float)$$24).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
        }

        float $$25;
        for($$24 = this.minecraft.level.getMinBuildHeight(); $$24 <= this.minecraft.level.getMaxBuildHeight(); $$24 += 2) {
            $$25 = (float)((double)$$24 - p_113361_);
            int $$21 = $$24 % 8 == 0 ? CELL_BORDER : YELLOW;
            $$11.vertex($$12, $$9, $$25, $$10).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
            $$11.vertex($$12, $$9, $$25, $$10).color($$21).endVertex();
            $$11.vertex($$12, $$9, $$25, $$10 + 16.0F).color($$21).endVertex();
            $$11.vertex($$12, $$9 + 16.0F, $$25, $$10 + 16.0F).color($$21).endVertex();
            $$11.vertex($$12, $$9 + 16.0F, $$25, $$10).color($$21).endVertex();
            $$11.vertex($$12, $$9, $$25, $$10).color($$21).endVertex();
            $$11.vertex($$12, $$9, $$25, $$10).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
        }

        $$11 = p_113359_.getBuffer(RenderType.debugLineStrip(2.0));

        for($$24 = 0; $$24 <= 16; $$24 += 16) {
            for($$23 = 0; $$23 <= 16; $$23 += 16) {
                $$11.vertex($$12, $$9 + (float)$$24, $$6, $$10 + (float)$$23).color(0.25F, 0.25F, 1.0F, 0.0F).endVertex();
                $$11.vertex($$12, $$9 + (float)$$24, $$6, $$10 + (float)$$23).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
                $$11.vertex($$12, $$9 + (float)$$24, $$7, $$10 + (float)$$23).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
                $$11.vertex($$12, $$9 + (float)$$24, $$7, $$10 + (float)$$23).color(0.25F, 0.25F, 1.0F, 0.0F).endVertex();
            }
        }

        for($$24 = this.minecraft.level.getMinBuildHeight(); $$24 <= this.minecraft.level.getMaxBuildHeight(); $$24 += 16) {
            $$25 = (float)((double)$$24 - p_113361_);
            $$11.vertex($$12, $$9, $$25, $$10).color(0.25F, 0.25F, 1.0F, 0.0F).endVertex();
            $$11.vertex($$12, $$9, $$25, $$10).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
            $$11.vertex($$12, $$9, $$25, $$10 + 16.0F).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
            $$11.vertex($$12, $$9 + 16.0F, $$25, $$10 + 16.0F).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
            $$11.vertex($$12, $$9 + 16.0F, $$25, $$10).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
            $$11.vertex($$12, $$9, $$25, $$10).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
            $$11.vertex($$12, $$9, $$25, $$10).color(0.25F, 0.25F, 1.0F, 0.0F).endVertex();
        }

    }
}

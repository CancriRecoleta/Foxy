//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class SolidFaceRenderer implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;

    public SolidFaceRenderer(Minecraft p_113668_) {
        this.minecraft = p_113668_;
    }

    public void render(PoseStack p_113670_, MultiBufferSource p_113671_, double p_113672_, double p_113673_, double p_113674_) {
        Matrix4f $$5 = p_113670_.last().pose();
        BlockGetter $$6 = this.minecraft.player.level();
        BlockPos $$7 = BlockPos.containing(p_113672_, p_113673_, p_113674_);
        Iterator var12 = BlockPos.betweenClosed($$7.offset(-6, -6, -6), $$7.offset(6, 6, 6)).iterator();

        while(true) {
            BlockPos $$8;
            BlockState $$9;
            do {
                if (!var12.hasNext()) {
                    return;
                }

                $$8 = (BlockPos)var12.next();
                $$9 = $$6.getBlockState($$8);
            } while($$9.is(Blocks.AIR));

            VoxelShape $$10 = $$9.getShape($$6, $$8);
            Iterator var16 = $$10.toAabbs().iterator();

            while(var16.hasNext()) {
                AABB $$11 = (AABB)var16.next();
                AABB $$12 = $$11.move($$8).inflate(0.002);
                float $$13 = (float)($$12.minX - p_113672_);
                float $$14 = (float)($$12.minY - p_113673_);
                float $$15 = (float)($$12.minZ - p_113674_);
                float $$16 = (float)($$12.maxX - p_113672_);
                float $$17 = (float)($$12.maxY - p_113673_);
                float $$18 = (float)($$12.maxZ - p_113674_);
                float $$19 = 1.0F;
                float $$20 = 0.0F;
                float $$21 = 0.0F;
                float $$22 = 0.5F;
                VertexConsumer $$28;
                if ($$9.isFaceSturdy($$6, $$8, Direction.WEST)) {
                    $$28 = p_113671_.getBuffer(RenderType.debugFilledBox());
                    $$28.vertex($$5, $$13, $$14, $$15).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                    $$28.vertex($$5, $$13, $$14, $$18).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                    $$28.vertex($$5, $$13, $$17, $$15).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                    $$28.vertex($$5, $$13, $$17, $$18).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                }

                if ($$9.isFaceSturdy($$6, $$8, Direction.SOUTH)) {
                    $$28 = p_113671_.getBuffer(RenderType.debugFilledBox());
                    $$28.vertex($$5, $$13, $$17, $$18).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                    $$28.vertex($$5, $$13, $$14, $$18).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                    $$28.vertex($$5, $$16, $$17, $$18).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                    $$28.vertex($$5, $$16, $$14, $$18).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                }

                if ($$9.isFaceSturdy($$6, $$8, Direction.EAST)) {
                    $$28 = p_113671_.getBuffer(RenderType.debugFilledBox());
                    $$28.vertex($$5, $$16, $$14, $$18).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                    $$28.vertex($$5, $$16, $$14, $$15).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                    $$28.vertex($$5, $$16, $$17, $$18).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                    $$28.vertex($$5, $$16, $$17, $$15).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                }

                if ($$9.isFaceSturdy($$6, $$8, Direction.NORTH)) {
                    $$28 = p_113671_.getBuffer(RenderType.debugFilledBox());
                    $$28.vertex($$5, $$16, $$17, $$15).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                    $$28.vertex($$5, $$16, $$14, $$15).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                    $$28.vertex($$5, $$13, $$17, $$15).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                    $$28.vertex($$5, $$13, $$14, $$15).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                }

                if ($$9.isFaceSturdy($$6, $$8, Direction.DOWN)) {
                    $$28 = p_113671_.getBuffer(RenderType.debugFilledBox());
                    $$28.vertex($$5, $$13, $$14, $$15).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                    $$28.vertex($$5, $$16, $$14, $$15).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                    $$28.vertex($$5, $$13, $$14, $$18).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                    $$28.vertex($$5, $$16, $$14, $$18).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                }

                if ($$9.isFaceSturdy($$6, $$8, Direction.UP)) {
                    $$28 = p_113671_.getBuffer(RenderType.debugFilledBox());
                    $$28.vertex($$5, $$13, $$17, $$15).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                    $$28.vertex($$5, $$13, $$17, $$18).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                    $$28.vertex($$5, $$16, $$17, $$15).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                    $$28.vertex($$5, $$16, $$17, $$18).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                }
            }
        }
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WaterDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;

    public WaterDebugRenderer(Minecraft p_113717_) {
        this.minecraft = p_113717_;
    }

    public void render(PoseStack p_113719_, MultiBufferSource p_113720_, double p_113721_, double p_113722_, double p_113723_) {
        BlockPos $$5 = this.minecraft.player.blockPosition();
        LevelReader $$6 = this.minecraft.player.level();
        Iterator var11 = BlockPos.betweenClosed($$5.offset(-10, -10, -10), $$5.offset(10, 10, 10)).iterator();

        BlockPos $$10;
        FluidState $$11;
        while(var11.hasNext()) {
            $$10 = (BlockPos)var11.next();
            $$11 = $$6.getFluidState($$10);
            if ($$11.is(FluidTags.WATER)) {
                double $$9 = (double)((float)$$10.getY() + $$11.getHeight($$6, $$10));
                DebugRenderer.renderFilledBox(p_113719_, p_113720_, (new AABB((double)((float)$$10.getX() + 0.01F), (double)((float)$$10.getY() + 0.01F), (double)((float)$$10.getZ() + 0.01F), (double)((float)$$10.getX() + 0.99F), $$9, (double)((float)$$10.getZ() + 0.99F))).move(-p_113721_, -p_113722_, -p_113723_), 0.0F, 1.0F, 0.0F, 0.15F);
            }
        }

        var11 = BlockPos.betweenClosed($$5.offset(-10, -10, -10), $$5.offset(10, 10, 10)).iterator();

        while(var11.hasNext()) {
            $$10 = (BlockPos)var11.next();
            $$11 = $$6.getFluidState($$10);
            if ($$11.is(FluidTags.WATER)) {
                DebugRenderer.renderFloatingText(p_113719_, p_113720_, String.valueOf($$11.getAmount()), (double)$$10.getX() + 0.5, (double)((float)$$10.getY() + $$11.getHeight($$6, $$10)), (double)$$10.getZ() + 0.5, -16777216);
            }
        }

    }
}

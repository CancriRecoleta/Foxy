//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LightDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;
    private static final int MAX_RENDER_DIST = 10;

    public LightDebugRenderer(Minecraft p_113585_) {
        this.minecraft = p_113585_;
    }

    public void render(PoseStack p_113587_, MultiBufferSource p_113588_, double p_113589_, double p_113590_, double p_113591_) {
        Level $$5 = this.minecraft.level;
        BlockPos $$6 = BlockPos.containing(p_113589_, p_113590_, p_113591_);
        LongSet $$7 = new LongOpenHashSet();
        Iterator var12 = BlockPos.betweenClosed($$6.offset(-10, -10, -10), $$6.offset(10, 10, 10)).iterator();

        while(var12.hasNext()) {
            BlockPos $$8 = (BlockPos)var12.next();
            int $$9 = $$5.getBrightness(LightLayer.SKY, $$8);
            float $$10 = (float)(15 - $$9) / 15.0F * 0.5F + 0.16F;
            int $$11 = Mth.hsvToRgb($$10, 0.9F, 0.9F);
            long $$12 = SectionPos.blockToSection($$8.asLong());
            if ($$7.add($$12)) {
                DebugRenderer.renderFloatingText(p_113587_, p_113588_, $$5.getChunkSource().getLightEngine().getDebugData(LightLayer.SKY, SectionPos.of($$12)), (double)SectionPos.sectionToBlockCoord(SectionPos.x($$12), 8), (double)SectionPos.sectionToBlockCoord(SectionPos.y($$12), 8), (double)SectionPos.sectionToBlockCoord(SectionPos.z($$12), 8), 16711680, 0.3F);
            }

            if ($$9 != 15) {
                DebugRenderer.renderFloatingText(p_113587_, p_113588_, String.valueOf($$9), (double)$$8.getX() + 0.5, (double)$$8.getY() + 0.25, (double)$$8.getZ() + 0.5, $$11);
            }
        }

    }
}

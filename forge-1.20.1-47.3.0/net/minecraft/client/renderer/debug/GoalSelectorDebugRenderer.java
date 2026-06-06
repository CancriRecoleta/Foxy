//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GoalSelectorDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
    private static final int MAX_RENDER_DIST = 160;
    private final Minecraft minecraft;
    private final Map<Integer, List<DebugGoal>> goalSelectors = Maps.newHashMap();

    public void clear() {
        this.goalSelectors.clear();
    }

    public void addGoalSelector(int p_113549_, List<DebugGoal> p_113550_) {
        this.goalSelectors.put(p_113549_, p_113550_);
    }

    public void removeGoalSelector(int p_173889_) {
        this.goalSelectors.remove(p_173889_);
    }

    public GoalSelectorDebugRenderer(Minecraft p_113546_) {
        this.minecraft = p_113546_;
    }

    public void render(PoseStack p_113552_, MultiBufferSource p_113553_, double p_113554_, double p_113555_, double p_113556_) {
        Camera $$5 = this.minecraft.gameRenderer.getMainCamera();
        BlockPos $$6 = BlockPos.containing($$5.getPosition().x, 0.0, $$5.getPosition().z);
        this.goalSelectors.forEach((p_269742_, p_269743_) -> {
            for(int $$5 = 0; $$5 < p_269743_.size(); ++$$5) {
                DebugGoal $$6x = (DebugGoal)p_269743_.get($$5);
                if ($$6.closerThan($$6x.pos, 160.0)) {
                    double $$7 = (double)$$6x.pos.getX() + 0.5;
                    double $$8 = (double)$$6x.pos.getY() + 2.0 + (double)$$5 * 0.25;
                    double $$9 = (double)$$6x.pos.getZ() + 0.5;
                    int $$10 = $$6x.isRunning ? -16711936 : -3355444;
                    DebugRenderer.renderFloatingText(p_113552_, p_113553_, $$6x.name, $$7, $$8, $$9, $$10);
                }
            }

        });
    }

    @OnlyIn(Dist.CLIENT)
    public static class DebugGoal {
        public final BlockPos pos;
        public final int priority;
        public final String name;
        public final boolean isRunning;

        public DebugGoal(BlockPos p_113566_, int p_113567_, String p_113568_, boolean p_113569_) {
            this.pos = p_113566_;
            this.priority = p_113567_;
            this.name = p_113568_;
            this.isRunning = p_113569_;
        }
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;

public class TradeWithPlayerGoal extends Goal {
    private final AbstractVillager mob;

    public TradeWithPlayerGoal(AbstractVillager p_25958_) {
        this.mob = p_25958_;
        this.setFlags(EnumSet.of(net.minecraft.world.entity.ai.goal.Goal.Flag.JUMP, net.minecraft.world.entity.ai.goal.Goal.Flag.MOVE));
    }

    public boolean canUse() {
        if (!this.mob.isAlive()) {
            return false;
        } else if (this.mob.isInWater()) {
            return false;
        } else if (!this.mob.onGround()) {
            return false;
        } else if (this.mob.hurtMarked) {
            return false;
        } else {
            Player $$0 = this.mob.getTradingPlayer();
            if ($$0 == null) {
                return false;
            } else if (this.mob.distanceToSqr($$0) > 16.0) {
                return false;
            } else {
                return $$0.containerMenu != null;
            }
        }
    }

    public void start() {
        this.mob.getNavigation().stop();
    }

    public void stop() {
        this.mob.setTradingPlayer((Player)null);
    }
}

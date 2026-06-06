//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;

public abstract class ContainerOpenersCounter {
    private static final int CHECK_TICK_DELAY = 5;
    private int openCount;

    public ContainerOpenersCounter() {
    }

    protected abstract void onOpen(Level var1, BlockPos var2, BlockState var3);

    protected abstract void onClose(Level var1, BlockPos var2, BlockState var3);

    protected abstract void openerCountChanged(Level var1, BlockPos var2, BlockState var3, int var4, int var5);

    protected abstract boolean isOwnContainer(Player var1);

    public void incrementOpeners(Player p_155453_, Level p_155454_, BlockPos p_155455_, BlockState p_155456_) {
        int $$4 = this.openCount++;
        if ($$4 == 0) {
            this.onOpen(p_155454_, p_155455_, p_155456_);
            p_155454_.gameEvent(p_155453_, GameEvent.CONTAINER_OPEN, p_155455_);
            scheduleRecheck(p_155454_, p_155455_, p_155456_);
        }

        this.openerCountChanged(p_155454_, p_155455_, p_155456_, $$4, this.openCount);
    }

    public void decrementOpeners(Player p_155469_, Level p_155470_, BlockPos p_155471_, BlockState p_155472_) {
        int $$4 = this.openCount--;
        if (this.openCount == 0) {
            this.onClose(p_155470_, p_155471_, p_155472_);
            p_155470_.gameEvent(p_155469_, GameEvent.CONTAINER_CLOSE, p_155471_);
        }

        this.openerCountChanged(p_155470_, p_155471_, p_155472_, $$4, this.openCount);
    }

    private int getOpenCount(Level p_155458_, BlockPos p_155459_) {
        int $$2 = p_155459_.getX();
        int $$3 = p_155459_.getY();
        int $$4 = p_155459_.getZ();
        float $$5 = 5.0F;
        AABB $$6 = new AABB((double)((float)$$2 - 5.0F), (double)((float)$$3 - 5.0F), (double)((float)$$4 - 5.0F), (double)((float)($$2 + 1) + 5.0F), (double)((float)($$3 + 1) + 5.0F), (double)((float)($$4 + 1) + 5.0F));
        return p_155458_.getEntities(EntityTypeTest.forClass(Player.class), $$6, this::isOwnContainer).size();
    }

    public void recheckOpeners(Level p_155477_, BlockPos p_155478_, BlockState p_155479_) {
        int $$3 = this.getOpenCount(p_155477_, p_155478_);
        int $$4 = this.openCount;
        if ($$4 != $$3) {
            boolean $$5 = $$3 != 0;
            boolean $$6 = $$4 != 0;
            if ($$5 && !$$6) {
                this.onOpen(p_155477_, p_155478_, p_155479_);
                p_155477_.gameEvent((Entity)null, GameEvent.CONTAINER_OPEN, p_155478_);
            } else if (!$$5) {
                this.onClose(p_155477_, p_155478_, p_155479_);
                p_155477_.gameEvent((Entity)null, GameEvent.CONTAINER_CLOSE, p_155478_);
            }

            this.openCount = $$3;
        }

        this.openerCountChanged(p_155477_, p_155478_, p_155479_, $$4, $$3);
        if ($$3 > 0) {
            scheduleRecheck(p_155477_, p_155478_, p_155479_);
        }

    }

    public int getOpenerCount() {
        return this.openCount;
    }

    private static void scheduleRecheck(Level p_155481_, BlockPos p_155482_, BlockState p_155483_) {
        p_155481_.scheduleTick(p_155482_, p_155483_.getBlock(), 5);
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.Vec3;

public abstract class PathfinderMob extends Mob {
    protected static final float DEFAULT_WALK_TARGET_VALUE = 0.0F;

    protected PathfinderMob(EntityType<? extends PathfinderMob> p_21683_, Level p_21684_) {
        super(p_21683_, p_21684_);
    }

    public float getWalkTargetValue(BlockPos p_21693_) {
        return this.getWalkTargetValue(p_21693_, this.level());
    }

    public float getWalkTargetValue(BlockPos p_21688_, LevelReader p_21689_) {
        return 0.0F;
    }

    public boolean checkSpawnRules(LevelAccessor p_21686_, MobSpawnType p_21687_) {
        return this.getWalkTargetValue(this.blockPosition(), p_21686_) >= 0.0F;
    }

    public boolean isPathFinding() {
        return !this.getNavigation().isDone();
    }

    protected void tickLeash() {
        super.tickLeash();
        Entity $$0 = this.getLeashHolder();
        if ($$0 != null && $$0.level() == this.level()) {
            this.restrictTo($$0.blockPosition(), 5);
            float $$1 = this.distanceTo($$0);
            if (this instanceof TamableAnimal && ((TamableAnimal)this).isInSittingPose()) {
                if ($$1 > 10.0F) {
                    this.dropLeash(true, true);
                }

                return;
            }

            this.onLeashDistance($$1);
            if ($$1 > 10.0F) {
                this.dropLeash(true, true);
                this.goalSelector.disableControlFlag(Flag.MOVE);
            } else if ($$1 > 6.0F) {
                double $$2 = ($$0.getX() - this.getX()) / (double)$$1;
                double $$3 = ($$0.getY() - this.getY()) / (double)$$1;
                double $$4 = ($$0.getZ() - this.getZ()) / (double)$$1;
                this.setDeltaMovement(this.getDeltaMovement().add(Math.copySign($$2 * $$2 * 0.4, $$2), Math.copySign($$3 * $$3 * 0.4, $$3), Math.copySign($$4 * $$4 * 0.4, $$4)));
                this.checkSlowFallDistance();
            } else if (this.shouldStayCloseToLeashHolder()) {
                this.goalSelector.enableControlFlag(Flag.MOVE);
                float $$5 = 2.0F;
                Vec3 $$6 = (new Vec3($$0.getX() - this.getX(), $$0.getY() - this.getY(), $$0.getZ() - this.getZ())).normalize().scale((double)Math.max($$1 - 2.0F, 0.0F));
                this.getNavigation().moveTo(this.getX() + $$6.x, this.getY() + $$6.y, this.getZ() + $$6.z, this.followLeashSpeed());
            }
        }

    }

    protected boolean shouldStayCloseToLeashHolder() {
        return true;
    }

    protected double followLeashSpeed() {
        return 1.0;
    }

    protected void onLeashDistance(float p_21694_) {
    }
}

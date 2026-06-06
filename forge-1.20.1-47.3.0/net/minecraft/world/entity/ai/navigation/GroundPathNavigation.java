//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.entity.ai.navigation;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;

public class GroundPathNavigation extends PathNavigation {
    private boolean avoidSun;

    public GroundPathNavigation(Mob p_26448_, Level p_26449_) {
        super(p_26448_, p_26449_);
    }

    protected PathFinder createPathFinder(int p_26453_) {
        this.nodeEvaluator = new WalkNodeEvaluator();
        this.nodeEvaluator.setCanPassDoors(true);
        return new PathFinder(this.nodeEvaluator, p_26453_);
    }

    protected boolean canUpdatePath() {
        return this.mob.onGround() || this.isInLiquid() || this.mob.isPassenger();
    }

    protected Vec3 getTempMobPos() {
        return new Vec3(this.mob.getX(), (double)this.getSurfaceY(), this.mob.getZ());
    }

    public Path createPath(BlockPos p_26475_, int p_26476_) {
        BlockPos $$3;
        if (this.level.getBlockState(p_26475_).isAir()) {
            for($$3 = p_26475_.below(); $$3.getY() > this.level.getMinBuildHeight() && this.level.getBlockState($$3).isAir(); $$3 = $$3.below()) {
            }

            if ($$3.getY() > this.level.getMinBuildHeight()) {
                return super.createPath($$3.above(), p_26476_);
            }

            while($$3.getY() < this.level.getMaxBuildHeight() && this.level.getBlockState($$3).isAir()) {
                $$3 = $$3.above();
            }

            p_26475_ = $$3;
        }

        if (!this.level.getBlockState(p_26475_).isSolid()) {
            return super.createPath(p_26475_, p_26476_);
        } else {
            for($$3 = p_26475_.above(); $$3.getY() < this.level.getMaxBuildHeight() && this.level.getBlockState($$3).isSolid(); $$3 = $$3.above()) {
            }

            return super.createPath($$3, p_26476_);
        }
    }

    public Path createPath(Entity p_26465_, int p_26466_) {
        return this.createPath(p_26465_.blockPosition(), p_26466_);
    }

    private int getSurfaceY() {
        if (this.mob.isInWater() && this.canFloat()) {
            int $$0 = this.mob.getBlockY();
            BlockState $$1 = this.level.getBlockState(BlockPos.containing(this.mob.getX(), (double)$$0, this.mob.getZ()));
            int $$2 = 0;

            do {
                if (!$$1.is(Blocks.WATER)) {
                    return $$0;
                }

                ++$$0;
                $$1 = this.level.getBlockState(BlockPos.containing(this.mob.getX(), (double)$$0, this.mob.getZ()));
                ++$$2;
            } while($$2 <= 16);

            return this.mob.getBlockY();
        } else {
            return Mth.floor(this.mob.getY() + 0.5);
        }
    }

    protected void trimPath() {
        super.trimPath();
        if (this.avoidSun) {
            if (this.level.canSeeSky(BlockPos.containing(this.mob.getX(), this.mob.getY() + 0.5, this.mob.getZ()))) {
                return;
            }

            for(int $$0 = 0; $$0 < this.path.getNodeCount(); ++$$0) {
                Node $$1 = this.path.getNode($$0);
                if (this.level.canSeeSky(new BlockPos($$1.x, $$1.y, $$1.z))) {
                    this.path.truncateNodes($$0);
                    return;
                }
            }
        }

    }

    protected boolean hasValidPathType(BlockPathTypes p_26467_) {
        if (p_26467_ == BlockPathTypes.WATER) {
            return false;
        } else if (p_26467_ == BlockPathTypes.LAVA) {
            return false;
        } else {
            return p_26467_ != BlockPathTypes.OPEN;
        }
    }

    public void setCanOpenDoors(boolean p_26478_) {
        this.nodeEvaluator.setCanOpenDoors(p_26478_);
    }

    public boolean canPassDoors() {
        return this.nodeEvaluator.canPassDoors();
    }

    public void setCanPassDoors(boolean p_148215_) {
        this.nodeEvaluator.setCanPassDoors(p_148215_);
    }

    public boolean canOpenDoors() {
        return this.nodeEvaluator.canPassDoors();
    }

    public void setAvoidSun(boolean p_26491_) {
        this.avoidSun = p_26491_;
    }

    public void setCanWalkOverFences(boolean p_255877_) {
        this.nodeEvaluator.setCanWalkOverFences(p_255877_);
    }
}

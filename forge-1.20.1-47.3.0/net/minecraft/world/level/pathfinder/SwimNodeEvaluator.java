//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.pathfinder;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class SwimNodeEvaluator extends NodeEvaluator {
    private final boolean allowBreaching;
    private final Long2ObjectMap<BlockPathTypes> pathTypesByPosCache = new Long2ObjectOpenHashMap();

    public SwimNodeEvaluator(boolean p_77457_) {
        this.allowBreaching = p_77457_;
    }

    public void prepare(PathNavigationRegion p_192959_, Mob p_192960_) {
        super.prepare(p_192959_, p_192960_);
        this.pathTypesByPosCache.clear();
    }

    public void done() {
        super.done();
        this.pathTypesByPosCache.clear();
    }

    public Node getStart() {
        return this.getNode(Mth.floor(this.mob.getBoundingBox().minX), Mth.floor(this.mob.getBoundingBox().minY + 0.5), Mth.floor(this.mob.getBoundingBox().minZ));
    }

    public Target getGoal(double p_77459_, double p_77460_, double p_77461_) {
        return this.getTargetFromNode(this.getNode(Mth.floor(p_77459_), Mth.floor(p_77460_), Mth.floor(p_77461_)));
    }

    public int getNeighbors(Node[] p_77483_, Node p_77484_) {
        int $$2 = 0;
        Map<Direction, Node> $$3 = Maps.newEnumMap(Direction.class);
        Direction[] var5 = Direction.values();
        int var6 = var5.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            Direction $$4 = var5[var7];
            Node $$5 = this.findAcceptedNode(p_77484_.x + $$4.getStepX(), p_77484_.y + $$4.getStepY(), p_77484_.z + $$4.getStepZ());
            $$3.put($$4, $$5);
            if (this.isNodeValid($$5)) {
                p_77483_[$$2++] = $$5;
            }
        }

        Iterator var10 = Plane.HORIZONTAL.iterator();

        while(var10.hasNext()) {
            Direction $$6 = (Direction)var10.next();
            Direction $$7 = $$6.getClockWise();
            Node $$8 = this.findAcceptedNode(p_77484_.x + $$6.getStepX() + $$7.getStepX(), p_77484_.y, p_77484_.z + $$6.getStepZ() + $$7.getStepZ());
            if (this.isDiagonalNodeValid($$8, (Node)$$3.get($$6), (Node)$$3.get($$7))) {
                p_77483_[$$2++] = $$8;
            }
        }

        return $$2;
    }

    protected boolean isNodeValid(@Nullable Node p_192962_) {
        return p_192962_ != null && !p_192962_.closed;
    }

    protected boolean isDiagonalNodeValid(@Nullable Node p_192964_, @Nullable Node p_192965_, @Nullable Node p_192966_) {
        return this.isNodeValid(p_192964_) && p_192965_ != null && p_192965_.costMalus >= 0.0F && p_192966_ != null && p_192966_.costMalus >= 0.0F;
    }

    @Nullable
    protected Node findAcceptedNode(int p_263032_, int p_263066_, int p_263105_) {
        Node $$3 = null;
        BlockPathTypes $$4 = this.getCachedBlockType(p_263032_, p_263066_, p_263105_);
        if (this.allowBreaching && $$4 == BlockPathTypes.BREACH || $$4 == BlockPathTypes.WATER) {
            float $$5 = this.mob.getPathfindingMalus($$4);
            if ($$5 >= 0.0F) {
                $$3 = this.getNode(p_263032_, p_263066_, p_263105_);
                $$3.type = $$4;
                $$3.costMalus = Math.max($$3.costMalus, $$5);
                if (this.level.getFluidState(new BlockPos(p_263032_, p_263066_, p_263105_)).isEmpty()) {
                    $$3.costMalus += 8.0F;
                }
            }
        }

        return $$3;
    }

    protected BlockPathTypes getCachedBlockType(int p_192968_, int p_192969_, int p_192970_) {
        return (BlockPathTypes)this.pathTypesByPosCache.computeIfAbsent(BlockPos.asLong(p_192968_, p_192969_, p_192970_), (p_192957_) -> {
            return this.getBlockPathType(this.level, p_192968_, p_192969_, p_192970_);
        });
    }

    public BlockPathTypes getBlockPathType(BlockGetter p_77467_, int p_77468_, int p_77469_, int p_77470_) {
        return this.getBlockPathType(p_77467_, p_77468_, p_77469_, p_77470_, this.mob);
    }

    public BlockPathTypes getBlockPathType(BlockGetter p_77472_, int p_77473_, int p_77474_, int p_77475_, Mob p_77476_) {
        BlockPos.MutableBlockPos $$5 = new BlockPos.MutableBlockPos();

        for(int $$6 = p_77473_; $$6 < p_77473_ + this.entityWidth; ++$$6) {
            for(int $$7 = p_77474_; $$7 < p_77474_ + this.entityHeight; ++$$7) {
                for(int $$8 = p_77475_; $$8 < p_77475_ + this.entityDepth; ++$$8) {
                    FluidState $$9 = p_77472_.getFluidState($$5.set($$6, $$7, $$8));
                    BlockState $$10 = p_77472_.getBlockState($$5.set($$6, $$7, $$8));
                    if ($$9.isEmpty() && $$10.isPathfindable(p_77472_, $$5.below(), PathComputationType.WATER) && $$10.isAir()) {
                        return BlockPathTypes.BREACH;
                    }

                    if (!$$9.is(FluidTags.WATER)) {
                        return BlockPathTypes.BLOCKED;
                    }
                }
            }
        }

        BlockState $$11 = p_77472_.getBlockState($$5);
        if ($$11.isPathfindable(p_77472_, $$5, PathComputationType.WATER)) {
            return BlockPathTypes.WATER;
        } else {
            return BlockPathTypes.BLOCKED;
        }
    }
}

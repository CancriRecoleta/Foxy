//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.pathfinder;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class FlyNodeEvaluator extends WalkNodeEvaluator {
    private final Long2ObjectMap<BlockPathTypes> pathTypeByPosCache = new Long2ObjectOpenHashMap();
    private static final float SMALL_MOB_INFLATED_START_NODE_BOUNDING_BOX = 1.5F;
    private static final int MAX_START_NODE_CANDIDATES = 10;

    public FlyNodeEvaluator() {
    }

    public void prepare(PathNavigationRegion p_77261_, Mob p_77262_) {
        super.prepare(p_77261_, p_77262_);
        this.pathTypeByPosCache.clear();
        p_77262_.onPathfindingStart();
    }

    public void done() {
        this.mob.onPathfindingDone();
        this.pathTypeByPosCache.clear();
        super.done();
    }

    public Node getStart() {
        int $$0;
        if (this.canFloat() && this.mob.isInWater()) {
            $$0 = this.mob.getBlockY();
            BlockPos.MutableBlockPos $$1 = new BlockPos.MutableBlockPos(this.mob.getX(), (double)$$0, this.mob.getZ());

            for(BlockState $$2 = this.level.getBlockState($$1); $$2.is(Blocks.WATER); $$2 = this.level.getBlockState($$1)) {
                ++$$0;
                $$1.set(this.mob.getX(), (double)$$0, this.mob.getZ());
            }
        } else {
            $$0 = Mth.floor(this.mob.getY() + 0.5);
        }

        BlockPos $$4 = BlockPos.containing(this.mob.getX(), (double)$$0, this.mob.getZ());
        if (!this.canStartAt($$4)) {
            Iterator var6 = this.iteratePathfindingStartNodeCandidatePositions(this.mob).iterator();

            while(var6.hasNext()) {
                BlockPos $$5 = (BlockPos)var6.next();
                if (this.canStartAt($$5)) {
                    return super.getStartNode($$5);
                }
            }
        }

        return super.getStartNode($$4);
    }

    protected boolean canStartAt(BlockPos p_262645_) {
        BlockPathTypes $$1 = this.getBlockPathType(this.mob, p_262645_);
        return this.mob.getPathfindingMalus($$1) >= 0.0F;
    }

    public Target getGoal(double p_77229_, double p_77230_, double p_77231_) {
        return this.getTargetFromNode(this.getNode(Mth.floor(p_77229_), Mth.floor(p_77230_), Mth.floor(p_77231_)));
    }

    public int getNeighbors(Node[] p_77266_, Node p_77267_) {
        int $$2 = 0;
        Node $$3 = this.findAcceptedNode(p_77267_.x, p_77267_.y, p_77267_.z + 1);
        if (this.isOpen($$3)) {
            p_77266_[$$2++] = $$3;
        }

        Node $$4 = this.findAcceptedNode(p_77267_.x - 1, p_77267_.y, p_77267_.z);
        if (this.isOpen($$4)) {
            p_77266_[$$2++] = $$4;
        }

        Node $$5 = this.findAcceptedNode(p_77267_.x + 1, p_77267_.y, p_77267_.z);
        if (this.isOpen($$5)) {
            p_77266_[$$2++] = $$5;
        }

        Node $$6 = this.findAcceptedNode(p_77267_.x, p_77267_.y, p_77267_.z - 1);
        if (this.isOpen($$6)) {
            p_77266_[$$2++] = $$6;
        }

        Node $$7 = this.findAcceptedNode(p_77267_.x, p_77267_.y + 1, p_77267_.z);
        if (this.isOpen($$7)) {
            p_77266_[$$2++] = $$7;
        }

        Node $$8 = this.findAcceptedNode(p_77267_.x, p_77267_.y - 1, p_77267_.z);
        if (this.isOpen($$8)) {
            p_77266_[$$2++] = $$8;
        }

        Node $$9 = this.findAcceptedNode(p_77267_.x, p_77267_.y + 1, p_77267_.z + 1);
        if (this.isOpen($$9) && this.hasMalus($$3) && this.hasMalus($$7)) {
            p_77266_[$$2++] = $$9;
        }

        Node $$10 = this.findAcceptedNode(p_77267_.x - 1, p_77267_.y + 1, p_77267_.z);
        if (this.isOpen($$10) && this.hasMalus($$4) && this.hasMalus($$7)) {
            p_77266_[$$2++] = $$10;
        }

        Node $$11 = this.findAcceptedNode(p_77267_.x + 1, p_77267_.y + 1, p_77267_.z);
        if (this.isOpen($$11) && this.hasMalus($$5) && this.hasMalus($$7)) {
            p_77266_[$$2++] = $$11;
        }

        Node $$12 = this.findAcceptedNode(p_77267_.x, p_77267_.y + 1, p_77267_.z - 1);
        if (this.isOpen($$12) && this.hasMalus($$6) && this.hasMalus($$7)) {
            p_77266_[$$2++] = $$12;
        }

        Node $$13 = this.findAcceptedNode(p_77267_.x, p_77267_.y - 1, p_77267_.z + 1);
        if (this.isOpen($$13) && this.hasMalus($$3) && this.hasMalus($$8)) {
            p_77266_[$$2++] = $$13;
        }

        Node $$14 = this.findAcceptedNode(p_77267_.x - 1, p_77267_.y - 1, p_77267_.z);
        if (this.isOpen($$14) && this.hasMalus($$4) && this.hasMalus($$8)) {
            p_77266_[$$2++] = $$14;
        }

        Node $$15 = this.findAcceptedNode(p_77267_.x + 1, p_77267_.y - 1, p_77267_.z);
        if (this.isOpen($$15) && this.hasMalus($$5) && this.hasMalus($$8)) {
            p_77266_[$$2++] = $$15;
        }

        Node $$16 = this.findAcceptedNode(p_77267_.x, p_77267_.y - 1, p_77267_.z - 1);
        if (this.isOpen($$16) && this.hasMalus($$6) && this.hasMalus($$8)) {
            p_77266_[$$2++] = $$16;
        }

        Node $$17 = this.findAcceptedNode(p_77267_.x + 1, p_77267_.y, p_77267_.z - 1);
        if (this.isOpen($$17) && this.hasMalus($$6) && this.hasMalus($$5)) {
            p_77266_[$$2++] = $$17;
        }

        Node $$18 = this.findAcceptedNode(p_77267_.x + 1, p_77267_.y, p_77267_.z + 1);
        if (this.isOpen($$18) && this.hasMalus($$3) && this.hasMalus($$5)) {
            p_77266_[$$2++] = $$18;
        }

        Node $$19 = this.findAcceptedNode(p_77267_.x - 1, p_77267_.y, p_77267_.z - 1);
        if (this.isOpen($$19) && this.hasMalus($$6) && this.hasMalus($$4)) {
            p_77266_[$$2++] = $$19;
        }

        Node $$20 = this.findAcceptedNode(p_77267_.x - 1, p_77267_.y, p_77267_.z + 1);
        if (this.isOpen($$20) && this.hasMalus($$3) && this.hasMalus($$4)) {
            p_77266_[$$2++] = $$20;
        }

        Node $$21 = this.findAcceptedNode(p_77267_.x + 1, p_77267_.y + 1, p_77267_.z - 1);
        if (this.isOpen($$21) && this.hasMalus($$17) && this.hasMalus($$6) && this.hasMalus($$5) && this.hasMalus($$7) && this.hasMalus($$12) && this.hasMalus($$11)) {
            p_77266_[$$2++] = $$21;
        }

        Node $$22 = this.findAcceptedNode(p_77267_.x + 1, p_77267_.y + 1, p_77267_.z + 1);
        if (this.isOpen($$22) && this.hasMalus($$18) && this.hasMalus($$3) && this.hasMalus($$5) && this.hasMalus($$7) && this.hasMalus($$9) && this.hasMalus($$11)) {
            p_77266_[$$2++] = $$22;
        }

        Node $$23 = this.findAcceptedNode(p_77267_.x - 1, p_77267_.y + 1, p_77267_.z - 1);
        if (this.isOpen($$23) && this.hasMalus($$19) && this.hasMalus($$6) && this.hasMalus($$4) && this.hasMalus($$7) && this.hasMalus($$12) && this.hasMalus($$10)) {
            p_77266_[$$2++] = $$23;
        }

        Node $$24 = this.findAcceptedNode(p_77267_.x - 1, p_77267_.y + 1, p_77267_.z + 1);
        if (this.isOpen($$24) && this.hasMalus($$20) && this.hasMalus($$3) && this.hasMalus($$4) && this.hasMalus($$7) && this.hasMalus($$9) && this.hasMalus($$10)) {
            p_77266_[$$2++] = $$24;
        }

        Node $$25 = this.findAcceptedNode(p_77267_.x + 1, p_77267_.y - 1, p_77267_.z - 1);
        if (this.isOpen($$25) && this.hasMalus($$17) && this.hasMalus($$6) && this.hasMalus($$5) && this.hasMalus($$8) && this.hasMalus($$16) && this.hasMalus($$15)) {
            p_77266_[$$2++] = $$25;
        }

        Node $$26 = this.findAcceptedNode(p_77267_.x + 1, p_77267_.y - 1, p_77267_.z + 1);
        if (this.isOpen($$26) && this.hasMalus($$18) && this.hasMalus($$3) && this.hasMalus($$5) && this.hasMalus($$8) && this.hasMalus($$13) && this.hasMalus($$15)) {
            p_77266_[$$2++] = $$26;
        }

        Node $$27 = this.findAcceptedNode(p_77267_.x - 1, p_77267_.y - 1, p_77267_.z - 1);
        if (this.isOpen($$27) && this.hasMalus($$19) && this.hasMalus($$6) && this.hasMalus($$4) && this.hasMalus($$8) && this.hasMalus($$16) && this.hasMalus($$14)) {
            p_77266_[$$2++] = $$27;
        }

        Node $$28 = this.findAcceptedNode(p_77267_.x - 1, p_77267_.y - 1, p_77267_.z + 1);
        if (this.isOpen($$28) && this.hasMalus($$20) && this.hasMalus($$3) && this.hasMalus($$4) && this.hasMalus($$8) && this.hasMalus($$13) && this.hasMalus($$14)) {
            p_77266_[$$2++] = $$28;
        }

        return $$2;
    }

    private boolean hasMalus(@Nullable Node p_77264_) {
        return p_77264_ != null && p_77264_.costMalus >= 0.0F;
    }

    private boolean isOpen(@Nullable Node p_77270_) {
        return p_77270_ != null && !p_77270_.closed;
    }

    @Nullable
    protected Node findAcceptedNode(int p_262970_, int p_263018_, int p_262947_) {
        Node $$3 = null;
        BlockPathTypes $$4 = this.getCachedBlockPathType(p_262970_, p_263018_, p_262947_);
        float $$5 = this.mob.getPathfindingMalus($$4);
        if ($$5 >= 0.0F) {
            $$3 = this.getNode(p_262970_, p_263018_, p_262947_);
            $$3.type = $$4;
            $$3.costMalus = Math.max($$3.costMalus, $$5);
            if ($$4 == BlockPathTypes.WALKABLE) {
                ++$$3.costMalus;
            }
        }

        return $$3;
    }

    private BlockPathTypes getCachedBlockPathType(int p_164694_, int p_164695_, int p_164696_) {
        return (BlockPathTypes)this.pathTypeByPosCache.computeIfAbsent(BlockPos.asLong(p_164694_, p_164695_, p_164696_), (p_265010_) -> {
            return this.getBlockPathType(this.level, p_164694_, p_164695_, p_164696_, this.mob);
        });
    }

    public BlockPathTypes getBlockPathType(BlockGetter p_265753_, int p_265243_, int p_265376_, int p_265253_, Mob p_265367_) {
        EnumSet<BlockPathTypes> $$5 = EnumSet.noneOf(BlockPathTypes.class);
        BlockPathTypes $$6 = BlockPathTypes.BLOCKED;
        BlockPos $$7 = p_265367_.blockPosition();
        $$6 = super.getBlockPathTypes(p_265753_, p_265243_, p_265376_, p_265253_, $$5, $$6, $$7);
        if ($$5.contains(BlockPathTypes.FENCE)) {
            return BlockPathTypes.FENCE;
        } else {
            BlockPathTypes $$8 = BlockPathTypes.BLOCKED;
            Iterator var10 = $$5.iterator();

            while(var10.hasNext()) {
                BlockPathTypes $$9 = (BlockPathTypes)var10.next();
                if (p_265367_.getPathfindingMalus($$9) < 0.0F) {
                    return $$9;
                }

                if (p_265367_.getPathfindingMalus($$9) >= p_265367_.getPathfindingMalus($$8)) {
                    $$8 = $$9;
                }
            }

            if ($$6 == BlockPathTypes.OPEN && p_265367_.getPathfindingMalus($$8) == 0.0F) {
                return BlockPathTypes.OPEN;
            } else {
                return $$8;
            }
        }
    }

    public BlockPathTypes getBlockPathType(BlockGetter p_77245_, int p_77246_, int p_77247_, int p_77248_) {
        BlockPos.MutableBlockPos $$4 = new BlockPos.MutableBlockPos();
        BlockPathTypes $$5 = getBlockPathTypeRaw(p_77245_, $$4.set(p_77246_, p_77247_, p_77248_));
        if ($$5 == BlockPathTypes.OPEN && p_77247_ >= p_77245_.getMinBuildHeight() + 1) {
            BlockPathTypes $$6 = getBlockPathTypeRaw(p_77245_, $$4.set(p_77246_, p_77247_ - 1, p_77248_));
            if ($$6 != BlockPathTypes.DAMAGE_FIRE && $$6 != BlockPathTypes.LAVA) {
                if ($$6 == BlockPathTypes.DAMAGE_OTHER) {
                    $$5 = BlockPathTypes.DAMAGE_OTHER;
                } else if ($$6 == BlockPathTypes.COCOA) {
                    $$5 = BlockPathTypes.COCOA;
                } else if ($$6 == BlockPathTypes.FENCE) {
                    if (!$$4.equals(this.mob.blockPosition())) {
                        $$5 = BlockPathTypes.FENCE;
                    }
                } else {
                    $$5 = $$6 != BlockPathTypes.WALKABLE && $$6 != BlockPathTypes.OPEN && $$6 != BlockPathTypes.WATER ? BlockPathTypes.WALKABLE : BlockPathTypes.OPEN;
                }
            } else {
                $$5 = BlockPathTypes.DAMAGE_FIRE;
            }
        }

        if ($$5 == BlockPathTypes.WALKABLE || $$5 == BlockPathTypes.OPEN) {
            $$5 = checkNeighbourBlocks(p_77245_, $$4.set(p_77246_, p_77247_, p_77248_), $$5);
        }

        return $$5;
    }

    private Iterable<BlockPos> iteratePathfindingStartNodeCandidatePositions(Mob p_263108_) {
        float $$1 = 1.0F;
        AABB $$2 = p_263108_.getBoundingBox();
        boolean $$3 = $$2.getSize() < 1.0;
        if (!$$3) {
            return List.of(BlockPos.containing($$2.minX, (double)p_263108_.getBlockY(), $$2.minZ), BlockPos.containing($$2.minX, (double)p_263108_.getBlockY(), $$2.maxZ), BlockPos.containing($$2.maxX, (double)p_263108_.getBlockY(), $$2.minZ), BlockPos.containing($$2.maxX, (double)p_263108_.getBlockY(), $$2.maxZ));
        } else {
            double $$4 = Math.max(0.0, (1.5 - $$2.getZsize()) / 2.0);
            double $$5 = Math.max(0.0, (1.5 - $$2.getXsize()) / 2.0);
            double $$6 = Math.max(0.0, (1.5 - $$2.getYsize()) / 2.0);
            AABB $$7 = $$2.inflate($$5, $$6, $$4);
            return BlockPos.randomBetweenClosed(p_263108_.getRandom(), 10, Mth.floor($$7.minX), Mth.floor($$7.minY), Mth.floor($$7.minZ), Mth.floor($$7.maxX), Mth.floor($$7.maxY), Mth.floor($$7.maxZ));
        }
    }
}

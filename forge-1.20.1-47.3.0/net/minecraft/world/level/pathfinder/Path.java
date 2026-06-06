//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.pathfinder;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class Path {
    private final List<Node> nodes;
    private Node[] openSet = new Node[0];
    private Node[] closedSet = new Node[0];
    @Nullable
    private Set<Target> targetNodes;
    private int nextNodeIndex;
    private final BlockPos target;
    private final float distToTarget;
    private final boolean reached;

    public Path(List<Node> p_77371_, BlockPos p_77372_, boolean p_77373_) {
        this.nodes = p_77371_;
        this.target = p_77372_;
        this.distToTarget = p_77371_.isEmpty() ? Float.MAX_VALUE : ((Node)this.nodes.get(this.nodes.size() - 1)).distanceManhattan(this.target);
        this.reached = p_77373_;
    }

    public void advance() {
        ++this.nextNodeIndex;
    }

    public boolean notStarted() {
        return this.nextNodeIndex <= 0;
    }

    public boolean isDone() {
        return this.nextNodeIndex >= this.nodes.size();
    }

    @Nullable
    public Node getEndNode() {
        return !this.nodes.isEmpty() ? (Node)this.nodes.get(this.nodes.size() - 1) : null;
    }

    public Node getNode(int p_77376_) {
        return (Node)this.nodes.get(p_77376_);
    }

    public void truncateNodes(int p_77389_) {
        if (this.nodes.size() > p_77389_) {
            this.nodes.subList(p_77389_, this.nodes.size()).clear();
        }

    }

    public void replaceNode(int p_77378_, Node p_77379_) {
        this.nodes.set(p_77378_, p_77379_);
    }

    public int getNodeCount() {
        return this.nodes.size();
    }

    public int getNextNodeIndex() {
        return this.nextNodeIndex;
    }

    public void setNextNodeIndex(int p_77394_) {
        this.nextNodeIndex = p_77394_;
    }

    public Vec3 getEntityPosAtNode(Entity p_77383_, int p_77384_) {
        Node $$2 = (Node)this.nodes.get(p_77384_);
        double $$3 = (double)$$2.x + (double)((int)(p_77383_.getBbWidth() + 1.0F)) * 0.5;
        double $$4 = (double)$$2.y;
        double $$5 = (double)$$2.z + (double)((int)(p_77383_.getBbWidth() + 1.0F)) * 0.5;
        return new Vec3($$3, $$4, $$5);
    }

    public BlockPos getNodePos(int p_77397_) {
        return ((Node)this.nodes.get(p_77397_)).asBlockPos();
    }

    public Vec3 getNextEntityPos(Entity p_77381_) {
        return this.getEntityPosAtNode(p_77381_, this.nextNodeIndex);
    }

    public BlockPos getNextNodePos() {
        return ((Node)this.nodes.get(this.nextNodeIndex)).asBlockPos();
    }

    public Node getNextNode() {
        return (Node)this.nodes.get(this.nextNodeIndex);
    }

    @Nullable
    public Node getPreviousNode() {
        return this.nextNodeIndex > 0 ? (Node)this.nodes.get(this.nextNodeIndex - 1) : null;
    }

    public boolean sameAs(@Nullable Path p_77386_) {
        if (p_77386_ == null) {
            return false;
        } else if (p_77386_.nodes.size() != this.nodes.size()) {
            return false;
        } else {
            for(int $$1 = 0; $$1 < this.nodes.size(); ++$$1) {
                Node $$2 = (Node)this.nodes.get($$1);
                Node $$3 = (Node)p_77386_.nodes.get($$1);
                if ($$2.x != $$3.x || $$2.y != $$3.y || $$2.z != $$3.z) {
                    return false;
                }
            }

            return true;
        }
    }

    public boolean canReach() {
        return this.reached;
    }

    @VisibleForDebug
    void setDebug(Node[] p_164710_, Node[] p_164711_, Set<Target> p_164712_) {
        this.openSet = p_164710_;
        this.closedSet = p_164711_;
        this.targetNodes = p_164712_;
    }

    @VisibleForDebug
    public Node[] getOpenSet() {
        return this.openSet;
    }

    @VisibleForDebug
    public Node[] getClosedSet() {
        return this.closedSet;
    }

    public void writeToStream(FriendlyByteBuf p_164705_) {
        if (this.targetNodes != null && !this.targetNodes.isEmpty()) {
            p_164705_.writeBoolean(this.reached);
            p_164705_.writeInt(this.nextNodeIndex);
            p_164705_.writeInt(this.targetNodes.size());
            this.targetNodes.forEach((p_164708_) -> {
                p_164708_.writeToStream(p_164705_);
            });
            p_164705_.writeInt(this.target.getX());
            p_164705_.writeInt(this.target.getY());
            p_164705_.writeInt(this.target.getZ());
            p_164705_.writeInt(this.nodes.size());
            Iterator var2 = this.nodes.iterator();

            while(var2.hasNext()) {
                Node $$1 = (Node)var2.next();
                $$1.writeToStream(p_164705_);
            }

            p_164705_.writeInt(this.openSet.length);
            Node[] var6 = this.openSet;
            int var7 = var6.length;

            int var4;
            Node $$3;
            for(var4 = 0; var4 < var7; ++var4) {
                $$3 = var6[var4];
                $$3.writeToStream(p_164705_);
            }

            p_164705_.writeInt(this.closedSet.length);
            var6 = this.closedSet;
            var7 = var6.length;

            for(var4 = 0; var4 < var7; ++var4) {
                $$3 = var6[var4];
                $$3.writeToStream(p_164705_);
            }

        }
    }

    public static Path createFromStream(FriendlyByteBuf p_77391_) {
        boolean $$1 = p_77391_.readBoolean();
        int $$2 = p_77391_.readInt();
        int $$3 = p_77391_.readInt();
        Set<Target> $$4 = Sets.newHashSet();

        for(int $$5 = 0; $$5 < $$3; ++$$5) {
            $$4.add(Target.createFromStream(p_77391_));
        }

        BlockPos $$6 = new BlockPos(p_77391_.readInt(), p_77391_.readInt(), p_77391_.readInt());
        List<Node> $$7 = Lists.newArrayList();
        int $$8 = p_77391_.readInt();

        for(int $$9 = 0; $$9 < $$8; ++$$9) {
            $$7.add(Node.createFromStream(p_77391_));
        }

        Node[] $$10 = new Node[p_77391_.readInt()];

        for(int $$11 = 0; $$11 < $$10.length; ++$$11) {
            $$10[$$11] = Node.createFromStream(p_77391_);
        }

        Node[] $$12 = new Node[p_77391_.readInt()];

        for(int $$13 = 0; $$13 < $$12.length; ++$$13) {
            $$12[$$13] = Node.createFromStream(p_77391_);
        }

        Path $$14 = new Path($$7, $$6, $$1);
        $$14.openSet = $$10;
        $$14.closedSet = $$12;
        $$14.targetNodes = $$4;
        $$14.nextNodeIndex = $$2;
        return $$14;
    }

    public String toString() {
        return "Path(length=" + this.nodes.size() + ")";
    }

    public BlockPos getTarget() {
        return this.target;
    }

    public float getDistToTarget() {
        return this.distToTarget;
    }
}

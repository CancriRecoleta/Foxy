//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.phys.Vec3;

public class LlamaFollowCaravanGoal extends Goal {
    public final Llama llama;
    private double speedModifier;
    private static final int CARAVAN_LIMIT = 8;
    private int distCheckCounter;

    public LlamaFollowCaravanGoal(Llama p_25501_, double p_25502_) {
        this.llama = p_25501_;
        this.speedModifier = p_25502_;
        this.setFlags(EnumSet.of(net.minecraft.world.entity.ai.goal.Goal.Flag.MOVE));
    }

    public boolean canUse() {
        if (!this.llama.isLeashed() && !this.llama.inCaravan()) {
            List<Entity> $$0 = this.llama.level().getEntities((Entity)this.llama, this.llama.getBoundingBox().inflate(9.0, 4.0, 9.0), (p_25505_) -> {
                EntityType<?> $$1 = p_25505_.getType();
                return $$1 == EntityType.LLAMA || $$1 == EntityType.TRADER_LLAMA;
            });
            Llama $$1 = null;
            double $$2 = Double.MAX_VALUE;
            Iterator var5 = $$0.iterator();

            Entity $$6;
            Llama $$7;
            double $$8;
            while(var5.hasNext()) {
                $$6 = (Entity)var5.next();
                $$7 = (Llama)$$6;
                if ($$7.inCaravan() && !$$7.hasCaravanTail()) {
                    $$8 = this.llama.distanceToSqr($$7);
                    if (!($$8 > $$2)) {
                        $$2 = $$8;
                        $$1 = $$7;
                    }
                }
            }

            if ($$1 == null) {
                var5 = $$0.iterator();

                while(var5.hasNext()) {
                    $$6 = (Entity)var5.next();
                    $$7 = (Llama)$$6;
                    if ($$7.isLeashed() && !$$7.hasCaravanTail()) {
                        $$8 = this.llama.distanceToSqr($$7);
                        if (!($$8 > $$2)) {
                            $$2 = $$8;
                            $$1 = $$7;
                        }
                    }
                }
            }

            if ($$1 == null) {
                return false;
            } else if ($$2 < 4.0) {
                return false;
            } else if (!$$1.isLeashed() && !this.firstIsLeashed($$1, 1)) {
                return false;
            } else {
                this.llama.joinCaravan($$1);
                return true;
            }
        } else {
            return false;
        }
    }

    public boolean canContinueToUse() {
        if (this.llama.inCaravan() && this.llama.getCaravanHead().isAlive() && this.firstIsLeashed(this.llama, 0)) {
            double $$0 = this.llama.distanceToSqr(this.llama.getCaravanHead());
            if ($$0 > 676.0) {
                if (this.speedModifier <= 3.0) {
                    this.speedModifier *= 1.2;
                    this.distCheckCounter = reducedTickDelay(40);
                    return true;
                }

                if (this.distCheckCounter == 0) {
                    return false;
                }
            }

            if (this.distCheckCounter > 0) {
                --this.distCheckCounter;
            }

            return true;
        } else {
            return false;
        }
    }

    public void stop() {
        this.llama.leaveCaravan();
        this.speedModifier = 2.1;
    }

    public void tick() {
        if (this.llama.inCaravan()) {
            if (!(this.llama.getLeashHolder() instanceof LeashFenceKnotEntity)) {
                Llama $$0 = this.llama.getCaravanHead();
                double $$1 = (double)this.llama.distanceTo($$0);
                float $$2 = 2.0F;
                Vec3 $$3 = (new Vec3($$0.getX() - this.llama.getX(), $$0.getY() - this.llama.getY(), $$0.getZ() - this.llama.getZ())).normalize().scale(Math.max($$1 - 2.0, 0.0));
                this.llama.getNavigation().moveTo(this.llama.getX() + $$3.x, this.llama.getY() + $$3.y, this.llama.getZ() + $$3.z, this.speedModifier);
            }
        }
    }

    private boolean firstIsLeashed(Llama p_25507_, int p_25508_) {
        if (p_25508_ > 8) {
            return false;
        } else if (p_25507_.inCaravan()) {
            if (p_25507_.getCaravanHead().isLeashed()) {
                return true;
            } else {
                Llama var10001 = p_25507_.getCaravanHead();
                ++p_25508_;
                return this.firstIsLeashed(var10001, p_25508_);
            }
        } else {
            return false;
        }
    }
}

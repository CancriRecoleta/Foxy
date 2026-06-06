//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;

public class PotentialCalculator {
    private final List<PointCharge> charges = Lists.newArrayList();

    public PotentialCalculator() {
    }

    public void addCharge(BlockPos p_47193_, double p_47194_) {
        if (p_47194_ != 0.0) {
            this.charges.add(new PointCharge(p_47193_, p_47194_));
        }

    }

    public double getPotentialEnergyChange(BlockPos p_47196_, double p_47197_) {
        if (p_47197_ == 0.0) {
            return 0.0;
        } else {
            double $$2 = 0.0;

            PointCharge $$3;
            for(Iterator var6 = this.charges.iterator(); var6.hasNext(); $$2 += $$3.getPotentialChange(p_47196_)) {
                $$3 = (PointCharge)var6.next();
            }

            return $$2 * p_47197_;
        }
    }

    private static class PointCharge {
        private final BlockPos pos;
        private final double charge;

        public PointCharge(BlockPos p_47201_, double p_47202_) {
            this.pos = p_47201_;
            this.charge = p_47202_;
        }

        public double getPotentialChange(BlockPos p_47204_) {
            double $$1 = this.pos.distSqr(p_47204_);
            return $$1 == 0.0 ? Double.POSITIVE_INFINITY : this.charge / Math.sqrt($$1);
        }
    }
}

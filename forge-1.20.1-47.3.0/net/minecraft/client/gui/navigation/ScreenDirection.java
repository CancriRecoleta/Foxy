//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.navigation;

import it.unimi.dsi.fastutil.ints.IntComparator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum ScreenDirection {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    private final IntComparator coordinateValueComparator = (p_265081_, p_265641_) -> {
        return p_265081_ == p_265641_ ? 0 : (this.isBefore(p_265081_, p_265641_) ? -1 : 1);
    };

    private ScreenDirection() {
    }

    public ScreenAxis getAxis() {
        ScreenAxis var10000;
        switch (this) {
            case UP:
            case DOWN:
                var10000 = ScreenAxis.VERTICAL;
                break;
            case LEFT:
            case RIGHT:
                var10000 = ScreenAxis.HORIZONTAL;
                break;
            default:
                throw new IncompatibleClassChangeError();
        }

        return var10000;
    }

    public ScreenDirection getOpposite() {
        ScreenDirection var10000;
        switch (this) {
            case UP -> var10000 = DOWN;
            case DOWN -> var10000 = UP;
            case LEFT -> var10000 = RIGHT;
            case RIGHT -> var10000 = LEFT;
            default -> throw new IncompatibleClassChangeError();
        }

        return var10000;
    }

    public boolean isPositive() {
        boolean var10000;
        switch (this) {
            case UP:
            case LEFT:
                var10000 = false;
                break;
            case DOWN:
            case RIGHT:
                var10000 = true;
                break;
            default:
                throw new IncompatibleClassChangeError();
        }

        return var10000;
    }

    public boolean isAfter(int p_265461_, int p_265553_) {
        if (this.isPositive()) {
            return p_265461_ > p_265553_;
        } else {
            return p_265553_ > p_265461_;
        }
    }

    public boolean isBefore(int p_265215_, int p_265040_) {
        if (this.isPositive()) {
            return p_265215_ < p_265040_;
        } else {
            return p_265040_ < p_265215_;
        }
    }

    public IntComparator coordinateValueComparator() {
        return this.coordinateValueComparator;
    }
}

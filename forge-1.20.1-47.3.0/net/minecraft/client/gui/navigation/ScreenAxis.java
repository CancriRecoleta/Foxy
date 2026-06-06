//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.navigation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum ScreenAxis {
    HORIZONTAL,
    VERTICAL;

    private ScreenAxis() {
    }

    public ScreenAxis orthogonal() {
        ScreenAxis var10000;
        switch (this) {
            case HORIZONTAL -> var10000 = VERTICAL;
            case VERTICAL -> var10000 = HORIZONTAL;
            default -> throw new IncompatibleClassChangeError();
        }

        return var10000;
    }

    public ScreenDirection getPositive() {
        ScreenDirection var10000;
        switch (this) {
            case HORIZONTAL -> var10000 = ScreenDirection.RIGHT;
            case VERTICAL -> var10000 = ScreenDirection.DOWN;
            default -> throw new IncompatibleClassChangeError();
        }

        return var10000;
    }

    public ScreenDirection getNegative() {
        ScreenDirection var10000;
        switch (this) {
            case HORIZONTAL -> var10000 = ScreenDirection.LEFT;
            case VERTICAL -> var10000 = ScreenDirection.UP;
            default -> throw new IncompatibleClassChangeError();
        }

        return var10000;
    }

    public ScreenDirection getDirection(boolean p_265698_) {
        return p_265698_ ? this.getPositive() : this.getNegative();
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.navigation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public record ScreenPosition(int x, int y) {
    public ScreenPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static ScreenPosition of(ScreenAxis p_265175_, int p_265751_, int p_265120_) {
        ScreenPosition var10000;
        switch (p_265175_) {
            case HORIZONTAL -> var10000 = new ScreenPosition(p_265751_, p_265120_);
            case VERTICAL -> var10000 = new ScreenPosition(p_265120_, p_265751_);
            default -> throw new IncompatibleClassChangeError();
        }

        return var10000;
    }

    public ScreenPosition step(ScreenDirection p_265084_) {
        ScreenPosition var10000;
        switch (p_265084_) {
            case DOWN -> var10000 = new ScreenPosition(this.x, this.y + 1);
            case UP -> var10000 = new ScreenPosition(this.x, this.y - 1);
            case LEFT -> var10000 = new ScreenPosition(this.x - 1, this.y);
            case RIGHT -> var10000 = new ScreenPosition(this.x + 1, this.y);
            default -> throw new IncompatibleClassChangeError();
        }

        return var10000;
    }

    public int getCoordinate(ScreenAxis p_265656_) {
        int var10000;
        switch (p_265656_) {
            case HORIZONTAL -> var10000 = this.x;
            case VERTICAL -> var10000 = this.y;
            default -> throw new IncompatibleClassChangeError();
        }

        return var10000;
    }

    public int x() {
        return this.x;
    }

    public int y() {
        return this.y;
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.synth;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class SimplexNoise {
    protected static final int[][] GRADIENT = new int[][]{{1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0}, {1, 0, 1}, {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1}, {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1}, {1, 1, 0}, {0, -1, 1}, {-1, 1, 0}, {0, -1, -1}};
    private static final double SQRT_3 = Math.sqrt(3.0);
    private static final double F2;
    private static final double G2;
    private final int[] p = new int[512];
    public final double xo;
    public final double yo;
    public final double zo;

    public SimplexNoise(RandomSource p_230549_) {
        this.xo = p_230549_.nextDouble() * 256.0;
        this.yo = p_230549_.nextDouble() * 256.0;
        this.zo = p_230549_.nextDouble() * 256.0;

        int $$2;
        for($$2 = 0; $$2 < 256; this.p[$$2] = $$2++) {
        }

        for($$2 = 0; $$2 < 256; ++$$2) {
            int $$3 = p_230549_.nextInt(256 - $$2);
            int $$4 = this.p[$$2];
            this.p[$$2] = this.p[$$3 + $$2];
            this.p[$$3 + $$2] = $$4;
        }

    }

    private int p(int p_75472_) {
        return this.p[p_75472_ & 255];
    }

    protected static double dot(int[] p_75480_, double p_75481_, double p_75482_, double p_75483_) {
        return (double)p_75480_[0] * p_75481_ + (double)p_75480_[1] * p_75482_ + (double)p_75480_[2] * p_75483_;
    }

    private double getCornerNoise3D(int p_75474_, double p_75475_, double p_75476_, double p_75477_, double p_75478_) {
        double $$5 = p_75478_ - p_75475_ * p_75475_ - p_75476_ * p_75476_ - p_75477_ * p_75477_;
        double $$7;
        if ($$5 < 0.0) {
            $$7 = 0.0;
        } else {
            $$5 *= $$5;
            $$7 = $$5 * $$5 * dot(GRADIENT[p_75474_], p_75475_, p_75476_, p_75477_);
        }

        return $$7;
    }

    public double getValue(double p_75465_, double p_75466_) {
        double $$2 = (p_75465_ + p_75466_) * F2;
        int $$3 = Mth.floor(p_75465_ + $$2);
        int $$4 = Mth.floor(p_75466_ + $$2);
        double $$5 = (double)($$3 + $$4) * G2;
        double $$6 = (double)$$3 - $$5;
        double $$7 = (double)$$4 - $$5;
        double $$8 = p_75465_ - $$6;
        double $$9 = p_75466_ - $$7;
        byte $$12;
        byte $$13;
        if ($$8 > $$9) {
            $$12 = 1;
            $$13 = 0;
        } else {
            $$12 = 0;
            $$13 = 1;
        }

        double $$14 = $$8 - (double)$$12 + G2;
        double $$15 = $$9 - (double)$$13 + G2;
        double $$16 = $$8 - 1.0 + 2.0 * G2;
        double $$17 = $$9 - 1.0 + 2.0 * G2;
        int $$18 = $$3 & 255;
        int $$19 = $$4 & 255;
        int $$20 = this.p($$18 + this.p($$19)) % 12;
        int $$21 = this.p($$18 + $$12 + this.p($$19 + $$13)) % 12;
        int $$22 = this.p($$18 + 1 + this.p($$19 + 1)) % 12;
        double $$23 = this.getCornerNoise3D($$20, $$8, $$9, 0.0, 0.5);
        double $$24 = this.getCornerNoise3D($$21, $$14, $$15, 0.0, 0.5);
        double $$25 = this.getCornerNoise3D($$22, $$16, $$17, 0.0, 0.5);
        return 70.0 * ($$23 + $$24 + $$25);
    }

    public double getValue(double p_75468_, double p_75469_, double p_75470_) {
        double $$3 = 0.3333333333333333;
        double $$4 = (p_75468_ + p_75469_ + p_75470_) * 0.3333333333333333;
        int $$5 = Mth.floor(p_75468_ + $$4);
        int $$6 = Mth.floor(p_75469_ + $$4);
        int $$7 = Mth.floor(p_75470_ + $$4);
        double $$8 = 0.16666666666666666;
        double $$9 = (double)($$5 + $$6 + $$7) * 0.16666666666666666;
        double $$10 = (double)$$5 - $$9;
        double $$11 = (double)$$6 - $$9;
        double $$12 = (double)$$7 - $$9;
        double $$13 = p_75468_ - $$10;
        double $$14 = p_75469_ - $$11;
        double $$15 = p_75470_ - $$12;
        byte $$22;
        byte $$23;
        byte $$24;
        byte $$25;
        byte $$26;
        byte $$51;
        if ($$13 >= $$14) {
            if ($$14 >= $$15) {
                $$22 = 1;
                $$23 = 0;
                $$24 = 0;
                $$25 = 1;
                $$26 = 1;
                $$51 = 0;
            } else if ($$13 >= $$15) {
                $$22 = 1;
                $$23 = 0;
                $$24 = 0;
                $$25 = 1;
                $$26 = 0;
                $$51 = 1;
            } else {
                $$22 = 0;
                $$23 = 0;
                $$24 = 1;
                $$25 = 1;
                $$26 = 0;
                $$51 = 1;
            }
        } else if ($$14 < $$15) {
            $$22 = 0;
            $$23 = 0;
            $$24 = 1;
            $$25 = 0;
            $$26 = 1;
            $$51 = 1;
        } else if ($$13 < $$15) {
            $$22 = 0;
            $$23 = 1;
            $$24 = 0;
            $$25 = 0;
            $$26 = 1;
            $$51 = 1;
        } else {
            $$22 = 0;
            $$23 = 1;
            $$24 = 0;
            $$25 = 1;
            $$26 = 1;
            $$51 = 0;
        }

        double $$52 = $$13 - (double)$$22 + 0.16666666666666666;
        double $$53 = $$14 - (double)$$23 + 0.16666666666666666;
        double $$54 = $$15 - (double)$$24 + 0.16666666666666666;
        double $$55 = $$13 - (double)$$25 + 0.3333333333333333;
        double $$56 = $$14 - (double)$$26 + 0.3333333333333333;
        double $$57 = $$15 - (double)$$51 + 0.3333333333333333;
        double $$58 = $$13 - 1.0 + 0.5;
        double $$59 = $$14 - 1.0 + 0.5;
        double $$60 = $$15 - 1.0 + 0.5;
        int $$61 = $$5 & 255;
        int $$62 = $$6 & 255;
        int $$63 = $$7 & 255;
        int $$64 = this.p($$61 + this.p($$62 + this.p($$63))) % 12;
        int $$65 = this.p($$61 + $$22 + this.p($$62 + $$23 + this.p($$63 + $$24))) % 12;
        int $$66 = this.p($$61 + $$25 + this.p($$62 + $$26 + this.p($$63 + $$51))) % 12;
        int $$67 = this.p($$61 + 1 + this.p($$62 + 1 + this.p($$63 + 1))) % 12;
        double $$68 = this.getCornerNoise3D($$64, $$13, $$14, $$15, 0.6);
        double $$69 = this.getCornerNoise3D($$65, $$52, $$53, $$54, 0.6);
        double $$70 = this.getCornerNoise3D($$66, $$55, $$56, $$57, 0.6);
        double $$71 = this.getCornerNoise3D($$67, $$58, $$59, $$60, 0.6);
        return 32.0 * ($$68 + $$69 + $$70 + $$71);
    }

    static {
        F2 = 0.5 * (SQRT_3 - 1.0);
        G2 = (3.0 - SQRT_3) / 6.0;
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.mojang.math;

import java.util.Arrays;
import net.minecraft.Util;
import org.joml.Matrix3f;

public enum SymmetricGroup3 {
    P123(0, 1, 2),
    P213(1, 0, 2),
    P132(0, 2, 1),
    P231(1, 2, 0),
    P312(2, 0, 1),
    P321(2, 1, 0);

    private final int[] permutation;
    private final Matrix3f transformation;
    private static final int ORDER = 3;
    private static final SymmetricGroup3[][] cayleyTable = (SymmetricGroup3[][])Util.make(new SymmetricGroup3[values().length][values().length], (p_109188_) -> {
        SymmetricGroup3[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            SymmetricGroup3 $$1 = var1[var3];
            SymmetricGroup3[] var5 = values();
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                SymmetricGroup3 $$2 = var5[var7];
                int[] $$3 = new int[3];

                for(int $$4 = 0; $$4 < 3; ++$$4) {
                    $$3[$$4] = $$1.permutation[$$2.permutation[$$4]];
                }

                SymmetricGroup3 $$5 = (SymmetricGroup3)Arrays.stream(values()).filter((p_175577_) -> {
                    return Arrays.equals(p_175577_.permutation, $$3);
                }).findFirst().get();
                p_109188_[$$1.ordinal()][$$2.ordinal()] = $$5;
            }
        }

    });

    private SymmetricGroup3(int p_109176_, int p_109177_, int p_109178_) {
        this.permutation = new int[]{p_109176_, p_109177_, p_109178_};
        this.transformation = new Matrix3f();
        this.transformation.set(this.permutation(0), 0, 1.0F);
        this.transformation.set(this.permutation(1), 1, 1.0F);
        this.transformation.set(this.permutation(2), 2, 1.0F);
    }

    public SymmetricGroup3 compose(SymmetricGroup3 p_109183_) {
        return cayleyTable[this.ordinal()][p_109183_.ordinal()];
    }

    public int permutation(int p_109181_) {
        return this.permutation[p_109181_];
    }

    public Matrix3f transformation() {
        return this.transformation;
    }
}

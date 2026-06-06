//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util.valueproviders;

import java.util.Arrays;
import net.minecraft.util.RandomSource;

public class MultipliedFloats implements SampledFloat {
    private final SampledFloat[] values;

    public MultipliedFloats(SampledFloat... p_216858_) {
        this.values = p_216858_;
    }

    public float sample(RandomSource p_216860_) {
        float $$1 = 1.0F;

        for(int $$2 = 0; $$2 < this.values.length; ++$$2) {
            $$1 *= this.values[$$2].sample(p_216860_);
        }

        return $$1;
    }

    public String toString() {
        return "MultipliedFloats" + Arrays.toString(this.values);
    }
}

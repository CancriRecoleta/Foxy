//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.model;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelUtils {
    public ModelUtils() {
    }

    public static float rotlerpRad(float p_103126_, float p_103127_, float p_103128_) {
        float $$3;
        for($$3 = p_103127_ - p_103126_; $$3 < -3.1415927F; $$3 += 6.2831855F) {
        }

        while($$3 >= 3.1415927F) {
            $$3 -= 6.2831855F;
        }

        return p_103126_ + p_103128_ * $$3;
    }
}

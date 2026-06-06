//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.structure.placement;

import com.mojang.serialization.Codec;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;

public enum RandomSpreadType implements StringRepresentable {
    LINEAR("linear"),
    TRIANGULAR("triangular");

    public static final Codec<RandomSpreadType> CODEC = StringRepresentable.fromEnum(RandomSpreadType::values);
    private final String id;

    private RandomSpreadType(String p_205022_) {
        this.id = p_205022_;
    }

    public String getSerializedName() {
        return this.id;
    }

    public int evaluate(RandomSource p_227019_, int p_227020_) {
        int var10000;
        switch (this) {
            case LINEAR -> var10000 = p_227019_.nextInt(p_227020_);
            case TRIANGULAR -> var10000 = (p_227019_.nextInt(p_227020_) + p_227019_.nextInt(p_227020_)) / 2;
            default -> throw new IncompatibleClassChangeError();
        }

        return var10000;
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client;

import java.util.function.IntFunction;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.OptionEnum;
import net.minecraft.util.ByIdMap.OutOfBoundsStrategy;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum GraphicsStatus implements OptionEnum {
    FAST(0, "options.graphics.fast"),
    FANCY(1, "options.graphics.fancy"),
    FABULOUS(2, "options.graphics.fabulous");

    private static final IntFunction<GraphicsStatus> BY_ID = ByIdMap.continuous(GraphicsStatus::getId, values(), OutOfBoundsStrategy.WRAP);
    private final int id;
    private final String key;

    private GraphicsStatus(int p_90771_, String p_90772_) {
        this.id = p_90771_;
        this.key = p_90772_;
    }

    public int getId() {
        return this.id;
    }

    public String getKey() {
        return this.key;
    }

    public String toString() {
        String var10000;
        switch (this) {
            case FAST -> var10000 = "fast";
            case FANCY -> var10000 = "fancy";
            case FABULOUS -> var10000 = "fabulous";
            default -> throw new IncompatibleClassChangeError();
        }

        return var10000;
    }

    public static GraphicsStatus byId(int p_90775_) {
        return (GraphicsStatus)BY_ID.apply(p_90775_);
    }
}

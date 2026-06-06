//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.ticks;

public enum TickPriority {
    EXTREMELY_HIGH(-3),
    VERY_HIGH(-2),
    HIGH(-1),
    NORMAL(0),
    LOW(1),
    VERY_LOW(2),
    EXTREMELY_LOW(3);

    private final int value;

    private TickPriority(int p_193444_) {
        this.value = p_193444_;
    }

    public static TickPriority byValue(int p_193447_) {
        TickPriority[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            TickPriority $$1 = var1[var3];
            if ($$1.value == p_193447_) {
                return $$1;
            }
        }

        if (p_193447_ < EXTREMELY_HIGH.value) {
            return EXTREMELY_HIGH;
        } else {
            return EXTREMELY_LOW;
        }
    }

    public int getValue() {
        return this.value;
    }
}

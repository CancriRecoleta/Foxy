//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public enum ChestType implements StringRepresentable {
    SINGLE("single"),
    LEFT("left"),
    RIGHT("right");

    private final String name;

    private ChestType(String p_263109_) {
        this.name = p_263109_;
    }

    public String getSerializedName() {
        return this.name;
    }

    public ChestType getOpposite() {
        ChestType var10000;
        switch (this) {
            case SINGLE -> var10000 = SINGLE;
            case LEFT -> var10000 = RIGHT;
            case RIGHT -> var10000 = LEFT;
            default -> throw new IncompatibleClassChangeError();
        }

        return var10000;
    }
}

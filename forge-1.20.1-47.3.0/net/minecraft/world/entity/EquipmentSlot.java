//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.entity;

public enum EquipmentSlot {
    MAINHAND(net.minecraft.world.entity.EquipmentSlot.Type.HAND, 0, 0, "mainhand"),
    OFFHAND(net.minecraft.world.entity.EquipmentSlot.Type.HAND, 1, 5, "offhand"),
    FEET(net.minecraft.world.entity.EquipmentSlot.Type.ARMOR, 0, 1, "feet"),
    LEGS(net.minecraft.world.entity.EquipmentSlot.Type.ARMOR, 1, 2, "legs"),
    CHEST(net.minecraft.world.entity.EquipmentSlot.Type.ARMOR, 2, 3, "chest"),
    HEAD(net.minecraft.world.entity.EquipmentSlot.Type.ARMOR, 3, 4, "head");

    private final Type type;
    private final int index;
    private final int filterFlag;
    private final String name;

    private EquipmentSlot(Type p_20739_, int p_20740_, int p_20741_, String p_20742_) {
        this.type = p_20739_;
        this.index = p_20740_;
        this.filterFlag = p_20741_;
        this.name = p_20742_;
    }

    public Type getType() {
        return this.type;
    }

    public int getIndex() {
        return this.index;
    }

    public int getIndex(int p_147069_) {
        return p_147069_ + this.index;
    }

    public int getFilterFlag() {
        return this.filterFlag;
    }

    public String getName() {
        return this.name;
    }

    public boolean isArmor() {
        return this.type == net.minecraft.world.entity.EquipmentSlot.Type.ARMOR;
    }

    public static EquipmentSlot byName(String p_20748_) {
        EquipmentSlot[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EquipmentSlot $$1 = var1[var3];
            if ($$1.getName().equals(p_20748_)) {
                return $$1;
            }
        }

        throw new IllegalArgumentException("Invalid slot '" + p_20748_ + "'");
    }

    public static EquipmentSlot byTypeAndIndex(Type p_20745_, int p_20746_) {
        EquipmentSlot[] var2 = values();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            EquipmentSlot $$2 = var2[var4];
            if ($$2.getType() == p_20745_ && $$2.getIndex() == p_20746_) {
                return $$2;
            }
        }

        throw new IllegalArgumentException("Invalid slot '" + p_20745_ + "': " + p_20746_);
    }

    public static enum Type {
        HAND,
        ARMOR;

        private Type() {
        }
    }
}

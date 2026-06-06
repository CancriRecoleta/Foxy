//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.core;

import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Mth;

public class Rotations {
    protected final float x;
    protected final float y;
    protected final float z;

    public Rotations(float p_123150_, float p_123151_, float p_123152_) {
        this.x = !Float.isInfinite(p_123150_) && !Float.isNaN(p_123150_) ? p_123150_ % 360.0F : 0.0F;
        this.y = !Float.isInfinite(p_123151_) && !Float.isNaN(p_123151_) ? p_123151_ % 360.0F : 0.0F;
        this.z = !Float.isInfinite(p_123152_) && !Float.isNaN(p_123152_) ? p_123152_ % 360.0F : 0.0F;
    }

    public Rotations(ListTag p_123154_) {
        this(p_123154_.getFloat(0), p_123154_.getFloat(1), p_123154_.getFloat(2));
    }

    public ListTag save() {
        ListTag $$0 = new ListTag();
        $$0.add(FloatTag.valueOf(this.x));
        $$0.add(FloatTag.valueOf(this.y));
        $$0.add(FloatTag.valueOf(this.z));
        return $$0;
    }

    public boolean equals(Object p_123160_) {
        if (!(p_123160_ instanceof Rotations $$1)) {
            return false;
        } else {
            return this.x == $$1.x && this.y == $$1.y && this.z == $$1.z;
        }
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getZ() {
        return this.z;
    }

    public float getWrappedX() {
        return Mth.wrapDegrees(this.x);
    }

    public float getWrappedY() {
        return Mth.wrapDegrees(this.y);
    }

    public float getWrappedZ() {
        return Mth.wrapDegrees(this.z);
    }
}

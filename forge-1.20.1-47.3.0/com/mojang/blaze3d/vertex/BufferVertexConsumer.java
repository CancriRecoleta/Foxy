//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.vertex.VertexFormatElement.Type;
import com.mojang.blaze3d.vertex.VertexFormatElement.Usage;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface BufferVertexConsumer extends VertexConsumer {
    VertexFormatElement currentElement();

    void nextElement();

    void putByte(int var1, byte var2);

    void putShort(int var1, short var2);

    void putFloat(int var1, float var2);

    default VertexConsumer vertex(double p_85771_, double p_85772_, double p_85773_) {
        if (this.currentElement().getUsage() != Usage.POSITION) {
            return this;
        } else if (this.currentElement().getType() == Type.FLOAT && this.currentElement().getCount() == 3) {
            this.putFloat(0, (float)p_85771_);
            this.putFloat(4, (float)p_85772_);
            this.putFloat(8, (float)p_85773_);
            this.nextElement();
            return this;
        } else {
            throw new IllegalStateException();
        }
    }

    default VertexConsumer color(int p_85787_, int p_85788_, int p_85789_, int p_85790_) {
        VertexFormatElement $$4 = this.currentElement();
        if ($$4.getUsage() != Usage.COLOR) {
            return this;
        } else if ($$4.getType() == Type.UBYTE && $$4.getCount() == 4) {
            this.putByte(0, (byte)p_85787_);
            this.putByte(1, (byte)p_85788_);
            this.putByte(2, (byte)p_85789_);
            this.putByte(3, (byte)p_85790_);
            this.nextElement();
            return this;
        } else {
            throw new IllegalStateException();
        }
    }

    default VertexConsumer uv(float p_85777_, float p_85778_) {
        VertexFormatElement $$2 = this.currentElement();
        if ($$2.getUsage() == Usage.UV && $$2.getIndex() == 0) {
            if ($$2.getType() == Type.FLOAT && $$2.getCount() == 2) {
                this.putFloat(0, p_85777_);
                this.putFloat(4, p_85778_);
                this.nextElement();
                return this;
            } else {
                throw new IllegalStateException();
            }
        } else {
            return this;
        }
    }

    default VertexConsumer overlayCoords(int p_85784_, int p_85785_) {
        return this.uvShort((short)p_85784_, (short)p_85785_, 1);
    }

    default VertexConsumer uv2(int p_85802_, int p_85803_) {
        return this.uvShort((short)p_85802_, (short)p_85803_, 2);
    }

    default VertexConsumer uvShort(short p_85794_, short p_85795_, int p_85796_) {
        VertexFormatElement $$3 = this.currentElement();
        if ($$3.getUsage() == Usage.UV && $$3.getIndex() == p_85796_) {
            if ($$3.getType() == Type.SHORT && $$3.getCount() == 2) {
                this.putShort(0, p_85794_);
                this.putShort(2, p_85795_);
                this.nextElement();
                return this;
            } else {
                throw new IllegalStateException();
            }
        } else {
            return this;
        }
    }

    default VertexConsumer normal(float p_85798_, float p_85799_, float p_85800_) {
        VertexFormatElement $$3 = this.currentElement();
        if ($$3.getUsage() != Usage.NORMAL) {
            return this;
        } else if ($$3.getType() == Type.BYTE && $$3.getCount() == 3) {
            this.putByte(0, normalIntValue(p_85798_));
            this.putByte(1, normalIntValue(p_85799_));
            this.putByte(2, normalIntValue(p_85800_));
            this.nextElement();
            return this;
        } else {
            throw new IllegalStateException();
        }
    }

    static byte normalIntValue(float p_85775_) {
        return (byte)((int)(Mth.clamp(p_85775_, -1.0F, 1.0F) * 127.0F) & 255);
    }
}

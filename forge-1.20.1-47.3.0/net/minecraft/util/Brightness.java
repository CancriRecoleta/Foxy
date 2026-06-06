//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Brightness(int block, int sky) {
    public static final Codec<Integer> LIGHT_VALUE_CODEC = ExtraCodecs.intRange(0, 15);
    public static final Codec<Brightness> CODEC = RecordCodecBuilder.create((p_270774_) -> {
        return p_270774_.group(LIGHT_VALUE_CODEC.fieldOf("block").forGetter(Brightness::block), LIGHT_VALUE_CODEC.fieldOf("sky").forGetter(Brightness::sky)).apply(p_270774_, Brightness::new);
    });
    public static Brightness FULL_BRIGHT = new Brightness(15, 15);

    public Brightness(int block, int sky) {
        this.block = block;
        this.sky = sky;
    }

    public int pack() {
        return this.block << 4 | this.sky << 20;
    }

    public static Brightness unpack(int p_270207_) {
        int $$1 = p_270207_ >> 4 & '\uffff';
        int $$2 = p_270207_ >> 20 & '\uffff';
        return new Brightness($$1, $$2);
    }

    public int block() {
        return this.block;
    }

    public int sky() {
        return this.sky;
    }
}

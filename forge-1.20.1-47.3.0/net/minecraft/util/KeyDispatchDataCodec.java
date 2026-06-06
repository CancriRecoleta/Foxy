//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

public record KeyDispatchDataCodec<A>(Codec<A> codec) {
    public KeyDispatchDataCodec(Codec<A> codec) {
        this.codec = codec;
    }

    public static <A> KeyDispatchDataCodec<A> of(Codec<A> p_216237_) {
        return new KeyDispatchDataCodec(p_216237_);
    }

    public static <A> KeyDispatchDataCodec<A> of(MapCodec<A> p_216239_) {
        return new KeyDispatchDataCodec(p_216239_.codec());
    }

    public Codec<A> codec() {
        return this.codec;
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.util.StringRepresentable;

public interface IExtensibleEnum {
    /** @deprecated */
    @Deprecated
    default void init() {
    }

    static <E extends Enum<E> & StringRepresentable> Codec<E> createCodecForExtensibleEnum(Supplier<E[]> valuesSupplier, Function<? super String, ? extends E> enumValueFromNameFunction) {
        return Codec.either(Codec.STRING, Codec.INT).comapFlatMap((either) -> {
            return (DataResult)either.map((str) -> {
                E val = (Enum)enumValueFromNameFunction.apply(str);
                return val != null ? DataResult.success(val) : DataResult.error(() -> {
                    return "Unknown enum value name: " + str;
                });
            }, (num) -> {
                E[] values = (Enum[])valuesSupplier.get();
                return num >= 0 && num < values.length ? DataResult.success(values[num]) : DataResult.error(() -> {
                    return "Unknown enum id: " + num;
                });
            });
        }, (value) -> {
            return Either.left(((StringRepresentable)value).getSerializedName());
        });
    }
}

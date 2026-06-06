//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.gameevent;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface PositionSourceType<T extends PositionSource> {
    PositionSourceType<BlockPositionSource> BLOCK = register("block", new BlockPositionSource.Type());
    PositionSourceType<EntityPositionSource> ENTITY = register("entity", new EntityPositionSource.Type());

    T read(FriendlyByteBuf var1);

    void write(FriendlyByteBuf var1, T var2);

    Codec<T> codec();

    static <S extends PositionSourceType<T>, T extends PositionSource> S register(String p_157878_, S p_157879_) {
        return (PositionSourceType)Registry.register(BuiltInRegistries.POSITION_SOURCE_TYPE, (String)p_157878_, p_157879_);
    }

    static PositionSource fromNetwork(FriendlyByteBuf p_157886_) {
        ResourceLocation $$1 = p_157886_.readResourceLocation();
        return ((PositionSourceType)BuiltInRegistries.POSITION_SOURCE_TYPE.getOptional($$1).orElseThrow(() -> {
            return new IllegalArgumentException("Unknown position source type " + $$1);
        })).read(p_157886_);
    }

    static <T extends PositionSource> void toNetwork(T p_157875_, FriendlyByteBuf p_157876_) {
        p_157876_.writeResourceLocation(BuiltInRegistries.POSITION_SOURCE_TYPE.getKey(p_157875_.getType()));
        p_157875_.getType().write(p_157876_, p_157875_);
    }
}

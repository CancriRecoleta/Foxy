//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.item;

import com.mojang.serialization.Codec;
import java.util.Objects;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraftforge.common.IExtensibleEnum;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryInternal;

public enum ItemDisplayContext implements StringRepresentable, IExtensibleEnum {
    NONE(0, "none"),
    THIRD_PERSON_LEFT_HAND(1, "thirdperson_lefthand"),
    THIRD_PERSON_RIGHT_HAND(2, "thirdperson_righthand"),
    FIRST_PERSON_LEFT_HAND(3, "firstperson_lefthand"),
    FIRST_PERSON_RIGHT_HAND(4, "firstperson_righthand"),
    HEAD(5, "head"),
    GUI(6, "gui"),
    GROUND(7, "ground"),
    FIXED(8, "fixed");

    public static final Codec<ItemDisplayContext> CODEC = ExtraCodecs.lazyInitializedCodec(() -> {
        return ((IForgeRegistry)ForgeRegistries.DISPLAY_CONTEXTS.get()).getCodec();
    });
    public static final IntFunction<ItemDisplayContext> BY_ID = (id) -> {
        return (ItemDisplayContext)Objects.requireNonNullElse((ItemDisplayContext)((IForgeRegistryInternal)ForgeRegistries.DISPLAY_CONTEXTS.get()).getValue(id < 0 ? 127 + -id : id), NONE);
    };
    private byte id;
    private final String name;
    private final boolean isModded;
    @Nullable
    private final ItemDisplayContext fallback;
    public static final IForgeRegistry.AddCallback<ItemDisplayContext> ADD_CALLBACK = (owner, stage, id, key, obj, oldObj) -> {
        obj.id = id > 127 ? (byte)(-(id - 127)) : (byte)id;
    };

    private ItemDisplayContext(int p_270624_, String p_270851_) {
        this.name = p_270851_;
        this.id = (byte)p_270624_;
        this.isModded = false;
        this.fallback = null;
    }

    public String getSerializedName() {
        return this.name;
    }

    public byte getId() {
        return this.id;
    }

    public boolean firstPerson() {
        return this == FIRST_PERSON_LEFT_HAND || this == FIRST_PERSON_RIGHT_HAND;
    }

    private ItemDisplayContext(ResourceLocation serializeName, ItemDisplayContext fallback) {
        this.id = 0;
        this.name = ((ResourceLocation)Objects.requireNonNull(serializeName, "Modded ItemDisplayContexts must have a non-null serializeName")).toString();
        this.isModded = true;
        this.fallback = fallback;
    }

    public boolean isModded() {
        return this.isModded;
    }

    @Nullable
    public ItemDisplayContext fallback() {
        return this.fallback;
    }

    public static ItemDisplayContext create(String keyName, ResourceLocation serializedName, @Nullable ItemDisplayContext fallback) {
        throw new IllegalStateException("Enum not extended!");
    }
}

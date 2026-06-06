//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.nbt;

public class TagTypes {
    private static final TagType<?>[] TYPES;

    public TagTypes() {
    }

    public static TagType<?> getType(int p_129398_) {
        return p_129398_ >= 0 && p_129398_ < TYPES.length ? TYPES[p_129398_] : TagType.createInvalid(p_129398_);
    }

    static {
        TYPES = new TagType[]{EndTag.TYPE, ByteTag.TYPE, ShortTag.TYPE, IntTag.TYPE, LongTag.TYPE, FloatTag.TYPE, DoubleTag.TYPE, ByteArrayTag.TYPE, StringTag.TYPE, ListTag.TYPE, CompoundTag.TYPE, IntArrayTag.TYPE, LongArrayTag.TYPE};
    }
}

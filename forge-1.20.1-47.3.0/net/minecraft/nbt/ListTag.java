//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.nbt;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import net.minecraft.nbt.StreamTagVisitor.ValueResult;

public class ListTag extends CollectionTag<Tag> {
    private static final int SELF_SIZE_IN_BYTES = 37;
    public static final TagType<ListTag> TYPE = new TagType.VariableSize<ListTag>() {
        public ListTag load(DataInput p_128792_, int p_128793_, NbtAccounter p_128794_) throws IOException {
            p_128794_.accountBytes(37L);
            if (p_128793_ > 512) {
                throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
            } else {
                byte $$3 = p_128792_.readByte();
                int $$4 = p_128792_.readInt();
                if ($$3 == 0 && $$4 > 0) {
                    throw new RuntimeException("Missing type on ListTag");
                } else {
                    p_128794_.accountBytes(4L * (long)$$4);
                    TagType<?> $$5 = TagTypes.getType($$3);
                    List<Tag> $$6 = Lists.newArrayListWithCapacity($$4);

                    for(int $$7 = 0; $$7 < $$4; ++$$7) {
                        $$6.add($$5.load(p_128792_, p_128793_ + 1, p_128794_));
                    }

                    return new ListTag($$6, $$3);
                }
            }
        }

        public StreamTagVisitor.ValueResult parse(DataInput p_197491_, StreamTagVisitor p_197492_) throws IOException {
            TagType<?> $$2 = TagTypes.getType(p_197491_.readByte());
            int $$3 = p_197491_.readInt();
            switch (p_197492_.visitList($$2, $$3)) {
                case HALT:
                    return ValueResult.HALT;
                case BREAK:
                    $$2.skip(p_197491_, $$3);
                    return p_197492_.visitContainerEnd();
                default:
                    int $$4 = 0;

                    label34:
                    for(; $$4 < $$3; ++$$4) {
                        switch (p_197492_.visitElement($$2, $$4)) {
                            case HALT:
                                return ValueResult.HALT;
                            case BREAK:
                                $$2.skip(p_197491_);
                                break label34;
                            case SKIP:
                                $$2.skip(p_197491_);
                                break;
                            default:
                                switch ($$2.parse(p_197491_, p_197492_)) {
                                    case HALT -> return ValueResult.HALT;
                                    case BREAK -> { }
                                }
                        }
                    }

                    int $$5 = $$3 - 1 - $$4;
                    if ($$5 > 0) {
                        $$2.skip(p_197491_, $$5);
                    }

                    return p_197492_.visitContainerEnd();
            }
        }

        public void skip(DataInput p_197489_) throws IOException {
            TagType<?> $$1 = TagTypes.getType(p_197489_.readByte());
            int $$2 = p_197489_.readInt();
            $$1.skip(p_197489_, $$2);
        }

        public String getName() {
            return "LIST";
        }

        public String getPrettyName() {
            return "TAG_List";
        }
    };
    private final List<Tag> list;
    private byte type;

    ListTag(List<Tag> p_128721_, byte p_128722_) {
        this.list = p_128721_;
        this.type = p_128722_;
    }

    public ListTag() {
        this(Lists.newArrayList(), (byte)0);
    }

    public void write(DataOutput p_128734_) throws IOException {
        if (this.list.isEmpty()) {
            this.type = 0;
        } else {
            this.type = ((Tag)this.list.get(0)).getId();
        }

        p_128734_.writeByte(this.type);
        p_128734_.writeInt(this.list.size());
        Iterator var2 = this.list.iterator();

        while(var2.hasNext()) {
            Tag $$1 = (Tag)var2.next();
            $$1.write(p_128734_);
        }

    }

    public int sizeInBytes() {
        int $$0 = 37;
        $$0 += 4 * this.list.size();

        Tag $$1;
        for(Iterator var2 = this.list.iterator(); var2.hasNext(); $$0 += $$1.sizeInBytes()) {
            $$1 = (Tag)var2.next();
        }

        return $$0;
    }

    public byte getId() {
        return 9;
    }

    public TagType<ListTag> getType() {
        return TYPE;
    }

    public String toString() {
        return this.getAsString();
    }

    private void updateTypeAfterRemove() {
        if (this.list.isEmpty()) {
            this.type = 0;
        }

    }

    public Tag remove(int p_128751_) {
        Tag $$1 = (Tag)this.list.remove(p_128751_);
        this.updateTypeAfterRemove();
        return $$1;
    }

    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    public CompoundTag getCompound(int p_128729_) {
        if (p_128729_ >= 0 && p_128729_ < this.list.size()) {
            Tag $$1 = (Tag)this.list.get(p_128729_);
            if ($$1.getId() == 10) {
                return (CompoundTag)$$1;
            }
        }

        return new CompoundTag();
    }

    public ListTag getList(int p_128745_) {
        if (p_128745_ >= 0 && p_128745_ < this.list.size()) {
            Tag $$1 = (Tag)this.list.get(p_128745_);
            if ($$1.getId() == 9) {
                return (ListTag)$$1;
            }
        }

        return new ListTag();
    }

    public short getShort(int p_128758_) {
        if (p_128758_ >= 0 && p_128758_ < this.list.size()) {
            Tag $$1 = (Tag)this.list.get(p_128758_);
            if ($$1.getId() == 2) {
                return ((ShortTag)$$1).getAsShort();
            }
        }

        return 0;
    }

    public int getInt(int p_128764_) {
        if (p_128764_ >= 0 && p_128764_ < this.list.size()) {
            Tag $$1 = (Tag)this.list.get(p_128764_);
            if ($$1.getId() == 3) {
                return ((IntTag)$$1).getAsInt();
            }
        }

        return 0;
    }

    public int[] getIntArray(int p_128768_) {
        if (p_128768_ >= 0 && p_128768_ < this.list.size()) {
            Tag $$1 = (Tag)this.list.get(p_128768_);
            if ($$1.getId() == 11) {
                return ((IntArrayTag)$$1).getAsIntArray();
            }
        }

        return new int[0];
    }

    public long[] getLongArray(int p_177992_) {
        if (p_177992_ >= 0 && p_177992_ < this.list.size()) {
            Tag $$1 = (Tag)this.list.get(p_177992_);
            if ($$1.getId() == 12) {
                return ((LongArrayTag)$$1).getAsLongArray();
            }
        }

        return new long[0];
    }

    public double getDouble(int p_128773_) {
        if (p_128773_ >= 0 && p_128773_ < this.list.size()) {
            Tag $$1 = (Tag)this.list.get(p_128773_);
            if ($$1.getId() == 6) {
                return ((DoubleTag)$$1).getAsDouble();
            }
        }

        return 0.0;
    }

    public float getFloat(int p_128776_) {
        if (p_128776_ >= 0 && p_128776_ < this.list.size()) {
            Tag $$1 = (Tag)this.list.get(p_128776_);
            if ($$1.getId() == 5) {
                return ((FloatTag)$$1).getAsFloat();
            }
        }

        return 0.0F;
    }

    public String getString(int p_128779_) {
        if (p_128779_ >= 0 && p_128779_ < this.list.size()) {
            Tag $$1 = (Tag)this.list.get(p_128779_);
            return $$1.getId() == 8 ? $$1.getAsString() : $$1.toString();
        } else {
            return "";
        }
    }

    public int size() {
        return this.list.size();
    }

    public Tag get(int p_128781_) {
        return (Tag)this.list.get(p_128781_);
    }

    public Tag set(int p_128760_, Tag p_128761_) {
        Tag $$2 = this.get(p_128760_);
        if (!this.setTag(p_128760_, p_128761_)) {
            throw new UnsupportedOperationException(String.format(Locale.ROOT, "Trying to add tag of type %d to list of %d", p_128761_.getId(), this.type));
        } else {
            return $$2;
        }
    }

    public void add(int p_128753_, Tag p_128754_) {
        if (!this.addTag(p_128753_, p_128754_)) {
            throw new UnsupportedOperationException(String.format(Locale.ROOT, "Trying to add tag of type %d to list of %d", p_128754_.getId(), this.type));
        }
    }

    public boolean setTag(int p_128731_, Tag p_128732_) {
        if (this.updateType(p_128732_)) {
            this.list.set(p_128731_, p_128732_);
            return true;
        } else {
            return false;
        }
    }

    public boolean addTag(int p_128747_, Tag p_128748_) {
        if (this.updateType(p_128748_)) {
            this.list.add(p_128747_, p_128748_);
            return true;
        } else {
            return false;
        }
    }

    private boolean updateType(Tag p_128739_) {
        if (p_128739_.getId() == 0) {
            return false;
        } else if (this.type == 0) {
            this.type = p_128739_.getId();
            return true;
        } else {
            return this.type == p_128739_.getId();
        }
    }

    public ListTag copy() {
        Iterable<Tag> $$0 = TagTypes.getType(this.type).isValue() ? this.list : Iterables.transform(this.list, Tag::copy);
        List<Tag> $$1 = Lists.newArrayList((Iterable)$$0);
        return new ListTag($$1, this.type);
    }

    public boolean equals(Object p_128766_) {
        if (this == p_128766_) {
            return true;
        } else {
            return p_128766_ instanceof ListTag && Objects.equals(this.list, ((ListTag)p_128766_).list);
        }
    }

    public int hashCode() {
        return this.list.hashCode();
    }

    public void accept(TagVisitor p_177990_) {
        p_177990_.visitList(this);
    }

    public byte getElementType() {
        return this.type;
    }

    public void clear() {
        this.list.clear();
        this.type = 0;
    }

    public StreamTagVisitor.ValueResult accept(StreamTagVisitor p_197487_) {
        switch (p_197487_.visitList(TagTypes.getType(this.type), this.list.size())) {
            case HALT:
                return ValueResult.HALT;
            case BREAK:
                return p_197487_.visitContainerEnd();
            default:
                int $$1 = 0;

                while($$1 < this.list.size()) {
                    Tag $$2 = (Tag)this.list.get($$1);
                    switch (p_197487_.visitElement($$2.getType(), $$1)) {
                        case HALT:
                            return ValueResult.HALT;
                        case BREAK:
                            return p_197487_.visitContainerEnd();
                        default:
                            switch ($$2.accept(p_197487_)) {
                                case HALT -> return ValueResult.HALT;
                                case BREAK -> return p_197487_.visitContainerEnd();
                            }
                        case SKIP:
                            ++$$1;
                    }
                }

                return p_197487_.visitContainerEnd();
        }
    }
}

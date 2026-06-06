//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.nbt;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;

public class LongArrayTag extends CollectionTag<LongTag> {
    private static final int SELF_SIZE_IN_BYTES = 24;
    public static final TagType<LongArrayTag> TYPE = new TagType.VariableSize<LongArrayTag>() {
        public LongArrayTag load(DataInput p_128865_, int p_128866_, NbtAccounter p_128867_) throws IOException {
            p_128867_.accountBytes(24L);
            int $$3 = p_128865_.readInt();
            p_128867_.accountBytes(8L * (long)$$3);
            long[] $$4 = new long[$$3];

            for(int $$5 = 0; $$5 < $$3; ++$$5) {
                $$4[$$5] = p_128865_.readLong();
            }

            return new LongArrayTag($$4);
        }

        public StreamTagVisitor.ValueResult parse(DataInput p_197501_, StreamTagVisitor p_197502_) throws IOException {
            int $$2 = p_197501_.readInt();
            long[] $$3 = new long[$$2];

            for(int $$4 = 0; $$4 < $$2; ++$$4) {
                $$3[$$4] = p_197501_.readLong();
            }

            return p_197502_.visit($$3);
        }

        public void skip(DataInput p_197499_) throws IOException {
            p_197499_.skipBytes(p_197499_.readInt() * 8);
        }

        public String getName() {
            return "LONG[]";
        }

        public String getPrettyName() {
            return "TAG_Long_Array";
        }
    };
    private long[] data;

    public LongArrayTag(long[] p_128808_) {
        this.data = p_128808_;
    }

    public LongArrayTag(LongSet p_128804_) {
        this.data = p_128804_.toLongArray();
    }

    public LongArrayTag(List<Long> p_128806_) {
        this(toArray(p_128806_));
    }

    private static long[] toArray(List<Long> p_128824_) {
        long[] $$1 = new long[p_128824_.size()];

        for(int $$2 = 0; $$2 < p_128824_.size(); ++$$2) {
            Long $$3 = (Long)p_128824_.get($$2);
            $$1[$$2] = $$3 == null ? 0L : $$3;
        }

        return $$1;
    }

    public void write(DataOutput p_128819_) throws IOException {
        p_128819_.writeInt(this.data.length);
        long[] var2 = this.data;
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            long $$1 = var2[var4];
            p_128819_.writeLong($$1);
        }

    }

    public int sizeInBytes() {
        return 24 + 8 * this.data.length;
    }

    public byte getId() {
        return 12;
    }

    public TagType<LongArrayTag> getType() {
        return TYPE;
    }

    public String toString() {
        return this.getAsString();
    }

    public LongArrayTag copy() {
        long[] $$0 = new long[this.data.length];
        System.arraycopy(this.data, 0, $$0, 0, this.data.length);
        return new LongArrayTag($$0);
    }

    public boolean equals(Object p_128850_) {
        if (this == p_128850_) {
            return true;
        } else {
            return p_128850_ instanceof LongArrayTag && Arrays.equals(this.data, ((LongArrayTag)p_128850_).data);
        }
    }

    public int hashCode() {
        return Arrays.hashCode(this.data);
    }

    public void accept(TagVisitor p_177995_) {
        p_177995_.visitLongArray(this);
    }

    public long[] getAsLongArray() {
        return this.data;
    }

    public int size() {
        return this.data.length;
    }

    public LongTag get(int p_128811_) {
        return LongTag.valueOf(this.data[p_128811_]);
    }

    public LongTag set(int p_128813_, LongTag p_128814_) {
        long $$2 = this.data[p_128813_];
        this.data[p_128813_] = p_128814_.getAsLong();
        return LongTag.valueOf($$2);
    }

    public void add(int p_128832_, LongTag p_128833_) {
        this.data = ArrayUtils.add(this.data, p_128832_, p_128833_.getAsLong());
    }

    public boolean setTag(int p_128816_, Tag p_128817_) {
        if (p_128817_ instanceof NumericTag) {
            this.data[p_128816_] = ((NumericTag)p_128817_).getAsLong();
            return true;
        } else {
            return false;
        }
    }

    public boolean addTag(int p_128835_, Tag p_128836_) {
        if (p_128836_ instanceof NumericTag) {
            this.data = ArrayUtils.add(this.data, p_128835_, ((NumericTag)p_128836_).getAsLong());
            return true;
        } else {
            return false;
        }
    }

    public LongTag remove(int p_128830_) {
        long $$1 = this.data[p_128830_];
        this.data = ArrayUtils.remove(this.data, p_128830_);
        return LongTag.valueOf($$1);
    }

    public byte getElementType() {
        return 4;
    }

    public void clear() {
        this.data = new long[0];
    }

    public StreamTagVisitor.ValueResult accept(StreamTagVisitor p_197497_) {
        return p_197497_.visit(this.data);
    }
}

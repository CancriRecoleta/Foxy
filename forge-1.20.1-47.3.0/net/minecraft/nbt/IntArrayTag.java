//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;

public class IntArrayTag extends CollectionTag<IntTag> {
    private static final int SELF_SIZE_IN_BYTES = 24;
    public static final TagType<IntArrayTag> TYPE = new TagType.VariableSize<IntArrayTag>() {
        public IntArrayTag load(DataInput p_128662_, int p_128663_, NbtAccounter p_128664_) throws IOException {
            p_128664_.accountBytes(24L);
            int $$3 = p_128662_.readInt();
            p_128664_.accountBytes(4L * (long)$$3);
            int[] $$4 = new int[$$3];

            for(int $$5 = 0; $$5 < $$3; ++$$5) {
                $$4[$$5] = p_128662_.readInt();
            }

            return new IntArrayTag($$4);
        }

        public StreamTagVisitor.ValueResult parse(DataInput p_197478_, StreamTagVisitor p_197479_) throws IOException {
            int $$2 = p_197478_.readInt();
            int[] $$3 = new int[$$2];

            for(int $$4 = 0; $$4 < $$2; ++$$4) {
                $$3[$$4] = p_197478_.readInt();
            }

            return p_197479_.visit($$3);
        }

        public void skip(DataInput p_197476_) throws IOException {
            p_197476_.skipBytes(p_197476_.readInt() * 4);
        }

        public String getName() {
            return "INT[]";
        }

        public String getPrettyName() {
            return "TAG_Int_Array";
        }
    };
    private int[] data;

    public IntArrayTag(int[] p_128605_) {
        this.data = p_128605_;
    }

    public IntArrayTag(List<Integer> p_128603_) {
        this(toArray(p_128603_));
    }

    private static int[] toArray(List<Integer> p_128621_) {
        int[] $$1 = new int[p_128621_.size()];

        for(int $$2 = 0; $$2 < p_128621_.size(); ++$$2) {
            Integer $$3 = (Integer)p_128621_.get($$2);
            $$1[$$2] = $$3 == null ? 0 : $$3;
        }

        return $$1;
    }

    public void write(DataOutput p_128616_) throws IOException {
        p_128616_.writeInt(this.data.length);
        int[] var2 = this.data;
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            int $$1 = var2[var4];
            p_128616_.writeInt($$1);
        }

    }

    public int sizeInBytes() {
        return 24 + 4 * this.data.length;
    }

    public byte getId() {
        return 11;
    }

    public TagType<IntArrayTag> getType() {
        return TYPE;
    }

    public String toString() {
        return this.getAsString();
    }

    public IntArrayTag copy() {
        int[] $$0 = new int[this.data.length];
        System.arraycopy(this.data, 0, $$0, 0, this.data.length);
        return new IntArrayTag($$0);
    }

    public boolean equals(Object p_128647_) {
        if (this == p_128647_) {
            return true;
        } else {
            return p_128647_ instanceof IntArrayTag && Arrays.equals(this.data, ((IntArrayTag)p_128647_).data);
        }
    }

    public int hashCode() {
        return Arrays.hashCode(this.data);
    }

    public int[] getAsIntArray() {
        return this.data;
    }

    public void accept(TagVisitor p_177869_) {
        p_177869_.visitIntArray(this);
    }

    public int size() {
        return this.data.length;
    }

    public IntTag get(int p_128608_) {
        return IntTag.valueOf(this.data[p_128608_]);
    }

    public IntTag set(int p_128610_, IntTag p_128611_) {
        int $$2 = this.data[p_128610_];
        this.data[p_128610_] = p_128611_.getAsInt();
        return IntTag.valueOf($$2);
    }

    public void add(int p_128629_, IntTag p_128630_) {
        this.data = ArrayUtils.add(this.data, p_128629_, p_128630_.getAsInt());
    }

    public boolean setTag(int p_128613_, Tag p_128614_) {
        if (p_128614_ instanceof NumericTag) {
            this.data[p_128613_] = ((NumericTag)p_128614_).getAsInt();
            return true;
        } else {
            return false;
        }
    }

    public boolean addTag(int p_128632_, Tag p_128633_) {
        if (p_128633_ instanceof NumericTag) {
            this.data = ArrayUtils.add(this.data, p_128632_, ((NumericTag)p_128633_).getAsInt());
            return true;
        } else {
            return false;
        }
    }

    public IntTag remove(int p_128627_) {
        int $$1 = this.data[p_128627_];
        this.data = ArrayUtils.remove(this.data, p_128627_);
        return IntTag.valueOf($$1);
    }

    public byte getElementType() {
        return 3;
    }

    public void clear() {
        this.data = new int[0];
    }

    public StreamTagVisitor.ValueResult accept(StreamTagVisitor p_197474_) {
        return p_197474_.visit(this.data);
    }
}

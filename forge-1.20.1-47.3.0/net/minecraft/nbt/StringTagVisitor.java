//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.nbt;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class StringTagVisitor implements TagVisitor {
    private static final Pattern SIMPLE_VALUE = Pattern.compile("[A-Za-z0-9._+-]+");
    private final StringBuilder builder = new StringBuilder();

    public StringTagVisitor() {
    }

    public String visit(Tag p_178188_) {
        p_178188_.accept((TagVisitor)this);
        return this.builder.toString();
    }

    public void visitString(StringTag p_178186_) {
        this.builder.append(StringTag.quoteAndEscape(p_178186_.getAsString()));
    }

    public void visitByte(ByteTag p_178164_) {
        this.builder.append(p_178164_.getAsNumber()).append('b');
    }

    public void visitShort(ShortTag p_178184_) {
        this.builder.append(p_178184_.getAsNumber()).append('s');
    }

    public void visitInt(IntTag p_178176_) {
        this.builder.append(p_178176_.getAsNumber());
    }

    public void visitLong(LongTag p_178182_) {
        this.builder.append(p_178182_.getAsNumber()).append('L');
    }

    public void visitFloat(FloatTag p_178172_) {
        this.builder.append(p_178172_.getAsFloat()).append('f');
    }

    public void visitDouble(DoubleTag p_178168_) {
        this.builder.append(p_178168_.getAsDouble()).append('d');
    }

    public void visitByteArray(ByteArrayTag p_178162_) {
        this.builder.append("[B;");
        byte[] $$1 = p_178162_.getAsByteArray();

        for(int $$2 = 0; $$2 < $$1.length; ++$$2) {
            if ($$2 != 0) {
                this.builder.append(',');
            }

            this.builder.append($$1[$$2]).append('B');
        }

        this.builder.append(']');
    }

    public void visitIntArray(IntArrayTag p_178174_) {
        this.builder.append("[I;");
        int[] $$1 = p_178174_.getAsIntArray();

        for(int $$2 = 0; $$2 < $$1.length; ++$$2) {
            if ($$2 != 0) {
                this.builder.append(',');
            }

            this.builder.append($$1[$$2]);
        }

        this.builder.append(']');
    }

    public void visitLongArray(LongArrayTag p_178180_) {
        this.builder.append("[L;");
        long[] $$1 = p_178180_.getAsLongArray();

        for(int $$2 = 0; $$2 < $$1.length; ++$$2) {
            if ($$2 != 0) {
                this.builder.append(',');
            }

            this.builder.append($$1[$$2]).append('L');
        }

        this.builder.append(']');
    }

    public void visitList(ListTag p_178178_) {
        this.builder.append('[');

        for(int $$1 = 0; $$1 < p_178178_.size(); ++$$1) {
            if ($$1 != 0) {
                this.builder.append(',');
            }

            this.builder.append((new StringTagVisitor()).visit(p_178178_.get($$1)));
        }

        this.builder.append(']');
    }

    public void visitCompound(CompoundTag p_178166_) {
        this.builder.append('{');
        List<String> $$1 = Lists.newArrayList(p_178166_.getAllKeys());
        Collections.sort($$1);

        String $$2;
        for(Iterator var3 = $$1.iterator(); var3.hasNext(); this.builder.append(handleEscape($$2)).append(':').append((new StringTagVisitor()).visit(p_178166_.get($$2)))) {
            $$2 = (String)var3.next();
            if (this.builder.length() != 1) {
                this.builder.append(',');
            }
        }

        this.builder.append('}');
    }

    protected static String handleEscape(String p_178160_) {
        return SIMPLE_VALUE.matcher(p_178160_).matches() ? p_178160_ : StringTag.quoteAndEscape(p_178160_);
    }

    public void visitEnd(EndTag p_178170_) {
        this.builder.append("END");
    }
}

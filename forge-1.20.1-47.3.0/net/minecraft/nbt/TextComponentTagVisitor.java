//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.nbt;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteOpenHashSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.slf4j.Logger;

public class TextComponentTagVisitor implements TagVisitor {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int INLINE_LIST_THRESHOLD = 8;
    private static final ByteCollection INLINE_ELEMENT_TYPES = new ByteOpenHashSet(Arrays.asList(1, 2, 3, 4, 5, 6));
    private static final ChatFormatting SYNTAX_HIGHLIGHTING_KEY;
    private static final ChatFormatting SYNTAX_HIGHLIGHTING_STRING;
    private static final ChatFormatting SYNTAX_HIGHLIGHTING_NUMBER;
    private static final ChatFormatting SYNTAX_HIGHLIGHTING_NUMBER_TYPE;
    private static final Pattern SIMPLE_VALUE;
    private static final String NAME_VALUE_SEPARATOR;
    private static final String ELEMENT_SEPARATOR;
    private static final String LIST_OPEN = "[";
    private static final String LIST_CLOSE = "]";
    private static final String LIST_TYPE_SEPARATOR = ";";
    private static final String ELEMENT_SPACING = " ";
    private static final String STRUCT_OPEN = "{";
    private static final String STRUCT_CLOSE = "}";
    private static final String NEWLINE = "\n";
    private final String indentation;
    private final int depth;
    private Component result;

    public TextComponentTagVisitor(String p_178251_, int p_178252_) {
        this.result = CommonComponents.EMPTY;
        this.indentation = p_178251_;
        this.depth = p_178252_;
    }

    public Component visit(Tag p_178282_) {
        p_178282_.accept((TagVisitor)this);
        return this.result;
    }

    public void visitString(StringTag p_178280_) {
        String $$1 = StringTag.quoteAndEscape(p_178280_.getAsString());
        String $$2 = $$1.substring(0, 1);
        Component $$3 = Component.literal($$1.substring(1, $$1.length() - 1)).withStyle(SYNTAX_HIGHLIGHTING_STRING);
        this.result = Component.literal($$2).append((Component)$$3).append($$2);
    }

    public void visitByte(ByteTag p_178258_) {
        Component $$1 = Component.literal("b").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        this.result = Component.literal(String.valueOf(p_178258_.getAsNumber())).append((Component)$$1).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
    }

    public void visitShort(ShortTag p_178278_) {
        Component $$1 = Component.literal("s").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        this.result = Component.literal(String.valueOf(p_178278_.getAsNumber())).append((Component)$$1).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
    }

    public void visitInt(IntTag p_178270_) {
        this.result = Component.literal(String.valueOf(p_178270_.getAsNumber())).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
    }

    public void visitLong(LongTag p_178276_) {
        Component $$1 = Component.literal("L").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        this.result = Component.literal(String.valueOf(p_178276_.getAsNumber())).append((Component)$$1).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
    }

    public void visitFloat(FloatTag p_178266_) {
        Component $$1 = Component.literal("f").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        this.result = Component.literal(String.valueOf(p_178266_.getAsFloat())).append((Component)$$1).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
    }

    public void visitDouble(DoubleTag p_178262_) {
        Component $$1 = Component.literal("d").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        this.result = Component.literal(String.valueOf(p_178262_.getAsDouble())).append((Component)$$1).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
    }

    public void visitByteArray(ByteArrayTag p_178256_) {
        Component $$1 = Component.literal("B").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        MutableComponent $$2 = Component.literal("[").append((Component)$$1).append(";");
        byte[] $$3 = p_178256_.getAsByteArray();

        for(int $$4 = 0; $$4 < $$3.length; ++$$4) {
            MutableComponent $$5 = Component.literal(String.valueOf($$3[$$4])).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
            $$2.append(" ").append((Component)$$5).append((Component)$$1);
            if ($$4 != $$3.length - 1) {
                $$2.append(ELEMENT_SEPARATOR);
            }
        }

        $$2.append("]");
        this.result = $$2;
    }

    public void visitIntArray(IntArrayTag p_178268_) {
        Component $$1 = Component.literal("I").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        MutableComponent $$2 = Component.literal("[").append((Component)$$1).append(";");
        int[] $$3 = p_178268_.getAsIntArray();

        for(int $$4 = 0; $$4 < $$3.length; ++$$4) {
            $$2.append(" ").append((Component)Component.literal(String.valueOf($$3[$$4])).withStyle(SYNTAX_HIGHLIGHTING_NUMBER));
            if ($$4 != $$3.length - 1) {
                $$2.append(ELEMENT_SEPARATOR);
            }
        }

        $$2.append("]");
        this.result = $$2;
    }

    public void visitLongArray(LongArrayTag p_178274_) {
        Component $$1 = Component.literal("L").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        MutableComponent $$2 = Component.literal("[").append((Component)$$1).append(";");
        long[] $$3 = p_178274_.getAsLongArray();

        for(int $$4 = 0; $$4 < $$3.length; ++$$4) {
            Component $$5 = Component.literal(String.valueOf($$3[$$4])).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
            $$2.append(" ").append((Component)$$5).append((Component)$$1);
            if ($$4 != $$3.length - 1) {
                $$2.append(ELEMENT_SEPARATOR);
            }
        }

        $$2.append("]");
        this.result = $$2;
    }

    public void visitList(ListTag p_178272_) {
        if (p_178272_.isEmpty()) {
            this.result = Component.literal("[]");
        } else if (INLINE_ELEMENT_TYPES.contains(p_178272_.getElementType()) && p_178272_.size() <= 8) {
            String $$1 = ELEMENT_SEPARATOR + " ";
            MutableComponent $$2 = Component.literal("[");

            for(int $$3 = 0; $$3 < p_178272_.size(); ++$$3) {
                if ($$3 != 0) {
                    $$2.append($$1);
                }

                $$2.append((new TextComponentTagVisitor(this.indentation, this.depth)).visit(p_178272_.get($$3)));
            }

            $$2.append("]");
            this.result = $$2;
        } else {
            MutableComponent $$4 = Component.literal("[");
            if (!this.indentation.isEmpty()) {
                $$4.append("\n");
            }

            for(int $$5 = 0; $$5 < p_178272_.size(); ++$$5) {
                MutableComponent $$6 = Component.literal(Strings.repeat(this.indentation, this.depth + 1));
                $$6.append((new TextComponentTagVisitor(this.indentation, this.depth + 1)).visit(p_178272_.get($$5)));
                if ($$5 != p_178272_.size() - 1) {
                    $$6.append(ELEMENT_SEPARATOR).append(this.indentation.isEmpty() ? " " : "\n");
                }

                $$4.append((Component)$$6);
            }

            if (!this.indentation.isEmpty()) {
                $$4.append("\n").append(Strings.repeat(this.indentation, this.depth));
            }

            $$4.append("]");
            this.result = $$4;
        }
    }

    public void visitCompound(CompoundTag p_178260_) {
        if (p_178260_.isEmpty()) {
            this.result = Component.literal("{}");
        } else {
            MutableComponent $$1 = Component.literal("{");
            Collection<String> $$2 = p_178260_.getAllKeys();
            if (LOGGER.isDebugEnabled()) {
                List<String> $$3 = Lists.newArrayList(p_178260_.getAllKeys());
                Collections.sort($$3);
                $$2 = $$3;
            }

            if (!this.indentation.isEmpty()) {
                $$1.append("\n");
            }

            MutableComponent $$6;
            for(Iterator<String> $$4 = ((Collection)$$2).iterator(); $$4.hasNext(); $$1.append((Component)$$6)) {
                String $$5 = (String)$$4.next();
                $$6 = Component.literal(Strings.repeat(this.indentation, this.depth + 1)).append(handleEscapePretty($$5)).append(NAME_VALUE_SEPARATOR).append(" ").append((new TextComponentTagVisitor(this.indentation, this.depth + 1)).visit(p_178260_.get($$5)));
                if ($$4.hasNext()) {
                    $$6.append(ELEMENT_SEPARATOR).append(this.indentation.isEmpty() ? " " : "\n");
                }
            }

            if (!this.indentation.isEmpty()) {
                $$1.append("\n").append(Strings.repeat(this.indentation, this.depth));
            }

            $$1.append("}");
            this.result = $$1;
        }
    }

    protected static Component handleEscapePretty(String p_178254_) {
        if (SIMPLE_VALUE.matcher(p_178254_).matches()) {
            return Component.literal(p_178254_).withStyle(SYNTAX_HIGHLIGHTING_KEY);
        } else {
            String $$1 = StringTag.quoteAndEscape(p_178254_);
            String $$2 = $$1.substring(0, 1);
            Component $$3 = Component.literal($$1.substring(1, $$1.length() - 1)).withStyle(SYNTAX_HIGHLIGHTING_KEY);
            return Component.literal($$2).append((Component)$$3).append($$2);
        }
    }

    public void visitEnd(EndTag p_178264_) {
        this.result = CommonComponents.EMPTY;
    }

    static {
        SYNTAX_HIGHLIGHTING_KEY = ChatFormatting.AQUA;
        SYNTAX_HIGHLIGHTING_STRING = ChatFormatting.GREEN;
        SYNTAX_HIGHLIGHTING_NUMBER = ChatFormatting.GOLD;
        SYNTAX_HIGHLIGHTING_NUMBER_TYPE = ChatFormatting.RED;
        SIMPLE_VALUE = Pattern.compile("[A-Za-z0-9._+-]+");
        NAME_VALUE_SEPARATOR = String.valueOf(':');
        ELEMENT_SEPARATOR = String.valueOf(',');
    }
}

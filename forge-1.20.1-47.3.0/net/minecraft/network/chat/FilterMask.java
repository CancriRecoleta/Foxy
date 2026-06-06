//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network.chat;

import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.HoverEvent.Action;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import org.apache.commons.lang3.StringUtils;

public class FilterMask {
    public static final Codec<FilterMask> CODEC = StringRepresentable.fromEnum(Type::values).dispatch(FilterMask::type, Type::codec);
    public static final FilterMask FULLY_FILTERED;
    public static final FilterMask PASS_THROUGH;
    public static final Style FILTERED_STYLE;
    static final Codec<FilterMask> PASS_THROUGH_CODEC;
    static final Codec<FilterMask> FULLY_FILTERED_CODEC;
    static final Codec<FilterMask> PARTIALLY_FILTERED_CODEC;
    private static final char HASH = '#';
    private final BitSet mask;
    private final Type type;

    private FilterMask(BitSet p_243243_, Type p_243249_) {
        this.mask = p_243243_;
        this.type = p_243249_;
    }

    private FilterMask(BitSet p_253780_) {
        this.mask = p_253780_;
        this.type = net.minecraft.network.chat.FilterMask.Type.PARTIALLY_FILTERED;
    }

    public FilterMask(int p_243210_) {
        this(new BitSet(p_243210_), net.minecraft.network.chat.FilterMask.Type.PARTIALLY_FILTERED);
    }

    private Type type() {
        return this.type;
    }

    private BitSet mask() {
        return this.mask;
    }

    public static FilterMask read(FriendlyByteBuf p_243205_) {
        Type $$1 = (Type)p_243205_.readEnum(Type.class);
        FilterMask var10000;
        switch ($$1) {
            case PASS_THROUGH -> var10000 = PASS_THROUGH;
            case FULLY_FILTERED -> var10000 = FULLY_FILTERED;
            case PARTIALLY_FILTERED -> var10000 = new FilterMask(p_243205_.readBitSet(), net.minecraft.network.chat.FilterMask.Type.PARTIALLY_FILTERED);
            default -> throw new IncompatibleClassChangeError();
        }

        return var10000;
    }

    public static void write(FriendlyByteBuf p_243308_, FilterMask p_243231_) {
        p_243308_.writeEnum(p_243231_.type);
        if (p_243231_.type == net.minecraft.network.chat.FilterMask.Type.PARTIALLY_FILTERED) {
            p_243308_.writeBitSet(p_243231_.mask);
        }

    }

    public void setFiltered(int p_243202_) {
        this.mask.set(p_243202_);
    }

    @Nullable
    public String apply(String p_243317_) {
        String var10000;
        switch (this.type) {
            case PASS_THROUGH:
                var10000 = p_243317_;
                break;
            case FULLY_FILTERED:
                var10000 = null;
                break;
            case PARTIALLY_FILTERED:
                char[] $$1 = p_243317_.toCharArray();

                for(int $$2 = 0; $$2 < $$1.length && $$2 < this.mask.length(); ++$$2) {
                    if (this.mask.get($$2)) {
                        $$1[$$2] = '#';
                    }
                }

                var10000 = new String($$1);
                break;
            default:
                throw new IncompatibleClassChangeError();
        }

        return var10000;
    }

    @Nullable
    public Component applyWithFormatting(String p_251709_) {
        MutableComponent var10000;
        switch (this.type) {
            case PASS_THROUGH:
                var10000 = Component.literal(p_251709_);
                break;
            case FULLY_FILTERED:
                var10000 = null;
                break;
            case PARTIALLY_FILTERED:
                MutableComponent $$1 = Component.empty();
                int $$2 = 0;
                boolean $$3 = this.mask.get(0);

                while(true) {
                    int $$4 = $$3 ? this.mask.nextClearBit($$2) : this.mask.nextSetBit($$2);
                    $$4 = $$4 < 0 ? p_251709_.length() : $$4;
                    if ($$4 == $$2) {
                        var10000 = $$1;
                        return var10000;
                    }

                    if ($$3) {
                        $$1.append((Component)Component.literal(StringUtils.repeat('#', $$4 - $$2)).withStyle(FILTERED_STYLE));
                    } else {
                        $$1.append(p_251709_.substring($$2, $$4));
                    }

                    $$3 = !$$3;
                    $$2 = $$4;
                }
            default:
                throw new IncompatibleClassChangeError();
        }

        return var10000;
    }

    public boolean isEmpty() {
        return this.type == net.minecraft.network.chat.FilterMask.Type.PASS_THROUGH;
    }

    public boolean isFullyFiltered() {
        return this.type == net.minecraft.network.chat.FilterMask.Type.FULLY_FILTERED;
    }

    public boolean equals(Object p_254275_) {
        if (this == p_254275_) {
            return true;
        } else if (p_254275_ != null && this.getClass() == p_254275_.getClass()) {
            FilterMask $$1 = (FilterMask)p_254275_;
            return this.mask.equals($$1.mask) && this.type == $$1.type;
        } else {
            return false;
        }
    }

    public int hashCode() {
        int $$0 = this.mask.hashCode();
        $$0 = 31 * $$0 + this.type.hashCode();
        return $$0;
    }

    static {
        FULLY_FILTERED = new FilterMask(new BitSet(0), net.minecraft.network.chat.FilterMask.Type.FULLY_FILTERED);
        PASS_THROUGH = new FilterMask(new BitSet(0), net.minecraft.network.chat.FilterMask.Type.PASS_THROUGH);
        FILTERED_STYLE = Style.EMPTY.withColor(ChatFormatting.DARK_GRAY).withHoverEvent(new HoverEvent(Action.SHOW_TEXT, Component.translatable("chat.filtered")));
        PASS_THROUGH_CODEC = Codec.unit(PASS_THROUGH);
        FULLY_FILTERED_CODEC = Codec.unit(FULLY_FILTERED);
        PARTIALLY_FILTERED_CODEC = ExtraCodecs.BIT_SET.xmap(FilterMask::new, FilterMask::mask);
    }

    private static enum Type implements StringRepresentable {
        PASS_THROUGH("pass_through", () -> {
            return FilterMask.PASS_THROUGH_CODEC;
        }),
        FULLY_FILTERED("fully_filtered", () -> {
            return FilterMask.FULLY_FILTERED_CODEC;
        }),
        PARTIALLY_FILTERED("partially_filtered", () -> {
            return FilterMask.PARTIALLY_FILTERED_CODEC;
        });

        private final String serializedName;
        private final Supplier<Codec<FilterMask>> codec;

        private Type(String p_253679_, Supplier p_253988_) {
            this.serializedName = p_253679_;
            this.codec = p_253988_;
        }

        public String getSerializedName() {
            return this.serializedName;
        }

        private Codec<FilterMask> codec() {
            return (Codec)this.codec.get();
        }
    }
}

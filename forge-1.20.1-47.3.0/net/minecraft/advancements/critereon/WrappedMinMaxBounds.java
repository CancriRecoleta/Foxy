//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;

public class WrappedMinMaxBounds {
    public static final WrappedMinMaxBounds ANY = new WrappedMinMaxBounds((Float)null, (Float)null);
    public static final SimpleCommandExceptionType ERROR_INTS_ONLY = new SimpleCommandExceptionType(Component.translatable("argument.range.ints"));
    @Nullable
    private final Float min;
    @Nullable
    private final Float max;

    public WrappedMinMaxBounds(@Nullable Float p_75356_, @Nullable Float p_75357_) {
        this.min = p_75356_;
        this.max = p_75357_;
    }

    public static WrappedMinMaxBounds exactly(float p_164403_) {
        return new WrappedMinMaxBounds(p_164403_, p_164403_);
    }

    public static WrappedMinMaxBounds between(float p_164405_, float p_164406_) {
        return new WrappedMinMaxBounds(p_164405_, p_164406_);
    }

    public static WrappedMinMaxBounds atLeast(float p_164415_) {
        return new WrappedMinMaxBounds(p_164415_, (Float)null);
    }

    public static WrappedMinMaxBounds atMost(float p_164418_) {
        return new WrappedMinMaxBounds((Float)null, p_164418_);
    }

    public boolean matches(float p_164420_) {
        if (this.min != null && this.max != null && this.min > this.max && this.min > p_164420_ && this.max < p_164420_) {
            return false;
        } else if (this.min != null && this.min > p_164420_) {
            return false;
        } else {
            return this.max == null || !(this.max < p_164420_);
        }
    }

    public boolean matchesSqr(double p_164401_) {
        if (this.min != null && this.max != null && this.min > this.max && (double)(this.min * this.min) > p_164401_ && (double)(this.max * this.max) < p_164401_) {
            return false;
        } else if (this.min != null && (double)(this.min * this.min) > p_164401_) {
            return false;
        } else {
            return this.max == null || !((double)(this.max * this.max) < p_164401_);
        }
    }

    @Nullable
    public Float getMin() {
        return this.min;
    }

    @Nullable
    public Float getMax() {
        return this.max;
    }

    public JsonElement serializeToJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        } else if (this.min != null && this.max != null && this.min.equals(this.max)) {
            return new JsonPrimitive(this.min);
        } else {
            JsonObject $$0 = new JsonObject();
            if (this.min != null) {
                $$0.addProperty("min", this.min);
            }

            if (this.max != null) {
                $$0.addProperty("max", this.min);
            }

            return $$0;
        }
    }

    public static WrappedMinMaxBounds fromJson(@Nullable JsonElement p_164408_) {
        if (p_164408_ != null && !p_164408_.isJsonNull()) {
            if (GsonHelper.isNumberValue(p_164408_)) {
                float $$1 = GsonHelper.convertToFloat(p_164408_, "value");
                return new WrappedMinMaxBounds($$1, $$1);
            } else {
                JsonObject $$2 = GsonHelper.convertToJsonObject(p_164408_, "value");
                Float $$3 = $$2.has("min") ? GsonHelper.getAsFloat($$2, "min") : null;
                Float $$4 = $$2.has("max") ? GsonHelper.getAsFloat($$2, "max") : null;
                return new WrappedMinMaxBounds($$3, $$4);
            }
        } else {
            return ANY;
        }
    }

    public static WrappedMinMaxBounds fromReader(StringReader p_164410_, boolean p_164411_) throws CommandSyntaxException {
        return fromReader(p_164410_, p_164411_, (p_164413_) -> {
            return p_164413_;
        });
    }

    public static WrappedMinMaxBounds fromReader(StringReader p_75360_, boolean p_75361_, Function<Float, Float> p_75362_) throws CommandSyntaxException {
        if (!p_75360_.canRead()) {
            throw MinMaxBounds.ERROR_EMPTY.createWithContext(p_75360_);
        } else {
            int $$3 = p_75360_.getCursor();
            Float $$4 = optionallyFormat(readNumber(p_75360_, p_75361_), p_75362_);
            Float $$6;
            if (p_75360_.canRead(2) && p_75360_.peek() == '.' && p_75360_.peek(1) == '.') {
                p_75360_.skip();
                p_75360_.skip();
                $$6 = optionallyFormat(readNumber(p_75360_, p_75361_), p_75362_);
                if ($$4 == null && $$6 == null) {
                    p_75360_.setCursor($$3);
                    throw MinMaxBounds.ERROR_EMPTY.createWithContext(p_75360_);
                }
            } else {
                if (!p_75361_ && p_75360_.canRead() && p_75360_.peek() == '.') {
                    p_75360_.setCursor($$3);
                    throw ERROR_INTS_ONLY.createWithContext(p_75360_);
                }

                $$6 = $$4;
            }

            if ($$4 == null && $$6 == null) {
                p_75360_.setCursor($$3);
                throw MinMaxBounds.ERROR_EMPTY.createWithContext(p_75360_);
            } else {
                return new WrappedMinMaxBounds($$4, $$6);
            }
        }
    }

    @Nullable
    private static Float readNumber(StringReader p_75368_, boolean p_75369_) throws CommandSyntaxException {
        int $$2 = p_75368_.getCursor();

        while(p_75368_.canRead() && isAllowedNumber(p_75368_, p_75369_)) {
            p_75368_.skip();
        }

        String $$3 = p_75368_.getString().substring($$2, p_75368_.getCursor());
        if ($$3.isEmpty()) {
            return null;
        } else {
            try {
                return Float.parseFloat($$3);
            } catch (NumberFormatException var5) {
                if (p_75369_) {
                    throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidDouble().createWithContext(p_75368_, $$3);
                } else {
                    throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidInt().createWithContext(p_75368_, $$3);
                }
            }
        }
    }

    private static boolean isAllowedNumber(StringReader p_75371_, boolean p_75372_) {
        char $$2 = p_75371_.peek();
        if (($$2 < '0' || $$2 > '9') && $$2 != '-') {
            if (p_75372_ && $$2 == '.') {
                return !p_75371_.canRead(2) || p_75371_.peek(1) != '.';
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    @Nullable
    private static Float optionallyFormat(@Nullable Float p_75364_, Function<Float, Float> p_75365_) {
        return p_75364_ == null ? null : (Float)p_75365_.apply(p_75364_);
    }
}

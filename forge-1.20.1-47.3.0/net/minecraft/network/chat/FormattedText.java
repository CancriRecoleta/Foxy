//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network.chat;

import com.google.common.collect.ImmutableList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.util.Unit;

public interface FormattedText {
    Optional<Unit> STOP_ITERATION = Optional.of(Unit.INSTANCE);
    FormattedText EMPTY = new FormattedText() {
        public <T> Optional<T> visit(ContentConsumer<T> p_130779_) {
            return Optional.empty();
        }

        public <T> Optional<T> visit(StyledContentConsumer<T> p_130781_, Style p_130782_) {
            return Optional.empty();
        }
    };

    <T> Optional<T> visit(ContentConsumer<T> var1);

    <T> Optional<T> visit(StyledContentConsumer<T> var1, Style var2);

    static FormattedText of(final String p_130776_) {
        return new FormattedText() {
            public <T> Optional<T> visit(ContentConsumer<T> p_130787_) {
                return p_130787_.accept(p_130776_);
            }

            public <T> Optional<T> visit(StyledContentConsumer<T> p_130789_, Style p_130790_) {
                return p_130789_.accept(p_130790_, p_130776_);
            }
        };
    }

    static FormattedText of(final String p_130763_, final Style p_130764_) {
        return new FormattedText() {
            public <T> Optional<T> visit(ContentConsumer<T> p_130797_) {
                return p_130797_.accept(p_130763_);
            }

            public <T> Optional<T> visit(StyledContentConsumer<T> p_130799_, Style p_130800_) {
                return p_130799_.accept(p_130764_.applyTo(p_130800_), p_130763_);
            }
        };
    }

    static FormattedText composite(FormattedText... p_130774_) {
        return composite((List)ImmutableList.copyOf(p_130774_));
    }

    static FormattedText composite(final List<? extends FormattedText> p_130769_) {
        return new FormattedText() {
            public <T> Optional<T> visit(ContentConsumer<T> p_130805_) {
                Iterator var2 = p_130769_.iterator();

                Optional $$2;
                do {
                    if (!var2.hasNext()) {
                        return Optional.empty();
                    }

                    FormattedText $$1 = (FormattedText)var2.next();
                    $$2 = $$1.visit(p_130805_);
                } while(!$$2.isPresent());

                return $$2;
            }

            public <T> Optional<T> visit(StyledContentConsumer<T> p_130807_, Style p_130808_) {
                Iterator var3 = p_130769_.iterator();

                Optional $$3;
                do {
                    if (!var3.hasNext()) {
                        return Optional.empty();
                    }

                    FormattedText $$2 = (FormattedText)var3.next();
                    $$3 = $$2.visit(p_130807_, p_130808_);
                } while(!$$3.isPresent());

                return $$3;
            }
        };
    }

    default String getString() {
        StringBuilder $$0 = new StringBuilder();
        this.visit((p_130767_) -> {
            $$0.append(p_130767_);
            return Optional.empty();
        });
        return $$0.toString();
    }

    public interface ContentConsumer<T> {
        Optional<T> accept(String var1);
    }

    public interface StyledContentConsumer<T> {
        Optional<T> accept(Style var1, String var2);
    }
}

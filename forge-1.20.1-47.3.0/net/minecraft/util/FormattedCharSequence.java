//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.Iterator;
import java.util.List;
import net.minecraft.network.chat.Style;

@FunctionalInterface
public interface FormattedCharSequence {
    FormattedCharSequence EMPTY = (p_13704_) -> {
        return true;
    };

    boolean accept(FormattedCharSink var1);

    static FormattedCharSequence codepoint(int p_13694_, Style p_13695_) {
        return (p_13730_) -> {
            return p_13730_.accept(0, p_13695_, p_13694_);
        };
    }

    static FormattedCharSequence forward(String p_13715_, Style p_13716_) {
        return p_13715_.isEmpty() ? EMPTY : (p_13739_) -> {
            return StringDecomposer.iterate(p_13715_, p_13716_, p_13739_);
        };
    }

    static FormattedCharSequence forward(String p_144718_, Style p_144719_, Int2IntFunction p_144720_) {
        return p_144718_.isEmpty() ? EMPTY : (p_144730_) -> {
            return StringDecomposer.iterate(p_144718_, p_144719_, decorateOutput(p_144730_, p_144720_));
        };
    }

    static FormattedCharSequence backward(String p_144724_, Style p_144725_) {
        return p_144724_.isEmpty() ? EMPTY : (p_144716_) -> {
            return StringDecomposer.iterateBackwards(p_144724_, p_144725_, p_144716_);
        };
    }

    static FormattedCharSequence backward(String p_13741_, Style p_13742_, Int2IntFunction p_13743_) {
        return p_13741_.isEmpty() ? EMPTY : (p_13721_) -> {
            return StringDecomposer.iterateBackwards(p_13741_, p_13742_, decorateOutput(p_13721_, p_13743_));
        };
    }

    static FormattedCharSink decorateOutput(FormattedCharSink p_13706_, Int2IntFunction p_13707_) {
        return (p_13711_, p_13712_, p_13713_) -> {
            return p_13706_.accept(p_13711_, p_13712_, (Integer)p_13707_.apply(p_13713_));
        };
    }

    static FormattedCharSequence composite() {
        return EMPTY;
    }

    static FormattedCharSequence composite(FormattedCharSequence p_144712_) {
        return p_144712_;
    }

    static FormattedCharSequence composite(FormattedCharSequence p_13697_, FormattedCharSequence p_13698_) {
        return fromPair(p_13697_, p_13698_);
    }

    static FormattedCharSequence composite(FormattedCharSequence... p_144722_) {
        return fromList(ImmutableList.copyOf(p_144722_));
    }

    static FormattedCharSequence composite(List<FormattedCharSequence> p_13723_) {
        int $$1 = p_13723_.size();
        switch ($$1) {
            case 0 -> return EMPTY;
            case 1 -> return (FormattedCharSequence)p_13723_.get(0);
            case 2 -> return fromPair((FormattedCharSequence)p_13723_.get(0), (FormattedCharSequence)p_13723_.get(1));
            default -> return fromList(ImmutableList.copyOf(p_13723_));
        }
    }

    static FormattedCharSequence fromPair(FormattedCharSequence p_13734_, FormattedCharSequence p_13735_) {
        return (p_13702_) -> {
            return p_13734_.accept(p_13702_) && p_13735_.accept(p_13702_);
        };
    }

    static FormattedCharSequence fromList(List<FormattedCharSequence> p_13745_) {
        return (p_13726_) -> {
            Iterator var2 = p_13745_.iterator();

            FormattedCharSequence $$2;
            do {
                if (!var2.hasNext()) {
                    return true;
                }

                $$2 = (FormattedCharSequence)var2.next();
            } while($$2.accept(p_13726_));

            return false;
        };
    }
}

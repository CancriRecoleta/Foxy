//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.components;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface MultiLineLabel {
    MultiLineLabel EMPTY = new MultiLineLabel() {
        public int renderCentered(GuiGraphics p_283287_, int p_94383_, int p_94384_) {
            return p_94384_;
        }

        public int renderCentered(GuiGraphics p_283384_, int p_94395_, int p_94396_, int p_94397_, int p_94398_) {
            return p_94396_;
        }

        public int renderLeftAligned(GuiGraphics p_283077_, int p_94379_, int p_94380_, int p_282157_, int p_282742_) {
            return p_94380_;
        }

        public int renderLeftAlignedNoShadow(GuiGraphics p_283645_, int p_94389_, int p_94390_, int p_94391_, int p_94392_) {
            return p_94390_;
        }

        public void renderBackgroundCentered(GuiGraphics p_283208_, int p_210825_, int p_210826_, int p_210827_, int p_210828_, int p_210829_) {
        }

        public int getLineCount() {
            return 0;
        }

        public int getWidth() {
            return 0;
        }
    };

    static MultiLineLabel create(Font p_94342_, FormattedText p_94343_, int p_94344_) {
        return createFixed(p_94342_, (List)p_94342_.split(p_94343_, p_94344_).stream().map((p_94374_) -> {
            return new TextWithWidth(p_94374_, p_94342_.width(p_94374_));
        }).collect(ImmutableList.toImmutableList()));
    }

    static MultiLineLabel create(Font p_94346_, FormattedText p_94347_, int p_94348_, int p_94349_) {
        return createFixed(p_94346_, (List)p_94346_.split(p_94347_, p_94348_).stream().limit((long)p_94349_).map((p_94371_) -> {
            return new TextWithWidth(p_94371_, p_94346_.width(p_94371_));
        }).collect(ImmutableList.toImmutableList()));
    }

    static MultiLineLabel create(Font p_94351_, Component... p_94352_) {
        return createFixed(p_94351_, (List)Arrays.stream(p_94352_).map(Component::getVisualOrderText).map((p_94360_) -> {
            return new TextWithWidth(p_94360_, p_94351_.width(p_94360_));
        }).collect(ImmutableList.toImmutableList()));
    }

    static MultiLineLabel create(Font p_169037_, List<Component> p_169038_) {
        return createFixed(p_169037_, (List)p_169038_.stream().map(Component::getVisualOrderText).map((p_169035_) -> {
            return new TextWithWidth(p_169035_, p_169037_.width(p_169035_));
        }).collect(ImmutableList.toImmutableList()));
    }

    static MultiLineLabel createFixed(final Font p_94362_, final List<TextWithWidth> p_94363_) {
        return p_94363_.isEmpty() ? EMPTY : new MultiLineLabel() {
            private final int width = p_94363_.stream().mapToInt((p_232527_) -> {
                return p_232527_.width;
            }).max().orElse(0);

            public int renderCentered(GuiGraphics p_283492_, int p_283184_, int p_282078_) {
                Objects.requireNonNull(p_94362_);
                return this.renderCentered(p_283492_, p_283184_, p_282078_, 9, 16777215);
            }

            public int renderCentered(GuiGraphics p_281603_, int p_281267_, int p_281819_, int p_281545_, int p_282780_) {
                int $$5 = p_281819_;

                for(Iterator var7 = p_94363_.iterator(); var7.hasNext(); $$5 += p_281545_) {
                    TextWithWidth $$6 = (TextWithWidth)var7.next();
                    p_281603_.drawString(p_94362_, $$6.text, p_281267_ - $$6.width / 2, $$5, p_282780_);
                }

                return $$5;
            }

            public int renderLeftAligned(GuiGraphics p_282318_, int p_283665_, int p_283416_, int p_281919_, int p_281686_) {
                int $$5 = p_283416_;

                for(Iterator var7 = p_94363_.iterator(); var7.hasNext(); $$5 += p_281919_) {
                    TextWithWidth $$6 = (TextWithWidth)var7.next();
                    p_282318_.drawString(p_94362_, $$6.text, p_283665_, $$5, p_281686_);
                }

                return $$5;
            }

            public int renderLeftAlignedNoShadow(GuiGraphics p_281782_, int p_282841_, int p_283554_, int p_282768_, int p_283499_) {
                int $$5 = p_283554_;

                for(Iterator var7 = p_94363_.iterator(); var7.hasNext(); $$5 += p_282768_) {
                    TextWithWidth $$6 = (TextWithWidth)var7.next();
                    p_281782_.drawString(p_94362_, $$6.text, p_282841_, $$5, p_283499_, false);
                }

                return $$5;
            }

            public void renderBackgroundCentered(GuiGraphics p_281633_, int p_210832_, int p_210833_, int p_210834_, int p_210835_, int p_210836_) {
                int $$6 = p_94363_.stream().mapToInt((p_232524_) -> {
                    return p_232524_.width;
                }).max().orElse(0);
                if ($$6 > 0) {
                    p_281633_.fill(p_210832_ - $$6 / 2 - p_210835_, p_210833_ - p_210835_, p_210832_ + $$6 / 2 + p_210835_, p_210833_ + p_94363_.size() * p_210834_ + p_210835_, p_210836_);
                }

            }

            public int getLineCount() {
                return p_94363_.size();
            }

            public int getWidth() {
                return this.width;
            }
        };
    }

    int renderCentered(GuiGraphics var1, int var2, int var3);

    int renderCentered(GuiGraphics var1, int var2, int var3, int var4, int var5);

    int renderLeftAligned(GuiGraphics var1, int var2, int var3, int var4, int var5);

    int renderLeftAlignedNoShadow(GuiGraphics var1, int var2, int var3, int var4, int var5);

    void renderBackgroundCentered(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6);

    int getLineCount();

    int getWidth();

    @OnlyIn(Dist.CLIENT)
    public static class TextWithWidth {
        final FormattedCharSequence text;
        final int width;

        TextWithWidth(FormattedCharSequence p_94430_, int p_94431_) {
            this.text = p_94430_;
            this.width = p_94431_;
        }
    }
}

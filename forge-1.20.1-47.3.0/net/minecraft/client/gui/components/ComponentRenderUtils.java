//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.components;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.ComponentCollector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ComponentRenderUtils {
    private static final FormattedCharSequence INDENT;

    public ComponentRenderUtils() {
    }

    private static String stripColor(String p_94000_) {
        return (Boolean)Minecraft.getInstance().options.chatColors().get() ? p_94000_ : ChatFormatting.stripFormatting(p_94000_);
    }

    public static List<FormattedCharSequence> wrapComponents(FormattedText p_94006_, int p_94007_, Font p_94008_) {
        ComponentCollector $$3 = new ComponentCollector();
        p_94006_.visit((p_93997_, p_93998_) -> {
            $$3.append(FormattedText.of(stripColor(p_93998_), p_93997_));
            return Optional.empty();
        }, Style.EMPTY);
        List<FormattedCharSequence> $$4 = Lists.newArrayList();
        p_94008_.getSplitter().splitLines($$3.getResultOrEmpty(), p_94007_, Style.EMPTY, (p_94003_, p_94004_) -> {
            FormattedCharSequence $$3 = Language.getInstance().getVisualOrder(p_94003_);
            $$4.add(p_94004_ ? FormattedCharSequence.composite(INDENT, $$3) : $$3);
        });
        return $$4.isEmpty() ? Lists.newArrayList(new FormattedCharSequence[]{FormattedCharSequence.EMPTY}) : $$4;
    }

    static {
        INDENT = FormattedCharSequence.codepoint(32, Style.EMPTY);
    }
}

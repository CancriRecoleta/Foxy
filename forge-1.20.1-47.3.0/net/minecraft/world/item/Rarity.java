//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.item;

import java.util.function.UnaryOperator;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.minecraftforge.common.IExtensibleEnum;

public enum Rarity implements IExtensibleEnum {
    COMMON(ChatFormatting.WHITE),
    UNCOMMON(ChatFormatting.YELLOW),
    RARE(ChatFormatting.AQUA),
    EPIC(ChatFormatting.LIGHT_PURPLE);

    /** @deprecated */
    @Deprecated
    public final ChatFormatting color;
    private final UnaryOperator<Style> styleModifier;

    private Rarity(ChatFormatting p_43028_) {
        this.color = p_43028_;
        this.styleModifier = (style) -> {
            return style.withColor(p_43028_);
        };
    }

    private Rarity(UnaryOperator styleModifier) {
        this.color = ChatFormatting.BLACK;
        this.styleModifier = styleModifier;
    }

    public UnaryOperator<Style> getStyleModifier() {
        return this.styleModifier;
    }

    public static Rarity create(String name, ChatFormatting p_43028_) {
        throw new IllegalStateException("Enum not extended");
    }

    public static Rarity create(String name, UnaryOperator<Style> styleModifier) {
        throw new IllegalStateException("Enum not extended");
    }
}

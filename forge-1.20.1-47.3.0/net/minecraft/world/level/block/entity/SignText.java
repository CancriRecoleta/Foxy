//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;

public class SignText {
    private static final Codec<Component[]> LINES_CODEC;
    public static final Codec<SignText> DIRECT_CODEC;
    public static final int LINES = 4;
    private final Component[] messages;
    private final Component[] filteredMessages;
    private final DyeColor color;
    private final boolean hasGlowingText;
    @Nullable
    private FormattedCharSequence[] renderMessages;
    private boolean renderMessagedFiltered;

    public SignText() {
        this(emptyMessages(), emptyMessages(), DyeColor.BLACK, false);
    }

    public SignText(Component[] p_277506_, Component[] p_277908_, DyeColor p_277883_, boolean p_278091_) {
        this.messages = p_277506_;
        this.filteredMessages = p_277908_;
        this.color = p_277883_;
        this.hasGlowingText = p_278091_;
    }

    private static Component[] emptyMessages() {
        return new Component[]{CommonComponents.EMPTY, CommonComponents.EMPTY, CommonComponents.EMPTY, CommonComponents.EMPTY};
    }

    private static SignText load(Component[] p_277661_, Optional<Component[]> p_277768_, DyeColor p_277345_, boolean p_278008_) {
        Component[] $$4 = (Component[])p_277768_.orElseGet(SignText::emptyMessages);
        populateFilteredMessagesWithRawMessages(p_277661_, $$4);
        return new SignText(p_277661_, $$4, p_277345_, p_278008_);
    }

    private static void populateFilteredMessagesWithRawMessages(Component[] p_277696_, Component[] p_277436_) {
        for(int $$2 = 0; $$2 < 4; ++$$2) {
            if (p_277436_[$$2].equals(CommonComponents.EMPTY)) {
                p_277436_[$$2] = p_277696_[$$2];
            }
        }

    }

    public boolean hasGlowingText() {
        return this.hasGlowingText;
    }

    public SignText setHasGlowingText(boolean p_277953_) {
        return p_277953_ == this.hasGlowingText ? this : new SignText(this.messages, this.filteredMessages, this.color, p_277953_);
    }

    public DyeColor getColor() {
        return this.color;
    }

    public SignText setColor(DyeColor p_277507_) {
        return p_277507_ == this.getColor() ? this : new SignText(this.messages, this.filteredMessages, p_277507_, this.hasGlowingText);
    }

    public Component getMessage(int p_277404_, boolean p_278108_) {
        return this.getMessages(p_278108_)[p_277404_];
    }

    public SignText setMessage(int p_277878_, Component p_277360_) {
        return this.setMessage(p_277878_, p_277360_, p_277360_);
    }

    public SignText setMessage(int p_277690_, Component p_277852_, Component p_277564_) {
        Component[] $$3 = (Component[])Arrays.copyOf(this.messages, this.messages.length);
        Component[] $$4 = (Component[])Arrays.copyOf(this.filteredMessages, this.filteredMessages.length);
        $$3[p_277690_] = p_277852_;
        $$4[p_277690_] = p_277564_;
        return new SignText($$3, $$4, this.color, this.hasGlowingText);
    }

    public boolean hasMessage(Player p_277764_) {
        return Arrays.stream(this.getMessages(p_277764_.isTextFilteringEnabled())).anyMatch((p_277499_) -> {
            return !p_277499_.getString().isEmpty();
        });
    }

    public Component[] getMessages(boolean p_277992_) {
        return p_277992_ ? this.filteredMessages : this.messages;
    }

    public FormattedCharSequence[] getRenderMessages(boolean p_277336_, Function<Component, FormattedCharSequence> p_277538_) {
        if (this.renderMessages == null || this.renderMessagedFiltered != p_277336_) {
            this.renderMessagedFiltered = p_277336_;
            this.renderMessages = new FormattedCharSequence[4];

            for(int $$2 = 0; $$2 < 4; ++$$2) {
                this.renderMessages[$$2] = (FormattedCharSequence)p_277538_.apply(this.getMessage($$2, p_277336_));
            }
        }

        return this.renderMessages;
    }

    private Optional<Component[]> getOnlyFilteredMessages() {
        Component[] $$0 = new Component[4];
        boolean $$1 = false;

        for(int $$2 = 0; $$2 < 4; ++$$2) {
            Component $$3 = this.filteredMessages[$$2];
            if (!$$3.equals(this.messages[$$2])) {
                $$0[$$2] = $$3;
                $$1 = true;
            } else {
                $$0[$$2] = CommonComponents.EMPTY;
            }
        }

        return $$1 ? Optional.of($$0) : Optional.empty();
    }

    public boolean hasAnyClickCommands(Player p_277865_) {
        Component[] var2 = this.getMessages(p_277865_.isTextFilteringEnabled());
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Component $$1 = var2[var4];
            Style $$2 = $$1.getStyle();
            ClickEvent $$3 = $$2.getClickEvent();
            if ($$3 != null && $$3.getAction() == Action.RUN_COMMAND) {
                return true;
            }
        }

        return false;
    }

    static {
        LINES_CODEC = ExtraCodecs.FLAT_COMPONENT.listOf().comapFlatMap((p_277790_) -> {
            return Util.fixedSize((List)p_277790_, 4).map((p_277881_) -> {
                return new Component[]{(Component)p_277881_.get(0), (Component)p_277881_.get(1), (Component)p_277881_.get(2), (Component)p_277881_.get(3)};
            });
        }, (p_277460_) -> {
            return List.of(p_277460_[0], p_277460_[1], p_277460_[2], p_277460_[3]);
        });
        DIRECT_CODEC = RecordCodecBuilder.create((p_277675_) -> {
            return p_277675_.group(LINES_CODEC.fieldOf("messages").forGetter((p_277822_) -> {
                return p_277822_.messages;
            }), LINES_CODEC.optionalFieldOf("filtered_messages").forGetter(SignText::getOnlyFilteredMessages), DyeColor.CODEC.fieldOf("color").orElse(DyeColor.BLACK).forGetter((p_277343_) -> {
                return p_277343_.color;
            }), Codec.BOOL.fieldOf("has_glowing_text").orElse(false).forGetter((p_277555_) -> {
                return p_277555_.hasGlowingText;
            })).apply(p_277675_, SignText::load);
        });
    }
}

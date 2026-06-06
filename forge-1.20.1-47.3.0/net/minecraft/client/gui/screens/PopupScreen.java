//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.screens;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.FormattedText;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PopupScreen extends Screen {
    private static final int BUTTON_PADDING = 20;
    private static final int BUTTON_MARGIN = 5;
    private static final int BUTTON_HEIGHT = 20;
    private final Component narrationMessage;
    private final FormattedText message;
    private final ImmutableList<ButtonOption> buttonOptions;
    private MultiLineLabel messageLines;
    private int contentTop;
    private int buttonWidth;

    protected PopupScreen(Component p_96345_, List<Component> p_96346_, ImmutableList<ButtonOption> p_96347_) {
        super(p_96345_);
        this.messageLines = MultiLineLabel.EMPTY;
        this.message = FormattedText.composite(p_96346_);
        this.narrationMessage = CommonComponents.joinForNarration(p_96345_, ComponentUtils.formatList(p_96346_, (Component)CommonComponents.EMPTY));
        this.buttonOptions = p_96347_;
    }

    public Component getNarrationMessage() {
        return this.narrationMessage;
    }

    public void init() {
        ButtonOption $$0;
        for(UnmodifiableIterator var1 = this.buttonOptions.iterator(); var1.hasNext(); this.buttonWidth = Math.max(this.buttonWidth, 20 + this.font.width((FormattedText)$$0.message) + 20)) {
            $$0 = (ButtonOption)var1.next();
        }

        int $$1 = 5 + this.buttonWidth + 5;
        int $$2 = $$1 * this.buttonOptions.size();
        this.messageLines = MultiLineLabel.create(this.font, this.message, $$2);
        int var10000 = this.messageLines.getLineCount();
        Objects.requireNonNull(this.font);
        int $$3 = var10000 * 9;
        this.contentTop = (int)((double)this.height / 2.0 - (double)$$3 / 2.0);
        var10000 = this.contentTop + $$3;
        Objects.requireNonNull(this.font);
        int $$4 = var10000 + 9 * 2;
        int $$5 = (int)((double)this.width / 2.0 - (double)$$2 / 2.0);

        for(UnmodifiableIterator var6 = this.buttonOptions.iterator(); var6.hasNext(); $$5 += $$1) {
            ButtonOption $$6 = (ButtonOption)var6.next();
            this.addRenderableWidget(Button.builder($$6.message, $$6.onPress).bounds($$5, $$4, this.buttonWidth, 20).build());
        }

    }

    public void render(GuiGraphics p_283167_, int p_96350_, int p_96351_, float p_96352_) {
        this.renderDirtBackground(p_283167_);
        Font var10001 = this.font;
        Component var10002 = this.title;
        int var10003 = this.width / 2;
        int var10004 = this.contentTop;
        Objects.requireNonNull(this.font);
        p_283167_.drawCenteredString(var10001, (Component)var10002, var10003, var10004 - 9 * 2, -1);
        this.messageLines.renderCentered(p_283167_, this.width / 2, this.contentTop);
        super.render(p_283167_, p_96350_, p_96351_, p_96352_);
    }

    public boolean shouldCloseOnEsc() {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public static final class ButtonOption {
        final Component message;
        final Button.OnPress onPress;

        public ButtonOption(Component p_96362_, Button.OnPress p_96363_) {
            this.message = p_96362_;
            this.onPress = p_96363_;
        }
    }
}

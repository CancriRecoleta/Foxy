//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.screens;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ConfirmScreen extends Screen {
    private static final int MARGIN = 20;
    private final Component message;
    private MultiLineLabel multilineMessage;
    protected Component yesButton;
    protected Component noButton;
    private int delayTicker;
    protected final BooleanConsumer callback;
    private final List<Button> exitButtons;

    public ConfirmScreen(BooleanConsumer p_95654_, Component p_95655_, Component p_95656_) {
        this(p_95654_, p_95655_, p_95656_, CommonComponents.GUI_YES, CommonComponents.GUI_NO);
    }

    public ConfirmScreen(BooleanConsumer p_95658_, Component p_95659_, Component p_95660_, Component p_95661_, Component p_95662_) {
        super(p_95659_);
        this.multilineMessage = MultiLineLabel.EMPTY;
        this.exitButtons = Lists.newArrayList();
        this.callback = p_95658_;
        this.message = p_95660_;
        this.yesButton = p_95661_;
        this.noButton = p_95662_;
    }

    public Component getNarrationMessage() {
        return CommonComponents.joinForNarration(super.getNarrationMessage(), this.message);
    }

    protected void init() {
        super.init();
        this.multilineMessage = MultiLineLabel.create(this.font, this.message, this.width - 50);
        int $$0 = Mth.clamp(this.messageTop() + this.messageHeight() + 20, this.height / 6 + 96, this.height - 24);
        this.exitButtons.clear();
        this.addButtons($$0);
    }

    protected void addButtons(int p_169252_) {
        this.addExitButton(Button.builder(this.yesButton, (p_169259_) -> {
            this.callback.accept(true);
        }).bounds(this.width / 2 - 155, p_169252_, 150, 20).build());
        this.addExitButton(Button.builder(this.noButton, (p_169257_) -> {
            this.callback.accept(false);
        }).bounds(this.width / 2 - 155 + 160, p_169252_, 150, 20).build());
    }

    protected void addExitButton(Button p_169254_) {
        this.exitButtons.add((Button)this.addRenderableWidget(p_169254_));
    }

    public void render(GuiGraphics p_281588_, int p_283592_, int p_283446_, float p_282443_) {
        this.renderBackground(p_281588_);
        p_281588_.drawCenteredString(this.font, this.title, this.width / 2, this.titleTop(), 16777215);
        this.multilineMessage.renderCentered(p_281588_, this.width / 2, this.messageTop());
        super.render(p_281588_, p_283592_, p_283446_, p_282443_);
    }

    private int titleTop() {
        int $$0 = (this.height - this.messageHeight()) / 2;
        int var10000 = $$0 - 20;
        Objects.requireNonNull(this.font);
        return Mth.clamp(var10000 - 9, 10, 80);
    }

    private int messageTop() {
        return this.titleTop() + 20;
    }

    private int messageHeight() {
        int var10000 = this.multilineMessage.getLineCount();
        Objects.requireNonNull(this.font);
        return var10000 * 9;
    }

    public void setDelay(int p_95664_) {
        this.delayTicker = p_95664_;

        Button $$1;
        for(Iterator var2 = this.exitButtons.iterator(); var2.hasNext(); $$1.active = false) {
            $$1 = (Button)var2.next();
        }

    }

    public void tick() {
        super.tick();
        Button $$0;
        if (--this.delayTicker == 0) {
            for(Iterator var1 = this.exitButtons.iterator(); var1.hasNext(); $$0.active = true) {
                $$0 = (Button)var1.next();
            }
        }

    }

    public boolean shouldCloseOnEsc() {
        return false;
    }

    public boolean keyPressed(int p_95666_, int p_95667_, int p_95668_) {
        if (p_95666_ == 256) {
            this.callback.accept(false);
            return true;
        } else {
            return super.keyPressed(p_95666_, p_95667_, p_95668_);
        }
    }
}

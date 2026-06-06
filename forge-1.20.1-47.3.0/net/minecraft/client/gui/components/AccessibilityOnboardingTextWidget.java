//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.components;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AccessibilityOnboardingTextWidget extends MultiLineTextWidget {
    private static final int BORDER_COLOR_FOCUSED = -1;
    private static final int BORDER_COLOR = -6250336;
    private static final int BACKGROUND_COLOR = 1426063360;
    private static final int PADDING = 3;
    private static final int BORDER = 1;

    public AccessibilityOnboardingTextWidget(Font p_265441_, Component p_265136_, int p_265628_) {
        super(p_265136_, p_265441_);
        this.setMaxWidth(p_265628_);
        this.setCentered(true);
        this.active = true;
    }

    protected void updateWidgetNarration(NarrationElementOutput p_265791_) {
        p_265791_.add(NarratedElementType.TITLE, this.getMessage());
    }

    public void renderWidget(GuiGraphics p_283425_, int p_267981_, int p_268038_, float p_268050_) {
        int $$4 = this.getX() - 3;
        int $$5 = this.getY() - 3;
        int $$6 = this.getX() + this.getWidth() + 3;
        int $$7 = this.getY() + this.getHeight() + 3;
        int $$8 = this.isFocused() ? -1 : -6250336;
        p_283425_.fill($$4 - 1, $$5 - 1, $$4, $$7 + 1, $$8);
        p_283425_.fill($$6, $$5 - 1, $$6 + 1, $$7 + 1, $$8);
        p_283425_.fill($$4, $$5, $$6, $$5 - 1, $$8);
        p_283425_.fill($$4, $$7, $$6, $$7 + 1, $$8);
        p_283425_.fill($$4, $$5, $$6, $$7, 1426063360);
        super.renderWidget(p_283425_, p_267981_, p_268038_, p_268050_);
    }

    public void playDownSound(SoundManager p_265780_) {
    }
}

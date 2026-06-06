//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.BelowOrAboveWidgetTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.MenuTooltipPositioner;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractWidget implements Renderable, GuiEventListener, LayoutElement, NarratableEntry {
    public static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
    public static final ResourceLocation ACCESSIBILITY_TEXTURE = new ResourceLocation("textures/gui/accessibility.png");
    public static final double PERIOD_PER_SCROLLED_PIXEL = 0.5;
    public static final double MIN_SCROLL_PERIOD = 3.0;
    public int width;
    public int height;
    public int x;
    public int y;
    public Component message;
    public boolean isHovered;
    public boolean active = true;
    public boolean visible = true;
    public float alpha = 1.0F;
    public int tabOrderGroup;
    public boolean focused;
    @Nullable
    public Tooltip tooltip;
    public int tooltipMsDelay;
    public long hoverOrFocusedStartTime;
    public boolean wasHoveredOrFocused;
    public static final int UNSET_FG_COLOR = -1;
    public int packedFGColor = -1;

    public AbstractWidget(int p_93629_, int p_93630_, int p_93631_, int p_93632_, Component p_93633_) {
        this.x = p_93629_;
        this.y = p_93630_;
        this.width = p_93631_;
        this.height = p_93632_;
        this.message = p_93633_;
    }

    public int getHeight() {
        return this.height;
    }

    public void render(GuiGraphics p_282421_, int p_93658_, int p_93659_, float p_93660_) {
        if (this.visible) {
            this.isHovered = p_93658_ >= this.getX() && p_93659_ >= this.getY() && p_93658_ < this.getX() + this.width && p_93659_ < this.getY() + this.height;
            this.renderWidget(p_282421_, p_93658_, p_93659_, p_93660_);
            this.updateTooltip();
        }

    }

    public void updateTooltip() {
        if (this.tooltip != null) {
            boolean flag = this.isHovered || this.isFocused() && Minecraft.getInstance().getLastInputType().isKeyboard();
            if (flag != this.wasHoveredOrFocused) {
                if (flag) {
                    this.hoverOrFocusedStartTime = Util.getMillis();
                }

                this.wasHoveredOrFocused = flag;
            }

            if (flag && Util.getMillis() - this.hoverOrFocusedStartTime > (long)this.tooltipMsDelay) {
                Screen screen = Minecraft.getInstance().screen;
                if (screen != null) {
                    screen.setTooltipForNextRenderPass(this.tooltip, this.createTooltipPositioner(), this.isFocused());
                }
            }
        }

    }

    public ClientTooltipPositioner createTooltipPositioner() {
        return (ClientTooltipPositioner)(!this.isHovered && this.isFocused() && Minecraft.getInstance().getLastInputType().isKeyboard() ? new BelowOrAboveWidgetTooltipPositioner(this) : new MenuTooltipPositioner(this));
    }

    public void setTooltip(@Nullable Tooltip p_259796_) {
        this.tooltip = p_259796_;
    }

    @Nullable
    public Tooltip getTooltip() {
        return this.tooltip;
    }

    public void setTooltipDelay(int p_259732_) {
        this.tooltipMsDelay = p_259732_;
    }

    public MutableComponent createNarrationMessage() {
        return wrapDefaultNarrationMessage(this.getMessage());
    }

    public static MutableComponent wrapDefaultNarrationMessage(Component p_168800_) {
        return Component.translatable("gui.narrate.button", p_168800_);
    }

    public abstract void renderWidget(GuiGraphics var1, int var2, int var3, float var4);

    public static void renderScrollingString(GuiGraphics p_281620_, Font p_282651_, Component p_281467_, int p_283621_, int p_282084_, int p_283398_, int p_281938_, int p_283471_) {
        int i = p_282651_.width((FormattedText)p_281467_);
        int j = (p_282084_ + p_281938_ - 9) / 2 + 1;
        int k = p_283398_ - p_283621_;
        if (i > k) {
            int l = i - k;
            double d0 = (double)Util.getMillis() / 1000.0;
            double d1 = Math.max((double)l * 0.5, 3.0);
            double d2 = Math.sin(1.5707963267948966 * Math.cos(6.283185307179586 * d0 / d1)) / 2.0 + 0.5;
            double d3 = Mth.lerp(d2, 0.0, (double)l);
            p_281620_.enableScissor(p_283621_, p_282084_, p_283398_, p_281938_);
            p_281620_.drawString(p_282651_, p_281467_, p_283621_ - (int)d3, j, p_283471_);
            p_281620_.disableScissor();
        } else {
            p_281620_.drawCenteredString(p_282651_, p_281467_, (p_283621_ + p_283398_) / 2, j, p_283471_);
        }

    }

    public void renderScrollingString(GuiGraphics p_281857_, Font p_282790_, int p_282664_, int p_282944_) {
        int i = this.getX() + p_282664_;
        int j = this.getX() + this.getWidth() - p_282664_;
        renderScrollingString(p_281857_, p_282790_, this.getMessage(), i, this.getY(), j, this.getY() + this.getHeight(), p_282944_);
    }

    public void renderTexture(GuiGraphics p_283546_, ResourceLocation p_281674_, int p_281808_, int p_282444_, int p_283651_, int p_281601_, int p_283472_, int p_282390_, int p_281441_, int p_281711_, int p_281541_) {
        int i = p_281601_;
        if (!this.isActive()) {
            i = p_281601_ + p_283472_ * 2;
        } else if (this.isHoveredOrFocused()) {
            i = p_281601_ + p_283472_;
        }

        RenderSystem.enableDepthTest();
        p_283546_.blit(p_281674_, p_281808_, p_282444_, (float)p_283651_, (float)i, p_282390_, p_281441_, p_281711_, p_281541_);
    }

    public void onClick(double p_93634_, double p_93635_) {
    }

    public void onRelease(double p_93669_, double p_93670_) {
    }

    public void onDrag(double p_93636_, double p_93637_, double p_93638_, double p_93639_) {
    }

    public boolean mouseClicked(double p_93641_, double p_93642_, int p_93643_) {
        if (this.active && this.visible) {
            if (this.isValidClickButton(p_93643_)) {
                boolean flag = this.clicked(p_93641_, p_93642_);
                if (flag) {
                    this.playDownSound(Minecraft.getInstance().getSoundManager());
                    this.onClick(p_93641_, p_93642_);
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    public boolean mouseReleased(double p_93684_, double p_93685_, int p_93686_) {
        if (this.isValidClickButton(p_93686_)) {
            this.onRelease(p_93684_, p_93685_);
            return true;
        } else {
            return false;
        }
    }

    public boolean isValidClickButton(int p_93652_) {
        return p_93652_ == 0;
    }

    public boolean mouseDragged(double p_93645_, double p_93646_, int p_93647_, double p_93648_, double p_93649_) {
        if (this.isValidClickButton(p_93647_)) {
            this.onDrag(p_93645_, p_93646_, p_93648_, p_93649_);
            return true;
        } else {
            return false;
        }
    }

    public boolean clicked(double p_93681_, double p_93682_) {
        return this.active && this.visible && p_93681_ >= (double)this.getX() && p_93682_ >= (double)this.getY() && p_93681_ < (double)(this.getX() + this.width) && p_93682_ < (double)(this.getY() + this.height);
    }

    @Nullable
    public ComponentPath nextFocusPath(FocusNavigationEvent p_265640_) {
        if (this.active && this.visible) {
            return !this.isFocused() ? ComponentPath.leaf(this) : null;
        } else {
            return null;
        }
    }

    public boolean isMouseOver(double p_93672_, double p_93673_) {
        return this.active && this.visible && p_93672_ >= (double)this.getX() && p_93673_ >= (double)this.getY() && p_93672_ < (double)(this.getX() + this.width) && p_93673_ < (double)(this.getY() + this.height);
    }

    public void playDownSound(SoundManager p_93665_) {
        p_93665_.play(SimpleSoundInstance.forUI((Holder)SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int p_93675_) {
        this.width = p_93675_;
    }

    public void setHeight(int value) {
        this.height = value;
    }

    public void setAlpha(float p_93651_) {
        this.alpha = p_93651_;
    }

    public void setMessage(Component p_93667_) {
        this.message = p_93667_;
    }

    public Component getMessage() {
        return this.message;
    }

    public boolean isFocused() {
        return this.focused;
    }

    public boolean isHovered() {
        return this.isHovered;
    }

    public boolean isHoveredOrFocused() {
        return this.isHovered() || this.isFocused();
    }

    public boolean isActive() {
        return this.visible && this.active;
    }

    public void setFocused(boolean p_93693_) {
        this.focused = p_93693_;
    }

    public int getFGColor() {
        if (this.packedFGColor != -1) {
            return this.packedFGColor;
        } else {
            return this.active ? 16777215 : 10526880;
        }
    }

    public void setFGColor(int color) {
        this.packedFGColor = color;
    }

    public void clearFGColor() {
        this.packedFGColor = -1;
    }

    public NarratableEntry.NarrationPriority narrationPriority() {
        if (this.isFocused()) {
            return net.minecraft.client.gui.narration.NarratableEntry.NarrationPriority.FOCUSED;
        } else {
            return this.isHovered ? net.minecraft.client.gui.narration.NarratableEntry.NarrationPriority.HOVERED : net.minecraft.client.gui.narration.NarratableEntry.NarrationPriority.NONE;
        }
    }

    public final void updateNarration(NarrationElementOutput p_259921_) {
        this.updateWidgetNarration(p_259921_);
        if (this.tooltip != null) {
            this.tooltip.updateNarration(p_259921_);
        }

    }

    public abstract void updateWidgetNarration(NarrationElementOutput var1);

    public void defaultButtonNarrationText(NarrationElementOutput p_168803_) {
        p_168803_.add(NarratedElementType.TITLE, (Component)this.createNarrationMessage());
        if (this.active) {
            if (this.isFocused()) {
                p_168803_.add(NarratedElementType.USAGE, (Component)Component.translatable("narration.button.usage.focused"));
            } else {
                p_168803_.add(NarratedElementType.USAGE, (Component)Component.translatable("narration.button.usage.hovered"));
            }
        }

    }

    public int getX() {
        return this.x;
    }

    public void setX(int p_254495_) {
        this.x = p_254495_;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int p_253718_) {
        this.y = p_253718_;
    }

    public void visitWidgets(Consumer<AbstractWidget> p_265566_) {
        p_265566_.accept(this);
    }

    public ScreenRectangle getRectangle() {
        return LayoutElement.super.getRectangle();
    }

    public int getTabOrderGroup() {
        return this.tabOrderGroup;
    }

    public void setTabOrderGroup(int p_268123_) {
        this.tabOrderGroup = p_268123_;
    }
}

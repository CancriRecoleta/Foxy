//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.screens.packs;

import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TransferableSelectionList extends ObjectSelectionList<PackEntry> {
    static final ResourceLocation ICON_OVERLAY_LOCATION = new ResourceLocation("textures/gui/resource_packs.png");
    static final Component INCOMPATIBLE_TITLE = Component.translatable("pack.incompatible");
    static final Component INCOMPATIBLE_CONFIRM_TITLE = Component.translatable("pack.incompatible.confirm.title");
    private final Component title;
    final PackSelectionScreen screen;

    public TransferableSelectionList(Minecraft p_265029_, PackSelectionScreen p_265777_, int p_265774_, int p_265153_, Component p_265124_) {
        super(p_265029_, p_265774_, p_265153_, 32, p_265153_ - 55 + 4, 36);
        this.screen = p_265777_;
        this.title = p_265124_;
        this.centerListVertically = false;
        Objects.requireNonNull(p_265029_.font);
        this.setRenderHeader(true, (int)(9.0F * 1.5F));
    }

    protected void renderHeader(GuiGraphics p_282135_, int p_282032_, int p_283198_) {
        Component $$3 = Component.empty().append(this.title).withStyle(ChatFormatting.UNDERLINE, ChatFormatting.BOLD);
        p_282135_.drawString(this.minecraft.font, (Component)$$3, p_282032_ + this.width / 2 - this.minecraft.font.width((FormattedText)$$3) / 2, Math.min(this.y0 + 3, p_283198_), 16777215, false);
    }

    public int getRowWidth() {
        return this.width;
    }

    protected int getScrollbarPosition() {
        return this.x1 - 6;
    }

    public boolean keyPressed(int p_265499_, int p_265510_, int p_265548_) {
        if (this.getSelected() != null) {
            switch (p_265499_) {
                case 32:
                case 257:
                    ((PackEntry)this.getSelected()).keyboardSelection();
                    return true;
                default:
                    if (Screen.hasShiftDown()) {
                        switch (p_265499_) {
                            case 264:
                                ((PackEntry)this.getSelected()).keyboardMoveDown();
                                return true;
                            case 265:
                                ((PackEntry)this.getSelected()).keyboardMoveUp();
                                return true;
                        }
                    }
            }
        }

        return super.keyPressed(p_265499_, p_265510_, p_265548_);
    }

    @OnlyIn(Dist.CLIENT)
    public static class PackEntry extends ObjectSelectionList.Entry<PackEntry> {
        private static final int ICON_OVERLAY_X_MOVE_RIGHT = 0;
        private static final int ICON_OVERLAY_X_MOVE_LEFT = 32;
        private static final int ICON_OVERLAY_X_MOVE_DOWN = 64;
        private static final int ICON_OVERLAY_X_MOVE_UP = 96;
        private static final int ICON_OVERLAY_Y_UNSELECTED = 0;
        private static final int ICON_OVERLAY_Y_SELECTED = 32;
        private static final int MAX_DESCRIPTION_WIDTH_PIXELS = 157;
        private static final int MAX_NAME_WIDTH_PIXELS = 157;
        private static final String TOO_LONG_NAME_SUFFIX = "...";
        private final TransferableSelectionList parent;
        protected final Minecraft minecraft;
        private final PackSelectionModel.Entry pack;
        private final FormattedCharSequence nameDisplayCache;
        private final MultiLineLabel descriptionDisplayCache;
        private final FormattedCharSequence incompatibleNameDisplayCache;
        private final MultiLineLabel incompatibleDescriptionDisplayCache;

        public PackEntry(Minecraft p_265717_, TransferableSelectionList p_265075_, PackSelectionModel.Entry p_265360_) {
            this.minecraft = p_265717_;
            this.pack = p_265360_;
            this.parent = p_265075_;
            this.nameDisplayCache = cacheName(p_265717_, p_265360_.getTitle());
            this.descriptionDisplayCache = cacheDescription(p_265717_, p_265360_.getExtendedDescription());
            this.incompatibleNameDisplayCache = cacheName(p_265717_, TransferableSelectionList.INCOMPATIBLE_TITLE);
            this.incompatibleDescriptionDisplayCache = cacheDescription(p_265717_, p_265360_.getCompatibility().getDescription());
        }

        private static FormattedCharSequence cacheName(Minecraft p_100105_, Component p_100106_) {
            int $$2 = p_100105_.font.width((FormattedText)p_100106_);
            if ($$2 > 157) {
                FormattedText $$3 = FormattedText.composite(p_100105_.font.substrByWidth(p_100106_, 157 - p_100105_.font.width("...")), FormattedText.of("..."));
                return Language.getInstance().getVisualOrder($$3);
            } else {
                return p_100106_.getVisualOrderText();
            }
        }

        private static MultiLineLabel cacheDescription(Minecraft p_100110_, Component p_100111_) {
            return MultiLineLabel.create(p_100110_.font, p_100111_, 157, 2);
        }

        public Component getNarration() {
            return Component.translatable("narrator.select", this.pack.getTitle());
        }

        public void render(GuiGraphics p_281314_, int p_283311_, int p_281984_, int p_282250_, int p_281869_, int p_283138_, int p_282529_, int p_282107_, boolean p_282429_, float p_282306_) {
            PackCompatibility $$10 = this.pack.getCompatibility();
            if (!$$10.isCompatible()) {
                p_281314_.fill(p_282250_ - 1, p_281984_ - 1, p_282250_ + p_281869_ - 9, p_281984_ + p_283138_ + 1, -8978432);
            }

            p_281314_.blit(this.pack.getIconTexture(), p_282250_, p_281984_, 0.0F, 0.0F, 32, 32, 32, 32);
            FormattedCharSequence $$11 = this.nameDisplayCache;
            MultiLineLabel $$12 = this.descriptionDisplayCache;
            if (this.showHoverOverlay() && ((Boolean)this.minecraft.options.touchscreen().get() || p_282429_ || this.parent.getSelected() == this && this.parent.isFocused())) {
                p_281314_.fill(p_282250_, p_281984_, p_282250_ + 32, p_281984_ + 32, -1601138544);
                int $$13 = p_282529_ - p_282250_;
                int $$14 = p_282107_ - p_281984_;
                if (!this.pack.getCompatibility().isCompatible()) {
                    $$11 = this.incompatibleNameDisplayCache;
                    $$12 = this.incompatibleDescriptionDisplayCache;
                }

                if (this.pack.canSelect()) {
                    if ($$13 < 32) {
                        p_281314_.blit(TransferableSelectionList.ICON_OVERLAY_LOCATION, p_282250_, p_281984_, 0.0F, 32.0F, 32, 32, 256, 256);
                    } else {
                        p_281314_.blit(TransferableSelectionList.ICON_OVERLAY_LOCATION, p_282250_, p_281984_, 0.0F, 0.0F, 32, 32, 256, 256);
                    }
                } else {
                    if (this.pack.canUnselect()) {
                        if ($$13 < 16) {
                            p_281314_.blit(TransferableSelectionList.ICON_OVERLAY_LOCATION, p_282250_, p_281984_, 32.0F, 32.0F, 32, 32, 256, 256);
                        } else {
                            p_281314_.blit(TransferableSelectionList.ICON_OVERLAY_LOCATION, p_282250_, p_281984_, 32.0F, 0.0F, 32, 32, 256, 256);
                        }
                    }

                    if (this.pack.canMoveUp()) {
                        if ($$13 < 32 && $$13 > 16 && $$14 < 16) {
                            p_281314_.blit(TransferableSelectionList.ICON_OVERLAY_LOCATION, p_282250_, p_281984_, 96.0F, 32.0F, 32, 32, 256, 256);
                        } else {
                            p_281314_.blit(TransferableSelectionList.ICON_OVERLAY_LOCATION, p_282250_, p_281984_, 96.0F, 0.0F, 32, 32, 256, 256);
                        }
                    }

                    if (this.pack.canMoveDown()) {
                        if ($$13 < 32 && $$13 > 16 && $$14 > 16) {
                            p_281314_.blit(TransferableSelectionList.ICON_OVERLAY_LOCATION, p_282250_, p_281984_, 64.0F, 32.0F, 32, 32, 256, 256);
                        } else {
                            p_281314_.blit(TransferableSelectionList.ICON_OVERLAY_LOCATION, p_282250_, p_281984_, 64.0F, 0.0F, 32, 32, 256, 256);
                        }
                    }
                }
            }

            p_281314_.drawString(this.minecraft.font, $$11, p_282250_ + 32 + 2, p_281984_ + 1, 16777215);
            $$12.renderLeftAligned(p_281314_, p_282250_ + 32 + 2, p_281984_ + 12, 10, 8421504);
        }

        public String getPackId() {
            return this.pack.getId();
        }

        private boolean showHoverOverlay() {
            return !this.pack.isFixedPosition() || !this.pack.isRequired();
        }

        public void keyboardSelection() {
            if (this.pack.canSelect() && this.handlePackSelection()) {
                this.parent.screen.updateFocus(this.parent);
            } else if (this.pack.canUnselect()) {
                this.pack.unselect();
                this.parent.screen.updateFocus(this.parent);
            }

        }

        void keyboardMoveUp() {
            if (this.pack.canMoveUp()) {
                this.pack.moveUp();
            }

        }

        void keyboardMoveDown() {
            if (this.pack.canMoveDown()) {
                this.pack.moveDown();
            }

        }

        private boolean handlePackSelection() {
            if (this.pack.getCompatibility().isCompatible()) {
                this.pack.select();
                return true;
            } else {
                Component $$0 = this.pack.getCompatibility().getConfirmation();
                this.minecraft.setScreen(new ConfirmScreen((p_264693_) -> {
                    this.minecraft.setScreen(this.parent.screen);
                    if (p_264693_) {
                        this.pack.select();
                    }

                }, TransferableSelectionList.INCOMPATIBLE_CONFIRM_TITLE, $$0));
                return false;
            }
        }

        public boolean mouseClicked(double p_100090_, double p_100091_, int p_100092_) {
            if (p_100092_ != 0) {
                return false;
            } else {
                double $$3 = p_100090_ - (double)this.parent.getRowLeft();
                double $$4 = p_100091_ - (double)this.parent.getRowTop(this.parent.children().indexOf(this));
                if (this.showHoverOverlay() && $$3 <= 32.0) {
                    this.parent.screen.clearSelected();
                    if (this.pack.canSelect()) {
                        this.handlePackSelection();
                        return true;
                    }

                    if ($$3 < 16.0 && this.pack.canUnselect()) {
                        this.pack.unselect();
                        return true;
                    }

                    if ($$3 > 16.0 && $$4 < 16.0 && this.pack.canMoveUp()) {
                        this.pack.moveUp();
                        return true;
                    }

                    if ($$3 > 16.0 && $$4 > 16.0 && this.pack.canMoveDown()) {
                        this.pack.moveDown();
                        return true;
                    }
                }

                return false;
            }
        }
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.screens.reporting;

import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.Optionull;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.chat.report.ReportReason;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ReportReasonSelectionScreen extends Screen {
    private static final Component REASON_TITLE = Component.translatable("gui.abuseReport.reason.title");
    private static final Component REASON_DESCRIPTION = Component.translatable("gui.abuseReport.reason.description");
    private static final Component READ_INFO_LABEL = Component.translatable("gui.chatReport.read_info");
    private static final int FOOTER_HEIGHT = 95;
    private static final int BUTTON_WIDTH = 150;
    private static final int BUTTON_HEIGHT = 20;
    private static final int CONTENT_WIDTH = 320;
    private static final int PADDING = 4;
    @Nullable
    private final Screen lastScreen;
    @Nullable
    private ReasonSelectionList reasonSelectionList;
    @Nullable
    ReportReason currentlySelectedReason;
    private final Consumer<ReportReason> onSelectedReason;

    public ReportReasonSelectionScreen(@Nullable Screen p_239438_, @Nullable ReportReason p_239439_, Consumer<ReportReason> p_239440_) {
        super(REASON_TITLE);
        this.lastScreen = p_239438_;
        this.currentlySelectedReason = p_239439_;
        this.onSelectedReason = p_239440_;
    }

    protected void init() {
        this.reasonSelectionList = new ReasonSelectionList(this.minecraft);
        this.reasonSelectionList.setRenderBackground(false);
        this.addWidget(this.reasonSelectionList);
        ReportReason var10000 = this.currentlySelectedReason;
        ReasonSelectionList var10001 = this.reasonSelectionList;
        Objects.requireNonNull(var10001);
        ReasonSelectionList.Entry $$0 = (ReasonSelectionList.Entry)Optionull.map(var10000, var10001::findEntry);
        this.reasonSelectionList.setSelected($$0);
        int $$1 = this.width / 2 - 150 - 5;
        this.addRenderableWidget(Button.builder(READ_INFO_LABEL, (p_280887_) -> {
            this.minecraft.setScreen(new ConfirmLinkScreen((p_280888_) -> {
                if (p_280888_) {
                    Util.getPlatform().openUri("https://aka.ms/aboutjavareporting");
                }

                this.minecraft.setScreen(this);
            }, "https://aka.ms/aboutjavareporting", true));
        }).bounds($$1, this.buttonTop(), 150, 20).build());
        int $$2 = this.width / 2 + 5;
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (p_280889_) -> {
            ReasonSelectionList.Entry $$1 = (ReasonSelectionList.Entry)this.reasonSelectionList.getSelected();
            if ($$1 != null) {
                this.onSelectedReason.accept($$1.getReason());
            }

            this.minecraft.setScreen(this.lastScreen);
        }).bounds($$2, this.buttonTop(), 150, 20).build());
        super.init();
    }

    public void render(GuiGraphics p_282815_, int p_283039_, int p_283620_, float p_281336_) {
        this.renderBackground(p_282815_);
        this.reasonSelectionList.render(p_282815_, p_283039_, p_283620_, p_281336_);
        p_282815_.drawCenteredString(this.font, (Component)this.title, this.width / 2, 16, 16777215);
        super.render(p_282815_, p_283039_, p_283620_, p_281336_);
        p_282815_.fill(this.contentLeft(), this.descriptionTop(), this.contentRight(), this.descriptionBottom(), 2130706432);
        p_282815_.drawString(this.font, REASON_DESCRIPTION, this.contentLeft() + 4, this.descriptionTop() + 4, -8421505);
        ReasonSelectionList.Entry $$4 = (ReasonSelectionList.Entry)this.reasonSelectionList.getSelected();
        if ($$4 != null) {
            int $$5 = this.contentLeft() + 4 + 16;
            int $$6 = this.contentRight() - 4;
            int var10000 = this.descriptionTop() + 4;
            Objects.requireNonNull(this.font);
            int $$7 = var10000 + 9 + 2;
            int $$8 = this.descriptionBottom() - 4;
            int $$9 = $$6 - $$5;
            int $$10 = $$8 - $$7;
            int $$11 = this.font.wordWrapHeight((FormattedText)$$4.reason.description(), $$9);
            p_282815_.drawWordWrap(this.font, $$4.reason.description(), $$5, $$7 + ($$10 - $$11) / 2, $$9, -1);
        }

    }

    private int buttonTop() {
        return this.height - 20 - 4;
    }

    private int contentLeft() {
        return (this.width - 320) / 2;
    }

    private int contentRight() {
        return (this.width + 320) / 2;
    }

    private int descriptionTop() {
        return this.height - 95 + 4;
    }

    private int descriptionBottom() {
        return this.buttonTop() - 4;
    }

    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    @OnlyIn(Dist.CLIENT)
    public class ReasonSelectionList extends ObjectSelectionList<Entry> {
        public ReasonSelectionList(Minecraft p_239715_) {
            super(p_239715_, ReportReasonSelectionScreen.this.width, ReportReasonSelectionScreen.this.height, 40, ReportReasonSelectionScreen.this.height - 95, 18);
            ReportReason[] var3 = ReportReason.values();
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                ReportReason $$2 = var3[var5];
                this.addEntry(new Entry($$2));
            }

        }

        @Nullable
        public Entry findEntry(ReportReason p_239168_) {
            return (Entry)this.children().stream().filter((p_239293_) -> {
                return p_239293_.reason == p_239168_;
            }).findFirst().orElse((Object)null);
        }

        public int getRowWidth() {
            return 320;
        }

        protected int getScrollbarPosition() {
            return this.getRowRight() - 2;
        }

        public void setSelected(@Nullable Entry p_240601_) {
            super.setSelected(p_240601_);
            ReportReasonSelectionScreen.this.currentlySelectedReason = p_240601_ != null ? p_240601_.getReason() : null;
        }

        @OnlyIn(Dist.CLIENT)
        public class Entry extends ObjectSelectionList.Entry<Entry> {
            final ReportReason reason;

            public Entry(ReportReason p_239267_) {
                this.reason = p_239267_;
            }

            public void render(GuiGraphics p_281941_, int p_281450_, int p_281781_, int p_283334_, int p_283073_, int p_282523_, int p_282667_, int p_281567_, boolean p_282095_, float p_283305_) {
                int $$10 = p_283334_ + 1;
                Objects.requireNonNull(ReportReasonSelectionScreen.this.font);
                int $$11 = p_281781_ + (p_282523_ - 9) / 2 + 1;
                p_281941_.drawString(ReportReasonSelectionScreen.this.font, (Component)this.reason.title(), $$10, $$11, -1);
            }

            public Component getNarration() {
                return Component.translatable("gui.abuseReport.reason.narration", this.reason.title(), this.reason.description());
            }

            public boolean mouseClicked(double p_240021_, double p_240022_, int p_240023_) {
                if (p_240023_ == 0) {
                    ReasonSelectionList.this.setSelected(this);
                    return true;
                } else {
                    return false;
                }
            }

            public ReportReason getReason() {
                return this.reason;
            }
        }
    }
}

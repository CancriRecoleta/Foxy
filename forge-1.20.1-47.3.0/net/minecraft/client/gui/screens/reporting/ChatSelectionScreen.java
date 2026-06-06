//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.screens.reporting;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Optionull;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.chat.ChatTrustLevel;
import net.minecraft.client.multiplayer.chat.LoggedChatMessage;
import net.minecraft.client.multiplayer.chat.report.ChatReportBuilder;
import net.minecraft.client.multiplayer.chat.report.ReportingContext;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChatSelectionScreen extends Screen {
    private static final Component TITLE = Component.translatable("gui.chatSelection.title");
    private static final Component CONTEXT_INFO;
    @Nullable
    private final Screen lastScreen;
    private final ReportingContext reportingContext;
    private Button confirmSelectedButton;
    private MultiLineLabel contextInfoLabel;
    @Nullable
    private ChatSelectionList chatSelectionList;
    final ChatReportBuilder report;
    private final Consumer<ChatReportBuilder> onSelected;
    private ChatSelectionLogFiller chatLogFiller;

    public ChatSelectionScreen(@Nullable Screen p_239090_, ReportingContext p_239091_, ChatReportBuilder p_239092_, Consumer<ChatReportBuilder> p_239093_) {
        super(TITLE);
        this.lastScreen = p_239090_;
        this.reportingContext = p_239091_;
        this.report = p_239092_.copy();
        this.onSelected = p_239093_;
    }

    protected void init() {
        this.chatLogFiller = new ChatSelectionLogFiller(this.reportingContext, this::canReport);
        this.contextInfoLabel = MultiLineLabel.create(this.font, CONTEXT_INFO, this.width - 16);
        Minecraft var10004 = this.minecraft;
        int var10005 = this.contextInfoLabel.getLineCount() + 1;
        Objects.requireNonNull(this.font);
        this.chatSelectionList = new ChatSelectionList(var10004, var10005 * 9);
        this.chatSelectionList.setRenderBackground(false);
        this.addWidget(this.chatSelectionList);
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, (p_239860_) -> {
            this.onClose();
        }).bounds(this.width / 2 - 155, this.height - 32, 150, 20).build());
        this.confirmSelectedButton = (Button)this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (p_239591_) -> {
            this.onSelected.accept(this.report);
            this.onClose();
        }).bounds(this.width / 2 - 155 + 160, this.height - 32, 150, 20).build());
        this.updateConfirmSelectedButton();
        this.extendLog();
        this.chatSelectionList.setScrollAmount((double)this.chatSelectionList.getMaxScroll());
    }

    private boolean canReport(LoggedChatMessage p_242240_) {
        return p_242240_.canReport(this.report.reportedProfileId());
    }

    private void extendLog() {
        int $$0 = this.chatSelectionList.getMaxVisibleEntries();
        this.chatLogFiller.fillNextPage($$0, this.chatSelectionList);
    }

    void onReachedScrollTop() {
        this.extendLog();
    }

    void updateConfirmSelectedButton() {
        this.confirmSelectedButton.active = !this.report.reportedMessages().isEmpty();
    }

    public void render(GuiGraphics p_282899_, int p_239287_, int p_239288_, float p_239289_) {
        this.renderBackground(p_282899_);
        this.chatSelectionList.render(p_282899_, p_239287_, p_239288_, p_239289_);
        p_282899_.drawCenteredString(this.font, (Component)this.title, this.width / 2, 16, 16777215);
        AbuseReportLimits $$4 = this.reportingContext.sender().reportLimits();
        int $$5 = this.report.reportedMessages().size();
        int $$6 = $$4.maxReportedMessageCount();
        Component $$7 = Component.translatable("gui.chatSelection.selected", $$5, $$6);
        Font var10001 = this.font;
        int var10003 = this.width / 2;
        Objects.requireNonNull(this.font);
        p_282899_.drawCenteredString(var10001, (Component)$$7, var10003, 16 + 9 * 3 / 2, 10526880);
        this.contextInfoLabel.renderCentered(p_282899_, this.width / 2, this.chatSelectionList.getFooterTop());
        super.render(p_282899_, p_239287_, p_239288_, p_239289_);
    }

    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    public Component getNarrationMessage() {
        return CommonComponents.joinForNarration(super.getNarrationMessage(), CONTEXT_INFO);
    }

    static {
        CONTEXT_INFO = Component.translatable("gui.chatSelection.context").withStyle(ChatFormatting.GRAY);
    }

    @OnlyIn(Dist.CLIENT)
    public class ChatSelectionList extends ObjectSelectionList<Entry> implements ChatSelectionLogFiller.Output {
        @Nullable
        private Heading previousHeading;

        public ChatSelectionList(Minecraft p_239060_, int p_239061_) {
            super(p_239060_, ChatSelectionScreen.this.width, ChatSelectionScreen.this.height, 40, ChatSelectionScreen.this.height - 40 - p_239061_, 16);
        }

        public void setScrollAmount(double p_239021_) {
            double $$1 = this.getScrollAmount();
            super.setScrollAmount(p_239021_);
            if ((float)this.getMaxScroll() > 1.0E-5F && p_239021_ <= 9.999999747378752E-6 && !Mth.equal(p_239021_, $$1)) {
                ChatSelectionScreen.this.onReachedScrollTop();
            }

        }

        public void acceptMessage(int p_242846_, LoggedChatMessage.Player p_242909_) {
            boolean $$2 = p_242909_.canReport(ChatSelectionScreen.this.report.reportedProfileId());
            ChatTrustLevel $$3 = p_242909_.trustLevel();
            GuiMessageTag $$4 = $$3.createTag(p_242909_.message());
            Entry $$5 = new MessageEntry(p_242846_, p_242909_.toContentComponent(), p_242909_.toNarrationComponent(), $$4, $$2, true);
            this.addEntryToTop($$5);
            this.updateHeading(p_242909_, $$2);
        }

        private void updateHeading(LoggedChatMessage.Player p_242229_, boolean p_240019_) {
            Entry $$2 = new MessageHeadingEntry(p_242229_.profile(), p_242229_.toHeadingComponent(), p_240019_);
            this.addEntryToTop($$2);
            Heading $$3 = new Heading(p_242229_.profileId(), $$2);
            if (this.previousHeading != null && this.previousHeading.canCombine($$3)) {
                this.removeEntryFromTop(this.previousHeading.entry());
            }

            this.previousHeading = $$3;
        }

        public void acceptDivider(Component p_239876_) {
            this.addEntryToTop(new PaddingEntry());
            this.addEntryToTop(new DividerEntry(p_239876_));
            this.addEntryToTop(new PaddingEntry());
            this.previousHeading = null;
        }

        protected int getScrollbarPosition() {
            return (this.width + this.getRowWidth()) / 2;
        }

        public int getRowWidth() {
            return Math.min(350, this.width - 50);
        }

        public int getMaxVisibleEntries() {
            return Mth.positiveCeilDiv(this.y1 - this.y0, this.itemHeight);
        }

        protected void renderItem(GuiGraphics p_281532_, int p_239775_, int p_239776_, float p_239777_, int p_239778_, int p_239779_, int p_239780_, int p_239781_, int p_239782_) {
            Entry $$9 = (Entry)this.getEntry(p_239778_);
            if (this.shouldHighlightEntry($$9)) {
                boolean $$10 = this.getSelected() == $$9;
                int $$11 = this.isFocused() && $$10 ? -1 : -8355712;
                this.renderSelection(p_281532_, p_239780_, p_239781_, p_239782_, $$11, -16777216);
            }

            $$9.render(p_281532_, p_239778_, p_239780_, p_239779_, p_239781_, p_239782_, p_239775_, p_239776_, this.getHovered() == $$9, p_239777_);
        }

        private boolean shouldHighlightEntry(Entry p_240327_) {
            if (p_240327_.canSelect()) {
                boolean $$1 = this.getSelected() == p_240327_;
                boolean $$2 = this.getSelected() == null;
                boolean $$3 = this.getHovered() == p_240327_;
                return $$1 || $$2 && $$3 && p_240327_.canReport();
            } else {
                return false;
            }
        }

        @Nullable
        protected Entry nextEntry(ScreenDirection p_265203_) {
            return (Entry)this.nextEntry(p_265203_, Entry::canSelect);
        }

        public void setSelected(@Nullable Entry p_265249_) {
            super.setSelected(p_265249_);
            Entry $$1 = this.nextEntry(ScreenDirection.UP);
            if ($$1 == null) {
                ChatSelectionScreen.this.onReachedScrollTop();
            }

        }

        public boolean keyPressed(int p_239322_, int p_239323_, int p_239324_) {
            Entry $$3 = (Entry)this.getSelected();
            return $$3 != null && $$3.keyPressed(p_239322_, p_239323_, p_239324_) ? true : super.keyPressed(p_239322_, p_239323_, p_239324_);
        }

        public int getFooterTop() {
            int var10000 = this.y1;
            Objects.requireNonNull(ChatSelectionScreen.this.font);
            return var10000 + 9;
        }

        @OnlyIn(Dist.CLIENT)
        public class MessageEntry extends Entry {
            private static final ResourceLocation CHECKMARK_TEXTURE = new ResourceLocation("minecraft", "textures/gui/checkmark.png");
            private static final int CHECKMARK_WIDTH = 9;
            private static final int CHECKMARK_HEIGHT = 8;
            private static final int INDENT_AMOUNT = 11;
            private static final int TAG_MARGIN_LEFT = 4;
            private final int chatId;
            private final FormattedText text;
            private final Component narration;
            @Nullable
            private final List<FormattedCharSequence> hoverText;
            @Nullable
            private final GuiMessageTag.Icon tagIcon;
            @Nullable
            private final List<FormattedCharSequence> tagHoverText;
            private final boolean canReport;
            private final boolean playerMessage;

            public MessageEntry(int p_240650_, Component p_240525_, Component p_240539_, @Nullable GuiMessageTag p_240551_, boolean p_240596_, boolean p_240615_) {
                super();
                this.chatId = p_240650_;
                this.tagIcon = (GuiMessageTag.Icon)Optionull.map(p_240551_, GuiMessageTag::icon);
                this.tagHoverText = p_240551_ != null && p_240551_.text() != null ? ChatSelectionScreen.this.font.split(p_240551_.text(), ChatSelectionList.this.getRowWidth()) : null;
                this.canReport = p_240596_;
                this.playerMessage = p_240615_;
                FormattedText $$7 = ChatSelectionScreen.this.font.substrByWidth(p_240525_, this.getMaximumTextWidth() - ChatSelectionScreen.this.font.width((FormattedText)CommonComponents.ELLIPSIS));
                if (p_240525_ != $$7) {
                    this.text = FormattedText.composite($$7, CommonComponents.ELLIPSIS);
                    this.hoverText = ChatSelectionScreen.this.font.split(p_240525_, ChatSelectionList.this.getRowWidth());
                } else {
                    this.text = p_240525_;
                    this.hoverText = null;
                }

                this.narration = p_240539_;
            }

            public void render(GuiGraphics p_281361_, int p_239596_, int p_239597_, int p_239598_, int p_239599_, int p_239600_, int p_239601_, int p_239602_, boolean p_239603_, float p_239604_) {
                if (this.isSelected() && this.canReport) {
                    this.renderSelectedCheckmark(p_281361_, p_239597_, p_239598_, p_239600_);
                }

                int $$10 = p_239598_ + this.getTextIndent();
                int var10000 = p_239597_ + 1;
                Objects.requireNonNull(ChatSelectionScreen.this.font);
                int $$11 = var10000 + (p_239600_ - 9) / 2;
                p_281361_.drawString(ChatSelectionScreen.this.font, Language.getInstance().getVisualOrder(this.text), $$10, $$11, this.canReport ? -1 : -1593835521);
                if (this.hoverText != null && p_239603_) {
                    ChatSelectionScreen.this.setTooltipForNextRenderPass(this.hoverText);
                }

                int $$12 = ChatSelectionScreen.this.font.width(this.text);
                this.renderTag(p_281361_, $$10 + $$12 + 4, p_239597_, p_239600_, p_239601_, p_239602_);
            }

            private void renderTag(GuiGraphics p_281776_, int p_240566_, int p_240565_, int p_240581_, int p_240614_, int p_240612_) {
                if (this.tagIcon != null) {
                    int $$6 = p_240565_ + (p_240581_ - this.tagIcon.height) / 2;
                    this.tagIcon.draw(p_281776_, p_240566_, $$6);
                    if (this.tagHoverText != null && p_240614_ >= p_240566_ && p_240614_ <= p_240566_ + this.tagIcon.width && p_240612_ >= $$6 && p_240612_ <= $$6 + this.tagIcon.height) {
                        ChatSelectionScreen.this.setTooltipForNextRenderPass(this.tagHoverText);
                    }
                }

            }

            private void renderSelectedCheckmark(GuiGraphics p_281342_, int p_281492_, int p_283046_, int p_283458_) {
                int $$4 = p_283046_;
                int $$5 = p_281492_ + (p_283458_ - 8) / 2;
                RenderSystem.enableBlend();
                p_281342_.blit(CHECKMARK_TEXTURE, $$4, $$5, 0.0F, 0.0F, 9, 8, 9, 8);
                RenderSystem.disableBlend();
            }

            private int getMaximumTextWidth() {
                int $$0 = this.tagIcon != null ? this.tagIcon.width + 4 : 0;
                return ChatSelectionList.this.getRowWidth() - this.getTextIndent() - 4 - $$0;
            }

            private int getTextIndent() {
                return this.playerMessage ? 11 : 0;
            }

            public Component getNarration() {
                return (Component)(this.isSelected() ? Component.translatable("narrator.select", this.narration) : this.narration);
            }

            public boolean mouseClicked(double p_239729_, double p_239730_, int p_239731_) {
                if (p_239731_ == 0) {
                    ChatSelectionList.this.setSelected((Entry)null);
                    return this.toggleReport();
                } else {
                    return false;
                }
            }

            public boolean keyPressed(int p_239368_, int p_239369_, int p_239370_) {
                return CommonInputs.selected(p_239368_) ? this.toggleReport() : false;
            }

            public boolean isSelected() {
                return ChatSelectionScreen.this.report.isReported(this.chatId);
            }

            public boolean canSelect() {
                return true;
            }

            public boolean canReport() {
                return this.canReport;
            }

            private boolean toggleReport() {
                if (this.canReport) {
                    ChatSelectionScreen.this.report.toggleReported(this.chatId);
                    ChatSelectionScreen.this.updateConfirmSelectedButton();
                    return true;
                } else {
                    return false;
                }
            }
        }

        @OnlyIn(Dist.CLIENT)
        public class MessageHeadingEntry extends Entry {
            private static final int FACE_SIZE = 12;
            private final Component heading;
            private final ResourceLocation skin;
            private final boolean canReport;

            public MessageHeadingEntry(GameProfile p_240080_, Component p_240081_, boolean p_240082_) {
                super();
                this.heading = p_240081_;
                this.canReport = p_240082_;
                this.skin = ChatSelectionList.this.minecraft.getSkinManager().getInsecureSkinLocation(p_240080_);
            }

            public void render(GuiGraphics p_281320_, int p_283177_, int p_282422_, int p_282017_, int p_282555_, int p_283255_, int p_283682_, int p_281582_, boolean p_282259_, float p_283561_) {
                int $$10 = p_282017_ - 12 - 4;
                int $$11 = p_282422_ + (p_283255_ - 12) / 2;
                PlayerFaceRenderer.draw(p_281320_, this.skin, $$10, $$11, 12);
                int var10000 = p_282422_ + 1;
                Objects.requireNonNull(ChatSelectionScreen.this.font);
                int $$12 = var10000 + (p_283255_ - 9) / 2;
                p_281320_.drawString(ChatSelectionScreen.this.font, this.heading, p_282017_, $$12, this.canReport ? -1 : -1593835521);
            }
        }

        @OnlyIn(Dist.CLIENT)
        private static record Heading(UUID sender, Entry entry) {
            Heading(UUID sender, Entry entry) {
                this.sender = sender;
                this.entry = entry;
            }

            public boolean canCombine(Heading p_239748_) {
                return p_239748_.sender.equals(this.sender);
            }

            public UUID sender() {
                return this.sender;
            }

            public Entry entry() {
                return this.entry;
            }
        }

        @OnlyIn(Dist.CLIENT)
        public abstract class Entry extends ObjectSelectionList.Entry<Entry> {
            public Entry() {
            }

            public Component getNarration() {
                return CommonComponents.EMPTY;
            }

            public boolean isSelected() {
                return false;
            }

            public boolean canSelect() {
                return false;
            }

            public boolean canReport() {
                return this.canSelect();
            }
        }

        @OnlyIn(Dist.CLIENT)
        public class PaddingEntry extends Entry {
            public PaddingEntry() {
                super();
            }

            public void render(GuiGraphics p_282007_, int p_240110_, int p_240111_, int p_240112_, int p_240113_, int p_240114_, int p_240115_, int p_240116_, boolean p_240117_, float p_240118_) {
            }
        }

        @OnlyIn(Dist.CLIENT)
        public class DividerEntry extends Entry {
            private static final int COLOR = -6250336;
            private final Component text;

            public DividerEntry(Component p_239672_) {
                super();
                this.text = p_239672_;
            }

            public void render(GuiGraphics p_283635_, int p_239815_, int p_239816_, int p_239817_, int p_239818_, int p_239819_, int p_239820_, int p_239821_, boolean p_239822_, float p_239823_) {
                int $$10 = p_239816_ + p_239819_ / 2;
                int $$11 = p_239817_ + p_239818_ - 8;
                int $$12 = ChatSelectionScreen.this.font.width((FormattedText)this.text);
                int $$13 = (p_239817_ + $$11 - $$12) / 2;
                Objects.requireNonNull(ChatSelectionScreen.this.font);
                int $$14 = $$10 - 9 / 2;
                p_283635_.drawString(ChatSelectionScreen.this.font, this.text, $$13, $$14, -6250336);
            }

            public Component getNarration() {
                return this.text;
            }
        }
    }
}

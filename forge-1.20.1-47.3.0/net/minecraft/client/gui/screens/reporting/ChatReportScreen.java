//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.screens.reporting;

import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Optionull;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.GenericWaitingScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.WarningScreen;
import net.minecraft.client.multiplayer.chat.report.ChatReportBuilder;
import net.minecraft.client.multiplayer.chat.report.ReportReason;
import net.minecraft.client.multiplayer.chat.report.ReportingContext;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ThrowingComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ChatReportScreen extends Screen {
    private static final int BUTTON_WIDTH = 120;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_MARGIN = 20;
    private static final int BUTTON_MARGIN_HALF = 10;
    private static final int LABEL_HEIGHT = 25;
    private static final int SCREEN_WIDTH = 280;
    private static final int SCREEN_HEIGHT = 300;
    private static final Component OBSERVED_WHAT_LABEL = Component.translatable("gui.chatReport.observed_what");
    private static final Component SELECT_REASON = Component.translatable("gui.chatReport.select_reason");
    private static final Component MORE_COMMENTS_LABEL = Component.translatable("gui.chatReport.more_comments");
    private static final Component DESCRIBE_PLACEHOLDER = Component.translatable("gui.chatReport.describe");
    private static final Component REPORT_SENT_MESSAGE = Component.translatable("gui.chatReport.report_sent_msg");
    private static final Component SELECT_CHAT_MESSAGE = Component.translatable("gui.chatReport.select_chat");
    private static final Component REPORT_SENDING_TITLE;
    private static final Component REPORT_SENT_TITLE;
    private static final Component REPORT_ERROR_TITLE;
    private static final Component REPORT_SEND_GENERIC_ERROR;
    private static final Logger LOGGER;
    @Nullable
    final Screen lastScreen;
    private final ReportingContext reportingContext;
    @Nullable
    private MultiLineLabel reasonDescriptionLabel;
    @Nullable
    private MultiLineEditBox commentBox;
    private Button sendButton;
    private ChatReportBuilder reportBuilder;
    @Nullable
    private ChatReportBuilder.CannotBuildReason cannotBuildReason;

    private ChatReportScreen(@Nullable Screen p_253839_, ReportingContext p_254386_, ChatReportBuilder p_254309_) {
        super(Component.translatable("gui.chatReport.title"));
        this.lastScreen = p_253839_;
        this.reportingContext = p_254386_;
        this.reportBuilder = p_254309_;
    }

    public ChatReportScreen(@Nullable Screen p_239116_, ReportingContext p_239117_, UUID p_239118_) {
        this(p_239116_, p_239117_, new ChatReportBuilder(p_239118_, p_239117_.sender().reportLimits()));
    }

    public ChatReportScreen(@Nullable Screen p_254505_, ReportingContext p_254531_, ChatReportBuilder.ChatReport p_253775_) {
        this(p_254505_, p_254531_, new ChatReportBuilder(p_253775_, p_254531_.sender().reportLimits()));
    }

    protected void init() {
        AbuseReportLimits $$0 = this.reportingContext.sender().reportLimits();
        int $$1 = this.width / 2;
        ReportReason $$2 = this.reportBuilder.reason();
        if ($$2 != null) {
            this.reasonDescriptionLabel = MultiLineLabel.create(this.font, $$2.description(), 280);
        } else {
            this.reasonDescriptionLabel = null;
        }

        IntSet $$3 = this.reportBuilder.reportedMessages();
        Object $$5;
        if ($$3.isEmpty()) {
            $$5 = SELECT_CHAT_MESSAGE;
        } else {
            $$5 = Component.translatable("gui.chatReport.selected_chat", $$3.size());
        }

        this.addRenderableWidget(Button.builder((Component)$$5, (p_280882_) -> {
            this.minecraft.setScreen(new ChatSelectionScreen(this, this.reportingContext, this.reportBuilder, (p_239697_) -> {
                this.reportBuilder = p_239697_;
                this.onReportChanged();
            }));
        }).bounds(this.contentLeft(), this.selectChatTop(), 280, 20).build());
        Component $$6 = (Component)Optionull.mapOrDefault($$2, ReportReason::title, SELECT_REASON);
        this.addRenderableWidget(Button.builder($$6, (p_280881_) -> {
            this.minecraft.setScreen(new ReportReasonSelectionScreen(this, this.reportBuilder.reason(), (p_239513_) -> {
                this.reportBuilder.setReason(p_239513_);
                this.onReportChanged();
            }));
        }).bounds(this.contentLeft(), this.selectInfoTop(), 280, 20).build());
        this.commentBox = (MultiLineEditBox)this.addRenderableWidget(new MultiLineEditBox(this.minecraft.font, this.contentLeft(), this.commentBoxTop(), 280, this.commentBoxBottom() - this.commentBoxTop(), DESCRIBE_PLACEHOLDER, Component.translatable("gui.chatReport.comments")));
        this.commentBox.setValue(this.reportBuilder.comments());
        this.commentBox.setCharacterLimit($$0.maxOpinionCommentsLength());
        this.commentBox.setValueListener((p_240036_) -> {
            this.reportBuilder.setComments(p_240036_);
            this.onReportChanged();
        });
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, (p_239971_) -> {
            this.onClose();
        }).bounds($$1 - 120, this.completeButtonTop(), 120, 20).build());
        this.sendButton = (Button)this.addRenderableWidget(Button.builder(Component.translatable("gui.chatReport.send"), (p_239742_) -> {
            this.sendReport();
        }).bounds($$1 + 10, this.completeButtonTop(), 120, 20).build());
        this.onReportChanged();
    }

    private void onReportChanged() {
        this.cannotBuildReason = this.reportBuilder.checkBuildable();
        this.sendButton.active = this.cannotBuildReason == null;
        this.sendButton.setTooltip((Tooltip)Optionull.map(this.cannotBuildReason, (p_258134_) -> {
            return Tooltip.create(p_258134_.message());
        }));
    }

    private void sendReport() {
        this.reportBuilder.build(this.reportingContext).ifLeft((p_280883_) -> {
            CompletableFuture<?> $$1 = this.reportingContext.sender().send(p_280883_.id(), p_280883_.report());
            this.minecraft.setScreen(GenericWaitingScreen.createWaiting(REPORT_SENDING_TITLE, CommonComponents.GUI_CANCEL, () -> {
                this.minecraft.setScreen(this);
                $$1.cancel(true);
            }));
            $$1.handleAsync((p_240236_, p_240237_) -> {
                if (p_240237_ == null) {
                    this.onReportSendSuccess();
                } else {
                    if (p_240237_ instanceof CancellationException) {
                        return null;
                    }

                    this.onReportSendError(p_240237_);
                }

                return null;
            }, this.minecraft);
        }).ifRight((p_242967_) -> {
            this.displayReportSendError(p_242967_.message());
        });
    }

    private void onReportSendSuccess() {
        this.clearDraft();
        this.minecraft.setScreen(GenericWaitingScreen.createCompleted(REPORT_SENT_TITLE, REPORT_SENT_MESSAGE, CommonComponents.GUI_DONE, () -> {
            this.minecraft.setScreen((Screen)null);
        }));
    }

    private void onReportSendError(Throwable p_240314_) {
        LOGGER.error("Encountered error while sending abuse report", p_240314_);
        Throwable var4 = p_240314_.getCause();
        Component $$3;
        if (var4 instanceof ThrowingComponent $$1) {
            $$3 = $$1.getComponent();
        } else {
            $$3 = REPORT_SEND_GENERIC_ERROR;
        }

        this.displayReportSendError($$3);
    }

    private void displayReportSendError(Component p_242978_) {
        Component $$1 = p_242978_.copy().withStyle(ChatFormatting.RED);
        this.minecraft.setScreen(GenericWaitingScreen.createCompleted(REPORT_ERROR_TITLE, $$1, CommonComponents.GUI_BACK, () -> {
            this.minecraft.setScreen(this);
        }));
    }

    void saveDraft() {
        if (this.reportBuilder.hasContent()) {
            this.reportingContext.setChatReportDraft(this.reportBuilder.report().copy());
        }

    }

    void clearDraft() {
        this.reportingContext.setChatReportDraft((ChatReportBuilder.ChatReport)null);
    }

    public void render(GuiGraphics p_283069_, int p_239923_, int p_239924_, float p_239925_) {
        int $$4 = this.width / 2;
        this.renderBackground(p_283069_);
        p_283069_.drawCenteredString(this.font, (Component)this.title, $$4, 10, 16777215);
        Font var10001 = this.font;
        Component var10002 = OBSERVED_WHAT_LABEL;
        int var10004 = this.selectChatTop();
        Objects.requireNonNull(this.font);
        p_283069_.drawCenteredString(var10001, var10002, $$4, var10004 - 9 - 6, 16777215);
        int var10003;
        if (this.reasonDescriptionLabel != null) {
            MultiLineLabel var10000 = this.reasonDescriptionLabel;
            int var6 = this.contentLeft();
            var10003 = this.selectInfoTop() + 20 + 5;
            Objects.requireNonNull(this.font);
            var10000.renderLeftAligned(p_283069_, var6, var10003, 9, 16777215);
        }

        var10001 = this.font;
        var10002 = MORE_COMMENTS_LABEL;
        var10003 = this.contentLeft();
        var10004 = this.commentBoxTop();
        Objects.requireNonNull(this.font);
        p_283069_.drawString(var10001, var10002, var10003, var10004 - 9 - 6, 16777215);
        super.render(p_283069_, p_239923_, p_239924_, p_239925_);
    }

    public void tick() {
        this.commentBox.tick();
        super.tick();
    }

    public void onClose() {
        if (this.reportBuilder.hasContent()) {
            this.minecraft.setScreen(new DiscardReportWarningScreen());
        } else {
            this.minecraft.setScreen(this.lastScreen);
        }

    }

    public void removed() {
        this.saveDraft();
        super.removed();
    }

    public boolean mouseReleased(double p_239350_, double p_239351_, int p_239352_) {
        return super.mouseReleased(p_239350_, p_239351_, p_239352_) ? true : this.commentBox.mouseReleased(p_239350_, p_239351_, p_239352_);
    }

    private int contentLeft() {
        return this.width / 2 - 140;
    }

    private int contentRight() {
        return this.width / 2 + 140;
    }

    private int contentTop() {
        return Math.max((this.height - 300) / 2, 0);
    }

    private int contentBottom() {
        return Math.min((this.height + 300) / 2, this.height);
    }

    private int selectChatTop() {
        return this.contentTop() + 40;
    }

    private int selectInfoTop() {
        return this.selectChatTop() + 10 + 20;
    }

    private int commentBoxTop() {
        int $$0 = this.selectInfoTop() + 20 + 25;
        if (this.reasonDescriptionLabel != null) {
            int var10001 = this.reasonDescriptionLabel.getLineCount() + 1;
            Objects.requireNonNull(this.font);
            $$0 += var10001 * 9;
        }

        return $$0;
    }

    private int commentBoxBottom() {
        return this.completeButtonTop() - 20;
    }

    private int completeButtonTop() {
        return this.contentBottom() - 20 - 10;
    }

    static {
        REPORT_SENDING_TITLE = Component.translatable("gui.abuseReport.sending.title").withStyle(ChatFormatting.BOLD);
        REPORT_SENT_TITLE = Component.translatable("gui.abuseReport.sent.title").withStyle(ChatFormatting.BOLD);
        REPORT_ERROR_TITLE = Component.translatable("gui.abuseReport.error.title").withStyle(ChatFormatting.BOLD);
        REPORT_SEND_GENERIC_ERROR = Component.translatable("gui.abuseReport.send.generic_error");
        LOGGER = LogUtils.getLogger();
    }

    @OnlyIn(Dist.CLIENT)
    class DiscardReportWarningScreen extends WarningScreen {
        private static final Component TITLE;
        private static final Component MESSAGE;
        private static final Component RETURN;
        private static final Component DRAFT;
        private static final Component DISCARD;

        protected DiscardReportWarningScreen() {
            super(TITLE, MESSAGE, MESSAGE);
        }

        protected void initButtons(int p_239753_) {
            int $$1 = true;
            this.addRenderableWidget(Button.builder(RETURN, (p_239525_) -> {
                this.onClose();
            }).bounds(this.width / 2 - 155, 100 + p_239753_, 150, 20).build());
            this.addRenderableWidget(Button.builder(DRAFT, (p_280885_) -> {
                ChatReportScreen.this.saveDraft();
                this.minecraft.setScreen(ChatReportScreen.this.lastScreen);
            }).bounds(this.width / 2 + 5, 100 + p_239753_, 150, 20).build());
            this.addRenderableWidget(Button.builder(DISCARD, (p_280886_) -> {
                ChatReportScreen.this.clearDraft();
                this.minecraft.setScreen(ChatReportScreen.this.lastScreen);
            }).bounds(this.width / 2 - 75, 130 + p_239753_, 150, 20).build());
        }

        public void onClose() {
            this.minecraft.setScreen(ChatReportScreen.this);
        }

        public boolean shouldCloseOnEsc() {
            return false;
        }

        protected void renderTitle(GuiGraphics p_282506_) {
            p_282506_.drawString(this.font, (Component)this.title, this.width / 2 - 155, 30, 16777215);
        }

        static {
            TITLE = Component.translatable("gui.chatReport.discard.title").withStyle(ChatFormatting.BOLD);
            MESSAGE = Component.translatable("gui.chatReport.discard.content");
            RETURN = Component.translatable("gui.chatReport.discard.return");
            DRAFT = Component.translatable("gui.chatReport.discard.draft");
            DISCARD = Component.translatable("gui.chatReport.discard.discard");
        }
    }
}

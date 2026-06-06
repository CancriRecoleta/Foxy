//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.multiplayer.chat.report;

import com.google.common.collect.Lists;
import com.mojang.authlib.minecraft.report.AbuseReport;
import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.authlib.minecraft.report.ReportChatMessage;
import com.mojang.authlib.minecraft.report.ReportEvidence;
import com.mojang.authlib.minecraft.report.ReportedEntity;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Optionull;
import net.minecraft.client.multiplayer.chat.ChatLog;
import net.minecraft.client.multiplayer.chat.LoggedChatMessage;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.SignedMessageBody;
import net.minecraft.network.chat.SignedMessageLink;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;

@OnlyIn(Dist.CLIENT)
public class ChatReportBuilder {
    private final ChatReport report;
    private final AbuseReportLimits limits;

    public ChatReportBuilder(ChatReport p_254092_, AbuseReportLimits p_254265_) {
        this.report = p_254092_;
        this.limits = p_254265_;
    }

    public ChatReportBuilder(UUID p_239528_, AbuseReportLimits p_239529_) {
        this.report = new ChatReport(UUID.randomUUID(), Instant.now(), p_239528_);
        this.limits = p_239529_;
    }

    public ChatReport report() {
        return this.report;
    }

    public UUID reportedProfileId() {
        return this.report.reportedProfileId;
    }

    public IntSet reportedMessages() {
        return this.report.reportedMessages;
    }

    public String comments() {
        return this.report.comments;
    }

    public void setComments(String p_239080_) {
        this.report.comments = p_239080_;
    }

    @Nullable
    public ReportReason reason() {
        return this.report.reason;
    }

    public void setReason(ReportReason p_239098_) {
        this.report.reason = p_239098_;
    }

    public void toggleReported(int p_239052_) {
        this.report.toggleReported(p_239052_, this.limits);
    }

    public boolean isReported(int p_243333_) {
        return this.report.reportedMessages.contains(p_243333_);
    }

    public boolean hasContent() {
        return StringUtils.isNotEmpty(this.comments()) || !this.reportedMessages().isEmpty() || this.reason() != null;
    }

    @Nullable
    public CannotBuildReason checkBuildable() {
        if (this.report.reportedMessages.isEmpty()) {
            return net.minecraft.client.multiplayer.chat.report.ChatReportBuilder.CannotBuildReason.NO_REPORTED_MESSAGES;
        } else if (this.report.reportedMessages.size() > this.limits.maxReportedMessageCount()) {
            return net.minecraft.client.multiplayer.chat.report.ChatReportBuilder.CannotBuildReason.TOO_MANY_MESSAGES;
        } else if (this.report.reason == null) {
            return net.minecraft.client.multiplayer.chat.report.ChatReportBuilder.CannotBuildReason.NO_REASON;
        } else {
            return this.report.comments.length() > this.limits.maxOpinionCommentsLength() ? net.minecraft.client.multiplayer.chat.report.ChatReportBuilder.CannotBuildReason.COMMENTS_TOO_LONG : null;
        }
    }

    public Either<Result, CannotBuildReason> build(ReportingContext p_240129_) {
        CannotBuildReason $$1 = this.checkBuildable();
        if ($$1 != null) {
            return Either.right($$1);
        } else {
            String $$2 = ((ReportReason)Objects.requireNonNull(this.report.reason)).backendName();
            ReportEvidence $$3 = this.buildEvidence(p_240129_.chatLog());
            ReportedEntity $$4 = new ReportedEntity(this.report.reportedProfileId);
            AbuseReport $$5 = new AbuseReport(this.report.comments, $$2, $$3, $$4, this.report.createdAt);
            return Either.left(new Result(this.report.reportId, $$5));
        }
    }

    private ReportEvidence buildEvidence(ChatLog p_239183_) {
        List<ReportChatMessage> $$1 = new ArrayList();
        ChatReportContextBuilder $$2 = new ChatReportContextBuilder(this.limits.leadingContextMessageCount());
        $$2.collectAllContext(p_239183_, this.report.reportedMessages, (p_247891_, p_247892_) -> {
            $$1.add(this.buildReportedChatMessage(p_247892_, this.isReported(p_247891_)));
        });
        return new ReportEvidence(Lists.reverse($$1));
    }

    private ReportChatMessage buildReportedChatMessage(LoggedChatMessage.Player p_251321_, boolean p_252182_) {
        SignedMessageLink $$2 = p_251321_.message().link();
        SignedMessageBody $$3 = p_251321_.message().signedBody();
        List<ByteBuffer> $$4 = $$3.lastSeen().entries().stream().map(MessageSignature::asByteBuffer).toList();
        ByteBuffer $$5 = (ByteBuffer)Optionull.map(p_251321_.message().signature(), MessageSignature::asByteBuffer);
        return new ReportChatMessage($$2.index(), $$2.sender(), $$2.sessionId(), $$3.timeStamp(), $$3.salt(), $$4, $$3.content(), $$5, p_252182_);
    }

    public ChatReportBuilder copy() {
        return new ChatReportBuilder(this.report.copy(), this.limits);
    }

    @OnlyIn(Dist.CLIENT)
    public class ChatReport {
        final UUID reportId;
        final Instant createdAt;
        final UUID reportedProfileId;
        final IntSet reportedMessages = new IntOpenHashSet();
        String comments = "";
        @Nullable
        ReportReason reason;

        ChatReport(UUID p_254298_, Instant p_253854_, UUID p_253630_) {
            this.reportId = p_254298_;
            this.createdAt = p_253854_;
            this.reportedProfileId = p_253630_;
        }

        public void toggleReported(int p_254375_, AbuseReportLimits p_254456_) {
            if (this.reportedMessages.contains(p_254375_)) {
                this.reportedMessages.remove(p_254375_);
            } else if (this.reportedMessages.size() < p_254456_.maxReportedMessageCount()) {
                this.reportedMessages.add(p_254375_);
            }

        }

        public ChatReport copy() {
            ChatReport $$0 = ChatReportBuilder.this.new ChatReport(this.reportId, this.createdAt, this.reportedProfileId);
            $$0.reportedMessages.addAll(this.reportedMessages);
            $$0.comments = this.comments;
            $$0.reason = this.reason;
            return $$0;
        }

        public boolean isReportedPlayer(UUID p_253762_) {
            return p_253762_.equals(this.reportedProfileId);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static record CannotBuildReason(Component message) {
        public static final CannotBuildReason NO_REASON = new CannotBuildReason(Component.translatable("gui.chatReport.send.no_reason"));
        public static final CannotBuildReason NO_REPORTED_MESSAGES = new CannotBuildReason(Component.translatable("gui.chatReport.send.no_reported_messages"));
        public static final CannotBuildReason TOO_MANY_MESSAGES = new CannotBuildReason(Component.translatable("gui.chatReport.send.too_many_messages"));
        public static final CannotBuildReason COMMENTS_TOO_LONG = new CannotBuildReason(Component.translatable("gui.chatReport.send.comments_too_long"));

        public CannotBuildReason(Component message) {
            this.message = message;
        }

        public Component message() {
            return this.message;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static record Result(UUID id, AbuseReport report) {
        public Result(UUID id, AbuseReport report) {
            this.id = id;
            this.report = report;
        }

        public UUID id() {
            return this.id;
        }

        public AbuseReport report() {
            return this.report;
        }
    }
}

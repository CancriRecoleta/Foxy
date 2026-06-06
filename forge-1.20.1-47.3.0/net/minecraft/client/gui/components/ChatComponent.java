//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.components;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Optionull;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.ChatVisiblity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ChatComponent {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int MAX_CHAT_HISTORY = 100;
    private static final int MESSAGE_NOT_FOUND = -1;
    private static final int MESSAGE_INDENT = 4;
    private static final int MESSAGE_TAG_MARGIN_LEFT = 4;
    private static final int BOTTOM_MARGIN = 40;
    private static final int TIME_BEFORE_MESSAGE_DELETION = 60;
    private static final Component DELETED_CHAT_MESSAGE;
    private final Minecraft minecraft;
    private final List<String> recentChat = Lists.newArrayList();
    private final List<GuiMessage> allMessages = Lists.newArrayList();
    private final List<GuiMessage.Line> trimmedMessages = Lists.newArrayList();
    private int chatScrollbarPos;
    private boolean newMessageSinceScroll;
    private final List<DelayedMessageDeletion> messageDeletionQueue = new ArrayList();

    public ChatComponent(Minecraft p_93768_) {
        this.minecraft = p_93768_;
    }

    public void tick() {
        if (!this.messageDeletionQueue.isEmpty()) {
            this.processMessageDeletionQueue();
        }

    }

    public void render(GuiGraphics p_282077_, int p_283491_, int p_282406_, int p_283111_) {
        if (!this.isChatHidden()) {
            int $$4 = this.getLinesPerPage();
            int $$5 = this.trimmedMessages.size();
            if ($$5 > 0) {
                boolean $$6 = this.isChatFocused();
                float $$7 = (float)this.getScale();
                int $$8 = Mth.ceil((float)this.getWidth() / $$7);
                int $$9 = p_282077_.guiHeight();
                p_282077_.pose().pushPose();
                p_282077_.pose().scale($$7, $$7, 1.0F);
                p_282077_.pose().translate(4.0F, 0.0F, 0.0F);
                int $$10 = Mth.floor((float)($$9 - 40) / $$7);
                int $$11 = this.getMessageEndIndexAt(this.screenToChatX((double)p_282406_), this.screenToChatY((double)p_283111_));
                double $$12 = (Double)this.minecraft.options.chatOpacity().get() * 0.8999999761581421 + 0.10000000149011612;
                double $$13 = (Double)this.minecraft.options.textBackgroundOpacity().get();
                double $$14 = (Double)this.minecraft.options.chatLineSpacing().get();
                int $$15 = this.getLineHeight();
                int $$16 = (int)Math.round(-8.0 * ($$14 + 1.0) + 4.0 * $$14);
                int $$17 = 0;

                int $$36;
                int $$23;
                int $$40;
                int $$26;
                for(int $$18 = 0; $$18 + this.chatScrollbarPos < this.trimmedMessages.size() && $$18 < $$4; ++$$18) {
                    int $$19 = $$18 + this.chatScrollbarPos;
                    GuiMessage.Line $$20 = (GuiMessage.Line)this.trimmedMessages.get($$19);
                    if ($$20 != null) {
                        $$36 = p_283491_ - $$20.addedTime();
                        if ($$36 < 200 || $$6) {
                            double $$22 = $$6 ? 1.0 : getTimeFactor($$36);
                            $$23 = (int)(255.0 * $$22 * $$12);
                            $$40 = (int)(255.0 * $$22 * $$13);
                            ++$$17;
                            if ($$23 > 3) {
                                int $$25 = false;
                                $$26 = $$10 - $$18 * $$15;
                                int $$27 = $$26 + $$16;
                                p_282077_.pose().pushPose();
                                p_282077_.pose().translate(0.0F, 0.0F, 50.0F);
                                p_282077_.fill(-4, $$26 - $$15, 0 + $$8 + 4 + 4, $$26, $$40 << 24);
                                GuiMessageTag $$28 = $$20.tag();
                                if ($$28 != null) {
                                    int $$29 = $$28.indicatorColor() | $$23 << 24;
                                    p_282077_.fill(-4, $$26 - $$15, -2, $$26, $$29);
                                    if ($$19 == $$11 && $$28.icon() != null) {
                                        int $$30 = this.getTagIconLeft($$20);
                                        Objects.requireNonNull(this.minecraft.font);
                                        int $$31 = $$27 + 9;
                                        this.drawTagIcon(p_282077_, $$30, $$31, $$28.icon());
                                    }
                                }

                                p_282077_.pose().translate(0.0F, 0.0F, 50.0F);
                                p_282077_.drawString(this.minecraft.font, (FormattedCharSequence)$$20.content(), 0, $$27, 16777215 + ($$23 << 24));
                                p_282077_.pose().popPose();
                            }
                        }
                    }
                }

                long $$32 = this.minecraft.getChatListener().queueSize();
                int $$35;
                if ($$32 > 0L) {
                    $$35 = (int)(128.0 * $$12);
                    $$36 = (int)(255.0 * $$13);
                    p_282077_.pose().pushPose();
                    p_282077_.pose().translate(0.0F, (float)$$10, 50.0F);
                    p_282077_.fill(-2, 0, $$8 + 4, 9, $$36 << 24);
                    p_282077_.pose().translate(0.0F, 0.0F, 50.0F);
                    p_282077_.drawString(this.minecraft.font, (Component)Component.translatable("chat.queue", $$32), 0, 1, 16777215 + ($$35 << 24));
                    p_282077_.pose().popPose();
                }

                if ($$6) {
                    $$35 = this.getLineHeight();
                    $$36 = $$5 * $$35;
                    int $$37 = $$17 * $$35;
                    int $$38 = this.chatScrollbarPos * $$37 / $$5 - $$10;
                    $$23 = $$37 * $$37 / $$36;
                    if ($$36 != $$37) {
                        $$40 = $$38 > 0 ? 170 : 96;
                        int $$41 = this.newMessageSinceScroll ? 13382451 : 3355562;
                        $$26 = $$8 + 4;
                        p_282077_.fill($$26, -$$38, $$26 + 2, -$$38 - $$23, $$41 + ($$40 << 24));
                        p_282077_.fill($$26 + 2, -$$38, $$26 + 1, -$$38 - $$23, 13421772 + ($$40 << 24));
                    }
                }

                p_282077_.pose().popPose();
            }
        }
    }

    private void drawTagIcon(GuiGraphics p_283206_, int p_281677_, int p_281878_, GuiMessageTag.Icon p_282783_) {
        int $$4 = p_281878_ - p_282783_.height - 1;
        p_282783_.draw(p_283206_, p_281677_, $$4);
    }

    private int getTagIconLeft(GuiMessage.Line p_240622_) {
        return this.minecraft.font.width(p_240622_.content()) + 4;
    }

    private boolean isChatHidden() {
        return this.minecraft.options.chatVisibility().get() == ChatVisiblity.HIDDEN;
    }

    private static double getTimeFactor(int p_93776_) {
        double $$1 = (double)p_93776_ / 200.0;
        $$1 = 1.0 - $$1;
        $$1 *= 10.0;
        $$1 = Mth.clamp($$1, 0.0, 1.0);
        $$1 *= $$1;
        return $$1;
    }

    public void clearMessages(boolean p_93796_) {
        this.minecraft.getChatListener().clearQueue();
        this.messageDeletionQueue.clear();
        this.trimmedMessages.clear();
        this.allMessages.clear();
        if (p_93796_) {
            this.recentChat.clear();
        }

    }

    public void addMessage(Component p_93786_) {
        this.addMessage(p_93786_, (MessageSignature)null, this.minecraft.isSingleplayer() ? GuiMessageTag.systemSinglePlayer() : GuiMessageTag.system());
    }

    public void addMessage(Component p_241484_, @Nullable MessageSignature p_241323_, @Nullable GuiMessageTag p_241297_) {
        this.logChatMessage(p_241484_, p_241297_);
        this.addMessage(p_241484_, p_241323_, this.minecraft.gui.getGuiTicks(), p_241297_, false);
    }

    private void logChatMessage(Component p_242919_, @Nullable GuiMessageTag p_242840_) {
        String $$2 = p_242919_.getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n");
        String $$3 = (String)Optionull.map(p_242840_, GuiMessageTag::logTag);
        if ($$3 != null) {
            LOGGER.info("[{}] [CHAT] {}", $$3, $$2);
        } else {
            LOGGER.info("[CHAT] {}", $$2);
        }

    }

    private void addMessage(Component p_240562_, @Nullable MessageSignature p_241566_, int p_240583_, @Nullable GuiMessageTag p_240624_, boolean p_240558_) {
        int $$5 = Mth.floor((double)this.getWidth() / this.getScale());
        if (p_240624_ != null && p_240624_.icon() != null) {
            $$5 -= p_240624_.icon().width + 4 + 2;
        }

        List<FormattedCharSequence> $$6 = ComponentRenderUtils.wrapComponents(p_240562_, $$5, this.minecraft.font);
        boolean $$7 = this.isChatFocused();

        for(int $$8 = 0; $$8 < $$6.size(); ++$$8) {
            FormattedCharSequence $$9 = (FormattedCharSequence)$$6.get($$8);
            if ($$7 && this.chatScrollbarPos > 0) {
                this.newMessageSinceScroll = true;
                this.scrollChat(1);
            }

            boolean $$10 = $$8 == $$6.size() - 1;
            this.trimmedMessages.add(0, new GuiMessage.Line(p_240583_, $$9, p_240624_, $$10));
        }

        while(this.trimmedMessages.size() > 100) {
            this.trimmedMessages.remove(this.trimmedMessages.size() - 1);
        }

        if (!p_240558_) {
            this.allMessages.add(0, new GuiMessage(p_240583_, p_240562_, p_241566_, p_240624_));

            while(this.allMessages.size() > 100) {
                this.allMessages.remove(this.allMessages.size() - 1);
            }
        }

    }

    private void processMessageDeletionQueue() {
        int $$0 = this.minecraft.gui.getGuiTicks();
        this.messageDeletionQueue.removeIf((p_250713_) -> {
            if ($$0 >= p_250713_.deletableAfter()) {
                return this.deleteMessageOrDelay(p_250713_.signature()) == null;
            } else {
                return false;
            }
        });
    }

    public void deleteMessage(MessageSignature p_241324_) {
        DelayedMessageDeletion $$1 = this.deleteMessageOrDelay(p_241324_);
        if ($$1 != null) {
            this.messageDeletionQueue.add($$1);
        }

    }

    @Nullable
    private DelayedMessageDeletion deleteMessageOrDelay(MessageSignature p_251812_) {
        int $$1 = this.minecraft.gui.getGuiTicks();
        ListIterator<GuiMessage> $$2 = this.allMessages.listIterator();

        GuiMessage $$3;
        do {
            if (!$$2.hasNext()) {
                return null;
            }

            $$3 = (GuiMessage)$$2.next();
        } while(!p_251812_.equals($$3.signature()));

        int $$4 = $$3.addedTime() + 60;
        if ($$1 >= $$4) {
            $$2.set(this.createDeletedMarker($$3));
            this.refreshTrimmedMessage();
            return null;
        } else {
            return new DelayedMessageDeletion(p_251812_, $$4);
        }
    }

    private GuiMessage createDeletedMarker(GuiMessage p_249789_) {
        return new GuiMessage(p_249789_.addedTime(), DELETED_CHAT_MESSAGE, (MessageSignature)null, GuiMessageTag.system());
    }

    public void rescaleChat() {
        this.resetChatScroll();
        this.refreshTrimmedMessage();
    }

    private void refreshTrimmedMessage() {
        this.trimmedMessages.clear();

        for(int $$0 = this.allMessages.size() - 1; $$0 >= 0; --$$0) {
            GuiMessage $$1 = (GuiMessage)this.allMessages.get($$0);
            this.addMessage($$1.content(), $$1.signature(), $$1.addedTime(), $$1.tag(), true);
        }

    }

    public List<String> getRecentChat() {
        return this.recentChat;
    }

    public void addRecentChat(String p_93784_) {
        if (this.recentChat.isEmpty() || !((String)this.recentChat.get(this.recentChat.size() - 1)).equals(p_93784_)) {
            this.recentChat.add(p_93784_);
        }

    }

    public void resetChatScroll() {
        this.chatScrollbarPos = 0;
        this.newMessageSinceScroll = false;
    }

    public void scrollChat(int p_205361_) {
        this.chatScrollbarPos += p_205361_;
        int $$1 = this.trimmedMessages.size();
        if (this.chatScrollbarPos > $$1 - this.getLinesPerPage()) {
            this.chatScrollbarPos = $$1 - this.getLinesPerPage();
        }

        if (this.chatScrollbarPos <= 0) {
            this.chatScrollbarPos = 0;
            this.newMessageSinceScroll = false;
        }

    }

    public boolean handleChatQueueClicked(double p_93773_, double p_93774_) {
        if (this.isChatFocused() && !this.minecraft.options.hideGui && !this.isChatHidden()) {
            ChatListener $$2 = this.minecraft.getChatListener();
            if ($$2.queueSize() == 0L) {
                return false;
            } else {
                double $$3 = p_93773_ - 2.0;
                double $$4 = (double)this.minecraft.getWindow().getGuiScaledHeight() - p_93774_ - 40.0;
                if ($$3 <= (double)Mth.floor((double)this.getWidth() / this.getScale()) && $$4 < 0.0 && $$4 > (double)Mth.floor(-9.0 * this.getScale())) {
                    $$2.acceptNextDelayedMessage();
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    @Nullable
    public Style getClickedComponentStyleAt(double p_93801_, double p_93802_) {
        double $$2 = this.screenToChatX(p_93801_);
        double $$3 = this.screenToChatY(p_93802_);
        int $$4 = this.getMessageLineIndexAt($$2, $$3);
        if ($$4 >= 0 && $$4 < this.trimmedMessages.size()) {
            GuiMessage.Line $$5 = (GuiMessage.Line)this.trimmedMessages.get($$4);
            return this.minecraft.font.getSplitter().componentStyleAtWidth($$5.content(), Mth.floor($$2));
        } else {
            return null;
        }
    }

    @Nullable
    public GuiMessageTag getMessageTagAt(double p_240576_, double p_240554_) {
        double $$2 = this.screenToChatX(p_240576_);
        double $$3 = this.screenToChatY(p_240554_);
        int $$4 = this.getMessageEndIndexAt($$2, $$3);
        if ($$4 >= 0 && $$4 < this.trimmedMessages.size()) {
            GuiMessage.Line $$5 = (GuiMessage.Line)this.trimmedMessages.get($$4);
            GuiMessageTag $$6 = $$5.tag();
            if ($$6 != null && this.hasSelectedMessageTag($$2, $$5, $$6)) {
                return $$6;
            }
        }

        return null;
    }

    private boolean hasSelectedMessageTag(double p_240619_, GuiMessage.Line p_240547_, GuiMessageTag p_240637_) {
        if (p_240619_ < 0.0) {
            return true;
        } else {
            GuiMessageTag.Icon $$3 = p_240637_.icon();
            if ($$3 == null) {
                return false;
            } else {
                int $$4 = this.getTagIconLeft(p_240547_);
                int $$5 = $$4 + $$3.width;
                return p_240619_ >= (double)$$4 && p_240619_ <= (double)$$5;
            }
        }
    }

    private double screenToChatX(double p_240580_) {
        return p_240580_ / this.getScale() - 4.0;
    }

    private double screenToChatY(double p_240548_) {
        double $$1 = (double)this.minecraft.getWindow().getGuiScaledHeight() - p_240548_ - 40.0;
        return $$1 / (this.getScale() * (double)this.getLineHeight());
    }

    private int getMessageEndIndexAt(double p_249245_, double p_252282_) {
        int $$2 = this.getMessageLineIndexAt(p_249245_, p_252282_);
        if ($$2 == -1) {
            return -1;
        } else {
            while($$2 >= 0) {
                if (((GuiMessage.Line)this.trimmedMessages.get($$2)).endOfEntry()) {
                    return $$2;
                }

                --$$2;
            }

            return $$2;
        }
    }

    private int getMessageLineIndexAt(double p_249099_, double p_250008_) {
        if (this.isChatFocused() && !this.minecraft.options.hideGui && !this.isChatHidden()) {
            if (!(p_249099_ < -4.0) && !(p_249099_ > (double)Mth.floor((double)this.getWidth() / this.getScale()))) {
                int $$2 = Math.min(this.getLinesPerPage(), this.trimmedMessages.size());
                if (p_250008_ >= 0.0 && p_250008_ < (double)$$2) {
                    int $$3 = Mth.floor(p_250008_ + (double)this.chatScrollbarPos);
                    if ($$3 >= 0 && $$3 < this.trimmedMessages.size()) {
                        return $$3;
                    }
                }

                return -1;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    private boolean isChatFocused() {
        return this.minecraft.screen instanceof ChatScreen;
    }

    public int getWidth() {
        return getWidth((Double)this.minecraft.options.chatWidth().get());
    }

    public int getHeight() {
        return getHeight(this.isChatFocused() ? (Double)this.minecraft.options.chatHeightFocused().get() : (Double)this.minecraft.options.chatHeightUnfocused().get());
    }

    public double getScale() {
        return (Double)this.minecraft.options.chatScale().get();
    }

    public static int getWidth(double p_93799_) {
        int $$1 = true;
        int $$2 = true;
        return Mth.floor(p_93799_ * 280.0 + 40.0);
    }

    public static int getHeight(double p_93812_) {
        int $$1 = true;
        int $$2 = true;
        return Mth.floor(p_93812_ * 160.0 + 20.0);
    }

    public static double defaultUnfocusedPct() {
        int $$0 = true;
        int $$1 = true;
        return 70.0 / (double)(getHeight(1.0) - 20);
    }

    public int getLinesPerPage() {
        return this.getHeight() / this.getLineHeight();
    }

    private int getLineHeight() {
        Objects.requireNonNull(this.minecraft.font);
        return (int)(9.0 * ((Double)this.minecraft.options.chatLineSpacing().get() + 1.0));
    }

    static {
        DELETED_CHAT_MESSAGE = Component.translatable("chat.deleted_marker").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);
    }

    @OnlyIn(Dist.CLIENT)
    private static record DelayedMessageDeletion(MessageSignature signature, int deletableAfter) {
        DelayedMessageDeletion(MessageSignature signature, int deletableAfter) {
            this.signature = signature;
            this.deletableAfter = deletableAfter;
        }

        public MessageSignature signature() {
            return this.signature;
        }

        public int deletableAfter() {
            return this.deletableAfter;
        }
    }
}

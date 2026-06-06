//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.screens.social;

import java.util.Collection;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SocialInteractionsScreen extends Screen {
    protected static final ResourceLocation SOCIAL_INTERACTIONS_LOCATION = new ResourceLocation("textures/gui/social_interactions.png");
    private static final Component TAB_ALL = Component.translatable("gui.socialInteractions.tab_all");
    private static final Component TAB_HIDDEN = Component.translatable("gui.socialInteractions.tab_hidden");
    private static final Component TAB_BLOCKED = Component.translatable("gui.socialInteractions.tab_blocked");
    private static final Component TAB_ALL_SELECTED;
    private static final Component TAB_HIDDEN_SELECTED;
    private static final Component TAB_BLOCKED_SELECTED;
    private static final Component SEARCH_HINT;
    static final Component EMPTY_SEARCH;
    private static final Component EMPTY_HIDDEN;
    private static final Component EMPTY_BLOCKED;
    private static final Component BLOCKING_HINT;
    private static final int BG_BORDER_SIZE = 8;
    private static final int BG_WIDTH = 236;
    private static final int SEARCH_HEIGHT = 16;
    private static final int MARGIN_Y = 64;
    public static final int SEARCH_START = 72;
    public static final int LIST_START = 88;
    private static final int IMAGE_WIDTH = 238;
    private static final int BUTTON_HEIGHT = 20;
    private static final int ITEM_HEIGHT = 36;
    SocialInteractionsPlayerList socialInteractionsPlayerList;
    EditBox searchBox;
    private String lastSearch = "";
    private Page page;
    private Button allButton;
    private Button hiddenButton;
    private Button blockedButton;
    private Button blockingHintButton;
    @Nullable
    private Component serverLabel;
    private int playerCount;
    private boolean initialized;

    public SocialInteractionsScreen() {
        super(Component.translatable("gui.socialInteractions.title"));
        this.page = net.minecraft.client.gui.screens.social.SocialInteractionsScreen.Page.ALL;
        this.updateServerLabel(Minecraft.getInstance());
    }

    private int windowHeight() {
        return Math.max(52, this.height - 128 - 16);
    }

    private int listEnd() {
        return 80 + this.windowHeight() - 8;
    }

    private int marginX() {
        return (this.width - 238) / 2;
    }

    public Component getNarrationMessage() {
        return (Component)(this.serverLabel != null ? CommonComponents.joinForNarration(super.getNarrationMessage(), this.serverLabel) : super.getNarrationMessage());
    }

    public void tick() {
        super.tick();
        this.searchBox.tick();
    }

    protected void init() {
        if (this.initialized) {
            this.socialInteractionsPlayerList.updateSize(this.width, this.height, 88, this.listEnd());
        } else {
            this.socialInteractionsPlayerList = new SocialInteractionsPlayerList(this, this.minecraft, this.width, this.height, 88, this.listEnd(), 36);
        }

        int $$0 = this.socialInteractionsPlayerList.getRowWidth() / 3;
        int $$1 = this.socialInteractionsPlayerList.getRowLeft();
        int $$2 = this.socialInteractionsPlayerList.getRowRight();
        int $$3 = this.font.width((FormattedText)BLOCKING_HINT) + 40;
        int $$4 = 64 + this.windowHeight();
        int $$5 = (this.width - $$3) / 2 + 3;
        this.allButton = (Button)this.addRenderableWidget(Button.builder(TAB_ALL, (p_240243_) -> {
            this.showPage(net.minecraft.client.gui.screens.social.SocialInteractionsScreen.Page.ALL);
        }).bounds($$1, 45, $$0, 20).build());
        this.hiddenButton = (Button)this.addRenderableWidget(Button.builder(TAB_HIDDEN, (p_100791_) -> {
            this.showPage(net.minecraft.client.gui.screens.social.SocialInteractionsScreen.Page.HIDDEN);
        }).bounds(($$1 + $$2 - $$0) / 2 + 1, 45, $$0, 20).build());
        this.blockedButton = (Button)this.addRenderableWidget(Button.builder(TAB_BLOCKED, (p_100785_) -> {
            this.showPage(net.minecraft.client.gui.screens.social.SocialInteractionsScreen.Page.BLOCKED);
        }).bounds($$2 - $$0 + 1, 45, $$0, 20).build());
        String $$6 = this.searchBox != null ? this.searchBox.getValue() : "";
        this.searchBox = new EditBox(this.font, this.marginX() + 29, 75, 198, 13, SEARCH_HINT) {
            protected MutableComponent createNarrationMessage() {
                return !SocialInteractionsScreen.this.searchBox.getValue().isEmpty() && SocialInteractionsScreen.this.socialInteractionsPlayerList.isEmpty() ? super.createNarrationMessage().append(", ").append(SocialInteractionsScreen.EMPTY_SEARCH) : super.createNarrationMessage();
            }
        };
        this.searchBox.setMaxLength(16);
        this.searchBox.setVisible(true);
        this.searchBox.setTextColor(16777215);
        this.searchBox.setValue($$6);
        this.searchBox.setHint(SEARCH_HINT);
        this.searchBox.setResponder(this::checkSearchStringUpdate);
        this.addWidget(this.searchBox);
        this.addWidget(this.socialInteractionsPlayerList);
        this.blockingHintButton = (Button)this.addRenderableWidget(Button.builder(BLOCKING_HINT, (p_280890_) -> {
            this.minecraft.setScreen(new ConfirmLinkScreen((p_280891_) -> {
                if (p_280891_) {
                    Util.getPlatform().openUri("https://aka.ms/javablocking");
                }

                this.minecraft.setScreen(this);
            }, "https://aka.ms/javablocking", true));
        }).bounds($$5, $$4, $$3, 20).build());
        this.initialized = true;
        this.showPage(this.page);
    }

    private void showPage(Page p_100772_) {
        this.page = p_100772_;
        this.allButton.setMessage(TAB_ALL);
        this.hiddenButton.setMessage(TAB_HIDDEN);
        this.blockedButton.setMessage(TAB_BLOCKED);
        boolean $$1 = false;
        switch (p_100772_) {
            case ALL:
                this.allButton.setMessage(TAB_ALL_SELECTED);
                Collection<UUID> $$2 = this.minecraft.player.connection.getOnlinePlayerIds();
                this.socialInteractionsPlayerList.updatePlayerList($$2, this.socialInteractionsPlayerList.getScrollAmount(), true);
                break;
            case HIDDEN:
                this.hiddenButton.setMessage(TAB_HIDDEN_SELECTED);
                Set<UUID> $$3 = this.minecraft.getPlayerSocialManager().getHiddenPlayers();
                $$1 = $$3.isEmpty();
                this.socialInteractionsPlayerList.updatePlayerList($$3, this.socialInteractionsPlayerList.getScrollAmount(), false);
                break;
            case BLOCKED:
                this.blockedButton.setMessage(TAB_BLOCKED_SELECTED);
                PlayerSocialManager $$4 = this.minecraft.getPlayerSocialManager();
                Stream var10000 = this.minecraft.player.connection.getOnlinePlayerIds().stream();
                Objects.requireNonNull($$4);
                Set<UUID> $$5 = (Set)var10000.filter($$4::isBlocked).collect(Collectors.toSet());
                $$1 = $$5.isEmpty();
                this.socialInteractionsPlayerList.updatePlayerList($$5, this.socialInteractionsPlayerList.getScrollAmount(), false);
        }

        GameNarrator $$6 = this.minecraft.getNarrator();
        if (!this.searchBox.getValue().isEmpty() && this.socialInteractionsPlayerList.isEmpty() && !this.searchBox.isFocused()) {
            $$6.sayNow(EMPTY_SEARCH);
        } else if ($$1) {
            if (p_100772_ == net.minecraft.client.gui.screens.social.SocialInteractionsScreen.Page.HIDDEN) {
                $$6.sayNow(EMPTY_HIDDEN);
            } else if (p_100772_ == net.minecraft.client.gui.screens.social.SocialInteractionsScreen.Page.BLOCKED) {
                $$6.sayNow(EMPTY_BLOCKED);
            }
        }

    }

    public void renderBackground(GuiGraphics p_283202_) {
        int $$1 = this.marginX() + 3;
        super.renderBackground(p_283202_);
        p_283202_.blitNineSliced(SOCIAL_INTERACTIONS_LOCATION, $$1, 64, 236, this.windowHeight() + 16, 8, 236, 34, 1, 1);
        p_283202_.blit(SOCIAL_INTERACTIONS_LOCATION, $$1 + 10, 76, 243, 1, 12, 12);
    }

    public void render(GuiGraphics p_282516_, int p_100764_, int p_100765_, float p_100766_) {
        this.updateServerLabel(this.minecraft);
        this.renderBackground(p_282516_);
        if (this.serverLabel != null) {
            p_282516_.drawString(this.minecraft.font, (Component)this.serverLabel, this.marginX() + 8, 35, -1);
        }

        if (!this.socialInteractionsPlayerList.isEmpty()) {
            this.socialInteractionsPlayerList.render(p_282516_, p_100764_, p_100765_, p_100766_);
        } else if (!this.searchBox.getValue().isEmpty()) {
            p_282516_.drawCenteredString(this.minecraft.font, (Component)EMPTY_SEARCH, this.width / 2, (72 + this.listEnd()) / 2, -1);
        } else if (this.page == net.minecraft.client.gui.screens.social.SocialInteractionsScreen.Page.HIDDEN) {
            p_282516_.drawCenteredString(this.minecraft.font, (Component)EMPTY_HIDDEN, this.width / 2, (72 + this.listEnd()) / 2, -1);
        } else if (this.page == net.minecraft.client.gui.screens.social.SocialInteractionsScreen.Page.BLOCKED) {
            p_282516_.drawCenteredString(this.minecraft.font, (Component)EMPTY_BLOCKED, this.width / 2, (72 + this.listEnd()) / 2, -1);
        }

        this.searchBox.render(p_282516_, p_100764_, p_100765_, p_100766_);
        this.blockingHintButton.visible = this.page == net.minecraft.client.gui.screens.social.SocialInteractionsScreen.Page.BLOCKED;
        super.render(p_282516_, p_100764_, p_100765_, p_100766_);
    }

    public boolean keyPressed(int p_100757_, int p_100758_, int p_100759_) {
        if (!this.searchBox.isFocused() && this.minecraft.options.keySocialInteractions.matches(p_100757_, p_100758_)) {
            this.minecraft.setScreen((Screen)null);
            return true;
        } else {
            return super.keyPressed(p_100757_, p_100758_, p_100759_);
        }
    }

    public boolean isPauseScreen() {
        return false;
    }

    private void checkSearchStringUpdate(String p_100789_) {
        p_100789_ = p_100789_.toLowerCase(Locale.ROOT);
        if (!p_100789_.equals(this.lastSearch)) {
            this.socialInteractionsPlayerList.setFilter(p_100789_);
            this.lastSearch = p_100789_;
            this.showPage(this.page);
        }

    }

    private void updateServerLabel(Minecraft p_100768_) {
        int $$1 = p_100768_.getConnection().getOnlinePlayers().size();
        if (this.playerCount != $$1) {
            String $$2 = "";
            ServerData $$3 = p_100768_.getCurrentServer();
            if (p_100768_.isLocalServer()) {
                $$2 = p_100768_.getSingleplayerServer().getMotd();
            } else if ($$3 != null) {
                $$2 = $$3.name;
            }

            if ($$1 > 1) {
                this.serverLabel = Component.translatable("gui.socialInteractions.server_label.multiple", $$2, $$1);
            } else {
                this.serverLabel = Component.translatable("gui.socialInteractions.server_label.single", $$2, $$1);
            }

            this.playerCount = $$1;
        }

    }

    public void onAddPlayer(PlayerInfo p_100776_) {
        this.socialInteractionsPlayerList.addPlayer(p_100776_, this.page);
    }

    public void onRemovePlayer(UUID p_100780_) {
        this.socialInteractionsPlayerList.removePlayer(p_100780_);
    }

    static {
        TAB_ALL_SELECTED = TAB_ALL.plainCopy().withStyle(ChatFormatting.UNDERLINE);
        TAB_HIDDEN_SELECTED = TAB_HIDDEN.plainCopy().withStyle(ChatFormatting.UNDERLINE);
        TAB_BLOCKED_SELECTED = TAB_BLOCKED.plainCopy().withStyle(ChatFormatting.UNDERLINE);
        SEARCH_HINT = Component.translatable("gui.socialInteractions.search_hint").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY);
        EMPTY_SEARCH = Component.translatable("gui.socialInteractions.search_empty").withStyle(ChatFormatting.GRAY);
        EMPTY_HIDDEN = Component.translatable("gui.socialInteractions.empty_hidden").withStyle(ChatFormatting.GRAY);
        EMPTY_BLOCKED = Component.translatable("gui.socialInteractions.empty_blocked").withStyle(ChatFormatting.GRAY);
        BLOCKING_HINT = Component.translatable("gui.socialInteractions.blocking_hint");
    }

    @OnlyIn(Dist.CLIENT)
    public static enum Page {
        ALL,
        HIDDEN,
        BLOCKED;

        private Page() {
        }
    }
}

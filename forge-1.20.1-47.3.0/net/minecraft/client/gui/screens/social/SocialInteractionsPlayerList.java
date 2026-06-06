//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.screens.social;

import com.google.common.base.Strings;
import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.screens.social.SocialInteractionsScreen.Page;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.multiplayer.chat.ChatLog;
import net.minecraft.client.multiplayer.chat.LoggedChatEvent;
import net.minecraft.client.multiplayer.chat.LoggedChatMessage;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SocialInteractionsPlayerList extends ContainerObjectSelectionList<PlayerEntry> {
    private final SocialInteractionsScreen socialInteractionsScreen;
    private final List<PlayerEntry> players = Lists.newArrayList();
    @Nullable
    private String filter;

    public SocialInteractionsPlayerList(SocialInteractionsScreen p_100697_, Minecraft p_100698_, int p_100699_, int p_100700_, int p_100701_, int p_100702_, int p_100703_) {
        super(p_100698_, p_100699_, p_100700_, p_100701_, p_100702_, p_100703_);
        this.socialInteractionsScreen = p_100697_;
        this.setRenderBackground(false);
        this.setRenderTopAndBottom(false);
    }

    protected void enableScissor(GuiGraphics p_281892_) {
        p_281892_.enableScissor(this.x0, this.y0 + 4, this.x1, this.y1);
    }

    public void updatePlayerList(Collection<UUID> p_240798_, double p_240792_, boolean p_240829_) {
        Map<UUID, PlayerEntry> $$3 = new HashMap();
        this.addOnlinePlayers(p_240798_, $$3);
        this.updatePlayersFromChatLog($$3, p_240829_);
        this.updateFiltersAndScroll($$3.values(), p_240792_);
    }

    private void addOnlinePlayers(Collection<UUID> p_240813_, Map<UUID, PlayerEntry> p_240796_) {
        ClientPacketListener $$2 = this.minecraft.player.connection;
        Iterator var4 = p_240813_.iterator();

        while(var4.hasNext()) {
            UUID $$3 = (UUID)var4.next();
            PlayerInfo $$4 = $$2.getPlayerInfo($$3);
            if ($$4 != null) {
                boolean $$5 = $$4.hasVerifiableChat();
                Minecraft var10004 = this.minecraft;
                SocialInteractionsScreen var10005 = this.socialInteractionsScreen;
                String var10007 = $$4.getProfile().getName();
                Objects.requireNonNull($$4);
                p_240796_.put($$3, new PlayerEntry(var10004, var10005, $$3, var10007, $$4::getSkinLocation, $$5));
            }
        }

    }

    private void updatePlayersFromChatLog(Map<UUID, PlayerEntry> p_240780_, boolean p_240827_) {
        Collection<GameProfile> $$2 = collectProfilesFromChatLog(this.minecraft.getReportingContext().chatLog());
        Iterator var4 = $$2.iterator();

        while(true) {
            PlayerEntry $$5;
            do {
                if (!var4.hasNext()) {
                    return;
                }

                GameProfile $$3 = (GameProfile)var4.next();
                if (p_240827_) {
                    $$5 = (PlayerEntry)p_240780_.computeIfAbsent($$3.getId(), (p_243147_) -> {
                        PlayerEntry $$2 = new PlayerEntry(this.minecraft, this.socialInteractionsScreen, $$3.getId(), $$3.getName(), Suppliers.memoize(() -> {
                            return this.minecraft.getSkinManager().getInsecureSkinLocation($$3);
                        }), true);
                        $$2.setRemoved(true);
                        return $$2;
                    });
                    break;
                }

                $$5 = (PlayerEntry)p_240780_.get($$3.getId());
            } while($$5 == null);

            $$5.setHasRecentMessages(true);
        }
    }

    private static Collection<GameProfile> collectProfilesFromChatLog(ChatLog p_250748_) {
        Set<GameProfile> $$1 = new ObjectLinkedOpenHashSet();

        for(int $$2 = p_250748_.end(); $$2 >= p_250748_.start(); --$$2) {
            LoggedChatEvent $$3 = p_250748_.lookup($$2);
            if ($$3 instanceof LoggedChatMessage.Player $$4) {
                if ($$4.message().hasSignature()) {
                    $$1.add($$4.profile());
                }
            }
        }

        return $$1;
    }

    private void sortPlayerEntries() {
        this.players.sort(Comparator.comparing((p_240744_) -> {
            if (p_240744_.getPlayerId().equals(this.minecraft.getUser().getProfileId())) {
                return 0;
            } else if (p_240744_.getPlayerId().version() == 2) {
                return 4;
            } else if (this.minecraft.getReportingContext().hasDraftReportFor(p_240744_.getPlayerId())) {
                return 1;
            } else {
                return p_240744_.hasRecentMessages() ? 2 : 3;
            }
        }).thenComparing((p_240745_) -> {
            if (!p_240745_.getPlayerName().isBlank()) {
                int $$1 = p_240745_.getPlayerName().codePointAt(0);
                if ($$1 == 95 || $$1 >= 97 && $$1 <= 122 || $$1 >= 65 && $$1 <= 90 || $$1 >= 48 && $$1 <= 57) {
                    return 0;
                }
            }

            return 1;
        }).thenComparing(PlayerEntry::getPlayerName, String::compareToIgnoreCase));
    }

    private void updateFiltersAndScroll(Collection<PlayerEntry> p_240809_, double p_240830_) {
        this.players.clear();
        this.players.addAll(p_240809_);
        this.sortPlayerEntries();
        this.updateFilteredPlayers();
        this.replaceEntries(this.players);
        this.setScrollAmount(p_240830_);
    }

    private void updateFilteredPlayers() {
        if (this.filter != null) {
            this.players.removeIf((p_100710_) -> {
                return !p_100710_.getPlayerName().toLowerCase(Locale.ROOT).contains(this.filter);
            });
            this.replaceEntries(this.players);
        }

    }

    public void setFilter(String p_100718_) {
        this.filter = p_100718_;
    }

    public boolean isEmpty() {
        return this.players.isEmpty();
    }

    public void addPlayer(PlayerInfo p_100715_, SocialInteractionsScreen.Page p_100716_) {
        UUID $$2 = p_100715_.getProfile().getId();
        Iterator var4 = this.players.iterator();

        PlayerEntry $$5;
        while(var4.hasNext()) {
            $$5 = (PlayerEntry)var4.next();
            if ($$5.getPlayerId().equals($$2)) {
                $$5.setRemoved(false);
                return;
            }
        }

        if ((p_100716_ == Page.ALL || this.minecraft.getPlayerSocialManager().shouldHideMessageFrom($$2)) && (Strings.isNullOrEmpty(this.filter) || p_100715_.getProfile().getName().toLowerCase(Locale.ROOT).contains(this.filter))) {
            boolean $$4 = p_100715_.hasVerifiableChat();
            Minecraft var10002 = this.minecraft;
            SocialInteractionsScreen var10003 = this.socialInteractionsScreen;
            UUID var10004 = p_100715_.getProfile().getId();
            String var10005 = p_100715_.getProfile().getName();
            Objects.requireNonNull(p_100715_);
            $$5 = new PlayerEntry(var10002, var10003, var10004, var10005, p_100715_::getSkinLocation, $$4);
            this.addEntry($$5);
            this.players.add($$5);
        }

    }

    public void removePlayer(UUID p_100723_) {
        Iterator var2 = this.players.iterator();

        PlayerEntry $$1;
        do {
            if (!var2.hasNext()) {
                return;
            }

            $$1 = (PlayerEntry)var2.next();
        } while(!$$1.getPlayerId().equals(p_100723_));

        $$1.setRemoved(true);
    }
}

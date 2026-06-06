//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.components;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Optionull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PlayerTabOverlay {
    private static final Comparator<PlayerInfo> PLAYER_COMPARATOR = Comparator.comparingInt((p_253306_) -> {
        return p_253306_.getGameMode() == GameType.SPECTATOR ? 1 : 0;
    }).thenComparing((p_269613_) -> {
        return (String)Optionull.mapOrDefault(p_269613_.getTeam(), PlayerTeam::getName, "");
    }).thenComparing((p_253305_) -> {
        return p_253305_.getProfile().getName();
    }, String::compareToIgnoreCase);
    private static final ResourceLocation GUI_ICONS_LOCATION = new ResourceLocation("textures/gui/icons.png");
    public static final int MAX_ROWS_PER_COL = 20;
    public static final int HEART_EMPTY_CONTAINER = 16;
    public static final int HEART_EMPTY_CONTAINER_BLINKING = 25;
    public static final int HEART_FULL = 52;
    public static final int HEART_HALF_FULL = 61;
    public static final int HEART_GOLDEN_FULL = 160;
    public static final int HEART_GOLDEN_HALF_FULL = 169;
    public static final int HEART_GHOST_FULL = 70;
    public static final int HEART_GHOST_HALF_FULL = 79;
    private final Minecraft minecraft;
    private final Gui gui;
    @Nullable
    private Component footer;
    @Nullable
    private Component header;
    private boolean visible;
    private final Map<UUID, HealthState> healthStates = new Object2ObjectOpenHashMap();

    public PlayerTabOverlay(Minecraft p_94527_, Gui p_94528_) {
        this.minecraft = p_94527_;
        this.gui = p_94528_;
    }

    public Component getNameForDisplay(PlayerInfo p_94550_) {
        return p_94550_.getTabListDisplayName() != null ? this.decorateName(p_94550_, p_94550_.getTabListDisplayName().copy()) : this.decorateName(p_94550_, PlayerTeam.formatNameForTeam(p_94550_.getTeam(), Component.literal(p_94550_.getProfile().getName())));
    }

    private Component decorateName(PlayerInfo p_94552_, MutableComponent p_94553_) {
        return p_94552_.getGameMode() == GameType.SPECTATOR ? p_94553_.withStyle(ChatFormatting.ITALIC) : p_94553_;
    }

    public void setVisible(boolean p_94557_) {
        if (this.visible != p_94557_) {
            this.healthStates.clear();
            this.visible = p_94557_;
            if (p_94557_) {
                Component $$1 = ComponentUtils.formatList(this.getPlayerInfos(), (Component)Component.literal(", "), this::getNameForDisplay);
                this.minecraft.getNarrator().sayNow((Component)Component.translatable("multiplayer.player.list.narration", $$1));
            }
        }

    }

    private List<PlayerInfo> getPlayerInfos() {
        return this.minecraft.player.connection.getListedOnlinePlayers().stream().sorted(PLAYER_COMPARATOR).limit(80L).toList();
    }

    public void render(GuiGraphics p_281484_, int p_283602_, Scoreboard p_282338_, @Nullable Objective p_282369_) {
        List<PlayerInfo> $$4 = this.getPlayerInfos();
        int $$5 = 0;
        int $$6 = 0;
        Iterator var8 = $$4.iterator();

        int $$12;
        while(var8.hasNext()) {
            PlayerInfo $$7 = (PlayerInfo)var8.next();
            $$12 = this.minecraft.font.width((FormattedText)this.getNameForDisplay($$7));
            $$5 = Math.max($$5, $$12);
            if (p_282369_ != null && p_282369_.getRenderType() != RenderType.HEARTS) {
                Font var10000 = this.minecraft.font;
                Score var10001 = p_282338_.getOrCreatePlayerScore($$7.getProfile().getName(), p_282369_);
                $$12 = var10000.width(" " + var10001.getScore());
                $$6 = Math.max($$6, $$12);
            }
        }

        if (!this.healthStates.isEmpty()) {
            Set<UUID> $$9 = (Set)$$4.stream().map((p_250472_) -> {
                return p_250472_.getProfile().getId();
            }).collect(Collectors.toSet());
            this.healthStates.keySet().removeIf((p_248583_) -> {
                return !$$9.contains(p_248583_);
            });
        }

        int $$10 = $$4.size();
        int $$11 = $$10;

        for($$12 = 1; $$11 > 20; $$11 = ($$10 + $$12 - 1) / $$12) {
            ++$$12;
        }

        boolean $$13 = this.minecraft.isLocalServer() || this.minecraft.getConnection().getConnection().isEncrypted();
        int $$16;
        if (p_282369_ != null) {
            if (p_282369_.getRenderType() == RenderType.HEARTS) {
                $$16 = 90;
            } else {
                $$16 = $$6;
            }
        } else {
            $$16 = 0;
        }

        int $$17 = Math.min($$12 * (($$13 ? 9 : 0) + $$5 + $$16 + 13), p_283602_ - 50) / $$12;
        int $$18 = p_283602_ / 2 - ($$17 * $$12 + ($$12 - 1) * 5) / 2;
        int $$19 = 10;
        int $$20 = $$17 * $$12 + ($$12 - 1) * 5;
        List<FormattedCharSequence> $$21 = null;
        if (this.header != null) {
            $$21 = this.minecraft.font.split(this.header, p_283602_ - 50);

            FormattedCharSequence $$22;
            for(Iterator var18 = $$21.iterator(); var18.hasNext(); $$20 = Math.max($$20, this.minecraft.font.width($$22))) {
                $$22 = (FormattedCharSequence)var18.next();
            }
        }

        List<FormattedCharSequence> $$23 = null;
        FormattedCharSequence $$25;
        Iterator var35;
        if (this.footer != null) {
            $$23 = this.minecraft.font.split(this.footer, p_283602_ - 50);

            for(var35 = $$23.iterator(); var35.hasNext(); $$20 = Math.max($$20, this.minecraft.font.width($$25))) {
                $$25 = (FormattedCharSequence)var35.next();
            }
        }

        int var10002;
        int var10003;
        int var10005;
        int $$29;
        int var33;
        if ($$21 != null) {
            var33 = p_283602_ / 2 - $$20 / 2 - 1;
            var10002 = $$19 - 1;
            var10003 = p_283602_ / 2 + $$20 / 2 + 1;
            var10005 = $$21.size();
            Objects.requireNonNull(this.minecraft.font);
            p_281484_.fill(var33, var10002, var10003, $$19 + var10005 * 9, Integer.MIN_VALUE);

            for(var35 = $$21.iterator(); var35.hasNext(); $$19 += 9) {
                $$25 = (FormattedCharSequence)var35.next();
                $$29 = this.minecraft.font.width($$25);
                p_281484_.drawString(this.minecraft.font, (FormattedCharSequence)$$25, p_283602_ / 2 - $$29 / 2, $$19, -1);
                Objects.requireNonNull(this.minecraft.font);
            }

            ++$$19;
        }

        p_281484_.fill(p_283602_ / 2 - $$20 / 2 - 1, $$19 - 1, p_283602_ / 2 + $$20 / 2 + 1, $$19 + $$11 * 9, Integer.MIN_VALUE);
        int $$27 = this.minecraft.options.getBackgroundColor(553648127);

        int $$41;
        for(int $$28 = 0; $$28 < $$10; ++$$28) {
            $$29 = $$28 / $$11;
            $$41 = $$28 % $$11;
            int $$31 = $$18 + $$29 * $$17 + $$29 * 5;
            int $$32 = $$19 + $$41 * 9;
            p_281484_.fill($$31, $$32, $$31 + $$17, $$32 + 8, $$27);
            RenderSystem.enableBlend();
            if ($$28 < $$4.size()) {
                PlayerInfo $$33 = (PlayerInfo)$$4.get($$28);
                GameProfile $$34 = $$33.getProfile();
                if ($$13) {
                    Player $$35 = this.minecraft.level.getPlayerByUUID($$34.getId());
                    boolean $$36 = $$35 != null && LivingEntityRenderer.isEntityUpsideDown($$35);
                    boolean $$37 = $$35 != null && $$35.isModelPartShown(PlayerModelPart.HAT);
                    PlayerFaceRenderer.draw(p_281484_, $$33.getSkinLocation(), $$31, $$32, 8, $$37, $$36);
                    $$31 += 9;
                }

                p_281484_.drawString(this.minecraft.font, this.getNameForDisplay($$33), $$31, $$32, $$33.getGameMode() == GameType.SPECTATOR ? -1862270977 : -1);
                if (p_282369_ != null && $$33.getGameMode() != GameType.SPECTATOR) {
                    int $$38 = $$31 + $$5 + 1;
                    int $$39 = $$38 + $$16;
                    if ($$39 - $$38 > 5) {
                        this.renderTablistScore(p_282369_, $$32, $$34.getName(), $$38, $$39, $$34.getId(), p_281484_);
                    }
                }

                this.renderPingIcon(p_281484_, $$17, $$31 - ($$13 ? 9 : 0), $$32, $$33);
            }
        }

        if ($$23 != null) {
            $$19 += $$11 * 9 + 1;
            var33 = p_283602_ / 2 - $$20 / 2 - 1;
            var10002 = $$19 - 1;
            var10003 = p_283602_ / 2 + $$20 / 2 + 1;
            var10005 = $$23.size();
            Objects.requireNonNull(this.minecraft.font);
            p_281484_.fill(var33, var10002, var10003, $$19 + var10005 * 9, Integer.MIN_VALUE);

            for(Iterator var38 = $$23.iterator(); var38.hasNext(); $$19 += 9) {
                FormattedCharSequence $$40 = (FormattedCharSequence)var38.next();
                $$41 = this.minecraft.font.width($$40);
                p_281484_.drawString(this.minecraft.font, (FormattedCharSequence)$$40, p_283602_ / 2 - $$41 / 2, $$19, -1);
                Objects.requireNonNull(this.minecraft.font);
            }
        }

    }

    protected void renderPingIcon(GuiGraphics p_283286_, int p_281809_, int p_282801_, int p_282223_, PlayerInfo p_282986_) {
        int $$5 = false;
        byte $$11;
        if (p_282986_.getLatency() < 0) {
            $$11 = 5;
        } else if (p_282986_.getLatency() < 150) {
            $$11 = 0;
        } else if (p_282986_.getLatency() < 300) {
            $$11 = 1;
        } else if (p_282986_.getLatency() < 600) {
            $$11 = 2;
        } else if (p_282986_.getLatency() < 1000) {
            $$11 = 3;
        } else {
            $$11 = 4;
        }

        p_283286_.pose().pushPose();
        p_283286_.pose().translate(0.0F, 0.0F, 100.0F);
        p_283286_.blit(GUI_ICONS_LOCATION, p_282801_ + p_281809_ - 11, p_282223_, 0, 176 + $$11 * 8, 10, 8);
        p_283286_.pose().popPose();
    }

    private void renderTablistScore(Objective p_283381_, int p_282557_, String p_283058_, int p_283533_, int p_281254_, UUID p_283099_, GuiGraphics p_282280_) {
        int $$7 = p_283381_.getScoreboard().getOrCreatePlayerScore(p_283058_, p_283381_).getScore();
        if (p_283381_.getRenderType() == RenderType.HEARTS) {
            this.renderTablistHearts(p_282557_, p_283533_, p_281254_, p_283099_, p_282280_, $$7);
        } else {
            String $$8 = ChatFormatting.YELLOW + $$7;
            p_282280_.drawString(this.minecraft.font, $$8, p_281254_ - this.minecraft.font.width($$8), p_282557_, 16777215);
        }
    }

    private void renderTablistHearts(int p_282904_, int p_283173_, int p_282149_, UUID p_283348_, GuiGraphics p_281723_, int p_281354_) {
        HealthState $$6 = (HealthState)this.healthStates.computeIfAbsent(p_283348_, (p_249546_) -> {
            return new HealthState(p_281354_);
        });
        $$6.update(p_281354_, (long)this.gui.getGuiTicks());
        int $$7 = Mth.positiveCeilDiv(Math.max(p_281354_, $$6.displayedValue()), 2);
        int $$8 = Math.max(p_281354_, Math.max($$6.displayedValue(), 20)) / 2;
        boolean $$9 = $$6.isBlinking((long)this.gui.getGuiTicks());
        if ($$7 > 0) {
            int $$10 = Mth.floor(Math.min((float)(p_282149_ - p_283173_ - 4) / (float)$$8, 9.0F));
            if ($$10 <= 3) {
                float $$11 = Mth.clamp((float)p_281354_ / 20.0F, 0.0F, 1.0F);
                int $$12 = (int)((1.0F - $$11) * 255.0F) << 16 | (int)($$11 * 255.0F) << 8;
                String $$13 = "" + (float)p_281354_ / 2.0F;
                if (p_282149_ - this.minecraft.font.width($$13 + "hp") >= p_283173_) {
                    $$13 = $$13 + "hp";
                }

                p_281723_.drawString(this.minecraft.font, $$13, (p_282149_ + p_283173_ - this.minecraft.font.width($$13)) / 2, p_282904_, $$12);
            } else {
                int $$15;
                for($$15 = $$7; $$15 < $$8; ++$$15) {
                    p_281723_.blit(GUI_ICONS_LOCATION, p_283173_ + $$15 * $$10, p_282904_, $$9 ? 25 : 16, 0, 9, 9);
                }

                for($$15 = 0; $$15 < $$7; ++$$15) {
                    p_281723_.blit(GUI_ICONS_LOCATION, p_283173_ + $$15 * $$10, p_282904_, $$9 ? 25 : 16, 0, 9, 9);
                    if ($$9) {
                        if ($$15 * 2 + 1 < $$6.displayedValue()) {
                            p_281723_.blit(GUI_ICONS_LOCATION, p_283173_ + $$15 * $$10, p_282904_, 70, 0, 9, 9);
                        }

                        if ($$15 * 2 + 1 == $$6.displayedValue()) {
                            p_281723_.blit(GUI_ICONS_LOCATION, p_283173_ + $$15 * $$10, p_282904_, 79, 0, 9, 9);
                        }
                    }

                    if ($$15 * 2 + 1 < p_281354_) {
                        p_281723_.blit(GUI_ICONS_LOCATION, p_283173_ + $$15 * $$10, p_282904_, $$15 >= 10 ? 160 : 52, 0, 9, 9);
                    }

                    if ($$15 * 2 + 1 == p_281354_) {
                        p_281723_.blit(GUI_ICONS_LOCATION, p_283173_ + $$15 * $$10, p_282904_, $$15 >= 10 ? 169 : 61, 0, 9, 9);
                    }
                }

            }
        }
    }

    public void setFooter(@Nullable Component p_94555_) {
        this.footer = p_94555_;
    }

    public void setHeader(@Nullable Component p_94559_) {
        this.header = p_94559_;
    }

    public void reset() {
        this.header = null;
        this.footer = null;
    }

    @OnlyIn(Dist.CLIENT)
    private static class HealthState {
        private static final long DISPLAY_UPDATE_DELAY = 20L;
        private static final long DECREASE_BLINK_DURATION = 20L;
        private static final long INCREASE_BLINK_DURATION = 10L;
        private int lastValue;
        private int displayedValue;
        private long lastUpdateTick;
        private long blinkUntilTick;

        public HealthState(int p_250562_) {
            this.displayedValue = p_250562_;
            this.lastValue = p_250562_;
        }

        public void update(int p_251066_, long p_251460_) {
            if (p_251066_ != this.lastValue) {
                long $$2 = p_251066_ < this.lastValue ? 20L : 10L;
                this.blinkUntilTick = p_251460_ + $$2;
                this.lastValue = p_251066_;
                this.lastUpdateTick = p_251460_;
            }

            if (p_251460_ - this.lastUpdateTick > 20L) {
                this.displayedValue = p_251066_;
            }

        }

        public int displayedValue() {
            return this.displayedValue;
        }

        public boolean isBlinking(long p_251847_) {
            return this.blinkUntilTick > p_251847_ && (this.blinkUntilTick - p_251847_) % 6L >= 3L;
        }
    }
}

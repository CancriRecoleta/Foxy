//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.scores;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.HoverEvent.Action;

public class PlayerTeam extends Team {
    private static final int BIT_FRIENDLY_FIRE = 0;
    private static final int BIT_SEE_INVISIBLES = 1;
    private final Scoreboard scoreboard;
    private final String name;
    private final Set<String> players = Sets.newHashSet();
    private Component displayName;
    private Component playerPrefix;
    private Component playerSuffix;
    private boolean allowFriendlyFire;
    private boolean seeFriendlyInvisibles;
    private Team.Visibility nameTagVisibility;
    private Team.Visibility deathMessageVisibility;
    private ChatFormatting color;
    private Team.CollisionRule collisionRule;
    private final Style displayNameStyle;

    public PlayerTeam(Scoreboard p_83340_, String p_83341_) {
        this.playerPrefix = CommonComponents.EMPTY;
        this.playerSuffix = CommonComponents.EMPTY;
        this.allowFriendlyFire = true;
        this.seeFriendlyInvisibles = true;
        this.nameTagVisibility = net.minecraft.world.scores.Team.Visibility.ALWAYS;
        this.deathMessageVisibility = net.minecraft.world.scores.Team.Visibility.ALWAYS;
        this.color = ChatFormatting.RESET;
        this.collisionRule = net.minecraft.world.scores.Team.CollisionRule.ALWAYS;
        this.scoreboard = p_83340_;
        this.name = p_83341_;
        this.displayName = Component.literal(p_83341_);
        this.displayNameStyle = Style.EMPTY.withInsertion(p_83341_).withHoverEvent(new HoverEvent(Action.SHOW_TEXT, Component.literal(p_83341_)));
    }

    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    public String getName() {
        return this.name;
    }

    public Component getDisplayName() {
        return this.displayName;
    }

    public MutableComponent getFormattedDisplayName() {
        MutableComponent $$0 = ComponentUtils.wrapInSquareBrackets(this.displayName.copy().withStyle(this.displayNameStyle));
        ChatFormatting $$1 = this.getColor();
        if ($$1 != ChatFormatting.RESET) {
            $$0.withStyle($$1);
        }

        return $$0;
    }

    public void setDisplayName(Component p_83354_) {
        if (p_83354_ == null) {
            throw new IllegalArgumentException("Name cannot be null");
        } else {
            this.displayName = p_83354_;
            this.scoreboard.onTeamChanged(this);
        }
    }

    public void setPlayerPrefix(@Nullable Component p_83361_) {
        this.playerPrefix = p_83361_ == null ? CommonComponents.EMPTY : p_83361_;
        this.scoreboard.onTeamChanged(this);
    }

    public Component getPlayerPrefix() {
        return this.playerPrefix;
    }

    public void setPlayerSuffix(@Nullable Component p_83366_) {
        this.playerSuffix = p_83366_ == null ? CommonComponents.EMPTY : p_83366_;
        this.scoreboard.onTeamChanged(this);
    }

    public Component getPlayerSuffix() {
        return this.playerSuffix;
    }

    public Collection<String> getPlayers() {
        return this.players;
    }

    public MutableComponent getFormattedName(Component p_83369_) {
        MutableComponent $$1 = Component.empty().append(this.playerPrefix).append(p_83369_).append(this.playerSuffix);
        ChatFormatting $$2 = this.getColor();
        if ($$2 != ChatFormatting.RESET) {
            $$1.withStyle($$2);
        }

        return $$1;
    }

    public static MutableComponent formatNameForTeam(@Nullable Team p_83349_, Component p_83350_) {
        return p_83349_ == null ? p_83350_.copy() : p_83349_.getFormattedName(p_83350_);
    }

    public boolean isAllowFriendlyFire() {
        return this.allowFriendlyFire;
    }

    public void setAllowFriendlyFire(boolean p_83356_) {
        this.allowFriendlyFire = p_83356_;
        this.scoreboard.onTeamChanged(this);
    }

    public boolean canSeeFriendlyInvisibles() {
        return this.seeFriendlyInvisibles;
    }

    public void setSeeFriendlyInvisibles(boolean p_83363_) {
        this.seeFriendlyInvisibles = p_83363_;
        this.scoreboard.onTeamChanged(this);
    }

    public Team.Visibility getNameTagVisibility() {
        return this.nameTagVisibility;
    }

    public Team.Visibility getDeathMessageVisibility() {
        return this.deathMessageVisibility;
    }

    public void setNameTagVisibility(Team.Visibility p_83347_) {
        this.nameTagVisibility = p_83347_;
        this.scoreboard.onTeamChanged(this);
    }

    public void setDeathMessageVisibility(Team.Visibility p_83359_) {
        this.deathMessageVisibility = p_83359_;
        this.scoreboard.onTeamChanged(this);
    }

    public Team.CollisionRule getCollisionRule() {
        return this.collisionRule;
    }

    public void setCollisionRule(Team.CollisionRule p_83345_) {
        this.collisionRule = p_83345_;
        this.scoreboard.onTeamChanged(this);
    }

    public int packOptions() {
        int $$0 = 0;
        if (this.isAllowFriendlyFire()) {
            $$0 |= 1;
        }

        if (this.canSeeFriendlyInvisibles()) {
            $$0 |= 2;
        }

        return $$0;
    }

    public void unpackOptions(int p_83343_) {
        this.setAllowFriendlyFire((p_83343_ & 1) > 0);
        this.setSeeFriendlyInvisibles((p_83343_ & 2) > 0);
    }

    public void setColor(ChatFormatting p_83352_) {
        this.color = p_83352_;
        this.scoreboard.onTeamChanged(this);
    }

    public ChatFormatting getColor() {
        return this.color;
    }
}

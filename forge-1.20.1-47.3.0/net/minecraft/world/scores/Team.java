//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.scores;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public abstract class Team {
    public Team() {
    }

    public boolean isAlliedTo(@Nullable Team p_83537_) {
        if (p_83537_ == null) {
            return false;
        } else {
            return this == p_83537_;
        }
    }

    public abstract String getName();

    public abstract MutableComponent getFormattedName(Component var1);

    public abstract boolean canSeeFriendlyInvisibles();

    public abstract boolean isAllowFriendlyFire();

    public abstract Visibility getNameTagVisibility();

    public abstract ChatFormatting getColor();

    public abstract Collection<String> getPlayers();

    public abstract Visibility getDeathMessageVisibility();

    public abstract CollisionRule getCollisionRule();

    public static enum CollisionRule {
        ALWAYS("always", 0),
        NEVER("never", 1),
        PUSH_OTHER_TEAMS("pushOtherTeams", 2),
        PUSH_OWN_TEAM("pushOwnTeam", 3);

        private static final Map<String, CollisionRule> BY_NAME = (Map)Arrays.stream(values()).collect(Collectors.toMap((p_83559_) -> {
            return p_83559_.name;
        }, (p_83554_) -> {
            return p_83554_;
        }));
        public final String name;
        public final int id;

        @Nullable
        public static CollisionRule byName(String p_83556_) {
            return (CollisionRule)BY_NAME.get(p_83556_);
        }

        private CollisionRule(String p_83551_, int p_83552_) {
            this.name = p_83551_;
            this.id = p_83552_;
        }

        public Component getDisplayName() {
            return Component.translatable("team.collision." + this.name);
        }
    }

    public static enum Visibility {
        ALWAYS("always", 0),
        NEVER("never", 1),
        HIDE_FOR_OTHER_TEAMS("hideForOtherTeams", 2),
        HIDE_FOR_OWN_TEAM("hideForOwnTeam", 3);

        private static final Map<String, Visibility> BY_NAME = (Map)Arrays.stream(values()).collect(Collectors.toMap((p_83583_) -> {
            return p_83583_.name;
        }, (p_83578_) -> {
            return p_83578_;
        }));
        public final String name;
        public final int id;

        public static String[] getAllNames() {
            return (String[])BY_NAME.keySet().toArray(new String[0]);
        }

        @Nullable
        public static Visibility byName(String p_83580_) {
            return (Visibility)BY_NAME.get(p_83580_);
        }

        private Visibility(String p_83575_, int p_83576_) {
            this.name = p_83575_;
            this.id = p_83576_;
        }

        public Component getDisplayName() {
            return Component.translatable("team.visibility." + this.name);
        }
    }
}

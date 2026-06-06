//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.scores;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.slf4j.Logger;

public class Scoreboard {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int DISPLAY_SLOT_LIST = 0;
    public static final int DISPLAY_SLOT_SIDEBAR = 1;
    public static final int DISPLAY_SLOT_BELOW_NAME = 2;
    public static final int DISPLAY_SLOT_TEAMS_SIDEBAR_START = 3;
    public static final int DISPLAY_SLOT_TEAMS_SIDEBAR_END = 18;
    public static final int DISPLAY_SLOTS = 19;
    private final Map<String, Objective> objectivesByName = Maps.newHashMap();
    private final Map<ObjectiveCriteria, List<Objective>> objectivesByCriteria = Maps.newHashMap();
    private final Map<String, Map<Objective, Score>> playerScores = Maps.newHashMap();
    private final Objective[] displayObjectives = new Objective[19];
    private final Map<String, PlayerTeam> teamsByName = Maps.newHashMap();
    private final Map<String, PlayerTeam> teamsByPlayer = Maps.newHashMap();
    @Nullable
    private static String[] displaySlotNames;

    public Scoreboard() {
    }

    public boolean hasObjective(String p_83460_) {
        return this.objectivesByName.containsKey(p_83460_);
    }

    public Objective getOrCreateObjective(String p_83470_) {
        return (Objective)this.objectivesByName.get(p_83470_);
    }

    @Nullable
    public Objective getObjective(@Nullable String p_83478_) {
        return (Objective)this.objectivesByName.get(p_83478_);
    }

    public Objective addObjective(String p_83437_, ObjectiveCriteria p_83438_, Component p_83439_, ObjectiveCriteria.RenderType p_83440_) {
        if (this.objectivesByName.containsKey(p_83437_)) {
            throw new IllegalArgumentException("An objective with the name '" + p_83437_ + "' already exists!");
        } else {
            Objective $$4 = new Objective(this, p_83437_, p_83438_, p_83439_, p_83440_);
            ((List)this.objectivesByCriteria.computeIfAbsent(p_83438_, (p_83426_) -> {
                return Lists.newArrayList();
            })).add($$4);
            this.objectivesByName.put(p_83437_, $$4);
            this.onObjectiveAdded($$4);
            return $$4;
        }
    }

    public final void forAllObjectives(ObjectiveCriteria p_83428_, String p_83429_, Consumer<Score> p_83430_) {
        ((List)this.objectivesByCriteria.getOrDefault(p_83428_, Collections.emptyList())).forEach((p_83444_) -> {
            p_83430_.accept(this.getOrCreatePlayerScore(p_83429_, p_83444_));
        });
    }

    public boolean hasPlayerScore(String p_83462_, Objective p_83463_) {
        Map<Objective, Score> $$2 = (Map)this.playerScores.get(p_83462_);
        if ($$2 == null) {
            return false;
        } else {
            Score $$3 = (Score)$$2.get(p_83463_);
            return $$3 != null;
        }
    }

    public Score getOrCreatePlayerScore(String p_83472_, Objective p_83473_) {
        Map<Objective, Score> $$2 = (Map)this.playerScores.computeIfAbsent(p_83472_, (p_83507_) -> {
            return Maps.newHashMap();
        });
        return (Score)$$2.computeIfAbsent(p_83473_, (p_83487_) -> {
            Score $$2 = new Score(this, p_83487_, p_83472_);
            $$2.setScore(0);
            return $$2;
        });
    }

    public Collection<Score> getPlayerScores(Objective p_83499_) {
        List<Score> $$1 = Lists.newArrayList();
        Iterator var3 = this.playerScores.values().iterator();

        while(var3.hasNext()) {
            Map<Objective, Score> $$2 = (Map)var3.next();
            Score $$3 = (Score)$$2.get(p_83499_);
            if ($$3 != null) {
                $$1.add($$3);
            }
        }

        $$1.sort(Score.SCORE_COMPARATOR);
        return $$1;
    }

    public Collection<Objective> getObjectives() {
        return this.objectivesByName.values();
    }

    public Collection<String> getObjectiveNames() {
        return this.objectivesByName.keySet();
    }

    public Collection<String> getTrackedPlayers() {
        return Lists.newArrayList(this.playerScores.keySet());
    }

    public void resetPlayerScore(String p_83480_, @Nullable Objective p_83481_) {
        Map $$3;
        if (p_83481_ == null) {
            $$3 = (Map)this.playerScores.remove(p_83480_);
            if ($$3 != null) {
                this.onPlayerRemoved(p_83480_);
            }
        } else {
            $$3 = (Map)this.playerScores.get(p_83480_);
            if ($$3 != null) {
                Score $$4 = (Score)$$3.remove(p_83481_);
                if ($$3.size() < 1) {
                    Map<Objective, Score> $$5 = (Map)this.playerScores.remove(p_83480_);
                    if ($$5 != null) {
                        this.onPlayerRemoved(p_83480_);
                    }
                } else if ($$4 != null) {
                    this.onPlayerScoreRemoved(p_83480_, p_83481_);
                }
            }
        }

    }

    public Map<Objective, Score> getPlayerScores(String p_83484_) {
        Map<Objective, Score> $$1 = (Map)this.playerScores.get(p_83484_);
        if ($$1 == null) {
            $$1 = Maps.newHashMap();
        }

        return (Map)$$1;
    }

    public void removeObjective(Objective p_83503_) {
        this.objectivesByName.remove(p_83503_.getName());

        for(int $$1 = 0; $$1 < 19; ++$$1) {
            if (this.getDisplayObjective($$1) == p_83503_) {
                this.setDisplayObjective($$1, (Objective)null);
            }
        }

        List<Objective> $$2 = (List)this.objectivesByCriteria.get(p_83503_.getCriteria());
        if ($$2 != null) {
            $$2.remove(p_83503_);
        }

        Iterator var3 = this.playerScores.values().iterator();

        while(var3.hasNext()) {
            Map<Objective, Score> $$3 = (Map)var3.next();
            $$3.remove(p_83503_);
        }

        this.onObjectiveRemoved(p_83503_);
    }

    public void setDisplayObjective(int p_83418_, @Nullable Objective p_83419_) {
        this.displayObjectives[p_83418_] = p_83419_;
    }

    @Nullable
    public Objective getDisplayObjective(int p_83417_) {
        return this.displayObjectives[p_83417_];
    }

    @Nullable
    public PlayerTeam getPlayerTeam(String p_83490_) {
        return (PlayerTeam)this.teamsByName.get(p_83490_);
    }

    public PlayerTeam addPlayerTeam(String p_83493_) {
        PlayerTeam $$1 = this.getPlayerTeam(p_83493_);
        if ($$1 != null) {
            LOGGER.warn("Requested creation of existing team '{}'", p_83493_);
            return $$1;
        } else {
            $$1 = new PlayerTeam(this, p_83493_);
            this.teamsByName.put(p_83493_, $$1);
            this.onTeamAdded($$1);
            return $$1;
        }
    }

    public void removePlayerTeam(PlayerTeam p_83476_) {
        this.teamsByName.remove(p_83476_.getName());
        Iterator var2 = p_83476_.getPlayers().iterator();

        while(var2.hasNext()) {
            String $$1 = (String)var2.next();
            this.teamsByPlayer.remove($$1);
        }

        this.onTeamRemoved(p_83476_);
    }

    public boolean addPlayerToTeam(String p_83434_, PlayerTeam p_83435_) {
        if (this.getPlayersTeam(p_83434_) != null) {
            this.removePlayerFromTeam(p_83434_);
        }

        this.teamsByPlayer.put(p_83434_, p_83435_);
        return p_83435_.getPlayers().add(p_83434_);
    }

    public boolean removePlayerFromTeam(String p_83496_) {
        PlayerTeam $$1 = this.getPlayersTeam(p_83496_);
        if ($$1 != null) {
            this.removePlayerFromTeam(p_83496_, $$1);
            return true;
        } else {
            return false;
        }
    }

    public void removePlayerFromTeam(String p_83464_, PlayerTeam p_83465_) {
        if (this.getPlayersTeam(p_83464_) != p_83465_) {
            throw new IllegalStateException("Player is either on another team or not on any team. Cannot remove from team '" + p_83465_.getName() + "'.");
        } else {
            this.teamsByPlayer.remove(p_83464_);
            p_83465_.getPlayers().remove(p_83464_);
        }
    }

    public Collection<String> getTeamNames() {
        return this.teamsByName.keySet();
    }

    public Collection<PlayerTeam> getPlayerTeams() {
        return this.teamsByName.values();
    }

    @Nullable
    public PlayerTeam getPlayersTeam(String p_83501_) {
        return (PlayerTeam)this.teamsByPlayer.get(p_83501_);
    }

    public void onObjectiveAdded(Objective p_83422_) {
    }

    public void onObjectiveChanged(Objective p_83455_) {
    }

    public void onObjectiveRemoved(Objective p_83467_) {
    }

    public void onScoreChanged(Score p_83424_) {
    }

    public void onPlayerRemoved(String p_83431_) {
    }

    public void onPlayerScoreRemoved(String p_83432_, Objective p_83433_) {
    }

    public void onTeamAdded(PlayerTeam p_83423_) {
    }

    public void onTeamChanged(PlayerTeam p_83456_) {
    }

    public void onTeamRemoved(PlayerTeam p_83468_) {
    }

    public static String getDisplaySlotName(int p_83454_) {
        switch (p_83454_) {
            case 0:
                return "list";
            case 1:
                return "sidebar";
            case 2:
                return "belowName";
            default:
                if (p_83454_ >= 3 && p_83454_ <= 18) {
                    ChatFormatting $$1 = ChatFormatting.getById(p_83454_ - 3);
                    if ($$1 != null && $$1 != ChatFormatting.RESET) {
                        return "sidebar.team." + $$1.getName();
                    }
                }

                return null;
        }
    }

    public static int getDisplaySlotByName(String p_83505_) {
        if ("list".equalsIgnoreCase(p_83505_)) {
            return 0;
        } else if ("sidebar".equalsIgnoreCase(p_83505_)) {
            return 1;
        } else if ("belowName".equalsIgnoreCase(p_83505_)) {
            return 2;
        } else {
            if (p_83505_.startsWith("sidebar.team.")) {
                String $$1 = p_83505_.substring("sidebar.team.".length());
                ChatFormatting $$2 = ChatFormatting.getByName($$1);
                if ($$2 != null && $$2.getId() >= 0) {
                    return $$2.getId() + 3;
                }
            }

            return -1;
        }
    }

    public static String[] getDisplaySlotNames() {
        if (displaySlotNames == null) {
            displaySlotNames = new String[19];

            for(int $$0 = 0; $$0 < 19; ++$$0) {
                displaySlotNames[$$0] = getDisplaySlotName($$0);
            }
        }

        return displaySlotNames;
    }

    public void entityRemoved(Entity p_83421_) {
        if (p_83421_ != null && !(p_83421_ instanceof Player) && !p_83421_.isAlive()) {
            String $$1 = p_83421_.getStringUUID();
            this.resetPlayerScore($$1, (Objective)null);
            this.removePlayerFromTeam($$1);
        }
    }

    protected ListTag savePlayerScores() {
        ListTag $$0 = new ListTag();
        this.playerScores.values().stream().map(Map::values).forEach((p_83452_) -> {
            p_83452_.stream().filter((p_166098_) -> {
                return p_166098_.getObjective() != null;
            }).forEach((p_166096_) -> {
                CompoundTag $$2 = new CompoundTag();
                $$2.putString("Name", p_166096_.getOwner());
                $$2.putString("Objective", p_166096_.getObjective().getName());
                $$2.putInt("Score", p_166096_.getScore());
                $$2.putBoolean("Locked", p_166096_.isLocked());
                $$0.add($$2);
            });
        });
        return $$0;
    }

    protected void loadPlayerScores(ListTag p_83446_) {
        for(int $$1 = 0; $$1 < p_83446_.size(); ++$$1) {
            CompoundTag $$2 = p_83446_.getCompound($$1);
            Objective $$3 = this.getOrCreateObjective($$2.getString("Objective"));
            String $$4 = $$2.getString("Name");
            Score $$5 = this.getOrCreatePlayerScore($$4, $$3);
            $$5.setScore($$2.getInt("Score"));
            if ($$2.contains("Locked")) {
                $$5.setLocked($$2.getBoolean("Locked"));
            }
        }

    }
}

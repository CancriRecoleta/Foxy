//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket.Action;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.ScoreboardSaveData;

public class ServerScoreboard extends Scoreboard {
    private final MinecraftServer server;
    private final Set<Objective> trackedObjectives = Sets.newHashSet();
    private final List<Runnable> dirtyListeners = Lists.newArrayList();

    public ServerScoreboard(MinecraftServer p_136197_) {
        this.server = p_136197_;
    }

    public void onScoreChanged(Score p_136206_) {
        super.onScoreChanged(p_136206_);
        if (this.trackedObjectives.contains(p_136206_.getObjective())) {
            this.server.getPlayerList().broadcastAll(new ClientboundSetScorePacket(net.minecraft.server.ServerScoreboard.Method.CHANGE, p_136206_.getObjective().getName(), p_136206_.getOwner(), p_136206_.getScore()));
        }

        this.setDirty();
    }

    public void onPlayerRemoved(String p_136210_) {
        super.onPlayerRemoved(p_136210_);
        this.server.getPlayerList().broadcastAll(new ClientboundSetScorePacket(net.minecraft.server.ServerScoreboard.Method.REMOVE, (String)null, p_136210_, 0));
        this.setDirty();
    }

    public void onPlayerScoreRemoved(String p_136212_, Objective p_136213_) {
        super.onPlayerScoreRemoved(p_136212_, p_136213_);
        if (this.trackedObjectives.contains(p_136213_)) {
            this.server.getPlayerList().broadcastAll(new ClientboundSetScorePacket(net.minecraft.server.ServerScoreboard.Method.REMOVE, p_136213_.getName(), p_136212_, 0));
        }

        this.setDirty();
    }

    public void setDisplayObjective(int p_136199_, @Nullable Objective p_136200_) {
        Objective $$2 = this.getDisplayObjective(p_136199_);
        super.setDisplayObjective(p_136199_, p_136200_);
        if ($$2 != p_136200_ && $$2 != null) {
            if (this.getObjectiveDisplaySlotCount($$2) > 0) {
                this.server.getPlayerList().broadcastAll(new ClientboundSetDisplayObjectivePacket(p_136199_, p_136200_));
            } else {
                this.stopTrackingObjective($$2);
            }
        }

        if (p_136200_ != null) {
            if (this.trackedObjectives.contains(p_136200_)) {
                this.server.getPlayerList().broadcastAll(new ClientboundSetDisplayObjectivePacket(p_136199_, p_136200_));
            } else {
                this.startTrackingObjective(p_136200_);
            }
        }

        this.setDirty();
    }

    public boolean addPlayerToTeam(String p_136215_, PlayerTeam p_136216_) {
        if (super.addPlayerToTeam(p_136215_, p_136216_)) {
            this.server.getPlayerList().broadcastAll(ClientboundSetPlayerTeamPacket.createPlayerPacket(p_136216_, p_136215_, Action.ADD));
            this.setDirty();
            return true;
        } else {
            return false;
        }
    }

    public void removePlayerFromTeam(String p_136223_, PlayerTeam p_136224_) {
        super.removePlayerFromTeam(p_136223_, p_136224_);
        this.server.getPlayerList().broadcastAll(ClientboundSetPlayerTeamPacket.createPlayerPacket(p_136224_, p_136223_, Action.REMOVE));
        this.setDirty();
    }

    public void onObjectiveAdded(Objective p_136202_) {
        super.onObjectiveAdded(p_136202_);
        this.setDirty();
    }

    public void onObjectiveChanged(Objective p_136219_) {
        super.onObjectiveChanged(p_136219_);
        if (this.trackedObjectives.contains(p_136219_)) {
            this.server.getPlayerList().broadcastAll(new ClientboundSetObjectivePacket(p_136219_, 2));
        }

        this.setDirty();
    }

    public void onObjectiveRemoved(Objective p_136226_) {
        super.onObjectiveRemoved(p_136226_);
        if (this.trackedObjectives.contains(p_136226_)) {
            this.stopTrackingObjective(p_136226_);
        }

        this.setDirty();
    }

    public void onTeamAdded(PlayerTeam p_136204_) {
        super.onTeamAdded(p_136204_);
        this.server.getPlayerList().broadcastAll(ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(p_136204_, true));
        this.setDirty();
    }

    public void onTeamChanged(PlayerTeam p_136221_) {
        super.onTeamChanged(p_136221_);
        this.server.getPlayerList().broadcastAll(ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(p_136221_, false));
        this.setDirty();
    }

    public void onTeamRemoved(PlayerTeam p_136228_) {
        super.onTeamRemoved(p_136228_);
        this.server.getPlayerList().broadcastAll(ClientboundSetPlayerTeamPacket.createRemovePacket(p_136228_));
        this.setDirty();
    }

    public void addDirtyListener(Runnable p_136208_) {
        this.dirtyListeners.add(p_136208_);
    }

    protected void setDirty() {
        Iterator var1 = this.dirtyListeners.iterator();

        while(var1.hasNext()) {
            Runnable $$0 = (Runnable)var1.next();
            $$0.run();
        }

    }

    public List<Packet<?>> getStartTrackingPackets(Objective p_136230_) {
        List<Packet<?>> $$1 = Lists.newArrayList();
        $$1.add(new ClientboundSetObjectivePacket(p_136230_, 0));

        for(int $$2 = 0; $$2 < 19; ++$$2) {
            if (this.getDisplayObjective($$2) == p_136230_) {
                $$1.add(new ClientboundSetDisplayObjectivePacket($$2, p_136230_));
            }
        }

        Iterator var5 = this.getPlayerScores(p_136230_).iterator();

        while(var5.hasNext()) {
            Score $$3 = (Score)var5.next();
            $$1.add(new ClientboundSetScorePacket(net.minecraft.server.ServerScoreboard.Method.CHANGE, $$3.getObjective().getName(), $$3.getOwner(), $$3.getScore()));
        }

        return $$1;
    }

    public void startTrackingObjective(Objective p_136232_) {
        List<Packet<?>> $$1 = this.getStartTrackingPackets(p_136232_);
        Iterator var3 = this.server.getPlayerList().getPlayers().iterator();

        while(var3.hasNext()) {
            ServerPlayer $$2 = (ServerPlayer)var3.next();
            Iterator var5 = $$1.iterator();

            while(var5.hasNext()) {
                Packet<?> $$3 = (Packet)var5.next();
                $$2.connection.send($$3);
            }
        }

        this.trackedObjectives.add(p_136232_);
    }

    public List<Packet<?>> getStopTrackingPackets(Objective p_136234_) {
        List<Packet<?>> $$1 = Lists.newArrayList();
        $$1.add(new ClientboundSetObjectivePacket(p_136234_, 1));

        for(int $$2 = 0; $$2 < 19; ++$$2) {
            if (this.getDisplayObjective($$2) == p_136234_) {
                $$1.add(new ClientboundSetDisplayObjectivePacket($$2, p_136234_));
            }
        }

        return $$1;
    }

    public void stopTrackingObjective(Objective p_136236_) {
        List<Packet<?>> $$1 = this.getStopTrackingPackets(p_136236_);
        Iterator var3 = this.server.getPlayerList().getPlayers().iterator();

        while(var3.hasNext()) {
            ServerPlayer $$2 = (ServerPlayer)var3.next();
            Iterator var5 = $$1.iterator();

            while(var5.hasNext()) {
                Packet<?> $$3 = (Packet)var5.next();
                $$2.connection.send($$3);
            }
        }

        this.trackedObjectives.remove(p_136236_);
    }

    public int getObjectiveDisplaySlotCount(Objective p_136238_) {
        int $$1 = 0;

        for(int $$2 = 0; $$2 < 19; ++$$2) {
            if (this.getDisplayObjective($$2) == p_136238_) {
                ++$$1;
            }
        }

        return $$1;
    }

    public ScoreboardSaveData createData() {
        ScoreboardSaveData $$0 = new ScoreboardSaveData(this);
        Objects.requireNonNull($$0);
        this.addDirtyListener($$0::setDirty);
        return $$0;
    }

    public ScoreboardSaveData createData(CompoundTag p_180014_) {
        return this.createData().load(p_180014_);
    }

    public static enum Method {
        CHANGE,
        REMOVE;

        private Method() {
        }
    }
}

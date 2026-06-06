//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.players;

import java.util.Iterator;
import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class SleepStatus {
    private int activePlayers;
    private int sleepingPlayers;

    public SleepStatus() {
    }

    public boolean areEnoughSleeping(int p_144003_) {
        return this.sleepingPlayers >= this.sleepersNeeded(p_144003_);
    }

    public boolean areEnoughDeepSleeping(int p_144005_, List<ServerPlayer> p_144006_) {
        int $$2 = (int)p_144006_.stream().filter(Player::isSleepingLongEnough).count();
        return $$2 >= this.sleepersNeeded(p_144005_);
    }

    public int sleepersNeeded(int p_144011_) {
        return Math.max(1, Mth.ceil((float)(this.activePlayers * p_144011_) / 100.0F));
    }

    public void removeAllSleepers() {
        this.sleepingPlayers = 0;
    }

    public int amountSleeping() {
        return this.sleepingPlayers;
    }

    public boolean update(List<ServerPlayer> p_144008_) {
        int $$1 = this.activePlayers;
        int $$2 = this.sleepingPlayers;
        this.activePlayers = 0;
        this.sleepingPlayers = 0;
        Iterator var4 = p_144008_.iterator();

        while(var4.hasNext()) {
            ServerPlayer $$3 = (ServerPlayer)var4.next();
            if (!$$3.isSpectator()) {
                ++this.activePlayers;
                if ($$3.isSleeping()) {
                    ++this.sleepingPlayers;
                }
            }
        }

        return ($$2 > 0 || this.sleepingPlayers > 0) && ($$1 != this.activePlayers || $$2 != this.sleepingPlayers);
    }
}

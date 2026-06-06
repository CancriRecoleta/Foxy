//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.event;

import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.Nullable;

public class OnDatapackSyncEvent extends Event {
    private final PlayerList playerList;
    private final @Nullable ServerPlayer player;

    public OnDatapackSyncEvent(PlayerList playerList, @Nullable ServerPlayer player) {
        this.playerList = playerList;
        this.player = player;
    }

    public PlayerList getPlayerList() {
        return this.playerList;
    }

    public @Nullable ServerPlayer getPlayer() {
        return this.player;
    }

    public List<ServerPlayer> getPlayers() {
        return this.player == null ? this.playerList.getPlayers() : List.of(this.player);
    }
}

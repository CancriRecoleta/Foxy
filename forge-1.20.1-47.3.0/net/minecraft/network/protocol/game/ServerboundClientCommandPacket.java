//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundClientCommandPacket implements Packet<ServerGamePacketListener> {
    private final Action action;

    public ServerboundClientCommandPacket(Action p_133843_) {
        this.action = p_133843_;
    }

    public ServerboundClientCommandPacket(FriendlyByteBuf p_179547_) {
        this.action = (Action)p_179547_.readEnum(Action.class);
    }

    public void write(FriendlyByteBuf p_133852_) {
        p_133852_.writeEnum(this.action);
    }

    public void handle(ServerGamePacketListener p_133849_) {
        p_133849_.handleClientCommand(this);
    }

    public Action getAction() {
        return this.action;
    }

    public static enum Action {
        PERFORM_RESPAWN,
        REQUEST_STATS;

        private Action() {
        }
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public record ServerboundChatAckPacket(int offset) implements Packet<ServerGamePacketListener> {
    public ServerboundChatAckPacket(FriendlyByteBuf p_242339_) {
        this(p_242339_.readVarInt());
    }

    public ServerboundChatAckPacket(int offset) {
        this.offset = offset;
    }

    public void write(FriendlyByteBuf p_242345_) {
        p_242345_.writeVarInt(this.offset);
    }

    public void handle(ServerGamePacketListener p_242391_) {
        p_242391_.handleChatAck(this);
    }

    public int offset() {
        return this.offset;
    }
}

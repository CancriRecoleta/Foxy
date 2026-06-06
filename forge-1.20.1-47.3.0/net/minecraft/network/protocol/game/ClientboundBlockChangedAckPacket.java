//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public record ClientboundBlockChangedAckPacket(int sequence) implements Packet<ClientGamePacketListener> {
    public ClientboundBlockChangedAckPacket(FriendlyByteBuf p_237582_) {
        this(p_237582_.readVarInt());
    }

    public ClientboundBlockChangedAckPacket(int sequence) {
        this.sequence = sequence;
    }

    public void write(FriendlyByteBuf p_237584_) {
        p_237584_.writeVarInt(this.sequence);
    }

    public void handle(ClientGamePacketListener p_237588_) {
        p_237588_.handleBlockChangedAck(this);
    }

    public int sequence() {
        return this.sequence;
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.MessageSignature.Packed;
import net.minecraft.network.protocol.Packet;

public record ClientboundDeleteChatPacket(MessageSignature.Packed messageSignature) implements Packet<ClientGamePacketListener> {
    public ClientboundDeleteChatPacket(FriendlyByteBuf p_241415_) {
        this(Packed.read(p_241415_));
    }

    public ClientboundDeleteChatPacket(MessageSignature.Packed messageSignature) {
        this.messageSignature = messageSignature;
    }

    public void write(FriendlyByteBuf p_241358_) {
        Packed.write(p_241358_, this.messageSignature);
    }

    public void handle(ClientGamePacketListener p_241426_) {
        p_241426_.handleDeleteChat(this);
    }

    public MessageSignature.Packed messageSignature() {
        return this.messageSignature;
    }
}

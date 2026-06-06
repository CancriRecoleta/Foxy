//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

public record ClientboundDisguisedChatPacket(Component message, ChatType.BoundNetwork chatType) implements Packet<ClientGamePacketListener> {
    public ClientboundDisguisedChatPacket(FriendlyByteBuf p_249018_) {
        this(p_249018_.readComponent(), new ChatType.BoundNetwork(p_249018_));
    }

    public ClientboundDisguisedChatPacket(Component message, ChatType.BoundNetwork chatType) {
        this.message = message;
        this.chatType = chatType;
    }

    public void write(FriendlyByteBuf p_250975_) {
        p_250975_.writeComponent(this.message);
        this.chatType.write(p_250975_);
    }

    public void handle(ClientGamePacketListener p_251953_) {
        p_251953_.handleDisguisedChat(this);
    }

    public boolean isSkippable() {
        return true;
    }

    public Component message() {
        return this.message;
    }

    public ChatType.BoundNetwork chatType() {
        return this.chatType;
    }
}

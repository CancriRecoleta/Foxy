//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.chat.RemoteChatSession.Data;
import net.minecraft.network.protocol.Packet;

public record ServerboundChatSessionUpdatePacket(RemoteChatSession.Data chatSession) implements Packet<ServerGamePacketListener> {
    public ServerboundChatSessionUpdatePacket(FriendlyByteBuf p_254010_) {
        this(Data.read(p_254010_));
    }

    public ServerboundChatSessionUpdatePacket(RemoteChatSession.Data chatSession) {
        this.chatSession = chatSession;
    }

    public void write(FriendlyByteBuf p_253690_) {
        Data.write(p_253690_, this.chatSession);
    }

    public void handle(ServerGamePacketListener p_253620_) {
        p_253620_.handleChatSessionUpdate(this);
    }

    public RemoteChatSession.Data chatSession() {
        return this.chatSession;
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network.protocol.game;

import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public record ClientboundCustomChatCompletionsPacket(Action action, List<String> entries) implements Packet<ClientGamePacketListener> {
    public ClientboundCustomChatCompletionsPacket(FriendlyByteBuf p_243340_) {
        this((Action)p_243340_.readEnum(Action.class), p_243340_.readList(FriendlyByteBuf::readUtf));
    }

    public ClientboundCustomChatCompletionsPacket(Action action, List<String> entries) {
        this.action = action;
        this.entries = entries;
    }

    public void write(FriendlyByteBuf p_240782_) {
        p_240782_.writeEnum(this.action);
        p_240782_.writeCollection(this.entries, FriendlyByteBuf::writeUtf);
    }

    public void handle(ClientGamePacketListener p_240794_) {
        p_240794_.handleCustomChatCompletions(this);
    }

    public Action action() {
        return this.action;
    }

    public List<String> entries() {
        return this.entries;
    }

    public static enum Action {
        ADD,
        REMOVE,
        SET;

        private Action() {
        }
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network.protocol.game;

import java.time.Instant;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.protocol.Packet;

public record ServerboundChatPacket(String message, Instant timeStamp, long salt, @Nullable MessageSignature signature, LastSeenMessages.Update lastSeenMessages) implements Packet<ServerGamePacketListener> {
    public ServerboundChatPacket(FriendlyByteBuf p_179545_) {
        this(p_179545_.readUtf(256), p_179545_.readInstant(), p_179545_.readLong(), (MessageSignature)p_179545_.readNullable(MessageSignature::read), new LastSeenMessages.Update(p_179545_));
    }

    public ServerboundChatPacket(String message, Instant timeStamp, long salt, @Nullable MessageSignature signature, LastSeenMessages.Update lastSeenMessages) {
        this.message = message;
        this.timeStamp = timeStamp;
        this.salt = salt;
        this.signature = signature;
        this.lastSeenMessages = lastSeenMessages;
    }

    public void write(FriendlyByteBuf p_133839_) {
        p_133839_.writeUtf(this.message, 256);
        p_133839_.writeInstant(this.timeStamp);
        p_133839_.writeLong(this.salt);
        p_133839_.writeNullable(this.signature, MessageSignature::write);
        this.lastSeenMessages.write(p_133839_);
    }

    public void handle(ServerGamePacketListener p_133836_) {
        p_133836_.handleChat(this);
    }

    public String message() {
        return this.message;
    }

    public Instant timeStamp() {
        return this.timeStamp;
    }

    public long salt() {
        return this.salt;
    }

    @Nullable
    public MessageSignature signature() {
        return this.signature;
    }

    public LastSeenMessages.Update lastSeenMessages() {
        return this.lastSeenMessages;
    }
}

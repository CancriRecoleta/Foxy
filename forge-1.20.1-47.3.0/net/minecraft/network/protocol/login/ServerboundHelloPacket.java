//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network.protocol.login;

import java.util.Optional;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public record ServerboundHelloPacket(String name, Optional<UUID> profileId) implements Packet<ServerLoginPacketListener> {
    public ServerboundHelloPacket(FriendlyByteBuf p_179827_) {
        this(p_179827_.readUtf(16), p_179827_.readOptional(FriendlyByteBuf::readUUID));
    }

    public ServerboundHelloPacket(String name, Optional<UUID> profileId) {
        this.name = name;
        this.profileId = profileId;
    }

    public void write(FriendlyByteBuf p_134851_) {
        p_134851_.writeUtf(this.name, 16);
        p_134851_.writeOptional(this.profileId, FriendlyByteBuf::writeUUID);
    }

    public void handle(ServerLoginPacketListener p_134848_) {
        p_134848_.handleHello(this);
    }

    public String name() {
        return this.name;
    }

    public Optional<UUID> profileId() {
        return this.profileId;
    }
}

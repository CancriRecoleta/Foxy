//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network.protocol.status;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import org.jetbrains.annotations.Nullable;

public record ClientboundStatusResponsePacket(ServerStatus status, @Nullable String cachedStatus) implements Packet<ClientStatusPacketListener> {
    public ClientboundStatusResponsePacket(ServerStatus status) {
        this(status, (String)null);
    }

    public ClientboundStatusResponsePacket(FriendlyByteBuf p_179834_) {
        this((ServerStatus)p_179834_.readJsonWithCodec(ServerStatus.CODEC));
    }

    public ClientboundStatusResponsePacket(ServerStatus status, @Nullable String cachedStatus) {
        this.status = status;
        this.cachedStatus = cachedStatus;
    }

    public void write(FriendlyByteBuf p_134899_) {
        if (this.cachedStatus != null) {
            p_134899_.writeUtf(this.cachedStatus);
        } else {
            p_134899_.writeJsonWithCodec(ServerStatus.CODEC, this.status);
        }

    }

    public void handle(ClientStatusPacketListener p_134896_) {
        p_134896_.handleStatusResponse(this);
    }

    public ServerStatus status() {
        return this.status;
    }

    public @Nullable String cachedStatus() {
        return this.cachedStatus;
    }
}

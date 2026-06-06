//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.LivingEntity;

public record ClientboundHurtAnimationPacket(int id, float yaw) implements Packet<ClientGamePacketListener> {
    public ClientboundHurtAnimationPacket(LivingEntity p_265293_) {
        this(p_265293_.getId(), p_265293_.getHurtDir());
    }

    public ClientboundHurtAnimationPacket(FriendlyByteBuf p_265181_) {
        this(p_265181_.readVarInt(), p_265181_.readFloat());
    }

    public ClientboundHurtAnimationPacket(int id, float yaw) {
        this.id = id;
        this.yaw = yaw;
    }

    public void write(FriendlyByteBuf p_265156_) {
        p_265156_.writeVarInt(this.id);
        p_265156_.writeFloat(this.yaw);
    }

    public void handle(ClientGamePacketListener p_265654_) {
        p_265654_.handleHurtAnimation(this);
    }

    public int id() {
        return this.id;
    }

    public float yaw() {
        return this.yaw;
    }
}

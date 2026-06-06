//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public record ClientboundSetSimulationDistancePacket(int simulationDistance) implements Packet<ClientGamePacketListener> {
    public ClientboundSetSimulationDistancePacket(FriendlyByteBuf p_195800_) {
        this(p_195800_.readVarInt());
    }

    public ClientboundSetSimulationDistancePacket(int simulationDistance) {
        this.simulationDistance = simulationDistance;
    }

    public void write(FriendlyByteBuf p_195802_) {
        p_195802_.writeVarInt(this.simulationDistance);
    }

    public void handle(ClientGamePacketListener p_195806_) {
        p_195806_.handleSetSimulationDistance(this);
    }

    public int simulationDistance() {
        return this.simulationDistance;
    }
}

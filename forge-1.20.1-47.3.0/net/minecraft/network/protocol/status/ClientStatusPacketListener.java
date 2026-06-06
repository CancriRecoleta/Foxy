//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network.protocol.status;

import net.minecraft.network.PacketListener;

public interface ClientStatusPacketListener extends PacketListener {
    void handleStatusResponse(ClientboundStatusResponsePacket var1);

    void handlePongResponse(ClientboundPongResponsePacket var1);
}

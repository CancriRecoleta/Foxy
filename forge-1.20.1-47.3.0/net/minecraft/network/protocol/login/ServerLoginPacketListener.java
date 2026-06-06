//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network.protocol.login;

import net.minecraft.network.protocol.game.ServerPacketListener;

public interface ServerLoginPacketListener extends ServerPacketListener {
    void handleHello(ServerboundHelloPacket var1);

    void handleKey(ServerboundKeyPacket var1);

    void handleCustomQueryPacket(ServerboundCustomQueryPacket var1);
}

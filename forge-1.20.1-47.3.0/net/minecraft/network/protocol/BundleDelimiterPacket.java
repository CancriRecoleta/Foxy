//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network.protocol;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;

public class BundleDelimiterPacket<T extends PacketListener> implements Packet<T> {
    public BundleDelimiterPacket() {
    }

    public final void write(FriendlyByteBuf p_265437_) {
    }

    public final void handle(T p_265392_) {
        throw new AssertionError("This packet should be handled by pipeline");
    }
}

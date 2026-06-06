//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;
import java.util.Objects;
import net.minecraft.network.protocol.BundlerInfo;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;

public class PacketBundleUnpacker extends MessageToMessageEncoder<Packet<?>> {
    private final PacketFlow flow;

    public PacketBundleUnpacker(PacketFlow p_265529_) {
        this.flow = p_265529_;
    }

    protected void encode(ChannelHandlerContext p_265691_, Packet<?> p_265038_, List<Object> p_265735_) throws Exception {
        BundlerInfo.Provider $$3 = (BundlerInfo.Provider)p_265691_.channel().attr(BundlerInfo.BUNDLER_PROVIDER).get();
        if ($$3 == null) {
            throw new EncoderException("Bundler not configured: " + p_265038_);
        } else {
            BundlerInfo var10000 = $$3.getBundlerInfo(this.flow);
            Objects.requireNonNull(p_265735_);
            var10000.unbundlePacket(p_265038_, p_265735_::add);
        }
    }
}

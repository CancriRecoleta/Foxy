//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.protocol.BundlerInfo;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;

public class PacketBundlePacker extends MessageToMessageDecoder<Packet<?>> {
    @Nullable
    private BundlerInfo.Bundler currentBundler;
    @Nullable
    private BundlerInfo infoForCurrentBundler;
    private final PacketFlow flow;

    public PacketBundlePacker(PacketFlow p_265129_) {
        this.flow = p_265129_;
    }

    protected void decode(ChannelHandlerContext p_265208_, Packet<?> p_265182_, List<Object> p_265368_) throws Exception {
        BundlerInfo.Provider $$3 = (BundlerInfo.Provider)p_265208_.channel().attr(BundlerInfo.BUNDLER_PROVIDER).get();
        if ($$3 == null) {
            throw new DecoderException("Bundler not configured: " + p_265182_);
        } else {
            BundlerInfo $$4 = $$3.getBundlerInfo(this.flow);
            if (this.currentBundler != null) {
                if (this.infoForCurrentBundler != $$4) {
                    throw new DecoderException("Bundler handler changed during bundling");
                }

                Packet<?> $$5 = this.currentBundler.addPacket(p_265182_);
                if ($$5 != null) {
                    this.infoForCurrentBundler = null;
                    this.currentBundler = null;
                    p_265368_.add($$5);
                }
            } else {
                BundlerInfo.Bundler $$6 = $$4.startPacketBundling(p_265182_);
                if ($$6 != null) {
                    this.currentBundler = $$6;
                    this.infoForCurrentBundler = $$4;
                } else {
                    p_265368_.add(p_265182_);
                }
            }

        }
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network;

import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.io.IOException;
import java.util.List;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import org.slf4j.Logger;

public class PacketDecoder extends ByteToMessageDecoder {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final PacketFlow flow;

    public PacketDecoder(PacketFlow p_130533_) {
        this.flow = p_130533_;
    }

    protected void decode(ChannelHandlerContext p_130535_, ByteBuf p_130536_, List<Object> p_130537_) throws Exception {
        int $$3 = p_130536_.readableBytes();
        if ($$3 != 0) {
            FriendlyByteBuf $$4 = new FriendlyByteBuf(p_130536_);
            int $$5 = $$4.readVarInt();
            Packet<?> $$6 = ((ConnectionProtocol)p_130535_.channel().attr(Connection.ATTRIBUTE_PROTOCOL).get()).createPacket(this.flow, $$5, $$4);
            if ($$6 == null) {
                throw new IOException("Bad packet id " + $$5);
            } else {
                int $$7 = ((ConnectionProtocol)p_130535_.channel().attr(Connection.ATTRIBUTE_PROTOCOL).get()).getId();
                JvmProfiler.INSTANCE.onPacketReceived($$7, $$5, p_130535_.channel().remoteAddress(), $$3);
                if ($$4.readableBytes() > 0) {
                    int var10002 = ((ConnectionProtocol)p_130535_.channel().attr(Connection.ATTRIBUTE_PROTOCOL).get()).getId();
                    throw new IOException("Packet " + var10002 + "/" + $$5 + " (" + $$6.getClass().getSimpleName() + ") was larger than I expected, found " + $$4.readableBytes() + " bytes extra whilst reading packet " + $$5);
                } else {
                    p_130537_.add($$6);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(Connection.PACKET_RECEIVED_MARKER, " IN: [{}:{}] {}", new Object[]{p_130535_.channel().attr(Connection.ATTRIBUTE_PROTOCOL).get(), $$5, $$6.getClass().getName()});
                    }

                }
            }
        }
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network;

import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.io.IOException;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import org.slf4j.Logger;

public class PacketEncoder extends MessageToByteEncoder<Packet<?>> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final PacketFlow flow;

    public PacketEncoder(PacketFlow p_130543_) {
        this.flow = p_130543_;
    }

    protected void encode(ChannelHandlerContext p_130545_, Packet<?> p_130546_, ByteBuf p_130547_) throws Exception {
        ConnectionProtocol $$3 = (ConnectionProtocol)p_130545_.channel().attr(Connection.ATTRIBUTE_PROTOCOL).get();
        if ($$3 == null) {
            throw new RuntimeException("ConnectionProtocol unknown: " + p_130546_);
        } else {
            int $$4 = $$3.getPacketId(this.flow, p_130546_);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(Connection.PACKET_SENT_MARKER, "OUT: [{}:{}] {}", new Object[]{p_130545_.channel().attr(Connection.ATTRIBUTE_PROTOCOL).get(), $$4, p_130546_.getClass().getName()});
            }

            if ($$4 == -1) {
                throw new IOException("Can't serialize unregistered packet");
            } else {
                FriendlyByteBuf $$5 = new FriendlyByteBuf(p_130547_);
                $$5.writeVarInt($$4);

                try {
                    int $$6 = $$5.writerIndex();
                    p_130546_.write($$5);
                    int $$7 = $$5.writerIndex() - $$6;
                    if ($$7 > 8388608) {
                        throw new IllegalArgumentException("Packet too big (is " + $$7 + ", should be less than 8388608): " + p_130546_);
                    } else {
                        int $$8 = ((ConnectionProtocol)p_130545_.channel().attr(Connection.ATTRIBUTE_PROTOCOL).get()).getId();
                        JvmProfiler.INSTANCE.onPacketSent($$8, $$4, p_130545_.channel().remoteAddress(), $$7);
                    }
                } catch (Throwable var10) {
                    Throwable $$9 = var10;
                    LOGGER.error("Error receiving packet {}", $$4, $$9);
                    if (p_130546_.isSkippable()) {
                        throw new SkipPacketException($$9);
                    } else {
                        throw $$9;
                    }
                }
            }
        }
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import java.util.List;

public class Varint21FrameDecoder extends ByteToMessageDecoder {
    public Varint21FrameDecoder() {
    }

    protected void decode(ChannelHandlerContext p_130566_, ByteBuf p_130567_, List<Object> p_130568_) {
        p_130567_.markReaderIndex();
        byte[] $$3 = new byte[3];

        for(int $$4 = 0; $$4 < $$3.length; ++$$4) {
            if (!p_130567_.isReadable()) {
                p_130567_.resetReaderIndex();
                return;
            }

            $$3[$$4] = p_130567_.readByte();
            if ($$3[$$4] >= 0) {
                FriendlyByteBuf $$5 = new FriendlyByteBuf(Unpooled.wrappedBuffer($$3));

                try {
                    int $$6 = $$5.readVarInt();
                    if (p_130567_.readableBytes() < $$6) {
                        p_130567_.resetReaderIndex();
                        return;
                    }

                    p_130568_.add(p_130567_.readBytes($$6));
                } finally {
                    $$5.release();
                }

                return;
            }
        }

        throw new CorruptedFrameException("length wider than 21-bit");
    }
}

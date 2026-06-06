//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.MessageToByteEncoder;

@Sharable
public class Varint21LengthFieldPrepender extends MessageToByteEncoder<ByteBuf> {
    private static final int MAX_BYTES = 3;

    public Varint21LengthFieldPrepender() {
    }

    protected void encode(ChannelHandlerContext p_130571_, ByteBuf p_130572_, ByteBuf p_130573_) {
        int $$3 = p_130572_.readableBytes();
        int $$4 = FriendlyByteBuf.getVarIntSize($$3);
        if ($$4 > 3) {
            throw new IllegalArgumentException("unable to fit " + $$3 + " into 3");
        } else {
            FriendlyByteBuf $$5 = new FriendlyByteBuf(p_130573_);
            $$5.ensureWritable($$4 + $$3);
            $$5.writeVarInt($$3);
            $$5.writeBytes(p_130572_, p_130572_.readerIndex(), $$3);
        }
    }
}

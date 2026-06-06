//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import java.util.List;
import java.util.zip.Inflater;

public class CompressionDecoder extends ByteToMessageDecoder {
    public static final int MAXIMUM_COMPRESSED_LENGTH = 2097152;
    public static final int MAXIMUM_UNCOMPRESSED_LENGTH = 8388608;
    private final Inflater inflater;
    private int threshold;
    private boolean validateDecompressed;

    public CompressionDecoder(int p_182675_, boolean p_182676_) {
        this.threshold = p_182675_;
        this.validateDecompressed = p_182676_;
        this.inflater = new Inflater();
    }

    protected void decode(ChannelHandlerContext p_129441_, ByteBuf p_129442_, List<Object> p_129443_) throws Exception {
        if (p_129442_.readableBytes() != 0) {
            FriendlyByteBuf $$3 = new FriendlyByteBuf(p_129442_);
            int $$4 = $$3.readVarInt();
            if ($$4 == 0) {
                p_129443_.add($$3.readBytes($$3.readableBytes()));
            } else {
                if (this.validateDecompressed) {
                    if ($$4 < this.threshold) {
                        throw new DecoderException("Badly compressed packet - size of " + $$4 + " is below server threshold of " + this.threshold);
                    }

                    if ($$4 > 8388608) {
                        throw new DecoderException("Badly compressed packet - size of " + $$4 + " is larger than protocol maximum of 8388608");
                    }
                }

                byte[] $$5 = new byte[$$3.readableBytes()];
                $$3.readBytes($$5);
                this.inflater.setInput($$5);
                byte[] $$6 = new byte[$$4];
                this.inflater.inflate($$6);
                p_129443_.add(Unpooled.wrappedBuffer($$6));
                this.inflater.reset();
            }
        }
    }

    public void setThreshold(int p_182678_, boolean p_182679_) {
        this.threshold = p_182678_;
        this.validateDecompressed = p_182679_;
    }
}

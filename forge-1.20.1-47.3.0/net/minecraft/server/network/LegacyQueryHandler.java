//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.network;

import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;

public class LegacyQueryHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int FAKE_PROTOCOL_VERSION = 127;
    private final ServerConnectionListener serverConnectionListener;

    public LegacyQueryHandler(ServerConnectionListener p_9679_) {
        this.serverConnectionListener = p_9679_;
    }

    public void channelRead(ChannelHandlerContext p_9686_, Object p_9687_) {
        ByteBuf $$2 = (ByteBuf)p_9687_;
        $$2.markReaderIndex();
        boolean $$3 = true;

        try {
            try {
                if ($$2.readUnsignedByte() != 254) {
                    return;
                }

                InetSocketAddress $$4 = (InetSocketAddress)p_9686_.channel().remoteAddress();
                MinecraftServer $$5 = this.serverConnectionListener.getServer();
                int $$6 = $$2.readableBytes();
                String $$8;
                switch ($$6) {
                    case 0:
                        LOGGER.debug("Ping: (<1.3.x) from {}:{}", $$4.getAddress(), $$4.getPort());
                        $$8 = String.format(Locale.ROOT, "%s§%d§%d", $$5.getMotd(), $$5.getPlayerCount(), $$5.getMaxPlayers());
                        this.sendFlushAndClose(p_9686_, this.createReply($$8));
                        break;
                    case 1:
                        if ($$2.readUnsignedByte() != 1) {
                            return;
                        }

                        LOGGER.debug("Ping: (1.4-1.5.x) from {}:{}", $$4.getAddress(), $$4.getPort());
                        $$8 = String.format(Locale.ROOT, "§1\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d", 127, $$5.getServerVersion(), $$5.getMotd(), $$5.getPlayerCount(), $$5.getMaxPlayers());
                        this.sendFlushAndClose(p_9686_, this.createReply($$8));
                        break;
                    default:
                        boolean $$9 = $$2.readUnsignedByte() == 1;
                        $$9 &= $$2.readUnsignedByte() == 250;
                        $$9 &= "MC|PingHost".equals(new String($$2.readBytes($$2.readShort() * 2).array(), StandardCharsets.UTF_16BE));
                        int $$10 = $$2.readUnsignedShort();
                        $$9 &= $$2.readUnsignedByte() >= 73;
                        $$9 &= 3 + $$2.readBytes($$2.readShort() * 2).array().length + 4 == $$10;
                        $$9 &= $$2.readInt() <= 65535;
                        $$9 &= $$2.readableBytes() == 0;
                        if (!$$9) {
                            return;
                        }

                        LOGGER.debug("Ping: (1.6) from {}:{}", $$4.getAddress(), $$4.getPort());
                        String $$11 = String.format(Locale.ROOT, "§1\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d", 127, $$5.getServerVersion(), $$5.getMotd(), $$5.getPlayerCount(), $$5.getMaxPlayers());
                        ByteBuf $$12 = this.createReply($$11);

                        try {
                            this.sendFlushAndClose(p_9686_, $$12);
                        } finally {
                            $$12.release();
                        }
                }

                $$2.release();
                $$3 = false;
            } catch (RuntimeException var21) {
            }

        } finally {
            if ($$3) {
                $$2.resetReaderIndex();
                p_9686_.channel().pipeline().remove("legacy_query");
                p_9686_.fireChannelRead(p_9687_);
            }

        }
    }

    private void sendFlushAndClose(ChannelHandlerContext p_9681_, ByteBuf p_9682_) {
        p_9681_.pipeline().firstContext().writeAndFlush(p_9682_).addListener(ChannelFutureListener.CLOSE);
    }

    private ByteBuf createReply(String p_9684_) {
        ByteBuf $$1 = Unpooled.buffer();
        $$1.writeByte(255);
        char[] $$2 = p_9684_.toCharArray();
        $$1.writeShort($$2.length);
        char[] var4 = $$2;
        int var5 = $$2.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            char $$3 = var4[var6];
            $$1.writeChar($$3);
        }

        return $$1;
    }
}

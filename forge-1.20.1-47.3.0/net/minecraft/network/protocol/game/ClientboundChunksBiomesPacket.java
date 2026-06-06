//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;

public record ClientboundChunksBiomesPacket(List<ChunkBiomeData> chunkBiomeData) implements Packet<ClientGamePacketListener> {
    private static final int TWO_MEGABYTES = 2097152;

    public ClientboundChunksBiomesPacket(FriendlyByteBuf p_275221_) {
        this(p_275221_.readList(ChunkBiomeData::new));
    }

    public ClientboundChunksBiomesPacket(List<ChunkBiomeData> chunkBiomeData) {
        this.chunkBiomeData = chunkBiomeData;
    }

    public static ClientboundChunksBiomesPacket forChunks(List<LevelChunk> p_275394_) {
        return new ClientboundChunksBiomesPacket(p_275394_.stream().map(ChunkBiomeData::new).toList());
    }

    public void write(FriendlyByteBuf p_275376_) {
        p_275376_.writeCollection(this.chunkBiomeData, (p_275199_, p_275200_) -> {
            p_275200_.write(p_275199_);
        });
    }

    public void handle(ClientGamePacketListener p_275524_) {
        p_275524_.handleChunksBiomes(this);
    }

    public List<ChunkBiomeData> chunkBiomeData() {
        return this.chunkBiomeData;
    }

    public static record ChunkBiomeData(ChunkPos pos, byte[] buffer) {
        public ChunkBiomeData(LevelChunk p_275569_) {
            this(p_275569_.getPos(), new byte[calculateChunkSize(p_275569_)]);
            extractChunkData(new FriendlyByteBuf(this.getWriteBuffer()), p_275569_);
        }

        public ChunkBiomeData(FriendlyByteBuf p_275255_) {
            this(p_275255_.readChunkPos(), p_275255_.readByteArray(2097152));
        }

        public ChunkBiomeData(ChunkPos pos, byte[] buffer) {
            this.pos = pos;
            this.buffer = buffer;
        }

        private static int calculateChunkSize(LevelChunk p_275324_) {
            int $$1 = 0;
            LevelChunkSection[] var2 = p_275324_.getSections();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                LevelChunkSection $$2 = var2[var4];
                $$1 += $$2.getBiomes().getSerializedSize();
            }

            return $$1;
        }

        public FriendlyByteBuf getReadBuffer() {
            return new FriendlyByteBuf(Unpooled.wrappedBuffer(this.buffer));
        }

        private ByteBuf getWriteBuffer() {
            ByteBuf $$0 = Unpooled.wrappedBuffer(this.buffer);
            $$0.writerIndex(0);
            return $$0;
        }

        public static void extractChunkData(FriendlyByteBuf p_275626_, LevelChunk p_275570_) {
            LevelChunkSection[] var2 = p_275570_.getSections();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                LevelChunkSection $$2 = var2[var4];
                $$2.getBiomes().write(p_275626_);
            }

        }

        public void write(FriendlyByteBuf p_275467_) {
            p_275467_.writeChunkPos(this.pos);
            p_275467_.writeByteArray(this.buffer);
        }

        public ChunkPos pos() {
            return this.pos;
        }

        public byte[] buffer() {
            return this.buffer;
        }
    }
}

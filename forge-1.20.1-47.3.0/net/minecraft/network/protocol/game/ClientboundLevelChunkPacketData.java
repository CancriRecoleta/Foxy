//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;

public class ClientboundLevelChunkPacketData {
    private static final int TWO_MEGABYTES = 2097152;
    private final CompoundTag heightmaps;
    private final byte[] buffer;
    private final List<BlockEntityInfo> blockEntitiesData;

    public ClientboundLevelChunkPacketData(LevelChunk p_195651_) {
        this.heightmaps = new CompoundTag();
        Iterator var2 = p_195651_.getHeightmaps().iterator();

        Map.Entry $$2;
        while(var2.hasNext()) {
            $$2 = (Map.Entry)var2.next();
            if (((Heightmap.Types)$$2.getKey()).sendToClient()) {
                this.heightmaps.put(((Heightmap.Types)$$2.getKey()).getSerializationKey(), new LongArrayTag(((Heightmap)$$2.getValue()).getRawData()));
            }
        }

        this.buffer = new byte[calculateChunkSize(p_195651_)];
        extractChunkData(new FriendlyByteBuf(this.getWriteBuffer()), p_195651_);
        this.blockEntitiesData = Lists.newArrayList();
        var2 = p_195651_.getBlockEntities().entrySet().iterator();

        while(var2.hasNext()) {
            $$2 = (Map.Entry)var2.next();
            this.blockEntitiesData.add(net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData.BlockEntityInfo.create((BlockEntity)$$2.getValue()));
        }

    }

    public ClientboundLevelChunkPacketData(FriendlyByteBuf p_195653_, int p_195654_, int p_195655_) {
        this.heightmaps = p_195653_.readNbt();
        if (this.heightmaps == null) {
            throw new RuntimeException("Can't read heightmap in packet for [" + p_195654_ + ", " + p_195655_ + "]");
        } else {
            int $$3 = p_195653_.readVarInt();
            if ($$3 > 2097152) {
                throw new RuntimeException("Chunk Packet trying to allocate too much memory on read.");
            } else {
                this.buffer = new byte[$$3];
                p_195653_.readBytes(this.buffer);
                this.blockEntitiesData = p_195653_.readList(BlockEntityInfo::new);
            }
        }
    }

    public void write(FriendlyByteBuf p_195667_) {
        p_195667_.writeNbt(this.heightmaps);
        p_195667_.writeVarInt(this.buffer.length);
        p_195667_.writeBytes(this.buffer);
        p_195667_.writeCollection(this.blockEntitiesData, (p_195672_, p_195673_) -> {
            p_195673_.write(p_195672_);
        });
    }

    private static int calculateChunkSize(LevelChunk p_195665_) {
        int $$1 = 0;
        LevelChunkSection[] var2 = p_195665_.getSections();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            LevelChunkSection $$2 = var2[var4];
            $$1 += $$2.getSerializedSize();
        }

        return $$1;
    }

    private ByteBuf getWriteBuffer() {
        ByteBuf $$0 = Unpooled.wrappedBuffer(this.buffer);
        $$0.writerIndex(0);
        return $$0;
    }

    public static void extractChunkData(FriendlyByteBuf p_195669_, LevelChunk p_195670_) {
        LevelChunkSection[] var2 = p_195670_.getSections();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            LevelChunkSection $$2 = var2[var4];
            $$2.write(p_195669_);
        }

    }

    public Consumer<BlockEntityTagOutput> getBlockEntitiesTagsConsumer(int p_195658_, int p_195659_) {
        return (p_195663_) -> {
            this.getBlockEntitiesTags(p_195663_, p_195658_, p_195659_);
        };
    }

    private void getBlockEntitiesTags(BlockEntityTagOutput p_195675_, int p_195676_, int p_195677_) {
        int $$3 = 16 * p_195676_;
        int $$4 = 16 * p_195677_;
        BlockPos.MutableBlockPos $$5 = new BlockPos.MutableBlockPos();
        Iterator var7 = this.blockEntitiesData.iterator();

        while(var7.hasNext()) {
            BlockEntityInfo $$6 = (BlockEntityInfo)var7.next();
            int $$7 = $$3 + SectionPos.sectionRelative($$6.packedXZ >> 4);
            int $$8 = $$4 + SectionPos.sectionRelative($$6.packedXZ);
            $$5.set($$7, $$6.y, $$8);
            p_195675_.accept($$5, $$6.type, $$6.tag);
        }

    }

    public FriendlyByteBuf getReadBuffer() {
        return new FriendlyByteBuf(Unpooled.wrappedBuffer(this.buffer));
    }

    public CompoundTag getHeightmaps() {
        return this.heightmaps;
    }

    static class BlockEntityInfo {
        final int packedXZ;
        final int y;
        final BlockEntityType<?> type;
        @Nullable
        final CompoundTag tag;

        private BlockEntityInfo(int p_195685_, int p_195686_, BlockEntityType<?> p_195687_, @Nullable CompoundTag p_195688_) {
            this.packedXZ = p_195685_;
            this.y = p_195686_;
            this.type = p_195687_;
            this.tag = p_195688_;
        }

        private BlockEntityInfo(FriendlyByteBuf p_195690_) {
            this.packedXZ = p_195690_.readByte();
            this.y = p_195690_.readShort();
            this.type = (BlockEntityType)p_195690_.readById(BuiltInRegistries.BLOCK_ENTITY_TYPE);
            this.tag = p_195690_.readNbt();
        }

        void write(FriendlyByteBuf p_195694_) {
            p_195694_.writeByte(this.packedXZ);
            p_195694_.writeShort(this.y);
            p_195694_.writeId(BuiltInRegistries.BLOCK_ENTITY_TYPE, this.type);
            p_195694_.writeNbt(this.tag);
        }

        static BlockEntityInfo create(BlockEntity p_195692_) {
            CompoundTag $$1 = p_195692_.getUpdateTag();
            BlockPos $$2 = p_195692_.getBlockPos();
            int $$3 = SectionPos.sectionRelative($$2.getX()) << 4 | SectionPos.sectionRelative($$2.getZ());
            return new BlockEntityInfo($$3, $$2.getY(), p_195692_.getType(), $$1.isEmpty() ? null : $$1);
        }
    }

    @FunctionalInterface
    public interface BlockEntityTagOutput {
        void accept(BlockPos var1, BlockEntityType<?> var2, @Nullable CompoundTag var3);
    }
}

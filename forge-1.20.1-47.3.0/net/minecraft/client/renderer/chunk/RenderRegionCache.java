//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer.chunk;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderRegionCache {
    private final Long2ObjectMap<ChunkInfo> chunkInfoCache = new Long2ObjectOpenHashMap();

    public RenderRegionCache() {
    }

    @Nullable
    public RenderChunkRegion createRegion(Level p_200466_, BlockPos p_200467_, BlockPos p_200468_, int p_200469_) {
        int $$4 = SectionPos.blockToSectionCoord(p_200467_.getX() - p_200469_);
        int $$5 = SectionPos.blockToSectionCoord(p_200467_.getZ() - p_200469_);
        int $$6 = SectionPos.blockToSectionCoord(p_200468_.getX() + p_200469_);
        int $$7 = SectionPos.blockToSectionCoord(p_200468_.getZ() + p_200469_);
        ChunkInfo[][] $$8 = new ChunkInfo[$$6 - $$4 + 1][$$7 - $$5 + 1];

        int $$12;
        for(int $$9 = $$4; $$9 <= $$6; ++$$9) {
            for($$12 = $$5; $$12 <= $$7; ++$$12) {
                $$8[$$9 - $$4][$$12 - $$5] = (ChunkInfo)this.chunkInfoCache.computeIfAbsent(ChunkPos.asLong($$9, $$12), (p_200464_) -> {
                    return new ChunkInfo(p_200466_.getChunk(ChunkPos.getX(p_200464_), ChunkPos.getZ(p_200464_)));
                });
            }
        }

        if (isAllEmpty(p_200467_, p_200468_, $$4, $$5, $$8)) {
            return null;
        } else {
            RenderChunk[][] $$11 = new RenderChunk[$$6 - $$4 + 1][$$7 - $$5 + 1];

            for($$12 = $$4; $$12 <= $$6; ++$$12) {
                for(int $$13 = $$5; $$13 <= $$7; ++$$13) {
                    $$11[$$12 - $$4][$$13 - $$5] = $$8[$$12 - $$4][$$13 - $$5].renderChunk();
                }
            }

            return new RenderChunkRegion(p_200466_, $$4, $$5, $$11);
        }
    }

    private static boolean isAllEmpty(BlockPos p_200471_, BlockPos p_200472_, int p_200473_, int p_200474_, ChunkInfo[][] p_200475_) {
        int $$5 = SectionPos.blockToSectionCoord(p_200471_.getX());
        int $$6 = SectionPos.blockToSectionCoord(p_200471_.getZ());
        int $$7 = SectionPos.blockToSectionCoord(p_200472_.getX());
        int $$8 = SectionPos.blockToSectionCoord(p_200472_.getZ());

        for(int $$9 = $$5; $$9 <= $$7; ++$$9) {
            for(int $$10 = $$6; $$10 <= $$8; ++$$10) {
                LevelChunk $$11 = p_200475_[$$9 - p_200473_][$$10 - p_200474_].chunk();
                if (!$$11.isYSpaceEmpty(p_200471_.getY(), p_200472_.getY())) {
                    return false;
                }
            }
        }

        return true;
    }

    @OnlyIn(Dist.CLIENT)
    private static final class ChunkInfo {
        private final LevelChunk chunk;
        @Nullable
        private RenderChunk renderChunk;

        ChunkInfo(LevelChunk p_200479_) {
            this.chunk = p_200479_;
        }

        public LevelChunk chunk() {
            return this.chunk;
        }

        public RenderChunk renderChunk() {
            if (this.renderChunk == null) {
                this.renderChunk = new RenderChunk(this.chunk);
            }

            return this.renderChunk;
        }
    }
}

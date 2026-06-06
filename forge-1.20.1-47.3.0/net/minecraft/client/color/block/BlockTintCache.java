//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.color.block;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.ToIntFunction;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockTintCache {
    private static final int MAX_CACHE_ENTRIES = 256;
    private final ThreadLocal<LatestCacheInfo> latestChunkOnThread = ThreadLocal.withInitial(LatestCacheInfo::new);
    private final Long2ObjectLinkedOpenHashMap<CacheData> cache = new Long2ObjectLinkedOpenHashMap(256, 0.25F);
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ToIntFunction<BlockPos> source;

    public BlockTintCache(ToIntFunction<BlockPos> p_193811_) {
        this.source = p_193811_;
    }

    public int getColor(BlockPos p_193813_) {
        int $$1 = SectionPos.blockToSectionCoord(p_193813_.getX());
        int $$2 = SectionPos.blockToSectionCoord(p_193813_.getZ());
        LatestCacheInfo $$3 = (LatestCacheInfo)this.latestChunkOnThread.get();
        if ($$3.x != $$1 || $$3.z != $$2 || $$3.cache == null || $$3.cache.isInvalidated()) {
            $$3.x = $$1;
            $$3.z = $$2;
            $$3.cache = this.findOrCreateChunkCache($$1, $$2);
        }

        int[] $$4 = $$3.cache.getLayer(p_193813_.getY());
        int $$5 = p_193813_.getX() & 15;
        int $$6 = p_193813_.getZ() & 15;
        int $$7 = $$6 << 4 | $$5;
        int $$8 = $$4[$$7];
        if ($$8 != -1) {
            return $$8;
        } else {
            int $$9 = this.source.applyAsInt(p_193813_);
            $$4[$$7] = $$9;
            return $$9;
        }
    }

    public void invalidateForChunk(int p_92656_, int p_92657_) {
        try {
            this.lock.writeLock().lock();

            for(int $$2 = -1; $$2 <= 1; ++$$2) {
                for(int $$3 = -1; $$3 <= 1; ++$$3) {
                    long $$4 = ChunkPos.asLong(p_92656_ + $$2, p_92657_ + $$3);
                    CacheData $$5 = (CacheData)this.cache.remove($$4);
                    if ($$5 != null) {
                        $$5.invalidate();
                    }
                }
            }
        } finally {
            this.lock.writeLock().unlock();
        }

    }

    public void invalidateAll() {
        try {
            this.lock.writeLock().lock();
            this.cache.values().forEach(CacheData::invalidate);
            this.cache.clear();
        } finally {
            this.lock.writeLock().unlock();
        }

    }

    private CacheData findOrCreateChunkCache(int p_193815_, int p_193816_) {
        long $$2 = ChunkPos.asLong(p_193815_, p_193816_);
        this.lock.readLock().lock();

        CacheData $$4;
        CacheData $$5;
        label128: {
            try {
                $$4 = (CacheData)this.cache.get($$2);
                if ($$4 == null) {
                    break label128;
                }

                $$5 = $$4;
            } finally {
                this.lock.readLock().unlock();
            }

            return $$5;
        }

        this.lock.writeLock().lock();

        try {
            $$4 = (CacheData)this.cache.get($$2);
            if ($$4 == null) {
                $$5 = new CacheData();
                CacheData $$6;
                if (this.cache.size() >= 256) {
                    $$6 = (CacheData)this.cache.removeFirst();
                    if ($$6 != null) {
                        $$6.invalidate();
                    }
                }

                this.cache.put($$2, $$5);
                $$6 = $$5;
                return $$6;
            }

            $$5 = $$4;
        } finally {
            this.lock.writeLock().unlock();
        }

        return $$5;
    }

    @OnlyIn(Dist.CLIENT)
    static class LatestCacheInfo {
        public int x = Integer.MIN_VALUE;
        public int z = Integer.MIN_VALUE;
        @Nullable
        CacheData cache;

        private LatestCacheInfo() {
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static class CacheData {
        private final Int2ObjectArrayMap<int[]> cache = new Int2ObjectArrayMap(16);
        private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        private static final int BLOCKS_PER_LAYER = Mth.square(16);
        private volatile boolean invalidated;

        CacheData() {
        }

        public int[] getLayer(int p_193824_) {
            this.lock.readLock().lock();

            int[] $$1;
            try {
                $$1 = (int[])this.cache.get(p_193824_);
                if ($$1 != null) {
                    int[] var3 = $$1;
                    return var3;
                }
            } finally {
                this.lock.readLock().unlock();
            }

            this.lock.writeLock().lock();

            try {
                $$1 = (int[])this.cache.computeIfAbsent(p_193824_, (p_193826_) -> {
                    return this.allocateLayer();
                });
            } finally {
                this.lock.writeLock().unlock();
            }

            return $$1;
        }

        private int[] allocateLayer() {
            int[] $$0 = new int[BLOCKS_PER_LAYER];
            Arrays.fill($$0, -1);
            return $$0;
        }

        public boolean isInvalidated() {
            return this.invalidated;
        }

        public void invalidate() {
            this.invalidated = true;
        }
    }
}

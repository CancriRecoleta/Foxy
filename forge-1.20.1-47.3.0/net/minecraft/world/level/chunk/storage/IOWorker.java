//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.chunk.storage;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import java.io.IOException;
import java.nio.file.Path;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.visitors.CollectFields;
import net.minecraft.nbt.visitors.FieldSelector;
import net.minecraft.util.Unit;
import net.minecraft.util.thread.ProcessorMailbox;
import net.minecraft.util.thread.StrictQueue;
import net.minecraft.world.level.ChunkPos;
import org.slf4j.Logger;

public class IOWorker implements ChunkScanAccess, AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final AtomicBoolean shutdownRequested = new AtomicBoolean();
    private final ProcessorMailbox<StrictQueue.IntRunnable> mailbox;
    private final RegionFileStorage storage;
    private final Map<ChunkPos, PendingStore> pendingWrites = Maps.newLinkedHashMap();
    private final Long2ObjectLinkedOpenHashMap<CompletableFuture<BitSet>> regionCacheForBlender = new Long2ObjectLinkedOpenHashMap();
    private static final int REGION_CACHE_SIZE = 1024;

    protected IOWorker(Path p_196930_, boolean p_196931_, String p_196932_) {
        this.storage = new RegionFileStorage(p_196930_, p_196931_);
        this.mailbox = new ProcessorMailbox(new StrictQueue.FixedPriorityQueue(net.minecraft.world.level.chunk.storage.IOWorker.Priority.values().length), Util.ioPool(), "IOWorker-" + p_196932_);
    }

    public boolean isOldChunkAround(ChunkPos p_223472_, int p_223473_) {
        ChunkPos $$2 = new ChunkPos(p_223472_.x - p_223473_, p_223472_.z - p_223473_);
        ChunkPos $$3 = new ChunkPos(p_223472_.x + p_223473_, p_223472_.z + p_223473_);

        for(int $$4 = $$2.getRegionX(); $$4 <= $$3.getRegionX(); ++$$4) {
            for(int $$5 = $$2.getRegionZ(); $$5 <= $$3.getRegionZ(); ++$$5) {
                BitSet $$6 = (BitSet)this.getOrCreateOldDataForRegion($$4, $$5).join();
                if (!$$6.isEmpty()) {
                    ChunkPos $$7 = ChunkPos.minFromRegion($$4, $$5);
                    int $$8 = Math.max($$2.x - $$7.x, 0);
                    int $$9 = Math.max($$2.z - $$7.z, 0);
                    int $$10 = Math.min($$3.x - $$7.x, 31);
                    int $$11 = Math.min($$3.z - $$7.z, 31);

                    for(int $$12 = $$8; $$12 <= $$10; ++$$12) {
                        for(int $$13 = $$9; $$13 <= $$11; ++$$13) {
                            int $$14 = $$13 * 32 + $$12;
                            if ($$6.get($$14)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    private CompletableFuture<BitSet> getOrCreateOldDataForRegion(int p_223464_, int p_223465_) {
        long $$2 = ChunkPos.asLong(p_223464_, p_223465_);
        synchronized(this.regionCacheForBlender) {
            CompletableFuture<BitSet> $$3 = (CompletableFuture)this.regionCacheForBlender.getAndMoveToFirst($$2);
            if ($$3 == null) {
                $$3 = this.createOldDataForRegion(p_223464_, p_223465_);
                this.regionCacheForBlender.putAndMoveToFirst($$2, $$3);
                if (this.regionCacheForBlender.size() > 1024) {
                    this.regionCacheForBlender.removeLast();
                }
            }

            return $$3;
        }
    }

    private CompletableFuture<BitSet> createOldDataForRegion(int p_223490_, int p_223491_) {
        return CompletableFuture.supplyAsync(() -> {
            ChunkPos $$2 = ChunkPos.minFromRegion(p_223490_, p_223491_);
            ChunkPos $$3 = ChunkPos.maxFromRegion(p_223490_, p_223491_);
            BitSet $$4 = new BitSet();
            ChunkPos.rangeClosed($$2, $$3).forEach((p_223480_) -> {
                CollectFields $$2 = new CollectFields(new FieldSelector[]{new FieldSelector(IntTag.TYPE, "DataVersion"), new FieldSelector(CompoundTag.TYPE, "blending_data")});

                try {
                    this.scanChunk(p_223480_, $$2).join();
                } catch (Exception var7) {
                    Exception $$3 = var7;
                    LOGGER.warn("Failed to scan chunk {}", p_223480_, $$3);
                    return;
                }

                Tag $$4x = $$2.getResult();
                if ($$4x instanceof CompoundTag $$5) {
                    if (this.isOldChunk($$5)) {
                        int $$6 = p_223480_.getRegionLocalZ() * 32 + p_223480_.getRegionLocalX();
                        $$4.set($$6);
                    }
                }

            });
            return $$4;
        }, Util.backgroundExecutor());
    }

    private boolean isOldChunk(CompoundTag p_223485_) {
        return p_223485_.contains("DataVersion", 99) && p_223485_.getInt("DataVersion") >= 3441 ? p_223485_.contains("blending_data", 10) : true;
    }

    public CompletableFuture<Void> store(ChunkPos p_63539_, @Nullable CompoundTag p_63540_) {
        return this.submitTask(() -> {
            PendingStore $$2 = (PendingStore)this.pendingWrites.computeIfAbsent(p_63539_, (p_223488_) -> {
                return new PendingStore(p_63540_);
            });
            $$2.data = p_63540_;
            return Either.left($$2.result);
        }).thenCompose(Function.identity());
    }

    public CompletableFuture<Optional<CompoundTag>> loadAsync(ChunkPos p_156588_) {
        return this.submitTask(() -> {
            PendingStore $$1 = (PendingStore)this.pendingWrites.get(p_156588_);
            if ($$1 != null) {
                return Either.left(Optional.ofNullable($$1.data));
            } else {
                try {
                    CompoundTag $$2 = this.storage.read(p_156588_);
                    return Either.left(Optional.ofNullable($$2));
                } catch (Exception var4) {
                    Exception $$3 = var4;
                    LOGGER.warn("Failed to read chunk {}", p_156588_, $$3);
                    return Either.right($$3);
                }
            }
        });
    }

    public CompletableFuture<Void> synchronize(boolean p_182499_) {
        CompletableFuture<Void> $$1 = this.submitTask(() -> {
            return Either.left(CompletableFuture.allOf((CompletableFuture[])this.pendingWrites.values().stream().map((p_223475_) -> {
                return p_223475_.result;
            }).toArray((p_223462_) -> {
                return new CompletableFuture[p_223462_];
            })));
        }).thenCompose(Function.identity());
        return p_182499_ ? $$1.thenCompose((p_182494_) -> {
            return this.submitTask(() -> {
                try {
                    this.storage.flush();
                    return Either.left((Object)null);
                } catch (Exception var2) {
                    Exception $$0 = var2;
                    LOGGER.warn("Failed to synchronize chunks", $$0);
                    return Either.right($$0);
                }
            });
        }) : $$1.thenCompose((p_223477_) -> {
            return this.submitTask(() -> {
                return Either.left((Object)null);
            });
        });
    }

    public CompletableFuture<Void> scanChunk(ChunkPos p_196939_, StreamTagVisitor p_196940_) {
        return this.submitTask(() -> {
            try {
                PendingStore $$2 = (PendingStore)this.pendingWrites.get(p_196939_);
                if ($$2 != null) {
                    if ($$2.data != null) {
                        $$2.data.acceptAsRoot(p_196940_);
                    }
                } else {
                    this.storage.scanChunk(p_196939_, p_196940_);
                }

                return Either.left((Object)null);
            } catch (Exception var4) {
                Exception $$3 = var4;
                LOGGER.warn("Failed to bulk scan chunk {}", p_196939_, $$3);
                return Either.right($$3);
            }
        });
    }

    private <T> CompletableFuture<T> submitTask(Supplier<Either<T, Exception>> p_63546_) {
        return this.mailbox.askEither((p_223483_) -> {
            return new StrictQueue.IntRunnable(net.minecraft.world.level.chunk.storage.IOWorker.Priority.FOREGROUND.ordinal(), () -> {
                if (!this.shutdownRequested.get()) {
                    p_223483_.tell((Either)p_63546_.get());
                }

                this.tellStorePending();
            });
        });
    }

    private void storePendingChunk() {
        if (!this.pendingWrites.isEmpty()) {
            Iterator<Map.Entry<ChunkPos, PendingStore>> $$0 = this.pendingWrites.entrySet().iterator();
            Map.Entry<ChunkPos, PendingStore> $$1 = (Map.Entry)$$0.next();
            $$0.remove();
            this.runStore((ChunkPos)$$1.getKey(), (PendingStore)$$1.getValue());
            this.tellStorePending();
        }
    }

    private void tellStorePending() {
        this.mailbox.tell(new StrictQueue.IntRunnable(net.minecraft.world.level.chunk.storage.IOWorker.Priority.BACKGROUND.ordinal(), this::storePendingChunk));
    }

    private void runStore(ChunkPos p_63536_, PendingStore p_63537_) {
        try {
            this.storage.write(p_63536_, p_63537_.data);
            p_63537_.result.complete((Object)null);
        } catch (Exception var4) {
            Exception $$2 = var4;
            LOGGER.error("Failed to store chunk {}", p_63536_, $$2);
            p_63537_.result.completeExceptionally($$2);
        }

    }

    public void close() throws IOException {
        if (this.shutdownRequested.compareAndSet(false, true)) {
            this.mailbox.ask((p_223467_) -> {
                return new StrictQueue.IntRunnable(net.minecraft.world.level.chunk.storage.IOWorker.Priority.SHUTDOWN.ordinal(), () -> {
                    p_223467_.tell(Unit.INSTANCE);
                });
            }).join();
            this.mailbox.close();

            try {
                this.storage.close();
            } catch (Exception var2) {
                Exception $$0 = var2;
                LOGGER.error("Failed to close storage", $$0);
            }

        }
    }

    static enum Priority {
        FOREGROUND,
        BACKGROUND,
        SHUTDOWN;

        private Priority() {
        }
    }

    private static class PendingStore {
        @Nullable
        CompoundTag data;
        final CompletableFuture<Void> result = new CompletableFuture();

        public PendingStore(@Nullable CompoundTag p_63568_) {
            this.data = p_63568_;
        }
    }
}

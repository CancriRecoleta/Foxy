//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.chunk.storage;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.OptionalDynamic;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import org.slf4j.Logger;

public class SectionStorage<R> implements AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String SECTIONS_TAG = "Sections";
    private final IOWorker worker;
    private final Long2ObjectMap<Optional<R>> storage = new Long2ObjectOpenHashMap();
    private final LongLinkedOpenHashSet dirty = new LongLinkedOpenHashSet();
    private final Function<Runnable, Codec<R>> codec;
    private final Function<Runnable, R> factory;
    private final DataFixer fixerUpper;
    private final DataFixTypes type;
    private final RegistryAccess registryAccess;
    protected final LevelHeightAccessor levelHeightAccessor;

    public SectionStorage(Path p_223509_, Function<Runnable, Codec<R>> p_223510_, Function<Runnable, R> p_223511_, DataFixer p_223512_, DataFixTypes p_223513_, boolean p_223514_, RegistryAccess p_223515_, LevelHeightAccessor p_223516_) {
        this.codec = p_223510_;
        this.factory = p_223511_;
        this.fixerUpper = p_223512_;
        this.type = p_223513_;
        this.registryAccess = p_223515_;
        this.levelHeightAccessor = p_223516_;
        this.worker = new IOWorker(p_223509_, p_223514_, p_223509_.getFileName().toString());
    }

    protected void tick(BooleanSupplier p_63812_) {
        while(this.hasWork() && p_63812_.getAsBoolean()) {
            ChunkPos $$1 = SectionPos.of(this.dirty.firstLong()).chunk();
            this.writeColumn($$1);
        }

    }

    public boolean hasWork() {
        return !this.dirty.isEmpty();
    }

    @Nullable
    protected Optional<R> get(long p_63819_) {
        return (Optional)this.storage.get(p_63819_);
    }

    protected Optional<R> getOrLoad(long p_63824_) {
        if (this.outsideStoredRange(p_63824_)) {
            return Optional.empty();
        } else {
            Optional<R> $$1 = this.get(p_63824_);
            if ($$1 != null) {
                return $$1;
            } else {
                this.readColumn(SectionPos.of(p_63824_).chunk());
                $$1 = this.get(p_63824_);
                if ($$1 == null) {
                    throw (IllegalStateException)Util.pauseInIde(new IllegalStateException());
                } else {
                    return $$1;
                }
            }
        }
    }

    protected boolean outsideStoredRange(long p_156631_) {
        int $$1 = SectionPos.sectionToBlockCoord(SectionPos.y(p_156631_));
        return this.levelHeightAccessor.isOutsideBuildHeight($$1);
    }

    protected R getOrCreate(long p_63828_) {
        if (this.outsideStoredRange(p_63828_)) {
            throw (IllegalArgumentException)Util.pauseInIde(new IllegalArgumentException("sectionPos out of bounds"));
        } else {
            Optional<R> $$1 = this.getOrLoad(p_63828_);
            if ($$1.isPresent()) {
                return $$1.get();
            } else {
                R $$2 = this.factory.apply(() -> {
                    this.setDirty(p_63828_);
                });
                this.storage.put(p_63828_, Optional.of($$2));
                return $$2;
            }
        }
    }

    private void readColumn(ChunkPos p_63815_) {
        Optional<CompoundTag> $$1 = (Optional)this.tryRead(p_63815_).join();
        RegistryOps<Tag> $$2 = RegistryOps.create(NbtOps.INSTANCE, (HolderLookup.Provider)this.registryAccess);
        this.readColumn(p_63815_, $$2, (Tag)$$1.orElse((Object)null));
    }

    private CompletableFuture<Optional<CompoundTag>> tryRead(ChunkPos p_223533_) {
        return this.worker.loadAsync(p_223533_).exceptionally((p_223526_) -> {
            if (p_223526_ instanceof IOException $$2) {
                LOGGER.error("Error reading chunk {} data from disk", p_223533_, $$2);
                return Optional.empty();
            } else {
                throw new CompletionException(p_223526_);
            }
        });
    }

    private <T> void readColumn(ChunkPos p_63802_, DynamicOps<T> p_63803_, @Nullable T p_63804_) {
        if (p_63804_ == null) {
            for(int $$3 = this.levelHeightAccessor.getMinSection(); $$3 < this.levelHeightAccessor.getMaxSection(); ++$$3) {
                this.storage.put(getKey(p_63802_, $$3), Optional.empty());
            }
        } else {
            Dynamic<T> $$4 = new Dynamic(p_63803_, p_63804_);
            int $$5 = getVersion($$4);
            int $$6 = SharedConstants.getCurrentVersion().getDataVersion().getVersion();
            boolean $$7 = $$5 != $$6;
            Dynamic<T> $$8 = this.type.update(this.fixerUpper, $$4, $$5, $$6);
            OptionalDynamic<T> $$9 = $$8.get("Sections");

            for(int $$10 = this.levelHeightAccessor.getMinSection(); $$10 < this.levelHeightAccessor.getMaxSection(); ++$$10) {
                long $$11 = getKey(p_63802_, $$10);
                Optional<R> $$12 = $$9.get(Integer.toString($$10)).result().flatMap((p_223519_) -> {
                    DataResult var10000 = ((Codec)this.codec.apply(() -> {
                        this.setDirty($$11);
                    })).parse(p_223519_);
                    Logger var10001 = LOGGER;
                    Objects.requireNonNull(var10001);
                    return var10000.resultOrPartial(var10001::error);
                });
                this.storage.put($$11, $$12);
                $$12.ifPresent((p_223523_) -> {
                    this.onSectionLoad($$11);
                    if ($$7) {
                        this.setDirty($$11);
                    }

                });
            }
        }

    }

    private void writeColumn(ChunkPos p_63826_) {
        RegistryOps<Tag> $$1 = RegistryOps.create(NbtOps.INSTANCE, (HolderLookup.Provider)this.registryAccess);
        Dynamic<Tag> $$2 = this.writeColumn(p_63826_, $$1);
        Tag $$3 = (Tag)$$2.getValue();
        if ($$3 instanceof CompoundTag) {
            this.worker.store(p_63826_, (CompoundTag)$$3);
        } else {
            LOGGER.error("Expected compound tag, got {}", $$3);
        }

    }

    private <T> Dynamic<T> writeColumn(ChunkPos p_63799_, DynamicOps<T> p_63800_) {
        Map<T, T> $$2 = Maps.newHashMap();

        for(int $$3 = this.levelHeightAccessor.getMinSection(); $$3 < this.levelHeightAccessor.getMaxSection(); ++$$3) {
            long $$4 = getKey(p_63799_, $$3);
            this.dirty.remove($$4);
            Optional<R> $$5 = (Optional)this.storage.get($$4);
            if ($$5 != null && $$5.isPresent()) {
                DataResult<T> $$6 = ((Codec)this.codec.apply(() -> {
                    this.setDirty($$4);
                })).encodeStart(p_63800_, $$5.get());
                String $$7 = Integer.toString($$3);
                Logger var10001 = LOGGER;
                Objects.requireNonNull(var10001);
                $$6.resultOrPartial(var10001::error).ifPresent((p_223531_) -> {
                    $$2.put(p_63800_.createString($$7), p_223531_);
                });
            }
        }

        return new Dynamic(p_63800_, p_63800_.createMap(ImmutableMap.of(p_63800_.createString("Sections"), p_63800_.createMap($$2), p_63800_.createString("DataVersion"), p_63800_.createInt(SharedConstants.getCurrentVersion().getDataVersion().getVersion()))));
    }

    private static long getKey(ChunkPos p_156628_, int p_156629_) {
        return SectionPos.asLong(p_156628_.x, p_156629_, p_156628_.z);
    }

    protected void onSectionLoad(long p_63813_) {
    }

    protected void setDirty(long p_63788_) {
        Optional<R> $$1 = (Optional)this.storage.get(p_63788_);
        if ($$1 != null && $$1.isPresent()) {
            this.dirty.add(p_63788_);
        } else {
            LOGGER.warn("No data for position: {}", SectionPos.of(p_63788_));
        }
    }

    private static int getVersion(Dynamic<?> p_63806_) {
        return p_63806_.get("DataVersion").asInt(1945);
    }

    public void flush(ChunkPos p_63797_) {
        if (this.hasWork()) {
            for(int $$1 = this.levelHeightAccessor.getMinSection(); $$1 < this.levelHeightAccessor.getMaxSection(); ++$$1) {
                long $$2 = getKey(p_63797_, $$1);
                if (this.dirty.contains($$2)) {
                    this.writeColumn(p_63797_);
                    return;
                }
            }
        }

    }

    public void close() throws IOException {
        this.worker.close();
    }
}

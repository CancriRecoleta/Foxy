//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.chunk.storage;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.util.ExceptionCollector;
import net.minecraft.world.level.ChunkPos;

public final class RegionFileStorage implements AutoCloseable {
    public static final String ANVIL_EXTENSION = ".mca";
    private static final int MAX_CACHE_SIZE = 256;
    private final Long2ObjectLinkedOpenHashMap<RegionFile> regionCache = new Long2ObjectLinkedOpenHashMap();
    private final Path folder;
    private final boolean sync;

    RegionFileStorage(Path p_196954_, boolean p_196955_) {
        this.folder = p_196954_;
        this.sync = p_196955_;
    }

    private RegionFile getRegionFile(ChunkPos p_63712_) throws IOException {
        long $$1 = ChunkPos.asLong(p_63712_.getRegionX(), p_63712_.getRegionZ());
        RegionFile $$2 = (RegionFile)this.regionCache.getAndMoveToFirst($$1);
        if ($$2 != null) {
            return $$2;
        } else {
            if (this.regionCache.size() >= 256) {
                ((RegionFile)this.regionCache.removeLast()).close();
            }

            FileUtil.createDirectoriesSafe(this.folder);
            Path var10000 = this.folder;
            int var10001 = p_63712_.getRegionX();
            Path $$3 = var10000.resolve("r." + var10001 + "." + p_63712_.getRegionZ() + ".mca");
            RegionFile $$4 = new RegionFile($$3, this.folder, this.sync);
            this.regionCache.putAndMoveToFirst($$1, $$4);
            return $$4;
        }
    }

    @Nullable
    public CompoundTag read(ChunkPos p_63707_) throws IOException {
        RegionFile $$1 = this.getRegionFile(p_63707_);
        DataInputStream $$2 = $$1.getChunkDataInputStream(p_63707_);

        CompoundTag var4;
        label43: {
            try {
                if ($$2 == null) {
                    var4 = null;
                    break label43;
                }

                var4 = NbtIo.read((DataInput)$$2);
            } catch (Throwable var7) {
                if ($$2 != null) {
                    try {
                        $$2.close();
                    } catch (Throwable var6) {
                        var7.addSuppressed(var6);
                    }
                }

                throw var7;
            }

            if ($$2 != null) {
                $$2.close();
            }

            return var4;
        }

        if ($$2 != null) {
            $$2.close();
        }

        return var4;
    }

    public void scanChunk(ChunkPos p_196957_, StreamTagVisitor p_196958_) throws IOException {
        RegionFile $$2 = this.getRegionFile(p_196957_);
        DataInputStream $$3 = $$2.getChunkDataInputStream(p_196957_);

        try {
            if ($$3 != null) {
                NbtIo.parse($$3, p_196958_);
            }
        } catch (Throwable var8) {
            if ($$3 != null) {
                try {
                    $$3.close();
                } catch (Throwable var7) {
                    var8.addSuppressed(var7);
                }
            }

            throw var8;
        }

        if ($$3 != null) {
            $$3.close();
        }

    }

    protected void write(ChunkPos p_63709_, @Nullable CompoundTag p_63710_) throws IOException {
        RegionFile $$2 = this.getRegionFile(p_63709_);
        if (p_63710_ == null) {
            $$2.clear(p_63709_);
        } else {
            DataOutputStream $$3 = $$2.getChunkDataOutputStream(p_63709_);

            try {
                NbtIo.write(p_63710_, (DataOutput)$$3);
            } catch (Throwable var8) {
                if ($$3 != null) {
                    try {
                        $$3.close();
                    } catch (Throwable var7) {
                        var8.addSuppressed(var7);
                    }
                }

                throw var8;
            }

            if ($$3 != null) {
                $$3.close();
            }
        }

    }

    public void close() throws IOException {
        ExceptionCollector<IOException> $$0 = new ExceptionCollector();
        ObjectIterator var2 = this.regionCache.values().iterator();

        while(var2.hasNext()) {
            RegionFile $$1 = (RegionFile)var2.next();

            try {
                $$1.close();
            } catch (IOException var5) {
                IOException $$2 = var5;
                $$0.add($$2);
            }
        }

        $$0.throwIfPresent();
    }

    public void flush() throws IOException {
        ObjectIterator var1 = this.regionCache.values().iterator();

        while(var1.hasNext()) {
            RegionFile $$0 = (RegionFile)var1.next();
            $$0.flush();
        }

    }
}

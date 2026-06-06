//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util.worldupdate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMaps;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenCustomHashMap;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import net.minecraft.world.level.chunk.storage.RegionFile;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.slf4j.Logger;

public class WorldUpgrader {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ThreadFactory THREAD_FACTORY = (new ThreadFactoryBuilder()).setDaemon(true).build();
    private final Registry<LevelStem> dimensions;
    private final Set<ResourceKey<Level>> levels;
    private final boolean eraseCache;
    private final LevelStorageSource.LevelStorageAccess levelStorage;
    private final Thread thread;
    private final DataFixer dataFixer;
    private volatile boolean running = true;
    private volatile boolean finished;
    private volatile float progress;
    private volatile int totalChunks;
    private volatile int converted;
    private volatile int skipped;
    private final Object2FloatMap<ResourceKey<Level>> progressMap = Object2FloatMaps.synchronize(new Object2FloatOpenCustomHashMap(Util.identityStrategy()));
    private volatile Component status = Component.translatable("optimizeWorld.stage.counting");
    private static final Pattern REGEX = Pattern.compile("^r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca$");
    private final DimensionDataStorage overworldDataStorage;

    public WorldUpgrader(LevelStorageSource.LevelStorageAccess p_249922_, DataFixer p_250273_, Registry<LevelStem> p_252191_, boolean p_250738_) {
        this.dimensions = p_252191_;
        this.levels = (Set)p_252191_.registryKeySet().stream().map(Registries::levelStemToLevel).collect(Collectors.toUnmodifiableSet());
        this.eraseCache = p_250738_;
        this.dataFixer = p_250273_;
        this.levelStorage = p_249922_;
        this.overworldDataStorage = new DimensionDataStorage(this.levelStorage.getDimensionPath(Level.OVERWORLD).resolve("data").toFile(), p_250273_);
        this.thread = THREAD_FACTORY.newThread(this::work);
        this.thread.setUncaughtExceptionHandler((p_18825_, p_18826_) -> {
            LOGGER.error("Error upgrading world", p_18826_);
            this.status = Component.translatable("optimizeWorld.stage.failed");
            this.finished = true;
        });
        this.thread.start();
    }

    public void cancel() {
        this.running = false;

        try {
            this.thread.join();
        } catch (InterruptedException var2) {
        }

    }

    private void work() {
        this.totalChunks = 0;
        ImmutableMap.Builder<ResourceKey<Level>, ListIterator<ChunkPos>> $$0 = ImmutableMap.builder();

        List $$2;
        for(Iterator var2 = this.levels.iterator(); var2.hasNext(); this.totalChunks += $$2.size()) {
            ResourceKey<Level> $$1 = (ResourceKey)var2.next();
            $$2 = this.getAllChunkPos($$1);
            $$0.put($$1, $$2.listIterator());
        }

        if (this.totalChunks == 0) {
            this.finished = true;
        } else {
            float $$3 = (float)this.totalChunks;
            ImmutableMap<ResourceKey<Level>, ListIterator<ChunkPos>> $$4 = $$0.build();
            ImmutableMap.Builder<ResourceKey<Level>, ChunkStorage> $$5 = ImmutableMap.builder();
            Iterator var5 = this.levels.iterator();

            while(var5.hasNext()) {
                ResourceKey<Level> $$6 = (ResourceKey)var5.next();
                Path $$7 = this.levelStorage.getDimensionPath($$6);
                $$5.put($$6, new ChunkStorage($$7.resolve("region"), this.dataFixer, true));
            }

            ImmutableMap<ResourceKey<Level>, ChunkStorage> $$8 = $$5.build();
            long $$9 = Util.getMillis();
            this.status = Component.translatable("optimizeWorld.stage.upgrading");

            while(this.running) {
                boolean $$10 = false;
                float $$11 = 0.0F;

                float $$28;
                for(Iterator var10 = this.levels.iterator(); var10.hasNext(); $$11 += $$28) {
                    ResourceKey<Level> $$12 = (ResourceKey)var10.next();
                    ListIterator<ChunkPos> $$13 = (ListIterator)$$4.get($$12);
                    ChunkStorage $$14 = (ChunkStorage)$$8.get($$12);
                    if ($$13.hasNext()) {
                        ChunkPos $$15 = (ChunkPos)$$13.next();
                        boolean $$16 = false;

                        try {
                            CompoundTag $$17 = (CompoundTag)((Optional)$$14.read($$15).join()).orElse((Object)null);
                            if ($$17 != null) {
                                int $$18 = ChunkStorage.getVersion($$17);
                                ChunkGenerator $$19 = ((LevelStem)this.dimensions.getOrThrow(Registries.levelToLevelStem($$12))).generator();
                                CompoundTag $$20 = $$14.upgradeChunkTag($$12, () -> {
                                    return this.overworldDataStorage;
                                }, $$17, $$19.getTypeNameForDataFixer());
                                ChunkPos $$21 = new ChunkPos($$20.getInt("xPos"), $$20.getInt("zPos"));
                                if (!$$21.equals($$15)) {
                                    LOGGER.warn("Chunk {} has invalid position {}", $$15, $$21);
                                }

                                boolean $$22 = $$18 < SharedConstants.getCurrentVersion().getDataVersion().getVersion();
                                if (this.eraseCache) {
                                    $$22 = $$22 || $$20.contains("Heightmaps");
                                    $$20.remove("Heightmaps");
                                    $$22 = $$22 || $$20.contains("isLightOn");
                                    $$20.remove("isLightOn");
                                    ListTag $$23 = $$20.getList("sections", 10);

                                    for(int $$24 = 0; $$24 < $$23.size(); ++$$24) {
                                        CompoundTag $$25 = $$23.getCompound($$24);
                                        $$22 = $$22 || $$25.contains("BlockLight");
                                        $$25.remove("BlockLight");
                                        $$22 = $$22 || $$25.contains("SkyLight");
                                        $$25.remove("SkyLight");
                                    }
                                }

                                if ($$22) {
                                    $$14.write($$15, $$20);
                                    $$16 = true;
                                }
                            }
                        } catch (CompletionException | ReportedException var26) {
                            RuntimeException $$26 = var26;
                            Throwable $$27 = $$26.getCause();
                            if (!($$27 instanceof IOException)) {
                                throw $$26;
                            }

                            LOGGER.error("Error upgrading chunk {}", $$15, $$27);
                        }

                        if ($$16) {
                            ++this.converted;
                        } else {
                            ++this.skipped;
                        }

                        $$10 = true;
                    }

                    $$28 = (float)$$13.nextIndex() / $$3;
                    this.progressMap.put($$12, $$28);
                }

                this.progress = $$11;
                if (!$$10) {
                    this.running = false;
                }
            }

            this.status = Component.translatable("optimizeWorld.stage.finished");
            UnmodifiableIterator var32 = $$8.values().iterator();

            while(var32.hasNext()) {
                ChunkStorage $$29 = (ChunkStorage)var32.next();

                try {
                    $$29.close();
                } catch (IOException var25) {
                    LOGGER.error("Error upgrading chunk", var25);
                }
            }

            this.overworldDataStorage.save();
            $$9 = Util.getMillis() - $$9;
            LOGGER.info("World optimizaton finished after {} ms", $$9);
            this.finished = true;
        }
    }

    private List<ChunkPos> getAllChunkPos(ResourceKey<Level> p_18831_) {
        File $$1 = this.levelStorage.getDimensionPath(p_18831_).toFile();
        File $$2 = new File($$1, "region");
        File[] $$3 = $$2.listFiles((p_18822_, p_18823_) -> {
            return p_18823_.endsWith(".mca");
        });
        if ($$3 == null) {
            return ImmutableList.of();
        } else {
            List<ChunkPos> $$4 = Lists.newArrayList();
            File[] var6 = $$3;
            int var7 = $$3.length;

            for(int var8 = 0; var8 < var7; ++var8) {
                File $$5 = var6[var8];
                Matcher $$6 = REGEX.matcher($$5.getName());
                if ($$6.matches()) {
                    int $$7 = Integer.parseInt($$6.group(1)) << 5;
                    int $$8 = Integer.parseInt($$6.group(2)) << 5;

                    try {
                        RegionFile $$9 = new RegionFile($$5.toPath(), $$2.toPath(), true);

                        try {
                            for(int $$10 = 0; $$10 < 32; ++$$10) {
                                for(int $$11 = 0; $$11 < 32; ++$$11) {
                                    ChunkPos $$12 = new ChunkPos($$10 + $$7, $$11 + $$8);
                                    if ($$9.doesChunkExist($$12)) {
                                        $$4.add($$12);
                                    }
                                }
                            }
                        } catch (Throwable var18) {
                            try {
                                $$9.close();
                            } catch (Throwable var17) {
                                var18.addSuppressed(var17);
                            }

                            throw var18;
                        }

                        $$9.close();
                    } catch (Throwable var19) {
                    }
                }
            }

            return $$4;
        }
    }

    public boolean isFinished() {
        return this.finished;
    }

    public Set<ResourceKey<Level>> levels() {
        return this.levels;
    }

    public float dimensionProgress(ResourceKey<Level> p_18828_) {
        return this.progressMap.getFloat(p_18828_);
    }

    public float getProgress() {
        return this.progress;
    }

    public int getTotalChunks() {
        return this.totalChunks;
    }

    public int getConverted() {
        return this.converted;
    }

    public int getSkipped() {
        return this.skipped;
    }

    public Component getStatus() {
        return this.status;
    }
}

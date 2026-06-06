//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.structure;

import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2BooleanMap;
import it.unimi.dsi.fastutil.longs.Long2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.visitors.CollectFields;
import net.minecraft.nbt.visitors.FieldSelector;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.storage.ChunkScanAccess;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.slf4j.Logger;

public class StructureCheck {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int NO_STRUCTURE = -1;
    private final ChunkScanAccess storageAccess;
    private final RegistryAccess registryAccess;
    private final Registry<Biome> biomes;
    private final Registry<Structure> structureConfigs;
    private final StructureTemplateManager structureTemplateManager;
    private final ResourceKey<Level> dimension;
    private final ChunkGenerator chunkGenerator;
    private final RandomState randomState;
    private final LevelHeightAccessor heightAccessor;
    private final BiomeSource biomeSource;
    private final long seed;
    private final DataFixer fixerUpper;
    private final Long2ObjectMap<Object2IntMap<Structure>> loadedChunks = new Long2ObjectOpenHashMap();
    private final Map<Structure, Long2BooleanMap> featureChecks = new HashMap();

    public StructureCheck(ChunkScanAccess p_226712_, RegistryAccess p_226713_, StructureTemplateManager p_226714_, ResourceKey<Level> p_226715_, ChunkGenerator p_226716_, RandomState p_226717_, LevelHeightAccessor p_226718_, BiomeSource p_226719_, long p_226720_, DataFixer p_226721_) {
        this.storageAccess = p_226712_;
        this.registryAccess = p_226713_;
        this.structureTemplateManager = p_226714_;
        this.dimension = p_226715_;
        this.chunkGenerator = p_226716_;
        this.randomState = p_226717_;
        this.heightAccessor = p_226718_;
        this.biomeSource = p_226719_;
        this.seed = p_226720_;
        this.fixerUpper = p_226721_;
        this.biomes = p_226713_.registryOrThrow(Registries.BIOME);
        this.structureConfigs = p_226713_.registryOrThrow(Registries.STRUCTURE);
    }

    public StructureCheckResult checkStart(ChunkPos p_226730_, Structure p_226731_, boolean p_226732_) {
        long $$3 = p_226730_.toLong();
        Object2IntMap<Structure> $$4 = (Object2IntMap)this.loadedChunks.get($$3);
        if ($$4 != null) {
            return this.checkStructureInfo($$4, p_226731_, p_226732_);
        } else {
            StructureCheckResult $$5 = this.tryLoadFromStorage(p_226730_, p_226731_, p_226732_, $$3);
            if ($$5 != null) {
                return $$5;
            } else {
                boolean $$6 = ((Long2BooleanMap)this.featureChecks.computeIfAbsent(p_226731_, (p_226739_) -> {
                    return new Long2BooleanOpenHashMap();
                })).computeIfAbsent($$3, (p_226728_) -> {
                    return this.canCreateStructure(p_226730_, p_226731_);
                });
                return !$$6 ? StructureCheckResult.START_NOT_PRESENT : StructureCheckResult.CHUNK_LOAD_NEEDED;
            }
        }
    }

    private boolean canCreateStructure(ChunkPos p_226756_, Structure p_226757_) {
        RegistryAccess var10003 = this.registryAccess;
        ChunkGenerator var10004 = this.chunkGenerator;
        BiomeSource var10005 = this.biomeSource;
        RandomState var10006 = this.randomState;
        StructureTemplateManager var10007 = this.structureTemplateManager;
        long var10008 = this.seed;
        LevelHeightAccessor var10010 = this.heightAccessor;
        HolderSet var10011 = p_226757_.biomes();
        Objects.requireNonNull(var10011);
        return p_226757_.findValidGenerationPoint(new Structure.GenerationContext(var10003, var10004, var10005, var10006, var10007, var10008, p_226756_, var10010, var10011::contains)).isPresent();
    }

    @Nullable
    private StructureCheckResult tryLoadFromStorage(ChunkPos p_226734_, Structure p_226735_, boolean p_226736_, long p_226737_) {
        CollectFields $$4 = new CollectFields(new FieldSelector[]{new FieldSelector(IntTag.TYPE, "DataVersion"), new FieldSelector("Level", "Structures", CompoundTag.TYPE, "Starts"), new FieldSelector("structures", CompoundTag.TYPE, "starts")});

        try {
            this.storageAccess.scanChunk(p_226734_, $$4).join();
        } catch (Exception var13) {
            Exception $$5 = var13;
            LOGGER.warn("Failed to read chunk {}", p_226734_, $$5);
            return StructureCheckResult.CHUNK_LOAD_NEEDED;
        }

        Tag $$6 = $$4.getResult();
        if (!($$6 instanceof CompoundTag $$7)) {
            return null;
        } else {
            int $$8 = ChunkStorage.getVersion($$7);
            if ($$8 <= 1493) {
                return StructureCheckResult.CHUNK_LOAD_NEEDED;
            } else {
                ChunkStorage.injectDatafixingContext($$7, this.dimension, this.chunkGenerator.getTypeNameForDataFixer());

                CompoundTag $$11;
                try {
                    $$11 = DataFixTypes.CHUNK.updateToCurrentVersion(this.fixerUpper, $$7, $$8);
                } catch (Exception var12) {
                    Exception $$10 = var12;
                    LOGGER.warn("Failed to partially datafix chunk {}", p_226734_, $$10);
                    return StructureCheckResult.CHUNK_LOAD_NEEDED;
                }

                Object2IntMap<Structure> $$12 = this.loadStructures($$11);
                if ($$12 == null) {
                    return null;
                } else {
                    this.storeFullResults(p_226737_, $$12);
                    return this.checkStructureInfo($$12, p_226735_, p_226736_);
                }
            }
        }
    }

    @Nullable
    private Object2IntMap<Structure> loadStructures(CompoundTag p_197312_) {
        if (!p_197312_.contains("structures", 10)) {
            return null;
        } else {
            CompoundTag $$1 = p_197312_.getCompound("structures");
            if (!$$1.contains("starts", 10)) {
                return null;
            } else {
                CompoundTag $$2 = $$1.getCompound("starts");
                if ($$2.isEmpty()) {
                    return Object2IntMaps.emptyMap();
                } else {
                    Object2IntMap<Structure> $$3 = new Object2IntOpenHashMap();
                    Registry<Structure> $$4 = this.registryAccess.registryOrThrow(Registries.STRUCTURE);
                    Iterator var6 = $$2.getAllKeys().iterator();

                    while(var6.hasNext()) {
                        String $$5 = (String)var6.next();
                        ResourceLocation $$6 = ResourceLocation.tryParse($$5);
                        if ($$6 != null) {
                            Structure $$7 = (Structure)$$4.get($$6);
                            if ($$7 != null) {
                                CompoundTag $$8 = $$2.getCompound($$5);
                                if (!$$8.isEmpty()) {
                                    String $$9 = $$8.getString("id");
                                    if (!"INVALID".equals($$9)) {
                                        int $$10 = $$8.getInt("references");
                                        $$3.put($$7, $$10);
                                    }
                                }
                            }
                        }
                    }

                    return $$3;
                }
            }
        }
    }

    private static Object2IntMap<Structure> deduplicateEmptyMap(Object2IntMap<Structure> p_197299_) {
        return p_197299_.isEmpty() ? Object2IntMaps.emptyMap() : p_197299_;
    }

    private StructureCheckResult checkStructureInfo(Object2IntMap<Structure> p_226752_, Structure p_226753_, boolean p_226754_) {
        int $$3 = p_226752_.getOrDefault(p_226753_, -1);
        return $$3 == -1 || p_226754_ && $$3 != 0 ? StructureCheckResult.START_NOT_PRESENT : StructureCheckResult.START_PRESENT;
    }

    public void onStructureLoad(ChunkPos p_197283_, Map<Structure, StructureStart> p_197284_) {
        long $$2 = p_197283_.toLong();
        Object2IntMap<Structure> $$3 = new Object2IntOpenHashMap();
        p_197284_.forEach((p_226749_, p_226750_) -> {
            if (p_226750_.isValid()) {
                $$3.put(p_226749_, p_226750_.getReferences());
            }

        });
        this.storeFullResults($$2, $$3);
    }

    private void storeFullResults(long p_197264_, Object2IntMap<Structure> p_197265_) {
        this.loadedChunks.put(p_197264_, deduplicateEmptyMap(p_197265_));
        this.featureChecks.values().forEach((p_209956_) -> {
            p_209956_.remove(p_197264_);
        });
    }

    public void incrementReference(ChunkPos p_226723_, Structure p_226724_) {
        this.loadedChunks.compute(p_226723_.toLong(), (p_226745_, p_226746_) -> {
            if (p_226746_ == null || ((Object2IntMap)p_226746_).isEmpty()) {
                p_226746_ = new Object2IntOpenHashMap();
            }

            ((Object2IntMap)p_226746_).computeInt(p_226724_, (p_226741_, p_226742_) -> {
                return p_226742_ == null ? 1 : p_226742_ + 1;
            });
            return (Object2IntMap)p_226746_;
        });
    }
}

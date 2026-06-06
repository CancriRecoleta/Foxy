//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.chunk;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import net.minecraft.util.profiling.jfr.callback.ProfiledDuration;
import net.minecraft.world.level.levelgen.BelowZeroRetrogen;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.GenerationStep.Carving;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class ChunkStatus {
    public static final int MAX_STRUCTURE_DISTANCE = 8;
    private static final EnumSet<Heightmap.Types> PRE_FEATURES;
    public static final EnumSet<Heightmap.Types> POST_FEATURES;
    private static final LoadingTask PASSTHROUGH_LOAD_TASK;
    public static final ChunkStatus EMPTY;
    public static final ChunkStatus STRUCTURE_STARTS;
    public static final ChunkStatus STRUCTURE_REFERENCES;
    public static final ChunkStatus BIOMES;
    public static final ChunkStatus NOISE;
    public static final ChunkStatus SURFACE;
    public static final ChunkStatus CARVERS;
    public static final ChunkStatus FEATURES;
    public static final ChunkStatus INITIALIZE_LIGHT;
    public static final ChunkStatus LIGHT;
    public static final ChunkStatus SPAWN;
    public static final ChunkStatus FULL;
    private static final List<ChunkStatus> STATUS_BY_RANGE;
    private static final IntList RANGE_BY_STATUS;
    private final int index;
    private final ChunkStatus parent;
    private final GenerationTask generationTask;
    private final LoadingTask loadingTask;
    private final int range;
    private final boolean hasLoadDependencies;
    private final ChunkType chunkType;
    private final EnumSet<Heightmap.Types> heightmapsAfter;

    private static CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> initializeLight(ThreadedLevelLightEngine p_282288_, ChunkAccess p_282906_) {
        p_282906_.initializeLightSources();
        ((ProtoChunk)p_282906_).setLightEngine(p_282288_);
        boolean $$2 = isLighted(p_282906_);
        return p_282288_.initializeLight(p_282906_, $$2).thenApply(Either::left);
    }

    private static CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> lightChunk(ThreadedLevelLightEngine p_285039_, ChunkAccess p_285316_) {
        boolean $$2 = isLighted(p_285316_);
        return p_285039_.lightChunk(p_285316_, $$2).thenApply(Either::left);
    }

    private static ChunkStatus registerSimple(String p_62415_, @Nullable ChunkStatus p_62416_, int p_62417_, EnumSet<Heightmap.Types> p_62418_, ChunkType p_62419_, SimpleGenerationTask p_62420_) {
        return register(p_62415_, p_62416_, p_62417_, p_62418_, p_62419_, p_62420_);
    }

    private static ChunkStatus register(String p_62400_, @Nullable ChunkStatus p_62401_, int p_62402_, EnumSet<Heightmap.Types> p_62403_, ChunkType p_62404_, GenerationTask p_62405_) {
        return register(p_62400_, p_62401_, p_62402_, false, p_62403_, p_62404_, p_62405_, PASSTHROUGH_LOAD_TASK);
    }

    private static ChunkStatus register(String p_282817_, @Nullable ChunkStatus p_282644_, int p_281535_, boolean p_282329_, EnumSet<Heightmap.Types> p_281310_, ChunkType p_281968_, GenerationTask p_283654_, LoadingTask p_282175_) {
        return (ChunkStatus)Registry.register(BuiltInRegistries.CHUNK_STATUS, (String)p_282817_, new ChunkStatus(p_282644_, p_281535_, p_282329_, p_281310_, p_281968_, p_283654_, p_282175_));
    }

    public static List<ChunkStatus> getStatusList() {
        List<ChunkStatus> $$0 = Lists.newArrayList();

        ChunkStatus $$1;
        for($$1 = FULL; $$1.getParent() != $$1; $$1 = $$1.getParent()) {
            $$0.add($$1);
        }

        $$0.add($$1);
        Collections.reverse($$0);
        return $$0;
    }

    private static boolean isLighted(ChunkAccess p_285378_) {
        return p_285378_.getStatus().isOrAfter(LIGHT) && p_285378_.isLightCorrect();
    }

    public static ChunkStatus getStatusAroundFullChunk(int p_156186_) {
        if (p_156186_ >= STATUS_BY_RANGE.size()) {
            return EMPTY;
        } else {
            return p_156186_ < 0 ? FULL : (ChunkStatus)STATUS_BY_RANGE.get(p_156186_);
        }
    }

    public static int maxDistance() {
        return STATUS_BY_RANGE.size();
    }

    public static int getDistance(ChunkStatus p_62371_) {
        return RANGE_BY_STATUS.getInt(p_62371_.getIndex());
    }

    public ChunkStatus(@Nullable ChunkStatus p_289640_, int p_289655_, boolean p_289657_, EnumSet<Heightmap.Types> p_289662_, ChunkType p_289652_, GenerationTask p_289679_, LoadingTask p_289646_) {
        this.parent = p_289640_ == null ? this : p_289640_;
        this.generationTask = p_289679_;
        this.loadingTask = p_289646_;
        this.range = p_289655_;
        this.hasLoadDependencies = p_289657_;
        this.chunkType = p_289652_;
        this.heightmapsAfter = p_289662_;
        this.index = p_289640_ == null ? 0 : p_289640_.getIndex() + 1;
    }

    public int getIndex() {
        return this.index;
    }

    public ChunkStatus getParent() {
        return this.parent;
    }

    public CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> generate(Executor p_283276_, ServerLevel p_281420_, ChunkGenerator p_281836_, StructureTemplateManager p_281305_, ThreadedLevelLightEngine p_282570_, Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> p_283114_, List<ChunkAccess> p_282723_) {
        ChunkAccess $$7 = (ChunkAccess)p_282723_.get(p_282723_.size() / 2);
        ProfiledDuration $$8 = JvmProfiler.INSTANCE.onChunkGenerate($$7.getPos(), p_281420_.dimension(), this.toString());
        return this.generationTask.doWork(this, p_283276_, p_281420_, p_281836_, p_281305_, p_282570_, p_283114_, p_282723_, $$7).thenApply((p_281217_) -> {
            p_281217_.ifLeft((p_290029_) -> {
                if (p_290029_ instanceof ProtoChunk $$1) {
                    if (!$$1.getStatus().isOrAfter(this)) {
                        $$1.setStatus(this);
                    }
                }

            });
            if ($$8 != null) {
                $$8.finish();
            }

            return p_281217_;
        });
    }

    public CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> load(ServerLevel p_223245_, StructureTemplateManager p_223246_, ThreadedLevelLightEngine p_223247_, Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> p_223248_, ChunkAccess p_223249_) {
        return this.loadingTask.doWork(this, p_223245_, p_223246_, p_223247_, p_223248_, p_223249_);
    }

    public int getRange() {
        return this.range;
    }

    public boolean hasLoadDependencies() {
        return this.hasLoadDependencies;
    }

    public ChunkType getChunkType() {
        return this.chunkType;
    }

    public static ChunkStatus byName(String p_62398_) {
        return (ChunkStatus)BuiltInRegistries.CHUNK_STATUS.get(ResourceLocation.tryParse(p_62398_));
    }

    public EnumSet<Heightmap.Types> heightmapsAfter() {
        return this.heightmapsAfter;
    }

    public boolean isOrAfter(ChunkStatus p_62428_) {
        return this.getIndex() >= p_62428_.getIndex();
    }

    public String toString() {
        return BuiltInRegistries.CHUNK_STATUS.getKey(this).toString();
    }

    static {
        PRE_FEATURES = EnumSet.of(Types.OCEAN_FLOOR_WG, Types.WORLD_SURFACE_WG);
        POST_FEATURES = EnumSet.of(Types.OCEAN_FLOOR, Types.WORLD_SURFACE, Types.MOTION_BLOCKING, Types.MOTION_BLOCKING_NO_LEAVES);
        PASSTHROUGH_LOAD_TASK = (p_281194_, p_281195_, p_281196_, p_281197_, p_281198_, p_281199_) -> {
            return CompletableFuture.completedFuture(Either.left(p_281199_));
        };
        EMPTY = registerSimple("empty", (ChunkStatus)null, -1, PRE_FEATURES, net.minecraft.world.level.chunk.ChunkStatus.ChunkType.PROTOCHUNK, (p_156307_, p_156308_, p_156309_, p_156310_, p_156311_) -> {
        });
        STRUCTURE_STARTS = register("structure_starts", EMPTY, 0, false, PRE_FEATURES, net.minecraft.world.level.chunk.ChunkStatus.ChunkType.PROTOCHUNK, (p_289514_, p_289515_, p_289516_, p_289517_, p_289518_, p_289519_, p_289520_, p_289521_, p_289522_) -> {
            if (p_289516_.getServer().getWorldData().worldGenOptions().generateStructures()) {
                p_289517_.createStructures(p_289516_.registryAccess(), p_289516_.getChunkSource().getGeneratorState(), p_289516_.structureManager(), p_289522_, p_289518_);
            }

            p_289516_.onStructureStartsAvailable(p_289522_);
            return CompletableFuture.completedFuture(Either.left(p_289522_));
        }, (p_281209_, p_281210_, p_281211_, p_281212_, p_281213_, p_281214_) -> {
            p_281210_.onStructureStartsAvailable(p_281214_);
            return CompletableFuture.completedFuture(Either.left(p_281214_));
        });
        STRUCTURE_REFERENCES = registerSimple("structure_references", STRUCTURE_STARTS, 8, PRE_FEATURES, net.minecraft.world.level.chunk.ChunkStatus.ChunkType.PROTOCHUNK, (p_196843_, p_196844_, p_196845_, p_196846_, p_196847_) -> {
            WorldGenRegion $$5 = new WorldGenRegion(p_196844_, p_196846_, p_196843_, -1);
            p_196845_.createReferences($$5, p_196844_.structureManager().forWorldGenRegion($$5), p_196847_);
        });
        BIOMES = register("biomes", STRUCTURE_REFERENCES, 8, PRE_FEATURES, net.minecraft.world.level.chunk.ChunkStatus.ChunkType.PROTOCHUNK, (p_281200_, p_281201_, p_281202_, p_281203_, p_281204_, p_281205_, p_281206_, p_281207_, p_281208_) -> {
            WorldGenRegion $$9 = new WorldGenRegion(p_281202_, p_281207_, p_281200_, -1);
            return p_281203_.createBiomes(p_281201_, p_281202_.getChunkSource().randomState(), Blender.of($$9), p_281202_.structureManager().forWorldGenRegion($$9), p_281208_).thenApply((p_281193_) -> {
                return Either.left(p_281193_);
            });
        });
        NOISE = register("noise", BIOMES, 8, PRE_FEATURES, net.minecraft.world.level.chunk.ChunkStatus.ChunkType.PROTOCHUNK, (p_281161_, p_281162_, p_281163_, p_281164_, p_281165_, p_281166_, p_281167_, p_281168_, p_281169_) -> {
            WorldGenRegion $$9 = new WorldGenRegion(p_281163_, p_281168_, p_281161_, 0);
            return p_281164_.fillFromNoise(p_281162_, Blender.of($$9), p_281163_.getChunkSource().randomState(), p_281163_.structureManager().forWorldGenRegion($$9), p_281169_).thenApply((p_281218_) -> {
                if (p_281218_ instanceof ProtoChunk $$1) {
                    BelowZeroRetrogen $$2 = $$1.getBelowZeroRetrogen();
                    if ($$2 != null) {
                        BelowZeroRetrogen.replaceOldBedrock($$1);
                        if ($$2.hasBedrockHoles()) {
                            $$2.applyBedrockMask($$1);
                        }
                    }
                }

                return Either.left(p_281218_);
            });
        });
        SURFACE = registerSimple("surface", NOISE, 8, PRE_FEATURES, net.minecraft.world.level.chunk.ChunkStatus.ChunkType.PROTOCHUNK, (p_156247_, p_156248_, p_156249_, p_156250_, p_156251_) -> {
            WorldGenRegion $$5 = new WorldGenRegion(p_156248_, p_156250_, p_156247_, 0);
            p_156249_.buildSurface($$5, p_156248_.structureManager().forWorldGenRegion($$5), p_156248_.getChunkSource().randomState(), p_156251_);
        });
        CARVERS = registerSimple("carvers", SURFACE, 8, POST_FEATURES, net.minecraft.world.level.chunk.ChunkStatus.ChunkType.PROTOCHUNK, (p_289523_, p_289524_, p_289525_, p_289526_, p_289527_) -> {
            WorldGenRegion $$5 = new WorldGenRegion(p_289524_, p_289526_, p_289523_, 0);
            if (p_289527_ instanceof ProtoChunk $$6) {
                Blender.addAroundOldChunksCarvingMaskFilter($$5, $$6);
            }

            p_289525_.applyCarvers($$5, p_289524_.getSeed(), p_289524_.getChunkSource().randomState(), p_289524_.getBiomeManager(), p_289524_.structureManager().forWorldGenRegion($$5), p_289527_, Carving.AIR);
        });
        FEATURES = registerSimple("features", CARVERS, 8, POST_FEATURES, net.minecraft.world.level.chunk.ChunkStatus.ChunkType.PROTOCHUNK, (p_281188_, p_281189_, p_281190_, p_281191_, p_281192_) -> {
            Heightmap.primeHeightmaps(p_281192_, EnumSet.of(Types.MOTION_BLOCKING, Types.MOTION_BLOCKING_NO_LEAVES, Types.OCEAN_FLOOR, Types.WORLD_SURFACE));
            WorldGenRegion $$5 = new WorldGenRegion(p_281189_, p_281191_, p_281188_, 1);
            p_281190_.applyBiomeDecoration($$5, p_281192_, p_281189_.structureManager().forWorldGenRegion($$5));
            Blender.generateBorderTicks($$5, p_281192_);
        });
        INITIALIZE_LIGHT = register("initialize_light", FEATURES, 0, false, POST_FEATURES, net.minecraft.world.level.chunk.ChunkStatus.ChunkType.PROTOCHUNK, (p_281179_, p_281180_, p_281181_, p_281182_, p_281183_, p_281184_, p_281185_, p_281186_, p_281187_) -> {
            return initializeLight(p_281184_, p_281187_);
        }, (p_281155_, p_281156_, p_281157_, p_281158_, p_281159_, p_281160_) -> {
            return initializeLight(p_281158_, p_281160_);
        });
        LIGHT = register("light", INITIALIZE_LIGHT, 1, true, POST_FEATURES, net.minecraft.world.level.chunk.ChunkStatus.ChunkType.PROTOCHUNK, (p_284904_, p_284905_, p_284906_, p_284907_, p_284908_, p_284909_, p_284910_, p_284911_, p_284912_) -> {
            return lightChunk(p_284909_, p_284912_);
        }, (p_284898_, p_284899_, p_284900_, p_284901_, p_284902_, p_284903_) -> {
            return lightChunk(p_284901_, p_284903_);
        });
        SPAWN = registerSimple("spawn", LIGHT, 0, POST_FEATURES, net.minecraft.world.level.chunk.ChunkStatus.ChunkType.PROTOCHUNK, (p_196758_, p_196759_, p_196760_, p_196761_, p_196762_) -> {
            if (!p_196762_.isUpgrading()) {
                p_196760_.spawnOriginalMobs(new WorldGenRegion(p_196759_, p_196761_, p_196758_, -1));
            }

        });
        FULL = register("full", SPAWN, 0, false, POST_FEATURES, net.minecraft.world.level.chunk.ChunkStatus.ChunkType.LEVELCHUNK, (p_223267_, p_223268_, p_223269_, p_223270_, p_223271_, p_223272_, p_223273_, p_223274_, p_223275_) -> {
            return (CompletableFuture)p_223273_.apply(p_223275_);
        }, (p_223260_, p_223261_, p_223262_, p_223263_, p_223264_, p_223265_) -> {
            return (CompletableFuture)p_223264_.apply(p_223265_);
        });
        STATUS_BY_RANGE = ImmutableList.of(FULL, INITIALIZE_LIGHT, CARVERS, BIOMES, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, new ChunkStatus[0]);
        RANGE_BY_STATUS = (IntList)Util.make(new IntArrayList(getStatusList().size()), (p_283066_) -> {
            int $$1 = 0;

            for(int $$2 = getStatusList().size() - 1; $$2 >= 0; --$$2) {
                while($$1 + 1 < STATUS_BY_RANGE.size() && $$2 <= ((ChunkStatus)STATUS_BY_RANGE.get($$1 + 1)).getIndex()) {
                    ++$$1;
                }

                p_283066_.add(0, $$1);
            }

        });
    }

    public static enum ChunkType {
        PROTOCHUNK,
        LEVELCHUNK;

        private ChunkType() {
        }
    }

    private interface GenerationTask {
        CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> doWork(ChunkStatus var1, Executor var2, ServerLevel var3, ChunkGenerator var4, StructureTemplateManager var5, ThreadedLevelLightEngine var6, Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> var7, List<ChunkAccess> var8, ChunkAccess var9);
    }

    interface LoadingTask {
        CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> doWork(ChunkStatus var1, ServerLevel var2, StructureTemplateManager var3, ThreadedLevelLightEngine var4, Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> var5, ChunkAccess var6);
    }

    private interface SimpleGenerationTask extends GenerationTask {
        default CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> doWork(ChunkStatus p_281382_, Executor p_283285_, ServerLevel p_283408_, ChunkGenerator p_282263_, StructureTemplateManager p_282374_, ThreadedLevelLightEngine p_281701_, Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> p_282473_, List<ChunkAccess> p_282316_, ChunkAccess p_281861_) {
            this.doWork(p_281382_, p_283408_, p_282263_, p_282316_, p_281861_);
            return CompletableFuture.completedFuture(Either.left(p_281861_));
        }

        void doWork(ChunkStatus var1, ServerLevel var2, ChunkGenerator var3, List<ChunkAccess> var4, ChunkAccess var5);
    }
}

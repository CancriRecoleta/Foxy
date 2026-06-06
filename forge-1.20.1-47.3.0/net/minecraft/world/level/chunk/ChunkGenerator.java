//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.chunk;

import com.google.common.base.Suppliers;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.FeatureSorter;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureCheckResult;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride.BoundingBoxType;
import net.minecraft.world.level.levelgen.structure.placement.ConcentricRingsStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.apache.commons.lang3.mutable.MutableBoolean;

public abstract class ChunkGenerator {
    public static final Codec<ChunkGenerator> CODEC;
    protected final BiomeSource biomeSource;
    private final Supplier<List<FeatureSorter.StepFeatureData>> featuresPerStep;
    private final Function<Holder<Biome>, BiomeGenerationSettings> generationSettingsGetter;

    public ChunkGenerator(BiomeSource p_256133_) {
        this(p_256133_, (p_223234_) -> {
            return ((Biome)p_223234_.value()).getGenerationSettings();
        });
    }

    public ChunkGenerator(BiomeSource p_255838_, Function<Holder<Biome>, BiomeGenerationSettings> p_256216_) {
        this.biomeSource = p_255838_;
        this.generationSettingsGetter = p_256216_;
        this.featuresPerStep = Suppliers.memoize(() -> {
            return FeatureSorter.buildFeaturesPerStep(List.copyOf(p_255838_.possibleBiomes()), (p_223216_) -> {
                return ((BiomeGenerationSettings)p_256216_.apply(p_223216_)).features();
            }, true);
        });
    }

    protected abstract Codec<? extends ChunkGenerator> codec();

    public ChunkGeneratorStructureState createState(HolderLookup<StructureSet> p_256405_, RandomState p_256101_, long p_256018_) {
        return ChunkGeneratorStructureState.createForNormal(p_256101_, p_256018_, this.biomeSource, p_256405_);
    }

    public Optional<ResourceKey<Codec<? extends ChunkGenerator>>> getTypeNameForDataFixer() {
        return BuiltInRegistries.CHUNK_GENERATOR.getResourceKey(this.codec());
    }

    public CompletableFuture<ChunkAccess> createBiomes(Executor p_223159_, RandomState p_223160_, Blender p_223161_, StructureManager p_223162_, ChunkAccess p_223163_) {
        return CompletableFuture.supplyAsync(Util.wrapThreadWithTaskName("init_biomes", () -> {
            p_223163_.fillBiomesFromNoise(this.biomeSource, p_223160_.sampler());
            return p_223163_;
        }), Util.backgroundExecutor());
    }

    public abstract void applyCarvers(WorldGenRegion var1, long var2, RandomState var4, BiomeManager var5, StructureManager var6, ChunkAccess var7, GenerationStep.Carving var8);

    @Nullable
    public Pair<BlockPos, Holder<Structure>> findNearestMapStructure(ServerLevel p_223038_, HolderSet<Structure> p_223039_, BlockPos p_223040_, int p_223041_, boolean p_223042_) {
        ChunkGeneratorStructureState $$5 = p_223038_.getChunkSource().getGeneratorState();
        Map<StructurePlacement, Set<Holder<Structure>>> $$6 = new Object2ObjectArrayMap();
        Iterator var8 = p_223039_.iterator();

        while(var8.hasNext()) {
            Holder<Structure> $$7 = (Holder)var8.next();
            Iterator var10 = $$5.getPlacementsForStructure($$7).iterator();

            while(var10.hasNext()) {
                StructurePlacement $$8 = (StructurePlacement)var10.next();
                ((Set)$$6.computeIfAbsent($$8, (p_223127_) -> {
                    return new ObjectArraySet();
                })).add($$7);
            }
        }

        if ($$6.isEmpty()) {
            return null;
        } else {
            Pair<BlockPos, Holder<Structure>> $$9 = null;
            double $$10 = Double.MAX_VALUE;
            StructureManager $$11 = p_223038_.structureManager();
            List<Map.Entry<StructurePlacement, Set<Holder<Structure>>>> $$12 = new ArrayList($$6.size());
            Iterator var13 = $$6.entrySet().iterator();

            while(var13.hasNext()) {
                Map.Entry<StructurePlacement, Set<Holder<Structure>>> $$13 = (Map.Entry)var13.next();
                StructurePlacement $$14 = (StructurePlacement)$$13.getKey();
                if ($$14 instanceof ConcentricRingsStructurePlacement) {
                    ConcentricRingsStructurePlacement $$15 = (ConcentricRingsStructurePlacement)$$14;
                    Pair<BlockPos, Holder<Structure>> $$16 = this.getNearestGeneratedStructure((Set)$$13.getValue(), p_223038_, $$11, p_223040_, p_223042_, $$15);
                    if ($$16 != null) {
                        BlockPos $$17 = (BlockPos)$$16.getFirst();
                        double $$18 = p_223040_.distSqr($$17);
                        if ($$18 < $$10) {
                            $$10 = $$18;
                            $$9 = $$16;
                        }
                    }
                } else if ($$14 instanceof RandomSpreadStructurePlacement) {
                    $$12.add($$13);
                }
            }

            if (!$$12.isEmpty()) {
                int $$19 = SectionPos.blockToSectionCoord(p_223040_.getX());
                int $$20 = SectionPos.blockToSectionCoord(p_223040_.getZ());

                for(int $$21 = 0; $$21 <= p_223041_; ++$$21) {
                    boolean $$22 = false;
                    Iterator var30 = $$12.iterator();

                    while(var30.hasNext()) {
                        Map.Entry<StructurePlacement, Set<Holder<Structure>>> $$23 = (Map.Entry)var30.next();
                        RandomSpreadStructurePlacement $$24 = (RandomSpreadStructurePlacement)$$23.getKey();
                        Pair<BlockPos, Holder<Structure>> $$25 = getNearestGeneratedStructure((Set)$$23.getValue(), p_223038_, $$11, $$19, $$20, $$21, p_223042_, $$5.getLevelSeed(), $$24);
                        if ($$25 != null) {
                            $$22 = true;
                            double $$26 = p_223040_.distSqr((Vec3i)$$25.getFirst());
                            if ($$26 < $$10) {
                                $$10 = $$26;
                                $$9 = $$25;
                            }
                        }
                    }

                    if ($$22) {
                        return $$9;
                    }
                }
            }

            return $$9;
        }
    }

    @Nullable
    private Pair<BlockPos, Holder<Structure>> getNearestGeneratedStructure(Set<Holder<Structure>> p_223182_, ServerLevel p_223183_, StructureManager p_223184_, BlockPos p_223185_, boolean p_223186_, ConcentricRingsStructurePlacement p_223187_) {
        List<ChunkPos> $$6 = p_223183_.getChunkSource().getGeneratorState().getRingPositionsFor(p_223187_);
        if ($$6 == null) {
            throw new IllegalStateException("Somehow tried to find structures for a placement that doesn't exist");
        } else {
            Pair<BlockPos, Holder<Structure>> $$7 = null;
            double $$8 = Double.MAX_VALUE;
            BlockPos.MutableBlockPos $$9 = new BlockPos.MutableBlockPos();
            Iterator var12 = $$6.iterator();

            while(var12.hasNext()) {
                ChunkPos $$10 = (ChunkPos)var12.next();
                $$9.set(SectionPos.sectionToBlockCoord($$10.x, 8), 32, SectionPos.sectionToBlockCoord($$10.z, 8));
                double $$11 = $$9.distSqr(p_223185_);
                boolean $$12 = $$7 == null || $$11 < $$8;
                if ($$12) {
                    Pair<BlockPos, Holder<Structure>> $$13 = getStructureGeneratingAt(p_223182_, p_223183_, p_223184_, p_223186_, p_223187_, $$10);
                    if ($$13 != null) {
                        $$7 = $$13;
                        $$8 = $$11;
                    }
                }
            }

            return $$7;
        }
    }

    @Nullable
    private static Pair<BlockPos, Holder<Structure>> getNearestGeneratedStructure(Set<Holder<Structure>> p_223189_, LevelReader p_223190_, StructureManager p_223191_, int p_223192_, int p_223193_, int p_223194_, boolean p_223195_, long p_223196_, RandomSpreadStructurePlacement p_223197_) {
        int $$9 = p_223197_.spacing();

        for(int $$10 = -p_223194_; $$10 <= p_223194_; ++$$10) {
            boolean $$11 = $$10 == -p_223194_ || $$10 == p_223194_;

            for(int $$12 = -p_223194_; $$12 <= p_223194_; ++$$12) {
                boolean $$13 = $$12 == -p_223194_ || $$12 == p_223194_;
                if ($$11 || $$13) {
                    int $$14 = p_223192_ + $$9 * $$10;
                    int $$15 = p_223193_ + $$9 * $$12;
                    ChunkPos $$16 = p_223197_.getPotentialStructureChunk(p_223196_, $$14, $$15);
                    Pair<BlockPos, Holder<Structure>> $$17 = getStructureGeneratingAt(p_223189_, p_223190_, p_223191_, p_223195_, p_223197_, $$16);
                    if ($$17 != null) {
                        return $$17;
                    }
                }
            }
        }

        return null;
    }

    @Nullable
    private static Pair<BlockPos, Holder<Structure>> getStructureGeneratingAt(Set<Holder<Structure>> p_223199_, LevelReader p_223200_, StructureManager p_223201_, boolean p_223202_, StructurePlacement p_223203_, ChunkPos p_223204_) {
        Iterator var6 = p_223199_.iterator();

        Holder $$6;
        StructureStart $$9;
        do {
            do {
                do {
                    StructureCheckResult $$7;
                    do {
                        if (!var6.hasNext()) {
                            return null;
                        }

                        $$6 = (Holder)var6.next();
                        $$7 = p_223201_.checkStructurePresence(p_223204_, (Structure)$$6.value(), p_223202_);
                    } while($$7 == StructureCheckResult.START_NOT_PRESENT);

                    if (!p_223202_ && $$7 == StructureCheckResult.START_PRESENT) {
                        return Pair.of(p_223203_.getLocatePos(p_223204_), $$6);
                    }

                    ChunkAccess $$8 = p_223200_.getChunk(p_223204_.x, p_223204_.z, ChunkStatus.STRUCTURE_STARTS);
                    $$9 = p_223201_.getStartForStructure(SectionPos.bottomOf($$8), (Structure)$$6.value(), $$8);
                } while($$9 == null);
            } while(!$$9.isValid());
        } while(p_223202_ && !tryAddReference(p_223201_, $$9));

        return Pair.of(p_223203_.getLocatePos($$9.getChunkPos()), $$6);
    }

    private static boolean tryAddReference(StructureManager p_223060_, StructureStart p_223061_) {
        if (p_223061_.canBeReferenced()) {
            p_223060_.addReference(p_223061_);
            return true;
        } else {
            return false;
        }
    }

    public void applyBiomeDecoration(WorldGenLevel p_223087_, ChunkAccess p_223088_, StructureManager p_223089_) {
        ChunkPos $$3 = p_223088_.getPos();
        if (!SharedConstants.debugVoidTerrain($$3)) {
            SectionPos $$4 = SectionPos.of($$3, p_223087_.getMinSection());
            BlockPos $$5 = $$4.origin();
            Registry<Structure> $$6 = p_223087_.registryAccess().registryOrThrow(Registries.STRUCTURE);
            Map<Integer, List<Structure>> $$7 = (Map)$$6.stream().collect(Collectors.groupingBy((p_223103_) -> {
                return p_223103_.step().ordinal();
            }));
            List<FeatureSorter.StepFeatureData> $$8 = (List)this.featuresPerStep.get();
            WorldgenRandom $$9 = new WorldgenRandom(new XoroshiroRandomSource(RandomSupport.generateUniqueSeed()));
            long $$10 = $$9.setDecorationSeed(p_223087_.getSeed(), $$5.getX(), $$5.getZ());
            Set<Holder<Biome>> $$11 = new ObjectArraySet();
            ChunkPos.rangeClosed($$4.chunk(), 1).forEach((p_223093_) -> {
                ChunkAccess $$3 = p_223087_.getChunk(p_223093_.x, p_223093_.z);
                LevelChunkSection[] var4 = $$3.getSections();
                int var5 = var4.length;

                for(int var6 = 0; var6 < var5; ++var6) {
                    LevelChunkSection $$4 = var4[var6];
                    PalettedContainerRO var10000 = $$4.getBiomes();
                    Objects.requireNonNull($$11);
                    var10000.getAll($$11::add);
                }

            });
            $$11.retainAll(this.biomeSource.possibleBiomes());
            int $$12 = $$8.size();

            try {
                Registry<PlacedFeature> $$13 = p_223087_.registryAccess().registryOrThrow(Registries.PLACED_FEATURE);
                int $$14 = Math.max(Decoration.values().length, $$12);

                for(int $$15 = 0; $$15 < $$14; ++$$15) {
                    int $$16 = 0;
                    CrashReportCategory var10000;
                    Iterator var20;
                    if (p_223089_.shouldGenerateStructures()) {
                        List<Structure> $$17 = (List)$$7.getOrDefault($$15, Collections.emptyList());

                        for(var20 = $$17.iterator(); var20.hasNext(); ++$$16) {
                            Structure $$18 = (Structure)var20.next();
                            $$9.setFeatureSeed($$10, $$16, $$15);
                            Supplier<String> $$19 = () -> {
                                Optional var10000 = $$6.getResourceKey($$18).map(Object::toString);
                                Objects.requireNonNull($$18);
                                return (String)var10000.orElseGet($$18::toString);
                            };

                            try {
                                p_223087_.setCurrentlyGenerating($$19);
                                p_223089_.startsForStructure($$4, $$18).forEach((p_223086_) -> {
                                    p_223086_.placeInChunk(p_223087_, p_223089_, this, $$9, getWritableArea(p_223088_), $$3);
                                });
                            } catch (Exception var29) {
                                Exception $$20 = var29;
                                CrashReport $$21 = CrashReport.forThrowable($$20, "Feature placement");
                                var10000 = $$21.addCategory("Feature");
                                Objects.requireNonNull($$19);
                                var10000.setDetail("Description", $$19::get);
                                throw new ReportedException($$21);
                            }
                        }
                    }

                    if ($$15 < $$12) {
                        IntSet $$22 = new IntArraySet();
                        var20 = $$11.iterator();

                        while(var20.hasNext()) {
                            Holder<Biome> $$23 = (Holder)var20.next();
                            List<HolderSet<PlacedFeature>> $$24 = ((BiomeGenerationSettings)this.generationSettingsGetter.apply($$23)).features();
                            if ($$15 < $$24.size()) {
                                HolderSet<PlacedFeature> $$25 = (HolderSet)$$24.get($$15);
                                FeatureSorter.StepFeatureData $$26 = (FeatureSorter.StepFeatureData)$$8.get($$15);
                                $$25.stream().map(Holder::value).forEach((p_223174_) -> {
                                    $$22.add($$26.indexMapping().applyAsInt(p_223174_));
                                });
                            }
                        }

                        int $$27 = $$22.size();
                        int[] $$28 = $$22.toIntArray();
                        Arrays.sort($$28);
                        FeatureSorter.StepFeatureData $$29 = (FeatureSorter.StepFeatureData)$$8.get($$15);

                        for(int $$30 = 0; $$30 < $$27; ++$$30) {
                            int $$31 = $$28[$$30];
                            PlacedFeature $$32 = (PlacedFeature)$$29.features().get($$31);
                            Supplier<String> $$33 = () -> {
                                Optional var10000 = $$13.getResourceKey($$32).map(Object::toString);
                                Objects.requireNonNull($$32);
                                return (String)var10000.orElseGet($$32::toString);
                            };
                            $$9.setFeatureSeed($$10, $$31, $$15);

                            try {
                                p_223087_.setCurrentlyGenerating($$33);
                                $$32.placeWithBiomeCheck(p_223087_, this, $$9, $$5);
                            } catch (Exception var30) {
                                Exception $$34 = var30;
                                CrashReport $$35 = CrashReport.forThrowable($$34, "Feature placement");
                                var10000 = $$35.addCategory("Feature");
                                Objects.requireNonNull($$33);
                                var10000.setDetail("Description", $$33::get);
                                throw new ReportedException($$35);
                            }
                        }
                    }
                }

                p_223087_.setCurrentlyGenerating((Supplier)null);
            } catch (Exception var31) {
                Exception $$36 = var31;
                CrashReport $$37 = CrashReport.forThrowable($$36, "Biome decoration");
                $$37.addCategory("Generation").setDetail("CenterX", (Object)$$3.x).setDetail("CenterZ", (Object)$$3.z).setDetail("Seed", (Object)$$10);
                throw new ReportedException($$37);
            }
        }
    }

    private static BoundingBox getWritableArea(ChunkAccess p_187718_) {
        ChunkPos $$1 = p_187718_.getPos();
        int $$2 = $$1.getMinBlockX();
        int $$3 = $$1.getMinBlockZ();
        LevelHeightAccessor $$4 = p_187718_.getHeightAccessorForGeneration();
        int $$5 = $$4.getMinBuildHeight() + 1;
        int $$6 = $$4.getMaxBuildHeight() - 1;
        return new BoundingBox($$2, $$5, $$3, $$2 + 15, $$6, $$3 + 15);
    }

    public abstract void buildSurface(WorldGenRegion var1, StructureManager var2, RandomState var3, ChunkAccess var4);

    public abstract void spawnOriginalMobs(WorldGenRegion var1);

    public int getSpawnHeight(LevelHeightAccessor p_156157_) {
        return 64;
    }

    public BiomeSource getBiomeSource() {
        return this.biomeSource;
    }

    public abstract int getGenDepth();

    public WeightedRandomList<MobSpawnSettings.SpawnerData> getMobsAt(Holder<Biome> p_223134_, StructureManager p_223135_, MobCategory p_223136_, BlockPos p_223137_) {
        Map<Structure, LongSet> $$4 = p_223135_.getAllStructuresAt(p_223137_);
        Iterator var6 = $$4.entrySet().iterator();

        while(var6.hasNext()) {
            Map.Entry<Structure, LongSet> $$5 = (Map.Entry)var6.next();
            Structure $$6 = (Structure)$$5.getKey();
            StructureSpawnOverride $$7 = (StructureSpawnOverride)$$6.spawnOverrides().get(p_223136_);
            if ($$7 != null) {
                MutableBoolean $$8 = new MutableBoolean(false);
                Predicate<StructureStart> $$9 = $$7.boundingBox() == BoundingBoxType.PIECE ? (p_223065_) -> {
                    return p_223135_.structureHasPieceAt(p_223137_, p_223065_);
                } : (p_223130_) -> {
                    return p_223130_.getBoundingBox().isInside(p_223137_);
                };
                p_223135_.fillStartsForStructure($$6, (LongSet)$$5.getValue(), (p_223220_) -> {
                    if ($$8.isFalse() && $$9.test(p_223220_)) {
                        $$8.setTrue();
                    }

                });
                if ($$8.isTrue()) {
                    return $$7.spawns();
                }
            }
        }

        return ((Biome)p_223134_.value()).getMobSettings().getMobs(p_223136_);
    }

    public void createStructures(RegistryAccess p_255835_, ChunkGeneratorStructureState p_256505_, StructureManager p_255934_, ChunkAccess p_255767_, StructureTemplateManager p_255832_) {
        ChunkPos $$5 = p_255767_.getPos();
        SectionPos $$6 = SectionPos.bottomOf(p_255767_);
        RandomState $$7 = p_256505_.randomState();
        p_256505_.possibleStructureSets().forEach((p_255564_) -> {
            StructurePlacement $$9 = ((StructureSet)p_255564_.value()).placement();
            List<StructureSet.StructureSelectionEntry> $$10 = ((StructureSet)p_255564_.value()).structures();
            Iterator var12 = $$10.iterator();

            while(var12.hasNext()) {
                StructureSet.StructureSelectionEntry $$11 = (StructureSet.StructureSelectionEntry)var12.next();
                StructureStart $$12 = p_255934_.getStartForStructure($$6, (Structure)$$11.structure().value(), p_255767_);
                if ($$12 != null && $$12.isValid()) {
                    return;
                }
            }

            if ($$9.isStructureChunk(p_256505_, $$5.x, $$5.z)) {
                if ($$10.size() == 1) {
                    this.tryGenerateStructure((StructureSet.StructureSelectionEntry)$$10.get(0), p_255934_, p_255835_, $$7, p_255832_, p_256505_.getLevelSeed(), p_255767_, $$5, $$6);
                } else {
                    ArrayList<StructureSet.StructureSelectionEntry> $$13 = new ArrayList($$10.size());
                    $$13.addAll($$10);
                    WorldgenRandom $$14 = new WorldgenRandom(new LegacyRandomSource(0L));
                    $$14.setLargeFeatureSeed(p_256505_.getLevelSeed(), $$5.x, $$5.z);
                    int $$15 = 0;

                    StructureSet.StructureSelectionEntry $$16;
                    for(Iterator var15 = $$13.iterator(); var15.hasNext(); $$15 += $$16.weight()) {
                        $$16 = (StructureSet.StructureSelectionEntry)var15.next();
                    }

                    while(!$$13.isEmpty()) {
                        int $$17 = $$14.nextInt($$15);
                        int $$18 = 0;

                        for(Iterator var17 = $$13.iterator(); var17.hasNext(); ++$$18) {
                            StructureSet.StructureSelectionEntry $$19 = (StructureSet.StructureSelectionEntry)var17.next();
                            $$17 -= $$19.weight();
                            if ($$17 < 0) {
                                break;
                            }
                        }

                        StructureSet.StructureSelectionEntry $$20 = (StructureSet.StructureSelectionEntry)$$13.get($$18);
                        if (this.tryGenerateStructure($$20, p_255934_, p_255835_, $$7, p_255832_, p_256505_.getLevelSeed(), p_255767_, $$5, $$6)) {
                            return;
                        }

                        $$13.remove($$18);
                        $$15 -= $$20.weight();
                    }

                }
            }
        });
    }

    private boolean tryGenerateStructure(StructureSet.StructureSelectionEntry p_223105_, StructureManager p_223106_, RegistryAccess p_223107_, RandomState p_223108_, StructureTemplateManager p_223109_, long p_223110_, ChunkAccess p_223111_, ChunkPos p_223112_, SectionPos p_223113_) {
        Structure $$9 = (Structure)p_223105_.structure().value();
        int $$10 = fetchReferences(p_223106_, p_223111_, p_223113_, $$9);
        HolderSet<Biome> $$11 = $$9.biomes();
        Objects.requireNonNull($$11);
        Predicate<Holder<Biome>> $$12 = $$11::contains;
        StructureStart $$13 = $$9.generate(p_223107_, this, this.biomeSource, p_223108_, p_223109_, p_223110_, p_223112_, $$10, p_223111_, $$12);
        if ($$13.isValid()) {
            p_223106_.setStartForStructure(p_223113_, $$9, $$13, p_223111_);
            return true;
        } else {
            return false;
        }
    }

    private static int fetchReferences(StructureManager p_223055_, ChunkAccess p_223056_, SectionPos p_223057_, Structure p_223058_) {
        StructureStart $$4 = p_223055_.getStartForStructure(p_223057_, p_223058_, p_223056_);
        return $$4 != null ? $$4.getReferences() : 0;
    }

    public void createReferences(WorldGenLevel p_223077_, StructureManager p_223078_, ChunkAccess p_223079_) {
        int $$3 = true;
        ChunkPos $$4 = p_223079_.getPos();
        int $$5 = $$4.x;
        int $$6 = $$4.z;
        int $$7 = $$4.getMinBlockX();
        int $$8 = $$4.getMinBlockZ();
        SectionPos $$9 = SectionPos.bottomOf(p_223079_);

        for(int $$10 = $$5 - 8; $$10 <= $$5 + 8; ++$$10) {
            for(int $$11 = $$6 - 8; $$11 <= $$6 + 8; ++$$11) {
                long $$12 = ChunkPos.asLong($$10, $$11);
                Iterator var15 = p_223077_.getChunk($$10, $$11).getAllStarts().values().iterator();

                while(var15.hasNext()) {
                    StructureStart $$13 = (StructureStart)var15.next();

                    try {
                        if ($$13.isValid() && $$13.getBoundingBox().intersects($$7, $$8, $$7 + 15, $$8 + 15)) {
                            p_223078_.addReferenceForStructure($$9, $$13.getStructure(), $$12, p_223079_);
                            DebugPackets.sendStructurePacket(p_223077_, $$13);
                        }
                    } catch (Exception var21) {
                        Exception $$14 = var21;
                        CrashReport $$15 = CrashReport.forThrowable($$14, "Generating structure reference");
                        CrashReportCategory $$16 = $$15.addCategory("Structure");
                        Optional<? extends Registry<Structure>> $$17 = p_223077_.registryAccess().registry(Registries.STRUCTURE);
                        $$16.setDetail("Id", () -> {
                            return (String)$$17.map((p_258977_) -> {
                                return p_258977_.getKey($$13.getStructure()).toString();
                            }).orElse("UNKNOWN");
                        });
                        $$16.setDetail("Name", () -> {
                            return BuiltInRegistries.STRUCTURE_TYPE.getKey($$13.getStructure().type()).toString();
                        });
                        $$16.setDetail("Class", () -> {
                            return $$13.getStructure().getClass().getCanonicalName();
                        });
                        throw new ReportedException($$15);
                    }
                }
            }
        }

    }

    public abstract CompletableFuture<ChunkAccess> fillFromNoise(Executor var1, Blender var2, RandomState var3, StructureManager var4, ChunkAccess var5);

    public abstract int getSeaLevel();

    public abstract int getMinY();

    public abstract int getBaseHeight(int var1, int var2, Heightmap.Types var3, LevelHeightAccessor var4, RandomState var5);

    public abstract NoiseColumn getBaseColumn(int var1, int var2, LevelHeightAccessor var3, RandomState var4);

    public int getFirstFreeHeight(int p_223222_, int p_223223_, Heightmap.Types p_223224_, LevelHeightAccessor p_223225_, RandomState p_223226_) {
        return this.getBaseHeight(p_223222_, p_223223_, p_223224_, p_223225_, p_223226_);
    }

    public int getFirstOccupiedHeight(int p_223236_, int p_223237_, Heightmap.Types p_223238_, LevelHeightAccessor p_223239_, RandomState p_223240_) {
        return this.getBaseHeight(p_223236_, p_223237_, p_223238_, p_223239_, p_223240_) - 1;
    }

    public abstract void addDebugScreenInfo(List<String> var1, RandomState var2, BlockPos var3);

    /** @deprecated */
    @Deprecated
    public BiomeGenerationSettings getBiomeGenerationSettings(Holder<Biome> p_223132_) {
        return (BiomeGenerationSettings)this.generationSettingsGetter.apply(p_223132_);
    }

    static {
        CODEC = BuiltInRegistries.CHUNK_GENERATOR.byNameCodec().dispatchStable(ChunkGenerator::codec, Function.identity());
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Suppliers;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.DensityFunctions.BeardifierMarker;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import org.apache.commons.lang3.mutable.MutableObject;

public class NoiseBasedChunkGenerator extends ChunkGenerator {
    public static final Codec<NoiseBasedChunkGenerator> CODEC = RecordCodecBuilder.create((p_255585_) -> {
        return p_255585_.group(BiomeSource.CODEC.fieldOf("biome_source").forGetter((p_255584_) -> {
            return p_255584_.biomeSource;
        }), NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter((p_224278_) -> {
            return p_224278_.settings;
        })).apply(p_255585_, p_255585_.stable(NoiseBasedChunkGenerator::new));
    });
    private static final BlockState AIR;
    private final Holder<NoiseGeneratorSettings> settings;
    private final Supplier<Aquifer.FluidPicker> globalFluidPicker;

    public NoiseBasedChunkGenerator(BiomeSource p_256415_, Holder<NoiseGeneratorSettings> p_256182_) {
        super(p_256415_);
        this.settings = p_256182_;
        this.globalFluidPicker = Suppliers.memoize(() -> {
            return createFluidPicker((NoiseGeneratorSettings)p_256182_.value());
        });
    }

    private static Aquifer.FluidPicker createFluidPicker(NoiseGeneratorSettings p_249264_) {
        Aquifer.FluidStatus $$1 = new Aquifer.FluidStatus(-54, Blocks.LAVA.defaultBlockState());
        int $$2 = p_249264_.seaLevel();
        Aquifer.FluidStatus $$3 = new Aquifer.FluidStatus($$2, p_249264_.defaultFluid());
        Aquifer.FluidStatus $$4 = new Aquifer.FluidStatus(DimensionType.MIN_Y * 2, Blocks.AIR.defaultBlockState());
        return (p_224274_, p_224275_, p_224276_) -> {
            return p_224275_ < Math.min(-54, $$2) ? $$1 : $$3;
        };
    }

    public CompletableFuture<ChunkAccess> createBiomes(Executor p_224298_, RandomState p_224299_, Blender p_224300_, StructureManager p_224301_, ChunkAccess p_224302_) {
        return CompletableFuture.supplyAsync(Util.wrapThreadWithTaskName("init_biomes", () -> {
            this.doCreateBiomes(p_224300_, p_224299_, p_224301_, p_224302_);
            return p_224302_;
        }), Util.backgroundExecutor());
    }

    private void doCreateBiomes(Blender p_224292_, RandomState p_224293_, StructureManager p_224294_, ChunkAccess p_224295_) {
        NoiseChunk $$4 = p_224295_.getOrCreateNoiseChunk((p_224340_) -> {
            return this.createNoiseChunk(p_224340_, p_224294_, p_224292_, p_224293_);
        });
        BiomeResolver $$5 = BelowZeroRetrogen.getBiomeResolver(p_224292_.getBiomeResolver(this.biomeSource), p_224295_);
        p_224295_.fillBiomesFromNoise($$5, $$4.cachedClimateSampler(p_224293_.router(), ((NoiseGeneratorSettings)this.settings.value()).spawnTarget()));
    }

    private NoiseChunk createNoiseChunk(ChunkAccess p_224257_, StructureManager p_224258_, Blender p_224259_, RandomState p_224260_) {
        return NoiseChunk.forChunk(p_224257_, p_224260_, Beardifier.forStructuresInChunk(p_224258_, p_224257_.getPos()), (NoiseGeneratorSettings)this.settings.value(), (Aquifer.FluidPicker)this.globalFluidPicker.get(), p_224259_);
    }

    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    public Holder<NoiseGeneratorSettings> generatorSettings() {
        return this.settings;
    }

    public boolean stable(ResourceKey<NoiseGeneratorSettings> p_224222_) {
        return this.settings.is(p_224222_);
    }

    public int getBaseHeight(int p_224216_, int p_224217_, Heightmap.Types p_224218_, LevelHeightAccessor p_224219_, RandomState p_224220_) {
        return this.iterateNoiseColumn(p_224219_, p_224220_, p_224216_, p_224217_, (MutableObject)null, p_224218_.isOpaque()).orElse(p_224219_.getMinBuildHeight());
    }

    public NoiseColumn getBaseColumn(int p_224211_, int p_224212_, LevelHeightAccessor p_224213_, RandomState p_224214_) {
        MutableObject<NoiseColumn> $$4 = new MutableObject();
        this.iterateNoiseColumn(p_224213_, p_224214_, p_224211_, p_224212_, $$4, (Predicate)null);
        return (NoiseColumn)$$4.getValue();
    }

    public void addDebugScreenInfo(List<String> p_224304_, RandomState p_224305_, BlockPos p_224306_) {
        DecimalFormat $$3 = new DecimalFormat("0.000");
        NoiseRouter $$4 = p_224305_.router();
        DensityFunction.SinglePointContext $$5 = new DensityFunction.SinglePointContext(p_224306_.getX(), p_224306_.getY(), p_224306_.getZ());
        double $$6 = $$4.ridges().compute($$5);
        String var10001 = $$3.format($$4.temperature().compute($$5));
        p_224304_.add("NoiseRouter T: " + var10001 + " V: " + $$3.format($$4.vegetation().compute($$5)) + " C: " + $$3.format($$4.continents().compute($$5)) + " E: " + $$3.format($$4.erosion().compute($$5)) + " D: " + $$3.format($$4.depth().compute($$5)) + " W: " + $$3.format($$6) + " PV: " + $$3.format((double)NoiseRouterData.peaksAndValleys((float)$$6)) + " AS: " + $$3.format($$4.initialDensityWithoutJaggedness().compute($$5)) + " N: " + $$3.format($$4.finalDensity().compute($$5)));
    }

    protected OptionalInt iterateNoiseColumn(LevelHeightAccessor p_224240_, RandomState p_224241_, int p_224242_, int p_224243_, @Nullable MutableObject<NoiseColumn> p_224244_, @Nullable Predicate<BlockState> p_224245_) {
        NoiseSettings $$6 = ((NoiseGeneratorSettings)this.settings.value()).noiseSettings().clampToHeightAccessor(p_224240_);
        int $$7 = $$6.getCellHeight();
        int $$8 = $$6.minY();
        int $$9 = Mth.floorDiv($$8, $$7);
        int $$10 = Mth.floorDiv($$6.height(), $$7);
        if ($$10 <= 0) {
            return OptionalInt.empty();
        } else {
            BlockState[] $$12;
            if (p_224244_ == null) {
                $$12 = null;
            } else {
                $$12 = new BlockState[$$6.height()];
                p_224244_.setValue(new NoiseColumn($$8, $$12));
            }

            int $$13 = $$6.getCellWidth();
            int $$14 = Math.floorDiv(p_224242_, $$13);
            int $$15 = Math.floorDiv(p_224243_, $$13);
            int $$16 = Math.floorMod(p_224242_, $$13);
            int $$17 = Math.floorMod(p_224243_, $$13);
            int $$18 = $$14 * $$13;
            int $$19 = $$15 * $$13;
            double $$20 = (double)$$16 / (double)$$13;
            double $$21 = (double)$$17 / (double)$$13;
            NoiseChunk $$22 = new NoiseChunk(1, p_224241_, $$18, $$19, $$6, BeardifierMarker.INSTANCE, (NoiseGeneratorSettings)this.settings.value(), (Aquifer.FluidPicker)this.globalFluidPicker.get(), Blender.empty());
            $$22.initializeForFirstCellX();
            $$22.advanceCellX(0);

            for(int $$23 = $$10 - 1; $$23 >= 0; --$$23) {
                $$22.selectCellYZ($$23, 0);

                for(int $$24 = $$7 - 1; $$24 >= 0; --$$24) {
                    int $$25 = ($$9 + $$23) * $$7 + $$24;
                    double $$26 = (double)$$24 / (double)$$7;
                    $$22.updateForY($$25, $$26);
                    $$22.updateForX(p_224242_, $$20);
                    $$22.updateForZ(p_224243_, $$21);
                    BlockState $$27 = $$22.getInterpolatedState();
                    BlockState $$28 = $$27 == null ? ((NoiseGeneratorSettings)this.settings.value()).defaultBlock() : $$27;
                    if ($$12 != null) {
                        int $$29 = $$23 * $$7 + $$24;
                        $$12[$$29] = $$28;
                    }

                    if (p_224245_ != null && p_224245_.test($$28)) {
                        $$22.stopInterpolation();
                        return OptionalInt.of($$25 + 1);
                    }
                }
            }

            $$22.stopInterpolation();
            return OptionalInt.empty();
        }
    }

    public void buildSurface(WorldGenRegion p_224232_, StructureManager p_224233_, RandomState p_224234_, ChunkAccess p_224235_) {
        if (!SharedConstants.debugVoidTerrain(p_224235_.getPos())) {
            WorldGenerationContext $$4 = new WorldGenerationContext(this, p_224232_);
            this.buildSurface(p_224235_, $$4, p_224234_, p_224233_, p_224232_.getBiomeManager(), p_224232_.registryAccess().registryOrThrow(Registries.BIOME), Blender.of(p_224232_));
        }
    }

    @VisibleForTesting
    public void buildSurface(ChunkAccess p_224262_, WorldGenerationContext p_224263_, RandomState p_224264_, StructureManager p_224265_, BiomeManager p_224266_, Registry<Biome> p_224267_, Blender p_224268_) {
        NoiseChunk $$7 = p_224262_.getOrCreateNoiseChunk((p_224321_) -> {
            return this.createNoiseChunk(p_224321_, p_224265_, p_224268_, p_224264_);
        });
        NoiseGeneratorSettings $$8 = (NoiseGeneratorSettings)this.settings.value();
        p_224264_.surfaceSystem().buildSurface(p_224264_, p_224266_, p_224267_, $$8.useLegacyRandomSource(), p_224263_, p_224262_, $$7, $$8.surfaceRule());
    }

    public void applyCarvers(WorldGenRegion p_224224_, long p_224225_, RandomState p_224226_, BiomeManager p_224227_, StructureManager p_224228_, ChunkAccess p_224229_, GenerationStep.Carving p_224230_) {
        BiomeManager $$7 = p_224227_.withDifferentSource((p_255581_, p_255582_, p_255583_) -> {
            return this.biomeSource.getNoiseBiome(p_255581_, p_255582_, p_255583_, p_224226_.sampler());
        });
        WorldgenRandom $$8 = new WorldgenRandom(new LegacyRandomSource(RandomSupport.generateUniqueSeed()));
        int $$9 = true;
        ChunkPos $$10 = p_224229_.getPos();
        NoiseChunk $$11 = p_224229_.getOrCreateNoiseChunk((p_224250_) -> {
            return this.createNoiseChunk(p_224250_, p_224228_, Blender.of(p_224224_), p_224226_);
        });
        Aquifer $$12 = $$11.aquifer();
        CarvingContext $$13 = new CarvingContext(this, p_224224_.registryAccess(), p_224229_.getHeightAccessorForGeneration(), $$11, p_224226_, ((NoiseGeneratorSettings)this.settings.value()).surfaceRule());
        CarvingMask $$14 = ((ProtoChunk)p_224229_).getOrCreateCarvingMask(p_224230_);

        for(int $$15 = -8; $$15 <= 8; ++$$15) {
            for(int $$16 = -8; $$16 <= 8; ++$$16) {
                ChunkPos $$17 = new ChunkPos($$10.x + $$15, $$10.z + $$16);
                ChunkAccess $$18 = p_224224_.getChunk($$17.x, $$17.z);
                BiomeGenerationSettings $$19 = $$18.carverBiome(() -> {
                    return this.getBiomeGenerationSettings(this.biomeSource.getNoiseBiome(QuartPos.fromBlock($$17.getMinBlockX()), 0, QuartPos.fromBlock($$17.getMinBlockZ()), p_224226_.sampler()));
                });
                Iterable<Holder<ConfiguredWorldCarver<?>>> $$20 = $$19.getCarvers(p_224230_);
                int $$21 = 0;

                for(Iterator var24 = $$20.iterator(); var24.hasNext(); ++$$21) {
                    Holder<ConfiguredWorldCarver<?>> $$22 = (Holder)var24.next();
                    ConfiguredWorldCarver<?> $$23 = (ConfiguredWorldCarver)$$22.value();
                    $$8.setLargeFeatureSeed(p_224225_ + (long)$$21, $$17.x, $$17.z);
                    if ($$23.isStartChunk($$8)) {
                        Objects.requireNonNull($$7);
                        $$23.carve($$13, p_224229_, $$7::getBiome, $$8, $$12, $$17, $$14);
                    }
                }
            }
        }

    }

    public CompletableFuture<ChunkAccess> fillFromNoise(Executor p_224312_, Blender p_224313_, RandomState p_224314_, StructureManager p_224315_, ChunkAccess p_224316_) {
        NoiseSettings $$5 = ((NoiseGeneratorSettings)this.settings.value()).noiseSettings().clampToHeightAccessor(p_224316_.getHeightAccessorForGeneration());
        int $$6 = $$5.minY();
        int $$7 = Mth.floorDiv($$6, $$5.getCellHeight());
        int $$8 = Mth.floorDiv($$5.height(), $$5.getCellHeight());
        if ($$8 <= 0) {
            return CompletableFuture.completedFuture(p_224316_);
        } else {
            int $$9 = p_224316_.getSectionIndex($$8 * $$5.getCellHeight() - 1 + $$6);
            int $$10 = p_224316_.getSectionIndex($$6);
            Set<LevelChunkSection> $$11 = Sets.newHashSet();

            for(int $$12 = $$9; $$12 >= $$10; --$$12) {
                LevelChunkSection $$13 = p_224316_.getSection($$12);
                $$13.acquire();
                $$11.add($$13);
            }

            return CompletableFuture.supplyAsync(Util.wrapThreadWithTaskName("wgen_fill_noise", () -> {
                return this.doFill(p_224313_, p_224315_, p_224314_, p_224316_, $$7, $$8);
            }), Util.backgroundExecutor()).whenCompleteAsync((p_224309_, p_224310_) -> {
                Iterator var3 = $$11.iterator();

                while(var3.hasNext()) {
                    LevelChunkSection $$3 = (LevelChunkSection)var3.next();
                    $$3.release();
                }

            }, p_224312_);
        }
    }

    private ChunkAccess doFill(Blender p_224285_, StructureManager p_224286_, RandomState p_224287_, ChunkAccess p_224288_, int p_224289_, int p_224290_) {
        NoiseChunk $$6 = p_224288_.getOrCreateNoiseChunk((p_224255_) -> {
            return this.createNoiseChunk(p_224255_, p_224286_, p_224285_, p_224287_);
        });
        Heightmap $$7 = p_224288_.getOrCreateHeightmapUnprimed(Types.OCEAN_FLOOR_WG);
        Heightmap $$8 = p_224288_.getOrCreateHeightmapUnprimed(Types.WORLD_SURFACE_WG);
        ChunkPos $$9 = p_224288_.getPos();
        int $$10 = $$9.getMinBlockX();
        int $$11 = $$9.getMinBlockZ();
        Aquifer $$12 = $$6.aquifer();
        $$6.initializeForFirstCellX();
        BlockPos.MutableBlockPos $$13 = new BlockPos.MutableBlockPos();
        int $$14 = $$6.cellWidth();
        int $$15 = $$6.cellHeight();
        int $$16 = 16 / $$14;
        int $$17 = 16 / $$14;

        for(int $$18 = 0; $$18 < $$16; ++$$18) {
            $$6.advanceCellX($$18);

            for(int $$19 = 0; $$19 < $$17; ++$$19) {
                int $$20 = p_224288_.getSectionsCount() - 1;
                LevelChunkSection $$21 = p_224288_.getSection($$20);

                for(int $$22 = p_224290_ - 1; $$22 >= 0; --$$22) {
                    $$6.selectCellYZ($$22, $$19);

                    for(int $$23 = $$15 - 1; $$23 >= 0; --$$23) {
                        int $$24 = (p_224289_ + $$22) * $$15 + $$23;
                        int $$25 = $$24 & 15;
                        int $$26 = p_224288_.getSectionIndex($$24);
                        if ($$20 != $$26) {
                            $$20 = $$26;
                            $$21 = p_224288_.getSection($$26);
                        }

                        double $$27 = (double)$$23 / (double)$$15;
                        $$6.updateForY($$24, $$27);

                        for(int $$28 = 0; $$28 < $$14; ++$$28) {
                            int $$29 = $$10 + $$18 * $$14 + $$28;
                            int $$30 = $$29 & 15;
                            double $$31 = (double)$$28 / (double)$$14;
                            $$6.updateForX($$29, $$31);

                            for(int $$32 = 0; $$32 < $$14; ++$$32) {
                                int $$33 = $$11 + $$19 * $$14 + $$32;
                                int $$34 = $$33 & 15;
                                double $$35 = (double)$$32 / (double)$$14;
                                $$6.updateForZ($$33, $$35);
                                BlockState $$36 = $$6.getInterpolatedState();
                                if ($$36 == null) {
                                    $$36 = ((NoiseGeneratorSettings)this.settings.value()).defaultBlock();
                                }

                                $$36 = this.debugPreliminarySurfaceLevel($$6, $$29, $$24, $$33, $$36);
                                if ($$36 != AIR && !SharedConstants.debugVoidTerrain(p_224288_.getPos())) {
                                    $$21.setBlockState($$30, $$25, $$34, $$36, false);
                                    $$7.update($$30, $$24, $$34, $$36);
                                    $$8.update($$30, $$24, $$34, $$36);
                                    if ($$12.shouldScheduleFluidUpdate() && !$$36.getFluidState().isEmpty()) {
                                        $$13.set($$29, $$24, $$33);
                                        p_224288_.markPosForPostprocessing($$13);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            $$6.swapSlices();
        }

        $$6.stopInterpolation();
        return p_224288_;
    }

    private BlockState debugPreliminarySurfaceLevel(NoiseChunk p_198232_, int p_198233_, int p_198234_, int p_198235_, BlockState p_198236_) {
        return p_198236_;
    }

    public int getGenDepth() {
        return ((NoiseGeneratorSettings)this.settings.value()).noiseSettings().height();
    }

    public int getSeaLevel() {
        return ((NoiseGeneratorSettings)this.settings.value()).seaLevel();
    }

    public int getMinY() {
        return ((NoiseGeneratorSettings)this.settings.value()).noiseSettings().minY();
    }

    public void spawnOriginalMobs(WorldGenRegion p_64379_) {
        if (!((NoiseGeneratorSettings)this.settings.value()).disableMobGeneration()) {
            ChunkPos $$1 = p_64379_.getCenter();
            Holder<Biome> $$2 = p_64379_.getBiome($$1.getWorldPosition().atY(p_64379_.getMaxBuildHeight() - 1));
            WorldgenRandom $$3 = new WorldgenRandom(new LegacyRandomSource(RandomSupport.generateUniqueSeed()));
            $$3.setDecorationSeed(p_64379_.getSeed(), $$1.getMinBlockX(), $$1.getMinBlockZ());
            NaturalSpawner.spawnMobsForChunkGeneration(p_64379_, $$2, $$1, $$3);
        }
    }

    static {
        AIR = Blocks.AIR.defaultBlockState();
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.structure.pieces;

import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

@FunctionalInterface
public interface PieceGeneratorSupplier<C extends FeatureConfiguration> {
    Optional<PieceGenerator<C>> createGenerator(Context<C> var1);

    static <C extends FeatureConfiguration> PieceGeneratorSupplier<C> simple(Predicate<Context<C>> p_197350_, PieceGenerator<C> p_197351_) {
        Optional<PieceGenerator<C>> $$2 = Optional.of(p_197351_);
        return (p_197344_) -> {
            return p_197350_.test(p_197344_) ? $$2 : Optional.empty();
        };
    }

    static <C extends FeatureConfiguration> Predicate<Context<C>> checkForBiomeOnTop(Heightmap.Types p_197346_) {
        return (p_197340_) -> {
            return p_197340_.validBiomeOnTop(p_197346_);
        };
    }

    public static record Context<C extends FeatureConfiguration>(ChunkGenerator chunkGenerator, BiomeSource biomeSource, RandomState randomState, long seed, ChunkPos chunkPos, C config, LevelHeightAccessor heightAccessor, Predicate<Holder<Biome>> validBiome, StructureTemplateManager structureTemplateManager, RegistryAccess registryAccess) {
        public Context(ChunkGenerator chunkGenerator, BiomeSource biomeSource, RandomState randomState, long seed, ChunkPos chunkPos, C config, LevelHeightAccessor heightAccessor, Predicate<Holder<Biome>> validBiome, StructureTemplateManager structureTemplateManager, RegistryAccess registryAccess) {
            this.chunkGenerator = chunkGenerator;
            this.biomeSource = biomeSource;
            this.randomState = randomState;
            this.seed = seed;
            this.chunkPos = chunkPos;
            this.config = config;
            this.heightAccessor = heightAccessor;
            this.validBiome = validBiome;
            this.structureTemplateManager = structureTemplateManager;
            this.registryAccess = registryAccess;
        }

        public boolean validBiomeOnTop(Heightmap.Types p_197381_) {
            int $$1 = this.chunkPos.getMiddleBlockX();
            int $$2 = this.chunkPos.getMiddleBlockZ();
            int $$3 = this.chunkGenerator.getFirstOccupiedHeight($$1, $$2, p_197381_, this.heightAccessor, this.randomState);
            Holder<Biome> $$4 = this.chunkGenerator.getBiomeSource().getNoiseBiome(QuartPos.fromBlock($$1), QuartPos.fromBlock($$3), QuartPos.fromBlock($$2), this.randomState.sampler());
            return this.validBiome.test($$4);
        }

        public ChunkGenerator chunkGenerator() {
            return this.chunkGenerator;
        }

        public BiomeSource biomeSource() {
            return this.biomeSource;
        }

        public RandomState randomState() {
            return this.randomState;
        }

        public long seed() {
            return this.seed;
        }

        public ChunkPos chunkPos() {
            return this.chunkPos;
        }

        public C config() {
            return this.config;
        }

        public LevelHeightAccessor heightAccessor() {
            return this.heightAccessor;
        }

        public Predicate<Holder<Biome>> validBiome() {
            return this.validBiome;
        }

        public StructureTemplateManager structureTemplateManager() {
            return this.structureTemplateManager;
        }

        public RegistryAccess registryAccess() {
            return this.registryAccess;
        }
    }
}

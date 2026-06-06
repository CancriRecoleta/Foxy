//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterLists;
import net.minecraft.world.level.biome.TheEndBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.PrimaryLevelData.SpecialWorldProperty;

public record WorldDimensions(Registry<LevelStem> dimensions) {
    public static final MapCodec<WorldDimensions> CODEC = RecordCodecBuilder.mapCodec((p_258996_) -> {
        return p_258996_.group(RegistryCodecs.fullCodec(Registries.LEVEL_STEM, Lifecycle.stable(), LevelStem.CODEC).fieldOf("dimensions").forGetter(WorldDimensions::dimensions)).apply(p_258996_, p_258996_.stable(WorldDimensions::new));
    });
    private static final Set<ResourceKey<LevelStem>> BUILTIN_ORDER;
    private static final int VANILLA_DIMENSION_COUNT;

    public WorldDimensions(Registry<LevelStem> dimensions) {
        LevelStem $$1 = (LevelStem)dimensions.get(LevelStem.OVERWORLD);
        if ($$1 == null) {
            throw new IllegalStateException("Overworld settings missing");
        } else {
            this.dimensions = dimensions;
        }
    }

    public static Stream<ResourceKey<LevelStem>> keysInOrder(Stream<ResourceKey<LevelStem>> p_251309_) {
        return Stream.concat(BUILTIN_ORDER.stream(), p_251309_.filter((p_251885_) -> {
            return !BUILTIN_ORDER.contains(p_251885_);
        }));
    }

    public WorldDimensions replaceOverworldGenerator(RegistryAccess p_251390_, ChunkGenerator p_248755_) {
        Registry<DimensionType> $$2 = p_251390_.registryOrThrow(Registries.DIMENSION_TYPE);
        Registry<LevelStem> $$3 = withOverworld($$2, this.dimensions, p_248755_);
        return new WorldDimensions($$3);
    }

    public static Registry<LevelStem> withOverworld(Registry<DimensionType> p_248853_, Registry<LevelStem> p_251908_, ChunkGenerator p_251737_) {
        LevelStem $$3 = (LevelStem)p_251908_.get(LevelStem.OVERWORLD);
        Holder<DimensionType> $$4 = $$3 == null ? p_248853_.getHolderOrThrow(BuiltinDimensionTypes.OVERWORLD) : $$3.type();
        return withOverworld(p_251908_, (Holder)$$4, p_251737_);
    }

    public static Registry<LevelStem> withOverworld(Registry<LevelStem> p_248907_, Holder<DimensionType> p_251895_, ChunkGenerator p_250220_) {
        WritableRegistry<LevelStem> $$3 = new MappedRegistry(Registries.LEVEL_STEM, Lifecycle.experimental());
        $$3.register(LevelStem.OVERWORLD, new LevelStem(p_251895_, p_250220_), Lifecycle.stable());
        Iterator var4 = p_248907_.entrySet().iterator();

        while(var4.hasNext()) {
            Map.Entry<ResourceKey<LevelStem>, LevelStem> $$4 = (Map.Entry)var4.next();
            ResourceKey<LevelStem> $$5 = (ResourceKey)$$4.getKey();
            if ($$5 != LevelStem.OVERWORLD) {
                $$3.register($$5, (LevelStem)$$4.getValue(), p_248907_.lifecycle((LevelStem)$$4.getValue()));
            }
        }

        return $$3.freeze();
    }

    public ChunkGenerator overworld() {
        LevelStem $$0 = (LevelStem)this.dimensions.get(LevelStem.OVERWORLD);
        if ($$0 == null) {
            throw new IllegalStateException("Overworld settings missing");
        } else {
            return $$0.generator();
        }
    }

    public Optional<LevelStem> get(ResourceKey<LevelStem> p_250824_) {
        return this.dimensions.getOptional(p_250824_);
    }

    public ImmutableSet<ResourceKey<Level>> levels() {
        return (ImmutableSet)this.dimensions().entrySet().stream().map(Map.Entry::getKey).map(Registries::levelStemToLevel).collect(ImmutableSet.toImmutableSet());
    }

    public boolean isDebug() {
        return this.overworld() instanceof DebugLevelSource;
    }

    private static PrimaryLevelData.SpecialWorldProperty specialWorldProperty(Registry<LevelStem> p_251549_) {
        return (PrimaryLevelData.SpecialWorldProperty)p_251549_.getOptional(LevelStem.OVERWORLD).map((p_251481_) -> {
            ChunkGenerator $$1 = p_251481_.generator();
            if ($$1 instanceof DebugLevelSource) {
                return SpecialWorldProperty.DEBUG;
            } else {
                return $$1 instanceof FlatLevelSource ? SpecialWorldProperty.FLAT : SpecialWorldProperty.NONE;
            }
        }).orElse(SpecialWorldProperty.NONE);
    }

    static Lifecycle checkStability(ResourceKey<LevelStem> p_250764_, LevelStem p_248865_) {
        return isVanillaLike(p_250764_, p_248865_) ? Lifecycle.stable() : Lifecycle.experimental();
    }

    private static boolean isVanillaLike(ResourceKey<LevelStem> p_250556_, LevelStem p_250034_) {
        if (p_250556_ == LevelStem.OVERWORLD) {
            return isStableOverworld(p_250034_);
        } else if (p_250556_ == LevelStem.NETHER) {
            return isStableNether(p_250034_);
        } else {
            return p_250556_ == LevelStem.END ? isStableEnd(p_250034_) : false;
        }
    }

    private static boolean isStableOverworld(LevelStem p_250762_) {
        Holder<DimensionType> $$1 = p_250762_.type();
        if (!$$1.is(BuiltinDimensionTypes.OVERWORLD) && !$$1.is(BuiltinDimensionTypes.OVERWORLD_CAVES)) {
            return false;
        } else {
            BiomeSource var3 = p_250762_.generator().getBiomeSource();
            if (var3 instanceof MultiNoiseBiomeSource) {
                MultiNoiseBiomeSource $$2 = (MultiNoiseBiomeSource)var3;
                if (!$$2.stable(MultiNoiseBiomeSourceParameterLists.OVERWORLD)) {
                    return false;
                }
            }

            return true;
        }
    }

    private static boolean isStableNether(LevelStem p_250497_) {
        boolean var10000;
        if (p_250497_.type().is(BuiltinDimensionTypes.NETHER)) {
            ChunkGenerator var3 = p_250497_.generator();
            if (var3 instanceof NoiseBasedChunkGenerator) {
                NoiseBasedChunkGenerator $$1 = (NoiseBasedChunkGenerator)var3;
                if ($$1.stable(NoiseGeneratorSettings.NETHER)) {
                    BiomeSource var4 = $$1.getBiomeSource();
                    if (var4 instanceof MultiNoiseBiomeSource) {
                        MultiNoiseBiomeSource $$2 = (MultiNoiseBiomeSource)var4;
                        if ($$2.stable(MultiNoiseBiomeSourceParameterLists.NETHER)) {
                            var10000 = true;
                            return var10000;
                        }
                    }
                }
            }
        }

        var10000 = false;
        return var10000;
    }

    private static boolean isStableEnd(LevelStem p_250720_) {
        boolean var10000;
        if (p_250720_.type().is(BuiltinDimensionTypes.END)) {
            ChunkGenerator var2 = p_250720_.generator();
            if (var2 instanceof NoiseBasedChunkGenerator) {
                NoiseBasedChunkGenerator $$1 = (NoiseBasedChunkGenerator)var2;
                if ($$1.stable(NoiseGeneratorSettings.END) && $$1.getBiomeSource() instanceof TheEndBiomeSource) {
                    var10000 = true;
                    return var10000;
                }
            }
        }

        var10000 = false;
        return var10000;
    }

    public Complete bake(Registry<LevelStem> p_248787_) {
        Stream<ResourceKey<LevelStem>> $$1 = Stream.concat(p_248787_.registryKeySet().stream(), this.dimensions.registryKeySet().stream()).distinct();
        List<Entry> $$2 = new ArrayList();
        keysInOrder($$1).forEach((p_248571_) -> {
            p_248787_.getOptional(p_248571_).or(() -> {
                return this.dimensions.getOptional(p_248571_);
            }).ifPresent((p_250263_) -> {
                record Entry(ResourceKey<LevelStem> key, LevelStem value) {
                    Entry(ResourceKey<LevelStem> key, LevelStem value) {
                        this.key = key;
                        this.value = value;
                    }

                    Lifecycle lifecycle() {
                        return WorldDimensions.checkStability(this.key, this.value);
                    }

                    public ResourceKey<LevelStem> key() {
                        return this.key;
                    }

                    public LevelStem value() {
                        return this.value;
                    }
                }

                $$2.add(new Entry(p_248571_, p_250263_));
            });
        });
        Lifecycle $$3 = $$2.size() == VANILLA_DIMENSION_COUNT ? Lifecycle.stable() : Lifecycle.experimental();
        WritableRegistry<LevelStem> $$4 = new MappedRegistry(Registries.LEVEL_STEM, $$3);
        $$2.forEach((p_259001_) -> {
            $$4.register(p_259001_.key, p_259001_.value, p_259001_.lifecycle());
        });
        Registry<LevelStem> $$5 = $$4.freeze();
        PrimaryLevelData.SpecialWorldProperty $$6 = specialWorldProperty($$5);
        return new Complete($$5.freeze(), $$6);
    }

    public Registry<LevelStem> dimensions() {
        return this.dimensions;
    }

    static {
        BUILTIN_ORDER = ImmutableSet.of(LevelStem.OVERWORLD, LevelStem.NETHER, LevelStem.END);
        VANILLA_DIMENSION_COUNT = BUILTIN_ORDER.size();
    }

    public static record Complete(Registry<LevelStem> dimensions, PrimaryLevelData.SpecialWorldProperty specialWorldProperty) {
        public Complete(Registry<LevelStem> dimensions, PrimaryLevelData.SpecialWorldProperty specialWorldProperty) {
            this.dimensions = dimensions;
            this.specialWorldProperty = specialWorldProperty;
        }

        public Lifecycle lifecycle() {
            return this.dimensions.registryLifecycle();
        }

        public RegistryAccess.Frozen dimensionsRegistryAccess() {
            return (new RegistryAccess.ImmutableRegistryAccess(List.of(this.dimensions))).freeze();
        }

        public Registry<LevelStem> dimensions() {
            return this.dimensions;
        }

        public PrimaryLevelData.SpecialWorldProperty specialWorldProperty() {
            return this.specialWorldProperty;
        }
    }
}

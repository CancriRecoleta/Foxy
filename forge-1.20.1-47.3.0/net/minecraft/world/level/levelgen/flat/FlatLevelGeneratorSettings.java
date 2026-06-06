//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.flat;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.LayerConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.BuiltinStructureSets;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import org.slf4j.Logger;

public class FlatLevelGeneratorSettings {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Codec<FlatLevelGeneratorSettings> CODEC = RecordCodecBuilder.create((p_209800_) -> {
        return p_209800_.group(RegistryCodecs.homogeneousList(Registries.STRUCTURE_SET).optionalFieldOf("structure_overrides").forGetter((p_209812_) -> {
            return p_209812_.structureOverrides;
        }), FlatLayerInfo.CODEC.listOf().fieldOf("layers").forGetter(FlatLevelGeneratorSettings::getLayersInfo), Codec.BOOL.fieldOf("lakes").orElse(false).forGetter((p_161912_) -> {
            return p_161912_.addLakes;
        }), Codec.BOOL.fieldOf("features").orElse(false).forGetter((p_209809_) -> {
            return p_209809_.decoration;
        }), Biome.CODEC.optionalFieldOf("biome").orElseGet(Optional::empty).forGetter((p_209807_) -> {
            return Optional.of(p_209807_.biome);
        }), RegistryOps.retrieveElement(Biomes.PLAINS), RegistryOps.retrieveElement(MiscOverworldPlacements.LAKE_LAVA_UNDERGROUND), RegistryOps.retrieveElement(MiscOverworldPlacements.LAKE_LAVA_SURFACE)).apply(p_209800_, FlatLevelGeneratorSettings::new);
    }).comapFlatMap(FlatLevelGeneratorSettings::validateHeight, Function.identity()).stable();
    private final Optional<HolderSet<StructureSet>> structureOverrides;
    private final List<FlatLayerInfo> layersInfo;
    private final Holder<Biome> biome;
    private final List<BlockState> layers;
    private boolean voidGen;
    private boolean decoration;
    private boolean addLakes;
    private final List<Holder<PlacedFeature>> lakes;

    private static DataResult<FlatLevelGeneratorSettings> validateHeight(FlatLevelGeneratorSettings p_161906_) {
        int $$1 = p_161906_.layersInfo.stream().mapToInt(FlatLayerInfo::getHeight).sum();
        return $$1 > DimensionType.Y_SIZE ? DataResult.error(() -> {
            return "Sum of layer heights is > " + DimensionType.Y_SIZE;
        }, p_161906_) : DataResult.success(p_161906_);
    }

    private FlatLevelGeneratorSettings(Optional<HolderSet<StructureSet>> p_256456_, List<FlatLayerInfo> p_255826_, boolean p_255740_, boolean p_255726_, Optional<Holder<Biome>> p_256292_, Holder.Reference<Biome> p_255964_, Holder<PlacedFeature> p_256419_, Holder<PlacedFeature> p_255710_) {
        this(p_256456_, getBiome(p_256292_, p_255964_), List.of(p_256419_, p_255710_));
        if (p_255740_) {
            this.setAddLakes();
        }

        if (p_255726_) {
            this.setDecoration();
        }

        this.layersInfo.addAll(p_255826_);
        this.updateLayers();
    }

    private static Holder<Biome> getBiome(Optional<? extends Holder<Biome>> p_256142_, Holder<Biome> p_256475_) {
        if (p_256142_.isEmpty()) {
            LOGGER.error("Unknown biome, defaulting to plains");
            return p_256475_;
        } else {
            return (Holder)p_256142_.get();
        }
    }

    public FlatLevelGeneratorSettings(Optional<HolderSet<StructureSet>> p_256029_, Holder<Biome> p_256190_, List<Holder<PlacedFeature>> p_255960_) {
        this.layersInfo = Lists.newArrayList();
        this.structureOverrides = p_256029_;
        this.biome = p_256190_;
        this.layers = Lists.newArrayList();
        this.lakes = p_255960_;
    }

    public FlatLevelGeneratorSettings withBiomeAndLayers(List<FlatLayerInfo> p_256587_, Optional<HolderSet<StructureSet>> p_256500_, Holder<Biome> p_256598_) {
        FlatLevelGeneratorSettings $$3 = new FlatLevelGeneratorSettings(p_256500_, p_256598_, this.lakes);
        Iterator var5 = p_256587_.iterator();

        while(var5.hasNext()) {
            FlatLayerInfo $$4 = (FlatLayerInfo)var5.next();
            $$3.layersInfo.add(new FlatLayerInfo($$4.getHeight(), $$4.getBlockState().getBlock()));
            $$3.updateLayers();
        }

        if (this.decoration) {
            $$3.setDecoration();
        }

        if (this.addLakes) {
            $$3.setAddLakes();
        }

        return $$3;
    }

    public void setDecoration() {
        this.decoration = true;
    }

    public void setAddLakes() {
        this.addLakes = true;
    }

    public BiomeGenerationSettings adjustGenerationSettings(Holder<Biome> p_226295_) {
        if (!p_226295_.equals(this.biome)) {
            return ((Biome)p_226295_.value()).getGenerationSettings();
        } else {
            BiomeGenerationSettings $$1 = ((Biome)this.getBiome().value()).getGenerationSettings();
            BiomeGenerationSettings.PlainBuilder $$2 = new BiomeGenerationSettings.PlainBuilder();
            if (this.addLakes) {
                Iterator var4 = this.lakes.iterator();

                while(var4.hasNext()) {
                    Holder<PlacedFeature> $$3 = (Holder)var4.next();
                    $$2.addFeature(Decoration.LAKES, $$3);
                }
            }

            boolean $$4 = (!this.voidGen || p_226295_.is(Biomes.THE_VOID)) && this.decoration;
            int $$6;
            List $$9;
            if ($$4) {
                $$9 = $$1.features();

                for($$6 = 0; $$6 < $$9.size(); ++$$6) {
                    if ($$6 != Decoration.UNDERGROUND_STRUCTURES.ordinal() && $$6 != Decoration.SURFACE_STRUCTURES.ordinal() && (!this.addLakes || $$6 != Decoration.LAKES.ordinal())) {
                        HolderSet<PlacedFeature> $$7 = (HolderSet)$$9.get($$6);
                        Iterator var8 = $$7.iterator();

                        while(var8.hasNext()) {
                            Holder<PlacedFeature> $$8 = (Holder)var8.next();
                            $$2.addFeature($$6, $$8);
                        }
                    }
                }
            }

            $$9 = this.getLayers();

            for($$6 = 0; $$6 < $$9.size(); ++$$6) {
                BlockState $$11 = (BlockState)$$9.get($$6);
                if (!Types.MOTION_BLOCKING.isOpaque().test($$11)) {
                    $$9.set($$6, (Object)null);
                    $$2.addFeature(Decoration.TOP_LAYER_MODIFICATION, PlacementUtils.inlinePlaced(Feature.FILL_LAYER, new LayerConfiguration($$6, $$11)));
                }
            }

            return $$2.build();
        }
    }

    public Optional<HolderSet<StructureSet>> structureOverrides() {
        return this.structureOverrides;
    }

    public Holder<Biome> getBiome() {
        return this.biome;
    }

    public List<FlatLayerInfo> getLayersInfo() {
        return this.layersInfo;
    }

    public List<BlockState> getLayers() {
        return this.layers;
    }

    public void updateLayers() {
        this.layers.clear();
        Iterator var1 = this.layersInfo.iterator();

        while(var1.hasNext()) {
            FlatLayerInfo $$0 = (FlatLayerInfo)var1.next();

            for(int $$1 = 0; $$1 < $$0.getHeight(); ++$$1) {
                this.layers.add($$0.getBlockState());
            }
        }

        this.voidGen = this.layers.stream().allMatch((p_209802_) -> {
            return p_209802_.is(Blocks.AIR);
        });
    }

    public static FlatLevelGeneratorSettings getDefault(HolderGetter<Biome> p_256175_, HolderGetter<StructureSet> p_256081_, HolderGetter<PlacedFeature> p_256484_) {
        HolderSet<StructureSet> $$3 = HolderSet.direct(p_256081_.getOrThrow(BuiltinStructureSets.STRONGHOLDS), p_256081_.getOrThrow(BuiltinStructureSets.VILLAGES));
        FlatLevelGeneratorSettings $$4 = new FlatLevelGeneratorSettings(Optional.of($$3), getDefaultBiome(p_256175_), createLakesList(p_256484_));
        $$4.getLayersInfo().add(new FlatLayerInfo(1, Blocks.BEDROCK));
        $$4.getLayersInfo().add(new FlatLayerInfo(2, Blocks.DIRT));
        $$4.getLayersInfo().add(new FlatLayerInfo(1, Blocks.GRASS_BLOCK));
        $$4.updateLayers();
        return $$4;
    }

    public static Holder<Biome> getDefaultBiome(HolderGetter<Biome> p_256645_) {
        return p_256645_.getOrThrow(Biomes.PLAINS);
    }

    public static List<Holder<PlacedFeature>> createLakesList(HolderGetter<PlacedFeature> p_256282_) {
        return List.of(p_256282_.getOrThrow(MiscOverworldPlacements.LAKE_LAVA_UNDERGROUND), p_256282_.getOrThrow(MiscOverworldPlacements.LAKE_LAVA_SURFACE));
    }
}

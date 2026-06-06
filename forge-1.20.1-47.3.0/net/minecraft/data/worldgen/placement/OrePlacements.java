//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.data.worldgen.placement;

import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;

public class OrePlacements {
    public static final ResourceKey<PlacedFeature> ORE_MAGMA = PlacementUtils.createKey("ore_magma");
    public static final ResourceKey<PlacedFeature> ORE_SOUL_SAND = PlacementUtils.createKey("ore_soul_sand");
    public static final ResourceKey<PlacedFeature> ORE_GOLD_DELTAS = PlacementUtils.createKey("ore_gold_deltas");
    public static final ResourceKey<PlacedFeature> ORE_QUARTZ_DELTAS = PlacementUtils.createKey("ore_quartz_deltas");
    public static final ResourceKey<PlacedFeature> ORE_GOLD_NETHER = PlacementUtils.createKey("ore_gold_nether");
    public static final ResourceKey<PlacedFeature> ORE_QUARTZ_NETHER = PlacementUtils.createKey("ore_quartz_nether");
    public static final ResourceKey<PlacedFeature> ORE_GRAVEL_NETHER = PlacementUtils.createKey("ore_gravel_nether");
    public static final ResourceKey<PlacedFeature> ORE_BLACKSTONE = PlacementUtils.createKey("ore_blackstone");
    public static final ResourceKey<PlacedFeature> ORE_DIRT = PlacementUtils.createKey("ore_dirt");
    public static final ResourceKey<PlacedFeature> ORE_GRAVEL = PlacementUtils.createKey("ore_gravel");
    public static final ResourceKey<PlacedFeature> ORE_GRANITE_UPPER = PlacementUtils.createKey("ore_granite_upper");
    public static final ResourceKey<PlacedFeature> ORE_GRANITE_LOWER = PlacementUtils.createKey("ore_granite_lower");
    public static final ResourceKey<PlacedFeature> ORE_DIORITE_UPPER = PlacementUtils.createKey("ore_diorite_upper");
    public static final ResourceKey<PlacedFeature> ORE_DIORITE_LOWER = PlacementUtils.createKey("ore_diorite_lower");
    public static final ResourceKey<PlacedFeature> ORE_ANDESITE_UPPER = PlacementUtils.createKey("ore_andesite_upper");
    public static final ResourceKey<PlacedFeature> ORE_ANDESITE_LOWER = PlacementUtils.createKey("ore_andesite_lower");
    public static final ResourceKey<PlacedFeature> ORE_TUFF = PlacementUtils.createKey("ore_tuff");
    public static final ResourceKey<PlacedFeature> ORE_COAL_UPPER = PlacementUtils.createKey("ore_coal_upper");
    public static final ResourceKey<PlacedFeature> ORE_COAL_LOWER = PlacementUtils.createKey("ore_coal_lower");
    public static final ResourceKey<PlacedFeature> ORE_IRON_UPPER = PlacementUtils.createKey("ore_iron_upper");
    public static final ResourceKey<PlacedFeature> ORE_IRON_MIDDLE = PlacementUtils.createKey("ore_iron_middle");
    public static final ResourceKey<PlacedFeature> ORE_IRON_SMALL = PlacementUtils.createKey("ore_iron_small");
    public static final ResourceKey<PlacedFeature> ORE_GOLD_EXTRA = PlacementUtils.createKey("ore_gold_extra");
    public static final ResourceKey<PlacedFeature> ORE_GOLD = PlacementUtils.createKey("ore_gold");
    public static final ResourceKey<PlacedFeature> ORE_GOLD_LOWER = PlacementUtils.createKey("ore_gold_lower");
    public static final ResourceKey<PlacedFeature> ORE_REDSTONE = PlacementUtils.createKey("ore_redstone");
    public static final ResourceKey<PlacedFeature> ORE_REDSTONE_LOWER = PlacementUtils.createKey("ore_redstone_lower");
    public static final ResourceKey<PlacedFeature> ORE_DIAMOND = PlacementUtils.createKey("ore_diamond");
    public static final ResourceKey<PlacedFeature> ORE_DIAMOND_LARGE = PlacementUtils.createKey("ore_diamond_large");
    public static final ResourceKey<PlacedFeature> ORE_DIAMOND_BURIED = PlacementUtils.createKey("ore_diamond_buried");
    public static final ResourceKey<PlacedFeature> ORE_LAPIS = PlacementUtils.createKey("ore_lapis");
    public static final ResourceKey<PlacedFeature> ORE_LAPIS_BURIED = PlacementUtils.createKey("ore_lapis_buried");
    public static final ResourceKey<PlacedFeature> ORE_INFESTED = PlacementUtils.createKey("ore_infested");
    public static final ResourceKey<PlacedFeature> ORE_EMERALD = PlacementUtils.createKey("ore_emerald");
    public static final ResourceKey<PlacedFeature> ORE_ANCIENT_DEBRIS_LARGE = PlacementUtils.createKey("ore_ancient_debris_large");
    public static final ResourceKey<PlacedFeature> ORE_ANCIENT_DEBRIS_SMALL = PlacementUtils.createKey("ore_debris_small");
    public static final ResourceKey<PlacedFeature> ORE_COPPER = PlacementUtils.createKey("ore_copper");
    public static final ResourceKey<PlacedFeature> ORE_COPPER_LARGE = PlacementUtils.createKey("ore_copper_large");
    public static final ResourceKey<PlacedFeature> ORE_CLAY = PlacementUtils.createKey("ore_clay");

    public OrePlacements() {
    }

    private static List<PlacementModifier> orePlacement(PlacementModifier p_195347_, PlacementModifier p_195348_) {
        return List.of(p_195347_, InSquarePlacement.spread(), p_195348_, BiomeFilter.biome());
    }

    private static List<PlacementModifier> commonOrePlacement(int p_195344_, PlacementModifier p_195345_) {
        return orePlacement(CountPlacement.of(p_195344_), p_195345_);
    }

    private static List<PlacementModifier> rareOrePlacement(int p_195350_, PlacementModifier p_195351_) {
        return orePlacement(RarityFilter.onAverageOnceEvery(p_195350_), p_195351_);
    }

    public static void bootstrap(BootstapContext<PlacedFeature> p_256238_) {
        HolderGetter<ConfiguredFeature<?, ?>> $$1 = p_256238_.lookup(Registries.CONFIGURED_FEATURE);
        Holder<ConfiguredFeature<?, ?>> $$2 = $$1.getOrThrow(OreFeatures.ORE_MAGMA);
        Holder<ConfiguredFeature<?, ?>> $$3 = $$1.getOrThrow(OreFeatures.ORE_SOUL_SAND);
        Holder<ConfiguredFeature<?, ?>> $$4 = $$1.getOrThrow(OreFeatures.ORE_NETHER_GOLD);
        Holder<ConfiguredFeature<?, ?>> $$5 = $$1.getOrThrow(OreFeatures.ORE_QUARTZ);
        Holder<ConfiguredFeature<?, ?>> $$6 = $$1.getOrThrow(OreFeatures.ORE_GRAVEL_NETHER);
        Holder<ConfiguredFeature<?, ?>> $$7 = $$1.getOrThrow(OreFeatures.ORE_BLACKSTONE);
        Holder<ConfiguredFeature<?, ?>> $$8 = $$1.getOrThrow(OreFeatures.ORE_DIRT);
        Holder<ConfiguredFeature<?, ?>> $$9 = $$1.getOrThrow(OreFeatures.ORE_GRAVEL);
        Holder<ConfiguredFeature<?, ?>> $$10 = $$1.getOrThrow(OreFeatures.ORE_GRANITE);
        Holder<ConfiguredFeature<?, ?>> $$11 = $$1.getOrThrow(OreFeatures.ORE_DIORITE);
        Holder<ConfiguredFeature<?, ?>> $$12 = $$1.getOrThrow(OreFeatures.ORE_ANDESITE);
        Holder<ConfiguredFeature<?, ?>> $$13 = $$1.getOrThrow(OreFeatures.ORE_TUFF);
        Holder<ConfiguredFeature<?, ?>> $$14 = $$1.getOrThrow(OreFeatures.ORE_COAL);
        Holder<ConfiguredFeature<?, ?>> $$15 = $$1.getOrThrow(OreFeatures.ORE_COAL_BURIED);
        Holder<ConfiguredFeature<?, ?>> $$16 = $$1.getOrThrow(OreFeatures.ORE_IRON);
        Holder<ConfiguredFeature<?, ?>> $$17 = $$1.getOrThrow(OreFeatures.ORE_IRON_SMALL);
        Holder<ConfiguredFeature<?, ?>> $$18 = $$1.getOrThrow(OreFeatures.ORE_GOLD);
        Holder<ConfiguredFeature<?, ?>> $$19 = $$1.getOrThrow(OreFeatures.ORE_GOLD_BURIED);
        Holder<ConfiguredFeature<?, ?>> $$20 = $$1.getOrThrow(OreFeatures.ORE_REDSTONE);
        Holder<ConfiguredFeature<?, ?>> $$21 = $$1.getOrThrow(OreFeatures.ORE_DIAMOND_SMALL);
        Holder<ConfiguredFeature<?, ?>> $$22 = $$1.getOrThrow(OreFeatures.ORE_DIAMOND_LARGE);
        Holder<ConfiguredFeature<?, ?>> $$23 = $$1.getOrThrow(OreFeatures.ORE_DIAMOND_BURIED);
        Holder<ConfiguredFeature<?, ?>> $$24 = $$1.getOrThrow(OreFeatures.ORE_LAPIS);
        Holder<ConfiguredFeature<?, ?>> $$25 = $$1.getOrThrow(OreFeatures.ORE_LAPIS_BURIED);
        Holder<ConfiguredFeature<?, ?>> $$26 = $$1.getOrThrow(OreFeatures.ORE_INFESTED);
        Holder<ConfiguredFeature<?, ?>> $$27 = $$1.getOrThrow(OreFeatures.ORE_EMERALD);
        Holder<ConfiguredFeature<?, ?>> $$28 = $$1.getOrThrow(OreFeatures.ORE_ANCIENT_DEBRIS_LARGE);
        Holder<ConfiguredFeature<?, ?>> $$29 = $$1.getOrThrow(OreFeatures.ORE_ANCIENT_DEBRIS_SMALL);
        Holder<ConfiguredFeature<?, ?>> $$30 = $$1.getOrThrow(OreFeatures.ORE_COPPPER_SMALL);
        Holder<ConfiguredFeature<?, ?>> $$31 = $$1.getOrThrow(OreFeatures.ORE_COPPER_LARGE);
        Holder<ConfiguredFeature<?, ?>> $$32 = $$1.getOrThrow(OreFeatures.ORE_CLAY);
        PlacementUtils.register(p_256238_, ORE_MAGMA, $$2, (List)commonOrePlacement(4, HeightRangePlacement.uniform(VerticalAnchor.absolute(27), VerticalAnchor.absolute(36))));
        PlacementUtils.register(p_256238_, ORE_SOUL_SAND, $$3, (List)commonOrePlacement(12, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(31))));
        PlacementUtils.register(p_256238_, ORE_GOLD_DELTAS, $$4, (List)commonOrePlacement(20, PlacementUtils.RANGE_10_10));
        PlacementUtils.register(p_256238_, ORE_QUARTZ_DELTAS, $$5, (List)commonOrePlacement(32, PlacementUtils.RANGE_10_10));
        PlacementUtils.register(p_256238_, ORE_GOLD_NETHER, $$4, (List)commonOrePlacement(10, PlacementUtils.RANGE_10_10));
        PlacementUtils.register(p_256238_, ORE_QUARTZ_NETHER, $$5, (List)commonOrePlacement(16, PlacementUtils.RANGE_10_10));
        PlacementUtils.register(p_256238_, ORE_GRAVEL_NETHER, $$6, (List)commonOrePlacement(2, HeightRangePlacement.uniform(VerticalAnchor.absolute(5), VerticalAnchor.absolute(41))));
        PlacementUtils.register(p_256238_, ORE_BLACKSTONE, $$7, (List)commonOrePlacement(2, HeightRangePlacement.uniform(VerticalAnchor.absolute(5), VerticalAnchor.absolute(31))));
        PlacementUtils.register(p_256238_, ORE_DIRT, $$8, (List)commonOrePlacement(7, HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(160))));
        PlacementUtils.register(p_256238_, ORE_GRAVEL, $$9, (List)commonOrePlacement(14, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.top())));
        PlacementUtils.register(p_256238_, ORE_GRANITE_UPPER, $$10, (List)rareOrePlacement(6, HeightRangePlacement.uniform(VerticalAnchor.absolute(64), VerticalAnchor.absolute(128))));
        PlacementUtils.register(p_256238_, ORE_GRANITE_LOWER, $$10, (List)commonOrePlacement(2, HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(60))));
        PlacementUtils.register(p_256238_, ORE_DIORITE_UPPER, $$11, (List)rareOrePlacement(6, HeightRangePlacement.uniform(VerticalAnchor.absolute(64), VerticalAnchor.absolute(128))));
        PlacementUtils.register(p_256238_, ORE_DIORITE_LOWER, $$11, (List)commonOrePlacement(2, HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(60))));
        PlacementUtils.register(p_256238_, ORE_ANDESITE_UPPER, $$12, (List)rareOrePlacement(6, HeightRangePlacement.uniform(VerticalAnchor.absolute(64), VerticalAnchor.absolute(128))));
        PlacementUtils.register(p_256238_, ORE_ANDESITE_LOWER, $$12, (List)commonOrePlacement(2, HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(60))));
        PlacementUtils.register(p_256238_, ORE_TUFF, $$13, (List)commonOrePlacement(2, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(0))));
        PlacementUtils.register(p_256238_, ORE_COAL_UPPER, $$14, (List)commonOrePlacement(30, HeightRangePlacement.uniform(VerticalAnchor.absolute(136), VerticalAnchor.top())));
        PlacementUtils.register(p_256238_, ORE_COAL_LOWER, $$15, (List)commonOrePlacement(20, HeightRangePlacement.triangle(VerticalAnchor.absolute(0), VerticalAnchor.absolute(192))));
        PlacementUtils.register(p_256238_, ORE_IRON_UPPER, $$16, (List)commonOrePlacement(90, HeightRangePlacement.triangle(VerticalAnchor.absolute(80), VerticalAnchor.absolute(384))));
        PlacementUtils.register(p_256238_, ORE_IRON_MIDDLE, $$16, (List)commonOrePlacement(10, HeightRangePlacement.triangle(VerticalAnchor.absolute(-24), VerticalAnchor.absolute(56))));
        PlacementUtils.register(p_256238_, ORE_IRON_SMALL, $$17, (List)commonOrePlacement(10, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(72))));
        PlacementUtils.register(p_256238_, ORE_GOLD_EXTRA, $$18, (List)commonOrePlacement(50, HeightRangePlacement.uniform(VerticalAnchor.absolute(32), VerticalAnchor.absolute(256))));
        PlacementUtils.register(p_256238_, ORE_GOLD, $$19, (List)commonOrePlacement(4, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(32))));
        PlacementUtils.register(p_256238_, ORE_GOLD_LOWER, $$19, (List)orePlacement(CountPlacement.of(UniformInt.of(0, 1)), HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(-48))));
        PlacementUtils.register(p_256238_, ORE_REDSTONE, $$20, (List)commonOrePlacement(4, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(15))));
        PlacementUtils.register(p_256238_, ORE_REDSTONE_LOWER, $$20, (List)commonOrePlacement(8, HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-32), VerticalAnchor.aboveBottom(32))));
        PlacementUtils.register(p_256238_, ORE_DIAMOND, $$21, (List)commonOrePlacement(7, HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-80), VerticalAnchor.aboveBottom(80))));
        PlacementUtils.register(p_256238_, ORE_DIAMOND_LARGE, $$22, (List)rareOrePlacement(9, HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-80), VerticalAnchor.aboveBottom(80))));
        PlacementUtils.register(p_256238_, ORE_DIAMOND_BURIED, $$23, (List)commonOrePlacement(4, HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-80), VerticalAnchor.aboveBottom(80))));
        PlacementUtils.register(p_256238_, ORE_LAPIS, $$24, (List)commonOrePlacement(2, HeightRangePlacement.triangle(VerticalAnchor.absolute(-32), VerticalAnchor.absolute(32))));
        PlacementUtils.register(p_256238_, ORE_LAPIS_BURIED, $$25, (List)commonOrePlacement(4, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(64))));
        PlacementUtils.register(p_256238_, ORE_INFESTED, $$26, (List)commonOrePlacement(14, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(63))));
        PlacementUtils.register(p_256238_, ORE_EMERALD, $$27, (List)commonOrePlacement(100, HeightRangePlacement.triangle(VerticalAnchor.absolute(-16), VerticalAnchor.absolute(480))));
        PlacementUtils.register(p_256238_, ORE_ANCIENT_DEBRIS_LARGE, $$28, (PlacementModifier[])(InSquarePlacement.spread(), HeightRangePlacement.triangle(VerticalAnchor.absolute(8), VerticalAnchor.absolute(24)), BiomeFilter.biome()));
        PlacementUtils.register(p_256238_, ORE_ANCIENT_DEBRIS_SMALL, $$29, (PlacementModifier[])(InSquarePlacement.spread(), PlacementUtils.RANGE_8_8, BiomeFilter.biome()));
        PlacementUtils.register(p_256238_, ORE_COPPER, $$30, (List)commonOrePlacement(16, HeightRangePlacement.triangle(VerticalAnchor.absolute(-16), VerticalAnchor.absolute(112))));
        PlacementUtils.register(p_256238_, ORE_COPPER_LARGE, $$31, (List)commonOrePlacement(16, HeightRangePlacement.triangle(VerticalAnchor.absolute(-16), VerticalAnchor.absolute(112))));
        PlacementUtils.register(p_256238_, ORE_CLAY, $$32, (List)commonOrePlacement(46, PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT));
    }
}

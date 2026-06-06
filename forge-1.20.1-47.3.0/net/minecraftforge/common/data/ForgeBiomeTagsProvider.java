//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common.data;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

public final class ForgeBiomeTagsProvider extends BiomeTagsProvider {
    public ForgeBiomeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, "forge", existingFileHelper);
    }

    protected void addTags(HolderLookup.Provider lookupProvider) {
        this.tag(Biomes.PLAINS, net.minecraftforge.common.Tags.Biomes.IS_PLAINS);
        this.tag(Biomes.DESERT, net.minecraftforge.common.Tags.Biomes.IS_HOT_OVERWORLD, net.minecraftforge.common.Tags.Biomes.IS_DRY_OVERWORLD, net.minecraftforge.common.Tags.Biomes.IS_SANDY, net.minecraftforge.common.Tags.Biomes.IS_DESERT);
        this.tag(Biomes.TAIGA, net.minecraftforge.common.Tags.Biomes.IS_COLD_OVERWORLD, net.minecraftforge.common.Tags.Biomes.IS_CONIFEROUS);
        this.tag(Biomes.SWAMP, net.minecraftforge.common.Tags.Biomes.IS_WET_OVERWORLD, net.minecraftforge.common.Tags.Biomes.IS_SWAMP);
        this.tag(Biomes.NETHER_WASTES, net.minecraftforge.common.Tags.Biomes.IS_HOT_NETHER, net.minecraftforge.common.Tags.Biomes.IS_DRY_NETHER);
        this.tag(Biomes.THE_END, net.minecraftforge.common.Tags.Biomes.IS_COLD_END, net.minecraftforge.common.Tags.Biomes.IS_DRY_END);
        this.tag(Biomes.FROZEN_OCEAN, net.minecraftforge.common.Tags.Biomes.IS_COLD_OVERWORLD, net.minecraftforge.common.Tags.Biomes.IS_SNOWY);
        this.tag(Biomes.FROZEN_RIVER, net.minecraftforge.common.Tags.Biomes.IS_COLD_OVERWORLD, net.minecraftforge.common.Tags.Biomes.IS_SNOWY);
        this.tag(Biomes.SNOWY_PLAINS, net.minecraftforge.common.Tags.Biomes.IS_COLD_OVERWORLD, net.minecraftforge.common.Tags.Biomes.IS_SNOWY, net.minecraftforge.common.Tags.Biomes.IS_WASTELAND, net.minecraftforge.common.Tags.Biomes.IS_PLAINS);
        this.tag(Biomes.MUSHROOM_FIELDS, net.minecraftforge.common.Tags.Biomes.IS_MUSHROOM, net.minecraftforge.common.Tags.Biomes.IS_RARE);
        this.tag(Biomes.JUNGLE, net.minecraftforge.common.Tags.Biomes.IS_HOT_OVERWORLD, net.minecraftforge.common.Tags.Biomes.IS_WET_OVERWORLD, net.minecraftforge.common.Tags.Biomes.IS_DENSE_OVERWORLD);
        this.tag(Biomes.SPARSE_JUNGLE, net.minecraftforge.common.Tags.Biomes.IS_HOT_OVERWORLD, net.minecraftforge.common.Tags.Biomes.IS_WET_OVERWORLD, net.minecraftforge.common.Tags.Biomes.IS_RARE);
        this.tag(Biomes.BEACH, net.minecraftforge.common.Tags.Biomes.IS_WET_OVERWORLD, net.minecraftforge.common.Tags.Biomes.IS_SANDY);
        this.tag(Biomes.SNOWY_BEACH, net.minecraftforge.common.Tags.Biomes.IS_COLD_OVERWORLD, net.minecraftforge.common.Tags.Biomes.IS_SNOWY);
        this.tag(Biomes.DARK_FOREST, net.minecraftforge.common.Tags.Biomes.IS_SPOOKY, net.minecraftforge.common.Tags.Biomes.IS_DENSE_OVERWORLD);
        this.tag(Biomes.SNOWY_TAIGA, net.minecraftforge.common.Tags.Biomes.IS_COLD_OVERWORLD, net.minecraftforge.common.Tags.Biomes.IS_CONIFEROUS, net.minecraftforge.common.Tags.Biomes.IS_SNOWY);
        this.tag(Biomes.OLD_GROWTH_PINE_TAIGA, net.minecraftforge.common.Tags.Biomes.IS_COLD_OVERWORLD, net.minecraftforge.common.Tags.Biomes.IS_CONIFEROUS);
        this.tag(Biomes.WINDSWEPT_FOREST, net.minecraftforge.common.Tags.Biomes.IS_SPARSE_OVERWORLD);
        this.tag(Biomes.SAVANNA, net.minecraftforge.common.Tags.Biomes.IS_HOT_OVERWORLD, net.minecraftforge.common.Tags.Biomes.IS_SPARSE_OVERWORLD);
        this.tag(Biomes.SAVANNA_PLATEAU, net.minecraftforge.common.Tags.Biomes.IS_HOT_OVERWORLD, net.minecraftforge.common.Tags.Biomes.IS_SPARSE_OVERWORLD, net.minecraftforge.common.Tags.Biomes.IS_RARE, net.minecraftforge.common.Tags.Biomes.IS_SLOPE, net.minecraftforge.common.Tags.Biomes.IS_PLATEAU);
        this.tag(Biomes.BADLANDS, net.minecraftforge.common.Tags.Biomes.IS_SANDY, net.minecraftforge.common.Tags.Biomes.IS_DRY_OVERWORLD);
        this.tag(Biomes.WOODED_BADLANDS, net.minecraftforge.common.Tags.Biomes.IS_SANDY, net.minecraftforge.common.Tags.Biomes.IS_DRY_OVERWORLD, net.minecraftforge.common.Tags.Biomes.IS_SPARSE_OVERWORLD, net.minecraftforge.common.Tags.Biomes.IS_SLOPE, net.minecraftforge.common.Tags.Biomes.IS_PLATEAU);
        this.tag(Biomes.MEADOW, net.minecraftforge.common.Tags.Biomes.IS_PLAINS, net.minecraftforge.common.Tags.Biomes.IS_PLATEAU, net.minecraftforge.common.Tags.Biomes.IS_SLOPE);
        this.tag(Biomes.GROVE, net.minecraftforge.common.Tags.Biomes.IS_COLD_OVERWORLD, net.minecraftforge.common.Tags.Biomes.IS_CONIFEROUS, net.minecraftforge.common.Tags.Biomes.IS_SNOWY, net.minecraftforge.common.Tags.Biomes.IS_SLOPE);
        this.tag(Biomes.SNOWY_SLOPES, net.minecraftforge.common.Tags.Biomes.IS_COLD_OVERWORLD, net.minecraftforge.common.Tags.Biomes.IS_SPARSE_OVERWORLD, net.minecraftforge.common.Tags.Biomes.IS_SNOWY, net.minecraftforge.common.Tags.Biomes.IS_SLOPE);
        this.tag(Biomes.JAGGED_PEAKS, net.minecraftforge.common.Tags.Biomes.IS_COLD_OVERWORLD, net.minecraftforge.common.Tags.Biomes.IS_SPARSE_OVERWORLD, net.minecraftforge.common.Tags.Biomes.IS_SNOWY, net.minecraftforge.common.Tags.Biomes.IS_PEAK);
        this.tag(Biomes.FROZEN_PEAKS, net.minecraftforge.common.Tags.Biomes.IS_COLD_OVERWORLD, net.minecraftforge.common.Tags.Biomes.IS_SPARSE_OVERWORLD, net.minecraftforge.common.Tags.Biomes.IS_SNOWY, net.minecraftforge.common.Tags.Biomes.IS_PEAK);
        this.tag(Biomes.STONY_PEAKS, net.minecraftforge.common.Tags.Biomes.IS_HOT_OVERWORLD, net.minecraftforge.common.Tags.Biomes.IS_PEAK);
        this.tag(Biomes.SMALL_END_ISLANDS, net.minecraftforge.common.Tags.Biomes.IS_COLD_END, net.minecraftforge.common.Tags.Biomes.IS_DRY_END);
        this.tag(Biomes.END_MIDLANDS, net.minecraftforge.common.Tags.Biomes.IS_COLD_END, net.minecraftforge.common.Tags.Biomes.IS_DRY_END);
        this.tag(Biomes.END_HIGHLANDS, net.minecraftforge.common.Tags.Biomes.IS_COLD_END, net.minecraftforge.common.Tags.Biomes.IS_DRY_END);
        this.tag(Biomes.END_BARRENS, net.minecraftforge.common.Tags.Biomes.IS_COLD_END, net.minecraftforge.common.Tags.Biomes.IS_DRY_END);
        this.tag(Biomes.WARM_OCEAN, net.minecraftforge.common.Tags.Biomes.IS_HOT_OVERWORLD);
        this.tag(Biomes.COLD_OCEAN, net.minecraftforge.common.Tags.Biomes.IS_COLD_OVERWORLD);
        this.tag(Biomes.DEEP_COLD_OCEAN, net.minecraftforge.common.Tags.Biomes.IS_COLD_OVERWORLD);
        this.tag(Biomes.DEEP_FROZEN_OCEAN, net.minecraftforge.common.Tags.Biomes.IS_COLD_OVERWORLD);
        this.tag(Biomes.THE_VOID, net.minecraftforge.common.Tags.Biomes.IS_VOID);
        this.tag(Biomes.SUNFLOWER_PLAINS, net.minecraftforge.common.Tags.Biomes.IS_PLAINS, net.minecraftforge.common.Tags.Biomes.IS_RARE);
        this.tag(Biomes.WINDSWEPT_GRAVELLY_HILLS, net.minecraftforge.common.Tags.Biomes.IS_SPARSE_OVERWORLD, net.minecraftforge.common.Tags.Biomes.IS_RARE);
        this.tag(Biomes.FLOWER_FOREST, net.minecraftforge.common.Tags.Biomes.IS_RARE);
        this.tag(Biomes.ICE_SPIKES, net.minecraftforge.common.Tags.Biomes.IS_COLD_OVERWORLD, net.minecraftforge.common.Tags.Biomes.IS_SNOWY, net.minecraftforge.common.Tags.Biomes.IS_RARE);
        this.tag(Biomes.OLD_GROWTH_BIRCH_FOREST, net.minecraftforge.common.Tags.Biomes.IS_DENSE_OVERWORLD, net.minecraftforge.common.Tags.Biomes.IS_RARE);
        this.tag(Biomes.OLD_GROWTH_SPRUCE_TAIGA, net.minecraftforge.common.Tags.Biomes.IS_DENSE_OVERWORLD, net.minecraftforge.common.Tags.Biomes.IS_RARE);
        this.tag(Biomes.WINDSWEPT_SAVANNA, net.minecraftforge.common.Tags.Biomes.IS_HOT_OVERWORLD, net.minecraftforge.common.Tags.Biomes.IS_DRY_OVERWORLD, net.minecraftforge.common.Tags.Biomes.IS_SPARSE_OVERWORLD, net.minecraftforge.common.Tags.Biomes.IS_RARE);
        this.tag(Biomes.ERODED_BADLANDS, net.minecraftforge.common.Tags.Biomes.IS_HOT_OVERWORLD, net.minecraftforge.common.Tags.Biomes.IS_DRY_OVERWORLD, net.minecraftforge.common.Tags.Biomes.IS_SPARSE_OVERWORLD, net.minecraftforge.common.Tags.Biomes.IS_RARE);
        this.tag(Biomes.BAMBOO_JUNGLE, net.minecraftforge.common.Tags.Biomes.IS_HOT_OVERWORLD, net.minecraftforge.common.Tags.Biomes.IS_WET_OVERWORLD, net.minecraftforge.common.Tags.Biomes.IS_RARE);
        this.tag(Biomes.LUSH_CAVES, net.minecraftforge.common.Tags.Biomes.IS_CAVE, net.minecraftforge.common.Tags.Biomes.IS_LUSH, net.minecraftforge.common.Tags.Biomes.IS_WET_OVERWORLD);
        this.tag(Biomes.DRIPSTONE_CAVES, net.minecraftforge.common.Tags.Biomes.IS_CAVE, net.minecraftforge.common.Tags.Biomes.IS_SPARSE_OVERWORLD);
        this.tag(Biomes.SOUL_SAND_VALLEY, net.minecraftforge.common.Tags.Biomes.IS_HOT_NETHER, net.minecraftforge.common.Tags.Biomes.IS_DRY_NETHER);
        this.tag(Biomes.CRIMSON_FOREST, net.minecraftforge.common.Tags.Biomes.IS_HOT_NETHER, net.minecraftforge.common.Tags.Biomes.IS_DRY_NETHER);
        this.tag(Biomes.WARPED_FOREST, net.minecraftforge.common.Tags.Biomes.IS_HOT_NETHER, net.minecraftforge.common.Tags.Biomes.IS_DRY_NETHER);
        this.tag(Biomes.BASALT_DELTAS, net.minecraftforge.common.Tags.Biomes.IS_HOT_NETHER, net.minecraftforge.common.Tags.Biomes.IS_DRY_NETHER);
        this.tag(Biomes.MANGROVE_SWAMP, net.minecraftforge.common.Tags.Biomes.IS_WET_OVERWORLD, net.minecraftforge.common.Tags.Biomes.IS_HOT_OVERWORLD, net.minecraftforge.common.Tags.Biomes.IS_SWAMP);
        this.tag(Biomes.DEEP_DARK, net.minecraftforge.common.Tags.Biomes.IS_CAVE, net.minecraftforge.common.Tags.Biomes.IS_RARE, net.minecraftforge.common.Tags.Biomes.IS_SPOOKY);
        this.tag(net.minecraftforge.common.Tags.Biomes.IS_HOT).addTag(net.minecraftforge.common.Tags.Biomes.IS_HOT_OVERWORLD).addTag(net.minecraftforge.common.Tags.Biomes.IS_HOT_NETHER).addOptionalTag(net.minecraftforge.common.Tags.Biomes.IS_HOT_END.location());
        this.tag(net.minecraftforge.common.Tags.Biomes.IS_COLD).addTag(net.minecraftforge.common.Tags.Biomes.IS_COLD_OVERWORLD).addOptionalTag(net.minecraftforge.common.Tags.Biomes.IS_COLD_NETHER.location()).addTag(net.minecraftforge.common.Tags.Biomes.IS_COLD_END);
        this.tag(net.minecraftforge.common.Tags.Biomes.IS_SPARSE).addTag(net.minecraftforge.common.Tags.Biomes.IS_SPARSE_OVERWORLD).addOptionalTag(net.minecraftforge.common.Tags.Biomes.IS_SPARSE_NETHER.location()).addOptionalTag(net.minecraftforge.common.Tags.Biomes.IS_SPARSE_END.location());
        this.tag(net.minecraftforge.common.Tags.Biomes.IS_DENSE).addTag(net.minecraftforge.common.Tags.Biomes.IS_DENSE_OVERWORLD).addOptionalTag(net.minecraftforge.common.Tags.Biomes.IS_DENSE_NETHER.location()).addOptionalTag(net.minecraftforge.common.Tags.Biomes.IS_DENSE_END.location());
        this.tag(net.minecraftforge.common.Tags.Biomes.IS_WET).addTag(net.minecraftforge.common.Tags.Biomes.IS_WET_OVERWORLD).addOptionalTag(net.minecraftforge.common.Tags.Biomes.IS_WET_NETHER.location()).addOptionalTag(net.minecraftforge.common.Tags.Biomes.IS_WET_END.location());
        this.tag(net.minecraftforge.common.Tags.Biomes.IS_DRY).addTag(net.minecraftforge.common.Tags.Biomes.IS_DRY_OVERWORLD).addTag(net.minecraftforge.common.Tags.Biomes.IS_DRY_NETHER).addTag(net.minecraftforge.common.Tags.Biomes.IS_DRY_END);
        this.tag(net.minecraftforge.common.Tags.Biomes.IS_WATER).addTag(BiomeTags.IS_OCEAN).addTag(BiomeTags.IS_RIVER);
        this.tag(net.minecraftforge.common.Tags.Biomes.IS_MOUNTAIN).addTag(net.minecraftforge.common.Tags.Biomes.IS_PEAK).addTag(net.minecraftforge.common.Tags.Biomes.IS_SLOPE);
        this.tag(net.minecraftforge.common.Tags.Biomes.IS_UNDERGROUND).addTag(net.minecraftforge.common.Tags.Biomes.IS_CAVE);
    }

    @SafeVarargs
    private void tag(ResourceKey<Biome> biome, TagKey<Biome>... tags) {
        TagKey[] var3 = tags;
        int var4 = tags.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            TagKey<Biome> key = var3[var5];
            this.tag(key).add(biome);
        }

    }

    public String getName() {
        return "Forge Biome Tags";
    }
}

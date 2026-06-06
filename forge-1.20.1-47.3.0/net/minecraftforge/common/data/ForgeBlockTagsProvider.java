//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common.data;

import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.Tags.Blocks;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public final class ForgeBlockTagsProvider extends BlockTagsProvider {
    public ForgeBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, "forge", existingFileHelper);
    }

    public void addTags(HolderLookup.Provider p_256380_) {
        this.tag(Blocks.BARRELS).addTag(Blocks.BARRELS_WOODEN);
        this.tag(Blocks.BARRELS_WOODEN).add((Object)net.minecraft.world.level.block.Blocks.BARREL);
        this.tag(Blocks.BOOKSHELVES).add((Object)net.minecraft.world.level.block.Blocks.BOOKSHELF);
        this.tag(Blocks.CHESTS).addTags(new TagKey[]{Blocks.CHESTS_ENDER, Blocks.CHESTS_TRAPPED, Blocks.CHESTS_WOODEN});
        this.tag(Blocks.CHESTS_ENDER).add((Object)net.minecraft.world.level.block.Blocks.ENDER_CHEST);
        this.tag(Blocks.CHESTS_TRAPPED).add((Object)net.minecraft.world.level.block.Blocks.TRAPPED_CHEST);
        this.tag(Blocks.CHESTS_WOODEN).add((Object[])(net.minecraft.world.level.block.Blocks.CHEST, net.minecraft.world.level.block.Blocks.TRAPPED_CHEST));
        this.tag(Blocks.COBBLESTONE).addTags(new TagKey[]{Blocks.COBBLESTONE_NORMAL, Blocks.COBBLESTONE_INFESTED, Blocks.COBBLESTONE_MOSSY, Blocks.COBBLESTONE_DEEPSLATE});
        this.tag(Blocks.COBBLESTONE_NORMAL).add((Object)net.minecraft.world.level.block.Blocks.COBBLESTONE);
        this.tag(Blocks.COBBLESTONE_INFESTED).add((Object)net.minecraft.world.level.block.Blocks.INFESTED_COBBLESTONE);
        this.tag(Blocks.COBBLESTONE_MOSSY).add((Object)net.minecraft.world.level.block.Blocks.MOSSY_COBBLESTONE);
        this.tag(Blocks.COBBLESTONE_DEEPSLATE).add((Object)net.minecraft.world.level.block.Blocks.COBBLED_DEEPSLATE);
        this.tag(Blocks.END_STONES).add((Object)net.minecraft.world.level.block.Blocks.END_STONE);
        this.tag(Blocks.ENDERMAN_PLACE_ON_BLACKLIST);
        this.tag(Blocks.FENCE_GATES).addTags(new TagKey[]{Blocks.FENCE_GATES_WOODEN});
        this.tag(Blocks.FENCE_GATES_WOODEN).add((Object[])(net.minecraft.world.level.block.Blocks.OAK_FENCE_GATE, net.minecraft.world.level.block.Blocks.SPRUCE_FENCE_GATE, net.minecraft.world.level.block.Blocks.BIRCH_FENCE_GATE, net.minecraft.world.level.block.Blocks.JUNGLE_FENCE_GATE, net.minecraft.world.level.block.Blocks.ACACIA_FENCE_GATE, net.minecraft.world.level.block.Blocks.DARK_OAK_FENCE_GATE, net.minecraft.world.level.block.Blocks.CRIMSON_FENCE_GATE, net.minecraft.world.level.block.Blocks.WARPED_FENCE_GATE, net.minecraft.world.level.block.Blocks.MANGROVE_FENCE_GATE, net.minecraft.world.level.block.Blocks.BAMBOO_FENCE_GATE, net.minecraft.world.level.block.Blocks.CHERRY_FENCE_GATE));
        this.tag(Blocks.FENCES).addTags(new TagKey[]{Blocks.FENCES_NETHER_BRICK, Blocks.FENCES_WOODEN});
        this.tag(Blocks.FENCES_NETHER_BRICK).add((Object)net.minecraft.world.level.block.Blocks.NETHER_BRICK_FENCE);
        this.tag(Blocks.FENCES_WOODEN).addTag(BlockTags.WOODEN_FENCES);
        this.tag(Blocks.GLASS).addTags(new TagKey[]{Blocks.GLASS_COLORLESS, Blocks.STAINED_GLASS, Blocks.GLASS_TINTED});
        this.tag(Blocks.GLASS_COLORLESS).add((Object)net.minecraft.world.level.block.Blocks.GLASS);
        this.tag(Blocks.GLASS_SILICA).add((Object[])(net.minecraft.world.level.block.Blocks.GLASS, net.minecraft.world.level.block.Blocks.BLACK_STAINED_GLASS, net.minecraft.world.level.block.Blocks.BLUE_STAINED_GLASS, net.minecraft.world.level.block.Blocks.BROWN_STAINED_GLASS, net.minecraft.world.level.block.Blocks.CYAN_STAINED_GLASS, net.minecraft.world.level.block.Blocks.GRAY_STAINED_GLASS, net.minecraft.world.level.block.Blocks.GREEN_STAINED_GLASS, net.minecraft.world.level.block.Blocks.LIGHT_BLUE_STAINED_GLASS, net.minecraft.world.level.block.Blocks.LIGHT_GRAY_STAINED_GLASS, net.minecraft.world.level.block.Blocks.LIME_STAINED_GLASS, net.minecraft.world.level.block.Blocks.MAGENTA_STAINED_GLASS, net.minecraft.world.level.block.Blocks.ORANGE_STAINED_GLASS, net.minecraft.world.level.block.Blocks.PINK_STAINED_GLASS, net.minecraft.world.level.block.Blocks.PURPLE_STAINED_GLASS, net.minecraft.world.level.block.Blocks.RED_STAINED_GLASS, net.minecraft.world.level.block.Blocks.WHITE_STAINED_GLASS, net.minecraft.world.level.block.Blocks.YELLOW_STAINED_GLASS));
        this.tag(Blocks.GLASS_TINTED).add((Object)net.minecraft.world.level.block.Blocks.TINTED_GLASS);
        IntrinsicHolderTagsProvider.IntrinsicTagAppender var10001 = this.tag(Blocks.STAINED_GLASS);
        Objects.requireNonNull(var10001);
        this.addColored(var10001::add, Blocks.GLASS, "{color}_stained_glass");
        this.tag(Blocks.GLASS_PANES).addTags(new TagKey[]{Blocks.GLASS_PANES_COLORLESS, Blocks.STAINED_GLASS_PANES});
        this.tag(Blocks.GLASS_PANES_COLORLESS).add((Object)net.minecraft.world.level.block.Blocks.GLASS_PANE);
        var10001 = this.tag(Blocks.STAINED_GLASS_PANES);
        Objects.requireNonNull(var10001);
        this.addColored(var10001::add, Blocks.GLASS_PANES, "{color}_stained_glass_pane");
        this.tag(Blocks.GRAVEL).add((Object)net.minecraft.world.level.block.Blocks.GRAVEL);
        this.tag(Blocks.NETHERRACK).add((Object)net.minecraft.world.level.block.Blocks.NETHERRACK);
        this.tag(Blocks.OBSIDIAN).add((Object)net.minecraft.world.level.block.Blocks.OBSIDIAN);
        this.tag(Blocks.ORE_BEARING_GROUND_DEEPSLATE).add((Object)net.minecraft.world.level.block.Blocks.DEEPSLATE);
        this.tag(Blocks.ORE_BEARING_GROUND_NETHERRACK).add((Object)net.minecraft.world.level.block.Blocks.NETHERRACK);
        this.tag(Blocks.ORE_BEARING_GROUND_STONE).add((Object)net.minecraft.world.level.block.Blocks.STONE);
        this.tag(Blocks.ORE_RATES_DENSE).add((Object[])(net.minecraft.world.level.block.Blocks.COPPER_ORE, net.minecraft.world.level.block.Blocks.DEEPSLATE_COPPER_ORE, net.minecraft.world.level.block.Blocks.DEEPSLATE_LAPIS_ORE, net.minecraft.world.level.block.Blocks.DEEPSLATE_REDSTONE_ORE, net.minecraft.world.level.block.Blocks.LAPIS_ORE, net.minecraft.world.level.block.Blocks.REDSTONE_ORE));
        this.tag(Blocks.ORE_RATES_SINGULAR).add((Object[])(net.minecraft.world.level.block.Blocks.ANCIENT_DEBRIS, net.minecraft.world.level.block.Blocks.COAL_ORE, net.minecraft.world.level.block.Blocks.DEEPSLATE_COAL_ORE, net.minecraft.world.level.block.Blocks.DEEPSLATE_DIAMOND_ORE, net.minecraft.world.level.block.Blocks.DEEPSLATE_EMERALD_ORE, net.minecraft.world.level.block.Blocks.DEEPSLATE_GOLD_ORE, net.minecraft.world.level.block.Blocks.DEEPSLATE_IRON_ORE, net.minecraft.world.level.block.Blocks.DIAMOND_ORE, net.minecraft.world.level.block.Blocks.EMERALD_ORE, net.minecraft.world.level.block.Blocks.GOLD_ORE, net.minecraft.world.level.block.Blocks.IRON_ORE, net.minecraft.world.level.block.Blocks.NETHER_QUARTZ_ORE));
        this.tag(Blocks.ORE_RATES_SPARSE).add((Object)net.minecraft.world.level.block.Blocks.NETHER_GOLD_ORE);
        this.tag(Blocks.ORES).addTags(new TagKey[]{Blocks.ORES_COAL, Blocks.ORES_COPPER, Blocks.ORES_DIAMOND, Blocks.ORES_EMERALD, Blocks.ORES_GOLD, Blocks.ORES_IRON, Blocks.ORES_LAPIS, Blocks.ORES_REDSTONE, Blocks.ORES_QUARTZ, Blocks.ORES_NETHERITE_SCRAP});
        this.tag(Blocks.ORES_COAL).addTag(BlockTags.COAL_ORES);
        this.tag(Blocks.ORES_COPPER).addTag(BlockTags.COPPER_ORES);
        this.tag(Blocks.ORES_DIAMOND).addTag(BlockTags.DIAMOND_ORES);
        this.tag(Blocks.ORES_EMERALD).addTag(BlockTags.EMERALD_ORES);
        this.tag(Blocks.ORES_GOLD).addTag(BlockTags.GOLD_ORES);
        this.tag(Blocks.ORES_IRON).addTag(BlockTags.IRON_ORES);
        this.tag(Blocks.ORES_LAPIS).addTag(BlockTags.LAPIS_ORES);
        this.tag(Blocks.ORES_QUARTZ).add((Object)net.minecraft.world.level.block.Blocks.NETHER_QUARTZ_ORE);
        this.tag(Blocks.ORES_REDSTONE).addTag(BlockTags.REDSTONE_ORES);
        this.tag(Blocks.ORES_NETHERITE_SCRAP).add((Object)net.minecraft.world.level.block.Blocks.ANCIENT_DEBRIS);
        this.tag(Blocks.ORES_IN_GROUND_DEEPSLATE).add((Object[])(net.minecraft.world.level.block.Blocks.DEEPSLATE_COAL_ORE, net.minecraft.world.level.block.Blocks.DEEPSLATE_COPPER_ORE, net.minecraft.world.level.block.Blocks.DEEPSLATE_DIAMOND_ORE, net.minecraft.world.level.block.Blocks.DEEPSLATE_EMERALD_ORE, net.minecraft.world.level.block.Blocks.DEEPSLATE_GOLD_ORE, net.minecraft.world.level.block.Blocks.DEEPSLATE_IRON_ORE, net.minecraft.world.level.block.Blocks.DEEPSLATE_LAPIS_ORE, net.minecraft.world.level.block.Blocks.DEEPSLATE_REDSTONE_ORE));
        this.tag(Blocks.ORES_IN_GROUND_NETHERRACK).add((Object[])(net.minecraft.world.level.block.Blocks.NETHER_GOLD_ORE, net.minecraft.world.level.block.Blocks.NETHER_QUARTZ_ORE));
        this.tag(Blocks.ORES_IN_GROUND_STONE).add((Object[])(net.minecraft.world.level.block.Blocks.COAL_ORE, net.minecraft.world.level.block.Blocks.COPPER_ORE, net.minecraft.world.level.block.Blocks.DIAMOND_ORE, net.minecraft.world.level.block.Blocks.EMERALD_ORE, net.minecraft.world.level.block.Blocks.GOLD_ORE, net.minecraft.world.level.block.Blocks.IRON_ORE, net.minecraft.world.level.block.Blocks.LAPIS_ORE, net.minecraft.world.level.block.Blocks.REDSTONE_ORE));
        this.tag(Blocks.SAND).addTags(new TagKey[]{Blocks.SAND_COLORLESS, Blocks.SAND_RED});
        this.tag(Blocks.SAND_COLORLESS).add((Object)net.minecraft.world.level.block.Blocks.SAND);
        this.tag(Blocks.SAND_RED).add((Object)net.minecraft.world.level.block.Blocks.RED_SAND);
        this.tag(Blocks.SANDSTONE).add((Object[])(net.minecraft.world.level.block.Blocks.SANDSTONE, net.minecraft.world.level.block.Blocks.CUT_SANDSTONE, net.minecraft.world.level.block.Blocks.CHISELED_SANDSTONE, net.minecraft.world.level.block.Blocks.SMOOTH_SANDSTONE, net.minecraft.world.level.block.Blocks.RED_SANDSTONE, net.minecraft.world.level.block.Blocks.CUT_RED_SANDSTONE, net.minecraft.world.level.block.Blocks.CHISELED_RED_SANDSTONE, net.minecraft.world.level.block.Blocks.SMOOTH_RED_SANDSTONE));
        this.tag(Blocks.STONE).add((Object[])(net.minecraft.world.level.block.Blocks.ANDESITE, net.minecraft.world.level.block.Blocks.DIORITE, net.minecraft.world.level.block.Blocks.GRANITE, net.minecraft.world.level.block.Blocks.INFESTED_STONE, net.minecraft.world.level.block.Blocks.STONE, net.minecraft.world.level.block.Blocks.POLISHED_ANDESITE, net.minecraft.world.level.block.Blocks.POLISHED_DIORITE, net.minecraft.world.level.block.Blocks.POLISHED_GRANITE, net.minecraft.world.level.block.Blocks.DEEPSLATE, net.minecraft.world.level.block.Blocks.POLISHED_DEEPSLATE, net.minecraft.world.level.block.Blocks.INFESTED_DEEPSLATE, net.minecraft.world.level.block.Blocks.TUFF));
        this.tag(Blocks.STORAGE_BLOCKS).addTags(new TagKey[]{Blocks.STORAGE_BLOCKS_AMETHYST, Blocks.STORAGE_BLOCKS_COAL, Blocks.STORAGE_BLOCKS_COPPER, Blocks.STORAGE_BLOCKS_DIAMOND, Blocks.STORAGE_BLOCKS_EMERALD, Blocks.STORAGE_BLOCKS_GOLD, Blocks.STORAGE_BLOCKS_IRON, Blocks.STORAGE_BLOCKS_LAPIS, Blocks.STORAGE_BLOCKS_QUARTZ, Blocks.STORAGE_BLOCKS_RAW_COPPER, Blocks.STORAGE_BLOCKS_RAW_GOLD, Blocks.STORAGE_BLOCKS_RAW_IRON, Blocks.STORAGE_BLOCKS_REDSTONE, Blocks.STORAGE_BLOCKS_NETHERITE});
        this.tag(Blocks.STORAGE_BLOCKS_AMETHYST).add((Object)net.minecraft.world.level.block.Blocks.AMETHYST_BLOCK);
        this.tag(Blocks.STORAGE_BLOCKS_COAL).add((Object)net.minecraft.world.level.block.Blocks.COAL_BLOCK);
        this.tag(Blocks.STORAGE_BLOCKS_COPPER).add((Object)net.minecraft.world.level.block.Blocks.COPPER_BLOCK);
        this.tag(Blocks.STORAGE_BLOCKS_DIAMOND).add((Object)net.minecraft.world.level.block.Blocks.DIAMOND_BLOCK);
        this.tag(Blocks.STORAGE_BLOCKS_EMERALD).add((Object)net.minecraft.world.level.block.Blocks.EMERALD_BLOCK);
        this.tag(Blocks.STORAGE_BLOCKS_GOLD).add((Object)net.minecraft.world.level.block.Blocks.GOLD_BLOCK);
        this.tag(Blocks.STORAGE_BLOCKS_IRON).add((Object)net.minecraft.world.level.block.Blocks.IRON_BLOCK);
        this.tag(Blocks.STORAGE_BLOCKS_LAPIS).add((Object)net.minecraft.world.level.block.Blocks.LAPIS_BLOCK);
        this.tag(Blocks.STORAGE_BLOCKS_QUARTZ).add((Object)net.minecraft.world.level.block.Blocks.QUARTZ_BLOCK);
        this.tag(Blocks.STORAGE_BLOCKS_RAW_COPPER).add((Object)net.minecraft.world.level.block.Blocks.RAW_COPPER_BLOCK);
        this.tag(Blocks.STORAGE_BLOCKS_RAW_GOLD).add((Object)net.minecraft.world.level.block.Blocks.RAW_GOLD_BLOCK);
        this.tag(Blocks.STORAGE_BLOCKS_RAW_IRON).add((Object)net.minecraft.world.level.block.Blocks.RAW_IRON_BLOCK);
        this.tag(Blocks.STORAGE_BLOCKS_REDSTONE).add((Object)net.minecraft.world.level.block.Blocks.REDSTONE_BLOCK);
        this.tag(Blocks.STORAGE_BLOCKS_NETHERITE).add((Object)net.minecraft.world.level.block.Blocks.NETHERITE_BLOCK);
    }

    private void addColored(Consumer<Block> consumer, TagKey<Block> group, String pattern) {
        String var10000 = group.location().getPath();
        String prefix = var10000.toUpperCase(Locale.ENGLISH) + "_";
        DyeColor[] var5 = DyeColor.values();
        int var6 = var5.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            DyeColor color = var5[var7];
            ResourceLocation key = new ResourceLocation("minecraft", pattern.replace("{color}", color.getName()));
            TagKey<Block> tag = this.getForgeTag(prefix + color.getName());
            Block block = (Block)ForgeRegistries.BLOCKS.getValue(key);
            if (block == null || block == net.minecraft.world.level.block.Blocks.AIR) {
                throw new IllegalStateException("Unknown vanilla block: " + key.toString());
            }

            this.tag(tag).add((Object)block);
            consumer.accept(block);
        }

    }

    private TagKey<Block> getForgeTag(String name) {
        try {
            name = name.toUpperCase(Locale.ENGLISH);
            return (TagKey)Tags.Blocks.class.getDeclaredField(name).get((Object)null);
        } catch (IllegalAccessException | NoSuchFieldException | SecurityException | IllegalArgumentException var3) {
            String var10002 = Tags.Blocks.class.getName();
            throw new IllegalStateException(var10002 + " is missing tag name: " + name);
        }
    }

    public String getName() {
        return "Forge Block Tags";
    }
}

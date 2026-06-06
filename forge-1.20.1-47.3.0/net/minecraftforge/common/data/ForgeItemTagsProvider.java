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
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.Tags.Blocks;
import net.minecraftforge.common.Tags.Items;
import net.minecraftforge.registries.ForgeRegistries;

public final class ForgeItemTagsProvider extends ItemTagsProvider {
    public ForgeItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagsProvider.TagLookup<Block>> blockTagProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTagProvider, "forge", existingFileHelper);
    }

    public void addTags(HolderLookup.Provider lookupProvider) {
        this.copy(Blocks.BARRELS, Items.BARRELS);
        this.copy(Blocks.BARRELS_WOODEN, Items.BARRELS_WOODEN);
        this.tag(Items.BONES).add((Object)net.minecraft.world.item.Items.BONE);
        this.copy(Blocks.BOOKSHELVES, Items.BOOKSHELVES);
        this.copy(Blocks.CHESTS, Items.CHESTS);
        this.copy(Blocks.CHESTS_ENDER, Items.CHESTS_ENDER);
        this.copy(Blocks.CHESTS_TRAPPED, Items.CHESTS_TRAPPED);
        this.copy(Blocks.CHESTS_WOODEN, Items.CHESTS_WOODEN);
        this.copy(Blocks.COBBLESTONE, Items.COBBLESTONE);
        this.copy(Blocks.COBBLESTONE_NORMAL, Items.COBBLESTONE_NORMAL);
        this.copy(Blocks.COBBLESTONE_INFESTED, Items.COBBLESTONE_INFESTED);
        this.copy(Blocks.COBBLESTONE_MOSSY, Items.COBBLESTONE_MOSSY);
        this.copy(Blocks.COBBLESTONE_DEEPSLATE, Items.COBBLESTONE_DEEPSLATE);
        this.tag(Items.CROPS).addTags(new TagKey[]{Items.CROPS_BEETROOT, Items.CROPS_CARROT, Items.CROPS_NETHER_WART, Items.CROPS_POTATO, Items.CROPS_WHEAT});
        this.tag(Items.CROPS_BEETROOT).add((Object)net.minecraft.world.item.Items.BEETROOT);
        this.tag(Items.CROPS_CARROT).add((Object)net.minecraft.world.item.Items.CARROT);
        this.tag(Items.CROPS_NETHER_WART).add((Object)net.minecraft.world.item.Items.NETHER_WART);
        this.tag(Items.CROPS_POTATO).add((Object)net.minecraft.world.item.Items.POTATO);
        this.tag(Items.CROPS_WHEAT).add((Object)net.minecraft.world.item.Items.WHEAT);
        this.tag(Items.DUSTS).addTags(new TagKey[]{Items.DUSTS_GLOWSTONE, Items.DUSTS_PRISMARINE, Items.DUSTS_REDSTONE});
        this.tag(Items.DUSTS_GLOWSTONE).add((Object)net.minecraft.world.item.Items.GLOWSTONE_DUST);
        this.tag(Items.DUSTS_PRISMARINE).add((Object)net.minecraft.world.item.Items.PRISMARINE_SHARD);
        this.tag(Items.DUSTS_REDSTONE).add((Object)net.minecraft.world.item.Items.REDSTONE);
        IntrinsicHolderTagsProvider.IntrinsicTagAppender var10001 = this.tag(Items.DYES);
        Objects.requireNonNull(var10001);
        this.addColored((xva$0) -> {
            var10001.addTags(new TagKey[]{xva$0});
        }, Items.DYES, "{color}_dye");
        this.tag(Items.EGGS).add((Object)net.minecraft.world.item.Items.EGG);
        this.tag(Items.ENCHANTING_FUELS).addTag(Items.GEMS_LAPIS);
        this.copy(Blocks.END_STONES, Items.END_STONES);
        this.tag(Items.ENDER_PEARLS).add((Object)net.minecraft.world.item.Items.ENDER_PEARL);
        this.tag(Items.FEATHERS).add((Object)net.minecraft.world.item.Items.FEATHER);
        this.copy(Blocks.FENCE_GATES, Items.FENCE_GATES);
        this.copy(Blocks.FENCE_GATES_WOODEN, Items.FENCE_GATES_WOODEN);
        this.copy(Blocks.FENCES, Items.FENCES);
        this.copy(Blocks.FENCES_NETHER_BRICK, Items.FENCES_NETHER_BRICK);
        this.copy(Blocks.FENCES_WOODEN, Items.FENCES_WOODEN);
        this.tag(Items.GEMS).addTags(new TagKey[]{Items.GEMS_AMETHYST, Items.GEMS_DIAMOND, Items.GEMS_EMERALD, Items.GEMS_LAPIS, Items.GEMS_PRISMARINE, Items.GEMS_QUARTZ});
        this.tag(Items.GEMS_AMETHYST).add((Object)net.minecraft.world.item.Items.AMETHYST_SHARD);
        this.tag(Items.GEMS_DIAMOND).add((Object)net.minecraft.world.item.Items.DIAMOND);
        this.tag(Items.GEMS_EMERALD).add((Object)net.minecraft.world.item.Items.EMERALD);
        this.tag(Items.GEMS_LAPIS).add((Object)net.minecraft.world.item.Items.LAPIS_LAZULI);
        this.tag(Items.GEMS_PRISMARINE).add((Object)net.minecraft.world.item.Items.PRISMARINE_CRYSTALS);
        this.tag(Items.GEMS_QUARTZ).add((Object)net.minecraft.world.item.Items.QUARTZ);
        this.copy(Blocks.GLASS, Items.GLASS);
        this.copy(Blocks.GLASS_TINTED, Items.GLASS_TINTED);
        this.copy(Blocks.GLASS_SILICA, Items.GLASS_SILICA);
        this.copyColored(Blocks.GLASS, Items.GLASS);
        this.copy(Blocks.GLASS_PANES, Items.GLASS_PANES);
        this.copyColored(Blocks.GLASS_PANES, Items.GLASS_PANES);
        this.copy(Blocks.GRAVEL, Items.GRAVEL);
        this.tag(Items.GUNPOWDER).add((Object)net.minecraft.world.item.Items.GUNPOWDER);
        this.tag(Items.HEADS).add((Object[])(net.minecraft.world.item.Items.CREEPER_HEAD, net.minecraft.world.item.Items.DRAGON_HEAD, net.minecraft.world.item.Items.PLAYER_HEAD, net.minecraft.world.item.Items.SKELETON_SKULL, net.minecraft.world.item.Items.WITHER_SKELETON_SKULL, net.minecraft.world.item.Items.ZOMBIE_HEAD));
        this.tag(Items.INGOTS).addTags(new TagKey[]{Items.INGOTS_BRICK, Items.INGOTS_COPPER, Items.INGOTS_GOLD, Items.INGOTS_IRON, Items.INGOTS_NETHERITE, Items.INGOTS_NETHER_BRICK});
        this.tag(Items.INGOTS_BRICK).add((Object)net.minecraft.world.item.Items.BRICK);
        this.tag(Items.INGOTS_COPPER).add((Object)net.minecraft.world.item.Items.COPPER_INGOT);
        this.tag(Items.INGOTS_GOLD).add((Object)net.minecraft.world.item.Items.GOLD_INGOT);
        this.tag(Items.INGOTS_IRON).add((Object)net.minecraft.world.item.Items.IRON_INGOT);
        this.tag(Items.INGOTS_NETHERITE).add((Object)net.minecraft.world.item.Items.NETHERITE_INGOT);
        this.tag(Items.INGOTS_NETHER_BRICK).add((Object)net.minecraft.world.item.Items.NETHER_BRICK);
        this.tag(Items.LEATHER).add((Object)net.minecraft.world.item.Items.LEATHER);
        this.tag(Items.MUSHROOMS).add((Object[])(net.minecraft.world.item.Items.BROWN_MUSHROOM, net.minecraft.world.item.Items.RED_MUSHROOM));
        this.tag(Items.NETHER_STARS).add((Object)net.minecraft.world.item.Items.NETHER_STAR);
        this.copy(Blocks.NETHERRACK, Items.NETHERRACK);
        this.tag(Items.NUGGETS).addTags(new TagKey[]{Items.NUGGETS_IRON, Items.NUGGETS_GOLD});
        this.tag(Items.NUGGETS_IRON).add((Object)net.minecraft.world.item.Items.IRON_NUGGET);
        this.tag(Items.NUGGETS_GOLD).add((Object)net.minecraft.world.item.Items.GOLD_NUGGET);
        this.copy(Blocks.OBSIDIAN, Items.OBSIDIAN);
        this.copy(Blocks.ORE_BEARING_GROUND_DEEPSLATE, Items.ORE_BEARING_GROUND_DEEPSLATE);
        this.copy(Blocks.ORE_BEARING_GROUND_NETHERRACK, Items.ORE_BEARING_GROUND_NETHERRACK);
        this.copy(Blocks.ORE_BEARING_GROUND_STONE, Items.ORE_BEARING_GROUND_STONE);
        this.copy(Blocks.ORE_RATES_DENSE, Items.ORE_RATES_DENSE);
        this.copy(Blocks.ORE_RATES_SINGULAR, Items.ORE_RATES_SINGULAR);
        this.copy(Blocks.ORE_RATES_SPARSE, Items.ORE_RATES_SPARSE);
        this.copy(Blocks.ORES, Items.ORES);
        this.copy(Blocks.ORES_COAL, Items.ORES_COAL);
        this.copy(Blocks.ORES_COPPER, Items.ORES_COPPER);
        this.copy(Blocks.ORES_DIAMOND, Items.ORES_DIAMOND);
        this.copy(Blocks.ORES_EMERALD, Items.ORES_EMERALD);
        this.copy(Blocks.ORES_GOLD, Items.ORES_GOLD);
        this.copy(Blocks.ORES_IRON, Items.ORES_IRON);
        this.copy(Blocks.ORES_LAPIS, Items.ORES_LAPIS);
        this.copy(Blocks.ORES_QUARTZ, Items.ORES_QUARTZ);
        this.copy(Blocks.ORES_REDSTONE, Items.ORES_REDSTONE);
        this.copy(Blocks.ORES_NETHERITE_SCRAP, Items.ORES_NETHERITE_SCRAP);
        this.copy(Blocks.ORES_IN_GROUND_DEEPSLATE, Items.ORES_IN_GROUND_DEEPSLATE);
        this.copy(Blocks.ORES_IN_GROUND_NETHERRACK, Items.ORES_IN_GROUND_NETHERRACK);
        this.copy(Blocks.ORES_IN_GROUND_STONE, Items.ORES_IN_GROUND_STONE);
        this.tag(Items.RAW_MATERIALS).addTags(new TagKey[]{Items.RAW_MATERIALS_COPPER, Items.RAW_MATERIALS_GOLD, Items.RAW_MATERIALS_IRON});
        this.tag(Items.RAW_MATERIALS_COPPER).add((Object)net.minecraft.world.item.Items.RAW_COPPER);
        this.tag(Items.RAW_MATERIALS_GOLD).add((Object)net.minecraft.world.item.Items.RAW_GOLD);
        this.tag(Items.RAW_MATERIALS_IRON).add((Object)net.minecraft.world.item.Items.RAW_IRON);
        this.tag(Items.RODS).addTags(new TagKey[]{Items.RODS_BLAZE, Items.RODS_WOODEN});
        this.tag(Items.RODS_BLAZE).add((Object)net.minecraft.world.item.Items.BLAZE_ROD);
        this.tag(Items.RODS_WOODEN).add((Object)net.minecraft.world.item.Items.STICK);
        this.copy(Blocks.SAND, Items.SAND);
        this.copy(Blocks.SAND_COLORLESS, Items.SAND_COLORLESS);
        this.copy(Blocks.SAND_RED, Items.SAND_RED);
        this.copy(Blocks.SANDSTONE, Items.SANDSTONE);
        this.tag(Items.SEEDS).addTags(new TagKey[]{Items.SEEDS_BEETROOT, Items.SEEDS_MELON, Items.SEEDS_PUMPKIN, Items.SEEDS_WHEAT});
        this.tag(Items.SEEDS_BEETROOT).add((Object)net.minecraft.world.item.Items.BEETROOT_SEEDS);
        this.tag(Items.SEEDS_MELON).add((Object)net.minecraft.world.item.Items.MELON_SEEDS);
        this.tag(Items.SEEDS_PUMPKIN).add((Object)net.minecraft.world.item.Items.PUMPKIN_SEEDS);
        this.tag(Items.SEEDS_WHEAT).add((Object)net.minecraft.world.item.Items.WHEAT_SEEDS);
        this.tag(Items.SHEARS).add((Object)net.minecraft.world.item.Items.SHEARS);
        this.tag(Items.SLIMEBALLS).add((Object)net.minecraft.world.item.Items.SLIME_BALL);
        this.copy(Blocks.STAINED_GLASS, Items.STAINED_GLASS);
        this.copy(Blocks.STAINED_GLASS_PANES, Items.STAINED_GLASS_PANES);
        this.copy(Blocks.STONE, Items.STONE);
        this.copy(Blocks.STORAGE_BLOCKS, Items.STORAGE_BLOCKS);
        this.copy(Blocks.STORAGE_BLOCKS_AMETHYST, Items.STORAGE_BLOCKS_AMETHYST);
        this.copy(Blocks.STORAGE_BLOCKS_COAL, Items.STORAGE_BLOCKS_COAL);
        this.copy(Blocks.STORAGE_BLOCKS_COPPER, Items.STORAGE_BLOCKS_COPPER);
        this.copy(Blocks.STORAGE_BLOCKS_DIAMOND, Items.STORAGE_BLOCKS_DIAMOND);
        this.copy(Blocks.STORAGE_BLOCKS_EMERALD, Items.STORAGE_BLOCKS_EMERALD);
        this.copy(Blocks.STORAGE_BLOCKS_GOLD, Items.STORAGE_BLOCKS_GOLD);
        this.copy(Blocks.STORAGE_BLOCKS_IRON, Items.STORAGE_BLOCKS_IRON);
        this.copy(Blocks.STORAGE_BLOCKS_LAPIS, Items.STORAGE_BLOCKS_LAPIS);
        this.copy(Blocks.STORAGE_BLOCKS_QUARTZ, Items.STORAGE_BLOCKS_QUARTZ);
        this.copy(Blocks.STORAGE_BLOCKS_REDSTONE, Items.STORAGE_BLOCKS_REDSTONE);
        this.copy(Blocks.STORAGE_BLOCKS_RAW_COPPER, Items.STORAGE_BLOCKS_RAW_COPPER);
        this.copy(Blocks.STORAGE_BLOCKS_RAW_GOLD, Items.STORAGE_BLOCKS_RAW_GOLD);
        this.copy(Blocks.STORAGE_BLOCKS_RAW_IRON, Items.STORAGE_BLOCKS_RAW_IRON);
        this.copy(Blocks.STORAGE_BLOCKS_NETHERITE, Items.STORAGE_BLOCKS_NETHERITE);
        this.tag(Items.STRING).add((Object)net.minecraft.world.item.Items.STRING);
        this.tag(Items.TOOLS_SHIELDS).add((Object)net.minecraft.world.item.Items.SHIELD);
        this.tag(Items.TOOLS_BOWS).add((Object)net.minecraft.world.item.Items.BOW);
        this.tag(Items.TOOLS_CROSSBOWS).add((Object)net.minecraft.world.item.Items.CROSSBOW);
        this.tag(Items.TOOLS_FISHING_RODS).add((Object)net.minecraft.world.item.Items.FISHING_ROD);
        this.tag(Items.TOOLS_TRIDENTS).add((Object)net.minecraft.world.item.Items.TRIDENT);
        this.tag(Items.TOOLS).addTags(new TagKey[]{ItemTags.SWORDS, ItemTags.AXES, ItemTags.PICKAXES, ItemTags.SHOVELS, ItemTags.HOES}).addTags(new TagKey[]{Items.TOOLS_SHIELDS, Items.TOOLS_BOWS, Items.TOOLS_CROSSBOWS, Items.TOOLS_FISHING_RODS, Items.TOOLS_TRIDENTS});
        this.tag(Items.ARMORS_HELMETS).add((Object[])(net.minecraft.world.item.Items.LEATHER_HELMET, net.minecraft.world.item.Items.TURTLE_HELMET, net.minecraft.world.item.Items.CHAINMAIL_HELMET, net.minecraft.world.item.Items.IRON_HELMET, net.minecraft.world.item.Items.GOLDEN_HELMET, net.minecraft.world.item.Items.DIAMOND_HELMET, net.minecraft.world.item.Items.NETHERITE_HELMET));
        this.tag(Items.ARMORS_CHESTPLATES).add((Object[])(net.minecraft.world.item.Items.LEATHER_CHESTPLATE, net.minecraft.world.item.Items.CHAINMAIL_CHESTPLATE, net.minecraft.world.item.Items.IRON_CHESTPLATE, net.minecraft.world.item.Items.GOLDEN_CHESTPLATE, net.minecraft.world.item.Items.DIAMOND_CHESTPLATE, net.minecraft.world.item.Items.NETHERITE_CHESTPLATE));
        this.tag(Items.ARMORS_LEGGINGS).add((Object[])(net.minecraft.world.item.Items.LEATHER_LEGGINGS, net.minecraft.world.item.Items.CHAINMAIL_LEGGINGS, net.minecraft.world.item.Items.IRON_LEGGINGS, net.minecraft.world.item.Items.GOLDEN_LEGGINGS, net.minecraft.world.item.Items.DIAMOND_LEGGINGS, net.minecraft.world.item.Items.NETHERITE_LEGGINGS));
        this.tag(Items.ARMORS_BOOTS).add((Object[])(net.minecraft.world.item.Items.LEATHER_BOOTS, net.minecraft.world.item.Items.CHAINMAIL_BOOTS, net.minecraft.world.item.Items.IRON_BOOTS, net.minecraft.world.item.Items.GOLDEN_BOOTS, net.minecraft.world.item.Items.DIAMOND_BOOTS, net.minecraft.world.item.Items.NETHERITE_BOOTS));
        this.tag(Items.ARMORS).addTags(new TagKey[]{Items.ARMORS_HELMETS, Items.ARMORS_CHESTPLATES, Items.ARMORS_LEGGINGS, Items.ARMORS_BOOTS});
    }

    private void addColored(Consumer<TagKey<Item>> consumer, TagKey<Item> group, String pattern) {
        String var10000 = group.location().getPath();
        String prefix = var10000.toUpperCase(Locale.ENGLISH) + "_";
        DyeColor[] var5 = DyeColor.values();
        int var6 = var5.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            DyeColor color = var5[var7];
            ResourceLocation key = new ResourceLocation("minecraft", pattern.replace("{color}", color.getName()));
            TagKey<Item> tag = this.getForgeItemTag(prefix + color.getName());
            Item item = (Item)ForgeRegistries.ITEMS.getValue(key);
            if (item == null || item == net.minecraft.world.item.Items.AIR) {
                throw new IllegalStateException("Unknown vanilla item: " + key.toString());
            }

            this.tag(tag).add((Object)item);
            consumer.accept(tag);
        }

    }

    private void copyColored(TagKey<Block> blockGroup, TagKey<Item> itemGroup) {
        String var10000 = blockGroup.location().getPath();
        String blockPre = var10000.toUpperCase(Locale.ENGLISH) + "_";
        var10000 = itemGroup.location().getPath();
        String itemPre = var10000.toUpperCase(Locale.ENGLISH) + "_";
        DyeColor[] var5 = DyeColor.values();
        int var6 = var5.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            DyeColor color = var5[var7];
            TagKey<Block> from = this.getForgeBlockTag(blockPre + color.getName());
            TagKey<Item> to = this.getForgeItemTag(itemPre + color.getName());
            this.copy(from, to);
        }

        this.copy(this.getForgeBlockTag(blockPre + "colorless"), this.getForgeItemTag(itemPre + "colorless"));
    }

    private TagKey<Block> getForgeBlockTag(String name) {
        try {
            name = name.toUpperCase(Locale.ENGLISH);
            return (TagKey)Tags.Blocks.class.getDeclaredField(name).get((Object)null);
        } catch (IllegalAccessException | NoSuchFieldException | SecurityException | IllegalArgumentException var3) {
            String var10002 = Tags.Blocks.class.getName();
            throw new IllegalStateException(var10002 + " is missing tag name: " + name);
        }
    }

    private TagKey<Item> getForgeItemTag(String name) {
        try {
            name = name.toUpperCase(Locale.ENGLISH);
            return (TagKey)Tags.Items.class.getDeclaredField(name).get((Object)null);
        } catch (IllegalAccessException | NoSuchFieldException | SecurityException | IllegalArgumentException var3) {
            String var10002 = Tags.Items.class.getName();
            throw new IllegalStateException(var10002 + " is missing tag name: " + name);
        }
    }

    public String getName() {
        return "Forge Item Tags";
    }
}

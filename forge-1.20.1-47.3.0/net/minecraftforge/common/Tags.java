//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class Tags {
    public Tags() {
    }

    public static void init() {
        net.minecraftforge.common.Tags.Blocks.init();
        net.minecraftforge.common.Tags.EntityTypes.init();
        net.minecraftforge.common.Tags.Items.init();
        net.minecraftforge.common.Tags.Fluids.init();
        net.minecraftforge.common.Tags.Biomes.init();
    }

    public static class Blocks {
        public static final TagKey<Block> BARRELS = tag("barrels");
        public static final TagKey<Block> BARRELS_WOODEN = tag("barrels/wooden");
        public static final TagKey<Block> BOOKSHELVES = tag("bookshelves");
        public static final TagKey<Block> CHESTS = tag("chests");
        public static final TagKey<Block> CHESTS_ENDER = tag("chests/ender");
        public static final TagKey<Block> CHESTS_TRAPPED = tag("chests/trapped");
        public static final TagKey<Block> CHESTS_WOODEN = tag("chests/wooden");
        public static final TagKey<Block> COBBLESTONE = tag("cobblestone");
        public static final TagKey<Block> COBBLESTONE_NORMAL = tag("cobblestone/normal");
        public static final TagKey<Block> COBBLESTONE_INFESTED = tag("cobblestone/infested");
        public static final TagKey<Block> COBBLESTONE_MOSSY = tag("cobblestone/mossy");
        public static final TagKey<Block> COBBLESTONE_DEEPSLATE = tag("cobblestone/deepslate");
        public static final TagKey<Block> END_STONES = tag("end_stones");
        public static final TagKey<Block> ENDERMAN_PLACE_ON_BLACKLIST = tag("enderman_place_on_blacklist");
        public static final TagKey<Block> FENCE_GATES = tag("fence_gates");
        public static final TagKey<Block> FENCE_GATES_WOODEN = tag("fence_gates/wooden");
        public static final TagKey<Block> FENCES = tag("fences");
        public static final TagKey<Block> FENCES_NETHER_BRICK = tag("fences/nether_brick");
        public static final TagKey<Block> FENCES_WOODEN = tag("fences/wooden");
        public static final TagKey<Block> GLASS = tag("glass");
        public static final TagKey<Block> GLASS_BLACK = tag("glass/black");
        public static final TagKey<Block> GLASS_BLUE = tag("glass/blue");
        public static final TagKey<Block> GLASS_BROWN = tag("glass/brown");
        public static final TagKey<Block> GLASS_COLORLESS = tag("glass/colorless");
        public static final TagKey<Block> GLASS_CYAN = tag("glass/cyan");
        public static final TagKey<Block> GLASS_GRAY = tag("glass/gray");
        public static final TagKey<Block> GLASS_GREEN = tag("glass/green");
        public static final TagKey<Block> GLASS_LIGHT_BLUE = tag("glass/light_blue");
        public static final TagKey<Block> GLASS_LIGHT_GRAY = tag("glass/light_gray");
        public static final TagKey<Block> GLASS_LIME = tag("glass/lime");
        public static final TagKey<Block> GLASS_MAGENTA = tag("glass/magenta");
        public static final TagKey<Block> GLASS_ORANGE = tag("glass/orange");
        public static final TagKey<Block> GLASS_PINK = tag("glass/pink");
        public static final TagKey<Block> GLASS_PURPLE = tag("glass/purple");
        public static final TagKey<Block> GLASS_RED = tag("glass/red");
        public static final TagKey<Block> GLASS_SILICA = tag("glass/silica");
        public static final TagKey<Block> GLASS_TINTED = tag("glass/tinted");
        public static final TagKey<Block> GLASS_WHITE = tag("glass/white");
        public static final TagKey<Block> GLASS_YELLOW = tag("glass/yellow");
        public static final TagKey<Block> GLASS_PANES = tag("glass_panes");
        public static final TagKey<Block> GLASS_PANES_BLACK = tag("glass_panes/black");
        public static final TagKey<Block> GLASS_PANES_BLUE = tag("glass_panes/blue");
        public static final TagKey<Block> GLASS_PANES_BROWN = tag("glass_panes/brown");
        public static final TagKey<Block> GLASS_PANES_COLORLESS = tag("glass_panes/colorless");
        public static final TagKey<Block> GLASS_PANES_CYAN = tag("glass_panes/cyan");
        public static final TagKey<Block> GLASS_PANES_GRAY = tag("glass_panes/gray");
        public static final TagKey<Block> GLASS_PANES_GREEN = tag("glass_panes/green");
        public static final TagKey<Block> GLASS_PANES_LIGHT_BLUE = tag("glass_panes/light_blue");
        public static final TagKey<Block> GLASS_PANES_LIGHT_GRAY = tag("glass_panes/light_gray");
        public static final TagKey<Block> GLASS_PANES_LIME = tag("glass_panes/lime");
        public static final TagKey<Block> GLASS_PANES_MAGENTA = tag("glass_panes/magenta");
        public static final TagKey<Block> GLASS_PANES_ORANGE = tag("glass_panes/orange");
        public static final TagKey<Block> GLASS_PANES_PINK = tag("glass_panes/pink");
        public static final TagKey<Block> GLASS_PANES_PURPLE = tag("glass_panes/purple");
        public static final TagKey<Block> GLASS_PANES_RED = tag("glass_panes/red");
        public static final TagKey<Block> GLASS_PANES_WHITE = tag("glass_panes/white");
        public static final TagKey<Block> GLASS_PANES_YELLOW = tag("glass_panes/yellow");
        public static final TagKey<Block> GRAVEL = tag("gravel");
        public static final TagKey<Block> NETHERRACK = tag("netherrack");
        public static final TagKey<Block> OBSIDIAN = tag("obsidian");
        public static final TagKey<Block> ORE_BEARING_GROUND_DEEPSLATE = tag("ore_bearing_ground/deepslate");
        public static final TagKey<Block> ORE_BEARING_GROUND_NETHERRACK = tag("ore_bearing_ground/netherrack");
        public static final TagKey<Block> ORE_BEARING_GROUND_STONE = tag("ore_bearing_ground/stone");
        public static final TagKey<Block> ORE_RATES_DENSE = tag("ore_rates/dense");
        public static final TagKey<Block> ORE_RATES_SINGULAR = tag("ore_rates/singular");
        public static final TagKey<Block> ORE_RATES_SPARSE = tag("ore_rates/sparse");
        public static final TagKey<Block> ORES = tag("ores");
        public static final TagKey<Block> ORES_COAL = tag("ores/coal");
        public static final TagKey<Block> ORES_COPPER = tag("ores/copper");
        public static final TagKey<Block> ORES_DIAMOND = tag("ores/diamond");
        public static final TagKey<Block> ORES_EMERALD = tag("ores/emerald");
        public static final TagKey<Block> ORES_GOLD = tag("ores/gold");
        public static final TagKey<Block> ORES_IRON = tag("ores/iron");
        public static final TagKey<Block> ORES_LAPIS = tag("ores/lapis");
        public static final TagKey<Block> ORES_NETHERITE_SCRAP = tag("ores/netherite_scrap");
        public static final TagKey<Block> ORES_QUARTZ = tag("ores/quartz");
        public static final TagKey<Block> ORES_REDSTONE = tag("ores/redstone");
        public static final TagKey<Block> ORES_IN_GROUND_DEEPSLATE = tag("ores_in_ground/deepslate");
        public static final TagKey<Block> ORES_IN_GROUND_NETHERRACK = tag("ores_in_ground/netherrack");
        public static final TagKey<Block> ORES_IN_GROUND_STONE = tag("ores_in_ground/stone");
        public static final TagKey<Block> SAND = tag("sand");
        public static final TagKey<Block> SAND_COLORLESS = tag("sand/colorless");
        public static final TagKey<Block> SAND_RED = tag("sand/red");
        public static final TagKey<Block> SANDSTONE = tag("sandstone");
        public static final TagKey<Block> STAINED_GLASS = tag("stained_glass");
        public static final TagKey<Block> STAINED_GLASS_PANES = tag("stained_glass_panes");
        public static final TagKey<Block> STONE = tag("stone");
        public static final TagKey<Block> STORAGE_BLOCKS = tag("storage_blocks");
        public static final TagKey<Block> STORAGE_BLOCKS_AMETHYST = tag("storage_blocks/amethyst");
        public static final TagKey<Block> STORAGE_BLOCKS_COAL = tag("storage_blocks/coal");
        public static final TagKey<Block> STORAGE_BLOCKS_COPPER = tag("storage_blocks/copper");
        public static final TagKey<Block> STORAGE_BLOCKS_DIAMOND = tag("storage_blocks/diamond");
        public static final TagKey<Block> STORAGE_BLOCKS_EMERALD = tag("storage_blocks/emerald");
        public static final TagKey<Block> STORAGE_BLOCKS_GOLD = tag("storage_blocks/gold");
        public static final TagKey<Block> STORAGE_BLOCKS_IRON = tag("storage_blocks/iron");
        public static final TagKey<Block> STORAGE_BLOCKS_LAPIS = tag("storage_blocks/lapis");
        public static final TagKey<Block> STORAGE_BLOCKS_NETHERITE = tag("storage_blocks/netherite");
        public static final TagKey<Block> STORAGE_BLOCKS_QUARTZ = tag("storage_blocks/quartz");
        public static final TagKey<Block> STORAGE_BLOCKS_RAW_COPPER = tag("storage_blocks/raw_copper");
        public static final TagKey<Block> STORAGE_BLOCKS_RAW_GOLD = tag("storage_blocks/raw_gold");
        public static final TagKey<Block> STORAGE_BLOCKS_RAW_IRON = tag("storage_blocks/raw_iron");
        public static final TagKey<Block> STORAGE_BLOCKS_REDSTONE = tag("storage_blocks/redstone");
        public static final TagKey<Block> NEEDS_WOOD_TOOL = tag("needs_wood_tool");
        public static final TagKey<Block> NEEDS_GOLD_TOOL = tag("needs_gold_tool");
        public static final TagKey<Block> NEEDS_NETHERITE_TOOL = tag("needs_netherite_tool");

        public Blocks() {
        }

        private static void init() {
        }

        private static TagKey<Block> tag(String name) {
            return BlockTags.create(new ResourceLocation("forge", name));
        }
    }

    public static class EntityTypes {
        public static final TagKey<EntityType<?>> BOSSES = tag("bosses");

        public EntityTypes() {
        }

        private static void init() {
        }

        private static TagKey<EntityType<?>> tag(String name) {
            return TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("forge", name));
        }
    }

    public static class Items {
        public static final TagKey<Item> BARRELS = tag("barrels");
        public static final TagKey<Item> BARRELS_WOODEN = tag("barrels/wooden");
        public static final TagKey<Item> BONES = tag("bones");
        public static final TagKey<Item> BOOKSHELVES = tag("bookshelves");
        public static final TagKey<Item> CHESTS = tag("chests");
        public static final TagKey<Item> CHESTS_ENDER = tag("chests/ender");
        public static final TagKey<Item> CHESTS_TRAPPED = tag("chests/trapped");
        public static final TagKey<Item> CHESTS_WOODEN = tag("chests/wooden");
        public static final TagKey<Item> COBBLESTONE = tag("cobblestone");
        public static final TagKey<Item> COBBLESTONE_NORMAL = tag("cobblestone/normal");
        public static final TagKey<Item> COBBLESTONE_INFESTED = tag("cobblestone/infested");
        public static final TagKey<Item> COBBLESTONE_MOSSY = tag("cobblestone/mossy");
        public static final TagKey<Item> COBBLESTONE_DEEPSLATE = tag("cobblestone/deepslate");
        public static final TagKey<Item> CROPS = tag("crops");
        public static final TagKey<Item> CROPS_BEETROOT = tag("crops/beetroot");
        public static final TagKey<Item> CROPS_CARROT = tag("crops/carrot");
        public static final TagKey<Item> CROPS_NETHER_WART = tag("crops/nether_wart");
        public static final TagKey<Item> CROPS_POTATO = tag("crops/potato");
        public static final TagKey<Item> CROPS_WHEAT = tag("crops/wheat");
        public static final TagKey<Item> DUSTS = tag("dusts");
        public static final TagKey<Item> DUSTS_PRISMARINE = tag("dusts/prismarine");
        public static final TagKey<Item> DUSTS_REDSTONE = tag("dusts/redstone");
        public static final TagKey<Item> DUSTS_GLOWSTONE = tag("dusts/glowstone");
        public static final TagKey<Item> DYES = tag("dyes");
        public static final TagKey<Item> DYES_BLACK;
        public static final TagKey<Item> DYES_RED;
        public static final TagKey<Item> DYES_GREEN;
        public static final TagKey<Item> DYES_BROWN;
        public static final TagKey<Item> DYES_BLUE;
        public static final TagKey<Item> DYES_PURPLE;
        public static final TagKey<Item> DYES_CYAN;
        public static final TagKey<Item> DYES_LIGHT_GRAY;
        public static final TagKey<Item> DYES_GRAY;
        public static final TagKey<Item> DYES_PINK;
        public static final TagKey<Item> DYES_LIME;
        public static final TagKey<Item> DYES_YELLOW;
        public static final TagKey<Item> DYES_LIGHT_BLUE;
        public static final TagKey<Item> DYES_MAGENTA;
        public static final TagKey<Item> DYES_ORANGE;
        public static final TagKey<Item> DYES_WHITE;
        public static final TagKey<Item> EGGS;
        public static final TagKey<Item> ENCHANTING_FUELS;
        public static final TagKey<Item> END_STONES;
        public static final TagKey<Item> ENDER_PEARLS;
        public static final TagKey<Item> FEATHERS;
        public static final TagKey<Item> FENCE_GATES;
        public static final TagKey<Item> FENCE_GATES_WOODEN;
        public static final TagKey<Item> FENCES;
        public static final TagKey<Item> FENCES_NETHER_BRICK;
        public static final TagKey<Item> FENCES_WOODEN;
        public static final TagKey<Item> GEMS;
        public static final TagKey<Item> GEMS_DIAMOND;
        public static final TagKey<Item> GEMS_EMERALD;
        public static final TagKey<Item> GEMS_AMETHYST;
        public static final TagKey<Item> GEMS_LAPIS;
        public static final TagKey<Item> GEMS_PRISMARINE;
        public static final TagKey<Item> GEMS_QUARTZ;
        public static final TagKey<Item> GLASS;
        public static final TagKey<Item> GLASS_BLACK;
        public static final TagKey<Item> GLASS_BLUE;
        public static final TagKey<Item> GLASS_BROWN;
        public static final TagKey<Item> GLASS_COLORLESS;
        public static final TagKey<Item> GLASS_CYAN;
        public static final TagKey<Item> GLASS_GRAY;
        public static final TagKey<Item> GLASS_GREEN;
        public static final TagKey<Item> GLASS_LIGHT_BLUE;
        public static final TagKey<Item> GLASS_LIGHT_GRAY;
        public static final TagKey<Item> GLASS_LIME;
        public static final TagKey<Item> GLASS_MAGENTA;
        public static final TagKey<Item> GLASS_ORANGE;
        public static final TagKey<Item> GLASS_PINK;
        public static final TagKey<Item> GLASS_PURPLE;
        public static final TagKey<Item> GLASS_RED;
        public static final TagKey<Item> GLASS_SILICA;
        public static final TagKey<Item> GLASS_TINTED;
        public static final TagKey<Item> GLASS_WHITE;
        public static final TagKey<Item> GLASS_YELLOW;
        public static final TagKey<Item> GLASS_PANES;
        public static final TagKey<Item> GLASS_PANES_BLACK;
        public static final TagKey<Item> GLASS_PANES_BLUE;
        public static final TagKey<Item> GLASS_PANES_BROWN;
        public static final TagKey<Item> GLASS_PANES_COLORLESS;
        public static final TagKey<Item> GLASS_PANES_CYAN;
        public static final TagKey<Item> GLASS_PANES_GRAY;
        public static final TagKey<Item> GLASS_PANES_GREEN;
        public static final TagKey<Item> GLASS_PANES_LIGHT_BLUE;
        public static final TagKey<Item> GLASS_PANES_LIGHT_GRAY;
        public static final TagKey<Item> GLASS_PANES_LIME;
        public static final TagKey<Item> GLASS_PANES_MAGENTA;
        public static final TagKey<Item> GLASS_PANES_ORANGE;
        public static final TagKey<Item> GLASS_PANES_PINK;
        public static final TagKey<Item> GLASS_PANES_PURPLE;
        public static final TagKey<Item> GLASS_PANES_RED;
        public static final TagKey<Item> GLASS_PANES_WHITE;
        public static final TagKey<Item> GLASS_PANES_YELLOW;
        public static final TagKey<Item> GRAVEL;
        public static final TagKey<Item> GUNPOWDER;
        public static final TagKey<Item> HEADS;
        public static final TagKey<Item> INGOTS;
        public static final TagKey<Item> INGOTS_BRICK;
        public static final TagKey<Item> INGOTS_COPPER;
        public static final TagKey<Item> INGOTS_GOLD;
        public static final TagKey<Item> INGOTS_IRON;
        public static final TagKey<Item> INGOTS_NETHERITE;
        public static final TagKey<Item> INGOTS_NETHER_BRICK;
        public static final TagKey<Item> LEATHER;
        public static final TagKey<Item> MUSHROOMS;
        public static final TagKey<Item> NETHER_STARS;
        public static final TagKey<Item> NETHERRACK;
        public static final TagKey<Item> NUGGETS;
        public static final TagKey<Item> NUGGETS_GOLD;
        public static final TagKey<Item> NUGGETS_IRON;
        public static final TagKey<Item> OBSIDIAN;
        public static final TagKey<Item> ORE_BEARING_GROUND_DEEPSLATE;
        public static final TagKey<Item> ORE_BEARING_GROUND_NETHERRACK;
        public static final TagKey<Item> ORE_BEARING_GROUND_STONE;
        public static final TagKey<Item> ORE_RATES_DENSE;
        public static final TagKey<Item> ORE_RATES_SINGULAR;
        public static final TagKey<Item> ORE_RATES_SPARSE;
        public static final TagKey<Item> ORES;
        public static final TagKey<Item> ORES_COAL;
        public static final TagKey<Item> ORES_COPPER;
        public static final TagKey<Item> ORES_DIAMOND;
        public static final TagKey<Item> ORES_EMERALD;
        public static final TagKey<Item> ORES_GOLD;
        public static final TagKey<Item> ORES_IRON;
        public static final TagKey<Item> ORES_LAPIS;
        public static final TagKey<Item> ORES_NETHERITE_SCRAP;
        public static final TagKey<Item> ORES_QUARTZ;
        public static final TagKey<Item> ORES_REDSTONE;
        public static final TagKey<Item> ORES_IN_GROUND_DEEPSLATE;
        public static final TagKey<Item> ORES_IN_GROUND_NETHERRACK;
        public static final TagKey<Item> ORES_IN_GROUND_STONE;
        public static final TagKey<Item> RAW_MATERIALS;
        public static final TagKey<Item> RAW_MATERIALS_COPPER;
        public static final TagKey<Item> RAW_MATERIALS_GOLD;
        public static final TagKey<Item> RAW_MATERIALS_IRON;
        public static final TagKey<Item> RODS;
        public static final TagKey<Item> RODS_BLAZE;
        public static final TagKey<Item> RODS_WOODEN;
        public static final TagKey<Item> SAND;
        public static final TagKey<Item> SAND_COLORLESS;
        public static final TagKey<Item> SAND_RED;
        public static final TagKey<Item> SANDSTONE;
        public static final TagKey<Item> SEEDS;
        public static final TagKey<Item> SEEDS_BEETROOT;
        public static final TagKey<Item> SEEDS_MELON;
        public static final TagKey<Item> SEEDS_PUMPKIN;
        public static final TagKey<Item> SEEDS_WHEAT;
        public static final TagKey<Item> SHEARS;
        public static final TagKey<Item> SLIMEBALLS;
        public static final TagKey<Item> STAINED_GLASS;
        public static final TagKey<Item> STAINED_GLASS_PANES;
        public static final TagKey<Item> STONE;
        public static final TagKey<Item> STORAGE_BLOCKS;
        public static final TagKey<Item> STORAGE_BLOCKS_AMETHYST;
        public static final TagKey<Item> STORAGE_BLOCKS_COAL;
        public static final TagKey<Item> STORAGE_BLOCKS_COPPER;
        public static final TagKey<Item> STORAGE_BLOCKS_DIAMOND;
        public static final TagKey<Item> STORAGE_BLOCKS_EMERALD;
        public static final TagKey<Item> STORAGE_BLOCKS_GOLD;
        public static final TagKey<Item> STORAGE_BLOCKS_IRON;
        public static final TagKey<Item> STORAGE_BLOCKS_LAPIS;
        public static final TagKey<Item> STORAGE_BLOCKS_NETHERITE;
        public static final TagKey<Item> STORAGE_BLOCKS_QUARTZ;
        public static final TagKey<Item> STORAGE_BLOCKS_RAW_COPPER;
        public static final TagKey<Item> STORAGE_BLOCKS_RAW_GOLD;
        public static final TagKey<Item> STORAGE_BLOCKS_RAW_IRON;
        public static final TagKey<Item> STORAGE_BLOCKS_REDSTONE;
        public static final TagKey<Item> STRING;
        public static final TagKey<Item> TOOLS;
        public static final TagKey<Item> TOOLS_SHIELDS;
        public static final TagKey<Item> TOOLS_BOWS;
        public static final TagKey<Item> TOOLS_CROSSBOWS;
        public static final TagKey<Item> TOOLS_FISHING_RODS;
        public static final TagKey<Item> TOOLS_TRIDENTS;
        public static final TagKey<Item> ARMORS;
        public static final TagKey<Item> ARMORS_HELMETS;
        public static final TagKey<Item> ARMORS_CHESTPLATES;
        public static final TagKey<Item> ARMORS_LEGGINGS;
        public static final TagKey<Item> ARMORS_BOOTS;

        public Items() {
        }

        private static void init() {
        }

        private static TagKey<Item> tag(String name) {
            return ItemTags.create(new ResourceLocation("forge", name));
        }

        static {
            DYES_BLACK = DyeColor.BLACK.getTag();
            DYES_RED = DyeColor.RED.getTag();
            DYES_GREEN = DyeColor.GREEN.getTag();
            DYES_BROWN = DyeColor.BROWN.getTag();
            DYES_BLUE = DyeColor.BLUE.getTag();
            DYES_PURPLE = DyeColor.PURPLE.getTag();
            DYES_CYAN = DyeColor.CYAN.getTag();
            DYES_LIGHT_GRAY = DyeColor.LIGHT_GRAY.getTag();
            DYES_GRAY = DyeColor.GRAY.getTag();
            DYES_PINK = DyeColor.PINK.getTag();
            DYES_LIME = DyeColor.LIME.getTag();
            DYES_YELLOW = DyeColor.YELLOW.getTag();
            DYES_LIGHT_BLUE = DyeColor.LIGHT_BLUE.getTag();
            DYES_MAGENTA = DyeColor.MAGENTA.getTag();
            DYES_ORANGE = DyeColor.ORANGE.getTag();
            DYES_WHITE = DyeColor.WHITE.getTag();
            EGGS = tag("eggs");
            ENCHANTING_FUELS = tag("enchanting_fuels");
            END_STONES = tag("end_stones");
            ENDER_PEARLS = tag("ender_pearls");
            FEATHERS = tag("feathers");
            FENCE_GATES = tag("fence_gates");
            FENCE_GATES_WOODEN = tag("fence_gates/wooden");
            FENCES = tag("fences");
            FENCES_NETHER_BRICK = tag("fences/nether_brick");
            FENCES_WOODEN = tag("fences/wooden");
            GEMS = tag("gems");
            GEMS_DIAMOND = tag("gems/diamond");
            GEMS_EMERALD = tag("gems/emerald");
            GEMS_AMETHYST = tag("gems/amethyst");
            GEMS_LAPIS = tag("gems/lapis");
            GEMS_PRISMARINE = tag("gems/prismarine");
            GEMS_QUARTZ = tag("gems/quartz");
            GLASS = tag("glass");
            GLASS_BLACK = tag("glass/black");
            GLASS_BLUE = tag("glass/blue");
            GLASS_BROWN = tag("glass/brown");
            GLASS_COLORLESS = tag("glass/colorless");
            GLASS_CYAN = tag("glass/cyan");
            GLASS_GRAY = tag("glass/gray");
            GLASS_GREEN = tag("glass/green");
            GLASS_LIGHT_BLUE = tag("glass/light_blue");
            GLASS_LIGHT_GRAY = tag("glass/light_gray");
            GLASS_LIME = tag("glass/lime");
            GLASS_MAGENTA = tag("glass/magenta");
            GLASS_ORANGE = tag("glass/orange");
            GLASS_PINK = tag("glass/pink");
            GLASS_PURPLE = tag("glass/purple");
            GLASS_RED = tag("glass/red");
            GLASS_SILICA = tag("glass/silica");
            GLASS_TINTED = tag("glass/tinted");
            GLASS_WHITE = tag("glass/white");
            GLASS_YELLOW = tag("glass/yellow");
            GLASS_PANES = tag("glass_panes");
            GLASS_PANES_BLACK = tag("glass_panes/black");
            GLASS_PANES_BLUE = tag("glass_panes/blue");
            GLASS_PANES_BROWN = tag("glass_panes/brown");
            GLASS_PANES_COLORLESS = tag("glass_panes/colorless");
            GLASS_PANES_CYAN = tag("glass_panes/cyan");
            GLASS_PANES_GRAY = tag("glass_panes/gray");
            GLASS_PANES_GREEN = tag("glass_panes/green");
            GLASS_PANES_LIGHT_BLUE = tag("glass_panes/light_blue");
            GLASS_PANES_LIGHT_GRAY = tag("glass_panes/light_gray");
            GLASS_PANES_LIME = tag("glass_panes/lime");
            GLASS_PANES_MAGENTA = tag("glass_panes/magenta");
            GLASS_PANES_ORANGE = tag("glass_panes/orange");
            GLASS_PANES_PINK = tag("glass_panes/pink");
            GLASS_PANES_PURPLE = tag("glass_panes/purple");
            GLASS_PANES_RED = tag("glass_panes/red");
            GLASS_PANES_WHITE = tag("glass_panes/white");
            GLASS_PANES_YELLOW = tag("glass_panes/yellow");
            GRAVEL = tag("gravel");
            GUNPOWDER = tag("gunpowder");
            HEADS = tag("heads");
            INGOTS = tag("ingots");
            INGOTS_BRICK = tag("ingots/brick");
            INGOTS_COPPER = tag("ingots/copper");
            INGOTS_GOLD = tag("ingots/gold");
            INGOTS_IRON = tag("ingots/iron");
            INGOTS_NETHERITE = tag("ingots/netherite");
            INGOTS_NETHER_BRICK = tag("ingots/nether_brick");
            LEATHER = tag("leather");
            MUSHROOMS = tag("mushrooms");
            NETHER_STARS = tag("nether_stars");
            NETHERRACK = tag("netherrack");
            NUGGETS = tag("nuggets");
            NUGGETS_GOLD = tag("nuggets/gold");
            NUGGETS_IRON = tag("nuggets/iron");
            OBSIDIAN = tag("obsidian");
            ORE_BEARING_GROUND_DEEPSLATE = tag("ore_bearing_ground/deepslate");
            ORE_BEARING_GROUND_NETHERRACK = tag("ore_bearing_ground/netherrack");
            ORE_BEARING_GROUND_STONE = tag("ore_bearing_ground/stone");
            ORE_RATES_DENSE = tag("ore_rates/dense");
            ORE_RATES_SINGULAR = tag("ore_rates/singular");
            ORE_RATES_SPARSE = tag("ore_rates/sparse");
            ORES = tag("ores");
            ORES_COAL = tag("ores/coal");
            ORES_COPPER = tag("ores/copper");
            ORES_DIAMOND = tag("ores/diamond");
            ORES_EMERALD = tag("ores/emerald");
            ORES_GOLD = tag("ores/gold");
            ORES_IRON = tag("ores/iron");
            ORES_LAPIS = tag("ores/lapis");
            ORES_NETHERITE_SCRAP = tag("ores/netherite_scrap");
            ORES_QUARTZ = tag("ores/quartz");
            ORES_REDSTONE = tag("ores/redstone");
            ORES_IN_GROUND_DEEPSLATE = tag("ores_in_ground/deepslate");
            ORES_IN_GROUND_NETHERRACK = tag("ores_in_ground/netherrack");
            ORES_IN_GROUND_STONE = tag("ores_in_ground/stone");
            RAW_MATERIALS = tag("raw_materials");
            RAW_MATERIALS_COPPER = tag("raw_materials/copper");
            RAW_MATERIALS_GOLD = tag("raw_materials/gold");
            RAW_MATERIALS_IRON = tag("raw_materials/iron");
            RODS = tag("rods");
            RODS_BLAZE = tag("rods/blaze");
            RODS_WOODEN = tag("rods/wooden");
            SAND = tag("sand");
            SAND_COLORLESS = tag("sand/colorless");
            SAND_RED = tag("sand/red");
            SANDSTONE = tag("sandstone");
            SEEDS = tag("seeds");
            SEEDS_BEETROOT = tag("seeds/beetroot");
            SEEDS_MELON = tag("seeds/melon");
            SEEDS_PUMPKIN = tag("seeds/pumpkin");
            SEEDS_WHEAT = tag("seeds/wheat");
            SHEARS = tag("shears");
            SLIMEBALLS = tag("slimeballs");
            STAINED_GLASS = tag("stained_glass");
            STAINED_GLASS_PANES = tag("stained_glass_panes");
            STONE = tag("stone");
            STORAGE_BLOCKS = tag("storage_blocks");
            STORAGE_BLOCKS_AMETHYST = tag("storage_blocks/amethyst");
            STORAGE_BLOCKS_COAL = tag("storage_blocks/coal");
            STORAGE_BLOCKS_COPPER = tag("storage_blocks/copper");
            STORAGE_BLOCKS_DIAMOND = tag("storage_blocks/diamond");
            STORAGE_BLOCKS_EMERALD = tag("storage_blocks/emerald");
            STORAGE_BLOCKS_GOLD = tag("storage_blocks/gold");
            STORAGE_BLOCKS_IRON = tag("storage_blocks/iron");
            STORAGE_BLOCKS_LAPIS = tag("storage_blocks/lapis");
            STORAGE_BLOCKS_NETHERITE = tag("storage_blocks/netherite");
            STORAGE_BLOCKS_QUARTZ = tag("storage_blocks/quartz");
            STORAGE_BLOCKS_RAW_COPPER = tag("storage_blocks/raw_copper");
            STORAGE_BLOCKS_RAW_GOLD = tag("storage_blocks/raw_gold");
            STORAGE_BLOCKS_RAW_IRON = tag("storage_blocks/raw_iron");
            STORAGE_BLOCKS_REDSTONE = tag("storage_blocks/redstone");
            STRING = tag("string");
            TOOLS = tag("tools");
            TOOLS_SHIELDS = tag("tools/shields");
            TOOLS_BOWS = tag("tools/bows");
            TOOLS_CROSSBOWS = tag("tools/crossbows");
            TOOLS_FISHING_RODS = tag("tools/fishing_rods");
            TOOLS_TRIDENTS = tag("tools/tridents");
            ARMORS = tag("armors");
            ARMORS_HELMETS = tag("armors/helmets");
            ARMORS_CHESTPLATES = tag("armors/chestplates");
            ARMORS_LEGGINGS = tag("armors/leggings");
            ARMORS_BOOTS = tag("armors/boots");
        }
    }

    public static class Fluids {
        public static final TagKey<Fluid> MILK = tag("milk");
        public static final TagKey<Fluid> GASEOUS = tag("gaseous");

        public Fluids() {
        }

        private static void init() {
        }

        private static TagKey<Fluid> tag(String name) {
            return FluidTags.create(new ResourceLocation("forge", name));
        }
    }

    public static class Biomes {
        public static final TagKey<Biome> IS_HOT = tag("is_hot");
        public static final TagKey<Biome> IS_HOT_OVERWORLD = tag("is_hot/overworld");
        public static final TagKey<Biome> IS_HOT_NETHER = tag("is_hot/nether");
        public static final TagKey<Biome> IS_HOT_END = tag("is_hot/end");
        public static final TagKey<Biome> IS_COLD = tag("is_cold");
        public static final TagKey<Biome> IS_COLD_OVERWORLD = tag("is_cold/overworld");
        public static final TagKey<Biome> IS_COLD_NETHER = tag("is_cold/nether");
        public static final TagKey<Biome> IS_COLD_END = tag("is_cold/end");
        public static final TagKey<Biome> IS_SPARSE = tag("is_sparse");
        public static final TagKey<Biome> IS_SPARSE_OVERWORLD = tag("is_sparse/overworld");
        public static final TagKey<Biome> IS_SPARSE_NETHER = tag("is_sparse/nether");
        public static final TagKey<Biome> IS_SPARSE_END = tag("is_sparse/end");
        public static final TagKey<Biome> IS_DENSE = tag("is_dense");
        public static final TagKey<Biome> IS_DENSE_OVERWORLD = tag("is_dense/overworld");
        public static final TagKey<Biome> IS_DENSE_NETHER = tag("is_dense/nether");
        public static final TagKey<Biome> IS_DENSE_END = tag("is_dense/end");
        public static final TagKey<Biome> IS_WET = tag("is_wet");
        public static final TagKey<Biome> IS_WET_OVERWORLD = tag("is_wet/overworld");
        public static final TagKey<Biome> IS_WET_NETHER = tag("is_wet/nether");
        public static final TagKey<Biome> IS_WET_END = tag("is_wet/end");
        public static final TagKey<Biome> IS_DRY = tag("is_dry");
        public static final TagKey<Biome> IS_DRY_OVERWORLD = tag("is_dry/overworld");
        public static final TagKey<Biome> IS_DRY_NETHER = tag("is_dry/nether");
        public static final TagKey<Biome> IS_DRY_END = tag("is_dry/end");
        public static final TagKey<Biome> IS_CONIFEROUS = tag("is_coniferous");
        public static final TagKey<Biome> IS_SPOOKY = tag("is_spooky");
        public static final TagKey<Biome> IS_DEAD = tag("is_dead");
        public static final TagKey<Biome> IS_LUSH = tag("is_lush");
        public static final TagKey<Biome> IS_MUSHROOM = tag("is_mushroom");
        public static final TagKey<Biome> IS_MAGICAL = tag("is_magical");
        public static final TagKey<Biome> IS_RARE = tag("is_rare");
        public static final TagKey<Biome> IS_PLATEAU = tag("is_plateau");
        public static final TagKey<Biome> IS_MODIFIED = tag("is_modified");
        public static final TagKey<Biome> IS_WATER = tag("is_water");
        public static final TagKey<Biome> IS_DESERT = tag("is_desert");
        public static final TagKey<Biome> IS_PLAINS = tag("is_plains");
        public static final TagKey<Biome> IS_SWAMP = tag("is_swamp");
        public static final TagKey<Biome> IS_SANDY = tag("is_sandy");
        public static final TagKey<Biome> IS_SNOWY = tag("is_snowy");
        public static final TagKey<Biome> IS_WASTELAND = tag("is_wasteland");
        public static final TagKey<Biome> IS_VOID = tag("is_void");
        public static final TagKey<Biome> IS_UNDERGROUND = tag("is_underground");
        public static final TagKey<Biome> IS_CAVE = tag("is_cave");
        public static final TagKey<Biome> IS_PEAK = tag("is_peak");
        public static final TagKey<Biome> IS_SLOPE = tag("is_slope");
        public static final TagKey<Biome> IS_MOUNTAIN = tag("is_mountain");

        public Biomes() {
        }

        private static void init() {
        }

        private static TagKey<Biome> tag(String name) {
            return TagKey.create(Registries.BIOME, new ResourceLocation("forge", name));
        }
    }
}

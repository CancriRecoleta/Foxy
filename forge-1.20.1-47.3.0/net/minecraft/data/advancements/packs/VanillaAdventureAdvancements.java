//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.data.advancements.packs;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.DistancePredicate;
import net.minecraft.advancements.critereon.EntityEquipmentPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.LighthingBoltPredicate;
import net.minecraft.advancements.critereon.LightningStrikeTrigger;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.advancements.critereon.TagPredicate;
import net.minecraft.advancements.critereon.UsingItemTrigger;
import net.minecraft.advancements.critereon.EntityPredicate.Builder;
import net.minecraft.advancements.critereon.LightningStrikeTrigger.TriggerInstance;
import net.minecraft.advancements.critereon.MinMaxBounds.Doubles;
import net.minecraft.advancements.critereon.MinMaxBounds.Ints;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.data.recipes.packs.VanillaRecipeProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.DecoratedPotRecipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList.Preset;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComparatorBlock;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.predicates.AllOfCondition;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class VanillaAdventureAdvancements implements AdvancementSubProvider {
    private static final int DISTANCE_FROM_BOTTOM_TO_TOP = 384;
    private static final int Y_COORDINATE_AT_TOP = 320;
    private static final int Y_COORDINATE_AT_BOTTOM = -64;
    private static final int BEDROCK_THICKNESS = 5;
    private static final EntityType<?>[] MOBS_TO_KILL;

    public VanillaAdventureAdvancements() {
    }

    private static LightningStrikeTrigger.TriggerInstance fireCountAndBystander(MinMaxBounds.Ints p_252298_, EntityPredicate p_251894_) {
        return TriggerInstance.lighthingStrike(Builder.entity().distance(DistancePredicate.absolute(Doubles.atMost(30.0))).subPredicate(LighthingBoltPredicate.blockSetOnFire(p_252298_)).build(), p_251894_);
    }

    private static UsingItemTrigger.TriggerInstance lookAtThroughItem(EntityType<?> p_249703_, Item p_250746_) {
        return net.minecraft.advancements.critereon.UsingItemTrigger.TriggerInstance.lookingAt(Builder.entity().subPredicate(net.minecraft.advancements.critereon.PlayerPredicate.Builder.player().setLookingAt(Builder.entity().of(p_249703_).build()).build()), net.minecraft.advancements.critereon.ItemPredicate.Builder.item().of(p_250746_));
    }

    public void generate(HolderLookup.Provider p_255887_, Consumer<Advancement> p_256428_) {
        Advancement $$2 = net.minecraft.advancements.Advancement.Builder.advancement().display((ItemLike)Items.MAP, Component.translatable("advancements.adventure.root.title"), Component.translatable("advancements.adventure.root.description"), new ResourceLocation("textures/gui/advancements/backgrounds/adventure.png"), FrameType.TASK, false, false, false).requirements(RequirementsStrategy.OR).addCriterion("killed_something", (CriterionTriggerInstance)net.minecraft.advancements.critereon.KilledTrigger.TriggerInstance.playerKilledEntity()).addCriterion("killed_by_something", (CriterionTriggerInstance)net.minecraft.advancements.critereon.KilledTrigger.TriggerInstance.entityKilledPlayer()).save(p_256428_, "adventure/root");
        Advancement $$3 = net.minecraft.advancements.Advancement.Builder.advancement().parent($$2).display((ItemLike)Blocks.RED_BED, Component.translatable("advancements.adventure.sleep_in_bed.title"), Component.translatable("advancements.adventure.sleep_in_bed.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("slept_in_bed", (CriterionTriggerInstance)net.minecraft.advancements.critereon.PlayerTrigger.TriggerInstance.sleptInBed()).save(p_256428_, "adventure/sleep_in_bed");
        createAdventuringTime(p_256428_, $$3, Preset.OVERWORLD);
        Advancement $$4 = net.minecraft.advancements.Advancement.Builder.advancement().parent($$2).display((ItemLike)Items.EMERALD, Component.translatable("advancements.adventure.trade.title"), Component.translatable("advancements.adventure.trade.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("traded", (CriterionTriggerInstance)net.minecraft.advancements.critereon.TradeTrigger.TriggerInstance.tradedWithVillager()).save(p_256428_, "adventure/trade");
        net.minecraft.advancements.Advancement.Builder.advancement().parent($$4).display((ItemLike)Items.EMERALD, Component.translatable("advancements.adventure.trade_at_world_height.title"), Component.translatable("advancements.adventure.trade_at_world_height.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("trade_at_world_height", (CriterionTriggerInstance)net.minecraft.advancements.critereon.TradeTrigger.TriggerInstance.tradedWithVillager(Builder.entity().located(LocationPredicate.atYLocation(Doubles.atLeast(319.0))))).save(p_256428_, "adventure/trade_at_world_height");
        Advancement $$5 = addMobsToKill(net.minecraft.advancements.Advancement.Builder.advancement()).parent($$2).display((ItemLike)Items.IRON_SWORD, Component.translatable("advancements.adventure.kill_a_mob.title"), Component.translatable("advancements.adventure.kill_a_mob.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).requirements(RequirementsStrategy.OR).save(p_256428_, "adventure/kill_a_mob");
        addMobsToKill(net.minecraft.advancements.Advancement.Builder.advancement()).parent($$5).display((ItemLike)Items.DIAMOND_SWORD, Component.translatable("advancements.adventure.kill_all_mobs.title"), Component.translatable("advancements.adventure.kill_all_mobs.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).rewards(net.minecraft.advancements.AdvancementRewards.Builder.experience(100)).save(p_256428_, "adventure/kill_all_mobs");
        Advancement $$6 = net.minecraft.advancements.Advancement.Builder.advancement().parent($$5).display((ItemLike)Items.BOW, Component.translatable("advancements.adventure.shoot_arrow.title"), Component.translatable("advancements.adventure.shoot_arrow.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("shot_arrow", (CriterionTriggerInstance)net.minecraft.advancements.critereon.PlayerHurtEntityTrigger.TriggerInstance.playerHurtEntity(net.minecraft.advancements.critereon.DamagePredicate.Builder.damageInstance().type(net.minecraft.advancements.critereon.DamageSourcePredicate.Builder.damageType().tag(TagPredicate.is(DamageTypeTags.IS_PROJECTILE)).direct(Builder.entity().of(EntityTypeTags.ARROWS))))).save(p_256428_, "adventure/shoot_arrow");
        Advancement $$7 = net.minecraft.advancements.Advancement.Builder.advancement().parent($$5).display((ItemLike)Items.TRIDENT, Component.translatable("advancements.adventure.throw_trident.title"), Component.translatable("advancements.adventure.throw_trident.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("shot_trident", (CriterionTriggerInstance)net.minecraft.advancements.critereon.PlayerHurtEntityTrigger.TriggerInstance.playerHurtEntity(net.minecraft.advancements.critereon.DamagePredicate.Builder.damageInstance().type(net.minecraft.advancements.critereon.DamageSourcePredicate.Builder.damageType().tag(TagPredicate.is(DamageTypeTags.IS_PROJECTILE)).direct(Builder.entity().of(EntityType.TRIDENT))))).save(p_256428_, "adventure/throw_trident");
        net.minecraft.advancements.Advancement.Builder.advancement().parent($$7).display((ItemLike)Items.TRIDENT, Component.translatable("advancements.adventure.very_very_frightening.title"), Component.translatable("advancements.adventure.very_very_frightening.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("struck_villager", (CriterionTriggerInstance)net.minecraft.advancements.critereon.ChanneledLightningTrigger.TriggerInstance.channeledLightning(Builder.entity().of(EntityType.VILLAGER).build())).save(p_256428_, "adventure/very_very_frightening");
        net.minecraft.advancements.Advancement.Builder.advancement().parent($$4).display((ItemLike)Blocks.CARVED_PUMPKIN, Component.translatable("advancements.adventure.summon_iron_golem.title"), Component.translatable("advancements.adventure.summon_iron_golem.description"), (ResourceLocation)null, FrameType.GOAL, true, true, false).addCriterion("summoned_golem", (CriterionTriggerInstance)net.minecraft.advancements.critereon.SummonedEntityTrigger.TriggerInstance.summonedEntity(Builder.entity().of(EntityType.IRON_GOLEM))).save(p_256428_, "adventure/summon_iron_golem");
        net.minecraft.advancements.Advancement.Builder.advancement().parent($$6).display((ItemLike)Items.ARROW, Component.translatable("advancements.adventure.sniper_duel.title"), Component.translatable("advancements.adventure.sniper_duel.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).rewards(net.minecraft.advancements.AdvancementRewards.Builder.experience(50)).addCriterion("killed_skeleton", (CriterionTriggerInstance)net.minecraft.advancements.critereon.KilledTrigger.TriggerInstance.playerKilledEntity(Builder.entity().of(EntityType.SKELETON).distance(DistancePredicate.horizontal(Doubles.atLeast(50.0))), net.minecraft.advancements.critereon.DamageSourcePredicate.Builder.damageType().tag(TagPredicate.is(DamageTypeTags.IS_PROJECTILE)))).save(p_256428_, "adventure/sniper_duel");
        net.minecraft.advancements.Advancement.Builder.advancement().parent($$5).display((ItemLike)Items.TOTEM_OF_UNDYING, Component.translatable("advancements.adventure.totem_of_undying.title"), Component.translatable("advancements.adventure.totem_of_undying.description"), (ResourceLocation)null, FrameType.GOAL, true, true, false).addCriterion("used_totem", (CriterionTriggerInstance)net.minecraft.advancements.critereon.UsedTotemTrigger.TriggerInstance.usedTotem((ItemLike)Items.TOTEM_OF_UNDYING)).save(p_256428_, "adventure/totem_of_undying");
        Advancement $$8 = net.minecraft.advancements.Advancement.Builder.advancement().parent($$2).display((ItemLike)Items.CROSSBOW, Component.translatable("advancements.adventure.ol_betsy.title"), Component.translatable("advancements.adventure.ol_betsy.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("shot_crossbow", (CriterionTriggerInstance)net.minecraft.advancements.critereon.ShotCrossbowTrigger.TriggerInstance.shotCrossbow((ItemLike)Items.CROSSBOW)).save(p_256428_, "adventure/ol_betsy");
        net.minecraft.advancements.Advancement.Builder.advancement().parent($$8).display((ItemLike)Items.CROSSBOW, Component.translatable("advancements.adventure.whos_the_pillager_now.title"), Component.translatable("advancements.adventure.whos_the_pillager_now.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("kill_pillager", (CriterionTriggerInstance)net.minecraft.advancements.critereon.KilledByCrossbowTrigger.TriggerInstance.crossbowKilled(Builder.entity().of(EntityType.PILLAGER))).save(p_256428_, "adventure/whos_the_pillager_now");
        net.minecraft.advancements.Advancement.Builder.advancement().parent($$8).display((ItemLike)Items.CROSSBOW, Component.translatable("advancements.adventure.two_birds_one_arrow.title"), Component.translatable("advancements.adventure.two_birds_one_arrow.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).rewards(net.minecraft.advancements.AdvancementRewards.Builder.experience(65)).addCriterion("two_birds", (CriterionTriggerInstance)net.minecraft.advancements.critereon.KilledByCrossbowTrigger.TriggerInstance.crossbowKilled(Builder.entity().of(EntityType.PHANTOM), Builder.entity().of(EntityType.PHANTOM))).save(p_256428_, "adventure/two_birds_one_arrow");
        net.minecraft.advancements.Advancement.Builder.advancement().parent($$8).display((ItemLike)Items.CROSSBOW, Component.translatable("advancements.adventure.arbalistic.title"), Component.translatable("advancements.adventure.arbalistic.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, true).rewards(net.minecraft.advancements.AdvancementRewards.Builder.experience(85)).addCriterion("arbalistic", (CriterionTriggerInstance)net.minecraft.advancements.critereon.KilledByCrossbowTrigger.TriggerInstance.crossbowKilled(Ints.exactly(5))).save(p_256428_, "adventure/arbalistic");
        Advancement $$9 = net.minecraft.advancements.Advancement.Builder.advancement().parent($$2).display((ItemStack)Raid.getLeaderBannerInstance(), Component.translatable("advancements.adventure.voluntary_exile.title"), Component.translatable("advancements.adventure.voluntary_exile.description"), (ResourceLocation)null, FrameType.TASK, true, true, true).addCriterion("voluntary_exile", (CriterionTriggerInstance)net.minecraft.advancements.critereon.KilledTrigger.TriggerInstance.playerKilledEntity(Builder.entity().of(EntityTypeTags.RAIDERS).equipment(EntityEquipmentPredicate.CAPTAIN))).save(p_256428_, "adventure/voluntary_exile");
        net.minecraft.advancements.Advancement.Builder.advancement().parent($$9).display((ItemStack)Raid.getLeaderBannerInstance(), Component.translatable("advancements.adventure.hero_of_the_village.title"), Component.translatable("advancements.adventure.hero_of_the_village.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, true).rewards(net.minecraft.advancements.AdvancementRewards.Builder.experience(100)).addCriterion("hero_of_the_village", (CriterionTriggerInstance)net.minecraft.advancements.critereon.PlayerTrigger.TriggerInstance.raidWon()).save(p_256428_, "adventure/hero_of_the_village");
        net.minecraft.advancements.Advancement.Builder.advancement().parent($$2).display((ItemLike)Blocks.HONEY_BLOCK.asItem(), Component.translatable("advancements.adventure.honey_block_slide.title"), Component.translatable("advancements.adventure.honey_block_slide.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("honey_block_slide", (CriterionTriggerInstance)net.minecraft.advancements.critereon.SlideDownBlockTrigger.TriggerInstance.slidesDownBlock(Blocks.HONEY_BLOCK)).save(p_256428_, "adventure/honey_block_slide");
        net.minecraft.advancements.Advancement.Builder.advancement().parent($$6).display((ItemLike)Blocks.TARGET.asItem(), Component.translatable("advancements.adventure.bullseye.title"), Component.translatable("advancements.adventure.bullseye.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).rewards(net.minecraft.advancements.AdvancementRewards.Builder.experience(50)).addCriterion("bullseye", (CriterionTriggerInstance)net.minecraft.advancements.critereon.TargetBlockTrigger.TriggerInstance.targetHit(Ints.exactly(15), EntityPredicate.wrap(Builder.entity().distance(DistancePredicate.horizontal(Doubles.atLeast(30.0))).build()))).save(p_256428_, "adventure/bullseye");
        net.minecraft.advancements.Advancement.Builder.advancement().parent($$3).display((ItemLike)Items.LEATHER_BOOTS, Component.translatable("advancements.adventure.walk_on_powder_snow_with_leather_boots.title"), Component.translatable("advancements.adventure.walk_on_powder_snow_with_leather_boots.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("walk_on_powder_snow_with_leather_boots", (CriterionTriggerInstance)net.minecraft.advancements.critereon.PlayerTrigger.TriggerInstance.walkOnBlockWithEquipment(Blocks.POWDER_SNOW, Items.LEATHER_BOOTS)).save(p_256428_, "adventure/walk_on_powder_snow_with_leather_boots");
        net.minecraft.advancements.Advancement.Builder.advancement().parent($$2).display((ItemLike)Items.LIGHTNING_ROD, Component.translatable("advancements.adventure.lightning_rod_with_villager_no_fire.title"), Component.translatable("advancements.adventure.lightning_rod_with_villager_no_fire.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("lightning_rod_with_villager_no_fire", (CriterionTriggerInstance)fireCountAndBystander(Ints.exactly(0), Builder.entity().of(EntityType.VILLAGER).build())).save(p_256428_, "adventure/lightning_rod_with_villager_no_fire");
        Advancement $$10 = net.minecraft.advancements.Advancement.Builder.advancement().parent($$2).display((ItemLike)Items.SPYGLASS, Component.translatable("advancements.adventure.spyglass_at_parrot.title"), Component.translatable("advancements.adventure.spyglass_at_parrot.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("spyglass_at_parrot", (CriterionTriggerInstance)lookAtThroughItem(EntityType.PARROT, Items.SPYGLASS)).save(p_256428_, "adventure/spyglass_at_parrot");
        Advancement $$11 = net.minecraft.advancements.Advancement.Builder.advancement().parent($$10).display((ItemLike)Items.SPYGLASS, Component.translatable("advancements.adventure.spyglass_at_ghast.title"), Component.translatable("advancements.adventure.spyglass_at_ghast.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("spyglass_at_ghast", (CriterionTriggerInstance)lookAtThroughItem(EntityType.GHAST, Items.SPYGLASS)).save(p_256428_, "adventure/spyglass_at_ghast");
        net.minecraft.advancements.Advancement.Builder.advancement().parent($$3).display((ItemLike)Items.JUKEBOX, Component.translatable("advancements.adventure.play_jukebox_in_meadows.title"), Component.translatable("advancements.adventure.play_jukebox_in_meadows.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("play_jukebox_in_meadows", (CriterionTriggerInstance)net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(net.minecraft.advancements.critereon.LocationPredicate.Builder.location().setBiome(Biomes.MEADOW).setBlock(net.minecraft.advancements.critereon.BlockPredicate.Builder.block().of(Blocks.JUKEBOX).build()), net.minecraft.advancements.critereon.ItemPredicate.Builder.item().of(ItemTags.MUSIC_DISCS))).save(p_256428_, "adventure/play_jukebox_in_meadows");
        net.minecraft.advancements.Advancement.Builder.advancement().parent($$11).display((ItemLike)Items.SPYGLASS, Component.translatable("advancements.adventure.spyglass_at_dragon.title"), Component.translatable("advancements.adventure.spyglass_at_dragon.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("spyglass_at_dragon", (CriterionTriggerInstance)lookAtThroughItem(EntityType.ENDER_DRAGON, Items.SPYGLASS)).save(p_256428_, "adventure/spyglass_at_dragon");
        net.minecraft.advancements.Advancement.Builder.advancement().parent($$2).display((ItemLike)Items.WATER_BUCKET, Component.translatable("advancements.adventure.fall_from_world_height.title"), Component.translatable("advancements.adventure.fall_from_world_height.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("fall_from_world_height", (CriterionTriggerInstance)net.minecraft.advancements.critereon.DistanceTrigger.TriggerInstance.fallFromHeight(Builder.entity().located(LocationPredicate.atYLocation(Doubles.atMost(-59.0))), DistancePredicate.vertical(Doubles.atLeast(379.0)), LocationPredicate.atYLocation(Doubles.atLeast(319.0)))).save(p_256428_, "adventure/fall_from_world_height");
        net.minecraft.advancements.Advancement.Builder.advancement().parent($$5).display((ItemLike)Blocks.SCULK_CATALYST, Component.translatable("advancements.adventure.kill_mob_near_sculk_catalyst.title"), Component.translatable("advancements.adventure.kill_mob_near_sculk_catalyst.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).addCriterion("kill_mob_near_sculk_catalyst", (CriterionTriggerInstance)net.minecraft.advancements.critereon.KilledTrigger.TriggerInstance.playerKilledEntityNearSculkCatalyst()).save(p_256428_, "adventure/kill_mob_near_sculk_catalyst");
        net.minecraft.advancements.Advancement.Builder.advancement().parent($$2).display((ItemLike)Blocks.SCULK_SENSOR, Component.translatable("advancements.adventure.avoid_vibration.title"), Component.translatable("advancements.adventure.avoid_vibration.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("avoid_vibration", (CriterionTriggerInstance)net.minecraft.advancements.critereon.PlayerTrigger.TriggerInstance.avoidVibration()).save(p_256428_, "adventure/avoid_vibration");
        Advancement $$12 = respectingTheRemnantsCriterions(net.minecraft.advancements.Advancement.Builder.advancement()).parent($$2).display((ItemLike)Items.BRUSH, Component.translatable("advancements.adventure.salvage_sherd.title"), Component.translatable("advancements.adventure.salvage_sherd.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).save(p_256428_, "adventure/salvage_sherd");
        net.minecraft.advancements.Advancement.Builder.advancement().parent($$12).display((ItemStack)DecoratedPotRecipe.createDecoratedPotItem(new DecoratedPotBlockEntity.Decorations(Items.BRICK, Items.HEART_POTTERY_SHERD, Items.BRICK, Items.EXPLORER_POTTERY_SHERD)), Component.translatable("advancements.adventure.craft_decorated_pot_using_only_sherds.title"), Component.translatable("advancements.adventure.craft_decorated_pot_using_only_sherds.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("pot_crafted_using_only_sherds", (CriterionTriggerInstance)net.minecraft.advancements.critereon.RecipeCraftedTrigger.TriggerInstance.craftedItem(new ResourceLocation("minecraft:decorated_pot"), List.of(net.minecraft.advancements.critereon.ItemPredicate.Builder.item().of(ItemTags.DECORATED_POT_SHERDS).build(), net.minecraft.advancements.critereon.ItemPredicate.Builder.item().of(ItemTags.DECORATED_POT_SHERDS).build(), net.minecraft.advancements.critereon.ItemPredicate.Builder.item().of(ItemTags.DECORATED_POT_SHERDS).build(), net.minecraft.advancements.critereon.ItemPredicate.Builder.item().of(ItemTags.DECORATED_POT_SHERDS).build()))).save(p_256428_, "adventure/craft_decorated_pot_using_only_sherds");
        Advancement $$13 = craftingANewLook(net.minecraft.advancements.Advancement.Builder.advancement()).parent($$2).display((ItemStack)(new ItemStack(Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE)), Component.translatable("advancements.adventure.trim_with_any_armor_pattern.title"), Component.translatable("advancements.adventure.trim_with_any_armor_pattern.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).save(p_256428_, "adventure/trim_with_any_armor_pattern");
        smithingWithStyle(net.minecraft.advancements.Advancement.Builder.advancement()).parent($$13).display((ItemStack)(new ItemStack(Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE)), Component.translatable("advancements.adventure.trim_with_all_exclusive_armor_patterns.title"), Component.translatable("advancements.adventure.trim_with_all_exclusive_armor_patterns.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).save(p_256428_, "adventure/trim_with_all_exclusive_armor_patterns");
        net.minecraft.advancements.Advancement.Builder.advancement().parent($$2).display((ItemLike)Items.CHISELED_BOOKSHELF, Component.translatable("advancements.adventure.read_power_from_chiseled_bookshelf.title"), Component.translatable("advancements.adventure.read_power_from_chiseled_bookshelf.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).requirements(RequirementsStrategy.OR).addCriterion("chiseled_bookshelf", placedBlockReadByComparator(Blocks.CHISELED_BOOKSHELF)).addCriterion("comparator", placedComparatorReadingBlock(Blocks.CHISELED_BOOKSHELF)).save(p_256428_, "adventure/read_power_of_chiseled_bookshelf");
    }

    private static CriterionTriggerInstance placedBlockReadByComparator(Block p_286401_) {
        LootItemCondition.Builder[] $$1 = (LootItemCondition.Builder[])ComparatorBlock.FACING.getPossibleValues().stream().map((p_286187_) -> {
            StatePropertiesPredicate $$1 = net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder.properties().hasProperty(ComparatorBlock.FACING, (Comparable)p_286187_).build();
            BlockPredicate $$2 = net.minecraft.advancements.critereon.BlockPredicate.Builder.block().of(Blocks.COMPARATOR).setProperties($$1).build();
            return LocationCheck.checkLocation(net.minecraft.advancements.critereon.LocationPredicate.Builder.location().setBlock($$2), new BlockPos(p_286187_.getOpposite().getNormal()));
        }).toArray((p_286188_) -> {
            return new LootItemCondition.Builder[p_286188_];
        });
        return net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(LootItemBlockStatePropertyCondition.hasBlockStateProperties(p_286401_), AnyOfCondition.anyOf($$1));
    }

    private static CriterionTriggerInstance placedComparatorReadingBlock(Block p_286250_) {
        LootItemCondition.Builder[] $$1 = (LootItemCondition.Builder[])ComparatorBlock.FACING.getPossibleValues().stream().map((p_286190_) -> {
            StatePropertiesPredicate.Builder $$2 = net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder.properties().hasProperty(ComparatorBlock.FACING, (Comparable)p_286190_);
            LootItemBlockStatePropertyCondition.Builder $$3 = (new LootItemBlockStatePropertyCondition.Builder(Blocks.COMPARATOR)).setProperties($$2);
            LootItemCondition.Builder $$4 = LocationCheck.checkLocation(net.minecraft.advancements.critereon.LocationPredicate.Builder.location().setBlock(net.minecraft.advancements.critereon.BlockPredicate.Builder.block().of(p_286250_).build()), new BlockPos(p_286190_.getNormal()));
            return AllOfCondition.allOf($$3, $$4);
        }).toArray((p_286191_) -> {
            return new LootItemCondition.Builder[p_286191_];
        });
        return net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(AnyOfCondition.anyOf($$1));
    }

    private static Advancement.Builder smithingWithStyle(Advancement.Builder p_285368_) {
        p_285368_.requirements(RequirementsStrategy.AND);
        Map<Item, ResourceLocation> $$1 = VanillaRecipeProvider.smithingTrims();
        Stream.of(Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE, Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE, Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE, Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE).forEach((p_284946_) -> {
            ResourceLocation $$3 = (ResourceLocation)$$1.get(p_284946_);
            p_285368_.addCriterion("armor_trimmed_" + $$3, (CriterionTriggerInstance)net.minecraft.advancements.critereon.RecipeCraftedTrigger.TriggerInstance.craftedItem($$3));
        });
        return p_285368_;
    }

    private static Advancement.Builder craftingANewLook(Advancement.Builder p_285062_) {
        p_285062_.requirements(RequirementsStrategy.OR);
        Iterator var1 = VanillaRecipeProvider.smithingTrims().values().iterator();

        while(var1.hasNext()) {
            ResourceLocation $$1 = (ResourceLocation)var1.next();
            p_285062_.addCriterion("armor_trimmed_" + $$1, (CriterionTriggerInstance)net.minecraft.advancements.critereon.RecipeCraftedTrigger.TriggerInstance.craftedItem($$1));
        }

        return p_285062_;
    }

    private static Advancement.Builder respectingTheRemnantsCriterions(Advancement.Builder p_285170_) {
        p_285170_.addCriterion("desert_pyramid", (CriterionTriggerInstance)net.minecraft.advancements.critereon.LootTableTrigger.TriggerInstance.lootTableUsed(BuiltInLootTables.DESERT_PYRAMID_ARCHAEOLOGY));
        p_285170_.addCriterion("desert_well", (CriterionTriggerInstance)net.minecraft.advancements.critereon.LootTableTrigger.TriggerInstance.lootTableUsed(BuiltInLootTables.DESERT_WELL_ARCHAEOLOGY));
        p_285170_.addCriterion("ocean_ruin_cold", (CriterionTriggerInstance)net.minecraft.advancements.critereon.LootTableTrigger.TriggerInstance.lootTableUsed(BuiltInLootTables.OCEAN_RUIN_COLD_ARCHAEOLOGY));
        p_285170_.addCriterion("ocean_ruin_warm", (CriterionTriggerInstance)net.minecraft.advancements.critereon.LootTableTrigger.TriggerInstance.lootTableUsed(BuiltInLootTables.OCEAN_RUIN_WARM_ARCHAEOLOGY));
        p_285170_.addCriterion("trail_ruins_rare", (CriterionTriggerInstance)net.minecraft.advancements.critereon.LootTableTrigger.TriggerInstance.lootTableUsed(BuiltInLootTables.TRAIL_RUINS_ARCHAEOLOGY_RARE));
        p_285170_.addCriterion("trail_ruins_common", (CriterionTriggerInstance)net.minecraft.advancements.critereon.LootTableTrigger.TriggerInstance.lootTableUsed(BuiltInLootTables.TRAIL_RUINS_ARCHAEOLOGY_COMMON));
        String[] $$1 = (String[])p_285170_.getCriteria().keySet().toArray((p_285038_) -> {
            return new String[p_285038_];
        });
        String $$2 = "has_sherd";
        p_285170_.addCriterion("has_sherd", (CriterionTriggerInstance)net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems(net.minecraft.advancements.critereon.ItemPredicate.Builder.item().of(ItemTags.DECORATED_POT_SHERDS).build()));
        p_285170_.requirements(new String[][]{$$1, {"has_sherd"}});
        return p_285170_;
    }

    protected static void createAdventuringTime(Consumer<Advancement> p_275645_, Advancement p_275219_, MultiNoiseBiomeSourceParameterList.Preset p_275211_) {
        addBiomes(net.minecraft.advancements.Advancement.Builder.advancement(), p_275211_.usedBiomes().toList()).parent(p_275219_).display((ItemLike)Items.DIAMOND_BOOTS, Component.translatable("advancements.adventure.adventuring_time.title"), Component.translatable("advancements.adventure.adventuring_time.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).rewards(net.minecraft.advancements.AdvancementRewards.Builder.experience(500)).save(p_275645_, "adventure/adventuring_time");
    }

    private static Advancement.Builder addMobsToKill(Advancement.Builder p_248814_) {
        EntityType[] var1 = MOBS_TO_KILL;
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EntityType<?> $$1 = var1[var3];
            p_248814_.addCriterion(BuiltInRegistries.ENTITY_TYPE.getKey($$1).toString(), (CriterionTriggerInstance)net.minecraft.advancements.critereon.KilledTrigger.TriggerInstance.playerKilledEntity(Builder.entity().of($$1)));
        }

        return p_248814_;
    }

    protected static Advancement.Builder addBiomes(Advancement.Builder p_249250_, List<ResourceKey<Biome>> p_251338_) {
        Iterator var2 = p_251338_.iterator();

        while(var2.hasNext()) {
            ResourceKey<Biome> $$2 = (ResourceKey)var2.next();
            p_249250_.addCriterion($$2.location().toString(), (CriterionTriggerInstance)net.minecraft.advancements.critereon.PlayerTrigger.TriggerInstance.located(LocationPredicate.inBiome($$2)));
        }

        return p_249250_;
    }

    static {
        MOBS_TO_KILL = new EntityType[]{EntityType.BLAZE, EntityType.CAVE_SPIDER, EntityType.CREEPER, EntityType.DROWNED, EntityType.ELDER_GUARDIAN, EntityType.ENDER_DRAGON, EntityType.ENDERMAN, EntityType.ENDERMITE, EntityType.EVOKER, EntityType.GHAST, EntityType.GUARDIAN, EntityType.HOGLIN, EntityType.HUSK, EntityType.MAGMA_CUBE, EntityType.PHANTOM, EntityType.PIGLIN, EntityType.PIGLIN_BRUTE, EntityType.PILLAGER, EntityType.RAVAGER, EntityType.SHULKER, EntityType.SILVERFISH, EntityType.SKELETON, EntityType.SLIME, EntityType.SPIDER, EntityType.STRAY, EntityType.VEX, EntityType.VINDICATOR, EntityType.WITCH, EntityType.WITHER_SKELETON, EntityType.WITHER, EntityType.ZOGLIN, EntityType.ZOMBIE_VILLAGER, EntityType.ZOMBIE, EntityType.ZOMBIFIED_PIGLIN};
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.data.advancements.packs;

import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.Advancement.Builder;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.TagPredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;

public class VanillaStoryAdvancements implements AdvancementSubProvider {
    public VanillaStoryAdvancements() {
    }

    public void generate(HolderLookup.Provider p_256574_, Consumer<Advancement> p_248554_) {
        Advancement $$2 = Builder.advancement().display((ItemLike)Blocks.GRASS_BLOCK, Component.translatable("advancements.story.root.title"), Component.translatable("advancements.story.root.description"), new ResourceLocation("textures/gui/advancements/backgrounds/stone.png"), FrameType.TASK, false, false, false).addCriterion("crafting_table", (CriterionTriggerInstance)TriggerInstance.hasItems(Blocks.CRAFTING_TABLE)).save(p_248554_, "story/root");
        Advancement $$3 = Builder.advancement().parent($$2).display((ItemLike)Items.WOODEN_PICKAXE, Component.translatable("advancements.story.mine_stone.title"), Component.translatable("advancements.story.mine_stone.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("get_stone", (CriterionTriggerInstance)TriggerInstance.hasItems(net.minecraft.advancements.critereon.ItemPredicate.Builder.item().of(ItemTags.STONE_TOOL_MATERIALS).build())).save(p_248554_, "story/mine_stone");
        Advancement $$4 = Builder.advancement().parent($$3).display((ItemLike)Items.STONE_PICKAXE, Component.translatable("advancements.story.upgrade_tools.title"), Component.translatable("advancements.story.upgrade_tools.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("stone_pickaxe", (CriterionTriggerInstance)TriggerInstance.hasItems(Items.STONE_PICKAXE)).save(p_248554_, "story/upgrade_tools");
        Advancement $$5 = Builder.advancement().parent($$4).display((ItemLike)Items.IRON_INGOT, Component.translatable("advancements.story.smelt_iron.title"), Component.translatable("advancements.story.smelt_iron.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("iron", (CriterionTriggerInstance)TriggerInstance.hasItems(Items.IRON_INGOT)).save(p_248554_, "story/smelt_iron");
        Advancement $$6 = Builder.advancement().parent($$5).display((ItemLike)Items.IRON_PICKAXE, Component.translatable("advancements.story.iron_tools.title"), Component.translatable("advancements.story.iron_tools.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("iron_pickaxe", (CriterionTriggerInstance)TriggerInstance.hasItems(Items.IRON_PICKAXE)).save(p_248554_, "story/iron_tools");
        Advancement $$7 = Builder.advancement().parent($$6).display((ItemLike)Items.DIAMOND, Component.translatable("advancements.story.mine_diamond.title"), Component.translatable("advancements.story.mine_diamond.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("diamond", (CriterionTriggerInstance)TriggerInstance.hasItems(Items.DIAMOND)).save(p_248554_, "story/mine_diamond");
        Advancement $$8 = Builder.advancement().parent($$5).display((ItemLike)Items.LAVA_BUCKET, Component.translatable("advancements.story.lava_bucket.title"), Component.translatable("advancements.story.lava_bucket.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("lava_bucket", (CriterionTriggerInstance)TriggerInstance.hasItems(Items.LAVA_BUCKET)).save(p_248554_, "story/lava_bucket");
        Advancement $$9 = Builder.advancement().parent($$5).display((ItemLike)Items.IRON_CHESTPLATE, Component.translatable("advancements.story.obtain_armor.title"), Component.translatable("advancements.story.obtain_armor.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).requirements(RequirementsStrategy.OR).addCriterion("iron_helmet", (CriterionTriggerInstance)TriggerInstance.hasItems(Items.IRON_HELMET)).addCriterion("iron_chestplate", (CriterionTriggerInstance)TriggerInstance.hasItems(Items.IRON_CHESTPLATE)).addCriterion("iron_leggings", (CriterionTriggerInstance)TriggerInstance.hasItems(Items.IRON_LEGGINGS)).addCriterion("iron_boots", (CriterionTriggerInstance)TriggerInstance.hasItems(Items.IRON_BOOTS)).save(p_248554_, "story/obtain_armor");
        Builder.advancement().parent($$7).display((ItemLike)Items.ENCHANTED_BOOK, Component.translatable("advancements.story.enchant_item.title"), Component.translatable("advancements.story.enchant_item.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("enchanted_item", (CriterionTriggerInstance)net.minecraft.advancements.critereon.EnchantedItemTrigger.TriggerInstance.enchantedItem()).save(p_248554_, "story/enchant_item");
        Advancement $$10 = Builder.advancement().parent($$8).display((ItemLike)Blocks.OBSIDIAN, Component.translatable("advancements.story.form_obsidian.title"), Component.translatable("advancements.story.form_obsidian.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("obsidian", (CriterionTriggerInstance)TriggerInstance.hasItems(Blocks.OBSIDIAN)).save(p_248554_, "story/form_obsidian");
        Builder.advancement().parent($$9).display((ItemLike)Items.SHIELD, Component.translatable("advancements.story.deflect_arrow.title"), Component.translatable("advancements.story.deflect_arrow.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("deflected_projectile", (CriterionTriggerInstance)net.minecraft.advancements.critereon.EntityHurtPlayerTrigger.TriggerInstance.entityHurtPlayer(net.minecraft.advancements.critereon.DamagePredicate.Builder.damageInstance().type(net.minecraft.advancements.critereon.DamageSourcePredicate.Builder.damageType().tag(TagPredicate.is(DamageTypeTags.IS_PROJECTILE))).blocked(true))).save(p_248554_, "story/deflect_arrow");
        Builder.advancement().parent($$7).display((ItemLike)Items.DIAMOND_CHESTPLATE, Component.translatable("advancements.story.shiny_gear.title"), Component.translatable("advancements.story.shiny_gear.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).requirements(RequirementsStrategy.OR).addCriterion("diamond_helmet", (CriterionTriggerInstance)TriggerInstance.hasItems(Items.DIAMOND_HELMET)).addCriterion("diamond_chestplate", (CriterionTriggerInstance)TriggerInstance.hasItems(Items.DIAMOND_CHESTPLATE)).addCriterion("diamond_leggings", (CriterionTriggerInstance)TriggerInstance.hasItems(Items.DIAMOND_LEGGINGS)).addCriterion("diamond_boots", (CriterionTriggerInstance)TriggerInstance.hasItems(Items.DIAMOND_BOOTS)).save(p_248554_, "story/shiny_gear");
        Advancement $$11 = Builder.advancement().parent($$10).display((ItemLike)Items.FLINT_AND_STEEL, Component.translatable("advancements.story.enter_the_nether.title"), Component.translatable("advancements.story.enter_the_nether.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("entered_nether", (CriterionTriggerInstance)net.minecraft.advancements.critereon.ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(Level.NETHER)).save(p_248554_, "story/enter_the_nether");
        Builder.advancement().parent($$11).display((ItemLike)Items.GOLDEN_APPLE, Component.translatable("advancements.story.cure_zombie_villager.title"), Component.translatable("advancements.story.cure_zombie_villager.description"), (ResourceLocation)null, FrameType.GOAL, true, true, false).addCriterion("cured_zombie", (CriterionTriggerInstance)net.minecraft.advancements.critereon.CuredZombieVillagerTrigger.TriggerInstance.curedZombieVillager()).save(p_248554_, "story/cure_zombie_villager");
        Advancement $$12 = Builder.advancement().parent($$11).display((ItemLike)Items.ENDER_EYE, Component.translatable("advancements.story.follow_ender_eye.title"), Component.translatable("advancements.story.follow_ender_eye.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("in_stronghold", (CriterionTriggerInstance)net.minecraft.advancements.critereon.PlayerTrigger.TriggerInstance.located(LocationPredicate.inStructure(BuiltinStructures.STRONGHOLD))).save(p_248554_, "story/follow_ender_eye");
        Builder.advancement().parent($$12).display((ItemLike)Blocks.END_STONE, Component.translatable("advancements.story.enter_the_end.title"), Component.translatable("advancements.story.enter_the_end.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("entered_end", (CriterionTriggerInstance)net.minecraft.advancements.critereon.ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(Level.END)).save(p_248554_, "story/enter_the_end");
    }
}

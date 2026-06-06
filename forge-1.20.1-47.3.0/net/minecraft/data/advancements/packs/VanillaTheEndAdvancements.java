//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.data.advancements.packs;

import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.Advancement.Builder;
import net.minecraft.advancements.critereon.DistancePredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.ChangeDimensionTrigger.TriggerInstance;
import net.minecraft.advancements.critereon.MinMaxBounds.Doubles;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;

public class VanillaTheEndAdvancements implements AdvancementSubProvider {
    public VanillaTheEndAdvancements() {
    }

    public void generate(HolderLookup.Provider p_256214_, Consumer<Advancement> p_250851_) {
        Advancement $$2 = Builder.advancement().display((ItemLike)Blocks.END_STONE, Component.translatable("advancements.end.root.title"), Component.translatable("advancements.end.root.description"), new ResourceLocation("textures/gui/advancements/backgrounds/end.png"), FrameType.TASK, false, false, false).addCriterion("entered_end", (CriterionTriggerInstance)TriggerInstance.changedDimensionTo(Level.END)).save(p_250851_, "end/root");
        Advancement $$3 = Builder.advancement().parent($$2).display((ItemLike)Blocks.DRAGON_HEAD, Component.translatable("advancements.end.kill_dragon.title"), Component.translatable("advancements.end.kill_dragon.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("killed_dragon", (CriterionTriggerInstance)net.minecraft.advancements.critereon.KilledTrigger.TriggerInstance.playerKilledEntity(net.minecraft.advancements.critereon.EntityPredicate.Builder.entity().of(EntityType.ENDER_DRAGON))).save(p_250851_, "end/kill_dragon");
        Advancement $$4 = Builder.advancement().parent($$3).display((ItemLike)Items.ENDER_PEARL, Component.translatable("advancements.end.enter_end_gateway.title"), Component.translatable("advancements.end.enter_end_gateway.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("entered_end_gateway", (CriterionTriggerInstance)net.minecraft.advancements.critereon.EnterBlockTrigger.TriggerInstance.entersBlock(Blocks.END_GATEWAY)).save(p_250851_, "end/enter_end_gateway");
        Builder.advancement().parent($$3).display((ItemLike)Items.END_CRYSTAL, Component.translatable("advancements.end.respawn_dragon.title"), Component.translatable("advancements.end.respawn_dragon.description"), (ResourceLocation)null, FrameType.GOAL, true, true, false).addCriterion("summoned_dragon", (CriterionTriggerInstance)net.minecraft.advancements.critereon.SummonedEntityTrigger.TriggerInstance.summonedEntity(net.minecraft.advancements.critereon.EntityPredicate.Builder.entity().of(EntityType.ENDER_DRAGON))).save(p_250851_, "end/respawn_dragon");
        Advancement $$5 = Builder.advancement().parent($$4).display((ItemLike)Blocks.PURPUR_BLOCK, Component.translatable("advancements.end.find_end_city.title"), Component.translatable("advancements.end.find_end_city.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("in_city", (CriterionTriggerInstance)net.minecraft.advancements.critereon.PlayerTrigger.TriggerInstance.located(LocationPredicate.inStructure(BuiltinStructures.END_CITY))).save(p_250851_, "end/find_end_city");
        Builder.advancement().parent($$3).display((ItemLike)Items.DRAGON_BREATH, Component.translatable("advancements.end.dragon_breath.title"), Component.translatable("advancements.end.dragon_breath.description"), (ResourceLocation)null, FrameType.GOAL, true, true, false).addCriterion("dragon_breath", (CriterionTriggerInstance)net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems(Items.DRAGON_BREATH)).save(p_250851_, "end/dragon_breath");
        Builder.advancement().parent($$5).display((ItemLike)Items.SHULKER_SHELL, Component.translatable("advancements.end.levitate.title"), Component.translatable("advancements.end.levitate.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).rewards(net.minecraft.advancements.AdvancementRewards.Builder.experience(50)).addCriterion("levitated", (CriterionTriggerInstance)net.minecraft.advancements.critereon.LevitationTrigger.TriggerInstance.levitated(DistancePredicate.vertical(Doubles.atLeast(50.0)))).save(p_250851_, "end/levitate");
        Builder.advancement().parent($$5).display((ItemLike)Items.ELYTRA, Component.translatable("advancements.end.elytra.title"), Component.translatable("advancements.end.elytra.description"), (ResourceLocation)null, FrameType.GOAL, true, true, false).addCriterion("elytra", (CriterionTriggerInstance)net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems(Items.ELYTRA)).save(p_250851_, "end/elytra");
        Builder.advancement().parent($$3).display((ItemLike)Blocks.DRAGON_EGG, Component.translatable("advancements.end.dragon_egg.title"), Component.translatable("advancements.end.dragon_egg.description"), (ResourceLocation)null, FrameType.GOAL, true, true, false).addCriterion("dragon_egg", (CriterionTriggerInstance)net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.DRAGON_EGG)).save(p_250851_, "end/dragon_egg");
    }
}

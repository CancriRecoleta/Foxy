//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.data.advancements.packs;

import com.google.common.collect.BiMap;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.Advancement.Builder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.ConsumeItemTrigger.TriggerInstance;
import net.minecraft.advancements.critereon.MinMaxBounds.Ints;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

public class VanillaHusbandryAdvancements implements AdvancementSubProvider {
    public static final List<EntityType<?>> BREEDABLE_ANIMALS;
    public static final List<EntityType<?>> INDIRECTLY_BREEDABLE_ANIMALS;
    private static final Item[] FISH;
    private static final Item[] FISH_BUCKETS;
    private static final Item[] EDIBLE_ITEMS;
    private static final Item[] WAX_SCRAPING_TOOLS;

    public VanillaHusbandryAdvancements() {
    }

    public void generate(HolderLookup.Provider p_255680_, Consumer<Advancement> p_251389_) {
        Advancement $$2 = Builder.advancement().display((ItemLike)Blocks.HAY_BLOCK, Component.translatable("advancements.husbandry.root.title"), Component.translatable("advancements.husbandry.root.description"), new ResourceLocation("textures/gui/advancements/backgrounds/husbandry.png"), FrameType.TASK, false, false, false).addCriterion("consumed_item", (CriterionTriggerInstance)TriggerInstance.usedItem()).save(p_251389_, "husbandry/root");
        Advancement $$3 = Builder.advancement().parent($$2).display((ItemLike)Items.WHEAT, Component.translatable("advancements.husbandry.plant_seed.title"), Component.translatable("advancements.husbandry.plant_seed.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).requirements(RequirementsStrategy.OR).addCriterion("wheat", (CriterionTriggerInstance)net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(Blocks.WHEAT)).addCriterion("pumpkin_stem", (CriterionTriggerInstance)net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(Blocks.PUMPKIN_STEM)).addCriterion("melon_stem", (CriterionTriggerInstance)net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(Blocks.MELON_STEM)).addCriterion("beetroots", (CriterionTriggerInstance)net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(Blocks.BEETROOTS)).addCriterion("nether_wart", (CriterionTriggerInstance)net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(Blocks.NETHER_WART)).addCriterion("torchflower", (CriterionTriggerInstance)net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(Blocks.TORCHFLOWER_CROP)).addCriterion("pitcher_pod", (CriterionTriggerInstance)net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(Blocks.PITCHER_CROP)).save(p_251389_, "husbandry/plant_seed");
        Advancement $$4 = Builder.advancement().parent($$2).display((ItemLike)Items.WHEAT, Component.translatable("advancements.husbandry.breed_an_animal.title"), Component.translatable("advancements.husbandry.breed_an_animal.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).requirements(RequirementsStrategy.OR).addCriterion("bred", (CriterionTriggerInstance)net.minecraft.advancements.critereon.BredAnimalsTrigger.TriggerInstance.bredAnimals()).save(p_251389_, "husbandry/breed_an_animal");
        createBreedAllAnimalsAdvancement($$4, p_251389_, BREEDABLE_ANIMALS.stream(), INDIRECTLY_BREEDABLE_ANIMALS.stream());
        addFood(Builder.advancement()).parent($$3).display((ItemLike)Items.APPLE, Component.translatable("advancements.husbandry.balanced_diet.title"), Component.translatable("advancements.husbandry.balanced_diet.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).rewards(net.minecraft.advancements.AdvancementRewards.Builder.experience(100)).save(p_251389_, "husbandry/balanced_diet");
        Builder.advancement().parent($$3).display((ItemLike)Items.NETHERITE_HOE, Component.translatable("advancements.husbandry.netherite_hoe.title"), Component.translatable("advancements.husbandry.netherite_hoe.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).rewards(net.minecraft.advancements.AdvancementRewards.Builder.experience(100)).addCriterion("netherite_hoe", (CriterionTriggerInstance)net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems(Items.NETHERITE_HOE)).save(p_251389_, "husbandry/obtain_netherite_hoe");
        Advancement $$5 = Builder.advancement().parent($$2).display((ItemLike)Items.LEAD, Component.translatable("advancements.husbandry.tame_an_animal.title"), Component.translatable("advancements.husbandry.tame_an_animal.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("tamed_animal", (CriterionTriggerInstance)net.minecraft.advancements.critereon.TameAnimalTrigger.TriggerInstance.tamedAnimal()).save(p_251389_, "husbandry/tame_an_animal");
        Advancement $$6 = addFish(Builder.advancement()).parent($$2).requirements(RequirementsStrategy.OR).display((ItemLike)Items.FISHING_ROD, Component.translatable("advancements.husbandry.fishy_business.title"), Component.translatable("advancements.husbandry.fishy_business.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).save(p_251389_, "husbandry/fishy_business");
        Advancement $$7 = addFishBuckets(Builder.advancement()).parent($$6).requirements(RequirementsStrategy.OR).display((ItemLike)Items.PUFFERFISH_BUCKET, Component.translatable("advancements.husbandry.tactical_fishing.title"), Component.translatable("advancements.husbandry.tactical_fishing.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).save(p_251389_, "husbandry/tactical_fishing");
        Advancement $$8 = Builder.advancement().parent($$7).requirements(RequirementsStrategy.OR).addCriterion(BuiltInRegistries.ITEM.getKey(Items.AXOLOTL_BUCKET).getPath(), (CriterionTriggerInstance)net.minecraft.advancements.critereon.FilledBucketTrigger.TriggerInstance.filledBucket(net.minecraft.advancements.critereon.ItemPredicate.Builder.item().of(Items.AXOLOTL_BUCKET).build())).display((ItemLike)Items.AXOLOTL_BUCKET, Component.translatable("advancements.husbandry.axolotl_in_a_bucket.title"), Component.translatable("advancements.husbandry.axolotl_in_a_bucket.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).save(p_251389_, "husbandry/axolotl_in_a_bucket");
        Builder.advancement().parent($$8).addCriterion("kill_axolotl_target", (CriterionTriggerInstance)net.minecraft.advancements.critereon.EffectsChangedTrigger.TriggerInstance.gotEffectsFrom(net.minecraft.advancements.critereon.EntityPredicate.Builder.entity().of(EntityType.AXOLOTL).build())).display((ItemLike)Items.TROPICAL_FISH_BUCKET, Component.translatable("advancements.husbandry.kill_axolotl_target.title"), Component.translatable("advancements.husbandry.kill_axolotl_target.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).save(p_251389_, "husbandry/kill_axolotl_target");
        addCatVariants(Builder.advancement()).parent($$5).display((ItemLike)Items.COD, Component.translatable("advancements.husbandry.complete_catalogue.title"), Component.translatable("advancements.husbandry.complete_catalogue.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).rewards(net.minecraft.advancements.AdvancementRewards.Builder.experience(50)).save(p_251389_, "husbandry/complete_catalogue");
        Advancement $$9 = Builder.advancement().parent($$2).addCriterion("safely_harvest_honey", (CriterionTriggerInstance)net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(net.minecraft.advancements.critereon.LocationPredicate.Builder.location().setBlock(net.minecraft.advancements.critereon.BlockPredicate.Builder.block().of(BlockTags.BEEHIVES).build()).setSmokey(true), net.minecraft.advancements.critereon.ItemPredicate.Builder.item().of(Items.GLASS_BOTTLE))).display((ItemLike)Items.HONEY_BOTTLE, Component.translatable("advancements.husbandry.safely_harvest_honey.title"), Component.translatable("advancements.husbandry.safely_harvest_honey.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).save(p_251389_, "husbandry/safely_harvest_honey");
        Advancement $$10 = Builder.advancement().parent($$9).display((ItemLike)Items.HONEYCOMB, Component.translatable("advancements.husbandry.wax_on.title"), Component.translatable("advancements.husbandry.wax_on.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("wax_on", (CriterionTriggerInstance)net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(net.minecraft.advancements.critereon.LocationPredicate.Builder.location().setBlock(net.minecraft.advancements.critereon.BlockPredicate.Builder.block().of((Iterable)((BiMap)HoneycombItem.WAXABLES.get()).keySet()).build()), net.minecraft.advancements.critereon.ItemPredicate.Builder.item().of(Items.HONEYCOMB))).save(p_251389_, "husbandry/wax_on");
        Builder.advancement().parent($$10).display((ItemLike)Items.STONE_AXE, Component.translatable("advancements.husbandry.wax_off.title"), Component.translatable("advancements.husbandry.wax_off.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("wax_off", (CriterionTriggerInstance)net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(net.minecraft.advancements.critereon.LocationPredicate.Builder.location().setBlock(net.minecraft.advancements.critereon.BlockPredicate.Builder.block().of((Iterable)((BiMap)HoneycombItem.WAX_OFF_BY_BLOCK.get()).keySet()).build()), net.minecraft.advancements.critereon.ItemPredicate.Builder.item().of((ItemLike[])WAX_SCRAPING_TOOLS))).save(p_251389_, "husbandry/wax_off");
        Advancement $$11 = Builder.advancement().parent($$2).addCriterion(BuiltInRegistries.ITEM.getKey(Items.TADPOLE_BUCKET).getPath(), (CriterionTriggerInstance)net.minecraft.advancements.critereon.FilledBucketTrigger.TriggerInstance.filledBucket(net.minecraft.advancements.critereon.ItemPredicate.Builder.item().of(Items.TADPOLE_BUCKET).build())).display((ItemLike)Items.TADPOLE_BUCKET, Component.translatable("advancements.husbandry.tadpole_in_a_bucket.title"), Component.translatable("advancements.husbandry.tadpole_in_a_bucket.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).save(p_251389_, "husbandry/tadpole_in_a_bucket");
        Advancement $$12 = addLeashedFrogVariants(Builder.advancement()).parent($$11).display((ItemLike)Items.LEAD, Component.translatable("advancements.husbandry.leash_all_frog_variants.title"), Component.translatable("advancements.husbandry.leash_all_frog_variants.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).save(p_251389_, "husbandry/leash_all_frog_variants");
        Builder.advancement().parent($$12).display((ItemLike)Items.VERDANT_FROGLIGHT, Component.translatable("advancements.husbandry.froglights.title"), Component.translatable("advancements.husbandry.froglights.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).addCriterion("froglights", (CriterionTriggerInstance)net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems(Items.OCHRE_FROGLIGHT, Items.PEARLESCENT_FROGLIGHT, Items.VERDANT_FROGLIGHT)).save(p_251389_, "husbandry/froglights");
        Builder.advancement().parent($$2).addCriterion("silk_touch_nest", (CriterionTriggerInstance)net.minecraft.advancements.critereon.BeeNestDestroyedTrigger.TriggerInstance.destroyedBeeNest(Blocks.BEE_NEST, net.minecraft.advancements.critereon.ItemPredicate.Builder.item().hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, Ints.atLeast(1))), Ints.exactly(3))).display((ItemLike)Blocks.BEE_NEST, Component.translatable("advancements.husbandry.silk_touch_nest.title"), Component.translatable("advancements.husbandry.silk_touch_nest.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).save(p_251389_, "husbandry/silk_touch_nest");
        Builder.advancement().parent($$2).display((ItemLike)Items.OAK_BOAT, Component.translatable("advancements.husbandry.ride_a_boat_with_a_goat.title"), Component.translatable("advancements.husbandry.ride_a_boat_with_a_goat.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("ride_a_boat_with_a_goat", (CriterionTriggerInstance)net.minecraft.advancements.critereon.StartRidingTrigger.TriggerInstance.playerStartsRiding(net.minecraft.advancements.critereon.EntityPredicate.Builder.entity().vehicle(net.minecraft.advancements.critereon.EntityPredicate.Builder.entity().of(EntityType.BOAT).passenger(net.minecraft.advancements.critereon.EntityPredicate.Builder.entity().of(EntityType.GOAT).build()).build()))).save(p_251389_, "husbandry/ride_a_boat_with_a_goat");
        Builder.advancement().parent($$2).display((ItemLike)Items.GLOW_INK_SAC, Component.translatable("advancements.husbandry.make_a_sign_glow.title"), Component.translatable("advancements.husbandry.make_a_sign_glow.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("make_a_sign_glow", (CriterionTriggerInstance)net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(net.minecraft.advancements.critereon.LocationPredicate.Builder.location().setBlock(net.minecraft.advancements.critereon.BlockPredicate.Builder.block().of(BlockTags.ALL_SIGNS).build()), net.minecraft.advancements.critereon.ItemPredicate.Builder.item().of(Items.GLOW_INK_SAC))).save(p_251389_, "husbandry/make_a_sign_glow");
        Advancement $$13 = Builder.advancement().parent($$2).display((ItemLike)Items.COOKIE, Component.translatable("advancements.husbandry.allay_deliver_item_to_player.title"), Component.translatable("advancements.husbandry.allay_deliver_item_to_player.description"), (ResourceLocation)null, FrameType.TASK, true, true, true).addCriterion("allay_deliver_item_to_player", (CriterionTriggerInstance)net.minecraft.advancements.critereon.PickedUpItemTrigger.TriggerInstance.thrownItemPickedUpByPlayer(ContextAwarePredicate.ANY, ItemPredicate.ANY, EntityPredicate.wrap(net.minecraft.advancements.critereon.EntityPredicate.Builder.entity().of(EntityType.ALLAY).build()))).save(p_251389_, "husbandry/allay_deliver_item_to_player");
        Builder.advancement().parent($$13).display((ItemLike)Items.NOTE_BLOCK, Component.translatable("advancements.husbandry.allay_deliver_cake_to_note_block.title"), Component.translatable("advancements.husbandry.allay_deliver_cake_to_note_block.description"), (ResourceLocation)null, FrameType.TASK, true, true, true).addCriterion("allay_deliver_cake_to_note_block", (CriterionTriggerInstance)net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger.TriggerInstance.allayDropItemOnBlock(net.minecraft.advancements.critereon.LocationPredicate.Builder.location().setBlock(net.minecraft.advancements.critereon.BlockPredicate.Builder.block().of(Blocks.NOTE_BLOCK).build()), net.minecraft.advancements.critereon.ItemPredicate.Builder.item().of(Items.CAKE))).save(p_251389_, "husbandry/allay_deliver_cake_to_note_block");
        Advancement $$14 = Builder.advancement().parent($$2).display((ItemLike)Items.SNIFFER_EGG, Component.translatable("advancements.husbandry.obtain_sniffer_egg.title"), Component.translatable("advancements.husbandry.obtain_sniffer_egg.description"), (ResourceLocation)null, FrameType.TASK, true, true, true).addCriterion("obtain_sniffer_egg", (CriterionTriggerInstance)net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems(Items.SNIFFER_EGG)).save(p_251389_, "husbandry/obtain_sniffer_egg");
        Advancement $$15 = Builder.advancement().parent($$14).display((ItemLike)Items.TORCHFLOWER_SEEDS, Component.translatable("advancements.husbandry.feed_snifflet.title"), Component.translatable("advancements.husbandry.feed_snifflet.description"), (ResourceLocation)null, FrameType.TASK, true, true, true).addCriterion("feed_snifflet", (CriterionTriggerInstance)net.minecraft.advancements.critereon.PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(net.minecraft.advancements.critereon.ItemPredicate.Builder.item().of(ItemTags.SNIFFER_FOOD), EntityPredicate.wrap(net.minecraft.advancements.critereon.EntityPredicate.Builder.entity().of(EntityType.SNIFFER).flags(net.minecraft.advancements.critereon.EntityFlagsPredicate.Builder.flags().setIsBaby(true).build()).build()))).save(p_251389_, "husbandry/feed_snifflet");
        Builder.advancement().parent($$15).display((ItemLike)Items.PITCHER_POD, Component.translatable("advancements.husbandry.plant_any_sniffer_seed.title"), Component.translatable("advancements.husbandry.plant_any_sniffer_seed.description"), (ResourceLocation)null, FrameType.TASK, true, true, true).requirements(RequirementsStrategy.OR).addCriterion("torchflower", (CriterionTriggerInstance)net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(Blocks.TORCHFLOWER_CROP)).addCriterion("pitcher_pod", (CriterionTriggerInstance)net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(Blocks.PITCHER_CROP)).save(p_251389_, "husbandry/plant_any_sniffer_seed");
    }

    public static Advancement createBreedAllAnimalsAdvancement(Advancement p_267284_, Consumer<Advancement> p_266923_, Stream<EntityType<?>> p_266961_, Stream<EntityType<?>> p_266751_) {
        return addBreedable(Builder.advancement(), p_266961_, p_266751_).parent(p_267284_).display((ItemLike)Items.GOLDEN_CARROT, Component.translatable("advancements.husbandry.breed_all_animals.title"), Component.translatable("advancements.husbandry.breed_all_animals.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).rewards(net.minecraft.advancements.AdvancementRewards.Builder.experience(100)).save(p_266923_, "husbandry/bred_all_animals");
    }

    private static Advancement.Builder addLeashedFrogVariants(Advancement.Builder p_249739_) {
        BuiltInRegistries.FROG_VARIANT.holders().forEach((p_286193_) -> {
            p_249739_.addCriterion(p_286193_.key().location().toString(), (CriterionTriggerInstance)net.minecraft.advancements.critereon.PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(net.minecraft.advancements.critereon.ItemPredicate.Builder.item().of(Items.LEAD), EntityPredicate.wrap(net.minecraft.advancements.critereon.EntityPredicate.Builder.entity().of(EntityType.FROG).subPredicate(EntitySubPredicate.variant((FrogVariant)p_286193_.value())).build())));
        });
        return p_249739_;
    }

    private static Advancement.Builder addFood(Advancement.Builder p_248532_) {
        Item[] var1 = EDIBLE_ITEMS;
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            Item $$1 = var1[var3];
            p_248532_.addCriterion(BuiltInRegistries.ITEM.getKey($$1).getPath(), (CriterionTriggerInstance)TriggerInstance.usedItem((ItemLike)$$1));
        }

        return p_248532_;
    }

    private static Advancement.Builder addBreedable(Advancement.Builder p_266978_, Stream<EntityType<?>> p_267147_, Stream<EntityType<?>> p_267091_) {
        p_267147_.forEach((p_266621_) -> {
            p_266978_.addCriterion(EntityType.getKey(p_266621_).toString(), (CriterionTriggerInstance)net.minecraft.advancements.critereon.BredAnimalsTrigger.TriggerInstance.bredAnimals(net.minecraft.advancements.critereon.EntityPredicate.Builder.entity().of(p_266621_)));
        });
        p_267091_.forEach((p_266619_) -> {
            p_266978_.addCriterion(EntityType.getKey(p_266619_).toString(), (CriterionTriggerInstance)net.minecraft.advancements.critereon.BredAnimalsTrigger.TriggerInstance.bredAnimals(net.minecraft.advancements.critereon.EntityPredicate.Builder.entity().of(p_266619_).build(), net.minecraft.advancements.critereon.EntityPredicate.Builder.entity().of(p_266619_).build(), EntityPredicate.ANY));
        });
        return p_266978_;
    }

    private static Advancement.Builder addFishBuckets(Advancement.Builder p_249285_) {
        Item[] var1 = FISH_BUCKETS;
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            Item $$1 = var1[var3];
            p_249285_.addCriterion(BuiltInRegistries.ITEM.getKey($$1).getPath(), (CriterionTriggerInstance)net.minecraft.advancements.critereon.FilledBucketTrigger.TriggerInstance.filledBucket(net.minecraft.advancements.critereon.ItemPredicate.Builder.item().of($$1).build()));
        }

        return p_249285_;
    }

    private static Advancement.Builder addFish(Advancement.Builder p_248725_) {
        Item[] var1 = FISH;
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            Item $$1 = var1[var3];
            p_248725_.addCriterion(BuiltInRegistries.ITEM.getKey($$1).getPath(), (CriterionTriggerInstance)net.minecraft.advancements.critereon.FishingRodHookedTrigger.TriggerInstance.fishedItem(ItemPredicate.ANY, EntityPredicate.ANY, net.minecraft.advancements.critereon.ItemPredicate.Builder.item().of($$1).build()));
        }

        return p_248725_;
    }

    private static Advancement.Builder addCatVariants(Advancement.Builder p_249232_) {
        BuiltInRegistries.CAT_VARIANT.entrySet().stream().sorted(Entry.comparingByKey(Comparator.comparing(ResourceKey::location))).forEach((p_249721_) -> {
            p_249232_.addCriterion(((ResourceKey)p_249721_.getKey()).location().toString(), (CriterionTriggerInstance)net.minecraft.advancements.critereon.TameAnimalTrigger.TriggerInstance.tamedAnimal(net.minecraft.advancements.critereon.EntityPredicate.Builder.entity().subPredicate(EntitySubPredicate.variant((CatVariant)p_249721_.getValue())).build()));
        });
        return p_249232_;
    }

    static {
        BREEDABLE_ANIMALS = List.of(EntityType.HORSE, EntityType.DONKEY, EntityType.MULE, EntityType.SHEEP, EntityType.COW, EntityType.MOOSHROOM, EntityType.PIG, EntityType.CHICKEN, EntityType.WOLF, EntityType.OCELOT, EntityType.RABBIT, EntityType.LLAMA, EntityType.CAT, EntityType.PANDA, EntityType.FOX, EntityType.BEE, EntityType.HOGLIN, EntityType.STRIDER, EntityType.GOAT, EntityType.AXOLOTL, EntityType.CAMEL);
        INDIRECTLY_BREEDABLE_ANIMALS = List.of(EntityType.TURTLE, EntityType.FROG, EntityType.SNIFFER);
        FISH = new Item[]{Items.COD, Items.TROPICAL_FISH, Items.PUFFERFISH, Items.SALMON};
        FISH_BUCKETS = new Item[]{Items.COD_BUCKET, Items.TROPICAL_FISH_BUCKET, Items.PUFFERFISH_BUCKET, Items.SALMON_BUCKET};
        EDIBLE_ITEMS = new Item[]{Items.APPLE, Items.MUSHROOM_STEW, Items.BREAD, Items.PORKCHOP, Items.COOKED_PORKCHOP, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE, Items.COD, Items.SALMON, Items.TROPICAL_FISH, Items.PUFFERFISH, Items.COOKED_COD, Items.COOKED_SALMON, Items.COOKIE, Items.MELON_SLICE, Items.BEEF, Items.COOKED_BEEF, Items.CHICKEN, Items.COOKED_CHICKEN, Items.ROTTEN_FLESH, Items.SPIDER_EYE, Items.CARROT, Items.POTATO, Items.BAKED_POTATO, Items.POISONOUS_POTATO, Items.GOLDEN_CARROT, Items.PUMPKIN_PIE, Items.RABBIT, Items.COOKED_RABBIT, Items.RABBIT_STEW, Items.MUTTON, Items.COOKED_MUTTON, Items.CHORUS_FRUIT, Items.BEETROOT, Items.BEETROOT_SOUP, Items.DRIED_KELP, Items.SUSPICIOUS_STEW, Items.SWEET_BERRIES, Items.HONEY_BOTTLE, Items.GLOW_BERRIES};
        WAX_SCRAPING_TOOLS = new Item[]{Items.WOODEN_AXE, Items.GOLDEN_AXE, Items.STONE_AXE, Items.IRON_AXE, Items.DIAMOND_AXE, Items.NETHERITE_AXE};
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.entity;

import com.google.common.collect.ImmutableSet;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Salmon;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.animal.horse.Donkey;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.animal.horse.Mule;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.animal.horse.TraderLlama;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.GlowItemFrame;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Giant;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Illusioner;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.entity.projectile.ThrownExperienceBottle;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraft.world.entity.vehicle.MinecartCommandBlock;
import net.minecraft.world.entity.vehicle.MinecartFurnace;
import net.minecraft.world.entity.vehicle.MinecartHopper;
import net.minecraft.world.entity.vehicle.MinecartSpawner;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.PlayMessages;
import org.slf4j.Logger;

public class EntityType<T extends Entity> implements FeatureElement, EntityTypeTest<Entity, T> {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String ENTITY_TAG = "EntityTag";
    private final Holder.Reference<EntityType<?>> builtInRegistryHolder;
    private static final float MAGIC_HORSE_WIDTH = 1.3964844F;
    private static final int DISPLAY_TRACKING_RANGE = 10;
    public static final EntityType<Allay> ALLAY;
    public static final EntityType<AreaEffectCloud> AREA_EFFECT_CLOUD;
    public static final EntityType<ArmorStand> ARMOR_STAND;
    public static final EntityType<Arrow> ARROW;
    public static final EntityType<Axolotl> AXOLOTL;
    public static final EntityType<Bat> BAT;
    public static final EntityType<Bee> BEE;
    public static final EntityType<Blaze> BLAZE;
    public static final EntityType<Display.BlockDisplay> BLOCK_DISPLAY;
    public static final EntityType<Boat> BOAT;
    public static final EntityType<Camel> CAMEL;
    public static final EntityType<Cat> CAT;
    public static final EntityType<CaveSpider> CAVE_SPIDER;
    public static final EntityType<ChestBoat> CHEST_BOAT;
    public static final EntityType<MinecartChest> CHEST_MINECART;
    public static final EntityType<Chicken> CHICKEN;
    public static final EntityType<Cod> COD;
    public static final EntityType<MinecartCommandBlock> COMMAND_BLOCK_MINECART;
    public static final EntityType<Cow> COW;
    public static final EntityType<Creeper> CREEPER;
    public static final EntityType<Dolphin> DOLPHIN;
    public static final EntityType<Donkey> DONKEY;
    public static final EntityType<DragonFireball> DRAGON_FIREBALL;
    public static final EntityType<Drowned> DROWNED;
    public static final EntityType<ThrownEgg> EGG;
    public static final EntityType<ElderGuardian> ELDER_GUARDIAN;
    public static final EntityType<EndCrystal> END_CRYSTAL;
    public static final EntityType<EnderDragon> ENDER_DRAGON;
    public static final EntityType<ThrownEnderpearl> ENDER_PEARL;
    public static final EntityType<EnderMan> ENDERMAN;
    public static final EntityType<Endermite> ENDERMITE;
    public static final EntityType<Evoker> EVOKER;
    public static final EntityType<EvokerFangs> EVOKER_FANGS;
    public static final EntityType<ThrownExperienceBottle> EXPERIENCE_BOTTLE;
    public static final EntityType<ExperienceOrb> EXPERIENCE_ORB;
    public static final EntityType<EyeOfEnder> EYE_OF_ENDER;
    public static final EntityType<FallingBlockEntity> FALLING_BLOCK;
    public static final EntityType<FireworkRocketEntity> FIREWORK_ROCKET;
    public static final EntityType<Fox> FOX;
    public static final EntityType<Frog> FROG;
    public static final EntityType<MinecartFurnace> FURNACE_MINECART;
    public static final EntityType<Ghast> GHAST;
    public static final EntityType<Giant> GIANT;
    public static final EntityType<GlowItemFrame> GLOW_ITEM_FRAME;
    public static final EntityType<GlowSquid> GLOW_SQUID;
    public static final EntityType<Goat> GOAT;
    public static final EntityType<Guardian> GUARDIAN;
    public static final EntityType<Hoglin> HOGLIN;
    public static final EntityType<MinecartHopper> HOPPER_MINECART;
    public static final EntityType<Horse> HORSE;
    public static final EntityType<Husk> HUSK;
    public static final EntityType<Illusioner> ILLUSIONER;
    public static final EntityType<Interaction> INTERACTION;
    public static final EntityType<IronGolem> IRON_GOLEM;
    public static final EntityType<ItemEntity> ITEM;
    public static final EntityType<Display.ItemDisplay> ITEM_DISPLAY;
    public static final EntityType<ItemFrame> ITEM_FRAME;
    public static final EntityType<LargeFireball> FIREBALL;
    public static final EntityType<LeashFenceKnotEntity> LEASH_KNOT;
    public static final EntityType<LightningBolt> LIGHTNING_BOLT;
    public static final EntityType<Llama> LLAMA;
    public static final EntityType<LlamaSpit> LLAMA_SPIT;
    public static final EntityType<MagmaCube> MAGMA_CUBE;
    public static final EntityType<Marker> MARKER;
    public static final EntityType<Minecart> MINECART;
    public static final EntityType<MushroomCow> MOOSHROOM;
    public static final EntityType<Mule> MULE;
    public static final EntityType<Ocelot> OCELOT;
    public static final EntityType<Painting> PAINTING;
    public static final EntityType<Panda> PANDA;
    public static final EntityType<Parrot> PARROT;
    public static final EntityType<Phantom> PHANTOM;
    public static final EntityType<Pig> PIG;
    public static final EntityType<Piglin> PIGLIN;
    public static final EntityType<PiglinBrute> PIGLIN_BRUTE;
    public static final EntityType<Pillager> PILLAGER;
    public static final EntityType<PolarBear> POLAR_BEAR;
    public static final EntityType<ThrownPotion> POTION;
    public static final EntityType<Pufferfish> PUFFERFISH;
    public static final EntityType<Rabbit> RABBIT;
    public static final EntityType<Ravager> RAVAGER;
    public static final EntityType<Salmon> SALMON;
    public static final EntityType<Sheep> SHEEP;
    public static final EntityType<Shulker> SHULKER;
    public static final EntityType<ShulkerBullet> SHULKER_BULLET;
    public static final EntityType<Silverfish> SILVERFISH;
    public static final EntityType<Skeleton> SKELETON;
    public static final EntityType<SkeletonHorse> SKELETON_HORSE;
    public static final EntityType<Slime> SLIME;
    public static final EntityType<SmallFireball> SMALL_FIREBALL;
    public static final EntityType<Sniffer> SNIFFER;
    public static final EntityType<SnowGolem> SNOW_GOLEM;
    public static final EntityType<Snowball> SNOWBALL;
    public static final EntityType<MinecartSpawner> SPAWNER_MINECART;
    public static final EntityType<SpectralArrow> SPECTRAL_ARROW;
    public static final EntityType<Spider> SPIDER;
    public static final EntityType<Squid> SQUID;
    public static final EntityType<Stray> STRAY;
    public static final EntityType<Strider> STRIDER;
    public static final EntityType<Tadpole> TADPOLE;
    public static final EntityType<Display.TextDisplay> TEXT_DISPLAY;
    public static final EntityType<PrimedTnt> TNT;
    public static final EntityType<MinecartTNT> TNT_MINECART;
    public static final EntityType<TraderLlama> TRADER_LLAMA;
    public static final EntityType<ThrownTrident> TRIDENT;
    public static final EntityType<TropicalFish> TROPICAL_FISH;
    public static final EntityType<Turtle> TURTLE;
    public static final EntityType<Vex> VEX;
    public static final EntityType<Villager> VILLAGER;
    public static final EntityType<Vindicator> VINDICATOR;
    public static final EntityType<WanderingTrader> WANDERING_TRADER;
    public static final EntityType<Warden> WARDEN;
    public static final EntityType<Witch> WITCH;
    public static final EntityType<WitherBoss> WITHER;
    public static final EntityType<WitherSkeleton> WITHER_SKELETON;
    public static final EntityType<WitherSkull> WITHER_SKULL;
    public static final EntityType<Wolf> WOLF;
    public static final EntityType<Zoglin> ZOGLIN;
    public static final EntityType<Zombie> ZOMBIE;
    public static final EntityType<ZombieHorse> ZOMBIE_HORSE;
    public static final EntityType<ZombieVillager> ZOMBIE_VILLAGER;
    public static final EntityType<ZombifiedPiglin> ZOMBIFIED_PIGLIN;
    public static final EntityType<Player> PLAYER;
    public static final EntityType<FishingHook> FISHING_BOBBER;
    private final EntityFactory<T> factory;
    private final MobCategory category;
    private final ImmutableSet<Block> immuneTo;
    private final boolean serialize;
    private final boolean summon;
    private final boolean fireImmune;
    private final boolean canSpawnFarFromPlayer;
    private final int clientTrackingRange;
    private final int updateInterval;
    @Nullable
    private String descriptionId;
    @Nullable
    private Component description;
    @Nullable
    private ResourceLocation lootTable;
    private final EntityDimensions dimensions;
    private final FeatureFlagSet requiredFeatures;
    private final Predicate<EntityType<?>> velocityUpdateSupplier;
    private final ToIntFunction<EntityType<?>> trackingRangeSupplier;
    private final ToIntFunction<EntityType<?>> updateIntervalSupplier;
    private final BiFunction<PlayMessages.SpawnEntity, Level, T> customClientFactory;

    private static <T extends Entity> EntityType<T> register(String p_20635_, Builder<T> p_20636_) {
        return (EntityType)Registry.register(BuiltInRegistries.ENTITY_TYPE, (String)p_20635_, p_20636_.build(p_20635_));
    }

    public static ResourceLocation getKey(EntityType<?> p_20614_) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(p_20614_);
    }

    public static Optional<EntityType<?>> byString(String p_20633_) {
        return BuiltInRegistries.ENTITY_TYPE.getOptional(ResourceLocation.tryParse(p_20633_));
    }

    public EntityType(EntityFactory<T> p_273268_, MobCategory p_272918_, boolean p_273417_, boolean p_273389_, boolean p_273556_, boolean p_272654_, ImmutableSet<Block> p_273631_, EntityDimensions p_272946_, int p_272895_, int p_273451_, FeatureFlagSet p_273518_) {
        this(p_273268_, p_272918_, p_273417_, p_273389_, p_273556_, p_272654_, p_273631_, p_272946_, p_272895_, p_273451_, p_273518_, EntityType::defaultVelocitySupplier, EntityType::defaultTrackingRangeSupplier, EntityType::defaultUpdateIntervalSupplier, (BiFunction)null);
    }

    public EntityType(EntityFactory<T> p_251402_, MobCategory p_251431_, boolean p_251439_, boolean p_251973_, boolean p_252007_, boolean p_250908_, ImmutableSet<Block> p_250201_, EntityDimensions p_251742_, int p_250479_, int p_249249_, FeatureFlagSet p_250427_, Predicate<EntityType<?>> velocityUpdateSupplier, ToIntFunction<EntityType<?>> trackingRangeSupplier, ToIntFunction<EntityType<?>> updateIntervalSupplier, BiFunction<PlayMessages.SpawnEntity, Level, T> customClientFactory) {
        this.builtInRegistryHolder = BuiltInRegistries.ENTITY_TYPE.createIntrusiveHolder(this);
        this.factory = p_251402_;
        this.category = p_251431_;
        this.canSpawnFarFromPlayer = p_250908_;
        this.serialize = p_251439_;
        this.summon = p_251973_;
        this.fireImmune = p_252007_;
        this.immuneTo = p_250201_;
        this.dimensions = p_251742_;
        this.clientTrackingRange = p_250479_;
        this.updateInterval = p_249249_;
        this.requiredFeatures = p_250427_;
        this.velocityUpdateSupplier = velocityUpdateSupplier;
        this.trackingRangeSupplier = trackingRangeSupplier;
        this.updateIntervalSupplier = updateIntervalSupplier;
        this.customClientFactory = customClientFactory;
    }

    @Nullable
    public T spawn(ServerLevel p_20593_, @Nullable ItemStack p_20594_, @Nullable Player p_20595_, BlockPos p_20596_, MobSpawnType p_20597_, boolean p_20598_, boolean p_20599_) {
        Consumer consumer;
        CompoundTag compoundtag;
        if (p_20594_ != null) {
            compoundtag = p_20594_.getTag();
            consumer = createDefaultStackConfig(p_20593_, p_20594_, p_20595_);
        } else {
            consumer = (p_263563_) -> {
            };
            compoundtag = null;
        }

        return this.spawn(p_20593_, compoundtag, consumer, p_20596_, p_20597_, p_20598_, p_20599_);
    }

    public static <T extends Entity> Consumer<T> createDefaultStackConfig(ServerLevel p_263583_, ItemStack p_263568_, @Nullable Player p_263575_) {
        return appendDefaultStackConfig((p_262561_) -> {
        }, p_263583_, p_263568_, p_263575_);
    }

    public static <T extends Entity> Consumer<T> appendDefaultStackConfig(Consumer<T> p_265154_, ServerLevel p_265733_, ItemStack p_265598_, @Nullable Player p_265666_) {
        return appendCustomEntityStackConfig(appendCustomNameConfig(p_265154_, p_265598_), p_265733_, p_265598_, p_265666_);
    }

    public static <T extends Entity> Consumer<T> appendCustomNameConfig(Consumer<T> p_263567_, ItemStack p_263564_) {
        return p_263564_.hasCustomHoverName() ? p_263567_.andThen((p_262560_) -> {
            p_262560_.setCustomName(p_263564_.getHoverName());
        }) : p_263567_;
    }

    public static <T extends Entity> Consumer<T> appendCustomEntityStackConfig(Consumer<T> p_263579_, ServerLevel p_263571_, ItemStack p_263582_, @Nullable Player p_263574_) {
        CompoundTag compoundtag = p_263582_.getTag();
        return compoundtag != null ? p_263579_.andThen((p_262558_) -> {
            updateCustomEntityTag(p_263571_, p_263574_, p_262558_, compoundtag);
        }) : p_263579_;
    }

    @Nullable
    public T spawn(ServerLevel p_262634_, BlockPos p_262707_, MobSpawnType p_262597_) {
        return this.spawn(p_262634_, (CompoundTag)null, (Consumer)null, p_262707_, p_262597_, false, false);
    }

    @Nullable
    public T spawn(ServerLevel p_262704_, @Nullable CompoundTag p_262603_, @Nullable Consumer<T> p_262621_, BlockPos p_262672_, MobSpawnType p_262644_, boolean p_262690_, boolean p_262590_) {
        T t = this.create(p_262704_, p_262603_, p_262621_, p_262672_, p_262644_, p_262690_, p_262590_);
        if (t != null) {
            p_262704_.addFreshEntityWithPassengers(t);
        }

        return t;
    }

    @Nullable
    public T create(ServerLevel p_262637_, @Nullable CompoundTag p_262687_, @Nullable Consumer<T> p_262629_, BlockPos p_262595_, MobSpawnType p_262666_, boolean p_262685_, boolean p_262588_) {
        T t = this.create(p_262637_);
        if (t == null) {
            return (Entity)null;
        } else {
            double d0;
            if (p_262685_) {
                t.setPos((double)p_262595_.getX() + 0.5, (double)(p_262595_.getY() + 1), (double)p_262595_.getZ() + 0.5);
                d0 = getYOffset(p_262637_, p_262595_, p_262588_, t.getBoundingBox());
            } else {
                d0 = 0.0;
            }

            t.moveTo((double)p_262595_.getX() + 0.5, (double)p_262595_.getY() + d0, (double)p_262595_.getZ() + 0.5, Mth.wrapDegrees(p_262637_.random.nextFloat() * 360.0F), 0.0F);
            if (t instanceof Mob) {
                Mob mob = (Mob)t;
                mob.yHeadRot = mob.getYRot();
                mob.yBodyRot = mob.getYRot();
                mob.finalizeSpawn(p_262637_, p_262637_.getCurrentDifficultyAt(mob.blockPosition()), p_262666_, (SpawnGroupData)null, p_262687_);
                mob.playAmbientSound();
            }

            if (p_262629_ != null) {
                p_262629_.accept(t);
            }

            return t;
        }
    }

    protected static double getYOffset(LevelReader p_20626_, BlockPos p_20627_, boolean p_20628_, AABB p_20629_) {
        AABB aabb = new AABB(p_20627_);
        if (p_20628_) {
            aabb = aabb.expandTowards(0.0, -1.0, 0.0);
        }

        Iterable<VoxelShape> iterable = p_20626_.getCollisions((Entity)null, aabb);
        return 1.0 + Shapes.collide(Axis.Y, p_20629_, iterable, p_20628_ ? -2.0 : -1.0);
    }

    public static void updateCustomEntityTag(Level p_20621_, @Nullable Player p_20622_, @Nullable Entity p_20623_, @Nullable CompoundTag p_20624_) {
        if (p_20624_ != null && p_20624_.contains("EntityTag", 10)) {
            MinecraftServer minecraftserver = p_20621_.getServer();
            if (minecraftserver != null && p_20623_ != null && (p_20621_.isClientSide || !p_20623_.onlyOpCanSetNbt() || p_20622_ != null && minecraftserver.getPlayerList().isOp(p_20622_.getGameProfile()))) {
                CompoundTag compoundtag = p_20623_.saveWithoutId(new CompoundTag());
                UUID uuid = p_20623_.getUUID();
                compoundtag.merge(p_20624_.getCompound("EntityTag"));
                p_20623_.setUUID(uuid);
                p_20623_.load(compoundtag);
            }
        }

    }

    public boolean canSerialize() {
        return this.serialize;
    }

    public boolean canSummon() {
        return this.summon;
    }

    public boolean fireImmune() {
        return this.fireImmune;
    }

    public boolean canSpawnFarFromPlayer() {
        return this.canSpawnFarFromPlayer;
    }

    public MobCategory getCategory() {
        return this.category;
    }

    public String getDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.makeDescriptionId("entity", BuiltInRegistries.ENTITY_TYPE.getKey(this));
        }

        return this.descriptionId;
    }

    public Component getDescription() {
        if (this.description == null) {
            this.description = Component.translatable(this.getDescriptionId());
        }

        return this.description;
    }

    public String toString() {
        return this.getDescriptionId();
    }

    public String toShortString() {
        int i = this.getDescriptionId().lastIndexOf(46);
        return i == -1 ? this.getDescriptionId() : this.getDescriptionId().substring(i + 1);
    }

    public ResourceLocation getDefaultLootTable() {
        if (this.lootTable == null) {
            ResourceLocation resourcelocation = BuiltInRegistries.ENTITY_TYPE.getKey(this);
            this.lootTable = resourcelocation.withPrefix("entities/");
        }

        return this.lootTable;
    }

    public float getWidth() {
        return this.dimensions.width;
    }

    public float getHeight() {
        return this.dimensions.height;
    }

    public FeatureFlagSet requiredFeatures() {
        return this.requiredFeatures;
    }

    @Nullable
    public T create(Level p_20616_) {
        return !this.isEnabled(p_20616_.enabledFeatures()) ? null : this.factory.create(this, p_20616_);
    }

    public static Optional<Entity> create(CompoundTag p_20643_, Level p_20644_) {
        return Util.ifElse(by(p_20643_).map((p_185998_) -> {
            return p_185998_.create(p_20644_);
        }), (p_185990_) -> {
            p_185990_.load(p_20643_);
        }, () -> {
            LOGGER.warn("Skipping Entity with id {}", p_20643_.getString("id"));
        });
    }

    public AABB getAABB(double p_20586_, double p_20587_, double p_20588_) {
        float f = this.getWidth() / 2.0F;
        return new AABB(p_20586_ - (double)f, p_20587_, p_20588_ - (double)f, p_20586_ + (double)f, p_20587_ + (double)this.getHeight(), p_20588_ + (double)f);
    }

    public boolean isBlockDangerous(BlockState p_20631_) {
        if (this.immuneTo.contains(p_20631_.getBlock())) {
            return false;
        } else if (!this.fireImmune && WalkNodeEvaluator.isBurningBlock(p_20631_)) {
            return true;
        } else {
            return p_20631_.is(Blocks.WITHER_ROSE) || p_20631_.is(Blocks.SWEET_BERRY_BUSH) || p_20631_.is(Blocks.CACTUS) || p_20631_.is(Blocks.POWDER_SNOW);
        }
    }

    public EntityDimensions getDimensions() {
        return this.dimensions;
    }

    public static Optional<EntityType<?>> by(CompoundTag p_20638_) {
        return BuiltInRegistries.ENTITY_TYPE.getOptional(new ResourceLocation(p_20638_.getString("id")));
    }

    @Nullable
    public static Entity loadEntityRecursive(CompoundTag p_20646_, Level p_20647_, Function<Entity, Entity> p_20648_) {
        return (Entity)loadStaticEntity(p_20646_, p_20647_).map(p_20648_).map((p_185995_) -> {
            if (p_20646_.contains("Passengers", 9)) {
                ListTag listtag = p_20646_.getList("Passengers", 10);

                for(int i = 0; i < listtag.size(); ++i) {
                    Entity entity = loadEntityRecursive(listtag.getCompound(i), p_20647_, p_20648_);
                    if (entity != null) {
                        entity.startRiding(p_185995_, true);
                    }
                }
            }

            return p_185995_;
        }).orElse((Entity)null);
    }

    public static Stream<Entity> loadEntitiesRecursive(final List<? extends Tag> p_147046_, final Level p_147047_) {
        final Spliterator<? extends Tag> spliterator = p_147046_.spliterator();
        return StreamSupport.stream(new Spliterator<Entity>() {
            public boolean tryAdvance(Consumer<? super Entity> p_147066_) {
                return spliterator.tryAdvance((p_147059_) -> {
                    EntityType.loadEntityRecursive((CompoundTag)p_147059_, p_147047_, (p_147062_) -> {
                        p_147066_.accept(p_147062_);
                        return p_147062_;
                    });
                });
            }

            public Spliterator<Entity> trySplit() {
                return null;
            }

            public long estimateSize() {
                return (long)p_147046_.size();
            }

            public int characteristics() {
                return 1297;
            }
        }, false);
    }

    private static Optional<Entity> loadStaticEntity(CompoundTag p_20670_, Level p_20671_) {
        try {
            return create(p_20670_, p_20671_);
        } catch (RuntimeException var3) {
            RuntimeException runtimeexception = var3;
            LOGGER.warn("Exception loading entity: ", runtimeexception);
            return Optional.empty();
        }
    }

    public int clientTrackingRange() {
        return this.trackingRangeSupplier.applyAsInt(this);
    }

    private int defaultTrackingRangeSupplier() {
        return this.clientTrackingRange;
    }

    public int updateInterval() {
        return this.updateIntervalSupplier.applyAsInt(this);
    }

    private int defaultUpdateIntervalSupplier() {
        return this.updateInterval;
    }

    public boolean trackDeltas() {
        return this.velocityUpdateSupplier.test(this);
    }

    private boolean defaultVelocitySupplier() {
        return this != PLAYER && this != LLAMA_SPIT && this != WITHER && this != BAT && this != ITEM_FRAME && this != GLOW_ITEM_FRAME && this != LEASH_KNOT && this != PAINTING && this != END_CRYSTAL && this != EVOKER_FANGS;
    }

    public boolean is(TagKey<EntityType<?>> p_204040_) {
        return this.builtInRegistryHolder.is(p_204040_);
    }

    @Nullable
    public T tryCast(Entity p_147042_) {
        return p_147042_.getType() == this ? p_147042_ : null;
    }

    public Class<? extends Entity> getBaseClass() {
        return Entity.class;
    }

    /** @deprecated */
    @Deprecated
    public Holder.Reference<EntityType<?>> builtInRegistryHolder() {
        return this.builtInRegistryHolder;
    }

    public T customClientSpawn(PlayMessages.SpawnEntity packet, Level world) {
        return this.customClientFactory == null ? this.create(world) : (Entity)this.customClientFactory.apply(packet, world);
    }

    public Stream<TagKey<EntityType<?>>> getTags() {
        return this.builtInRegistryHolder().tags();
    }

    static {
        ALLAY = register("allay", net.minecraft.world.entity.EntityType.Builder.of(Allay::new, MobCategory.CREATURE).sized(0.35F, 0.6F).clientTrackingRange(8).updateInterval(2));
        AREA_EFFECT_CLOUD = register("area_effect_cloud", net.minecraft.world.entity.EntityType.Builder.of(AreaEffectCloud::new, MobCategory.MISC).fireImmune().sized(6.0F, 0.5F).clientTrackingRange(10).updateInterval(Integer.MAX_VALUE));
        ARMOR_STAND = register("armor_stand", net.minecraft.world.entity.EntityType.Builder.of(ArmorStand::new, MobCategory.MISC).sized(0.5F, 1.975F).clientTrackingRange(10));
        ARROW = register("arrow", net.minecraft.world.entity.EntityType.Builder.of(Arrow::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20));
        AXOLOTL = register("axolotl", net.minecraft.world.entity.EntityType.Builder.of(Axolotl::new, MobCategory.AXOLOTLS).sized(0.75F, 0.42F).clientTrackingRange(10));
        BAT = register("bat", net.minecraft.world.entity.EntityType.Builder.of(Bat::new, MobCategory.AMBIENT).sized(0.5F, 0.9F).clientTrackingRange(5));
        BEE = register("bee", net.minecraft.world.entity.EntityType.Builder.of(Bee::new, MobCategory.CREATURE).sized(0.7F, 0.6F).clientTrackingRange(8));
        BLAZE = register("blaze", net.minecraft.world.entity.EntityType.Builder.of(Blaze::new, MobCategory.MONSTER).fireImmune().sized(0.6F, 1.8F).clientTrackingRange(8));
        BLOCK_DISPLAY = register("block_display", net.minecraft.world.entity.EntityType.Builder.of(Display.BlockDisplay::new, MobCategory.MISC).sized(0.0F, 0.0F).clientTrackingRange(10).updateInterval(1));
        BOAT = register("boat", net.minecraft.world.entity.EntityType.Builder.of(Boat::new, MobCategory.MISC).sized(1.375F, 0.5625F).clientTrackingRange(10));
        CAMEL = register("camel", net.minecraft.world.entity.EntityType.Builder.of(Camel::new, MobCategory.CREATURE).sized(1.7F, 2.375F).clientTrackingRange(10));
        CAT = register("cat", net.minecraft.world.entity.EntityType.Builder.of(Cat::new, MobCategory.CREATURE).sized(0.6F, 0.7F).clientTrackingRange(8));
        CAVE_SPIDER = register("cave_spider", net.minecraft.world.entity.EntityType.Builder.of(CaveSpider::new, MobCategory.MONSTER).sized(0.7F, 0.5F).clientTrackingRange(8));
        CHEST_BOAT = register("chest_boat", net.minecraft.world.entity.EntityType.Builder.of(ChestBoat::new, MobCategory.MISC).sized(1.375F, 0.5625F).clientTrackingRange(10));
        CHEST_MINECART = register("chest_minecart", net.minecraft.world.entity.EntityType.Builder.of(MinecartChest::new, MobCategory.MISC).sized(0.98F, 0.7F).clientTrackingRange(8));
        CHICKEN = register("chicken", net.minecraft.world.entity.EntityType.Builder.of(Chicken::new, MobCategory.CREATURE).sized(0.4F, 0.7F).clientTrackingRange(10));
        COD = register("cod", net.minecraft.world.entity.EntityType.Builder.of(Cod::new, MobCategory.WATER_AMBIENT).sized(0.5F, 0.3F).clientTrackingRange(4));
        COMMAND_BLOCK_MINECART = register("command_block_minecart", net.minecraft.world.entity.EntityType.Builder.of(MinecartCommandBlock::new, MobCategory.MISC).sized(0.98F, 0.7F).clientTrackingRange(8));
        COW = register("cow", net.minecraft.world.entity.EntityType.Builder.of(Cow::new, MobCategory.CREATURE).sized(0.9F, 1.4F).clientTrackingRange(10));
        CREEPER = register("creeper", net.minecraft.world.entity.EntityType.Builder.of(Creeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8));
        DOLPHIN = register("dolphin", net.minecraft.world.entity.EntityType.Builder.of(Dolphin::new, MobCategory.WATER_CREATURE).sized(0.9F, 0.6F));
        DONKEY = register("donkey", net.minecraft.world.entity.EntityType.Builder.of(Donkey::new, MobCategory.CREATURE).sized(1.3964844F, 1.5F).clientTrackingRange(10));
        DRAGON_FIREBALL = register("dragon_fireball", net.minecraft.world.entity.EntityType.Builder.of(DragonFireball::new, MobCategory.MISC).sized(1.0F, 1.0F).clientTrackingRange(4).updateInterval(10));
        DROWNED = register("drowned", net.minecraft.world.entity.EntityType.Builder.of(Drowned::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8));
        EGG = register("egg", net.minecraft.world.entity.EntityType.Builder.of(ThrownEgg::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
        ELDER_GUARDIAN = register("elder_guardian", net.minecraft.world.entity.EntityType.Builder.of(ElderGuardian::new, MobCategory.MONSTER).sized(1.9975F, 1.9975F).clientTrackingRange(10));
        END_CRYSTAL = register("end_crystal", net.minecraft.world.entity.EntityType.Builder.of(EndCrystal::new, MobCategory.MISC).sized(2.0F, 2.0F).clientTrackingRange(16).updateInterval(Integer.MAX_VALUE));
        ENDER_DRAGON = register("ender_dragon", net.minecraft.world.entity.EntityType.Builder.of(EnderDragon::new, MobCategory.MONSTER).fireImmune().sized(16.0F, 8.0F).clientTrackingRange(10));
        ENDER_PEARL = register("ender_pearl", net.minecraft.world.entity.EntityType.Builder.of(ThrownEnderpearl::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
        ENDERMAN = register("enderman", net.minecraft.world.entity.EntityType.Builder.of(EnderMan::new, MobCategory.MONSTER).sized(0.6F, 2.9F).clientTrackingRange(8));
        ENDERMITE = register("endermite", net.minecraft.world.entity.EntityType.Builder.of(Endermite::new, MobCategory.MONSTER).sized(0.4F, 0.3F).clientTrackingRange(8));
        EVOKER = register("evoker", net.minecraft.world.entity.EntityType.Builder.of(Evoker::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8));
        EVOKER_FANGS = register("evoker_fangs", net.minecraft.world.entity.EntityType.Builder.of(EvokerFangs::new, MobCategory.MISC).sized(0.5F, 0.8F).clientTrackingRange(6).updateInterval(2));
        EXPERIENCE_BOTTLE = register("experience_bottle", net.minecraft.world.entity.EntityType.Builder.of(ThrownExperienceBottle::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
        EXPERIENCE_ORB = register("experience_orb", net.minecraft.world.entity.EntityType.Builder.of(ExperienceOrb::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(6).updateInterval(20));
        EYE_OF_ENDER = register("eye_of_ender", net.minecraft.world.entity.EntityType.Builder.of(EyeOfEnder::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(4));
        FALLING_BLOCK = register("falling_block", net.minecraft.world.entity.EntityType.Builder.of(FallingBlockEntity::new, MobCategory.MISC).sized(0.98F, 0.98F).clientTrackingRange(10).updateInterval(20));
        FIREWORK_ROCKET = register("firework_rocket", net.minecraft.world.entity.EntityType.Builder.of(FireworkRocketEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
        FOX = register("fox", net.minecraft.world.entity.EntityType.Builder.of(Fox::new, MobCategory.CREATURE).sized(0.6F, 0.7F).clientTrackingRange(8).immuneTo(Blocks.SWEET_BERRY_BUSH));
        FROG = register("frog", net.minecraft.world.entity.EntityType.Builder.of(Frog::new, MobCategory.CREATURE).sized(0.5F, 0.5F).clientTrackingRange(10));
        FURNACE_MINECART = register("furnace_minecart", net.minecraft.world.entity.EntityType.Builder.of(MinecartFurnace::new, MobCategory.MISC).sized(0.98F, 0.7F).clientTrackingRange(8));
        GHAST = register("ghast", net.minecraft.world.entity.EntityType.Builder.of(Ghast::new, MobCategory.MONSTER).fireImmune().sized(4.0F, 4.0F).clientTrackingRange(10));
        GIANT = register("giant", net.minecraft.world.entity.EntityType.Builder.of(Giant::new, MobCategory.MONSTER).sized(3.6F, 12.0F).clientTrackingRange(10));
        GLOW_ITEM_FRAME = register("glow_item_frame", net.minecraft.world.entity.EntityType.Builder.of(GlowItemFrame::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(Integer.MAX_VALUE));
        GLOW_SQUID = register("glow_squid", net.minecraft.world.entity.EntityType.Builder.of(GlowSquid::new, MobCategory.UNDERGROUND_WATER_CREATURE).sized(0.8F, 0.8F).clientTrackingRange(10));
        GOAT = register("goat", net.minecraft.world.entity.EntityType.Builder.of(Goat::new, MobCategory.CREATURE).sized(0.9F, 1.3F).clientTrackingRange(10));
        GUARDIAN = register("guardian", net.minecraft.world.entity.EntityType.Builder.of(Guardian::new, MobCategory.MONSTER).sized(0.85F, 0.85F).clientTrackingRange(8));
        HOGLIN = register("hoglin", net.minecraft.world.entity.EntityType.Builder.of(Hoglin::new, MobCategory.MONSTER).sized(1.3964844F, 1.4F).clientTrackingRange(8));
        HOPPER_MINECART = register("hopper_minecart", net.minecraft.world.entity.EntityType.Builder.of(MinecartHopper::new, MobCategory.MISC).sized(0.98F, 0.7F).clientTrackingRange(8));
        HORSE = register("horse", net.minecraft.world.entity.EntityType.Builder.of(Horse::new, MobCategory.CREATURE).sized(1.3964844F, 1.6F).clientTrackingRange(10));
        HUSK = register("husk", net.minecraft.world.entity.EntityType.Builder.of(Husk::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8));
        ILLUSIONER = register("illusioner", net.minecraft.world.entity.EntityType.Builder.of(Illusioner::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8));
        INTERACTION = register("interaction", net.minecraft.world.entity.EntityType.Builder.of(Interaction::new, MobCategory.MISC).sized(0.0F, 0.0F).clientTrackingRange(10));
        IRON_GOLEM = register("iron_golem", net.minecraft.world.entity.EntityType.Builder.of(IronGolem::new, MobCategory.MISC).sized(1.4F, 2.7F).clientTrackingRange(10));
        ITEM = register("item", net.minecraft.world.entity.EntityType.Builder.of(ItemEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(6).updateInterval(20));
        ITEM_DISPLAY = register("item_display", net.minecraft.world.entity.EntityType.Builder.of(Display.ItemDisplay::new, MobCategory.MISC).sized(0.0F, 0.0F).clientTrackingRange(10).updateInterval(1));
        ITEM_FRAME = register("item_frame", net.minecraft.world.entity.EntityType.Builder.of(ItemFrame::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(Integer.MAX_VALUE));
        FIREBALL = register("fireball", net.minecraft.world.entity.EntityType.Builder.of(LargeFireball::new, MobCategory.MISC).sized(1.0F, 1.0F).clientTrackingRange(4).updateInterval(10));
        LEASH_KNOT = register("leash_knot", net.minecraft.world.entity.EntityType.Builder.of(LeashFenceKnotEntity::new, MobCategory.MISC).noSave().sized(0.375F, 0.5F).clientTrackingRange(10).updateInterval(Integer.MAX_VALUE));
        LIGHTNING_BOLT = register("lightning_bolt", net.minecraft.world.entity.EntityType.Builder.of(LightningBolt::new, MobCategory.MISC).noSave().sized(0.0F, 0.0F).clientTrackingRange(16).updateInterval(Integer.MAX_VALUE));
        LLAMA = register("llama", net.minecraft.world.entity.EntityType.Builder.of(Llama::new, MobCategory.CREATURE).sized(0.9F, 1.87F).clientTrackingRange(10));
        LLAMA_SPIT = register("llama_spit", net.minecraft.world.entity.EntityType.Builder.of(LlamaSpit::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
        MAGMA_CUBE = register("magma_cube", net.minecraft.world.entity.EntityType.Builder.of(MagmaCube::new, MobCategory.MONSTER).fireImmune().sized(2.04F, 2.04F).clientTrackingRange(8));
        MARKER = register("marker", net.minecraft.world.entity.EntityType.Builder.of(Marker::new, MobCategory.MISC).sized(0.0F, 0.0F).clientTrackingRange(0));
        MINECART = register("minecart", net.minecraft.world.entity.EntityType.Builder.of(Minecart::new, MobCategory.MISC).sized(0.98F, 0.7F).clientTrackingRange(8));
        MOOSHROOM = register("mooshroom", net.minecraft.world.entity.EntityType.Builder.of(MushroomCow::new, MobCategory.CREATURE).sized(0.9F, 1.4F).clientTrackingRange(10));
        MULE = register("mule", net.minecraft.world.entity.EntityType.Builder.of(Mule::new, MobCategory.CREATURE).sized(1.3964844F, 1.6F).clientTrackingRange(8));
        OCELOT = register("ocelot", net.minecraft.world.entity.EntityType.Builder.of(Ocelot::new, MobCategory.CREATURE).sized(0.6F, 0.7F).clientTrackingRange(10));
        PAINTING = register("painting", net.minecraft.world.entity.EntityType.Builder.of(Painting::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(Integer.MAX_VALUE));
        PANDA = register("panda", net.minecraft.world.entity.EntityType.Builder.of(Panda::new, MobCategory.CREATURE).sized(1.3F, 1.25F).clientTrackingRange(10));
        PARROT = register("parrot", net.minecraft.world.entity.EntityType.Builder.of(Parrot::new, MobCategory.CREATURE).sized(0.5F, 0.9F).clientTrackingRange(8));
        PHANTOM = register("phantom", net.minecraft.world.entity.EntityType.Builder.of(Phantom::new, MobCategory.MONSTER).sized(0.9F, 0.5F).clientTrackingRange(8));
        PIG = register("pig", net.minecraft.world.entity.EntityType.Builder.of(Pig::new, MobCategory.CREATURE).sized(0.9F, 0.9F).clientTrackingRange(10));
        PIGLIN = register("piglin", net.minecraft.world.entity.EntityType.Builder.of(Piglin::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8));
        PIGLIN_BRUTE = register("piglin_brute", net.minecraft.world.entity.EntityType.Builder.of(PiglinBrute::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8));
        PILLAGER = register("pillager", net.minecraft.world.entity.EntityType.Builder.of(Pillager::new, MobCategory.MONSTER).canSpawnFarFromPlayer().sized(0.6F, 1.95F).clientTrackingRange(8));
        POLAR_BEAR = register("polar_bear", net.minecraft.world.entity.EntityType.Builder.of(PolarBear::new, MobCategory.CREATURE).immuneTo(Blocks.POWDER_SNOW).sized(1.4F, 1.4F).clientTrackingRange(10));
        POTION = register("potion", net.minecraft.world.entity.EntityType.Builder.of(ThrownPotion::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
        PUFFERFISH = register("pufferfish", net.minecraft.world.entity.EntityType.Builder.of(Pufferfish::new, MobCategory.WATER_AMBIENT).sized(0.7F, 0.7F).clientTrackingRange(4));
        RABBIT = register("rabbit", net.minecraft.world.entity.EntityType.Builder.of(Rabbit::new, MobCategory.CREATURE).sized(0.4F, 0.5F).clientTrackingRange(8));
        RAVAGER = register("ravager", net.minecraft.world.entity.EntityType.Builder.of(Ravager::new, MobCategory.MONSTER).sized(1.95F, 2.2F).clientTrackingRange(10));
        SALMON = register("salmon", net.minecraft.world.entity.EntityType.Builder.of(Salmon::new, MobCategory.WATER_AMBIENT).sized(0.7F, 0.4F).clientTrackingRange(4));
        SHEEP = register("sheep", net.minecraft.world.entity.EntityType.Builder.of(Sheep::new, MobCategory.CREATURE).sized(0.9F, 1.3F).clientTrackingRange(10));
        SHULKER = register("shulker", net.minecraft.world.entity.EntityType.Builder.of(Shulker::new, MobCategory.MONSTER).fireImmune().canSpawnFarFromPlayer().sized(1.0F, 1.0F).clientTrackingRange(10));
        SHULKER_BULLET = register("shulker_bullet", net.minecraft.world.entity.EntityType.Builder.of(ShulkerBullet::new, MobCategory.MISC).sized(0.3125F, 0.3125F).clientTrackingRange(8));
        SILVERFISH = register("silverfish", net.minecraft.world.entity.EntityType.Builder.of(Silverfish::new, MobCategory.MONSTER).sized(0.4F, 0.3F).clientTrackingRange(8));
        SKELETON = register("skeleton", net.minecraft.world.entity.EntityType.Builder.of(Skeleton::new, MobCategory.MONSTER).sized(0.6F, 1.99F).clientTrackingRange(8));
        SKELETON_HORSE = register("skeleton_horse", net.minecraft.world.entity.EntityType.Builder.of(SkeletonHorse::new, MobCategory.CREATURE).sized(1.3964844F, 1.6F).clientTrackingRange(10));
        SLIME = register("slime", net.minecraft.world.entity.EntityType.Builder.of(Slime::new, MobCategory.MONSTER).sized(2.04F, 2.04F).clientTrackingRange(10));
        SMALL_FIREBALL = register("small_fireball", net.minecraft.world.entity.EntityType.Builder.of(SmallFireball::new, MobCategory.MISC).sized(0.3125F, 0.3125F).clientTrackingRange(4).updateInterval(10));
        SNIFFER = register("sniffer", net.minecraft.world.entity.EntityType.Builder.of(Sniffer::new, MobCategory.CREATURE).sized(1.9F, 1.75F).clientTrackingRange(10));
        SNOW_GOLEM = register("snow_golem", net.minecraft.world.entity.EntityType.Builder.of(SnowGolem::new, MobCategory.MISC).immuneTo(Blocks.POWDER_SNOW).sized(0.7F, 1.9F).clientTrackingRange(8));
        SNOWBALL = register("snowball", net.minecraft.world.entity.EntityType.Builder.of(Snowball::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
        SPAWNER_MINECART = register("spawner_minecart", net.minecraft.world.entity.EntityType.Builder.of(MinecartSpawner::new, MobCategory.MISC).sized(0.98F, 0.7F).clientTrackingRange(8));
        SPECTRAL_ARROW = register("spectral_arrow", net.minecraft.world.entity.EntityType.Builder.of(SpectralArrow::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20));
        SPIDER = register("spider", net.minecraft.world.entity.EntityType.Builder.of(Spider::new, MobCategory.MONSTER).sized(1.4F, 0.9F).clientTrackingRange(8));
        SQUID = register("squid", net.minecraft.world.entity.EntityType.Builder.of(Squid::new, MobCategory.WATER_CREATURE).sized(0.8F, 0.8F).clientTrackingRange(8));
        STRAY = register("stray", net.minecraft.world.entity.EntityType.Builder.of(Stray::new, MobCategory.MONSTER).sized(0.6F, 1.99F).immuneTo(Blocks.POWDER_SNOW).clientTrackingRange(8));
        STRIDER = register("strider", net.minecraft.world.entity.EntityType.Builder.of(Strider::new, MobCategory.CREATURE).fireImmune().sized(0.9F, 1.7F).clientTrackingRange(10));
        TADPOLE = register("tadpole", net.minecraft.world.entity.EntityType.Builder.of(Tadpole::new, MobCategory.CREATURE).sized(Tadpole.HITBOX_WIDTH, Tadpole.HITBOX_HEIGHT).clientTrackingRange(10));
        TEXT_DISPLAY = register("text_display", net.minecraft.world.entity.EntityType.Builder.of(Display.TextDisplay::new, MobCategory.MISC).sized(0.0F, 0.0F).clientTrackingRange(10).updateInterval(1));
        TNT = register("tnt", net.minecraft.world.entity.EntityType.Builder.of(PrimedTnt::new, MobCategory.MISC).fireImmune().sized(0.98F, 0.98F).clientTrackingRange(10).updateInterval(10));
        TNT_MINECART = register("tnt_minecart", net.minecraft.world.entity.EntityType.Builder.of(MinecartTNT::new, MobCategory.MISC).sized(0.98F, 0.7F).clientTrackingRange(8));
        TRADER_LLAMA = register("trader_llama", net.minecraft.world.entity.EntityType.Builder.of(TraderLlama::new, MobCategory.CREATURE).sized(0.9F, 1.87F).clientTrackingRange(10));
        TRIDENT = register("trident", net.minecraft.world.entity.EntityType.Builder.of(ThrownTrident::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20));
        TROPICAL_FISH = register("tropical_fish", net.minecraft.world.entity.EntityType.Builder.of(TropicalFish::new, MobCategory.WATER_AMBIENT).sized(0.5F, 0.4F).clientTrackingRange(4));
        TURTLE = register("turtle", net.minecraft.world.entity.EntityType.Builder.of(Turtle::new, MobCategory.CREATURE).sized(1.2F, 0.4F).clientTrackingRange(10));
        VEX = register("vex", net.minecraft.world.entity.EntityType.Builder.of(Vex::new, MobCategory.MONSTER).fireImmune().sized(0.4F, 0.8F).clientTrackingRange(8));
        VILLAGER = register("villager", net.minecraft.world.entity.EntityType.Builder.of(Villager::new, MobCategory.MISC).sized(0.6F, 1.95F).clientTrackingRange(10));
        VINDICATOR = register("vindicator", net.minecraft.world.entity.EntityType.Builder.of(Vindicator::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8));
        WANDERING_TRADER = register("wandering_trader", net.minecraft.world.entity.EntityType.Builder.of(WanderingTrader::new, MobCategory.CREATURE).sized(0.6F, 1.95F).clientTrackingRange(10));
        WARDEN = register("warden", net.minecraft.world.entity.EntityType.Builder.of(Warden::new, MobCategory.MONSTER).sized(0.9F, 2.9F).clientTrackingRange(16).fireImmune());
        WITCH = register("witch", net.minecraft.world.entity.EntityType.Builder.of(Witch::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8));
        WITHER = register("wither", net.minecraft.world.entity.EntityType.Builder.of(WitherBoss::new, MobCategory.MONSTER).fireImmune().immuneTo(Blocks.WITHER_ROSE).sized(0.9F, 3.5F).clientTrackingRange(10));
        WITHER_SKELETON = register("wither_skeleton", net.minecraft.world.entity.EntityType.Builder.of(WitherSkeleton::new, MobCategory.MONSTER).fireImmune().immuneTo(Blocks.WITHER_ROSE).sized(0.7F, 2.4F).clientTrackingRange(8));
        WITHER_SKULL = register("wither_skull", net.minecraft.world.entity.EntityType.Builder.of(WitherSkull::new, MobCategory.MISC).sized(0.3125F, 0.3125F).clientTrackingRange(4).updateInterval(10));
        WOLF = register("wolf", net.minecraft.world.entity.EntityType.Builder.of(Wolf::new, MobCategory.CREATURE).sized(0.6F, 0.85F).clientTrackingRange(10));
        ZOGLIN = register("zoglin", net.minecraft.world.entity.EntityType.Builder.of(Zoglin::new, MobCategory.MONSTER).fireImmune().sized(1.3964844F, 1.4F).clientTrackingRange(8));
        ZOMBIE = register("zombie", net.minecraft.world.entity.EntityType.Builder.of(Zombie::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8));
        ZOMBIE_HORSE = register("zombie_horse", net.minecraft.world.entity.EntityType.Builder.of(ZombieHorse::new, MobCategory.CREATURE).sized(1.3964844F, 1.6F).clientTrackingRange(10));
        ZOMBIE_VILLAGER = register("zombie_villager", net.minecraft.world.entity.EntityType.Builder.of(ZombieVillager::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8));
        ZOMBIFIED_PIGLIN = register("zombified_piglin", net.minecraft.world.entity.EntityType.Builder.of(ZombifiedPiglin::new, MobCategory.MONSTER).fireImmune().sized(0.6F, 1.95F).clientTrackingRange(8));
        PLAYER = register("player", net.minecraft.world.entity.EntityType.Builder.createNothing(MobCategory.MISC).noSave().noSummon().sized(0.6F, 1.8F).clientTrackingRange(32).updateInterval(2));
        FISHING_BOBBER = register("fishing_bobber", net.minecraft.world.entity.EntityType.Builder.of(FishingHook::new, MobCategory.MISC).noSave().noSummon().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(5));
    }

    public static class Builder<T extends Entity> {
        private final EntityFactory<T> factory;
        private final MobCategory category;
        private ImmutableSet<Block> immuneTo = ImmutableSet.of();
        private boolean serialize = true;
        private boolean summon = true;
        private boolean fireImmune;
        private boolean canSpawnFarFromPlayer;
        private int clientTrackingRange = 5;
        private int updateInterval = 3;
        private EntityDimensions dimensions = EntityDimensions.scalable(0.6F, 1.8F);
        private FeatureFlagSet requiredFeatures;
        private Predicate<EntityType<?>> velocityUpdateSupplier;
        private ToIntFunction<EntityType<?>> trackingRangeSupplier;
        private ToIntFunction<EntityType<?>> updateIntervalSupplier;
        private BiFunction<PlayMessages.SpawnEntity, Level, T> customClientFactory;

        private Builder(EntityFactory<T> p_20696_, MobCategory p_20697_) {
            this.requiredFeatures = FeatureFlags.VANILLA_SET;
            this.velocityUpdateSupplier = EntityType::defaultVelocitySupplier;
            this.trackingRangeSupplier = EntityType::defaultTrackingRangeSupplier;
            this.updateIntervalSupplier = EntityType::defaultUpdateIntervalSupplier;
            this.factory = p_20696_;
            this.category = p_20697_;
            this.canSpawnFarFromPlayer = p_20697_ == MobCategory.CREATURE || p_20697_ == MobCategory.MISC;
        }

        public static <T extends Entity> Builder<T> of(EntityFactory<T> p_20705_, MobCategory p_20706_) {
            return new Builder(p_20705_, p_20706_);
        }

        public static <T extends Entity> Builder<T> createNothing(MobCategory p_20711_) {
            return new Builder((p_20708_, p_20709_) -> {
                return (Entity)null;
            }, p_20711_);
        }

        public Builder<T> sized(float p_20700_, float p_20701_) {
            this.dimensions = EntityDimensions.scalable(p_20700_, p_20701_);
            return this;
        }

        public Builder<T> noSummon() {
            this.summon = false;
            return this;
        }

        public Builder<T> noSave() {
            this.serialize = false;
            return this;
        }

        public Builder<T> fireImmune() {
            this.fireImmune = true;
            return this;
        }

        public Builder<T> immuneTo(Block... p_20715_) {
            this.immuneTo = ImmutableSet.copyOf(p_20715_);
            return this;
        }

        public Builder<T> canSpawnFarFromPlayer() {
            this.canSpawnFarFromPlayer = true;
            return this;
        }

        public Builder<T> clientTrackingRange(int p_20703_) {
            this.clientTrackingRange = p_20703_;
            return this;
        }

        public Builder<T> updateInterval(int p_20718_) {
            this.updateInterval = p_20718_;
            return this;
        }

        public Builder<T> requiredFeatures(FeatureFlag... p_251646_) {
            this.requiredFeatures = FeatureFlags.REGISTRY.subset(p_251646_);
            return this;
        }

        public Builder<T> setUpdateInterval(int interval) {
            this.updateIntervalSupplier = (t) -> {
                return interval;
            };
            return this;
        }

        public Builder<T> setTrackingRange(int range) {
            this.trackingRangeSupplier = (t) -> {
                return range;
            };
            return this;
        }

        public Builder<T> setShouldReceiveVelocityUpdates(boolean value) {
            this.velocityUpdateSupplier = (t) -> {
                return value;
            };
            return this;
        }

        public Builder<T> setCustomClientFactory(BiFunction<PlayMessages.SpawnEntity, Level, T> customClientFactory) {
            this.customClientFactory = customClientFactory;
            return this;
        }

        public EntityType<T> build(String p_20713_) {
            if (this.serialize) {
                Util.fetchChoiceType(References.ENTITY_TREE, p_20713_);
            }

            return new EntityType(this.factory, this.category, this.serialize, this.summon, this.fireImmune, this.canSpawnFarFromPlayer, this.immuneTo, this.dimensions, this.clientTrackingRange, this.updateInterval, this.requiredFeatures, this.velocityUpdateSupplier, this.trackingRangeSupplier, this.updateIntervalSupplier, this.customClientFactory);
        }
    }

    public interface EntityFactory<T extends Entity> {
        T create(EntityType<T> var1, Level var2);
    }
}

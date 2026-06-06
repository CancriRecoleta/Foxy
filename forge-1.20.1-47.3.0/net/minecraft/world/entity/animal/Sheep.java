//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.entity.animal;

import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.EatBlockGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraftforge.common.IForgeShearable;
import org.jetbrains.annotations.NotNull;

public class Sheep extends Animal implements Shearable, IForgeShearable {
    private static final int EAT_ANIMATION_TICKS = 40;
    private static final EntityDataAccessor<Byte> DATA_WOOL_ID;
    private static final Map<DyeColor, ItemLike> ITEM_BY_DYE;
    private static final Map<DyeColor, float[]> COLORARRAY_BY_COLOR;
    private int eatAnimationTick;
    private EatBlockGoal eatBlockGoal;

    private static float[] createSheepColor(DyeColor p_29866_) {
        if (p_29866_ == DyeColor.WHITE) {
            return new float[]{0.9019608F, 0.9019608F, 0.9019608F};
        } else {
            float[] afloat = p_29866_.getTextureDiffuseColors();
            float f = 0.75F;
            return new float[]{afloat[0] * 0.75F, afloat[1] * 0.75F, afloat[2] * 0.75F};
        }
    }

    public static float[] getColorArray(DyeColor p_29830_) {
        return (float[])COLORARRAY_BY_COLOR.get(p_29830_);
    }

    public Sheep(EntityType<? extends Sheep> p_29806_, Level p_29807_) {
        super(p_29806_, p_29807_);
    }

    protected void registerGoals() {
        this.eatBlockGoal = new EatBlockGoal(this);
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.25));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.1, Ingredient.of(Items.WHEAT), false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1));
        this.goalSelector.addGoal(5, this.eatBlockGoal);
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    protected void customServerAiStep() {
        this.eatAnimationTick = this.eatBlockGoal.getEatAnimationTick();
        super.customServerAiStep();
    }

    public void aiStep() {
        if (this.level().isClientSide) {
            this.eatAnimationTick = Math.max(0, this.eatAnimationTick - 1);
        }

        super.aiStep();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 8.0).add(Attributes.MOVEMENT_SPEED, 0.23000000417232513);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_WOOL_ID, (byte)0);
    }

    public ResourceLocation getDefaultLootTable() {
        if (this.isSheared()) {
            return this.getType().getDefaultLootTable();
        } else {
            ResourceLocation resourcelocation;
            switch (this.getColor()) {
                case WHITE -> resourcelocation = BuiltInLootTables.SHEEP_WHITE;
                case ORANGE -> resourcelocation = BuiltInLootTables.SHEEP_ORANGE;
                case MAGENTA -> resourcelocation = BuiltInLootTables.SHEEP_MAGENTA;
                case LIGHT_BLUE -> resourcelocation = BuiltInLootTables.SHEEP_LIGHT_BLUE;
                case YELLOW -> resourcelocation = BuiltInLootTables.SHEEP_YELLOW;
                case LIME -> resourcelocation = BuiltInLootTables.SHEEP_LIME;
                case PINK -> resourcelocation = BuiltInLootTables.SHEEP_PINK;
                case GRAY -> resourcelocation = BuiltInLootTables.SHEEP_GRAY;
                case LIGHT_GRAY -> resourcelocation = BuiltInLootTables.SHEEP_LIGHT_GRAY;
                case CYAN -> resourcelocation = BuiltInLootTables.SHEEP_CYAN;
                case PURPLE -> resourcelocation = BuiltInLootTables.SHEEP_PURPLE;
                case BLUE -> resourcelocation = BuiltInLootTables.SHEEP_BLUE;
                case BROWN -> resourcelocation = BuiltInLootTables.SHEEP_BROWN;
                case GREEN -> resourcelocation = BuiltInLootTables.SHEEP_GREEN;
                case RED -> resourcelocation = BuiltInLootTables.SHEEP_RED;
                case BLACK -> resourcelocation = BuiltInLootTables.SHEEP_BLACK;
                default -> throw new IncompatibleClassChangeError();
            }

            return resourcelocation;
        }
    }

    public void handleEntityEvent(byte p_29814_) {
        if (p_29814_ == 10) {
            this.eatAnimationTick = 40;
        } else {
            super.handleEntityEvent(p_29814_);
        }

    }

    public float getHeadEatPositionScale(float p_29881_) {
        if (this.eatAnimationTick <= 0) {
            return 0.0F;
        } else if (this.eatAnimationTick >= 4 && this.eatAnimationTick <= 36) {
            return 1.0F;
        } else {
            return this.eatAnimationTick < 4 ? ((float)this.eatAnimationTick - p_29881_) / 4.0F : -((float)(this.eatAnimationTick - 40) - p_29881_) / 4.0F;
        }
    }

    public float getHeadEatAngleScale(float p_29883_) {
        if (this.eatAnimationTick > 4 && this.eatAnimationTick <= 36) {
            float f = ((float)(this.eatAnimationTick - 4) - p_29883_) / 32.0F;
            return 0.62831855F + 0.21991149F * Mth.sin(f * 28.7F);
        } else {
            return this.eatAnimationTick > 0 ? 0.62831855F : this.getXRot() * 0.017453292F;
        }
    }

    public InteractionResult mobInteract(Player p_29853_, InteractionHand p_29854_) {
        p_29853_.getItemInHand(p_29854_);
        return super.mobInteract(p_29853_, p_29854_);
    }

    public void shear(SoundSource p_29819_) {
        this.level().playSound((Player)((Player)null), (Entity)this, SoundEvents.SHEEP_SHEAR, p_29819_, 1.0F, 1.0F);
        this.setSheared(true);
        int i = 1 + this.random.nextInt(3);

        for(int j = 0; j < i; ++j) {
            ItemEntity itementity = this.spawnAtLocation((ItemLike)ITEM_BY_DYE.get(this.getColor()), 1);
            if (itementity != null) {
                itementity.setDeltaMovement(itementity.getDeltaMovement().add((double)((this.random.nextFloat() - this.random.nextFloat()) * 0.1F), (double)(this.random.nextFloat() * 0.05F), (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.1F)));
            }
        }

    }

    public boolean readyForShearing() {
        return this.isAlive() && !this.isSheared() && !this.isBaby();
    }

    public void addAdditionalSaveData(CompoundTag p_29864_) {
        super.addAdditionalSaveData(p_29864_);
        p_29864_.putBoolean("Sheared", this.isSheared());
        p_29864_.putByte("Color", (byte)this.getColor().getId());
    }

    public void readAdditionalSaveData(CompoundTag p_29845_) {
        super.readAdditionalSaveData(p_29845_);
        this.setSheared(p_29845_.getBoolean("Sheared"));
        this.setColor(DyeColor.byId(p_29845_.getByte("Color")));
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.SHEEP_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource p_29872_) {
        return SoundEvents.SHEEP_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.SHEEP_DEATH;
    }

    protected void playStepSound(BlockPos p_29861_, BlockState p_29862_) {
        this.playSound(SoundEvents.SHEEP_STEP, 0.15F, 1.0F);
    }

    public DyeColor getColor() {
        return DyeColor.byId((Byte)this.entityData.get(DATA_WOOL_ID) & 15);
    }

    public void setColor(DyeColor p_29856_) {
        byte b0 = (Byte)this.entityData.get(DATA_WOOL_ID);
        this.entityData.set(DATA_WOOL_ID, (byte)(b0 & 240 | p_29856_.getId() & 15));
    }

    public boolean isSheared() {
        return ((Byte)this.entityData.get(DATA_WOOL_ID) & 16) != 0;
    }

    public void setSheared(boolean p_29879_) {
        byte b0 = (Byte)this.entityData.get(DATA_WOOL_ID);
        if (p_29879_) {
            this.entityData.set(DATA_WOOL_ID, (byte)(b0 | 16));
        } else {
            this.entityData.set(DATA_WOOL_ID, (byte)(b0 & -17));
        }

    }

    public static DyeColor getRandomSheepColor(RandomSource p_218262_) {
        int i = p_218262_.nextInt(100);
        if (i < 5) {
            return DyeColor.BLACK;
        } else if (i < 10) {
            return DyeColor.GRAY;
        } else if (i < 15) {
            return DyeColor.LIGHT_GRAY;
        } else if (i < 18) {
            return DyeColor.BROWN;
        } else {
            return p_218262_.nextInt(500) == 0 ? DyeColor.PINK : DyeColor.WHITE;
        }
    }

    @Nullable
    public Sheep getBreedOffspring(ServerLevel p_149044_, AgeableMob p_149045_) {
        Sheep sheep = (Sheep)EntityType.SHEEP.create(p_149044_);
        if (sheep != null) {
            sheep.setColor(this.getOffspringColor(this, (Sheep)p_149045_));
        }

        return sheep;
    }

    public void ate() {
        super.ate();
        this.setSheared(false);
        if (this.isBaby()) {
            this.ageUp(60);
        }

    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_29835_, DifficultyInstance p_29836_, MobSpawnType p_29837_, @Nullable SpawnGroupData p_29838_, @Nullable CompoundTag p_29839_) {
        this.setColor(getRandomSheepColor(p_29835_.getRandom()));
        return super.finalizeSpawn(p_29835_, p_29836_, p_29837_, p_29838_, p_29839_);
    }

    private DyeColor getOffspringColor(Animal p_29824_, Animal p_29825_) {
        DyeColor dyecolor = ((Sheep)p_29824_).getColor();
        DyeColor dyecolor1 = ((Sheep)p_29825_).getColor();
        CraftingContainer craftingcontainer = makeContainer(dyecolor, dyecolor1);
        Optional var10000 = this.level().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftingcontainer, this.level()).map((p_289444_) -> {
            return p_289444_.assemble(craftingcontainer, this.level().registryAccess());
        }).map(ItemStack::getItem);
        Objects.requireNonNull(DyeItem.class);
        var10000 = var10000.filter(DyeItem.class::isInstance);
        Objects.requireNonNull(DyeItem.class);
        return (DyeColor)var10000.map(DyeItem.class::cast).map(DyeItem::getDyeColor).orElseGet(() -> {
            return this.level().random.nextBoolean() ? dyecolor : dyecolor1;
        });
    }

    private static CraftingContainer makeContainer(DyeColor p_29832_, DyeColor p_29833_) {
        CraftingContainer craftingcontainer = new TransientCraftingContainer(new AbstractContainerMenu((MenuType)null, -1) {
            public ItemStack quickMoveStack(Player p_218264_, int p_218265_) {
                return ItemStack.EMPTY;
            }

            public boolean stillValid(Player p_29888_) {
                return false;
            }
        }, 2, 1);
        craftingcontainer.setItem(0, new ItemStack(DyeItem.byColor(p_29832_)));
        craftingcontainer.setItem(1, new ItemStack(DyeItem.byColor(p_29833_)));
        return craftingcontainer;
    }

    protected float getStandingEyeHeight(Pose p_29850_, EntityDimensions p_29851_) {
        return 0.95F * p_29851_.height;
    }

    public boolean isShearable(@NotNull ItemStack item, Level world, BlockPos pos) {
        return this.readyForShearing();
    }

    public @NotNull List<ItemStack> onSheared(@Nullable Player player, @NotNull ItemStack item, Level world, BlockPos pos, int fortune) {
        world.playSound((Player)null, (Entity)this, SoundEvents.SHEEP_SHEAR, player == null ? SoundSource.BLOCKS : SoundSource.PLAYERS, 1.0F, 1.0F);
        this.gameEvent(GameEvent.SHEAR, player);
        if (world.isClientSide) {
            return Collections.emptyList();
        } else {
            this.setSheared(true);
            int i = 1 + this.random.nextInt(3);
            List<ItemStack> items = new ArrayList();

            for(int j = 0; j < i; ++j) {
                items.add(new ItemStack((ItemLike)ITEM_BY_DYE.get(this.getColor())));
            }

            return items;
        }
    }

    static {
        DATA_WOOL_ID = SynchedEntityData.defineId(Sheep.class, EntityDataSerializers.BYTE);
        ITEM_BY_DYE = (Map)Util.make(Maps.newEnumMap(DyeColor.class), (p_29841_) -> {
            p_29841_.put(DyeColor.WHITE, Blocks.WHITE_WOOL);
            p_29841_.put(DyeColor.ORANGE, Blocks.ORANGE_WOOL);
            p_29841_.put(DyeColor.MAGENTA, Blocks.MAGENTA_WOOL);
            p_29841_.put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_WOOL);
            p_29841_.put(DyeColor.YELLOW, Blocks.YELLOW_WOOL);
            p_29841_.put(DyeColor.LIME, Blocks.LIME_WOOL);
            p_29841_.put(DyeColor.PINK, Blocks.PINK_WOOL);
            p_29841_.put(DyeColor.GRAY, Blocks.GRAY_WOOL);
            p_29841_.put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_WOOL);
            p_29841_.put(DyeColor.CYAN, Blocks.CYAN_WOOL);
            p_29841_.put(DyeColor.PURPLE, Blocks.PURPLE_WOOL);
            p_29841_.put(DyeColor.BLUE, Blocks.BLUE_WOOL);
            p_29841_.put(DyeColor.BROWN, Blocks.BROWN_WOOL);
            p_29841_.put(DyeColor.GREEN, Blocks.GREEN_WOOL);
            p_29841_.put(DyeColor.RED, Blocks.RED_WOOL);
            p_29841_.put(DyeColor.BLACK, Blocks.BLACK_WOOL);
        });
        COLORARRAY_BY_COLOR = Maps.newEnumMap((Map)Arrays.stream(DyeColor.values()).collect(Collectors.toMap((p_29868_) -> {
            return p_29868_;
        }, Sheep::createSheepColor)));
    }
}

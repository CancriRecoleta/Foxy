//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.entity.monster;

import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ItemBasedSteering;
import net.minecraft.world.entity.ItemSteerable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

public class Strider extends Animal implements ItemSteerable, Saddleable {
    private static final UUID SUFFOCATING_MODIFIER_UUID = UUID.fromString("9e362924-01de-4ddd-a2b2-d0f7a405a174");
    private static final AttributeModifier SUFFOCATING_MODIFIER;
    private static final float SUFFOCATE_STEERING_MODIFIER = 0.35F;
    private static final float STEERING_MODIFIER = 0.55F;
    private static final Ingredient FOOD_ITEMS;
    private static final Ingredient TEMPT_ITEMS;
    private static final EntityDataAccessor<Integer> DATA_BOOST_TIME;
    private static final EntityDataAccessor<Boolean> DATA_SUFFOCATING;
    private static final EntityDataAccessor<Boolean> DATA_SADDLE_ID;
    private final ItemBasedSteering steering;
    @Nullable
    private TemptGoal temptGoal;
    @Nullable
    private PanicGoal panicGoal;

    public Strider(EntityType<? extends Strider> p_33862_, Level p_33863_) {
        super(p_33862_, p_33863_);
        this.steering = new ItemBasedSteering(this.entityData, DATA_BOOST_TIME, DATA_SADDLE_ID);
        this.blocksBuilding = true;
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.LAVA, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0F);
    }

    public static boolean checkStriderSpawnRules(EntityType<Strider> p_219129_, LevelAccessor p_219130_, MobSpawnType p_219131_, BlockPos p_219132_, RandomSource p_219133_) {
        BlockPos.MutableBlockPos $$5 = p_219132_.mutable();

        do {
            $$5.move(Direction.UP);
        } while(p_219130_.getFluidState($$5).is(FluidTags.LAVA));

        return p_219130_.getBlockState($$5).isAir();
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_33900_) {
        if (DATA_BOOST_TIME.equals(p_33900_) && this.level().isClientSide) {
            this.steering.onSynced();
        }

        super.onSyncedDataUpdated(p_33900_);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_BOOST_TIME, 0);
        this.entityData.define(DATA_SUFFOCATING, false);
        this.entityData.define(DATA_SADDLE_ID, false);
    }

    public void addAdditionalSaveData(CompoundTag p_33918_) {
        super.addAdditionalSaveData(p_33918_);
        this.steering.addAdditionalSaveData(p_33918_);
    }

    public void readAdditionalSaveData(CompoundTag p_33898_) {
        super.readAdditionalSaveData(p_33898_);
        this.steering.readAdditionalSaveData(p_33898_);
    }

    public boolean isSaddled() {
        return this.steering.hasSaddle();
    }

    public boolean isSaddleable() {
        return this.isAlive() && !this.isBaby();
    }

    public void equipSaddle(@Nullable SoundSource p_33878_) {
        this.steering.setSaddle(true);
        if (p_33878_ != null) {
            this.level().playSound((Player)null, (Entity)this, SoundEvents.STRIDER_SADDLE, p_33878_, 0.5F, 1.0F);
        }

    }

    protected void registerGoals() {
        this.panicGoal = new PanicGoal(this, 1.65);
        this.goalSelector.addGoal(1, this.panicGoal);
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        this.temptGoal = new TemptGoal(this, 1.4, TEMPT_ITEMS, false);
        this.goalSelector.addGoal(3, this.temptGoal);
        this.goalSelector.addGoal(4, new StriderGoToLavaGoal(this, 1.0));
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.0));
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 1.0, 60));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Strider.class, 8.0F));
    }

    public void setSuffocating(boolean p_33952_) {
        this.entityData.set(DATA_SUFFOCATING, p_33952_);
        AttributeInstance $$1 = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if ($$1 != null) {
            $$1.removeModifier(SUFFOCATING_MODIFIER_UUID);
            if (p_33952_) {
                $$1.addTransientModifier(SUFFOCATING_MODIFIER);
            }
        }

    }

    public boolean isSuffocating() {
        return (Boolean)this.entityData.get(DATA_SUFFOCATING);
    }

    public boolean canStandOnFluid(FluidState p_204067_) {
        return p_204067_.is(FluidTags.LAVA);
    }

    public double getPassengersRidingOffset() {
        float $$0 = Math.min(0.25F, this.walkAnimation.speed());
        float $$1 = this.walkAnimation.position();
        return (double)this.getBbHeight() - 0.19 + (double)(0.12F * Mth.cos($$1 * 1.5F) * 2.0F * $$0);
    }

    public boolean checkSpawnObstruction(LevelReader p_33880_) {
        return p_33880_.isUnobstructed(this);
    }

    @Nullable
    public LivingEntity getControllingPassenger() {
        Entity var2 = this.getFirstPassenger();
        if (var2 instanceof Player $$0) {
            if ($$0.getMainHandItem().is(Items.WARPED_FUNGUS_ON_A_STICK) || $$0.getOffhandItem().is(Items.WARPED_FUNGUS_ON_A_STICK)) {
                return $$0;
            }
        }

        return null;
    }

    public Vec3 getDismountLocationForPassenger(LivingEntity p_33908_) {
        Vec3[] $$1 = new Vec3[]{getCollisionHorizontalEscapeVector((double)this.getBbWidth(), (double)p_33908_.getBbWidth(), p_33908_.getYRot()), getCollisionHorizontalEscapeVector((double)this.getBbWidth(), (double)p_33908_.getBbWidth(), p_33908_.getYRot() - 22.5F), getCollisionHorizontalEscapeVector((double)this.getBbWidth(), (double)p_33908_.getBbWidth(), p_33908_.getYRot() + 22.5F), getCollisionHorizontalEscapeVector((double)this.getBbWidth(), (double)p_33908_.getBbWidth(), p_33908_.getYRot() - 45.0F), getCollisionHorizontalEscapeVector((double)this.getBbWidth(), (double)p_33908_.getBbWidth(), p_33908_.getYRot() + 45.0F)};
        Set<BlockPos> $$2 = Sets.newLinkedHashSet();
        double $$3 = this.getBoundingBox().maxY;
        double $$4 = this.getBoundingBox().minY - 0.5;
        BlockPos.MutableBlockPos $$5 = new BlockPos.MutableBlockPos();
        Vec3[] var9 = $$1;
        int var10 = $$1.length;

        for(int var11 = 0; var11 < var10; ++var11) {
            Vec3 $$6 = var9[var11];
            $$5.set(this.getX() + $$6.x, $$3, this.getZ() + $$6.z);

            for(double $$7 = $$3; $$7 > $$4; --$$7) {
                $$2.add($$5.immutable());
                $$5.move(Direction.DOWN);
            }
        }

        Iterator var17 = $$2.iterator();

        while(true) {
            BlockPos $$8;
            double $$9;
            do {
                do {
                    if (!var17.hasNext()) {
                        return new Vec3(this.getX(), this.getBoundingBox().maxY, this.getZ());
                    }

                    $$8 = (BlockPos)var17.next();
                } while(this.level().getFluidState($$8).is(FluidTags.LAVA));

                $$9 = this.level().getBlockFloorHeight($$8);
            } while(!DismountHelper.isBlockFloorValid($$9));

            Vec3 $$10 = Vec3.upFromBottomCenterOf($$8, $$9);
            UnmodifiableIterator var14 = p_33908_.getDismountPoses().iterator();

            while(var14.hasNext()) {
                Pose $$11 = (Pose)var14.next();
                AABB $$12 = p_33908_.getLocalBoundsForPose($$11);
                if (DismountHelper.canDismountTo(this.level(), p_33908_, $$12.move($$10))) {
                    p_33908_.setPose($$11);
                    return $$10;
                }
            }
        }
    }

    protected void tickRidden(Player p_278331_, Vec3 p_278234_) {
        this.setRot(p_278331_.getYRot(), p_278331_.getXRot() * 0.5F);
        this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
        this.steering.tickBoost();
        super.tickRidden(p_278331_, p_278234_);
    }

    protected Vec3 getRiddenInput(Player p_278251_, Vec3 p_275578_) {
        return new Vec3(0.0, 0.0, 1.0);
    }

    protected float getRiddenSpeed(Player p_278317_) {
        return (float)(this.getAttributeValue(Attributes.MOVEMENT_SPEED) * (double)(this.isSuffocating() ? 0.35F : 0.55F) * (double)this.steering.boostFactor());
    }

    protected float nextStep() {
        return this.moveDist + 0.6F;
    }

    protected void playStepSound(BlockPos p_33915_, BlockState p_33916_) {
        this.playSound(this.isInLava() ? SoundEvents.STRIDER_STEP_LAVA : SoundEvents.STRIDER_STEP, 1.0F, 1.0F);
    }

    public boolean boost() {
        return this.steering.boost(this.getRandom());
    }

    protected void checkFallDamage(double p_33870_, boolean p_33871_, BlockState p_33872_, BlockPos p_33873_) {
        this.checkInsideBlocks();
        if (this.isInLava()) {
            this.resetFallDistance();
        } else {
            super.checkFallDamage(p_33870_, p_33871_, p_33872_, p_33873_);
        }
    }

    public void tick() {
        if (this.isBeingTempted() && this.random.nextInt(140) == 0) {
            this.playSound(SoundEvents.STRIDER_HAPPY, 1.0F, this.getVoicePitch());
        } else if (this.isPanicking() && this.random.nextInt(60) == 0) {
            this.playSound(SoundEvents.STRIDER_RETREAT, 1.0F, this.getVoicePitch());
        }

        if (!this.isNoAi()) {
            boolean var10000;
            boolean $$2;
            label36: {
                BlockState $$0 = this.level().getBlockState(this.blockPosition());
                BlockState $$1 = this.getBlockStateOnLegacy();
                $$2 = $$0.is(BlockTags.STRIDER_WARM_BLOCKS) || $$1.is(BlockTags.STRIDER_WARM_BLOCKS) || this.getFluidHeight(FluidTags.LAVA) > 0.0;
                Entity var6 = this.getVehicle();
                if (var6 instanceof Strider) {
                    Strider $$3 = (Strider)var6;
                    if ($$3.isSuffocating()) {
                        var10000 = true;
                        break label36;
                    }
                }

                var10000 = false;
            }

            boolean $$4 = var10000;
            this.setSuffocating(!$$2 || $$4);
        }

        super.tick();
        this.floatStrider();
        this.checkInsideBlocks();
    }

    private boolean isPanicking() {
        return this.panicGoal != null && this.panicGoal.isRunning();
    }

    private boolean isBeingTempted() {
        return this.temptGoal != null && this.temptGoal.isRunning();
    }

    protected boolean shouldPassengersInheritMalus() {
        return true;
    }

    private void floatStrider() {
        if (this.isInLava()) {
            CollisionContext $$0 = CollisionContext.of(this);
            if ($$0.isAbove(LiquidBlock.STABLE_SHAPE, this.blockPosition(), true) && !this.level().getFluidState(this.blockPosition().above()).is(FluidTags.LAVA)) {
                this.setOnGround(true);
            } else {
                this.setDeltaMovement(this.getDeltaMovement().scale(0.5).add(0.0, 0.05, 0.0));
            }
        }

    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.17499999701976776).add(Attributes.FOLLOW_RANGE, 16.0);
    }

    protected SoundEvent getAmbientSound() {
        return !this.isPanicking() && !this.isBeingTempted() ? SoundEvents.STRIDER_AMBIENT : null;
    }

    protected SoundEvent getHurtSound(DamageSource p_33934_) {
        return SoundEvents.STRIDER_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.STRIDER_DEATH;
    }

    protected boolean canAddPassenger(Entity p_33950_) {
        return !this.isVehicle() && !this.isEyeInFluid(FluidTags.LAVA);
    }

    public boolean isSensitiveToWater() {
        return true;
    }

    public boolean isOnFire() {
        return false;
    }

    protected PathNavigation createNavigation(Level p_33913_) {
        return new StriderPathNavigation(this, p_33913_);
    }

    public float getWalkTargetValue(BlockPos p_33895_, LevelReader p_33896_) {
        if (p_33896_.getBlockState(p_33895_).getFluidState().is(FluidTags.LAVA)) {
            return 10.0F;
        } else {
            return this.isInLava() ? Float.NEGATIVE_INFINITY : 0.0F;
        }
    }

    @Nullable
    public Strider getBreedOffspring(ServerLevel p_149861_, AgeableMob p_149862_) {
        return (Strider)EntityType.STRIDER.create(p_149861_);
    }

    public boolean isFood(ItemStack p_33946_) {
        return FOOD_ITEMS.test(p_33946_);
    }

    protected void dropEquipment() {
        super.dropEquipment();
        if (this.isSaddled()) {
            this.spawnAtLocation(Items.SADDLE);
        }

    }

    public InteractionResult mobInteract(Player p_33910_, InteractionHand p_33911_) {
        boolean $$2 = this.isFood(p_33910_.getItemInHand(p_33911_));
        if (!$$2 && this.isSaddled() && !this.isVehicle() && !p_33910_.isSecondaryUseActive()) {
            if (!this.level().isClientSide) {
                p_33910_.startRiding(this);
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide);
        } else {
            InteractionResult $$3 = super.mobInteract(p_33910_, p_33911_);
            if (!$$3.consumesAction()) {
                ItemStack $$4 = p_33910_.getItemInHand(p_33911_);
                return $$4.is(Items.SADDLE) ? $$4.interactLivingEntity(p_33910_, this, p_33911_) : InteractionResult.PASS;
            } else {
                if ($$2 && !this.isSilent()) {
                    this.level().playSound((Player)null, this.getX(), this.getY(), this.getZ(), SoundEvents.STRIDER_EAT, this.getSoundSource(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
                }

                return $$3;
            }
        }
    }

    public Vec3 getLeashOffset() {
        return new Vec3(0.0, (double)(0.6F * this.getEyeHeight()), (double)(this.getBbWidth() * 0.4F));
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_33887_, DifficultyInstance p_33888_, MobSpawnType p_33889_, @Nullable SpawnGroupData p_33890_, @Nullable CompoundTag p_33891_) {
        if (this.isBaby()) {
            return super.finalizeSpawn(p_33887_, p_33888_, p_33889_, (SpawnGroupData)p_33890_, p_33891_);
        } else {
            RandomSource $$5 = p_33887_.getRandom();
            if ($$5.nextInt(30) == 0) {
                Mob $$6 = (Mob)EntityType.ZOMBIFIED_PIGLIN.create(p_33887_.getLevel());
                if ($$6 != null) {
                    p_33890_ = this.spawnJockey(p_33887_, p_33888_, $$6, new Zombie.ZombieGroupData(Zombie.getSpawnAsBabyOdds($$5), false));
                    $$6.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.WARPED_FUNGUS_ON_A_STICK));
                    this.equipSaddle((SoundSource)null);
                }
            } else if ($$5.nextInt(10) == 0) {
                AgeableMob $$7 = (AgeableMob)EntityType.STRIDER.create(p_33887_.getLevel());
                if ($$7 != null) {
                    $$7.setAge(-24000);
                    p_33890_ = this.spawnJockey(p_33887_, p_33888_, $$7, (SpawnGroupData)null);
                }
            } else {
                p_33890_ = new AgeableMob.AgeableMobGroupData(0.5F);
            }

            return super.finalizeSpawn(p_33887_, p_33888_, p_33889_, (SpawnGroupData)p_33890_, p_33891_);
        }
    }

    private SpawnGroupData spawnJockey(ServerLevelAccessor p_33882_, DifficultyInstance p_33883_, Mob p_33884_, @Nullable SpawnGroupData p_33885_) {
        p_33884_.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
        p_33884_.finalizeSpawn(p_33882_, p_33883_, MobSpawnType.JOCKEY, p_33885_, (CompoundTag)null);
        p_33884_.startRiding(this, true);
        return new AgeableMob.AgeableMobGroupData(0.0F);
    }

    static {
        SUFFOCATING_MODIFIER = new AttributeModifier(SUFFOCATING_MODIFIER_UUID, "Strider suffocating modifier", -0.3400000035762787, Operation.MULTIPLY_BASE);
        FOOD_ITEMS = Ingredient.of(Items.WARPED_FUNGUS);
        TEMPT_ITEMS = Ingredient.of(Items.WARPED_FUNGUS, Items.WARPED_FUNGUS_ON_A_STICK);
        DATA_BOOST_TIME = SynchedEntityData.defineId(Strider.class, EntityDataSerializers.INT);
        DATA_SUFFOCATING = SynchedEntityData.defineId(Strider.class, EntityDataSerializers.BOOLEAN);
        DATA_SADDLE_ID = SynchedEntityData.defineId(Strider.class, EntityDataSerializers.BOOLEAN);
    }

    private static class StriderGoToLavaGoal extends MoveToBlockGoal {
        private final Strider strider;

        StriderGoToLavaGoal(Strider p_33955_, double p_33956_) {
            super(p_33955_, p_33956_, 8, 2);
            this.strider = p_33955_;
        }

        public BlockPos getMoveToTarget() {
            return this.blockPos;
        }

        public boolean canContinueToUse() {
            return !this.strider.isInLava() && this.isValidTarget(this.strider.level(), this.blockPos);
        }

        public boolean canUse() {
            return !this.strider.isInLava() && super.canUse();
        }

        public boolean shouldRecalculatePath() {
            return this.tryTicks % 20 == 0;
        }

        protected boolean isValidTarget(LevelReader p_33963_, BlockPos p_33964_) {
            return p_33963_.getBlockState(p_33964_).is(Blocks.LAVA) && p_33963_.getBlockState(p_33964_.above()).isPathfindable(p_33963_, p_33964_, PathComputationType.LAND);
        }
    }

    static class StriderPathNavigation extends GroundPathNavigation {
        StriderPathNavigation(Strider p_33969_, Level p_33970_) {
            super(p_33969_, p_33970_);
        }

        protected PathFinder createPathFinder(int p_33972_) {
            this.nodeEvaluator = new WalkNodeEvaluator();
            this.nodeEvaluator.setCanPassDoors(true);
            return new PathFinder(this.nodeEvaluator, p_33972_);
        }

        protected boolean hasValidPathType(BlockPathTypes p_33974_) {
            return p_33974_ != BlockPathTypes.LAVA && p_33974_ != BlockPathTypes.DAMAGE_FIRE && p_33974_ != BlockPathTypes.DANGER_FIRE ? super.hasValidPathType(p_33974_) : true;
        }

        public boolean isStableDestination(BlockPos p_33976_) {
            return this.level.getBlockState(p_33976_).is(Blocks.LAVA) || super.isStableDestination(p_33976_);
        }
    }
}

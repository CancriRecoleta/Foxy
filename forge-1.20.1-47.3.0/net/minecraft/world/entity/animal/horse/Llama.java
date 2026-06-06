//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.entity.animal.horse;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.ByIdMap.OutOfBoundsStrategy;
import net.minecraft.world.Container;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LlamaFollowCaravanGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.RunAroundLikeCrazyGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WoolCarpetBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class Llama extends AbstractChestedHorse implements VariantHolder<Variant>, RangedAttackMob {
    private static final int MAX_STRENGTH = 5;
    private static final Ingredient FOOD_ITEMS;
    private static final EntityDataAccessor<Integer> DATA_STRENGTH_ID;
    private static final EntityDataAccessor<Integer> DATA_SWAG_ID;
    private static final EntityDataAccessor<Integer> DATA_VARIANT_ID;
    boolean didSpit;
    @Nullable
    private Llama caravanHead;
    @Nullable
    private Llama caravanTail;

    public Llama(EntityType<? extends Llama> p_30750_, Level p_30751_) {
        super(p_30750_, p_30751_);
    }

    public boolean isTraderLlama() {
        return false;
    }

    private void setStrength(int p_30841_) {
        this.entityData.set(DATA_STRENGTH_ID, Math.max(1, Math.min(5, p_30841_)));
    }

    private void setRandomStrength(RandomSource p_218818_) {
        int $$1 = p_218818_.nextFloat() < 0.04F ? 5 : 3;
        this.setStrength(1 + p_218818_.nextInt($$1));
    }

    public int getStrength() {
        return (Integer)this.entityData.get(DATA_STRENGTH_ID);
    }

    public void addAdditionalSaveData(CompoundTag p_30793_) {
        super.addAdditionalSaveData(p_30793_);
        p_30793_.putInt("Variant", this.getVariant().id);
        p_30793_.putInt("Strength", this.getStrength());
        if (!this.inventory.getItem(1).isEmpty()) {
            p_30793_.put("DecorItem", this.inventory.getItem(1).save(new CompoundTag()));
        }

    }

    public void readAdditionalSaveData(CompoundTag p_30780_) {
        this.setStrength(p_30780_.getInt("Strength"));
        super.readAdditionalSaveData(p_30780_);
        this.setVariant(net.minecraft.world.entity.animal.horse.Llama.Variant.byId(p_30780_.getInt("Variant")));
        if (p_30780_.contains("DecorItem", 10)) {
            this.inventory.setItem(1, ItemStack.of(p_30780_.getCompound("DecorItem")));
        }

        this.updateContainerEquipment();
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RunAroundLikeCrazyGoal(this, 1.2));
        this.goalSelector.addGoal(2, new LlamaFollowCaravanGoal(this, 2.0999999046325684));
        this.goalSelector.addGoal(3, new RangedAttackGoal(this, 1.25, 40, 20.0F));
        this.goalSelector.addGoal(3, new PanicGoal(this, 1.2));
        this.goalSelector.addGoal(4, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(5, new TemptGoal(this, 1.25, Ingredient.of(Items.HAY_BLOCK), false));
        this.goalSelector.addGoal(6, new FollowParentGoal(this, 1.0));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.7));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new LlamaHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new LlamaAttackWolfGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createBaseChestedHorseAttributes().add(Attributes.FOLLOW_RANGE, 40.0);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_STRENGTH_ID, 0);
        this.entityData.define(DATA_SWAG_ID, -1);
        this.entityData.define(DATA_VARIANT_ID, 0);
    }

    public Variant getVariant() {
        return net.minecraft.world.entity.animal.horse.Llama.Variant.byId((Integer)this.entityData.get(DATA_VARIANT_ID));
    }

    public void setVariant(Variant p_262628_) {
        this.entityData.set(DATA_VARIANT_ID, p_262628_.id);
    }

    protected int getInventorySize() {
        return this.hasChest() ? 2 + 3 * this.getInventoryColumns() : super.getInventorySize();
    }

    protected void positionRider(Entity p_30830_, Entity.MoveFunction p_289531_) {
        if (this.hasPassenger(p_30830_)) {
            float $$2 = Mth.cos(this.yBodyRot * 0.017453292F);
            float $$3 = Mth.sin(this.yBodyRot * 0.017453292F);
            float $$4 = 0.3F;
            p_289531_.accept(p_30830_, this.getX() + (double)(0.3F * $$3), this.getY() + this.getPassengersRidingOffset() + p_30830_.getMyRidingOffset(), this.getZ() - (double)(0.3F * $$2));
        }
    }

    public double getPassengersRidingOffset() {
        return (double)this.getBbHeight() * 0.6;
    }

    @Nullable
    public LivingEntity getControllingPassenger() {
        return null;
    }

    public boolean isFood(ItemStack p_30832_) {
        return FOOD_ITEMS.test(p_30832_);
    }

    protected boolean handleEating(Player p_30796_, ItemStack p_30797_) {
        int $$2 = 0;
        int $$3 = 0;
        float $$4 = 0.0F;
        boolean $$5 = false;
        if (p_30797_.is(Items.WHEAT)) {
            $$2 = 10;
            $$3 = 3;
            $$4 = 2.0F;
        } else if (p_30797_.is(Blocks.HAY_BLOCK.asItem())) {
            $$2 = 90;
            $$3 = 6;
            $$4 = 10.0F;
            if (this.isTamed() && this.getAge() == 0 && this.canFallInLove()) {
                $$5 = true;
                this.setInLove(p_30796_);
            }
        }

        if (this.getHealth() < this.getMaxHealth() && $$4 > 0.0F) {
            this.heal($$4);
            $$5 = true;
        }

        if (this.isBaby() && $$2 > 0) {
            this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), 0.0, 0.0, 0.0);
            if (!this.level().isClientSide) {
                this.ageUp($$2);
            }

            $$5 = true;
        }

        if ($$3 > 0 && ($$5 || !this.isTamed()) && this.getTemper() < this.getMaxTemper()) {
            $$5 = true;
            if (!this.level().isClientSide) {
                this.modifyTemper($$3);
            }
        }

        if ($$5 && !this.isSilent()) {
            SoundEvent $$6 = this.getEatingSound();
            if ($$6 != null) {
                this.level().playSound((Player)null, this.getX(), this.getY(), this.getZ(), this.getEatingSound(), this.getSoundSource(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
            }
        }

        return $$5;
    }

    public boolean isImmobile() {
        return this.isDeadOrDying() || this.isEating();
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_30774_, DifficultyInstance p_30775_, MobSpawnType p_30776_, @Nullable SpawnGroupData p_30777_, @Nullable CompoundTag p_30778_) {
        RandomSource $$5 = p_30774_.getRandom();
        this.setRandomStrength($$5);
        Variant $$7;
        if (p_30777_ instanceof LlamaGroupData) {
            $$7 = ((LlamaGroupData)p_30777_).variant;
        } else {
            $$7 = (Variant)Util.getRandom((Object[])net.minecraft.world.entity.animal.horse.Llama.Variant.values(), $$5);
            p_30777_ = new LlamaGroupData($$7);
        }

        this.setVariant($$7);
        return super.finalizeSpawn(p_30774_, p_30775_, p_30776_, (SpawnGroupData)p_30777_, p_30778_);
    }

    protected boolean canPerformRearing() {
        return false;
    }

    protected SoundEvent getAngrySound() {
        return SoundEvents.LLAMA_ANGRY;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.LLAMA_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource p_30803_) {
        return SoundEvents.LLAMA_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.LLAMA_DEATH;
    }

    @Nullable
    protected SoundEvent getEatingSound() {
        return SoundEvents.LLAMA_EAT;
    }

    protected void playStepSound(BlockPos p_30790_, BlockState p_30791_) {
        this.playSound(SoundEvents.LLAMA_STEP, 0.15F, 1.0F);
    }

    protected void playChestEquipsSound() {
        this.playSound(SoundEvents.LLAMA_CHEST, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
    }

    public int getInventoryColumns() {
        return this.getStrength();
    }

    public boolean canWearArmor() {
        return true;
    }

    public boolean isWearingArmor() {
        return !this.inventory.getItem(1).isEmpty();
    }

    public boolean isArmor(ItemStack p_30834_) {
        return p_30834_.is(ItemTags.WOOL_CARPETS);
    }

    public boolean isSaddleable() {
        return false;
    }

    public void containerChanged(Container p_30760_) {
        DyeColor $$1 = this.getSwag();
        super.containerChanged(p_30760_);
        DyeColor $$2 = this.getSwag();
        if (this.tickCount > 20 && $$2 != null && $$2 != $$1) {
            this.playSound(SoundEvents.LLAMA_SWAG, 0.5F, 1.0F);
        }

    }

    protected void updateContainerEquipment() {
        if (!this.level().isClientSide) {
            super.updateContainerEquipment();
            this.setSwag(getDyeColor(this.inventory.getItem(1)));
        }
    }

    private void setSwag(@Nullable DyeColor p_30772_) {
        this.entityData.set(DATA_SWAG_ID, p_30772_ == null ? -1 : p_30772_.getId());
    }

    @Nullable
    private static DyeColor getDyeColor(ItemStack p_30836_) {
        Block $$1 = Block.byItem(p_30836_.getItem());
        return $$1 instanceof WoolCarpetBlock ? ((WoolCarpetBlock)$$1).getColor() : null;
    }

    @Nullable
    public DyeColor getSwag() {
        int $$0 = (Integer)this.entityData.get(DATA_SWAG_ID);
        return $$0 == -1 ? null : DyeColor.byId($$0);
    }

    public int getMaxTemper() {
        return 30;
    }

    public boolean canMate(Animal p_30765_) {
        return p_30765_ != this && p_30765_ instanceof Llama && this.canParent() && ((Llama)p_30765_).canParent();
    }

    @Nullable
    public Llama getBreedOffspring(ServerLevel p_149545_, AgeableMob p_149546_) {
        Llama $$2 = this.makeNewLlama();
        if ($$2 != null) {
            this.setOffspringAttributes(p_149546_, $$2);
            Llama $$3 = (Llama)p_149546_;
            int $$4 = this.random.nextInt(Math.max(this.getStrength(), $$3.getStrength())) + 1;
            if (this.random.nextFloat() < 0.03F) {
                ++$$4;
            }

            $$2.setStrength($$4);
            $$2.setVariant(this.random.nextBoolean() ? this.getVariant() : $$3.getVariant());
        }

        return $$2;
    }

    @Nullable
    protected Llama makeNewLlama() {
        return (Llama)EntityType.LLAMA.create(this.level());
    }

    private void spit(LivingEntity p_30828_) {
        LlamaSpit $$1 = new LlamaSpit(this.level(), this);
        double $$2 = p_30828_.getX() - this.getX();
        double $$3 = p_30828_.getY(0.3333333333333333) - $$1.getY();
        double $$4 = p_30828_.getZ() - this.getZ();
        double $$5 = Math.sqrt($$2 * $$2 + $$4 * $$4) * 0.20000000298023224;
        $$1.shoot($$2, $$3 + $$5, $$4, 1.5F, 10.0F);
        if (!this.isSilent()) {
            this.level().playSound((Player)null, this.getX(), this.getY(), this.getZ(), SoundEvents.LLAMA_SPIT, this.getSoundSource(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
        }

        this.level().addFreshEntity($$1);
        this.didSpit = true;
    }

    void setDidSpit(boolean p_30753_) {
        this.didSpit = p_30753_;
    }

    public boolean causeFallDamage(float p_149538_, float p_149539_, DamageSource p_149540_) {
        int $$3 = this.calculateFallDamage(p_149538_, p_149539_);
        if ($$3 <= 0) {
            return false;
        } else {
            if (p_149538_ >= 6.0F) {
                this.hurt(p_149540_, (float)$$3);
                if (this.isVehicle()) {
                    Iterator var5 = this.getIndirectPassengers().iterator();

                    while(var5.hasNext()) {
                        Entity $$4 = (Entity)var5.next();
                        $$4.hurt(p_149540_, (float)$$3);
                    }
                }
            }

            this.playBlockFallSound();
            return true;
        }
    }

    public void leaveCaravan() {
        if (this.caravanHead != null) {
            this.caravanHead.caravanTail = null;
        }

        this.caravanHead = null;
    }

    public void joinCaravan(Llama p_30767_) {
        this.caravanHead = p_30767_;
        this.caravanHead.caravanTail = this;
    }

    public boolean hasCaravanTail() {
        return this.caravanTail != null;
    }

    public boolean inCaravan() {
        return this.caravanHead != null;
    }

    @Nullable
    public Llama getCaravanHead() {
        return this.caravanHead;
    }

    protected double followLeashSpeed() {
        return 2.0;
    }

    protected void followMommy() {
        if (!this.inCaravan() && this.isBaby()) {
            super.followMommy();
        }

    }

    public boolean canEatGrass() {
        return false;
    }

    public void performRangedAttack(LivingEntity p_30762_, float p_30763_) {
        this.spit(p_30762_);
    }

    public Vec3 getLeashOffset() {
        return new Vec3(0.0, 0.75 * (double)this.getEyeHeight(), (double)this.getBbWidth() * 0.5);
    }

    static {
        FOOD_ITEMS = Ingredient.of(Items.WHEAT, Blocks.HAY_BLOCK.asItem());
        DATA_STRENGTH_ID = SynchedEntityData.defineId(Llama.class, EntityDataSerializers.INT);
        DATA_SWAG_ID = SynchedEntityData.defineId(Llama.class, EntityDataSerializers.INT);
        DATA_VARIANT_ID = SynchedEntityData.defineId(Llama.class, EntityDataSerializers.INT);
    }

    public static enum Variant implements StringRepresentable {
        CREAMY(0, "creamy"),
        WHITE(1, "white"),
        BROWN(2, "brown"),
        GRAY(3, "gray");

        public static final Codec<Variant> CODEC = StringRepresentable.fromEnum(Variant::values);
        private static final IntFunction<Variant> BY_ID = ByIdMap.continuous(Variant::getId, values(), OutOfBoundsStrategy.CLAMP);
        final int id;
        private final String name;

        private Variant(int p_262677_, String p_262641_) {
            this.id = p_262677_;
            this.name = p_262641_;
        }

        public int getId() {
            return this.id;
        }

        public static Variant byId(int p_262608_) {
            return (Variant)BY_ID.apply(p_262608_);
        }

        public String getSerializedName() {
            return this.name;
        }
    }

    static class LlamaHurtByTargetGoal extends HurtByTargetGoal {
        public LlamaHurtByTargetGoal(Llama p_30854_) {
            super(p_30854_);
        }

        public boolean canContinueToUse() {
            if (this.mob instanceof Llama) {
                Llama $$0 = (Llama)this.mob;
                if ($$0.didSpit) {
                    $$0.setDidSpit(false);
                    return false;
                }
            }

            return super.canContinueToUse();
        }
    }

    private static class LlamaAttackWolfGoal extends NearestAttackableTargetGoal<Wolf> {
        public LlamaAttackWolfGoal(Llama p_30843_) {
            super(p_30843_, Wolf.class, 16, false, true, (p_289450_) -> {
                return !((Wolf)p_289450_).isTame();
            });
        }

        protected double getFollowDistance() {
            return super.getFollowDistance() * 0.25;
        }
    }

    static class LlamaGroupData extends AgeableMob.AgeableMobGroupData {
        public final Variant variant;

        LlamaGroupData(Variant p_262658_) {
            super(true);
            this.variant = p_262658_;
        }
    }
}

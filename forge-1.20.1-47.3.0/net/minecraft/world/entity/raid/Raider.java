//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.entity.raid;

import com.google.common.collect.Lists;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.PathfindToRaidGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.ai.village.poi.PoiManager.Occupancy;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

public abstract class Raider extends PatrollingMonster {
    protected static final EntityDataAccessor<Boolean> IS_CELEBRATING;
    static final Predicate<ItemEntity> ALLOWED_ITEMS;
    @Nullable
    protected Raid raid;
    private int wave;
    private boolean canJoinRaid;
    private int ticksOutsideRaid;

    protected Raider(EntityType<? extends Raider> p_37839_, Level p_37840_) {
        super(p_37839_, p_37840_);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new ObtainRaidLeaderBannerGoal(this));
        this.goalSelector.addGoal(3, new PathfindToRaidGoal(this));
        this.goalSelector.addGoal(4, new RaiderMoveThroughVillageGoal(this, 1.0499999523162842, 1));
        this.goalSelector.addGoal(5, new RaiderCelebration(this));
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_CELEBRATING, false);
    }

    public abstract void applyRaidBuffs(int var1, boolean var2);

    public boolean canJoinRaid() {
        return this.canJoinRaid;
    }

    public void setCanJoinRaid(boolean p_37898_) {
        this.canJoinRaid = p_37898_;
    }

    public void aiStep() {
        if (this.level() instanceof ServerLevel && this.isAlive()) {
            Raid $$0 = this.getCurrentRaid();
            if (this.canJoinRaid()) {
                if ($$0 == null) {
                    if (this.level().getGameTime() % 20L == 0L) {
                        Raid $$1 = ((ServerLevel)this.level()).getRaidAt(this.blockPosition());
                        if ($$1 != null && Raids.canJoinRaid(this, $$1)) {
                            $$1.joinRaid($$1.getGroupsSpawned(), this, (BlockPos)null, true);
                        }
                    }
                } else {
                    LivingEntity $$2 = this.getTarget();
                    if ($$2 != null && ($$2.getType() == EntityType.PLAYER || $$2.getType() == EntityType.IRON_GOLEM)) {
                        this.noActionTime = 0;
                    }
                }
            }
        }

        super.aiStep();
    }

    protected void updateNoActionTime() {
        this.noActionTime += 2;
    }

    public void die(DamageSource p_37847_) {
        if (this.level() instanceof ServerLevel) {
            Entity $$1 = p_37847_.getEntity();
            Raid $$2 = this.getCurrentRaid();
            if ($$2 != null) {
                if (this.isPatrolLeader()) {
                    $$2.removeLeader(this.getWave());
                }

                if ($$1 != null && $$1.getType() == EntityType.PLAYER) {
                    $$2.addHeroOfTheVillage($$1);
                }

                $$2.removeFromRaid(this, false);
            }

            if (this.isPatrolLeader() && $$2 == null && ((ServerLevel)this.level()).getRaidAt(this.blockPosition()) == null) {
                ItemStack $$3 = this.getItemBySlot(EquipmentSlot.HEAD);
                Player $$4 = null;
                Entity $$5 = $$1;
                if ($$5 instanceof Player) {
                    $$4 = (Player)$$5;
                } else if ($$5 instanceof Wolf) {
                    Wolf $$6 = (Wolf)$$5;
                    LivingEntity $$7 = $$6.getOwner();
                    if ($$6.isTame() && $$7 instanceof Player) {
                        $$4 = (Player)$$7;
                    }
                }

                if (!$$3.isEmpty() && ItemStack.matches($$3, Raid.getLeaderBannerInstance()) && $$4 != null) {
                    MobEffectInstance $$8 = $$4.getEffect(MobEffects.BAD_OMEN);
                    int $$9 = 1;
                    if ($$8 != null) {
                        $$9 += $$8.getAmplifier();
                        $$4.removeEffectNoUpdate(MobEffects.BAD_OMEN);
                    } else {
                        --$$9;
                    }

                    $$9 = Mth.clamp($$9, 0, 4);
                    MobEffectInstance $$10 = new MobEffectInstance(MobEffects.BAD_OMEN, 120000, $$9, false, false, true);
                    if (!this.level().getGameRules().getBoolean(GameRules.RULE_DISABLE_RAIDS)) {
                        $$4.addEffect($$10);
                    }
                }
            }
        }

        super.die(p_37847_);
    }

    public boolean canJoinPatrol() {
        return !this.hasActiveRaid();
    }

    public void setCurrentRaid(@Nullable Raid p_37852_) {
        this.raid = p_37852_;
    }

    @Nullable
    public Raid getCurrentRaid() {
        return this.raid;
    }

    public boolean hasActiveRaid() {
        return this.getCurrentRaid() != null && this.getCurrentRaid().isActive();
    }

    public void setWave(int p_37843_) {
        this.wave = p_37843_;
    }

    public int getWave() {
        return this.wave;
    }

    public boolean isCelebrating() {
        return (Boolean)this.entityData.get(IS_CELEBRATING);
    }

    public void setCelebrating(boolean p_37900_) {
        this.entityData.set(IS_CELEBRATING, p_37900_);
    }

    public void addAdditionalSaveData(CompoundTag p_37870_) {
        super.addAdditionalSaveData(p_37870_);
        p_37870_.putInt("Wave", this.wave);
        p_37870_.putBoolean("CanJoinRaid", this.canJoinRaid);
        if (this.raid != null) {
            p_37870_.putInt("RaidId", this.raid.getId());
        }

    }

    public void readAdditionalSaveData(CompoundTag p_37862_) {
        super.readAdditionalSaveData(p_37862_);
        this.wave = p_37862_.getInt("Wave");
        this.canJoinRaid = p_37862_.getBoolean("CanJoinRaid");
        if (p_37862_.contains("RaidId", 3)) {
            if (this.level() instanceof ServerLevel) {
                this.raid = ((ServerLevel)this.level()).getRaids().get(p_37862_.getInt("RaidId"));
            }

            if (this.raid != null) {
                this.raid.addWaveMob(this.wave, this, false);
                if (this.isPatrolLeader()) {
                    this.raid.setLeader(this.wave, this);
                }
            }
        }

    }

    protected void pickUpItem(ItemEntity p_37866_) {
        ItemStack $$1 = p_37866_.getItem();
        boolean $$2 = this.hasActiveRaid() && this.getCurrentRaid().getLeader(this.getWave()) != null;
        if (this.hasActiveRaid() && !$$2 && ItemStack.matches($$1, Raid.getLeaderBannerInstance())) {
            EquipmentSlot $$3 = EquipmentSlot.HEAD;
            ItemStack $$4 = this.getItemBySlot($$3);
            double $$5 = (double)this.getEquipmentDropChance($$3);
            if (!$$4.isEmpty() && (double)Math.max(this.random.nextFloat() - 0.1F, 0.0F) < $$5) {
                this.spawnAtLocation($$4);
            }

            this.onItemPickup(p_37866_);
            this.setItemSlot($$3, $$1);
            this.take(p_37866_, $$1.getCount());
            p_37866_.discard();
            this.getCurrentRaid().setLeader(this.getWave(), this);
            this.setPatrolLeader(true);
        } else {
            super.pickUpItem(p_37866_);
        }

    }

    public boolean removeWhenFarAway(double p_37894_) {
        return this.getCurrentRaid() == null ? super.removeWhenFarAway(p_37894_) : false;
    }

    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.getCurrentRaid() != null;
    }

    public int getTicksOutsideRaid() {
        return this.ticksOutsideRaid;
    }

    public void setTicksOutsideRaid(int p_37864_) {
        this.ticksOutsideRaid = p_37864_;
    }

    public boolean hurt(DamageSource p_37849_, float p_37850_) {
        if (this.hasActiveRaid()) {
            this.getCurrentRaid().updateBossbar();
        }

        return super.hurt(p_37849_, p_37850_);
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_37856_, DifficultyInstance p_37857_, MobSpawnType p_37858_, @Nullable SpawnGroupData p_37859_, @Nullable CompoundTag p_37860_) {
        this.setCanJoinRaid(this.getType() != EntityType.WITCH || p_37858_ != MobSpawnType.NATURAL);
        return super.finalizeSpawn(p_37856_, p_37857_, p_37858_, p_37859_, p_37860_);
    }

    public abstract SoundEvent getCelebrateSound();

    static {
        IS_CELEBRATING = SynchedEntityData.defineId(Raider.class, EntityDataSerializers.BOOLEAN);
        ALLOWED_ITEMS = (p_289494_) -> {
            return !p_289494_.hasPickUpDelay() && p_289494_.isAlive() && ItemStack.matches(p_289494_.getItem(), Raid.getLeaderBannerInstance());
        };
    }

    public class ObtainRaidLeaderBannerGoal<T extends Raider> extends Goal {
        private final T mob;

        public ObtainRaidLeaderBannerGoal(T p_37917_) {
            this.mob = p_37917_;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            Raid $$0 = this.mob.getCurrentRaid();
            if (this.mob.hasActiveRaid() && !this.mob.getCurrentRaid().isOver() && this.mob.canBeLeader() && !ItemStack.matches(this.mob.getItemBySlot(EquipmentSlot.HEAD), Raid.getLeaderBannerInstance())) {
                Raider $$1 = $$0.getLeader(this.mob.getWave());
                if ($$1 == null || !$$1.isAlive()) {
                    List<ItemEntity> $$2 = this.mob.level().getEntitiesOfClass(ItemEntity.class, this.mob.getBoundingBox().inflate(16.0, 8.0, 16.0), Raider.ALLOWED_ITEMS);
                    if (!$$2.isEmpty()) {
                        return this.mob.getNavigation().moveTo((Entity)$$2.get(0), 1.149999976158142);
                    }
                }

                return false;
            } else {
                return false;
            }
        }

        public void tick() {
            if (this.mob.getNavigation().getTargetPos().closerToCenterThan(this.mob.position(), 1.414)) {
                List<ItemEntity> $$0 = this.mob.level().getEntitiesOfClass(ItemEntity.class, this.mob.getBoundingBox().inflate(4.0, 4.0, 4.0), Raider.ALLOWED_ITEMS);
                if (!$$0.isEmpty()) {
                    this.mob.pickUpItem((ItemEntity)$$0.get(0));
                }
            }

        }
    }

    private static class RaiderMoveThroughVillageGoal extends Goal {
        private final Raider raider;
        private final double speedModifier;
        private BlockPos poiPos;
        private final List<BlockPos> visited = Lists.newArrayList();
        private final int distanceToPoi;
        private boolean stuck;

        public RaiderMoveThroughVillageGoal(Raider p_37936_, double p_37937_, int p_37938_) {
            this.raider = p_37936_;
            this.speedModifier = p_37937_;
            this.distanceToPoi = p_37938_;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            this.updateVisited();
            return this.isValidRaid() && this.hasSuitablePoi() && this.raider.getTarget() == null;
        }

        private boolean isValidRaid() {
            return this.raider.hasActiveRaid() && !this.raider.getCurrentRaid().isOver();
        }

        private boolean hasSuitablePoi() {
            ServerLevel $$0 = (ServerLevel)this.raider.level();
            BlockPos $$1 = this.raider.blockPosition();
            Optional<BlockPos> $$2 = $$0.getPoiManager().getRandom((p_219843_) -> {
                return p_219843_.is(PoiTypes.HOME);
            }, this::hasNotVisited, Occupancy.ANY, $$1, 48, this.raider.random);
            if (!$$2.isPresent()) {
                return false;
            } else {
                this.poiPos = ((BlockPos)$$2.get()).immutable();
                return true;
            }
        }

        public boolean canContinueToUse() {
            if (this.raider.getNavigation().isDone()) {
                return false;
            } else {
                return this.raider.getTarget() == null && !this.poiPos.closerToCenterThan(this.raider.position(), (double)(this.raider.getBbWidth() + (float)this.distanceToPoi)) && !this.stuck;
            }
        }

        public void stop() {
            if (this.poiPos.closerToCenterThan(this.raider.position(), (double)this.distanceToPoi)) {
                this.visited.add(this.poiPos);
            }

        }

        public void start() {
            super.start();
            this.raider.setNoActionTime(0);
            this.raider.getNavigation().moveTo((double)this.poiPos.getX(), (double)this.poiPos.getY(), (double)this.poiPos.getZ(), this.speedModifier);
            this.stuck = false;
        }

        public void tick() {
            if (this.raider.getNavigation().isDone()) {
                Vec3 $$0 = Vec3.atBottomCenterOf(this.poiPos);
                Vec3 $$1 = DefaultRandomPos.getPosTowards(this.raider, 16, 7, $$0, 0.3141592741012573);
                if ($$1 == null) {
                    $$1 = DefaultRandomPos.getPosTowards(this.raider, 8, 7, $$0, 1.5707963705062866);
                }

                if ($$1 == null) {
                    this.stuck = true;
                    return;
                }

                this.raider.getNavigation().moveTo($$1.x, $$1.y, $$1.z, this.speedModifier);
            }

        }

        private boolean hasNotVisited(BlockPos p_37943_) {
            Iterator var2 = this.visited.iterator();

            BlockPos $$1;
            do {
                if (!var2.hasNext()) {
                    return true;
                }

                $$1 = (BlockPos)var2.next();
            } while(!Objects.equals(p_37943_, $$1));

            return false;
        }

        private void updateVisited() {
            if (this.visited.size() > 2) {
                this.visited.remove(0);
            }

        }
    }

    public class RaiderCelebration extends Goal {
        private final Raider mob;

        RaiderCelebration(Raider p_37924_) {
            this.mob = p_37924_;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            Raid $$0 = this.mob.getCurrentRaid();
            return this.mob.isAlive() && this.mob.getTarget() == null && $$0 != null && $$0.isLoss();
        }

        public void start() {
            this.mob.setCelebrating(true);
            super.start();
        }

        public void stop() {
            this.mob.setCelebrating(false);
            super.stop();
        }

        public void tick() {
            if (!this.mob.isSilent() && this.mob.random.nextInt(this.adjustedTickDelay(100)) == 0) {
                Raider.this.playSound(Raider.this.getCelebrateSound(), Raider.this.getSoundVolume(), Raider.this.getVoicePitch());
            }

            if (!this.mob.isPassenger() && this.mob.random.nextInt(this.adjustedTickDelay(50)) == 0) {
                this.mob.getJumpControl().jump();
            }

            super.tick();
        }
    }

    protected class HoldGroundAttackGoal extends Goal {
        private final Raider mob;
        private final float hostileRadiusSqr;
        public final TargetingConditions shoutTargeting = TargetingConditions.forNonCombat().range(8.0).ignoreLineOfSight().ignoreInvisibilityTesting();

        public HoldGroundAttackGoal(AbstractIllager p_37907_, float p_37908_) {
            this.mob = p_37907_;
            this.hostileRadiusSqr = p_37908_ * p_37908_;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity $$0 = this.mob.getLastHurtByMob();
            return this.mob.getCurrentRaid() == null && this.mob.isPatrolling() && this.mob.getTarget() != null && !this.mob.isAggressive() && ($$0 == null || $$0.getType() != EntityType.PLAYER);
        }

        public void start() {
            super.start();
            this.mob.getNavigation().stop();
            List<Raider> $$0 = this.mob.level().getNearbyEntities(Raider.class, this.shoutTargeting, this.mob, this.mob.getBoundingBox().inflate(8.0, 8.0, 8.0));
            Iterator var2 = $$0.iterator();

            while(var2.hasNext()) {
                Raider $$1 = (Raider)var2.next();
                $$1.setTarget(this.mob.getTarget());
            }

        }

        public void stop() {
            super.stop();
            LivingEntity $$0 = this.mob.getTarget();
            if ($$0 != null) {
                List<Raider> $$1 = this.mob.level().getNearbyEntities(Raider.class, this.shoutTargeting, this.mob, this.mob.getBoundingBox().inflate(8.0, 8.0, 8.0));
                Iterator var3 = $$1.iterator();

                while(var3.hasNext()) {
                    Raider $$2 = (Raider)var3.next();
                    $$2.setTarget($$0);
                    $$2.setAggressive(true);
                }

                this.mob.setAggressive(true);
            }

        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity $$0 = this.mob.getTarget();
            if ($$0 != null) {
                if (this.mob.distanceToSqr($$0) > (double)this.hostileRadiusSqr) {
                    this.mob.getLookControl().setLookAt($$0, 30.0F, 30.0F);
                    if (this.mob.random.nextInt(50) == 0) {
                        this.mob.playAmbientSound();
                    }
                } else {
                    this.mob.setAggressive(true);
                }

                super.tick();
            }
        }
    }
}

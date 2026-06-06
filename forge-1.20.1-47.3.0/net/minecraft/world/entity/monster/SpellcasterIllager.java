//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.util.ByIdMap.OutOfBoundsStrategy;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.level.Level;

public abstract class SpellcasterIllager extends AbstractIllager {
    private static final EntityDataAccessor<Byte> DATA_SPELL_CASTING_ID;
    protected int spellCastingTickCount;
    private IllagerSpell currentSpell;

    protected SpellcasterIllager(EntityType<? extends SpellcasterIllager> p_33724_, Level p_33725_) {
        super(p_33724_, p_33725_);
        this.currentSpell = net.minecraft.world.entity.monster.SpellcasterIllager.IllagerSpell.NONE;
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_SPELL_CASTING_ID, (byte)0);
    }

    public void readAdditionalSaveData(CompoundTag p_33732_) {
        super.readAdditionalSaveData(p_33732_);
        this.spellCastingTickCount = p_33732_.getInt("SpellTicks");
    }

    public void addAdditionalSaveData(CompoundTag p_33734_) {
        super.addAdditionalSaveData(p_33734_);
        p_33734_.putInt("SpellTicks", this.spellCastingTickCount);
    }

    public AbstractIllager.IllagerArmPose getArmPose() {
        if (this.isCastingSpell()) {
            return net.minecraft.world.entity.monster.AbstractIllager.IllagerArmPose.SPELLCASTING;
        } else {
            return this.isCelebrating() ? net.minecraft.world.entity.monster.AbstractIllager.IllagerArmPose.CELEBRATING : net.minecraft.world.entity.monster.AbstractIllager.IllagerArmPose.CROSSED;
        }
    }

    public boolean isCastingSpell() {
        if (this.level().isClientSide) {
            return (Byte)this.entityData.get(DATA_SPELL_CASTING_ID) > 0;
        } else {
            return this.spellCastingTickCount > 0;
        }
    }

    public void setIsCastingSpell(IllagerSpell p_33728_) {
        this.currentSpell = p_33728_;
        this.entityData.set(DATA_SPELL_CASTING_ID, (byte)p_33728_.id);
    }

    protected IllagerSpell getCurrentSpell() {
        return !this.level().isClientSide ? this.currentSpell : net.minecraft.world.entity.monster.SpellcasterIllager.IllagerSpell.byId((Byte)this.entityData.get(DATA_SPELL_CASTING_ID));
    }

    protected void customServerAiStep() {
        super.customServerAiStep();
        if (this.spellCastingTickCount > 0) {
            --this.spellCastingTickCount;
        }

    }

    public void tick() {
        super.tick();
        if (this.level().isClientSide && this.isCastingSpell()) {
            IllagerSpell $$0 = this.getCurrentSpell();
            double $$1 = $$0.spellColor[0];
            double $$2 = $$0.spellColor[1];
            double $$3 = $$0.spellColor[2];
            float $$4 = this.yBodyRot * 0.017453292F + Mth.cos((float)this.tickCount * 0.6662F) * 0.25F;
            float $$5 = Mth.cos($$4);
            float $$6 = Mth.sin($$4);
            this.level().addParticle(ParticleTypes.ENTITY_EFFECT, this.getX() + (double)$$5 * 0.6, this.getY() + 1.8, this.getZ() + (double)$$6 * 0.6, $$1, $$2, $$3);
            this.level().addParticle(ParticleTypes.ENTITY_EFFECT, this.getX() - (double)$$5 * 0.6, this.getY() + 1.8, this.getZ() - (double)$$6 * 0.6, $$1, $$2, $$3);
        }

    }

    protected int getSpellCastingTime() {
        return this.spellCastingTickCount;
    }

    protected abstract SoundEvent getCastingSoundEvent();

    static {
        DATA_SPELL_CASTING_ID = SynchedEntityData.defineId(SpellcasterIllager.class, EntityDataSerializers.BYTE);
    }

    protected static enum IllagerSpell {
        NONE(0, 0.0, 0.0, 0.0),
        SUMMON_VEX(1, 0.7, 0.7, 0.8),
        FANGS(2, 0.4, 0.3, 0.35),
        WOLOLO(3, 0.7, 0.5, 0.2),
        DISAPPEAR(4, 0.3, 0.3, 0.8),
        BLINDNESS(5, 0.1, 0.1, 0.2);

        private static final IntFunction<IllagerSpell> BY_ID = ByIdMap.continuous((p_263091_) -> {
            return p_263091_.id;
        }, values(), OutOfBoundsStrategy.ZERO);
        final int id;
        final double[] spellColor;

        private IllagerSpell(int p_33754_, double p_33755_, double p_33756_, double p_33757_) {
            this.id = p_33754_;
            this.spellColor = new double[]{p_33755_, p_33756_, p_33757_};
        }

        public static IllagerSpell byId(int p_33759_) {
            return (IllagerSpell)BY_ID.apply(p_33759_);
        }
    }

    protected abstract class SpellcasterUseSpellGoal extends Goal {
        protected int attackWarmupDelay;
        protected int nextAttackTickCount;

        protected SpellcasterUseSpellGoal() {
        }

        public boolean canUse() {
            LivingEntity $$0 = SpellcasterIllager.this.getTarget();
            if ($$0 != null && $$0.isAlive()) {
                if (SpellcasterIllager.this.isCastingSpell()) {
                    return false;
                } else {
                    return SpellcasterIllager.this.tickCount >= this.nextAttackTickCount;
                }
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            LivingEntity $$0 = SpellcasterIllager.this.getTarget();
            return $$0 != null && $$0.isAlive() && this.attackWarmupDelay > 0;
        }

        public void start() {
            this.attackWarmupDelay = this.adjustedTickDelay(this.getCastWarmupTime());
            SpellcasterIllager.this.spellCastingTickCount = this.getCastingTime();
            this.nextAttackTickCount = SpellcasterIllager.this.tickCount + this.getCastingInterval();
            SoundEvent $$0 = this.getSpellPrepareSound();
            if ($$0 != null) {
                SpellcasterIllager.this.playSound($$0, 1.0F, 1.0F);
            }

            SpellcasterIllager.this.setIsCastingSpell(this.getSpell());
        }

        public void tick() {
            --this.attackWarmupDelay;
            if (this.attackWarmupDelay == 0) {
                this.performSpellCasting();
                SpellcasterIllager.this.playSound(SpellcasterIllager.this.getCastingSoundEvent(), 1.0F, 1.0F);
            }

        }

        protected abstract void performSpellCasting();

        protected int getCastWarmupTime() {
            return 20;
        }

        protected abstract int getCastingTime();

        protected abstract int getCastingInterval();

        @Nullable
        protected abstract SoundEvent getSpellPrepareSound();

        protected abstract IllagerSpell getSpell();
    }

    protected class SpellcasterCastingSpellGoal extends Goal {
        public SpellcasterCastingSpellGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            return SpellcasterIllager.this.getSpellCastingTime() > 0;
        }

        public void start() {
            super.start();
            SpellcasterIllager.this.navigation.stop();
        }

        public void stop() {
            super.stop();
            SpellcasterIllager.this.setIsCastingSpell(net.minecraft.world.entity.monster.SpellcasterIllager.IllagerSpell.NONE);
        }

        public void tick() {
            if (SpellcasterIllager.this.getTarget() != null) {
                SpellcasterIllager.this.getLookControl().setLookAt(SpellcasterIllager.this.getTarget(), (float)SpellcasterIllager.this.getMaxHeadYRot(), (float)SpellcasterIllager.this.getMaxHeadXRot());
            }

        }
    }
}

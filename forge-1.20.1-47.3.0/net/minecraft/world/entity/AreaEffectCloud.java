//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import org.slf4j.Logger;

public class AreaEffectCloud extends Entity implements TraceableEntity {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int TIME_BETWEEN_APPLICATIONS = 5;
    private static final EntityDataAccessor<Float> DATA_RADIUS;
    private static final EntityDataAccessor<Integer> DATA_COLOR;
    private static final EntityDataAccessor<Boolean> DATA_WAITING;
    private static final EntityDataAccessor<ParticleOptions> DATA_PARTICLE;
    private static final float MAX_RADIUS = 32.0F;
    private static final float MINIMAL_RADIUS = 0.5F;
    private static final float DEFAULT_RADIUS = 3.0F;
    public static final float DEFAULT_WIDTH = 6.0F;
    public static final float HEIGHT = 0.5F;
    private Potion potion;
    private final List<MobEffectInstance> effects;
    private final Map<Entity, Integer> victims;
    private int duration;
    private int waitTime;
    private int reapplicationDelay;
    private boolean fixedColor;
    private int durationOnUse;
    private float radiusOnUse;
    private float radiusPerTick;
    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID ownerUUID;

    public AreaEffectCloud(EntityType<? extends AreaEffectCloud> p_19704_, Level p_19705_) {
        super(p_19704_, p_19705_);
        this.potion = Potions.EMPTY;
        this.effects = Lists.newArrayList();
        this.victims = Maps.newHashMap();
        this.duration = 600;
        this.waitTime = 20;
        this.reapplicationDelay = 20;
        this.noPhysics = true;
    }

    public AreaEffectCloud(Level p_19707_, double p_19708_, double p_19709_, double p_19710_) {
        this(EntityType.AREA_EFFECT_CLOUD, p_19707_);
        this.setPos(p_19708_, p_19709_, p_19710_);
    }

    protected void defineSynchedData() {
        this.getEntityData().define(DATA_COLOR, 0);
        this.getEntityData().define(DATA_RADIUS, 3.0F);
        this.getEntityData().define(DATA_WAITING, false);
        this.getEntityData().define(DATA_PARTICLE, ParticleTypes.ENTITY_EFFECT);
    }

    public void setRadius(float p_19713_) {
        if (!this.level().isClientSide) {
            this.getEntityData().set(DATA_RADIUS, Mth.clamp(p_19713_, 0.0F, 32.0F));
        }

    }

    public void refreshDimensions() {
        double $$0 = this.getX();
        double $$1 = this.getY();
        double $$2 = this.getZ();
        super.refreshDimensions();
        this.setPos($$0, $$1, $$2);
    }

    public float getRadius() {
        return (Float)this.getEntityData().get(DATA_RADIUS);
    }

    public void setPotion(Potion p_19723_) {
        this.potion = p_19723_;
        if (!this.fixedColor) {
            this.updateColor();
        }

    }

    private void updateColor() {
        if (this.potion == Potions.EMPTY && this.effects.isEmpty()) {
            this.getEntityData().set(DATA_COLOR, 0);
        } else {
            this.getEntityData().set(DATA_COLOR, PotionUtils.getColor((Collection)PotionUtils.getAllEffects(this.potion, this.effects)));
        }

    }

    public void addEffect(MobEffectInstance p_19717_) {
        this.effects.add(p_19717_);
        if (!this.fixedColor) {
            this.updateColor();
        }

    }

    public int getColor() {
        return (Integer)this.getEntityData().get(DATA_COLOR);
    }

    public void setFixedColor(int p_19715_) {
        this.fixedColor = true;
        this.getEntityData().set(DATA_COLOR, p_19715_);
    }

    public ParticleOptions getParticle() {
        return (ParticleOptions)this.getEntityData().get(DATA_PARTICLE);
    }

    public void setParticle(ParticleOptions p_19725_) {
        this.getEntityData().set(DATA_PARTICLE, p_19725_);
    }

    protected void setWaiting(boolean p_19731_) {
        this.getEntityData().set(DATA_WAITING, p_19731_);
    }

    public boolean isWaiting() {
        return (Boolean)this.getEntityData().get(DATA_WAITING);
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int p_19735_) {
        this.duration = p_19735_;
    }

    public void tick() {
        super.tick();
        boolean $$0 = this.isWaiting();
        float $$1 = this.getRadius();
        if (this.level().isClientSide) {
            if ($$0 && this.random.nextBoolean()) {
                return;
            }

            ParticleOptions $$2 = this.getParticle();
            int $$5;
            float $$6;
            if ($$0) {
                $$5 = 2;
                $$6 = 0.2F;
            } else {
                $$5 = Mth.ceil(3.1415927F * $$1 * $$1);
                $$6 = $$1;
            }

            for(int $$7 = 0; $$7 < $$5; ++$$7) {
                float $$8 = this.random.nextFloat() * 6.2831855F;
                float $$9 = Mth.sqrt(this.random.nextFloat()) * $$6;
                double $$10 = this.getX() + (double)(Mth.cos($$8) * $$9);
                double $$11 = this.getY();
                double $$12 = this.getZ() + (double)(Mth.sin($$8) * $$9);
                double $$17;
                double $$18;
                double $$22;
                if ($$2.getType() != ParticleTypes.ENTITY_EFFECT) {
                    if ($$0) {
                        $$17 = 0.0;
                        $$18 = 0.0;
                        $$22 = 0.0;
                    } else {
                        $$17 = (0.5 - this.random.nextDouble()) * 0.15;
                        $$18 = 0.009999999776482582;
                        $$22 = (0.5 - this.random.nextDouble()) * 0.15;
                    }
                } else {
                    int $$13 = $$0 && this.random.nextBoolean() ? 16777215 : this.getColor();
                    $$17 = (double)((float)($$13 >> 16 & 255) / 255.0F);
                    $$18 = (double)((float)($$13 >> 8 & 255) / 255.0F);
                    $$22 = (double)((float)($$13 & 255) / 255.0F);
                }

                this.level().addAlwaysVisibleParticle($$2, $$10, $$11, $$12, $$17, $$18, $$22);
            }
        } else {
            if (this.tickCount >= this.waitTime + this.duration) {
                this.discard();
                return;
            }

            boolean $$23 = this.tickCount < this.waitTime;
            if ($$0 != $$23) {
                this.setWaiting($$23);
            }

            if ($$23) {
                return;
            }

            if (this.radiusPerTick != 0.0F) {
                $$1 += this.radiusPerTick;
                if ($$1 < 0.5F) {
                    this.discard();
                    return;
                }

                this.setRadius($$1);
            }

            if (this.tickCount % 5 == 0) {
                this.victims.entrySet().removeIf((p_287380_) -> {
                    return this.tickCount >= (Integer)p_287380_.getValue();
                });
                List<MobEffectInstance> $$24 = Lists.newArrayList();
                Iterator var24 = this.potion.getEffects().iterator();

                while(var24.hasNext()) {
                    MobEffectInstance $$25 = (MobEffectInstance)var24.next();
                    $$24.add(new MobEffectInstance($$25.getEffect(), $$25.mapDuration((p_267926_) -> {
                        return p_267926_ / 4;
                    }), $$25.getAmplifier(), $$25.isAmbient(), $$25.isVisible()));
                }

                $$24.addAll(this.effects);
                if ($$24.isEmpty()) {
                    this.victims.clear();
                } else {
                    List<LivingEntity> $$26 = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox());
                    if (!$$26.isEmpty()) {
                        Iterator var27 = $$26.iterator();

                        while(true) {
                            double $$30;
                            LivingEntity $$27;
                            do {
                                do {
                                    do {
                                        if (!var27.hasNext()) {
                                            return;
                                        }

                                        $$27 = (LivingEntity)var27.next();
                                    } while(this.victims.containsKey($$27));
                                } while(!$$27.isAffectedByPotions());

                                double $$28 = $$27.getX() - this.getX();
                                double $$29 = $$27.getZ() - this.getZ();
                                $$30 = $$28 * $$28 + $$29 * $$29;
                            } while(!($$30 <= (double)($$1 * $$1)));

                            this.victims.put($$27, this.tickCount + this.reapplicationDelay);
                            Iterator var14 = $$24.iterator();

                            while(var14.hasNext()) {
                                MobEffectInstance $$31 = (MobEffectInstance)var14.next();
                                if ($$31.getEffect().isInstantenous()) {
                                    $$31.getEffect().applyInstantenousEffect(this, this.getOwner(), $$27, $$31.getAmplifier(), 0.5);
                                } else {
                                    $$27.addEffect(new MobEffectInstance($$31), this);
                                }
                            }

                            if (this.radiusOnUse != 0.0F) {
                                $$1 += this.radiusOnUse;
                                if ($$1 < 0.5F) {
                                    this.discard();
                                    return;
                                }

                                this.setRadius($$1);
                            }

                            if (this.durationOnUse != 0) {
                                this.duration += this.durationOnUse;
                                if (this.duration <= 0) {
                                    this.discard();
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    public float getRadiusOnUse() {
        return this.radiusOnUse;
    }

    public void setRadiusOnUse(float p_19733_) {
        this.radiusOnUse = p_19733_;
    }

    public float getRadiusPerTick() {
        return this.radiusPerTick;
    }

    public void setRadiusPerTick(float p_19739_) {
        this.radiusPerTick = p_19739_;
    }

    public int getDurationOnUse() {
        return this.durationOnUse;
    }

    public void setDurationOnUse(int p_146786_) {
        this.durationOnUse = p_146786_;
    }

    public int getWaitTime() {
        return this.waitTime;
    }

    public void setWaitTime(int p_19741_) {
        this.waitTime = p_19741_;
    }

    public void setOwner(@Nullable LivingEntity p_19719_) {
        this.owner = p_19719_;
        this.ownerUUID = p_19719_ == null ? null : p_19719_.getUUID();
    }

    @Nullable
    public LivingEntity getOwner() {
        if (this.owner == null && this.ownerUUID != null && this.level() instanceof ServerLevel) {
            Entity $$0 = ((ServerLevel)this.level()).getEntity(this.ownerUUID);
            if ($$0 instanceof LivingEntity) {
                this.owner = (LivingEntity)$$0;
            }
        }

        return this.owner;
    }

    protected void readAdditionalSaveData(CompoundTag p_19727_) {
        this.tickCount = p_19727_.getInt("Age");
        this.duration = p_19727_.getInt("Duration");
        this.waitTime = p_19727_.getInt("WaitTime");
        this.reapplicationDelay = p_19727_.getInt("ReapplicationDelay");
        this.durationOnUse = p_19727_.getInt("DurationOnUse");
        this.radiusOnUse = p_19727_.getFloat("RadiusOnUse");
        this.radiusPerTick = p_19727_.getFloat("RadiusPerTick");
        this.setRadius(p_19727_.getFloat("Radius"));
        if (p_19727_.hasUUID("Owner")) {
            this.ownerUUID = p_19727_.getUUID("Owner");
        }

        if (p_19727_.contains("Particle", 8)) {
            try {
                this.setParticle(ParticleArgument.readParticle(new StringReader(p_19727_.getString("Particle")), (HolderLookup)BuiltInRegistries.PARTICLE_TYPE.asLookup()));
            } catch (CommandSyntaxException var5) {
                CommandSyntaxException $$1 = var5;
                LOGGER.warn("Couldn't load custom particle {}", p_19727_.getString("Particle"), $$1);
            }
        }

        if (p_19727_.contains("Color", 99)) {
            this.setFixedColor(p_19727_.getInt("Color"));
        }

        if (p_19727_.contains("Potion", 8)) {
            this.setPotion(PotionUtils.getPotion(p_19727_));
        }

        if (p_19727_.contains("Effects", 9)) {
            ListTag $$2 = p_19727_.getList("Effects", 10);
            this.effects.clear();

            for(int $$3 = 0; $$3 < $$2.size(); ++$$3) {
                MobEffectInstance $$4 = MobEffectInstance.load($$2.getCompound($$3));
                if ($$4 != null) {
                    this.addEffect($$4);
                }
            }
        }

    }

    protected void addAdditionalSaveData(CompoundTag p_19737_) {
        p_19737_.putInt("Age", this.tickCount);
        p_19737_.putInt("Duration", this.duration);
        p_19737_.putInt("WaitTime", this.waitTime);
        p_19737_.putInt("ReapplicationDelay", this.reapplicationDelay);
        p_19737_.putInt("DurationOnUse", this.durationOnUse);
        p_19737_.putFloat("RadiusOnUse", this.radiusOnUse);
        p_19737_.putFloat("RadiusPerTick", this.radiusPerTick);
        p_19737_.putFloat("Radius", this.getRadius());
        p_19737_.putString("Particle", this.getParticle().writeToString());
        if (this.ownerUUID != null) {
            p_19737_.putUUID("Owner", this.ownerUUID);
        }

        if (this.fixedColor) {
            p_19737_.putInt("Color", this.getColor());
        }

        if (this.potion != Potions.EMPTY) {
            p_19737_.putString("Potion", BuiltInRegistries.POTION.getKey(this.potion).toString());
        }

        if (!this.effects.isEmpty()) {
            ListTag $$1 = new ListTag();
            Iterator var3 = this.effects.iterator();

            while(var3.hasNext()) {
                MobEffectInstance $$2 = (MobEffectInstance)var3.next();
                $$1.add($$2.save(new CompoundTag()));
            }

            p_19737_.put("Effects", $$1);
        }

    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_19729_) {
        if (DATA_RADIUS.equals(p_19729_)) {
            this.refreshDimensions();
        }

        super.onSyncedDataUpdated(p_19729_);
    }

    public Potion getPotion() {
        return this.potion;
    }

    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    public EntityDimensions getDimensions(Pose p_19721_) {
        return EntityDimensions.scalable(this.getRadius() * 2.0F, 0.5F);
    }

    static {
        DATA_RADIUS = SynchedEntityData.defineId(AreaEffectCloud.class, EntityDataSerializers.FLOAT);
        DATA_COLOR = SynchedEntityData.defineId(AreaEffectCloud.class, EntityDataSerializers.INT);
        DATA_WAITING = SynchedEntityData.defineId(AreaEffectCloud.class, EntityDataSerializers.BOOLEAN);
        DATA_PARTICLE = SynchedEntityData.defineId(AreaEffectCloud.class, EntityDataSerializers.PARTICLE);
    }
}

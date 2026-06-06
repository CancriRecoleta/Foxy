//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.entity.projectile;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;

public class Arrow extends AbstractArrow {
    private static final int EXPOSED_POTION_DECAY_TIME = 600;
    private static final int NO_EFFECT_COLOR = -1;
    private static final EntityDataAccessor<Integer> ID_EFFECT_COLOR;
    private static final byte EVENT_POTION_PUFF = 0;
    private Potion potion;
    private final Set<MobEffectInstance> effects;
    private boolean fixedColor;

    public Arrow(EntityType<? extends Arrow> p_36858_, Level p_36859_) {
        super(p_36858_, p_36859_);
        this.potion = Potions.EMPTY;
        this.effects = Sets.newHashSet();
    }

    public Arrow(Level p_36861_, double p_36862_, double p_36863_, double p_36864_) {
        super(EntityType.ARROW, p_36862_, p_36863_, p_36864_, p_36861_);
        this.potion = Potions.EMPTY;
        this.effects = Sets.newHashSet();
    }

    public Arrow(Level p_36866_, LivingEntity p_36867_) {
        super(EntityType.ARROW, p_36867_, p_36866_);
        this.potion = Potions.EMPTY;
        this.effects = Sets.newHashSet();
    }

    public void setEffectsFromItem(ItemStack p_36879_) {
        if (p_36879_.is(Items.TIPPED_ARROW)) {
            this.potion = PotionUtils.getPotion(p_36879_);
            Collection<MobEffectInstance> $$1 = PotionUtils.getCustomEffects(p_36879_);
            if (!$$1.isEmpty()) {
                Iterator var3 = $$1.iterator();

                while(var3.hasNext()) {
                    MobEffectInstance $$2 = (MobEffectInstance)var3.next();
                    this.effects.add(new MobEffectInstance($$2));
                }
            }

            int $$3 = getCustomColor(p_36879_);
            if ($$3 == -1) {
                this.updateColor();
            } else {
                this.setFixedColor($$3);
            }
        } else if (p_36879_.is(Items.ARROW)) {
            this.potion = Potions.EMPTY;
            this.effects.clear();
            this.entityData.set(ID_EFFECT_COLOR, -1);
        }

    }

    public static int getCustomColor(ItemStack p_36885_) {
        CompoundTag $$1 = p_36885_.getTag();
        return $$1 != null && $$1.contains("CustomPotionColor", 99) ? $$1.getInt("CustomPotionColor") : -1;
    }

    private void updateColor() {
        this.fixedColor = false;
        if (this.potion == Potions.EMPTY && this.effects.isEmpty()) {
            this.entityData.set(ID_EFFECT_COLOR, -1);
        } else {
            this.entityData.set(ID_EFFECT_COLOR, PotionUtils.getColor((Collection)PotionUtils.getAllEffects(this.potion, this.effects)));
        }

    }

    public void addEffect(MobEffectInstance p_36871_) {
        this.effects.add(p_36871_);
        this.getEntityData().set(ID_EFFECT_COLOR, PotionUtils.getColor((Collection)PotionUtils.getAllEffects(this.potion, this.effects)));
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ID_EFFECT_COLOR, -1);
    }

    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            if (this.inGround) {
                if (this.inGroundTime % 5 == 0) {
                    this.makeParticle(1);
                }
            } else {
                this.makeParticle(2);
            }
        } else if (this.inGround && this.inGroundTime != 0 && !this.effects.isEmpty() && this.inGroundTime >= 600) {
            this.level().broadcastEntityEvent(this, (byte)0);
            this.potion = Potions.EMPTY;
            this.effects.clear();
            this.entityData.set(ID_EFFECT_COLOR, -1);
        }

    }

    private void makeParticle(int p_36877_) {
        int $$1 = this.getColor();
        if ($$1 != -1 && p_36877_ > 0) {
            double $$2 = (double)($$1 >> 16 & 255) / 255.0;
            double $$3 = (double)($$1 >> 8 & 255) / 255.0;
            double $$4 = (double)($$1 >> 0 & 255) / 255.0;

            for(int $$5 = 0; $$5 < p_36877_; ++$$5) {
                this.level().addParticle(ParticleTypes.ENTITY_EFFECT, this.getRandomX(0.5), this.getRandomY(), this.getRandomZ(0.5), $$2, $$3, $$4);
            }

        }
    }

    public int getColor() {
        return (Integer)this.entityData.get(ID_EFFECT_COLOR);
    }

    private void setFixedColor(int p_36883_) {
        this.fixedColor = true;
        this.entityData.set(ID_EFFECT_COLOR, p_36883_);
    }

    public void addAdditionalSaveData(CompoundTag p_36881_) {
        super.addAdditionalSaveData(p_36881_);
        if (this.potion != Potions.EMPTY) {
            p_36881_.putString("Potion", BuiltInRegistries.POTION.getKey(this.potion).toString());
        }

        if (this.fixedColor) {
            p_36881_.putInt("Color", this.getColor());
        }

        if (!this.effects.isEmpty()) {
            ListTag $$1 = new ListTag();
            Iterator var3 = this.effects.iterator();

            while(var3.hasNext()) {
                MobEffectInstance $$2 = (MobEffectInstance)var3.next();
                $$1.add($$2.save(new CompoundTag()));
            }

            p_36881_.put("CustomPotionEffects", $$1);
        }

    }

    public void readAdditionalSaveData(CompoundTag p_36875_) {
        super.readAdditionalSaveData(p_36875_);
        if (p_36875_.contains("Potion", 8)) {
            this.potion = PotionUtils.getPotion(p_36875_);
        }

        Iterator var2 = PotionUtils.getCustomEffects(p_36875_).iterator();

        while(var2.hasNext()) {
            MobEffectInstance $$1 = (MobEffectInstance)var2.next();
            this.addEffect($$1);
        }

        if (p_36875_.contains("Color", 99)) {
            this.setFixedColor(p_36875_.getInt("Color"));
        } else {
            this.updateColor();
        }

    }

    protected void doPostHurtEffects(LivingEntity p_36873_) {
        super.doPostHurtEffects(p_36873_);
        Entity $$1 = this.getEffectSource();
        Iterator var3 = this.potion.getEffects().iterator();

        MobEffectInstance $$3;
        while(var3.hasNext()) {
            $$3 = (MobEffectInstance)var3.next();
            p_36873_.addEffect(new MobEffectInstance($$3.getEffect(), Math.max($$3.mapDuration((p_268168_) -> {
                return p_268168_ / 8;
            }), 1), $$3.getAmplifier(), $$3.isAmbient(), $$3.isVisible()), $$1);
        }

        if (!this.effects.isEmpty()) {
            var3 = this.effects.iterator();

            while(var3.hasNext()) {
                $$3 = (MobEffectInstance)var3.next();
                p_36873_.addEffect($$3, $$1);
            }
        }

    }

    protected ItemStack getPickupItem() {
        if (this.effects.isEmpty() && this.potion == Potions.EMPTY) {
            return new ItemStack(Items.ARROW);
        } else {
            ItemStack $$0 = new ItemStack(Items.TIPPED_ARROW);
            PotionUtils.setPotion($$0, this.potion);
            PotionUtils.setCustomEffects($$0, this.effects);
            if (this.fixedColor) {
                $$0.getOrCreateTag().putInt("CustomPotionColor", this.getColor());
            }

            return $$0;
        }
    }

    public void handleEntityEvent(byte p_36869_) {
        if (p_36869_ == 0) {
            int $$1 = this.getColor();
            if ($$1 != -1) {
                double $$2 = (double)($$1 >> 16 & 255) / 255.0;
                double $$3 = (double)($$1 >> 8 & 255) / 255.0;
                double $$4 = (double)($$1 >> 0 & 255) / 255.0;

                for(int $$5 = 0; $$5 < 20; ++$$5) {
                    this.level().addParticle(ParticleTypes.ENTITY_EFFECT, this.getRandomX(0.5), this.getRandomY(), this.getRandomZ(0.5), $$2, $$3, $$4);
                }
            }
        } else {
            super.handleEntityEvent(p_36869_);
        }

    }

    static {
        ID_EFFECT_COLOR = SynchedEntityData.defineId(Arrow.class, EntityDataSerializers.INT);
    }
}

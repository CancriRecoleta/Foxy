//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.entity.projectile;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;

public class ThrownPotion extends ThrowableItemProjectile implements ItemSupplier {
    public static final double SPLASH_RANGE = 4.0;
    private static final double SPLASH_RANGE_SQ = 16.0;
    public static final Predicate<LivingEntity> WATER_SENSITIVE_OR_ON_FIRE = (p_287524_) -> {
        return p_287524_.isSensitiveToWater() || p_287524_.isOnFire();
    };

    public ThrownPotion(EntityType<? extends ThrownPotion> p_37527_, Level p_37528_) {
        super(p_37527_, p_37528_);
    }

    public ThrownPotion(Level p_37535_, LivingEntity p_37536_) {
        super(EntityType.POTION, p_37536_, p_37535_);
    }

    public ThrownPotion(Level p_37530_, double p_37531_, double p_37532_, double p_37533_) {
        super(EntityType.POTION, p_37531_, p_37532_, p_37533_, p_37530_);
    }

    protected Item getDefaultItem() {
        return Items.SPLASH_POTION;
    }

    protected float getGravity() {
        return 0.05F;
    }

    protected void onHitBlock(BlockHitResult p_37541_) {
        super.onHitBlock(p_37541_);
        if (!this.level().isClientSide) {
            ItemStack $$1 = this.getItem();
            Potion $$2 = PotionUtils.getPotion($$1);
            List<MobEffectInstance> $$3 = PotionUtils.getMobEffects($$1);
            boolean $$4 = $$2 == Potions.WATER && $$3.isEmpty();
            Direction $$5 = p_37541_.getDirection();
            BlockPos $$6 = p_37541_.getBlockPos();
            BlockPos $$7 = $$6.relative($$5);
            if ($$4) {
                this.dowseFire($$7);
                this.dowseFire($$7.relative($$5.getOpposite()));
                Iterator var9 = Plane.HORIZONTAL.iterator();

                while(var9.hasNext()) {
                    Direction $$8 = (Direction)var9.next();
                    this.dowseFire($$7.relative($$8));
                }
            }

        }
    }

    protected void onHit(HitResult p_37543_) {
        super.onHit(p_37543_);
        if (!this.level().isClientSide) {
            ItemStack $$1 = this.getItem();
            Potion $$2 = PotionUtils.getPotion($$1);
            List<MobEffectInstance> $$3 = PotionUtils.getMobEffects($$1);
            boolean $$4 = $$2 == Potions.WATER && $$3.isEmpty();
            if ($$4) {
                this.applyWater();
            } else if (!$$3.isEmpty()) {
                if (this.isLingering()) {
                    this.makeAreaOfEffectCloud($$1, $$2);
                } else {
                    this.applySplash($$3, p_37543_.getType() == Type.ENTITY ? ((EntityHitResult)p_37543_).getEntity() : null);
                }
            }

            int $$5 = $$2.hasInstantEffects() ? 2007 : 2002;
            this.level().levelEvent($$5, this.blockPosition(), PotionUtils.getColor($$1));
            this.discard();
        }
    }

    private void applyWater() {
        AABB $$0 = this.getBoundingBox().inflate(4.0, 2.0, 4.0);
        List<LivingEntity> $$1 = this.level().getEntitiesOfClass(LivingEntity.class, $$0, WATER_SENSITIVE_OR_ON_FIRE);
        Iterator var3 = $$1.iterator();

        while(var3.hasNext()) {
            LivingEntity $$2 = (LivingEntity)var3.next();
            double $$3 = this.distanceToSqr($$2);
            if ($$3 < 16.0) {
                if ($$2.isSensitiveToWater()) {
                    $$2.hurt(this.damageSources().indirectMagic(this, this.getOwner()), 1.0F);
                }

                if ($$2.isOnFire() && $$2.isAlive()) {
                    $$2.extinguishFire();
                }
            }
        }

        List<Axolotl> $$4 = this.level().getEntitiesOfClass(Axolotl.class, $$0);
        Iterator var8 = $$4.iterator();

        while(var8.hasNext()) {
            Axolotl $$5 = (Axolotl)var8.next();
            $$5.rehydrate();
        }

    }

    private void applySplash(List<MobEffectInstance> p_37548_, @Nullable Entity p_37549_) {
        AABB $$2 = this.getBoundingBox().inflate(4.0, 2.0, 4.0);
        List<LivingEntity> $$3 = this.level().getEntitiesOfClass(LivingEntity.class, $$2);
        if (!$$3.isEmpty()) {
            Entity $$4 = this.getEffectSource();
            Iterator var6 = $$3.iterator();

            while(true) {
                LivingEntity $$5;
                double $$6;
                do {
                    do {
                        if (!var6.hasNext()) {
                            return;
                        }

                        $$5 = (LivingEntity)var6.next();
                    } while(!$$5.isAffectedByPotions());

                    $$6 = this.distanceToSqr($$5);
                } while(!($$6 < 16.0));

                double $$8;
                if ($$5 == p_37549_) {
                    $$8 = 1.0;
                } else {
                    $$8 = 1.0 - Math.sqrt($$6) / 4.0;
                }

                Iterator var12 = p_37548_.iterator();

                while(var12.hasNext()) {
                    MobEffectInstance $$9 = (MobEffectInstance)var12.next();
                    MobEffect $$10 = $$9.getEffect();
                    if ($$10.isInstantenous()) {
                        $$10.applyInstantenousEffect(this, this.getOwner(), $$5, $$9.getAmplifier(), $$8);
                    } else {
                        int $$11 = $$9.mapDuration((p_267930_) -> {
                            return (int)($$8 * (double)p_267930_ + 0.5);
                        });
                        MobEffectInstance $$12 = new MobEffectInstance($$10, $$11, $$9.getAmplifier(), $$9.isAmbient(), $$9.isVisible());
                        if (!$$12.endsWithin(20)) {
                            $$5.addEffect($$12, $$4);
                        }
                    }
                }
            }
        }
    }

    private void makeAreaOfEffectCloud(ItemStack p_37538_, Potion p_37539_) {
        AreaEffectCloud $$2 = new AreaEffectCloud(this.level(), this.getX(), this.getY(), this.getZ());
        Entity $$3 = this.getOwner();
        if ($$3 instanceof LivingEntity) {
            $$2.setOwner((LivingEntity)$$3);
        }

        $$2.setRadius(3.0F);
        $$2.setRadiusOnUse(-0.5F);
        $$2.setWaitTime(10);
        $$2.setRadiusPerTick(-$$2.getRadius() / (float)$$2.getDuration());
        $$2.setPotion(p_37539_);
        Iterator var5 = PotionUtils.getCustomEffects(p_37538_).iterator();

        while(var5.hasNext()) {
            MobEffectInstance $$4 = (MobEffectInstance)var5.next();
            $$2.addEffect(new MobEffectInstance($$4));
        }

        CompoundTag $$5 = p_37538_.getTag();
        if ($$5 != null && $$5.contains("CustomPotionColor", 99)) {
            $$2.setFixedColor($$5.getInt("CustomPotionColor"));
        }

        this.level().addFreshEntity($$2);
    }

    private boolean isLingering() {
        return this.getItem().is(Items.LINGERING_POTION);
    }

    private void dowseFire(BlockPos p_150193_) {
        BlockState $$1 = this.level().getBlockState(p_150193_);
        if ($$1.is(BlockTags.FIRE)) {
            this.level().removeBlock(p_150193_, false);
        } else if (AbstractCandleBlock.isLit($$1)) {
            AbstractCandleBlock.extinguish((Player)null, $$1, this.level(), p_150193_);
        } else if (CampfireBlock.isLitCampfire($$1)) {
            this.level().levelEvent((Player)null, 1009, p_150193_, 0);
            CampfireBlock.dowse(this.getOwner(), this.level(), p_150193_, $$1);
            this.level().setBlockAndUpdate(p_150193_, (BlockState)$$1.setValue(CampfireBlock.LIT, false));
        }

    }
}

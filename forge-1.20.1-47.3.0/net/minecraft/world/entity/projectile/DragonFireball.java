//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.entity.projectile;

import java.util.Iterator;
import java.util.List;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;

public class DragonFireball extends AbstractHurtingProjectile {
    public static final float SPLASH_RANGE = 4.0F;

    public DragonFireball(EntityType<? extends DragonFireball> p_36892_, Level p_36893_) {
        super(p_36892_, p_36893_);
    }

    public DragonFireball(Level p_36903_, LivingEntity p_36904_, double p_36905_, double p_36906_, double p_36907_) {
        super(EntityType.DRAGON_FIREBALL, p_36904_, p_36905_, p_36906_, p_36907_, p_36903_);
    }

    protected void onHit(HitResult p_36913_) {
        super.onHit(p_36913_);
        if (p_36913_.getType() != Type.ENTITY || !this.ownedBy(((EntityHitResult)p_36913_).getEntity())) {
            if (!this.level().isClientSide) {
                List<LivingEntity> $$1 = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(4.0, 2.0, 4.0));
                AreaEffectCloud $$2 = new AreaEffectCloud(this.level(), this.getX(), this.getY(), this.getZ());
                Entity $$3 = this.getOwner();
                if ($$3 instanceof LivingEntity) {
                    $$2.setOwner((LivingEntity)$$3);
                }

                $$2.setParticle(ParticleTypes.DRAGON_BREATH);
                $$2.setRadius(3.0F);
                $$2.setDuration(600);
                $$2.setRadiusPerTick((7.0F - $$2.getRadius()) / (float)$$2.getDuration());
                $$2.addEffect(new MobEffectInstance(MobEffects.HARM, 1, 1));
                if (!$$1.isEmpty()) {
                    Iterator var5 = $$1.iterator();

                    while(var5.hasNext()) {
                        LivingEntity $$4 = (LivingEntity)var5.next();
                        double $$5 = this.distanceToSqr($$4);
                        if ($$5 < 16.0) {
                            $$2.setPos($$4.getX(), $$4.getY(), $$4.getZ());
                            break;
                        }
                    }
                }

                this.level().levelEvent(2006, this.blockPosition(), this.isSilent() ? -1 : 1);
                this.level().addFreshEntity($$2);
                this.discard();
            }

        }
    }

    public boolean isPickable() {
        return false;
    }

    public boolean hurt(DamageSource p_36910_, float p_36911_) {
        return false;
    }

    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.DRAGON_BREATH;
    }

    protected boolean shouldBurn() {
        return false;
    }
}

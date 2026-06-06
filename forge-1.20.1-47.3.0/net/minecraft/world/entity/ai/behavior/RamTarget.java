//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class RamTarget extends Behavior<Goat> {
    public static final int TIME_OUT_DURATION = 200;
    public static final float RAM_SPEED_FORCE_FACTOR = 1.65F;
    private final Function<Goat, UniformInt> getTimeBetweenRams;
    private final TargetingConditions ramTargeting;
    private final float speed;
    private final ToDoubleFunction<Goat> getKnockbackForce;
    private Vec3 ramDirection;
    private final Function<Goat, SoundEvent> getImpactSound;
    private final Function<Goat, SoundEvent> getHornBreakSound;

    public RamTarget(Function<Goat, UniformInt> p_217342_, TargetingConditions p_217343_, float p_217344_, ToDoubleFunction<Goat> p_217345_, Function<Goat, SoundEvent> p_217346_, Function<Goat, SoundEvent> p_217347_) {
        super(ImmutableMap.of(MemoryModuleType.RAM_COOLDOWN_TICKS, MemoryStatus.VALUE_ABSENT, MemoryModuleType.RAM_TARGET, MemoryStatus.VALUE_PRESENT), 200);
        this.getTimeBetweenRams = p_217342_;
        this.ramTargeting = p_217343_;
        this.speed = p_217344_;
        this.getKnockbackForce = p_217345_;
        this.getImpactSound = p_217346_;
        this.getHornBreakSound = p_217347_;
        this.ramDirection = Vec3.ZERO;
    }

    protected boolean checkExtraStartConditions(ServerLevel p_217349_, Goat p_217350_) {
        return p_217350_.getBrain().hasMemoryValue(MemoryModuleType.RAM_TARGET);
    }

    protected boolean canStillUse(ServerLevel p_217352_, Goat p_217353_, long p_217354_) {
        return p_217353_.getBrain().hasMemoryValue(MemoryModuleType.RAM_TARGET);
    }

    protected void start(ServerLevel p_217359_, Goat p_217360_, long p_217361_) {
        BlockPos $$3 = p_217360_.blockPosition();
        Brain<?> $$4 = p_217360_.getBrain();
        Vec3 $$5 = (Vec3)$$4.getMemory(MemoryModuleType.RAM_TARGET).get();
        this.ramDirection = (new Vec3((double)$$3.getX() - $$5.x(), 0.0, (double)$$3.getZ() - $$5.z())).normalize();
        $$4.setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget($$5, this.speed, 0)));
    }

    protected void tick(ServerLevel p_217366_, Goat p_217367_, long p_217368_) {
        List<LivingEntity> $$3 = p_217366_.getNearbyEntities(LivingEntity.class, this.ramTargeting, p_217367_, p_217367_.getBoundingBox());
        Brain<?> $$4 = p_217367_.getBrain();
        if (!$$3.isEmpty()) {
            LivingEntity $$5 = (LivingEntity)$$3.get(0);
            $$5.hurt(p_217366_.damageSources().noAggroMobAttack(p_217367_), (float)p_217367_.getAttributeValue(Attributes.ATTACK_DAMAGE));
            int $$6 = p_217367_.hasEffect(MobEffects.MOVEMENT_SPEED) ? p_217367_.getEffect(MobEffects.MOVEMENT_SPEED).getAmplifier() + 1 : 0;
            int $$7 = p_217367_.hasEffect(MobEffects.MOVEMENT_SLOWDOWN) ? p_217367_.getEffect(MobEffects.MOVEMENT_SLOWDOWN).getAmplifier() + 1 : 0;
            float $$8 = 0.25F * (float)($$6 - $$7);
            float $$9 = Mth.clamp(p_217367_.getSpeed() * 1.65F, 0.2F, 3.0F) + $$8;
            float $$10 = $$5.isDamageSourceBlocked(p_217366_.damageSources().mobAttack(p_217367_)) ? 0.5F : 1.0F;
            $$5.knockback((double)($$10 * $$9) * this.getKnockbackForce.applyAsDouble(p_217367_), this.ramDirection.x(), this.ramDirection.z());
            this.finishRam(p_217366_, p_217367_);
            p_217366_.playSound((Player)null, p_217367_, (SoundEvent)this.getImpactSound.apply(p_217367_), SoundSource.NEUTRAL, 1.0F, 1.0F);
        } else if (this.hasRammedHornBreakingBlock(p_217366_, p_217367_)) {
            p_217366_.playSound((Player)null, p_217367_, (SoundEvent)this.getImpactSound.apply(p_217367_), SoundSource.NEUTRAL, 1.0F, 1.0F);
            boolean $$11 = p_217367_.dropHorn();
            if ($$11) {
                p_217366_.playSound((Player)null, p_217367_, (SoundEvent)this.getHornBreakSound.apply(p_217367_), SoundSource.NEUTRAL, 1.0F, 1.0F);
            }

            this.finishRam(p_217366_, p_217367_);
        } else {
            Optional<WalkTarget> $$12 = $$4.getMemory(MemoryModuleType.WALK_TARGET);
            Optional<Vec3> $$13 = $$4.getMemory(MemoryModuleType.RAM_TARGET);
            boolean $$14 = $$12.isEmpty() || $$13.isEmpty() || ((WalkTarget)$$12.get()).getTarget().currentPosition().closerThan((Position)$$13.get(), 0.25);
            if ($$14) {
                this.finishRam(p_217366_, p_217367_);
            }
        }

    }

    private boolean hasRammedHornBreakingBlock(ServerLevel p_217363_, Goat p_217364_) {
        Vec3 $$2 = p_217364_.getDeltaMovement().multiply(1.0, 0.0, 1.0).normalize();
        BlockPos $$3 = BlockPos.containing(p_217364_.position().add($$2));
        return p_217363_.getBlockState($$3).is(BlockTags.SNAPS_GOAT_HORN) || p_217363_.getBlockState($$3.above()).is(BlockTags.SNAPS_GOAT_HORN);
    }

    protected void finishRam(ServerLevel p_217356_, Goat p_217357_) {
        p_217356_.broadcastEntityEvent(p_217357_, (byte)59);
        p_217357_.getBrain().setMemory(MemoryModuleType.RAM_COOLDOWN_TICKS, (Object)((UniformInt)this.getTimeBetweenRams.apply(p_217357_)).sample(p_217356_.random));
        p_217357_.getBrain().eraseMemory(MemoryModuleType.RAM_TARGET);
    }
}

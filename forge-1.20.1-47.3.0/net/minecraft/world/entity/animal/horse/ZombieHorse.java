//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.entity.animal.horse;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ZombieHorse extends AbstractHorse {
    public ZombieHorse(EntityType<? extends ZombieHorse> p_30994_, Level p_30995_) {
        super(p_30994_, p_30995_);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createBaseHorseAttributes().add(Attributes.MAX_HEALTH, 15.0).add(Attributes.MOVEMENT_SPEED, 0.20000000298023224);
    }

    protected void randomizeAttributes(RandomSource p_218823_) {
        AttributeInstance var10000 = this.getAttribute(Attributes.JUMP_STRENGTH);
        Objects.requireNonNull(p_218823_);
        var10000.setBaseValue(generateJumpStrength(p_218823_::nextDouble));
    }

    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.ZOMBIE_HORSE_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ZOMBIE_HORSE_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource p_31006_) {
        return SoundEvents.ZOMBIE_HORSE_HURT;
    }

    @Nullable
    public AgeableMob getBreedOffspring(ServerLevel p_149561_, AgeableMob p_149562_) {
        return (AgeableMob)EntityType.ZOMBIE_HORSE.create(p_149561_);
    }

    public InteractionResult mobInteract(Player p_31001_, InteractionHand p_31002_) {
        return !this.isTamed() ? InteractionResult.PASS : super.mobInteract(p_31001_, p_31002_);
    }

    protected void addBehaviourGoals() {
    }
}

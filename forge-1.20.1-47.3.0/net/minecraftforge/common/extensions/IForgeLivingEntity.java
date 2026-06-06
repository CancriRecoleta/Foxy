//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common.extensions;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fluids.FluidType;

public interface IForgeLivingEntity extends IForgeEntity {
    default LivingEntity self() {
        return (LivingEntity)this;
    }

    default boolean canSwimInFluidType(FluidType type) {
        if (type == ForgeMod.WATER_TYPE.get()) {
            return !this.self().isSensitiveToWater();
        } else {
            return IForgeEntity.super.canSwimInFluidType(type);
        }
    }

    default void jumpInFluid(FluidType type) {
        this.self().setDeltaMovement(this.self().getDeltaMovement().add(0.0, 0.03999999910593033 * this.self().getAttributeValue((Attribute)ForgeMod.SWIM_SPEED.get()), 0.0));
    }

    default void sinkInFluid(FluidType type) {
        this.self().setDeltaMovement(this.self().getDeltaMovement().add(0.0, -0.03999999910593033 * this.self().getAttributeValue((Attribute)ForgeMod.SWIM_SPEED.get()), 0.0));
    }

    default boolean canDrownInFluidType(FluidType type) {
        if (type == ForgeMod.WATER_TYPE.get()) {
            return !this.self().canBreatheUnderwater();
        } else {
            return type.canDrownIn(this.self());
        }
    }

    default boolean moveInFluid(FluidState state, Vec3 movementVector, double gravity) {
        return state.move(this.self(), movementVector, gravity);
    }
}

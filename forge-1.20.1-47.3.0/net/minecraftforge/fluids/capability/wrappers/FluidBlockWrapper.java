//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.fluids.capability.wrappers;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public class FluidBlockWrapper implements IFluidHandler {
    protected final IFluidBlock fluidBlock;
    protected final Level world;
    protected final BlockPos blockPos;

    public FluidBlockWrapper(IFluidBlock fluidBlock, Level world, BlockPos blockPos) {
        this.fluidBlock = fluidBlock;
        this.world = world;
        this.blockPos = blockPos;
    }

    public int getTanks() {
        return 1;
    }

    public @NotNull FluidStack getFluidInTank(int tank) {
        return tank == 0 ? this.fluidBlock.drain(this.world, this.blockPos, net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE) : FluidStack.EMPTY;
    }

    public int getTankCapacity(int tank) {
        FluidStack stored = this.getFluidInTank(tank);
        if (!stored.isEmpty()) {
            float filledPercentage = this.fluidBlock.getFilledPercentage(this.world, this.blockPos);
            if (filledPercentage > 0.0F) {
                return (int)((float)stored.getAmount() / filledPercentage);
            }
        }

        return 1000;
    }

    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return stack.getFluid() == this.fluidBlock.getFluid();
    }

    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        return this.fluidBlock.place(this.world, this.blockPos, resource, action);
    }

    public @NotNull FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
        if (!resource.isEmpty() && this.fluidBlock.canDrain(this.world, this.blockPos) && resource.getFluid() == this.fluidBlock.getFluid()) {
            FluidStack simulatedDrained = this.fluidBlock.drain(this.world, this.blockPos, net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE);
            if (simulatedDrained.getAmount() <= resource.getAmount() && resource.isFluidEqual(simulatedDrained)) {
                if (action.execute()) {
                    return this.fluidBlock.drain(this.world, this.blockPos, net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE).copy();
                }

                return simulatedDrained.copy();
            }
        }

        return FluidStack.EMPTY;
    }

    public @NotNull FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
        if (maxDrain > 0 && this.fluidBlock.canDrain(this.world, this.blockPos)) {
            FluidStack simulatedDrained = this.fluidBlock.drain(this.world, this.blockPos, net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE);
            if (simulatedDrained.getAmount() <= maxDrain) {
                if (action.execute()) {
                    return this.fluidBlock.drain(this.world, this.blockPos, net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE).copy();
                }

                return simulatedDrained.copy();
            }
        }

        return FluidStack.EMPTY;
    }
}

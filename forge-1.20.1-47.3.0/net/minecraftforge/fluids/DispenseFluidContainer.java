//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.fluids;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import org.jetbrains.annotations.NotNull;

public class DispenseFluidContainer extends DefaultDispenseItemBehavior {
    private static final DispenseFluidContainer INSTANCE = new DispenseFluidContainer();
    private final DefaultDispenseItemBehavior dispenseBehavior = new DefaultDispenseItemBehavior();

    public static DispenseFluidContainer getInstance() {
        return INSTANCE;
    }

    private DispenseFluidContainer() {
    }

    public @NotNull ItemStack execute(@NotNull BlockSource source, @NotNull ItemStack stack) {
        return FluidUtil.getFluidContained(stack).isPresent() ? this.dumpContainer(source, stack) : this.fillContainer(source, stack);
    }

    private @NotNull ItemStack fillContainer(@NotNull BlockSource source, @NotNull ItemStack stack) {
        Level level = source.getLevel();
        Direction dispenserFacing = (Direction)source.getBlockState().getValue(DispenserBlock.FACING);
        BlockPos blockpos = source.getPos().relative(dispenserFacing);
        FluidActionResult actionResult = FluidUtil.tryPickUpFluid(stack, (Player)null, level, blockpos, dispenserFacing.getOpposite());
        ItemStack resultStack = actionResult.getResult();
        if (actionResult.isSuccess() && !resultStack.isEmpty()) {
            if (stack.getCount() == 1) {
                return resultStack;
            } else {
                if (((DispenserBlockEntity)source.getEntity()).addItem(resultStack) < 0) {
                    this.dispenseBehavior.dispense(source, resultStack);
                }

                ItemStack stackCopy = stack.copy();
                stackCopy.shrink(1);
                return stackCopy;
            }
        } else {
            return super.execute(source, stack);
        }
    }

    private @NotNull ItemStack dumpContainer(BlockSource source, @NotNull ItemStack stack) {
        ItemStack singleStack = stack.copy();
        singleStack.setCount(1);
        IFluidHandlerItem fluidHandler = (IFluidHandlerItem)FluidUtil.getFluidHandler(singleStack).orElse((Object)null);
        if (fluidHandler == null) {
            return super.execute(source, stack);
        } else {
            FluidStack fluidStack = fluidHandler.drain(1000, FluidAction.EXECUTE);
            Direction dispenserFacing = (Direction)source.getBlockState().getValue(DispenserBlock.FACING);
            BlockPos blockpos = source.getPos().relative(dispenserFacing);
            FluidActionResult result = FluidUtil.tryPlaceFluid((Player)null, source.getLevel(), InteractionHand.MAIN_HAND, blockpos, (ItemStack)stack, fluidStack);
            if (result.isSuccess()) {
                ItemStack drainedStack = result.getResult();
                if (drainedStack.getCount() == 1) {
                    return drainedStack;
                } else {
                    if (!drainedStack.isEmpty() && ((DispenserBlockEntity)source.getEntity()).addItem(drainedStack) < 0) {
                        this.dispenseBehavior.dispense(source, drainedStack);
                    }

                    ItemStack stackCopy = drainedStack.copy();
                    stackCopy.shrink(1);
                    return stackCopy;
                }
            } else {
                return this.dispenseBehavior.dispense(source, stack);
            }
        }
    }
}

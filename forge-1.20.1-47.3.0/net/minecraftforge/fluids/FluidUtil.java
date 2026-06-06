//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.fluids;

import com.google.common.base.Preconditions;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.wrappers.BlockWrapper;
import net.minecraftforge.fluids.capability.wrappers.BucketPickupHandlerWrapper;
import net.minecraftforge.fluids.capability.wrappers.FluidBlockWrapper;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluidUtil {
    private FluidUtil() {
    }

    public static boolean interactWithFluidHandler(@NotNull Player player, @NotNull InteractionHand hand, @NotNull Level level, @NotNull BlockPos pos, @Nullable Direction side) {
        Preconditions.checkNotNull(level);
        Preconditions.checkNotNull(pos);
        return (Boolean)getFluidHandler(level, pos, side).map((handler) -> {
            return interactWithFluidHandler(player, hand, handler);
        }).orElse(false);
    }

    public static boolean interactWithFluidHandler(@NotNull Player player, @NotNull InteractionHand hand, @NotNull IFluidHandler handler) {
        Preconditions.checkNotNull(player);
        Preconditions.checkNotNull(hand);
        Preconditions.checkNotNull(handler);
        ItemStack heldItem = player.getItemInHand(hand);
        return !heldItem.isEmpty() ? (Boolean)player.getCapability(ForgeCapabilities.ITEM_HANDLER).map((playerInventory) -> {
            FluidActionResult fluidActionResult = tryFillContainerAndStow(heldItem, handler, playerInventory, Integer.MAX_VALUE, player, true);
            if (!fluidActionResult.isSuccess()) {
                fluidActionResult = tryEmptyContainerAndStow(heldItem, handler, playerInventory, Integer.MAX_VALUE, player, true);
            }

            if (fluidActionResult.isSuccess()) {
                player.setItemInHand(hand, fluidActionResult.getResult());
                return true;
            } else {
                return false;
            }
        }).orElse(false) : false;
    }

    public static @NotNull FluidActionResult tryFillContainer(@NotNull ItemStack container, IFluidHandler fluidSource, int maxAmount, @Nullable Player player, boolean doFill) {
        ItemStack containerCopy = ItemHandlerHelper.copyStackWithSize(container, 1);
        return (FluidActionResult)getFluidHandler(containerCopy).map((containerFluidHandler) -> {
            FluidStack simulatedTransfer = tryFluidTransfer(containerFluidHandler, fluidSource, maxAmount, false);
            if (!simulatedTransfer.isEmpty()) {
                if (doFill) {
                    tryFluidTransfer(containerFluidHandler, fluidSource, maxAmount, true);
                    if (player != null) {
                        SoundEvent soundevent = simulatedTransfer.getFluid().getFluidType().getSound(simulatedTransfer, SoundActions.BUCKET_FILL);
                        if (soundevent != null) {
                            player.level().playSound((Player)null, player.getX(), player.getY() + 0.5, player.getZ(), soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
                        }
                    }
                } else {
                    containerFluidHandler.fill(simulatedTransfer, FluidAction.EXECUTE);
                }

                ItemStack resultContainer = containerFluidHandler.getContainer();
                return new FluidActionResult(resultContainer);
            } else {
                return FluidActionResult.FAILURE;
            }
        }).orElse(FluidActionResult.FAILURE);
    }

    public static @NotNull FluidActionResult tryEmptyContainer(@NotNull ItemStack container, IFluidHandler fluidDestination, int maxAmount, @Nullable Player player, boolean doDrain) {
        ItemStack containerCopy = ItemHandlerHelper.copyStackWithSize(container, 1);
        return (FluidActionResult)getFluidHandler(containerCopy).map((containerFluidHandler) -> {
            FluidStack transfer = tryFluidTransfer(fluidDestination, containerFluidHandler, maxAmount, doDrain);
            if (transfer.isEmpty()) {
                return FluidActionResult.FAILURE;
            } else {
                if (!doDrain) {
                    containerFluidHandler.drain(transfer, FluidAction.EXECUTE);
                }

                if (doDrain && player != null) {
                    SoundEvent soundevent = transfer.getFluid().getFluidType().getSound(transfer, SoundActions.BUCKET_EMPTY);
                    if (soundevent != null) {
                        player.level().playSound((Player)null, player.getX(), player.getY() + 0.5, player.getZ(), soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
                    }
                }

                ItemStack resultContainer = containerFluidHandler.getContainer();
                return new FluidActionResult(resultContainer);
            }
        }).orElse(FluidActionResult.FAILURE);
    }

    public static @NotNull FluidActionResult tryFillContainerAndStow(@NotNull ItemStack container, IFluidHandler fluidSource, IItemHandler inventory, int maxAmount, @Nullable Player player, boolean doFill) {
        if (container.isEmpty()) {
            return FluidActionResult.FAILURE;
        } else {
            FluidActionResult filledReal;
            if (player != null && player.getAbilities().instabuild) {
                filledReal = tryFillContainer(container, fluidSource, maxAmount, player, doFill);
                if (filledReal.isSuccess()) {
                    return new FluidActionResult(container);
                }
            } else if (container.getCount() == 1) {
                filledReal = tryFillContainer(container, fluidSource, maxAmount, player, doFill);
                if (filledReal.isSuccess()) {
                    return filledReal;
                }
            } else {
                filledReal = tryFillContainer(container, fluidSource, maxAmount, player, false);
                if (filledReal.isSuccess()) {
                    ItemStack remainder = ItemHandlerHelper.insertItemStacked(inventory, filledReal.getResult(), true);
                    if (remainder.isEmpty() || player != null) {
                        FluidActionResult filledReal = tryFillContainer(container, fluidSource, maxAmount, player, doFill);
                        remainder = ItemHandlerHelper.insertItemStacked(inventory, filledReal.getResult(), !doFill);
                        if (!remainder.isEmpty() && player != null && doFill) {
                            ItemHandlerHelper.giveItemToPlayer(player, remainder);
                        }

                        ItemStack containerCopy = container.copy();
                        containerCopy.shrink(1);
                        return new FluidActionResult(containerCopy);
                    }
                }
            }

            return FluidActionResult.FAILURE;
        }
    }

    public static @NotNull FluidActionResult tryEmptyContainerAndStow(@NotNull ItemStack container, IFluidHandler fluidDestination, IItemHandler inventory, int maxAmount, @Nullable Player player, boolean doDrain) {
        if (container.isEmpty()) {
            return FluidActionResult.FAILURE;
        } else {
            FluidActionResult emptiedReal;
            if (player != null && player.getAbilities().instabuild) {
                emptiedReal = tryEmptyContainer(container, fluidDestination, maxAmount, player, doDrain);
                if (emptiedReal.isSuccess()) {
                    return new FluidActionResult(container);
                }
            } else if (container.getCount() == 1) {
                emptiedReal = tryEmptyContainer(container, fluidDestination, maxAmount, player, doDrain);
                if (emptiedReal.isSuccess()) {
                    return emptiedReal;
                }
            } else {
                emptiedReal = tryEmptyContainer(container, fluidDestination, maxAmount, player, false);
                if (emptiedReal.isSuccess()) {
                    ItemStack remainder = ItemHandlerHelper.insertItemStacked(inventory, emptiedReal.getResult(), true);
                    if (remainder.isEmpty() || player != null) {
                        FluidActionResult emptiedReal = tryEmptyContainer(container, fluidDestination, maxAmount, player, doDrain);
                        remainder = ItemHandlerHelper.insertItemStacked(inventory, emptiedReal.getResult(), !doDrain);
                        if (!remainder.isEmpty() && player != null && doDrain) {
                            ItemHandlerHelper.giveItemToPlayer(player, remainder);
                        }

                        ItemStack containerCopy = container.copy();
                        containerCopy.shrink(1);
                        return new FluidActionResult(containerCopy);
                    }
                }
            }

            return FluidActionResult.FAILURE;
        }
    }

    public static @NotNull FluidStack tryFluidTransfer(IFluidHandler fluidDestination, IFluidHandler fluidSource, int maxAmount, boolean doTransfer) {
        FluidStack drainable = fluidSource.drain(maxAmount, FluidAction.SIMULATE);
        return !drainable.isEmpty() ? tryFluidTransfer_Internal(fluidDestination, fluidSource, drainable, doTransfer) : FluidStack.EMPTY;
    }

    public static @NotNull FluidStack tryFluidTransfer(IFluidHandler fluidDestination, IFluidHandler fluidSource, FluidStack resource, boolean doTransfer) {
        FluidStack drainable = fluidSource.drain(resource, FluidAction.SIMULATE);
        return !drainable.isEmpty() && resource.isFluidEqual(drainable) ? tryFluidTransfer_Internal(fluidDestination, fluidSource, drainable, doTransfer) : FluidStack.EMPTY;
    }

    private static @NotNull FluidStack tryFluidTransfer_Internal(IFluidHandler fluidDestination, IFluidHandler fluidSource, FluidStack drainable, boolean doTransfer) {
        int fillableAmount = fluidDestination.fill(drainable, FluidAction.SIMULATE);
        if (fillableAmount > 0) {
            drainable.setAmount(fillableAmount);
            if (!doTransfer) {
                return drainable;
            }

            FluidStack drained = fluidSource.drain(drainable, FluidAction.EXECUTE);
            if (!drained.isEmpty()) {
                drained.setAmount(fluidDestination.fill(drained, FluidAction.EXECUTE));
                return drained;
            }
        }

        return FluidStack.EMPTY;
    }

    public static LazyOptional<IFluidHandlerItem> getFluidHandler(@NotNull ItemStack itemStack) {
        return itemStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
    }

    public static Optional<FluidStack> getFluidContained(@NotNull ItemStack container) {
        if (!container.isEmpty()) {
            container = ItemHandlerHelper.copyStackWithSize(container, 1);
            Optional<FluidStack> fluidContained = getFluidHandler(container).map((handler) -> {
                return handler.drain(Integer.MAX_VALUE, FluidAction.SIMULATE);
            });
            if (fluidContained.isPresent() && !((FluidStack)fluidContained.get()).isEmpty()) {
                return fluidContained;
            }
        }

        return Optional.empty();
    }

    public static LazyOptional<IFluidHandler> getFluidHandler(Level level, BlockPos blockPos, @Nullable Direction side) {
        BlockState state = level.getBlockState(blockPos);
        if (state.hasBlockEntity()) {
            BlockEntity blockEntity = level.getBlockEntity(blockPos);
            if (blockEntity != null) {
                return blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER, side);
            }
        }

        return LazyOptional.empty();
    }

    public static @NotNull FluidActionResult tryPickUpFluid(@NotNull ItemStack emptyContainer, @Nullable Player playerIn, Level level, BlockPos pos, Direction side) {
        if (!emptyContainer.isEmpty() && level != null && pos != null) {
            BlockState state = level.getBlockState(pos);
            Block block = state.getBlock();
            Object targetFluidHandler;
            if (block instanceof IFluidBlock) {
                targetFluidHandler = new FluidBlockWrapper((IFluidBlock)block, level, pos);
            } else if (block instanceof BucketPickup) {
                targetFluidHandler = new BucketPickupHandlerWrapper((BucketPickup)block, level, pos);
            } else {
                Optional<IFluidHandler> fluidHandler = getFluidHandler(level, pos, side).resolve();
                if (!fluidHandler.isPresent()) {
                    return FluidActionResult.FAILURE;
                }

                targetFluidHandler = (IFluidHandler)fluidHandler.get();
            }

            return tryFillContainer(emptyContainer, (IFluidHandler)targetFluidHandler, Integer.MAX_VALUE, playerIn, true);
        } else {
            return FluidActionResult.FAILURE;
        }
    }

    public static @NotNull FluidActionResult tryPlaceFluid(@Nullable Player player, Level level, InteractionHand hand, BlockPos pos, @NotNull ItemStack container, FluidStack resource) {
        ItemStack containerCopy = ItemHandlerHelper.copyStackWithSize(container, 1);
        return (FluidActionResult)getFluidHandler(containerCopy).filter((handler) -> {
            return tryPlaceFluid(player, level, hand, pos, (IFluidHandler)handler, resource);
        }).map(IFluidHandlerItem::getContainer).map(FluidActionResult::new).orElse(FluidActionResult.FAILURE);
    }

    public static boolean tryPlaceFluid(@Nullable Player player, Level level, InteractionHand hand, BlockPos pos, IFluidHandler fluidSource, FluidStack resource) {
        if (level != null && pos != null) {
            Fluid fluid = resource.getFluid();
            if (fluid != Fluids.EMPTY && fluid.getFluidType().canBePlacedInLevel(level, pos, (FluidStack)resource)) {
                if (fluidSource.drain(resource, FluidAction.SIMULATE).isEmpty()) {
                    return false;
                } else {
                    BlockPlaceContext context = new BlockPlaceContext(level, player, hand, player == null ? ItemStack.EMPTY : player.getItemInHand(hand), new BlockHitResult(Vec3.ZERO, Direction.UP, pos, false));
                    BlockState destBlockState = level.getBlockState(pos);
                    boolean isDestNonSolid = !destBlockState.isSolid();
                    boolean isDestReplaceable = destBlockState.canBeReplaced(context);
                    boolean canDestContainFluid = destBlockState.getBlock() instanceof LiquidBlockContainer && ((LiquidBlockContainer)destBlockState.getBlock()).canPlaceLiquid(level, pos, destBlockState, fluid);
                    if (!level.isEmptyBlock(pos) && !isDestNonSolid && !isDestReplaceable && !canDestContainFluid) {
                        return false;
                    } else {
                        if (fluid.getFluidType().isVaporizedOnPlacement(level, pos, resource)) {
                            FluidStack result = fluidSource.drain(resource, FluidAction.EXECUTE);
                            if (!result.isEmpty()) {
                                result.getFluid().getFluidType().onVaporize(player, level, pos, result);
                                return true;
                            }
                        } else {
                            Object handler;
                            if (canDestContainFluid) {
                                handler = new BlockWrapper.LiquidContainerBlockWrapper((LiquidBlockContainer)destBlockState.getBlock(), level, pos);
                            } else {
                                handler = getFluidBlockHandler(fluid, level, pos);
                            }

                            FluidStack result = tryFluidTransfer((IFluidHandler)handler, fluidSource, resource, true);
                            if (!result.isEmpty()) {
                                SoundEvent soundevent = resource.getFluid().getFluidType().getSound(resource, SoundActions.BUCKET_EMPTY);
                                if (soundevent != null) {
                                    level.playSound(player, pos, soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
                                }

                                return true;
                            }
                        }

                        return false;
                    }
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private static IFluidHandler getFluidBlockHandler(Fluid fluid, Level level, BlockPos pos) {
        BlockState state = fluid.getFluidType().getBlockForFluidState(level, pos, fluid.defaultFluidState());
        return new BlockWrapper(state, level, pos);
    }

    public static void destroyBlockOnFluidPlacement(Level level, BlockPos pos) {
        if (!level.isClientSide) {
            BlockState destBlockState = level.getBlockState(pos);
            boolean isDestNonSolid = !destBlockState.isSolid();
            boolean isDestReplaceable = false;
            if ((isDestNonSolid || isDestReplaceable) && !destBlockState.liquid()) {
                level.destroyBlock(pos, true);
            }
        }

    }

    public static @NotNull ItemStack getFilledBucket(@NotNull FluidStack fluidStack) {
        Fluid fluid = fluidStack.getFluid();
        if (!fluidStack.hasTag() || fluidStack.getTag().isEmpty()) {
            if (fluid == Fluids.WATER) {
                return new ItemStack(Items.WATER_BUCKET);
            }

            if (fluid == Fluids.LAVA) {
                return new ItemStack(Items.LAVA_BUCKET);
            }
        }

        return fluid.getFluidType().getBucket(fluidStack);
    }
}

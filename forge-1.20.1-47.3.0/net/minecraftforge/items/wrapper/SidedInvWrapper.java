//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.items.wrapper;

import java.util.function.IntUnaryOperator;
import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SidedInvWrapper implements IItemHandlerModifiable {
    protected final WorldlyContainer inv;
    protected final @Nullable Direction side;
    private final IntUnaryOperator slotLimit;
    private final InsertLimit newStackInsertLimit;

    public static LazyOptional<IItemHandlerModifiable>[] create(WorldlyContainer inv, Direction... sides) {
        LazyOptional<IItemHandlerModifiable>[] ret = new LazyOptional[sides.length];

        for(int x = 0; x < sides.length; ++x) {
            Direction side = sides[x];
            ret[x] = LazyOptional.of(() -> {
                return new SidedInvWrapper(inv, side);
            });
        }

        return ret;
    }

    public SidedInvWrapper(WorldlyContainer inv, @Nullable Direction side) {
        this.inv = inv;
        this.side = side;
        if (inv instanceof BrewingStandBlockEntity) {
            this.slotLimit = (wrapperSlot) -> {
                return getSlot(inv, wrapperSlot, side) < 3 ? 1 : inv.getMaxStackSize();
            };
        } else {
            this.slotLimit = (wrapperSlot) -> {
                return inv.getMaxStackSize();
            };
        }

        if (inv instanceof AbstractFurnaceBlockEntity) {
            this.newStackInsertLimit = (wrapperSlot, invSlot, stack) -> {
                return invSlot == 1 && stack.is(Items.BUCKET) ? 1 : Math.min(stack.getMaxStackSize(), this.getSlotLimit(wrapperSlot));
            };
        } else {
            this.newStackInsertLimit = (wrapperSlot, invSlot, stack) -> {
                return Math.min(stack.getMaxStackSize(), this.getSlotLimit(wrapperSlot));
            };
        }

    }

    public static int getSlot(WorldlyContainer inv, int slot, @Nullable Direction side) {
        int[] slots = inv.getSlotsForFace(side);
        return slot < slots.length ? slots[slot] : -1;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            SidedInvWrapper that = (SidedInvWrapper)o;
            return this.inv.equals(that.inv) && this.side == that.side;
        } else {
            return false;
        }
    }

    public int hashCode() {
        int result = this.inv.hashCode();
        result = 31 * result + (this.side == null ? 0 : this.side.hashCode());
        return result;
    }

    public int getSlots() {
        return this.inv.getSlotsForFace(this.side).length;
    }

    public @NotNull ItemStack getStackInSlot(int slot) {
        int i = getSlot(this.inv, slot, this.side);
        return i == -1 ? ItemStack.EMPTY : this.inv.getItem(i);
    }

    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            int slot1 = getSlot(this.inv, slot, this.side);
            if (slot1 == -1) {
                return stack;
            } else {
                ItemStack stackInSlot = this.inv.getItem(slot1);
                int m;
                if (!stackInSlot.isEmpty()) {
                    if (stackInSlot.getCount() >= Math.min(stackInSlot.getMaxStackSize(), this.getSlotLimit(slot))) {
                        return stack;
                    } else if (!ItemHandlerHelper.canItemStacksStack(stack, stackInSlot)) {
                        return stack;
                    } else if (this.inv.canPlaceItemThroughFace(slot1, stack, this.side) && this.inv.canPlaceItem(slot1, stack)) {
                        m = Math.min(stack.getMaxStackSize(), this.getSlotLimit(slot)) - stackInSlot.getCount();
                        ItemStack copy;
                        if (stack.getCount() <= m) {
                            if (!simulate) {
                                copy = stack.copy();
                                copy.grow(stackInSlot.getCount());
                                this.setInventorySlotContents(slot1, copy);
                            }

                            return ItemStack.EMPTY;
                        } else {
                            stack = stack.copy();
                            if (!simulate) {
                                copy = stack.split(m);
                                copy.grow(stackInSlot.getCount());
                                this.setInventorySlotContents(slot1, copy);
                                return stack;
                            } else {
                                stack.shrink(m);
                                return stack;
                            }
                        }
                    } else {
                        return stack;
                    }
                } else if (this.inv.canPlaceItemThroughFace(slot1, stack, this.side) && this.inv.canPlaceItem(slot1, stack)) {
                    m = this.newStackInsertLimit.limitInsert(slot, slot1, stack);
                    if (m < stack.getCount()) {
                        stack = stack.copy();
                        if (!simulate) {
                            this.setInventorySlotContents(slot1, stack.split(m));
                            return stack;
                        } else {
                            stack.shrink(m);
                            return stack;
                        }
                    } else {
                        if (!simulate) {
                            this.setInventorySlotContents(slot1, stack);
                        }

                        return ItemStack.EMPTY;
                    }
                } else {
                    return stack;
                }
            }
        }
    }

    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        int slot1 = getSlot(this.inv, slot, this.side);
        if (slot1 != -1) {
            this.setInventorySlotContents(slot1, stack);
        }

    }

    private void setInventorySlotContents(int slot, ItemStack stack) {
        this.inv.setChanged();
        this.inv.setItem(slot, stack);
    }

    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0) {
            return ItemStack.EMPTY;
        } else {
            int slot1 = getSlot(this.inv, slot, this.side);
            if (slot1 == -1) {
                return ItemStack.EMPTY;
            } else {
                ItemStack stackInSlot = this.inv.getItem(slot1);
                if (stackInSlot.isEmpty()) {
                    return ItemStack.EMPTY;
                } else if (!this.inv.canTakeItemThroughFace(slot1, stackInSlot, this.side)) {
                    return ItemStack.EMPTY;
                } else if (simulate) {
                    if (stackInSlot.getCount() < amount) {
                        return stackInSlot.copy();
                    } else {
                        ItemStack copy = stackInSlot.copy();
                        copy.setCount(amount);
                        return copy;
                    }
                } else {
                    int m = Math.min(stackInSlot.getCount(), amount);
                    ItemStack ret = this.inv.removeItem(slot1, m);
                    this.inv.setChanged();
                    return ret;
                }
            }
        }
    }

    public int getSlotLimit(int slot) {
        return this.slotLimit.applyAsInt(slot);
    }

    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        int slot1 = getSlot(this.inv, slot, this.side);
        return slot1 == -1 ? false : this.inv.canPlaceItem(slot1, stack);
    }

    private interface InsertLimit {
        int limitInsert(int var1, int var2, ItemStack var3);
    }
}

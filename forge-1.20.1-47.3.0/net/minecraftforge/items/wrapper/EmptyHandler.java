//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.items.wrapper;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

public class EmptyHandler implements IItemHandlerModifiable {
    public static final IItemHandler INSTANCE = new EmptyHandler();

    public EmptyHandler() {
    }

    public int getSlots() {
        return 0;
    }

    public @NotNull ItemStack getStackInSlot(int slot) {
        return ItemStack.EMPTY;
    }

    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return stack;
    }

    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        return ItemStack.EMPTY;
    }

    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
    }

    public int getSlotLimit(int slot) {
        return 0;
    }

    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return false;
    }
}

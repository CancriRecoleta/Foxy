//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.inventory;

import net.minecraft.world.item.ItemStack;

public interface ContainerListener {
    void slotChanged(AbstractContainerMenu var1, int var2, ItemStack var3);

    void dataChanged(AbstractContainerMenu var1, int var2, int var3);
}

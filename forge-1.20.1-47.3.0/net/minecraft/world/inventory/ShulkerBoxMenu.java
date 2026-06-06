//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ShulkerBoxMenu extends AbstractContainerMenu {
    private static final int CONTAINER_SIZE = 27;
    private final Container container;

    public ShulkerBoxMenu(int p_40188_, Inventory p_40189_) {
        this(p_40188_, p_40189_, new SimpleContainer(27));
    }

    public ShulkerBoxMenu(int p_40191_, Inventory p_40192_, Container p_40193_) {
        super(MenuType.SHULKER_BOX, p_40191_);
        checkContainerSize(p_40193_, 27);
        this.container = p_40193_;
        p_40193_.startOpen(p_40192_.player);
        int $$3 = true;
        int $$4 = true;

        int $$9;
        int $$8;
        for($$9 = 0; $$9 < 3; ++$$9) {
            for($$8 = 0; $$8 < 9; ++$$8) {
                this.addSlot(new ShulkerBoxSlot(p_40193_, $$8 + $$9 * 9, 8 + $$8 * 18, 18 + $$9 * 18));
            }
        }

        for($$9 = 0; $$9 < 3; ++$$9) {
            for($$8 = 0; $$8 < 9; ++$$8) {
                this.addSlot(new Slot(p_40192_, $$8 + $$9 * 9 + 9, 8 + $$8 * 18, 84 + $$9 * 18));
            }
        }

        for($$9 = 0; $$9 < 9; ++$$9) {
            this.addSlot(new Slot(p_40192_, $$9, 8 + $$9 * 18, 142));
        }

    }

    public boolean stillValid(Player p_40195_) {
        return this.container.stillValid(p_40195_);
    }

    public ItemStack quickMoveStack(Player p_40199_, int p_40200_) {
        ItemStack $$2 = ItemStack.EMPTY;
        Slot $$3 = (Slot)this.slots.get(p_40200_);
        if ($$3 != null && $$3.hasItem()) {
            ItemStack $$4 = $$3.getItem();
            $$2 = $$4.copy();
            if (p_40200_ < this.container.getContainerSize()) {
                if (!this.moveItemStackTo($$4, this.container.getContainerSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo($$4, 0, this.container.getContainerSize(), false)) {
                return ItemStack.EMPTY;
            }

            if ($$4.isEmpty()) {
                $$3.setByPlayer(ItemStack.EMPTY);
            } else {
                $$3.setChanged();
            }
        }

        return $$2;
    }

    public void removed(Player p_40197_) {
        super.removed(p_40197_);
        this.container.stopOpen(p_40197_);
    }
}

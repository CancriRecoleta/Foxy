//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class HorseInventoryMenu extends AbstractContainerMenu {
    private final Container horseContainer;
    private final AbstractHorse horse;

    public HorseInventoryMenu(int p_39656_, Inventory p_39657_, Container p_39658_, final AbstractHorse p_39659_) {
        super((MenuType)null, p_39656_);
        this.horseContainer = p_39658_;
        this.horse = p_39659_;
        int $$4 = true;
        p_39658_.startOpen(p_39657_.player);
        int $$5 = true;
        this.addSlot(new Slot(p_39658_, 0, 8, 18) {
            public boolean mayPlace(ItemStack p_39677_) {
                return p_39677_.is(Items.SADDLE) && !this.hasItem() && p_39659_.isSaddleable();
            }

            public boolean isActive() {
                return p_39659_.isSaddleable();
            }
        });
        this.addSlot(new Slot(p_39658_, 1, 8, 36) {
            public boolean mayPlace(ItemStack p_39690_) {
                return p_39659_.isArmor(p_39690_);
            }

            public boolean isActive() {
                return p_39659_.canWearArmor();
            }

            public int getMaxStackSize() {
                return 1;
            }
        });
        int $$10;
        int $$9;
        if (this.hasChest(p_39659_)) {
            for($$10 = 0; $$10 < 3; ++$$10) {
                for($$9 = 0; $$9 < ((AbstractChestedHorse)p_39659_).getInventoryColumns(); ++$$9) {
                    this.addSlot(new Slot(p_39658_, 2 + $$9 + $$10 * ((AbstractChestedHorse)p_39659_).getInventoryColumns(), 80 + $$9 * 18, 18 + $$10 * 18));
                }
            }
        }

        for($$10 = 0; $$10 < 3; ++$$10) {
            for($$9 = 0; $$9 < 9; ++$$9) {
                this.addSlot(new Slot(p_39657_, $$9 + $$10 * 9 + 9, 8 + $$9 * 18, 102 + $$10 * 18 + -18));
            }
        }

        for($$10 = 0; $$10 < 9; ++$$10) {
            this.addSlot(new Slot(p_39657_, $$10, 8 + $$10 * 18, 142));
        }

    }

    public boolean stillValid(Player p_39661_) {
        return !this.horse.hasInventoryChanged(this.horseContainer) && this.horseContainer.stillValid(p_39661_) && this.horse.isAlive() && this.horse.distanceTo(p_39661_) < 8.0F;
    }

    private boolean hasChest(AbstractHorse p_150578_) {
        return p_150578_ instanceof AbstractChestedHorse && ((AbstractChestedHorse)p_150578_).hasChest();
    }

    public ItemStack quickMoveStack(Player p_39665_, int p_39666_) {
        ItemStack $$2 = ItemStack.EMPTY;
        Slot $$3 = (Slot)this.slots.get(p_39666_);
        if ($$3 != null && $$3.hasItem()) {
            ItemStack $$4 = $$3.getItem();
            $$2 = $$4.copy();
            int $$5 = this.horseContainer.getContainerSize();
            if (p_39666_ < $$5) {
                if (!this.moveItemStackTo($$4, $$5, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.getSlot(1).mayPlace($$4) && !this.getSlot(1).hasItem()) {
                if (!this.moveItemStackTo($$4, 1, 2, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.getSlot(0).mayPlace($$4)) {
                if (!this.moveItemStackTo($$4, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if ($$5 <= 2 || !this.moveItemStackTo($$4, 2, $$5, false)) {
                int $$6 = $$5;
                int $$7 = $$6 + 27;
                int $$8 = $$7;
                int $$9 = $$8 + 9;
                if (p_39666_ >= $$8 && p_39666_ < $$9) {
                    if (!this.moveItemStackTo($$4, $$6, $$7, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (p_39666_ >= $$6 && p_39666_ < $$7) {
                    if (!this.moveItemStackTo($$4, $$8, $$9, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo($$4, $$8, $$7, false)) {
                    return ItemStack.EMPTY;
                }

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

    public void removed(Player p_39663_) {
        super.removed(p_39663_);
        this.horseContainer.stopOpen(p_39663_);
    }
}

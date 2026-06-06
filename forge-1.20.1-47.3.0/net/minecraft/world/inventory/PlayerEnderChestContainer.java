//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.inventory;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;

public class PlayerEnderChestContainer extends SimpleContainer {
    @Nullable
    private EnderChestBlockEntity activeChest;

    public PlayerEnderChestContainer() {
        super(27);
    }

    public void setActiveChest(EnderChestBlockEntity p_40106_) {
        this.activeChest = p_40106_;
    }

    public boolean isActiveChest(EnderChestBlockEntity p_150634_) {
        return this.activeChest == p_150634_;
    }

    public void fromTag(ListTag p_40108_) {
        int $$2;
        for($$2 = 0; $$2 < this.getContainerSize(); ++$$2) {
            this.setItem($$2, ItemStack.EMPTY);
        }

        for($$2 = 0; $$2 < p_40108_.size(); ++$$2) {
            CompoundTag $$3 = p_40108_.getCompound($$2);
            int $$4 = $$3.getByte("Slot") & 255;
            if ($$4 >= 0 && $$4 < this.getContainerSize()) {
                this.setItem($$4, ItemStack.of($$3));
            }
        }

    }

    public ListTag createTag() {
        ListTag $$0 = new ListTag();

        for(int $$1 = 0; $$1 < this.getContainerSize(); ++$$1) {
            ItemStack $$2 = this.getItem($$1);
            if (!$$2.isEmpty()) {
                CompoundTag $$3 = new CompoundTag();
                $$3.putByte("Slot", (byte)$$1);
                $$2.save($$3);
                $$0.add($$3);
            }
        }

        return $$0;
    }

    public boolean stillValid(Player p_40104_) {
        return this.activeChest != null && !this.activeChest.stillValid(p_40104_) ? false : super.stillValid(p_40104_);
    }

    public void startOpen(Player p_40112_) {
        if (this.activeChest != null) {
            this.activeChest.startOpen(p_40112_);
        }

        super.startOpen(p_40112_);
    }

    public void stopOpen(Player p_40110_) {
        if (this.activeChest != null) {
            this.activeChest.stopOpen(p_40110_);
        }

        super.stopOpen(p_40110_);
        this.activeChest = null;
    }
}

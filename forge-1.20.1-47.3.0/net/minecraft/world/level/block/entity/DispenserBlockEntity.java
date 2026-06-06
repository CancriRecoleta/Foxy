//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DispenserMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class DispenserBlockEntity extends RandomizableContainerBlockEntity {
    public static final int CONTAINER_SIZE = 9;
    private NonNullList<ItemStack> items;

    protected DispenserBlockEntity(BlockEntityType<?> p_155489_, BlockPos p_155490_, BlockState p_155491_) {
        super(p_155489_, p_155490_, p_155491_);
        this.items = NonNullList.withSize(9, ItemStack.EMPTY);
    }

    public DispenserBlockEntity(BlockPos p_155493_, BlockState p_155494_) {
        this(BlockEntityType.DISPENSER, p_155493_, p_155494_);
    }

    public int getContainerSize() {
        return 9;
    }

    public int getRandomSlot(RandomSource p_222762_) {
        this.unpackLootTable((Player)null);
        int $$1 = -1;
        int $$2 = 1;

        for(int $$3 = 0; $$3 < this.items.size(); ++$$3) {
            if (!((ItemStack)this.items.get($$3)).isEmpty() && p_222762_.nextInt($$2++) == 0) {
                $$1 = $$3;
            }
        }

        return $$1;
    }

    public int addItem(ItemStack p_59238_) {
        for(int $$1 = 0; $$1 < this.items.size(); ++$$1) {
            if (((ItemStack)this.items.get($$1)).isEmpty()) {
                this.setItem($$1, p_59238_);
                return $$1;
            }
        }

        return -1;
    }

    protected Component getDefaultName() {
        return Component.translatable("container.dispenser");
    }

    public void load(CompoundTag p_155496_) {
        super.load(p_155496_);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(p_155496_)) {
            ContainerHelper.loadAllItems(p_155496_, this.items);
        }

    }

    protected void saveAdditional(CompoundTag p_187498_) {
        super.saveAdditional(p_187498_);
        if (!this.trySaveLootTable(p_187498_)) {
            ContainerHelper.saveAllItems(p_187498_, this.items);
        }

    }

    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    protected void setItems(NonNullList<ItemStack> p_59243_) {
        this.items = p_59243_;
    }

    protected AbstractContainerMenu createMenu(int p_59235_, Inventory p_59236_) {
        return new DispenserMenu(p_59235_, p_59236_, this);
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.entity.vehicle;

import java.util.Iterator;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.HopperMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MinecartHopper extends AbstractMinecartContainer implements Hopper {
    private boolean enabled = true;

    public MinecartHopper(EntityType<? extends MinecartHopper> p_38584_, Level p_38585_) {
        super(p_38584_, p_38585_);
    }

    public MinecartHopper(Level p_38587_, double p_38588_, double p_38589_, double p_38590_) {
        super(EntityType.HOPPER_MINECART, p_38588_, p_38589_, p_38590_, p_38587_);
    }

    public AbstractMinecart.Type getMinecartType() {
        return net.minecraft.world.entity.vehicle.AbstractMinecart.Type.HOPPER;
    }

    public BlockState getDefaultDisplayBlockState() {
        return Blocks.HOPPER.defaultBlockState();
    }

    public int getDefaultDisplayOffset() {
        return 1;
    }

    public int getContainerSize() {
        return 5;
    }

    public void activateMinecart(int p_38596_, int p_38597_, int p_38598_, boolean p_38599_) {
        boolean $$4 = !p_38599_;
        if ($$4 != this.isEnabled()) {
            this.setEnabled($$4);
        }

    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean p_38614_) {
        this.enabled = p_38614_;
    }

    public double getLevelX() {
        return this.getX();
    }

    public double getLevelY() {
        return this.getY() + 0.5;
    }

    public double getLevelZ() {
        return this.getZ();
    }

    public void tick() {
        super.tick();
        if (!this.level().isClientSide && this.isAlive() && this.isEnabled() && this.suckInItems()) {
            this.setChanged();
        }

    }

    public boolean suckInItems() {
        if (HopperBlockEntity.suckInItems(this.level(), this)) {
            return true;
        } else {
            List<ItemEntity> $$0 = this.level().getEntitiesOfClass(ItemEntity.class, this.getBoundingBox().inflate(0.25, 0.0, 0.25), EntitySelector.ENTITY_STILL_ALIVE);
            Iterator var2 = $$0.iterator();

            ItemEntity $$1;
            do {
                if (!var2.hasNext()) {
                    return false;
                }

                $$1 = (ItemEntity)var2.next();
            } while(!HopperBlockEntity.addItem(this, $$1));

            return true;
        }
    }

    protected Item getDropItem() {
        return Items.HOPPER_MINECART;
    }

    protected void addAdditionalSaveData(CompoundTag p_38608_) {
        super.addAdditionalSaveData(p_38608_);
        p_38608_.putBoolean("Enabled", this.enabled);
    }

    protected void readAdditionalSaveData(CompoundTag p_38606_) {
        super.readAdditionalSaveData(p_38606_);
        this.enabled = p_38606_.contains("Enabled") ? p_38606_.getBoolean("Enabled") : true;
    }

    public AbstractContainerMenu createMenu(int p_38601_, Inventory p_38602_) {
        return new HopperMenu(p_38601_, p_38602_, this);
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.core.dispenser;

import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

public abstract class AbstractProjectileDispenseBehavior extends DefaultDispenseItemBehavior {
    public AbstractProjectileDispenseBehavior() {
    }

    public ItemStack execute(BlockSource p_123366_, ItemStack p_123367_) {
        Level $$2 = p_123366_.getLevel();
        Position $$3 = DispenserBlock.getDispensePosition(p_123366_);
        Direction $$4 = (Direction)p_123366_.getBlockState().getValue(DispenserBlock.FACING);
        Projectile $$5 = this.getProjectile($$2, $$3, p_123367_);
        $$5.shoot((double)$$4.getStepX(), (double)((float)$$4.getStepY() + 0.1F), (double)$$4.getStepZ(), this.getPower(), this.getUncertainty());
        $$2.addFreshEntity($$5);
        p_123367_.shrink(1);
        return p_123367_;
    }

    protected void playSound(BlockSource p_123364_) {
        p_123364_.getLevel().levelEvent(1002, p_123364_.getPos(), 0);
    }

    protected abstract Projectile getProjectile(Level var1, Position var2, ItemStack var3);

    protected float getUncertainty() {
        return 6.0F;
    }

    protected float getPower() {
        return 1.1F;
    }
}

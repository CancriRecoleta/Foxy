//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.core.dispenser;

import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

public class DefaultDispenseItemBehavior implements DispenseItemBehavior {
    public DefaultDispenseItemBehavior() {
    }

    public final ItemStack dispense(BlockSource p_123391_, ItemStack p_123392_) {
        ItemStack $$2 = this.execute(p_123391_, p_123392_);
        this.playSound(p_123391_);
        this.playAnimation(p_123391_, (Direction)p_123391_.getBlockState().getValue(DispenserBlock.FACING));
        return $$2;
    }

    protected ItemStack execute(BlockSource p_123385_, ItemStack p_123386_) {
        Direction $$2 = (Direction)p_123385_.getBlockState().getValue(DispenserBlock.FACING);
        Position $$3 = DispenserBlock.getDispensePosition(p_123385_);
        ItemStack $$4 = p_123386_.split(1);
        spawnItem(p_123385_.getLevel(), $$4, 6, $$2, $$3);
        return p_123386_;
    }

    public static void spawnItem(Level p_123379_, ItemStack p_123380_, int p_123381_, Direction p_123382_, Position p_123383_) {
        double $$5 = p_123383_.x();
        double $$6 = p_123383_.y();
        double $$7 = p_123383_.z();
        if (p_123382_.getAxis() == Axis.Y) {
            $$6 -= 0.125;
        } else {
            $$6 -= 0.15625;
        }

        ItemEntity $$8 = new ItemEntity(p_123379_, $$5, $$6, $$7, p_123380_);
        double $$9 = p_123379_.random.nextDouble() * 0.1 + 0.2;
        $$8.setDeltaMovement(p_123379_.random.triangle((double)p_123382_.getStepX() * $$9, 0.0172275 * (double)p_123381_), p_123379_.random.triangle(0.2, 0.0172275 * (double)p_123381_), p_123379_.random.triangle((double)p_123382_.getStepZ() * $$9, 0.0172275 * (double)p_123381_));
        p_123379_.addFreshEntity($$8);
    }

    protected void playSound(BlockSource p_123384_) {
        p_123384_.getLevel().levelEvent(1000, p_123384_.getPos(), 0);
    }

    protected void playAnimation(BlockSource p_123388_, Direction p_123389_) {
        p_123388_.getLevel().levelEvent(2000, p_123388_.getPos(), p_123389_.get3DDataValue());
    }
}

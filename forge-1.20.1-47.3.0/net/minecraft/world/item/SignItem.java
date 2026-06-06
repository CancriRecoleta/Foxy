//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SignItem extends StandingAndWallBlockItem {
    public SignItem(Item.Properties p_43126_, Block p_43127_, Block p_43128_) {
        super(p_43127_, p_43128_, p_43126_, Direction.DOWN);
    }

    public SignItem(Item.Properties p_278081_, Block p_277743_, Block p_277375_, Direction p_278052_) {
        super(p_277743_, p_277375_, p_278081_, p_278052_);
    }

    protected boolean updateCustomBlockEntityTag(BlockPos p_43130_, Level p_43131_, @Nullable Player p_43132_, ItemStack p_43133_, BlockState p_43134_) {
        boolean $$5 = super.updateCustomBlockEntityTag(p_43130_, p_43131_, p_43132_, p_43133_, p_43134_);
        if (!p_43131_.isClientSide && !$$5 && p_43132_ != null) {
            BlockEntity var9 = p_43131_.getBlockEntity(p_43130_);
            if (var9 instanceof SignBlockEntity) {
                SignBlockEntity $$6 = (SignBlockEntity)var9;
                Block var10 = p_43131_.getBlockState(p_43130_).getBlock();
                if (var10 instanceof SignBlock) {
                    SignBlock $$7 = (SignBlock)var10;
                    $$7.openTextEdit(p_43132_, $$6, true);
                }
            }
        }

        return $$5;
    }
}

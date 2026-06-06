//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;

public class FireChargeItem extends Item {
    public FireChargeItem(Item.Properties p_41202_) {
        super(p_41202_);
    }

    public InteractionResult useOn(UseOnContext p_41204_) {
        Level $$1 = p_41204_.getLevel();
        BlockPos $$2 = p_41204_.getClickedPos();
        BlockState $$3 = $$1.getBlockState($$2);
        boolean $$4 = false;
        if (!CampfireBlock.canLight($$3) && !CandleBlock.canLight($$3) && !CandleCakeBlock.canLight($$3)) {
            $$2 = $$2.relative(p_41204_.getClickedFace());
            if (BaseFireBlock.canBePlacedAt($$1, $$2, p_41204_.getHorizontalDirection())) {
                this.playSound($$1, $$2);
                $$1.setBlockAndUpdate($$2, BaseFireBlock.getState($$1, $$2));
                $$1.gameEvent(p_41204_.getPlayer(), GameEvent.BLOCK_PLACE, $$2);
                $$4 = true;
            }
        } else {
            this.playSound($$1, $$2);
            $$1.setBlockAndUpdate($$2, (BlockState)$$3.setValue(BlockStateProperties.LIT, true));
            $$1.gameEvent(p_41204_.getPlayer(), GameEvent.BLOCK_CHANGE, $$2);
            $$4 = true;
        }

        if ($$4) {
            p_41204_.getItemInHand().shrink(1);
            return InteractionResult.sidedSuccess($$1.isClientSide);
        } else {
            return InteractionResult.FAIL;
        }
    }

    private void playSound(Level p_41206_, BlockPos p_41207_) {
        RandomSource $$2 = p_41206_.getRandom();
        p_41206_.playSound((Player)null, (BlockPos)p_41207_, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0F, ($$2.nextFloat() - $$2.nextFloat()) * 0.2F + 1.0F);
    }
}

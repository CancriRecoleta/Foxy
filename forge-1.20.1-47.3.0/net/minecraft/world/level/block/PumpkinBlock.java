//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ToolActions;

public class PumpkinBlock extends StemGrownBlock {
    public PumpkinBlock(BlockBehaviour.Properties p_55284_) {
        super(p_55284_);
    }

    public InteractionResult use(BlockState p_55289_, Level p_55290_, BlockPos p_55291_, Player p_55292_, InteractionHand p_55293_, BlockHitResult p_55294_) {
        ItemStack itemstack = p_55292_.getItemInHand(p_55293_);
        if (itemstack.canPerformAction(ToolActions.SHEARS_CARVE)) {
            if (!p_55290_.isClientSide) {
                Direction direction = p_55294_.getDirection();
                Direction direction1 = direction.getAxis() == Axis.Y ? p_55292_.getDirection().getOpposite() : direction;
                p_55290_.playSound((Player)null, p_55291_, SoundEvents.PUMPKIN_CARVE, SoundSource.BLOCKS, 1.0F, 1.0F);
                p_55290_.setBlock(p_55291_, (BlockState)Blocks.CARVED_PUMPKIN.defaultBlockState().setValue(CarvedPumpkinBlock.FACING, direction1), 11);
                ItemEntity itementity = new ItemEntity(p_55290_, (double)p_55291_.getX() + 0.5 + (double)direction1.getStepX() * 0.65, (double)p_55291_.getY() + 0.1, (double)p_55291_.getZ() + 0.5 + (double)direction1.getStepZ() * 0.65, new ItemStack(Items.PUMPKIN_SEEDS, 4));
                itementity.setDeltaMovement(0.05 * (double)direction1.getStepX() + p_55290_.random.nextDouble() * 0.02, 0.05, 0.05 * (double)direction1.getStepZ() + p_55290_.random.nextDouble() * 0.02);
                p_55290_.addFreshEntity(itementity);
                itemstack.hurtAndBreak(1, p_55292_, (p_55287_) -> {
                    p_55287_.broadcastBreakEvent(p_55293_);
                });
                p_55290_.gameEvent(p_55292_, GameEvent.SHEAR, p_55291_);
                p_55292_.awardStat(Stats.ITEM_USED.get(Items.SHEARS));
            }

            return InteractionResult.sidedSuccess(p_55290_.isClientSide);
        } else {
            return super.use(p_55289_, p_55290_, p_55291_, p_55292_, p_55293_, p_55294_);
        }
    }

    public StemBlock getStem() {
        return (StemBlock)Blocks.PUMPKIN_STEM;
    }

    public AttachedStemBlock getAttachedStem() {
        return (AttachedStemBlock)Blocks.ATTACHED_PUMPKIN_STEM;
    }
}

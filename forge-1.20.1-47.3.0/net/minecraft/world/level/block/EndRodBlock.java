//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class EndRodBlock extends RodBlock {
    public EndRodBlock(BlockBehaviour.Properties p_53085_) {
        super(p_53085_);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.UP));
    }

    public BlockState getStateForPlacement(BlockPlaceContext p_53087_) {
        Direction $$1 = p_53087_.getClickedFace();
        BlockState $$2 = p_53087_.getLevel().getBlockState(p_53087_.getClickedPos().relative($$1.getOpposite()));
        return $$2.is(this) && $$2.getValue(FACING) == $$1 ? (BlockState)this.defaultBlockState().setValue(FACING, $$1.getOpposite()) : (BlockState)this.defaultBlockState().setValue(FACING, $$1);
    }

    public void animateTick(BlockState p_221107_, Level p_221108_, BlockPos p_221109_, RandomSource p_221110_) {
        Direction $$4 = (Direction)p_221107_.getValue(FACING);
        double $$5 = (double)p_221109_.getX() + 0.55 - (double)(p_221110_.nextFloat() * 0.1F);
        double $$6 = (double)p_221109_.getY() + 0.55 - (double)(p_221110_.nextFloat() * 0.1F);
        double $$7 = (double)p_221109_.getZ() + 0.55 - (double)(p_221110_.nextFloat() * 0.1F);
        double $$8 = (double)(0.4F - (p_221110_.nextFloat() + p_221110_.nextFloat()) * 0.4F);
        if (p_221110_.nextInt(5) == 0) {
            p_221108_.addParticle(ParticleTypes.END_ROD, $$5 + (double)$$4.getStepX() * $$8, $$6 + (double)$$4.getStepY() * $$8, $$7 + (double)$$4.getStepZ() * $$8, p_221110_.nextGaussian() * 0.005, p_221110_.nextGaussian() * 0.005, p_221110_.nextGaussian() * 0.005);
        }

    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_53105_) {
        p_53105_.add(FACING);
    }
}

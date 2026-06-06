//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block;

import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class InfestedRotatedPillarBlock extends InfestedBlock {
    public InfestedRotatedPillarBlock(Block p_153438_, BlockBehaviour.Properties p_153439_) {
        super(p_153438_, p_153439_);
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue(RotatedPillarBlock.AXIS, Axis.Y));
    }

    public BlockState rotate(BlockState p_153443_, Rotation p_153444_) {
        return RotatedPillarBlock.rotatePillar(p_153443_, p_153444_);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_153446_) {
        p_153446_.add(RotatedPillarBlock.AXIS);
    }

    public BlockState getStateForPlacement(BlockPlaceContext p_153441_) {
        return (BlockState)this.defaultBlockState().setValue(RotatedPillarBlock.AXIS, p_153441_.getClickedFace().getAxis());
    }
}

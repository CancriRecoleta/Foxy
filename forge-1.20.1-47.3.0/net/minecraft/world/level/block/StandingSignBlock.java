//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class StandingSignBlock extends SignBlock {
    public static final IntegerProperty ROTATION;

    public StandingSignBlock(BlockBehaviour.Properties p_56990_, WoodType p_56991_) {
        super(p_56990_.sound(p_56991_.soundType()), p_56991_);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(ROTATION, 0)).setValue(WATERLOGGED, false));
    }

    public boolean canSurvive(BlockState p_56995_, LevelReader p_56996_, BlockPos p_56997_) {
        return p_56996_.getBlockState(p_56997_.below()).isSolid();
    }

    public BlockState getStateForPlacement(BlockPlaceContext p_56993_) {
        FluidState $$1 = p_56993_.getLevel().getFluidState(p_56993_.getClickedPos());
        return (BlockState)((BlockState)this.defaultBlockState().setValue(ROTATION, RotationSegment.convertToSegment(p_56993_.getRotation() + 180.0F))).setValue(WATERLOGGED, $$1.getType() == Fluids.WATER);
    }

    public BlockState updateShape(BlockState p_57005_, Direction p_57006_, BlockState p_57007_, LevelAccessor p_57008_, BlockPos p_57009_, BlockPos p_57010_) {
        return p_57006_ == Direction.DOWN && !this.canSurvive(p_57005_, p_57008_, p_57009_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_57005_, p_57006_, p_57007_, p_57008_, p_57009_, p_57010_);
    }

    public float getYRotationDegrees(BlockState p_277795_) {
        return RotationSegment.convertToDegrees((Integer)p_277795_.getValue(ROTATION));
    }

    public BlockState rotate(BlockState p_57002_, Rotation p_57003_) {
        return (BlockState)p_57002_.setValue(ROTATION, p_57003_.rotate((Integer)p_57002_.getValue(ROTATION), 16));
    }

    public BlockState mirror(BlockState p_56999_, Mirror p_57000_) {
        return (BlockState)p_56999_.setValue(ROTATION, p_57000_.mirror((Integer)p_56999_.getValue(ROTATION), 16));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_57012_) {
        p_57012_.add(ROTATION, WATERLOGGED);
    }

    static {
        ROTATION = BlockStateProperties.ROTATION_16;
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BaseCoralPlantTypeBlock extends Block implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED;
    private static final VoxelShape AABB;

    public BaseCoralPlantTypeBlock(BlockBehaviour.Properties p_49161_) {
        super(p_49161_);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(WATERLOGGED, true));
    }

    protected void tryScheduleDieTick(BlockState p_49165_, LevelAccessor p_49166_, BlockPos p_49167_) {
        if (!scanForWater(p_49165_, p_49166_, p_49167_)) {
            p_49166_.scheduleTick(p_49167_, (Block)this, 60 + p_49166_.getRandom().nextInt(40));
        }

    }

    protected static boolean scanForWater(BlockState p_49187_, BlockGetter p_49188_, BlockPos p_49189_) {
        if ((Boolean)p_49187_.getValue(WATERLOGGED)) {
            return true;
        } else {
            Direction[] var3 = Direction.values();
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                Direction $$3 = var3[var5];
                if (p_49188_.getFluidState(p_49189_.relative($$3)).is(FluidTags.WATER)) {
                    return true;
                }
            }

            return false;
        }
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext p_49163_) {
        FluidState $$1 = p_49163_.getLevel().getFluidState(p_49163_.getClickedPos());
        return (BlockState)this.defaultBlockState().setValue(WATERLOGGED, $$1.is(FluidTags.WATER) && $$1.getAmount() == 8);
    }

    public VoxelShape getShape(BlockState p_49182_, BlockGetter p_49183_, BlockPos p_49184_, CollisionContext p_49185_) {
        return AABB;
    }

    public BlockState updateShape(BlockState p_49173_, Direction p_49174_, BlockState p_49175_, LevelAccessor p_49176_, BlockPos p_49177_, BlockPos p_49178_) {
        if ((Boolean)p_49173_.getValue(WATERLOGGED)) {
            p_49176_.scheduleTick(p_49177_, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(p_49176_));
        }

        return p_49174_ == Direction.DOWN && !this.canSurvive(p_49173_, p_49176_, p_49177_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_49173_, p_49174_, p_49175_, p_49176_, p_49177_, p_49178_);
    }

    public boolean canSurvive(BlockState p_49169_, LevelReader p_49170_, BlockPos p_49171_) {
        BlockPos $$3 = p_49171_.below();
        return p_49170_.getBlockState($$3).isFaceSturdy(p_49170_, $$3, Direction.UP);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49180_) {
        p_49180_.add(WATERLOGGED);
    }

    public FluidState getFluidState(BlockState p_49191_) {
        return (Boolean)p_49191_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_49191_);
    }

    static {
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
        AABB = Block.box(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);
    }
}

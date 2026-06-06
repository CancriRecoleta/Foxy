//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block;

import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class HugeMushroomBlock extends Block {
    public static final BooleanProperty NORTH;
    public static final BooleanProperty EAST;
    public static final BooleanProperty SOUTH;
    public static final BooleanProperty WEST;
    public static final BooleanProperty UP;
    public static final BooleanProperty DOWN;
    private static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION;

    public HugeMushroomBlock(BlockBehaviour.Properties p_54136_) {
        super(p_54136_);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(NORTH, true)).setValue(EAST, true)).setValue(SOUTH, true)).setValue(WEST, true)).setValue(UP, true)).setValue(DOWN, true));
    }

    public BlockState getStateForPlacement(BlockPlaceContext p_54138_) {
        BlockGetter $$1 = p_54138_.getLevel();
        BlockPos $$2 = p_54138_.getClickedPos();
        return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(DOWN, !$$1.getBlockState($$2.below()).is(this))).setValue(UP, !$$1.getBlockState($$2.above()).is(this))).setValue(NORTH, !$$1.getBlockState($$2.north()).is(this))).setValue(EAST, !$$1.getBlockState($$2.east()).is(this))).setValue(SOUTH, !$$1.getBlockState($$2.south()).is(this))).setValue(WEST, !$$1.getBlockState($$2.west()).is(this));
    }

    public BlockState updateShape(BlockState p_54146_, Direction p_54147_, BlockState p_54148_, LevelAccessor p_54149_, BlockPos p_54150_, BlockPos p_54151_) {
        return p_54148_.is(this) ? (BlockState)p_54146_.setValue((Property)PROPERTY_BY_DIRECTION.get(p_54147_), false) : super.updateShape(p_54146_, p_54147_, p_54148_, p_54149_, p_54150_, p_54151_);
    }

    public BlockState rotate(BlockState p_54143_, Rotation p_54144_) {
        return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)p_54143_.setValue((Property)PROPERTY_BY_DIRECTION.get(p_54144_.rotate(Direction.NORTH)), (Boolean)p_54143_.getValue(NORTH))).setValue((Property)PROPERTY_BY_DIRECTION.get(p_54144_.rotate(Direction.SOUTH)), (Boolean)p_54143_.getValue(SOUTH))).setValue((Property)PROPERTY_BY_DIRECTION.get(p_54144_.rotate(Direction.EAST)), (Boolean)p_54143_.getValue(EAST))).setValue((Property)PROPERTY_BY_DIRECTION.get(p_54144_.rotate(Direction.WEST)), (Boolean)p_54143_.getValue(WEST))).setValue((Property)PROPERTY_BY_DIRECTION.get(p_54144_.rotate(Direction.UP)), (Boolean)p_54143_.getValue(UP))).setValue((Property)PROPERTY_BY_DIRECTION.get(p_54144_.rotate(Direction.DOWN)), (Boolean)p_54143_.getValue(DOWN));
    }

    public BlockState mirror(BlockState p_54140_, Mirror p_54141_) {
        return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)p_54140_.setValue((Property)PROPERTY_BY_DIRECTION.get(p_54141_.mirror(Direction.NORTH)), (Boolean)p_54140_.getValue(NORTH))).setValue((Property)PROPERTY_BY_DIRECTION.get(p_54141_.mirror(Direction.SOUTH)), (Boolean)p_54140_.getValue(SOUTH))).setValue((Property)PROPERTY_BY_DIRECTION.get(p_54141_.mirror(Direction.EAST)), (Boolean)p_54140_.getValue(EAST))).setValue((Property)PROPERTY_BY_DIRECTION.get(p_54141_.mirror(Direction.WEST)), (Boolean)p_54140_.getValue(WEST))).setValue((Property)PROPERTY_BY_DIRECTION.get(p_54141_.mirror(Direction.UP)), (Boolean)p_54140_.getValue(UP))).setValue((Property)PROPERTY_BY_DIRECTION.get(p_54141_.mirror(Direction.DOWN)), (Boolean)p_54140_.getValue(DOWN));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_54153_) {
        p_54153_.add(UP, DOWN, NORTH, EAST, SOUTH, WEST);
    }

    static {
        NORTH = PipeBlock.NORTH;
        EAST = PipeBlock.EAST;
        SOUTH = PipeBlock.SOUTH;
        WEST = PipeBlock.WEST;
        UP = PipeBlock.UP;
        DOWN = PipeBlock.DOWN;
        PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION;
    }
}

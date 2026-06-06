//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block;

import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class RotatedPillarBlock extends Block {
    public static final EnumProperty<Direction.Axis> AXIS;

    public RotatedPillarBlock(BlockBehaviour.Properties p_55926_) {
        super(p_55926_);
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue(AXIS, Axis.Y));
    }

    public BlockState rotate(BlockState p_55930_, Rotation p_55931_) {
        return rotatePillar(p_55930_, p_55931_);
    }

    public static BlockState rotatePillar(BlockState p_154377_, Rotation p_154378_) {
        switch (p_154378_) {
            case COUNTERCLOCKWISE_90:
            case CLOCKWISE_90:
                switch ((Direction.Axis)p_154377_.getValue(AXIS)) {
                    case X -> return (BlockState)p_154377_.setValue(AXIS, Axis.Z);
                    case Z -> return (BlockState)p_154377_.setValue(AXIS, Axis.X);
                    default -> return p_154377_;
                }
            default:
                return p_154377_;
        }
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_55933_) {
        p_55933_.add(AXIS);
    }

    public BlockState getStateForPlacement(BlockPlaceContext p_55928_) {
        return (BlockState)this.defaultBlockState().setValue(AXIS, p_55928_.getClickedFace().getAxis());
    }

    static {
        AXIS = BlockStateProperties.AXIS;
    }
}

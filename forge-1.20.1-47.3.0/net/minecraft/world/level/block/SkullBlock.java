//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SkullBlock extends AbstractSkullBlock {
    public static final int MAX = RotationSegment.getMaxSegmentIndex();
    private static final int ROTATIONS;
    public static final IntegerProperty ROTATION;
    protected static final VoxelShape SHAPE;
    protected static final VoxelShape PIGLIN_SHAPE;

    public SkullBlock(Type p_56318_, BlockBehaviour.Properties p_56319_) {
        super(p_56318_, p_56319_);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(ROTATION, 0));
    }

    public VoxelShape getShape(BlockState p_56331_, BlockGetter p_56332_, BlockPos p_56333_, CollisionContext p_56334_) {
        return this.getType() == net.minecraft.world.level.block.SkullBlock.Types.PIGLIN ? PIGLIN_SHAPE : SHAPE;
    }

    public VoxelShape getOcclusionShape(BlockState p_56336_, BlockGetter p_56337_, BlockPos p_56338_) {
        return Shapes.empty();
    }

    public BlockState getStateForPlacement(BlockPlaceContext p_56321_) {
        return (BlockState)this.defaultBlockState().setValue(ROTATION, RotationSegment.convertToSegment(p_56321_.getRotation()));
    }

    public BlockState rotate(BlockState p_56326_, Rotation p_56327_) {
        return (BlockState)p_56326_.setValue(ROTATION, p_56327_.rotate((Integer)p_56326_.getValue(ROTATION), ROTATIONS));
    }

    public BlockState mirror(BlockState p_56323_, Mirror p_56324_) {
        return (BlockState)p_56323_.setValue(ROTATION, p_56324_.mirror((Integer)p_56323_.getValue(ROTATION), ROTATIONS));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_56329_) {
        p_56329_.add(ROTATION);
    }

    static {
        ROTATIONS = MAX + 1;
        ROTATION = BlockStateProperties.ROTATION_16;
        SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 8.0, 12.0);
        PIGLIN_SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 8.0, 13.0);
    }

    public interface Type {
    }

    public static enum Types implements Type {
        SKELETON,
        WITHER_SKELETON,
        PLAYER,
        ZOMBIE,
        CREEPER,
        PIGLIN,
        DRAGON;

        private Types() {
        }
    }
}

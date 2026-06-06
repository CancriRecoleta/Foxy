//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WallBannerBlock extends AbstractBannerBlock {
    public static final DirectionProperty FACING;
    private static final Map<Direction, VoxelShape> SHAPES;

    public WallBannerBlock(DyeColor p_57920_, BlockBehaviour.Properties p_57921_) {
        super(p_57920_, p_57921_);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH));
    }

    public String getDescriptionId() {
        return this.asItem().getDescriptionId();
    }

    public boolean canSurvive(BlockState p_57925_, LevelReader p_57926_, BlockPos p_57927_) {
        return p_57926_.getBlockState(p_57927_.relative(((Direction)p_57925_.getValue(FACING)).getOpposite())).isSolid();
    }

    public BlockState updateShape(BlockState p_57935_, Direction p_57936_, BlockState p_57937_, LevelAccessor p_57938_, BlockPos p_57939_, BlockPos p_57940_) {
        return p_57936_ == ((Direction)p_57935_.getValue(FACING)).getOpposite() && !p_57935_.canSurvive(p_57938_, p_57939_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_57935_, p_57936_, p_57937_, p_57938_, p_57939_, p_57940_);
    }

    public VoxelShape getShape(BlockState p_57944_, BlockGetter p_57945_, BlockPos p_57946_, CollisionContext p_57947_) {
        return (VoxelShape)SHAPES.get(p_57944_.getValue(FACING));
    }

    public BlockState getStateForPlacement(BlockPlaceContext p_57923_) {
        BlockState $$1 = this.defaultBlockState();
        LevelReader $$2 = p_57923_.getLevel();
        BlockPos $$3 = p_57923_.getClickedPos();
        Direction[] $$4 = p_57923_.getNearestLookingDirections();
        Direction[] var6 = $$4;
        int var7 = $$4.length;

        for(int var8 = 0; var8 < var7; ++var8) {
            Direction $$5 = var6[var8];
            if ($$5.getAxis().isHorizontal()) {
                Direction $$6 = $$5.getOpposite();
                $$1 = (BlockState)$$1.setValue(FACING, $$6);
                if ($$1.canSurvive($$2, $$3)) {
                    return $$1;
                }
            }
        }

        return null;
    }

    public BlockState rotate(BlockState p_57932_, Rotation p_57933_) {
        return (BlockState)p_57932_.setValue(FACING, p_57933_.rotate((Direction)p_57932_.getValue(FACING)));
    }

    public BlockState mirror(BlockState p_57929_, Mirror p_57930_) {
        return p_57929_.rotate(p_57930_.getRotation((Direction)p_57929_.getValue(FACING)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_57942_) {
        p_57942_.add(FACING);
    }

    static {
        FACING = HorizontalDirectionalBlock.FACING;
        SHAPES = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, Block.box(0.0, 0.0, 14.0, 16.0, 12.5, 16.0), Direction.SOUTH, Block.box(0.0, 0.0, 0.0, 16.0, 12.5, 2.0), Direction.WEST, Block.box(14.0, 0.0, 0.0, 16.0, 12.5, 16.0), Direction.EAST, Block.box(0.0, 0.0, 0.0, 2.0, 12.5, 16.0)));
    }
}

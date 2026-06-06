//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WallBlock extends Block implements SimpleWaterloggedBlock {
    public static final BooleanProperty UP;
    public static final EnumProperty<WallSide> EAST_WALL;
    public static final EnumProperty<WallSide> NORTH_WALL;
    public static final EnumProperty<WallSide> SOUTH_WALL;
    public static final EnumProperty<WallSide> WEST_WALL;
    public static final BooleanProperty WATERLOGGED;
    private final Map<BlockState, VoxelShape> shapeByIndex;
    private final Map<BlockState, VoxelShape> collisionShapeByIndex;
    private static final int WALL_WIDTH = 3;
    private static final int WALL_HEIGHT = 14;
    private static final int POST_WIDTH = 4;
    private static final int POST_COVER_WIDTH = 1;
    private static final int WALL_COVER_START = 7;
    private static final int WALL_COVER_END = 9;
    private static final VoxelShape POST_TEST;
    private static final VoxelShape NORTH_TEST;
    private static final VoxelShape SOUTH_TEST;
    private static final VoxelShape WEST_TEST;
    private static final VoxelShape EAST_TEST;

    public WallBlock(BlockBehaviour.Properties p_57964_) {
        super(p_57964_);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(UP, true)).setValue(NORTH_WALL, WallSide.NONE)).setValue(EAST_WALL, WallSide.NONE)).setValue(SOUTH_WALL, WallSide.NONE)).setValue(WEST_WALL, WallSide.NONE)).setValue(WATERLOGGED, false));
        this.shapeByIndex = this.makeShapes(4.0F, 3.0F, 16.0F, 0.0F, 14.0F, 16.0F);
        this.collisionShapeByIndex = this.makeShapes(4.0F, 3.0F, 24.0F, 0.0F, 24.0F, 24.0F);
    }

    private static VoxelShape applyWallShape(VoxelShape p_58034_, WallSide p_58035_, VoxelShape p_58036_, VoxelShape p_58037_) {
        if (p_58035_ == WallSide.TALL) {
            return Shapes.or(p_58034_, p_58037_);
        } else {
            return p_58035_ == WallSide.LOW ? Shapes.or(p_58034_, p_58036_) : p_58034_;
        }
    }

    private Map<BlockState, VoxelShape> makeShapes(float p_57966_, float p_57967_, float p_57968_, float p_57969_, float p_57970_, float p_57971_) {
        float $$6 = 8.0F - p_57966_;
        float $$7 = 8.0F + p_57966_;
        float $$8 = 8.0F - p_57967_;
        float $$9 = 8.0F + p_57967_;
        VoxelShape $$10 = Block.box((double)$$6, 0.0, (double)$$6, (double)$$7, (double)p_57968_, (double)$$7);
        VoxelShape $$11 = Block.box((double)$$8, (double)p_57969_, 0.0, (double)$$9, (double)p_57970_, (double)$$9);
        VoxelShape $$12 = Block.box((double)$$8, (double)p_57969_, (double)$$8, (double)$$9, (double)p_57970_, 16.0);
        VoxelShape $$13 = Block.box(0.0, (double)p_57969_, (double)$$8, (double)$$9, (double)p_57970_, (double)$$9);
        VoxelShape $$14 = Block.box((double)$$8, (double)p_57969_, (double)$$8, 16.0, (double)p_57970_, (double)$$9);
        VoxelShape $$15 = Block.box((double)$$8, (double)p_57969_, 0.0, (double)$$9, (double)p_57971_, (double)$$9);
        VoxelShape $$16 = Block.box((double)$$8, (double)p_57969_, (double)$$8, (double)$$9, (double)p_57971_, 16.0);
        VoxelShape $$17 = Block.box(0.0, (double)p_57969_, (double)$$8, (double)$$9, (double)p_57971_, (double)$$9);
        VoxelShape $$18 = Block.box((double)$$8, (double)p_57969_, (double)$$8, 16.0, (double)p_57971_, (double)$$9);
        ImmutableMap.Builder<BlockState, VoxelShape> $$19 = ImmutableMap.builder();
        Iterator var21 = UP.getPossibleValues().iterator();

        while(var21.hasNext()) {
            Boolean $$20 = (Boolean)var21.next();
            Iterator var23 = EAST_WALL.getPossibleValues().iterator();

            while(var23.hasNext()) {
                WallSide $$21 = (WallSide)var23.next();
                Iterator var25 = NORTH_WALL.getPossibleValues().iterator();

                while(var25.hasNext()) {
                    WallSide $$22 = (WallSide)var25.next();
                    Iterator var27 = WEST_WALL.getPossibleValues().iterator();

                    while(var27.hasNext()) {
                        WallSide $$23 = (WallSide)var27.next();
                        Iterator var29 = SOUTH_WALL.getPossibleValues().iterator();

                        while(var29.hasNext()) {
                            WallSide $$24 = (WallSide)var29.next();
                            VoxelShape $$25 = Shapes.empty();
                            $$25 = applyWallShape($$25, $$21, $$14, $$18);
                            $$25 = applyWallShape($$25, $$23, $$13, $$17);
                            $$25 = applyWallShape($$25, $$22, $$11, $$15);
                            $$25 = applyWallShape($$25, $$24, $$12, $$16);
                            if ($$20) {
                                $$25 = Shapes.or($$25, $$10);
                            }

                            BlockState $$26 = (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(UP, $$20)).setValue(EAST_WALL, $$21)).setValue(WEST_WALL, $$23)).setValue(NORTH_WALL, $$22)).setValue(SOUTH_WALL, $$24);
                            $$19.put((BlockState)$$26.setValue(WATERLOGGED, false), $$25);
                            $$19.put((BlockState)$$26.setValue(WATERLOGGED, true), $$25);
                        }
                    }
                }
            }
        }

        return $$19.build();
    }

    public VoxelShape getShape(BlockState p_58050_, BlockGetter p_58051_, BlockPos p_58052_, CollisionContext p_58053_) {
        return (VoxelShape)this.shapeByIndex.get(p_58050_);
    }

    public VoxelShape getCollisionShape(BlockState p_58055_, BlockGetter p_58056_, BlockPos p_58057_, CollisionContext p_58058_) {
        return (VoxelShape)this.collisionShapeByIndex.get(p_58055_);
    }

    public boolean isPathfindable(BlockState p_57996_, BlockGetter p_57997_, BlockPos p_57998_, PathComputationType p_57999_) {
        return false;
    }

    private boolean connectsTo(BlockState p_58021_, boolean p_58022_, Direction p_58023_) {
        Block $$3 = p_58021_.getBlock();
        boolean $$4 = $$3 instanceof FenceGateBlock && FenceGateBlock.connectsToDirection(p_58021_, p_58023_);
        return p_58021_.is(BlockTags.WALLS) || !isExceptionForConnection(p_58021_) && p_58022_ || $$3 instanceof IronBarsBlock || $$4;
    }

    public BlockState getStateForPlacement(BlockPlaceContext p_57973_) {
        LevelReader $$1 = p_57973_.getLevel();
        BlockPos $$2 = p_57973_.getClickedPos();
        FluidState $$3 = p_57973_.getLevel().getFluidState(p_57973_.getClickedPos());
        BlockPos $$4 = $$2.north();
        BlockPos $$5 = $$2.east();
        BlockPos $$6 = $$2.south();
        BlockPos $$7 = $$2.west();
        BlockPos $$8 = $$2.above();
        BlockState $$9 = $$1.getBlockState($$4);
        BlockState $$10 = $$1.getBlockState($$5);
        BlockState $$11 = $$1.getBlockState($$6);
        BlockState $$12 = $$1.getBlockState($$7);
        BlockState $$13 = $$1.getBlockState($$8);
        boolean $$14 = this.connectsTo($$9, $$9.isFaceSturdy($$1, $$4, Direction.SOUTH), Direction.SOUTH);
        boolean $$15 = this.connectsTo($$10, $$10.isFaceSturdy($$1, $$5, Direction.WEST), Direction.WEST);
        boolean $$16 = this.connectsTo($$11, $$11.isFaceSturdy($$1, $$6, Direction.NORTH), Direction.NORTH);
        boolean $$17 = this.connectsTo($$12, $$12.isFaceSturdy($$1, $$7, Direction.EAST), Direction.EAST);
        BlockState $$18 = (BlockState)this.defaultBlockState().setValue(WATERLOGGED, $$3.getType() == Fluids.WATER);
        return this.updateShape($$1, $$18, $$8, $$13, $$14, $$15, $$16, $$17);
    }

    public BlockState updateShape(BlockState p_58014_, Direction p_58015_, BlockState p_58016_, LevelAccessor p_58017_, BlockPos p_58018_, BlockPos p_58019_) {
        if ((Boolean)p_58014_.getValue(WATERLOGGED)) {
            p_58017_.scheduleTick(p_58018_, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(p_58017_));
        }

        if (p_58015_ == Direction.DOWN) {
            return super.updateShape(p_58014_, p_58015_, p_58016_, p_58017_, p_58018_, p_58019_);
        } else {
            return p_58015_ == Direction.UP ? this.topUpdate(p_58017_, p_58014_, p_58019_, p_58016_) : this.sideUpdate(p_58017_, p_58018_, p_58014_, p_58019_, p_58016_, p_58015_);
        }
    }

    private static boolean isConnected(BlockState p_58011_, Property<WallSide> p_58012_) {
        return p_58011_.getValue(p_58012_) != WallSide.NONE;
    }

    private static boolean isCovered(VoxelShape p_58039_, VoxelShape p_58040_) {
        return !Shapes.joinIsNotEmpty(p_58040_, p_58039_, BooleanOp.ONLY_FIRST);
    }

    private BlockState topUpdate(LevelReader p_57975_, BlockState p_57976_, BlockPos p_57977_, BlockState p_57978_) {
        boolean $$4 = isConnected(p_57976_, NORTH_WALL);
        boolean $$5 = isConnected(p_57976_, EAST_WALL);
        boolean $$6 = isConnected(p_57976_, SOUTH_WALL);
        boolean $$7 = isConnected(p_57976_, WEST_WALL);
        return this.updateShape(p_57975_, p_57976_, p_57977_, p_57978_, $$4, $$5, $$6, $$7);
    }

    private BlockState sideUpdate(LevelReader p_57989_, BlockPos p_57990_, BlockState p_57991_, BlockPos p_57992_, BlockState p_57993_, Direction p_57994_) {
        Direction $$6 = p_57994_.getOpposite();
        boolean $$7 = p_57994_ == Direction.NORTH ? this.connectsTo(p_57993_, p_57993_.isFaceSturdy(p_57989_, p_57992_, $$6), $$6) : isConnected(p_57991_, NORTH_WALL);
        boolean $$8 = p_57994_ == Direction.EAST ? this.connectsTo(p_57993_, p_57993_.isFaceSturdy(p_57989_, p_57992_, $$6), $$6) : isConnected(p_57991_, EAST_WALL);
        boolean $$9 = p_57994_ == Direction.SOUTH ? this.connectsTo(p_57993_, p_57993_.isFaceSturdy(p_57989_, p_57992_, $$6), $$6) : isConnected(p_57991_, SOUTH_WALL);
        boolean $$10 = p_57994_ == Direction.WEST ? this.connectsTo(p_57993_, p_57993_.isFaceSturdy(p_57989_, p_57992_, $$6), $$6) : isConnected(p_57991_, WEST_WALL);
        BlockPos $$11 = p_57990_.above();
        BlockState $$12 = p_57989_.getBlockState($$11);
        return this.updateShape(p_57989_, p_57991_, $$11, $$12, $$7, $$8, $$9, $$10);
    }

    private BlockState updateShape(LevelReader p_57980_, BlockState p_57981_, BlockPos p_57982_, BlockState p_57983_, boolean p_57984_, boolean p_57985_, boolean p_57986_, boolean p_57987_) {
        VoxelShape $$8 = p_57983_.getCollisionShape(p_57980_, p_57982_).getFaceShape(Direction.DOWN);
        BlockState $$9 = this.updateSides(p_57981_, p_57984_, p_57985_, p_57986_, p_57987_, $$8);
        return (BlockState)$$9.setValue(UP, this.shouldRaisePost($$9, p_57983_, $$8));
    }

    private boolean shouldRaisePost(BlockState p_58007_, BlockState p_58008_, VoxelShape p_58009_) {
        boolean $$3 = p_58008_.getBlock() instanceof WallBlock && (Boolean)p_58008_.getValue(UP);
        if ($$3) {
            return true;
        } else {
            WallSide $$4 = (WallSide)p_58007_.getValue(NORTH_WALL);
            WallSide $$5 = (WallSide)p_58007_.getValue(SOUTH_WALL);
            WallSide $$6 = (WallSide)p_58007_.getValue(EAST_WALL);
            WallSide $$7 = (WallSide)p_58007_.getValue(WEST_WALL);
            boolean $$8 = $$5 == WallSide.NONE;
            boolean $$9 = $$7 == WallSide.NONE;
            boolean $$10 = $$6 == WallSide.NONE;
            boolean $$11 = $$4 == WallSide.NONE;
            boolean $$12 = $$11 && $$8 && $$9 && $$10 || $$11 != $$8 || $$9 != $$10;
            if ($$12) {
                return true;
            } else {
                boolean $$13 = $$4 == WallSide.TALL && $$5 == WallSide.TALL || $$6 == WallSide.TALL && $$7 == WallSide.TALL;
                if ($$13) {
                    return false;
                } else {
                    return p_58008_.is(BlockTags.WALL_POST_OVERRIDE) || isCovered(p_58009_, POST_TEST);
                }
            }
        }
    }

    private BlockState updateSides(BlockState p_58025_, boolean p_58026_, boolean p_58027_, boolean p_58028_, boolean p_58029_, VoxelShape p_58030_) {
        return (BlockState)((BlockState)((BlockState)((BlockState)p_58025_.setValue(NORTH_WALL, this.makeWallState(p_58026_, p_58030_, NORTH_TEST))).setValue(EAST_WALL, this.makeWallState(p_58027_, p_58030_, EAST_TEST))).setValue(SOUTH_WALL, this.makeWallState(p_58028_, p_58030_, SOUTH_TEST))).setValue(WEST_WALL, this.makeWallState(p_58029_, p_58030_, WEST_TEST));
    }

    private WallSide makeWallState(boolean p_58042_, VoxelShape p_58043_, VoxelShape p_58044_) {
        if (p_58042_) {
            return isCovered(p_58043_, p_58044_) ? WallSide.TALL : WallSide.LOW;
        } else {
            return WallSide.NONE;
        }
    }

    public FluidState getFluidState(BlockState p_58060_) {
        return (Boolean)p_58060_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_58060_);
    }

    public boolean propagatesSkylightDown(BlockState p_58046_, BlockGetter p_58047_, BlockPos p_58048_) {
        return !(Boolean)p_58046_.getValue(WATERLOGGED);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_58032_) {
        p_58032_.add(UP, NORTH_WALL, EAST_WALL, WEST_WALL, SOUTH_WALL, WATERLOGGED);
    }

    public BlockState rotate(BlockState p_58004_, Rotation p_58005_) {
        switch (p_58005_) {
            case CLOCKWISE_180 -> return (BlockState)((BlockState)((BlockState)((BlockState)p_58004_.setValue(NORTH_WALL, (WallSide)p_58004_.getValue(SOUTH_WALL))).setValue(EAST_WALL, (WallSide)p_58004_.getValue(WEST_WALL))).setValue(SOUTH_WALL, (WallSide)p_58004_.getValue(NORTH_WALL))).setValue(WEST_WALL, (WallSide)p_58004_.getValue(EAST_WALL));
            case COUNTERCLOCKWISE_90 -> return (BlockState)((BlockState)((BlockState)((BlockState)p_58004_.setValue(NORTH_WALL, (WallSide)p_58004_.getValue(EAST_WALL))).setValue(EAST_WALL, (WallSide)p_58004_.getValue(SOUTH_WALL))).setValue(SOUTH_WALL, (WallSide)p_58004_.getValue(WEST_WALL))).setValue(WEST_WALL, (WallSide)p_58004_.getValue(NORTH_WALL));
            case CLOCKWISE_90 -> return (BlockState)((BlockState)((BlockState)((BlockState)p_58004_.setValue(NORTH_WALL, (WallSide)p_58004_.getValue(WEST_WALL))).setValue(EAST_WALL, (WallSide)p_58004_.getValue(NORTH_WALL))).setValue(SOUTH_WALL, (WallSide)p_58004_.getValue(EAST_WALL))).setValue(WEST_WALL, (WallSide)p_58004_.getValue(SOUTH_WALL));
            default -> return p_58004_;
        }
    }

    public BlockState mirror(BlockState p_58001_, Mirror p_58002_) {
        switch (p_58002_) {
            case LEFT_RIGHT -> return (BlockState)((BlockState)p_58001_.setValue(NORTH_WALL, (WallSide)p_58001_.getValue(SOUTH_WALL))).setValue(SOUTH_WALL, (WallSide)p_58001_.getValue(NORTH_WALL));
            case FRONT_BACK -> return (BlockState)((BlockState)p_58001_.setValue(EAST_WALL, (WallSide)p_58001_.getValue(WEST_WALL))).setValue(WEST_WALL, (WallSide)p_58001_.getValue(EAST_WALL));
            default -> return super.mirror(p_58001_, p_58002_);
        }
    }

    static {
        UP = BlockStateProperties.UP;
        EAST_WALL = BlockStateProperties.EAST_WALL;
        NORTH_WALL = BlockStateProperties.NORTH_WALL;
        SOUTH_WALL = BlockStateProperties.SOUTH_WALL;
        WEST_WALL = BlockStateProperties.WEST_WALL;
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
        POST_TEST = Block.box(7.0, 0.0, 7.0, 9.0, 16.0, 9.0);
        NORTH_TEST = Block.box(7.0, 0.0, 0.0, 9.0, 16.0, 9.0);
        SOUTH_TEST = Block.box(7.0, 0.0, 7.0, 9.0, 16.0, 16.0);
        WEST_TEST = Block.box(0.0, 0.0, 7.0, 9.0, 16.0, 9.0);
        EAST_TEST = Block.box(7.0, 0.0, 7.0, 16.0, 16.0, 9.0);
    }
}

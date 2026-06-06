//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block;

import com.google.common.collect.UnmodifiableIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CrossCollisionBlock extends Block implements SimpleWaterloggedBlock {
    public static final BooleanProperty NORTH;
    public static final BooleanProperty EAST;
    public static final BooleanProperty SOUTH;
    public static final BooleanProperty WEST;
    public static final BooleanProperty WATERLOGGED;
    protected static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION;
    protected final VoxelShape[] collisionShapeByIndex;
    protected final VoxelShape[] shapeByIndex;
    private final Object2IntMap<BlockState> stateToIndex = new Object2IntOpenHashMap();

    public CrossCollisionBlock(float p_52320_, float p_52321_, float p_52322_, float p_52323_, float p_52324_, BlockBehaviour.Properties p_52325_) {
        super(p_52325_);
        this.collisionShapeByIndex = this.makeShapes(p_52320_, p_52321_, p_52324_, 0.0F, p_52324_);
        this.shapeByIndex = this.makeShapes(p_52320_, p_52321_, p_52322_, 0.0F, p_52323_);
        UnmodifiableIterator var7 = this.stateDefinition.getPossibleStates().iterator();

        while(var7.hasNext()) {
            BlockState $$6 = (BlockState)var7.next();
            this.getAABBIndex($$6);
        }

    }

    protected VoxelShape[] makeShapes(float p_52327_, float p_52328_, float p_52329_, float p_52330_, float p_52331_) {
        float $$5 = 8.0F - p_52327_;
        float $$6 = 8.0F + p_52327_;
        float $$7 = 8.0F - p_52328_;
        float $$8 = 8.0F + p_52328_;
        VoxelShape $$9 = Block.box((double)$$5, 0.0, (double)$$5, (double)$$6, (double)p_52329_, (double)$$6);
        VoxelShape $$10 = Block.box((double)$$7, (double)p_52330_, 0.0, (double)$$8, (double)p_52331_, (double)$$8);
        VoxelShape $$11 = Block.box((double)$$7, (double)p_52330_, (double)$$7, (double)$$8, (double)p_52331_, 16.0);
        VoxelShape $$12 = Block.box(0.0, (double)p_52330_, (double)$$7, (double)$$8, (double)p_52331_, (double)$$8);
        VoxelShape $$13 = Block.box((double)$$7, (double)p_52330_, (double)$$7, 16.0, (double)p_52331_, (double)$$8);
        VoxelShape $$14 = Shapes.or($$10, $$13);
        VoxelShape $$15 = Shapes.or($$11, $$12);
        VoxelShape[] $$16 = new VoxelShape[]{Shapes.empty(), $$11, $$12, $$15, $$10, Shapes.or($$11, $$10), Shapes.or($$12, $$10), Shapes.or($$15, $$10), $$13, Shapes.or($$11, $$13), Shapes.or($$12, $$13), Shapes.or($$15, $$13), $$14, Shapes.or($$11, $$14), Shapes.or($$12, $$14), Shapes.or($$15, $$14)};

        for(int $$17 = 0; $$17 < 16; ++$$17) {
            $$16[$$17] = Shapes.or($$9, $$16[$$17]);
        }

        return $$16;
    }

    public boolean propagatesSkylightDown(BlockState p_52348_, BlockGetter p_52349_, BlockPos p_52350_) {
        return !(Boolean)p_52348_.getValue(WATERLOGGED);
    }

    public VoxelShape getShape(BlockState p_52352_, BlockGetter p_52353_, BlockPos p_52354_, CollisionContext p_52355_) {
        return this.shapeByIndex[this.getAABBIndex(p_52352_)];
    }

    public VoxelShape getCollisionShape(BlockState p_52357_, BlockGetter p_52358_, BlockPos p_52359_, CollisionContext p_52360_) {
        return this.collisionShapeByIndex[this.getAABBIndex(p_52357_)];
    }

    private static int indexFor(Direction p_52344_) {
        return 1 << p_52344_.get2DDataValue();
    }

    protected int getAABBIndex(BlockState p_52364_) {
        return this.stateToIndex.computeIntIfAbsent(p_52364_, (p_52366_) -> {
            int $$1 = 0;
            if ((Boolean)p_52366_.getValue(NORTH)) {
                $$1 |= indexFor(Direction.NORTH);
            }

            if ((Boolean)p_52366_.getValue(EAST)) {
                $$1 |= indexFor(Direction.EAST);
            }

            if ((Boolean)p_52366_.getValue(SOUTH)) {
                $$1 |= indexFor(Direction.SOUTH);
            }

            if ((Boolean)p_52366_.getValue(WEST)) {
                $$1 |= indexFor(Direction.WEST);
            }

            return $$1;
        });
    }

    public FluidState getFluidState(BlockState p_52362_) {
        return (Boolean)p_52362_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_52362_);
    }

    public boolean isPathfindable(BlockState p_52333_, BlockGetter p_52334_, BlockPos p_52335_, PathComputationType p_52336_) {
        return false;
    }

    public BlockState rotate(BlockState p_52341_, Rotation p_52342_) {
        switch (p_52342_) {
            case CLOCKWISE_180 -> return (BlockState)((BlockState)((BlockState)((BlockState)p_52341_.setValue(NORTH, (Boolean)p_52341_.getValue(SOUTH))).setValue(EAST, (Boolean)p_52341_.getValue(WEST))).setValue(SOUTH, (Boolean)p_52341_.getValue(NORTH))).setValue(WEST, (Boolean)p_52341_.getValue(EAST));
            case COUNTERCLOCKWISE_90 -> return (BlockState)((BlockState)((BlockState)((BlockState)p_52341_.setValue(NORTH, (Boolean)p_52341_.getValue(EAST))).setValue(EAST, (Boolean)p_52341_.getValue(SOUTH))).setValue(SOUTH, (Boolean)p_52341_.getValue(WEST))).setValue(WEST, (Boolean)p_52341_.getValue(NORTH));
            case CLOCKWISE_90 -> return (BlockState)((BlockState)((BlockState)((BlockState)p_52341_.setValue(NORTH, (Boolean)p_52341_.getValue(WEST))).setValue(EAST, (Boolean)p_52341_.getValue(NORTH))).setValue(SOUTH, (Boolean)p_52341_.getValue(EAST))).setValue(WEST, (Boolean)p_52341_.getValue(SOUTH));
            default -> return p_52341_;
        }
    }

    public BlockState mirror(BlockState p_52338_, Mirror p_52339_) {
        switch (p_52339_) {
            case LEFT_RIGHT -> return (BlockState)((BlockState)p_52338_.setValue(NORTH, (Boolean)p_52338_.getValue(SOUTH))).setValue(SOUTH, (Boolean)p_52338_.getValue(NORTH));
            case FRONT_BACK -> return (BlockState)((BlockState)p_52338_.setValue(EAST, (Boolean)p_52338_.getValue(WEST))).setValue(WEST, (Boolean)p_52338_.getValue(EAST));
            default -> return super.mirror(p_52338_, p_52339_);
        }
    }

    static {
        NORTH = PipeBlock.NORTH;
        EAST = PipeBlock.EAST;
        SOUTH = PipeBlock.SOUTH;
        WEST = PipeBlock.WEST;
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
        PROPERTY_BY_DIRECTION = (Map)PipeBlock.PROPERTY_BY_DIRECTION.entrySet().stream().filter((p_52346_) -> {
            return ((Direction)p_52346_.getKey()).getAxis().isHorizontal();
        }).collect(Util.toMap());
    }
}

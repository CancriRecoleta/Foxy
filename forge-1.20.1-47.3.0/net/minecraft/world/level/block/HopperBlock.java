//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.stats.Stats;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HopperBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING;
    public static final BooleanProperty ENABLED;
    private static final VoxelShape TOP;
    private static final VoxelShape FUNNEL;
    private static final VoxelShape CONVEX_BASE;
    private static final VoxelShape BASE;
    private static final VoxelShape DOWN_SHAPE;
    private static final VoxelShape EAST_SHAPE;
    private static final VoxelShape NORTH_SHAPE;
    private static final VoxelShape SOUTH_SHAPE;
    private static final VoxelShape WEST_SHAPE;
    private static final VoxelShape DOWN_INTERACTION_SHAPE;
    private static final VoxelShape EAST_INTERACTION_SHAPE;
    private static final VoxelShape NORTH_INTERACTION_SHAPE;
    private static final VoxelShape SOUTH_INTERACTION_SHAPE;
    private static final VoxelShape WEST_INTERACTION_SHAPE;

    public HopperBlock(BlockBehaviour.Properties p_54039_) {
        super(p_54039_);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.DOWN)).setValue(ENABLED, true));
    }

    public VoxelShape getShape(BlockState p_54105_, BlockGetter p_54106_, BlockPos p_54107_, CollisionContext p_54108_) {
        switch ((Direction)p_54105_.getValue(FACING)) {
            case DOWN -> return DOWN_SHAPE;
            case NORTH -> return NORTH_SHAPE;
            case SOUTH -> return SOUTH_SHAPE;
            case WEST -> return WEST_SHAPE;
            case EAST -> return EAST_SHAPE;
            default -> return BASE;
        }
    }

    public VoxelShape getInteractionShape(BlockState p_54099_, BlockGetter p_54100_, BlockPos p_54101_) {
        switch ((Direction)p_54099_.getValue(FACING)) {
            case DOWN -> return DOWN_INTERACTION_SHAPE;
            case NORTH -> return NORTH_INTERACTION_SHAPE;
            case SOUTH -> return SOUTH_INTERACTION_SHAPE;
            case WEST -> return WEST_INTERACTION_SHAPE;
            case EAST -> return EAST_INTERACTION_SHAPE;
            default -> return Hopper.INSIDE;
        }
    }

    public BlockState getStateForPlacement(BlockPlaceContext p_54041_) {
        Direction $$1 = p_54041_.getClickedFace().getOpposite();
        return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, $$1.getAxis() == Axis.Y ? Direction.DOWN : $$1)).setValue(ENABLED, true);
    }

    public BlockEntity newBlockEntity(BlockPos p_153382_, BlockState p_153383_) {
        return new HopperBlockEntity(p_153382_, p_153383_);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153378_, BlockState p_153379_, BlockEntityType<T> p_153380_) {
        return p_153378_.isClientSide ? null : createTickerHelper(p_153380_, BlockEntityType.HOPPER, HopperBlockEntity::pushItemsTick);
    }

    public void setPlacedBy(Level p_54049_, BlockPos p_54050_, BlockState p_54051_, LivingEntity p_54052_, ItemStack p_54053_) {
        if (p_54053_.hasCustomHoverName()) {
            BlockEntity $$5 = p_54049_.getBlockEntity(p_54050_);
            if ($$5 instanceof HopperBlockEntity) {
                ((HopperBlockEntity)$$5).setCustomName(p_54053_.getHoverName());
            }
        }

    }

    public void onPlace(BlockState p_54110_, Level p_54111_, BlockPos p_54112_, BlockState p_54113_, boolean p_54114_) {
        if (!p_54113_.is(p_54110_.getBlock())) {
            this.checkPoweredState(p_54111_, p_54112_, p_54110_, 2);
        }
    }

    public InteractionResult use(BlockState p_54071_, Level p_54072_, BlockPos p_54073_, Player p_54074_, InteractionHand p_54075_, BlockHitResult p_54076_) {
        if (p_54072_.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity $$6 = p_54072_.getBlockEntity(p_54073_);
            if ($$6 instanceof HopperBlockEntity) {
                p_54074_.openMenu((HopperBlockEntity)$$6);
                p_54074_.awardStat(Stats.INSPECT_HOPPER);
            }

            return InteractionResult.CONSUME;
        }
    }

    public void neighborChanged(BlockState p_54078_, Level p_54079_, BlockPos p_54080_, Block p_54081_, BlockPos p_54082_, boolean p_54083_) {
        this.checkPoweredState(p_54079_, p_54080_, p_54078_, 4);
    }

    private void checkPoweredState(Level p_275499_, BlockPos p_275298_, BlockState p_275611_, int p_275625_) {
        boolean $$4 = !p_275499_.hasNeighborSignal(p_275298_);
        if ($$4 != (Boolean)p_275611_.getValue(ENABLED)) {
            p_275499_.setBlock(p_275298_, (BlockState)p_275611_.setValue(ENABLED, $$4), p_275625_);
        }

    }

    public void onRemove(BlockState p_54085_, Level p_54086_, BlockPos p_54087_, BlockState p_54088_, boolean p_54089_) {
        if (!p_54085_.is(p_54088_.getBlock())) {
            BlockEntity $$5 = p_54086_.getBlockEntity(p_54087_);
            if ($$5 instanceof HopperBlockEntity) {
                Containers.dropContents(p_54086_, (BlockPos)p_54087_, (Container)((HopperBlockEntity)$$5));
                p_54086_.updateNeighbourForOutputSignal(p_54087_, this);
            }

            super.onRemove(p_54085_, p_54086_, p_54087_, p_54088_, p_54089_);
        }
    }

    public RenderShape getRenderShape(BlockState p_54103_) {
        return RenderShape.MODEL;
    }

    public boolean hasAnalogOutputSignal(BlockState p_54055_) {
        return true;
    }

    public int getAnalogOutputSignal(BlockState p_54062_, Level p_54063_, BlockPos p_54064_) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(p_54063_.getBlockEntity(p_54064_));
    }

    public BlockState rotate(BlockState p_54094_, Rotation p_54095_) {
        return (BlockState)p_54094_.setValue(FACING, p_54095_.rotate((Direction)p_54094_.getValue(FACING)));
    }

    public BlockState mirror(BlockState p_54091_, Mirror p_54092_) {
        return p_54091_.rotate(p_54092_.getRotation((Direction)p_54091_.getValue(FACING)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_54097_) {
        p_54097_.add(FACING, ENABLED);
    }

    public void entityInside(BlockState p_54066_, Level p_54067_, BlockPos p_54068_, Entity p_54069_) {
        BlockEntity $$4 = p_54067_.getBlockEntity(p_54068_);
        if ($$4 instanceof HopperBlockEntity) {
            HopperBlockEntity.entityInside(p_54067_, p_54068_, p_54066_, p_54069_, (HopperBlockEntity)$$4);
        }

    }

    public boolean isPathfindable(BlockState p_54057_, BlockGetter p_54058_, BlockPos p_54059_, PathComputationType p_54060_) {
        return false;
    }

    static {
        FACING = BlockStateProperties.FACING_HOPPER;
        ENABLED = BlockStateProperties.ENABLED;
        TOP = Block.box(0.0, 10.0, 0.0, 16.0, 16.0, 16.0);
        FUNNEL = Block.box(4.0, 4.0, 4.0, 12.0, 10.0, 12.0);
        CONVEX_BASE = Shapes.or(FUNNEL, TOP);
        BASE = Shapes.join(CONVEX_BASE, Hopper.INSIDE, BooleanOp.ONLY_FIRST);
        DOWN_SHAPE = Shapes.or(BASE, Block.box(6.0, 0.0, 6.0, 10.0, 4.0, 10.0));
        EAST_SHAPE = Shapes.or(BASE, Block.box(12.0, 4.0, 6.0, 16.0, 8.0, 10.0));
        NORTH_SHAPE = Shapes.or(BASE, Block.box(6.0, 4.0, 0.0, 10.0, 8.0, 4.0));
        SOUTH_SHAPE = Shapes.or(BASE, Block.box(6.0, 4.0, 12.0, 10.0, 8.0, 16.0));
        WEST_SHAPE = Shapes.or(BASE, Block.box(0.0, 4.0, 6.0, 4.0, 8.0, 10.0));
        DOWN_INTERACTION_SHAPE = Hopper.INSIDE;
        EAST_INTERACTION_SHAPE = Shapes.or(Hopper.INSIDE, Block.box(12.0, 8.0, 6.0, 16.0, 10.0, 10.0));
        NORTH_INTERACTION_SHAPE = Shapes.or(Hopper.INSIDE, Block.box(6.0, 8.0, 0.0, 10.0, 10.0, 4.0));
        SOUTH_INTERACTION_SHAPE = Shapes.or(Hopper.INSIDE, Block.box(6.0, 8.0, 12.0, 10.0, 10.0, 16.0));
        WEST_INTERACTION_SHAPE = Shapes.or(Hopper.INSIDE, Block.box(0.0, 8.0, 6.0, 4.0, 10.0, 10.0));
    }
}

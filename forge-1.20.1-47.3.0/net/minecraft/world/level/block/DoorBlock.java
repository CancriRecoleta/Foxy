//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DoorBlock extends Block {
    public static final DirectionProperty FACING;
    public static final BooleanProperty OPEN;
    public static final EnumProperty<DoorHingeSide> HINGE;
    public static final BooleanProperty POWERED;
    public static final EnumProperty<DoubleBlockHalf> HALF;
    protected static final float AABB_DOOR_THICKNESS = 3.0F;
    protected static final VoxelShape SOUTH_AABB;
    protected static final VoxelShape NORTH_AABB;
    protected static final VoxelShape WEST_AABB;
    protected static final VoxelShape EAST_AABB;
    private final BlockSetType type;

    public DoorBlock(BlockBehaviour.Properties p_273303_, BlockSetType p_272854_) {
        super(p_273303_.sound(p_272854_.soundType()));
        this.type = p_272854_;
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(OPEN, false)).setValue(HINGE, DoorHingeSide.LEFT)).setValue(POWERED, false)).setValue(HALF, DoubleBlockHalf.LOWER));
    }

    public BlockSetType type() {
        return this.type;
    }

    public VoxelShape getShape(BlockState p_52807_, BlockGetter p_52808_, BlockPos p_52809_, CollisionContext p_52810_) {
        Direction $$4 = (Direction)p_52807_.getValue(FACING);
        boolean $$5 = !(Boolean)p_52807_.getValue(OPEN);
        boolean $$6 = p_52807_.getValue(HINGE) == DoorHingeSide.RIGHT;
        switch ($$4) {
            case EAST:
            default:
                return $$5 ? EAST_AABB : ($$6 ? NORTH_AABB : SOUTH_AABB);
            case SOUTH:
                return $$5 ? SOUTH_AABB : ($$6 ? EAST_AABB : WEST_AABB);
            case WEST:
                return $$5 ? WEST_AABB : ($$6 ? SOUTH_AABB : NORTH_AABB);
            case NORTH:
                return $$5 ? NORTH_AABB : ($$6 ? WEST_AABB : EAST_AABB);
        }
    }

    public BlockState updateShape(BlockState p_52796_, Direction p_52797_, BlockState p_52798_, LevelAccessor p_52799_, BlockPos p_52800_, BlockPos p_52801_) {
        DoubleBlockHalf $$6 = (DoubleBlockHalf)p_52796_.getValue(HALF);
        if (p_52797_.getAxis() == Axis.Y && $$6 == DoubleBlockHalf.LOWER == (p_52797_ == Direction.UP)) {
            return p_52798_.is(this) && p_52798_.getValue(HALF) != $$6 ? (BlockState)((BlockState)((BlockState)((BlockState)p_52796_.setValue(FACING, (Direction)p_52798_.getValue(FACING))).setValue(OPEN, (Boolean)p_52798_.getValue(OPEN))).setValue(HINGE, (DoorHingeSide)p_52798_.getValue(HINGE))).setValue(POWERED, (Boolean)p_52798_.getValue(POWERED)) : Blocks.AIR.defaultBlockState();
        } else {
            return $$6 == DoubleBlockHalf.LOWER && p_52797_ == Direction.DOWN && !p_52796_.canSurvive(p_52799_, p_52800_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_52796_, p_52797_, p_52798_, p_52799_, p_52800_, p_52801_);
        }
    }

    public void playerWillDestroy(Level p_52755_, BlockPos p_52756_, BlockState p_52757_, Player p_52758_) {
        if (!p_52755_.isClientSide && p_52758_.isCreative()) {
            DoublePlantBlock.preventCreativeDropFromBottomPart(p_52755_, p_52756_, p_52757_, p_52758_);
        }

        super.playerWillDestroy(p_52755_, p_52756_, p_52757_, p_52758_);
    }

    public boolean isPathfindable(BlockState p_52764_, BlockGetter p_52765_, BlockPos p_52766_, PathComputationType p_52767_) {
        switch (p_52767_) {
            case LAND -> return (Boolean)p_52764_.getValue(OPEN);
            case WATER -> return false;
            case AIR -> return (Boolean)p_52764_.getValue(OPEN);
            default -> return false;
        }
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext p_52739_) {
        BlockPos $$1 = p_52739_.getClickedPos();
        Level $$2 = p_52739_.getLevel();
        if ($$1.getY() < $$2.getMaxBuildHeight() - 1 && $$2.getBlockState($$1.above()).canBeReplaced(p_52739_)) {
            boolean $$3 = $$2.hasNeighborSignal($$1) || $$2.hasNeighborSignal($$1.above());
            return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(FACING, p_52739_.getHorizontalDirection())).setValue(HINGE, this.getHinge(p_52739_))).setValue(POWERED, $$3)).setValue(OPEN, $$3)).setValue(HALF, DoubleBlockHalf.LOWER);
        } else {
            return null;
        }
    }

    public void setPlacedBy(Level p_52749_, BlockPos p_52750_, BlockState p_52751_, LivingEntity p_52752_, ItemStack p_52753_) {
        p_52749_.setBlock(p_52750_.above(), (BlockState)p_52751_.setValue(HALF, DoubleBlockHalf.UPPER), 3);
    }

    private DoorHingeSide getHinge(BlockPlaceContext p_52805_) {
        BlockGetter $$1 = p_52805_.getLevel();
        BlockPos $$2 = p_52805_.getClickedPos();
        Direction $$3 = p_52805_.getHorizontalDirection();
        BlockPos $$4 = $$2.above();
        Direction $$5 = $$3.getCounterClockWise();
        BlockPos $$6 = $$2.relative($$5);
        BlockState $$7 = $$1.getBlockState($$6);
        BlockPos $$8 = $$4.relative($$5);
        BlockState $$9 = $$1.getBlockState($$8);
        Direction $$10 = $$3.getClockWise();
        BlockPos $$11 = $$2.relative($$10);
        BlockState $$12 = $$1.getBlockState($$11);
        BlockPos $$13 = $$4.relative($$10);
        BlockState $$14 = $$1.getBlockState($$13);
        int $$15 = ($$7.isCollisionShapeFullBlock($$1, $$6) ? -1 : 0) + ($$9.isCollisionShapeFullBlock($$1, $$8) ? -1 : 0) + ($$12.isCollisionShapeFullBlock($$1, $$11) ? 1 : 0) + ($$14.isCollisionShapeFullBlock($$1, $$13) ? 1 : 0);
        boolean $$16 = $$7.is(this) && $$7.getValue(HALF) == DoubleBlockHalf.LOWER;
        boolean $$17 = $$12.is(this) && $$12.getValue(HALF) == DoubleBlockHalf.LOWER;
        if ((!$$16 || $$17) && $$15 <= 0) {
            if ((!$$17 || $$16) && $$15 >= 0) {
                int $$18 = $$3.getStepX();
                int $$19 = $$3.getStepZ();
                Vec3 $$20 = p_52805_.getClickLocation();
                double $$21 = $$20.x - (double)$$2.getX();
                double $$22 = $$20.z - (double)$$2.getZ();
                return ($$18 >= 0 || !($$22 < 0.5)) && ($$18 <= 0 || !($$22 > 0.5)) && ($$19 >= 0 || !($$21 > 0.5)) && ($$19 <= 0 || !($$21 < 0.5)) ? DoorHingeSide.LEFT : DoorHingeSide.RIGHT;
            } else {
                return DoorHingeSide.LEFT;
            }
        } else {
            return DoorHingeSide.RIGHT;
        }
    }

    public InteractionResult use(BlockState p_52769_, Level p_52770_, BlockPos p_52771_, Player p_52772_, InteractionHand p_52773_, BlockHitResult p_52774_) {
        if (!this.type.canOpenByHand()) {
            return InteractionResult.PASS;
        } else {
            p_52769_ = (BlockState)p_52769_.cycle(OPEN);
            p_52770_.setBlock(p_52771_, p_52769_, 10);
            this.playSound(p_52772_, p_52770_, p_52771_, (Boolean)p_52769_.getValue(OPEN));
            p_52770_.gameEvent(p_52772_, this.isOpen(p_52769_) ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, p_52771_);
            return InteractionResult.sidedSuccess(p_52770_.isClientSide);
        }
    }

    public boolean isOpen(BlockState p_52816_) {
        return (Boolean)p_52816_.getValue(OPEN);
    }

    public void setOpen(@Nullable Entity p_153166_, Level p_153167_, BlockState p_153168_, BlockPos p_153169_, boolean p_153170_) {
        if (p_153168_.is(this) && (Boolean)p_153168_.getValue(OPEN) != p_153170_) {
            p_153167_.setBlock(p_153169_, (BlockState)p_153168_.setValue(OPEN, p_153170_), 10);
            this.playSound(p_153166_, p_153167_, p_153169_, p_153170_);
            p_153167_.gameEvent(p_153166_, p_153170_ ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, p_153169_);
        }
    }

    public void neighborChanged(BlockState p_52776_, Level p_52777_, BlockPos p_52778_, Block p_52779_, BlockPos p_52780_, boolean p_52781_) {
        boolean $$6 = p_52777_.hasNeighborSignal(p_52778_) || p_52777_.hasNeighborSignal(p_52778_.relative(p_52776_.getValue(HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN));
        if (!this.defaultBlockState().is(p_52779_) && $$6 != (Boolean)p_52776_.getValue(POWERED)) {
            if ($$6 != (Boolean)p_52776_.getValue(OPEN)) {
                this.playSound((Entity)null, p_52777_, p_52778_, $$6);
                p_52777_.gameEvent((Entity)null, $$6 ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, p_52778_);
            }

            p_52777_.setBlock(p_52778_, (BlockState)((BlockState)p_52776_.setValue(POWERED, $$6)).setValue(OPEN, $$6), 2);
        }

    }

    public boolean canSurvive(BlockState p_52783_, LevelReader p_52784_, BlockPos p_52785_) {
        BlockPos $$3 = p_52785_.below();
        BlockState $$4 = p_52784_.getBlockState($$3);
        return p_52783_.getValue(HALF) == DoubleBlockHalf.LOWER ? $$4.isFaceSturdy(p_52784_, $$3, Direction.UP) : $$4.is(this);
    }

    private void playSound(@Nullable Entity p_251616_, Level p_249656_, BlockPos p_249439_, boolean p_251628_) {
        p_249656_.playSound(p_251616_, p_249439_, p_251628_ ? this.type.doorOpen() : this.type.doorClose(), SoundSource.BLOCKS, 1.0F, p_249656_.getRandom().nextFloat() * 0.1F + 0.9F);
    }

    public BlockState rotate(BlockState p_52790_, Rotation p_52791_) {
        return (BlockState)p_52790_.setValue(FACING, p_52791_.rotate((Direction)p_52790_.getValue(FACING)));
    }

    public BlockState mirror(BlockState p_52787_, Mirror p_52788_) {
        return p_52788_ == Mirror.NONE ? p_52787_ : (BlockState)p_52787_.rotate(p_52788_.getRotation((Direction)p_52787_.getValue(FACING))).cycle(HINGE);
    }

    public long getSeed(BlockState p_52793_, BlockPos p_52794_) {
        return Mth.getSeed(p_52794_.getX(), p_52794_.below(p_52793_.getValue(HALF) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), p_52794_.getZ());
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_52803_) {
        p_52803_.add(HALF, FACING, OPEN, HINGE, POWERED);
    }

    public static boolean isWoodenDoor(Level p_52746_, BlockPos p_52747_) {
        return isWoodenDoor(p_52746_.getBlockState(p_52747_));
    }

    public static boolean isWoodenDoor(BlockState p_52818_) {
        Block var2 = p_52818_.getBlock();
        boolean var10000;
        if (var2 instanceof DoorBlock $$1) {
            if ($$1.type().canOpenByHand()) {
                var10000 = true;
                return var10000;
            }
        }

        var10000 = false;
        return var10000;
    }

    static {
        FACING = HorizontalDirectionalBlock.FACING;
        OPEN = BlockStateProperties.OPEN;
        HINGE = BlockStateProperties.DOOR_HINGE;
        POWERED = BlockStateProperties.POWERED;
        HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
        SOUTH_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
        NORTH_AABB = Block.box(0.0, 0.0, 13.0, 16.0, 16.0, 16.0);
        WEST_AABB = Block.box(13.0, 0.0, 0.0, 16.0, 16.0, 16.0);
        EAST_AABB = Block.box(0.0, 0.0, 0.0, 3.0, 16.0, 16.0);
    }
}

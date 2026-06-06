//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TrapDoorBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock {
    public static final BooleanProperty OPEN;
    public static final EnumProperty<Half> HALF;
    public static final BooleanProperty POWERED;
    public static final BooleanProperty WATERLOGGED;
    protected static final int AABB_THICKNESS = 3;
    protected static final VoxelShape EAST_OPEN_AABB;
    protected static final VoxelShape WEST_OPEN_AABB;
    protected static final VoxelShape SOUTH_OPEN_AABB;
    protected static final VoxelShape NORTH_OPEN_AABB;
    protected static final VoxelShape BOTTOM_AABB;
    protected static final VoxelShape TOP_AABB;
    private final BlockSetType type;

    public TrapDoorBlock(BlockBehaviour.Properties p_273079_, BlockSetType p_272964_) {
        super(p_273079_.sound(p_272964_.soundType()));
        this.type = p_272964_;
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(OPEN, false)).setValue(HALF, Half.BOTTOM)).setValue(POWERED, false)).setValue(WATERLOGGED, false));
    }

    public VoxelShape getShape(BlockState p_57563_, BlockGetter p_57564_, BlockPos p_57565_, CollisionContext p_57566_) {
        if (!(Boolean)p_57563_.getValue(OPEN)) {
            return p_57563_.getValue(HALF) == Half.TOP ? TOP_AABB : BOTTOM_AABB;
        } else {
            switch ((Direction)p_57563_.getValue(FACING)) {
                case NORTH:
                default:
                    return NORTH_OPEN_AABB;
                case SOUTH:
                    return SOUTH_OPEN_AABB;
                case WEST:
                    return WEST_OPEN_AABB;
                case EAST:
                    return EAST_OPEN_AABB;
            }
        }
    }

    public boolean isPathfindable(BlockState p_57535_, BlockGetter p_57536_, BlockPos p_57537_, PathComputationType p_57538_) {
        switch (p_57538_) {
            case LAND -> return (Boolean)p_57535_.getValue(OPEN);
            case WATER -> return (Boolean)p_57535_.getValue(WATERLOGGED);
            case AIR -> return (Boolean)p_57535_.getValue(OPEN);
            default -> return false;
        }
    }

    public InteractionResult use(BlockState p_57540_, Level p_57541_, BlockPos p_57542_, Player p_57543_, InteractionHand p_57544_, BlockHitResult p_57545_) {
        if (!this.type.canOpenByHand()) {
            return InteractionResult.PASS;
        } else {
            p_57540_ = (BlockState)p_57540_.cycle(OPEN);
            p_57541_.setBlock(p_57542_, p_57540_, 2);
            if ((Boolean)p_57540_.getValue(WATERLOGGED)) {
                p_57541_.scheduleTick(p_57542_, Fluids.WATER, Fluids.WATER.getTickDelay(p_57541_));
            }

            this.playSound(p_57543_, p_57541_, p_57542_, (Boolean)p_57540_.getValue(OPEN));
            return InteractionResult.sidedSuccess(p_57541_.isClientSide);
        }
    }

    protected void playSound(@Nullable Player p_57528_, Level p_57529_, BlockPos p_57530_, boolean p_57531_) {
        p_57529_.playSound(p_57528_, p_57530_, p_57531_ ? this.type.trapdoorOpen() : this.type.trapdoorClose(), SoundSource.BLOCKS, 1.0F, p_57529_.getRandom().nextFloat() * 0.1F + 0.9F);
        p_57529_.gameEvent(p_57528_, p_57531_ ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, p_57530_);
    }

    public void neighborChanged(BlockState p_57547_, Level p_57548_, BlockPos p_57549_, Block p_57550_, BlockPos p_57551_, boolean p_57552_) {
        if (!p_57548_.isClientSide) {
            boolean flag = p_57548_.hasNeighborSignal(p_57549_);
            if (flag != (Boolean)p_57547_.getValue(POWERED)) {
                if ((Boolean)p_57547_.getValue(OPEN) != flag) {
                    p_57547_ = (BlockState)p_57547_.setValue(OPEN, flag);
                    this.playSound((Player)null, p_57548_, p_57549_, flag);
                }

                p_57548_.setBlock(p_57549_, (BlockState)p_57547_.setValue(POWERED, flag), 2);
                if ((Boolean)p_57547_.getValue(WATERLOGGED)) {
                    p_57548_.scheduleTick(p_57549_, Fluids.WATER, Fluids.WATER.getTickDelay(p_57548_));
                }
            }
        }

    }

    public BlockState getStateForPlacement(BlockPlaceContext p_57533_) {
        BlockState blockstate = this.defaultBlockState();
        FluidState fluidstate = p_57533_.getLevel().getFluidState(p_57533_.getClickedPos());
        Direction direction = p_57533_.getClickedFace();
        if (!p_57533_.replacingClickedOnBlock() && direction.getAxis().isHorizontal()) {
            blockstate = (BlockState)((BlockState)blockstate.setValue(FACING, direction)).setValue(HALF, p_57533_.getClickLocation().y - (double)p_57533_.getClickedPos().getY() > 0.5 ? Half.TOP : Half.BOTTOM);
        } else {
            blockstate = (BlockState)((BlockState)blockstate.setValue(FACING, p_57533_.getHorizontalDirection().getOpposite())).setValue(HALF, direction == Direction.UP ? Half.BOTTOM : Half.TOP);
        }

        if (p_57533_.getLevel().hasNeighborSignal(p_57533_.getClickedPos())) {
            blockstate = (BlockState)((BlockState)blockstate.setValue(OPEN, true)).setValue(POWERED, true);
        }

        return (BlockState)blockstate.setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_57561_) {
        p_57561_.add(FACING, OPEN, HALF, POWERED, WATERLOGGED);
    }

    public FluidState getFluidState(BlockState p_57568_) {
        return (Boolean)p_57568_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_57568_);
    }

    public BlockState updateShape(BlockState p_57554_, Direction p_57555_, BlockState p_57556_, LevelAccessor p_57557_, BlockPos p_57558_, BlockPos p_57559_) {
        if ((Boolean)p_57554_.getValue(WATERLOGGED)) {
            p_57557_.scheduleTick(p_57558_, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(p_57557_));
        }

        return super.updateShape(p_57554_, p_57555_, p_57556_, p_57557_, p_57558_, p_57559_);
    }

    public boolean isLadder(BlockState state, LevelReader world, BlockPos pos, LivingEntity entity) {
        if ((Boolean)state.getValue(OPEN)) {
            BlockPos downPos = pos.below();
            BlockState down = world.getBlockState(downPos);
            return down.getBlock().makesOpenTrapdoorAboveClimbable(down, world, downPos, state);
        } else {
            return false;
        }
    }

    static {
        OPEN = BlockStateProperties.OPEN;
        HALF = BlockStateProperties.HALF;
        POWERED = BlockStateProperties.POWERED;
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
        EAST_OPEN_AABB = Block.box(0.0, 0.0, 0.0, 3.0, 16.0, 16.0);
        WEST_OPEN_AABB = Block.box(13.0, 0.0, 0.0, 16.0, 16.0, 16.0);
        SOUTH_OPEN_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
        NORTH_OPEN_AABB = Block.box(0.0, 0.0, 13.0, 16.0, 16.0, 16.0);
        BOTTOM_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 3.0, 16.0);
        TOP_AABB = Block.box(0.0, 13.0, 0.0, 16.0, 16.0, 16.0);
    }
}

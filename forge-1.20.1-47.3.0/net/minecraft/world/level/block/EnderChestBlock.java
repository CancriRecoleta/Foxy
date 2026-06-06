//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EnderChestBlock extends AbstractChestBlock<EnderChestBlockEntity> implements SimpleWaterloggedBlock {
    public static final DirectionProperty FACING;
    public static final BooleanProperty WATERLOGGED;
    protected static final VoxelShape SHAPE;
    private static final Component CONTAINER_TITLE;

    public EnderChestBlock(BlockBehaviour.Properties p_53121_) {
        super(p_53121_, () -> {
            return BlockEntityType.ENDER_CHEST;
        });
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(WATERLOGGED, false));
    }

    public DoubleBlockCombiner.NeighborCombineResult<? extends ChestBlockEntity> combine(BlockState p_53149_, Level p_53150_, BlockPos p_53151_, boolean p_53152_) {
        return DoubleBlockCombiner.Combiner::acceptNone;
    }

    public VoxelShape getShape(BlockState p_53171_, BlockGetter p_53172_, BlockPos p_53173_, CollisionContext p_53174_) {
        return SHAPE;
    }

    public RenderShape getRenderShape(BlockState p_53169_) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    public BlockState getStateForPlacement(BlockPlaceContext p_53128_) {
        FluidState $$1 = p_53128_.getLevel().getFluidState(p_53128_.getClickedPos());
        return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, p_53128_.getHorizontalDirection().getOpposite())).setValue(WATERLOGGED, $$1.getType() == Fluids.WATER);
    }

    public InteractionResult use(BlockState p_53137_, Level p_53138_, BlockPos p_53139_, Player p_53140_, InteractionHand p_53141_, BlockHitResult p_53142_) {
        PlayerEnderChestContainer $$6 = p_53140_.getEnderChestInventory();
        BlockEntity $$7 = p_53138_.getBlockEntity(p_53139_);
        if ($$6 != null && $$7 instanceof EnderChestBlockEntity) {
            BlockPos $$8 = p_53139_.above();
            if (p_53138_.getBlockState($$8).isRedstoneConductor(p_53138_, $$8)) {
                return InteractionResult.sidedSuccess(p_53138_.isClientSide);
            } else if (p_53138_.isClientSide) {
                return InteractionResult.SUCCESS;
            } else {
                EnderChestBlockEntity $$9 = (EnderChestBlockEntity)$$7;
                $$6.setActiveChest($$9);
                p_53140_.openMenu(new SimpleMenuProvider((p_53124_, p_53125_, p_53126_) -> {
                    return ChestMenu.threeRows(p_53124_, p_53125_, $$6);
                }, CONTAINER_TITLE));
                p_53140_.awardStat(Stats.OPEN_ENDERCHEST);
                PiglinAi.angerNearbyPiglins(p_53140_, true);
                return InteractionResult.CONSUME;
            }
        } else {
            return InteractionResult.sidedSuccess(p_53138_.isClientSide);
        }
    }

    public BlockEntity newBlockEntity(BlockPos p_153208_, BlockState p_153209_) {
        return new EnderChestBlockEntity(p_153208_, p_153209_);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153199_, BlockState p_153200_, BlockEntityType<T> p_153201_) {
        return p_153199_.isClientSide ? createTickerHelper(p_153201_, BlockEntityType.ENDER_CHEST, EnderChestBlockEntity::lidAnimateTick) : null;
    }

    public void animateTick(BlockState p_221117_, Level p_221118_, BlockPos p_221119_, RandomSource p_221120_) {
        for(int $$4 = 0; $$4 < 3; ++$$4) {
            int $$5 = p_221120_.nextInt(2) * 2 - 1;
            int $$6 = p_221120_.nextInt(2) * 2 - 1;
            double $$7 = (double)p_221119_.getX() + 0.5 + 0.25 * (double)$$5;
            double $$8 = (double)((float)p_221119_.getY() + p_221120_.nextFloat());
            double $$9 = (double)p_221119_.getZ() + 0.5 + 0.25 * (double)$$6;
            double $$10 = (double)(p_221120_.nextFloat() * (float)$$5);
            double $$11 = ((double)p_221120_.nextFloat() - 0.5) * 0.125;
            double $$12 = (double)(p_221120_.nextFloat() * (float)$$6);
            p_221118_.addParticle(ParticleTypes.PORTAL, $$7, $$8, $$9, $$10, $$11, $$12);
        }

    }

    public BlockState rotate(BlockState p_53157_, Rotation p_53158_) {
        return (BlockState)p_53157_.setValue(FACING, p_53158_.rotate((Direction)p_53157_.getValue(FACING)));
    }

    public BlockState mirror(BlockState p_53154_, Mirror p_53155_) {
        return p_53154_.rotate(p_53155_.getRotation((Direction)p_53154_.getValue(FACING)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_53167_) {
        p_53167_.add(FACING, WATERLOGGED);
    }

    public FluidState getFluidState(BlockState p_53177_) {
        return (Boolean)p_53177_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_53177_);
    }

    public BlockState updateShape(BlockState p_53160_, Direction p_53161_, BlockState p_53162_, LevelAccessor p_53163_, BlockPos p_53164_, BlockPos p_53165_) {
        if ((Boolean)p_53160_.getValue(WATERLOGGED)) {
            p_53163_.scheduleTick(p_53164_, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(p_53163_));
        }

        return super.updateShape(p_53160_, p_53161_, p_53162_, p_53163_, p_53164_, p_53165_);
    }

    public boolean isPathfindable(BlockState p_53132_, BlockGetter p_53133_, BlockPos p_53134_, PathComputationType p_53135_) {
        return false;
    }

    public void tick(BlockState p_221112_, ServerLevel p_221113_, BlockPos p_221114_, RandomSource p_221115_) {
        BlockEntity $$4 = p_221113_.getBlockEntity(p_221114_);
        if ($$4 instanceof EnderChestBlockEntity) {
            ((EnderChestBlockEntity)$$4).recheckOpen();
        }

    }

    static {
        FACING = HorizontalDirectionalBlock.FACING;
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
        SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);
        CONTAINER_TITLE = Component.translatable("container.enderchest");
    }
}

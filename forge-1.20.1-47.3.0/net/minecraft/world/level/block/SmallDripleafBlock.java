//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SmallDripleafBlock extends DoublePlantBlock implements BonemealableBlock, SimpleWaterloggedBlock {
    private static final BooleanProperty WATERLOGGED;
    public static final DirectionProperty FACING;
    protected static final float AABB_OFFSET = 6.0F;
    protected static final VoxelShape SHAPE;

    public SmallDripleafBlock(BlockBehaviour.Properties p_154583_) {
        super(p_154583_);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(HALF, DoubleBlockHalf.LOWER)).setValue(WATERLOGGED, false)).setValue(FACING, Direction.NORTH));
    }

    public VoxelShape getShape(BlockState p_154610_, BlockGetter p_154611_, BlockPos p_154612_, CollisionContext p_154613_) {
        return SHAPE;
    }

    protected boolean mayPlaceOn(BlockState p_154636_, BlockGetter p_154637_, BlockPos p_154638_) {
        return p_154636_.is(BlockTags.SMALL_DRIPLEAF_PLACEABLE) || p_154637_.getFluidState(p_154638_.above()).isSourceOfType(Fluids.WATER) && super.mayPlaceOn(p_154636_, p_154637_, p_154638_);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext p_154592_) {
        BlockState $$1 = super.getStateForPlacement(p_154592_);
        return $$1 != null ? copyWaterloggedFrom(p_154592_.getLevel(), p_154592_.getClickedPos(), (BlockState)$$1.setValue(FACING, p_154592_.getHorizontalDirection().getOpposite())) : null;
    }

    public void setPlacedBy(Level p_154599_, BlockPos p_154600_, BlockState p_154601_, LivingEntity p_154602_, ItemStack p_154603_) {
        if (!p_154599_.isClientSide()) {
            BlockPos $$5 = p_154600_.above();
            BlockState $$6 = DoublePlantBlock.copyWaterloggedFrom(p_154599_, $$5, (BlockState)((BlockState)this.defaultBlockState().setValue(HALF, DoubleBlockHalf.UPPER)).setValue(FACING, (Direction)p_154601_.getValue(FACING)));
            p_154599_.setBlock($$5, $$6, 3);
        }

    }

    public FluidState getFluidState(BlockState p_154634_) {
        return (Boolean)p_154634_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_154634_);
    }

    public boolean canSurvive(BlockState p_154615_, LevelReader p_154616_, BlockPos p_154617_) {
        if (p_154615_.getValue(HALF) == DoubleBlockHalf.UPPER) {
            return super.canSurvive(p_154615_, p_154616_, p_154617_);
        } else {
            BlockPos $$3 = p_154617_.below();
            BlockState $$4 = p_154616_.getBlockState($$3);
            return this.mayPlaceOn($$4, p_154616_, $$3);
        }
    }

    public BlockState updateShape(BlockState p_154625_, Direction p_154626_, BlockState p_154627_, LevelAccessor p_154628_, BlockPos p_154629_, BlockPos p_154630_) {
        if ((Boolean)p_154625_.getValue(WATERLOGGED)) {
            p_154628_.scheduleTick(p_154629_, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(p_154628_));
        }

        return super.updateShape(p_154625_, p_154626_, p_154627_, p_154628_, p_154629_, p_154630_);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_154632_) {
        p_154632_.add(HALF, WATERLOGGED, FACING);
    }

    public boolean isValidBonemealTarget(LevelReader p_255772_, BlockPos p_154595_, BlockState p_154596_, boolean p_154597_) {
        return true;
    }

    public boolean isBonemealSuccess(Level p_222438_, RandomSource p_222439_, BlockPos p_222440_, BlockState p_222441_) {
        return true;
    }

    public void performBonemeal(ServerLevel p_222433_, RandomSource p_222434_, BlockPos p_222435_, BlockState p_222436_) {
        BlockPos $$4;
        if (p_222436_.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.LOWER) {
            $$4 = p_222435_.above();
            p_222433_.setBlock($$4, p_222433_.getFluidState($$4).createLegacyBlock(), 18);
            BigDripleafBlock.placeWithRandomHeight(p_222433_, p_222434_, p_222435_, (Direction)p_222436_.getValue(FACING));
        } else {
            $$4 = p_222435_.below();
            this.performBonemeal(p_222433_, p_222434_, $$4, p_222433_.getBlockState($$4));
        }

    }

    public BlockState rotate(BlockState p_154622_, Rotation p_154623_) {
        return (BlockState)p_154622_.setValue(FACING, p_154623_.rotate((Direction)p_154622_.getValue(FACING)));
    }

    public BlockState mirror(BlockState p_154619_, Mirror p_154620_) {
        return p_154619_.rotate(p_154620_.getRotation((Direction)p_154619_.getValue(FACING)));
    }

    public float getMaxVerticalOffset() {
        return 0.1F;
    }

    static {
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
        FACING = BlockStateProperties.HORIZONTAL_FACING;
        SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 13.0, 14.0);
    }
}

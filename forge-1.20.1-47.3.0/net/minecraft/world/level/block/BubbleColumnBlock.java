//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BubbleColumnBlock extends Block implements BucketPickup {
    public static final BooleanProperty DRAG_DOWN;
    private static final int CHECK_PERIOD = 5;

    public BubbleColumnBlock(BlockBehaviour.Properties p_50959_) {
        super(p_50959_);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(DRAG_DOWN, true));
    }

    public void entityInside(BlockState p_50976_, Level p_50977_, BlockPos p_50978_, Entity p_50979_) {
        BlockState $$4 = p_50977_.getBlockState(p_50978_.above());
        if ($$4.isAir()) {
            p_50979_.onAboveBubbleCol((Boolean)p_50976_.getValue(DRAG_DOWN));
            if (!p_50977_.isClientSide) {
                ServerLevel $$5 = (ServerLevel)p_50977_;

                for(int $$6 = 0; $$6 < 2; ++$$6) {
                    $$5.sendParticles(ParticleTypes.SPLASH, (double)p_50978_.getX() + p_50977_.random.nextDouble(), (double)(p_50978_.getY() + 1), (double)p_50978_.getZ() + p_50977_.random.nextDouble(), 1, 0.0, 0.0, 0.0, 1.0);
                    $$5.sendParticles(ParticleTypes.BUBBLE, (double)p_50978_.getX() + p_50977_.random.nextDouble(), (double)(p_50978_.getY() + 1), (double)p_50978_.getZ() + p_50977_.random.nextDouble(), 1, 0.0, 0.01, 0.0, 0.2);
                }
            }
        } else {
            p_50979_.onInsideBubbleColumn((Boolean)p_50976_.getValue(DRAG_DOWN));
        }

    }

    public void tick(BlockState p_220888_, ServerLevel p_220889_, BlockPos p_220890_, RandomSource p_220891_) {
        updateColumn(p_220889_, p_220890_, p_220888_, p_220889_.getBlockState(p_220890_.below()));
    }

    public FluidState getFluidState(BlockState p_51016_) {
        return Fluids.WATER.getSource(false);
    }

    public static void updateColumn(LevelAccessor p_152708_, BlockPos p_152709_, BlockState p_152710_) {
        updateColumn(p_152708_, p_152709_, p_152708_.getBlockState(p_152709_), p_152710_);
    }

    public static void updateColumn(LevelAccessor p_152703_, BlockPos p_152704_, BlockState p_152705_, BlockState p_152706_) {
        if (canExistIn(p_152705_)) {
            BlockState $$4 = getColumnState(p_152706_);
            p_152703_.setBlock(p_152704_, $$4, 2);
            BlockPos.MutableBlockPos $$5 = p_152704_.mutable().move(Direction.UP);

            while(canExistIn(p_152703_.getBlockState($$5))) {
                if (!p_152703_.setBlock($$5, $$4, 2)) {
                    return;
                }

                $$5.move(Direction.UP);
            }

        }
    }

    private static boolean canExistIn(BlockState p_152716_) {
        return p_152716_.is(Blocks.BUBBLE_COLUMN) || p_152716_.is(Blocks.WATER) && p_152716_.getFluidState().getAmount() >= 8 && p_152716_.getFluidState().isSource();
    }

    private static BlockState getColumnState(BlockState p_152718_) {
        if (p_152718_.is(Blocks.BUBBLE_COLUMN)) {
            return p_152718_;
        } else if (p_152718_.is(Blocks.SOUL_SAND)) {
            return (BlockState)Blocks.BUBBLE_COLUMN.defaultBlockState().setValue(DRAG_DOWN, false);
        } else {
            return p_152718_.is(Blocks.MAGMA_BLOCK) ? (BlockState)Blocks.BUBBLE_COLUMN.defaultBlockState().setValue(DRAG_DOWN, true) : Blocks.WATER.defaultBlockState();
        }
    }

    public void animateTick(BlockState p_220893_, Level p_220894_, BlockPos p_220895_, RandomSource p_220896_) {
        double $$4 = (double)p_220895_.getX();
        double $$5 = (double)p_220895_.getY();
        double $$6 = (double)p_220895_.getZ();
        if ((Boolean)p_220893_.getValue(DRAG_DOWN)) {
            p_220894_.addAlwaysVisibleParticle(ParticleTypes.CURRENT_DOWN, $$4 + 0.5, $$5 + 0.8, $$6, 0.0, 0.0, 0.0);
            if (p_220896_.nextInt(200) == 0) {
                p_220894_.playLocalSound($$4, $$5, $$6, SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, SoundSource.BLOCKS, 0.2F + p_220896_.nextFloat() * 0.2F, 0.9F + p_220896_.nextFloat() * 0.15F, false);
            }
        } else {
            p_220894_.addAlwaysVisibleParticle(ParticleTypes.BUBBLE_COLUMN_UP, $$4 + 0.5, $$5, $$6 + 0.5, 0.0, 0.04, 0.0);
            p_220894_.addAlwaysVisibleParticle(ParticleTypes.BUBBLE_COLUMN_UP, $$4 + (double)p_220896_.nextFloat(), $$5 + (double)p_220896_.nextFloat(), $$6 + (double)p_220896_.nextFloat(), 0.0, 0.04, 0.0);
            if (p_220896_.nextInt(200) == 0) {
                p_220894_.playLocalSound($$4, $$5, $$6, SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundSource.BLOCKS, 0.2F + p_220896_.nextFloat() * 0.2F, 0.9F + p_220896_.nextFloat() * 0.15F, false);
            }
        }

    }

    public BlockState updateShape(BlockState p_50990_, Direction p_50991_, BlockState p_50992_, LevelAccessor p_50993_, BlockPos p_50994_, BlockPos p_50995_) {
        p_50993_.scheduleTick(p_50994_, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(p_50993_));
        if (!p_50990_.canSurvive(p_50993_, p_50994_) || p_50991_ == Direction.DOWN || p_50991_ == Direction.UP && !p_50992_.is(Blocks.BUBBLE_COLUMN) && canExistIn(p_50992_)) {
            p_50993_.scheduleTick(p_50994_, (Block)this, 5);
        }

        return super.updateShape(p_50990_, p_50991_, p_50992_, p_50993_, p_50994_, p_50995_);
    }

    public boolean canSurvive(BlockState p_50986_, LevelReader p_50987_, BlockPos p_50988_) {
        BlockState $$3 = p_50987_.getBlockState(p_50988_.below());
        return $$3.is(Blocks.BUBBLE_COLUMN) || $$3.is(Blocks.MAGMA_BLOCK) || $$3.is(Blocks.SOUL_SAND);
    }

    public VoxelShape getShape(BlockState p_51005_, BlockGetter p_51006_, BlockPos p_51007_, CollisionContext p_51008_) {
        return Shapes.empty();
    }

    public RenderShape getRenderShape(BlockState p_51003_) {
        return RenderShape.INVISIBLE;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_50997_) {
        p_50997_.add(DRAG_DOWN);
    }

    public ItemStack pickupBlock(LevelAccessor p_152712_, BlockPos p_152713_, BlockState p_152714_) {
        p_152712_.setBlock(p_152713_, Blocks.AIR.defaultBlockState(), 11);
        return new ItemStack(Items.WATER_BUCKET);
    }

    public Optional<SoundEvent> getPickupSound() {
        return Fluids.WATER.getPickupSound();
    }

    static {
        DRAG_DOWN = BlockStateProperties.DRAG;
    }
}

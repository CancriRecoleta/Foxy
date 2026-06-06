//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BrewingStandBlock extends BaseEntityBlock {
    public static final BooleanProperty[] HAS_BOTTLE;
    protected static final VoxelShape SHAPE;

    public BrewingStandBlock(BlockBehaviour.Properties p_50909_) {
        super(p_50909_);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(HAS_BOTTLE[0], false)).setValue(HAS_BOTTLE[1], false)).setValue(HAS_BOTTLE[2], false));
    }

    public RenderShape getRenderShape(BlockState p_50950_) {
        return RenderShape.MODEL;
    }

    public BlockEntity newBlockEntity(BlockPos p_152698_, BlockState p_152699_) {
        return new BrewingStandBlockEntity(p_152698_, p_152699_);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152694_, BlockState p_152695_, BlockEntityType<T> p_152696_) {
        return p_152694_.isClientSide ? null : createTickerHelper(p_152696_, BlockEntityType.BREWING_STAND, BrewingStandBlockEntity::serverTick);
    }

    public VoxelShape getShape(BlockState p_50952_, BlockGetter p_50953_, BlockPos p_50954_, CollisionContext p_50955_) {
        return SHAPE;
    }

    public InteractionResult use(BlockState p_50930_, Level p_50931_, BlockPos p_50932_, Player p_50933_, InteractionHand p_50934_, BlockHitResult p_50935_) {
        if (p_50931_.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity $$6 = p_50931_.getBlockEntity(p_50932_);
            if ($$6 instanceof BrewingStandBlockEntity) {
                p_50933_.openMenu((BrewingStandBlockEntity)$$6);
                p_50933_.awardStat(Stats.INTERACT_WITH_BREWINGSTAND);
            }

            return InteractionResult.CONSUME;
        }
    }

    public void setPlacedBy(Level p_50913_, BlockPos p_50914_, BlockState p_50915_, LivingEntity p_50916_, ItemStack p_50917_) {
        if (p_50917_.hasCustomHoverName()) {
            BlockEntity $$5 = p_50913_.getBlockEntity(p_50914_);
            if ($$5 instanceof BrewingStandBlockEntity) {
                ((BrewingStandBlockEntity)$$5).setCustomName(p_50917_.getHoverName());
            }
        }

    }

    public void animateTick(BlockState p_220883_, Level p_220884_, BlockPos p_220885_, RandomSource p_220886_) {
        double $$4 = (double)p_220885_.getX() + 0.4 + (double)p_220886_.nextFloat() * 0.2;
        double $$5 = (double)p_220885_.getY() + 0.7 + (double)p_220886_.nextFloat() * 0.3;
        double $$6 = (double)p_220885_.getZ() + 0.4 + (double)p_220886_.nextFloat() * 0.2;
        p_220884_.addParticle(ParticleTypes.SMOKE, $$4, $$5, $$6, 0.0, 0.0, 0.0);
    }

    public void onRemove(BlockState p_50937_, Level p_50938_, BlockPos p_50939_, BlockState p_50940_, boolean p_50941_) {
        if (!p_50937_.is(p_50940_.getBlock())) {
            BlockEntity $$5 = p_50938_.getBlockEntity(p_50939_);
            if ($$5 instanceof BrewingStandBlockEntity) {
                Containers.dropContents(p_50938_, (BlockPos)p_50939_, (Container)((BrewingStandBlockEntity)$$5));
            }

            super.onRemove(p_50937_, p_50938_, p_50939_, p_50940_, p_50941_);
        }
    }

    public boolean hasAnalogOutputSignal(BlockState p_50919_) {
        return true;
    }

    public int getAnalogOutputSignal(BlockState p_50926_, Level p_50927_, BlockPos p_50928_) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(p_50927_.getBlockEntity(p_50928_));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_50948_) {
        p_50948_.add(HAS_BOTTLE[0], HAS_BOTTLE[1], HAS_BOTTLE[2]);
    }

    public boolean isPathfindable(BlockState p_50921_, BlockGetter p_50922_, BlockPos p_50923_, PathComputationType p_50924_) {
        return false;
    }

    static {
        HAS_BOTTLE = new BooleanProperty[]{BlockStateProperties.HAS_BOTTLE_0, BlockStateProperties.HAS_BOTTLE_1, BlockStateProperties.HAS_BOTTLE_2};
        SHAPE = Shapes.or(Block.box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0), Block.box(7.0, 0.0, 7.0, 9.0, 14.0, 9.0));
    }
}

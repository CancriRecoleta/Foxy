//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public class RepeaterBlock extends DiodeBlock {
    public static final BooleanProperty LOCKED;
    public static final IntegerProperty DELAY;

    public RepeaterBlock(BlockBehaviour.Properties p_55801_) {
        super(p_55801_);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(DELAY, 1)).setValue(LOCKED, false)).setValue(POWERED, false));
    }

    public InteractionResult use(BlockState p_55809_, Level p_55810_, BlockPos p_55811_, Player p_55812_, InteractionHand p_55813_, BlockHitResult p_55814_) {
        if (!p_55812_.getAbilities().mayBuild) {
            return InteractionResult.PASS;
        } else {
            p_55810_.setBlock(p_55811_, (BlockState)p_55809_.cycle(DELAY), 3);
            return InteractionResult.sidedSuccess(p_55810_.isClientSide);
        }
    }

    protected int getDelay(BlockState p_55830_) {
        return (Integer)p_55830_.getValue(DELAY) * 2;
    }

    public BlockState getStateForPlacement(BlockPlaceContext p_55803_) {
        BlockState $$1 = super.getStateForPlacement(p_55803_);
        return (BlockState)$$1.setValue(LOCKED, this.isLocked(p_55803_.getLevel(), p_55803_.getClickedPos(), $$1));
    }

    public BlockState updateShape(BlockState p_55821_, Direction p_55822_, BlockState p_55823_, LevelAccessor p_55824_, BlockPos p_55825_, BlockPos p_55826_) {
        return !p_55824_.isClientSide() && p_55822_.getAxis() != ((Direction)p_55821_.getValue(FACING)).getAxis() ? (BlockState)p_55821_.setValue(LOCKED, this.isLocked(p_55824_, p_55825_, p_55821_)) : super.updateShape(p_55821_, p_55822_, p_55823_, p_55824_, p_55825_, p_55826_);
    }

    public boolean isLocked(LevelReader p_55805_, BlockPos p_55806_, BlockState p_55807_) {
        return this.getAlternateSignal(p_55805_, p_55806_, p_55807_) > 0;
    }

    protected boolean sideInputDiodesOnly() {
        return true;
    }

    public void animateTick(BlockState p_221964_, Level p_221965_, BlockPos p_221966_, RandomSource p_221967_) {
        if ((Boolean)p_221964_.getValue(POWERED)) {
            Direction $$4 = (Direction)p_221964_.getValue(FACING);
            double $$5 = (double)p_221966_.getX() + 0.5 + (p_221967_.nextDouble() - 0.5) * 0.2;
            double $$6 = (double)p_221966_.getY() + 0.4 + (p_221967_.nextDouble() - 0.5) * 0.2;
            double $$7 = (double)p_221966_.getZ() + 0.5 + (p_221967_.nextDouble() - 0.5) * 0.2;
            float $$8 = -5.0F;
            if (p_221967_.nextBoolean()) {
                $$8 = (float)((Integer)p_221964_.getValue(DELAY) * 2 - 1);
            }

            $$8 /= 16.0F;
            double $$9 = (double)($$8 * (float)$$4.getStepX());
            double $$10 = (double)($$8 * (float)$$4.getStepZ());
            p_221965_.addParticle(DustParticleOptions.REDSTONE, $$5 + $$9, $$6, $$7 + $$10, 0.0, 0.0, 0.0);
        }
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_55828_) {
        p_55828_.add(FACING, DELAY, LOCKED, POWERED);
    }

    static {
        LOCKED = BlockStateProperties.LOCKED;
        DELAY = BlockStateProperties.DELAY;
    }
}

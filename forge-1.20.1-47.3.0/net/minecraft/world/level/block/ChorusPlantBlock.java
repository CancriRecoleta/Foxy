//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block;

import java.util.Iterator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;

public class ChorusPlantBlock extends PipeBlock {
    public ChorusPlantBlock(BlockBehaviour.Properties p_51707_) {
        super(0.3125F, p_51707_);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(NORTH, false)).setValue(EAST, false)).setValue(SOUTH, false)).setValue(WEST, false)).setValue(UP, false)).setValue(DOWN, false));
    }

    public BlockState getStateForPlacement(BlockPlaceContext p_51709_) {
        return this.getStateForPlacement(p_51709_.getLevel(), p_51709_.getClickedPos());
    }

    public BlockState getStateForPlacement(BlockGetter p_51711_, BlockPos p_51712_) {
        BlockState $$2 = p_51711_.getBlockState(p_51712_.below());
        BlockState $$3 = p_51711_.getBlockState(p_51712_.above());
        BlockState $$4 = p_51711_.getBlockState(p_51712_.north());
        BlockState $$5 = p_51711_.getBlockState(p_51712_.east());
        BlockState $$6 = p_51711_.getBlockState(p_51712_.south());
        BlockState $$7 = p_51711_.getBlockState(p_51712_.west());
        return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(DOWN, $$2.is(this) || $$2.is(Blocks.CHORUS_FLOWER) || $$2.is(Blocks.END_STONE))).setValue(UP, $$3.is(this) || $$3.is(Blocks.CHORUS_FLOWER))).setValue(NORTH, $$4.is(this) || $$4.is(Blocks.CHORUS_FLOWER))).setValue(EAST, $$5.is(this) || $$5.is(Blocks.CHORUS_FLOWER))).setValue(SOUTH, $$6.is(this) || $$6.is(Blocks.CHORUS_FLOWER))).setValue(WEST, $$7.is(this) || $$7.is(Blocks.CHORUS_FLOWER));
    }

    public BlockState updateShape(BlockState p_51728_, Direction p_51729_, BlockState p_51730_, LevelAccessor p_51731_, BlockPos p_51732_, BlockPos p_51733_) {
        if (!p_51728_.canSurvive(p_51731_, p_51732_)) {
            p_51731_.scheduleTick(p_51732_, (Block)this, 1);
            return super.updateShape(p_51728_, p_51729_, p_51730_, p_51731_, p_51732_, p_51733_);
        } else {
            boolean $$6 = p_51730_.is(this) || p_51730_.is(Blocks.CHORUS_FLOWER) || p_51729_ == Direction.DOWN && p_51730_.is(Blocks.END_STONE);
            return (BlockState)p_51728_.setValue((Property)PROPERTY_BY_DIRECTION.get(p_51729_), $$6);
        }
    }

    public void tick(BlockState p_220985_, ServerLevel p_220986_, BlockPos p_220987_, RandomSource p_220988_) {
        if (!p_220985_.canSurvive(p_220986_, p_220987_)) {
            p_220986_.destroyBlock(p_220987_, true);
        }

    }

    public boolean canSurvive(BlockState p_51724_, LevelReader p_51725_, BlockPos p_51726_) {
        BlockState $$3 = p_51725_.getBlockState(p_51726_.below());
        boolean $$4 = !p_51725_.getBlockState(p_51726_.above()).isAir() && !$$3.isAir();
        Iterator var6 = Plane.HORIZONTAL.iterator();

        BlockState $$8;
        do {
            BlockPos $$6;
            BlockState $$7;
            do {
                if (!var6.hasNext()) {
                    return $$3.is(this) || $$3.is(Blocks.END_STONE);
                }

                Direction $$5 = (Direction)var6.next();
                $$6 = p_51726_.relative($$5);
                $$7 = p_51725_.getBlockState($$6);
            } while(!$$7.is(this));

            if ($$4) {
                return false;
            }

            $$8 = p_51725_.getBlockState($$6.below());
        } while(!$$8.is(this) && !$$8.is(Blocks.END_STONE));

        return true;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_51735_) {
        p_51735_.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    public boolean isPathfindable(BlockState p_51719_, BlockGetter p_51720_, BlockPos p_51721_, PathComputationType p_51722_) {
        return false;
    }
}

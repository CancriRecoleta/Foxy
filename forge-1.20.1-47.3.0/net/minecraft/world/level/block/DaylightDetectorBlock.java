//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.DaylightDetectorBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DaylightDetectorBlock extends BaseEntityBlock {
    public static final IntegerProperty POWER;
    public static final BooleanProperty INVERTED;
    protected static final VoxelShape SHAPE;

    public DaylightDetectorBlock(BlockBehaviour.Properties p_52382_) {
        super(p_52382_);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(POWER, 0)).setValue(INVERTED, false));
    }

    public VoxelShape getShape(BlockState p_52402_, BlockGetter p_52403_, BlockPos p_52404_, CollisionContext p_52405_) {
        return SHAPE;
    }

    public boolean useShapeForLightOcclusion(BlockState p_52409_) {
        return true;
    }

    public int getSignal(BlockState p_52386_, BlockGetter p_52387_, BlockPos p_52388_, Direction p_52389_) {
        return (Integer)p_52386_.getValue(POWER);
    }

    private static void updateSignalStrength(BlockState p_52411_, Level p_52412_, BlockPos p_52413_) {
        int $$3 = p_52412_.getBrightness(LightLayer.SKY, p_52413_) - p_52412_.getSkyDarken();
        float $$4 = p_52412_.getSunAngle(1.0F);
        boolean $$5 = (Boolean)p_52411_.getValue(INVERTED);
        if ($$5) {
            $$3 = 15 - $$3;
        } else if ($$3 > 0) {
            float $$6 = $$4 < 3.1415927F ? 0.0F : 6.2831855F;
            $$4 += ($$6 - $$4) * 0.2F;
            $$3 = Math.round((float)$$3 * Mth.cos($$4));
        }

        $$3 = Mth.clamp($$3, 0, 15);
        if ((Integer)p_52411_.getValue(POWER) != $$3) {
            p_52412_.setBlock(p_52413_, (BlockState)p_52411_.setValue(POWER, $$3), 3);
        }

    }

    public InteractionResult use(BlockState p_52391_, Level p_52392_, BlockPos p_52393_, Player p_52394_, InteractionHand p_52395_, BlockHitResult p_52396_) {
        if (p_52394_.mayBuild()) {
            if (p_52392_.isClientSide) {
                return InteractionResult.SUCCESS;
            } else {
                BlockState $$6 = (BlockState)p_52391_.cycle(INVERTED);
                p_52392_.setBlock(p_52393_, $$6, 4);
                p_52392_.gameEvent(GameEvent.BLOCK_CHANGE, p_52393_, Context.of(p_52394_, $$6));
                updateSignalStrength($$6, p_52392_, p_52393_);
                return InteractionResult.CONSUME;
            }
        } else {
            return super.use(p_52391_, p_52392_, p_52393_, p_52394_, p_52395_, p_52396_);
        }
    }

    public RenderShape getRenderShape(BlockState p_52400_) {
        return RenderShape.MODEL;
    }

    public boolean isSignalSource(BlockState p_52407_) {
        return true;
    }

    public BlockEntity newBlockEntity(BlockPos p_153118_, BlockState p_153119_) {
        return new DaylightDetectorBlockEntity(p_153118_, p_153119_);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153109_, BlockState p_153110_, BlockEntityType<T> p_153111_) {
        return !p_153109_.isClientSide && p_153109_.dimensionType().hasSkyLight() ? createTickerHelper(p_153111_, BlockEntityType.DAYLIGHT_DETECTOR, DaylightDetectorBlock::tickEntity) : null;
    }

    private static void tickEntity(Level p_153113_, BlockPos p_153114_, BlockState p_153115_, DaylightDetectorBlockEntity p_153116_) {
        if (p_153113_.getGameTime() % 20L == 0L) {
            updateSignalStrength(p_153115_, p_153113_, p_153114_);
        }

    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_52398_) {
        p_52398_.add(POWER, INVERTED);
    }

    static {
        POWER = BlockStateProperties.POWER;
        INVERTED = BlockStateProperties.INVERTED;
        SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 6.0, 16.0);
    }
}

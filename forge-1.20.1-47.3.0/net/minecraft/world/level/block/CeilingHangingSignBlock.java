//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.HangingSignBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CeilingHangingSignBlock extends SignBlock {
    public static final IntegerProperty ROTATION;
    public static final BooleanProperty ATTACHED;
    protected static final float AABB_OFFSET = 5.0F;
    protected static final VoxelShape SHAPE;
    private static final Map<Integer, VoxelShape> AABBS;

    public CeilingHangingSignBlock(BlockBehaviour.Properties p_250481_, WoodType p_248716_) {
        super(p_250481_.sound(p_248716_.hangingSignSoundType()), p_248716_);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(ROTATION, 0)).setValue(ATTACHED, false)).setValue(WATERLOGGED, false));
    }

    public InteractionResult use(BlockState p_251161_, Level p_249327_, BlockPos p_248552_, Player p_248644_, InteractionHand p_251941_, BlockHitResult p_252016_) {
        BlockEntity var8 = p_249327_.getBlockEntity(p_248552_);
        if (var8 instanceof SignBlockEntity $$6) {
            ItemStack $$7 = p_248644_.getItemInHand(p_251941_);
            if (this.shouldTryToChainAnotherHangingSign(p_248644_, p_252016_, $$6, $$7)) {
                return InteractionResult.PASS;
            }
        }

        return super.use(p_251161_, p_249327_, p_248552_, p_248644_, p_251941_, p_252016_);
    }

    private boolean shouldTryToChainAnotherHangingSign(Player p_278279_, BlockHitResult p_278273_, SignBlockEntity p_278236_, ItemStack p_278343_) {
        return !p_278236_.canExecuteClickCommands(p_278236_.isFacingFrontText(p_278279_), p_278279_) && p_278343_.getItem() instanceof HangingSignItem && p_278273_.getDirection().equals(Direction.DOWN);
    }

    public boolean canSurvive(BlockState p_248994_, LevelReader p_249061_, BlockPos p_249490_) {
        return p_249061_.getBlockState(p_249490_.above()).isFaceSturdy(p_249061_, p_249490_.above(), Direction.DOWN, SupportType.CENTER);
    }

    public BlockState getStateForPlacement(BlockPlaceContext p_252121_) {
        Level $$1 = p_252121_.getLevel();
        FluidState $$2 = $$1.getFluidState(p_252121_.getClickedPos());
        BlockPos $$3 = p_252121_.getClickedPos().above();
        BlockState $$4 = $$1.getBlockState($$3);
        boolean $$5 = $$4.is(BlockTags.ALL_HANGING_SIGNS);
        Direction $$6 = Direction.fromYRot((double)p_252121_.getRotation());
        boolean $$7 = !Block.isFaceFull($$4.getCollisionShape($$1, $$3), Direction.DOWN) || p_252121_.isSecondaryUseActive();
        if ($$5 && !p_252121_.isSecondaryUseActive()) {
            if ($$4.hasProperty(WallHangingSignBlock.FACING)) {
                Direction $$8 = (Direction)$$4.getValue(WallHangingSignBlock.FACING);
                if ($$8.getAxis().test($$6)) {
                    $$7 = false;
                }
            } else if ($$4.hasProperty(ROTATION)) {
                Optional<Direction> $$9 = RotationSegment.convertToDirection((Integer)$$4.getValue(ROTATION));
                if ($$9.isPresent() && ((Direction)$$9.get()).getAxis().test($$6)) {
                    $$7 = false;
                }
            }
        }

        int $$10 = !$$7 ? RotationSegment.convertToSegment($$6.getOpposite()) : RotationSegment.convertToSegment(p_252121_.getRotation() + 180.0F);
        return (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(ATTACHED, $$7)).setValue(ROTATION, $$10)).setValue(WATERLOGGED, $$2.getType() == Fluids.WATER);
    }

    public VoxelShape getShape(BlockState p_250564_, BlockGetter p_248998_, BlockPos p_249501_, CollisionContext p_248978_) {
        VoxelShape $$4 = (VoxelShape)AABBS.get(p_250564_.getValue(ROTATION));
        return $$4 == null ? SHAPE : $$4;
    }

    public VoxelShape getBlockSupportShape(BlockState p_254482_, BlockGetter p_253669_, BlockPos p_253916_) {
        return this.getShape(p_254482_, p_253669_, p_253916_, CollisionContext.empty());
    }

    public BlockState updateShape(BlockState p_251270_, Direction p_250331_, BlockState p_249591_, LevelAccessor p_251903_, BlockPos p_249685_, BlockPos p_251506_) {
        return p_250331_ == Direction.UP && !this.canSurvive(p_251270_, p_251903_, p_249685_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_251270_, p_250331_, p_249591_, p_251903_, p_249685_, p_251506_);
    }

    public float getYRotationDegrees(BlockState p_277758_) {
        return RotationSegment.convertToDegrees((Integer)p_277758_.getValue(ROTATION));
    }

    public BlockState rotate(BlockState p_251162_, Rotation p_250515_) {
        return (BlockState)p_251162_.setValue(ROTATION, p_250515_.rotate((Integer)p_251162_.getValue(ROTATION), 16));
    }

    public BlockState mirror(BlockState p_249682_, Mirror p_250199_) {
        return (BlockState)p_249682_.setValue(ROTATION, p_250199_.mirror((Integer)p_249682_.getValue(ROTATION), 16));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_251174_) {
        p_251174_.add(ROTATION, ATTACHED, WATERLOGGED);
    }

    public BlockEntity newBlockEntity(BlockPos p_249338_, BlockState p_250706_) {
        return new HangingSignBlockEntity(p_249338_, p_250706_);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_279379_, BlockState p_279390_, BlockEntityType<T> p_279231_) {
        return createTickerHelper(p_279231_, BlockEntityType.HANGING_SIGN, SignBlockEntity::tick);
    }

    static {
        ROTATION = BlockStateProperties.ROTATION_16;
        ATTACHED = BlockStateProperties.ATTACHED;
        SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 16.0, 13.0);
        AABBS = Maps.newHashMap(ImmutableMap.of(0, Block.box(1.0, 0.0, 7.0, 15.0, 10.0, 9.0), 4, Block.box(7.0, 0.0, 1.0, 9.0, 10.0, 15.0), 8, Block.box(1.0, 0.0, 7.0, 15.0, 10.0, 9.0), 12, Block.box(7.0, 0.0, 1.0, 9.0, 10.0, 15.0)));
    }
}

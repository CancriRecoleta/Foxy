//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.List;
import java.util.function.ToIntFunction;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CandleBlock extends AbstractCandleBlock implements SimpleWaterloggedBlock {
    public static final int MIN_CANDLES = 1;
    public static final int MAX_CANDLES = 4;
    public static final IntegerProperty CANDLES;
    public static final BooleanProperty LIT;
    public static final BooleanProperty WATERLOGGED;
    public static final ToIntFunction<BlockState> LIGHT_EMISSION;
    private static final Int2ObjectMap<List<Vec3>> PARTICLE_OFFSETS;
    private static final VoxelShape ONE_AABB;
    private static final VoxelShape TWO_AABB;
    private static final VoxelShape THREE_AABB;
    private static final VoxelShape FOUR_AABB;

    public CandleBlock(BlockBehaviour.Properties p_152801_) {
        super(p_152801_);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(CANDLES, 1)).setValue(LIT, false)).setValue(WATERLOGGED, false));
    }

    public InteractionResult use(BlockState p_152822_, Level p_152823_, BlockPos p_152824_, Player p_152825_, InteractionHand p_152826_, BlockHitResult p_152827_) {
        if (p_152825_.getAbilities().mayBuild && p_152825_.getItemInHand(p_152826_).isEmpty() && (Boolean)p_152822_.getValue(LIT)) {
            extinguish(p_152825_, p_152822_, p_152823_, p_152824_);
            return InteractionResult.sidedSuccess(p_152823_.isClientSide);
        } else {
            return InteractionResult.PASS;
        }
    }

    public boolean canBeReplaced(BlockState p_152814_, BlockPlaceContext p_152815_) {
        return !p_152815_.isSecondaryUseActive() && p_152815_.getItemInHand().getItem() == this.asItem() && (Integer)p_152814_.getValue(CANDLES) < 4 ? true : super.canBeReplaced(p_152814_, p_152815_);
    }

    public BlockState getStateForPlacement(BlockPlaceContext p_152803_) {
        BlockState $$1 = p_152803_.getLevel().getBlockState(p_152803_.getClickedPos());
        if ($$1.is(this)) {
            return (BlockState)$$1.cycle(CANDLES);
        } else {
            FluidState $$2 = p_152803_.getLevel().getFluidState(p_152803_.getClickedPos());
            boolean $$3 = $$2.getType() == Fluids.WATER;
            return (BlockState)super.getStateForPlacement(p_152803_).setValue(WATERLOGGED, $$3);
        }
    }

    public BlockState updateShape(BlockState p_152833_, Direction p_152834_, BlockState p_152835_, LevelAccessor p_152836_, BlockPos p_152837_, BlockPos p_152838_) {
        if ((Boolean)p_152833_.getValue(WATERLOGGED)) {
            p_152836_.scheduleTick(p_152837_, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(p_152836_));
        }

        return super.updateShape(p_152833_, p_152834_, p_152835_, p_152836_, p_152837_, p_152838_);
    }

    public FluidState getFluidState(BlockState p_152844_) {
        return (Boolean)p_152844_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_152844_);
    }

    public VoxelShape getShape(BlockState p_152817_, BlockGetter p_152818_, BlockPos p_152819_, CollisionContext p_152820_) {
        switch ((Integer)p_152817_.getValue(CANDLES)) {
            case 1:
            default:
                return ONE_AABB;
            case 2:
                return TWO_AABB;
            case 3:
                return THREE_AABB;
            case 4:
                return FOUR_AABB;
        }
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_152840_) {
        p_152840_.add(CANDLES, LIT, WATERLOGGED);
    }

    public boolean placeLiquid(LevelAccessor p_152805_, BlockPos p_152806_, BlockState p_152807_, FluidState p_152808_) {
        if (!(Boolean)p_152807_.getValue(WATERLOGGED) && p_152808_.getType() == Fluids.WATER) {
            BlockState $$4 = (BlockState)p_152807_.setValue(WATERLOGGED, true);
            if ((Boolean)p_152807_.getValue(LIT)) {
                extinguish((Player)null, $$4, p_152805_, p_152806_);
            } else {
                p_152805_.setBlock(p_152806_, $$4, 3);
            }

            p_152805_.scheduleTick(p_152806_, p_152808_.getType(), p_152808_.getType().getTickDelay(p_152805_));
            return true;
        } else {
            return false;
        }
    }

    public static boolean canLight(BlockState p_152846_) {
        return p_152846_.is(BlockTags.CANDLES, (p_152810_) -> {
            return p_152810_.hasProperty(LIT) && p_152810_.hasProperty(WATERLOGGED);
        }) && !(Boolean)p_152846_.getValue(LIT) && !(Boolean)p_152846_.getValue(WATERLOGGED);
    }

    protected Iterable<Vec3> getParticleOffsets(BlockState p_152812_) {
        return (Iterable)PARTICLE_OFFSETS.get((Integer)p_152812_.getValue(CANDLES));
    }

    protected boolean canBeLit(BlockState p_152842_) {
        return !(Boolean)p_152842_.getValue(WATERLOGGED) && super.canBeLit(p_152842_);
    }

    public boolean canSurvive(BlockState p_152829_, LevelReader p_152830_, BlockPos p_152831_) {
        return Block.canSupportCenter(p_152830_, p_152831_.below(), Direction.UP);
    }

    static {
        CANDLES = BlockStateProperties.CANDLES;
        LIT = AbstractCandleBlock.LIT;
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
        LIGHT_EMISSION = (p_152848_) -> {
            return (Boolean)p_152848_.getValue(LIT) ? 3 * (Integer)p_152848_.getValue(CANDLES) : 0;
        };
        PARTICLE_OFFSETS = (Int2ObjectMap)Util.make(() -> {
            Int2ObjectMap<List<Vec3>> $$0 = new Int2ObjectOpenHashMap();
            $$0.defaultReturnValue(ImmutableList.of());
            $$0.put(1, ImmutableList.of(new Vec3(0.5, 0.5, 0.5)));
            $$0.put(2, ImmutableList.of(new Vec3(0.375, 0.44, 0.5), new Vec3(0.625, 0.5, 0.44)));
            $$0.put(3, ImmutableList.of(new Vec3(0.5, 0.313, 0.625), new Vec3(0.375, 0.44, 0.5), new Vec3(0.56, 0.5, 0.44)));
            $$0.put(4, ImmutableList.of(new Vec3(0.44, 0.313, 0.56), new Vec3(0.625, 0.44, 0.56), new Vec3(0.375, 0.44, 0.375), new Vec3(0.56, 0.5, 0.375)));
            return Int2ObjectMaps.unmodifiable($$0);
        });
        ONE_AABB = Block.box(7.0, 0.0, 7.0, 9.0, 6.0, 9.0);
        TWO_AABB = Block.box(5.0, 0.0, 6.0, 11.0, 6.0, 9.0);
        THREE_AABB = Block.box(5.0, 0.0, 6.0, 10.0, 6.0, 11.0);
        FOUR_AABB = Block.box(5.0, 0.0, 5.0, 11.0, 6.0, 10.0);
    }
}

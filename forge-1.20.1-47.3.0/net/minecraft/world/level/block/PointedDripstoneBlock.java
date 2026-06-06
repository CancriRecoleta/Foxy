//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block;

import com.google.common.annotations.VisibleForTesting;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownTrident;
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
import net.minecraft.world.level.block.state.properties.DripstoneThickness;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PointedDripstoneBlock extends Block implements Fallable, SimpleWaterloggedBlock {
    public static final DirectionProperty TIP_DIRECTION;
    public static final EnumProperty<DripstoneThickness> THICKNESS;
    public static final BooleanProperty WATERLOGGED;
    private static final int MAX_SEARCH_LENGTH_WHEN_CHECKING_DRIP_TYPE = 11;
    private static final int DELAY_BEFORE_FALLING = 2;
    private static final float DRIP_PROBABILITY_PER_ANIMATE_TICK = 0.02F;
    private static final float DRIP_PROBABILITY_PER_ANIMATE_TICK_IF_UNDER_LIQUID_SOURCE = 0.12F;
    private static final int MAX_SEARCH_LENGTH_BETWEEN_STALACTITE_TIP_AND_CAULDRON = 11;
    private static final float WATER_TRANSFER_PROBABILITY_PER_RANDOM_TICK = 0.17578125F;
    private static final float LAVA_TRANSFER_PROBABILITY_PER_RANDOM_TICK = 0.05859375F;
    private static final double MIN_TRIDENT_VELOCITY_TO_BREAK_DRIPSTONE = 0.6;
    private static final float STALACTITE_DAMAGE_PER_FALL_DISTANCE_AND_SIZE = 1.0F;
    private static final int STALACTITE_MAX_DAMAGE = 40;
    private static final int MAX_STALACTITE_HEIGHT_FOR_DAMAGE_CALCULATION = 6;
    private static final float STALAGMITE_FALL_DISTANCE_OFFSET = 2.0F;
    private static final int STALAGMITE_FALL_DAMAGE_MODIFIER = 2;
    private static final float AVERAGE_DAYS_PER_GROWTH = 5.0F;
    private static final float GROWTH_PROBABILITY_PER_RANDOM_TICK = 0.011377778F;
    private static final int MAX_GROWTH_LENGTH = 7;
    private static final int MAX_STALAGMITE_SEARCH_RANGE_WHEN_GROWING = 10;
    private static final float STALACTITE_DRIP_START_PIXEL = 0.6875F;
    private static final VoxelShape TIP_MERGE_SHAPE;
    private static final VoxelShape TIP_SHAPE_UP;
    private static final VoxelShape TIP_SHAPE_DOWN;
    private static final VoxelShape FRUSTUM_SHAPE;
    private static final VoxelShape MIDDLE_SHAPE;
    private static final VoxelShape BASE_SHAPE;
    private static final float MAX_HORIZONTAL_OFFSET = 0.125F;
    private static final VoxelShape REQUIRED_SPACE_TO_DRIP_THROUGH_NON_SOLID_BLOCK;

    public PointedDripstoneBlock(BlockBehaviour.Properties p_154025_) {
        super(p_154025_);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(TIP_DIRECTION, Direction.UP)).setValue(THICKNESS, DripstoneThickness.TIP)).setValue(WATERLOGGED, false));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_154157_) {
        p_154157_.add(TIP_DIRECTION, THICKNESS, WATERLOGGED);
    }

    public boolean canSurvive(BlockState p_154137_, LevelReader p_154138_, BlockPos p_154139_) {
        return isValidPointedDripstonePlacement(p_154138_, p_154139_, (Direction)p_154137_.getValue(TIP_DIRECTION));
    }

    public BlockState updateShape(BlockState p_154147_, Direction p_154148_, BlockState p_154149_, LevelAccessor p_154150_, BlockPos p_154151_, BlockPos p_154152_) {
        if ((Boolean)p_154147_.getValue(WATERLOGGED)) {
            p_154150_.scheduleTick(p_154151_, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(p_154150_));
        }

        if (p_154148_ != Direction.UP && p_154148_ != Direction.DOWN) {
            return p_154147_;
        } else {
            Direction $$6 = (Direction)p_154147_.getValue(TIP_DIRECTION);
            if ($$6 == Direction.DOWN && p_154150_.getBlockTicks().hasScheduledTick(p_154151_, this)) {
                return p_154147_;
            } else if (p_154148_ == $$6.getOpposite() && !this.canSurvive(p_154147_, p_154150_, p_154151_)) {
                if ($$6 == Direction.DOWN) {
                    p_154150_.scheduleTick(p_154151_, (Block)this, 2);
                } else {
                    p_154150_.scheduleTick(p_154151_, (Block)this, 1);
                }

                return p_154147_;
            } else {
                boolean $$7 = p_154147_.getValue(THICKNESS) == DripstoneThickness.TIP_MERGE;
                DripstoneThickness $$8 = calculateDripstoneThickness(p_154150_, p_154151_, $$6, $$7);
                return (BlockState)p_154147_.setValue(THICKNESS, $$8);
            }
        }
    }

    public void onProjectileHit(Level p_154042_, BlockState p_154043_, BlockHitResult p_154044_, Projectile p_154045_) {
        BlockPos $$4 = p_154044_.getBlockPos();
        if (!p_154042_.isClientSide && p_154045_.mayInteract(p_154042_, $$4) && p_154045_ instanceof ThrownTrident && p_154045_.getDeltaMovement().length() > 0.6) {
            p_154042_.destroyBlock($$4, true);
        }

    }

    public void fallOn(Level p_154047_, BlockState p_154048_, BlockPos p_154049_, Entity p_154050_, float p_154051_) {
        if (p_154048_.getValue(TIP_DIRECTION) == Direction.UP && p_154048_.getValue(THICKNESS) == DripstoneThickness.TIP) {
            p_154050_.causeFallDamage(p_154051_ + 2.0F, 2.0F, p_154047_.damageSources().stalagmite());
        } else {
            super.fallOn(p_154047_, p_154048_, p_154049_, p_154050_, p_154051_);
        }

    }

    public void animateTick(BlockState p_221870_, Level p_221871_, BlockPos p_221872_, RandomSource p_221873_) {
        if (canDrip(p_221870_)) {
            float $$4 = p_221873_.nextFloat();
            if (!($$4 > 0.12F)) {
                getFluidAboveStalactite(p_221871_, p_221872_, p_221870_).filter((p_221848_) -> {
                    return $$4 < 0.02F || canFillCauldron(p_221848_.fluid);
                }).ifPresent((p_221881_) -> {
                    spawnDripParticle(p_221871_, p_221872_, p_221870_, p_221881_.fluid);
                });
            }
        }
    }

    public void tick(BlockState p_221865_, ServerLevel p_221866_, BlockPos p_221867_, RandomSource p_221868_) {
        if (isStalagmite(p_221865_) && !this.canSurvive(p_221865_, p_221866_, p_221867_)) {
            p_221866_.destroyBlock(p_221867_, true);
        } else {
            spawnFallingStalactite(p_221865_, p_221866_, p_221867_);
        }

    }

    public void randomTick(BlockState p_221883_, ServerLevel p_221884_, BlockPos p_221885_, RandomSource p_221886_) {
        maybeTransferFluid(p_221883_, p_221884_, p_221885_, p_221886_.nextFloat());
        if (p_221886_.nextFloat() < 0.011377778F && isStalactiteStartPos(p_221883_, p_221884_, p_221885_)) {
            growStalactiteOrStalagmiteIfPossible(p_221883_, p_221884_, p_221885_, p_221886_);
        }

    }

    @VisibleForTesting
    public static void maybeTransferFluid(BlockState p_221860_, ServerLevel p_221861_, BlockPos p_221862_, float p_221863_) {
        if (!(p_221863_ > 0.17578125F) || !(p_221863_ > 0.05859375F)) {
            if (isStalactiteStartPos(p_221860_, p_221861_, p_221862_)) {
                Optional<FluidInfo> $$4 = getFluidAboveStalactite(p_221861_, p_221862_, p_221860_);
                if (!$$4.isEmpty()) {
                    Fluid $$5 = ((FluidInfo)$$4.get()).fluid;
                    float $$8;
                    if ($$5 == Fluids.WATER) {
                        $$8 = 0.17578125F;
                    } else {
                        if ($$5 != Fluids.LAVA) {
                            return;
                        }

                        $$8 = 0.05859375F;
                    }

                    if (!(p_221863_ >= $$8)) {
                        BlockPos $$9 = findTip(p_221860_, p_221861_, p_221862_, 11, false);
                        if ($$9 != null) {
                            if (((FluidInfo)$$4.get()).sourceState.is(Blocks.MUD) && $$5 == Fluids.WATER) {
                                BlockState $$10 = Blocks.CLAY.defaultBlockState();
                                p_221861_.setBlockAndUpdate(((FluidInfo)$$4.get()).pos, $$10);
                                Block.pushEntitiesUp(((FluidInfo)$$4.get()).sourceState, $$10, p_221861_, ((FluidInfo)$$4.get()).pos);
                                p_221861_.gameEvent(GameEvent.BLOCK_CHANGE, ((FluidInfo)$$4.get()).pos, Context.of($$10));
                                p_221861_.levelEvent(1504, $$9, 0);
                            } else {
                                BlockPos $$11 = findFillableCauldronBelowStalactiteTip(p_221861_, $$9, $$5);
                                if ($$11 != null) {
                                    p_221861_.levelEvent(1504, $$9, 0);
                                    int $$12 = $$9.getY() - $$11.getY();
                                    int $$13 = 50 + $$12;
                                    BlockState $$14 = p_221861_.getBlockState($$11);
                                    p_221861_.scheduleTick($$11, $$14.getBlock(), $$13);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext p_154040_) {
        LevelAccessor $$1 = p_154040_.getLevel();
        BlockPos $$2 = p_154040_.getClickedPos();
        Direction $$3 = p_154040_.getNearestLookingVerticalDirection().getOpposite();
        Direction $$4 = calculateTipDirection($$1, $$2, $$3);
        if ($$4 == null) {
            return null;
        } else {
            boolean $$5 = !p_154040_.isSecondaryUseActive();
            DripstoneThickness $$6 = calculateDripstoneThickness($$1, $$2, $$4, $$5);
            return $$6 == null ? null : (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(TIP_DIRECTION, $$4)).setValue(THICKNESS, $$6)).setValue(WATERLOGGED, $$1.getFluidState($$2).getType() == Fluids.WATER);
        }
    }

    public FluidState getFluidState(BlockState p_154235_) {
        return (Boolean)p_154235_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_154235_);
    }

    public VoxelShape getOcclusionShape(BlockState p_154170_, BlockGetter p_154171_, BlockPos p_154172_) {
        return Shapes.empty();
    }

    public VoxelShape getShape(BlockState p_154117_, BlockGetter p_154118_, BlockPos p_154119_, CollisionContext p_154120_) {
        DripstoneThickness $$4 = (DripstoneThickness)p_154117_.getValue(THICKNESS);
        VoxelShape $$10;
        if ($$4 == DripstoneThickness.TIP_MERGE) {
            $$10 = TIP_MERGE_SHAPE;
        } else if ($$4 == DripstoneThickness.TIP) {
            if (p_154117_.getValue(TIP_DIRECTION) == Direction.DOWN) {
                $$10 = TIP_SHAPE_DOWN;
            } else {
                $$10 = TIP_SHAPE_UP;
            }
        } else if ($$4 == DripstoneThickness.FRUSTUM) {
            $$10 = FRUSTUM_SHAPE;
        } else if ($$4 == DripstoneThickness.MIDDLE) {
            $$10 = MIDDLE_SHAPE;
        } else {
            $$10 = BASE_SHAPE;
        }

        Vec3 $$11 = p_154117_.getOffset(p_154118_, p_154119_);
        return $$10.move($$11.x, 0.0, $$11.z);
    }

    public boolean isCollisionShapeFullBlock(BlockState p_181235_, BlockGetter p_181236_, BlockPos p_181237_) {
        return false;
    }

    public float getMaxHorizontalOffset() {
        return 0.125F;
    }

    public void onBrokenAfterFall(Level p_154059_, BlockPos p_154060_, FallingBlockEntity p_154061_) {
        if (!p_154061_.isSilent()) {
            p_154059_.levelEvent(1045, p_154060_, 0);
        }

    }

    public DamageSource getFallDamageSource(Entity p_254432_) {
        return p_254432_.damageSources().fallingStalactite(p_254432_);
    }

    private static void spawnFallingStalactite(BlockState p_154098_, ServerLevel p_154099_, BlockPos p_154100_) {
        BlockPos.MutableBlockPos $$3 = p_154100_.mutable();

        for(BlockState $$4 = p_154098_; isStalactite($$4); $$4 = p_154099_.getBlockState($$3)) {
            FallingBlockEntity $$5 = FallingBlockEntity.fall(p_154099_, $$3, $$4);
            if (isTip($$4, true)) {
                int $$6 = Math.max(1 + p_154100_.getY() - $$3.getY(), 6);
                float $$7 = 1.0F * (float)$$6;
                $$5.setHurtsEntities($$7, 40);
                break;
            }

            $$3.move(Direction.DOWN);
        }

    }

    @VisibleForTesting
    public static void growStalactiteOrStalagmiteIfPossible(BlockState p_221888_, ServerLevel p_221889_, BlockPos p_221890_, RandomSource p_221891_) {
        BlockState $$4 = p_221889_.getBlockState(p_221890_.above(1));
        BlockState $$5 = p_221889_.getBlockState(p_221890_.above(2));
        if (canGrow($$4, $$5)) {
            BlockPos $$6 = findTip(p_221888_, p_221889_, p_221890_, 7, false);
            if ($$6 != null) {
                BlockState $$7 = p_221889_.getBlockState($$6);
                if (canDrip($$7) && canTipGrow($$7, p_221889_, $$6)) {
                    if (p_221891_.nextBoolean()) {
                        grow(p_221889_, $$6, Direction.DOWN);
                    } else {
                        growStalagmiteBelow(p_221889_, $$6);
                    }

                }
            }
        }
    }

    private static void growStalagmiteBelow(ServerLevel p_154033_, BlockPos p_154034_) {
        BlockPos.MutableBlockPos $$2 = p_154034_.mutable();

        for(int $$3 = 0; $$3 < 10; ++$$3) {
            $$2.move(Direction.DOWN);
            BlockState $$4 = p_154033_.getBlockState($$2);
            if (!$$4.getFluidState().isEmpty()) {
                return;
            }

            if (isUnmergedTipWithDirection($$4, Direction.UP) && canTipGrow($$4, p_154033_, $$2)) {
                grow(p_154033_, $$2, Direction.UP);
                return;
            }

            if (isValidPointedDripstonePlacement(p_154033_, $$2, Direction.UP) && !p_154033_.isWaterAt($$2.below())) {
                grow(p_154033_, $$2.below(), Direction.UP);
                return;
            }

            if (!canDripThrough(p_154033_, $$2, $$4)) {
                return;
            }
        }

    }

    private static void grow(ServerLevel p_154036_, BlockPos p_154037_, Direction p_154038_) {
        BlockPos $$3 = p_154037_.relative(p_154038_);
        BlockState $$4 = p_154036_.getBlockState($$3);
        if (isUnmergedTipWithDirection($$4, p_154038_.getOpposite())) {
            createMergedTips($$4, p_154036_, $$3);
        } else if ($$4.isAir() || $$4.is(Blocks.WATER)) {
            createDripstone(p_154036_, $$3, p_154038_, DripstoneThickness.TIP);
        }

    }

    private static void createDripstone(LevelAccessor p_154088_, BlockPos p_154089_, Direction p_154090_, DripstoneThickness p_154091_) {
        BlockState $$4 = (BlockState)((BlockState)((BlockState)Blocks.POINTED_DRIPSTONE.defaultBlockState().setValue(TIP_DIRECTION, p_154090_)).setValue(THICKNESS, p_154091_)).setValue(WATERLOGGED, p_154088_.getFluidState(p_154089_).getType() == Fluids.WATER);
        p_154088_.setBlock(p_154089_, $$4, 3);
    }

    private static void createMergedTips(BlockState p_154231_, LevelAccessor p_154232_, BlockPos p_154233_) {
        BlockPos $$5;
        BlockPos $$6;
        if (p_154231_.getValue(TIP_DIRECTION) == Direction.UP) {
            $$6 = p_154233_;
            $$5 = p_154233_.above();
        } else {
            $$5 = p_154233_;
            $$6 = p_154233_.below();
        }

        createDripstone(p_154232_, $$5, Direction.DOWN, DripstoneThickness.TIP_MERGE);
        createDripstone(p_154232_, $$6, Direction.UP, DripstoneThickness.TIP_MERGE);
    }

    public static void spawnDripParticle(Level p_154063_, BlockPos p_154064_, BlockState p_154065_) {
        getFluidAboveStalactite(p_154063_, p_154064_, p_154065_).ifPresent((p_221856_) -> {
            spawnDripParticle(p_154063_, p_154064_, p_154065_, p_221856_.fluid);
        });
    }

    private static void spawnDripParticle(Level p_154072_, BlockPos p_154073_, BlockState p_154074_, Fluid p_154075_) {
        Vec3 $$4 = p_154074_.getOffset(p_154072_, p_154073_);
        double $$5 = 0.0625;
        double $$6 = (double)p_154073_.getX() + 0.5 + $$4.x;
        double $$7 = (double)((float)(p_154073_.getY() + 1) - 0.6875F) - 0.0625;
        double $$8 = (double)p_154073_.getZ() + 0.5 + $$4.z;
        Fluid $$9 = getDripFluid(p_154072_, p_154075_);
        ParticleOptions $$10 = $$9.is(FluidTags.LAVA) ? ParticleTypes.DRIPPING_DRIPSTONE_LAVA : ParticleTypes.DRIPPING_DRIPSTONE_WATER;
        p_154072_.addParticle($$10, $$6, $$7, $$8, 0.0, 0.0, 0.0);
    }

    @Nullable
    private static BlockPos findTip(BlockState p_154131_, LevelAccessor p_154132_, BlockPos p_154133_, int p_154134_, boolean p_154135_) {
        if (isTip(p_154131_, p_154135_)) {
            return p_154133_;
        } else {
            Direction $$5 = (Direction)p_154131_.getValue(TIP_DIRECTION);
            BiPredicate<BlockPos, BlockState> $$6 = (p_202023_, p_202024_) -> {
                return p_202024_.is(Blocks.POINTED_DRIPSTONE) && p_202024_.getValue(TIP_DIRECTION) == $$5;
            };
            return (BlockPos)findBlockVertical(p_154132_, p_154133_, $$5.getAxisDirection(), $$6, (p_154168_) -> {
                return isTip(p_154168_, p_154135_);
            }, p_154134_).orElse((Object)null);
        }
    }

    @Nullable
    private static Direction calculateTipDirection(LevelReader p_154191_, BlockPos p_154192_, Direction p_154193_) {
        Direction $$5;
        if (isValidPointedDripstonePlacement(p_154191_, p_154192_, p_154193_)) {
            $$5 = p_154193_;
        } else {
            if (!isValidPointedDripstonePlacement(p_154191_, p_154192_, p_154193_.getOpposite())) {
                return null;
            }

            $$5 = p_154193_.getOpposite();
        }

        return $$5;
    }

    private static DripstoneThickness calculateDripstoneThickness(LevelReader p_154093_, BlockPos p_154094_, Direction p_154095_, boolean p_154096_) {
        Direction $$4 = p_154095_.getOpposite();
        BlockState $$5 = p_154093_.getBlockState(p_154094_.relative(p_154095_));
        if (isPointedDripstoneWithDirection($$5, $$4)) {
            return !p_154096_ && $$5.getValue(THICKNESS) != DripstoneThickness.TIP_MERGE ? DripstoneThickness.TIP : DripstoneThickness.TIP_MERGE;
        } else if (!isPointedDripstoneWithDirection($$5, p_154095_)) {
            return DripstoneThickness.TIP;
        } else {
            DripstoneThickness $$6 = (DripstoneThickness)$$5.getValue(THICKNESS);
            if ($$6 != DripstoneThickness.TIP && $$6 != DripstoneThickness.TIP_MERGE) {
                BlockState $$7 = p_154093_.getBlockState(p_154094_.relative($$4));
                return !isPointedDripstoneWithDirection($$7, p_154095_) ? DripstoneThickness.BASE : DripstoneThickness.MIDDLE;
            } else {
                return DripstoneThickness.FRUSTUM;
            }
        }
    }

    public static boolean canDrip(BlockState p_154239_) {
        return isStalactite(p_154239_) && p_154239_.getValue(THICKNESS) == DripstoneThickness.TIP && !(Boolean)p_154239_.getValue(WATERLOGGED);
    }

    private static boolean canTipGrow(BlockState p_154195_, ServerLevel p_154196_, BlockPos p_154197_) {
        Direction $$3 = (Direction)p_154195_.getValue(TIP_DIRECTION);
        BlockPos $$4 = p_154197_.relative($$3);
        BlockState $$5 = p_154196_.getBlockState($$4);
        if (!$$5.getFluidState().isEmpty()) {
            return false;
        } else {
            return $$5.isAir() ? true : isUnmergedTipWithDirection($$5, $$3.getOpposite());
        }
    }

    private static Optional<BlockPos> findRootBlock(Level p_154067_, BlockPos p_154068_, BlockState p_154069_, int p_154070_) {
        Direction $$4 = (Direction)p_154069_.getValue(TIP_DIRECTION);
        BiPredicate<BlockPos, BlockState> $$5 = (p_202015_, p_202016_) -> {
            return p_202016_.is(Blocks.POINTED_DRIPSTONE) && p_202016_.getValue(TIP_DIRECTION) == $$4;
        };
        return findBlockVertical(p_154067_, p_154068_, $$4.getOpposite().getAxisDirection(), $$5, (p_154245_) -> {
            return !p_154245_.is(Blocks.POINTED_DRIPSTONE);
        }, p_154070_);
    }

    private static boolean isValidPointedDripstonePlacement(LevelReader p_154222_, BlockPos p_154223_, Direction p_154224_) {
        BlockPos $$3 = p_154223_.relative(p_154224_.getOpposite());
        BlockState $$4 = p_154222_.getBlockState($$3);
        return $$4.isFaceSturdy(p_154222_, $$3, p_154224_) || isPointedDripstoneWithDirection($$4, p_154224_);
    }

    private static boolean isTip(BlockState p_154154_, boolean p_154155_) {
        if (!p_154154_.is(Blocks.POINTED_DRIPSTONE)) {
            return false;
        } else {
            DripstoneThickness $$2 = (DripstoneThickness)p_154154_.getValue(THICKNESS);
            return $$2 == DripstoneThickness.TIP || p_154155_ && $$2 == DripstoneThickness.TIP_MERGE;
        }
    }

    private static boolean isUnmergedTipWithDirection(BlockState p_154144_, Direction p_154145_) {
        return isTip(p_154144_, false) && p_154144_.getValue(TIP_DIRECTION) == p_154145_;
    }

    private static boolean isStalactite(BlockState p_154241_) {
        return isPointedDripstoneWithDirection(p_154241_, Direction.DOWN);
    }

    private static boolean isStalagmite(BlockState p_154243_) {
        return isPointedDripstoneWithDirection(p_154243_, Direction.UP);
    }

    private static boolean isStalactiteStartPos(BlockState p_154204_, LevelReader p_154205_, BlockPos p_154206_) {
        return isStalactite(p_154204_) && !p_154205_.getBlockState(p_154206_.above()).is(Blocks.POINTED_DRIPSTONE);
    }

    public boolean isPathfindable(BlockState p_154112_, BlockGetter p_154113_, BlockPos p_154114_, PathComputationType p_154115_) {
        return false;
    }

    private static boolean isPointedDripstoneWithDirection(BlockState p_154208_, Direction p_154209_) {
        return p_154208_.is(Blocks.POINTED_DRIPSTONE) && p_154208_.getValue(TIP_DIRECTION) == p_154209_;
    }

    @Nullable
    private static BlockPos findFillableCauldronBelowStalactiteTip(Level p_154077_, BlockPos p_154078_, Fluid p_154079_) {
        Predicate<BlockState> $$3 = (p_154162_) -> {
            return p_154162_.getBlock() instanceof AbstractCauldronBlock && ((AbstractCauldronBlock)p_154162_.getBlock()).canReceiveStalactiteDrip(p_154079_);
        };
        BiPredicate<BlockPos, BlockState> $$4 = (p_202034_, p_202035_) -> {
            return canDripThrough(p_154077_, p_202034_, p_202035_);
        };
        return (BlockPos)findBlockVertical(p_154077_, p_154078_, Direction.DOWN.getAxisDirection(), $$4, $$3, 11).orElse((Object)null);
    }

    @Nullable
    public static BlockPos findStalactiteTipAboveCauldron(Level p_154056_, BlockPos p_154057_) {
        BiPredicate<BlockPos, BlockState> $$2 = (p_202030_, p_202031_) -> {
            return canDripThrough(p_154056_, p_202030_, p_202031_);
        };
        return (BlockPos)findBlockVertical(p_154056_, p_154057_, Direction.UP.getAxisDirection(), $$2, PointedDripstoneBlock::canDrip, 11).orElse((Object)null);
    }

    public static Fluid getCauldronFillFluidType(ServerLevel p_221850_, BlockPos p_221851_) {
        return (Fluid)getFluidAboveStalactite(p_221850_, p_221851_, p_221850_.getBlockState(p_221851_)).map((p_221858_) -> {
            return p_221858_.fluid;
        }).filter(PointedDripstoneBlock::canFillCauldron).orElse(Fluids.EMPTY);
    }

    private static Optional<FluidInfo> getFluidAboveStalactite(Level p_154182_, BlockPos p_154183_, BlockState p_154184_) {
        return !isStalactite(p_154184_) ? Optional.empty() : findRootBlock(p_154182_, p_154183_, p_154184_, 11).map((p_221876_) -> {
            BlockPos $$2 = p_221876_.above();
            BlockState $$3 = p_154182_.getBlockState($$2);
            Object $$5;
            if ($$3.is(Blocks.MUD) && !p_154182_.dimensionType().ultraWarm()) {
                $$5 = Fluids.WATER;
            } else {
                $$5 = p_154182_.getFluidState($$2).getType();
            }

            return new FluidInfo($$2, (Fluid)$$5, $$3);
        });
    }

    private static boolean canFillCauldron(Fluid p_154159_) {
        return p_154159_ == Fluids.LAVA || p_154159_ == Fluids.WATER;
    }

    private static boolean canGrow(BlockState p_154141_, BlockState p_154142_) {
        return p_154141_.is(Blocks.DRIPSTONE_BLOCK) && p_154142_.is(Blocks.WATER) && p_154142_.getFluidState().isSource();
    }

    private static Fluid getDripFluid(Level p_154053_, Fluid p_154054_) {
        if (p_154054_.isSame(Fluids.EMPTY)) {
            return p_154053_.dimensionType().ultraWarm() ? Fluids.LAVA : Fluids.WATER;
        } else {
            return p_154054_;
        }
    }

    private static Optional<BlockPos> findBlockVertical(LevelAccessor p_202007_, BlockPos p_202008_, Direction.AxisDirection p_202009_, BiPredicate<BlockPos, BlockState> p_202010_, Predicate<BlockState> p_202011_, int p_202012_) {
        Direction $$6 = Direction.get(p_202009_, Axis.Y);
        BlockPos.MutableBlockPos $$7 = p_202008_.mutable();

        for(int $$8 = 1; $$8 < p_202012_; ++$$8) {
            $$7.move($$6);
            BlockState $$9 = p_202007_.getBlockState($$7);
            if (p_202011_.test($$9)) {
                return Optional.of($$7.immutable());
            }

            if (p_202007_.isOutsideBuildHeight($$7.getY()) || !p_202010_.test($$7, $$9)) {
                return Optional.empty();
            }
        }

        return Optional.empty();
    }

    private static boolean canDripThrough(BlockGetter p_202018_, BlockPos p_202019_, BlockState p_202020_) {
        if (p_202020_.isAir()) {
            return true;
        } else if (p_202020_.isSolidRender(p_202018_, p_202019_)) {
            return false;
        } else if (!p_202020_.getFluidState().isEmpty()) {
            return false;
        } else {
            VoxelShape $$3 = p_202020_.getCollisionShape(p_202018_, p_202019_);
            return !Shapes.joinIsNotEmpty(REQUIRED_SPACE_TO_DRIP_THROUGH_NON_SOLID_BLOCK, $$3, BooleanOp.AND);
        }
    }

    static {
        TIP_DIRECTION = BlockStateProperties.VERTICAL_DIRECTION;
        THICKNESS = BlockStateProperties.DRIPSTONE_THICKNESS;
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
        TIP_MERGE_SHAPE = Block.box(5.0, 0.0, 5.0, 11.0, 16.0, 11.0);
        TIP_SHAPE_UP = Block.box(5.0, 0.0, 5.0, 11.0, 11.0, 11.0);
        TIP_SHAPE_DOWN = Block.box(5.0, 5.0, 5.0, 11.0, 16.0, 11.0);
        FRUSTUM_SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);
        MIDDLE_SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 16.0, 13.0);
        BASE_SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);
        REQUIRED_SPACE_TO_DRIP_THROUGH_NON_SOLID_BLOCK = Block.box(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);
    }

    static record FluidInfo(BlockPos pos, Fluid fluid, BlockState sourceState) {
        FluidInfo(BlockPos pos, Fluid fluid, BlockState sourceState) {
            this.pos = pos;
            this.fluid = fluid;
            this.sourceState = sourceState;
        }

        public BlockPos pos() {
            return this.pos;
        }

        public Fluid fluid() {
            return this.fluid;
        }

        public BlockState sourceState() {
            return this.sourceState;
        }
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block.piston;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.event.ForgeEventFactory;

public class PistonBaseBlock extends DirectionalBlock {
    public static final BooleanProperty EXTENDED;
    public static final int TRIGGER_EXTEND = 0;
    public static final int TRIGGER_CONTRACT = 1;
    public static final int TRIGGER_DROP = 2;
    public static final float PLATFORM_THICKNESS = 4.0F;
    protected static final VoxelShape EAST_AABB;
    protected static final VoxelShape WEST_AABB;
    protected static final VoxelShape SOUTH_AABB;
    protected static final VoxelShape NORTH_AABB;
    protected static final VoxelShape UP_AABB;
    protected static final VoxelShape DOWN_AABB;
    private final boolean isSticky;

    public PistonBaseBlock(boolean p_60163_, BlockBehaviour.Properties p_60164_) {
        super(p_60164_);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(EXTENDED, false));
        this.isSticky = p_60163_;
    }

    public VoxelShape getShape(BlockState p_60220_, BlockGetter p_60221_, BlockPos p_60222_, CollisionContext p_60223_) {
        if ((Boolean)p_60220_.getValue(EXTENDED)) {
            switch ((Direction)p_60220_.getValue(FACING)) {
                case DOWN:
                    return DOWN_AABB;
                case UP:
                default:
                    return UP_AABB;
                case NORTH:
                    return NORTH_AABB;
                case SOUTH:
                    return SOUTH_AABB;
                case WEST:
                    return WEST_AABB;
                case EAST:
                    return EAST_AABB;
            }
        } else {
            return Shapes.block();
        }
    }

    public void setPlacedBy(Level p_60172_, BlockPos p_60173_, BlockState p_60174_, LivingEntity p_60175_, ItemStack p_60176_) {
        if (!p_60172_.isClientSide) {
            this.checkIfExtend(p_60172_, p_60173_, p_60174_);
        }

    }

    public void neighborChanged(BlockState p_60198_, Level p_60199_, BlockPos p_60200_, Block p_60201_, BlockPos p_60202_, boolean p_60203_) {
        if (!p_60199_.isClientSide) {
            this.checkIfExtend(p_60199_, p_60200_, p_60198_);
        }

    }

    public void onPlace(BlockState p_60225_, Level p_60226_, BlockPos p_60227_, BlockState p_60228_, boolean p_60229_) {
        if (!p_60228_.is(p_60225_.getBlock()) && !p_60226_.isClientSide && p_60226_.getBlockEntity(p_60227_) == null) {
            this.checkIfExtend(p_60226_, p_60227_, p_60225_);
        }

    }

    public BlockState getStateForPlacement(BlockPlaceContext p_60166_) {
        return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, p_60166_.getNearestLookingDirection().getOpposite())).setValue(EXTENDED, false);
    }

    private void checkIfExtend(Level p_60168_, BlockPos p_60169_, BlockState p_60170_) {
        Direction direction = (Direction)p_60170_.getValue(FACING);
        boolean flag = this.getNeighborSignal(p_60168_, p_60169_, direction);
        if (flag && !(Boolean)p_60170_.getValue(EXTENDED)) {
            if ((new PistonStructureResolver(p_60168_, p_60169_, direction, true)).resolve()) {
                p_60168_.blockEvent(p_60169_, this, 0, direction.get3DDataValue());
            }
        } else if (!flag && (Boolean)p_60170_.getValue(EXTENDED)) {
            BlockPos blockpos = p_60169_.relative((Direction)direction, 2);
            BlockState blockstate = p_60168_.getBlockState(blockpos);
            int i = 1;
            if (blockstate.is(Blocks.MOVING_PISTON) && blockstate.getValue(FACING) == direction) {
                BlockEntity blockentity = p_60168_.getBlockEntity(blockpos);
                if (blockentity instanceof PistonMovingBlockEntity) {
                    PistonMovingBlockEntity pistonmovingblockentity = (PistonMovingBlockEntity)blockentity;
                    if (pistonmovingblockentity.isExtending() && (pistonmovingblockentity.getProgress(0.0F) < 0.5F || p_60168_.getGameTime() == pistonmovingblockentity.getLastTicked() || ((ServerLevel)p_60168_).isHandlingTick())) {
                        i = 2;
                    }
                }
            }

            p_60168_.blockEvent(p_60169_, this, i, direction.get3DDataValue());
        }

    }

    private boolean getNeighborSignal(SignalGetter p_277378_, BlockPos p_60179_, Direction p_60180_) {
        Direction[] var4 = Direction.values();
        int var5 = var4.length;

        int var6;
        for(var6 = 0; var6 < var5; ++var6) {
            Direction direction = var4[var6];
            if (direction != p_60180_ && p_277378_.hasSignal(p_60179_.relative(direction), direction)) {
                return true;
            }
        }

        if (p_277378_.hasSignal(p_60179_, Direction.DOWN)) {
            return true;
        } else {
            BlockPos blockpos = p_60179_.above();
            Direction[] var10 = Direction.values();
            var6 = var10.length;

            for(int var11 = 0; var11 < var6; ++var11) {
                Direction direction1 = var10[var11];
                if (direction1 != Direction.DOWN && p_277378_.hasSignal(blockpos.relative(direction1), direction1)) {
                    return true;
                }
            }

            return false;
        }
    }

    public boolean triggerEvent(BlockState p_60192_, Level p_60193_, BlockPos p_60194_, int p_60195_, int p_60196_) {
        Direction direction = (Direction)p_60192_.getValue(FACING);
        BlockState blockstate = (BlockState)p_60192_.setValue(EXTENDED, true);
        if (!p_60193_.isClientSide) {
            boolean flag = this.getNeighborSignal(p_60193_, p_60194_, direction);
            if (flag && (p_60195_ == 1 || p_60195_ == 2)) {
                p_60193_.setBlock(p_60194_, blockstate, 2);
                return false;
            }

            if (!flag && p_60195_ == 0) {
                return false;
            }
        }

        if (p_60195_ == 0) {
            if (ForgeEventFactory.onPistonMovePre(p_60193_, p_60194_, direction, true)) {
                return false;
            }

            if (!this.moveBlocks(p_60193_, p_60194_, direction, true)) {
                return false;
            }

            p_60193_.setBlock(p_60194_, blockstate, 67);
            p_60193_.playSound((Player)null, p_60194_, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.5F, p_60193_.random.nextFloat() * 0.25F + 0.6F);
            p_60193_.gameEvent(GameEvent.BLOCK_ACTIVATE, p_60194_, Context.of(blockstate));
        } else if (p_60195_ == 1 || p_60195_ == 2) {
            if (ForgeEventFactory.onPistonMovePre(p_60193_, p_60194_, direction, false)) {
                return false;
            }

            BlockEntity blockentity1 = p_60193_.getBlockEntity(p_60194_.relative(direction));
            if (blockentity1 instanceof PistonMovingBlockEntity) {
                ((PistonMovingBlockEntity)blockentity1).finalTick();
            }

            BlockState blockstate1 = (BlockState)((BlockState)Blocks.MOVING_PISTON.defaultBlockState().setValue(MovingPistonBlock.FACING, direction)).setValue(MovingPistonBlock.TYPE, this.isSticky ? PistonType.STICKY : PistonType.DEFAULT);
            p_60193_.setBlock(p_60194_, blockstate1, 20);
            p_60193_.setBlockEntity(MovingPistonBlock.newMovingBlockEntity(p_60194_, blockstate1, (BlockState)this.defaultBlockState().setValue(FACING, Direction.from3DDataValue(p_60196_ & 7)), direction, false, true));
            p_60193_.blockUpdated(p_60194_, blockstate1.getBlock());
            blockstate1.updateNeighbourShapes(p_60193_, p_60194_, 2);
            if (this.isSticky) {
                BlockPos blockpos = p_60194_.offset(direction.getStepX() * 2, direction.getStepY() * 2, direction.getStepZ() * 2);
                BlockState blockstate2 = p_60193_.getBlockState(blockpos);
                boolean flag1 = false;
                if (blockstate2.is(Blocks.MOVING_PISTON)) {
                    BlockEntity blockentity = p_60193_.getBlockEntity(blockpos);
                    if (blockentity instanceof PistonMovingBlockEntity) {
                        PistonMovingBlockEntity pistonmovingblockentity = (PistonMovingBlockEntity)blockentity;
                        if (pistonmovingblockentity.getDirection() == direction && pistonmovingblockentity.isExtending()) {
                            pistonmovingblockentity.finalTick();
                            flag1 = true;
                        }
                    }
                }

                if (!flag1) {
                    if (p_60195_ == 1 && !blockstate2.isAir() && isPushable(blockstate2, p_60193_, blockpos, direction.getOpposite(), false, direction) && (blockstate2.getPistonPushReaction() == PushReaction.NORMAL || blockstate2.is(Blocks.PISTON) || blockstate2.is(Blocks.STICKY_PISTON))) {
                        this.moveBlocks(p_60193_, p_60194_, direction, false);
                    } else {
                        p_60193_.removeBlock(p_60194_.relative(direction), false);
                    }
                }
            } else {
                p_60193_.removeBlock(p_60194_.relative(direction), false);
            }

            p_60193_.playSound((Player)null, p_60194_, SoundEvents.PISTON_CONTRACT, SoundSource.BLOCKS, 0.5F, p_60193_.random.nextFloat() * 0.15F + 0.6F);
            p_60193_.gameEvent(GameEvent.BLOCK_DEACTIVATE, p_60194_, Context.of(blockstate1));
        }

        ForgeEventFactory.onPistonMovePost(p_60193_, p_60194_, direction, p_60195_ == 0);
        return true;
    }

    public static boolean isPushable(BlockState p_60205_, Level p_60206_, BlockPos p_60207_, Direction p_60208_, boolean p_60209_, Direction p_60210_) {
        if (p_60207_.getY() >= p_60206_.getMinBuildHeight() && p_60207_.getY() <= p_60206_.getMaxBuildHeight() - 1 && p_60206_.getWorldBorder().isWithinBounds(p_60207_)) {
            if (p_60205_.isAir()) {
                return true;
            } else if (!p_60205_.is(Blocks.OBSIDIAN) && !p_60205_.is(Blocks.CRYING_OBSIDIAN) && !p_60205_.is(Blocks.RESPAWN_ANCHOR) && !p_60205_.is(Blocks.REINFORCED_DEEPSLATE)) {
                if (p_60208_ == Direction.DOWN && p_60207_.getY() == p_60206_.getMinBuildHeight()) {
                    return false;
                } else if (p_60208_ == Direction.UP && p_60207_.getY() == p_60206_.getMaxBuildHeight() - 1) {
                    return false;
                } else {
                    if (!p_60205_.is(Blocks.PISTON) && !p_60205_.is(Blocks.STICKY_PISTON)) {
                        if (p_60205_.getDestroySpeed(p_60206_, p_60207_) == -1.0F) {
                            return false;
                        }

                        switch (p_60205_.getPistonPushReaction()) {
                            case BLOCK -> return false;
                            case DESTROY -> return p_60209_;
                            case PUSH_ONLY -> return p_60208_ == p_60210_;
                        }
                    } else if ((Boolean)p_60205_.getValue(EXTENDED)) {
                        return false;
                    }

                    return !p_60205_.hasBlockEntity();
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean moveBlocks(Level p_60182_, BlockPos p_60183_, Direction p_60184_, boolean p_60185_) {
        BlockPos blockpos = p_60183_.relative(p_60184_);
        if (!p_60185_ && p_60182_.getBlockState(blockpos).is(Blocks.PISTON_HEAD)) {
            p_60182_.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 20);
        }

        PistonStructureResolver pistonstructureresolver = new PistonStructureResolver(p_60182_, p_60183_, p_60184_, p_60185_);
        if (!pistonstructureresolver.resolve()) {
            return false;
        } else {
            Map<BlockPos, BlockState> map = Maps.newHashMap();
            List<BlockPos> list = pistonstructureresolver.getToPush();
            List<BlockState> list1 = Lists.newArrayList();

            for(int i = 0; i < list.size(); ++i) {
                BlockPos blockpos1 = (BlockPos)list.get(i);
                BlockState blockstate = p_60182_.getBlockState(blockpos1);
                list1.add(blockstate);
                map.put(blockpos1, blockstate);
            }

            List<BlockPos> list2 = pistonstructureresolver.getToDestroy();
            BlockState[] ablockstate = new BlockState[list.size() + list2.size()];
            Direction direction = p_60185_ ? p_60184_ : p_60184_.getOpposite();
            int j = 0;

            int l;
            BlockPos blockpos3;
            BlockState blockstate7;
            for(l = list2.size() - 1; l >= 0; --l) {
                blockpos3 = (BlockPos)list2.get(l);
                blockstate7 = p_60182_.getBlockState(blockpos3);
                BlockEntity blockentity = blockstate7.hasBlockEntity() ? p_60182_.getBlockEntity(blockpos3) : null;
                dropResources(blockstate7, p_60182_, blockpos3, blockentity);
                p_60182_.setBlock(blockpos3, Blocks.AIR.defaultBlockState(), 18);
                p_60182_.gameEvent(GameEvent.BLOCK_DESTROY, blockpos3, Context.of(blockstate7));
                if (!blockstate7.is(BlockTags.FIRE)) {
                    p_60182_.addDestroyBlockEffect(blockpos3, blockstate7);
                }

                ablockstate[j++] = blockstate7;
            }

            for(l = list.size() - 1; l >= 0; --l) {
                blockpos3 = (BlockPos)list.get(l);
                blockstate7 = p_60182_.getBlockState(blockpos3);
                blockpos3 = blockpos3.relative(direction);
                map.remove(blockpos3);
                BlockState blockstate8 = (BlockState)Blocks.MOVING_PISTON.defaultBlockState().setValue(FACING, p_60184_);
                p_60182_.setBlock(blockpos3, blockstate8, 68);
                p_60182_.setBlockEntity(MovingPistonBlock.newMovingBlockEntity(blockpos3, blockstate8, (BlockState)list1.get(l), p_60184_, p_60185_, false));
                ablockstate[j++] = blockstate7;
            }

            if (p_60185_) {
                PistonType pistontype = this.isSticky ? PistonType.STICKY : PistonType.DEFAULT;
                BlockState blockstate4 = (BlockState)((BlockState)Blocks.PISTON_HEAD.defaultBlockState().setValue(PistonHeadBlock.FACING, p_60184_)).setValue(PistonHeadBlock.TYPE, pistontype);
                blockstate7 = (BlockState)((BlockState)Blocks.MOVING_PISTON.defaultBlockState().setValue(MovingPistonBlock.FACING, p_60184_)).setValue(MovingPistonBlock.TYPE, this.isSticky ? PistonType.STICKY : PistonType.DEFAULT);
                map.remove(blockpos);
                p_60182_.setBlock(blockpos, blockstate7, 68);
                p_60182_.setBlockEntity(MovingPistonBlock.newMovingBlockEntity(blockpos, blockstate7, blockstate4, p_60184_, true, true));
            }

            BlockState blockstate3 = Blocks.AIR.defaultBlockState();
            Iterator var25 = map.keySet().iterator();

            while(var25.hasNext()) {
                BlockPos blockpos4 = (BlockPos)var25.next();
                p_60182_.setBlock(blockpos4, blockstate3, 82);
            }

            var25 = map.entrySet().iterator();

            BlockPos blockpos6;
            while(var25.hasNext()) {
                Map.Entry<BlockPos, BlockState> entry = (Map.Entry)var25.next();
                blockpos6 = (BlockPos)entry.getKey();
                BlockState blockstate2 = (BlockState)entry.getValue();
                blockstate2.updateIndirectNeighbourShapes(p_60182_, blockpos6, 2);
                blockstate3.updateNeighbourShapes(p_60182_, blockpos6, 2);
                blockstate3.updateIndirectNeighbourShapes(p_60182_, blockpos6, 2);
            }

            j = 0;

            int j1;
            for(j1 = list2.size() - 1; j1 >= 0; --j1) {
                blockstate7 = ablockstate[j++];
                blockpos6 = (BlockPos)list2.get(j1);
                blockstate7.updateIndirectNeighbourShapes(p_60182_, blockpos6, 2);
                p_60182_.updateNeighborsAt(blockpos6, blockstate7.getBlock());
            }

            for(j1 = list.size() - 1; j1 >= 0; --j1) {
                p_60182_.updateNeighborsAt((BlockPos)list.get(j1), ablockstate[j++].getBlock());
            }

            if (p_60185_) {
                p_60182_.updateNeighborsAt(blockpos, Blocks.PISTON_HEAD);
            }

            return true;
        }
    }

    public BlockState rotate(BlockState p_60215_, Rotation p_60216_) {
        return (BlockState)p_60215_.setValue(FACING, p_60216_.rotate((Direction)p_60215_.getValue(FACING)));
    }

    public BlockState rotate(BlockState state, LevelAccessor world, BlockPos pos, Rotation direction) {
        return (Boolean)state.getValue(EXTENDED) ? state : super.rotate(state, world, pos, direction);
    }

    public BlockState mirror(BlockState p_60212_, Mirror p_60213_) {
        return p_60212_.rotate(p_60213_.getRotation((Direction)p_60212_.getValue(FACING)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_60218_) {
        p_60218_.add(FACING, EXTENDED);
    }

    public boolean useShapeForLightOcclusion(BlockState p_60231_) {
        return (Boolean)p_60231_.getValue(EXTENDED);
    }

    public boolean isPathfindable(BlockState p_60187_, BlockGetter p_60188_, BlockPos p_60189_, PathComputationType p_60190_) {
        return false;
    }

    static {
        EXTENDED = BlockStateProperties.EXTENDED;
        EAST_AABB = Block.box(0.0, 0.0, 0.0, 12.0, 16.0, 16.0);
        WEST_AABB = Block.box(4.0, 0.0, 0.0, 16.0, 16.0, 16.0);
        SOUTH_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 12.0);
        NORTH_AABB = Block.box(0.0, 0.0, 4.0, 16.0, 16.0, 16.0);
        UP_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);
        DOWN_AABB = Block.box(0.0, 4.0, 0.0, 16.0, 16.0, 16.0);
    }
}

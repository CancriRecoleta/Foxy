//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LeverBlock extends FaceAttachedHorizontalDirectionalBlock {
    public static final BooleanProperty POWERED;
    protected static final int DEPTH = 6;
    protected static final int WIDTH = 6;
    protected static final int HEIGHT = 8;
    protected static final VoxelShape NORTH_AABB;
    protected static final VoxelShape SOUTH_AABB;
    protected static final VoxelShape WEST_AABB;
    protected static final VoxelShape EAST_AABB;
    protected static final VoxelShape UP_AABB_Z;
    protected static final VoxelShape UP_AABB_X;
    protected static final VoxelShape DOWN_AABB_Z;
    protected static final VoxelShape DOWN_AABB_X;

    public LeverBlock(BlockBehaviour.Properties p_54633_) {
        super(p_54633_);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(POWERED, false)).setValue(FACE, AttachFace.WALL));
    }

    public VoxelShape getShape(BlockState p_54665_, BlockGetter p_54666_, BlockPos p_54667_, CollisionContext p_54668_) {
        switch ((AttachFace)p_54665_.getValue(FACE)) {
            case FLOOR:
                switch (((Direction)p_54665_.getValue(FACING)).getAxis()) {
                    case X:
                        return UP_AABB_X;
                    case Z:
                    default:
                        return UP_AABB_Z;
                }
            case WALL:
                switch ((Direction)p_54665_.getValue(FACING)) {
                    case EAST:
                        return EAST_AABB;
                    case WEST:
                        return WEST_AABB;
                    case SOUTH:
                        return SOUTH_AABB;
                    case NORTH:
                    default:
                        return NORTH_AABB;
                }
            case CEILING:
            default:
                switch (((Direction)p_54665_.getValue(FACING)).getAxis()) {
                    case X:
                        return DOWN_AABB_X;
                    case Z:
                    default:
                        return DOWN_AABB_Z;
                }
        }
    }

    public InteractionResult use(BlockState p_54640_, Level p_54641_, BlockPos p_54642_, Player p_54643_, InteractionHand p_54644_, BlockHitResult p_54645_) {
        BlockState $$6;
        if (p_54641_.isClientSide) {
            $$6 = (BlockState)p_54640_.cycle(POWERED);
            if ((Boolean)$$6.getValue(POWERED)) {
                makeParticle($$6, p_54641_, p_54642_, 1.0F);
            }

            return InteractionResult.SUCCESS;
        } else {
            $$6 = this.pull(p_54640_, p_54641_, p_54642_);
            float $$8 = (Boolean)$$6.getValue(POWERED) ? 0.6F : 0.5F;
            p_54641_.playSound((Player)null, (BlockPos)p_54642_, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.3F, $$8);
            p_54641_.gameEvent(p_54643_, (Boolean)$$6.getValue(POWERED) ? GameEvent.BLOCK_ACTIVATE : GameEvent.BLOCK_DEACTIVATE, p_54642_);
            return InteractionResult.CONSUME;
        }
    }

    public BlockState pull(BlockState p_54677_, Level p_54678_, BlockPos p_54679_) {
        p_54677_ = (BlockState)p_54677_.cycle(POWERED);
        p_54678_.setBlock(p_54679_, p_54677_, 3);
        this.updateNeighbours(p_54677_, p_54678_, p_54679_);
        return p_54677_;
    }

    private static void makeParticle(BlockState p_54658_, LevelAccessor p_54659_, BlockPos p_54660_, float p_54661_) {
        Direction $$4 = ((Direction)p_54658_.getValue(FACING)).getOpposite();
        Direction $$5 = getConnectedDirection(p_54658_).getOpposite();
        double $$6 = (double)p_54660_.getX() + 0.5 + 0.1 * (double)$$4.getStepX() + 0.2 * (double)$$5.getStepX();
        double $$7 = (double)p_54660_.getY() + 0.5 + 0.1 * (double)$$4.getStepY() + 0.2 * (double)$$5.getStepY();
        double $$8 = (double)p_54660_.getZ() + 0.5 + 0.1 * (double)$$4.getStepZ() + 0.2 * (double)$$5.getStepZ();
        p_54659_.addParticle(new DustParticleOptions(DustParticleOptions.REDSTONE_PARTICLE_COLOR, p_54661_), $$6, $$7, $$8, 0.0, 0.0, 0.0);
    }

    public void animateTick(BlockState p_221395_, Level p_221396_, BlockPos p_221397_, RandomSource p_221398_) {
        if ((Boolean)p_221395_.getValue(POWERED) && p_221398_.nextFloat() < 0.25F) {
            makeParticle(p_221395_, p_221396_, p_221397_, 0.5F);
        }

    }

    public void onRemove(BlockState p_54647_, Level p_54648_, BlockPos p_54649_, BlockState p_54650_, boolean p_54651_) {
        if (!p_54651_ && !p_54647_.is(p_54650_.getBlock())) {
            if ((Boolean)p_54647_.getValue(POWERED)) {
                this.updateNeighbours(p_54647_, p_54648_, p_54649_);
            }

            super.onRemove(p_54647_, p_54648_, p_54649_, p_54650_, p_54651_);
        }
    }

    public int getSignal(BlockState p_54635_, BlockGetter p_54636_, BlockPos p_54637_, Direction p_54638_) {
        return (Boolean)p_54635_.getValue(POWERED) ? 15 : 0;
    }

    public int getDirectSignal(BlockState p_54670_, BlockGetter p_54671_, BlockPos p_54672_, Direction p_54673_) {
        return (Boolean)p_54670_.getValue(POWERED) && getConnectedDirection(p_54670_) == p_54673_ ? 15 : 0;
    }

    public boolean isSignalSource(BlockState p_54675_) {
        return true;
    }

    private void updateNeighbours(BlockState p_54681_, Level p_54682_, BlockPos p_54683_) {
        p_54682_.updateNeighborsAt(p_54683_, this);
        p_54682_.updateNeighborsAt(p_54683_.relative(getConnectedDirection(p_54681_).getOpposite()), this);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_54663_) {
        p_54663_.add(FACE, FACING, POWERED);
    }

    static {
        POWERED = BlockStateProperties.POWERED;
        NORTH_AABB = Block.box(5.0, 4.0, 10.0, 11.0, 12.0, 16.0);
        SOUTH_AABB = Block.box(5.0, 4.0, 0.0, 11.0, 12.0, 6.0);
        WEST_AABB = Block.box(10.0, 4.0, 5.0, 16.0, 12.0, 11.0);
        EAST_AABB = Block.box(0.0, 4.0, 5.0, 6.0, 12.0, 11.0);
        UP_AABB_Z = Block.box(5.0, 0.0, 4.0, 11.0, 6.0, 12.0);
        UP_AABB_X = Block.box(4.0, 0.0, 5.0, 12.0, 6.0, 11.0);
        DOWN_AABB_Z = Block.box(5.0, 10.0, 4.0, 11.0, 16.0, 12.0);
        DOWN_AABB_X = Block.box(4.0, 10.0, 5.0, 12.0, 16.0, 11.0);
    }
}

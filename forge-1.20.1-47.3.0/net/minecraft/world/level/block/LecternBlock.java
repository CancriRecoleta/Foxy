//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LecternBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING;
    public static final BooleanProperty POWERED;
    public static final BooleanProperty HAS_BOOK;
    public static final VoxelShape SHAPE_BASE;
    public static final VoxelShape SHAPE_POST;
    public static final VoxelShape SHAPE_COMMON;
    public static final VoxelShape SHAPE_TOP_PLATE;
    public static final VoxelShape SHAPE_COLLISION;
    public static final VoxelShape SHAPE_WEST;
    public static final VoxelShape SHAPE_NORTH;
    public static final VoxelShape SHAPE_EAST;
    public static final VoxelShape SHAPE_SOUTH;
    private static final int PAGE_CHANGE_IMPULSE_TICKS = 2;

    public LecternBlock(BlockBehaviour.Properties p_54479_) {
        super(p_54479_);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(POWERED, false)).setValue(HAS_BOOK, false));
    }

    public RenderShape getRenderShape(BlockState p_54559_) {
        return RenderShape.MODEL;
    }

    public VoxelShape getOcclusionShape(BlockState p_54584_, BlockGetter p_54585_, BlockPos p_54586_) {
        return SHAPE_COMMON;
    }

    public boolean useShapeForLightOcclusion(BlockState p_54582_) {
        return true;
    }

    public BlockState getStateForPlacement(BlockPlaceContext p_54481_) {
        Level $$1 = p_54481_.getLevel();
        ItemStack $$2 = p_54481_.getItemInHand();
        Player $$3 = p_54481_.getPlayer();
        boolean $$4 = false;
        if (!$$1.isClientSide && $$3 != null && $$3.canUseGameMasterBlocks()) {
            CompoundTag $$5 = BlockItem.getBlockEntityData($$2);
            if ($$5 != null && $$5.contains("Book")) {
                $$4 = true;
            }
        }

        return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, p_54481_.getHorizontalDirection().getOpposite())).setValue(HAS_BOOK, $$4);
    }

    public VoxelShape getCollisionShape(BlockState p_54577_, BlockGetter p_54578_, BlockPos p_54579_, CollisionContext p_54580_) {
        return SHAPE_COLLISION;
    }

    public VoxelShape getShape(BlockState p_54561_, BlockGetter p_54562_, BlockPos p_54563_, CollisionContext p_54564_) {
        switch ((Direction)p_54561_.getValue(FACING)) {
            case NORTH -> return SHAPE_NORTH;
            case SOUTH -> return SHAPE_SOUTH;
            case EAST -> return SHAPE_EAST;
            case WEST -> return SHAPE_WEST;
            default -> return SHAPE_COMMON;
        }
    }

    public BlockState rotate(BlockState p_54540_, Rotation p_54541_) {
        return (BlockState)p_54540_.setValue(FACING, p_54541_.rotate((Direction)p_54540_.getValue(FACING)));
    }

    public BlockState mirror(BlockState p_54537_, Mirror p_54538_) {
        return p_54537_.rotate(p_54538_.getRotation((Direction)p_54537_.getValue(FACING)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_54543_) {
        p_54543_.add(FACING, POWERED, HAS_BOOK);
    }

    public BlockEntity newBlockEntity(BlockPos p_153573_, BlockState p_153574_) {
        return new LecternBlockEntity(p_153573_, p_153574_);
    }

    public static boolean tryPlaceBook(@Nullable Entity p_270350_, Level p_270604_, BlockPos p_270276_, BlockState p_270445_, ItemStack p_270458_) {
        if (!(Boolean)p_270445_.getValue(HAS_BOOK)) {
            if (!p_270604_.isClientSide) {
                placeBook(p_270350_, p_270604_, p_270276_, p_270445_, p_270458_);
            }

            return true;
        } else {
            return false;
        }
    }

    private static void placeBook(@Nullable Entity p_270891_, Level p_270065_, BlockPos p_270155_, BlockState p_270753_, ItemStack p_270173_) {
        BlockEntity $$5 = p_270065_.getBlockEntity(p_270155_);
        if ($$5 instanceof LecternBlockEntity $$6) {
            $$6.setBook(p_270173_.split(1));
            resetBookState(p_270891_, p_270065_, p_270155_, p_270753_, true);
            p_270065_.playSound((Player)null, (BlockPos)p_270155_, SoundEvents.BOOK_PUT, SoundSource.BLOCKS, 1.0F, 1.0F);
        }

    }

    public static void resetBookState(@Nullable Entity p_270231_, Level p_270114_, BlockPos p_270251_, BlockState p_270758_, boolean p_270452_) {
        BlockState $$5 = (BlockState)((BlockState)p_270758_.setValue(POWERED, false)).setValue(HAS_BOOK, p_270452_);
        p_270114_.setBlock(p_270251_, $$5, 3);
        p_270114_.gameEvent(GameEvent.BLOCK_CHANGE, p_270251_, Context.of(p_270231_, $$5));
        updateBelow(p_270114_, p_270251_, p_270758_);
    }

    public static void signalPageChange(Level p_54489_, BlockPos p_54490_, BlockState p_54491_) {
        changePowered(p_54489_, p_54490_, p_54491_, true);
        p_54489_.scheduleTick(p_54490_, p_54491_.getBlock(), 2);
        p_54489_.levelEvent(1043, p_54490_, 0);
    }

    private static void changePowered(Level p_54554_, BlockPos p_54555_, BlockState p_54556_, boolean p_54557_) {
        p_54554_.setBlock(p_54555_, (BlockState)p_54556_.setValue(POWERED, p_54557_), 3);
        updateBelow(p_54554_, p_54555_, p_54556_);
    }

    private static void updateBelow(Level p_54545_, BlockPos p_54546_, BlockState p_54547_) {
        p_54545_.updateNeighborsAt(p_54546_.below(), p_54547_.getBlock());
    }

    public void tick(BlockState p_221388_, ServerLevel p_221389_, BlockPos p_221390_, RandomSource p_221391_) {
        changePowered(p_221389_, p_221390_, p_221388_, false);
    }

    public void onRemove(BlockState p_54531_, Level p_54532_, BlockPos p_54533_, BlockState p_54534_, boolean p_54535_) {
        if (!p_54531_.is(p_54534_.getBlock())) {
            if ((Boolean)p_54531_.getValue(HAS_BOOK)) {
                this.popBook(p_54531_, p_54532_, p_54533_);
            }

            if ((Boolean)p_54531_.getValue(POWERED)) {
                p_54532_.updateNeighborsAt(p_54533_.below(), this);
            }

            super.onRemove(p_54531_, p_54532_, p_54533_, p_54534_, p_54535_);
        }
    }

    private void popBook(BlockState p_54588_, Level p_54589_, BlockPos p_54590_) {
        BlockEntity $$3 = p_54589_.getBlockEntity(p_54590_);
        if ($$3 instanceof LecternBlockEntity $$4) {
            Direction $$5 = (Direction)p_54588_.getValue(FACING);
            ItemStack $$6 = $$4.getBook().copy();
            float $$7 = 0.25F * (float)$$5.getStepX();
            float $$8 = 0.25F * (float)$$5.getStepZ();
            ItemEntity $$9 = new ItemEntity(p_54589_, (double)p_54590_.getX() + 0.5 + (double)$$7, (double)(p_54590_.getY() + 1), (double)p_54590_.getZ() + 0.5 + (double)$$8, $$6);
            $$9.setDefaultPickUpDelay();
            p_54589_.addFreshEntity($$9);
            $$4.clearContent();
        }

    }

    public boolean isSignalSource(BlockState p_54575_) {
        return true;
    }

    public int getSignal(BlockState p_54515_, BlockGetter p_54516_, BlockPos p_54517_, Direction p_54518_) {
        return (Boolean)p_54515_.getValue(POWERED) ? 15 : 0;
    }

    public int getDirectSignal(BlockState p_54566_, BlockGetter p_54567_, BlockPos p_54568_, Direction p_54569_) {
        return p_54569_ == Direction.UP && (Boolean)p_54566_.getValue(POWERED) ? 15 : 0;
    }

    public boolean hasAnalogOutputSignal(BlockState p_54503_) {
        return true;
    }

    public int getAnalogOutputSignal(BlockState p_54520_, Level p_54521_, BlockPos p_54522_) {
        if ((Boolean)p_54520_.getValue(HAS_BOOK)) {
            BlockEntity $$3 = p_54521_.getBlockEntity(p_54522_);
            if ($$3 instanceof LecternBlockEntity) {
                return ((LecternBlockEntity)$$3).getRedstoneSignal();
            }
        }

        return 0;
    }

    public InteractionResult use(BlockState p_54524_, Level p_54525_, BlockPos p_54526_, Player p_54527_, InteractionHand p_54528_, BlockHitResult p_54529_) {
        if ((Boolean)p_54524_.getValue(HAS_BOOK)) {
            if (!p_54525_.isClientSide) {
                this.openScreen(p_54525_, p_54526_, p_54527_);
            }

            return InteractionResult.sidedSuccess(p_54525_.isClientSide);
        } else {
            ItemStack $$6 = p_54527_.getItemInHand(p_54528_);
            return !$$6.isEmpty() && !$$6.is(ItemTags.LECTERN_BOOKS) ? InteractionResult.CONSUME : InteractionResult.PASS;
        }
    }

    @Nullable
    public MenuProvider getMenuProvider(BlockState p_54571_, Level p_54572_, BlockPos p_54573_) {
        return !(Boolean)p_54571_.getValue(HAS_BOOK) ? null : super.getMenuProvider(p_54571_, p_54572_, p_54573_);
    }

    private void openScreen(Level p_54485_, BlockPos p_54486_, Player p_54487_) {
        BlockEntity $$3 = p_54485_.getBlockEntity(p_54486_);
        if ($$3 instanceof LecternBlockEntity) {
            p_54487_.openMenu((LecternBlockEntity)$$3);
            p_54487_.awardStat(Stats.INTERACT_WITH_LECTERN);
        }

    }

    public boolean isPathfindable(BlockState p_54510_, BlockGetter p_54511_, BlockPos p_54512_, PathComputationType p_54513_) {
        return false;
    }

    static {
        FACING = HorizontalDirectionalBlock.FACING;
        POWERED = BlockStateProperties.POWERED;
        HAS_BOOK = BlockStateProperties.HAS_BOOK;
        SHAPE_BASE = Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
        SHAPE_POST = Block.box(4.0, 2.0, 4.0, 12.0, 14.0, 12.0);
        SHAPE_COMMON = Shapes.or(SHAPE_BASE, SHAPE_POST);
        SHAPE_TOP_PLATE = Block.box(0.0, 15.0, 0.0, 16.0, 15.0, 16.0);
        SHAPE_COLLISION = Shapes.or(SHAPE_COMMON, SHAPE_TOP_PLATE);
        SHAPE_WEST = Shapes.or(Block.box(1.0, 10.0, 0.0, 5.333333, 14.0, 16.0), Block.box(5.333333, 12.0, 0.0, 9.666667, 16.0, 16.0), Block.box(9.666667, 14.0, 0.0, 14.0, 18.0, 16.0), SHAPE_COMMON);
        SHAPE_NORTH = Shapes.or(Block.box(0.0, 10.0, 1.0, 16.0, 14.0, 5.333333), Block.box(0.0, 12.0, 5.333333, 16.0, 16.0, 9.666667), Block.box(0.0, 14.0, 9.666667, 16.0, 18.0, 14.0), SHAPE_COMMON);
        SHAPE_EAST = Shapes.or(Block.box(10.666667, 10.0, 0.0, 15.0, 14.0, 16.0), Block.box(6.333333, 12.0, 0.0, 10.666667, 16.0, 16.0), Block.box(2.0, 14.0, 0.0, 6.333333, 18.0, 16.0), SHAPE_COMMON);
        SHAPE_SOUTH = Shapes.or(Block.box(0.0, 10.0, 10.666667, 16.0, 14.0, 15.0), Block.box(0.0, 12.0, 6.333333, 16.0, 16.0, 10.666667), Block.box(0.0, 14.0, 2.0, 16.0, 18.0, 6.333333), SHAPE_COMMON);
    }
}

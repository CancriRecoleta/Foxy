//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ChiseledBookShelfBlock extends BaseEntityBlock {
    private static final int MAX_BOOKS_IN_STORAGE = 6;
    public static final int BOOKS_PER_ROW = 3;
    public static final List<BooleanProperty> SLOT_OCCUPIED_PROPERTIES;

    public ChiseledBookShelfBlock(BlockBehaviour.Properties p_249989_) {
        super(p_249989_);
        BlockState $$1 = (BlockState)((BlockState)this.stateDefinition.any()).setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH);

        BooleanProperty $$2;
        for(Iterator var3 = SLOT_OCCUPIED_PROPERTIES.iterator(); var3.hasNext(); $$1 = (BlockState)$$1.setValue($$2, false)) {
            $$2 = (BooleanProperty)var3.next();
        }

        this.registerDefaultState($$1);
    }

    public RenderShape getRenderShape(BlockState p_251274_) {
        return RenderShape.MODEL;
    }

    public InteractionResult use(BlockState p_251144_, Level p_251668_, BlockPos p_249108_, Player p_249954_, InteractionHand p_249823_, BlockHitResult p_250640_) {
        BlockEntity var8 = p_251668_.getBlockEntity(p_249108_);
        if (var8 instanceof ChiseledBookShelfBlockEntity $$7) {
            Optional $$8 = getRelativeHitCoordinatesForBlockFace(p_250640_, (Direction)p_251144_.getValue(HorizontalDirectionalBlock.FACING));
            if ($$8.isEmpty()) {
                return InteractionResult.PASS;
            } else {
                int $$9 = getHitSlot((Vec2)$$8.get());
                if ((Boolean)p_251144_.getValue((Property)SLOT_OCCUPIED_PROPERTIES.get($$9))) {
                    removeBook(p_251668_, p_249108_, p_249954_, $$7, $$9);
                    return InteractionResult.sidedSuccess(p_251668_.isClientSide);
                } else {
                    ItemStack $$10 = p_249954_.getItemInHand(p_249823_);
                    if ($$10.is(ItemTags.BOOKSHELF_BOOKS)) {
                        addBook(p_251668_, p_249108_, p_249954_, $$7, $$10, $$9);
                        return InteractionResult.sidedSuccess(p_251668_.isClientSide);
                    } else {
                        return InteractionResult.CONSUME;
                    }
                }
            }
        } else {
            return InteractionResult.PASS;
        }
    }

    private static Optional<Vec2> getRelativeHitCoordinatesForBlockFace(BlockHitResult p_261714_, Direction p_262116_) {
        Direction $$2 = p_261714_.getDirection();
        if (p_262116_ != $$2) {
            return Optional.empty();
        } else {
            BlockPos $$3 = p_261714_.getBlockPos().relative($$2);
            Vec3 $$4 = p_261714_.getLocation().subtract((double)$$3.getX(), (double)$$3.getY(), (double)$$3.getZ());
            double $$5 = $$4.x();
            double $$6 = $$4.y();
            double $$7 = $$4.z();
            Optional var10000;
            switch ($$2) {
                case NORTH:
                    var10000 = Optional.of(new Vec2((float)(1.0 - $$5), (float)$$6));
                    break;
                case SOUTH:
                    var10000 = Optional.of(new Vec2((float)$$5, (float)$$6));
                    break;
                case WEST:
                    var10000 = Optional.of(new Vec2((float)$$7, (float)$$6));
                    break;
                case EAST:
                    var10000 = Optional.of(new Vec2((float)(1.0 - $$7), (float)$$6));
                    break;
                case DOWN:
                case UP:
                    var10000 = Optional.empty();
                    break;
                default:
                    throw new IncompatibleClassChangeError();
            }

            return var10000;
        }
    }

    private static int getHitSlot(Vec2 p_261991_) {
        int $$1 = p_261991_.y >= 0.5F ? 0 : 1;
        int $$2 = getSection(p_261991_.x);
        return $$2 + $$1 * 3;
    }

    private static int getSection(float p_261599_) {
        float $$1 = 0.0625F;
        float $$2 = 0.375F;
        if (p_261599_ < 0.375F) {
            return 0;
        } else {
            float $$3 = 0.6875F;
            return p_261599_ < 0.6875F ? 1 : 2;
        }
    }

    private static void addBook(Level p_262592_, BlockPos p_262669_, Player p_262572_, ChiseledBookShelfBlockEntity p_262606_, ItemStack p_262587_, int p_262692_) {
        if (!p_262592_.isClientSide) {
            p_262572_.awardStat(Stats.ITEM_USED.get(p_262587_.getItem()));
            SoundEvent $$6 = p_262587_.is(Items.ENCHANTED_BOOK) ? SoundEvents.CHISELED_BOOKSHELF_INSERT_ENCHANTED : SoundEvents.CHISELED_BOOKSHELF_INSERT;
            p_262606_.setItem(p_262692_, p_262587_.split(1));
            p_262592_.playSound((Player)null, (BlockPos)p_262669_, $$6, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (p_262572_.isCreative()) {
                p_262587_.grow(1);
            }

            p_262592_.gameEvent(p_262572_, GameEvent.BLOCK_CHANGE, p_262669_);
        }
    }

    private static void removeBook(Level p_262654_, BlockPos p_262601_, Player p_262636_, ChiseledBookShelfBlockEntity p_262605_, int p_262673_) {
        if (!p_262654_.isClientSide) {
            ItemStack $$5 = p_262605_.removeItem(p_262673_, 1);
            SoundEvent $$6 = $$5.is(Items.ENCHANTED_BOOK) ? SoundEvents.CHISELED_BOOKSHELF_PICKUP_ENCHANTED : SoundEvents.CHISELED_BOOKSHELF_PICKUP;
            p_262654_.playSound((Player)null, (BlockPos)p_262601_, $$6, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (!p_262636_.getInventory().add($$5)) {
                p_262636_.drop($$5, false);
            }

            p_262654_.gameEvent(p_262636_, GameEvent.BLOCK_CHANGE, p_262601_);
        }
    }

    public @Nullable BlockEntity newBlockEntity(BlockPos p_250440_, BlockState p_248729_) {
        return new ChiseledBookShelfBlockEntity(p_250440_, p_248729_);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_250973_) {
        p_250973_.add(HorizontalDirectionalBlock.FACING);
        List var10000 = SLOT_OCCUPIED_PROPERTIES;
        Objects.requireNonNull(p_250973_);
        var10000.forEach((p_261456_) -> {
            p_250973_.add(p_261456_);
        });
    }

    public void onRemove(BlockState p_250071_, Level p_251485_, BlockPos p_251954_, BlockState p_251852_, boolean p_252250_) {
        if (!p_250071_.is(p_251852_.getBlock())) {
            BlockEntity $$5 = p_251485_.getBlockEntity(p_251954_);
            if ($$5 instanceof ChiseledBookShelfBlockEntity) {
                ChiseledBookShelfBlockEntity $$6 = (ChiseledBookShelfBlockEntity)$$5;
                if (!$$6.isEmpty()) {
                    for(int $$7 = 0; $$7 < 6; ++$$7) {
                        ItemStack $$8 = $$6.getItem($$7);
                        if (!$$8.isEmpty()) {
                            Containers.dropItemStack(p_251485_, (double)p_251954_.getX(), (double)p_251954_.getY(), (double)p_251954_.getZ(), $$8);
                        }
                    }

                    $$6.clearContent();
                    p_251485_.updateNeighbourForOutputSignal(p_251954_, this);
                }
            }

            super.onRemove(p_250071_, p_251485_, p_251954_, p_251852_, p_252250_);
        }
    }

    public BlockState getStateForPlacement(BlockPlaceContext p_251318_) {
        return (BlockState)this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, p_251318_.getHorizontalDirection().getOpposite());
    }

    public BlockState rotate(BlockState p_288975_, Rotation p_288993_) {
        return (BlockState)p_288975_.setValue(HorizontalDirectionalBlock.FACING, p_288993_.rotate((Direction)p_288975_.getValue(HorizontalDirectionalBlock.FACING)));
    }

    public BlockState mirror(BlockState p_289000_, Mirror p_288962_) {
        return p_289000_.rotate(p_288962_.getRotation((Direction)p_289000_.getValue(HorizontalDirectionalBlock.FACING)));
    }

    public boolean hasAnalogOutputSignal(BlockState p_249302_) {
        return true;
    }

    public int getAnalogOutputSignal(BlockState p_249192_, Level p_252207_, BlockPos p_248999_) {
        if (p_252207_.isClientSide()) {
            return 0;
        } else {
            BlockEntity var5 = p_252207_.getBlockEntity(p_248999_);
            if (var5 instanceof ChiseledBookShelfBlockEntity) {
                ChiseledBookShelfBlockEntity $$3 = (ChiseledBookShelfBlockEntity)var5;
                return $$3.getLastInteractedSlot() + 1;
            } else {
                return 0;
            }
        }
    }

    static {
        SLOT_OCCUPIED_PROPERTIES = List.of(BlockStateProperties.CHISELED_BOOKSHELF_SLOT_0_OCCUPIED, BlockStateProperties.CHISELED_BOOKSHELF_SLOT_1_OCCUPIED, BlockStateProperties.CHISELED_BOOKSHELF_SLOT_2_OCCUPIED, BlockStateProperties.CHISELED_BOOKSHELF_SLOT_3_OCCUPIED, BlockStateProperties.CHISELED_BOOKSHELF_SLOT_4_OCCUPIED, BlockStateProperties.CHISELED_BOOKSHELF_SLOT_5_OCCUPIED);
    }
}

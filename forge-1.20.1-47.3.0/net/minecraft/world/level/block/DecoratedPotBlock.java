//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block;

import java.util.List;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity.Decorations;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class DecoratedPotBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final ResourceLocation SHERDS_DYNAMIC_DROP_ID = new ResourceLocation("sherds");
    private static final VoxelShape BOUNDING_BOX = Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);
    private static final DirectionProperty HORIZONTAL_FACING;
    private static final BooleanProperty CRACKED;
    private static final BooleanProperty WATERLOGGED;

    public DecoratedPotBlock(BlockBehaviour.Properties p_273064_) {
        super(p_273064_);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(HORIZONTAL_FACING, Direction.NORTH)).setValue(WATERLOGGED, false)).setValue(CRACKED, false));
    }

    public BlockState updateShape(BlockState p_276307_, Direction p_276322_, BlockState p_276280_, LevelAccessor p_276320_, BlockPos p_276270_, BlockPos p_276312_) {
        if ((Boolean)p_276307_.getValue(WATERLOGGED)) {
            p_276320_.scheduleTick(p_276270_, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(p_276320_));
        }

        return super.updateShape(p_276307_, p_276322_, p_276280_, p_276320_, p_276270_, p_276312_);
    }

    public BlockState getStateForPlacement(BlockPlaceContext p_272711_) {
        FluidState $$1 = p_272711_.getLevel().getFluidState(p_272711_.getClickedPos());
        return (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(HORIZONTAL_FACING, p_272711_.getHorizontalDirection())).setValue(WATERLOGGED, $$1.getType() == Fluids.WATER)).setValue(CRACKED, false);
    }

    public boolean isPathfindable(BlockState p_276295_, BlockGetter p_276308_, BlockPos p_276313_, PathComputationType p_276303_) {
        return false;
    }

    public VoxelShape getShape(BlockState p_273112_, BlockGetter p_273055_, BlockPos p_273137_, CollisionContext p_273151_) {
        return BOUNDING_BOX;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_273169_) {
        p_273169_.add(HORIZONTAL_FACING, WATERLOGGED, CRACKED);
    }

    public @Nullable BlockEntity newBlockEntity(BlockPos p_273396_, BlockState p_272674_) {
        return new DecoratedPotBlockEntity(p_273396_, p_272674_);
    }

    public List<ItemStack> getDrops(BlockState p_287683_, LootParams.Builder p_287582_) {
        BlockEntity $$2 = (BlockEntity)p_287582_.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if ($$2 instanceof DecoratedPotBlockEntity $$3) {
            p_287582_.withDynamicDrop(SHERDS_DYNAMIC_DROP_ID, (p_284876_) -> {
                $$3.getDecorations().sorted().map(Item::getDefaultInstance).forEach(p_284876_);
            });
        }

        return super.getDrops(p_287683_, p_287582_);
    }

    public void playerWillDestroy(Level p_273590_, BlockPos p_273343_, BlockState p_272869_, Player p_273002_) {
        ItemStack $$4 = p_273002_.getMainHandItem();
        BlockState $$5 = p_272869_;
        if ($$4.is(ItemTags.BREAKS_DECORATED_POTS) && !EnchantmentHelper.hasSilkTouch($$4)) {
            $$5 = (BlockState)p_272869_.setValue(CRACKED, true);
            p_273590_.setBlock(p_273343_, $$5, 4);
        }

        super.playerWillDestroy(p_273590_, p_273343_, $$5, p_273002_);
    }

    public FluidState getFluidState(BlockState p_272593_) {
        return (Boolean)p_272593_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_272593_);
    }

    public SoundType getSoundType(BlockState p_277561_) {
        return (Boolean)p_277561_.getValue(CRACKED) ? SoundType.DECORATED_POT_CRACKED : SoundType.DECORATED_POT;
    }

    public void appendHoverText(ItemStack p_285238_, @Nullable BlockGetter p_285450_, List<Component> p_285448_, TooltipFlag p_284997_) {
        super.appendHoverText(p_285238_, p_285450_, p_285448_, p_284997_);
        DecoratedPotBlockEntity.Decorations $$4 = Decorations.load(BlockItem.getBlockEntityData(p_285238_));
        if (!$$4.equals(Decorations.EMPTY)) {
            p_285448_.add(CommonComponents.EMPTY);
            Stream.of($$4.front(), $$4.left(), $$4.right(), $$4.back()).forEach((p_284873_) -> {
                p_285448_.add((new ItemStack(p_284873_, 1)).getHoverName().plainCopy().withStyle(ChatFormatting.GRAY));
            });
        }
    }

    static {
        HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;
        CRACKED = BlockStateProperties.CRACKED;
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block;

import java.util.Arrays;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SignApplicator;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class SignBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED;
    protected static final float AABB_OFFSET = 4.0F;
    protected static final VoxelShape SHAPE;
    private final WoodType type;

    protected SignBlock(BlockBehaviour.Properties p_56273_, WoodType p_56274_) {
        super(p_56273_);
        this.type = p_56274_;
    }

    public BlockState updateShape(BlockState p_56285_, Direction p_56286_, BlockState p_56287_, LevelAccessor p_56288_, BlockPos p_56289_, BlockPos p_56290_) {
        if ((Boolean)p_56285_.getValue(WATERLOGGED)) {
            p_56288_.scheduleTick(p_56289_, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(p_56288_));
        }

        return super.updateShape(p_56285_, p_56286_, p_56287_, p_56288_, p_56289_, p_56290_);
    }

    public VoxelShape getShape(BlockState p_56293_, BlockGetter p_56294_, BlockPos p_56295_, CollisionContext p_56296_) {
        return SHAPE;
    }

    public boolean isPossibleToRespawnInThis(BlockState p_279137_) {
        return true;
    }

    public BlockEntity newBlockEntity(BlockPos p_154556_, BlockState p_154557_) {
        return new SignBlockEntity(p_154556_, p_154557_);
    }

    public InteractionResult use(BlockState p_56278_, Level p_56279_, BlockPos p_56280_, Player p_56281_, InteractionHand p_56282_, BlockHitResult p_56283_) {
        ItemStack $$6 = p_56281_.getItemInHand(p_56282_);
        Item $$7 = $$6.getItem();
        Item var11 = $$6.getItem();
        SignApplicator var10000;
        if (var11 instanceof SignApplicator $$8) {
            var10000 = $$8;
        } else {
            var10000 = null;
        }

        SignApplicator $$9 = var10000;
        boolean $$10 = $$9 != null && p_56281_.mayBuild();
        BlockEntity var12 = p_56279_.getBlockEntity(p_56280_);
        if (var12 instanceof SignBlockEntity $$11) {
            if (!p_56279_.isClientSide) {
                boolean $$12 = $$11.isFacingFrontText(p_56281_);
                SignText $$13 = $$11.getText($$12);
                boolean $$14 = $$11.executeClickCommandsIfPresent(p_56281_, p_56279_, p_56280_, $$12);
                if ($$11.isWaxed()) {
                    p_56279_.playSound((Player)null, $$11.getBlockPos(), SoundEvents.WAXED_SIGN_INTERACT_FAIL, SoundSource.BLOCKS);
                    return InteractionResult.PASS;
                } else if ($$10 && !this.otherPlayerIsEditingSign(p_56281_, $$11) && $$9.canApplyToSign($$13, p_56281_) && $$9.tryApplyToSign(p_56279_, $$11, $$12, p_56281_)) {
                    if (!p_56281_.isCreative()) {
                        $$6.shrink(1);
                    }

                    p_56279_.gameEvent(GameEvent.BLOCK_CHANGE, $$11.getBlockPos(), Context.of(p_56281_, $$11.getBlockState()));
                    p_56281_.awardStat(Stats.ITEM_USED.get($$7));
                    return InteractionResult.SUCCESS;
                } else if ($$14) {
                    return InteractionResult.SUCCESS;
                } else if (!this.otherPlayerIsEditingSign(p_56281_, $$11) && p_56281_.mayBuild() && this.hasEditableText(p_56281_, $$11, $$12)) {
                    this.openTextEdit(p_56281_, $$11, $$12);
                    return InteractionResult.SUCCESS;
                } else {
                    return InteractionResult.PASS;
                }
            } else {
                return !$$10 && !$$11.isWaxed() ? InteractionResult.CONSUME : InteractionResult.SUCCESS;
            }
        } else {
            return InteractionResult.PASS;
        }
    }

    private boolean hasEditableText(Player p_279394_, SignBlockEntity p_279187_, boolean p_279225_) {
        SignText $$3 = p_279187_.getText(p_279225_);
        return Arrays.stream($$3.getMessages(p_279394_.isTextFilteringEnabled())).allMatch((p_279411_) -> {
            return p_279411_.equals(CommonComponents.EMPTY) || p_279411_.getContents() instanceof LiteralContents;
        });
    }

    public abstract float getYRotationDegrees(BlockState var1);

    public Vec3 getSignHitboxCenterPosition(BlockState p_278294_) {
        return new Vec3(0.5, 0.5, 0.5);
    }

    public FluidState getFluidState(BlockState p_56299_) {
        return (Boolean)p_56299_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_56299_);
    }

    public WoodType type() {
        return this.type;
    }

    public static WoodType getWoodType(Block p_251096_) {
        WoodType $$2;
        if (p_251096_ instanceof SignBlock) {
            $$2 = ((SignBlock)p_251096_).type();
        } else {
            $$2 = WoodType.OAK;
        }

        return $$2;
    }

    public void openTextEdit(Player p_277738_, SignBlockEntity p_277467_, boolean p_277771_) {
        p_277467_.setAllowedPlayerEditor(p_277738_.getUUID());
        p_277738_.openTextEdit(p_277467_, p_277771_);
    }

    private boolean otherPlayerIsEditingSign(Player p_277952_, SignBlockEntity p_277599_) {
        UUID $$2 = p_277599_.getPlayerWhoMayEdit();
        return $$2 != null && !$$2.equals(p_277952_.getUUID());
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_277367_, BlockState p_277896_, BlockEntityType<T> p_277724_) {
        return createTickerHelper(p_277724_, BlockEntityType.SIGN, SignBlockEntity::tick);
    }

    static {
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
        SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);
    }
}

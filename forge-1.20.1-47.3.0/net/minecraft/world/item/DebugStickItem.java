//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.item;

import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

public class DebugStickItem extends Item {
    public DebugStickItem(Item.Properties p_40948_) {
        super(p_40948_);
    }

    public boolean isFoil(ItemStack p_40978_) {
        return true;
    }

    public boolean canAttackBlock(BlockState p_40962_, Level p_40963_, BlockPos p_40964_, Player p_40965_) {
        if (!p_40963_.isClientSide) {
            this.handleInteraction(p_40965_, p_40962_, p_40963_, p_40964_, false, p_40965_.getItemInHand(InteractionHand.MAIN_HAND));
        }

        return false;
    }

    public InteractionResult useOn(UseOnContext p_40960_) {
        Player $$1 = p_40960_.getPlayer();
        Level $$2 = p_40960_.getLevel();
        if (!$$2.isClientSide && $$1 != null) {
            BlockPos $$3 = p_40960_.getClickedPos();
            if (!this.handleInteraction($$1, $$2.getBlockState($$3), $$2, $$3, true, p_40960_.getItemInHand())) {
                return InteractionResult.FAIL;
            }
        }

        return InteractionResult.sidedSuccess($$2.isClientSide);
    }

    private boolean handleInteraction(Player p_150803_, BlockState p_150804_, LevelAccessor p_150805_, BlockPos p_150806_, boolean p_150807_, ItemStack p_150808_) {
        if (!p_150803_.canUseGameMasterBlocks()) {
            return false;
        } else {
            Block $$6 = p_150804_.getBlock();
            StateDefinition<Block, BlockState> $$7 = $$6.getStateDefinition();
            Collection<Property<?>> $$8 = $$7.getProperties();
            String $$9 = BuiltInRegistries.BLOCK.getKey($$6).toString();
            if ($$8.isEmpty()) {
                message(p_150803_, Component.translatable(this.getDescriptionId() + ".empty", $$9));
                return false;
            } else {
                CompoundTag $$10 = p_150808_.getOrCreateTagElement("DebugProperty");
                String $$11 = $$10.getString($$9);
                Property<?> $$12 = $$7.getProperty($$11);
                if (p_150807_) {
                    if ($$12 == null) {
                        $$12 = (Property)$$8.iterator().next();
                    }

                    BlockState $$13 = cycleState(p_150804_, $$12, p_150803_.isSecondaryUseActive());
                    p_150805_.setBlock(p_150806_, $$13, 18);
                    message(p_150803_, Component.translatable(this.getDescriptionId() + ".update", $$12.getName(), getNameHelper($$13, $$12)));
                } else {
                    $$12 = (Property)getRelative($$8, $$12, p_150803_.isSecondaryUseActive());
                    String $$14 = $$12.getName();
                    $$10.putString($$9, $$14);
                    message(p_150803_, Component.translatable(this.getDescriptionId() + ".select", $$14, getNameHelper(p_150804_, $$12)));
                }

                return true;
            }
        }
    }

    private static <T extends Comparable<T>> BlockState cycleState(BlockState p_40970_, Property<T> p_40971_, boolean p_40972_) {
        return (BlockState)p_40970_.setValue(p_40971_, (Comparable)getRelative(p_40971_.getPossibleValues(), p_40970_.getValue(p_40971_), p_40972_));
    }

    private static <T> T getRelative(Iterable<T> p_40974_, @Nullable T p_40975_, boolean p_40976_) {
        return p_40976_ ? Util.findPreviousInIterable(p_40974_, p_40975_) : Util.findNextInIterable(p_40974_, p_40975_);
    }

    private static void message(Player p_40957_, Component p_40958_) {
        ((ServerPlayer)p_40957_).sendSystemMessage(p_40958_, true);
    }

    private static <T extends Comparable<T>> String getNameHelper(BlockState p_40967_, Property<T> p_40968_) {
        return p_40968_.getName(p_40967_.getValue(p_40968_));
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.state.BlockState;

public class WritableBookItem extends Item {
    public WritableBookItem(Item.Properties p_43445_) {
        super(p_43445_);
    }

    public InteractionResult useOn(UseOnContext p_43447_) {
        Level $$1 = p_43447_.getLevel();
        BlockPos $$2 = p_43447_.getClickedPos();
        BlockState $$3 = $$1.getBlockState($$2);
        if ($$3.is(Blocks.LECTERN)) {
            return LecternBlock.tryPlaceBook(p_43447_.getPlayer(), $$1, $$2, $$3, p_43447_.getItemInHand()) ? InteractionResult.sidedSuccess($$1.isClientSide) : InteractionResult.PASS;
        } else {
            return InteractionResult.PASS;
        }
    }

    public InteractionResultHolder<ItemStack> use(Level p_43449_, Player p_43450_, InteractionHand p_43451_) {
        ItemStack $$3 = p_43450_.getItemInHand(p_43451_);
        p_43450_.openItemGui($$3, p_43451_);
        p_43450_.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess($$3, p_43449_.isClientSide());
    }

    public static boolean makeSureTagIsValid(@Nullable CompoundTag p_43453_) {
        if (p_43453_ == null) {
            return false;
        } else if (!p_43453_.contains("pages", 9)) {
            return false;
        } else {
            ListTag $$1 = p_43453_.getList("pages", 8);

            for(int $$2 = 0; $$2 < $$1.size(); ++$$2) {
                String $$3 = $$1.getString($$2);
                if ($$3.length() > 32767) {
                    return false;
                }
            }

            return true;
        }
    }
}

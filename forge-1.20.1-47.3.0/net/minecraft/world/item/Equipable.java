//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public interface Equipable extends Vanishable {
    EquipmentSlot getEquipmentSlot();

    default SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_GENERIC;
    }

    default InteractionResultHolder<ItemStack> swapWithEquipmentSlot(Item p_270453_, Level p_270395_, Player p_270300_, InteractionHand p_270262_) {
        ItemStack $$4 = p_270300_.getItemInHand(p_270262_);
        EquipmentSlot $$5 = Mob.getEquipmentSlotForItem($$4);
        ItemStack $$6 = p_270300_.getItemBySlot($$5);
        if (!EnchantmentHelper.hasBindingCurse($$6) && !ItemStack.matches($$4, $$6)) {
            if (!p_270395_.isClientSide()) {
                p_270300_.awardStat(Stats.ITEM_USED.get(p_270453_));
            }

            ItemStack $$7 = $$6.isEmpty() ? $$4 : $$6.copyAndClear();
            ItemStack $$8 = $$4.copyAndClear();
            p_270300_.setItemSlot($$5, $$8);
            return InteractionResultHolder.sidedSuccess($$7, p_270395_.isClientSide());
        } else {
            return InteractionResultHolder.fail($$4);
        }
    }

    @Nullable
    static Equipable get(ItemStack p_270317_) {
        Item var2 = p_270317_.getItem();
        if (var2 instanceof Equipable $$1) {
            return $$1;
        } else {
            Item var3 = p_270317_.getItem();
            if (var3 instanceof BlockItem $$2) {
                Block var6 = $$2.getBlock();
                if (var6 instanceof Equipable $$3) {
                    return $$3;
                }
            }

            return null;
        }
    }
}

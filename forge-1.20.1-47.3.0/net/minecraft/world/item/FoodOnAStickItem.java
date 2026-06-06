//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.item;

import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ItemSteerable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class FoodOnAStickItem<T extends Entity & ItemSteerable> extends Item {
    private final EntityType<T> canInteractWith;
    private final int consumeItemDamage;

    public FoodOnAStickItem(Item.Properties p_41307_, EntityType<T> p_41308_, int p_41309_) {
        super(p_41307_);
        this.canInteractWith = p_41308_;
        this.consumeItemDamage = p_41309_;
    }

    public InteractionResultHolder<ItemStack> use(Level p_41314_, Player p_41315_, InteractionHand p_41316_) {
        ItemStack $$3 = p_41315_.getItemInHand(p_41316_);
        if (p_41314_.isClientSide) {
            return InteractionResultHolder.pass($$3);
        } else {
            Entity $$4 = p_41315_.getControlledVehicle();
            if (p_41315_.isPassenger() && $$4 instanceof ItemSteerable) {
                ItemSteerable $$5 = (ItemSteerable)$$4;
                if ($$4.getType() == this.canInteractWith && $$5.boost()) {
                    $$3.hurtAndBreak(this.consumeItemDamage, p_41315_, (p_41312_) -> {
                        p_41312_.broadcastBreakEvent(p_41316_);
                    });
                    if ($$3.isEmpty()) {
                        ItemStack $$6 = new ItemStack(Items.FISHING_ROD);
                        $$6.setTag($$3.getTag());
                        return InteractionResultHolder.success($$6);
                    }

                    return InteractionResultHolder.success($$3);
                }
            }

            p_41315_.awardStat(Stats.ITEM_USED.get(this));
            return InteractionResultHolder.pass($$3);
        }
    }
}

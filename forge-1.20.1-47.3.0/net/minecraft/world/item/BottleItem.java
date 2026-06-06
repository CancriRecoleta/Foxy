//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.item;

import java.util.List;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;

public class BottleItem extends Item {
    public BottleItem(Item.Properties p_40648_) {
        super(p_40648_);
    }

    public InteractionResultHolder<ItemStack> use(Level p_40656_, Player p_40657_, InteractionHand p_40658_) {
        List<AreaEffectCloud> $$3 = p_40656_.getEntitiesOfClass(AreaEffectCloud.class, p_40657_.getBoundingBox().inflate(2.0), (p_289499_) -> {
            return p_289499_ != null && p_289499_.isAlive() && p_289499_.getOwner() instanceof EnderDragon;
        });
        ItemStack $$4 = p_40657_.getItemInHand(p_40658_);
        if (!$$3.isEmpty()) {
            AreaEffectCloud $$5 = (AreaEffectCloud)$$3.get(0);
            $$5.setRadius($$5.getRadius() - 0.5F);
            p_40656_.playSound((Player)null, p_40657_.getX(), p_40657_.getY(), p_40657_.getZ(), SoundEvents.BOTTLE_FILL_DRAGONBREATH, SoundSource.NEUTRAL, 1.0F, 1.0F);
            p_40656_.gameEvent(p_40657_, GameEvent.FLUID_PICKUP, p_40657_.position());
            if (p_40657_ instanceof ServerPlayer) {
                ServerPlayer $$6 = (ServerPlayer)p_40657_;
                CriteriaTriggers.PLAYER_INTERACTED_WITH_ENTITY.trigger($$6, $$4, $$5);
            }

            return InteractionResultHolder.sidedSuccess(this.turnBottleIntoItem($$4, p_40657_, new ItemStack(Items.DRAGON_BREATH)), p_40656_.isClientSide());
        } else {
            BlockHitResult $$7 = getPlayerPOVHitResult(p_40656_, p_40657_, Fluid.SOURCE_ONLY);
            if ($$7.getType() == Type.MISS) {
                return InteractionResultHolder.pass($$4);
            } else {
                if ($$7.getType() == Type.BLOCK) {
                    BlockPos $$8 = $$7.getBlockPos();
                    if (!p_40656_.mayInteract(p_40657_, $$8)) {
                        return InteractionResultHolder.pass($$4);
                    }

                    if (p_40656_.getFluidState($$8).is(FluidTags.WATER)) {
                        p_40656_.playSound(p_40657_, p_40657_.getX(), p_40657_.getY(), p_40657_.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
                        p_40656_.gameEvent(p_40657_, GameEvent.FLUID_PICKUP, $$8);
                        return InteractionResultHolder.sidedSuccess(this.turnBottleIntoItem($$4, p_40657_, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER)), p_40656_.isClientSide());
                    }
                }

                return InteractionResultHolder.pass($$4);
            }
        }
    }

    protected ItemStack turnBottleIntoItem(ItemStack p_40652_, Player p_40653_, ItemStack p_40654_) {
        p_40653_.awardStat(Stats.ITEM_USED.get(this));
        return ItemUtils.createFilledResult(p_40652_, p_40653_, p_40654_);
    }
}

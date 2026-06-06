//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.item;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class PotionItem extends Item {
    private static final int DRINK_DURATION = 32;

    public PotionItem(Item.Properties p_42979_) {
        super(p_42979_);
    }

    public ItemStack getDefaultInstance() {
        return PotionUtils.setPotion(super.getDefaultInstance(), Potions.WATER);
    }

    public ItemStack finishUsingItem(ItemStack p_42984_, Level p_42985_, LivingEntity p_42986_) {
        Player $$3 = p_42986_ instanceof Player ? (Player)p_42986_ : null;
        if ($$3 instanceof ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)$$3, p_42984_);
        }

        if (!p_42985_.isClientSide) {
            List<MobEffectInstance> $$4 = PotionUtils.getMobEffects(p_42984_);
            Iterator var6 = $$4.iterator();

            while(var6.hasNext()) {
                MobEffectInstance $$5 = (MobEffectInstance)var6.next();
                if ($$5.getEffect().isInstantenous()) {
                    $$5.getEffect().applyInstantenousEffect($$3, $$3, p_42986_, $$5.getAmplifier(), 1.0);
                } else {
                    p_42986_.addEffect(new MobEffectInstance($$5));
                }
            }
        }

        if ($$3 != null) {
            $$3.awardStat(Stats.ITEM_USED.get(this));
            if (!$$3.getAbilities().instabuild) {
                p_42984_.shrink(1);
            }
        }

        if ($$3 == null || !$$3.getAbilities().instabuild) {
            if (p_42984_.isEmpty()) {
                return new ItemStack(Items.GLASS_BOTTLE);
            }

            if ($$3 != null) {
                $$3.getInventory().add(new ItemStack(Items.GLASS_BOTTLE));
            }
        }

        p_42986_.gameEvent(GameEvent.DRINK);
        return p_42984_;
    }

    public InteractionResult useOn(UseOnContext p_220235_) {
        Level $$1 = p_220235_.getLevel();
        BlockPos $$2 = p_220235_.getClickedPos();
        Player $$3 = p_220235_.getPlayer();
        ItemStack $$4 = p_220235_.getItemInHand();
        BlockState $$5 = $$1.getBlockState($$2);
        if (p_220235_.getClickedFace() != Direction.DOWN && $$5.is(BlockTags.CONVERTABLE_TO_MUD) && PotionUtils.getPotion($$4) == Potions.WATER) {
            $$1.playSound((Player)null, (BlockPos)$$2, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 1.0F, 1.0F);
            $$3.setItemInHand(p_220235_.getHand(), ItemUtils.createFilledResult($$4, $$3, new ItemStack(Items.GLASS_BOTTLE)));
            $$3.awardStat(Stats.ITEM_USED.get($$4.getItem()));
            if (!$$1.isClientSide) {
                ServerLevel $$6 = (ServerLevel)$$1;

                for(int $$7 = 0; $$7 < 5; ++$$7) {
                    $$6.sendParticles(ParticleTypes.SPLASH, (double)$$2.getX() + $$1.random.nextDouble(), (double)($$2.getY() + 1), (double)$$2.getZ() + $$1.random.nextDouble(), 1, 0.0, 0.0, 0.0, 1.0);
                }
            }

            $$1.playSound((Player)null, (BlockPos)$$2, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
            $$1.gameEvent((Entity)null, GameEvent.FLUID_PLACE, $$2);
            $$1.setBlockAndUpdate($$2, Blocks.MUD.defaultBlockState());
            return InteractionResult.sidedSuccess($$1.isClientSide);
        } else {
            return InteractionResult.PASS;
        }
    }

    public int getUseDuration(ItemStack p_43001_) {
        return 32;
    }

    public UseAnim getUseAnimation(ItemStack p_42997_) {
        return UseAnim.DRINK;
    }

    public InteractionResultHolder<ItemStack> use(Level p_42993_, Player p_42994_, InteractionHand p_42995_) {
        return ItemUtils.startUsingInstantly(p_42993_, p_42994_, p_42995_);
    }

    public String getDescriptionId(ItemStack p_43003_) {
        return PotionUtils.getPotion(p_43003_).getName(this.getDescriptionId() + ".effect.");
    }

    public void appendHoverText(ItemStack p_42988_, @Nullable Level p_42989_, List<Component> p_42990_, TooltipFlag p_42991_) {
        PotionUtils.addPotionTooltip(p_42988_, p_42990_, 1.0F);
    }
}

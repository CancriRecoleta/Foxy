//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.item;

import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.phys.AABB;

public class LeadItem extends Item {
    public LeadItem(Item.Properties p_42828_) {
        super(p_42828_);
    }

    public InteractionResult useOn(UseOnContext p_42834_) {
        Level $$1 = p_42834_.getLevel();
        BlockPos $$2 = p_42834_.getClickedPos();
        BlockState $$3 = $$1.getBlockState($$2);
        if ($$3.is(BlockTags.FENCES)) {
            Player $$4 = p_42834_.getPlayer();
            if (!$$1.isClientSide && $$4 != null) {
                bindPlayerMobs($$4, $$1, $$2);
            }

            return InteractionResult.sidedSuccess($$1.isClientSide);
        } else {
            return InteractionResult.PASS;
        }
    }

    public static InteractionResult bindPlayerMobs(Player p_42830_, Level p_42831_, BlockPos p_42832_) {
        LeashFenceKnotEntity $$3 = null;
        boolean $$4 = false;
        double $$5 = 7.0;
        int $$6 = p_42832_.getX();
        int $$7 = p_42832_.getY();
        int $$8 = p_42832_.getZ();
        List<Mob> $$9 = p_42831_.getEntitiesOfClass(Mob.class, new AABB((double)$$6 - 7.0, (double)$$7 - 7.0, (double)$$8 - 7.0, (double)$$6 + 7.0, (double)$$7 + 7.0, (double)$$8 + 7.0));
        Iterator var11 = $$9.iterator();

        while(var11.hasNext()) {
            Mob $$10 = (Mob)var11.next();
            if ($$10.getLeashHolder() == p_42830_) {
                if ($$3 == null) {
                    $$3 = LeashFenceKnotEntity.getOrCreateKnot(p_42831_, p_42832_);
                    $$3.playPlacementSound();
                }

                $$10.setLeashedTo($$3, true);
                $$4 = true;
            }
        }

        if ($$4) {
            p_42831_.gameEvent(GameEvent.BLOCK_ATTACH, p_42832_, Context.of((Entity)p_42830_));
        }

        return $$4 ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }
}

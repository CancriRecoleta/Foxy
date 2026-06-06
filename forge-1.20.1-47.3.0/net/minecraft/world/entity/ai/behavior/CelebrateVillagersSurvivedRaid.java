//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.FireworkRocketItem.Shape;

public class CelebrateVillagersSurvivedRaid extends Behavior<Villager> {
    @Nullable
    private Raid currentRaid;

    public CelebrateVillagersSurvivedRaid(int p_22684_, int p_22685_) {
        super(ImmutableMap.of(), p_22684_, p_22685_);
    }

    protected boolean checkExtraStartConditions(ServerLevel p_22690_, Villager p_22691_) {
        BlockPos $$2 = p_22691_.blockPosition();
        this.currentRaid = p_22690_.getRaidAt($$2);
        return this.currentRaid != null && this.currentRaid.isVictory() && MoveToSkySeeingSpot.hasNoBlocksAbove(p_22690_, p_22691_, $$2);
    }

    protected boolean canStillUse(ServerLevel p_22693_, Villager p_22694_, long p_22695_) {
        return this.currentRaid != null && !this.currentRaid.isStopped();
    }

    protected void stop(ServerLevel p_22704_, Villager p_22705_, long p_22706_) {
        this.currentRaid = null;
        p_22705_.getBrain().updateActivityFromSchedule(p_22704_.getDayTime(), p_22704_.getGameTime());
    }

    protected void tick(ServerLevel p_22712_, Villager p_22713_, long p_22714_) {
        RandomSource $$3 = p_22713_.getRandom();
        if ($$3.nextInt(100) == 0) {
            p_22713_.playCelebrateSound();
        }

        if ($$3.nextInt(200) == 0 && MoveToSkySeeingSpot.hasNoBlocksAbove(p_22712_, p_22713_, p_22713_.blockPosition())) {
            DyeColor $$4 = (DyeColor)Util.getRandom((Object[])DyeColor.values(), $$3);
            int $$5 = $$3.nextInt(3);
            ItemStack $$6 = this.getFirework($$4, $$5);
            FireworkRocketEntity $$7 = new FireworkRocketEntity(p_22713_.level(), p_22713_, p_22713_.getX(), p_22713_.getEyeY(), p_22713_.getZ(), $$6);
            p_22713_.level().addFreshEntity($$7);
        }

    }

    private ItemStack getFirework(DyeColor p_22697_, int p_22698_) {
        ItemStack $$2 = new ItemStack(Items.FIREWORK_ROCKET, 1);
        ItemStack $$3 = new ItemStack(Items.FIREWORK_STAR);
        CompoundTag $$4 = $$3.getOrCreateTagElement("Explosion");
        List<Integer> $$5 = Lists.newArrayList();
        $$5.add(p_22697_.getFireworkColor());
        $$4.putIntArray("Colors", (List)$$5);
        $$4.putByte("Type", (byte)Shape.BURST.getId());
        CompoundTag $$6 = $$2.getOrCreateTagElement("Fireworks");
        ListTag $$7 = new ListTag();
        CompoundTag $$8 = $$3.getTagElement("Explosion");
        if ($$8 != null) {
            $$7.add($$8);
        }

        $$6.putByte("Flight", (byte)p_22698_);
        if (!$$7.isEmpty()) {
            $$6.put("Explosions", $$7);
        }

        return $$2;
    }
}

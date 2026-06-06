//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.core.dispenser;

import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity.BeeReleaseStatus;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;

public class ShearsDispenseItemBehavior extends OptionalDispenseItemBehavior {
    public ShearsDispenseItemBehavior() {
    }

    protected ItemStack execute(BlockSource p_123580_, ItemStack p_123581_) {
        ServerLevel $$2 = p_123580_.getLevel();
        if (!$$2.isClientSide()) {
            BlockPos $$3 = p_123580_.getPos().relative((Direction)p_123580_.getBlockState().getValue(DispenserBlock.FACING));
            this.setSuccess(tryShearBeehive($$2, $$3) || tryShearLivingEntity($$2, $$3));
            if (this.isSuccess() && p_123581_.hurt(1, $$2.getRandom(), (ServerPlayer)null)) {
                p_123581_.setCount(0);
            }
        }

        return p_123581_;
    }

    private static boolean tryShearBeehive(ServerLevel p_123577_, BlockPos p_123578_) {
        BlockState $$2 = p_123577_.getBlockState(p_123578_);
        if ($$2.is(BlockTags.BEEHIVES, (p_202454_) -> {
            return p_202454_.hasProperty(BeehiveBlock.HONEY_LEVEL) && p_202454_.getBlock() instanceof BeehiveBlock;
        })) {
            int $$3 = (Integer)$$2.getValue(BeehiveBlock.HONEY_LEVEL);
            if ($$3 >= 5) {
                p_123577_.playSound((Player)null, p_123578_, SoundEvents.BEEHIVE_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
                BeehiveBlock.dropHoneycomb(p_123577_, p_123578_);
                ((BeehiveBlock)$$2.getBlock()).releaseBeesAndResetHoneyLevel(p_123577_, $$2, p_123578_, (Player)null, BeeReleaseStatus.BEE_RELEASED);
                p_123577_.gameEvent((Entity)null, GameEvent.SHEAR, p_123578_);
                return true;
            }
        }

        return false;
    }

    private static boolean tryShearLivingEntity(ServerLevel p_123583_, BlockPos p_123584_) {
        List<LivingEntity> $$2 = p_123583_.getEntitiesOfClass(LivingEntity.class, new AABB(p_123584_), EntitySelector.NO_SPECTATORS);
        Iterator var3 = $$2.iterator();

        while(var3.hasNext()) {
            LivingEntity $$3 = (LivingEntity)var3.next();
            if ($$3 instanceof Shearable $$4) {
                if ($$4.readyForShearing()) {
                    $$4.shear(SoundSource.BLOCKS);
                    p_123583_.gameEvent((Entity)null, GameEvent.SHEAR, p_123584_);
                    return true;
                }
            }
        }

        return false;
    }
}

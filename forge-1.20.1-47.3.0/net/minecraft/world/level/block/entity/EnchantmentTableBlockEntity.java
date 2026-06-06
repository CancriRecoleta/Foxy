//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class EnchantmentTableBlockEntity extends BlockEntity implements Nameable {
    public int time;
    public float flip;
    public float oFlip;
    public float flipT;
    public float flipA;
    public float open;
    public float oOpen;
    public float rot;
    public float oRot;
    public float tRot;
    private static final RandomSource RANDOM = RandomSource.create();
    private Component name;

    public EnchantmentTableBlockEntity(BlockPos p_155501_, BlockState p_155502_) {
        super(BlockEntityType.ENCHANTING_TABLE, p_155501_, p_155502_);
    }

    protected void saveAdditional(CompoundTag p_187500_) {
        super.saveAdditional(p_187500_);
        if (this.hasCustomName()) {
            p_187500_.putString("CustomName", Serializer.toJson(this.name));
        }

    }

    public void load(CompoundTag p_155509_) {
        super.load(p_155509_);
        if (p_155509_.contains("CustomName", 8)) {
            this.name = Serializer.fromJson(p_155509_.getString("CustomName"));
        }

    }

    public static void bookAnimationTick(Level p_155504_, BlockPos p_155505_, BlockState p_155506_, EnchantmentTableBlockEntity p_155507_) {
        p_155507_.oOpen = p_155507_.open;
        p_155507_.oRot = p_155507_.rot;
        Player $$4 = p_155504_.getNearestPlayer((double)p_155505_.getX() + 0.5, (double)p_155505_.getY() + 0.5, (double)p_155505_.getZ() + 0.5, 3.0, false);
        if ($$4 != null) {
            double $$5 = $$4.getX() - ((double)p_155505_.getX() + 0.5);
            double $$6 = $$4.getZ() - ((double)p_155505_.getZ() + 0.5);
            p_155507_.tRot = (float)Mth.atan2($$6, $$5);
            p_155507_.open += 0.1F;
            if (p_155507_.open < 0.5F || RANDOM.nextInt(40) == 0) {
                float $$7 = p_155507_.flipT;

                do {
                    p_155507_.flipT += (float)(RANDOM.nextInt(4) - RANDOM.nextInt(4));
                } while($$7 == p_155507_.flipT);
            }
        } else {
            p_155507_.tRot += 0.02F;
            p_155507_.open -= 0.1F;
        }

        while(p_155507_.rot >= 3.1415927F) {
            p_155507_.rot -= 6.2831855F;
        }

        while(p_155507_.rot < -3.1415927F) {
            p_155507_.rot += 6.2831855F;
        }

        while(p_155507_.tRot >= 3.1415927F) {
            p_155507_.tRot -= 6.2831855F;
        }

        while(p_155507_.tRot < -3.1415927F) {
            p_155507_.tRot += 6.2831855F;
        }

        float $$8;
        for($$8 = p_155507_.tRot - p_155507_.rot; $$8 >= 3.1415927F; $$8 -= 6.2831855F) {
        }

        while($$8 < -3.1415927F) {
            $$8 += 6.2831855F;
        }

        p_155507_.rot += $$8 * 0.4F;
        p_155507_.open = Mth.clamp(p_155507_.open, 0.0F, 1.0F);
        ++p_155507_.time;
        p_155507_.oFlip = p_155507_.flip;
        float $$9 = (p_155507_.flipT - p_155507_.flip) * 0.4F;
        float $$10 = 0.2F;
        $$9 = Mth.clamp($$9, -0.2F, 0.2F);
        p_155507_.flipA += ($$9 - p_155507_.flipA) * 0.9F;
        p_155507_.flip += p_155507_.flipA;
    }

    public Component getName() {
        return (Component)(this.name != null ? this.name : Component.translatable("container.enchant"));
    }

    public void setCustomName(@Nullable Component p_59273_) {
        this.name = p_59273_;
    }

    @Nullable
    public Component getCustomName() {
        return this.name;
    }
}

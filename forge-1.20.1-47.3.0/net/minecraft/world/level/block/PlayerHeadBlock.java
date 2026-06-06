//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class PlayerHeadBlock extends SkullBlock {
    public PlayerHeadBlock(BlockBehaviour.Properties p_55177_) {
        super(net.minecraft.world.level.block.SkullBlock.Types.PLAYER, p_55177_);
    }

    public void setPlacedBy(Level p_55179_, BlockPos p_55180_, BlockState p_55181_, @Nullable LivingEntity p_55182_, ItemStack p_55183_) {
        super.setPlacedBy(p_55179_, p_55180_, p_55181_, p_55182_, p_55183_);
        BlockEntity $$5 = p_55179_.getBlockEntity(p_55180_);
        if ($$5 instanceof SkullBlockEntity $$6) {
            GameProfile $$7 = null;
            if (p_55183_.hasTag()) {
                CompoundTag $$8 = p_55183_.getTag();
                if ($$8.contains("SkullOwner", 10)) {
                    $$7 = NbtUtils.readGameProfile($$8.getCompound("SkullOwner"));
                } else if ($$8.contains("SkullOwner", 8) && !Util.isBlank($$8.getString("SkullOwner"))) {
                    $$7 = new GameProfile((UUID)null, $$8.getString("SkullOwner"));
                }
            }

            $$6.setOwner($$7);
        }

    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.item;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

public class PlayerHeadItem extends StandingAndWallBlockItem {
    public static final String TAG_SKULL_OWNER = "SkullOwner";

    public PlayerHeadItem(Block p_42971_, Block p_42972_, Item.Properties p_42973_) {
        super(p_42971_, p_42972_, p_42973_, Direction.DOWN);
    }

    public Component getName(ItemStack p_42977_) {
        if (p_42977_.is(Items.PLAYER_HEAD) && p_42977_.hasTag()) {
            String $$1 = null;
            CompoundTag $$2 = p_42977_.getTag();
            if ($$2.contains("SkullOwner", 8)) {
                $$1 = $$2.getString("SkullOwner");
            } else if ($$2.contains("SkullOwner", 10)) {
                CompoundTag $$3 = $$2.getCompound("SkullOwner");
                if ($$3.contains("Name", 8)) {
                    $$1 = $$3.getString("Name");
                }
            }

            if ($$1 != null) {
                return Component.translatable(this.getDescriptionId() + ".named", $$1);
            }
        }

        return super.getName(p_42977_);
    }

    public void verifyTagAfterLoad(CompoundTag p_151179_) {
        super.verifyTagAfterLoad(p_151179_);
        if (p_151179_.contains("SkullOwner", 8) && !Util.isBlank(p_151179_.getString("SkullOwner"))) {
            GameProfile $$1 = new GameProfile((UUID)null, p_151179_.getString("SkullOwner"));
            SkullBlockEntity.updateGameprofile($$1, (p_151177_) -> {
                p_151179_.put("SkullOwner", NbtUtils.writeGameProfile(new CompoundTag(), p_151177_));
            });
        }

    }
}

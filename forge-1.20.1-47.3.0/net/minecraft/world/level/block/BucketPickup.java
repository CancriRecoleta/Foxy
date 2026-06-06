//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.extensions.IForgeBucketPickup;

public interface BucketPickup extends IForgeBucketPickup {
    ItemStack pickupBlock(LevelAccessor var1, BlockPos var2, BlockState var3);

    /** @deprecated */
    @Deprecated
    Optional<SoundEvent> getPickupSound();
}

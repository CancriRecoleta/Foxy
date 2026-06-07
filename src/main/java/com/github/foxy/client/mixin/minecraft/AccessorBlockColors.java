package com.github.foxy.client.mixin.minecraft;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

// A mixin accessor for the private BlockColors.blockColors map. Used instead of an access
// transformer because ForgeGradle's AT cache was unreliable for this particular field, whereas a
// mixin @Accessor is resolved through the refmap and works in both dev and production.
@Mixin(BlockColors.class)
public interface AccessorBlockColors {
    @Accessor("blockColors")
    Map<Holder.Reference<Block>, BlockColor> foxy$getBlockColors();
}

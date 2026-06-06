//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.data.tags;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class FluidTagsProvider extends IntrinsicHolderTagsProvider<Fluid> {
    /** @deprecated */
    @Deprecated
    public FluidTagsProvider(PackOutput p_255941_, CompletableFuture<HolderLookup.Provider> p_256600_) {
        this(p_255941_, p_256600_, "vanilla", (ExistingFileHelper)null);
    }

    public FluidTagsProvider(PackOutput p_255941_, CompletableFuture<HolderLookup.Provider> p_256600_, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_255941_, Registries.FLUID, p_256600_, (p_256474_) -> {
            return p_256474_.builtInRegistryHolder().key();
        }, modId, existingFileHelper);
    }

    protected void addTags(HolderLookup.Provider p_256366_) {
        this.tag(FluidTags.WATER).add((Object[])(Fluids.WATER, Fluids.FLOWING_WATER));
        this.tag(FluidTags.LAVA).add((Object[])(Fluids.LAVA, Fluids.FLOWING_LAVA));
    }
}

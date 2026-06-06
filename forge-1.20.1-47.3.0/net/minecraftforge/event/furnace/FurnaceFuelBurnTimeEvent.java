//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.event.furnace;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Cancelable
public class FurnaceFuelBurnTimeEvent extends Event {
    private final @NotNull ItemStack itemStack;
    private final @Nullable RecipeType<?> recipeType;
    private int burnTime;

    public FurnaceFuelBurnTimeEvent(@NotNull ItemStack itemStack, int burnTime, @Nullable RecipeType<?> recipeType) {
        this.itemStack = itemStack;
        this.burnTime = burnTime;
        this.recipeType = recipeType;
    }

    public @NotNull ItemStack getItemStack() {
        return this.itemStack;
    }

    public @Nullable RecipeType<?> getRecipeType() {
        return this.recipeType;
    }

    public void setBurnTime(int burnTime) {
        if (burnTime >= 0) {
            this.burnTime = burnTime;
            this.setCanceled(true);
        }

    }

    public int getBurnTime() {
        return this.burnTime;
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.fluids;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class FluidActionResult {
    public static final FluidActionResult FAILURE;
    public final boolean success;
    public final @NotNull ItemStack result;

    public FluidActionResult(@NotNull ItemStack result) {
        this(true, result);
    }

    private FluidActionResult(boolean success, @NotNull ItemStack result) {
        this.success = success;
        this.result = result;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public @NotNull ItemStack getResult() {
        return this.result;
    }

    static {
        FAILURE = new FluidActionResult(false, ItemStack.EMPTY);
    }
}

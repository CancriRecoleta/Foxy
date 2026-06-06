//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.event.entity.player;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class AnvilRepairEvent extends PlayerEvent {
    private final @NotNull ItemStack left;
    private final @NotNull ItemStack right;
    private final @NotNull ItemStack output;
    private float breakChance;

    public AnvilRepairEvent(Player player, @NotNull ItemStack left, @NotNull ItemStack right, @NotNull ItemStack output) {
        super(player);
        this.output = output;
        this.left = left;
        this.right = right;
        this.setBreakChance(0.12F);
    }

    public @NotNull ItemStack getOutput() {
        return this.output;
    }

    public @NotNull ItemStack getLeft() {
        return this.left;
    }

    public @NotNull ItemStack getRight() {
        return this.right;
    }

    public float getBreakChance() {
        return this.breakChance;
    }

    public void setBreakChance(float breakChance) {
        this.breakChance = breakChance;
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.event.entity.player;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerDestroyItemEvent extends PlayerEvent {
    private final @NotNull ItemStack original;
    private final @Nullable InteractionHand hand;

    public PlayerDestroyItemEvent(Player player, @NotNull ItemStack original, @Nullable InteractionHand hand) {
        super(player);
        this.original = original;
        this.hand = hand;
    }

    public @NotNull ItemStack getOriginal() {
        return this.original;
    }

    public @Nullable InteractionHand getHand() {
        return this.hand;
    }
}

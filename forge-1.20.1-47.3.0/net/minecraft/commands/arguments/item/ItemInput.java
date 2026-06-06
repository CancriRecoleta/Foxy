//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.commands.arguments.item;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemInput implements Predicate<ItemStack> {
    private static final Dynamic2CommandExceptionType ERROR_STACK_TOO_BIG = new Dynamic2CommandExceptionType((p_120986_, p_120987_) -> {
        return Component.translatable("arguments.item.overstacked", p_120986_, p_120987_);
    });
    private final Holder<Item> item;
    @Nullable
    private final CompoundTag tag;

    public ItemInput(Holder<Item> p_235282_, @Nullable CompoundTag p_235283_) {
        this.item = p_235282_;
        this.tag = p_235283_;
    }

    public Item getItem() {
        return (Item)this.item.value();
    }

    public boolean test(ItemStack p_120984_) {
        return p_120984_.is(this.item) && NbtUtils.compareNbt(this.tag, p_120984_.getTag(), true);
    }

    public ItemStack createItemStack(int p_120981_, boolean p_120982_) throws CommandSyntaxException {
        ItemStack $$2 = new ItemStack(this.item, p_120981_);
        if (this.tag != null) {
            $$2.setTag(this.tag);
        }

        if (p_120982_ && p_120981_ > $$2.getMaxStackSize()) {
            throw ERROR_STACK_TOO_BIG.create(this.getItemName(), $$2.getMaxStackSize());
        } else {
            return $$2;
        }
    }

    public String serialize() {
        StringBuilder $$0 = new StringBuilder(this.getItemName());
        if (this.tag != null) {
            $$0.append(this.tag);
        }

        return $$0.toString();
    }

    private String getItemName() {
        return this.item.unwrapKey().map(ResourceKey::location).orElseGet(() -> {
            return "unknown[" + this.item + "]";
        }).toString();
    }
}

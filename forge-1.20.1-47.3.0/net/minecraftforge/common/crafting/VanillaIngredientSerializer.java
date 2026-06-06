//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common.crafting;

import com.google.gson.JsonObject;
import java.util.stream.Stream;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class VanillaIngredientSerializer implements IIngredientSerializer<Ingredient> {
    public static final VanillaIngredientSerializer INSTANCE = new VanillaIngredientSerializer();

    public VanillaIngredientSerializer() {
    }

    public Ingredient parse(FriendlyByteBuf buffer) {
        return Ingredient.fromValues(Stream.generate(() -> {
            return new Ingredient.ItemValue(buffer.readItem());
        }).limit((long)buffer.readVarInt()));
    }

    public Ingredient parse(JsonObject json) {
        return Ingredient.fromValues(Stream.of(Ingredient.valueFromJson(json)));
    }

    public void write(FriendlyByteBuf buffer, Ingredient ingredient) {
        ItemStack[] items = ingredient.getItems();
        buffer.writeVarInt(items.length);
        ItemStack[] var4 = items;
        int var5 = items.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            ItemStack stack = var4[var6];
            buffer.writeItem(stack);
        }

    }
}

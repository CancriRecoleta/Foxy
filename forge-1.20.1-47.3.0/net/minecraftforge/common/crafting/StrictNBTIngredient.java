//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.stream.Stream;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class StrictNBTIngredient extends AbstractIngredient {
    private final ItemStack stack;

    protected StrictNBTIngredient(ItemStack stack) {
        super(Stream.of(new Ingredient.ItemValue(stack)));
        this.stack = stack;
    }

    public static StrictNBTIngredient of(ItemStack stack) {
        return new StrictNBTIngredient(stack);
    }

    public boolean test(@Nullable ItemStack input) {
        if (input == null) {
            return false;
        } else {
            return this.stack.getItem() == input.getItem() && this.stack.getDamageValue() == input.getDamageValue() && this.stack.areShareTagsEqual(input);
        }
    }

    public boolean isSimple() {
        return false;
    }

    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return net.minecraftforge.common.crafting.StrictNBTIngredient.Serializer.INSTANCE;
    }

    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", CraftingHelper.getID(net.minecraftforge.common.crafting.StrictNBTIngredient.Serializer.INSTANCE).toString());
        json.addProperty("item", ForgeRegistries.ITEMS.getKey(this.stack.getItem()).toString());
        json.addProperty("count", this.stack.getCount());
        if (this.stack.hasTag()) {
            json.addProperty("nbt", this.stack.getTag().toString());
        }

        return json;
    }

    public static class Serializer implements IIngredientSerializer<StrictNBTIngredient> {
        public static final Serializer INSTANCE = new Serializer();

        public Serializer() {
        }

        public StrictNBTIngredient parse(FriendlyByteBuf buffer) {
            return new StrictNBTIngredient(buffer.readItem());
        }

        public StrictNBTIngredient parse(JsonObject json) {
            return new StrictNBTIngredient(CraftingHelper.getItemStack(json, true));
        }

        public void write(FriendlyByteBuf buffer, StrictNBTIngredient ingredient) {
            buffer.writeItem(ingredient.stack);
        }
    }
}

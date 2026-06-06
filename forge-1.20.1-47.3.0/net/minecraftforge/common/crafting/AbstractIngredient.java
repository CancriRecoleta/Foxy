//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common.crafting;

import com.google.gson.JsonElement;
import java.util.stream.Stream;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractIngredient extends Ingredient {
    protected AbstractIngredient() {
        super(Stream.of());
    }

    protected AbstractIngredient(Stream<? extends Ingredient.Value> values) {
        super(values);
    }

    public abstract boolean isSimple();

    public abstract IIngredientSerializer<? extends Ingredient> getSerializer();

    public abstract JsonElement toJson();

    /** @deprecated */
    @Deprecated
    public static Ingredient fromValues(Stream<? extends Ingredient.Value> values) {
        throw new UnsupportedOperationException("Use Ingredient.fromValues()");
    }

    /** @deprecated */
    @Deprecated
    public static Ingredient of() {
        throw new UnsupportedOperationException("Use Ingredient.of()");
    }

    /** @deprecated */
    @Deprecated
    public static Ingredient of(ItemLike... items) {
        throw new UnsupportedOperationException("Use Ingredient.of()");
    }

    /** @deprecated */
    @Deprecated
    public static Ingredient of(ItemStack... stacks) {
        throw new UnsupportedOperationException("Use Ingredient.of()");
    }

    /** @deprecated */
    @Deprecated
    public static Ingredient of(Stream<ItemStack> stacks) {
        throw new UnsupportedOperationException("Use Ingredient.of()");
    }

    /** @deprecated */
    @Deprecated
    public static Ingredient of(TagKey<Item> tag) {
        throw new UnsupportedOperationException("Use Ingredient.of()");
    }

    /** @deprecated */
    @Deprecated
    public static Ingredient fromNetwork(FriendlyByteBuf buffer) {
        throw new UnsupportedOperationException("Use Ingredient.fromNetwork()");
    }

    /** @deprecated */
    @Deprecated
    public static Ingredient fromJson(@Nullable JsonElement json) {
        throw new UnsupportedOperationException("Use Ingredient.fromJson()");
    }
}

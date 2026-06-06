//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

public class IntersectionIngredient extends AbstractIngredient {
    private final List<Ingredient> children;
    private final boolean isSimple;
    private ItemStack[] intersectedMatchingStacks = null;
    private IntList packedMatchingStacks = null;

    protected IntersectionIngredient(List<Ingredient> children) {
        if (children.size() < 2) {
            throw new IllegalArgumentException("Cannot create an IntersectionIngredient with one or no children");
        } else {
            this.children = Collections.unmodifiableList(children);
            this.isSimple = children.stream().allMatch(Ingredient::isSimple);
        }
    }

    public static Ingredient of(Ingredient... ingredients) {
        if (ingredients.length == 0) {
            throw new IllegalArgumentException("Cannot create an IntersectionIngredient with no children, use Ingredient.of() to create an empty ingredient");
        } else {
            return (Ingredient)(ingredients.length == 1 ? ingredients[0] : new IntersectionIngredient(Arrays.asList(ingredients)));
        }
    }

    public boolean test(@Nullable ItemStack stack) {
        if (stack != null && !stack.isEmpty()) {
            Iterator var2 = this.children.iterator();

            Ingredient ingredient;
            do {
                if (!var2.hasNext()) {
                    return true;
                }

                ingredient = (Ingredient)var2.next();
            } while(ingredient.test(stack));

            return false;
        } else {
            return false;
        }
    }

    public ItemStack[] getItems() {
        if (this.intersectedMatchingStacks == null) {
            this.intersectedMatchingStacks = (ItemStack[])Arrays.stream(((Ingredient)this.children.get(0)).getItems()).filter((stack) -> {
                for(int i = 1; i < this.children.size(); ++i) {
                    if (!((Ingredient)this.children.get(i)).test(stack)) {
                        return false;
                    }
                }

                return true;
            }).toArray((x$0) -> {
                return new ItemStack[x$0];
            });
        }

        return this.intersectedMatchingStacks;
    }

    public boolean isEmpty() {
        return this.children.stream().anyMatch(Ingredient::isEmpty);
    }

    public boolean isSimple() {
        return this.isSimple;
    }

    protected void invalidate() {
        super.invalidate();
        this.intersectedMatchingStacks = null;
        this.packedMatchingStacks = null;
    }

    public IntList getStackingIds() {
        if (this.packedMatchingStacks == null || this.checkInvalidation()) {
            this.markValid();
            ItemStack[] matchingStacks = this.getItems();
            this.packedMatchingStacks = new IntArrayList(matchingStacks.length);
            ItemStack[] var2 = matchingStacks;
            int var3 = matchingStacks.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                ItemStack stack = var2[var4];
                this.packedMatchingStacks.add(StackedContents.getStackingIndex(stack));
            }

            this.packedMatchingStacks.sort(IntComparators.NATURAL_COMPARATOR);
        }

        return this.packedMatchingStacks;
    }

    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", CraftingHelper.getID(net.minecraftforge.common.crafting.IntersectionIngredient.Serializer.INSTANCE).toString());
        JsonArray array = new JsonArray();
        Iterator var3 = this.children.iterator();

        while(var3.hasNext()) {
            Ingredient ingredient = (Ingredient)var3.next();
            array.add(ingredient.toJson());
        }

        json.add("children", array);
        return json;
    }

    public IIngredientSerializer<IntersectionIngredient> getSerializer() {
        return net.minecraftforge.common.crafting.IntersectionIngredient.Serializer.INSTANCE;
    }

    public static class Serializer implements IIngredientSerializer<IntersectionIngredient> {
        public static final IIngredientSerializer<IntersectionIngredient> INSTANCE = new Serializer();

        public Serializer() {
        }

        public IntersectionIngredient parse(JsonObject json) {
            JsonArray children = GsonHelper.getAsJsonArray(json, "children");
            if (children.size() < 2) {
                throw new JsonSyntaxException("Must have at least two children for an intersection ingredient");
            } else {
                return new IntersectionIngredient(IntStream.range(0, children.size()).mapToObj((i) -> {
                    return Ingredient.fromJson(children.get(i), false);
                }).toList());
            }
        }

        public IntersectionIngredient parse(FriendlyByteBuf buffer) {
            return new IntersectionIngredient(Stream.generate(() -> {
                return Ingredient.fromNetwork(buffer);
            }).limit((long)buffer.readVarInt()).toList());
        }

        public void write(FriendlyByteBuf buffer, IntersectionIngredient intersection) {
            buffer.writeVarInt(intersection.children.size());
            Iterator var3 = intersection.children.iterator();

            while(var3.hasNext()) {
                Ingredient ingredient = (Ingredient)var3.next();
                ingredient.toNetwork(buffer);
            }

        }
    }
}

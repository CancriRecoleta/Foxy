//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common.crafting;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CompoundIngredient extends AbstractIngredient {
    private List<Ingredient> children;
    private ItemStack[] stacks;
    private IntList itemIds;
    private final boolean isSimple;

    protected CompoundIngredient(List<Ingredient> children) {
        this.children = Collections.unmodifiableList(children);
        this.isSimple = children.stream().allMatch(Ingredient::isSimple);
    }

    public static Ingredient of(Ingredient... children) {
        if (children.length == 0) {
            throw new IllegalArgumentException("Cannot create a compound ingredient with no children, use Ingredient.of() to create an empty ingredient");
        } else if (children.length == 1) {
            return children[0];
        } else {
            List<Ingredient> vanillaIngredients = new ArrayList();
            List<Ingredient> allIngredients = new ArrayList();
            Ingredient[] var3 = children;
            int var4 = children.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                Ingredient child = var3[var5];
                if (child.getSerializer() == VanillaIngredientSerializer.INSTANCE) {
                    vanillaIngredients.add(child);
                } else {
                    allIngredients.add(child);
                }
            }

            if (!vanillaIngredients.isEmpty()) {
                allIngredients.add(merge(vanillaIngredients));
            }

            if (allIngredients.size() == 1) {
                return (Ingredient)allIngredients.get(0);
            } else {
                return new CompoundIngredient(allIngredients);
            }
        }
    }

    public @NotNull ItemStack[] getItems() {
        if (this.stacks == null) {
            List<ItemStack> tmp = Lists.newArrayList();
            Iterator var2 = this.children.iterator();

            while(var2.hasNext()) {
                Ingredient child = (Ingredient)var2.next();
                Collections.addAll(tmp, child.getItems());
            }

            this.stacks = (ItemStack[])tmp.toArray(new ItemStack[tmp.size()]);
        }

        return this.stacks;
    }

    public @NotNull IntList getStackingIds() {
        boolean childrenNeedInvalidation = false;

        Iterator var2;
        Ingredient child;
        for(var2 = this.children.iterator(); var2.hasNext(); childrenNeedInvalidation |= child.checkInvalidation()) {
            child = (Ingredient)var2.next();
        }

        if (childrenNeedInvalidation || this.itemIds == null || this.checkInvalidation()) {
            this.markValid();
            this.itemIds = new IntArrayList();
            var2 = this.children.iterator();

            while(var2.hasNext()) {
                child = (Ingredient)var2.next();
                this.itemIds.addAll(child.getStackingIds());
            }

            this.itemIds.sort(IntComparators.NATURAL_COMPARATOR);
        }

        return this.itemIds;
    }

    public boolean test(@Nullable ItemStack target) {
        return target == null ? false : this.children.stream().anyMatch((c) -> {
            return c.test(target);
        });
    }

    protected void invalidate() {
        this.itemIds = null;
        this.stacks = null;
    }

    public boolean isSimple() {
        return this.isSimple;
    }

    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return net.minecraftforge.common.crafting.CompoundIngredient.Serializer.INSTANCE;
    }

    public @NotNull Collection<Ingredient> getChildren() {
        return this.children;
    }

    public JsonElement toJson() {
        if (this.children.size() == 1) {
            return ((Ingredient)this.children.get(0)).toJson();
        } else {
            JsonArray json = new JsonArray();
            this.children.stream().forEach((e) -> {
                json.add(e.toJson());
            });
            return json;
        }
    }

    public boolean isEmpty() {
        return this.children.stream().allMatch(Ingredient::isEmpty);
    }

    public static class Serializer implements IIngredientSerializer<CompoundIngredient> {
        public static final Serializer INSTANCE = new Serializer();

        public Serializer() {
        }

        public CompoundIngredient parse(FriendlyByteBuf buffer) {
            return new CompoundIngredient((List)Stream.generate(() -> {
                return Ingredient.fromNetwork(buffer);
            }).limit((long)buffer.readVarInt()).collect(Collectors.toList()));
        }

        public CompoundIngredient parse(JsonObject json) {
            throw new JsonSyntaxException("CompoundIngredient should not be directly referenced in json, just use an array of ingredients.");
        }

        public void write(FriendlyByteBuf buffer, CompoundIngredient ingredient) {
            buffer.writeVarInt(ingredient.children.size());
            ingredient.children.forEach((c) -> {
                c.toNetwork(buffer);
            });
        }
    }
}

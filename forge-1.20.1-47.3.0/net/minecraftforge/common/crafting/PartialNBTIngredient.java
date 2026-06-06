//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common.crafting;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.Nullable;

public class PartialNBTIngredient extends AbstractIngredient {
    private final Set<Item> items;
    private final CompoundTag nbt;
    private final NbtPredicate predicate;

    protected PartialNBTIngredient(Set<Item> items, CompoundTag nbt) {
        super(items.stream().map((item) -> {
            ItemStack stack = new ItemStack(item);
            stack.setTag(nbt.copy());
            return new Ingredient.ItemValue(stack);
        }));
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Cannot create a PartialNBTIngredient with no items");
        } else {
            this.items = Collections.unmodifiableSet(items);
            this.nbt = nbt;
            this.predicate = new NbtPredicate(nbt);
        }
    }

    public static PartialNBTIngredient of(CompoundTag nbt, ItemLike... items) {
        return new PartialNBTIngredient((Set)Arrays.stream(items).map(ItemLike::asItem).collect(Collectors.toSet()), nbt);
    }

    public static PartialNBTIngredient of(ItemLike item, CompoundTag nbt) {
        return new PartialNBTIngredient(Set.of(item.asItem()), nbt);
    }

    public boolean test(@Nullable ItemStack input) {
        if (input == null) {
            return false;
        } else {
            return this.items.contains(input.getItem()) && this.predicate.matches((Tag)input.getShareTag());
        }
    }

    public boolean isSimple() {
        return false;
    }

    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return net.minecraftforge.common.crafting.PartialNBTIngredient.Serializer.INSTANCE;
    }

    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", CraftingHelper.getID(net.minecraftforge.common.crafting.PartialNBTIngredient.Serializer.INSTANCE).toString());
        if (this.items.size() == 1) {
            json.addProperty("item", ForgeRegistries.ITEMS.getKey((Item)this.items.iterator().next()).toString());
        } else {
            JsonArray items = new JsonArray();
            Stream var10000 = this.items.stream();
            IForgeRegistry var10001 = ForgeRegistries.ITEMS;
            Objects.requireNonNull(var10001);
            var10000.map(var10001::getKey).sorted().forEach((name) -> {
                items.add(name.toString());
            });
            json.add("items", items);
        }

        json.addProperty("nbt", this.nbt.toString());
        return json;
    }

    public static class Serializer implements IIngredientSerializer<PartialNBTIngredient> {
        public static final Serializer INSTANCE = new Serializer();

        public Serializer() {
        }

        public PartialNBTIngredient parse(JsonObject json) {
            Object items;
            if (json.has("item")) {
                items = Set.of(CraftingHelper.getItem(GsonHelper.getAsString(json, "item"), true));
            } else {
                if (!json.has("items")) {
                    throw new JsonSyntaxException("Must set either 'item' or 'items'");
                }

                ImmutableSet.Builder<Item> builder = ImmutableSet.builder();
                JsonArray itemArray = GsonHelper.getAsJsonArray(json, "items");

                for(int i = 0; i < itemArray.size(); ++i) {
                    builder.add(CraftingHelper.getItem(GsonHelper.convertToString(itemArray.get(i), "items[" + i + "]"), true));
                }

                items = builder.build();
            }

            if (!json.has("nbt")) {
                throw new JsonSyntaxException("Missing nbt, expected to find a String or JsonObject");
            } else {
                CompoundTag nbt = CraftingHelper.getNBT(json.get("nbt"));
                return new PartialNBTIngredient((Set)items, nbt);
            }
        }

        public PartialNBTIngredient parse(FriendlyByteBuf buffer) {
            Set<Item> items = (Set)Stream.generate(() -> {
                return (Item)buffer.readRegistryIdUnsafe(ForgeRegistries.ITEMS);
            }).limit((long)buffer.readVarInt()).collect(Collectors.toSet());
            CompoundTag nbt = buffer.readNbt();
            return new PartialNBTIngredient(items, (CompoundTag)Objects.requireNonNull(nbt));
        }

        public void write(FriendlyByteBuf buffer, PartialNBTIngredient ingredient) {
            buffer.writeVarInt(ingredient.items.size());
            Iterator var3 = ingredient.items.iterator();

            while(var3.hasNext()) {
                Item item = (Item)var3.next();
                buffer.writeRegistryIdUnsafe(ForgeRegistries.ITEMS, item);
            }

            buffer.writeNbt(ingredient.nbt);
        }
    }
}

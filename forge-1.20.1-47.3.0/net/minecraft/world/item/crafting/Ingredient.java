//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.item.crafting;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.common.crafting.VanillaIngredientSerializer;

public class Ingredient implements Predicate<ItemStack> {
    private static final AtomicInteger INVALIDATION_COUNTER = new AtomicInteger();
    public static final Ingredient EMPTY = new Ingredient(Stream.empty());
    private final Value[] values;
    @Nullable
    private ItemStack[] itemStacks;
    @Nullable
    private IntList stackingIds;
    private int invalidationCounter;
    private final boolean isVanilla = this.getClass() == Ingredient.class;

    public static void invalidateAll() {
        INVALIDATION_COUNTER.incrementAndGet();
    }

    protected Ingredient(Stream<? extends Value> p_43907_) {
        this.values = (Value[])p_43907_.toArray((p_43933_) -> {
            return new Value[p_43933_];
        });
    }

    public ItemStack[] getItems() {
        if (this.itemStacks == null) {
            this.itemStacks = (ItemStack[])Arrays.stream(this.values).flatMap((p_43916_) -> {
                return p_43916_.getItems().stream();
            }).distinct().toArray((p_43910_) -> {
                return new ItemStack[p_43910_];
            });
        }

        return this.itemStacks;
    }

    public boolean test(@Nullable ItemStack p_43914_) {
        if (p_43914_ == null) {
            return false;
        } else if (this.isEmpty()) {
            return p_43914_.isEmpty();
        } else {
            ItemStack[] var2 = this.getItems();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                ItemStack itemstack = var2[var4];
                if (itemstack.is(p_43914_.getItem())) {
                    return true;
                }
            }

            return false;
        }
    }

    public IntList getStackingIds() {
        if (this.stackingIds == null || this.checkInvalidation()) {
            this.markValid();
            ItemStack[] aitemstack = this.getItems();
            this.stackingIds = new IntArrayList(aitemstack.length);
            ItemStack[] var2 = aitemstack;
            int var3 = aitemstack.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                ItemStack itemstack = var2[var4];
                this.stackingIds.add(StackedContents.getStackingIndex(itemstack));
            }

            this.stackingIds.sort(IntComparators.NATURAL_COMPARATOR);
        }

        return this.stackingIds;
    }

    public final void toNetwork(FriendlyByteBuf p_43924_) {
        if (!this.isVanilla()) {
            CraftingHelper.write(p_43924_, this);
        } else {
            p_43924_.writeCollection(Arrays.asList(this.getItems()), FriendlyByteBuf::writeItem);
        }
    }

    public JsonElement toJson() {
        if (this.values.length == 1) {
            return this.values[0].serialize();
        } else {
            JsonArray jsonarray = new JsonArray();
            Value[] var2 = this.values;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                Value ingredient$value = var2[var4];
                jsonarray.add(ingredient$value.serialize());
            }

            return jsonarray;
        }
    }

    public boolean isEmpty() {
        return this.values.length == 0;
    }

    public final boolean checkInvalidation() {
        int currentInvalidationCounter = INVALIDATION_COUNTER.get();
        if (this.invalidationCounter != currentInvalidationCounter) {
            this.invalidate();
            return true;
        } else {
            return false;
        }
    }

    protected final void markValid() {
        this.invalidationCounter = INVALIDATION_COUNTER.get();
    }

    protected void invalidate() {
        this.itemStacks = null;
        this.stackingIds = null;
    }

    public boolean isSimple() {
        return true;
    }

    public final boolean isVanilla() {
        return this.isVanilla;
    }

    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        if (!this.isVanilla()) {
            throw new IllegalStateException("Modders must implement Ingredient.getSerializer in their custom Ingredients: " + this);
        } else {
            return VanillaIngredientSerializer.INSTANCE;
        }
    }

    public static Ingredient fromValues(Stream<? extends Value> p_43939_) {
        Ingredient ingredient = new Ingredient(p_43939_);
        return ingredient.isEmpty() ? EMPTY : ingredient;
    }

    public static Ingredient of() {
        return EMPTY;
    }

    public static Ingredient of(ItemLike... p_43930_) {
        return of(Arrays.stream(p_43930_).map(ItemStack::new));
    }

    public static Ingredient of(ItemStack... p_43928_) {
        return of(Arrays.stream(p_43928_));
    }

    public static Ingredient of(Stream<ItemStack> p_43922_) {
        return fromValues(p_43922_.filter((p_43944_) -> {
            return !p_43944_.isEmpty();
        }).map(ItemValue::new));
    }

    public static Ingredient of(TagKey<Item> p_204133_) {
        return fromValues(Stream.of(new TagValue(p_204133_)));
    }

    public static Ingredient fromNetwork(FriendlyByteBuf p_43941_) {
        int size = p_43941_.readVarInt();
        return size == -1 ? CraftingHelper.getIngredient(p_43941_.readResourceLocation(), p_43941_) : fromValues(Stream.generate(() -> {
            return new ItemValue(p_43941_.readItem());
        }).limit((long)size));
    }

    public static Ingredient fromJson(@Nullable JsonElement p_43918_) {
        return fromJson(p_43918_, true);
    }

    public static Ingredient fromJson(@Nullable JsonElement p_289022_, boolean p_288974_) {
        if (p_289022_ != null && !p_289022_.isJsonNull()) {
            Ingredient ret = CraftingHelper.getIngredient(p_289022_, p_288974_);
            if (ret != null) {
                return ret;
            } else if (p_289022_.isJsonObject()) {
                return fromValues(Stream.of(valueFromJson(p_289022_.getAsJsonObject())));
            } else if (p_289022_.isJsonArray()) {
                JsonArray jsonarray = p_289022_.getAsJsonArray();
                if (jsonarray.size() == 0 && !p_288974_) {
                    throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
                } else {
                    return fromValues(StreamSupport.stream(jsonarray.spliterator(), false).map((p_289756_) -> {
                        return valueFromJson(GsonHelper.convertToJsonObject(p_289756_, "item"));
                    }));
                }
            } else {
                throw new JsonSyntaxException("Expected item to be object or array of objects");
            }
        } else {
            throw new JsonSyntaxException("Item cannot be null");
        }
    }

    public static Value valueFromJson(JsonObject p_289797_) {
        if (p_289797_.has("item") && p_289797_.has("tag")) {
            throw new JsonParseException("An ingredient entry is either a tag or an item, not both");
        } else if (p_289797_.has("item")) {
            Item item = ShapedRecipe.itemFromJson(p_289797_);
            return new ItemValue(new ItemStack(item));
        } else if (p_289797_.has("tag")) {
            ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.getAsString(p_289797_, "tag"));
            TagKey<Item> tagkey = TagKey.create(Registries.ITEM, resourcelocation);
            return new TagValue(tagkey);
        } else {
            throw new JsonParseException("An ingredient entry needs either a tag or an item");
        }
    }

    public static Ingredient merge(Collection<Ingredient> parts) {
        return fromValues(parts.stream().flatMap((i) -> {
            return Arrays.stream(i.values);
        }));
    }

    public interface Value {
        Collection<ItemStack> getItems();

        JsonObject serialize();
    }

    public static class TagValue implements Value {
        private final TagKey<Item> tag;

        public TagValue(TagKey<Item> p_204135_) {
            this.tag = p_204135_;
        }

        public Collection<ItemStack> getItems() {
            List<ItemStack> list = Lists.newArrayList();
            Iterator var2 = BuiltInRegistries.ITEM.getTagOrEmpty(this.tag).iterator();

            while(var2.hasNext()) {
                Holder<Item> holder = (Holder)var2.next();
                list.add(new ItemStack(holder));
            }

            if (list.size() == 0) {
                list.add((new ItemStack(Blocks.BARRIER)).setHoverName(Component.literal("Empty Tag: " + this.tag.location())));
            }

            return list;
        }

        public JsonObject serialize() {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("tag", this.tag.location().toString());
            return jsonobject;
        }
    }

    public static class ItemValue implements Value {
        private final ItemStack item;

        public ItemValue(ItemStack p_43953_) {
            this.item = p_43953_;
        }

        public Collection<ItemStack> getItems() {
            return Collections.singleton(this.item);
        }

        public JsonObject serialize() {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("item", BuiltInRegistries.ITEM.getKey(this.item.getItem()).toString());
            return jsonobject;
        }
    }
}

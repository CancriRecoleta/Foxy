//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.mojang.logging.LogUtils;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.RecipeBookManager;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ClientRecipeBook extends RecipeBook {
    private static final Logger LOGGER = LogUtils.getLogger();
    private Map<RecipeBookCategories, List<RecipeCollection>> collectionsByTab = ImmutableMap.of();
    private List<RecipeCollection> allCollections = ImmutableList.of();

    public ClientRecipeBook() {
    }

    public void setupCollections(Iterable<Recipe<?>> p_266814_, RegistryAccess p_266878_) {
        Map<RecipeBookCategories, List<List<Recipe<?>>>> map = categorizeAndGroupRecipes(p_266814_);
        Map<RecipeBookCategories, List<RecipeCollection>> map1 = Maps.newHashMap();
        ImmutableList.Builder<RecipeCollection> builder = ImmutableList.builder();
        map.forEach((p_266602_, p_266603_) -> {
            Stream var10002 = p_266603_.stream().map((p_266605_) -> {
                return new RecipeCollection(p_266878_, p_266605_);
            });
            Objects.requireNonNull(builder);
            map1.put(p_266602_, (List)var10002.peek(builder::add).collect(ImmutableList.toImmutableList()));
        });
        RecipeBookCategories.AGGREGATE_CATEGORIES.forEach((p_90637_, p_90638_) -> {
            map1.put(p_90637_, (List)p_90638_.stream().flatMap((p_167706_) -> {
                return ((List)map1.getOrDefault(p_167706_, ImmutableList.of())).stream();
            }).collect(ImmutableList.toImmutableList()));
        });
        this.collectionsByTab = ImmutableMap.copyOf(map1);
        this.allCollections = builder.build();
    }

    private static Map<RecipeBookCategories, List<List<Recipe<?>>>> categorizeAndGroupRecipes(Iterable<Recipe<?>> p_90643_) {
        Map<RecipeBookCategories, List<List<Recipe<?>>>> map = Maps.newHashMap();
        Table<RecipeBookCategories, String, List<Recipe<?>>> table = HashBasedTable.create();
        Iterator var3 = p_90643_.iterator();

        while(var3.hasNext()) {
            Recipe<?> recipe = (Recipe)var3.next();
            if (!recipe.isSpecial() && !recipe.isIncomplete()) {
                RecipeBookCategories recipebookcategories = getCategory(recipe);
                String s = recipe.getGroup().isEmpty() ? recipe.getId().toString() : recipe.getGroup();
                if (s.isEmpty()) {
                    ((List)map.computeIfAbsent(recipebookcategories, (p_90645_) -> {
                        return Lists.newArrayList();
                    })).add(ImmutableList.of(recipe));
                } else {
                    List<Recipe<?>> list = (List)table.get(recipebookcategories, s);
                    if (list == null) {
                        list = Lists.newArrayList();
                        table.put(recipebookcategories, s, list);
                        ((List)map.computeIfAbsent(recipebookcategories, (p_90641_) -> {
                            return Lists.newArrayList();
                        })).add(list);
                    }

                    ((List)list).add(recipe);
                }
            }
        }

        return map;
    }

    private static RecipeBookCategories getCategory(Recipe<?> p_90647_) {
        RecipeBookCategories categories;
        if (p_90647_ instanceof CraftingRecipe) {
            CraftingRecipe craftingrecipe = (CraftingRecipe)p_90647_;
            switch (craftingrecipe.category()) {
                case BUILDING -> categories = RecipeBookCategories.CRAFTING_BUILDING_BLOCKS;
                case EQUIPMENT -> categories = RecipeBookCategories.CRAFTING_EQUIPMENT;
                case REDSTONE -> categories = RecipeBookCategories.CRAFTING_REDSTONE;
                case MISC -> categories = RecipeBookCategories.CRAFTING_MISC;
                default -> throw new IncompatibleClassChangeError();
            }

            return categories;
        } else {
            RecipeType<?> recipetype = p_90647_.getType();
            if (p_90647_ instanceof AbstractCookingRecipe) {
                AbstractCookingRecipe abstractcookingrecipe = (AbstractCookingRecipe)p_90647_;
                CookingBookCategory cookingbookcategory = abstractcookingrecipe.category();
                if (recipetype == RecipeType.SMELTING) {
                    RecipeBookCategories recipebookcategories1;
                    switch (cookingbookcategory) {
                        case BLOCKS -> recipebookcategories1 = RecipeBookCategories.FURNACE_BLOCKS;
                        case FOOD -> recipebookcategories1 = RecipeBookCategories.FURNACE_FOOD;
                        case MISC -> recipebookcategories1 = RecipeBookCategories.FURNACE_MISC;
                        default -> throw new IncompatibleClassChangeError();
                    }

                    return recipebookcategories1;
                }

                if (recipetype == RecipeType.BLASTING) {
                    return cookingbookcategory == CookingBookCategory.BLOCKS ? RecipeBookCategories.BLAST_FURNACE_BLOCKS : RecipeBookCategories.BLAST_FURNACE_MISC;
                }

                if (recipetype == RecipeType.SMOKING) {
                    return RecipeBookCategories.SMOKER_FOOD;
                }

                if (recipetype == RecipeType.CAMPFIRE_COOKING) {
                    return RecipeBookCategories.CAMPFIRE;
                }
            }

            if (recipetype == RecipeType.STONECUTTING) {
                return RecipeBookCategories.STONECUTTER;
            } else if (recipetype == RecipeType.SMITHING) {
                return RecipeBookCategories.SMITHING;
            } else {
                categories = RecipeBookManager.findCategories(recipetype, p_90647_);
                if (categories != null) {
                    return categories;
                } else {
                    Logger var10000 = LOGGER;
                    Object var10002 = LogUtils.defer(() -> {
                        return BuiltInRegistries.RECIPE_TYPE.getKey(p_90647_.getType());
                    });
                    Objects.requireNonNull(p_90647_);
                    var10000.warn("Unknown recipe category: {}/{}", var10002, LogUtils.defer(p_90647_::getId));
                    return RecipeBookCategories.UNKNOWN;
                }
            }
        }
    }

    public List<RecipeCollection> getCollections() {
        return this.allCollections;
    }

    public List<RecipeCollection> getCollection(RecipeBookCategories p_90624_) {
        return (List)this.collectionsByTab.getOrDefault(p_90624_, Collections.emptyList());
    }
}

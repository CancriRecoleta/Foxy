//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.screens.recipebook;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.core.RegistryAccess;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RecipeCollection {
    private final RegistryAccess registryAccess;
    private final List<Recipe<?>> recipes;
    private final boolean singleResultItem;
    private final Set<Recipe<?>> craftable = Sets.newHashSet();
    private final Set<Recipe<?>> fitsDimensions = Sets.newHashSet();
    private final Set<Recipe<?>> known = Sets.newHashSet();

    public RecipeCollection(RegistryAccess p_266782_, List<Recipe<?>> p_267051_) {
        this.registryAccess = p_266782_;
        this.recipes = ImmutableList.copyOf(p_267051_);
        if (p_267051_.size() <= 1) {
            this.singleResultItem = true;
        } else {
            this.singleResultItem = allRecipesHaveSameResult(p_266782_, p_267051_);
        }

    }

    private static boolean allRecipesHaveSameResult(RegistryAccess p_267210_, List<Recipe<?>> p_100509_) {
        int $$2 = p_100509_.size();
        ItemStack $$3 = ((Recipe)p_100509_.get(0)).getResultItem(p_267210_);

        for(int $$4 = 1; $$4 < $$2; ++$$4) {
            ItemStack $$5 = ((Recipe)p_100509_.get($$4)).getResultItem(p_267210_);
            if (!ItemStack.isSameItemSameTags($$3, $$5)) {
                return false;
            }
        }

        return true;
    }

    public RegistryAccess registryAccess() {
        return this.registryAccess;
    }

    public boolean hasKnownRecipes() {
        return !this.known.isEmpty();
    }

    public void updateKnownRecipes(RecipeBook p_100500_) {
        Iterator var2 = this.recipes.iterator();

        while(var2.hasNext()) {
            Recipe<?> $$1 = (Recipe)var2.next();
            if (p_100500_.contains($$1)) {
                this.known.add($$1);
            }
        }

    }

    public void canCraft(StackedContents p_100502_, int p_100503_, int p_100504_, RecipeBook p_100505_) {
        Iterator var5 = this.recipes.iterator();

        while(true) {
            while(var5.hasNext()) {
                Recipe<?> $$4 = (Recipe)var5.next();
                boolean $$5 = $$4.canCraftInDimensions(p_100503_, p_100504_) && p_100505_.contains($$4);
                if ($$5) {
                    this.fitsDimensions.add($$4);
                } else {
                    this.fitsDimensions.remove($$4);
                }

                if ($$5 && p_100502_.canCraft($$4, (IntList)null)) {
                    this.craftable.add($$4);
                } else {
                    this.craftable.remove($$4);
                }
            }

            return;
        }
    }

    public boolean isCraftable(Recipe<?> p_100507_) {
        return this.craftable.contains(p_100507_);
    }

    public boolean hasCraftable() {
        return !this.craftable.isEmpty();
    }

    public boolean hasFitting() {
        return !this.fitsDimensions.isEmpty();
    }

    public List<Recipe<?>> getRecipes() {
        return this.recipes;
    }

    public List<Recipe<?>> getRecipes(boolean p_100511_) {
        List<Recipe<?>> $$1 = Lists.newArrayList();
        Set<Recipe<?>> $$2 = p_100511_ ? this.craftable : this.fitsDimensions;
        Iterator var4 = this.recipes.iterator();

        while(var4.hasNext()) {
            Recipe<?> $$3 = (Recipe)var4.next();
            if ($$2.contains($$3)) {
                $$1.add($$3);
            }
        }

        return $$1;
    }

    public List<Recipe<?>> getDisplayRecipes(boolean p_100514_) {
        List<Recipe<?>> $$1 = Lists.newArrayList();
        Iterator var3 = this.recipes.iterator();

        while(var3.hasNext()) {
            Recipe<?> $$2 = (Recipe)var3.next();
            if (this.fitsDimensions.contains($$2) && this.craftable.contains($$2) == p_100514_) {
                $$1.add($$2);
            }
        }

        return $$1;
    }

    public boolean hasSingleResultItem() {
        return this.singleResultItem;
    }
}

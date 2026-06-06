//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.screens.recipebook;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.RecipeBook;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RecipeButton extends AbstractWidget {
    private static final ResourceLocation RECIPE_BOOK_LOCATION = new ResourceLocation("textures/gui/recipe_book.png");
    private static final float ANIMATION_TIME = 15.0F;
    private static final int BACKGROUND_SIZE = 25;
    public static final int TICKS_TO_SWAP = 30;
    private static final Component MORE_RECIPES_TOOLTIP = Component.translatable("gui.recipebook.moreRecipes");
    private RecipeBookMenu<?> menu;
    private RecipeBook book;
    private RecipeCollection collection;
    private float time;
    private float animationTime;
    private int currentIndex;

    public RecipeButton() {
        super(0, 0, 25, 25, CommonComponents.EMPTY);
    }

    public void init(RecipeCollection p_100480_, RecipeBookPage p_100481_) {
        this.collection = p_100480_;
        this.menu = (RecipeBookMenu)p_100481_.getMinecraft().player.containerMenu;
        this.book = p_100481_.getRecipeBook();
        List<Recipe<?>> $$2 = p_100480_.getRecipes(this.book.isFiltering(this.menu));
        Iterator var4 = $$2.iterator();

        while(var4.hasNext()) {
            Recipe<?> $$3 = (Recipe)var4.next();
            if (this.book.willHighlight($$3)) {
                p_100481_.recipesShown($$2);
                this.animationTime = 15.0F;
                break;
            }
        }

    }

    public RecipeCollection getCollection() {
        return this.collection;
    }

    public void renderWidget(GuiGraphics p_281385_, int p_282779_, int p_282744_, float p_282439_) {
        if (!Screen.hasControlDown()) {
            this.time += p_282439_;
        }

        Minecraft $$4 = Minecraft.getInstance();
        int $$5 = 29;
        if (!this.collection.hasCraftable()) {
            $$5 += 25;
        }

        int $$6 = 206;
        if (this.collection.getRecipes(this.book.isFiltering(this.menu)).size() > 1) {
            $$6 += 25;
        }

        boolean $$7 = this.animationTime > 0.0F;
        if ($$7) {
            float $$8 = 1.0F + 0.1F * (float)Math.sin((double)(this.animationTime / 15.0F * 3.1415927F));
            p_281385_.pose().pushPose();
            p_281385_.pose().translate((float)(this.getX() + 8), (float)(this.getY() + 12), 0.0F);
            p_281385_.pose().scale($$8, $$8, 1.0F);
            p_281385_.pose().translate((float)(-(this.getX() + 8)), (float)(-(this.getY() + 12)), 0.0F);
            this.animationTime -= p_282439_;
        }

        p_281385_.blit(RECIPE_BOOK_LOCATION, this.getX(), this.getY(), $$5, $$6, this.width, this.height);
        List<Recipe<?>> $$9 = this.getOrderedRecipes();
        this.currentIndex = Mth.floor(this.time / 30.0F) % $$9.size();
        ItemStack $$10 = ((Recipe)$$9.get(this.currentIndex)).getResultItem(this.collection.registryAccess());
        int $$11 = 4;
        if (this.collection.hasSingleResultItem() && this.getOrderedRecipes().size() > 1) {
            p_281385_.renderItem($$10, this.getX() + $$11 + 1, this.getY() + $$11 + 1, 0, 10);
            --$$11;
        }

        p_281385_.renderFakeItem($$10, this.getX() + $$11, this.getY() + $$11);
        if ($$7) {
            p_281385_.pose().popPose();
        }

    }

    private List<Recipe<?>> getOrderedRecipes() {
        List<Recipe<?>> $$0 = this.collection.getDisplayRecipes(true);
        if (!this.book.isFiltering(this.menu)) {
            $$0.addAll(this.collection.getDisplayRecipes(false));
        }

        return $$0;
    }

    public boolean isOnlyOption() {
        return this.getOrderedRecipes().size() == 1;
    }

    public Recipe<?> getRecipe() {
        List<Recipe<?>> $$0 = this.getOrderedRecipes();
        return (Recipe)$$0.get(this.currentIndex);
    }

    public List<Component> getTooltipText() {
        ItemStack $$0 = ((Recipe)this.getOrderedRecipes().get(this.currentIndex)).getResultItem(this.collection.registryAccess());
        List<Component> $$1 = Lists.newArrayList(Screen.getTooltipFromItem(Minecraft.getInstance(), $$0));
        if (this.collection.getRecipes(this.book.isFiltering(this.menu)).size() > 1) {
            $$1.add(MORE_RECIPES_TOOLTIP);
        }

        return $$1;
    }

    public void updateWidgetNarration(NarrationElementOutput p_170060_) {
        ItemStack $$1 = ((Recipe)this.getOrderedRecipes().get(this.currentIndex)).getResultItem(this.collection.registryAccess());
        p_170060_.add(NarratedElementType.TITLE, (Component)Component.translatable("narration.recipe", $$1.getHoverName()));
        if (this.collection.getRecipes(this.book.isFiltering(this.menu)).size() > 1) {
            p_170060_.add(NarratedElementType.USAGE, Component.translatable("narration.button.usage.hovered"), Component.translatable("narration.recipe.usage.more"));
        } else {
            p_170060_.add(NarratedElementType.USAGE, (Component)Component.translatable("narration.button.usage.hovered"));
        }

    }

    public int getWidth() {
        return 25;
    }

    protected boolean isValidClickButton(int p_100473_) {
        return p_100473_ == 0 || p_100473_ == 1;
    }
}

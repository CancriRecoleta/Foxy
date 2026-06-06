//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.screens.recipebook;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RecipeBookPage {
    public static final int ITEMS_PER_PAGE = 20;
    private final List<RecipeButton> buttons = Lists.newArrayListWithCapacity(20);
    @Nullable
    private RecipeButton hoveredButton;
    private final OverlayRecipeComponent overlay = new OverlayRecipeComponent();
    private Minecraft minecraft;
    private final List<RecipeShownListener> showListeners = Lists.newArrayList();
    private List<RecipeCollection> recipeCollections = ImmutableList.of();
    private StateSwitchingButton forwardButton;
    private StateSwitchingButton backButton;
    private int totalPages;
    private int currentPage;
    private RecipeBook recipeBook;
    @Nullable
    private Recipe<?> lastClickedRecipe;
    @Nullable
    private RecipeCollection lastClickedRecipeCollection;

    public RecipeBookPage() {
        for(int $$0 = 0; $$0 < 20; ++$$0) {
            this.buttons.add(new RecipeButton());
        }

    }

    public void init(Minecraft p_100429_, int p_100430_, int p_100431_) {
        this.minecraft = p_100429_;
        this.recipeBook = p_100429_.player.getRecipeBook();

        for(int $$3 = 0; $$3 < this.buttons.size(); ++$$3) {
            ((RecipeButton)this.buttons.get($$3)).setPosition(p_100430_ + 11 + 25 * ($$3 % 5), p_100431_ + 31 + 25 * ($$3 / 5));
        }

        this.forwardButton = new StateSwitchingButton(p_100430_ + 93, p_100431_ + 137, 12, 17, false);
        this.forwardButton.initTextureValues(1, 208, 13, 18, RecipeBookComponent.RECIPE_BOOK_LOCATION);
        this.backButton = new StateSwitchingButton(p_100430_ + 38, p_100431_ + 137, 12, 17, true);
        this.backButton.initTextureValues(1, 208, 13, 18, RecipeBookComponent.RECIPE_BOOK_LOCATION);
    }

    public void addListener(RecipeBookComponent p_100433_) {
        this.showListeners.remove(p_100433_);
        this.showListeners.add(p_100433_);
    }

    public void updateCollections(List<RecipeCollection> p_100437_, boolean p_100438_) {
        this.recipeCollections = p_100437_;
        this.totalPages = (int)Math.ceil((double)p_100437_.size() / 20.0);
        if (this.totalPages <= this.currentPage || p_100438_) {
            this.currentPage = 0;
        }

        this.updateButtonsForPage();
    }

    private void updateButtonsForPage() {
        int $$0 = 20 * this.currentPage;

        for(int $$1 = 0; $$1 < this.buttons.size(); ++$$1) {
            RecipeButton $$2 = (RecipeButton)this.buttons.get($$1);
            if ($$0 + $$1 < this.recipeCollections.size()) {
                RecipeCollection $$3 = (RecipeCollection)this.recipeCollections.get($$0 + $$1);
                $$2.init($$3, this);
                $$2.visible = true;
            } else {
                $$2.visible = false;
            }
        }

        this.updateArrowButtons();
    }

    private void updateArrowButtons() {
        this.forwardButton.visible = this.totalPages > 1 && this.currentPage < this.totalPages - 1;
        this.backButton.visible = this.totalPages > 1 && this.currentPage > 0;
    }

    public void render(GuiGraphics p_281416_, int p_281888_, int p_281904_, int p_282278_, int p_282424_, float p_281712_) {
        if (this.totalPages > 1) {
            int var10000 = this.currentPage + 1;
            String $$6 = "" + var10000 + "/" + this.totalPages;
            int $$7 = this.minecraft.font.width($$6);
            p_281416_.drawString(this.minecraft.font, (String)$$6, p_281888_ - $$7 / 2 + 73, p_281904_ + 141, -1, false);
        }

        this.hoveredButton = null;
        Iterator var9 = this.buttons.iterator();

        while(var9.hasNext()) {
            RecipeButton $$8 = (RecipeButton)var9.next();
            $$8.render(p_281416_, p_282278_, p_282424_, p_281712_);
            if ($$8.visible && $$8.isHoveredOrFocused()) {
                this.hoveredButton = $$8;
            }
        }

        this.backButton.render(p_281416_, p_282278_, p_282424_, p_281712_);
        this.forwardButton.render(p_281416_, p_282278_, p_282424_, p_281712_);
        this.overlay.render(p_281416_, p_282278_, p_282424_, p_281712_);
    }

    public void renderTooltip(GuiGraphics p_283690_, int p_282626_, int p_282490_) {
        if (this.minecraft.screen != null && this.hoveredButton != null && !this.overlay.isVisible()) {
            p_283690_.renderComponentTooltip(this.minecraft.font, this.hoveredButton.getTooltipText(), p_282626_, p_282490_);
        }

    }

    @Nullable
    public Recipe<?> getLastClickedRecipe() {
        return this.lastClickedRecipe;
    }

    @Nullable
    public RecipeCollection getLastClickedRecipeCollection() {
        return this.lastClickedRecipeCollection;
    }

    public void setInvisible() {
        this.overlay.setVisible(false);
    }

    public boolean mouseClicked(double p_100410_, double p_100411_, int p_100412_, int p_100413_, int p_100414_, int p_100415_, int p_100416_) {
        this.lastClickedRecipe = null;
        this.lastClickedRecipeCollection = null;
        if (this.overlay.isVisible()) {
            if (this.overlay.mouseClicked(p_100410_, p_100411_, p_100412_)) {
                this.lastClickedRecipe = this.overlay.getLastRecipeClicked();
                this.lastClickedRecipeCollection = this.overlay.getRecipeCollection();
            } else {
                this.overlay.setVisible(false);
            }

            return true;
        } else if (this.forwardButton.mouseClicked(p_100410_, p_100411_, p_100412_)) {
            ++this.currentPage;
            this.updateButtonsForPage();
            return true;
        } else if (this.backButton.mouseClicked(p_100410_, p_100411_, p_100412_)) {
            --this.currentPage;
            this.updateButtonsForPage();
            return true;
        } else {
            Iterator var10 = this.buttons.iterator();

            RecipeButton $$7;
            do {
                if (!var10.hasNext()) {
                    return false;
                }

                $$7 = (RecipeButton)var10.next();
            } while(!$$7.mouseClicked(p_100410_, p_100411_, p_100412_));

            if (p_100412_ == 0) {
                this.lastClickedRecipe = $$7.getRecipe();
                this.lastClickedRecipeCollection = $$7.getCollection();
            } else if (p_100412_ == 1 && !this.overlay.isVisible() && !$$7.isOnlyOption()) {
                this.overlay.init(this.minecraft, $$7.getCollection(), $$7.getX(), $$7.getY(), p_100413_ + p_100415_ / 2, p_100414_ + 13 + p_100416_ / 2, (float)$$7.getWidth());
            }

            return true;
        }
    }

    public void recipesShown(List<Recipe<?>> p_100435_) {
        Iterator var2 = this.showListeners.iterator();

        while(var2.hasNext()) {
            RecipeShownListener $$1 = (RecipeShownListener)var2.next();
            $$1.recipesShown(p_100435_);
        }

    }

    public Minecraft getMinecraft() {
        return this.minecraft;
    }

    public RecipeBook getRecipeBook() {
        return this.recipeBook;
    }

    protected void listButtons(Consumer<AbstractWidget> p_170054_) {
        p_170054_.accept(this.forwardButton);
        p_170054_.accept(this.backButton);
        this.buttons.forEach(p_170054_);
    }
}

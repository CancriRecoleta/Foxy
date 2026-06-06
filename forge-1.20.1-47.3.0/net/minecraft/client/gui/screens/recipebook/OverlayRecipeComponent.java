//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.screens.recipebook;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.recipebook.PlaceRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OverlayRecipeComponent implements Renderable, GuiEventListener {
    static final ResourceLocation RECIPE_BOOK_LOCATION = new ResourceLocation("textures/gui/recipe_book.png");
    private static final int MAX_ROW = 4;
    private static final int MAX_ROW_LARGE = 5;
    private static final float ITEM_RENDER_SCALE = 0.375F;
    public static final int BUTTON_SIZE = 25;
    private final List<OverlayRecipeButton> recipeButtons = Lists.newArrayList();
    private boolean isVisible;
    private int x;
    private int y;
    private Minecraft minecraft;
    private RecipeCollection collection;
    @Nullable
    private Recipe<?> lastRecipeClicked;
    float time;
    boolean isFurnaceMenu;

    public OverlayRecipeComponent() {
    }

    public void init(Minecraft p_100195_, RecipeCollection p_100196_, int p_100197_, int p_100198_, int p_100199_, int p_100200_, float p_100201_) {
        this.minecraft = p_100195_;
        this.collection = p_100196_;
        if (p_100195_.player.containerMenu instanceof AbstractFurnaceMenu) {
            this.isFurnaceMenu = true;
        }

        boolean $$7 = p_100195_.player.getRecipeBook().isFiltering((RecipeBookMenu)p_100195_.player.containerMenu);
        List<Recipe<?>> $$8 = p_100196_.getDisplayRecipes(true);
        List<Recipe<?>> $$9 = $$7 ? Collections.emptyList() : p_100196_.getDisplayRecipes(false);
        int $$10 = $$8.size();
        int $$11 = $$10 + $$9.size();
        int $$12 = $$11 <= 16 ? 4 : 5;
        int $$13 = (int)Math.ceil((double)((float)$$11 / (float)$$12));
        this.x = p_100197_;
        this.y = p_100198_;
        float $$14 = (float)(this.x + Math.min($$11, $$12) * 25);
        float $$15 = (float)(p_100199_ + 50);
        if ($$14 > $$15) {
            this.x = (int)((float)this.x - p_100201_ * (float)((int)(($$14 - $$15) / p_100201_)));
        }

        float $$16 = (float)(this.y + $$13 * 25);
        float $$17 = (float)(p_100200_ + 50);
        if ($$16 > $$17) {
            this.y = (int)((float)this.y - p_100201_ * (float)Mth.ceil(($$16 - $$17) / p_100201_));
        }

        float $$18 = (float)this.y;
        float $$19 = (float)(p_100200_ - 100);
        if ($$18 < $$19) {
            this.y = (int)((float)this.y - p_100201_ * (float)Mth.ceil(($$18 - $$19) / p_100201_));
        }

        this.isVisible = true;
        this.recipeButtons.clear();

        for(int $$20 = 0; $$20 < $$11; ++$$20) {
            boolean $$21 = $$20 < $$10;
            Recipe<?> $$22 = $$21 ? (Recipe)$$8.get($$20) : (Recipe)$$9.get($$20 - $$10);
            int $$23 = this.x + 4 + 25 * ($$20 % $$12);
            int $$24 = this.y + 5 + 25 * ($$20 / $$12);
            if (this.isFurnaceMenu) {
                this.recipeButtons.add(new OverlaySmeltingRecipeButton($$23, $$24, $$22, $$21));
            } else {
                this.recipeButtons.add(new OverlayRecipeButton($$23, $$24, $$22, $$21));
            }
        }

        this.lastRecipeClicked = null;
    }

    public RecipeCollection getRecipeCollection() {
        return this.collection;
    }

    @Nullable
    public Recipe<?> getLastRecipeClicked() {
        return this.lastRecipeClicked;
    }

    public boolean mouseClicked(double p_100186_, double p_100187_, int p_100188_) {
        if (p_100188_ != 0) {
            return false;
        } else {
            Iterator var6 = this.recipeButtons.iterator();

            OverlayRecipeButton $$3;
            do {
                if (!var6.hasNext()) {
                    return false;
                }

                $$3 = (OverlayRecipeButton)var6.next();
            } while(!$$3.mouseClicked(p_100186_, p_100187_, p_100188_));

            this.lastRecipeClicked = $$3.recipe;
            return true;
        }
    }

    public boolean isMouseOver(double p_100208_, double p_100209_) {
        return false;
    }

    public void render(GuiGraphics p_281618_, int p_282646_, int p_283687_, float p_283147_) {
        if (this.isVisible) {
            this.time += p_283147_;
            RenderSystem.enableBlend();
            p_281618_.pose().pushPose();
            p_281618_.pose().translate(0.0F, 0.0F, 1000.0F);
            int $$4 = this.recipeButtons.size() <= 16 ? 4 : 5;
            int $$5 = Math.min(this.recipeButtons.size(), $$4);
            int $$6 = Mth.ceil((float)this.recipeButtons.size() / (float)$$4);
            int $$7 = true;
            p_281618_.blitNineSliced(RECIPE_BOOK_LOCATION, this.x, this.y, $$5 * 25 + 8, $$6 * 25 + 8, 4, 32, 32, 82, 208);
            RenderSystem.disableBlend();
            Iterator var9 = this.recipeButtons.iterator();

            while(var9.hasNext()) {
                OverlayRecipeButton $$8 = (OverlayRecipeButton)var9.next();
                $$8.render(p_281618_, p_282646_, p_283687_, p_283147_);
            }

            p_281618_.pose().popPose();
        }
    }

    public void setVisible(boolean p_100205_) {
        this.isVisible = p_100205_;
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    public void setFocused(boolean p_265597_) {
    }

    public boolean isFocused() {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    class OverlaySmeltingRecipeButton extends OverlayRecipeButton {
        public OverlaySmeltingRecipeButton(int p_100262_, int p_100263_, Recipe<?> p_100264_, boolean p_100265_) {
            super(p_100262_, p_100263_, p_100264_, p_100265_);
        }

        protected void calculateIngredientsPositions(Recipe<?> p_100267_) {
            ItemStack[] $$1 = ((Ingredient)p_100267_.getIngredients().get(0)).getItems();
            this.ingredientPos.add(new OverlayRecipeButton.Pos(10, 10, $$1));
        }
    }

    @OnlyIn(Dist.CLIENT)
    private class OverlayRecipeButton extends AbstractWidget implements PlaceRecipe<Ingredient> {
        final Recipe<?> recipe;
        private final boolean isCraftable;
        protected final List<Pos> ingredientPos = Lists.newArrayList();

        public OverlayRecipeButton(int p_100232_, int p_100233_, Recipe<?> p_100234_, boolean p_100235_) {
            super(p_100232_, p_100233_, 200, 20, CommonComponents.EMPTY);
            this.width = 24;
            this.height = 24;
            this.recipe = p_100234_;
            this.isCraftable = p_100235_;
            this.calculateIngredientsPositions(p_100234_);
        }

        protected void calculateIngredientsPositions(Recipe<?> p_100236_) {
            this.placeRecipe(3, 3, -1, p_100236_, p_100236_.getIngredients().iterator(), 0);
        }

        public void updateWidgetNarration(NarrationElementOutput p_259646_) {
            this.defaultButtonNarrationText(p_259646_);
        }

        public void addItemToSlot(Iterator<Ingredient> p_100240_, int p_100241_, int p_100242_, int p_100243_, int p_100244_) {
            ItemStack[] $$5 = ((Ingredient)p_100240_.next()).getItems();
            if ($$5.length != 0) {
                this.ingredientPos.add(new Pos(3 + p_100244_ * 7, 3 + p_100243_ * 7, $$5));
            }

        }

        public void renderWidget(GuiGraphics p_283557_, int p_283483_, int p_282919_, float p_282165_) {
            int $$4 = 152;
            if (!this.isCraftable) {
                $$4 += 26;
            }

            int $$5 = OverlayRecipeComponent.this.isFurnaceMenu ? 130 : 78;
            if (this.isHoveredOrFocused()) {
                $$5 += 26;
            }

            p_283557_.blit(OverlayRecipeComponent.RECIPE_BOOK_LOCATION, this.getX(), this.getY(), $$4, $$5, this.width, this.height);
            p_283557_.pose().pushPose();
            p_283557_.pose().translate((double)(this.getX() + 2), (double)(this.getY() + 2), 150.0);

            for(Iterator var7 = this.ingredientPos.iterator(); var7.hasNext(); p_283557_.pose().popPose()) {
                Pos $$6 = (Pos)var7.next();
                p_283557_.pose().pushPose();
                p_283557_.pose().translate((double)$$6.x, (double)$$6.y, 0.0);
                p_283557_.pose().scale(0.375F, 0.375F, 1.0F);
                p_283557_.pose().translate(-8.0, -8.0, 0.0);
                if ($$6.ingredients.length > 0) {
                    p_283557_.renderItem($$6.ingredients[Mth.floor(OverlayRecipeComponent.this.time / 30.0F) % $$6.ingredients.length], 0, 0);
                }
            }

            p_283557_.pose().popPose();
        }

        @OnlyIn(Dist.CLIENT)
        protected class Pos {
            public final ItemStack[] ingredients;
            public final int x;
            public final int y;

            public Pos(int p_100256_, int p_100257_, ItemStack[] p_100258_) {
                this.x = p_100256_;
                this.y = p_100257_;
                this.ingredients = p_100258_;
            }
        }
    }
}

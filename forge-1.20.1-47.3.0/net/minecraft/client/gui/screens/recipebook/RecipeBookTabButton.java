//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.screens.recipebook;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RecipeBookTabButton extends StateSwitchingButton {
    private final RecipeBookCategories category;
    private static final float ANIMATION_TIME = 15.0F;
    private float animationTime;

    public RecipeBookTabButton(RecipeBookCategories p_100448_) {
        super(0, 0, 35, 27, false);
        this.category = p_100448_;
        this.initTextureValues(153, 2, 35, 0, RecipeBookComponent.RECIPE_BOOK_LOCATION);
    }

    public void startAnimation(Minecraft p_100452_) {
        ClientRecipeBook $$1 = p_100452_.player.getRecipeBook();
        List<RecipeCollection> $$2 = $$1.getCollection(this.category);
        if (p_100452_.player.containerMenu instanceof RecipeBookMenu) {
            Iterator var4 = $$2.iterator();

            while(var4.hasNext()) {
                RecipeCollection $$3 = (RecipeCollection)var4.next();
                Iterator var6 = $$3.getRecipes($$1.isFiltering((RecipeBookMenu)p_100452_.player.containerMenu)).iterator();

                while(var6.hasNext()) {
                    Recipe<?> $$4 = (Recipe)var6.next();
                    if ($$1.willHighlight($$4)) {
                        this.animationTime = 15.0F;
                        return;
                    }
                }
            }

        }
    }

    public void renderWidget(GuiGraphics p_283195_, int p_283508_, int p_281788_, float p_283269_) {
        if (this.animationTime > 0.0F) {
            float $$4 = 1.0F + 0.1F * (float)Math.sin((double)(this.animationTime / 15.0F * 3.1415927F));
            p_283195_.pose().pushPose();
            p_283195_.pose().translate((float)(this.getX() + 8), (float)(this.getY() + 12), 0.0F);
            p_283195_.pose().scale(1.0F, $$4, 1.0F);
            p_283195_.pose().translate((float)(-(this.getX() + 8)), (float)(-(this.getY() + 12)), 0.0F);
        }

        Minecraft $$5 = Minecraft.getInstance();
        RenderSystem.disableDepthTest();
        int $$6 = this.xTexStart;
        int $$7 = this.yTexStart;
        if (this.isStateTriggered) {
            $$6 += this.xDiffTex;
        }

        if (this.isHoveredOrFocused()) {
            $$7 += this.yDiffTex;
        }

        int $$8 = this.getX();
        if (this.isStateTriggered) {
            $$8 -= 2;
        }

        p_283195_.blit(this.resourceLocation, $$8, this.getY(), $$6, $$7, this.width, this.height);
        RenderSystem.enableDepthTest();
        this.renderIcon(p_283195_, $$5.getItemRenderer());
        if (this.animationTime > 0.0F) {
            p_283195_.pose().popPose();
            this.animationTime -= p_283269_;
        }

    }

    private void renderIcon(GuiGraphics p_281802_, ItemRenderer p_282499_) {
        List<ItemStack> $$2 = this.category.getIconItems();
        int $$3 = this.isStateTriggered ? -2 : 0;
        if ($$2.size() == 1) {
            p_281802_.renderFakeItem((ItemStack)$$2.get(0), this.getX() + 9 + $$3, this.getY() + 5);
        } else if ($$2.size() == 2) {
            p_281802_.renderFakeItem((ItemStack)$$2.get(0), this.getX() + 3 + $$3, this.getY() + 5);
            p_281802_.renderFakeItem((ItemStack)$$2.get(1), this.getX() + 14 + $$3, this.getY() + 5);
        }

    }

    public RecipeBookCategories getCategory() {
        return this.category;
    }

    public boolean updateVisibility(ClientRecipeBook p_100450_) {
        List<RecipeCollection> $$1 = p_100450_.getCollection(this.category);
        this.visible = false;
        if ($$1 != null) {
            Iterator var3 = $$1.iterator();

            while(var3.hasNext()) {
                RecipeCollection $$2 = (RecipeCollection)var3.next();
                if ($$2.hasKnownRecipes() && $$2.hasFitting()) {
                    this.visible = true;
                    break;
                }
            }
        }

        return this.visible;
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.screens.inventory;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StonecutterScreen extends AbstractContainerScreen<StonecutterMenu> {
    private static final ResourceLocation BG_LOCATION = new ResourceLocation("textures/gui/container/stonecutter.png");
    private static final int SCROLLER_WIDTH = 12;
    private static final int SCROLLER_HEIGHT = 15;
    private static final int RECIPES_COLUMNS = 4;
    private static final int RECIPES_ROWS = 3;
    private static final int RECIPES_IMAGE_SIZE_WIDTH = 16;
    private static final int RECIPES_IMAGE_SIZE_HEIGHT = 18;
    private static final int SCROLLER_FULL_HEIGHT = 54;
    private static final int RECIPES_X = 52;
    private static final int RECIPES_Y = 14;
    private float scrollOffs;
    private boolean scrolling;
    private int startIndex;
    private boolean displayRecipes;

    public StonecutterScreen(StonecutterMenu p_99310_, Inventory p_99311_, Component p_99312_) {
        super(p_99310_, p_99311_, p_99312_);
        p_99310_.registerUpdateListener(this::containerChanged);
        --this.titleLabelY;
    }

    public void render(GuiGraphics p_281735_, int p_282517_, int p_282840_, float p_282389_) {
        super.render(p_281735_, p_282517_, p_282840_, p_282389_);
        this.renderTooltip(p_281735_, p_282517_, p_282840_);
    }

    protected void renderBg(GuiGraphics p_283115_, float p_282453_, int p_282940_, int p_282328_) {
        this.renderBackground(p_283115_);
        int $$4 = this.leftPos;
        int $$5 = this.topPos;
        p_283115_.blit(BG_LOCATION, $$4, $$5, 0, 0, this.imageWidth, this.imageHeight);
        int $$6 = (int)(41.0F * this.scrollOffs);
        p_283115_.blit(BG_LOCATION, $$4 + 119, $$5 + 15 + $$6, 176 + (this.isScrollBarActive() ? 0 : 12), 0, 12, 15);
        int $$7 = this.leftPos + 52;
        int $$8 = this.topPos + 14;
        int $$9 = this.startIndex + 12;
        this.renderButtons(p_283115_, p_282940_, p_282328_, $$7, $$8, $$9);
        this.renderRecipes(p_283115_, $$7, $$8, $$9);
    }

    protected void renderTooltip(GuiGraphics p_282396_, int p_283157_, int p_282258_) {
        super.renderTooltip(p_282396_, p_283157_, p_282258_);
        if (this.displayRecipes) {
            int $$3 = this.leftPos + 52;
            int $$4 = this.topPos + 14;
            int $$5 = this.startIndex + 12;
            List<StonecutterRecipe> $$6 = ((StonecutterMenu)this.menu).getRecipes();

            for(int $$7 = this.startIndex; $$7 < $$5 && $$7 < ((StonecutterMenu)this.menu).getNumRecipes(); ++$$7) {
                int $$8 = $$7 - this.startIndex;
                int $$9 = $$3 + $$8 % 4 * 16;
                int $$10 = $$4 + $$8 / 4 * 18 + 2;
                if (p_283157_ >= $$9 && p_283157_ < $$9 + 16 && p_282258_ >= $$10 && p_282258_ < $$10 + 18) {
                    p_282396_.renderTooltip(this.font, ((StonecutterRecipe)$$6.get($$7)).getResultItem(this.minecraft.level.registryAccess()), p_283157_, p_282258_);
                }
            }
        }

    }

    private void renderButtons(GuiGraphics p_282733_, int p_282136_, int p_282147_, int p_281987_, int p_281276_, int p_282688_) {
        for(int $$6 = this.startIndex; $$6 < p_282688_ && $$6 < ((StonecutterMenu)this.menu).getNumRecipes(); ++$$6) {
            int $$7 = $$6 - this.startIndex;
            int $$8 = p_281987_ + $$7 % 4 * 16;
            int $$9 = $$7 / 4;
            int $$10 = p_281276_ + $$9 * 18 + 2;
            int $$11 = this.imageHeight;
            if ($$6 == ((StonecutterMenu)this.menu).getSelectedRecipeIndex()) {
                $$11 += 18;
            } else if (p_282136_ >= $$8 && p_282147_ >= $$10 && p_282136_ < $$8 + 16 && p_282147_ < $$10 + 18) {
                $$11 += 36;
            }

            p_282733_.blit(BG_LOCATION, $$8, $$10 - 1, 0, $$11, 16, 18);
        }

    }

    private void renderRecipes(GuiGraphics p_281999_, int p_282658_, int p_282563_, int p_283352_) {
        List<StonecutterRecipe> $$4 = ((StonecutterMenu)this.menu).getRecipes();

        for(int $$5 = this.startIndex; $$5 < p_283352_ && $$5 < ((StonecutterMenu)this.menu).getNumRecipes(); ++$$5) {
            int $$6 = $$5 - this.startIndex;
            int $$7 = p_282658_ + $$6 % 4 * 16;
            int $$8 = $$6 / 4;
            int $$9 = p_282563_ + $$8 * 18 + 2;
            p_281999_.renderItem(((StonecutterRecipe)$$4.get($$5)).getResultItem(this.minecraft.level.registryAccess()), $$7, $$9);
        }

    }

    public boolean mouseClicked(double p_99318_, double p_99319_, int p_99320_) {
        this.scrolling = false;
        if (this.displayRecipes) {
            int $$3 = this.leftPos + 52;
            int $$4 = this.topPos + 14;
            int $$5 = this.startIndex + 12;

            for(int $$6 = this.startIndex; $$6 < $$5; ++$$6) {
                int $$7 = $$6 - this.startIndex;
                double $$8 = p_99318_ - (double)($$3 + $$7 % 4 * 16);
                double $$9 = p_99319_ - (double)($$4 + $$7 / 4 * 18);
                if ($$8 >= 0.0 && $$9 >= 0.0 && $$8 < 16.0 && $$9 < 18.0 && ((StonecutterMenu)this.menu).clickMenuButton(this.minecraft.player, $$6)) {
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
                    this.minecraft.gameMode.handleInventoryButtonClick(((StonecutterMenu)this.menu).containerId, $$6);
                    return true;
                }
            }

            $$3 = this.leftPos + 119;
            $$4 = this.topPos + 9;
            if (p_99318_ >= (double)$$3 && p_99318_ < (double)($$3 + 12) && p_99319_ >= (double)$$4 && p_99319_ < (double)($$4 + 54)) {
                this.scrolling = true;
            }
        }

        return super.mouseClicked(p_99318_, p_99319_, p_99320_);
    }

    public boolean mouseDragged(double p_99322_, double p_99323_, int p_99324_, double p_99325_, double p_99326_) {
        if (this.scrolling && this.isScrollBarActive()) {
            int $$5 = this.topPos + 14;
            int $$6 = $$5 + 54;
            this.scrollOffs = ((float)p_99323_ - (float)$$5 - 7.5F) / ((float)($$6 - $$5) - 15.0F);
            this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
            this.startIndex = (int)((double)(this.scrollOffs * (float)this.getOffscreenRows()) + 0.5) * 4;
            return true;
        } else {
            return super.mouseDragged(p_99322_, p_99323_, p_99324_, p_99325_, p_99326_);
        }
    }

    public boolean mouseScrolled(double p_99314_, double p_99315_, double p_99316_) {
        if (this.isScrollBarActive()) {
            int $$3 = this.getOffscreenRows();
            float $$4 = (float)p_99316_ / (float)$$3;
            this.scrollOffs = Mth.clamp(this.scrollOffs - $$4, 0.0F, 1.0F);
            this.startIndex = (int)((double)(this.scrollOffs * (float)$$3) + 0.5) * 4;
        }

        return true;
    }

    private boolean isScrollBarActive() {
        return this.displayRecipes && ((StonecutterMenu)this.menu).getNumRecipes() > 12;
    }

    protected int getOffscreenRows() {
        return (((StonecutterMenu)this.menu).getNumRecipes() + 4 - 1) / 4 - 3;
    }

    private void containerChanged() {
        this.displayRecipes = ((StonecutterMenu)this.menu).hasInputItem();
        if (!this.displayRecipes) {
            this.scrollOffs = 0.0F;
            this.startIndex = 0;
        }

    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BrewingStandScreen extends AbstractContainerScreen<BrewingStandMenu> {
    private static final ResourceLocation BREWING_STAND_LOCATION = new ResourceLocation("textures/gui/container/brewing_stand.png");
    private static final int[] BUBBLELENGTHS = new int[]{29, 24, 20, 16, 11, 6, 0};

    public BrewingStandScreen(BrewingStandMenu p_98332_, Inventory p_98333_, Component p_98334_) {
        super(p_98332_, p_98333_, p_98334_);
    }

    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width((FormattedText)this.title)) / 2;
    }

    public void render(GuiGraphics p_283297_, int p_283600_, int p_282033_, float p_283410_) {
        this.renderBackground(p_283297_);
        super.render(p_283297_, p_283600_, p_282033_, p_283410_);
        this.renderTooltip(p_283297_, p_283600_, p_282033_);
    }

    protected void renderBg(GuiGraphics p_282963_, float p_282080_, int p_283365_, int p_283150_) {
        int $$4 = (this.width - this.imageWidth) / 2;
        int $$5 = (this.height - this.imageHeight) / 2;
        p_282963_.blit(BREWING_STAND_LOCATION, $$4, $$5, 0, 0, this.imageWidth, this.imageHeight);
        int $$6 = ((BrewingStandMenu)this.menu).getFuel();
        int $$7 = Mth.clamp((18 * $$6 + 20 - 1) / 20, 0, 18);
        if ($$7 > 0) {
            p_282963_.blit(BREWING_STAND_LOCATION, $$4 + 60, $$5 + 44, 176, 29, $$7, 4);
        }

        int $$8 = ((BrewingStandMenu)this.menu).getBrewingTicks();
        if ($$8 > 0) {
            int $$9 = (int)(28.0F * (1.0F - (float)$$8 / 400.0F));
            if ($$9 > 0) {
                p_282963_.blit(BREWING_STAND_LOCATION, $$4 + 97, $$5 + 16, 176, 0, 9, $$9);
            }

            $$9 = BUBBLELENGTHS[$$8 / 2 % 7];
            if ($$9 > 0) {
                p_282963_.blit(BREWING_STAND_LOCATION, $$4 + 63, $$5 + 14 + 29 - $$9, 185, 29 - $$9, 12, $$9);
            }
        }

    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.screens.inventory;

import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.CartographyTableMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CartographyTableScreen extends AbstractContainerScreen<CartographyTableMenu> {
    private static final ResourceLocation BG_LOCATION = new ResourceLocation("textures/gui/container/cartography_table.png");

    public CartographyTableScreen(CartographyTableMenu p_98349_, Inventory p_98350_, Component p_98351_) {
        super(p_98349_, p_98350_, p_98351_);
        this.titleLabelY -= 2;
    }

    public void render(GuiGraphics p_281331_, int p_281706_, int p_282996_, float p_283037_) {
        super.render(p_281331_, p_281706_, p_282996_, p_283037_);
        this.renderTooltip(p_281331_, p_281706_, p_282996_);
    }

    protected void renderBg(GuiGraphics p_282101_, float p_282697_, int p_282380_, int p_282327_) {
        this.renderBackground(p_282101_);
        int $$4 = this.leftPos;
        int $$5 = this.topPos;
        p_282101_.blit(BG_LOCATION, $$4, $$5, 0, 0, this.imageWidth, this.imageHeight);
        ItemStack $$6 = ((CartographyTableMenu)this.menu).getSlot(1).getItem();
        boolean $$7 = $$6.is(Items.MAP);
        boolean $$8 = $$6.is(Items.PAPER);
        boolean $$9 = $$6.is(Items.GLASS_PANE);
        ItemStack $$10 = ((CartographyTableMenu)this.menu).getSlot(0).getItem();
        boolean $$11 = false;
        Integer $$14;
        MapItemSavedData $$13;
        if ($$10.is(Items.FILLED_MAP)) {
            $$14 = MapItem.getMapId($$10);
            $$13 = MapItem.getSavedData((Integer)$$14, this.minecraft.level);
            if ($$13 != null) {
                if ($$13.locked) {
                    $$11 = true;
                    if ($$8 || $$9) {
                        p_282101_.blit(BG_LOCATION, $$4 + 35, $$5 + 31, this.imageWidth + 50, 132, 28, 21);
                    }
                }

                if ($$8 && $$13.scale >= 4) {
                    $$11 = true;
                    p_282101_.blit(BG_LOCATION, $$4 + 35, $$5 + 31, this.imageWidth + 50, 132, 28, 21);
                }
            }
        } else {
            $$14 = null;
            $$13 = null;
        }

        this.renderResultingMap(p_282101_, $$14, $$13, $$7, $$8, $$9, $$11);
    }

    private void renderResultingMap(GuiGraphics p_282167_, @Nullable Integer p_282064_, @Nullable MapItemSavedData p_282045_, boolean p_282086_, boolean p_283531_, boolean p_282645_, boolean p_281646_) {
        int $$7 = this.leftPos;
        int $$8 = this.topPos;
        if (p_283531_ && !p_281646_) {
            p_282167_.blit(BG_LOCATION, $$7 + 67, $$8 + 13, this.imageWidth, 66, 66, 66);
            this.renderMap(p_282167_, p_282064_, p_282045_, $$7 + 85, $$8 + 31, 0.226F);
        } else if (p_282086_) {
            p_282167_.blit(BG_LOCATION, $$7 + 67 + 16, $$8 + 13, this.imageWidth, 132, 50, 66);
            this.renderMap(p_282167_, p_282064_, p_282045_, $$7 + 86, $$8 + 16, 0.34F);
            p_282167_.pose().pushPose();
            p_282167_.pose().translate(0.0F, 0.0F, 1.0F);
            p_282167_.blit(BG_LOCATION, $$7 + 67, $$8 + 13 + 16, this.imageWidth, 132, 50, 66);
            this.renderMap(p_282167_, p_282064_, p_282045_, $$7 + 70, $$8 + 32, 0.34F);
            p_282167_.pose().popPose();
        } else if (p_282645_) {
            p_282167_.blit(BG_LOCATION, $$7 + 67, $$8 + 13, this.imageWidth, 0, 66, 66);
            this.renderMap(p_282167_, p_282064_, p_282045_, $$7 + 71, $$8 + 17, 0.45F);
            p_282167_.pose().pushPose();
            p_282167_.pose().translate(0.0F, 0.0F, 1.0F);
            p_282167_.blit(BG_LOCATION, $$7 + 66, $$8 + 12, 0, this.imageHeight, 66, 66);
            p_282167_.pose().popPose();
        } else {
            p_282167_.blit(BG_LOCATION, $$7 + 67, $$8 + 13, this.imageWidth, 0, 66, 66);
            this.renderMap(p_282167_, p_282064_, p_282045_, $$7 + 71, $$8 + 17, 0.45F);
        }

    }

    private void renderMap(GuiGraphics p_282298_, @Nullable Integer p_281648_, @Nullable MapItemSavedData p_282897_, int p_281632_, int p_282115_, float p_283388_) {
        if (p_281648_ != null && p_282897_ != null) {
            p_282298_.pose().pushPose();
            p_282298_.pose().translate((float)p_281632_, (float)p_282115_, 1.0F);
            p_282298_.pose().scale(p_283388_, p_283388_, 1.0F);
            this.minecraft.gameRenderer.getMapRenderer().render(p_282298_.pose(), p_282298_.bufferSource(), p_281648_, p_282897_, true, 15728880);
            p_282298_.flush();
            p_282298_.pose().popPose();
        }

    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.LoomMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatterns;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LoomScreen extends AbstractContainerScreen<LoomMenu> {
    private static final ResourceLocation BG_LOCATION = new ResourceLocation("textures/gui/container/loom.png");
    private static final int PATTERN_COLUMNS = 4;
    private static final int PATTERN_ROWS = 4;
    private static final int SCROLLER_WIDTH = 12;
    private static final int SCROLLER_HEIGHT = 15;
    private static final int PATTERN_IMAGE_SIZE = 14;
    private static final int SCROLLER_FULL_HEIGHT = 56;
    private static final int PATTERNS_X = 60;
    private static final int PATTERNS_Y = 13;
    private ModelPart flag;
    @Nullable
    private List<Pair<Holder<BannerPattern>, DyeColor>> resultBannerPatterns;
    private ItemStack bannerStack;
    private ItemStack dyeStack;
    private ItemStack patternStack;
    private boolean displayPatterns;
    private boolean hasMaxPatterns;
    private float scrollOffs;
    private boolean scrolling;
    private int startRow;

    public LoomScreen(LoomMenu p_99075_, Inventory p_99076_, Component p_99077_) {
        super(p_99075_, p_99076_, p_99077_);
        this.bannerStack = ItemStack.EMPTY;
        this.dyeStack = ItemStack.EMPTY;
        this.patternStack = ItemStack.EMPTY;
        p_99075_.registerUpdateListener(this::containerChanged);
        this.titleLabelY -= 2;
    }

    protected void init() {
        super.init();
        this.flag = this.minecraft.getEntityModels().bakeLayer(ModelLayers.BANNER).getChild("flag");
    }

    public void render(GuiGraphics p_283513_, int p_282700_, int p_282637_, float p_281433_) {
        super.render(p_283513_, p_282700_, p_282637_, p_281433_);
        this.renderTooltip(p_283513_, p_282700_, p_282637_);
    }

    private int totalRowCount() {
        return Mth.positiveCeilDiv(((LoomMenu)this.menu).getSelectablePatterns().size(), 4);
    }

    protected void renderBg(GuiGraphics p_282870_, float p_281777_, int p_283331_, int p_283087_) {
        this.renderBackground(p_282870_);
        int $$4 = this.leftPos;
        int $$5 = this.topPos;
        p_282870_.blit(BG_LOCATION, $$4, $$5, 0, 0, this.imageWidth, this.imageHeight);
        Slot $$6 = ((LoomMenu)this.menu).getBannerSlot();
        Slot $$7 = ((LoomMenu)this.menu).getDyeSlot();
        Slot $$8 = ((LoomMenu)this.menu).getPatternSlot();
        Slot $$9 = ((LoomMenu)this.menu).getResultSlot();
        if (!$$6.hasItem()) {
            p_282870_.blit(BG_LOCATION, $$4 + $$6.x, $$5 + $$6.y, this.imageWidth, 0, 16, 16);
        }

        if (!$$7.hasItem()) {
            p_282870_.blit(BG_LOCATION, $$4 + $$7.x, $$5 + $$7.y, this.imageWidth + 16, 0, 16, 16);
        }

        if (!$$8.hasItem()) {
            p_282870_.blit(BG_LOCATION, $$4 + $$8.x, $$5 + $$8.y, this.imageWidth + 32, 0, 16, 16);
        }

        int $$10 = (int)(41.0F * this.scrollOffs);
        p_282870_.blit(BG_LOCATION, $$4 + 119, $$5 + 13 + $$10, 232 + (this.displayPatterns ? 0 : 12), 0, 12, 15);
        Lighting.setupForFlatItems();
        if (this.resultBannerPatterns != null && !this.hasMaxPatterns) {
            p_282870_.pose().pushPose();
            p_282870_.pose().translate((float)($$4 + 139), (float)($$5 + 52), 0.0F);
            p_282870_.pose().scale(24.0F, -24.0F, 1.0F);
            p_282870_.pose().translate(0.5F, 0.5F, 0.5F);
            float $$11 = 0.6666667F;
            p_282870_.pose().scale(0.6666667F, -0.6666667F, -0.6666667F);
            this.flag.xRot = 0.0F;
            this.flag.y = -32.0F;
            BannerRenderer.renderPatterns(p_282870_.pose(), p_282870_.bufferSource(), 15728880, OverlayTexture.NO_OVERLAY, this.flag, ModelBakery.BANNER_BASE, true, this.resultBannerPatterns);
            p_282870_.pose().popPose();
            p_282870_.flush();
        } else if (this.hasMaxPatterns) {
            p_282870_.blit(BG_LOCATION, $$4 + $$9.x - 2, $$5 + $$9.y - 2, this.imageWidth, 17, 17, 16);
        }

        if (this.displayPatterns) {
            int $$12 = $$4 + 60;
            int $$13 = $$5 + 13;
            List<Holder<BannerPattern>> $$14 = ((LoomMenu)this.menu).getSelectablePatterns();

            label63:
            for(int $$15 = 0; $$15 < 4; ++$$15) {
                for(int $$16 = 0; $$16 < 4; ++$$16) {
                    int $$17 = $$15 + this.startRow;
                    int $$18 = $$17 * 4 + $$16;
                    if ($$18 >= $$14.size()) {
                        break label63;
                    }

                    int $$19 = $$12 + $$16 * 14;
                    int $$20 = $$13 + $$15 * 14;
                    boolean $$21 = p_283331_ >= $$19 && p_283087_ >= $$20 && p_283331_ < $$19 + 14 && p_283087_ < $$20 + 14;
                    int $$24;
                    if ($$18 == ((LoomMenu)this.menu).getSelectedBannerPatternIndex()) {
                        $$24 = this.imageHeight + 14;
                    } else if ($$21) {
                        $$24 = this.imageHeight + 28;
                    } else {
                        $$24 = this.imageHeight;
                    }

                    p_282870_.blit(BG_LOCATION, $$19, $$20, 0, $$24, 14, 14);
                    this.renderPattern(p_282870_, (Holder)$$14.get($$18), $$19, $$20);
                }
            }
        }

        Lighting.setupFor3DItems();
    }

    private void renderPattern(GuiGraphics p_282452_, Holder<BannerPattern> p_281940_, int p_281872_, int p_282995_) {
        CompoundTag $$4 = new CompoundTag();
        ListTag $$5 = (new BannerPattern.Builder()).addPattern(BannerPatterns.BASE, DyeColor.GRAY).addPattern(p_281940_, DyeColor.WHITE).toListTag();
        $$4.put("Patterns", $$5);
        ItemStack $$6 = new ItemStack(Items.GRAY_BANNER);
        BlockItem.setBlockEntityData($$6, BlockEntityType.BANNER, $$4);
        PoseStack $$7 = new PoseStack();
        $$7.pushPose();
        $$7.translate((float)p_281872_ + 0.5F, (float)(p_282995_ + 16), 0.0F);
        $$7.scale(6.0F, -6.0F, 1.0F);
        $$7.translate(0.5F, 0.5F, 0.0F);
        $$7.translate(0.5F, 0.5F, 0.5F);
        float $$8 = 0.6666667F;
        $$7.scale(0.6666667F, -0.6666667F, -0.6666667F);
        this.flag.xRot = 0.0F;
        this.flag.y = -32.0F;
        List<Pair<Holder<BannerPattern>, DyeColor>> $$9 = BannerBlockEntity.createPatterns(DyeColor.GRAY, BannerBlockEntity.getItemPatterns($$6));
        BannerRenderer.renderPatterns($$7, p_282452_.bufferSource(), 15728880, OverlayTexture.NO_OVERLAY, this.flag, ModelBakery.BANNER_BASE, true, $$9);
        $$7.popPose();
        p_282452_.flush();
    }

    public boolean mouseClicked(double p_99083_, double p_99084_, int p_99085_) {
        this.scrolling = false;
        if (this.displayPatterns) {
            int $$3 = this.leftPos + 60;
            int $$4 = this.topPos + 13;

            for(int $$5 = 0; $$5 < 4; ++$$5) {
                for(int $$6 = 0; $$6 < 4; ++$$6) {
                    double $$7 = p_99083_ - (double)($$3 + $$6 * 14);
                    double $$8 = p_99084_ - (double)($$4 + $$5 * 14);
                    int $$9 = $$5 + this.startRow;
                    int $$10 = $$9 * 4 + $$6;
                    if ($$7 >= 0.0 && $$8 >= 0.0 && $$7 < 14.0 && $$8 < 14.0 && ((LoomMenu)this.menu).clickMenuButton(this.minecraft.player, $$10)) {
                        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_LOOM_SELECT_PATTERN, 1.0F));
                        this.minecraft.gameMode.handleInventoryButtonClick(((LoomMenu)this.menu).containerId, $$10);
                        return true;
                    }
                }
            }

            $$3 = this.leftPos + 119;
            $$4 = this.topPos + 9;
            if (p_99083_ >= (double)$$3 && p_99083_ < (double)($$3 + 12) && p_99084_ >= (double)$$4 && p_99084_ < (double)($$4 + 56)) {
                this.scrolling = true;
            }
        }

        return super.mouseClicked(p_99083_, p_99084_, p_99085_);
    }

    public boolean mouseDragged(double p_99087_, double p_99088_, int p_99089_, double p_99090_, double p_99091_) {
        int $$5 = this.totalRowCount() - 4;
        if (this.scrolling && this.displayPatterns && $$5 > 0) {
            int $$6 = this.topPos + 13;
            int $$7 = $$6 + 56;
            this.scrollOffs = ((float)p_99088_ - (float)$$6 - 7.5F) / ((float)($$7 - $$6) - 15.0F);
            this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
            this.startRow = Math.max((int)((double)(this.scrollOffs * (float)$$5) + 0.5), 0);
            return true;
        } else {
            return super.mouseDragged(p_99087_, p_99088_, p_99089_, p_99090_, p_99091_);
        }
    }

    public boolean mouseScrolled(double p_99079_, double p_99080_, double p_99081_) {
        int $$3 = this.totalRowCount() - 4;
        if (this.displayPatterns && $$3 > 0) {
            float $$4 = (float)p_99081_ / (float)$$3;
            this.scrollOffs = Mth.clamp(this.scrollOffs - $$4, 0.0F, 1.0F);
            this.startRow = Math.max((int)(this.scrollOffs * (float)$$3 + 0.5F), 0);
        }

        return true;
    }

    protected boolean hasClickedOutside(double p_99093_, double p_99094_, int p_99095_, int p_99096_, int p_99097_) {
        return p_99093_ < (double)p_99095_ || p_99094_ < (double)p_99096_ || p_99093_ >= (double)(p_99095_ + this.imageWidth) || p_99094_ >= (double)(p_99096_ + this.imageHeight);
    }

    private void containerChanged() {
        ItemStack $$0 = ((LoomMenu)this.menu).getResultSlot().getItem();
        if ($$0.isEmpty()) {
            this.resultBannerPatterns = null;
        } else {
            this.resultBannerPatterns = BannerBlockEntity.createPatterns(((BannerItem)$$0.getItem()).getColor(), BannerBlockEntity.getItemPatterns($$0));
        }

        ItemStack $$1 = ((LoomMenu)this.menu).getBannerSlot().getItem();
        ItemStack $$2 = ((LoomMenu)this.menu).getDyeSlot().getItem();
        ItemStack $$3 = ((LoomMenu)this.menu).getPatternSlot().getItem();
        CompoundTag $$4 = BlockItem.getBlockEntityData($$1);
        this.hasMaxPatterns = $$4 != null && $$4.contains("Patterns", 9) && !$$1.isEmpty() && $$4.getList("Patterns", 10).size() >= 6;
        if (this.hasMaxPatterns) {
            this.resultBannerPatterns = null;
        }

        if (!ItemStack.matches($$1, this.bannerStack) || !ItemStack.matches($$2, this.dyeStack) || !ItemStack.matches($$3, this.patternStack)) {
            this.displayPatterns = !$$1.isEmpty() && !$$2.isEmpty() && !this.hasMaxPatterns && !((LoomMenu)this.menu).getSelectablePatterns().isEmpty();
        }

        if (this.startRow >= this.totalRowCount()) {
            this.startRow = 0;
            this.scrollOffs = 0.0F;
        }

        this.bannerStack = $$1.copy();
        this.dyeStack = $$2.copy();
        this.patternStack = $$3.copy();
    }
}

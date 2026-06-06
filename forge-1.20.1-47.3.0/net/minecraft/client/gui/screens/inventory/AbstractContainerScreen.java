//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Type;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ContainerScreenEvent;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.client.extensions.common.IClientItemExtensions.FontContext;
import net.minecraftforge.common.MinecraftForge;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractContainerScreen<T extends AbstractContainerMenu> extends Screen implements MenuAccess<T> {
    public static final ResourceLocation INVENTORY_LOCATION = new ResourceLocation("textures/gui/container/inventory.png");
    private static final float SNAPBACK_SPEED = 100.0F;
    private static final int QUICKDROP_DELAY = 500;
    public static final int SLOT_ITEM_BLIT_OFFSET = 100;
    private static final int HOVER_ITEM_BLIT_OFFSET = 200;
    protected int imageWidth = 176;
    protected int imageHeight = 166;
    protected int titleLabelX;
    protected int titleLabelY;
    protected int inventoryLabelX;
    protected int inventoryLabelY;
    protected final T menu;
    protected final Component playerInventoryTitle;
    @Nullable
    protected Slot hoveredSlot;
    @Nullable
    private Slot clickedSlot;
    @Nullable
    private Slot snapbackEnd;
    @Nullable
    private Slot quickdropSlot;
    @Nullable
    private Slot lastClickSlot;
    protected int leftPos;
    protected int topPos;
    private boolean isSplittingStack;
    private ItemStack draggingItem;
    private int snapbackStartX;
    private int snapbackStartY;
    private long snapbackTime;
    private ItemStack snapbackItem;
    private long quickdropTime;
    protected final Set<Slot> quickCraftSlots;
    protected boolean isQuickCrafting;
    private int quickCraftingType;
    private int quickCraftingButton;
    private boolean skipNextRelease;
    private int quickCraftingRemainder;
    private long lastClickTime;
    private int lastClickButton;
    private boolean doubleclick;
    private ItemStack lastQuickMoved;
    protected int slotColor;

    public AbstractContainerScreen(T p_97741_, Inventory p_97742_, Component p_97743_) {
        super(p_97743_);
        this.draggingItem = ItemStack.EMPTY;
        this.snapbackItem = ItemStack.EMPTY;
        this.quickCraftSlots = Sets.newHashSet();
        this.lastQuickMoved = ItemStack.EMPTY;
        this.slotColor = -2130706433;
        this.menu = p_97741_;
        this.playerInventoryTitle = p_97742_.getDisplayName();
        this.skipNextRelease = true;
        this.titleLabelX = 8;
        this.titleLabelY = 6;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    protected void init() {
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
    }

    public void render(GuiGraphics p_283479_, int p_283661_, int p_281248_, float p_281886_) {
        int i = this.leftPos;
        int j = this.topPos;
        this.renderBg(p_283479_, p_281886_, p_283661_, p_281248_);
        MinecraftForge.EVENT_BUS.post(new ContainerScreenEvent.Render.Background(this, p_283479_, p_283661_, p_281248_));
        RenderSystem.disableDepthTest();
        super.render(p_283479_, p_283661_, p_281248_, p_281886_);
        p_283479_.pose().pushPose();
        p_283479_.pose().translate((float)i, (float)j, 0.0F);
        this.hoveredSlot = null;

        int j2;
        int k2;
        for(int k = 0; k < this.menu.slots.size(); ++k) {
            Slot slot = (Slot)this.menu.slots.get(k);
            if (slot.isActive()) {
                this.renderSlot(p_283479_, slot);
            }

            if (this.isHovering(slot, (double)p_283661_, (double)p_281248_) && slot.isActive()) {
                this.hoveredSlot = slot;
                j2 = slot.x;
                k2 = slot.y;
                if (this.hoveredSlot.isHighlightable()) {
                    renderSlotHighlight(p_283479_, j2, k2, 0, this.getSlotColor(k));
                }
            }
        }

        this.renderLabels(p_283479_, p_283661_, p_281248_);
        MinecraftForge.EVENT_BUS.post(new ContainerScreenEvent.Render.Foreground(this, p_283479_, p_283661_, p_281248_));
        ItemStack itemstack = this.draggingItem.isEmpty() ? this.menu.getCarried() : this.draggingItem;
        if (!itemstack.isEmpty()) {
            int l1 = true;
            j2 = this.draggingItem.isEmpty() ? 8 : 16;
            String s = null;
            if (!this.draggingItem.isEmpty() && this.isSplittingStack) {
                itemstack = itemstack.copyWithCount(Mth.ceil((float)itemstack.getCount() / 2.0F));
            } else if (this.isQuickCrafting && this.quickCraftSlots.size() > 1) {
                itemstack = itemstack.copyWithCount(this.quickCraftingRemainder);
                if (itemstack.isEmpty()) {
                    s = ChatFormatting.YELLOW + "0";
                }
            }

            this.renderFloatingItem(p_283479_, itemstack, p_283661_ - i - 8, p_281248_ - j - j2, s);
        }

        if (!this.snapbackItem.isEmpty()) {
            float f = (float)(Util.getMillis() - this.snapbackTime) / 100.0F;
            if (f >= 1.0F) {
                f = 1.0F;
                this.snapbackItem = ItemStack.EMPTY;
            }

            j2 = this.snapbackEnd.x - this.snapbackStartX;
            k2 = this.snapbackEnd.y - this.snapbackStartY;
            int j1 = this.snapbackStartX + (int)((float)j2 * f);
            int k1 = this.snapbackStartY + (int)((float)k2 * f);
            this.renderFloatingItem(p_283479_, this.snapbackItem, j1, k1, (String)null);
        }

        p_283479_.pose().popPose();
        RenderSystem.enableDepthTest();
    }

    public static void renderSlotHighlight(GuiGraphics p_283692_, int p_281453_, int p_281915_, int p_283504_) {
        renderSlotHighlight(p_283692_, p_281453_, p_281915_, p_283504_, -2130706433);
    }

    public static void renderSlotHighlight(GuiGraphics p_283692_, int p_281453_, int p_281915_, int p_283504_, int color) {
        p_283692_.fillGradient(RenderType.guiOverlay(), p_281453_, p_281915_, p_281453_ + 16, p_281915_ + 16, color, color, p_283504_);
    }

    protected void renderTooltip(GuiGraphics p_283594_, int p_282171_, int p_281909_) {
        if (this.menu.getCarried().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
            ItemStack itemstack = this.hoveredSlot.getItem();
            p_283594_.renderTooltip(this.font, this.getTooltipFromContainerItem(itemstack), itemstack.getTooltipImage(), itemstack, p_282171_, p_281909_);
        }

    }

    protected List<Component> getTooltipFromContainerItem(ItemStack p_283689_) {
        return getTooltipFromItem(this.minecraft, p_283689_);
    }

    private void renderFloatingItem(GuiGraphics p_282567_, ItemStack p_281330_, int p_281772_, int p_281689_, String p_282568_) {
        p_282567_.pose().pushPose();
        p_282567_.pose().translate(0.0F, 0.0F, 232.0F);
        p_282567_.renderItem(p_281330_, p_281772_, p_281689_);
        Font font = IClientItemExtensions.of(p_281330_).getFont(p_281330_, FontContext.ITEM_COUNT);
        p_282567_.renderItemDecorations(font == null ? this.font : font, p_281330_, p_281772_, p_281689_ - (this.draggingItem.isEmpty() ? 0 : 8), p_282568_);
        p_282567_.pose().popPose();
    }

    protected void renderLabels(GuiGraphics p_281635_, int p_282681_, int p_283686_) {
        p_281635_.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
        p_281635_.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
    }

    protected abstract void renderBg(GuiGraphics var1, float var2, int var3, int var4);

    private void renderSlot(GuiGraphics p_281607_, Slot p_282613_) {
        int i = p_282613_.x;
        int j = p_282613_.y;
        ItemStack itemstack = p_282613_.getItem();
        boolean flag = false;
        boolean flag1 = p_282613_ == this.clickedSlot && !this.draggingItem.isEmpty() && !this.isSplittingStack;
        ItemStack itemstack1 = this.menu.getCarried();
        String s = null;
        if (p_282613_ == this.clickedSlot && !this.draggingItem.isEmpty() && this.isSplittingStack && !itemstack.isEmpty()) {
            itemstack = itemstack.copyWithCount(itemstack.getCount() / 2);
        } else if (this.isQuickCrafting && this.quickCraftSlots.contains(p_282613_) && !itemstack1.isEmpty()) {
            if (this.quickCraftSlots.size() == 1) {
                return;
            }

            if (AbstractContainerMenu.canItemQuickReplace(p_282613_, itemstack1, true) && this.menu.canDragTo(p_282613_)) {
                flag = true;
                int k = Math.min(itemstack1.getMaxStackSize(), p_282613_.getMaxStackSize(itemstack1));
                int l = p_282613_.getItem().isEmpty() ? 0 : p_282613_.getItem().getCount();
                int i1 = AbstractContainerMenu.getQuickCraftPlaceCount(this.quickCraftSlots, this.quickCraftingType, itemstack1) + l;
                if (i1 > k) {
                    i1 = k;
                    String var10000 = ChatFormatting.YELLOW.toString();
                    s = var10000 + k;
                }

                itemstack = itemstack1.copyWithCount(i1);
            } else {
                this.quickCraftSlots.remove(p_282613_);
                this.recalculateQuickCraftRemaining();
            }
        }

        p_281607_.pose().pushPose();
        p_281607_.pose().translate(0.0F, 0.0F, 100.0F);
        if (itemstack.isEmpty() && p_282613_.isActive()) {
            Pair<ResourceLocation, ResourceLocation> pair = p_282613_.getNoItemIcon();
            if (pair != null) {
                TextureAtlasSprite textureatlassprite = (TextureAtlasSprite)this.minecraft.getTextureAtlas((ResourceLocation)pair.getFirst()).apply((ResourceLocation)pair.getSecond());
                p_281607_.blit(i, j, 0, 16, 16, textureatlassprite);
                flag1 = true;
            }
        }

        if (!flag1) {
            if (flag) {
                p_281607_.fill(i, j, i + 16, j + 16, -2130706433);
            }

            p_281607_.renderItem(itemstack, i, j, p_282613_.x + p_282613_.y * this.imageWidth);
            p_281607_.renderItemDecorations(this.font, itemstack, i, j, s);
        }

        p_281607_.pose().popPose();
    }

    private void recalculateQuickCraftRemaining() {
        ItemStack itemstack = this.menu.getCarried();
        if (!itemstack.isEmpty() && this.isQuickCrafting) {
            if (this.quickCraftingType == 2) {
                this.quickCraftingRemainder = itemstack.getMaxStackSize();
            } else {
                this.quickCraftingRemainder = itemstack.getCount();

                int i;
                int k;
                for(Iterator var2 = this.quickCraftSlots.iterator(); var2.hasNext(); this.quickCraftingRemainder -= k - i) {
                    Slot slot = (Slot)var2.next();
                    ItemStack itemstack1 = slot.getItem();
                    i = itemstack1.isEmpty() ? 0 : itemstack1.getCount();
                    int j = Math.min(itemstack.getMaxStackSize(), slot.getMaxStackSize(itemstack));
                    k = Math.min(AbstractContainerMenu.getQuickCraftPlaceCount(this.quickCraftSlots, this.quickCraftingType, itemstack) + i, j);
                }
            }
        }

    }

    @Nullable
    private Slot findSlot(double p_97745_, double p_97746_) {
        for(int i = 0; i < this.menu.slots.size(); ++i) {
            Slot slot = (Slot)this.menu.slots.get(i);
            if (this.isHovering(slot, p_97745_, p_97746_) && slot.isActive()) {
                return slot;
            }
        }

        return null;
    }

    public boolean mouseClicked(double p_97748_, double p_97749_, int p_97750_) {
        if (super.mouseClicked(p_97748_, p_97749_, p_97750_)) {
            return true;
        } else {
            InputConstants.Key mouseKey = Type.MOUSE.getOrCreate(p_97750_);
            boolean flag = this.minecraft.options.keyPickItem.isActiveAndMatches(mouseKey);
            Slot slot = this.findSlot(p_97748_, p_97749_);
            long i = Util.getMillis();
            this.doubleclick = this.lastClickSlot == slot && i - this.lastClickTime < 250L && this.lastClickButton == p_97750_;
            this.skipNextRelease = false;
            if (p_97750_ != 0 && p_97750_ != 1 && !flag) {
                this.checkHotbarMouseClicked(p_97750_);
            } else {
                int j = this.leftPos;
                int k = this.topPos;
                boolean flag1 = this.hasClickedOutside(p_97748_, p_97749_, j, k, p_97750_);
                if (slot != null) {
                    flag1 = false;
                }

                int l = -1;
                if (slot != null) {
                    l = slot.index;
                }

                if (flag1) {
                    l = -999;
                }

                if ((Boolean)this.minecraft.options.touchscreen().get() && flag1 && this.menu.getCarried().isEmpty()) {
                    this.onClose();
                    return true;
                }

                if (l != -1) {
                    if ((Boolean)this.minecraft.options.touchscreen().get()) {
                        if (slot != null && slot.hasItem()) {
                            this.clickedSlot = slot;
                            this.draggingItem = ItemStack.EMPTY;
                            this.isSplittingStack = p_97750_ == 1;
                        } else {
                            this.clickedSlot = null;
                        }
                    } else if (!this.isQuickCrafting) {
                        if (this.menu.getCarried().isEmpty()) {
                            if (this.minecraft.options.keyPickItem.isActiveAndMatches(mouseKey)) {
                                this.slotClicked(slot, l, p_97750_, ClickType.CLONE);
                            } else {
                                boolean flag2 = l != -999 && (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 344));
                                ClickType clicktype = ClickType.PICKUP;
                                if (flag2) {
                                    this.lastQuickMoved = slot != null && slot.hasItem() ? slot.getItem().copy() : ItemStack.EMPTY;
                                    clicktype = ClickType.QUICK_MOVE;
                                } else if (l == -999) {
                                    clicktype = ClickType.THROW;
                                }

                                this.slotClicked(slot, l, p_97750_, clicktype);
                            }

                            this.skipNextRelease = true;
                        } else {
                            this.isQuickCrafting = true;
                            this.quickCraftingButton = p_97750_;
                            this.quickCraftSlots.clear();
                            if (p_97750_ == 0) {
                                this.quickCraftingType = 0;
                            } else if (p_97750_ == 1) {
                                this.quickCraftingType = 1;
                            } else if (this.minecraft.options.keyPickItem.isActiveAndMatches(mouseKey)) {
                                this.quickCraftingType = 2;
                            }
                        }
                    }
                }
            }

            this.lastClickSlot = slot;
            this.lastClickTime = i;
            this.lastClickButton = p_97750_;
            return true;
        }
    }

    private void checkHotbarMouseClicked(int p_97763_) {
        if (this.hoveredSlot != null && this.menu.getCarried().isEmpty()) {
            if (this.minecraft.options.keySwapOffhand.matchesMouse(p_97763_)) {
                this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, 40, ClickType.SWAP);
                return;
            }

            for(int i = 0; i < 9; ++i) {
                if (this.minecraft.options.keyHotbarSlots[i].matchesMouse(p_97763_)) {
                    this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, i, ClickType.SWAP);
                }
            }
        }

    }

    protected boolean hasClickedOutside(double p_97757_, double p_97758_, int p_97759_, int p_97760_, int p_97761_) {
        return p_97757_ < (double)p_97759_ || p_97758_ < (double)p_97760_ || p_97757_ >= (double)(p_97759_ + this.imageWidth) || p_97758_ >= (double)(p_97760_ + this.imageHeight);
    }

    public boolean mouseDragged(double p_97752_, double p_97753_, int p_97754_, double p_97755_, double p_97756_) {
        Slot slot = this.findSlot(p_97752_, p_97753_);
        ItemStack itemstack = this.menu.getCarried();
        if (this.clickedSlot != null && (Boolean)this.minecraft.options.touchscreen().get()) {
            if (p_97754_ == 0 || p_97754_ == 1) {
                if (this.draggingItem.isEmpty()) {
                    if (slot != this.clickedSlot && !this.clickedSlot.getItem().isEmpty()) {
                        this.draggingItem = this.clickedSlot.getItem().copy();
                    }
                } else if (this.draggingItem.getCount() > 1 && slot != null && AbstractContainerMenu.canItemQuickReplace(slot, this.draggingItem, false)) {
                    long i = Util.getMillis();
                    if (this.quickdropSlot == slot) {
                        if (i - this.quickdropTime > 500L) {
                            this.slotClicked(this.clickedSlot, this.clickedSlot.index, 0, ClickType.PICKUP);
                            this.slotClicked(slot, slot.index, 1, ClickType.PICKUP);
                            this.slotClicked(this.clickedSlot, this.clickedSlot.index, 0, ClickType.PICKUP);
                            this.quickdropTime = i + 750L;
                            this.draggingItem.shrink(1);
                        }
                    } else {
                        this.quickdropSlot = slot;
                        this.quickdropTime = i;
                    }
                }
            }
        } else if (this.isQuickCrafting && slot != null && !itemstack.isEmpty() && (itemstack.getCount() > this.quickCraftSlots.size() || this.quickCraftingType == 2) && AbstractContainerMenu.canItemQuickReplace(slot, itemstack, true) && slot.mayPlace(itemstack) && this.menu.canDragTo(slot)) {
            this.quickCraftSlots.add(slot);
            this.recalculateQuickCraftRemaining();
        }

        return true;
    }

    public boolean mouseReleased(double p_97812_, double p_97813_, int p_97814_) {
        super.mouseReleased(p_97812_, p_97813_, p_97814_);
        Slot slot = this.findSlot(p_97812_, p_97813_);
        int i = this.leftPos;
        int j = this.topPos;
        boolean flag = this.hasClickedOutside(p_97812_, p_97813_, i, j, p_97814_);
        if (slot != null) {
            flag = false;
        }

        InputConstants.Key mouseKey = Type.MOUSE.getOrCreate(p_97814_);
        int k = -1;
        if (slot != null) {
            k = slot.index;
        }

        if (flag) {
            k = -999;
        }

        Slot slot1;
        Iterator var14;
        if (this.doubleclick && slot != null && p_97814_ == 0 && this.menu.canTakeItemForPickAll(ItemStack.EMPTY, slot)) {
            if (hasShiftDown()) {
                if (!this.lastQuickMoved.isEmpty()) {
                    var14 = this.menu.slots.iterator();

                    while(var14.hasNext()) {
                        slot1 = (Slot)var14.next();
                        if (slot1 != null && slot1.mayPickup(this.minecraft.player) && slot1.hasItem() && slot1.isSameInventory(slot) && AbstractContainerMenu.canItemQuickReplace(slot1, this.lastQuickMoved, true)) {
                            this.slotClicked(slot1, slot1.index, p_97814_, ClickType.QUICK_MOVE);
                        }
                    }
                }
            } else {
                this.slotClicked(slot, k, p_97814_, ClickType.PICKUP_ALL);
            }

            this.doubleclick = false;
            this.lastClickTime = 0L;
        } else {
            if (this.isQuickCrafting && this.quickCraftingButton != p_97814_) {
                this.isQuickCrafting = false;
                this.quickCraftSlots.clear();
                this.skipNextRelease = true;
                return true;
            }

            if (this.skipNextRelease) {
                this.skipNextRelease = false;
                return true;
            }

            boolean flag1;
            if (this.clickedSlot != null && (Boolean)this.minecraft.options.touchscreen().get()) {
                if (p_97814_ == 0 || p_97814_ == 1) {
                    if (this.draggingItem.isEmpty() && slot != this.clickedSlot) {
                        this.draggingItem = this.clickedSlot.getItem();
                    }

                    flag1 = AbstractContainerMenu.canItemQuickReplace(slot, this.draggingItem, false);
                    if (k != -1 && !this.draggingItem.isEmpty() && flag1) {
                        this.slotClicked(this.clickedSlot, this.clickedSlot.index, p_97814_, ClickType.PICKUP);
                        this.slotClicked(slot, k, 0, ClickType.PICKUP);
                        if (this.menu.getCarried().isEmpty()) {
                            this.snapbackItem = ItemStack.EMPTY;
                        } else {
                            this.slotClicked(this.clickedSlot, this.clickedSlot.index, p_97814_, ClickType.PICKUP);
                            this.snapbackStartX = Mth.floor(p_97812_ - (double)i);
                            this.snapbackStartY = Mth.floor(p_97813_ - (double)j);
                            this.snapbackEnd = this.clickedSlot;
                            this.snapbackItem = this.draggingItem;
                            this.snapbackTime = Util.getMillis();
                        }
                    } else if (!this.draggingItem.isEmpty()) {
                        this.snapbackStartX = Mth.floor(p_97812_ - (double)i);
                        this.snapbackStartY = Mth.floor(p_97813_ - (double)j);
                        this.snapbackEnd = this.clickedSlot;
                        this.snapbackItem = this.draggingItem;
                        this.snapbackTime = Util.getMillis();
                    }

                    this.clearDraggingState();
                }
            } else if (this.isQuickCrafting && !this.quickCraftSlots.isEmpty()) {
                this.slotClicked((Slot)null, -999, AbstractContainerMenu.getQuickcraftMask(0, this.quickCraftingType), ClickType.QUICK_CRAFT);
                var14 = this.quickCraftSlots.iterator();

                while(var14.hasNext()) {
                    slot1 = (Slot)var14.next();
                    this.slotClicked(slot1, slot1.index, AbstractContainerMenu.getQuickcraftMask(1, this.quickCraftingType), ClickType.QUICK_CRAFT);
                }

                this.slotClicked((Slot)null, -999, AbstractContainerMenu.getQuickcraftMask(2, this.quickCraftingType), ClickType.QUICK_CRAFT);
            } else if (!this.menu.getCarried().isEmpty()) {
                if (this.minecraft.options.keyPickItem.isActiveAndMatches(mouseKey)) {
                    this.slotClicked(slot, k, p_97814_, ClickType.CLONE);
                } else {
                    flag1 = k != -999 && (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 344));
                    if (flag1) {
                        this.lastQuickMoved = slot != null && slot.hasItem() ? slot.getItem().copy() : ItemStack.EMPTY;
                    }

                    this.slotClicked(slot, k, p_97814_, flag1 ? ClickType.QUICK_MOVE : ClickType.PICKUP);
                }
            }
        }

        if (this.menu.getCarried().isEmpty()) {
            this.lastClickTime = 0L;
        }

        this.isQuickCrafting = false;
        return true;
    }

    public void clearDraggingState() {
        this.draggingItem = ItemStack.EMPTY;
        this.clickedSlot = null;
    }

    private boolean isHovering(Slot p_97775_, double p_97776_, double p_97777_) {
        return this.isHovering(p_97775_.x, p_97775_.y, 16, 16, p_97776_, p_97777_);
    }

    protected boolean isHovering(int p_97768_, int p_97769_, int p_97770_, int p_97771_, double p_97772_, double p_97773_) {
        int i = this.leftPos;
        int j = this.topPos;
        p_97772_ -= (double)i;
        p_97773_ -= (double)j;
        return p_97772_ >= (double)(p_97768_ - 1) && p_97772_ < (double)(p_97768_ + p_97770_ + 1) && p_97773_ >= (double)(p_97769_ - 1) && p_97773_ < (double)(p_97769_ + p_97771_ + 1);
    }

    protected void slotClicked(Slot p_97778_, int p_97779_, int p_97780_, ClickType p_97781_) {
        if (p_97778_ != null) {
            p_97779_ = p_97778_.index;
        }

        this.minecraft.gameMode.handleInventoryMouseClick(this.menu.containerId, p_97779_, p_97780_, p_97781_, this.minecraft.player);
    }

    public boolean keyPressed(int p_97765_, int p_97766_, int p_97767_) {
        InputConstants.Key mouseKey = InputConstants.getKey(p_97765_, p_97766_);
        if (super.keyPressed(p_97765_, p_97766_, p_97767_)) {
            return true;
        } else if (this.minecraft.options.keyInventory.isActiveAndMatches(mouseKey)) {
            this.onClose();
            return true;
        } else {
            boolean handled = this.checkHotbarKeyPressed(p_97765_, p_97766_);
            if (this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
                if (this.minecraft.options.keyPickItem.isActiveAndMatches(mouseKey)) {
                    this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, 0, ClickType.CLONE);
                    handled = true;
                } else if (this.minecraft.options.keyDrop.isActiveAndMatches(mouseKey)) {
                    this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, hasControlDown() ? 1 : 0, ClickType.THROW);
                    handled = true;
                }
            } else if (this.minecraft.options.keyDrop.isActiveAndMatches(mouseKey)) {
                handled = true;
            }

            return handled;
        }
    }

    protected boolean checkHotbarKeyPressed(int p_97806_, int p_97807_) {
        if (this.menu.getCarried().isEmpty() && this.hoveredSlot != null) {
            if (this.minecraft.options.keySwapOffhand.isActiveAndMatches(InputConstants.getKey(p_97806_, p_97807_))) {
                this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, 40, ClickType.SWAP);
                return true;
            }

            for(int i = 0; i < 9; ++i) {
                if (this.minecraft.options.keyHotbarSlots[i].isActiveAndMatches(InputConstants.getKey(p_97806_, p_97807_))) {
                    this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, i, ClickType.SWAP);
                    return true;
                }
            }
        }

        return false;
    }

    public void removed() {
        if (this.minecraft.player != null) {
            this.menu.removed(this.minecraft.player);
        }

    }

    public boolean isPauseScreen() {
        return false;
    }

    public final void tick() {
        super.tick();
        if (this.minecraft.player.isAlive() && !this.minecraft.player.isRemoved()) {
            this.containerTick();
        } else {
            this.minecraft.player.closeContainer();
        }

    }

    protected void containerTick() {
    }

    public T getMenu() {
        return this.menu;
    }

    public @org.jetbrains.annotations.Nullable Slot getSlotUnderMouse() {
        return this.hoveredSlot;
    }

    public int getGuiLeft() {
        return this.leftPos;
    }

    public int getGuiTop() {
        return this.topPos;
    }

    public int getXSize() {
        return this.imageWidth;
    }

    public int getYSize() {
        return this.imageHeight;
    }

    public int getSlotColor(int index) {
        return this.slotColor;
    }

    public void onClose() {
        this.minecraft.player.closeContainer();
        super.onClose();
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.mojang.realmsclient.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.dto.RealmsServer.WorldType;
import com.mojang.realmsclient.util.RealmsTextureManager;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsWorldSlotButton extends Button {
    public static final ResourceLocation SLOT_FRAME_LOCATION = new ResourceLocation("realms", "textures/gui/realms/slot_frame.png");
    public static final ResourceLocation EMPTY_SLOT_LOCATION = new ResourceLocation("realms", "textures/gui/realms/empty_frame.png");
    public static final ResourceLocation CHECK_MARK_LOCATION = new ResourceLocation("minecraft", "textures/gui/checkmark.png");
    public static final ResourceLocation DEFAULT_WORLD_SLOT_1 = new ResourceLocation("minecraft", "textures/gui/title/background/panorama_0.png");
    public static final ResourceLocation DEFAULT_WORLD_SLOT_2 = new ResourceLocation("minecraft", "textures/gui/title/background/panorama_2.png");
    public static final ResourceLocation DEFAULT_WORLD_SLOT_3 = new ResourceLocation("minecraft", "textures/gui/title/background/panorama_3.png");
    private static final Component SLOT_ACTIVE_TOOLTIP = Component.translatable("mco.configure.world.slot.tooltip.active");
    private static final Component SWITCH_TO_MINIGAME_SLOT_TOOLTIP = Component.translatable("mco.configure.world.slot.tooltip.minigame");
    private static final Component SWITCH_TO_WORLD_SLOT_TOOLTIP = Component.translatable("mco.configure.world.slot.tooltip");
    private static final Component MINIGAME = Component.translatable("mco.worldSlot.minigame");
    private final Supplier<RealmsServer> serverDataProvider;
    private final Consumer<Component> toolTipSetter;
    private final int slotIndex;
    @Nullable
    private State state;

    public RealmsWorldSlotButton(int p_87929_, int p_87930_, int p_87931_, int p_87932_, Supplier<RealmsServer> p_87933_, Consumer<Component> p_87934_, int p_87935_, Button.OnPress p_87936_) {
        super(p_87929_, p_87930_, p_87931_, p_87932_, CommonComponents.EMPTY, p_87936_, DEFAULT_NARRATION);
        this.serverDataProvider = p_87933_;
        this.slotIndex = p_87935_;
        this.toolTipSetter = p_87934_;
    }

    @Nullable
    public State getState() {
        return this.state;
    }

    public void tick() {
        RealmsServer $$0 = (RealmsServer)this.serverDataProvider.get();
        if ($$0 != null) {
            RealmsWorldOptions $$1 = (RealmsWorldOptions)$$0.slots.get(this.slotIndex);
            boolean $$2 = this.slotIndex == 4;
            boolean $$8;
            String $$9;
            long $$10;
            String $$11;
            boolean $$12;
            if ($$2) {
                $$8 = $$0.worldType == WorldType.MINIGAME;
                $$9 = MINIGAME.getString();
                $$10 = (long)$$0.minigameId;
                $$11 = $$0.minigameImage;
                $$12 = $$0.minigameId == -1;
            } else {
                $$8 = $$0.activeSlot == this.slotIndex && $$0.worldType != WorldType.MINIGAME;
                $$9 = $$1.getSlotName(this.slotIndex);
                $$10 = $$1.templateId;
                $$11 = $$1.templateImage;
                $$12 = $$1.empty;
            }

            Action $$13 = getAction($$0, $$8, $$2);
            Pair<Component, Component> $$14 = this.getTooltipAndNarration($$0, $$9, $$12, $$2, $$13);
            this.state = new State($$8, $$9, $$10, $$11, $$12, $$2, $$13, (Component)$$14.getFirst());
            this.setMessage((Component)$$14.getSecond());
        }
    }

    private static Action getAction(RealmsServer p_87960_, boolean p_87961_, boolean p_87962_) {
        if (p_87961_) {
            if (!p_87960_.expired && p_87960_.state != com.mojang.realmsclient.dto.RealmsServer.State.UNINITIALIZED) {
                return com.mojang.realmsclient.gui.RealmsWorldSlotButton.Action.JOIN;
            }
        } else {
            if (!p_87962_) {
                return com.mojang.realmsclient.gui.RealmsWorldSlotButton.Action.SWITCH_SLOT;
            }

            if (!p_87960_.expired) {
                return com.mojang.realmsclient.gui.RealmsWorldSlotButton.Action.SWITCH_SLOT;
            }
        }

        return com.mojang.realmsclient.gui.RealmsWorldSlotButton.Action.NOTHING;
    }

    private Pair<Component, Component> getTooltipAndNarration(RealmsServer p_87954_, String p_87955_, boolean p_87956_, boolean p_87957_, Action p_87958_) {
        if (p_87958_ == com.mojang.realmsclient.gui.RealmsWorldSlotButton.Action.NOTHING) {
            return Pair.of((Object)null, Component.literal(p_87955_));
        } else {
            Object $$7;
            if (p_87957_) {
                if (p_87956_) {
                    $$7 = CommonComponents.EMPTY;
                } else {
                    $$7 = CommonComponents.space().append(p_87955_).append(CommonComponents.SPACE).append(p_87954_.minigameName);
                }
            } else {
                $$7 = CommonComponents.space().append(p_87955_);
            }

            Component $$9;
            if (p_87958_ == com.mojang.realmsclient.gui.RealmsWorldSlotButton.Action.JOIN) {
                $$9 = SLOT_ACTIVE_TOOLTIP;
            } else {
                $$9 = p_87957_ ? SWITCH_TO_MINIGAME_SLOT_TOOLTIP : SWITCH_TO_WORLD_SLOT_TOOLTIP;
            }

            Component $$10 = $$9.copy().append((Component)$$7);
            return Pair.of($$9, $$10);
        }
    }

    public void renderWidget(GuiGraphics p_282947_, int p_87965_, int p_87966_, float p_87967_) {
        if (this.state != null) {
            this.drawSlotFrame(p_282947_, this.getX(), this.getY(), p_87965_, p_87966_, this.state.isCurrentlyActiveSlot, this.state.slotName, this.slotIndex, this.state.imageId, this.state.image, this.state.empty, this.state.minigame, this.state.action, this.state.actionPrompt);
        }
    }

    private void drawSlotFrame(GuiGraphics p_282493_, int p_282407_, int p_283212_, int p_283646_, int p_283633_, boolean p_282019_, String p_283553_, int p_283521_, long p_281546_, @Nullable String p_283361_, boolean p_283516_, boolean p_281611_, Action p_281804_, @Nullable Component p_282910_) {
        boolean $$14 = this.isHoveredOrFocused();
        if (this.isMouseOver((double)p_283646_, (double)p_283633_) && p_282910_ != null) {
            this.toolTipSetter.accept(p_282910_);
        }

        Minecraft $$15 = Minecraft.getInstance();
        ResourceLocation $$22;
        if (p_281611_) {
            $$22 = RealmsTextureManager.worldTemplate(String.valueOf(p_281546_), p_283361_);
        } else if (p_283516_) {
            $$22 = EMPTY_SLOT_LOCATION;
        } else if (p_283361_ != null && p_281546_ != -1L) {
            $$22 = RealmsTextureManager.worldTemplate(String.valueOf(p_281546_), p_283361_);
        } else if (p_283521_ == 1) {
            $$22 = DEFAULT_WORLD_SLOT_1;
        } else if (p_283521_ == 2) {
            $$22 = DEFAULT_WORLD_SLOT_2;
        } else if (p_283521_ == 3) {
            $$22 = DEFAULT_WORLD_SLOT_3;
        } else {
            $$22 = EMPTY_SLOT_LOCATION;
        }

        if (p_282019_) {
            p_282493_.setColor(0.56F, 0.56F, 0.56F, 1.0F);
        }

        p_282493_.blit($$22, p_282407_ + 3, p_283212_ + 3, 0.0F, 0.0F, 74, 74, 74, 74);
        boolean $$23 = $$14 && p_281804_ != com.mojang.realmsclient.gui.RealmsWorldSlotButton.Action.NOTHING;
        if ($$23) {
            p_282493_.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        } else if (p_282019_) {
            p_282493_.setColor(0.8F, 0.8F, 0.8F, 1.0F);
        } else {
            p_282493_.setColor(0.56F, 0.56F, 0.56F, 1.0F);
        }

        p_282493_.blit(SLOT_FRAME_LOCATION, p_282407_, p_283212_, 0.0F, 0.0F, 80, 80, 80, 80);
        p_282493_.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        if (p_282019_) {
            this.renderCheckMark(p_282493_, p_282407_, p_283212_);
        }

        p_282493_.drawCenteredString($$15.font, p_283553_, p_282407_ + 40, p_283212_ + 66, 16777215);
    }

    private void renderCheckMark(GuiGraphics p_281366_, int p_281849_, int p_283407_) {
        RenderSystem.enableBlend();
        p_281366_.blit(CHECK_MARK_LOCATION, p_281849_ + 67, p_283407_ + 4, 0.0F, 0.0F, 9, 8, 9, 8);
        RenderSystem.disableBlend();
    }

    @OnlyIn(Dist.CLIENT)
    public static class State {
        final boolean isCurrentlyActiveSlot;
        final String slotName;
        final long imageId;
        @Nullable
        final String image;
        public final boolean empty;
        public final boolean minigame;
        public final Action action;
        @Nullable
        final Component actionPrompt;

        State(boolean p_87989_, String p_87990_, long p_87991_, @Nullable String p_87992_, boolean p_87993_, boolean p_87994_, Action p_87995_, @Nullable Component p_87996_) {
            this.isCurrentlyActiveSlot = p_87989_;
            this.slotName = p_87990_;
            this.imageId = p_87991_;
            this.image = p_87992_;
            this.empty = p_87993_;
            this.minigame = p_87994_;
            this.action = p_87995_;
            this.actionPrompt = p_87996_;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static enum Action {
        NOTHING,
        SWITCH_SLOT,
        JOIN;

        private Action() {
        }
    }
}

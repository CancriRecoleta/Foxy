//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.dto.RealmsServer.State;
import com.mojang.realmsclient.dto.RealmsServer.WorldType;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsWorldSlotButton;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen.Type;
import com.mojang.realmsclient.util.RealmsTextureManager;
import com.mojang.realmsclient.util.task.OpenServerTask;
import com.mojang.realmsclient.util.task.SwitchSlotTask;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsBrokenWorldScreen extends RealmsScreen {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int DEFAULT_BUTTON_WIDTH = 80;
    private final Screen lastScreen;
    private final RealmsMainScreen mainScreen;
    @Nullable
    private RealmsServer serverData;
    private final long serverId;
    private final Component[] message = new Component[]{Component.translatable("mco.brokenworld.message.line1"), Component.translatable("mco.brokenworld.message.line2")};
    private int leftX;
    private int rightX;
    private final List<Integer> slotsThatHasBeenDownloaded = Lists.newArrayList();
    private int animTick;

    public RealmsBrokenWorldScreen(Screen p_88296_, RealmsMainScreen p_88297_, long p_88298_, boolean p_88299_) {
        super(p_88299_ ? Component.translatable("mco.brokenworld.minigame.title") : Component.translatable("mco.brokenworld.title"));
        this.lastScreen = p_88296_;
        this.mainScreen = p_88297_;
        this.serverId = p_88298_;
    }

    public void init() {
        this.leftX = this.width / 2 - 150;
        this.rightX = this.width / 2 + 190;
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, (p_88333_) -> {
            this.backButtonClicked();
        }).bounds(this.rightX - 80 + 8, row(13) - 5, 70, 20).build());
        if (this.serverData == null) {
            this.fetchServerData(this.serverId);
        } else {
            this.addButtons();
        }

    }

    public Component getNarrationMessage() {
        return ComponentUtils.formatList((Collection)Stream.concat(Stream.of(this.title), Stream.of(this.message)).collect(Collectors.toList()), CommonComponents.SPACE);
    }

    private void addButtons() {
        Iterator var1 = this.serverData.slots.entrySet().iterator();

        while(var1.hasNext()) {
            Map.Entry<Integer, RealmsWorldOptions> $$0 = (Map.Entry)var1.next();
            int $$1 = (Integer)$$0.getKey();
            boolean $$2 = $$1 != this.serverData.activeSlot || this.serverData.worldType == WorldType.MINIGAME;
            Button $$4;
            if ($$2) {
                $$4 = Button.builder(Component.translatable("mco.brokenworld.play"), (p_88347_) -> {
                    if (((RealmsWorldOptions)this.serverData.slots.get($$1)).empty) {
                        RealmsResetWorldScreen $$2 = new RealmsResetWorldScreen(this, this.serverData, Component.translatable("mco.configure.world.switch.slot"), Component.translatable("mco.configure.world.switch.slot.subtitle"), 10526880, CommonComponents.GUI_CANCEL, this::doSwitchOrReset, () -> {
                            this.minecraft.setScreen(this);
                            this.doSwitchOrReset();
                        });
                        $$2.setSlot($$1);
                        $$2.setResetTitle(Component.translatable("mco.create.world.reset.title"));
                        this.minecraft.setScreen($$2);
                    } else {
                        this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new SwitchSlotTask(this.serverData.id, $$1, this::doSwitchOrReset)));
                    }

                }).bounds(this.getFramePositionX($$1), row(8), 80, 20).build();
            } else {
                $$4 = Button.builder(Component.translatable("mco.brokenworld.download"), (p_287302_) -> {
                    Component $$2 = Component.translatable("mco.configure.world.restore.download.question.line1");
                    Component $$3 = Component.translatable("mco.configure.world.restore.download.question.line2");
                    this.minecraft.setScreen(new RealmsLongConfirmationScreen((p_280705_) -> {
                        if (p_280705_) {
                            this.downloadWorld($$1);
                        } else {
                            this.minecraft.setScreen(this);
                        }

                    }, Type.INFO, $$2, $$3, true));
                }).bounds(this.getFramePositionX($$1), row(8), 80, 20).build();
            }

            if (this.slotsThatHasBeenDownloaded.contains($$1)) {
                $$4.active = false;
                $$4.setMessage(Component.translatable("mco.brokenworld.downloaded"));
            }

            this.addRenderableWidget($$4);
            this.addRenderableWidget(Button.builder(Component.translatable("mco.brokenworld.reset"), (p_280707_) -> {
                RealmsResetWorldScreen $$2 = new RealmsResetWorldScreen(this, this.serverData, this::doSwitchOrReset, () -> {
                    this.minecraft.setScreen(this);
                    this.doSwitchOrReset();
                });
                if ($$1 != this.serverData.activeSlot || this.serverData.worldType == WorldType.MINIGAME) {
                    $$2.setSlot($$1);
                }

                this.minecraft.setScreen($$2);
            }).bounds(this.getFramePositionX($$1), row(10), 80, 20).build());
        }

    }

    public void tick() {
        ++this.animTick;
    }

    public void render(GuiGraphics p_282934_, int p_88317_, int p_88318_, float p_88319_) {
        this.renderBackground(p_282934_);
        super.render(p_282934_, p_88317_, p_88318_, p_88319_);
        p_282934_.drawCenteredString(this.font, (Component)this.title, this.width / 2, 17, 16777215);

        for(int $$4 = 0; $$4 < this.message.length; ++$$4) {
            p_282934_.drawCenteredString(this.font, this.message[$$4], this.width / 2, row(-1) + 3 + $$4 * 12, 10526880);
        }

        if (this.serverData != null) {
            Iterator var7 = this.serverData.slots.entrySet().iterator();

            while(true) {
                while(var7.hasNext()) {
                    Map.Entry<Integer, RealmsWorldOptions> $$5 = (Map.Entry)var7.next();
                    if (((RealmsWorldOptions)$$5.getValue()).templateImage != null && ((RealmsWorldOptions)$$5.getValue()).templateId != -1L) {
                        this.drawSlotFrame(p_282934_, this.getFramePositionX((Integer)$$5.getKey()), row(1) + 5, p_88317_, p_88318_, this.serverData.activeSlot == (Integer)$$5.getKey() && !this.isMinigame(), ((RealmsWorldOptions)$$5.getValue()).getSlotName((Integer)$$5.getKey()), (Integer)$$5.getKey(), ((RealmsWorldOptions)$$5.getValue()).templateId, ((RealmsWorldOptions)$$5.getValue()).templateImage, ((RealmsWorldOptions)$$5.getValue()).empty);
                    } else {
                        this.drawSlotFrame(p_282934_, this.getFramePositionX((Integer)$$5.getKey()), row(1) + 5, p_88317_, p_88318_, this.serverData.activeSlot == (Integer)$$5.getKey() && !this.isMinigame(), ((RealmsWorldOptions)$$5.getValue()).getSlotName((Integer)$$5.getKey()), (Integer)$$5.getKey(), -1L, (String)null, ((RealmsWorldOptions)$$5.getValue()).empty);
                    }
                }

                return;
            }
        }
    }

    private int getFramePositionX(int p_88302_) {
        return this.leftX + (p_88302_ - 1) * 110;
    }

    public boolean keyPressed(int p_88304_, int p_88305_, int p_88306_) {
        if (p_88304_ == 256) {
            this.backButtonClicked();
            return true;
        } else {
            return super.keyPressed(p_88304_, p_88305_, p_88306_);
        }
    }

    private void backButtonClicked() {
        this.minecraft.setScreen(this.lastScreen);
    }

    private void fetchServerData(long p_88314_) {
        (new Thread(() -> {
            RealmsClient $$1 = RealmsClient.create();

            try {
                this.serverData = $$1.getOwnWorld(p_88314_);
                this.addButtons();
            } catch (RealmsServiceException var5) {
                RealmsServiceException $$2 = var5;
                LOGGER.error("Couldn't get own world");
                this.minecraft.setScreen(new RealmsGenericErrorScreen(Component.nullToEmpty($$2.getMessage()), this.lastScreen));
            }

        })).start();
    }

    public void doSwitchOrReset() {
        (new Thread(() -> {
            RealmsClient $$0 = RealmsClient.create();
            if (this.serverData.state == State.CLOSED) {
                this.minecraft.execute(() -> {
                    this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this, new OpenServerTask(this.serverData, this, this.mainScreen, true, this.minecraft)));
                });
            } else {
                try {
                    RealmsServer $$1 = $$0.getOwnWorld(this.serverId);
                    this.minecraft.execute(() -> {
                        this.mainScreen.newScreen().play($$1, this);
                    });
                } catch (RealmsServiceException var3) {
                    LOGGER.error("Couldn't get own world");
                    this.minecraft.execute(() -> {
                        this.minecraft.setScreen(this.lastScreen);
                    });
                }
            }

        })).start();
    }

    private void downloadWorld(int p_88336_) {
        RealmsClient $$1 = RealmsClient.create();

        try {
            WorldDownload $$2 = $$1.requestDownloadInfo(this.serverData.id, p_88336_);
            RealmsDownloadLatestWorldScreen $$3 = new RealmsDownloadLatestWorldScreen(this, $$2, this.serverData.getWorldName(p_88336_), (p_280702_) -> {
                if (p_280702_) {
                    this.slotsThatHasBeenDownloaded.add(p_88336_);
                    this.clearWidgets();
                    this.addButtons();
                } else {
                    this.minecraft.setScreen(this);
                }

            });
            this.minecraft.setScreen($$3);
        } catch (RealmsServiceException var5) {
            RealmsServiceException $$4 = var5;
            LOGGER.error("Couldn't download world data");
            this.minecraft.setScreen(new RealmsGenericErrorScreen($$4, this));
        }

    }

    private boolean isMinigame() {
        return this.serverData != null && this.serverData.worldType == WorldType.MINIGAME;
    }

    private void drawSlotFrame(GuiGraphics p_281929_, int p_283393_, int p_281553_, int p_283523_, int p_282823_, boolean p_283032_, String p_283498_, int p_283330_, long p_283588_, @Nullable String p_282484_, boolean p_282283_) {
        ResourceLocation $$16;
        if (p_282283_) {
            $$16 = RealmsWorldSlotButton.EMPTY_SLOT_LOCATION;
        } else if (p_282484_ != null && p_283588_ != -1L) {
            $$16 = RealmsTextureManager.worldTemplate(String.valueOf(p_283588_), p_282484_);
        } else if (p_283330_ == 1) {
            $$16 = RealmsWorldSlotButton.DEFAULT_WORLD_SLOT_1;
        } else if (p_283330_ == 2) {
            $$16 = RealmsWorldSlotButton.DEFAULT_WORLD_SLOT_2;
        } else if (p_283330_ == 3) {
            $$16 = RealmsWorldSlotButton.DEFAULT_WORLD_SLOT_3;
        } else {
            $$16 = RealmsTextureManager.worldTemplate(String.valueOf(this.serverData.minigameId), this.serverData.minigameImage);
        }

        if (!p_283032_) {
            p_281929_.setColor(0.56F, 0.56F, 0.56F, 1.0F);
        } else if (p_283032_) {
            float $$17 = 0.9F + 0.1F * Mth.cos((float)this.animTick * 0.2F);
            p_281929_.setColor($$17, $$17, $$17, 1.0F);
        }

        p_281929_.blit($$16, p_283393_ + 3, p_281553_ + 3, 0.0F, 0.0F, 74, 74, 74, 74);
        if (p_283032_) {
            p_281929_.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        } else {
            p_281929_.setColor(0.56F, 0.56F, 0.56F, 1.0F);
        }

        p_281929_.blit(RealmsWorldSlotButton.SLOT_FRAME_LOCATION, p_283393_, p_281553_, 0.0F, 0.0F, 80, 80, 80, 80);
        p_281929_.drawCenteredString(this.font, p_283498_, p_283393_ + 40, p_281553_ + 66, 16777215);
        p_281929_.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}

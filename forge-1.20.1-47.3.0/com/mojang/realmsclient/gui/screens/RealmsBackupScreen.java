//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.mojang.realmsclient.gui.screens;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.Backup;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen.Type;
import com.mojang.realmsclient.util.RealmsUtil;
import com.mojang.realmsclient.util.task.DownloadTask;
import com.mojang.realmsclient.util.task.RestoreTask;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsBackupScreen extends RealmsScreen {
    static final Logger LOGGER = LogUtils.getLogger();
    static final ResourceLocation PLUS_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/plus_icon.png");
    static final ResourceLocation RESTORE_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/restore_icon.png");
    static final Component RESTORE_TOOLTIP = Component.translatable("mco.backup.button.restore");
    static final Component HAS_CHANGES_TOOLTIP = Component.translatable("mco.backup.changes.tooltip");
    private static final Component TITLE = Component.translatable("mco.configure.world.backup");
    private static final Component NO_BACKUPS_LABEL = Component.translatable("mco.backup.nobackups");
    private final RealmsConfigureWorldScreen lastScreen;
    List<Backup> backups = Collections.emptyList();
    BackupObjectSelectionList backupObjectSelectionList;
    int selectedBackup = -1;
    private final int slotId;
    private Button downloadButton;
    private Button restoreButton;
    private Button changesButton;
    Boolean noBackups = false;
    final RealmsServer serverData;
    private static final String UPLOADED_KEY = "uploaded";

    public RealmsBackupScreen(RealmsConfigureWorldScreen p_88126_, RealmsServer p_88127_, int p_88128_) {
        super(Component.translatable("mco.configure.world.backup"));
        this.lastScreen = p_88126_;
        this.serverData = p_88127_;
        this.slotId = p_88128_;
    }

    public void init() {
        this.backupObjectSelectionList = new BackupObjectSelectionList();
        (new Thread("Realms-fetch-backups") {
            public void run() {
                RealmsClient $$0 = RealmsClient.create();

                try {
                    List<Backup> $$1 = $$0.backupsFor(RealmsBackupScreen.this.serverData.id).backups;
                    RealmsBackupScreen.this.minecraft.execute(() -> {
                        RealmsBackupScreen.this.backups = $$1;
                        RealmsBackupScreen.this.noBackups = RealmsBackupScreen.this.backups.isEmpty();
                        RealmsBackupScreen.this.backupObjectSelectionList.clear();
                        Iterator var2 = RealmsBackupScreen.this.backups.iterator();

                        while(var2.hasNext()) {
                            Backup $$1x = (Backup)var2.next();
                            RealmsBackupScreen.this.backupObjectSelectionList.addEntry($$1x);
                        }

                    });
                } catch (RealmsServiceException var3) {
                    RealmsServiceException $$2 = var3;
                    RealmsBackupScreen.LOGGER.error("Couldn't request backups", $$2);
                }

            }
        }).start();
        this.downloadButton = (Button)this.addRenderableWidget(Button.builder(Component.translatable("mco.backup.button.download"), (p_88185_) -> {
            this.downloadClicked();
        }).bounds(this.width - 135, row(1), 120, 20).build());
        this.restoreButton = (Button)this.addRenderableWidget(Button.builder(Component.translatable("mco.backup.button.restore"), (p_88179_) -> {
            this.restoreClicked(this.selectedBackup);
        }).bounds(this.width - 135, row(3), 120, 20).build());
        this.changesButton = (Button)this.addRenderableWidget(Button.builder(Component.translatable("mco.backup.changes.tooltip"), (p_280692_) -> {
            this.minecraft.setScreen(new RealmsBackupInfoScreen(this, (Backup)this.backups.get(this.selectedBackup)));
            this.selectedBackup = -1;
        }).bounds(this.width - 135, row(5), 120, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, (p_280691_) -> {
            this.minecraft.setScreen(this.lastScreen);
        }).bounds(this.width - 100, this.height - 35, 85, 20).build());
        this.addWidget(this.backupObjectSelectionList);
        this.magicalSpecialHackyFocus(this.backupObjectSelectionList);
        this.updateButtonStates();
    }

    void updateButtonStates() {
        this.restoreButton.visible = this.shouldRestoreButtonBeVisible();
        this.changesButton.visible = this.shouldChangesButtonBeVisible();
    }

    private boolean shouldChangesButtonBeVisible() {
        if (this.selectedBackup == -1) {
            return false;
        } else {
            return !((Backup)this.backups.get(this.selectedBackup)).changeList.isEmpty();
        }
    }

    private boolean shouldRestoreButtonBeVisible() {
        if (this.selectedBackup == -1) {
            return false;
        } else {
            return !this.serverData.expired;
        }
    }

    public boolean keyPressed(int p_88133_, int p_88134_, int p_88135_) {
        if (p_88133_ == 256) {
            this.minecraft.setScreen(this.lastScreen);
            return true;
        } else {
            return super.keyPressed(p_88133_, p_88134_, p_88135_);
        }
    }

    void restoreClicked(int p_88167_) {
        if (p_88167_ >= 0 && p_88167_ < this.backups.size() && !this.serverData.expired) {
            this.selectedBackup = p_88167_;
            Date $$1 = ((Backup)this.backups.get(p_88167_)).lastModifiedDate;
            String $$2 = DateFormat.getDateTimeInstance(3, 3).format($$1);
            Component $$3 = RealmsUtil.convertToAgePresentationFromInstant($$1);
            Component $$4 = Component.translatable("mco.configure.world.restore.question.line1", $$2, $$3);
            Component $$5 = Component.translatable("mco.configure.world.restore.question.line2");
            this.minecraft.setScreen(new RealmsLongConfirmationScreen((p_280693_) -> {
                if (p_280693_) {
                    this.restore();
                } else {
                    this.selectedBackup = -1;
                    this.minecraft.setScreen(this);
                }

            }, Type.WARNING, $$4, $$5, true));
        }

    }

    private void downloadClicked() {
        Component $$0 = Component.translatable("mco.configure.world.restore.download.question.line1");
        Component $$1 = Component.translatable("mco.configure.world.restore.download.question.line2");
        this.minecraft.setScreen(new RealmsLongConfirmationScreen((p_280690_) -> {
            if (p_280690_) {
                this.downloadWorldData();
            } else {
                this.minecraft.setScreen(this);
            }

        }, Type.INFO, $$0, $$1, true));
    }

    private void downloadWorldData() {
        Minecraft var10000 = this.minecraft;
        RealmsConfigureWorldScreen var10003 = this.lastScreen.getNewScreen();
        String var10008 = this.serverData.name;
        var10000.setScreen(new RealmsLongRunningMcoTaskScreen(var10003, new DownloadTask(this.serverData.id, this.slotId, var10008 + " (" + ((RealmsWorldOptions)this.serverData.slots.get(this.serverData.activeSlot)).getSlotName(this.serverData.activeSlot) + ")", this)));
    }

    private void restore() {
        Backup $$0 = (Backup)this.backups.get(this.selectedBackup);
        this.selectedBackup = -1;
        this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen.getNewScreen(), new RestoreTask($$0, this.serverData.id, this.lastScreen)));
    }

    public void render(GuiGraphics p_283405_, int p_282020_, int p_282404_, float p_281280_) {
        this.renderBackground(p_283405_);
        this.backupObjectSelectionList.render(p_283405_, p_282020_, p_282404_, p_281280_);
        p_283405_.drawCenteredString(this.font, (Component)this.title, this.width / 2, 12, 16777215);
        p_283405_.drawString(this.font, (Component)TITLE, (this.width - 150) / 2 - 90, 20, 10526880, false);
        if (this.noBackups) {
            p_283405_.drawString(this.font, (Component)NO_BACKUPS_LABEL, 20, this.height / 2 - 10, 16777215, false);
        }

        this.downloadButton.active = !this.noBackups;
        super.render(p_283405_, p_282020_, p_282404_, p_281280_);
    }

    @OnlyIn(Dist.CLIENT)
    private class BackupObjectSelectionList extends RealmsObjectSelectionList<Entry> {
        public BackupObjectSelectionList() {
            super(RealmsBackupScreen.this.width - 150, RealmsBackupScreen.this.height, 32, RealmsBackupScreen.this.height - 15, 36);
        }

        public void addEntry(Backup p_88235_) {
            this.addEntry(RealmsBackupScreen.this.new Entry(p_88235_));
        }

        public int getRowWidth() {
            return (int)((double)this.width * 0.93);
        }

        public int getMaxPosition() {
            return this.getItemCount() * 36;
        }

        public void renderBackground(GuiGraphics p_282900_) {
            RealmsBackupScreen.this.renderBackground(p_282900_);
        }

        public int getScrollbarPosition() {
            return this.width - 5;
        }

        public void selectItem(int p_88225_) {
            super.selectItem(p_88225_);
            this.selectInviteListItem(p_88225_);
        }

        public void selectInviteListItem(int p_88242_) {
            RealmsBackupScreen.this.selectedBackup = p_88242_;
            RealmsBackupScreen.this.updateButtonStates();
        }

        public void setSelected(@Nullable Entry p_88237_) {
            super.setSelected(p_88237_);
            RealmsBackupScreen.this.selectedBackup = this.children().indexOf(p_88237_);
            RealmsBackupScreen.this.updateButtonStates();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private class Entry extends ObjectSelectionList.Entry<Entry> {
        private static final int Y_PADDING = 2;
        private static final int X_PADDING = 7;
        private final Backup backup;
        private final List<AbstractWidget> children = new ArrayList();
        @Nullable
        private ImageButton restoreButton;
        @Nullable
        private ImageButton changesButton;

        public Entry(Backup p_88250_) {
            this.backup = p_88250_;
            this.populateChangeList(p_88250_);
            if (!p_88250_.changeList.isEmpty()) {
                this.addChangesButton();
            }

            if (!RealmsBackupScreen.this.serverData.expired) {
                this.addRestoreButton();
            }

        }

        private void populateChangeList(Backup p_279365_) {
            int $$1 = RealmsBackupScreen.this.backups.indexOf(p_279365_);
            if ($$1 != RealmsBackupScreen.this.backups.size() - 1) {
                Backup $$2 = (Backup)RealmsBackupScreen.this.backups.get($$1 + 1);
                Iterator var4 = p_279365_.metadata.keySet().iterator();

                while(true) {
                    while(var4.hasNext()) {
                        String $$3 = (String)var4.next();
                        if (!$$3.contains("uploaded") && $$2.metadata.containsKey($$3)) {
                            if (!((String)p_279365_.metadata.get($$3)).equals($$2.metadata.get($$3))) {
                                this.addToChangeList($$3);
                            }
                        } else {
                            this.addToChangeList($$3);
                        }
                    }

                    return;
                }
            }
        }

        private void addToChangeList(String p_279195_) {
            if (p_279195_.contains("uploaded")) {
                String $$1 = DateFormat.getDateTimeInstance(3, 3).format(this.backup.lastModifiedDate);
                this.backup.changeList.put(p_279195_, $$1);
                this.backup.setUploadedVersion(true);
            } else {
                this.backup.changeList.put(p_279195_, (String)this.backup.metadata.get(p_279195_));
            }

        }

        private void addChangesButton() {
            int $$0 = true;
            int $$1 = true;
            int $$2 = RealmsBackupScreen.this.backupObjectSelectionList.getRowRight() - 9 - 28;
            int $$3 = RealmsBackupScreen.this.backupObjectSelectionList.getRowTop(RealmsBackupScreen.this.backups.indexOf(this.backup)) + 2;
            this.changesButton = new ImageButton($$2, $$3, 9, 9, 0, 0, 9, RealmsBackupScreen.PLUS_ICON_LOCATION, 9, 18, (p_279278_) -> {
                RealmsBackupScreen.this.minecraft.setScreen(new RealmsBackupInfoScreen(RealmsBackupScreen.this, this.backup));
            });
            this.changesButton.setTooltip(Tooltip.create(RealmsBackupScreen.HAS_CHANGES_TOOLTIP));
            this.children.add(this.changesButton);
        }

        private void addRestoreButton() {
            int $$0 = true;
            int $$1 = true;
            int $$2 = RealmsBackupScreen.this.backupObjectSelectionList.getRowRight() - 17 - 7;
            int $$3 = RealmsBackupScreen.this.backupObjectSelectionList.getRowTop(RealmsBackupScreen.this.backups.indexOf(this.backup)) + 2;
            this.restoreButton = new ImageButton($$2, $$3, 17, 10, 0, 0, 10, RealmsBackupScreen.RESTORE_ICON_LOCATION, 17, 20, (p_279191_) -> {
                RealmsBackupScreen.this.restoreClicked(RealmsBackupScreen.this.backups.indexOf(this.backup));
            });
            this.restoreButton.setTooltip(Tooltip.create(RealmsBackupScreen.RESTORE_TOOLTIP));
            this.children.add(this.restoreButton);
        }

        public boolean mouseClicked(double p_279279_, double p_279118_, int p_279445_) {
            if (this.restoreButton != null) {
                this.restoreButton.mouseClicked(p_279279_, p_279118_, p_279445_);
            }

            if (this.changesButton != null) {
                this.changesButton.mouseClicked(p_279279_, p_279118_, p_279445_);
            }

            return true;
        }

        public void render(GuiGraphics p_281408_, int p_281974_, int p_282495_, int p_282463_, int p_281562_, int p_282782_, int p_281638_, int p_283190_, boolean p_283105_, float p_282066_) {
            int $$10 = this.backup.isUploadedVersion() ? -8388737 : 16777215;
            p_281408_.drawString(RealmsBackupScreen.this.font, (Component)Component.translatable("mco.backup.entry", RealmsUtil.convertToAgePresentationFromInstant(this.backup.lastModifiedDate)), p_282463_, p_282495_ + 1, $$10, false);
            p_281408_.drawString(RealmsBackupScreen.this.font, this.getMediumDatePresentation(this.backup.lastModifiedDate), p_282463_, p_282495_ + 12, 5000268, false);
            this.children.forEach((p_280700_) -> {
                p_280700_.setY(p_282495_ + 2);
                p_280700_.render(p_281408_, p_281638_, p_283190_, p_282066_);
            });
        }

        private String getMediumDatePresentation(Date p_88276_) {
            return DateFormat.getDateTimeInstance(3, 3).format(p_88276_);
        }

        public Component getNarration() {
            return Component.translatable("narrator.select", this.backup.lastModifiedDate.toString());
        }
    }
}

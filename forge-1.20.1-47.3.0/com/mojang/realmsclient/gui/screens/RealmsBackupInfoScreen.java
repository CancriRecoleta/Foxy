//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.dto.Backup;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsBackupInfoScreen extends RealmsScreen {
    private static final Component UNKNOWN = Component.translatable("mco.backup.unknown");
    private final Screen lastScreen;
    final Backup backup;
    private BackupInfoList backupInfoList;

    public RealmsBackupInfoScreen(Screen p_88048_, Backup p_88049_) {
        super(Component.translatable("mco.backup.info.title"));
        this.lastScreen = p_88048_;
        this.backup = p_88049_;
    }

    public void tick() {
    }

    public void init() {
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, (p_280689_) -> {
            this.minecraft.setScreen(this.lastScreen);
        }).bounds(this.width / 2 - 100, this.height / 4 + 120 + 24, 200, 20).build());
        this.backupInfoList = new BackupInfoList(this.minecraft);
        this.addWidget(this.backupInfoList);
        this.magicalSpecialHackyFocus(this.backupInfoList);
    }

    public boolean keyPressed(int p_88051_, int p_88052_, int p_88053_) {
        if (p_88051_ == 256) {
            this.minecraft.setScreen(this.lastScreen);
            return true;
        } else {
            return super.keyPressed(p_88051_, p_88052_, p_88053_);
        }
    }

    public void render(GuiGraphics p_282729_, int p_282525_, int p_281883_, float p_281644_) {
        this.renderBackground(p_282729_);
        this.backupInfoList.render(p_282729_, p_282525_, p_281883_, p_281644_);
        p_282729_.drawCenteredString(this.font, (Component)this.title, this.width / 2, 10, 16777215);
        super.render(p_282729_, p_282525_, p_281883_, p_281644_);
    }

    Component checkForSpecificMetadata(String p_88068_, String p_88069_) {
        String $$2 = p_88068_.toLowerCase(Locale.ROOT);
        if ($$2.contains("game") && $$2.contains("mode")) {
            return this.gameModeMetadata(p_88069_);
        } else {
            return (Component)($$2.contains("game") && $$2.contains("difficulty") ? this.gameDifficultyMetadata(p_88069_) : Component.literal(p_88069_));
        }
    }

    private Component gameDifficultyMetadata(String p_88074_) {
        try {
            return ((Difficulty)RealmsSlotOptionsScreen.DIFFICULTIES.get(Integer.parseInt(p_88074_))).getDisplayName();
        } catch (Exception var3) {
            return UNKNOWN;
        }
    }

    private Component gameModeMetadata(String p_88076_) {
        try {
            return ((GameType)RealmsSlotOptionsScreen.GAME_MODES.get(Integer.parseInt(p_88076_))).getShortDisplayName();
        } catch (Exception var3) {
            return UNKNOWN;
        }
    }

    @OnlyIn(Dist.CLIENT)
    private class BackupInfoList extends ObjectSelectionList<BackupInfoListEntry> {
        public BackupInfoList(Minecraft p_88082_) {
            super(p_88082_, RealmsBackupInfoScreen.this.width, RealmsBackupInfoScreen.this.height, 32, RealmsBackupInfoScreen.this.height - 64, 36);
            this.setRenderSelection(false);
            if (RealmsBackupInfoScreen.this.backup.changeList != null) {
                RealmsBackupInfoScreen.this.backup.changeList.forEach((p_88084_, p_88085_) -> {
                    this.addEntry(RealmsBackupInfoScreen.this.new BackupInfoListEntry(p_88084_, p_88085_));
                });
            }

        }
    }

    @OnlyIn(Dist.CLIENT)
    private class BackupInfoListEntry extends ObjectSelectionList.Entry<BackupInfoListEntry> {
        private static final Component TEMPLATE_NAME = Component.translatable("mco.backup.entry.templateName");
        private static final Component GAME_DIFFICULTY = Component.translatable("mco.backup.entry.gameDifficulty");
        private static final Component NAME = Component.translatable("mco.backup.entry.name");
        private static final Component GAME_SERVER_VERSION = Component.translatable("mco.backup.entry.gameServerVersion");
        private static final Component UPLOADED = Component.translatable("mco.backup.entry.uploaded");
        private static final Component ENABLED_PACK = Component.translatable("mco.backup.entry.enabledPack");
        private static final Component DESCRIPTION = Component.translatable("mco.backup.entry.description");
        private static final Component GAME_MODE = Component.translatable("mco.backup.entry.gameMode");
        private static final Component SEED = Component.translatable("mco.backup.entry.seed");
        private static final Component WORLD_TYPE = Component.translatable("mco.backup.entry.worldType");
        private static final Component UNDEFINED = Component.translatable("mco.backup.entry.undefined");
        private final String key;
        private final String value;

        public BackupInfoListEntry(String p_88091_, String p_88092_) {
            this.key = p_88091_;
            this.value = p_88092_;
        }

        public void render(GuiGraphics p_282911_, int p_281482_, int p_283643_, int p_282795_, int p_283291_, int p_282540_, int p_282181_, int p_283535_, boolean p_281916_, float p_282116_) {
            p_282911_.drawString(RealmsBackupInfoScreen.this.font, this.translateKey(this.key), p_282795_, p_283643_, 10526880);
            p_282911_.drawString(RealmsBackupInfoScreen.this.font, RealmsBackupInfoScreen.this.checkForSpecificMetadata(this.key, this.value), p_282795_, p_283643_ + 12, 16777215);
        }

        private Component translateKey(String p_287652_) {
            Component var10000;
            switch (p_287652_) {
                case "template_name" -> var10000 = TEMPLATE_NAME;
                case "game_difficulty" -> var10000 = GAME_DIFFICULTY;
                case "name" -> var10000 = NAME;
                case "game_server_version" -> var10000 = GAME_SERVER_VERSION;
                case "uploaded" -> var10000 = UPLOADED;
                case "enabled_pack" -> var10000 = ENABLED_PACK;
                case "description" -> var10000 = DESCRIPTION;
                case "game_mode" -> var10000 = GAME_MODE;
                case "seed" -> var10000 = SEED;
                case "world_type" -> var10000 = WORLD_TYPE;
                default -> var10000 = UNDEFINED;
            }

            return var10000;
        }

        public Component getNarration() {
            return Component.translatable("narrator.select", this.key + " " + this.value);
        }
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.forge.snapshots;

import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.VersionChecker.Status;

public class ForgeSnapshotsModClient {
    public ForgeSnapshotsModClient() {
    }

    public static void renderMainMenuWarning(VersionChecker.Status status, TitleScreen gui, GuiGraphics graphics, Font font, int width, int height, int alpha) {
        if (status == Status.BETA || status == Status.BETA_OUTDATED) {
            Component line = Component.translatable("forge.update.beta.1", ChatFormatting.RED, ChatFormatting.RESET).withStyle(ChatFormatting.RED);
            int var10003 = width / 2;
            Objects.requireNonNull(font);
            graphics.drawCenteredString(font, (Component)line, var10003, 4 + 0 * (9 + 1), 16777215 | alpha);
            line = Component.translatable("forge.update.beta.2");
            var10003 = width / 2;
            Objects.requireNonNull(font);
            graphics.drawCenteredString(font, (Component)line, var10003, 4 + 1 * (9 + 1), 16777215 | alpha);
        }

    }
}

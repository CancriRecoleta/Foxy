//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.client.gui;

import com.google.common.base.Strings;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.ErrorScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.client.gui.widget.ExtendedButton;
import net.minecraftforge.common.ForgeI18n;
import net.minecraftforge.fml.LoadingFailedException;
import net.minecraftforge.fml.ModLoadingException;
import net.minecraftforge.fml.ModLoadingWarning;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoadingErrorScreen extends ErrorScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Path modsDir;
    private final Path logFile;
    private final List<ModLoadingException> modLoadErrors;
    private final List<ModLoadingWarning> modLoadWarnings;
    private final Path dumpedLocation;
    private LoadingEntryList entryList;
    private Component errorHeader;
    private Component warningHeader;

    public LoadingErrorScreen(LoadingFailedException loadingException, List<ModLoadingWarning> warnings, File dumpedLocation) {
        super(Component.literal("Loading Error"), (Component)null);
        this.modLoadWarnings = warnings;
        this.modLoadErrors = loadingException == null ? Collections.emptyList() : loadingException.getErrors();
        this.modsDir = FMLPaths.MODSDIR.get();
        this.logFile = FMLPaths.GAMEDIR.get().resolve(Paths.get("logs", "latest.log"));
        this.dumpedLocation = dumpedLocation != null ? dumpedLocation.toPath() : null;
    }

    public void init() {
        super.init();
        this.clearWidgets();
        ChatFormatting var10001 = ChatFormatting.RED;
        this.errorHeader = Component.literal("" + var10001 + ForgeI18n.parseMessage("fml.loadingerrorscreen.errorheader", this.modLoadErrors.size()) + ChatFormatting.RESET);
        var10001 = ChatFormatting.YELLOW;
        this.warningHeader = Component.literal("" + var10001 + ForgeI18n.parseMessage("fml.loadingerrorscreen.warningheader", this.modLoadErrors.size()) + ChatFormatting.RESET);
        int yOffset = 46;
        this.addRenderableWidget(new ExtendedButton(50, this.height - yOffset, this.width / 2 - 55, 20, Component.literal(ForgeI18n.parseMessage("fml.button.open.mods.folder")), (b) -> {
            Util.getPlatform().openFile(this.modsDir.toFile());
        }));
        this.addRenderableWidget(new ExtendedButton(this.width / 2 + 5, this.height - yOffset, this.width / 2 - 55, 20, Component.literal(ForgeI18n.parseMessage("fml.button.open.file", this.logFile.getFileName())), (b) -> {
            Util.getPlatform().openFile(this.logFile.toFile());
        }));
        if (this.modLoadErrors.isEmpty()) {
            this.addRenderableWidget(new ExtendedButton(this.width / 4, this.height - 24, this.width / 2, 20, Component.literal(ForgeI18n.parseMessage("fml.button.continue.launch")), (b) -> {
                this.minecraft.setScreen((Screen)null);
            }));
        } else {
            this.addRenderableWidget(new ExtendedButton(this.width / 4, this.height - 24, this.width / 2, 20, Component.literal(ForgeI18n.parseMessage("fml.button.open.file", this.dumpedLocation.getFileName())), (b) -> {
                Util.getPlatform().openFile(this.dumpedLocation.toFile());
            }));
        }

        this.entryList = new LoadingEntryList(this, this.modLoadErrors, this.modLoadWarnings);
        this.addWidget(this.entryList);
        this.setFocused(this.entryList);
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        this.entryList.render(guiGraphics, mouseX, mouseY, partialTick);
        this.drawMultiLineCenteredString(guiGraphics, this.font, this.modLoadErrors.isEmpty() ? this.warningHeader : this.errorHeader, this.width / 2, 10);
        this.renderables.forEach((button) -> {
            button.render(guiGraphics, mouseX, mouseY, partialTick);
        });
    }

    private void drawMultiLineCenteredString(GuiGraphics guiGraphics, Font fr, Component str, int x, int y) {
        for(Iterator var6 = fr.split(str, this.width).iterator(); var6.hasNext(); y += 9) {
            FormattedCharSequence s = (FormattedCharSequence)var6.next();
            guiGraphics.drawString(fr, s, (float)((double)x - (double)fr.width(s) / 2.0), (float)y, 16777215, true);
            Objects.requireNonNull(fr);
        }

    }

    public static class LoadingEntryList extends ObjectSelectionList<LoadingMessageEntry> {
        LoadingEntryList(LoadingErrorScreen parent, List<ModLoadingException> errors, List<ModLoadingWarning> warnings) {
            Minecraft var10001 = (Minecraft)Objects.requireNonNull(parent.minecraft);
            int var10002 = parent.width;
            int var10003 = parent.height;
            int var10005 = parent.height - 50;
            int var10006 = Math.max(errors.stream().mapToInt((error) -> {
                return parent.font.split(Component.literal(error.getMessage() != null ? error.getMessage() : ""), parent.width - 20).size();
            }).max().orElse(0), warnings.stream().mapToInt((warning) -> {
                return parent.font.split(Component.literal(warning.formatToString() != null ? warning.formatToString() : ""), parent.width - 20).size();
            }).max().orElse(0));
            Objects.requireNonNull(parent.minecraft.font);
            super(var10001, var10002, var10003, 35, var10005, var10006 * 9 + 8);
            boolean both = !errors.isEmpty() && !warnings.isEmpty();
            if (both) {
                this.addEntry(new LoadingMessageEntry(parent.errorHeader, true));
            }

            errors.forEach((e) -> {
                this.addEntry(new LoadingMessageEntry(Component.literal(e.formatToString())));
            });
            if (both) {
                int maxChars = (this.width - 10) / parent.minecraft.font.width("-");
                this.addEntry(new LoadingMessageEntry(Component.literal("\n" + Strings.repeat("-", maxChars) + "\n")));
                this.addEntry(new LoadingMessageEntry(parent.warningHeader, true));
            }

            warnings.forEach((w) -> {
                this.addEntry(new LoadingMessageEntry(Component.literal(w.formatToString())));
            });
        }

        protected int getScrollbarPosition() {
            return this.getRight() - 6;
        }

        public int getRowWidth() {
            return this.width;
        }

        public class LoadingMessageEntry extends ObjectSelectionList.Entry<LoadingMessageEntry> {
            private final Component message;
            private final boolean center;

            LoadingMessageEntry(Component message) {
                this(message, false);
            }

            LoadingMessageEntry(Component message, boolean center) {
                this.message = (Component)Objects.requireNonNull(message);
                this.center = center;
            }

            public Component getNarration() {
                return Component.translatable("narrator.select", this.message);
            }

            public void render(GuiGraphics guiGraphics, int entryIdx, int top, int left, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean p_194999_5_, float partialTick) {
                Font font = Minecraft.getInstance().font;
                List<FormattedCharSequence> strings = font.split(this.message, LoadingEntryList.this.width - 20);
                int y = top + 2;

                for(Iterator var14 = strings.iterator(); var14.hasNext(); y += 9) {
                    FormattedCharSequence string = (FormattedCharSequence)var14.next();
                    if (this.center) {
                        guiGraphics.drawString(font, string, (float)left + (float)LoadingEntryList.this.width / 2.0F - (float)font.width(string) / 2.0F, (float)y, 16777215, false);
                    } else {
                        guiGraphics.drawString(font, string, left + 5, y, 16777215, false);
                    }

                    Objects.requireNonNull(font);
                }

            }
        }
    }
}

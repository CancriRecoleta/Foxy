//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.StringUtil;
import net.minecraftforge.client.gui.ModListScreen;
import net.minecraftforge.common.util.MavenVersionStringHelper;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.forgespi.language.IModInfo;

public class ModListWidget extends ObjectSelectionList<ModEntry> {
    private static final ResourceLocation VERSION_CHECK_ICONS = new ResourceLocation("forge", "textures/gui/version_check_icons.png");
    private final int listWidth;
    private ModListScreen parent;

    private static String stripControlCodes(String value) {
        return StringUtil.stripColor(value);
    }

    public ModListWidget(ModListScreen parent, int listWidth, int top, int bottom) {
        Minecraft var10001 = parent.getMinecraftInstance();
        int var10003 = parent.height;
        Objects.requireNonNull(parent.getFontRenderer());
        super(var10001, listWidth, var10003, top, bottom, 9 * 2 + 8);
        this.parent = parent;
        this.listWidth = listWidth;
        this.refreshList();
    }

    protected int getScrollbarPosition() {
        return this.listWidth;
    }

    public int getRowWidth() {
        return this.listWidth;
    }

    public void refreshList() {
        this.clearEntries();
        this.parent.buildModList((x$0) -> {
            this.addEntry(x$0);
        }, (mod) -> {
            return new ModEntry(mod, this.parent);
        });
    }

    protected void renderBackground(GuiGraphics guiGraphics) {
        this.parent.renderBackground(guiGraphics);
    }

    public class ModEntry extends ObjectSelectionList.Entry<ModEntry> {
        private final IModInfo modInfo;
        private final ModListScreen parent;

        ModEntry(IModInfo info, ModListScreen parent) {
            this.modInfo = info;
            this.parent = parent;
        }

        public Component getNarration() {
            return Component.translatable("narrator.select", this.modInfo.getDisplayName());
        }

        public void render(GuiGraphics guiGraphics, int entryIdx, int top, int left, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {
            Component name = Component.literal(ModListWidget.stripControlCodes(this.modInfo.getDisplayName()));
            Component version = Component.literal(ModListWidget.stripControlCodes(MavenVersionStringHelper.artifactVersionToString(this.modInfo.getVersion())));
            VersionChecker.CheckResult vercheck = VersionChecker.getResult(this.modInfo);
            Font font = this.parent.getFontRenderer();
            guiGraphics.drawString(font, Language.getInstance().getVisualOrder(FormattedText.composite(font.substrByWidth(name, ModListWidget.this.listWidth))), left + 3, top + 2, 16777215, false);
            FormattedCharSequence var10002 = Language.getInstance().getVisualOrder(FormattedText.composite(font.substrByWidth(version, ModListWidget.this.listWidth)));
            int var10003 = left + 3;
            int var10004 = top + 2;
            Objects.requireNonNull(font);
            guiGraphics.drawString(font, var10002, var10003, var10004 + 9, 13421772, false);
            if (vercheck.status().shouldDraw()) {
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                guiGraphics.pose().pushPose();
                guiGraphics.blit(ModListWidget.VERSION_CHECK_ICONS, ModListWidget.this.getLeft() + ModListWidget.this.width - 12, top + entryHeight / 4, (float)(vercheck.status().getSheetOffset() * 8), vercheck.status().isAnimated() && (System.currentTimeMillis() / 800L & 1L) == 1L ? 8.0F : 0.0F, 8, 8, 64, 16);
                guiGraphics.pose().popPose();
            }

        }

        public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
            this.parent.setSelected(this);
            ModListWidget.this.setSelected(this);
            return false;
        }

        public IModInfo getInfo() {
            return this.modInfo;
        }
    }
}

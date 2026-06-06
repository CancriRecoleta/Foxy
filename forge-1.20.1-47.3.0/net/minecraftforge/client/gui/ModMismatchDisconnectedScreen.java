//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.client.gui;

import com.mojang.blaze3d.vertex.Tesselator;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarratableEntry.NarrationPriority;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.HoverEvent.Action;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.client.gui.widget.ScrollPanel;
import net.minecraftforge.common.ForgeI18n;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.ConnectionData;
import net.minecraftforge.network.NetworkRegistry;
import org.apache.commons.lang3.tuple.Pair;

public class ModMismatchDisconnectedScreen extends Screen {
    private final Component reason;
    private MultiLineLabel message;
    private final Screen parent;
    private int textHeight;
    private final ConnectionData.ModMismatchData modMismatchData;
    private final Path modsDir;
    private final Path logFile;
    private final int listHeight;
    private final Map<ResourceLocation, Pair<String, String>> presentModData;
    private final List<ResourceLocation> missingModData;
    private final Map<ResourceLocation, String> mismatchedModData;
    private final List<String> allModIds;
    private final Map<String, String> presentModUrls;
    private final boolean mismatchedDataFromServer;

    public ModMismatchDisconnectedScreen(Screen parentScreen, Component title, Component reason, ConnectionData.ModMismatchData modMismatchData) {
        super(title);
        this.message = MultiLineLabel.EMPTY;
        this.parent = parentScreen;
        this.reason = reason;
        this.modMismatchData = modMismatchData;
        this.modsDir = FMLPaths.MODSDIR.get();
        this.logFile = FMLPaths.GAMEDIR.get().resolve(Paths.get("logs", "latest.log"));
        this.listHeight = modMismatchData.containsMismatches() ? 140 : 0;
        this.mismatchedDataFromServer = modMismatchData.mismatchedDataFromServer();
        this.presentModData = modMismatchData.presentModData();
        this.missingModData = (List)modMismatchData.mismatchedModData().entrySet().stream().filter((e) -> {
            return ((String)e.getValue()).equals(NetworkRegistry.ABSENT.version());
        }).map(Map.Entry::getKey).collect(Collectors.toList());
        this.mismatchedModData = (Map)modMismatchData.mismatchedModData().entrySet().stream().filter((e) -> {
            return !((String)e.getValue()).equals(NetworkRegistry.ABSENT.version());
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        this.allModIds = (List)this.presentModData.keySet().stream().map(ResourceLocation::getNamespace).distinct().collect(Collectors.toList());
        this.presentModUrls = (Map)ModList.get().getMods().stream().filter((info) -> {
            return this.allModIds.contains(info.getModId());
        }).map((info) -> {
            return Pair.of(info.getModId(), (String)info.getConfig().getConfigElement(new String[]{"displayURL"}).orElse(""));
        }).collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
    }

    protected void init() {
        this.message = MultiLineLabel.create(this.font, this.reason, this.width - 50);
        this.textHeight = this.message.getLineCount() * 9;
        int listLeft = Math.max(8, this.width / 2 - 220);
        int listWidth = Math.min(440, this.width - 16);
        int upperButtonHeight = Math.min((this.height + this.listHeight + this.textHeight) / 2 + 10, this.height - 50);
        int lowerButtonHeight = Math.min((this.height + this.listHeight + this.textHeight) / 2 + 35, this.height - 25);
        if (this.modMismatchData.containsMismatches()) {
            this.addRenderableWidget(new MismatchInfoPanel(this.minecraft, listWidth, this.listHeight, (this.height - this.listHeight) / 2, listLeft));
        }

        int buttonWidth = Math.min(210, this.width / 2 - 20);
        this.addRenderableWidget(Button.builder(Component.literal(ForgeI18n.parseMessage("fml.button.open.file", this.logFile.getFileName())), (button) -> {
            Util.getPlatform().openFile(this.logFile.toFile());
        }).bounds(Math.max(this.width / 4 - buttonWidth / 2, listLeft), upperButtonHeight, buttonWidth, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal(ForgeI18n.parseMessage("fml.button.open.mods.folder")), (button) -> {
            Util.getPlatform().openFile(this.modsDir.toFile());
        }).bounds(Math.min(this.width * 3 / 4 - buttonWidth / 2, listLeft + listWidth - buttonWidth), upperButtonHeight, buttonWidth, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("gui.toMenu"), (button) -> {
            this.minecraft.setScreen(this.parent);
        }).bounds((this.width - buttonWidth) / 2, lowerButtonHeight, buttonWidth, 20).build());
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        int textYOffset = this.modMismatchData.containsMismatches() ? 18 : 0;
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, (this.height - this.listHeight - this.textHeight) / 2 - textYOffset - 18, 11184810);
        this.message.renderCentered(guiGraphics, this.width / 2, (this.height - this.listHeight - this.textHeight) / 2 - textYOffset);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    class MismatchInfoPanel extends ScrollPanel {
        private final List<Pair<FormattedCharSequence, Pair<FormattedCharSequence, FormattedCharSequence>>> lineTable;
        private final int contentSize;
        private final int nameIndent = 10;
        private final int tableWidth;
        private final int nameWidth;
        private final int versionWidth;

        public MismatchInfoPanel(Minecraft client, int width, int height, int top, int left) {
            super(client, width, height, top, left);
            this.tableWidth = this.width - this.border * 2 - 6 - 10;
            this.nameWidth = this.tableWidth * 3 / 5;
            this.versionWidth = (this.tableWidth - this.nameWidth) / 2;
            List<Pair<MutableComponent, Pair<String, String>>> rawTable = new ArrayList();
            int i;
            Iterator var9;
            if (!ModMismatchDisconnectedScreen.this.missingModData.isEmpty()) {
                rawTable.add(Pair.of(Component.literal(ForgeI18n.parseMessage(ModMismatchDisconnectedScreen.this.mismatchedDataFromServer ? "fml.modmismatchscreen.missingmods.server" : "fml.modmismatchscreen.missingmods.client")).withStyle(ChatFormatting.GRAY), (Object)null));
                rawTable.add(Pair.of(Component.literal(ForgeI18n.parseMessage("fml.modmismatchscreen.table.modname")).withStyle(ChatFormatting.UNDERLINE), Pair.of("", ForgeI18n.parseMessage(ModMismatchDisconnectedScreen.this.mismatchedDataFromServer ? "fml.modmismatchscreen.table.youhave" : "fml.modmismatchscreen.table.youneed"))));
                i = 0;
                var9 = ModMismatchDisconnectedScreen.this.missingModData.iterator();

                while(var9.hasNext()) {
                    ResourceLocation mod = (ResourceLocation)var9.next();
                    rawTable.add(Pair.of(this.toModNameComponent(mod, (String)((Pair)ModMismatchDisconnectedScreen.this.presentModData.get(mod)).getLeft(), i), Pair.of("", (String)((Pair)ModMismatchDisconnectedScreen.this.presentModData.getOrDefault(mod, Pair.of("", ""))).getRight())));
                    ++i;
                    if (i >= 10) {
                        rawTable.add(Pair.of(Component.literal(ForgeI18n.parseMessage("fml.modmismatchscreen.additional", ModMismatchDisconnectedScreen.this.missingModData.size() - i)).withStyle(ChatFormatting.ITALIC), Pair.of("", "")));
                        break;
                    }
                }

                rawTable.add(Pair.of(Component.literal(" "), (Object)null));
            }

            if (!ModMismatchDisconnectedScreen.this.mismatchedModData.isEmpty()) {
                rawTable.add(Pair.of(Component.literal(ForgeI18n.parseMessage("fml.modmismatchscreen.mismatchedmods")).withStyle(ChatFormatting.GRAY), (Object)null));
                rawTable.add(Pair.of(Component.literal(ForgeI18n.parseMessage("fml.modmismatchscreen.table.modname")).withStyle(ChatFormatting.UNDERLINE), Pair.of(ForgeI18n.parseMessage(ModMismatchDisconnectedScreen.this.mismatchedDataFromServer ? "fml.modmismatchscreen.table.youhave" : "fml.modmismatchscreen.table.serverhas"), ForgeI18n.parseMessage(ModMismatchDisconnectedScreen.this.mismatchedDataFromServer ? "fml.modmismatchscreen.table.serverhas" : "fml.modmismatchscreen.table.youhave"))));
                i = 0;
                var9 = ModMismatchDisconnectedScreen.this.mismatchedModData.entrySet().iterator();

                while(var9.hasNext()) {
                    Map.Entry<ResourceLocation, String> modData = (Map.Entry)var9.next();
                    rawTable.add(Pair.of(this.toModNameComponent((ResourceLocation)modData.getKey(), (String)((Pair)ModMismatchDisconnectedScreen.this.presentModData.get(modData.getKey())).getLeft(), i), Pair.of((String)((Pair)ModMismatchDisconnectedScreen.this.presentModData.getOrDefault(modData.getKey(), Pair.of("", ""))).getRight(), (String)modData.getValue())));
                    ++i;
                    if (i >= 10) {
                        rawTable.add(Pair.of(Component.literal(ForgeI18n.parseMessage("fml.modmismatchscreen.additional", ModMismatchDisconnectedScreen.this.mismatchedModData.size() - i)).withStyle(ChatFormatting.ITALIC), Pair.of("", "")));
                        break;
                    }
                }

                rawTable.add(Pair.of(Component.literal(" "), (Object)null));
            }

            this.lineTable = (List)rawTable.stream().flatMap((p) -> {
                return this.splitLineToWidth((MutableComponent)p.getKey(), (Pair)p.getValue()).stream();
            }).collect(Collectors.toList());
            this.contentSize = this.lineTable.size();
        }

        private List<Pair<FormattedCharSequence, Pair<FormattedCharSequence, FormattedCharSequence>>> splitLineToWidth(MutableComponent name, Pair<String, String> versions) {
            Style style = name.getStyle();
            int versionColumns = versions == null ? 0 : (((String)versions.getLeft()).isEmpty() ? (((String)versions.getRight()).isEmpty() ? 0 : 1) : 2);
            int adaptedNameWidth = this.nameWidth + this.versionWidth * (2 - versionColumns) - 4;
            List<FormattedCharSequence> nameLines = ModMismatchDisconnectedScreen.this.font.split(name, adaptedNameWidth);
            List<FormattedCharSequence> clientVersionLines = ModMismatchDisconnectedScreen.this.font.split(Component.literal(versions != null ? (String)versions.getLeft() : "").setStyle(style), this.versionWidth - 4);
            List<FormattedCharSequence> serverVersionLines = ModMismatchDisconnectedScreen.this.font.split(Component.literal(versions != null ? (String)versions.getRight() : "").setStyle(style), this.versionWidth - 4);
            List<Pair<FormattedCharSequence, Pair<FormattedCharSequence, FormattedCharSequence>>> splitLines = new ArrayList();
            int rowsOccupied = Math.max(nameLines.size(), Math.max(clientVersionLines.size(), serverVersionLines.size()));

            for(int i = 0; i < rowsOccupied; ++i) {
                splitLines.add(Pair.of(i < nameLines.size() ? (FormattedCharSequence)nameLines.get(i) : FormattedCharSequence.EMPTY, versions == null ? null : Pair.of(i < clientVersionLines.size() ? (FormattedCharSequence)clientVersionLines.get(i) : FormattedCharSequence.EMPTY, i < serverVersionLines.size() ? (FormattedCharSequence)serverVersionLines.get(i) : FormattedCharSequence.EMPTY)));
            }

            return splitLines;
        }

        private MutableComponent toModNameComponent(ResourceLocation id, String modName, int color) {
            String modId = id.getNamespace();
            String tooltipId = id.getPath().isEmpty() ? id.getNamespace() : id.toString();
            return Component.literal(modName).withStyle(color % 2 == 0 ? ChatFormatting.GOLD : ChatFormatting.YELLOW).withStyle((s) -> {
                return s.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, Component.literal(tooltipId + (!((String)ModMismatchDisconnectedScreen.this.presentModUrls.getOrDefault(modId, "")).isEmpty() ? "\n" + ForgeI18n.parseMessage("fml.modmismatchscreen.homepage") : ""))));
            }).withStyle((s) -> {
                return s.withClickEvent(!((String)ModMismatchDisconnectedScreen.this.presentModUrls.getOrDefault(modId, "")).isEmpty() ? new ClickEvent(net.minecraft.network.chat.ClickEvent.Action.OPEN_URL, (String)ModMismatchDisconnectedScreen.this.presentModUrls.get(modId)) : null);
            });
        }

        protected int getContentHeight() {
            int var10000 = this.contentSize;
            Objects.requireNonNull(ModMismatchDisconnectedScreen.this.font);
            int height = var10000 * (9 + 3);
            if (height < this.bottom - this.top - 4) {
                height = this.bottom - this.top - 4;
            }

            return height;
        }

        protected void drawPanel(GuiGraphics guiGraphics, int entryRight, int relativeY, Tesselator tess, int mouseX, int mouseY) {
            int i = 0;

            for(Iterator var8 = this.lineTable.iterator(); var8.hasNext(); ++i) {
                Pair<FormattedCharSequence, Pair<FormattedCharSequence, FormattedCharSequence>> line = (Pair)var8.next();
                FormattedCharSequence name = (FormattedCharSequence)line.getLeft();
                Pair<FormattedCharSequence, FormattedCharSequence> versions = (Pair)line.getRight();
                int color = (Integer)Optional.ofNullable(ModMismatchDisconnectedScreen.this.font.getSplitter().componentStyleAtWidth((FormattedCharSequence)name, 0)).map(Style::getColor).map(TextColor::getValue).orElse(16777215);
                int nameLeft = this.left + this.border + (versions == null ? 0 : 10);
                guiGraphics.drawString(ModMismatchDisconnectedScreen.this.font, name, nameLeft, relativeY + i * 12, color, false);
                if (versions != null) {
                    guiGraphics.drawString(ModMismatchDisconnectedScreen.this.font, (FormattedCharSequence)versions.getLeft(), this.left + this.border + 10 + this.nameWidth, relativeY + i * 12, color, false);
                    guiGraphics.drawString(ModMismatchDisconnectedScreen.this.font, (FormattedCharSequence)versions.getRight(), this.left + this.border + 10 + this.nameWidth + this.versionWidth, relativeY + i * 12, color, false);
                }
            }

        }

        public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
            super.render(guiGraphics, mouseX, mouseY, partialTicks);
            Style style = this.getComponentStyleAt((double)mouseX, (double)mouseY);
            if (style != null && style.getHoverEvent() != null) {
                guiGraphics.renderComponentHoverEffect(ModMismatchDisconnectedScreen.this.font, style, mouseX, mouseY);
            }

        }

        public Style getComponentStyleAt(double x, double y) {
            if (this.isMouseOver(x, y)) {
                double relativeY = y - (double)this.top + (double)this.scrollDistance - (double)this.border;
                int slotIndex = (int)(relativeY + (double)(this.border / 2)) / 12;
                if (slotIndex < this.contentSize) {
                    double relativeX = x - (double)this.left - (double)this.border - (double)(((Pair)this.lineTable.get(slotIndex)).getRight() == null ? 0 : 10);
                    if (relativeX >= 0.0) {
                        return ModMismatchDisconnectedScreen.this.font.getSplitter().componentStyleAtWidth((FormattedCharSequence)((Pair)this.lineTable.get(slotIndex)).getLeft(), (int)relativeX);
                    }
                }
            }

            return null;
        }

        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            Style style = this.getComponentStyleAt(mouseX, mouseY);
            if (style != null) {
                ModMismatchDisconnectedScreen.this.handleComponentClicked(style);
                return true;
            } else {
                return super.mouseClicked(mouseX, mouseY, button);
            }
        }

        public NarratableEntry.NarrationPriority narrationPriority() {
            return NarrationPriority.NONE;
        }

        public void updateNarration(NarrationElementOutput output) {
        }
    }
}

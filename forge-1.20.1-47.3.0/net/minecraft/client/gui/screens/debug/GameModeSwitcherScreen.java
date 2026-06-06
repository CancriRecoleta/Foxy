//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.screens.debug;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Iterator;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GameModeSwitcherScreen extends Screen {
    static final ResourceLocation GAMEMODE_SWITCHER_LOCATION = new ResourceLocation("textures/gui/container/gamemode_switcher.png");
    private static final int SPRITE_SHEET_WIDTH = 128;
    private static final int SPRITE_SHEET_HEIGHT = 128;
    private static final int SLOT_AREA = 26;
    private static final int SLOT_PADDING = 5;
    private static final int SLOT_AREA_PADDED = 31;
    private static final int HELP_TIPS_OFFSET_Y = 5;
    private static final int ALL_SLOTS_WIDTH = net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen.GameModeIcon.values().length * 31 - 5;
    private static final Component SELECT_KEY;
    private final GameModeIcon previousHovered = net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen.GameModeIcon.getFromGameType(this.getDefaultSelected());
    private GameModeIcon currentlyHovered;
    private int firstMouseX;
    private int firstMouseY;
    private boolean setFirstMousePos;
    private final List<GameModeSlot> slots = Lists.newArrayList();

    public GameModeSwitcherScreen() {
        super(GameNarrator.NO_TITLE);
        this.currentlyHovered = this.previousHovered;
    }

    private GameType getDefaultSelected() {
        MultiPlayerGameMode $$0 = Minecraft.getInstance().gameMode;
        GameType $$1 = $$0.getPreviousPlayerMode();
        if ($$1 != null) {
            return $$1;
        } else {
            return $$0.getPlayerMode() == GameType.CREATIVE ? GameType.SURVIVAL : GameType.CREATIVE;
        }
    }

    protected void init() {
        super.init();
        this.currentlyHovered = this.previousHovered;

        for(int $$0 = 0; $$0 < net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen.GameModeIcon.VALUES.length; ++$$0) {
            GameModeIcon $$1 = net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen.GameModeIcon.VALUES[$$0];
            this.slots.add(new GameModeSlot($$1, this.width / 2 - ALL_SLOTS_WIDTH / 2 + $$0 * 31, this.height / 2 - 31));
        }

    }

    public void render(GuiGraphics p_281834_, int p_283223_, int p_282178_, float p_281339_) {
        if (!this.checkToClose()) {
            p_281834_.pose().pushPose();
            RenderSystem.enableBlend();
            int $$4 = this.width / 2 - 62;
            int $$5 = this.height / 2 - 31 - 27;
            p_281834_.blit(GAMEMODE_SWITCHER_LOCATION, $$4, $$5, 0.0F, 0.0F, 125, 75, 128, 128);
            p_281834_.pose().popPose();
            super.render(p_281834_, p_283223_, p_282178_, p_281339_);
            p_281834_.drawCenteredString(this.font, (Component)this.currentlyHovered.getName(), this.width / 2, this.height / 2 - 31 - 20, -1);
            p_281834_.drawCenteredString(this.font, SELECT_KEY, this.width / 2, this.height / 2 + 5, 16777215);
            if (!this.setFirstMousePos) {
                this.firstMouseX = p_283223_;
                this.firstMouseY = p_282178_;
                this.setFirstMousePos = true;
            }

            boolean $$6 = this.firstMouseX == p_283223_ && this.firstMouseY == p_282178_;
            Iterator var8 = this.slots.iterator();

            while(var8.hasNext()) {
                GameModeSlot $$7 = (GameModeSlot)var8.next();
                $$7.render(p_281834_, p_283223_, p_282178_, p_281339_);
                $$7.setSelected(this.currentlyHovered == $$7.icon);
                if (!$$6 && $$7.isHoveredOrFocused()) {
                    this.currentlyHovered = $$7.icon;
                }
            }

        }
    }

    private void switchToHoveredGameMode() {
        switchToHoveredGameMode(this.minecraft, this.currentlyHovered);
    }

    private static void switchToHoveredGameMode(Minecraft p_281340_, GameModeIcon p_281358_) {
        if (p_281340_.gameMode != null && p_281340_.player != null) {
            GameModeIcon $$2 = net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen.GameModeIcon.getFromGameType(p_281340_.gameMode.getPlayerMode());
            GameModeIcon $$3 = p_281358_;
            if (p_281340_.player.hasPermissions(2) && $$3 != $$2) {
                p_281340_.player.connection.sendUnsignedCommand($$3.getCommand());
            }

        }
    }

    private boolean checkToClose() {
        if (!InputConstants.isKeyDown(this.minecraft.getWindow().getWindow(), 292)) {
            this.switchToHoveredGameMode();
            this.minecraft.setScreen((Screen)null);
            return true;
        } else {
            return false;
        }
    }

    public boolean keyPressed(int p_97553_, int p_97554_, int p_97555_) {
        if (p_97553_ == 293) {
            this.setFirstMousePos = false;
            this.currentlyHovered = this.currentlyHovered.getNext();
            return true;
        } else {
            return super.keyPressed(p_97553_, p_97554_, p_97555_);
        }
    }

    public boolean isPauseScreen() {
        return false;
    }

    static {
        SELECT_KEY = Component.translatable("debug.gamemodes.select_next", Component.translatable("debug.gamemodes.press_f4").withStyle(ChatFormatting.AQUA));
    }

    @OnlyIn(Dist.CLIENT)
    static enum GameModeIcon {
        CREATIVE(Component.translatable("gameMode.creative"), "gamemode creative", new ItemStack(Blocks.GRASS_BLOCK)),
        SURVIVAL(Component.translatable("gameMode.survival"), "gamemode survival", new ItemStack(Items.IRON_SWORD)),
        ADVENTURE(Component.translatable("gameMode.adventure"), "gamemode adventure", new ItemStack(Items.MAP)),
        SPECTATOR(Component.translatable("gameMode.spectator"), "gamemode spectator", new ItemStack(Items.ENDER_EYE));

        protected static final GameModeIcon[] VALUES = values();
        private static final int ICON_AREA = 16;
        protected static final int ICON_TOP_LEFT = 5;
        final Component name;
        final String command;
        final ItemStack renderStack;

        private GameModeIcon(Component p_97594_, String p_97595_, ItemStack p_97596_) {
            this.name = p_97594_;
            this.command = p_97595_;
            this.renderStack = p_97596_;
        }

        void drawIcon(GuiGraphics p_282609_, int p_283301_, int p_281692_) {
            p_282609_.renderItem(this.renderStack, p_283301_, p_281692_);
        }

        Component getName() {
            return this.name;
        }

        String getCommand() {
            return this.command;
        }

        GameModeIcon getNext() {
            GameModeIcon var10000;
            switch (this) {
                case CREATIVE -> var10000 = SURVIVAL;
                case SURVIVAL -> var10000 = ADVENTURE;
                case ADVENTURE -> var10000 = SPECTATOR;
                case SPECTATOR -> var10000 = CREATIVE;
                default -> throw new IncompatibleClassChangeError();
            }

            return var10000;
        }

        static GameModeIcon getFromGameType(GameType p_283307_) {
            GameModeIcon var10000;
            switch (p_283307_) {
                case SPECTATOR -> var10000 = SPECTATOR;
                case SURVIVAL -> var10000 = SURVIVAL;
                case CREATIVE -> var10000 = CREATIVE;
                case ADVENTURE -> var10000 = ADVENTURE;
                default -> throw new IncompatibleClassChangeError();
            }

            return var10000;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public class GameModeSlot extends AbstractWidget {
        final GameModeIcon icon;
        private boolean isSelected;

        public GameModeSlot(GameModeIcon p_97627_, int p_97628_, int p_97629_) {
            super(p_97628_, p_97629_, 26, 26, p_97627_.getName());
            this.icon = p_97627_;
        }

        public void renderWidget(GuiGraphics p_281380_, int p_283094_, int p_283558_, float p_282631_) {
            this.drawSlot(p_281380_);
            this.icon.drawIcon(p_281380_, this.getX() + 5, this.getY() + 5);
            if (this.isSelected) {
                this.drawSelection(p_281380_);
            }

        }

        public void updateWidgetNarration(NarrationElementOutput p_259120_) {
            this.defaultButtonNarrationText(p_259120_);
        }

        public boolean isHoveredOrFocused() {
            return super.isHoveredOrFocused() || this.isSelected;
        }

        public void setSelected(boolean p_97644_) {
            this.isSelected = p_97644_;
        }

        private void drawSlot(GuiGraphics p_281786_) {
            p_281786_.blit(GameModeSwitcherScreen.GAMEMODE_SWITCHER_LOCATION, this.getX(), this.getY(), 0.0F, 75.0F, 26, 26, 128, 128);
        }

        private void drawSelection(GuiGraphics p_281820_) {
            p_281820_.blit(GameModeSwitcherScreen.GAMEMODE_SWITCHER_LOCATION, this.getX(), this.getY(), 26.0F, 75.0F, 26, 26, 128, 128);
        }
    }
}

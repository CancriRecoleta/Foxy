//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.screens;

import java.util.Objects;
import net.minecraft.Util;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DemoIntroScreen extends Screen {
    private static final ResourceLocation DEMO_BACKGROUND_LOCATION = new ResourceLocation("textures/gui/demo_background.png");
    private MultiLineLabel movementMessage;
    private MultiLineLabel durationMessage;

    public DemoIntroScreen() {
        super(Component.translatable("demo.help.title"));
        this.movementMessage = MultiLineLabel.EMPTY;
        this.durationMessage = MultiLineLabel.EMPTY;
    }

    protected void init() {
        int $$0 = true;
        this.addRenderableWidget(Button.builder(Component.translatable("demo.help.buy"), (p_280797_) -> {
            p_280797_.active = false;
            Util.getPlatform().openUri("https://aka.ms/BuyMinecraftJava");
        }).bounds(this.width / 2 - 116, this.height / 2 + 62 + -16, 114, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("demo.help.later"), (p_280798_) -> {
            this.minecraft.setScreen((Screen)null);
            this.minecraft.mouseHandler.grabMouse();
        }).bounds(this.width / 2 + 2, this.height / 2 + 62 + -16, 114, 20).build());
        Options $$1 = this.minecraft.options;
        this.movementMessage = MultiLineLabel.create(this.font, Component.translatable("demo.help.movementShort", $$1.keyUp.getTranslatedKeyMessage(), $$1.keyLeft.getTranslatedKeyMessage(), $$1.keyDown.getTranslatedKeyMessage(), $$1.keyRight.getTranslatedKeyMessage()), Component.translatable("demo.help.movementMouse"), Component.translatable("demo.help.jump", $$1.keyJump.getTranslatedKeyMessage()), Component.translatable("demo.help.inventory", $$1.keyInventory.getTranslatedKeyMessage()));
        this.durationMessage = MultiLineLabel.create(this.font, Component.translatable("demo.help.fullWrapped"), 218);
    }

    public void renderBackground(GuiGraphics p_283391_) {
        super.renderBackground(p_283391_);
        int $$1 = (this.width - 248) / 2;
        int $$2 = (this.height - 166) / 2;
        p_283391_.blit(DEMO_BACKGROUND_LOCATION, $$1, $$2, 0, 0, 248, 166);
    }

    public void render(GuiGraphics p_281247_, int p_281844_, int p_283693_, float p_281842_) {
        this.renderBackground(p_281247_);
        int $$4 = (this.width - 248) / 2 + 10;
        int $$5 = (this.height - 166) / 2 + 8;
        p_281247_.drawString(this.font, this.title, $$4, $$5, 2039583, false);
        $$5 = this.movementMessage.renderLeftAlignedNoShadow(p_281247_, $$4, $$5 + 12, 12, 5197647);
        MultiLineLabel var10000 = this.durationMessage;
        int var10003 = $$5 + 20;
        Objects.requireNonNull(this.font);
        var10000.renderLeftAlignedNoShadow(p_281247_, $$4, var10003, 9, 2039583);
        super.render(p_281247_, p_281844_, p_283693_, p_281842_);
    }
}

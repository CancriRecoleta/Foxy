//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.mojang.realmsclient.gui.screens;

import net.minecraft.Util;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsParentalConsentScreen extends RealmsScreen {
    private static final Component MESSAGE = Component.translatable("mco.account.privacyinfo");
    private final Screen nextScreen;
    private MultiLineLabel messageLines;

    public RealmsParentalConsentScreen(Screen p_88861_) {
        super(GameNarrator.NO_TITLE);
        this.messageLines = MultiLineLabel.EMPTY;
        this.nextScreen = p_88861_;
    }

    public void init() {
        Component $$0 = Component.translatable("mco.account.update");
        Component $$1 = CommonComponents.GUI_BACK;
        int $$2 = Math.max(this.font.width((FormattedText)$$0), this.font.width((FormattedText)$$1)) + 30;
        Component $$3 = Component.translatable("mco.account.privacy.info");
        int $$4 = (int)((double)this.font.width((FormattedText)$$3) * 1.2);
        this.addRenderableWidget(Button.builder($$3, (p_88873_) -> {
            Util.getPlatform().openUri("https://aka.ms/MinecraftGDPR");
        }).bounds(this.width / 2 - $$4 / 2, row(11), $$4, 20).build());
        this.addRenderableWidget(Button.builder($$0, (p_88871_) -> {
            Util.getPlatform().openUri("https://aka.ms/UpdateMojangAccount");
        }).bounds(this.width / 2 - ($$2 + 5), row(13), $$2, 20).build());
        this.addRenderableWidget(Button.builder($$1, (p_280730_) -> {
            this.minecraft.setScreen(this.nextScreen);
        }).bounds(this.width / 2 + 5, row(13), $$2, 20).build());
        this.messageLines = MultiLineLabel.create(this.font, MESSAGE, (int)Math.round((double)this.width * 0.9));
    }

    public Component getNarrationMessage() {
        return MESSAGE;
    }

    public void render(GuiGraphics p_282593_, int p_282889_, int p_283522_, float p_281349_) {
        this.renderBackground(p_282593_);
        this.messageLines.renderCentered(p_282593_, this.width / 2, 15, 15, 16777215);
        super.render(p_282593_, p_282889_, p_283522_, p_281349_);
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.screens;

import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SkinCustomizationScreen extends OptionsSubScreen {
    public SkinCustomizationScreen(Screen p_96684_, Options p_96685_) {
        super(p_96684_, p_96685_, Component.translatable("options.skinCustomisation.title"));
    }

    protected void init() {
        int $$0 = 0;
        PlayerModelPart[] var2 = PlayerModelPart.values();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            PlayerModelPart $$1 = var2[var4];
            this.addRenderableWidget(CycleButton.onOffBuilder(this.options.isModelPartEnabled($$1)).create(this.width / 2 - 155 + $$0 % 2 * 160, this.height / 6 + 24 * ($$0 >> 1), 150, 20, $$1.getName(), (p_169436_, p_169437_) -> {
                this.options.toggleModelPart($$1, p_169437_);
            }));
            ++$$0;
        }

        this.addRenderableWidget(this.options.mainHand().createButton(this.options, this.width / 2 - 155 + $$0 % 2 * 160, this.height / 6 + 24 * ($$0 >> 1), 150));
        ++$$0;
        if ($$0 % 2 == 1) {
            ++$$0;
        }

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (p_280828_) -> {
            this.minecraft.setScreen(this.lastScreen);
        }).bounds(this.width / 2 - 100, this.height / 6 + 24 * ($$0 >> 1), 200, 20).build());
    }

    public void render(GuiGraphics p_282063_, int p_283510_, int p_283109_, float p_283448_) {
        this.renderBackground(p_282063_);
        p_282063_.drawCenteredString(this.font, (Component)this.title, this.width / 2, 20, 16777215);
        super.render(p_282063_, p_283510_, p_283109_, p_283448_);
    }
}

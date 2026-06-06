//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.realms;

import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DisconnectedRealmsScreen extends RealmsScreen {
    private final Component reason;
    private MultiLineLabel message;
    private final Screen parent;
    private int textHeight;

    public DisconnectedRealmsScreen(Screen p_120653_, Component p_120654_, Component p_120655_) {
        super(p_120654_);
        this.message = MultiLineLabel.EMPTY;
        this.parent = p_120653_;
        this.reason = p_120655_;
    }

    public void init() {
        Minecraft $$0 = Minecraft.getInstance();
        $$0.setConnectedToRealms(false);
        $$0.getDownloadedPackSource().clearServerPack();
        this.message = MultiLineLabel.create(this.font, this.reason, this.width - 50);
        int var10001 = this.message.getLineCount();
        Objects.requireNonNull(this.font);
        this.textHeight = var10001 * 9;
        Button.Builder var2 = Button.builder(CommonComponents.GUI_BACK, (p_120663_) -> {
            $$0.setScreen(this.parent);
        });
        int var10002 = this.width / 2 - 100;
        int var10003 = this.height / 2 + this.textHeight / 2;
        Objects.requireNonNull(this.font);
        this.addRenderableWidget(var2.bounds(var10002, var10003 + 9, 200, 20).build());
    }

    public Component getNarrationMessage() {
        return Component.empty().append(this.title).append(": ").append(this.reason);
    }

    public void onClose() {
        Minecraft.getInstance().setScreen(this.parent);
    }

    public void render(GuiGraphics p_282959_, int p_120658_, int p_120659_, float p_120660_) {
        this.renderBackground(p_282959_);
        Font var10001 = this.font;
        Component var10002 = this.title;
        int var10003 = this.width / 2;
        int var10004 = this.height / 2 - this.textHeight / 2;
        Objects.requireNonNull(this.font);
        p_282959_.drawCenteredString(var10001, var10002, var10003, var10004 - 9 * 2, 11184810);
        this.message.renderCentered(p_282959_, this.width / 2, this.height / 2 - this.textHeight / 2);
        super.render(p_282959_, p_120658_, p_120659_, p_120660_);
    }
}

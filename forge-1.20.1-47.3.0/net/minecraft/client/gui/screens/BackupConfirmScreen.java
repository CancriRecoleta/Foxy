//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.screens;

import java.util.Objects;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BackupConfirmScreen extends Screen {
    private final Screen lastScreen;
    protected final Listener listener;
    private final Component description;
    private final boolean promptForCacheErase;
    private MultiLineLabel message;
    protected int id;
    private Checkbox eraseCache;

    public BackupConfirmScreen(Screen p_95543_, Listener p_95544_, Component p_95545_, Component p_95546_, boolean p_95547_) {
        super(p_95545_);
        this.message = MultiLineLabel.EMPTY;
        this.lastScreen = p_95543_;
        this.listener = p_95544_;
        this.description = p_95546_;
        this.promptForCacheErase = p_95547_;
    }

    protected void init() {
        super.init();
        this.message = MultiLineLabel.create(this.font, this.description, this.width - 50);
        int var10000 = this.message.getLineCount() + 1;
        Objects.requireNonNull(this.font);
        int $$0 = var10000 * 9;
        this.addRenderableWidget(Button.builder(Component.translatable("selectWorld.backupJoinConfirmButton"), (p_95564_) -> {
            this.listener.proceed(true, this.eraseCache.selected());
        }).bounds(this.width / 2 - 155, 100 + $$0, 150, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("selectWorld.backupJoinSkipButton"), (p_95562_) -> {
            this.listener.proceed(false, this.eraseCache.selected());
        }).bounds(this.width / 2 - 155 + 160, 100 + $$0, 150, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, (p_280786_) -> {
            this.minecraft.setScreen(this.lastScreen);
        }).bounds(this.width / 2 - 155 + 80, 124 + $$0, 150, 20).build());
        this.eraseCache = new Checkbox(this.width / 2 - 155 + 80, 76 + $$0, 150, 20, Component.translatable("selectWorld.backupEraseCache"), false);
        if (this.promptForCacheErase) {
            this.addRenderableWidget(this.eraseCache);
        }

    }

    public void render(GuiGraphics p_282759_, int p_282356_, int p_282725_, float p_281518_) {
        this.renderBackground(p_282759_);
        p_282759_.drawCenteredString(this.font, (Component)this.title, this.width / 2, 50, 16777215);
        this.message.renderCentered(p_282759_, this.width / 2, 70);
        super.render(p_282759_, p_282356_, p_282725_, p_281518_);
    }

    public boolean shouldCloseOnEsc() {
        return false;
    }

    public boolean keyPressed(int p_95549_, int p_95550_, int p_95551_) {
        if (p_95549_ == 256) {
            this.minecraft.setScreen(this.lastScreen);
            return true;
        } else {
            return super.keyPressed(p_95549_, p_95550_, p_95551_);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public interface Listener {
        void proceed(boolean var1, boolean var2);
    }
}

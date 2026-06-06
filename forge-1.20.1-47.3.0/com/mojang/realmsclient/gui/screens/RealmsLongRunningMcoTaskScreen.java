//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.mojang.realmsclient.gui.screens;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.exception.RealmsDefaultUncaughtExceptionHandler;
import com.mojang.realmsclient.gui.ErrorCallback;
import com.mojang.realmsclient.util.task.LongRunningTask;
import java.time.Duration;
import javax.annotation.Nullable;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.RepeatedNarrator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsLongRunningMcoTaskScreen extends RealmsScreen implements ErrorCallback {
    private static final RepeatedNarrator REPEATED_NARRATOR = new RepeatedNarrator(Duration.ofSeconds(5L));
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Screen lastScreen;
    private volatile Component title;
    @Nullable
    private volatile Component errorMessage;
    private volatile boolean aborted;
    private int animTicks;
    private final LongRunningTask task;
    private final int buttonLength;
    private Button cancelOrBackButton;
    public static final String[] SYMBOLS = new String[]{"▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃", "_ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄", "_ _ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅", "_ _ _ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆", "_ _ _ _ ▃ ▄ ▅ ▆ ▇ █ ▇", "_ _ _ _ _ ▃ ▄ ▅ ▆ ▇ █", "_ _ _ _ ▃ ▄ ▅ ▆ ▇ █ ▇", "_ _ _ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆", "_ _ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅", "_ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄", "▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃", "▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _", "▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _ _", "▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _ _ _", "▇ █ ▇ ▆ ▅ ▄ ▃ _ _ _ _", "█ ▇ ▆ ▅ ▄ ▃ _ _ _ _ _", "▇ █ ▇ ▆ ▅ ▄ ▃ _ _ _ _", "▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _ _ _", "▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _ _", "▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _"};

    public RealmsLongRunningMcoTaskScreen(Screen p_88777_, LongRunningTask p_88778_) {
        super(GameNarrator.NO_TITLE);
        this.title = CommonComponents.EMPTY;
        this.buttonLength = 212;
        this.lastScreen = p_88777_;
        this.task = p_88778_;
        p_88778_.setScreen(this);
        Thread $$2 = new Thread(p_88778_, "Realms-long-running-task");
        $$2.setUncaughtExceptionHandler(new RealmsDefaultUncaughtExceptionHandler(LOGGER));
        $$2.start();
    }

    public void tick() {
        super.tick();
        REPEATED_NARRATOR.narrate(this.minecraft.getNarrator(), this.title);
        ++this.animTicks;
        this.task.tick();
    }

    public boolean keyPressed(int p_88781_, int p_88782_, int p_88783_) {
        if (p_88781_ == 256) {
            this.cancelOrBackButtonClicked();
            return true;
        } else {
            return super.keyPressed(p_88781_, p_88782_, p_88783_);
        }
    }

    public void init() {
        this.task.init();
        this.cancelOrBackButton = (Button)this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, (p_88795_) -> {
            this.cancelOrBackButtonClicked();
        }).bounds(this.width / 2 - 106, row(12), 212, 20).build());
    }

    private void cancelOrBackButtonClicked() {
        this.aborted = true;
        this.task.abortTask();
        this.minecraft.setScreen(this.lastScreen);
    }

    public void render(GuiGraphics p_282789_, int p_88786_, int p_88787_, float p_88788_) {
        this.renderBackground(p_282789_);
        p_282789_.drawCenteredString(this.font, this.title, this.width / 2, row(3), 16777215);
        Component $$4 = this.errorMessage;
        if ($$4 == null) {
            p_282789_.drawCenteredString(this.font, SYMBOLS[this.animTicks % SYMBOLS.length], this.width / 2, row(8), 8421504);
        } else {
            p_282789_.drawCenteredString(this.font, $$4, this.width / 2, row(8), 16711680);
        }

        super.render(p_282789_, p_88786_, p_88787_, p_88788_);
    }

    public void error(Component p_88792_) {
        this.errorMessage = p_88792_;
        this.minecraft.getNarrator().sayNow(p_88792_);
        this.minecraft.execute(() -> {
            this.removeWidget(this.cancelOrBackButton);
            this.cancelOrBackButton = (Button)this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, (p_88790_) -> {
                this.cancelOrBackButtonClicked();
            }).bounds(this.width / 2 - 106, this.height / 4 + 120 + 12, 200, 20).build());
        });
    }

    public void setTitle(Component p_88797_) {
        this.title = p_88797_;
    }

    public boolean aborted() {
        return this.aborted;
    }
}

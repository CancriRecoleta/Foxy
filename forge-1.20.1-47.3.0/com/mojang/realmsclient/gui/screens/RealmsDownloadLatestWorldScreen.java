//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.Unit;
import com.mojang.realmsclient.client.FileDownload;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen.Type;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsDownloadLatestWorldScreen extends RealmsScreen {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ReentrantLock DOWNLOAD_LOCK = new ReentrantLock();
    private static final int BAR_WIDTH = 200;
    private static final int BAR_TOP = 80;
    private static final int BAR_BOTTOM = 95;
    private static final int BAR_BORDER = 1;
    private final Screen lastScreen;
    private final WorldDownload worldDownload;
    private final Component downloadTitle;
    private final RateLimiter narrationRateLimiter;
    private Button cancelButton;
    private final String worldName;
    private final DownloadStatus downloadStatus;
    @Nullable
    private volatile Component errorMessage;
    private volatile Component status = Component.translatable("mco.download.preparing");
    @Nullable
    private volatile String progress;
    private volatile boolean cancelled;
    private volatile boolean showDots = true;
    private volatile boolean finished;
    private volatile boolean extracting;
    @Nullable
    private Long previousWrittenBytes;
    @Nullable
    private Long previousTimeSnapshot;
    private long bytesPersSecond;
    private int animTick;
    private static final String[] DOTS = new String[]{"", ".", ". .", ". . ."};
    private int dotIndex;
    private boolean checked;
    private final BooleanConsumer callback;

    public RealmsDownloadLatestWorldScreen(Screen p_88625_, WorldDownload p_88626_, String p_88627_, BooleanConsumer p_88628_) {
        super(GameNarrator.NO_TITLE);
        this.callback = p_88628_;
        this.lastScreen = p_88625_;
        this.worldName = p_88627_;
        this.worldDownload = p_88626_;
        this.downloadStatus = new DownloadStatus();
        this.downloadTitle = Component.translatable("mco.download.title");
        this.narrationRateLimiter = RateLimiter.create(0.10000000149011612);
    }

    public void init() {
        this.cancelButton = (Button)this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, (p_88642_) -> {
            this.cancelled = true;
            this.backButtonClicked();
        }).bounds((this.width - 200) / 2, this.height - 42, 200, 20).build());
        this.checkDownloadSize();
    }

    private void checkDownloadSize() {
        if (!this.finished) {
            if (!this.checked && this.getContentLength(this.worldDownload.downloadLink) >= 5368709120L) {
                Component $$0 = Component.translatable("mco.download.confirmation.line1", Unit.humanReadable(5368709120L));
                Component $$1 = Component.translatable("mco.download.confirmation.line2");
                this.minecraft.setScreen(new RealmsLongConfirmationScreen((p_280727_) -> {
                    this.checked = true;
                    this.minecraft.setScreen(this);
                    this.downloadSave();
                }, Type.WARNING, $$0, $$1, false));
            } else {
                this.downloadSave();
            }

        }
    }

    private long getContentLength(String p_88647_) {
        FileDownload $$1 = new FileDownload();
        return $$1.contentLength(p_88647_);
    }

    public void tick() {
        super.tick();
        ++this.animTick;
        if (this.status != null && this.narrationRateLimiter.tryAcquire(1)) {
            Component $$0 = this.createProgressNarrationMessage();
            this.minecraft.getNarrator().sayNow($$0);
        }

    }

    private Component createProgressNarrationMessage() {
        List<Component> $$0 = Lists.newArrayList();
        $$0.add(this.downloadTitle);
        $$0.add(this.status);
        if (this.progress != null) {
            $$0.add(Component.literal(this.progress + "%"));
            $$0.add(Component.literal(Unit.humanReadable(this.bytesPersSecond) + "/s"));
        }

        if (this.errorMessage != null) {
            $$0.add(this.errorMessage);
        }

        return CommonComponents.joinLines((Collection)$$0);
    }

    public boolean keyPressed(int p_88630_, int p_88631_, int p_88632_) {
        if (p_88630_ == 256) {
            this.cancelled = true;
            this.backButtonClicked();
            return true;
        } else {
            return super.keyPressed(p_88630_, p_88631_, p_88632_);
        }
    }

    private void backButtonClicked() {
        if (this.finished && this.callback != null && this.errorMessage == null) {
            this.callback.accept(true);
        }

        this.minecraft.setScreen(this.lastScreen);
    }

    public void render(GuiGraphics p_282124_, int p_88635_, int p_88636_, float p_88637_) {
        this.renderBackground(p_282124_);
        p_282124_.drawCenteredString(this.font, (Component)this.downloadTitle, this.width / 2, 20, 16777215);
        p_282124_.drawCenteredString(this.font, (Component)this.status, this.width / 2, 50, 16777215);
        if (this.showDots) {
            this.drawDots(p_282124_);
        }

        if (this.downloadStatus.bytesWritten != 0L && !this.cancelled) {
            this.drawProgressBar(p_282124_);
            this.drawDownloadSpeed(p_282124_);
        }

        if (this.errorMessage != null) {
            p_282124_.drawCenteredString(this.font, (Component)this.errorMessage, this.width / 2, 110, 16711680);
        }

        super.render(p_282124_, p_88635_, p_88636_, p_88637_);
    }

    private void drawDots(GuiGraphics p_281948_) {
        int $$1 = this.font.width((FormattedText)this.status);
        if (this.animTick % 10 == 0) {
            ++this.dotIndex;
        }

        p_281948_.drawString(this.font, (String)DOTS[this.dotIndex % DOTS.length], this.width / 2 + $$1 / 2 + 5, 50, 16777215, false);
    }

    private void drawProgressBar(GuiGraphics p_281556_) {
        double $$1 = Math.min((double)this.downloadStatus.bytesWritten / (double)this.downloadStatus.totalBytes, 1.0);
        this.progress = String.format(Locale.ROOT, "%.1f", $$1 * 100.0);
        int $$2 = (this.width - 200) / 2;
        int $$3 = $$2 + (int)Math.round(200.0 * $$1);
        p_281556_.fill($$2 - 1, 79, $$3 + 1, 96, -2501934);
        p_281556_.fill($$2, 80, $$3, 95, -8355712);
        p_281556_.drawCenteredString(this.font, (Component)Component.translatable("mco.download.percent", this.progress), this.width / 2, 84, 16777215);
    }

    private void drawDownloadSpeed(GuiGraphics p_282236_) {
        if (this.animTick % 20 == 0) {
            if (this.previousWrittenBytes != null) {
                long $$1 = Util.getMillis() - this.previousTimeSnapshot;
                if ($$1 == 0L) {
                    $$1 = 1L;
                }

                this.bytesPersSecond = 1000L * (this.downloadStatus.bytesWritten - this.previousWrittenBytes) / $$1;
                this.drawDownloadSpeed0(p_282236_, this.bytesPersSecond);
            }

            this.previousWrittenBytes = this.downloadStatus.bytesWritten;
            this.previousTimeSnapshot = Util.getMillis();
        } else {
            this.drawDownloadSpeed0(p_282236_, this.bytesPersSecond);
        }

    }

    private void drawDownloadSpeed0(GuiGraphics p_283338_, long p_281931_) {
        if (p_281931_ > 0L) {
            int $$2 = this.font.width(this.progress);
            p_283338_.drawString(this.font, (Component)Component.translatable("mco.download.speed", Unit.humanReadable(p_281931_)), this.width / 2 + $$2 / 2 + 15, 84, 16777215, false);
        }

    }

    private void downloadSave() {
        (new Thread(() -> {
            try {
                if (!DOWNLOAD_LOCK.tryLock(1L, TimeUnit.SECONDS)) {
                    this.status = Component.translatable("mco.download.failed");
                    return;
                }

                if (!this.cancelled) {
                    this.status = Component.translatable("mco.download.downloading", this.worldName);
                    FileDownload $$0 = new FileDownload();
                    $$0.contentLength(this.worldDownload.downloadLink);
                    $$0.download(this.worldDownload, this.worldName, this.downloadStatus, this.minecraft.getLevelSource());

                    while(!$$0.isFinished()) {
                        if ($$0.isError()) {
                            $$0.cancel();
                            this.errorMessage = Component.translatable("mco.download.failed");
                            this.cancelButton.setMessage(CommonComponents.GUI_DONE);
                            return;
                        }

                        if ($$0.isExtracting()) {
                            if (!this.extracting) {
                                this.status = Component.translatable("mco.download.extracting");
                            }

                            this.extracting = true;
                        }

                        if (this.cancelled) {
                            $$0.cancel();
                            this.downloadCancelled();
                            return;
                        }

                        try {
                            Thread.sleep(500L);
                        } catch (InterruptedException var8) {
                            LOGGER.error("Failed to check Realms backup download status");
                        }
                    }

                    this.finished = true;
                    this.status = Component.translatable("mco.download.done");
                    this.cancelButton.setMessage(CommonComponents.GUI_DONE);
                    return;
                }

                this.downloadCancelled();
            } catch (InterruptedException var9) {
                LOGGER.error("Could not acquire upload lock");
                return;
            } catch (Exception var10) {
                Exception $$3 = var10;
                this.errorMessage = Component.translatable("mco.download.failed");
                $$3.printStackTrace();
                return;
            } finally {
                if (!DOWNLOAD_LOCK.isHeldByCurrentThread()) {
                    return;
                }

                DOWNLOAD_LOCK.unlock();
                this.showDots = false;
                this.finished = true;
            }

        })).start();
    }

    private void downloadCancelled() {
        this.status = Component.translatable("mco.download.cancelled");
    }

    @OnlyIn(Dist.CLIENT)
    public static class DownloadStatus {
        public volatile long bytesWritten;
        public volatile long totalBytes;

        public DownloadStatus() {
        }
    }
}

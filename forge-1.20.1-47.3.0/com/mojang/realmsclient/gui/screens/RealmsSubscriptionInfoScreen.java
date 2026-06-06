//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.mojang.realmsclient.gui.screens;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.Subscription;
import com.mojang.realmsclient.dto.Subscription.SubscriptionType;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen.Type;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.FittingMultiLineTextWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.CommonLinks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsSubscriptionInfoScreen extends RealmsScreen {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final Component SUBSCRIPTION_TITLE = Component.translatable("mco.configure.world.subscription.title");
    private static final Component SUBSCRIPTION_START_LABEL = Component.translatable("mco.configure.world.subscription.start");
    private static final Component TIME_LEFT_LABEL = Component.translatable("mco.configure.world.subscription.timeleft");
    private static final Component DAYS_LEFT_LABEL = Component.translatable("mco.configure.world.subscription.recurring.daysleft");
    private static final Component SUBSCRIPTION_EXPIRED_TEXT = Component.translatable("mco.configure.world.subscription.expired");
    private static final Component SUBSCRIPTION_LESS_THAN_A_DAY_TEXT = Component.translatable("mco.configure.world.subscription.less_than_a_day");
    private static final Component UNKNOWN = Component.translatable("mco.configure.world.subscription.unknown");
    private static final Component RECURRING_INFO = Component.translatable("mco.configure.world.subscription.recurring.info");
    private final Screen lastScreen;
    final RealmsServer serverData;
    final Screen mainScreen;
    private Component daysLeft;
    private Component startDate;
    @Nullable
    private Subscription.SubscriptionType type;

    public RealmsSubscriptionInfoScreen(Screen p_89979_, RealmsServer p_89980_, Screen p_89981_) {
        super(GameNarrator.NO_TITLE);
        this.daysLeft = UNKNOWN;
        this.startDate = UNKNOWN;
        this.lastScreen = p_89979_;
        this.serverData = p_89980_;
        this.mainScreen = p_89981_;
    }

    public void init() {
        this.getSubscription(this.serverData.id);
        this.addRenderableWidget(Button.builder(Component.translatable("mco.configure.world.subscription.extend"), (p_280761_) -> {
            String $$1 = CommonLinks.extendRealms(this.serverData.remoteSubscriptionId, this.minecraft.getUser().getUuid());
            this.minecraft.keyboardHandler.setClipboard($$1);
            Util.getPlatform().openUri($$1);
        }).bounds(this.width / 2 - 100, row(6), 200, 20).build());
        if (this.serverData.expired) {
            this.addRenderableWidget(Button.builder(Component.translatable("mco.configure.world.delete.button"), (p_287304_) -> {
                Component $$1 = Component.translatable("mco.configure.world.delete.question.line1");
                Component $$2 = Component.translatable("mco.configure.world.delete.question.line2");
                this.minecraft.setScreen(new RealmsLongConfirmationScreen(this::deleteRealm, Type.WARNING, $$1, $$2, true));
            }).bounds(this.width / 2 - 100, row(10), 200, 20).build());
        } else {
            this.addRenderableWidget((new FittingMultiLineTextWidget(this.width / 2 - 100, row(8), 200, 46, RECURRING_INFO, this.font)).setColor(10526880));
        }

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, (p_280760_) -> {
            this.minecraft.setScreen(this.lastScreen);
        }).bounds(this.width / 2 - 100, row(12), 200, 20).build());
    }

    public Component getNarrationMessage() {
        return CommonComponents.joinLines(SUBSCRIPTION_TITLE, SUBSCRIPTION_START_LABEL, this.startDate, TIME_LEFT_LABEL, this.daysLeft);
    }

    private void deleteRealm(boolean p_90012_) {
        if (p_90012_) {
            (new Thread("Realms-delete-realm") {
                public void run() {
                    try {
                        RealmsClient $$0 = RealmsClient.create();
                        $$0.deleteWorld(RealmsSubscriptionInfoScreen.this.serverData.id);
                    } catch (RealmsServiceException var2) {
                        RealmsServiceException $$1 = var2;
                        RealmsSubscriptionInfoScreen.LOGGER.error("Couldn't delete world", $$1);
                    }

                    RealmsSubscriptionInfoScreen.this.minecraft.execute(() -> {
                        RealmsSubscriptionInfoScreen.this.minecraft.setScreen(RealmsSubscriptionInfoScreen.this.mainScreen);
                    });
                }
            }).start();
        }

        this.minecraft.setScreen(this);
    }

    private void getSubscription(long p_89990_) {
        RealmsClient $$1 = RealmsClient.create();

        try {
            Subscription $$2 = $$1.subscriptionFor(p_89990_);
            this.daysLeft = this.daysLeftPresentation($$2.daysLeft);
            this.startDate = localPresentation($$2.startDate);
            this.type = $$2.type;
        } catch (RealmsServiceException var5) {
            RealmsServiceException $$3 = var5;
            LOGGER.error("Couldn't get subscription");
            this.minecraft.setScreen(new RealmsGenericErrorScreen($$3, this.lastScreen));
        }

    }

    private static Component localPresentation(long p_182539_) {
        Calendar $$1 = new GregorianCalendar(TimeZone.getDefault());
        $$1.setTimeInMillis(p_182539_);
        return Component.literal(DateFormat.getDateTimeInstance().format($$1.getTime()));
    }

    public boolean keyPressed(int p_89986_, int p_89987_, int p_89988_) {
        if (p_89986_ == 256) {
            this.minecraft.setScreen(this.lastScreen);
            return true;
        } else {
            return super.keyPressed(p_89986_, p_89987_, p_89988_);
        }
    }

    public void render(GuiGraphics p_282831_, int p_281266_, int p_281799_, float p_282330_) {
        this.renderBackground(p_282831_);
        int $$4 = this.width / 2 - 100;
        p_282831_.drawCenteredString(this.font, (Component)SUBSCRIPTION_TITLE, this.width / 2, 17, 16777215);
        p_282831_.drawString(this.font, SUBSCRIPTION_START_LABEL, $$4, row(0), 10526880, false);
        p_282831_.drawString(this.font, this.startDate, $$4, row(1), 16777215, false);
        if (this.type == SubscriptionType.NORMAL) {
            p_282831_.drawString(this.font, TIME_LEFT_LABEL, $$4, row(3), 10526880, false);
        } else if (this.type == SubscriptionType.RECURRING) {
            p_282831_.drawString(this.font, DAYS_LEFT_LABEL, $$4, row(3), 10526880, false);
        }

        p_282831_.drawString(this.font, this.daysLeft, $$4, row(4), 16777215, false);
        super.render(p_282831_, p_281266_, p_281799_, p_282330_);
    }

    private Component daysLeftPresentation(int p_89984_) {
        if (p_89984_ < 0 && this.serverData.expired) {
            return SUBSCRIPTION_EXPIRED_TEXT;
        } else if (p_89984_ <= 1) {
            return SUBSCRIPTION_LESS_THAN_A_DAY_TEXT;
        } else {
            int $$1 = p_89984_ / 30;
            int $$2 = p_89984_ % 30;
            boolean $$3 = $$1 > 0;
            boolean $$4 = $$2 > 0;
            if ($$3 && $$4) {
                return Component.translatable("mco.configure.world.subscription.remaining.months.days", $$1, $$2);
            } else if ($$3) {
                return Component.translatable("mco.configure.world.subscription.remaining.months", $$1);
            } else {
                return $$4 ? Component.translatable("mco.configure.world.subscription.remaining.days", $$2) : Component.empty();
            }
        }
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.mojang.realmsclient.dto;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsNotification {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String NOTIFICATION_UUID = "notificationUuid";
    private static final String DISMISSABLE = "dismissable";
    private static final String SEEN = "seen";
    private static final String TYPE = "type";
    private static final String VISIT_URL = "visitUrl";
    final UUID uuid;
    final boolean dismissable;
    final boolean seen;
    final String type;

    RealmsNotification(UUID p_275316_, boolean p_275303_, boolean p_275497_, String p_275401_) {
        this.uuid = p_275316_;
        this.dismissable = p_275303_;
        this.seen = p_275497_;
        this.type = p_275401_;
    }

    public boolean seen() {
        return this.seen;
    }

    public boolean dismissable() {
        return this.dismissable;
    }

    public UUID uuid() {
        return this.uuid;
    }

    public static List<RealmsNotification> parseList(String p_275464_) {
        List<RealmsNotification> $$1 = new ArrayList();

        try {
            JsonArray $$2 = JsonParser.parseString(p_275464_).getAsJsonObject().get("notifications").getAsJsonArray();
            Iterator var3 = $$2.iterator();

            while(var3.hasNext()) {
                JsonElement $$3 = (JsonElement)var3.next();
                $$1.add(parse($$3.getAsJsonObject()));
            }
        } catch (Exception var5) {
            Exception $$4 = var5;
            LOGGER.error("Could not parse list of RealmsNotifications", $$4);
        }

        return $$1;
    }

    private static RealmsNotification parse(JsonObject p_275549_) {
        UUID $$1 = JsonUtils.getUuidOr("notificationUuid", p_275549_, (UUID)null);
        if ($$1 == null) {
            throw new IllegalStateException("Missing required property notificationUuid");
        } else {
            boolean $$2 = JsonUtils.getBooleanOr("dismissable", p_275549_, true);
            boolean $$3 = JsonUtils.getBooleanOr("seen", p_275549_, false);
            String $$4 = JsonUtils.getRequiredString("type", p_275549_);
            RealmsNotification $$5 = new RealmsNotification($$1, $$2, $$3, $$4);
            return (RealmsNotification)("visitUrl".equals($$4) ? com.mojang.realmsclient.dto.RealmsNotification.VisitUrl.parse($$5, p_275549_) : $$5);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class VisitUrl extends RealmsNotification {
        private static final String URL = "url";
        private static final String BUTTON_TEXT = "buttonText";
        private static final String MESSAGE = "message";
        private final String url;
        private final RealmsText buttonText;
        private final RealmsText message;

        private VisitUrl(RealmsNotification p_275564_, String p_275312_, RealmsText p_275433_, RealmsText p_275541_) {
            super(p_275564_.uuid, p_275564_.dismissable, p_275564_.seen, p_275564_.type);
            this.url = p_275312_;
            this.buttonText = p_275433_;
            this.message = p_275541_;
        }

        public static VisitUrl parse(RealmsNotification p_275651_, JsonObject p_275278_) {
            String $$2 = JsonUtils.getRequiredString("url", p_275278_);
            RealmsText $$3 = (RealmsText)JsonUtils.getRequired("buttonText", p_275278_, RealmsText::parse);
            RealmsText $$4 = (RealmsText)JsonUtils.getRequired("message", p_275278_, RealmsText::parse);
            return new VisitUrl(p_275651_, $$2, $$3, $$4);
        }

        public Component getMessage() {
            return this.message.createComponent(Component.translatable("mco.notification.visitUrl.message.default"));
        }

        public Button buildOpenLinkButton(Screen p_275412_) {
            Component $$1 = this.buttonText.createComponent(Component.translatable("mco.notification.visitUrl.buttonText.default"));
            return Button.builder($$1, ConfirmLinkScreen.confirmLink(this.url, p_275412_, true)).build();
        }
    }
}

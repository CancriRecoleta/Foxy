//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.util.JsonUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class Subscription extends ValueObject {
    private static final Logger LOGGER = LogUtils.getLogger();
    public long startDate;
    public int daysLeft;
    public SubscriptionType type;

    public Subscription() {
        this.type = com.mojang.realmsclient.dto.Subscription.SubscriptionType.NORMAL;
    }

    public static Subscription parse(String p_87673_) {
        Subscription $$1 = new Subscription();

        try {
            JsonParser $$2 = new JsonParser();
            JsonObject $$3 = $$2.parse(p_87673_).getAsJsonObject();
            $$1.startDate = JsonUtils.getLongOr("startDate", $$3, 0L);
            $$1.daysLeft = JsonUtils.getIntOr("daysLeft", $$3, 0);
            $$1.type = typeFrom(JsonUtils.getStringOr("subscriptionType", $$3, com.mojang.realmsclient.dto.Subscription.SubscriptionType.NORMAL.name()));
        } catch (Exception var4) {
            Exception $$4 = var4;
            LOGGER.error("Could not parse Subscription: {}", $$4.getMessage());
        }

        return $$1;
    }

    private static SubscriptionType typeFrom(String p_87675_) {
        try {
            return com.mojang.realmsclient.dto.Subscription.SubscriptionType.valueOf(p_87675_);
        } catch (Exception var2) {
            return com.mojang.realmsclient.dto.Subscription.SubscriptionType.NORMAL;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static enum SubscriptionType {
        NORMAL,
        RECURRING;

        private SubscriptionType() {
        }
    }
}

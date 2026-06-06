//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.Date;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class PendingInvite extends ValueObject {
    private static final Logger LOGGER = LogUtils.getLogger();
    public String invitationId;
    public String worldName;
    public String worldOwnerName;
    public String worldOwnerUuid;
    public Date date;

    public PendingInvite() {
    }

    public static PendingInvite parse(JsonObject p_87431_) {
        PendingInvite $$1 = new PendingInvite();

        try {
            $$1.invitationId = JsonUtils.getStringOr("invitationId", p_87431_, "");
            $$1.worldName = JsonUtils.getStringOr("worldName", p_87431_, "");
            $$1.worldOwnerName = JsonUtils.getStringOr("worldOwnerName", p_87431_, "");
            $$1.worldOwnerUuid = JsonUtils.getStringOr("worldOwnerUuid", p_87431_, "");
            $$1.date = JsonUtils.getDateOr("date", p_87431_);
        } catch (Exception var3) {
            Exception $$2 = var3;
            LOGGER.error("Could not parse PendingInvite: {}", $$2.getMessage());
        }

        return $$1;
    }
}

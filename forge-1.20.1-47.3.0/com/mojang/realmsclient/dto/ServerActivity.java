//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ServerActivity extends ValueObject {
    public String profileUuid;
    public long joinTime;
    public long leaveTime;

    public ServerActivity() {
    }

    public static ServerActivity parse(JsonObject p_167317_) {
        ServerActivity $$1 = new ServerActivity();

        try {
            $$1.profileUuid = JsonUtils.getStringOr("profileUuid", p_167317_, (String)null);
            $$1.joinTime = JsonUtils.getLongOr("joinTime", p_167317_, Long.MIN_VALUE);
            $$1.leaveTime = JsonUtils.getLongOr("leaveTime", p_167317_, Long.MIN_VALUE);
        } catch (Exception var3) {
        }

        return $$1;
    }
}

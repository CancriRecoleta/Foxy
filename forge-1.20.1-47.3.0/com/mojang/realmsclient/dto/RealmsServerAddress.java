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
public class RealmsServerAddress extends ValueObject {
    private static final Logger LOGGER = LogUtils.getLogger();
    public String address;
    public String resourcePackUrl;
    public String resourcePackHash;

    public RealmsServerAddress() {
    }

    public static RealmsServerAddress parse(String p_87572_) {
        JsonParser $$1 = new JsonParser();
        RealmsServerAddress $$2 = new RealmsServerAddress();

        try {
            JsonObject $$3 = $$1.parse(p_87572_).getAsJsonObject();
            $$2.address = JsonUtils.getStringOr("address", $$3, (String)null);
            $$2.resourcePackUrl = JsonUtils.getStringOr("resourcePackUrl", $$3, (String)null);
            $$2.resourcePackHash = JsonUtils.getStringOr("resourcePackHash", $$3, (String)null);
        } catch (Exception var4) {
            Exception $$4 = var4;
            LOGGER.error("Could not parse RealmsServerAddress: {}", $$4.getMessage());
        }

        return $$2;
    }
}

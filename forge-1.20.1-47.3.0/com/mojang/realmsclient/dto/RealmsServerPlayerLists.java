//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.mojang.realmsclient.dto;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import java.util.Iterator;
import java.util.List;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsServerPlayerLists extends ValueObject {
    private static final Logger LOGGER = LogUtils.getLogger();
    public List<RealmsServerPlayerList> servers;

    public RealmsServerPlayerLists() {
    }

    public static RealmsServerPlayerLists parse(String p_87597_) {
        RealmsServerPlayerLists $$1 = new RealmsServerPlayerLists();
        $$1.servers = Lists.newArrayList();

        try {
            JsonParser $$2 = new JsonParser();
            JsonObject $$3 = $$2.parse(p_87597_).getAsJsonObject();
            if ($$3.get("lists").isJsonArray()) {
                JsonArray $$4 = $$3.get("lists").getAsJsonArray();
                Iterator<JsonElement> $$5 = $$4.iterator();

                while($$5.hasNext()) {
                    $$1.servers.add(RealmsServerPlayerList.parse(((JsonElement)$$5.next()).getAsJsonObject()));
                }
            }
        } catch (Exception var6) {
            Exception $$6 = var6;
            LOGGER.error("Could not parse RealmsServerPlayerLists: {}", $$6.getMessage());
        }

        return $$1;
    }
}

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
import com.mojang.realmsclient.util.JsonUtils;
import java.util.Iterator;
import java.util.List;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsServerPlayerList extends ValueObject {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final JsonParser JSON_PARSER = new JsonParser();
    public long serverId;
    public List<String> players;

    public RealmsServerPlayerList() {
    }

    public static RealmsServerPlayerList parse(JsonObject p_87591_) {
        RealmsServerPlayerList $$1 = new RealmsServerPlayerList();

        try {
            $$1.serverId = JsonUtils.getLongOr("serverId", p_87591_, -1L);
            String $$2 = JsonUtils.getStringOr("playerList", p_87591_, (String)null);
            if ($$2 != null) {
                JsonElement $$3 = JSON_PARSER.parse($$2);
                if ($$3.isJsonArray()) {
                    $$1.players = parsePlayers($$3.getAsJsonArray());
                } else {
                    $$1.players = Lists.newArrayList();
                }
            } else {
                $$1.players = Lists.newArrayList();
            }
        } catch (Exception var4) {
            Exception $$4 = var4;
            LOGGER.error("Could not parse RealmsServerPlayerList: {}", $$4.getMessage());
        }

        return $$1;
    }

    private static List<String> parsePlayers(JsonArray p_87589_) {
        List<String> $$1 = Lists.newArrayList();
        Iterator var2 = p_87589_.iterator();

        while(var2.hasNext()) {
            JsonElement $$2 = (JsonElement)var2.next();

            try {
                $$1.add($$2.getAsString());
            } catch (Exception var5) {
            }
        }

        return $$1;
    }
}

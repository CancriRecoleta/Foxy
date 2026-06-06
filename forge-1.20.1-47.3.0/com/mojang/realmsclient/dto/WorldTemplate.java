//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.util.JsonUtils;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class WorldTemplate extends ValueObject {
    private static final Logger LOGGER = LogUtils.getLogger();
    public String id = "";
    public String name = "";
    public String version = "";
    public String author = "";
    public String link = "";
    @Nullable
    public String image;
    public String trailer = "";
    public String recommendedPlayers = "";
    public WorldTemplateType type;

    public WorldTemplate() {
        this.type = com.mojang.realmsclient.dto.WorldTemplate.WorldTemplateType.WORLD_TEMPLATE;
    }

    public static WorldTemplate parse(JsonObject p_87739_) {
        WorldTemplate $$1 = new WorldTemplate();

        try {
            $$1.id = JsonUtils.getStringOr("id", p_87739_, "");
            $$1.name = JsonUtils.getStringOr("name", p_87739_, "");
            $$1.version = JsonUtils.getStringOr("version", p_87739_, "");
            $$1.author = JsonUtils.getStringOr("author", p_87739_, "");
            $$1.link = JsonUtils.getStringOr("link", p_87739_, "");
            $$1.image = JsonUtils.getStringOr("image", p_87739_, (String)null);
            $$1.trailer = JsonUtils.getStringOr("trailer", p_87739_, "");
            $$1.recommendedPlayers = JsonUtils.getStringOr("recommendedPlayers", p_87739_, "");
            $$1.type = com.mojang.realmsclient.dto.WorldTemplate.WorldTemplateType.valueOf(JsonUtils.getStringOr("type", p_87739_, com.mojang.realmsclient.dto.WorldTemplate.WorldTemplateType.WORLD_TEMPLATE.name()));
        } catch (Exception var3) {
            Exception $$2 = var3;
            LOGGER.error("Could not parse WorldTemplate: {}", $$2.getMessage());
        }

        return $$1;
    }

    @OnlyIn(Dist.CLIENT)
    public static enum WorldTemplateType {
        WORLD_TEMPLATE,
        MINIGAME,
        ADVENTUREMAP,
        EXPERIENCE,
        INSPIRATION;

        private WorldTemplateType() {
        }
    }
}

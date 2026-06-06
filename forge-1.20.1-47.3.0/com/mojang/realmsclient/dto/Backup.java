//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.mojang.realmsclient.dto;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class Backup extends ValueObject {
    private static final Logger LOGGER = LogUtils.getLogger();
    public String backupId;
    public Date lastModifiedDate;
    public long size;
    private boolean uploadedVersion;
    public Map<String, String> metadata = Maps.newHashMap();
    public Map<String, String> changeList = Maps.newHashMap();

    public Backup() {
    }

    public static Backup parse(JsonElement p_87400_) {
        JsonObject $$1 = p_87400_.getAsJsonObject();
        Backup $$2 = new Backup();

        try {
            $$2.backupId = JsonUtils.getStringOr("backupId", $$1, "");
            $$2.lastModifiedDate = JsonUtils.getDateOr("lastModifiedDate", $$1);
            $$2.size = JsonUtils.getLongOr("size", $$1, 0L);
            if ($$1.has("metadata")) {
                JsonObject $$3 = $$1.getAsJsonObject("metadata");
                Set<Map.Entry<String, JsonElement>> $$4 = $$3.entrySet();
                Iterator var5 = $$4.iterator();

                while(var5.hasNext()) {
                    Map.Entry<String, JsonElement> $$5 = (Map.Entry)var5.next();
                    if (!((JsonElement)$$5.getValue()).isJsonNull()) {
                        $$2.metadata.put((String)$$5.getKey(), ((JsonElement)$$5.getValue()).getAsString());
                    }
                }
            }
        } catch (Exception var7) {
            Exception $$6 = var7;
            LOGGER.error("Could not parse Backup: {}", $$6.getMessage());
        }

        return $$2;
    }

    public boolean isUploadedVersion() {
        return this.uploadedVersion;
    }

    public void setUploadedVersion(boolean p_87404_) {
        this.uploadedVersion = p_87404_;
    }
}

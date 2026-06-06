//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.mojang.realmsclient.client;

import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.util.JsonUtils;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsError {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final String errorMessage;
    private final int errorCode;

    private RealmsError(String p_87300_, int p_87301_) {
        this.errorMessage = p_87300_;
        this.errorCode = p_87301_;
    }

    @Nullable
    public static RealmsError parse(String p_87304_) {
        if (Strings.isNullOrEmpty(p_87304_)) {
            return null;
        } else {
            try {
                JsonObject $$1 = JsonParser.parseString(p_87304_).getAsJsonObject();
                String $$2 = JsonUtils.getStringOr("errorMsg", $$1, "");
                int $$3 = JsonUtils.getIntOr("errorCode", $$1, -1);
                return new RealmsError($$2, $$3);
            } catch (Exception var4) {
                Exception $$4 = var4;
                LOGGER.error("Could not parse RealmsError: {}", $$4.getMessage());
                LOGGER.error("The error was: {}", p_87304_);
                return null;
            }
        }
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public int getErrorCode() {
        return this.errorCode;
    }
}

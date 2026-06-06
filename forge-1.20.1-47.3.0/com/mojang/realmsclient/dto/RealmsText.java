//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.mojang.realmsclient.dto;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import javax.annotation.Nullable;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsText {
    private static final String TRANSLATION_KEY = "translationKey";
    private static final String ARGS = "args";
    private final String translationKey;
    @Nullable
    private final Object[] args;

    private RealmsText(String p_275727_, @Nullable Object[] p_275314_) {
        this.translationKey = p_275727_;
        this.args = p_275314_;
    }

    public Component createComponent(Component p_275681_) {
        if (!I18n.exists(this.translationKey)) {
            return p_275681_;
        } else {
            return this.args == null ? Component.translatable(this.translationKey) : Component.translatable(this.translationKey, this.args);
        }
    }

    public static RealmsText parse(JsonObject p_275381_) {
        String $$1 = JsonUtils.getRequiredString("translationKey", p_275381_);
        JsonElement $$2 = p_275381_.get("args");
        String[] $$5;
        if ($$2 != null && !$$2.isJsonNull()) {
            JsonArray $$4 = $$2.getAsJsonArray();
            $$5 = new String[$$4.size()];

            for(int $$6 = 0; $$6 < $$4.size(); ++$$6) {
                $$5[$$6] = $$4.get($$6).getAsString();
            }
        } else {
            $$5 = null;
        }

        return new RealmsText($$1, $$5);
    }
}

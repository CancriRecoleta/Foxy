//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.players;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;

public class UserBanListEntry extends BanListEntry<GameProfile> {
    public UserBanListEntry(GameProfile p_11436_) {
        this(p_11436_, (Date)null, (String)null, (Date)null, (String)null);
    }

    public UserBanListEntry(GameProfile p_11438_, @Nullable Date p_11439_, @Nullable String p_11440_, @Nullable Date p_11441_, @Nullable String p_11442_) {
        super(p_11438_, p_11439_, p_11440_, p_11441_, p_11442_);
    }

    public UserBanListEntry(JsonObject p_11434_) {
        super(createGameProfile(p_11434_), p_11434_);
    }

    protected void serialize(JsonObject p_11444_) {
        if (this.getUser() != null) {
            p_11444_.addProperty("uuid", ((GameProfile)this.getUser()).getId() == null ? "" : ((GameProfile)this.getUser()).getId().toString());
            p_11444_.addProperty("name", ((GameProfile)this.getUser()).getName());
            super.serialize(p_11444_);
        }
    }

    public Component getDisplayName() {
        GameProfile $$0 = (GameProfile)this.getUser();
        return Component.literal($$0.getName() != null ? $$0.getName() : Objects.toString($$0.getId(), "(Unknown)"));
    }

    private static GameProfile createGameProfile(JsonObject p_11446_) {
        if (p_11446_.has("uuid") && p_11446_.has("name")) {
            String $$1 = p_11446_.get("uuid").getAsString();

            UUID $$4;
            try {
                $$4 = UUID.fromString($$1);
            } catch (Throwable var4) {
                return null;
            }

            return new GameProfile($$4, p_11446_.get("name").getAsString());
        } else {
            return null;
        }
    }
}

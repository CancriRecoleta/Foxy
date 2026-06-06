//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;

public class CriterionProgress {
    private static final SimpleDateFormat DATE_FORMAT;
    @Nullable
    private Date obtained;

    public CriterionProgress() {
    }

    public boolean isDone() {
        return this.obtained != null;
    }

    public void grant() {
        this.obtained = new Date();
    }

    public void revoke() {
        this.obtained = null;
    }

    @Nullable
    public Date getObtained() {
        return this.obtained;
    }

    public String toString() {
        Object var10000 = this.obtained == null ? "false" : this.obtained;
        return "CriterionProgress{obtained=" + var10000 + "}";
    }

    public void serializeToNetwork(FriendlyByteBuf p_12915_) {
        p_12915_.writeNullable(this.obtained, FriendlyByteBuf::writeDate);
    }

    public JsonElement serializeToJson() {
        return (JsonElement)(this.obtained != null ? new JsonPrimitive(DATE_FORMAT.format(this.obtained)) : JsonNull.INSTANCE);
    }

    public static CriterionProgress fromNetwork(FriendlyByteBuf p_12918_) {
        CriterionProgress $$1 = new CriterionProgress();
        $$1.obtained = (Date)p_12918_.readNullable(FriendlyByteBuf::readDate);
        return $$1;
    }

    public static CriterionProgress fromJson(String p_12913_) {
        CriterionProgress $$1 = new CriterionProgress();

        try {
            $$1.obtained = DATE_FORMAT.parse(p_12913_);
            return $$1;
        } catch (ParseException var3) {
            ParseException $$2 = var3;
            throw new JsonSyntaxException("Invalid datetime: " + p_12913_, $$2);
        }
    }

    static {
        DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.ROOT);
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.packs.metadata.pack;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.ForgeHooks;

public class PackMetadataSectionSerializer implements MetadataSectionType<PackMetadataSection> {
    public PackMetadataSectionSerializer() {
    }

    public PackMetadataSection fromJson(JsonObject p_10380_) {
        Component component = Serializer.fromJson(p_10380_.get("description"));
        if (component == null) {
            throw new JsonParseException("Invalid/missing description!");
        } else {
            int i = GsonHelper.getAsInt(p_10380_, "pack_format");
            return new PackMetadataSection(component, i, ForgeHooks.readTypedPackFormats(p_10380_));
        }
    }

    public JsonObject toJson(PackMetadataSection p_250206_) {
        JsonObject jsonobject = new JsonObject();
        jsonobject.add("description", Serializer.toJsonTree(p_250206_.getDescription()));
        jsonobject.addProperty("pack_format", p_250206_.getPackFormat());
        ForgeHooks.writeTypedPackFormats(jsonobject, p_250206_);
        return jsonobject;
    }

    public String getMetadataSectionName() {
        return "pack";
    }
}

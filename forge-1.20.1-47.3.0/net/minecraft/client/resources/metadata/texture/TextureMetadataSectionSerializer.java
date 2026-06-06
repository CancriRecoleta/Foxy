//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.resources.metadata.texture;

import com.google.gson.JsonObject;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TextureMetadataSectionSerializer implements MetadataSectionSerializer<TextureMetadataSection> {
    public TextureMetadataSectionSerializer() {
    }

    public TextureMetadataSection fromJson(JsonObject p_119122_) {
        boolean $$1 = GsonHelper.getAsBoolean(p_119122_, "blur", false);
        boolean $$2 = GsonHelper.getAsBoolean(p_119122_, "clamp", false);
        return new TextureMetadataSection($$1, $$2);
    }

    public String getMetadataSectionName() {
        return "texture";
    }
}

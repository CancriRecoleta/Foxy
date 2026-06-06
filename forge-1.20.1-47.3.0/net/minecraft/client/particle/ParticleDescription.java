//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.particle;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleDescription {
    private final List<ResourceLocation> textures;

    private ParticleDescription(List<ResourceLocation> p_107281_) {
        this.textures = p_107281_;
    }

    public List<ResourceLocation> getTextures() {
        return this.textures;
    }

    public static ParticleDescription fromJson(JsonObject p_107286_) {
        JsonArray $$1 = GsonHelper.getAsJsonArray(p_107286_, "textures", (JsonArray)null);
        if ($$1 == null) {
            return new ParticleDescription(List.of());
        } else {
            List<ResourceLocation> $$2 = (List)Streams.stream($$1).map((p_107284_) -> {
                return GsonHelper.convertToString(p_107284_, "texture");
            }).map(ResourceLocation::new).collect(ImmutableList.toImmutableList());
            return new ParticleDescription($$2);
        }
    }
}

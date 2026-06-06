//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.resources.language;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ExtraCodecs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public record LanguageInfo(String region, String name, boolean bidirectional) {
    public static final Codec<LanguageInfo> CODEC = RecordCodecBuilder.create((p_265767_) -> {
        return p_265767_.group(ExtraCodecs.NON_EMPTY_STRING.fieldOf("region").forGetter(LanguageInfo::region), ExtraCodecs.NON_EMPTY_STRING.fieldOf("name").forGetter(LanguageInfo::name), Codec.BOOL.optionalFieldOf("bidirectional", false).forGetter(LanguageInfo::bidirectional)).apply(p_265767_, LanguageInfo::new);
    });

    public LanguageInfo(String region, String name, boolean bidirectional) {
        this.region = region;
        this.name = name;
        this.bidirectional = bidirectional;
    }

    public Component toComponent() {
        return Component.literal(this.name + " (" + this.region + ")");
    }

    public String region() {
        return this.region;
    }

    public String name() {
        return this.name;
    }

    public boolean bidirectional() {
        return this.bidirectional;
    }
}

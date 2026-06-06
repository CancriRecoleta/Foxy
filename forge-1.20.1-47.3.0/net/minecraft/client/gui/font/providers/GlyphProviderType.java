//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.font.providers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.gui.font.providers.BitmapProvider.Definition;
import net.minecraft.util.StringRepresentable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum GlyphProviderType implements StringRepresentable {
    BITMAP("bitmap", Definition.CODEC),
    TTF("ttf", TrueTypeGlyphProviderDefinition.CODEC),
    SPACE("space", com.mojang.blaze3d.font.SpaceProvider.Definition.CODEC),
    UNIHEX("unihex", net.minecraft.client.gui.font.providers.UnihexProvider.Definition.CODEC),
    REFERENCE("reference", ProviderReferenceDefinition.CODEC);

    public static final Codec<GlyphProviderType> CODEC = StringRepresentable.fromEnum(GlyphProviderType::values);
    private final String name;
    private final MapCodec<? extends GlyphProviderDefinition> codec;

    private GlyphProviderType(String p_286573_, MapCodec p_286248_) {
        this.name = p_286573_;
        this.codec = p_286248_;
    }

    public String getSerializedName() {
        return this.name;
    }

    public MapCodec<? extends GlyphProviderDefinition> mapCodec() {
        return this.codec;
    }
}

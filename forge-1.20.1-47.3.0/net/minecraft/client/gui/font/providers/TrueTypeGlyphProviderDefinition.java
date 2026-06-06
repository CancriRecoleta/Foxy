//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.font.providers;

import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.TrueTypeGlyphProvider;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryUtil;

@OnlyIn(Dist.CLIENT)
public record TrueTypeGlyphProviderDefinition(ResourceLocation location, float size, float oversample, Shift shift, String skip) implements GlyphProviderDefinition {
    private static final Codec<String> SKIP_LIST_CODEC;
    public static final MapCodec<TrueTypeGlyphProviderDefinition> CODEC;

    public TrueTypeGlyphProviderDefinition(ResourceLocation location, float size, float oversample, Shift shift, String skip) {
        this.location = location;
        this.size = size;
        this.oversample = oversample;
        this.shift = shift;
        this.skip = skip;
    }

    public GlyphProviderType type() {
        return GlyphProviderType.TTF;
    }

    public Either<GlyphProviderDefinition.Loader, GlyphProviderDefinition.Reference> unpack() {
        return Either.left(this::load);
    }

    private GlyphProvider load(ResourceManager p_286229_) throws IOException {
        STBTTFontinfo $$1 = null;
        ByteBuffer $$2 = null;

        try {
            InputStream $$3 = p_286229_.open(this.location.withPrefix("font/"));

            TrueTypeGlyphProvider var5;
            try {
                $$1 = STBTTFontinfo.malloc();
                $$2 = TextureUtil.readResource($$3);
                $$2.flip();
                if (!STBTruetype.stbtt_InitFont($$1, $$2)) {
                    throw new IOException("Invalid ttf");
                }

                var5 = new TrueTypeGlyphProvider($$2, $$1, this.size, this.oversample, this.shift.x, this.shift.y, this.skip);
            } catch (Throwable var8) {
                if ($$3 != null) {
                    try {
                        $$3.close();
                    } catch (Throwable var7) {
                        var8.addSuppressed(var7);
                    }
                }

                throw var8;
            }

            if ($$3 != null) {
                $$3.close();
            }

            return var5;
        } catch (Exception var9) {
            Exception $$4 = var9;
            if ($$1 != null) {
                $$1.free();
            }

            MemoryUtil.memFree($$2);
            throw $$4;
        }
    }

    public ResourceLocation location() {
        return this.location;
    }

    public float size() {
        return this.size;
    }

    public float oversample() {
        return this.oversample;
    }

    public Shift shift() {
        return this.shift;
    }

    public String skip() {
        return this.skip;
    }

    static {
        SKIP_LIST_CODEC = Codec.either(Codec.STRING, Codec.STRING.listOf()).xmap((p_286728_) -> {
            return (String)p_286728_.map((p_286306_) -> {
                return p_286306_;
            }, (p_286852_) -> {
                return String.join("", p_286852_);
            });
        }, Either::left);
        CODEC = RecordCodecBuilder.mapCodec((p_286284_) -> {
            return p_286284_.group(ResourceLocation.CODEC.fieldOf("file").forGetter(TrueTypeGlyphProviderDefinition::location), Codec.FLOAT.optionalFieldOf("size", 11.0F).forGetter(TrueTypeGlyphProviderDefinition::size), Codec.FLOAT.optionalFieldOf("oversample", 1.0F).forGetter(TrueTypeGlyphProviderDefinition::oversample), net.minecraft.client.gui.font.providers.TrueTypeGlyphProviderDefinition.Shift.CODEC.optionalFieldOf("shift", net.minecraft.client.gui.font.providers.TrueTypeGlyphProviderDefinition.Shift.NONE).forGetter(TrueTypeGlyphProviderDefinition::shift), SKIP_LIST_CODEC.optionalFieldOf("skip", "").forGetter(TrueTypeGlyphProviderDefinition::skip)).apply(p_286284_, TrueTypeGlyphProviderDefinition::new);
        });
    }

    @OnlyIn(Dist.CLIENT)
    public static record Shift(float x, float y) {
        public static final Shift NONE = new Shift(0.0F, 0.0F);
        public static final Codec<Shift> CODEC;

        public Shift(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public float x() {
            return this.x;
        }

        public float y() {
            return this.y;
        }

        static {
            CODEC = Codec.FLOAT.listOf().comapFlatMap((p_286374_) -> {
                return Util.fixedSize((List)p_286374_, 2).map((p_286746_) -> {
                    return new Shift((Float)p_286746_.get(0), (Float)p_286746_.get(1));
                });
            }, (p_286274_) -> {
                return List.of(p_286274_.x, p_286274_.y);
            });
        }
    }
}

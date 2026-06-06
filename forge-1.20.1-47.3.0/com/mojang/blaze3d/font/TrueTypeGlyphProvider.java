//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.mojang.blaze3d.font;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.NativeImage.Format;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

@OnlyIn(Dist.CLIENT)
public class TrueTypeGlyphProvider implements GlyphProvider {
    @Nullable
    private ByteBuffer fontMemory;
    @Nullable
    private STBTTFontinfo font;
    final float oversample;
    private final IntSet skip = new IntArraySet();
    final float shiftX;
    final float shiftY;
    final float pointScale;
    final float ascent;

    public TrueTypeGlyphProvider(ByteBuffer p_83846_, STBTTFontinfo p_83847_, float p_83848_, float p_83849_, float p_83850_, float p_83851_, String p_83852_) {
        this.fontMemory = p_83846_;
        this.font = p_83847_;
        this.oversample = p_83849_;
        IntStream var10000 = p_83852_.codePoints();
        IntSet var10001 = this.skip;
        Objects.requireNonNull(var10001);
        var10000.forEach(var10001::add);
        this.shiftX = p_83850_ * p_83849_;
        this.shiftY = p_83851_ * p_83849_;
        this.pointScale = STBTruetype.stbtt_ScaleForPixelHeight(p_83847_, p_83848_ * p_83849_);
        MemoryStack $$7 = MemoryStack.stackPush();

        try {
            IntBuffer $$8 = $$7.mallocInt(1);
            IntBuffer $$9 = $$7.mallocInt(1);
            IntBuffer $$10 = $$7.mallocInt(1);
            STBTruetype.stbtt_GetFontVMetrics(p_83847_, $$8, $$9, $$10);
            this.ascent = (float)$$8.get(0) * this.pointScale;
        } catch (Throwable var13) {
            if ($$7 != null) {
                try {
                    $$7.close();
                } catch (Throwable var12) {
                    var13.addSuppressed(var12);
                }
            }

            throw var13;
        }

        if ($$7 != null) {
            $$7.close();
        }

    }

    @Nullable
    public GlyphInfo getGlyph(int p_231116_) {
        STBTTFontinfo $$1 = this.validateFontOpen();
        if (this.skip.contains(p_231116_)) {
            return null;
        } else {
            MemoryStack $$2 = MemoryStack.stackPush();

            IntBuffer $$4;
            label61: {
                Glyph var17;
                label62: {
                    GlyphInfo.SpaceGlyphInfo var14;
                    try {
                        int $$3 = STBTruetype.stbtt_FindGlyphIndex($$1, p_231116_);
                        if ($$3 == 0) {
                            $$4 = null;
                            break label61;
                        }

                        $$4 = $$2.mallocInt(1);
                        IntBuffer $$5 = $$2.mallocInt(1);
                        IntBuffer $$6 = $$2.mallocInt(1);
                        IntBuffer $$7 = $$2.mallocInt(1);
                        IntBuffer $$8 = $$2.mallocInt(1);
                        IntBuffer $$9 = $$2.mallocInt(1);
                        STBTruetype.stbtt_GetGlyphHMetrics($$1, $$3, $$8, $$9);
                        STBTruetype.stbtt_GetGlyphBitmapBoxSubpixel($$1, $$3, this.pointScale, this.pointScale, this.shiftX, this.shiftY, $$4, $$5, $$6, $$7);
                        float $$10 = (float)$$8.get(0) * this.pointScale;
                        int $$11 = $$6.get(0) - $$4.get(0);
                        int $$12 = $$7.get(0) - $$5.get(0);
                        if ($$11 > 0 && $$12 > 0) {
                            var17 = new Glyph($$4.get(0), $$6.get(0), -$$5.get(0), -$$7.get(0), $$10, (float)$$9.get(0) * this.pointScale, $$3);
                            break label62;
                        }

                        var14 = () -> {
                            return $$10 / this.oversample;
                        };
                    } catch (Throwable var16) {
                        if ($$2 != null) {
                            try {
                                $$2.close();
                            } catch (Throwable var15) {
                                var16.addSuppressed(var15);
                            }
                        }

                        throw var16;
                    }

                    if ($$2 != null) {
                        $$2.close();
                    }

                    return var14;
                }

                if ($$2 != null) {
                    $$2.close();
                }

                return var17;
            }

            if ($$2 != null) {
                $$2.close();
            }

            return $$4;
        }
    }

    STBTTFontinfo validateFontOpen() {
        if (this.fontMemory != null && this.font != null) {
            return this.font;
        } else {
            throw new IllegalArgumentException("Provider already closed");
        }
    }

    public void close() {
        if (this.font != null) {
            this.font.free();
            this.font = null;
        }

        MemoryUtil.memFree(this.fontMemory);
        this.fontMemory = null;
    }

    public IntSet getSupportedGlyphs() {
        return (IntSet)IntStream.range(0, 65535).filter((p_231118_) -> {
            return !this.skip.contains(p_231118_);
        }).collect(IntOpenHashSet::new, IntCollection::add, IntCollection::addAll);
    }

    @OnlyIn(Dist.CLIENT)
    class Glyph implements GlyphInfo {
        final int width;
        final int height;
        final float bearingX;
        final float bearingY;
        private final float advance;
        final int index;

        Glyph(int p_83882_, int p_83883_, int p_83884_, int p_83885_, float p_83886_, float p_83887_, int p_83888_) {
            this.width = p_83883_ - p_83882_;
            this.height = p_83884_ - p_83885_;
            this.advance = p_83886_ / TrueTypeGlyphProvider.this.oversample;
            this.bearingX = (p_83887_ + (float)p_83882_ + TrueTypeGlyphProvider.this.shiftX) / TrueTypeGlyphProvider.this.oversample;
            this.bearingY = (TrueTypeGlyphProvider.this.ascent - (float)p_83884_ + TrueTypeGlyphProvider.this.shiftY) / TrueTypeGlyphProvider.this.oversample;
            this.index = p_83888_;
        }

        public float getAdvance() {
            return this.advance;
        }

        public BakedGlyph bake(Function<SheetGlyphInfo, BakedGlyph> p_231120_) {
            return (BakedGlyph)p_231120_.apply(new SheetGlyphInfo() {
                public int getPixelWidth() {
                    return Glyph.this.width;
                }

                public int getPixelHeight() {
                    return Glyph.this.height;
                }

                public float getOversample() {
                    return TrueTypeGlyphProvider.this.oversample;
                }

                public float getBearingX() {
                    return Glyph.this.bearingX;
                }

                public float getBearingY() {
                    return Glyph.this.bearingY;
                }

                public void upload(int p_231126_, int p_231127_) {
                    STBTTFontinfo $$2 = TrueTypeGlyphProvider.this.validateFontOpen();
                    NativeImage $$3 = new NativeImage(Format.LUMINANCE, Glyph.this.width, Glyph.this.height, false);
                    $$3.copyFromFont($$2, Glyph.this.index, Glyph.this.width, Glyph.this.height, TrueTypeGlyphProvider.this.pointScale, TrueTypeGlyphProvider.this.pointScale, TrueTypeGlyphProvider.this.shiftX, TrueTypeGlyphProvider.this.shiftY, 0, 0);
                    $$3.upload(0, p_231126_, p_231127_, 0, 0, Glyph.this.width, Glyph.this.height, false, true);
                }

                public boolean isColored() {
                    return false;
                }
            });
        }
    }
}

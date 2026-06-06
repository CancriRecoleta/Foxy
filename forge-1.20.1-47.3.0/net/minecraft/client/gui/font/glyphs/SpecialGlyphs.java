//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.font.glyphs;

import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.SheetGlyphInfo;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.NativeImage.Format;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum SpecialGlyphs implements GlyphInfo {
    WHITE(() -> {
        return generate(5, 8, (p_232613_, p_232614_) -> {
            return -1;
        });
    }),
    MISSING(() -> {
        int $$0 = true;
        int $$1 = true;
        return generate(5, 8, (p_232606_, p_232607_) -> {
            boolean $$2 = p_232606_ == 0 || p_232606_ + 1 == 5 || p_232607_ == 0 || p_232607_ + 1 == 8;
            return $$2 ? -1 : 0;
        });
    });

    final NativeImage image;

    private static NativeImage generate(int p_232609_, int p_232610_, PixelProvider p_232611_) {
        NativeImage $$3 = new NativeImage(Format.RGBA, p_232609_, p_232610_, false);

        for(int $$4 = 0; $$4 < p_232610_; ++$$4) {
            for(int $$5 = 0; $$5 < p_232609_; ++$$5) {
                $$3.setPixelRGBA($$5, $$4, p_232611_.getColor($$5, $$4));
            }
        }

        $$3.untrack();
        return $$3;
    }

    private SpecialGlyphs(Supplier p_232604_) {
        this.image = (NativeImage)p_232604_.get();
    }

    public float getAdvance() {
        return (float)(this.image.getWidth() + 1);
    }

    public BakedGlyph bake(Function<SheetGlyphInfo, BakedGlyph> p_232616_) {
        return (BakedGlyph)p_232616_.apply(new SheetGlyphInfo() {
            public int getPixelWidth() {
                return SpecialGlyphs.this.image.getWidth();
            }

            public int getPixelHeight() {
                return SpecialGlyphs.this.image.getHeight();
            }

            public float getOversample() {
                return 1.0F;
            }

            public void upload(int p_232629_, int p_232630_) {
                SpecialGlyphs.this.image.upload(0, p_232629_, p_232630_, false);
            }

            public boolean isColored() {
                return true;
            }
        });
    }

    @FunctionalInterface
    @OnlyIn(Dist.CLIENT)
    private interface PixelProvider {
        int getColor(int var1, int var2);
    }
}

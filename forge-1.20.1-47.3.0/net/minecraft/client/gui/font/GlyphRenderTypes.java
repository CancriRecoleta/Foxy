//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.font;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public record GlyphRenderTypes(RenderType normal, RenderType seeThrough, RenderType polygonOffset) {
    public GlyphRenderTypes(RenderType normal, RenderType seeThrough, RenderType polygonOffset) {
        this.normal = normal;
        this.seeThrough = seeThrough;
        this.polygonOffset = polygonOffset;
    }

    public static GlyphRenderTypes createForIntensityTexture(ResourceLocation p_285411_) {
        return new GlyphRenderTypes(RenderType.textIntensity(p_285411_), RenderType.textIntensitySeeThrough(p_285411_), RenderType.textIntensityPolygonOffset(p_285411_));
    }

    public static GlyphRenderTypes createForColorTexture(ResourceLocation p_285486_) {
        return new GlyphRenderTypes(RenderType.text(p_285486_), RenderType.textSeeThrough(p_285486_), RenderType.textPolygonOffset(p_285486_));
    }

    public RenderType select(Font.DisplayMode p_285259_) {
        RenderType var10000;
        switch (p_285259_) {
            case NORMAL -> var10000 = this.normal;
            case SEE_THROUGH -> var10000 = this.seeThrough;
            case POLYGON_OFFSET -> var10000 = this.polygonOffset;
            default -> throw new IncompatibleClassChangeError();
        }

        return var10000;
    }

    public RenderType normal() {
        return this.normal;
    }

    public RenderType seeThrough() {
        return this.seeThrough;
    }

    public RenderType polygonOffset() {
        return this.polygonOffset;
    }
}

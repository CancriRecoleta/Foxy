//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.font;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.SheetGlyphInfo;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.font.glyphs.SpecialGlyphs;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FontSet implements AutoCloseable {
    private static final RandomSource RANDOM = RandomSource.create();
    private static final float LARGE_FORWARD_ADVANCE = 32.0F;
    private final TextureManager textureManager;
    private final ResourceLocation name;
    private BakedGlyph missingGlyph;
    private BakedGlyph whiteGlyph;
    private final List<GlyphProvider> providers = Lists.newArrayList();
    private final CodepointMap<BakedGlyph> glyphs = new CodepointMap((p_284630_) -> {
        return new BakedGlyph[p_284630_];
    }, (p_284629_) -> {
        return new BakedGlyph[p_284629_][];
    });
    private final CodepointMap<GlyphInfoFilter> glyphInfos = new CodepointMap((p_284631_) -> {
        return new GlyphInfoFilter[p_284631_];
    }, (p_284632_) -> {
        return new GlyphInfoFilter[p_284632_][];
    });
    private final Int2ObjectMap<IntList> glyphsByWidth = new Int2ObjectOpenHashMap();
    private final List<FontTexture> textures = Lists.newArrayList();

    public FontSet(TextureManager p_95062_, ResourceLocation p_95063_) {
        this.textureManager = p_95062_;
        this.name = p_95063_;
    }

    public void reload(List<GlyphProvider> p_95072_) {
        this.closeProviders();
        this.closeTextures();
        this.glyphs.clear();
        this.glyphInfos.clear();
        this.glyphsByWidth.clear();
        this.missingGlyph = SpecialGlyphs.MISSING.bake(this::stitch);
        this.whiteGlyph = SpecialGlyphs.WHITE.bake(this::stitch);
        IntSet $$1 = new IntOpenHashSet();
        Iterator var3 = p_95072_.iterator();

        while(var3.hasNext()) {
            GlyphProvider $$2 = (GlyphProvider)var3.next();
            $$1.addAll($$2.getSupportedGlyphs());
        }

        Set<GlyphProvider> $$3 = Sets.newHashSet();
        $$1.forEach((p_232561_) -> {
            Iterator var4 = p_95072_.iterator();

            while(var4.hasNext()) {
                GlyphProvider $$3x = (GlyphProvider)var4.next();
                GlyphInfo $$4 = $$3x.getGlyph(p_232561_);
                if ($$4 != null) {
                    $$3.add($$3x);
                    if ($$4 != SpecialGlyphs.MISSING) {
                        ((IntList)this.glyphsByWidth.computeIfAbsent(Mth.ceil($$4.getAdvance(false)), (p_232567_) -> {
                            return new IntArrayList();
                        })).add(p_232561_);
                    }
                    break;
                }
            }

        });
        Stream var10000 = p_95072_.stream();
        Objects.requireNonNull($$3);
        var10000 = var10000.filter($$3::contains);
        List var10001 = this.providers;
        Objects.requireNonNull(var10001);
        var10000.forEach(var10001::add);
    }

    public void close() {
        this.closeProviders();
        this.closeTextures();
    }

    private void closeProviders() {
        Iterator var1 = this.providers.iterator();

        while(var1.hasNext()) {
            GlyphProvider $$0 = (GlyphProvider)var1.next();
            $$0.close();
        }

        this.providers.clear();
    }

    private void closeTextures() {
        Iterator var1 = this.textures.iterator();

        while(var1.hasNext()) {
            FontTexture $$0 = (FontTexture)var1.next();
            $$0.close();
        }

        this.textures.clear();
    }

    private static boolean hasFishyAdvance(GlyphInfo p_243323_) {
        float $$1 = p_243323_.getAdvance(false);
        if (!($$1 < 0.0F) && !($$1 > 32.0F)) {
            float $$2 = p_243323_.getAdvance(true);
            return $$2 < 0.0F || $$2 > 32.0F;
        } else {
            return true;
        }
    }

    private GlyphInfoFilter computeGlyphInfo(int p_243321_) {
        GlyphInfo $$1 = null;
        Iterator var3 = this.providers.iterator();

        while(var3.hasNext()) {
            GlyphProvider $$2 = (GlyphProvider)var3.next();
            GlyphInfo $$3 = $$2.getGlyph(p_243321_);
            if ($$3 != null) {
                if ($$1 == null) {
                    $$1 = $$3;
                }

                if (!hasFishyAdvance($$3)) {
                    return new GlyphInfoFilter($$1, $$3);
                }
            }
        }

        if ($$1 != null) {
            return new GlyphInfoFilter($$1, SpecialGlyphs.MISSING);
        } else {
            return net.minecraft.client.gui.font.FontSet.GlyphInfoFilter.MISSING;
        }
    }

    public GlyphInfo getGlyphInfo(int p_243235_, boolean p_243251_) {
        return ((GlyphInfoFilter)this.glyphInfos.computeIfAbsent(p_243235_, this::computeGlyphInfo)).select(p_243251_);
    }

    private BakedGlyph computeBakedGlyph(int p_232565_) {
        Iterator var2 = this.providers.iterator();

        GlyphInfo $$2;
        do {
            if (!var2.hasNext()) {
                return this.missingGlyph;
            }

            GlyphProvider $$1 = (GlyphProvider)var2.next();
            $$2 = $$1.getGlyph(p_232565_);
        } while($$2 == null);

        return $$2.bake(this::stitch);
    }

    public BakedGlyph getGlyph(int p_95079_) {
        return (BakedGlyph)this.glyphs.computeIfAbsent(p_95079_, this::computeBakedGlyph);
    }

    private BakedGlyph stitch(SheetGlyphInfo p_232557_) {
        Iterator var2 = this.textures.iterator();

        BakedGlyph $$2;
        do {
            if (!var2.hasNext()) {
                ResourceLocation $$3 = this.name.withSuffix("/" + this.textures.size());
                boolean $$4 = p_232557_.isColored();
                GlyphRenderTypes $$5 = $$4 ? GlyphRenderTypes.createForColorTexture($$3) : GlyphRenderTypes.createForIntensityTexture($$3);
                FontTexture $$6 = new FontTexture($$5, $$4);
                this.textures.add($$6);
                this.textureManager.register((ResourceLocation)$$3, (AbstractTexture)$$6);
                BakedGlyph $$7 = $$6.add(p_232557_);
                return $$7 == null ? this.missingGlyph : $$7;
            }

            FontTexture $$1 = (FontTexture)var2.next();
            $$2 = $$1.add(p_232557_);
        } while($$2 == null);

        return $$2;
    }

    public BakedGlyph getRandomGlyph(GlyphInfo p_95068_) {
        IntList $$1 = (IntList)this.glyphsByWidth.get(Mth.ceil(p_95068_.getAdvance(false)));
        return $$1 != null && !$$1.isEmpty() ? this.getGlyph($$1.getInt(RANDOM.nextInt($$1.size()))) : this.missingGlyph;
    }

    public BakedGlyph whiteGlyph() {
        return this.whiteGlyph;
    }

    @OnlyIn(Dist.CLIENT)
    private static record GlyphInfoFilter(GlyphInfo glyphInfo, GlyphInfo glyphInfoNotFishy) {
        static final GlyphInfoFilter MISSING;

        GlyphInfoFilter(GlyphInfo glyphInfo, GlyphInfo glyphInfoNotFishy) {
            this.glyphInfo = glyphInfo;
            this.glyphInfoNotFishy = glyphInfoNotFishy;
        }

        GlyphInfo select(boolean p_243218_) {
            return p_243218_ ? this.glyphInfoNotFishy : this.glyphInfo;
        }

        public GlyphInfo glyphInfo() {
            return this.glyphInfo;
        }

        public GlyphInfo glyphInfoNotFishy() {
            return this.glyphInfoNotFishy;
        }

        static {
            MISSING = new GlyphInfoFilter(SpecialGlyphs.MISSING, SpecialGlyphs.MISSING);
        }
    }
}

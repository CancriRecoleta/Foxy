//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer.texture.atlas.sources;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.NativeImage.Format;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class Unstitcher implements SpriteSource {
    static final Logger LOGGER = LogUtils.getLogger();
    public static final Codec<Unstitcher> CODEC = RecordCodecBuilder.create((p_262047_) -> {
        return p_262047_.group(ResourceLocation.CODEC.fieldOf("resource").forGetter((p_261910_) -> {
            return p_261910_.resource;
        }), ExtraCodecs.nonEmptyList(net.minecraft.client.renderer.texture.atlas.sources.Unstitcher.Region.CODEC.listOf()).fieldOf("regions").forGetter((p_261944_) -> {
            return p_261944_.regions;
        }), Codec.DOUBLE.optionalFieldOf("divisor_x", 1.0).forGetter((p_261601_) -> {
            return p_261601_.xDivisor;
        }), Codec.DOUBLE.optionalFieldOf("divisor_y", 1.0).forGetter((p_262039_) -> {
            return p_262039_.yDivisor;
        })).apply(p_262047_, Unstitcher::new);
    });
    private final ResourceLocation resource;
    private final List<Region> regions;
    private final double xDivisor;
    private final double yDivisor;

    public Unstitcher(ResourceLocation p_261679_, List<Region> p_261974_, double p_262181_, double p_261631_) {
        this.resource = p_261679_;
        this.regions = p_261974_;
        this.xDivisor = p_262181_;
        this.yDivisor = p_261631_;
    }

    public void run(ResourceManager p_261498_, SpriteSource.Output p_261828_) {
        ResourceLocation $$2 = TEXTURE_ID_CONVERTER.idToFile(this.resource);
        Optional<Resource> $$3 = p_261498_.getResource($$2);
        if ($$3.isPresent()) {
            LazyLoadedImage $$4 = new LazyLoadedImage($$2, (Resource)$$3.get(), this.regions.size());
            Iterator var6 = this.regions.iterator();

            while(var6.hasNext()) {
                Region $$5 = (Region)var6.next();
                p_261828_.add($$5.sprite, (SpriteSource.SpriteSupplier)(new RegionInstance($$4, $$5, this.xDivisor, this.yDivisor)));
            }
        } else {
            LOGGER.warn("Missing sprite: {}", $$2);
        }

    }

    public SpriteSourceType type() {
        return SpriteSources.UNSTITCHER;
    }

    @OnlyIn(Dist.CLIENT)
    private static record Region(ResourceLocation sprite, double x, double y, double width, double height) {
        public static final Codec<Region> CODEC = RecordCodecBuilder.create((p_261521_) -> {
            return p_261521_.group(ResourceLocation.CODEC.fieldOf("sprite").forGetter(Region::sprite), Codec.DOUBLE.fieldOf("x").forGetter(Region::x), Codec.DOUBLE.fieldOf("y").forGetter(Region::y), Codec.DOUBLE.fieldOf("width").forGetter(Region::width), Codec.DOUBLE.fieldOf("height").forGetter(Region::height)).apply(p_261521_, Region::new);
        });

        private Region(ResourceLocation sprite, double x, double y, double width, double height) {
            this.sprite = sprite;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public ResourceLocation sprite() {
            return this.sprite;
        }

        public double x() {
            return this.x;
        }

        public double y() {
            return this.y;
        }

        public double width() {
            return this.width;
        }

        public double height() {
            return this.height;
        }
    }

    @OnlyIn(Dist.CLIENT)
    static class RegionInstance implements SpriteSource.SpriteSupplier {
        private final LazyLoadedImage image;
        private final Region region;
        private final double xDivisor;
        private final double yDivisor;

        RegionInstance(LazyLoadedImage p_266678_, Region p_267197_, double p_266911_, double p_266789_) {
            this.image = p_266678_;
            this.region = p_267197_;
            this.xDivisor = p_266911_;
            this.yDivisor = p_266789_;
        }

        public SpriteContents get() {
            try {
                NativeImage $$0 = this.image.get();
                double $$1 = (double)$$0.getWidth() / this.xDivisor;
                double $$2 = (double)$$0.getHeight() / this.yDivisor;
                int $$3 = Mth.floor(this.region.x * $$1);
                int $$4 = Mth.floor(this.region.y * $$2);
                int $$5 = Mth.floor(this.region.width * $$1);
                int $$6 = Mth.floor(this.region.height * $$2);
                NativeImage $$7 = new NativeImage(Format.RGBA, $$5, $$6, false);
                $$0.copyRect($$7, $$3, $$4, 0, 0, $$5, $$6, false, false);
                SpriteContents var11 = new SpriteContents(this.region.sprite, new FrameSize($$5, $$6), $$7, AnimationMetadataSection.EMPTY);
                return var11;
            } catch (Exception var15) {
                Exception $$8 = var15;
                Unstitcher.LOGGER.error("Failed to unstitch region {}", this.region.sprite, $$8);
            } finally {
                this.image.release();
            }

            return MissingTextureAtlasSprite.create();
        }

        public void discard() {
            this.image.release();
        }
    }
}

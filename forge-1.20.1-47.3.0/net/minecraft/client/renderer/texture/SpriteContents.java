//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer.texture;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.textures.ForgeTextureMetadata;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class SpriteContents implements Stitcher.Entry, AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final ResourceLocation name;
    final int width;
    final int height;
    private final NativeImage originalImage;
    public NativeImage[] byMipLevel;
    @Nullable
    final AnimatedTexture animatedTexture;
    @Nullable
    public final ForgeTextureMetadata forgeMeta;

    /** @deprecated */
    @Deprecated
    public SpriteContents(ResourceLocation p_249787_, FrameSize p_251031_, NativeImage p_252131_, AnimationMetadataSection p_250432_) {
        this(p_249787_, p_251031_, p_252131_, p_250432_, (ForgeTextureMetadata)null);
    }

    public SpriteContents(ResourceLocation p_249787_, FrameSize p_251031_, NativeImage p_252131_, AnimationMetadataSection p_250432_, @org.jetbrains.annotations.Nullable ForgeTextureMetadata forgeMeta) {
        this.name = p_249787_;
        this.width = p_251031_.width();
        this.height = p_251031_.height();
        this.animatedTexture = this.createAnimatedTexture(p_251031_, p_252131_.getWidth(), p_252131_.getHeight(), p_250432_);
        this.originalImage = p_252131_;
        this.byMipLevel = new NativeImage[]{this.originalImage};
        this.forgeMeta = forgeMeta;
    }

    public NativeImage getOriginalImage() {
        return this.originalImage;
    }

    public void increaseMipLevel(int p_248864_) {
        try {
            this.byMipLevel = MipmapGenerator.generateMipLevels(this.byMipLevel, p_248864_);
        } catch (Throwable var6) {
            Throwable throwable = var6;
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Generating mipmaps for frame");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Sprite being mipmapped");
            crashreportcategory.setDetail("First frame", () -> {
                StringBuilder stringbuilder = new StringBuilder();
                if (stringbuilder.length() > 0) {
                    stringbuilder.append(", ");
                }

                stringbuilder.append(this.originalImage.getWidth()).append("x").append(this.originalImage.getHeight());
                return stringbuilder.toString();
            });
            CrashReportCategory crashreportcategory1 = crashreport.addCategory("Frame being iterated");
            crashreportcategory1.setDetail("Sprite name", (Object)this.name);
            crashreportcategory1.setDetail("Sprite size", () -> {
                return this.width + " x " + this.height;
            });
            crashreportcategory1.setDetail("Sprite frames", () -> {
                return this.getFrameCount() + " frames";
            });
            crashreportcategory1.setDetail("Mipmap levels", (Object)p_248864_);
            throw new ReportedException(crashreport);
        }
    }

    int getFrameCount() {
        return this.animatedTexture != null ? this.animatedTexture.frames.size() : 1;
    }

    @Nullable
    private AnimatedTexture createAnimatedTexture(FrameSize p_250817_, int p_249792_, int p_252353_, AnimationMetadataSection p_250947_) {
        int i = p_249792_ / p_250817_.width();
        int j = p_252353_ / p_250817_.height();
        int k = i * j;
        List<FrameInfo> list = new ArrayList();
        p_250947_.forEachFrame((p_251291_, p_251837_) -> {
            list.add(new FrameInfo(p_251291_, p_251837_));
        });
        int i1;
        if (list.isEmpty()) {
            for(i1 = 0; i1 < k; ++i1) {
                list.add(new FrameInfo(i1, p_250947_.getDefaultFrameTime()));
            }
        } else {
            i1 = 0;
            IntSet intset = new IntOpenHashSet();

            for(Iterator<FrameInfo> iterator = list.iterator(); iterator.hasNext(); ++i1) {
                FrameInfo spritecontents$frameinfo = (FrameInfo)iterator.next();
                boolean flag = true;
                if (spritecontents$frameinfo.time <= 0) {
                    LOGGER.warn("Invalid frame duration on sprite {} frame {}: {}", new Object[]{this.name, i1, spritecontents$frameinfo.time});
                    flag = false;
                }

                if (spritecontents$frameinfo.index < 0 || spritecontents$frameinfo.index >= k) {
                    LOGGER.warn("Invalid frame index on sprite {} frame {}: {}", new Object[]{this.name, i1, spritecontents$frameinfo.index});
                    flag = false;
                }

                if (flag) {
                    intset.add(spritecontents$frameinfo.index);
                } else {
                    iterator.remove();
                }
            }

            int[] aint = IntStream.range(0, k).filter((p_251185_) -> {
                return !intset.contains(p_251185_);
            }).toArray();
            if (aint.length > 0) {
                LOGGER.warn("Unused frames in sprite {}: {}", this.name, Arrays.toString(aint));
            }
        }

        return list.size() <= 1 ? null : new AnimatedTexture(ImmutableList.copyOf(list), i, p_250947_.isInterpolatedFrames());
    }

    void upload(int p_248895_, int p_250245_, int p_250458_, int p_251337_, NativeImage[] p_248825_) {
        for(int i = 0; i < this.byMipLevel.length && this.width >> i > 0 && this.height >> i > 0; ++i) {
            p_248825_[i].upload(i, p_248895_ >> i, p_250245_ >> i, p_250458_ >> i, p_251337_ >> i, this.width >> i, this.height >> i, this.byMipLevel.length > 1, false);
        }

    }

    public int width() {
        return this.width;
    }

    public int height() {
        return this.height;
    }

    public ResourceLocation name() {
        return this.name;
    }

    public IntStream getUniqueFrames() {
        return this.animatedTexture != null ? this.animatedTexture.getUniqueFrames() : IntStream.of(1);
    }

    @Nullable
    public SpriteTicker createTicker() {
        return this.animatedTexture != null ? this.animatedTexture.createTicker() : null;
    }

    public void close() {
        NativeImage[] var1 = this.byMipLevel;
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            NativeImage nativeimage = var1[var3];
            nativeimage.close();
        }

    }

    public String toString() {
        ResourceLocation var10000 = this.name;
        return "SpriteContents{name=" + var10000 + ", frameCount=" + this.getFrameCount() + ", height=" + this.height + ", width=" + this.width + "}";
    }

    public boolean isTransparent(int p_250374_, int p_250934_, int p_249573_) {
        int i = p_250934_;
        int j = p_249573_;
        if (this.animatedTexture != null) {
            i = p_250934_ + this.animatedTexture.getFrameX(p_250374_) * this.width;
            j = p_249573_ + this.animatedTexture.getFrameY(p_250374_) * this.height;
        }

        return (this.originalImage.getPixelRGBA(i, j) >> 24 & 255) == 0;
    }

    public void uploadFirstFrame(int p_252315_, int p_248634_) {
        if (this.animatedTexture != null) {
            this.animatedTexture.uploadFirstFrame(p_252315_, p_248634_);
        } else {
            this.upload(p_252315_, p_248634_, 0, 0, this.byMipLevel);
        }

    }

    @OnlyIn(Dist.CLIENT)
    class AnimatedTexture {
        final List<FrameInfo> frames;
        private final int frameRowSize;
        private final boolean interpolateFrames;

        AnimatedTexture(List<FrameInfo> p_250968_, int p_251686_, boolean p_251832_) {
            this.frames = p_250968_;
            this.frameRowSize = p_251686_;
            this.interpolateFrames = p_251832_;
        }

        int getFrameX(int p_249475_) {
            return p_249475_ % this.frameRowSize;
        }

        int getFrameY(int p_251327_) {
            return p_251327_ / this.frameRowSize;
        }

        void uploadFrame(int p_250449_, int p_248877_, int p_249060_) {
            int i = this.getFrameX(p_249060_) * SpriteContents.this.width;
            int j = this.getFrameY(p_249060_) * SpriteContents.this.height;
            SpriteContents.this.upload(p_250449_, p_248877_, i, j, SpriteContents.this.byMipLevel);
        }

        public SpriteTicker createTicker() {
            Ticker var10000 = new Ticker;
            SpriteContents var10002 = SpriteContents.this;
            Objects.requireNonNull(var10002);
            InterpolationData var10004;
            if (this.interpolateFrames) {
                SpriteContents var10006 = SpriteContents.this;
                Objects.requireNonNull(var10006);
                var10004 = var10006.new InterpolationData();
            } else {
                var10004 = null;
            }

            var10000.<init>(this, var10004);
            return var10000;
        }

        public void uploadFirstFrame(int p_251807_, int p_248676_) {
            this.uploadFrame(p_251807_, p_248676_, ((FrameInfo)this.frames.get(0)).index);
        }

        public IntStream getUniqueFrames() {
            return this.frames.stream().mapToInt((p_249981_) -> {
                return p_249981_.index;
            }).distinct();
        }
    }

    @OnlyIn(Dist.CLIENT)
    static class FrameInfo {
        final int index;
        final int time;

        FrameInfo(int p_248909_, int p_250552_) {
            this.index = p_248909_;
            this.time = p_250552_;
        }
    }

    @OnlyIn(Dist.CLIENT)
    class Ticker implements SpriteTicker {
        int frame;
        int subFrame;
        final AnimatedTexture animationInfo;
        @Nullable
        private final InterpolationData interpolationData;

        Ticker(AnimatedTexture p_249618_, @Nullable InterpolationData p_251097_) {
            this.animationInfo = p_249618_;
            this.interpolationData = p_251097_;
        }

        public void tickAndUpload(int p_249105_, int p_249676_) {
            ++this.subFrame;
            FrameInfo spritecontents$frameinfo = (FrameInfo)this.animationInfo.frames.get(this.frame);
            if (this.subFrame >= spritecontents$frameinfo.time) {
                int i = spritecontents$frameinfo.index;
                this.frame = (this.frame + 1) % this.animationInfo.frames.size();
                this.subFrame = 0;
                int j = ((FrameInfo)this.animationInfo.frames.get(this.frame)).index;
                if (i != j) {
                    this.animationInfo.uploadFrame(p_249105_, p_249676_, j);
                }
            } else if (this.interpolationData != null) {
                if (!RenderSystem.isOnRenderThread()) {
                    RenderSystem.recordRenderCall(() -> {
                        this.interpolationData.uploadInterpolatedFrame(p_249105_, p_249676_, this);
                    });
                } else {
                    this.interpolationData.uploadInterpolatedFrame(p_249105_, p_249676_, this);
                }
            }

        }

        public void close() {
            if (this.interpolationData != null) {
                this.interpolationData.close();
            }

        }
    }

    @OnlyIn(Dist.CLIENT)
    final class InterpolationData implements AutoCloseable {
        private final NativeImage[] activeFrame;

        InterpolationData() {
            this.activeFrame = new NativeImage[SpriteContents.this.byMipLevel.length];

            for(int i = 0; i < this.activeFrame.length; ++i) {
                int j = SpriteContents.this.width >> i;
                int k = SpriteContents.this.height >> i;
                this.activeFrame[i] = new NativeImage(Math.max(1, j), Math.max(1, k), false);
            }

        }

        void uploadInterpolatedFrame(int p_250513_, int p_251644_, Ticker p_248626_) {
            AnimatedTexture spritecontents$animatedtexture = p_248626_.animationInfo;
            List<FrameInfo> list = spritecontents$animatedtexture.frames;
            FrameInfo spritecontents$frameinfo = (FrameInfo)list.get(p_248626_.frame);
            double d0 = 1.0 - (double)p_248626_.subFrame / (double)spritecontents$frameinfo.time;
            int i = spritecontents$frameinfo.index;
            int j = ((FrameInfo)list.get((p_248626_.frame + 1) % list.size())).index;
            if (i != j) {
                for(int k = 0; k < this.activeFrame.length; ++k) {
                    int l = SpriteContents.this.width >> k;
                    int i1 = SpriteContents.this.height >> k;
                    if (l >= 1 && i1 >= 1) {
                        for(int j1 = 0; j1 < i1; ++j1) {
                            for(int k1 = 0; k1 < l; ++k1) {
                                int l1 = this.getPixel(spritecontents$animatedtexture, i, k, k1, j1);
                                int i2 = this.getPixel(spritecontents$animatedtexture, j, k, k1, j1);
                                int j2 = this.mix(d0, l1 >> 16 & 255, i2 >> 16 & 255);
                                int k2 = this.mix(d0, l1 >> 8 & 255, i2 >> 8 & 255);
                                int l2 = this.mix(d0, l1 & 255, i2 & 255);
                                this.activeFrame[k].setPixelRGBA(k1, j1, l1 & -16777216 | j2 << 16 | k2 << 8 | l2);
                            }
                        }
                    }
                }

                SpriteContents.this.upload(p_250513_, p_251644_, 0, 0, this.activeFrame);
            }

        }

        private int getPixel(AnimatedTexture p_251976_, int p_250761_, int p_250049_, int p_250004_, int p_251489_) {
            return SpriteContents.this.byMipLevel[p_250049_].getPixelRGBA(p_250004_ + (p_251976_.getFrameX(p_250761_) * SpriteContents.this.width >> p_250049_), p_251489_ + (p_251976_.getFrameY(p_250761_) * SpriteContents.this.height >> p_250049_));
        }

        private int mix(double p_250974_, int p_252151_, int p_249832_) {
            return (int)(p_250974_ * (double)p_252151_ + (1.0 - p_250974_) * (double)p_249832_);
        }

        public void close() {
            NativeImage[] var1 = this.activeFrame;
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                NativeImage nativeimage = var1[var3];
                nativeimage.close();
            }

        }
    }
}

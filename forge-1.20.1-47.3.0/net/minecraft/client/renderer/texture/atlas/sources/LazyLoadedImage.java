//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer.texture.atlas.sources;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LazyLoadedImage {
    private final ResourceLocation id;
    private final Resource resource;
    private final AtomicReference<NativeImage> image = new AtomicReference();
    private final AtomicInteger referenceCount;

    public LazyLoadedImage(ResourceLocation p_267104_, Resource p_266995_, int p_266778_) {
        this.id = p_267104_;
        this.resource = p_266995_;
        this.referenceCount = new AtomicInteger(p_266778_);
    }

    public NativeImage get() throws IOException {
        NativeImage $$0 = (NativeImage)this.image.get();
        if ($$0 == null) {
            synchronized(this) {
                $$0 = (NativeImage)this.image.get();
                if ($$0 == null) {
                    try {
                        InputStream $$1 = this.resource.open();

                        try {
                            $$0 = NativeImage.read($$1);
                            this.image.set($$0);
                        } catch (Throwable var8) {
                            if ($$1 != null) {
                                try {
                                    $$1.close();
                                } catch (Throwable var7) {
                                    var8.addSuppressed(var7);
                                }
                            }

                            throw var8;
                        }

                        if ($$1 != null) {
                            $$1.close();
                        }
                    } catch (IOException var9) {
                        IOException $$2 = var9;
                        throw new IOException("Failed to load image " + this.id, $$2);
                    }
                }
            }
        }

        return $$0;
    }

    public void release() {
        int $$0 = this.referenceCount.decrementAndGet();
        if ($$0 <= 0) {
            NativeImage $$1 = (NativeImage)this.image.getAndSet((Object)null);
            if ($$1 != null) {
                $$1.close();
            }
        }

    }
}

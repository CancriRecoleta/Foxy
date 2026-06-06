//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PreloadedTexture extends SimpleTexture {
    @Nullable
    private CompletableFuture<SimpleTexture.TextureImage> future;

    public PreloadedTexture(ResourceManager p_118102_, ResourceLocation p_118103_, Executor p_118104_) {
        super(p_118103_);
        this.future = CompletableFuture.supplyAsync(() -> {
            return net.minecraft.client.renderer.texture.SimpleTexture.TextureImage.load(p_118102_, p_118103_);
        }, p_118104_);
    }

    protected SimpleTexture.TextureImage getTextureImage(ResourceManager p_118126_) {
        if (this.future != null) {
            SimpleTexture.TextureImage $$1 = (SimpleTexture.TextureImage)this.future.join();
            this.future = null;
            return $$1;
        } else {
            return net.minecraft.client.renderer.texture.SimpleTexture.TextureImage.load(p_118126_, this.location);
        }
    }

    public CompletableFuture<Void> getFuture() {
        return this.future == null ? CompletableFuture.completedFuture((Object)null) : this.future.thenApply((p_118110_) -> {
            return null;
        });
    }

    public void reset(TextureManager p_118114_, ResourceManager p_118115_, ResourceLocation p_118116_, Executor p_118117_) {
        this.future = CompletableFuture.supplyAsync(() -> {
            return net.minecraft.client.renderer.texture.SimpleTexture.TextureImage.load(p_118115_, this.location);
        }, Util.backgroundExecutor());
        this.future.thenRunAsync(() -> {
            p_118114_.register((ResourceLocation)this.location, (AbstractTexture)this);
        }, executor(p_118117_));
    }

    private static Executor executor(Executor p_118121_) {
        return (p_118124_) -> {
            p_118121_.execute(() -> {
                Objects.requireNonNull(p_118124_);
                RenderSystem.recordRenderCall(p_118124_::run);
            });
        };
    }
}

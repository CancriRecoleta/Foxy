//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.sounds;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.audio.OggAudioStream;
import com.mojang.blaze3d.audio.SoundBuffer;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import net.minecraft.Util;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SoundBufferLibrary {
    private final ResourceProvider resourceManager;
    private final Map<ResourceLocation, CompletableFuture<SoundBuffer>> cache = Maps.newHashMap();

    public SoundBufferLibrary(ResourceProvider p_248900_) {
        this.resourceManager = p_248900_;
    }

    public CompletableFuture<SoundBuffer> getCompleteBuffer(ResourceLocation p_120203_) {
        return (CompletableFuture)this.cache.computeIfAbsent(p_120203_, (p_120208_) -> {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    InputStream $$1 = this.resourceManager.open(p_120208_);

                    SoundBuffer var5;
                    try {
                        OggAudioStream $$2 = new OggAudioStream($$1);

                        try {
                            ByteBuffer $$3 = $$2.readAll();
                            var5 = new SoundBuffer($$3, $$2.getFormat());
                        } catch (Throwable var8) {
                            try {
                                $$2.close();
                            } catch (Throwable var7) {
                                var8.addSuppressed(var7);
                            }

                            throw var8;
                        }

                        $$2.close();
                    } catch (Throwable var9) {
                        if ($$1 != null) {
                            try {
                                $$1.close();
                            } catch (Throwable var6) {
                                var9.addSuppressed(var6);
                            }
                        }

                        throw var9;
                    }

                    if ($$1 != null) {
                        $$1.close();
                    }

                    return var5;
                } catch (IOException var10) {
                    IOException $$4 = var10;
                    throw new CompletionException($$4);
                }
            }, Util.backgroundExecutor());
        });
    }

    public CompletableFuture<AudioStream> getStream(ResourceLocation p_120205_, boolean p_120206_) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                InputStream $$2 = this.resourceManager.open(p_120205_);
                return (AudioStream)(p_120206_ ? new LoopingAudioStream(OggAudioStream::new, $$2) : new OggAudioStream($$2));
            } catch (IOException var4) {
                IOException $$3 = var4;
                throw new CompletionException($$3);
            }
        }, Util.backgroundExecutor());
    }

    public void clear() {
        this.cache.values().forEach((p_120201_) -> {
            p_120201_.thenAccept(SoundBuffer::discardAlBuffer);
        });
        this.cache.clear();
    }

    public CompletableFuture<?> preload(Collection<Sound> p_120199_) {
        return CompletableFuture.allOf((CompletableFuture[])p_120199_.stream().map((p_120197_) -> {
            return this.getCompleteBuffer(p_120197_.getPath());
        }).toArray((p_120195_) -> {
            return new CompletableFuture[p_120195_];
        }));
    }
}

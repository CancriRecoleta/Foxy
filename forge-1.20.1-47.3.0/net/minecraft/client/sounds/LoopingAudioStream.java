//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.sounds;

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import javax.sound.sampled.AudioFormat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LoopingAudioStream implements AudioStream {
    private final AudioStreamProvider provider;
    private AudioStream stream;
    private final BufferedInputStream bufferedInputStream;

    public LoopingAudioStream(AudioStreamProvider p_120163_, InputStream p_120164_) throws IOException {
        this.provider = p_120163_;
        this.bufferedInputStream = new BufferedInputStream(p_120164_);
        this.bufferedInputStream.mark(Integer.MAX_VALUE);
        this.stream = p_120163_.create(new NoCloseBuffer(this.bufferedInputStream));
    }

    public AudioFormat getFormat() {
        return this.stream.getFormat();
    }

    public ByteBuffer read(int p_120167_) throws IOException {
        ByteBuffer $$1 = this.stream.read(p_120167_);
        if (!$$1.hasRemaining()) {
            this.stream.close();
            this.bufferedInputStream.reset();
            this.stream = this.provider.create(new NoCloseBuffer(this.bufferedInputStream));
            $$1 = this.stream.read(p_120167_);
        }

        return $$1;
    }

    public void close() throws IOException {
        this.stream.close();
        this.bufferedInputStream.close();
    }

    @FunctionalInterface
    @OnlyIn(Dist.CLIENT)
    public interface AudioStreamProvider {
        AudioStream create(InputStream var1) throws IOException;
    }

    @OnlyIn(Dist.CLIENT)
    private static class NoCloseBuffer extends FilterInputStream {
        NoCloseBuffer(InputStream p_120172_) {
            super(p_120172_);
        }

        public void close() {
        }
    }
}

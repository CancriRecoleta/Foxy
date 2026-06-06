//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.sounds;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.audio.Channel;
import com.mojang.blaze3d.audio.Library;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChannelAccess {
    private final Set<ChannelHandle> channels = Sets.newIdentityHashSet();
    final Library library;
    final Executor executor;

    public ChannelAccess(Library p_120125_, Executor p_120126_) {
        this.library = p_120125_;
        this.executor = p_120126_;
    }

    public CompletableFuture<ChannelHandle> createHandle(Library.Pool p_120129_) {
        CompletableFuture<ChannelHandle> $$1 = new CompletableFuture();
        this.executor.execute(() -> {
            Channel $$2 = this.library.acquireChannel(p_120129_);
            if ($$2 != null) {
                ChannelHandle $$3 = new ChannelHandle($$2);
                this.channels.add($$3);
                $$1.complete($$3);
            } else {
                $$1.complete((Object)null);
            }

        });
        return $$1;
    }

    public void executeOnChannels(Consumer<Stream<Channel>> p_120138_) {
        this.executor.execute(() -> {
            p_120138_.accept(this.channels.stream().map((p_174978_) -> {
                return p_174978_.channel;
            }).filter(Objects::nonNull));
        });
    }

    public void scheduleTick() {
        this.executor.execute(() -> {
            Iterator<ChannelHandle> $$0 = this.channels.iterator();

            while($$0.hasNext()) {
                ChannelHandle $$1 = (ChannelHandle)$$0.next();
                $$1.channel.updateStream();
                if ($$1.channel.stopped()) {
                    $$1.release();
                    $$0.remove();
                }
            }

        });
    }

    public void clear() {
        this.channels.forEach(ChannelHandle::release);
        this.channels.clear();
    }

    @OnlyIn(Dist.CLIENT)
    public class ChannelHandle {
        @Nullable
        Channel channel;
        private boolean stopped;

        public boolean isStopped() {
            return this.stopped;
        }

        public ChannelHandle(Channel p_120150_) {
            this.channel = p_120150_;
        }

        public void execute(Consumer<Channel> p_120155_) {
            ChannelAccess.this.executor.execute(() -> {
                if (this.channel != null) {
                    p_120155_.accept(this.channel);
                }

            });
        }

        public void release() {
            this.stopped = true;
            ChannelAccess.this.library.releaseChannel(this.channel);
            this.channel = null;
        }
    }
}

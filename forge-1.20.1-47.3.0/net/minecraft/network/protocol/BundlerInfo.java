//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network.protocol;

import io.netty.util.AttributeKey;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.network.PacketListener;

public interface BundlerInfo {
    AttributeKey<Provider> BUNDLER_PROVIDER = AttributeKey.valueOf("bundler");
    int BUNDLE_SIZE_LIMIT = 4096;
    BundlerInfo EMPTY = new BundlerInfo() {
        public void unbundlePacket(Packet<?> p_265538_, Consumer<Packet<?>> p_265064_) {
            p_265064_.accept(p_265538_);
        }

        @Nullable
        public Bundler startPacketBundling(Packet<?> p_265749_) {
            return null;
        }
    };

    static <T extends PacketListener, P extends BundlePacket<T>> BundlerInfo createForPacket(final Class<P> p_265438_, final Function<Iterable<Packet<T>>, P> p_265627_, final BundleDelimiterPacket<T> p_265373_) {
        return new BundlerInfo() {
            public void unbundlePacket(Packet<?> p_265337_, Consumer<Packet<?>> p_265615_) {
                if (p_265337_.getClass() == p_265438_) {
                    P $$2 = (BundlePacket)p_265337_;
                    p_265615_.accept(p_265373_);
                    $$2.subPackets().forEach(p_265615_);
                    p_265615_.accept(p_265373_);
                } else {
                    p_265615_.accept(p_265337_);
                }

            }

            @Nullable
            public Bundler startPacketBundling(Packet<?> p_265097_) {
                return p_265097_ == p_265373_ ? new Bundler() {
                    private final List<Packet<T>> bundlePackets = new ArrayList();

                    @Nullable
                    public Packet<?> addPacket(Packet<?> p_265205_) {
                        if (p_265205_ == p_265373_) {
                            return (Packet)p_265627_.apply(this.bundlePackets);
                        } else {
                            Packet<T> $$1 = p_265205_;
                            if (this.bundlePackets.size() >= 4096) {
                                throw new IllegalStateException("Too many packets in a bundle");
                            } else {
                                this.bundlePackets.add($$1);
                                return null;
                            }
                        }
                    }
                } : null;
            }
        };
    }

    void unbundlePacket(Packet<?> var1, Consumer<Packet<?>> var2);

    @Nullable
    Bundler startPacketBundling(Packet<?> var1);

    public interface Provider {
        BundlerInfo getBundlerInfo(PacketFlow var1);
    }

    public interface Bundler {
        @Nullable
        Packet<?> addPacket(Packet<?> var1);
    }
}

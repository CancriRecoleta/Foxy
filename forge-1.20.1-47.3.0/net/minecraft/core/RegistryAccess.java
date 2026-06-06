//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.core;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceKey;
import org.slf4j.Logger;

public interface RegistryAccess extends HolderLookup.Provider {
    Logger LOGGER = LogUtils.getLogger();
    Frozen EMPTY = (new ImmutableRegistryAccess(Map.of())).freeze();

    <E> Optional<Registry<E>> registry(ResourceKey<? extends Registry<? extends E>> var1);

    default <T> Optional<HolderLookup.RegistryLookup<T>> lookup(ResourceKey<? extends Registry<? extends T>> p_256275_) {
        return this.registry(p_256275_).map(Registry::asLookup);
    }

    default <E> Registry<E> registryOrThrow(ResourceKey<? extends Registry<? extends E>> p_175516_) {
        return (Registry)this.registry(p_175516_).orElseThrow(() -> {
            return new IllegalStateException("Missing registry: " + p_175516_);
        });
    }

    Stream<RegistryEntry<?>> registries();

    static Frozen fromRegistryOfRegistries(final Registry<? extends Registry<?>> p_206166_) {
        return new Frozen() {
            public <T> Optional<Registry<T>> registry(ResourceKey<? extends Registry<? extends T>> p_206220_) {
                Registry<Registry<T>> $$1 = p_206166_;
                return $$1.getOptional(p_206220_);
            }

            public Stream<RegistryEntry<?>> registries() {
                return p_206166_.entrySet().stream().map(RegistryEntry::fromMapEntry);
            }

            public Frozen freeze() {
                return this;
            }
        };
    }

    default Frozen freeze() {
        class FrozenAccess extends ImmutableRegistryAccess implements Frozen {
            protected FrozenAccess(Stream<RegistryEntry<?>> p_252031_) {
                super(p_252031_);
            }
        }

        return new FrozenAccess(this.registries().map(RegistryEntry::freeze));
    }

    default Lifecycle allRegistriesLifecycle() {
        return (Lifecycle)this.registries().map((p_258181_) -> {
            return p_258181_.value.registryLifecycle();
        }).reduce(Lifecycle.stable(), Lifecycle::add);
    }

    public static record RegistryEntry<T>(ResourceKey<? extends Registry<T>> key, Registry<T> value) {
        public RegistryEntry(ResourceKey<? extends Registry<T>> key, Registry<T> value) {
            this.key = key;
            this.value = value;
        }

        private static <T, R extends Registry<? extends T>> RegistryEntry<T> fromMapEntry(Map.Entry<? extends ResourceKey<? extends Registry<?>>, R> p_206242_) {
            return fromUntyped((ResourceKey)p_206242_.getKey(), (Registry)p_206242_.getValue());
        }

        private static <T> RegistryEntry<T> fromUntyped(ResourceKey<? extends Registry<?>> p_206244_, Registry<?> p_206245_) {
            return new RegistryEntry(p_206244_, p_206245_);
        }

        private RegistryEntry<T> freeze() {
            return new RegistryEntry(this.key, this.value.freeze());
        }

        public ResourceKey<? extends Registry<T>> key() {
            return this.key;
        }

        public Registry<T> value() {
            return this.value;
        }
    }

    public static class ImmutableRegistryAccess implements RegistryAccess {
        private final Map<? extends ResourceKey<? extends Registry<?>>, ? extends Registry<?>> registries;

        public ImmutableRegistryAccess(List<? extends Registry<?>> p_248540_) {
            this.registries = (Map)p_248540_.stream().collect(Collectors.toUnmodifiableMap(Registry::key, (p_206232_) -> {
                return p_206232_;
            }));
        }

        public ImmutableRegistryAccess(Map<? extends ResourceKey<? extends Registry<?>>, ? extends Registry<?>> p_206225_) {
            this.registries = Map.copyOf(p_206225_);
        }

        public ImmutableRegistryAccess(Stream<RegistryEntry<?>> p_206227_) {
            this.registries = (Map)p_206227_.collect(ImmutableMap.toImmutableMap(RegistryEntry::key, RegistryEntry::value));
        }

        public <E> Optional<Registry<E>> registry(ResourceKey<? extends Registry<? extends E>> p_206229_) {
            return Optional.ofNullable((Registry)this.registries.get(p_206229_)).map((p_247993_) -> {
                return p_247993_;
            });
        }

        public Stream<RegistryEntry<?>> registries() {
            return this.registries.entrySet().stream().map(RegistryEntry::fromMapEntry);
        }
    }

    public interface Frozen extends RegistryAccess {
    }
}

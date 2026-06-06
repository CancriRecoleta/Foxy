//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.core;

import com.mojang.serialization.Lifecycle;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.flag.FeatureFlagSet;

public interface HolderLookup<T> extends HolderGetter<T> {
    Stream<Holder.Reference<T>> listElements();

    default Stream<ResourceKey<T>> listElementIds() {
        return this.listElements().map(Holder.Reference::key);
    }

    Stream<HolderSet.Named<T>> listTags();

    default Stream<TagKey<T>> listTagIds() {
        return this.listTags().map(HolderSet.Named::key);
    }

    default HolderLookup<T> filterElements(final Predicate<T> p_256028_) {
        return new Delegate<T>(this) {
            public Optional<Holder.Reference<T>> get(ResourceKey<T> p_255836_) {
                return this.parent.get(p_255836_).filter((p_256496_) -> {
                    return p_256028_.test(p_256496_.value());
                });
            }

            public Stream<Holder.Reference<T>> listElements() {
                return this.parent.listElements().filter((p_255794_) -> {
                    return p_256028_.test(p_255794_.value());
                });
            }
        };
    }

    public interface Provider {
        <T> Optional<RegistryLookup<T>> lookup(ResourceKey<? extends Registry<? extends T>> var1);

        default <T> RegistryLookup<T> lookupOrThrow(ResourceKey<? extends Registry<? extends T>> p_255957_) {
            return (RegistryLookup)this.lookup(p_255957_).orElseThrow(() -> {
                return new IllegalStateException("Registry " + p_255957_.location() + " not found");
            });
        }

        default HolderGetter.Provider asGetterLookup() {
            return new HolderGetter.Provider() {
                public <T> Optional<HolderGetter<T>> lookup(ResourceKey<? extends Registry<? extends T>> p_256379_) {
                    return Provider.this.lookup(p_256379_).map((p_255952_) -> {
                        return p_255952_;
                    });
                }
            };
        }

        static Provider create(Stream<RegistryLookup<?>> p_256054_) {
            final Map<ResourceKey<? extends Registry<?>>, RegistryLookup<?>> $$1 = (Map)p_256054_.collect(Collectors.toUnmodifiableMap(RegistryLookup::key, (p_256335_) -> {
                return p_256335_;
            }));
            return new Provider() {
                public <T> Optional<RegistryLookup<T>> lookup(ResourceKey<? extends Registry<? extends T>> p_255663_) {
                    return Optional.ofNullable((RegistryLookup)$$1.get(p_255663_));
                }
            };
        }
    }

    public static class Delegate<T> implements HolderLookup<T> {
        protected final HolderLookup<T> parent;

        public Delegate(HolderLookup<T> p_256052_) {
            this.parent = p_256052_;
        }

        public Optional<Holder.Reference<T>> get(ResourceKey<T> p_256195_) {
            return this.parent.get(p_256195_);
        }

        public Stream<Holder.Reference<T>> listElements() {
            return this.parent.listElements();
        }

        public Optional<HolderSet.Named<T>> get(TagKey<T> p_256388_) {
            return this.parent.get(p_256388_);
        }

        public Stream<HolderSet.Named<T>> listTags() {
            return this.parent.listTags();
        }
    }

    public interface RegistryLookup<T> extends HolderLookup<T>, HolderOwner<T> {
        ResourceKey<? extends Registry<? extends T>> key();

        Lifecycle registryLifecycle();

        default HolderLookup<T> filterFeatures(FeatureFlagSet p_249397_) {
            return (HolderLookup)(FeatureElement.FILTERED_REGISTRIES.contains(this.key()) ? this.filterElements((p_250240_) -> {
                return ((FeatureElement)p_250240_).isEnabled(p_249397_);
            }) : this);
        }

        public abstract static class Delegate<T> implements RegistryLookup<T> {
            public Delegate() {
            }

            protected abstract RegistryLookup<T> parent();

            public ResourceKey<? extends Registry<? extends T>> key() {
                return this.parent().key();
            }

            public Lifecycle registryLifecycle() {
                return this.parent().registryLifecycle();
            }

            public Optional<Holder.Reference<T>> get(ResourceKey<T> p_255619_) {
                return this.parent().get(p_255619_);
            }

            public Stream<Holder.Reference<T>> listElements() {
                return this.parent().listElements();
            }

            public Optional<HolderSet.Named<T>> get(TagKey<T> p_256245_) {
                return this.parent().get(p_256245_);
            }

            public Stream<HolderSet.Named<T>> listTags() {
                return this.parent().listTags();
            }
        }
    }
}

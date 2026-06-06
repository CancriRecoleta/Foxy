//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.core;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.mojang.serialization.Lifecycle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.common.ForgeHooks;

public class RegistrySetBuilder {
    private final List<RegistryStub<?>> entries = new ArrayList();

    public RegistrySetBuilder() {
    }

    static <T> HolderGetter<T> wrapContextLookup(final HolderLookup.RegistryLookup<T> p_255625_) {
        return new EmptyTagLookup<T>(p_255625_) {
            public Optional<Holder.Reference<T>> get(ResourceKey<T> p_255765_) {
                return p_255625_.get(p_255765_);
            }
        };
    }

    public <T> RegistrySetBuilder add(ResourceKey<? extends Registry<T>> p_256446_, Lifecycle p_256394_, RegistryBootstrap<T> p_256638_) {
        this.entries.add(new RegistryStub(p_256446_, p_256394_, p_256638_));
        return this;
    }

    public <T> RegistrySetBuilder add(ResourceKey<? extends Registry<T>> p_256261_, RegistryBootstrap<T> p_256010_) {
        return this.add(p_256261_, Lifecycle.stable(), p_256010_);
    }

    public List<? extends ResourceKey<? extends Registry<?>>> getEntryKeys() {
        return this.entries.stream().map(RegistryStub::key).toList();
    }

    private BuildState createState(RegistryAccess p_256400_) {
        BuildState registrysetbuilder$buildstate = net.minecraft.core.RegistrySetBuilder.BuildState.create(p_256400_, this.entries.stream().map(RegistryStub::key));
        this.entries.forEach((p_255629_) -> {
            p_255629_.apply(registrysetbuilder$buildstate);
        });
        return registrysetbuilder$buildstate;
    }

    public HolderLookup.Provider build(RegistryAccess p_256112_) {
        BuildState registrysetbuilder$buildstate = this.createState(p_256112_);
        Stream<HolderLookup.RegistryLookup<?>> stream = p_256112_.registries().map((p_258195_) -> {
            return p_258195_.value().asLookup();
        });
        Stream<HolderLookup.RegistryLookup<?>> stream1 = this.entries.stream().map((p_255700_) -> {
            return p_255700_.collectChanges(registrysetbuilder$buildstate).buildAsLookup();
        });
        Objects.requireNonNull(registrysetbuilder$buildstate);
        HolderLookup.Provider holderlookup$provider = Provider.create(Stream.concat(stream, stream1.peek(registrysetbuilder$buildstate::addOwner)));
        registrysetbuilder$buildstate.reportRemainingUnreferencedValues();
        registrysetbuilder$buildstate.throwOnError();
        return holderlookup$provider;
    }

    public HolderLookup.Provider buildPatch(RegistryAccess p_255676_, HolderLookup.Provider p_255900_) {
        BuildState registrysetbuilder$buildstate = this.createState(p_255676_);
        Map<ResourceKey<? extends Registry<?>>, RegistryContents<?>> map = new HashMap();
        registrysetbuilder$buildstate.collectReferencedRegistries().forEach((p_272339_) -> {
            map.put(p_272339_.key, p_272339_);
        });
        this.entries.stream().map((p_272337_) -> {
            return p_272337_.collectChanges(registrysetbuilder$buildstate);
        }).forEach((p_272341_) -> {
            map.put(p_272341_.key, p_272341_);
        });
        Stream<HolderLookup.RegistryLookup<?>> stream = p_255676_.registries().map((p_258194_) -> {
            return p_258194_.value().asLookup();
        });
        Stream var10001 = map.values().stream().map(RegistryContents::buildAsLookup);
        Objects.requireNonNull(registrysetbuilder$buildstate);
        HolderLookup.Provider holderlookup$provider = Provider.create(Stream.concat(stream, var10001.peek(registrysetbuilder$buildstate::addOwner)));
        registrysetbuilder$buildstate.fillMissingHolders(p_255900_);
        registrysetbuilder$buildstate.reportRemainingUnreferencedValues();
        registrysetbuilder$buildstate.throwOnError();
        return holderlookup$provider;
    }

    static record RegistryStub<T>(ResourceKey<? extends Registry<T>> key, Lifecycle lifecycle, RegistryBootstrap<T> bootstrap) {
        RegistryStub(ResourceKey<? extends Registry<T>> key, Lifecycle lifecycle, RegistryBootstrap<T> bootstrap) {
            this.key = key;
            this.lifecycle = lifecycle;
            this.bootstrap = bootstrap;
        }

        void apply(BuildState p_256272_) {
            this.bootstrap.run(p_256272_.bootstapContext());
        }

        public RegistryContents<T> collectChanges(BuildState p_256416_) {
            Map<ResourceKey<T>, ValueAndHolder<T>> map = new HashMap();
            Iterator<Map.Entry<ResourceKey<?>, RegisteredValue<?>>> iterator = p_256416_.registeredValues.entrySet().iterator();

            while(iterator.hasNext()) {
                Map.Entry<ResourceKey<?>, RegisteredValue<?>> entry = (Map.Entry)iterator.next();
                ResourceKey<?> resourcekey = (ResourceKey)entry.getKey();
                if (resourcekey.isFor(this.key)) {
                    RegisteredValue<T> registeredvalue = (RegisteredValue)entry.getValue();
                    Holder.Reference<T> reference = (Holder.Reference)p_256416_.lookup.holders.remove(resourcekey);
                    map.put(resourcekey, new ValueAndHolder(registeredvalue, Optional.ofNullable(reference)));
                    iterator.remove();
                }
            }

            return new RegistryContents(this.key, this.lifecycle, map);
        }

        public ResourceKey<? extends Registry<T>> key() {
            return this.key;
        }

        public Lifecycle lifecycle() {
            return this.lifecycle;
        }

        public RegistryBootstrap<T> bootstrap() {
            return this.bootstrap;
        }
    }

    @FunctionalInterface
    public interface RegistryBootstrap<T> {
        void run(BootstapContext<T> var1);
    }

    static record BuildState(CompositeOwner owner, UniversalLookup lookup, Map<ResourceLocation, HolderGetter<?>> registries, Map<ResourceKey<?>, RegisteredValue<?>> registeredValues, List<RuntimeException> errors) {
        BuildState(CompositeOwner owner, UniversalLookup lookup, Map<ResourceLocation, HolderGetter<?>> registries, Map<ResourceKey<?>, RegisteredValue<?>> registeredValues, List<RuntimeException> errors) {
            this.owner = owner;
            this.lookup = lookup;
            this.registries = registries;
            this.registeredValues = registeredValues;
            this.errors = errors;
        }

        public static BuildState create(RegistryAccess p_255995_, Stream<ResourceKey<? extends Registry<?>>> p_256495_) {
            CompositeOwner registrysetbuilder$compositeowner = new CompositeOwner();
            List<RuntimeException> list = new ArrayList();
            UniversalLookup registrysetbuilder$universallookup = new UniversalLookup(registrysetbuilder$compositeowner);
            ImmutableMap.Builder<ResourceLocation, HolderGetter<?>> builder = ImmutableMap.builder();
            p_255995_.registries().forEach((p_258197_) -> {
                builder.put(p_258197_.key().location(), ForgeHooks.wrapRegistryLookup(p_258197_.value().asLookup()));
            });
            p_256495_.forEach((p_256603_) -> {
                builder.put(p_256603_.location(), registrysetbuilder$universallookup);
            });
            return new BuildState(registrysetbuilder$compositeowner, registrysetbuilder$universallookup, builder.build(), new HashMap(), list);
        }

        public <T> BootstapContext<T> bootstapContext() {
            return new BootstapContext<T>() {
                public Holder.Reference<T> register(ResourceKey<T> p_256176_, T p_256422_, Lifecycle p_255924_) {
                    RegisteredValue<?> registeredvalue = (RegisteredValue)BuildState.this.registeredValues.put(p_256176_, new RegisteredValue(p_256422_, p_255924_));
                    if (registeredvalue != null) {
                        BuildState.this.errors.add(new IllegalStateException("Duplicate registration for " + p_256176_ + ", new=" + p_256422_ + ", old=" + registeredvalue.value));
                    }

                    return BuildState.this.lookup.getOrCreate(p_256176_);
                }

                public <S> HolderGetter<S> lookup(ResourceKey<? extends Registry<? extends S>> p_255961_) {
                    return (HolderGetter)BuildState.this.registries.getOrDefault(p_255961_.location(), BuildState.this.lookup);
                }

                public <S> Optional<HolderLookup.RegistryLookup<S>> registryLookup(ResourceKey<? extends Registry<? extends S>> registry) {
                    return Optional.ofNullable((HolderLookup.RegistryLookup)BuildState.this.registries.get(registry.location()));
                }
            };
        }

        public void reportRemainingUnreferencedValues() {
            Iterator var1 = this.lookup.holders.keySet().iterator();

            while(var1.hasNext()) {
                ResourceKey<Object> resourcekey = (ResourceKey)var1.next();
                this.errors.add(new IllegalStateException("Unreferenced key: " + resourcekey));
            }

            this.registeredValues.forEach((p_256143_, p_256662_) -> {
                this.errors.add(new IllegalStateException("Orpaned value " + p_256662_.value + " for key " + p_256143_));
            });
        }

        public void throwOnError() {
            if (!this.errors.isEmpty()) {
                IllegalStateException illegalstateexception = new IllegalStateException("Errors during registry creation");
                Iterator var2 = this.errors.iterator();

                while(var2.hasNext()) {
                    RuntimeException runtimeexception = (RuntimeException)var2.next();
                    illegalstateexception.addSuppressed(runtimeexception);
                }

                throw illegalstateexception;
            }
        }

        public void addOwner(HolderOwner<?> p_256407_) {
            this.owner.add(p_256407_);
        }

        public void fillMissingHolders(HolderLookup.Provider p_255679_) {
            Map<ResourceLocation, Optional<? extends HolderLookup<Object>>> map = new HashMap();
            Iterator<Map.Entry<ResourceKey<Object>, Holder.Reference<Object>>> iterator = this.lookup.holders.entrySet().iterator();

            while(iterator.hasNext()) {
                Map.Entry<ResourceKey<Object>, Holder.Reference<Object>> entry = (Map.Entry)iterator.next();
                ResourceKey<Object> resourcekey = (ResourceKey)entry.getKey();
                Holder.Reference<Object> reference = (Holder.Reference)entry.getValue();
                ((Optional)map.computeIfAbsent(resourcekey.registry(), (p_255896_) -> {
                    return p_255679_.lookup(ResourceKey.createRegistryKey(p_255896_));
                })).flatMap((p_256068_) -> {
                    return p_256068_.get(resourcekey);
                }).ifPresent((p_256030_) -> {
                    reference.bindValue(p_256030_.value());
                    iterator.remove();
                });
            }

        }

        public Stream<RegistryContents<?>> collectReferencedRegistries() {
            return this.lookup.holders.keySet().stream().map(ResourceKey::registry).distinct().map((p_272342_) -> {
                return new RegistryContents(ResourceKey.createRegistryKey(p_272342_), Lifecycle.stable(), Map.of());
            });
        }

        public CompositeOwner owner() {
            return this.owner;
        }

        public UniversalLookup lookup() {
            return this.lookup;
        }

        public Map<ResourceLocation, HolderGetter<?>> registries() {
            return this.registries;
        }

        public Map<ResourceKey<?>, RegisteredValue<?>> registeredValues() {
            return this.registeredValues;
        }

        public List<RuntimeException> errors() {
            return this.errors;
        }
    }

    static record RegistryContents<T>(ResourceKey<? extends Registry<? extends T>> key, Lifecycle lifecycle, Map<ResourceKey<T>, ValueAndHolder<T>> values) {
        RegistryContents(ResourceKey<? extends Registry<? extends T>> key, Lifecycle lifecycle, Map<ResourceKey<T>, ValueAndHolder<T>> values) {
            this.key = key;
            this.lifecycle = lifecycle;
            this.values = values;
        }

        public HolderLookup.RegistryLookup<T> buildAsLookup() {
            return new HolderLookup.RegistryLookup<T>() {
                private final Map<ResourceKey<T>, Holder.Reference<T>> entries;

                {
                    this.entries = (Map)RegistryContents.this.values.entrySet().stream().collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, (p_256193_) -> {
                        ValueAndHolder<T> valueandholder = (ValueAndHolder)p_256193_.getValue();
                        Holder.Reference<T> reference = (Holder.Reference)valueandholder.holder().orElseGet(() -> {
                            return Reference.createStandAlone(this, (ResourceKey)p_256193_.getKey());
                        });
                        reference.bindValue(valueandholder.value().value());
                        return reference;
                    }));
                }

                public ResourceKey<? extends Registry<? extends T>> key() {
                    return RegistryContents.this.key;
                }

                public Lifecycle registryLifecycle() {
                    return RegistryContents.this.lifecycle;
                }

                public Optional<Holder.Reference<T>> get(ResourceKey<T> p_255760_) {
                    return Optional.ofNullable((Holder.Reference)this.entries.get(p_255760_));
                }

                public Stream<Holder.Reference<T>> listElements() {
                    return this.entries.values().stream();
                }

                public Optional<HolderSet.Named<T>> get(TagKey<T> p_255810_) {
                    return Optional.empty();
                }

                public Stream<HolderSet.Named<T>> listTags() {
                    return Stream.empty();
                }
            };
        }

        public ResourceKey<? extends Registry<? extends T>> key() {
            return this.key;
        }

        public Lifecycle lifecycle() {
            return this.lifecycle;
        }

        public Map<ResourceKey<T>, ValueAndHolder<T>> values() {
            return this.values;
        }
    }

    static record ValueAndHolder<T>(RegisteredValue<T> value, Optional<Holder.Reference<T>> holder) {
        ValueAndHolder(RegisteredValue<T> value, Optional<Holder.Reference<T>> holder) {
            this.value = value;
            this.holder = holder;
        }

        public RegisteredValue<T> value() {
            return this.value;
        }

        public Optional<Holder.Reference<T>> holder() {
            return this.holder;
        }
    }

    static class UniversalLookup extends EmptyTagLookup<Object> {
        final Map<ResourceKey<Object>, Holder.Reference<Object>> holders = new HashMap();

        public UniversalLookup(HolderOwner<Object> p_256629_) {
            super(p_256629_);
        }

        public Optional<Holder.Reference<Object>> get(ResourceKey<Object> p_256303_) {
            return Optional.of(this.getOrCreate(p_256303_));
        }

        <T> Holder.Reference<T> getOrCreate(ResourceKey<T> p_256298_) {
            return (Holder.Reference)this.holders.computeIfAbsent(p_256298_, (p_256154_) -> {
                return Reference.createStandAlone(this.owner, p_256154_);
            });
        }
    }

    static record RegisteredValue<T>(T value, Lifecycle lifecycle) {
        RegisteredValue(T value, Lifecycle lifecycle) {
            this.value = value;
            this.lifecycle = lifecycle;
        }

        public T value() {
            return this.value;
        }

        public Lifecycle lifecycle() {
            return this.lifecycle;
        }
    }

    abstract static class EmptyTagLookup<T> implements HolderGetter<T> {
        protected final HolderOwner<T> owner;

        protected EmptyTagLookup(HolderOwner<T> p_256166_) {
            this.owner = p_256166_;
        }

        public Optional<HolderSet.Named<T>> get(TagKey<T> p_256664_) {
            return Optional.of(HolderSet.emptyNamed(this.owner, p_256664_));
        }
    }

    static class CompositeOwner implements HolderOwner<Object> {
        private final Set<HolderOwner<?>> owners = Sets.newIdentityHashSet();

        CompositeOwner() {
        }

        public boolean canSerializeIn(HolderOwner<Object> p_256333_) {
            return this.owners.contains(p_256333_);
        }

        public void add(HolderOwner<?> p_256361_) {
            this.owners.add(p_256361_);
        }
    }
}

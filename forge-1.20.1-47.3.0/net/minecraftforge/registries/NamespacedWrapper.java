//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.registries;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.Holder.Reference.Type;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

class NamespacedWrapper<T> extends MappedRegistry<T> implements ILockableRegistry {
    static final Logger LOGGER = LogUtils.getLogger();
    private final ForgeRegistry<T> delegate;
    private final @Nullable Function<T, Holder.Reference<T>> intrusiveHolderCallback;
    private final Multimap<TagKey<T>, Supplier<T>> optionalTags = Multimaps.newSetMultimap(new IdentityHashMap(), HashSet::new);
    boolean locked = false;
    Lifecycle registryLifecycle = Lifecycle.stable();
    private boolean frozen = false;
    private List<Holder.Reference<T>> holdersSorted;
    private ObjectList<Holder.Reference<T>> holdersById = new ObjectArrayList(256);
    private Map<ResourceLocation, Holder.Reference<T>> holdersByName = new HashMap();
    private Map<T, Holder.Reference<T>> holders = new IdentityHashMap();
    private RegistryManager stage;
    private volatile Map<TagKey<T>, HolderSet.Named<T>> tags = new IdentityHashMap();

    NamespacedWrapper(ForgeRegistry<T> fowner, Function<T, Holder.Reference<T>> intrusiveHolderCallback, RegistryManager stage) {
        super(fowner.getRegistryKey(), Lifecycle.stable(), intrusiveHolderCallback != null);
        this.delegate = fowner;
        this.intrusiveHolderCallback = intrusiveHolderCallback;
        this.stage = stage;
    }

    public Holder.Reference<T> registerMapping(int id, ResourceKey<T> key, T value, Lifecycle lifecycle) {
        if (this.locked) {
            throw new IllegalStateException("Can not register to a locked registry. Modder should use Forge Register methods.");
        } else {
            Validate.notNull(value);
            this.markKnown();
            this.registryLifecycle = this.registryLifecycle.add(lifecycle);
            int realId = this.delegate.add(id, key.location(), value);
            if (realId != id && id != -1) {
                LOGGER.warn("Registered object did not get ID it asked for. Name: {} Expected: {} Got: {}", new Object[]{key, id, realId});
            }

            return this.getHolder(key, value);
        }
    }

    public Holder.Reference<T> register(ResourceKey<T> key, T value, Lifecycle lifecycle) {
        return this.registerMapping(-1, key, value, lifecycle);
    }

    public @Nullable T get(@Nullable ResourceLocation name) {
        return this.delegate.getRaw(name);
    }

    public Optional<T> getOptional(@Nullable ResourceLocation name) {
        return Optional.ofNullable(this.delegate.getRaw(name));
    }

    public @Nullable T get(@Nullable ResourceKey<T> name) {
        return name == null ? null : this.delegate.getRaw(name.location());
    }

    public @Nullable ResourceLocation getKey(T value) {
        return this.delegate.getKey(value);
    }

    public Optional<ResourceKey<T>> getResourceKey(T p_122755_) {
        return this.delegate.getResourceKey(p_122755_);
    }

    public boolean containsKey(ResourceLocation key) {
        return this.delegate.containsKey(key);
    }

    public boolean containsKey(ResourceKey<T> key) {
        return this.delegate.getRegistryName().equals(key.registry()) && this.containsKey(key.location());
    }

    public int getId(@Nullable T value) {
        return this.delegate.getID(value);
    }

    public @Nullable T byId(int id) {
        return this.delegate.getValue(id);
    }

    public Lifecycle lifecycle(T value) {
        return Lifecycle.stable();
    }

    public Lifecycle registryLifecycle() {
        return this.registryLifecycle;
    }

    public Iterator<T> iterator() {
        return this.delegate.iterator();
    }

    public Set<ResourceLocation> keySet() {
        return this.delegate.getKeys();
    }

    public Set<ResourceKey<T>> registryKeySet() {
        return this.delegate.getResourceKeys();
    }

    public Set<Map.Entry<ResourceKey<T>, T>> entrySet() {
        return this.delegate.getEntries();
    }

    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    public int size() {
        return this.delegate.size();
    }

    /** @deprecated */
    @Deprecated
    public void lock() {
        this.locked = true;
    }

    public Optional<Holder.Reference<T>> getHolder(int id) {
        return id >= 0 && id < this.holdersById.size() ? Optional.ofNullable((Holder.Reference)this.holdersById.get(id)) : Optional.empty();
    }

    public Optional<Holder.Reference<T>> getHolder(ResourceKey<T> key) {
        return Optional.ofNullable((Holder.Reference)this.holdersByName.get(key.location()));
    }

    public @NotNull Holder<T> wrapAsHolder(@NotNull T value) {
        Holder<T> holder = (Holder)this.holders.get(value);
        return holder == null ? Holder.direct(value) : holder;
    }

    Optional<Holder<T>> getHolder(ResourceLocation location) {
        return Optional.ofNullable((Holder)this.holdersByName.get(location));
    }

    Optional<Holder<T>> getHolder(T value) {
        return Optional.ofNullable((Holder)this.holders.get(value));
    }

    public HolderGetter<T> createRegistrationLookup() {
        this.validateWrite();
        return new HolderGetter<T>() {
            public Optional<Holder.Reference<T>> get(ResourceKey<T> p_259097_) {
                return Optional.of(this.getOrThrow(p_259097_));
            }

            public Holder.Reference<T> getOrThrow(ResourceKey<T> p_259750_) {
                return NamespacedWrapper.this.getOrCreateHolderOrThrow(p_259750_);
            }

            public Optional<HolderSet.Named<T>> get(TagKey<T> p_259486_) {
                return Optional.of(this.getOrThrow(p_259486_));
            }

            public HolderSet.Named<T> getOrThrow(TagKey<T> p_260298_) {
                return NamespacedWrapper.this.getOrCreateTag(p_260298_);
            }
        };
    }

    void validateWrite() {
        if (this.frozen) {
            throw new IllegalStateException("Registry is already frozen");
        }
    }

    void validateWrite(ResourceKey<T> key) {
        if (this.frozen) {
            throw new IllegalStateException("Registry is already frozen (trying to add key " + key + ")");
        }
    }

    Holder.Reference<T> getOrCreateHolderOrThrow(ResourceKey<T> key) {
        return (Holder.Reference)this.holdersByName.computeIfAbsent(key.location(), (k) -> {
            if (this.intrusiveHolderCallback != null) {
                throw new IllegalStateException("This registry can't create new holders without value");
            } else {
                this.validateWrite(key);
                return Reference.createStandAlone(this.holderOwner(), key);
            }
        });
    }

    public Optional<Holder.Reference<T>> getRandom(RandomSource rand) {
        return Util.getRandomSafe(this.getSortedHolders(), rand);
    }

    public Stream<Holder.Reference<T>> holders() {
        return this.getSortedHolders().stream();
    }

    public Stream<Pair<TagKey<T>, HolderSet.Named<T>>> getTags() {
        return this.tags.entrySet().stream().map((e) -> {
            return Pair.of((TagKey)e.getKey(), (HolderSet.Named)e.getValue());
        });
    }

    public HolderSet.Named<T> getOrCreateTag(TagKey<T> name) {
        HolderSet.Named<T> named = (HolderSet.Named)this.tags.get(name);
        if (named == null) {
            named = this.createTag(name);
            Map<TagKey<T>, HolderSet.Named<T>> map = new IdentityHashMap(this.tags);
            map.put(name, named);
            this.tags = map;
        }

        return named;
    }

    void addOptionalTag(TagKey<T> name, @NotNull Set<? extends Supplier<T>> defaults) {
        this.optionalTags.putAll(name, defaults);
    }

    public Stream<TagKey<T>> getTagNames() {
        return this.tags.keySet().stream();
    }

    public Registry<T> freeze() {
        this.frozen = true;
        List<ResourceLocation> unregistered = this.holdersByName.entrySet().stream().filter((e) -> {
            return !((Holder.Reference)e.getValue()).isBound();
        }).map(Map.Entry::getKey).sorted().toList();
        if (!unregistered.isEmpty()) {
            ResourceKey var2 = this.key();
            throw new IllegalStateException("Unbound values in registry " + var2 + ": " + (String)unregistered.stream().map(ResourceLocation::toString).collect(Collectors.joining(", \n\t")));
        } else if (this.unregisteredIntrusiveHolders != null && this.unregisteredIntrusiveHolders.values().stream().anyMatch((r) -> {
            return !r.isBound() && r.getType() == Type.INTRUSIVE;
        })) {
            Collection var10002 = this.unregisteredIntrusiveHolders.values();
            throw new IllegalStateException("Some intrusive holders were not registered: " + var10002 + " Hint: Did you register all your registry objects? Registry stage: " + this.stage.getName());
        } else {
            return this;
        }
    }

    public Holder.Reference<T> createIntrusiveHolder(T value) {
        if (this.intrusiveHolderCallback == null) {
            throw new IllegalStateException("This registry can't create intrusive holders");
        } else {
            this.validateWrite();
            return super.createIntrusiveHolder(value);
        }
    }

    public Optional<HolderSet.Named<T>> getTag(TagKey<T> name) {
        return Optional.ofNullable((HolderSet.Named)this.tags.get(name));
    }

    public void bindTags(Map<TagKey<T>, List<Holder<T>>> newTags) {
        Map<Holder.Reference<T>, List<TagKey<T>>> holderToTag = new IdentityHashMap();
        this.holdersByName.values().forEach((v) -> {
            holderToTag.put(v, new ArrayList());
        });
        newTags.forEach((name, values) -> {
            values.forEach((holder) -> {
                this.addTagToHolder(holderToTag, name, holder);
            });
        });
        Set<TagKey<T>> set = new HashSet(Sets.difference(this.tags.keySet(), newTags.keySet()));
        set.removeAll(this.optionalTags.keySet());
        if (!set.isEmpty()) {
            LOGGER.warn("Not all defined tags for registry {} are present in data pack: {}", this.key(), set.stream().map((k) -> {
                return k.location().toString();
            }).sorted().collect(Collectors.joining(", \n\t")));
        }

        Map<TagKey<T>, HolderSet.Named<T>> tmpTags = new IdentityHashMap(this.tags);
        newTags.forEach((k, v) -> {
            ((HolderSet.Named)tmpTags.computeIfAbsent(k, this::createTag)).bind(v);
        });
        Set<TagKey<T>> defaultedTags = Sets.difference(this.optionalTags.keySet(), newTags.keySet());
        defaultedTags.forEach((name) -> {
            List<Holder<T>> defaults = this.optionalTags.get(name).stream().map((valueSupplier) -> {
                return (Holder)this.getHolder(valueSupplier.get()).orElse((Object)null);
            }).filter(Objects::nonNull).distinct().toList();
            defaults.forEach((holder) -> {
                this.addTagToHolder(holderToTag, name, holder);
            });
            ((HolderSet.Named)tmpTags.computeIfAbsent(name, this::createTag)).bind(defaults);
        });
        holderToTag.forEach(Holder.Reference::bindTags);
        this.tags = tmpTags;
        this.delegate.onBindTags(this.tags, defaultedTags);
    }

    private void addTagToHolder(Map<Holder.Reference<T>, List<TagKey<T>>> holderToTag, TagKey<T> name, Holder<T> holder) {
        if (!holder.canSerializeIn(this.holderOwner())) {
            throw new IllegalStateException("Can't create named set " + name + " containing value " + holder + " from outside registry " + this);
        } else if (!(holder instanceof Holder.Reference)) {
            throw new IllegalStateException("Found direct holder " + holder + " value in tag " + name);
        } else {
            ((List)holderToTag.get((Holder.Reference)holder)).add(name);
        }
    }

    public void resetTags() {
        this.tags.values().forEach((t) -> {
            t.bind(List.of());
        });
        this.holders.values().forEach((v) -> {
            v.bindTags(Set.of());
        });
    }

    public void unfreeze() {
        this.frozen = false;
    }

    boolean isFrozen() {
        return this.frozen;
    }

    boolean isIntrusive() {
        return this.intrusiveHolderCallback != null;
    }

    @Nullable
    Holder.@Nullable Reference<T> onAdded(RegistryManager stage, int id, ResourceKey<T> key, T newValue, T oldValue) {
        if (stage == RegistryManager.ACTIVE || this.intrusiveHolderCallback != null && stage.isStaging()) {
            Holder.Reference<T> newHolder = this.getHolder(key, newValue);
            this.holdersById.size(Math.max(this.holdersById.size(), id + 1));
            this.holdersById.set(id, newHolder);
            this.holdersByName.put(key.location(), newHolder);
            this.holders.put(newValue, newHolder);
            if (this.unregisteredIntrusiveHolders != null) {
                this.unregisteredIntrusiveHolders.remove(newValue);
                newHolder.bindKey(key);
            }

            newHolder.bindValue(newValue);
            this.holdersSorted = null;
            return newHolder;
        } else {
            return null;
        }
    }

    private HolderSet.Named<T> createTag(TagKey<T> name) {
        return HolderSet.emptyNamed(this.holderOwner(), name);
    }

    private Holder.Reference<T> getHolder(ResourceKey<T> key, T value) {
        return this.intrusiveHolderCallback != null ? (Holder.Reference)this.intrusiveHolderCallback.apply(value) : (Holder.Reference)this.holdersByName.computeIfAbsent(key.location(), (k) -> {
            return Reference.createStandAlone(this.holderOwner(), key);
        });
    }

    private List<Holder.Reference<T>> getSortedHolders() {
        if (this.holdersSorted == null) {
            this.holdersSorted = this.holdersById.stream().filter(Objects::nonNull).toList();
        }

        return this.holdersSorted;
    }

    public static class Factory<V> implements IForgeRegistry.CreateCallback<V>, IForgeRegistry.AddCallback<V> {
        public static final ResourceLocation ID = new ResourceLocation("forge", "registry_defaulted_wrapper");

        public Factory() {
        }

        public void onCreate(IForgeRegistryInternal<V> owner, RegistryManager stage) {
            ForgeRegistry<V> fowner = (ForgeRegistry)owner;
            owner.setSlaveMap(ID, new NamespacedWrapper(fowner, fowner.getBuilder().getIntrusiveHolderCallback(), stage));
        }

        public void onAdd(IForgeRegistryInternal<V> owner, RegistryManager stage, int id, ResourceKey<V> key, V value, V oldValue) {
            ((NamespacedWrapper)owner.getSlaveMap(ID, NamespacedWrapper.class)).onAdded(stage, id, key, value, oldValue);
        }
    }
}

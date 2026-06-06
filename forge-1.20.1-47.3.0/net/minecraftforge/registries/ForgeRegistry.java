//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.registries;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntRBTreeMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.Holder.Reference;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.common.util.LogMessageAdapter;
import net.minecraftforge.common.util.TablePrinter;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.registries.MissingMappingsEvent.Action;
import net.minecraftforge.registries.NamespacedDefaultedWrapper.Factory;
import net.minecraftforge.registries.tags.ITagManager;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public class ForgeRegistry<V> implements IForgeRegistryInternal<V>, IForgeRegistryModifiable<V> {
    public static final Marker REGISTRIES = MarkerManager.getMarker("REGISTRIES");
    private static final Marker REGISTRYDUMP = MarkerManager.getMarker("REGISTRYDUMP");
    private static final Logger LOGGER = LogManager.getLogger();
    private final RegistryManager stage;
    private final BiMap<Integer, V> ids = HashBiMap.create();
    private final BiMap<ResourceLocation, V> names = HashBiMap.create();
    private final BiMap<ResourceKey<V>, V> keys = HashBiMap.create();
    private final Map<ResourceLocation, ResourceLocation> aliases = new HashMap();
    final Map<ResourceLocation, ?> slaves = new HashMap();
    private final ResourceLocation defaultKey;
    private final IForgeRegistry.CreateCallback<V> create;
    private final IForgeRegistry.AddCallback<V> add;
    private final IForgeRegistry.ClearCallback<V> clear;
    private final IForgeRegistry.ValidateCallback<V> validate;
    private final IForgeRegistry.BakeCallback<V> bake;
    private final IForgeRegistry.MissingFactory<V> missing;
    private final BitSet availabilityMap;
    private final IntSet blocked = new IntOpenHashSet();
    private final Multimap<ResourceLocation, V> overrides = ArrayListMultimap.create();
    private final Map<ResourceLocation, Holder.Reference<V>> delegatesByName = new HashMap();
    private final Map<V, Holder.Reference<V>> delegatesByValue = new HashMap();
    private final BiMap<OverrideOwner<V>, V> owners = HashBiMap.create();
    private final ForgeRegistryTagManager<V> tagManager;
    private final int min;
    private final int max;
    private final boolean allowOverrides;
    private final boolean isModifiable;
    private final boolean hasWrapper;
    private V defaultValue = null;
    boolean isFrozen = false;
    private final ResourceLocation name;
    private final ResourceKey<Registry<V>> key;
    private final RegistryBuilder<V> builder;
    private final Codec<V> codec = new RegistryCodec();

    ForgeRegistry(RegistryManager stage, ResourceLocation name, RegistryBuilder<V> builder) {
        this.name = name;
        this.key = ResourceKey.createRegistryKey(name);
        this.builder = builder;
        this.stage = stage;
        this.defaultKey = builder.getDefault();
        this.min = builder.getMinId();
        this.max = builder.getMaxId();
        this.availabilityMap = new BitSet(Math.min(this.max + 1, 4095));
        this.create = builder.getCreate();
        this.add = builder.getAdd();
        this.clear = builder.getClear();
        this.validate = builder.getValidate();
        this.bake = builder.getBake();
        this.missing = builder.getMissingFactory();
        this.allowOverrides = builder.getAllowOverrides();
        this.isModifiable = builder.getAllowModifications();
        this.hasWrapper = builder.getHasWrapper();
        this.tagManager = this.hasWrapper ? new ForgeRegistryTagManager(this) : null;
        if (this.create != null) {
            this.create.onCreate(this, stage);
        }

    }

    public void register(String key, V value) {
        this.register(GameData.checkPrefix(key, true), value);
    }

    public void register(ResourceLocation key, V value) {
        this.add(-1, key, value);
    }

    public Iterator<V> iterator() {
        return new Iterator<V>() {
            int cur = -1;
            V next = null;

            {
                this.next();
            }

            public boolean hasNext() {
                return this.next != null;
            }

            public V next() {
                V ret = this.next;

                do {
                    this.cur = ForgeRegistry.this.availabilityMap.nextSetBit(this.cur + 1);
                    this.next = ForgeRegistry.this.ids.get(this.cur);
                } while(this.next == null && this.cur != -1);

                return ret;
            }
        };
    }

    public ResourceLocation getRegistryName() {
        return this.name;
    }

    public ResourceKey<Registry<V>> getRegistryKey() {
        return this.key;
    }

    public @NotNull Codec<V> getCodec() {
        return this.codec;
    }

    public boolean containsKey(ResourceLocation key) {
        while(key != null) {
            if (this.names.containsKey(key)) {
                return true;
            }

            key = (ResourceLocation)this.aliases.get(key);
        }

        return false;
    }

    public boolean containsValue(V value) {
        return this.names.containsValue(value);
    }

    public boolean isEmpty() {
        return this.names.isEmpty();
    }

    int size() {
        return this.names.size();
    }

    public V getValue(ResourceLocation key) {
        V ret = this.names.get(key);

        for(key = (ResourceLocation)this.aliases.get(key); ret == null && key != null; key = (ResourceLocation)this.aliases.get(key)) {
            ret = this.names.get(key);
        }

        return ret == null ? this.defaultValue : ret;
    }

    public ResourceLocation getKey(V value) {
        return (ResourceLocation)this.getResourceKey(value).map(ResourceKey::location).orElse(this.defaultKey);
    }

    public @NotNull Optional<ResourceKey<V>> getResourceKey(V value) {
        return Optional.ofNullable((OverrideOwner)this.owners.inverse().get(value)).map(OverrideOwner::key);
    }

    @Nullable NamespacedWrapper<V> getWrapper() {
        if (!this.hasWrapper) {
            return null;
        } else {
            return this.defaultKey != null ? (NamespacedWrapper)this.getSlaveMap(Factory.ID, NamespacedDefaultedWrapper.class) : (NamespacedWrapper)this.getSlaveMap(net.minecraftforge.registries.NamespacedWrapper.Factory.ID, NamespacedWrapper.class);
        }
    }

    @NotNull NamespacedWrapper<V> getWrapperOrThrow() {
        NamespacedWrapper<V> wrapper = this.getWrapper();
        return wrapper;
    }

    void onBindTags(Map<TagKey<V>, HolderSet.Named<V>> tags, Set<TagKey<V>> defaultedTags) {
        if (this.tagManager != null) {
            this.tagManager.bind(tags, defaultedTags);
        }

    }

    public @NotNull Optional<Holder<V>> getHolder(ResourceKey<V> key) {
        return Optional.ofNullable(this.getWrapper()).flatMap((wrapper) -> {
            return wrapper.getHolder(key);
        });
    }

    public @NotNull Optional<Holder<V>> getHolder(ResourceLocation location) {
        return Optional.ofNullable(this.getWrapper()).flatMap((wrapper) -> {
            return wrapper.getHolder(location);
        });
    }

    public @NotNull Optional<Holder<V>> getHolder(V value) {
        return Optional.ofNullable(this.getWrapper()).flatMap((wrapper) -> {
            return wrapper.getHolder(value);
        });
    }

    public @Nullable ITagManager<V> tags() {
        return this.tagManager;
    }

    public @NotNull Set<ResourceLocation> getKeys() {
        return Collections.unmodifiableSet(this.names.keySet());
    }

    @NotNull Set<ResourceKey<V>> getResourceKeys() {
        return Collections.unmodifiableSet(this.keys.keySet());
    }

    public @NotNull Collection<V> getValues() {
        return Collections.unmodifiableSet(this.names.values());
    }

    public @NotNull Set<Map.Entry<ResourceKey<V>, V>> getEntries() {
        return Collections.unmodifiableSet(this.keys.entrySet());
    }

    public <T> T getSlaveMap(ResourceLocation name, Class<T> type) {
        return this.slaves.get(name);
    }

    public void setSlaveMap(ResourceLocation name, Object obj) {
        this.slaves.put(name, obj);
    }

    public int getID(V value) {
        Integer ret = (Integer)this.ids.inverse().get(value);
        if (ret == null && this.defaultValue != null) {
            ret = (Integer)this.ids.inverse().get(this.defaultValue);
        }

        return ret == null ? -1 : ret;
    }

    public int getID(ResourceLocation name) {
        return this.getID(this.names.get(name));
    }

    private int getIDRaw(V value) {
        Integer ret = (Integer)this.ids.inverse().get(value);
        return ret == null ? -1 : ret;
    }

    private int getIDRaw(ResourceLocation name) {
        return this.getIDRaw(this.names.get(name));
    }

    public V getValue(int id) {
        V ret = this.ids.get(id);
        return ret == null ? this.defaultValue : ret;
    }

    public @Nullable ResourceKey<V> getKey(int id) {
        V value = this.getValue(id);
        return (ResourceKey)this.keys.inverse().get(value);
    }

    void validateKey() {
        if (this.defaultKey != null) {
            Validate.notNull(this.defaultValue, "Missing default of ForgeRegistry: " + this.defaultKey + " Name: " + this.name, new Object[0]);
        }

    }

    public @Nullable ResourceLocation getDefaultKey() {
        return this.defaultKey;
    }

    ForgeRegistry<V> copy(RegistryManager stage) {
        return new ForgeRegistry(stage, this.name, this.builder);
    }

    public void register(int id, ResourceLocation key, V value) {
        this.add(id, key, value, key.getNamespace());
    }

    int add(int id, ResourceLocation key, V value) {
        String owner = ModLoadingContext.get().getActiveNamespace();
        return this.add(id, key, value, owner);
    }

    int add(int id, ResourceLocation key, V value, String owner) {
        Preconditions.checkNotNull(key, "Can't use a null-name for the registry, object %s.", value);
        Preconditions.checkNotNull(value, "Can't add null-object to the registry, name %s.", key);
        int idToUse = id;
        if (idToUse < 0 || this.availabilityMap.get(idToUse)) {
            idToUse = this.availabilityMap.nextClearBit(this.min);
        }

        if (idToUse > this.max) {
            throw new RuntimeException(String.format(Locale.ENGLISH, "Invalid id %d - maximum id range exceeded.", idToUse));
        } else {
            V oldEntry = this.getRaw(key);
            if (oldEntry == value) {
                LOGGER.warn(REGISTRIES, "Registry {}: The object {} has been registered twice for the same name {}.", this.name, value, key);
                return this.getID(value);
            } else {
                if (oldEntry != null) {
                    if (!this.allowOverrides) {
                        throw new IllegalArgumentException(String.format(Locale.ENGLISH, "The name %s has been registered twice, for %s and %s.", key, this.getRaw(key), value));
                    }

                    if (owner == null) {
                        throw new IllegalStateException(String.format(Locale.ENGLISH, "Could not determine owner for the override on %s. Value: %s", key, value));
                    }

                    LOGGER.debug(REGISTRIES, "Registry {} Override: {} {} -> {}", this.name, key, oldEntry, value);
                    idToUse = this.getID(oldEntry);
                }

                Integer foundId = (Integer)this.ids.inverse().get(value);
                if (foundId != null) {
                    V otherThing = this.ids.get(foundId);
                    throw new IllegalArgumentException(String.format(Locale.ENGLISH, "The object %s{%x} has been registered twice, using the names %s and %s. (Other object at this id is %s{%x})", value, System.identityHashCode(value), this.getKey(value), key, otherThing, System.identityHashCode(otherThing)));
                } else if (this.isLocked()) {
                    throw new IllegalStateException(String.format(Locale.ENGLISH, "The object %s (name %s) is being added too late.", value, key));
                } else {
                    if (this.defaultKey != null && this.defaultKey.equals(key)) {
                        if (this.defaultValue != null) {
                            throw new IllegalStateException(String.format(Locale.ENGLISH, "Attemped to override already set default value. This is not allowed: The object %s (name %s)", value, key));
                        }

                        this.defaultValue = value;
                    }

                    ResourceKey<V> rkey = ResourceKey.create(this.key, key);
                    this.names.put(key, value);
                    this.keys.put(rkey, value);
                    this.ids.put(idToUse, value);
                    this.availabilityMap.set(idToUse);
                    this.owners.put(new OverrideOwner(owner == null ? key.getNamespace() : owner, rkey), value);
                    if (this.hasWrapper) {
                        this.bindDelegate(rkey, value);
                        if (oldEntry != null) {
                            if (!this.overrides.get(key).contains(oldEntry)) {
                                this.overrides.put(key, oldEntry);
                            }

                            this.overrides.get(key).remove(value);
                        }
                    }

                    if (this.add != null) {
                        this.add.onAdd(this, this.stage, idToUse, rkey, value, oldEntry);
                    }

                    LOGGER.trace(REGISTRIES, "Registry {} add: {} {} {} (req. id {})", this.name, key, idToUse, value, id);
                    return idToUse;
                }
            }
        }
    }

    public V getRaw(ResourceLocation key) {
        V ret = this.names.get(key);

        for(key = (ResourceLocation)this.aliases.get(key); ret == null && key != null; key = (ResourceLocation)this.aliases.get(key)) {
            ret = this.names.get(key);
        }

        return ret;
    }

    public void addAlias(ResourceLocation src, ResourceLocation dst) {
        if (this.isLocked()) {
            throw new IllegalStateException(String.format(Locale.ENGLISH, "Attempted to register the alias %s -> %s too late", src, dst));
        } else if (src.equals(dst)) {
            LOGGER.warn(REGISTRIES, "Registry {} Ignoring invalid alias: {} -> {}", this.name, src, dst);
        } else {
            this.aliases.put(src, dst);
            LOGGER.trace(REGISTRIES, "Registry {} alias: {} -> {}", this.name, src, dst);
        }
    }

    public @NotNull Optional<Holder.Reference<V>> getDelegate(ResourceKey<V> rkey) {
        return Optional.ofNullable((Holder.Reference)this.delegatesByName.get(rkey.location()));
    }

    @NotNull
    public Holder.@NotNull Reference<V> getDelegateOrThrow(ResourceKey<V> rkey) {
        return (Holder.Reference)this.getDelegate(rkey).orElseThrow(() -> {
            return new IllegalArgumentException(String.format(Locale.ENGLISH, "No delegate exists for key %s", rkey));
        });
    }

    public @NotNull Optional<Holder.Reference<V>> getDelegate(ResourceLocation key) {
        return Optional.ofNullable((Holder.Reference)this.delegatesByName.get(key));
    }

    @NotNull
    public Holder.@NotNull Reference<V> getDelegateOrThrow(ResourceLocation key) {
        return (Holder.Reference)this.getDelegate(key).orElseThrow(() -> {
            return new IllegalArgumentException(String.format(Locale.ENGLISH, "No delegate exists for key %s", key));
        });
    }

    public @NotNull Optional<Holder.Reference<V>> getDelegate(V value) {
        return Optional.ofNullable((Holder.Reference)this.delegatesByValue.get(value));
    }

    @NotNull
    public Holder.@NotNull Reference<V> getDelegateOrThrow(V value) {
        return (Holder.Reference)this.getDelegate(value).orElseThrow(() -> {
            return new IllegalArgumentException(String.format(Locale.ENGLISH, "No delegate exists for value %s", value));
        });
    }

    private Holder.Reference<V> bindDelegate(ResourceKey<V> rkey, V value) {
        Holder.Reference<V> delegate = (Holder.Reference)this.delegatesByName.computeIfAbsent(rkey.location(), (k) -> {
            return Reference.createStandAlone(this.getWrapperOrThrow().holderOwner(), rkey);
        });
        delegate.bindKey(rkey);
        delegate.bindValue(value);
        this.delegatesByValue.put(value, delegate);
        return delegate;
    }

    void resetDelegates() {
        if (this.hasWrapper) {
            Iterator var1 = this.keys.entrySet().iterator();

            Map.Entry entry;
            while(var1.hasNext()) {
                entry = (Map.Entry)var1.next();
                this.bindDelegate((ResourceKey)entry.getKey(), entry.getValue());
            }

            var1 = this.overrides.entries().iterator();

            while(var1.hasNext()) {
                entry = (Map.Entry)var1.next();
                this.bindDelegate(ResourceKey.create(this.key, (ResourceLocation)entry.getKey()), entry.getValue());
            }

        }
    }

    V getDefault() {
        return this.defaultValue;
    }

    void validateContent(ResourceLocation registryName) {
        Iterator var2 = this.iterator();

        while(var2.hasNext()) {
            V obj = var2.next();
            int id = this.getID(obj);
            ResourceLocation name = this.getKey(obj);
            if (name == null) {
                throw new IllegalStateException(String.format(Locale.ENGLISH, "Registry entry for %s %s, id %d, doesn't yield a name.", registryName, obj, id));
            }

            if (id > this.max) {
                throw new IllegalStateException(String.format(Locale.ENGLISH, "Registry entry for %s %s, name %s uses the too large id %d.", registryName, obj, name, id));
            }

            if (this.getValue(id) != obj) {
                throw new IllegalStateException(String.format(Locale.ENGLISH, "Registry entry for id %d, name %s, doesn't yield the expected %s %s.", id, name, registryName, obj));
            }

            if (this.getValue(name) != obj) {
                throw new IllegalStateException(String.format(Locale.ENGLISH, "Registry entry for name %s, id %d, doesn't yield the expected %s %s.", name, id, registryName, obj));
            }

            if (this.getID(name) != id) {
                throw new IllegalStateException(String.format(Locale.ENGLISH, "Registry entry for name %s doesn't yield the expected id %d.", name, id));
            }

            if (this.validate != null) {
                this.validate.onValidate(this, this.stage, id, name, obj);
            }
        }

    }

    public void bake() {
        if (this.bake != null) {
            this.bake.onBake(this, this.stage);
        }

    }

    void sync(ResourceLocation name, ForgeRegistry<V> from) {
        LOGGER.debug(REGISTRIES, "Registry {} Sync: {} -> {}", this.name, this.stage.getName(), from.stage.getName());
        if (this == from) {
            throw new IllegalArgumentException("WTF We are the same!?!?!");
        } else {
            this.isFrozen = false;
            if (this.clear != null) {
                this.clear.onClear(this, this.stage);
            }

            Iterator var3 = from.aliases.entrySet().iterator();

            while(var3.hasNext()) {
                Map.Entry<ResourceLocation, ResourceLocation> entry = (Map.Entry)var3.next();
                if (!this.aliases.containsKey(entry.getKey())) {
                    this.aliases.put((ResourceLocation)entry.getKey(), (ResourceLocation)entry.getValue());
                }
            }

            this.ids.clear();
            this.names.clear();
            this.keys.clear();
            this.availabilityMap.clear(0, this.availabilityMap.length());
            this.defaultValue = null;
            this.overrides.clear();
            this.owners.clear();
            boolean errored = false;
            Iterator var13 = from.names.entrySet().iterator();

            while(true) {
                while(var13.hasNext()) {
                    Map.Entry<ResourceLocation, V> entry = (Map.Entry)var13.next();
                    List<V> overrides = new ArrayList(from.overrides.get((ResourceLocation)entry.getKey()));
                    int id = from.getID((ResourceLocation)entry.getKey());
                    if (overrides.isEmpty()) {
                        int realId = this.add(id, (ResourceLocation)entry.getKey(), entry.getValue());
                        if (id != realId && id != -1) {
                            LOGGER.warn(REGISTRIES, "Registry {}: Object did not get ID it asked for. Name: {} Expected: {} Got: {}", this.name, entry.getKey(), id, realId);
                            errored = true;
                        }
                    } else {
                        overrides.add(entry.getValue());
                        Iterator var8 = overrides.iterator();

                        while(var8.hasNext()) {
                            V value = var8.next();
                            OverrideOwner<V> owner = (OverrideOwner)from.owners.inverse().get(value);
                            if (owner == null) {
                                LOGGER.warn(REGISTRIES, "Registry {}: Override did not have an associated owner object. Name: {} Value: {}", this.name, entry.getKey(), value);
                                errored = true;
                            } else {
                                int realId = this.add(id, (ResourceLocation)entry.getKey(), value, owner.owner);
                                if (id != realId && id != -1) {
                                    LOGGER.warn(REGISTRIES, "Registry {}: Object did not get ID it asked for. Name: {} Expected: {} Got: {}", this.name, entry.getKey(), id, realId);
                                    errored = true;
                                }
                            }
                        }
                    }
                }

                if (errored) {
                    throw new RuntimeException("One of more entry values did not copy to the correct id. Check log for details!");
                }

                return;
            }
        }
    }

    public void clear() {
        if (!this.isModifiable) {
            throw new UnsupportedOperationException("Attempted to clear a non-modifiable Forge Registry");
        } else if (this.isLocked()) {
            throw new IllegalStateException("Attempted to clear the registry to late.");
        } else {
            if (this.clear != null) {
                this.clear.onClear(this, this.stage);
            }

            this.aliases.clear();
            this.ids.clear();
            this.names.clear();
            this.keys.clear();
            this.availabilityMap.clear(0, this.availabilityMap.length());
        }
    }

    public V remove(ResourceLocation key) {
        if (!this.isModifiable) {
            throw new UnsupportedOperationException("Attempted to remove from a non-modifiable Forge Registry");
        } else if (this.isLocked()) {
            throw new IllegalStateException("Attempted to remove from the registry to late.");
        } else {
            V value = this.names.remove(key);
            if (value != null) {
                ResourceKey<V> rkey = (ResourceKey)this.keys.inverse().remove(value);
                if (rkey == null) {
                    throw new IllegalStateException("Removed a entry that did not have an associated RegistryKey: " + key + " " + value.toString() + " This should never happen unless hackery!");
                }

                Integer id = (Integer)this.ids.inverse().remove(value);
                if (id == null) {
                    throw new IllegalStateException("Removed a entry that did not have an associated id: " + key + " " + value.toString() + " This should never happen unless hackery!");
                }

                LOGGER.trace(REGISTRIES, "Registry {} remove: {} {}", this.name, key, id);
            }

            return value;
        }
    }

    void block(int id) {
        this.blocked.add(id);
        this.availabilityMap.set(id);
    }

    public boolean isLocked() {
        return this.isFrozen;
    }

    public void freeze() {
        this.isFrozen = true;
    }

    public void unfreeze() {
        this.isFrozen = false;
    }

    void dump(ResourceLocation name) {
        if (LOGGER.isDebugEnabled(REGISTRYDUMP)) {
            TablePrinter<DumpRow> tab = (new TablePrinter()).header("ID", (r) -> {
                return r.id;
            }).header("Key", (r) -> {
                return r.key;
            }).header("Value", (r) -> {
                return r.value;
            });
            LOGGER.debug(REGISTRYDUMP, () -> {
                return LogMessageAdapter.adapt((sb) -> {
                    sb.append("Registry Name: ").append(name).append('\n');
                    tab.clearRows();
                    Stream var10000 = this.getKeys().stream().map(this::getID).sorted().map((id) -> {
                        V val = this.getValue(id);
                        ResourceLocation key = this.getKey(val);
                        return new DumpRow(Integer.toString(id), key.toString(), val.toString());
                    });
                    Objects.requireNonNull(tab);
                    var10000.forEach(tab::add);
                    tab.build(sb);
                });
            });
        }

    }

    public void loadIds(Object2IntMap<ResourceLocation> ids, Map<ResourceLocation, String> overrides, Object2IntMap<ResourceLocation> missing, Map<ResourceLocation, IdMappingEvent.IdRemapping> remapped, ForgeRegistry<V> old, ResourceLocation name) {
        Map<ResourceLocation, String> ovs = new HashMap(overrides);
        ObjectIterator var8 = ids.object2IntEntrySet().iterator();

        while(true) {
            ResourceLocation itemName;
            Object obj;
            while(var8.hasNext()) {
                Object2IntMap.Entry<ResourceLocation> entry = (Object2IntMap.Entry)var8.next();
                itemName = (ResourceLocation)entry.getKey();
                int newId = entry.getIntValue();
                int currId = old.getIDRaw(itemName);
                if (currId == -1) {
                    LOGGER.info(REGISTRIES, "Registry {}: Found a missing id from the world {}", this.name, itemName);
                    missing.put(itemName, newId);
                } else {
                    if (currId != newId) {
                        LOGGER.debug(REGISTRIES, "Registry {}: Fixed {} id mismatch {}: {} (init) -> {} (map).", this.name, name, itemName, currId, newId);
                        remapped.put(itemName, new IdMappingEvent.IdRemapping(currId, newId));
                    }

                    obj = old.getRaw(itemName);
                    Preconditions.checkState(obj != null, "objectKey has an ID but no object. Reflection/ASM hackery? Registry bug?");
                    List<V> lst = new ArrayList(old.overrides.get(itemName));
                    String primaryName = null;
                    if (old.overrides.containsKey(itemName)) {
                        if (!overrides.containsKey(itemName)) {
                            lst.add(obj);
                            obj = old.overrides.get(itemName).iterator().next();
                            primaryName = ((OverrideOwner)old.owners.inverse().get(obj)).owner;
                        } else {
                            primaryName = (String)overrides.get(itemName);
                        }
                    }

                    Iterator var16 = lst.iterator();

                    while(var16.hasNext()) {
                        V value = var16.next();
                        OverrideOwner<V> owner = (OverrideOwner)old.owners.inverse().get(value);
                        if (owner == null) {
                            LOGGER.warn(REGISTRIES, "Registry {}: Override did not have an associated owner object. Name: {} Value: {}", this.name, entry.getKey(), value);
                        } else if (!primaryName.equals(owner.owner)) {
                            int realId = this.add(newId, itemName, value, owner.owner);
                            if (newId != realId) {
                                LOGGER.warn(REGISTRIES, "Registry {}: Object did not get ID it asked for. Name: {} Expected: {} Got: {}", this.name, entry.getKey(), newId, realId);
                            }
                        }
                    }

                    int realId = this.add(newId, itemName, obj, primaryName == null ? itemName.getNamespace() : primaryName);
                    if (realId != newId) {
                        LOGGER.warn(REGISTRIES, "Registry {}: Object did not get ID it asked for. Name: {} Expected: {} Got: {}", this.name, entry.getKey(), newId, realId);
                    }

                    ovs.remove(itemName);
                }
            }

            Iterator var20 = ovs.entrySet().iterator();

            while(var20.hasNext()) {
                Map.Entry<ResourceLocation, String> entry = (Map.Entry)var20.next();
                itemName = (ResourceLocation)entry.getKey();
                String owner = (String)entry.getValue();
                String current = ((OverrideOwner)this.owners.inverse().get(this.getRaw(itemName))).owner;
                if (!owner.equals(current)) {
                    obj = this.owners.get(new OverrideOwner(owner, ResourceKey.create(this.key, itemName)));
                    if (obj == null) {
                        LOGGER.warn(REGISTRIES, "Registry {}: Skipping override for {}, Unknown owner {}", this.name, itemName, owner);
                    } else {
                        LOGGER.info(REGISTRIES, "Registry {}: Activating override {} for {}", this.name, owner, itemName);
                        int newId = this.getID(itemName);
                        int realId = this.add(newId, itemName, obj, owner);
                        if (newId != realId) {
                            LOGGER.warn(REGISTRIES, "Registry {}: Object did not get ID it asked for. Name: {} Expected: {} Got: {}", this.name, entry.getKey(), newId, realId);
                        }
                    }
                }
            }

            return;
        }
    }

    public Snapshot makeSnapshot() {
        Snapshot ret = new Snapshot();
        this.ids.forEach((id, value) -> {
            ret.ids.put(this.getKey(value), id);
        });
        ret.aliases.putAll(this.aliases);
        ret.blocked.addAll(this.blocked);
        ret.overrides.putAll(this.getOverrideOwners());
        return ret;
    }

    Map<ResourceLocation, String> getOverrideOwners() {
        Map<ResourceLocation, String> ret = new HashMap();

        ResourceLocation key;
        OverrideOwner owner;
        for(Iterator var2 = this.overrides.keySet().iterator(); var2.hasNext(); ret.put(key, owner.owner)) {
            key = (ResourceLocation)var2.next();
            V obj = this.names.get(key);
            owner = (OverrideOwner)this.owners.inverse().get(obj);
            if (owner == null) {
                LOGGER.debug(REGISTRIES, "Registry {} {}: Invalid override {} {}", this.name, this.stage.getName(), key, obj);
            }
        }

        return ret;
    }

    public MissingMappingsEvent getMissingEvent(ResourceLocation name, Object2IntMap<ResourceLocation> map) {
        List<MissingMappingsEvent.Mapping<V>> lst = new ArrayList();
        ForgeRegistry<V> pool = RegistryManager.ACTIVE.getRegistry(name);
        map.object2IntEntrySet().forEach((entry) -> {
            lst.add(new MissingMappingsEvent.Mapping(this, pool, (ResourceLocation)entry.getKey(), entry.getIntValue()));
        });
        return new MissingMappingsEvent(ResourceKey.createRegistryKey(name), this, lst);
    }

    void processMissingEvent(ResourceLocation name, ForgeRegistry<V> pool, List<MissingMappingsEvent.Mapping<V>> mappings, Object2IntMap<ResourceLocation> missing, Map<ResourceLocation, IdMappingEvent.IdRemapping> remaps, Collection<ResourceLocation> defaulted, Collection<ResourceLocation> failed, boolean injectNetworkDummies) {
        LOGGER.debug(REGISTRIES, "Processing missing event for {}:", name);
        int ignored = 0;
        Iterator var10 = mappings.iterator();

        while(var10.hasNext()) {
            MissingMappingsEvent.Mapping<V> remap = (MissingMappingsEvent.Mapping)var10.next();
            MissingMappingsEvent.Action action = remap.action;
            if (action == Action.REMAP) {
                int currId = this.getID(remap.target);
                ResourceLocation newName = pool.getKey(remap.target);
                LOGGER.debug(REGISTRIES, "  Remapping {} -> {}.", remap.key, newName);
                missing.removeInt(remap.key);
                int realId = this.add(remap.id, newName, remap.target);
                if (realId != remap.id) {
                    LOGGER.warn(REGISTRIES, "Registered object did not get ID it asked for. Name: {} Expected: {} Got: {}", newName, remap.id, realId);
                }

                this.addAlias(remap.key, newName);
                if (currId != realId) {
                    LOGGER.info(REGISTRIES, "Fixed id mismatch {}: {} (init) -> {} (map).", newName, currId, realId);
                    remaps.put(newName, new IdMappingEvent.IdRemapping(currId, realId));
                }
            } else {
                if (action == Action.DEFAULT) {
                    V m = this.missing == null ? null : this.missing.createMissing(remap.key, injectNetworkDummies);
                    if (m == null) {
                        defaulted.add(remap.key);
                    } else {
                        this.add(remap.id, remap.key, m, remap.key.getNamespace());
                    }
                } else if (action == Action.IGNORE) {
                    LOGGER.debug(REGISTRIES, "Ignoring {}", remap.key);
                    ++ignored;
                } else if (action == Action.FAIL) {
                    LOGGER.debug(REGISTRIES, "Failing {}!", remap.key);
                    failed.add(remap.key);
                } else if (action == Action.WARN) {
                    LOGGER.warn(REGISTRIES, "{} may cause world breakage!", remap.key);
                }

                this.block(remap.id);
            }
        }

        if (failed.isEmpty() && ignored > 0) {
            LOGGER.debug(REGISTRIES, "There were {} missing mappings that have been ignored", ignored);
        }

    }

    RegistryBuilder<V> getBuilder() {
        return this.builder;
    }

    private class RegistryCodec implements Codec<V> {
        private RegistryCodec() {
        }

        public <T> DataResult<Pair<V, T>> decode(DynamicOps<T> ops, T input) {
            return ops.compressMaps() ? ops.getNumberValue(input).flatMap((n) -> {
                int id = n.intValue();
                if (ForgeRegistry.this.ids.get(id) == null) {
                    return DataResult.error(() -> {
                        return "Unknown registry id in " + ForgeRegistry.this.key + ": " + n;
                    });
                } else {
                    V val = ForgeRegistry.this.getValue(id);
                    return DataResult.success(val);
                }
            }).map((v) -> {
                return Pair.of(v, ops.empty());
            }) : ResourceLocation.CODEC.decode(ops, input).flatMap((keyValuePair) -> {
                return !ForgeRegistry.this.containsKey((ResourceLocation)keyValuePair.getFirst()) ? DataResult.error(() -> {
                    ResourceKey var10000 = ForgeRegistry.this.key;
                    return "Unknown registry key in " + var10000 + ": " + keyValuePair.getFirst();
                }) : DataResult.success(keyValuePair.mapFirst(ForgeRegistry.this::getValue));
            });
        }

        public <T> DataResult<T> encode(V input, DynamicOps<T> ops, T prefix) {
            ResourceLocation key = ForgeRegistry.this.getKey(input);
            if (key == null) {
                return DataResult.error(() -> {
                    return "Unknown registry element in " + ForgeRegistry.this.key + ": " + input;
                });
            } else {
                T toMerge = ops.compressMaps() ? ops.createInt(ForgeRegistry.this.getID(input)) : ops.createString(key.toString());
                return ops.mergeToPrimitive(prefix, toMerge);
            }
        }
    }

    private static record OverrideOwner<V>(String owner, ResourceKey<V> key) {
        private OverrideOwner(String owner, ResourceKey<V> key) {
            this.owner = owner;
            this.key = key;
        }

        public String owner() {
            return this.owner;
        }

        public ResourceKey<V> key() {
            return this.key;
        }
    }

    public static class Snapshot {
        private static final Comparator<ResourceLocation> sorter = ResourceLocation::compareNamespaced;
        public final Object2IntMap<ResourceLocation> ids;
        public final Map<ResourceLocation, ResourceLocation> aliases;
        public final IntSet blocked;
        public final Map<ResourceLocation, String> overrides;
        private FriendlyByteBuf binary;

        public Snapshot() {
            this.ids = new Object2IntRBTreeMap(sorter);
            this.aliases = new TreeMap(sorter);
            this.blocked = new IntRBTreeSet();
            this.overrides = new TreeMap(sorter);
            this.binary = null;
        }

        public CompoundTag write() {
            CompoundTag data = new CompoundTag();
            ListTag ids = new ListTag();
            this.ids.object2IntEntrySet().forEach((e) -> {
                CompoundTag tag = new CompoundTag();
                tag.putString("K", ((ResourceLocation)e.getKey()).toString());
                tag.putInt("V", e.getIntValue());
                ids.add(tag);
            });
            data.put("ids", ids);
            ListTag aliases = new ListTag();
            this.aliases.entrySet().forEach((e) -> {
                CompoundTag tag = new CompoundTag();
                tag.putString("K", ((ResourceLocation)e.getKey()).toString());
                tag.putString("V", ((ResourceLocation)e.getValue()).toString());
                aliases.add(tag);
            });
            data.put("aliases", aliases);
            ListTag overrides = new ListTag();
            this.overrides.entrySet().forEach((e) -> {
                CompoundTag tag = new CompoundTag();
                tag.putString("K", ((ResourceLocation)e.getKey()).toString());
                tag.putString("V", (String)e.getValue());
                overrides.add(tag);
            });
            data.put("overrides", overrides);
            int[] blocked = this.blocked.intStream().sorted().toArray();
            data.putIntArray("blocked", blocked);
            return data;
        }

        public static Snapshot read(CompoundTag nbt) {
            Snapshot ret = new Snapshot();
            if (nbt == null) {
                return ret;
            } else {
                ListTag list = nbt.getList("ids", 10);
                list.forEach((e) -> {
                    CompoundTag comp = (CompoundTag)e;
                    ret.ids.put(new ResourceLocation(comp.getString("K")), comp.getInt("V"));
                });
                list = nbt.getList("aliases", 10);
                list.forEach((e) -> {
                    CompoundTag comp = (CompoundTag)e;
                    ret.aliases.put(new ResourceLocation(comp.getString("K")), new ResourceLocation(comp.getString("V")));
                });
                list = nbt.getList("overrides", 10);
                list.forEach((e) -> {
                    CompoundTag comp = (CompoundTag)e;
                    ret.overrides.put(new ResourceLocation(comp.getString("K")), comp.getString("V"));
                });
                int[] blocked = nbt.getIntArray("blocked");
                int[] var4 = blocked;
                int var5 = blocked.length;

                for(int var6 = 0; var6 < var5; ++var6) {
                    int i = var4[var6];
                    ret.blocked.add(i);
                }

                return ret;
            }
        }

        public synchronized FriendlyByteBuf getPacketData() {
            if (this.binary == null) {
                FriendlyByteBuf pkt = new FriendlyByteBuf(Unpooled.buffer());
                pkt.writeMap(this.ids, FriendlyByteBuf::writeResourceLocation, FriendlyByteBuf::writeVarInt);
                pkt.writeMap(this.aliases, FriendlyByteBuf::writeResourceLocation, FriendlyByteBuf::writeResourceLocation);
                pkt.writeMap(this.overrides, FriendlyByteBuf::writeResourceLocation, (b, v) -> {
                    b.writeUtf(v, 256);
                });
                pkt.writeCollection(this.blocked, FriendlyByteBuf::writeVarInt);
                this.binary = pkt;
            }

            return new FriendlyByteBuf(this.binary.slice());
        }

        public static Snapshot read(FriendlyByteBuf buf) {
            if (buf == null) {
                return new Snapshot();
            } else {
                Snapshot ret = new Snapshot();
                ret.ids.putAll(buf.readMap(FriendlyByteBuf::readResourceLocation, FriendlyByteBuf::readVarInt));
                ret.aliases.putAll(buf.readMap(FriendlyByteBuf::readResourceLocation, FriendlyByteBuf::readResourceLocation));
                ret.overrides.putAll(buf.readMap(FriendlyByteBuf::readResourceLocation, (b) -> {
                    return b.readUtf(256);
                }));
                ret.blocked.addAll(buf.readList(FriendlyByteBuf::readVarInt));
                return ret;
            }
        }
    }

    private static record DumpRow(String id, String key, String value) {
        private DumpRow(String id, String key, String value) {
            this.id = id;
            this.key = key;
            this.value = value;
        }

        public String id() {
            return this.id;
        }

        public String key() {
            return this.key;
        }

        public String value() {
            return this.value;
        }
    }
}

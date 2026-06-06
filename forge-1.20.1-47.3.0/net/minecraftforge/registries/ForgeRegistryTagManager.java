//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.registries;

import com.google.common.collect.Iterators;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.registries.tags.IReverseTag;
import net.minecraftforge.registries.tags.ITag;
import net.minecraftforge.registries.tags.ITagManager;
import org.jetbrains.annotations.NotNull;

class ForgeRegistryTagManager<V> implements ITagManager<V> {
    private final ForgeRegistry<V> owner;
    private volatile Map<TagKey<V>, ITag<V>> tags = new IdentityHashMap();

    ForgeRegistryTagManager(ForgeRegistry<V> owner) {
        this.owner = owner;
    }

    void bind(Map<TagKey<V>, HolderSet.Named<V>> holderTags, Set<TagKey<V>> defaultedTags) {
        IdentityHashMap<TagKey<V>, ITag<V>> newTags = new IdentityHashMap(this.tags);
        newTags.values().forEach((tag) -> {
            ((ForgeRegistryTag)tag).bind((HolderSet)null);
        });
        holderTags.forEach((key, holderSet) -> {
            ((ForgeRegistryTag)newTags.computeIfAbsent(key, ForgeRegistryTag::new)).bind(holderSet);
        });
        this.tags = newTags;
    }

    public @NotNull ITag<V> getTag(@NotNull TagKey<V> name) {
        Objects.requireNonNull(name);
        ITag<V> tag = (ITag)this.tags.get(name);
        if (tag == null) {
            tag = new ForgeRegistryTag(name);
            IdentityHashMap<TagKey<V>, ITag<V>> map = new IdentityHashMap(this.tags);
            map.put(name, tag);
            this.tags = map;
        }

        return (ITag)tag;
    }

    public @NotNull Optional<IReverseTag<V>> getReverseTag(@NotNull V value) {
        Objects.requireNonNull(value);
        return this.owner.getHolder(value);
    }

    public boolean isKnownTagName(@NotNull TagKey<V> name) {
        Objects.requireNonNull(name);
        ITag<V> tag = (ITag)this.tags.get(name);
        return tag != null && tag.isBound();
    }

    public @NotNull Iterator<ITag<V>> iterator() {
        return Iterators.unmodifiableIterator(this.tags.values().iterator());
    }

    public @NotNull Stream<ITag<V>> stream() {
        return this.tags.values().stream();
    }

    public @NotNull Stream<TagKey<V>> getTagNames() {
        return this.tags.keySet().stream();
    }

    public @NotNull TagKey<V> createTagKey(@NotNull ResourceLocation location) {
        Objects.requireNonNull(location);
        return TagKey.create(this.owner.getRegistryKey(), location);
    }

    public @NotNull TagKey<V> createOptionalTagKey(@NotNull ResourceLocation location, @NotNull Set<? extends Supplier<V>> defaults) {
        TagKey<V> tagKey = this.createTagKey(location);
        this.addOptionalTagDefaults(tagKey, defaults);
        return tagKey;
    }

    public void addOptionalTagDefaults(@NotNull TagKey<V> name, @NotNull Set<? extends Supplier<V>> defaults) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(defaults);
        NamespacedWrapper<V> wrapper = this.owner.getWrapper();
        if (wrapper != null) {
            wrapper.addOptionalTag(name, defaults);
        }

    }
}

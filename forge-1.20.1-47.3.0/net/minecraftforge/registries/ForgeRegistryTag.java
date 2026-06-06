//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.registries;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraftforge.registries.tags.ITag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class ForgeRegistryTag<V> implements ITag<V> {
    private final TagKey<V> key;
    private @Nullable HolderSet<V> holderSet;
    private @Nullable List<V> contents;

    ForgeRegistryTag(TagKey<V> key) {
        this.key = key;
    }

    public TagKey<V> getKey() {
        return this.key;
    }

    public @NotNull Iterator<V> iterator() {
        return this.getContents().iterator();
    }

    public Spliterator<V> spliterator() {
        return this.getContents().spliterator();
    }

    public boolean isEmpty() {
        return this.getContents().isEmpty();
    }

    public int size() {
        return this.getContents().size();
    }

    public Stream<V> stream() {
        return this.getContents().stream();
    }

    public boolean contains(V value) {
        return this.getContents().contains(value);
    }

    public Optional<V> getRandomElement(RandomSource random) {
        return Util.getRandomSafe(this.getContents(), random);
    }

    public boolean isBound() {
        return this.holderSet != null;
    }

    List<V> getContents() {
        if (this.contents == null && this.holderSet != null) {
            this.contents = this.holderSet.stream().map(Holder::value).toList();
        }

        return this.contents == null ? List.of() : this.contents;
    }

    void bind(@Nullable HolderSet<V> holderSet) {
        this.holderSet = holderSet;
        this.contents = null;
    }

    public String toString() {
        TagKey var10000 = this.key;
        return "Tag[key=" + var10000 + ", contents=" + this.getContents() + "]";
    }
}

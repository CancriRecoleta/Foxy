//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.registries.holdersets;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraftforge.common.ForgeMod;

public record AnyHolderSet<T>(HolderLookup.RegistryLookup<T> registryLookup) implements ICustomHolderSet<T> {
    public AnyHolderSet(HolderLookup.RegistryLookup<T> registryLookup) {
        this.registryLookup = registryLookup;
    }

    public static <T> Codec<? extends ICustomHolderSet<T>> codec(ResourceKey<? extends Registry<T>> registryKey, Codec<Holder<T>> holderCodec, boolean forceList) {
        return RegistryOps.retrieveRegistryLookup(registryKey).xmap(AnyHolderSet::new, AnyHolderSet::registryLookup).codec();
    }

    public HolderSetType type() {
        return (HolderSetType)ForgeMod.ANY_HOLDER_SET.get();
    }

    public Iterator<Holder<T>> iterator() {
        return this.stream().iterator();
    }

    public Stream<Holder<T>> stream() {
        return this.registryLookup.listElements().map(Function.identity());
    }

    public int size() {
        return (int)this.stream().count();
    }

    public Either<TagKey<T>, List<Holder<T>>> unwrap() {
        return Either.right(this.stream().toList());
    }

    public Optional<Holder<T>> getRandomElement(RandomSource random) {
        return Util.getRandomSafe(this.stream().toList(), random);
    }

    public Holder<T> get(int i) {
        List<Holder<T>> holders = this.stream().toList();
        Holder<T> holder = i >= holders.size() ? null : (Holder)holders.get(i);
        if (holder == null) {
            throw new NoSuchElementException("No element " + i + " in registry " + this.registryLookup.key());
        } else {
            return holder;
        }
    }

    public boolean contains(Holder<T> holder) {
        return (Boolean)holder.unwrapKey().map((key) -> {
            Stream var10000 = this.registryLookup.listElementIds();
            Objects.requireNonNull(key);
            return var10000.anyMatch(key::equals);
        }).orElse(false);
    }

    public boolean canSerializeIn(HolderOwner<T> holderOwner) {
        return this.registryLookup.canSerializeIn(holderOwner);
    }

    public Optional<TagKey<T>> unwrapKey() {
        return Optional.empty();
    }

    public String toString() {
        return "AnySet(" + this.registryLookup.key() + ")";
    }

    public HolderLookup.RegistryLookup<T> registryLookup() {
        return this.registryLookup;
    }
}

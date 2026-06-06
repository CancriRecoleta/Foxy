//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.registries.holdersets;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.common.ForgeMod;

public class AndHolderSet<T> extends CompositeHolderSet<T> {
    public static <T> Codec<? extends ICustomHolderSet<T>> codec(ResourceKey<? extends Registry<T>> registryKey, Codec<Holder<T>> holderCodec, boolean forceList) {
        return HolderSetCodec.create(registryKey, holderCodec, forceList).listOf().xmap(AndHolderSet::new, CompositeHolderSet::homogenize).fieldOf("values").codec();
    }

    public AndHolderSet(List<HolderSet<T>> values) {
        super(values);
    }

    public HolderSetType type() {
        return (HolderSetType)ForgeMod.AND_HOLDER_SET.get();
    }

    protected Set<Holder<T>> createSet() {
        List<HolderSet<T>> components = this.getComponents();
        if (components.size() < 1) {
            return Set.of();
        } else if (components.size() == 1) {
            return (Set)((HolderSet)components.get(0)).stream().collect(Collectors.toSet());
        } else {
            List<HolderSet<T>> remainingComponents = components.subList(1, components.size());
            return (Set)((HolderSet)components.get(0)).stream().filter((holder) -> {
                return remainingComponents.stream().allMatch((holderset) -> {
                    return holderset.contains(holder);
                });
            }).collect(Collectors.toSet());
        }
    }

    public String toString() {
        return "AndSet[" + this.getComponents() + "]";
    }
}

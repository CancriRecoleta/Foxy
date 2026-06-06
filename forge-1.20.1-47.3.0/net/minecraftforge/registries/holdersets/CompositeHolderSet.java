//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.registries.holdersets;

import com.mojang.datafixers.util.Either;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraftforge.common.extensions.IForgeHolderSet;
import org.jetbrains.annotations.Nullable;

public abstract class CompositeHolderSet<T> implements ICustomHolderSet<T> {
    private final List<Runnable> owners = new ArrayList();
    private final List<HolderSet<T>> components;
    private @Nullable Set<Holder<T>> set = null;
    private @Nullable List<Holder<T>> list = null;

    public CompositeHolderSet(List<HolderSet<T>> components) {
        this.components = components;
        Iterator var2 = components.iterator();

        while(var2.hasNext()) {
            HolderSet<T> holderset = (HolderSet)var2.next();
            holderset.addInvalidationListener(this::invalidate);
        }

    }

    protected abstract Set<Holder<T>> createSet();

    public List<HolderSet<T>> getComponents() {
        return this.components;
    }

    public Set<Holder<T>> getSet() {
        Set<Holder<T>> thisSet = this.set;
        if (thisSet == null) {
            Set<Holder<T>> set = this.createSet();
            this.set = set;
            return set;
        } else {
            return thisSet;
        }
    }

    public List<Holder<T>> getList() {
        List<Holder<T>> thisList = this.list;
        if (thisList == null) {
            List<Holder<T>> list = List.copyOf(this.getSet());
            this.list = list;
            return list;
        } else {
            return thisList;
        }
    }

    public void addInvalidationListener(Runnable runnable) {
        this.owners.add(runnable);
    }

    private void invalidate() {
        this.set = null;
        this.list = null;
        Iterator var1 = this.owners.iterator();

        while(var1.hasNext()) {
            Runnable runnable = (Runnable)var1.next();
            runnable.run();
        }

    }

    public Stream<Holder<T>> stream() {
        return this.getList().stream();
    }

    public int size() {
        return this.getList().size();
    }

    public Either<TagKey<T>, List<Holder<T>>> unwrap() {
        return Either.right(this.getList());
    }

    public Optional<Holder<T>> getRandomElement(RandomSource rand) {
        List<Holder<T>> list = this.getList();
        int size = list.size();
        return size > 0 ? Optional.of((Holder)list.get(rand.nextInt(size))) : Optional.empty();
    }

    public Holder<T> get(int i) {
        return (Holder)this.getList().get(i);
    }

    public boolean contains(Holder<T> holder) {
        return this.getSet().contains(holder);
    }

    public boolean canSerializeIn(HolderOwner<T> holderOwner) {
        Iterator var2 = this.components.iterator();

        HolderSet component;
        do {
            if (!var2.hasNext()) {
                return true;
            }

            component = (HolderSet)var2.next();
        } while(component.canSerializeIn(holderOwner));

        return false;
    }

    public Optional<TagKey<T>> unwrapKey() {
        return Optional.empty();
    }

    public Iterator<Holder<T>> iterator() {
        return this.getList().iterator();
    }

    public List<HolderSet<T>> homogenize() {
        List<HolderSet<T>> components = this.getComponents();
        if (this.isHomogenous()) {
            return components;
        } else {
            List<HolderSet<T>> outputs = new ArrayList();
            Iterator var3 = components.iterator();

            while(var3.hasNext()) {
                HolderSet<T> holderset = (HolderSet)var3.next();
                if (holderset instanceof ICustomHolderSet) {
                    outputs.add(holderset);
                } else {
                    outputs.add(new OrHolderSet(List.of(holderset)));
                }
            }

            return outputs;
        }
    }

    public boolean isHomogenous() {
        List<HolderSet<T>> holderSets = this.getComponents();
        if (holderSets.size() < 2) {
            return true;
        } else {
            IForgeHolderSet.SerializationType firstType = ((HolderSet)holderSets.get(0)).serializationType();
            if (firstType == net.minecraftforge.common.extensions.IForgeHolderSet.SerializationType.UNKNOWN) {
                return false;
            } else {
                int size = holderSets.size();

                for(int i = 1; i < size; ++i) {
                    IForgeHolderSet.SerializationType type = ((HolderSet)holderSets.get(i)).serializationType();
                    if (type != firstType) {
                        return false;
                    }
                }

                return true;
            }
        }
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.core;

import com.mojang.serialization.Lifecycle;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

public class DefaultedMappedRegistry<T> extends MappedRegistry<T> implements DefaultedRegistry<T> {
    private final ResourceLocation defaultKey;
    private Holder.Reference<T> defaultValue;

    public DefaultedMappedRegistry(String p_260196_, ResourceKey<? extends Registry<T>> p_259440_, Lifecycle p_260260_, boolean p_259808_) {
        super(p_259440_, p_260260_, p_259808_);
        this.defaultKey = new ResourceLocation(p_260196_);
    }

    public Holder.Reference<T> registerMapping(int p_259787_, ResourceKey<T> p_259677_, T p_259430_, Lifecycle p_259516_) {
        Holder.Reference<T> $$4 = super.registerMapping(p_259787_, p_259677_, p_259430_, p_259516_);
        if (this.defaultKey.equals(p_259677_.location())) {
            this.defaultValue = $$4;
        }

        return $$4;
    }

    public int getId(@Nullable T p_260033_) {
        int $$1 = super.getId(p_260033_);
        return $$1 == -1 ? super.getId(this.defaultValue.value()) : $$1;
    }

    @Nonnull
    public ResourceLocation getKey(T p_259233_) {
        ResourceLocation $$1 = super.getKey(p_259233_);
        return $$1 == null ? this.defaultKey : $$1;
    }

    @Nonnull
    public T get(@Nullable ResourceLocation p_260004_) {
        T $$1 = super.get(p_260004_);
        return $$1 == null ? this.defaultValue.value() : $$1;
    }

    public Optional<T> getOptional(@Nullable ResourceLocation p_260078_) {
        return Optional.ofNullable(super.get(p_260078_));
    }

    @Nonnull
    public T byId(int p_259534_) {
        T $$1 = super.byId(p_259534_);
        return $$1 == null ? this.defaultValue.value() : $$1;
    }

    public Optional<Holder.Reference<T>> getRandom(RandomSource p_260255_) {
        return super.getRandom(p_260255_).or(() -> {
            return Optional.of(this.defaultValue);
        });
    }

    public ResourceLocation getDefaultKey() {
        return this.defaultKey;
    }
}

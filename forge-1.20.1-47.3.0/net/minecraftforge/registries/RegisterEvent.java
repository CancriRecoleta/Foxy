//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.registries;

import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.IModBusEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RegisterEvent extends Event implements IModBusEvent {
    private final @NotNull ResourceKey<? extends Registry<?>> registryKey;
    final @Nullable ForgeRegistry<?> forgeRegistry;
    private final @Nullable Registry<?> vanillaRegistry;

    RegisterEvent(@NotNull ResourceKey<? extends Registry<?>> registryKey, @Nullable ForgeRegistry<?> forgeRegistry, @Nullable Registry<?> vanillaRegistry) {
        this.registryKey = registryKey;
        this.forgeRegistry = forgeRegistry;
        this.vanillaRegistry = vanillaRegistry;
    }

    public <T> void register(ResourceKey<? extends Registry<T>> registryKey, ResourceLocation name, Supplier<T> valueSupplier) {
        if (this.registryKey.equals(registryKey)) {
            if (this.forgeRegistry != null) {
                this.forgeRegistry.register(name, valueSupplier.get());
            } else if (this.vanillaRegistry != null) {
                Registry.register(this.vanillaRegistry, name, valueSupplier.get());
            }
        }

    }

    public <T> void register(ResourceKey<? extends Registry<T>> registryKey, Consumer<RegisterHelper<T>> consumer) {
        if (this.registryKey.equals(registryKey)) {
            consumer.accept((name, value) -> {
                this.register(registryKey, name, () -> {
                    return value;
                });
            });
        }

    }

    public @NotNull ResourceKey<? extends Registry<?>> getRegistryKey() {
        return this.registryKey;
    }

    public <T> @Nullable IForgeRegistry<T> getForgeRegistry() {
        return this.forgeRegistry;
    }

    public <T> @Nullable Registry<T> getVanillaRegistry() {
        return this.vanillaRegistry;
    }

    public String toString() {
        return "RegisterEvent";
    }

    @FunctionalInterface
    public interface RegisterHelper<T> {
        default void register(String name, T value) {
            this.register(new ResourceLocation(ModLoadingContext.get().getActiveNamespace(), name), value);
        }

        default void register(ResourceKey<T> key, T value) {
            this.register(key.location(), value);
        }

        void register(ResourceLocation var1, T var2);
    }
}

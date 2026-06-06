//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.registries;

import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class NewRegistryEvent extends Event implements IModBusEvent {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final List<RegistryData<?>> registries = new ArrayList();

    public NewRegistryEvent() {
    }

    public <V> Supplier<IForgeRegistry<V>> create(RegistryBuilder<V> builder) {
        return this.create(builder, (Consumer)null);
    }

    public <V> Supplier<IForgeRegistry<V>> create(RegistryBuilder<V> builder, @Nullable Consumer<IForgeRegistry<V>> onFill) {
        RegistryHolder<V> registryHolder = new RegistryHolder();
        this.registries.add(new RegistryData(builder, registryHolder, onFill));
        return registryHolder;
    }

    void fill() {
        RuntimeException aggregate = new RuntimeException();
        Map<RegistryBuilder<?>, IForgeRegistry<?>> builtRegistries = new IdentityHashMap();
        Registry var4 = BuiltInRegistries.REGISTRY;
        if (var4 instanceof MappedRegistry<?> rootRegistry) {
            rootRegistry.unfreeze();
        }

        Iterator var7 = this.registries.iterator();

        while(var7.hasNext()) {
            RegistryData<?> data = (RegistryData)var7.next();

            try {
                this.buildRegistry(builtRegistries, data);
            } catch (Throwable var6) {
                Throwable t = var6;
                aggregate.addSuppressed(t);
                return;
            }
        }

        var4 = BuiltInRegistries.REGISTRY;
        if (var4 instanceof MappedRegistry<?> rootRegistry) {
            rootRegistry.freeze();
        }

        if (aggregate.getSuppressed().length > 0) {
            LOGGER.error(LogUtils.FATAL_MARKER, "Failed to create some forge registries, see suppressed exceptions for details", aggregate);
        }

    }

    private <T> void buildRegistry(Map<RegistryBuilder<?>, IForgeRegistry<?>> builtRegistries, RegistryData<T> data) {
        RegistryBuilder<T> builder = data.builder;
        IForgeRegistry<T> registry = builder.create();
        builtRegistries.put(builder, registry);
        if (builder.getHasWrapper() && !BuiltInRegistries.REGISTRY.containsKey(registry.getRegistryName())) {
            RegistryManager.registerToRootRegistry((ForgeRegistry)registry);
        }

        data.registryHolder.registry = registry;
        if (data.onFill != null) {
            data.onFill.accept(registry);
        }

    }

    public String toString() {
        return "RegistryEvent.NewRegistry";
    }

    private static class RegistryHolder<V> implements Supplier<IForgeRegistry<V>> {
        IForgeRegistry<V> registry = null;

        private RegistryHolder() {
        }

        public IForgeRegistry<V> get() {
            return this.registry;
        }
    }

    private static record RegistryData<V>(RegistryBuilder<V> builder, RegistryHolder<V> registryHolder, Consumer<IForgeRegistry<V>> onFill) {
        private RegistryData(RegistryBuilder<V> builder, RegistryHolder<V> registryHolder, Consumer<IForgeRegistry<V>> onFill) {
            this.builder = builder;
            this.registryHolder = registryHolder;
            this.onFill = onFill;
        }

        public RegistryBuilder<V> builder() {
            return this.builder;
        }

        public RegistryHolder<V> registryHolder() {
            return this.registryHolder;
        }

        public Consumer<IForgeRegistry<V>> onFill() {
            return this.onFill;
        }
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.registries;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public class DataPackRegistryEvent extends Event implements IModBusEvent {
    @Internal
    public DataPackRegistryEvent() {
    }

    static record DataPackRegistryData<T>(RegistryDataLoader.RegistryData<T> loaderData, @Nullable Codec<T> networkCodec) {
        DataPackRegistryData(RegistryDataLoader.RegistryData<T> loaderData, @Nullable Codec<T> networkCodec) {
            this.loaderData = loaderData;
            this.networkCodec = networkCodec;
        }

        public RegistryDataLoader.RegistryData<T> loaderData() {
            return this.loaderData;
        }

        public @Nullable Codec<T> networkCodec() {
            return this.networkCodec;
        }
    }

    public static final class NewRegistry extends DataPackRegistryEvent {
        private final List<DataPackRegistryData<?>> registryDataList = new ArrayList();

        @Internal
        public NewRegistry() {
        }

        public <T> void dataPackRegistry(ResourceKey<Registry<T>> registryKey, Codec<T> codec) {
            this.dataPackRegistry(registryKey, codec, (Codec)null);
        }

        public <T> void dataPackRegistry(ResourceKey<Registry<T>> registryKey, Codec<T> codec, @Nullable Codec<T> networkCodec) {
            this.registryDataList.add(new DataPackRegistryData(new RegistryDataLoader.RegistryData(registryKey, codec), networkCodec));
        }

        void process() {
            Iterator var1 = this.registryDataList.iterator();

            while(var1.hasNext()) {
                DataPackRegistryData<?> registryData = (DataPackRegistryData)var1.next();
                DataPackRegistriesHooks.addRegistryCodec(registryData);
            }

        }
    }
}

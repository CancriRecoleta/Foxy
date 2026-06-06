//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.registries;

import net.minecraftforge.eventbus.api.IEventBus;

public class ForgeDeferredRegistriesSetup {
    private static boolean setup = false;

    public ForgeDeferredRegistriesSetup() {
    }

    public static void setup(IEventBus modEventBus) {
        Class var1 = ForgeDeferredRegistriesSetup.class;
        synchronized(ForgeDeferredRegistriesSetup.class) {
            if (setup) {
                throw new IllegalStateException("Setup has already been called!");
            }

            setup = true;
        }

        ForgeRegistries.DEFERRED_ENTITY_DATA_SERIALIZERS.register(modEventBus);
        ForgeRegistries.DEFERRED_GLOBAL_LOOT_MODIFIER_SERIALIZERS.register(modEventBus);
        ForgeRegistries.DEFERRED_BIOME_MODIFIER_SERIALIZERS.register(modEventBus);
        ForgeRegistries.DEFERRED_FLUID_TYPES.register(modEventBus);
        ForgeRegistries.DEFERRED_STRUCTURE_MODIFIER_SERIALIZERS.register(modEventBus);
        ForgeRegistries.DEFERRED_HOLDER_SET_TYPES.register(modEventBus);
        ForgeRegistries.DEFERRED_DISPLAY_CONTEXTS.register(modEventBus);
    }
}

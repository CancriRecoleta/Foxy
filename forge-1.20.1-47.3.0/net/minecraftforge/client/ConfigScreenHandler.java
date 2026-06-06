//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.client;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;

public class ConfigScreenHandler {
    public ConfigScreenHandler() {
    }

    public static Optional<BiFunction<Minecraft, Screen, Screen>> getScreenFactoryFor(IModInfo selectedMod) {
        return ModList.get().getModContainerById(selectedMod.getModId()).flatMap((mc) -> {
            return mc.getCustomExtension(ConfigScreenFactory.class).map(ConfigScreenFactory::screenFunction);
        });
    }

    public static record ConfigScreenFactory(BiFunction<Minecraft, Screen, Screen> screenFunction) implements IExtensionPoint<ConfigScreenFactory> {
        public ConfigScreenFactory(Function<Screen, Screen> screenFunction) {
            this((mcClient, modsScreen) -> {
                return (Screen)screenFunction.apply(modsScreen);
            });
        }

        public ConfigScreenFactory(BiFunction<Minecraft, Screen, Screen> screenFunction) {
            this.screenFunction = screenFunction;
        }

        public BiFunction<Minecraft, Screen, Screen> screenFunction() {
            return this.screenFunction;
        }
    }
}

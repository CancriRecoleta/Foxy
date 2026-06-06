//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.internal;

import java.util.function.Supplier;
import net.minecraftforge.common.ForgeI18n;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.I18NParser;
import net.minecraftforge.fml.IBindingsProvider;
import net.minecraftforge.fml.config.IConfigEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;

public class ForgeBindings implements IBindingsProvider {
    public ForgeBindings() {
    }

    public Supplier<IEventBus> getForgeBusSupplier() {
        return () -> {
            return MinecraftForge.EVENT_BUS;
        };
    }

    public Supplier<I18NParser> getMessageParser() {
        return () -> {
            return new I18NParser() {
                public String parseMessage(String i18nMessage, Object... args) {
                    return ForgeI18n.parseMessage(i18nMessage, args);
                }

                public String stripControlCodes(String toStrip) {
                    return ForgeI18n.stripControlCodes(toStrip);
                }
            };
        };
    }

    public Supplier<IConfigEvent.ConfigConfig> getConfigConfiguration() {
        return () -> {
            return new IConfigEvent.ConfigConfig(ModConfigEvent.Loading::new, ModConfigEvent.Reloading::new, ModConfigEvent.Unloading::new);
        };
    }
}

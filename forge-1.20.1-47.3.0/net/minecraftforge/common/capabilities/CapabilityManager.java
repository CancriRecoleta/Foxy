//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common.capabilities;

import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import net.minecraftforge.fml.Logging;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Type;

public enum CapabilityManager {
    INSTANCE;

    static final Logger LOGGER = LogManager.getLogger();
    private static final Type AUTO_REGISTER = Type.getType(AutoRegisterCapability.class);
    private final IdentityHashMap<String, Capability<?>> providers = new IdentityHashMap();

    private CapabilityManager() {
    }

    public static <T> Capability<T> get(CapabilityToken<T> type) {
        return INSTANCE.get(type.getType(), false);
    }

    <T> Capability<T> get(String realName, boolean registering) {
        Capability cap;
        synchronized(this.providers) {
            realName = realName.intern();
            cap = (Capability)this.providers.computeIfAbsent(realName, Capability::new);
        }

        if (registering) {
            synchronized(cap) {
                if (cap.isRegistered()) {
                    LOGGER.error(Logging.CAPABILITIES, "Cannot register capability implementation multiple times : {}", realName);
                    throw new IllegalArgumentException("Cannot register a capability implementation multiple times : " + realName);
                }

                cap.onRegister();
            }
        }

        return cap;
    }

    public void injectCapabilities(List<ModFileScanData> data) {
        List<Type> autos = data.stream().flatMap((e) -> {
            return e.getAnnotations().stream();
        }).filter((a) -> {
            return AUTO_REGISTER.equals(a.annotationType());
        }).map((a) -> {
            return a.clazz();
        }).distinct().sorted(Comparator.comparing(Type::toString)).toList();
        Iterator var3 = autos.iterator();

        while(var3.hasNext()) {
            Type auto = (Type)var3.next();
            LOGGER.debug(Logging.CAPABILITIES, "Attempting to automatically register: " + auto);
            this.get(auto.getInternalName(), true);
        }

        RegisterCapabilitiesEvent event = new RegisterCapabilitiesEvent();
        ModLoader.get().postEvent(event);
    }
}

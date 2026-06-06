//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.client.settings;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.KeyMapping;
import org.jetbrains.annotations.Nullable;

public class KeyMappingLookup {
    private static final EnumMap<KeyModifier, Map<InputConstants.Key, List<KeyMapping>>> map = new EnumMap(KeyModifier.class);

    public KeyMappingLookup() {
    }

    /** @deprecated */
    @Deprecated(
        forRemoval = true,
        since = "1.20.1"
    )
    public @Nullable KeyMapping get(InputConstants.Key keyCode) {
        KeyModifier activeModifier = KeyModifier.getActiveModifier();
        if (!activeModifier.matches(keyCode)) {
            KeyMapping binding = this.get(keyCode, activeModifier);
            if (binding != null) {
                return binding;
            }
        }

        return this.get(keyCode, KeyModifier.NONE);
    }

    /** @deprecated */
    @Deprecated(
        forRemoval = true,
        since = "1.20.1"
    )
    private @Nullable KeyMapping get(InputConstants.Key keyCode, KeyModifier keyModifier) {
        List<KeyMapping> bindings = (List)((Map)map.get(keyModifier)).get(keyCode);
        if (bindings != null) {
            Iterator var4 = bindings.iterator();

            while(var4.hasNext()) {
                KeyMapping binding = (KeyMapping)var4.next();
                if (binding.isActiveAndMatches(keyCode)) {
                    return binding;
                }
            }
        }

        return null;
    }

    public List<KeyMapping> getAll(InputConstants.Key keyCode) {
        ArrayList<KeyMapping> ret = new ArrayList();
        Iterator var3 = KeyModifier.getValues(false).iterator();

        while(true) {
            KeyModifier modifier;
            do {
                do {
                    if (!var3.hasNext()) {
                        if (!ret.isEmpty()) {
                            return ret;
                        }

                        var3 = this.get(KeyModifier.NONE, keyCode).iterator();

                        while(var3.hasNext()) {
                            KeyMapping binding = (KeyMapping)var3.next();
                            if (binding.isActiveAndMatches(keyCode)) {
                                ret.add(binding);
                            }
                        }

                        return ret;
                    }

                    modifier = (KeyModifier)var3.next();
                } while(!modifier.isActive((IKeyConflictContext)null));
            } while(modifier.matches(keyCode));

            Iterator var5 = this.get(modifier, keyCode).iterator();

            while(var5.hasNext()) {
                KeyMapping binding = (KeyMapping)var5.next();
                if (binding.isActiveAndMatches(keyCode)) {
                    ret.add(binding);
                }
            }
        }
    }

    private List<KeyMapping> get(KeyModifier modifier, InputConstants.Key keyCode) {
        List<KeyMapping> bindings = (List)((Map)map.get(modifier)).get(keyCode);
        return bindings == null ? Collections.emptyList() : bindings;
    }

    public void put(InputConstants.Key keyCode, KeyMapping keyBinding) {
        Map<InputConstants.Key, List<KeyMapping>> bindingsMap = (Map)map.get(keyBinding.getKeyModifier());
        List<KeyMapping> bindingsForKey = (List)bindingsMap.computeIfAbsent(keyCode, (k) -> {
            return new ArrayList();
        });
        bindingsForKey.add(keyBinding);
    }

    public void remove(KeyMapping keyBinding) {
        InputConstants.Key keyCode = keyBinding.getKey();
        Map<InputConstants.Key, List<KeyMapping>> bindingsMap = (Map)map.get(keyBinding.getKeyModifier());
        List<KeyMapping> bindingsForKey = (List)bindingsMap.get(keyCode);
        if (bindingsForKey != null) {
            bindingsForKey.remove(keyBinding);
            if (bindingsForKey.isEmpty()) {
                bindingsMap.remove(keyCode);
            }
        }

    }

    public void clear() {
        map.values().forEach(Map::clear);
    }

    static {
        KeyModifier[] var0 = KeyModifier.values();
        int var1 = var0.length;

        for(int var2 = 0; var2 < var1; ++var2) {
            KeyModifier modifier = var0[var2];
            map.put(modifier, new HashMap());
        }

    }
}

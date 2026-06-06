//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.client.settings;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public enum KeyModifier {
    CONTROL {
        public boolean matches(InputConstants.Key key) {
            int keyCode = key.getValue();
            if (Minecraft.ON_OSX) {
                return keyCode == 343 || keyCode == 347;
            } else {
                return keyCode == 341 || keyCode == 345;
            }
        }

        public boolean isActive(@Nullable IKeyConflictContext conflictContext) {
            return Screen.hasControlDown();
        }

        public Component getCombinedName(InputConstants.Key key, Supplier<Component> defaultLogic) {
            String localizationFormatKey = Minecraft.ON_OSX ? "forge.controlsgui.control.mac" : "forge.controlsgui.control";
            return Component.translatable(localizationFormatKey, defaultLogic.get());
        }
    },
    SHIFT {
        public boolean matches(InputConstants.Key key) {
            return key.getValue() == 340 || key.getValue() == 344;
        }

        public boolean isActive(@Nullable IKeyConflictContext conflictContext) {
            return Screen.hasShiftDown();
        }

        public Component getCombinedName(InputConstants.Key key, Supplier<Component> defaultLogic) {
            return Component.translatable("forge.controlsgui.shift", defaultLogic.get());
        }
    },
    ALT {
        public boolean matches(InputConstants.Key key) {
            return key.getValue() == 342 || key.getValue() == 346;
        }

        public boolean isActive(@Nullable IKeyConflictContext conflictContext) {
            return Screen.hasAltDown();
        }

        public Component getCombinedName(InputConstants.Key keyCode, Supplier<Component> defaultLogic) {
            return Component.translatable("forge.controlsgui.alt", defaultLogic.get());
        }
    },
    NONE {
        public boolean matches(InputConstants.Key key) {
            return false;
        }

        public boolean isActive(@Nullable IKeyConflictContext conflictContext) {
            if (conflictContext != null && !conflictContext.conflicts(KeyConflictContext.IN_GAME)) {
                KeyModifier[] var2 = KeyModifier.VALUES;
                int var3 = var2.length;

                for(int var4 = 0; var4 < var3; ++var4) {
                    KeyModifier keyModifier = var2[var4];
                    if (keyModifier.isActive(conflictContext)) {
                        return false;
                    }
                }
            }

            return true;
        }

        public Component getCombinedName(InputConstants.Key key, Supplier<Component> defaultLogic) {
            return (Component)defaultLogic.get();
        }
    };

    /** @deprecated */
    @Deprecated(
        forRemoval = true,
        since = "1.20.2"
    )
    public static final KeyModifier[] MODIFIER_VALUES = new KeyModifier[]{SHIFT, CONTROL, ALT};
    private static final KeyModifier[] VALUES = new KeyModifier[]{SHIFT, CONTROL, ALT};
    private static final List<KeyModifier> VALUES_LIST = List.of(SHIFT, CONTROL, ALT);
    private static final List<KeyModifier> ALL = List.of(SHIFT, CONTROL, ALT, NONE);

    private KeyModifier() {
    }

    /** @deprecated */
    @Deprecated(
        forRemoval = true,
        since = "1.20.2"
    )
    public static KeyModifier getActiveModifier() {
        KeyModifier[] var0 = VALUES;
        int var1 = var0.length;

        for(int var2 = 0; var2 < var1; ++var2) {
            KeyModifier keyModifier = var0[var2];
            if (keyModifier.isActive((IKeyConflictContext)null)) {
                return keyModifier;
            }
        }

        return NONE;
    }

    public static final List<KeyModifier> getValues(boolean includeNone) {
        return includeNone ? ALL : VALUES_LIST;
    }

    public static @Nullable KeyModifier getModifier(InputConstants.Key key) {
        KeyModifier[] var1 = VALUES;
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            KeyModifier modifier = var1[var3];
            if (modifier.matches(key)) {
                return modifier;
            }
        }

        return null;
    }

    public static boolean isKeyCodeModifier(InputConstants.Key key) {
        KeyModifier[] var1 = VALUES;
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            KeyModifier keyModifier = var1[var3];
            if (keyModifier.matches(key)) {
                return true;
            }
        }

        return false;
    }

    public static KeyModifier valueFromString(String stringValue) {
        try {
            return valueOf(stringValue);
        } catch (IllegalArgumentException | NullPointerException var2) {
            return NONE;
        }
    }

    public abstract boolean matches(InputConstants.Key var1);

    public abstract boolean isActive(@Nullable IKeyConflictContext var1);

    public abstract Component getCombinedName(InputConstants.Key var1, Supplier<Component> var2);
}

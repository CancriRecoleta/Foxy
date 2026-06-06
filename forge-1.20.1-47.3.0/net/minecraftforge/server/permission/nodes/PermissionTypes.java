//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.server.permission.nodes;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public final class PermissionTypes {
    public static final PermissionType<Boolean> BOOLEAN = new PermissionType(Boolean.class, "boolean");
    public static final PermissionType<Integer> INTEGER = new PermissionType(Integer.class, "integer");
    public static final PermissionType<String> STRING = new PermissionType(String.class, "string");
    public static final PermissionType<Component> COMPONENT = new PermissionType(Component.class, "component");

    private PermissionTypes() {
    }

    public static @Nullable PermissionType<?> getTypeByName(String name) {
        PermissionType var10000;
        switch (name) {
            case "boolean" -> var10000 = BOOLEAN;
            case "integer" -> var10000 = INTEGER;
            case "string" -> var10000 = STRING;
            case "component" -> var10000 = COMPONENT;
            default -> var10000 = null;
        }

        return var10000;
    }
}

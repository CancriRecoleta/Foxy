//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.server.permission.nodes;

import java.util.function.Function;

public record PermissionDynamicContextKey<T>(Class<T> typeToken, String name, Function<T, String> serializer) {
    public PermissionDynamicContextKey(Class<T> typeToken, String name, Function<T, String> serializer) {
        this.typeToken = typeToken;
        this.name = name;
        this.serializer = serializer;
    }

    public PermissionDynamicContext<T> createContext(T value) {
        return new PermissionDynamicContext(this, value);
    }

    public Class<T> typeToken() {
        return this.typeToken;
    }

    public String name() {
        return this.name;
    }

    public Function<T, String> serializer() {
        return this.serializer;
    }
}

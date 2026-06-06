//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.server.permission.nodes;

import java.util.Objects;

public final class PermissionType<T> {
    private final Class<T> typeToken;
    private final String typeName;

    PermissionType(Class<T> typeToken, String typeName) {
        this.typeToken = typeToken;
        this.typeName = typeName;
    }

    public Class<T> typeToken() {
        return this.typeToken;
    }

    public String typeName() {
        return this.typeName;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (!(obj instanceof PermissionType)) {
            return false;
        } else {
            PermissionType otherType = (PermissionType)obj;
            return Objects.equals(this.typeToken, otherType.typeToken) && Objects.equals(this.typeName, otherType.typeName);
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.typeToken, this.typeName});
    }

    public String toString() {
        return "PermissionType[typeToken=" + this.typeToken + ", typeName=" + this.typeName + "]";
    }
}

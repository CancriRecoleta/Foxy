//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common.capabilities;

public abstract class CapabilityToken<T> {
    public CapabilityToken() {
    }

    protected final String getType() {
        throw new RuntimeException("This will be implemented by a transformer");
    }

    public String toString() {
        return "CapabilityToken[" + this.getType() + "]";
    }
}

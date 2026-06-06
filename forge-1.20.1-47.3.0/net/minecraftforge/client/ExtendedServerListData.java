//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.client;

public record ExtendedServerListData(String type, boolean isCompatible, int numberOfMods, String extraReason, boolean truncated) {
    public ExtendedServerListData(String type, boolean isCompatible, int numberOfMods, String extraReason) {
        this(type, isCompatible, numberOfMods, extraReason, false);
    }

    public ExtendedServerListData(String type, boolean isCompatible, int numberOfMods, String extraReason, boolean truncated) {
        this.type = type;
        this.isCompatible = isCompatible;
        this.numberOfMods = numberOfMods;
        this.extraReason = extraReason;
        this.truncated = truncated;
    }

    public String type() {
        return this.type;
    }

    public boolean isCompatible() {
        return this.isCompatible;
    }

    public int numberOfMods() {
        return this.numberOfMods;
    }

    public String extraReason() {
        return this.extraReason;
    }

    public boolean truncated() {
        return this.truncated;
    }
}

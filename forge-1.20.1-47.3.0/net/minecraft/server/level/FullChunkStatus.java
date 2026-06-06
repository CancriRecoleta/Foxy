//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.level;

public enum FullChunkStatus {
    INACCESSIBLE,
    FULL,
    BLOCK_TICKING,
    ENTITY_TICKING;

    private FullChunkStatus() {
    }

    public boolean isOrAfter(FullChunkStatus p_287607_) {
        return this.ordinal() >= p_287607_.ordinal();
    }
}

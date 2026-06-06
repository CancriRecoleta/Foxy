//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.packs.metadata.pack;

import java.util.Map;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionType;

public class PackMetadataSection {
    public static final MetadataSectionType<PackMetadataSection> TYPE = new PackMetadataSectionSerializer();
    private final Component description;
    private final int packFormat;
    private final Map<PackType, Integer> packTypeVersions;

    public PackMetadataSection(Component p_10371_, int p_10372_) {
        this.description = p_10371_;
        this.packFormat = p_10372_;
        this.packTypeVersions = Map.of();
    }

    public PackMetadataSection(Component p_10371_, int p_10372_, Map<PackType, Integer> packTypeVersions) {
        this.description = p_10371_;
        this.packFormat = p_10372_;
        this.packTypeVersions = packTypeVersions;
    }

    public Component getDescription() {
        return this.description;
    }

    /** @deprecated */
    @Deprecated
    public int getPackFormat() {
        return this.packFormat;
    }

    public int getPackFormat(PackType packType) {
        return (Integer)this.packTypeVersions.getOrDefault(packType, this.packFormat);
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.packs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;

public record FeatureFlagsMetadataSection(FeatureFlagSet flags) {
    private static final Codec<FeatureFlagsMetadataSection> CODEC = RecordCodecBuilder.create((p_251762_) -> {
        return p_251762_.group(FeatureFlags.CODEC.fieldOf("enabled").forGetter(FeatureFlagsMetadataSection::flags)).apply(p_251762_, FeatureFlagsMetadataSection::new);
    });
    public static final MetadataSectionType<FeatureFlagsMetadataSection> TYPE;

    public FeatureFlagsMetadataSection(FeatureFlagSet flags) {
        this.flags = flags;
    }

    public FeatureFlagSet flags() {
        return this.flags;
    }

    static {
        TYPE = MetadataSectionType.fromCodec("features", CODEC);
    }
}

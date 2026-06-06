//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.feature.featuresize;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class FeatureSizeType<P extends FeatureSize> {
    public static final FeatureSizeType<TwoLayersFeatureSize> TWO_LAYERS_FEATURE_SIZE;
    public static final FeatureSizeType<ThreeLayersFeatureSize> THREE_LAYERS_FEATURE_SIZE;
    private final Codec<P> codec;

    private static <P extends FeatureSize> FeatureSizeType<P> register(String p_68304_, Codec<P> p_68305_) {
        return (FeatureSizeType)Registry.register(BuiltInRegistries.FEATURE_SIZE_TYPE, (String)p_68304_, new FeatureSizeType(p_68305_));
    }

    public FeatureSizeType(Codec<P> p_68301_) {
        this.codec = p_68301_;
    }

    public Codec<P> codec() {
        return this.codec;
    }

    static {
        TWO_LAYERS_FEATURE_SIZE = register("two_layers_feature_size", TwoLayersFeatureSize.CODEC);
        THREE_LAYERS_FEATURE_SIZE = register("three_layers_feature_size", ThreeLayersFeatureSize.CODEC);
    }
}

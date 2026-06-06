//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;

public class NoneFeatureConfiguration implements FeatureConfiguration {
    public static final Codec<NoneFeatureConfiguration> CODEC = Codec.unit(() -> {
        return INSTANCE;
    });
    public static final NoneFeatureConfiguration INSTANCE = new NoneFeatureConfiguration();

    public NoneFeatureConfiguration() {
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.biome;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;

public class BiomeSources {
    public BiomeSources() {
    }

    public static Codec<? extends BiomeSource> bootstrap(Registry<Codec<? extends BiomeSource>> p_220587_) {
        Registry.register(p_220587_, (String)"fixed", FixedBiomeSource.CODEC);
        Registry.register(p_220587_, (String)"multi_noise", MultiNoiseBiomeSource.CODEC);
        Registry.register(p_220587_, (String)"checkerboard", CheckerboardColumnBiomeSource.CODEC);
        return (Codec)Registry.register(p_220587_, (String)"the_end", TheEndBiomeSource.CODEC);
    }
}

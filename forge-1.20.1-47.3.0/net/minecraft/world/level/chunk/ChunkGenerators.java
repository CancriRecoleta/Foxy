//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.chunk;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;

public class ChunkGenerators {
    public ChunkGenerators() {
    }

    public static Codec<? extends ChunkGenerator> bootstrap(Registry<Codec<? extends ChunkGenerator>> p_223243_) {
        Registry.register(p_223243_, (String)"noise", NoiseBasedChunkGenerator.CODEC);
        Registry.register(p_223243_, (String)"flat", FlatLevelSource.CODEC);
        return (Codec)Registry.register(p_223243_, (String)"debug", DebugLevelSource.CODEC);
    }
}

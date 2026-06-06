//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class RandomSelectorFeature extends Feature<RandomFeatureConfiguration> {
    public RandomSelectorFeature(Codec<RandomFeatureConfiguration> p_66619_) {
        super(p_66619_);
    }

    public boolean place(FeaturePlaceContext<RandomFeatureConfiguration> p_160212_) {
        RandomFeatureConfiguration $$1 = (RandomFeatureConfiguration)p_160212_.config();
        RandomSource $$2 = p_160212_.random();
        WorldGenLevel $$3 = p_160212_.level();
        ChunkGenerator $$4 = p_160212_.chunkGenerator();
        BlockPos $$5 = p_160212_.origin();
        Iterator var7 = $$1.features.iterator();

        WeightedPlacedFeature $$6;
        do {
            if (!var7.hasNext()) {
                return ((PlacedFeature)$$1.defaultFeature.value()).place($$3, $$4, $$2, $$5);
            }

            $$6 = (WeightedPlacedFeature)var7.next();
        } while(!($$2.nextFloat() < $$6.chance));

        return $$6.place($$3, $$4, $$2, $$5);
    }
}

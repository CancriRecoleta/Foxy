//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;

public class ReplaceBlockFeature extends Feature<ReplaceBlockConfiguration> {
    public ReplaceBlockFeature(Codec<ReplaceBlockConfiguration> p_66651_) {
        super(p_66651_);
    }

    public boolean place(FeaturePlaceContext<ReplaceBlockConfiguration> p_160216_) {
        WorldGenLevel $$1 = p_160216_.level();
        BlockPos $$2 = p_160216_.origin();
        ReplaceBlockConfiguration $$3 = (ReplaceBlockConfiguration)p_160216_.config();
        Iterator var5 = $$3.targetStates.iterator();

        while(var5.hasNext()) {
            OreConfiguration.TargetBlockState $$4 = (OreConfiguration.TargetBlockState)var5.next();
            if ($$4.target.test($$1.getBlockState($$2), p_160216_.random())) {
                $$1.setBlock($$2, $$4.state, 2);
                break;
            }
        }

        return true;
    }
}

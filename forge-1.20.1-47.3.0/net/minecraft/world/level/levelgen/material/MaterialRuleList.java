//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.material;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseChunk;

public record MaterialRuleList(List<NoiseChunk.BlockStateFiller> materialRuleList) implements NoiseChunk.BlockStateFiller {
    public MaterialRuleList(List<NoiseChunk.BlockStateFiller> materialRuleList) {
        this.materialRuleList = materialRuleList;
    }

    @Nullable
    public BlockState calculate(DensityFunction.FunctionContext p_209815_) {
        Iterator var2 = this.materialRuleList.iterator();

        BlockState $$2;
        do {
            if (!var2.hasNext()) {
                return null;
            }

            NoiseChunk.BlockStateFiller $$1 = (NoiseChunk.BlockStateFiller)var2.next();
            $$2 = $$1.calculate(p_209815_);
        } while($$2 == null);

        return $$2;
    }

    public List<NoiseChunk.BlockStateFiller> materialRuleList() {
        return this.materialRuleList;
    }
}

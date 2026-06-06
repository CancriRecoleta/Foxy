//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.serialization.Codec;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public class RuleProcessor extends StructureProcessor {
    public static final Codec<RuleProcessor> CODEC;
    private final ImmutableList<ProcessorRule> rules;

    public RuleProcessor(List<? extends ProcessorRule> p_74296_) {
        this.rules = ImmutableList.copyOf(p_74296_);
    }

    @Nullable
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader p_74299_, BlockPos p_74300_, BlockPos p_74301_, StructureTemplate.StructureBlockInfo p_74302_, StructureTemplate.StructureBlockInfo p_74303_, StructurePlaceSettings p_74304_) {
        RandomSource $$6 = RandomSource.create(Mth.getSeed(p_74303_.pos()));
        BlockState $$7 = p_74299_.getBlockState(p_74303_.pos());
        UnmodifiableIterator var9 = this.rules.iterator();

        ProcessorRule $$8;
        do {
            if (!var9.hasNext()) {
                return p_74303_;
            }

            $$8 = (ProcessorRule)var9.next();
        } while(!$$8.test(p_74303_.state(), $$7, p_74302_.pos(), p_74303_.pos(), p_74301_, $$6));

        return new StructureTemplate.StructureBlockInfo(p_74303_.pos(), $$8.getOutputState(), $$8.getOutputTag($$6, p_74303_.nbt()));
    }

    protected StructureProcessorType<?> getType() {
        return StructureProcessorType.RULE;
    }

    static {
        CODEC = ProcessorRule.CODEC.listOf().fieldOf("rules").xmap(RuleProcessor::new, (p_74306_) -> {
            return p_74306_.rules;
        }).codec();
    }
}

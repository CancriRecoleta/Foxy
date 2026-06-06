//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;

public record RuleBasedBlockStateProvider(BlockStateProvider fallback, List<Rule> rules) {
    public static final Codec<RuleBasedBlockStateProvider> CODEC = RecordCodecBuilder.create((p_225939_) -> {
        return p_225939_.group(BlockStateProvider.CODEC.fieldOf("fallback").forGetter(RuleBasedBlockStateProvider::fallback), net.minecraft.world.level.levelgen.feature.stateproviders.RuleBasedBlockStateProvider.Rule.CODEC.listOf().fieldOf("rules").forGetter(RuleBasedBlockStateProvider::rules)).apply(p_225939_, RuleBasedBlockStateProvider::new);
    });

    public RuleBasedBlockStateProvider(BlockStateProvider fallback, List<Rule> rules) {
        this.fallback = fallback;
        this.rules = rules;
    }

    public static RuleBasedBlockStateProvider simple(BlockStateProvider p_225941_) {
        return new RuleBasedBlockStateProvider(p_225941_, List.of());
    }

    public static RuleBasedBlockStateProvider simple(Block p_225937_) {
        return simple((BlockStateProvider)BlockStateProvider.simple(p_225937_));
    }

    public BlockState getState(WorldGenLevel p_225933_, RandomSource p_225934_, BlockPos p_225935_) {
        Iterator var4 = this.rules.iterator();

        Rule $$3;
        do {
            if (!var4.hasNext()) {
                return this.fallback.getState(p_225934_, p_225935_);
            }

            $$3 = (Rule)var4.next();
        } while(!$$3.ifTrue().test(p_225933_, p_225935_));

        return $$3.then().getState(p_225934_, p_225935_);
    }

    public BlockStateProvider fallback() {
        return this.fallback;
    }

    public List<Rule> rules() {
        return this.rules;
    }

    public static record Rule(BlockPredicate ifTrue, BlockStateProvider then) {
        public static final Codec<Rule> CODEC = RecordCodecBuilder.create((p_225956_) -> {
            return p_225956_.group(BlockPredicate.CODEC.fieldOf("if_true").forGetter(Rule::ifTrue), BlockStateProvider.CODEC.fieldOf("then").forGetter(Rule::then)).apply(p_225956_, Rule::new);
        });

        public Rule(BlockPredicate ifTrue, BlockStateProvider then) {
            this.ifTrue = ifTrue;
            this.then = then;
        }

        public BlockPredicate ifTrue() {
            return this.ifTrue;
        }

        public BlockStateProvider then() {
            return this.then;
        }
    }
}

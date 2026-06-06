//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class BlockPileConfiguration implements FeatureConfiguration {
    public static final Codec<BlockPileConfiguration> CODEC;
    public final BlockStateProvider stateProvider;

    public BlockPileConfiguration(BlockStateProvider p_67543_) {
        this.stateProvider = p_67543_;
    }

    static {
        CODEC = BlockStateProvider.CODEC.fieldOf("state_provider").xmap(BlockPileConfiguration::new, (p_67545_) -> {
            return p_67545_.stateProvider;
        }).codec();
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.block.state.BlockState;

public class BlockStateConfiguration implements FeatureConfiguration {
    public static final Codec<BlockStateConfiguration> CODEC;
    public final BlockState state;

    public BlockStateConfiguration(BlockState p_67550_) {
        this.state = p_67550_;
    }

    static {
        CODEC = BlockState.CODEC.fieldOf("state").xmap(BlockStateConfiguration::new, (p_67552_) -> {
            return p_67552_.state;
        }).codec();
    }
}

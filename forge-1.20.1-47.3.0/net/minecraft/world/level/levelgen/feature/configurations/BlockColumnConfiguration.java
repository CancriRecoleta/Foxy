//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record BlockColumnConfiguration(List<Layer> layers, Direction direction, BlockPredicate allowedPlacement, boolean prioritizeTip) implements FeatureConfiguration {
    public static final Codec<BlockColumnConfiguration> CODEC = RecordCodecBuilder.create((p_191222_) -> {
        return p_191222_.group(net.minecraft.world.level.levelgen.feature.configurations.BlockColumnConfiguration.Layer.CODEC.listOf().fieldOf("layers").forGetter(BlockColumnConfiguration::layers), Direction.CODEC.fieldOf("direction").forGetter(BlockColumnConfiguration::direction), BlockPredicate.CODEC.fieldOf("allowed_placement").forGetter(BlockColumnConfiguration::allowedPlacement), Codec.BOOL.fieldOf("prioritize_tip").forGetter(BlockColumnConfiguration::prioritizeTip)).apply(p_191222_, BlockColumnConfiguration::new);
    });

    public BlockColumnConfiguration(List<Layer> layers, Direction direction, BlockPredicate allowedPlacement, boolean prioritizeTip) {
        this.layers = layers;
        this.direction = direction;
        this.allowedPlacement = allowedPlacement;
        this.prioritizeTip = prioritizeTip;
    }

    public static Layer layer(IntProvider p_191219_, BlockStateProvider p_191220_) {
        return new Layer(p_191219_, p_191220_);
    }

    public static BlockColumnConfiguration simple(IntProvider p_191225_, BlockStateProvider p_191226_) {
        return new BlockColumnConfiguration(List.of(layer(p_191225_, p_191226_)), Direction.UP, BlockPredicate.ONLY_IN_AIR_PREDICATE, false);
    }

    public List<Layer> layers() {
        return this.layers;
    }

    public Direction direction() {
        return this.direction;
    }

    public BlockPredicate allowedPlacement() {
        return this.allowedPlacement;
    }

    public boolean prioritizeTip() {
        return this.prioritizeTip;
    }

    public static record Layer(IntProvider height, BlockStateProvider state) {
        public static final Codec<Layer> CODEC = RecordCodecBuilder.create((p_191242_) -> {
            return p_191242_.group(IntProvider.NON_NEGATIVE_CODEC.fieldOf("height").forGetter(Layer::height), BlockStateProvider.CODEC.fieldOf("provider").forGetter(Layer::state)).apply(p_191242_, Layer::new);
        });

        public Layer(IntProvider height, BlockStateProvider state) {
            this.height = height;
            this.state = state;
        }

        public IntProvider height() {
            return this.height;
        }

        public BlockStateProvider state() {
            return this.state;
        }
    }
}

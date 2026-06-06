//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.RuleBasedBlockStateProvider;

public record DiskConfiguration(RuleBasedBlockStateProvider stateProvider, BlockPredicate target, IntProvider radius, int halfHeight) implements FeatureConfiguration {
    public static final Codec<DiskConfiguration> CODEC = RecordCodecBuilder.create((p_191250_) -> {
        return p_191250_.group(RuleBasedBlockStateProvider.CODEC.fieldOf("state_provider").forGetter(DiskConfiguration::stateProvider), BlockPredicate.CODEC.fieldOf("target").forGetter(DiskConfiguration::target), IntProvider.codec(0, 8).fieldOf("radius").forGetter(DiskConfiguration::radius), Codec.intRange(0, 4).fieldOf("half_height").forGetter(DiskConfiguration::halfHeight)).apply(p_191250_, DiskConfiguration::new);
    });

    public DiskConfiguration(RuleBasedBlockStateProvider stateProvider, BlockPredicate target, IntProvider radius, int halfHeight) {
        this.stateProvider = stateProvider;
        this.target = target;
        this.radius = radius;
        this.halfHeight = halfHeight;
    }

    public RuleBasedBlockStateProvider stateProvider() {
        return this.stateProvider;
    }

    public BlockPredicate target() {
        return this.target;
    }

    public IntProvider radius() {
        return this.radius;
    }

    public int halfHeight() {
        return this.halfHeight;
    }
}

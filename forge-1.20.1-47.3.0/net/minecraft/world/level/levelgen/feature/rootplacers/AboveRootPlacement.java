//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.feature.rootplacers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record AboveRootPlacement(BlockStateProvider aboveRootProvider, float aboveRootPlacementChance) {
    public static final Codec<AboveRootPlacement> CODEC = RecordCodecBuilder.create((p_225762_) -> {
        return p_225762_.group(BlockStateProvider.CODEC.fieldOf("above_root_provider").forGetter((p_225767_) -> {
            return p_225767_.aboveRootProvider;
        }), Codec.floatRange(0.0F, 1.0F).fieldOf("above_root_placement_chance").forGetter((p_225764_) -> {
            return p_225764_.aboveRootPlacementChance;
        })).apply(p_225762_, AboveRootPlacement::new);
    });

    public AboveRootPlacement(BlockStateProvider aboveRootProvider, float aboveRootPlacementChance) {
        this.aboveRootProvider = aboveRootProvider;
        this.aboveRootPlacementChance = aboveRootPlacementChance;
    }

    public BlockStateProvider aboveRootProvider() {
        return this.aboveRootProvider;
    }

    public float aboveRootPlacementChance() {
        return this.aboveRootPlacementChance;
    }
}

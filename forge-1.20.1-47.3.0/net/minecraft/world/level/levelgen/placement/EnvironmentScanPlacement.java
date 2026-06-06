//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;

public class EnvironmentScanPlacement extends PlacementModifier {
    private final Direction directionOfSearch;
    private final BlockPredicate targetCondition;
    private final BlockPredicate allowedSearchCondition;
    private final int maxSteps;
    public static final Codec<EnvironmentScanPlacement> CODEC = RecordCodecBuilder.create((p_191650_) -> {
        return p_191650_.group(Direction.VERTICAL_CODEC.fieldOf("direction_of_search").forGetter((p_191672_) -> {
            return p_191672_.directionOfSearch;
        }), BlockPredicate.CODEC.fieldOf("target_condition").forGetter((p_191670_) -> {
            return p_191670_.targetCondition;
        }), BlockPredicate.CODEC.optionalFieldOf("allowed_search_condition", BlockPredicate.alwaysTrue()).forGetter((p_191668_) -> {
            return p_191668_.allowedSearchCondition;
        }), Codec.intRange(1, 32).fieldOf("max_steps").forGetter((p_191652_) -> {
            return p_191652_.maxSteps;
        })).apply(p_191650_, EnvironmentScanPlacement::new);
    });

    private EnvironmentScanPlacement(Direction p_191645_, BlockPredicate p_191646_, BlockPredicate p_191647_, int p_191648_) {
        this.directionOfSearch = p_191645_;
        this.targetCondition = p_191646_;
        this.allowedSearchCondition = p_191647_;
        this.maxSteps = p_191648_;
    }

    public static EnvironmentScanPlacement scanningFor(Direction p_191658_, BlockPredicate p_191659_, BlockPredicate p_191660_, int p_191661_) {
        return new EnvironmentScanPlacement(p_191658_, p_191659_, p_191660_, p_191661_);
    }

    public static EnvironmentScanPlacement scanningFor(Direction p_191654_, BlockPredicate p_191655_, int p_191656_) {
        return scanningFor(p_191654_, p_191655_, BlockPredicate.alwaysTrue(), p_191656_);
    }

    public Stream<BlockPos> getPositions(PlacementContext p_226336_, RandomSource p_226337_, BlockPos p_226338_) {
        BlockPos.MutableBlockPos $$3 = p_226338_.mutable();
        WorldGenLevel $$4 = p_226336_.getLevel();
        if (!this.allowedSearchCondition.test($$4, $$3)) {
            return Stream.of();
        } else {
            int $$5 = 0;

            while(true) {
                if ($$5 < this.maxSteps) {
                    if (this.targetCondition.test($$4, $$3)) {
                        return Stream.of($$3);
                    }

                    $$3.move(this.directionOfSearch);
                    if ($$4.isOutsideBuildHeight($$3.getY())) {
                        return Stream.of();
                    }

                    if (this.allowedSearchCondition.test($$4, $$3)) {
                        ++$$5;
                        continue;
                    }
                }

                if (this.targetCondition.test($$4, $$3)) {
                    return Stream.of($$3);
                }

                return Stream.of();
            }
        }
    }

    public PlacementModifierType<?> type() {
        return PlacementModifierType.ENVIRONMENT_SCAN;
    }
}

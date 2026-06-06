//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.Heightmap.Types;

public class SurfaceWaterDepthFilter extends PlacementFilter {
    public static final Codec<SurfaceWaterDepthFilter> CODEC = RecordCodecBuilder.create((p_191953_) -> {
        return p_191953_.group(Codec.INT.fieldOf("max_water_depth").forGetter((p_191959_) -> {
            return p_191959_.maxWaterDepth;
        })).apply(p_191953_, SurfaceWaterDepthFilter::new);
    });
    private final int maxWaterDepth;

    private SurfaceWaterDepthFilter(int p_191949_) {
        this.maxWaterDepth = p_191949_;
    }

    public static SurfaceWaterDepthFilter forMaxDepth(int p_191951_) {
        return new SurfaceWaterDepthFilter(p_191951_);
    }

    protected boolean shouldPlace(PlacementContext p_226411_, RandomSource p_226412_, BlockPos p_226413_) {
        int $$3 = p_226411_.getHeight(Types.OCEAN_FLOOR, p_226413_.getX(), p_226413_.getZ());
        int $$4 = p_226411_.getHeight(Types.WORLD_SURFACE, p_226413_.getX(), p_226413_.getZ());
        return $$4 - $$3 <= this.maxWaterDepth;
    }

    public PlacementModifierType<?> type() {
        return PlacementModifierType.SURFACE_WATER_DEPTH_FILTER;
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.biome;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.level.biome.Climate.ParameterList;
import net.minecraft.world.level.levelgen.NoiseRouterData;

public class MultiNoiseBiomeSource extends BiomeSource {
    private static final MapCodec<Holder<Biome>> ENTRY_CODEC;
    public static final MapCodec<Climate.ParameterList<Holder<Biome>>> DIRECT_CODEC;
    private static final MapCodec<Holder<MultiNoiseBiomeSourceParameterList>> PRESET_CODEC;
    public static final Codec<MultiNoiseBiomeSource> CODEC;
    private final Either<Climate.ParameterList<Holder<Biome>>, Holder<MultiNoiseBiomeSourceParameterList>> parameters;

    private MultiNoiseBiomeSource(Either<Climate.ParameterList<Holder<Biome>>, Holder<MultiNoiseBiomeSourceParameterList>> p_275370_) {
        this.parameters = p_275370_;
    }

    public static MultiNoiseBiomeSource createFromList(Climate.ParameterList<Holder<Biome>> p_275223_) {
        return new MultiNoiseBiomeSource(Either.left(p_275223_));
    }

    public static MultiNoiseBiomeSource createFromPreset(Holder<MultiNoiseBiomeSourceParameterList> p_275250_) {
        return new MultiNoiseBiomeSource(Either.right(p_275250_));
    }

    private Climate.ParameterList<Holder<Biome>> parameters() {
        return (Climate.ParameterList)this.parameters.map((p_275171_) -> {
            return p_275171_;
        }, (p_275172_) -> {
            return ((MultiNoiseBiomeSourceParameterList)p_275172_.value()).parameters();
        });
    }

    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return this.parameters().values().stream().map(Pair::getSecond);
    }

    protected Codec<? extends BiomeSource> codec() {
        return CODEC;
    }

    public boolean stable(ResourceKey<MultiNoiseBiomeSourceParameterList> p_275637_) {
        Optional<Holder<MultiNoiseBiomeSourceParameterList>> $$1 = this.parameters.right();
        return $$1.isPresent() && ((Holder)$$1.get()).is(p_275637_);
    }

    public Holder<Biome> getNoiseBiome(int p_204272_, int p_204273_, int p_204274_, Climate.Sampler p_204275_) {
        return this.getNoiseBiome(p_204275_.sample(p_204272_, p_204273_, p_204274_));
    }

    @VisibleForDebug
    public Holder<Biome> getNoiseBiome(Climate.TargetPoint p_204270_) {
        return (Holder)this.parameters().findValue(p_204270_);
    }

    public void addDebugInfo(List<String> p_207895_, BlockPos p_207896_, Climate.Sampler p_207897_) {
        int $$3 = QuartPos.fromBlock(p_207896_.getX());
        int $$4 = QuartPos.fromBlock(p_207896_.getY());
        int $$5 = QuartPos.fromBlock(p_207896_.getZ());
        Climate.TargetPoint $$6 = p_207897_.sample($$3, $$4, $$5);
        float $$7 = Climate.unquantizeCoord($$6.continentalness());
        float $$8 = Climate.unquantizeCoord($$6.erosion());
        float $$9 = Climate.unquantizeCoord($$6.temperature());
        float $$10 = Climate.unquantizeCoord($$6.humidity());
        float $$11 = Climate.unquantizeCoord($$6.weirdness());
        double $$12 = (double)NoiseRouterData.peaksAndValleys($$11);
        OverworldBiomeBuilder $$13 = new OverworldBiomeBuilder();
        String var10001 = OverworldBiomeBuilder.getDebugStringForPeaksAndValleys($$12);
        p_207895_.add("Biome builder PV: " + var10001 + " C: " + $$13.getDebugStringForContinentalness((double)$$7) + " E: " + $$13.getDebugStringForErosion((double)$$8) + " T: " + $$13.getDebugStringForTemperature((double)$$9) + " H: " + $$13.getDebugStringForHumidity((double)$$10));
    }

    static {
        ENTRY_CODEC = Biome.CODEC.fieldOf("biome");
        DIRECT_CODEC = ParameterList.codec(ENTRY_CODEC).fieldOf("biomes");
        PRESET_CODEC = MultiNoiseBiomeSourceParameterList.CODEC.fieldOf("preset").withLifecycle(Lifecycle.stable());
        CODEC = Codec.mapEither(DIRECT_CODEC, PRESET_CODEC).xmap(MultiNoiseBiomeSource::new, (p_275170_) -> {
            return p_275170_.parameters;
        }).codec();
    }
}

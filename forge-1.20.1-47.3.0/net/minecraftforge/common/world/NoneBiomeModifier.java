//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common.world;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.ForgeMod;

public class NoneBiomeModifier implements BiomeModifier {
    public static final NoneBiomeModifier INSTANCE = new NoneBiomeModifier();

    public NoneBiomeModifier() {
    }

    public void modify(Holder<Biome> biome, BiomeModifier.Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
    }

    public Codec<? extends BiomeModifier> codec() {
        return (Codec)ForgeMod.NONE_BIOME_MODIFIER_TYPE.get();
    }
}

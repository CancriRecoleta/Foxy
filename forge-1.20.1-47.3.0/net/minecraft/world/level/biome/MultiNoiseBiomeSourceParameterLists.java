//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.biome;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList.Preset;

public class MultiNoiseBiomeSourceParameterLists {
    public static final ResourceKey<MultiNoiseBiomeSourceParameterList> NETHER = register("nether");
    public static final ResourceKey<MultiNoiseBiomeSourceParameterList> OVERWORLD = register("overworld");

    public MultiNoiseBiomeSourceParameterLists() {
    }

    public static void bootstrap(BootstapContext<MultiNoiseBiomeSourceParameterList> p_275387_) {
        HolderGetter<Biome> $$1 = p_275387_.lookup(Registries.BIOME);
        p_275387_.register(NETHER, new MultiNoiseBiomeSourceParameterList(Preset.NETHER, $$1));
        p_275387_.register(OVERWORLD, new MultiNoiseBiomeSourceParameterList(Preset.OVERWORLD, $$1));
    }

    private static ResourceKey<MultiNoiseBiomeSourceParameterList> register(String p_275281_) {
        return ResourceKey.create(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST, new ResourceLocation(p_275281_));
    }
}

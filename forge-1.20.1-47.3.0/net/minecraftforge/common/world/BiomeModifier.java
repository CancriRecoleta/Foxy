//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common.world;

import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ForgeRegistries.Keys;

public interface BiomeModifier {
    Codec<BiomeModifier> DIRECT_CODEC = ExtraCodecs.lazyInitializedCodec(() -> {
        return ((IForgeRegistry)ForgeRegistries.BIOME_MODIFIER_SERIALIZERS.get()).getCodec();
    }).dispatch(BiomeModifier::codec, Function.identity());
    Codec<Holder<BiomeModifier>> REFERENCE_CODEC = RegistryFileCodec.create(Keys.BIOME_MODIFIERS, DIRECT_CODEC);
    Codec<HolderSet<BiomeModifier>> LIST_CODEC = RegistryCodecs.homogeneousList(Keys.BIOME_MODIFIERS, DIRECT_CODEC);

    void modify(Holder<Biome> var1, Phase var2, ModifiableBiomeInfo.BiomeInfo.Builder var3);

    Codec<? extends BiomeModifier> codec();

    public static enum Phase {
        BEFORE_EVERYTHING,
        ADD,
        REMOVE,
        MODIFY,
        AFTER_EVERYTHING;

        private Phase() {
        }
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.flag;

import com.mojang.serialization.Codec;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;

public class FeatureFlags {
    public static final FeatureFlag VANILLA;
    public static final FeatureFlag BUNDLE;
    public static final FeatureFlagRegistry REGISTRY;
    public static final Codec<FeatureFlagSet> CODEC;
    public static final FeatureFlagSet VANILLA_SET;
    public static final FeatureFlagSet DEFAULT_FLAGS;

    public FeatureFlags() {
    }

    public static String printMissingFlags(FeatureFlagSet p_250581_, FeatureFlagSet p_250326_) {
        return printMissingFlags(REGISTRY, p_250581_, p_250326_);
    }

    public static String printMissingFlags(FeatureFlagRegistry p_249213_, FeatureFlagSet p_250429_, FeatureFlagSet p_250547_) {
        Set<ResourceLocation> $$3 = p_249213_.toNames(p_250547_);
        Set<ResourceLocation> $$4 = p_249213_.toNames(p_250429_);
        return (String)$$3.stream().filter((p_251831_) -> {
            return !$$4.contains(p_251831_);
        }).map(ResourceLocation::toString).collect(Collectors.joining(", "));
    }

    public static boolean isExperimental(FeatureFlagSet p_249170_) {
        return !p_249170_.isSubsetOf(VANILLA_SET);
    }

    static {
        FeatureFlagRegistry.Builder $$0 = new FeatureFlagRegistry.Builder("main");
        VANILLA = $$0.createVanilla("vanilla");
        BUNDLE = $$0.createVanilla("bundle");
        REGISTRY = $$0.build();
        CODEC = REGISTRY.codec();
        VANILLA_SET = FeatureFlagSet.of(VANILLA);
        DEFAULT_FLAGS = VANILLA_SET;
    }
}

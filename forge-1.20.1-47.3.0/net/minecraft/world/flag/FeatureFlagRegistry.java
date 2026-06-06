//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.flag;

import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class FeatureFlagRegistry {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final FeatureFlagUniverse universe;
    private final Map<ResourceLocation, FeatureFlag> names;
    private final FeatureFlagSet allFlags;

    FeatureFlagRegistry(FeatureFlagUniverse p_249715_, FeatureFlagSet p_249277_, Map<ResourceLocation, FeatureFlag> p_249557_) {
        this.universe = p_249715_;
        this.names = p_249557_;
        this.allFlags = p_249277_;
    }

    public boolean isSubset(FeatureFlagSet p_251939_) {
        return p_251939_.isSubsetOf(this.allFlags);
    }

    public FeatureFlagSet allFlags() {
        return this.allFlags;
    }

    public FeatureFlagSet fromNames(Iterable<ResourceLocation> p_250759_) {
        return this.fromNames(p_250759_, (p_251224_) -> {
            LOGGER.warn("Unknown feature flag: {}", p_251224_);
        });
    }

    public FeatureFlagSet subset(FeatureFlag... p_252295_) {
        return FeatureFlagSet.create(this.universe, Arrays.asList(p_252295_));
    }

    public FeatureFlagSet fromNames(Iterable<ResourceLocation> p_251769_, Consumer<ResourceLocation> p_251521_) {
        Set<FeatureFlag> $$2 = Sets.newIdentityHashSet();
        Iterator var4 = p_251769_.iterator();

        while(var4.hasNext()) {
            ResourceLocation $$3 = (ResourceLocation)var4.next();
            FeatureFlag $$4 = (FeatureFlag)this.names.get($$3);
            if ($$4 == null) {
                p_251521_.accept($$3);
            } else {
                $$2.add($$4);
            }
        }

        return FeatureFlagSet.create(this.universe, $$2);
    }

    public Set<ResourceLocation> toNames(FeatureFlagSet p_251153_) {
        Set<ResourceLocation> $$1 = new HashSet();
        this.names.forEach((p_252018_, p_250772_) -> {
            if (p_251153_.contains(p_250772_)) {
                $$1.add(p_252018_);
            }

        });
        return $$1;
    }

    public Codec<FeatureFlagSet> codec() {
        return ResourceLocation.CODEC.listOf().comapFlatMap((p_275144_) -> {
            Set<ResourceLocation> $$1 = new HashSet();
            Objects.requireNonNull($$1);
            FeatureFlagSet $$2 = this.fromNames(p_275144_, $$1::add);
            return !$$1.isEmpty() ? DataResult.error(() -> {
                return "Unknown feature ids: " + $$1;
            }, $$2) : DataResult.success($$2);
        }, (p_249796_) -> {
            return List.copyOf(this.toNames(p_249796_));
        });
    }

    public static class Builder {
        private final FeatureFlagUniverse universe;
        private int id;
        private final Map<ResourceLocation, FeatureFlag> flags = new LinkedHashMap();

        public Builder(String p_251576_) {
            this.universe = new FeatureFlagUniverse(p_251576_);
        }

        public FeatureFlag createVanilla(String p_251782_) {
            return this.create(new ResourceLocation("minecraft", p_251782_));
        }

        public FeatureFlag create(ResourceLocation p_250098_) {
            if (this.id >= 64) {
                throw new IllegalStateException("Too many feature flags");
            } else {
                FeatureFlag $$1 = new FeatureFlag(this.universe, this.id++);
                FeatureFlag $$2 = (FeatureFlag)this.flags.put(p_250098_, $$1);
                if ($$2 != null) {
                    throw new IllegalStateException("Duplicate feature flag " + p_250098_);
                } else {
                    return $$1;
                }
            }
        }

        public FeatureFlagRegistry build() {
            FeatureFlagSet $$0 = FeatureFlagSet.create(this.universe, this.flags.values());
            return new FeatureFlagRegistry(this.universe, $$0, Map.copyOf(this.flags));
        }
    }
}

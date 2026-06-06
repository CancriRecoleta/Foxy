//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import org.slf4j.Logger;

public class MobSpawnSettings {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final float DEFAULT_CREATURE_SPAWN_PROBABILITY = 0.1F;
    public static final WeightedRandomList<SpawnerData> EMPTY_MOB_LIST = WeightedRandomList.create();
    public static final MobSpawnSettings EMPTY = (new Builder()).build();
    public static final MapCodec<MobSpawnSettings> CODEC = RecordCodecBuilder.mapCodec((p_187051_) -> {
        RecordCodecBuilder var10001 = Codec.floatRange(0.0F, 0.9999999F).optionalFieldOf("creature_spawn_probability", 0.1F).forGetter((p_187055_) -> {
            return p_187055_.creatureGenerationProbability;
        });
        Codec var10002 = MobCategory.CODEC;
        Codec var10003 = WeightedRandomList.codec(net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData.CODEC);
        Logger var10005 = LOGGER;
        Objects.requireNonNull(var10005);
        return p_187051_.group(var10001, Codec.simpleMap(var10002, var10003.promotePartial(Util.prefix("Spawn data: ", var10005::error)), StringRepresentable.keys(MobCategory.values())).fieldOf("spawners").forGetter((p_187053_) -> {
            return p_187053_.spawners;
        }), Codec.simpleMap(BuiltInRegistries.ENTITY_TYPE.byNameCodec(), net.minecraft.world.level.biome.MobSpawnSettings.MobSpawnCost.CODEC, BuiltInRegistries.ENTITY_TYPE).fieldOf("spawn_costs").forGetter((p_187049_) -> {
            return p_187049_.mobSpawnCosts;
        })).apply(p_187051_, MobSpawnSettings::new);
    });
    private final float creatureGenerationProbability;
    private final Map<MobCategory, WeightedRandomList<SpawnerData>> spawners;
    private final Map<EntityType<?>, MobSpawnCost> mobSpawnCosts;
    private final Set<MobCategory> typesView;
    private final Set<EntityType<?>> costView;

    MobSpawnSettings(float p_196689_, Map<MobCategory, WeightedRandomList<SpawnerData>> p_196690_, Map<EntityType<?>, MobSpawnCost> p_196691_) {
        this.creatureGenerationProbability = p_196689_;
        this.spawners = ImmutableMap.copyOf(p_196690_);
        this.mobSpawnCosts = ImmutableMap.copyOf(p_196691_);
        this.typesView = Collections.unmodifiableSet(this.spawners.keySet());
        this.costView = Collections.unmodifiableSet(this.mobSpawnCosts.keySet());
    }

    public WeightedRandomList<SpawnerData> getMobs(MobCategory p_151799_) {
        return (WeightedRandomList)this.spawners.getOrDefault(p_151799_, EMPTY_MOB_LIST);
    }

    public Set<MobCategory> getSpawnerTypes() {
        return this.typesView;
    }

    @Nullable
    public MobSpawnCost getMobSpawnCost(EntityType<?> p_48346_) {
        return (MobSpawnCost)this.mobSpawnCosts.get(p_48346_);
    }

    public Set<EntityType<?>> getEntityTypes() {
        return this.costView;
    }

    public float getCreatureProbability() {
        return this.creatureGenerationProbability;
    }

    public static record MobSpawnCost(double energyBudget, double charge) {
        public static final Codec<MobSpawnCost> CODEC = RecordCodecBuilder.create((p_48399_) -> {
            return p_48399_.group(Codec.DOUBLE.fieldOf("energy_budget").forGetter((p_151813_) -> {
                return p_151813_.energyBudget;
            }), Codec.DOUBLE.fieldOf("charge").forGetter((p_151811_) -> {
                return p_151811_.charge;
            })).apply(p_48399_, MobSpawnCost::new);
        });

        public MobSpawnCost(double energyBudget, double charge) {
            this.energyBudget = energyBudget;
            this.charge = charge;
        }

        public double energyBudget() {
            return this.energyBudget;
        }

        public double charge() {
            return this.charge;
        }
    }

    public static class SpawnerData extends WeightedEntry.IntrusiveBase {
        public static final Codec<SpawnerData> CODEC = ExtraCodecs.validate(RecordCodecBuilder.create((p_275169_) -> {
            return p_275169_.group(BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("type").forGetter((p_151826_) -> {
                return p_151826_.type;
            }), Weight.CODEC.fieldOf("weight").forGetter(WeightedEntry.IntrusiveBase::getWeight), ExtraCodecs.POSITIVE_INT.fieldOf("minCount").forGetter((p_151824_) -> {
                return p_151824_.minCount;
            }), ExtraCodecs.POSITIVE_INT.fieldOf("maxCount").forGetter((p_151820_) -> {
                return p_151820_.maxCount;
            })).apply(p_275169_, SpawnerData::new);
        }), (p_275168_) -> {
            return p_275168_.minCount > p_275168_.maxCount ? DataResult.error(() -> {
                return "minCount needs to be smaller or equal to maxCount";
            }) : DataResult.success(p_275168_);
        });
        public final EntityType<?> type;
        public final int minCount;
        public final int maxCount;

        public SpawnerData(EntityType<?> p_48409_, int p_48410_, int p_48411_, int p_48412_) {
            this(p_48409_, Weight.of(p_48410_), p_48411_, p_48412_);
        }

        public SpawnerData(EntityType<?> p_151815_, Weight p_151816_, int p_151817_, int p_151818_) {
            super(p_151816_);
            this.type = p_151815_.getCategory() == MobCategory.MISC ? EntityType.PIG : p_151815_;
            this.minCount = p_151817_;
            this.maxCount = p_151818_;
        }

        public String toString() {
            ResourceLocation var10000 = EntityType.getKey(this.type);
            return "" + var10000 + "*(" + this.minCount + "-" + this.maxCount + "):" + this.getWeight();
        }
    }

    public static class Builder {
        protected final Map<MobCategory, List<SpawnerData>> spawners = (Map)Stream.of(MobCategory.values()).collect(ImmutableMap.toImmutableMap((p_48383_) -> {
            return p_48383_;
        }, (p_48375_) -> {
            return Lists.newArrayList();
        }));
        protected final Map<EntityType<?>, MobSpawnCost> mobSpawnCosts = Maps.newLinkedHashMap();
        protected float creatureGenerationProbability = 0.1F;

        public Builder() {
        }

        public Builder addSpawn(MobCategory p_48377_, SpawnerData p_48378_) {
            ((List)this.spawners.get(p_48377_)).add(p_48378_);
            return this;
        }

        public Builder addMobCharge(EntityType<?> p_48371_, double p_48372_, double p_48373_) {
            this.mobSpawnCosts.put(p_48371_, new MobSpawnCost(p_48373_, p_48372_));
            return this;
        }

        public Builder creatureGenerationProbability(float p_48369_) {
            this.creatureGenerationProbability = p_48369_;
            return this;
        }

        public MobSpawnSettings build() {
            return new MobSpawnSettings(this.creatureGenerationProbability, (Map)this.spawners.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, (p_151809_) -> {
                return WeightedRandomList.create((List)p_151809_.getValue());
            })), ImmutableMap.copyOf(this.mobSpawnCosts));
        }
    }
}

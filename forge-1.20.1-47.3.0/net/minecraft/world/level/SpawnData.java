//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.InclusiveRange;
import net.minecraft.util.random.SimpleWeightedRandomList;

public record SpawnData(CompoundTag entityToSpawn, Optional<CustomSpawnRules> customSpawnRules) {
    public static final String ENTITY_TAG = "entity";
    public static final Codec<SpawnData> CODEC = RecordCodecBuilder.create((p_186571_) -> {
        return p_186571_.group(CompoundTag.CODEC.fieldOf("entity").forGetter((p_186576_) -> {
            return p_186576_.entityToSpawn;
        }), net.minecraft.world.level.SpawnData.CustomSpawnRules.CODEC.optionalFieldOf("custom_spawn_rules").forGetter((p_186569_) -> {
            return p_186569_.customSpawnRules;
        })).apply(p_186571_, SpawnData::new);
    });
    public static final Codec<SimpleWeightedRandomList<SpawnData>> LIST_CODEC;

    public SpawnData() {
        this(new CompoundTag(), Optional.empty());
    }

    public SpawnData(CompoundTag entityToSpawn, Optional<CustomSpawnRules> customSpawnRules) {
        if (entityToSpawn.contains("id")) {
            ResourceLocation $$2 = ResourceLocation.tryParse(entityToSpawn.getString("id"));
            if ($$2 != null) {
                entityToSpawn.putString("id", $$2.toString());
            } else {
                entityToSpawn.remove("id");
            }
        }

        this.entityToSpawn = entityToSpawn;
        this.customSpawnRules = customSpawnRules;
    }

    public CompoundTag getEntityToSpawn() {
        return this.entityToSpawn;
    }

    public Optional<CustomSpawnRules> getCustomSpawnRules() {
        return this.customSpawnRules;
    }

    public CompoundTag entityToSpawn() {
        return this.entityToSpawn;
    }

    public Optional<CustomSpawnRules> customSpawnRules() {
        return this.customSpawnRules;
    }

    static {
        LIST_CODEC = SimpleWeightedRandomList.wrappedCodecAllowingEmpty(CODEC);
    }

    public static record CustomSpawnRules(InclusiveRange<Integer> blockLightLimit, InclusiveRange<Integer> skyLightLimit) {
        private static final InclusiveRange<Integer> LIGHT_RANGE = new InclusiveRange(0, 15);
        public static final Codec<CustomSpawnRules> CODEC = RecordCodecBuilder.create((p_286217_) -> {
            return p_286217_.group(lightLimit("block_light_limit").forGetter((p_186600_) -> {
                return p_186600_.blockLightLimit;
            }), lightLimit("sky_light_limit").forGetter((p_186595_) -> {
                return p_186595_.skyLightLimit;
            })).apply(p_286217_, CustomSpawnRules::new);
        });

        public CustomSpawnRules(InclusiveRange<Integer> blockLightLimit, InclusiveRange<Integer> skyLightLimit) {
            this.blockLightLimit = blockLightLimit;
            this.skyLightLimit = skyLightLimit;
        }

        private static DataResult<InclusiveRange<Integer>> checkLightBoundaries(InclusiveRange<Integer> p_186593_) {
            return !LIGHT_RANGE.contains(p_186593_) ? DataResult.error(() -> {
                return "Light values must be withing range " + LIGHT_RANGE;
            }) : DataResult.success(p_186593_);
        }

        private static MapCodec<InclusiveRange<Integer>> lightLimit(String p_286409_) {
            return ExtraCodecs.validate(InclusiveRange.INT.optionalFieldOf(p_286409_, LIGHT_RANGE), CustomSpawnRules::checkLightBoundaries);
        }

        public InclusiveRange<Integer> blockLightLimit() {
            return this.blockLightLimit;
        }

        public InclusiveRange<Integer> skyLightLimit() {
            return this.skyLightLimit;
        }
    }
}

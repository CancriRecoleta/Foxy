//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.dimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.nio.file.Path;
import java.util.Optional;
import java.util.OptionalLong;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public record DimensionType(OptionalLong fixedTime, boolean hasSkyLight, boolean hasCeiling, boolean ultraWarm, boolean natural, double coordinateScale, boolean bedWorks, boolean respawnAnchorWorks, int minY, int height, int logicalHeight, TagKey<Block> infiniburn, ResourceLocation effectsLocation, float ambientLight, MonsterSettings monsterSettings) {
    public static final int BITS_FOR_Y;
    public static final int MIN_HEIGHT = 16;
    public static final int Y_SIZE;
    public static final int MAX_Y;
    public static final int MIN_Y;
    public static final int WAY_ABOVE_MAX_Y;
    public static final int WAY_BELOW_MIN_Y;
    public static final Codec<DimensionType> DIRECT_CODEC;
    private static final int MOON_PHASES = 8;
    public static final float[] MOON_BRIGHTNESS_PER_PHASE;
    public static final Codec<Holder<DimensionType>> CODEC;

    public DimensionType(OptionalLong fixedTime, boolean hasSkyLight, boolean hasCeiling, boolean ultraWarm, boolean natural, double coordinateScale, boolean bedWorks, boolean respawnAnchorWorks, int minY, int height, int logicalHeight, TagKey<Block> infiniburn, ResourceLocation effectsLocation, float ambientLight, MonsterSettings monsterSettings) {
        if (height < 16) {
            throw new IllegalStateException("height has to be at least 16");
        } else if (minY + height > MAX_Y + 1) {
            throw new IllegalStateException("min_y + height cannot be higher than: " + (MAX_Y + 1));
        } else if (logicalHeight > height) {
            throw new IllegalStateException("logical_height cannot be higher than height");
        } else if (height % 16 != 0) {
            throw new IllegalStateException("height has to be multiple of 16");
        } else if (minY % 16 != 0) {
            throw new IllegalStateException("min_y has to be a multiple of 16");
        } else {
            this.fixedTime = fixedTime;
            this.hasSkyLight = hasSkyLight;
            this.hasCeiling = hasCeiling;
            this.ultraWarm = ultraWarm;
            this.natural = natural;
            this.coordinateScale = coordinateScale;
            this.bedWorks = bedWorks;
            this.respawnAnchorWorks = respawnAnchorWorks;
            this.minY = minY;
            this.height = height;
            this.logicalHeight = logicalHeight;
            this.infiniburn = infiniburn;
            this.effectsLocation = effectsLocation;
            this.ambientLight = ambientLight;
            this.monsterSettings = monsterSettings;
        }
    }

    /** @deprecated */
    @Deprecated
    public static DataResult<ResourceKey<Level>> parseLegacy(Dynamic<?> p_63912_) {
        Optional<Number> $$1 = p_63912_.asNumber().result();
        if ($$1.isPresent()) {
            int $$2 = ((Number)$$1.get()).intValue();
            if ($$2 == -1) {
                return DataResult.success(Level.NETHER);
            }

            if ($$2 == 0) {
                return DataResult.success(Level.OVERWORLD);
            }

            if ($$2 == 1) {
                return DataResult.success(Level.END);
            }
        }

        return Level.RESOURCE_KEY_CODEC.parse(p_63912_);
    }

    public static double getTeleportationScale(DimensionType p_63909_, DimensionType p_63910_) {
        double $$2 = p_63909_.coordinateScale();
        double $$3 = p_63910_.coordinateScale();
        return $$2 / $$3;
    }

    public static Path getStorageFolder(ResourceKey<Level> p_196976_, Path p_196977_) {
        if (p_196976_ == Level.OVERWORLD) {
            return p_196977_;
        } else if (p_196976_ == Level.END) {
            return p_196977_.resolve("DIM1");
        } else {
            return p_196976_ == Level.NETHER ? p_196977_.resolve("DIM-1") : p_196977_.resolve("dimensions").resolve(p_196976_.location().getNamespace()).resolve(p_196976_.location().getPath());
        }
    }

    public boolean hasFixedTime() {
        return this.fixedTime.isPresent();
    }

    public float timeOfDay(long p_63905_) {
        double $$1 = Mth.frac((double)this.fixedTime.orElse(p_63905_) / 24000.0 - 0.25);
        double $$2 = 0.5 - Math.cos($$1 * Math.PI) / 2.0;
        return (float)($$1 * 2.0 + $$2) / 3.0F;
    }

    public int moonPhase(long p_63937_) {
        return (int)(p_63937_ / 24000L % 8L + 8L) % 8;
    }

    public boolean piglinSafe() {
        return this.monsterSettings.piglinSafe();
    }

    public boolean hasRaids() {
        return this.monsterSettings.hasRaids();
    }

    public IntProvider monsterSpawnLightTest() {
        return this.monsterSettings.monsterSpawnLightTest();
    }

    public int monsterSpawnBlockLightLimit() {
        return this.monsterSettings.monsterSpawnBlockLightLimit();
    }

    public OptionalLong fixedTime() {
        return this.fixedTime;
    }

    public boolean hasSkyLight() {
        return this.hasSkyLight;
    }

    public boolean hasCeiling() {
        return this.hasCeiling;
    }

    public boolean ultraWarm() {
        return this.ultraWarm;
    }

    public boolean natural() {
        return this.natural;
    }

    public double coordinateScale() {
        return this.coordinateScale;
    }

    public boolean bedWorks() {
        return this.bedWorks;
    }

    public boolean respawnAnchorWorks() {
        return this.respawnAnchorWorks;
    }

    public int minY() {
        return this.minY;
    }

    public int height() {
        return this.height;
    }

    public int logicalHeight() {
        return this.logicalHeight;
    }

    public TagKey<Block> infiniburn() {
        return this.infiniburn;
    }

    public ResourceLocation effectsLocation() {
        return this.effectsLocation;
    }

    public float ambientLight() {
        return this.ambientLight;
    }

    public MonsterSettings monsterSettings() {
        return this.monsterSettings;
    }

    static {
        BITS_FOR_Y = BlockPos.PACKED_Y_LENGTH;
        Y_SIZE = (1 << BITS_FOR_Y) - 32;
        MAX_Y = (Y_SIZE >> 1) - 1;
        MIN_Y = MAX_Y - Y_SIZE + 1;
        WAY_ABOVE_MAX_Y = MAX_Y << 4;
        WAY_BELOW_MIN_Y = MIN_Y << 4;
        DIRECT_CODEC = ExtraCodecs.catchDecoderException(RecordCodecBuilder.create((p_223568_) -> {
            return p_223568_.group(ExtraCodecs.asOptionalLong(Codec.LONG.optionalFieldOf("fixed_time")).forGetter(DimensionType::fixedTime), Codec.BOOL.fieldOf("has_skylight").forGetter(DimensionType::hasSkyLight), Codec.BOOL.fieldOf("has_ceiling").forGetter(DimensionType::hasCeiling), Codec.BOOL.fieldOf("ultrawarm").forGetter(DimensionType::ultraWarm), Codec.BOOL.fieldOf("natural").forGetter(DimensionType::natural), Codec.doubleRange(9.999999747378752E-6, 3.0E7).fieldOf("coordinate_scale").forGetter(DimensionType::coordinateScale), Codec.BOOL.fieldOf("bed_works").forGetter(DimensionType::bedWorks), Codec.BOOL.fieldOf("respawn_anchor_works").forGetter(DimensionType::respawnAnchorWorks), Codec.intRange(MIN_Y, MAX_Y).fieldOf("min_y").forGetter(DimensionType::minY), Codec.intRange(16, Y_SIZE).fieldOf("height").forGetter(DimensionType::height), Codec.intRange(0, Y_SIZE).fieldOf("logical_height").forGetter(DimensionType::logicalHeight), TagKey.hashedCodec(Registries.BLOCK).fieldOf("infiniburn").forGetter(DimensionType::infiniburn), ResourceLocation.CODEC.fieldOf("effects").orElse(BuiltinDimensionTypes.OVERWORLD_EFFECTS).forGetter(DimensionType::effectsLocation), Codec.FLOAT.fieldOf("ambient_light").forGetter(DimensionType::ambientLight), net.minecraft.world.level.dimension.DimensionType.MonsterSettings.CODEC.forGetter(DimensionType::monsterSettings)).apply(p_223568_, DimensionType::new);
        }));
        MOON_BRIGHTNESS_PER_PHASE = new float[]{1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
        CODEC = RegistryFileCodec.create(Registries.DIMENSION_TYPE, DIRECT_CODEC);
    }

    public static record MonsterSettings(boolean piglinSafe, boolean hasRaids, IntProvider monsterSpawnLightTest, int monsterSpawnBlockLightLimit) {
        public static final MapCodec<MonsterSettings> CODEC = RecordCodecBuilder.mapCodec((p_223591_) -> {
            return p_223591_.group(Codec.BOOL.fieldOf("piglin_safe").forGetter(MonsterSettings::piglinSafe), Codec.BOOL.fieldOf("has_raids").forGetter(MonsterSettings::hasRaids), IntProvider.codec(0, 15).fieldOf("monster_spawn_light_level").forGetter(MonsterSettings::monsterSpawnLightTest), Codec.intRange(0, 15).fieldOf("monster_spawn_block_light_limit").forGetter(MonsterSettings::monsterSpawnBlockLightLimit)).apply(p_223591_, MonsterSettings::new);
        });

        public MonsterSettings(boolean piglinSafe, boolean hasRaids, IntProvider monsterSpawnLightTest, int monsterSpawnBlockLightLimit) {
            this.piglinSafe = piglinSafe;
            this.hasRaids = hasRaids;
            this.monsterSpawnLightTest = monsterSpawnLightTest;
            this.monsterSpawnBlockLightLimit = monsterSpawnBlockLightLimit;
        }

        public boolean piglinSafe() {
            return this.piglinSafe;
        }

        public boolean hasRaids() {
            return this.hasRaids;
        }

        public IntProvider monsterSpawnLightTest() {
            return this.monsterSpawnLightTest;
        }

        public int monsterSpawnBlockLightLimit() {
            return this.monsterSpawnBlockLightLimit;
        }
    }
}

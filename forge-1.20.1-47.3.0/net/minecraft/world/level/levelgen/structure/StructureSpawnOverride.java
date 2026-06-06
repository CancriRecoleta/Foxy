//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;

public record StructureSpawnOverride(BoundingBoxType boundingBox, WeightedRandomList<MobSpawnSettings.SpawnerData> spawns) {
    public static final Codec<StructureSpawnOverride> CODEC = RecordCodecBuilder.create((p_210051_) -> {
        return p_210051_.group(net.minecraft.world.level.levelgen.structure.StructureSpawnOverride.BoundingBoxType.CODEC.fieldOf("bounding_box").forGetter(StructureSpawnOverride::boundingBox), WeightedRandomList.codec(SpawnerData.CODEC).fieldOf("spawns").forGetter(StructureSpawnOverride::spawns)).apply(p_210051_, StructureSpawnOverride::new);
    });

    public StructureSpawnOverride(BoundingBoxType boundingBox, WeightedRandomList<MobSpawnSettings.SpawnerData> spawns) {
        this.boundingBox = boundingBox;
        this.spawns = spawns;
    }

    public BoundingBoxType boundingBox() {
        return this.boundingBox;
    }

    public WeightedRandomList<MobSpawnSettings.SpawnerData> spawns() {
        return this.spawns;
    }

    public static enum BoundingBoxType implements StringRepresentable {
        PIECE("piece"),
        STRUCTURE("full");

        public static final Codec<BoundingBoxType> CODEC = StringRepresentable.fromEnum(BoundingBoxType::values);
        private final String id;

        private BoundingBoxType(String p_210067_) {
            this.id = p_210067_;
        }

        public String getSerializedName() {
            return this.id;
        }
    }
}

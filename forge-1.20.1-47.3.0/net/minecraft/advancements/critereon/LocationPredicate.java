//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.MinMaxBounds.Doubles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.slf4j.Logger;

public class LocationPredicate {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final LocationPredicate ANY;
    private final MinMaxBounds.Doubles x;
    private final MinMaxBounds.Doubles y;
    private final MinMaxBounds.Doubles z;
    @Nullable
    private final ResourceKey<Biome> biome;
    @Nullable
    private final ResourceKey<Structure> structure;
    @Nullable
    private final ResourceKey<Level> dimension;
    @Nullable
    private final Boolean smokey;
    private final LightPredicate light;
    private final BlockPredicate block;
    private final FluidPredicate fluid;

    public LocationPredicate(MinMaxBounds.Doubles p_207916_, MinMaxBounds.Doubles p_207917_, MinMaxBounds.Doubles p_207918_, @Nullable ResourceKey<Biome> p_207919_, @Nullable ResourceKey<Structure> p_207920_, @Nullable ResourceKey<Level> p_207921_, @Nullable Boolean p_207922_, LightPredicate p_207923_, BlockPredicate p_207924_, FluidPredicate p_207925_) {
        this.x = p_207916_;
        this.y = p_207917_;
        this.z = p_207918_;
        this.biome = p_207919_;
        this.structure = p_207920_;
        this.dimension = p_207921_;
        this.smokey = p_207922_;
        this.light = p_207923_;
        this.block = p_207924_;
        this.fluid = p_207925_;
    }

    public static LocationPredicate inBiome(ResourceKey<Biome> p_52635_) {
        return new LocationPredicate(Doubles.ANY, Doubles.ANY, Doubles.ANY, p_52635_, (ResourceKey)null, (ResourceKey)null, (Boolean)null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
    }

    public static LocationPredicate inDimension(ResourceKey<Level> p_52639_) {
        return new LocationPredicate(Doubles.ANY, Doubles.ANY, Doubles.ANY, (ResourceKey)null, (ResourceKey)null, p_52639_, (Boolean)null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
    }

    public static LocationPredicate inStructure(ResourceKey<Structure> p_220590_) {
        return new LocationPredicate(Doubles.ANY, Doubles.ANY, Doubles.ANY, (ResourceKey)null, p_220590_, (ResourceKey)null, (Boolean)null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
    }

    public static LocationPredicate atYLocation(MinMaxBounds.Doubles p_187443_) {
        return new LocationPredicate(Doubles.ANY, p_187443_, Doubles.ANY, (ResourceKey)null, (ResourceKey)null, (ResourceKey)null, (Boolean)null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
    }

    public boolean matches(ServerLevel p_52618_, double p_52619_, double p_52620_, double p_52621_) {
        if (!this.x.matches(p_52619_)) {
            return false;
        } else if (!this.y.matches(p_52620_)) {
            return false;
        } else if (!this.z.matches(p_52621_)) {
            return false;
        } else if (this.dimension != null && this.dimension != p_52618_.dimension()) {
            return false;
        } else {
            BlockPos $$4 = BlockPos.containing(p_52619_, p_52620_, p_52621_);
            boolean $$5 = p_52618_.isLoaded($$4);
            if (this.biome != null && (!$$5 || !p_52618_.getBiome($$4).is(this.biome))) {
                return false;
            } else if (this.structure != null && (!$$5 || !p_52618_.structureManager().getStructureWithPieceAt($$4, this.structure).isValid())) {
                return false;
            } else if (this.smokey != null && (!$$5 || this.smokey != CampfireBlock.isSmokeyPos(p_52618_, $$4))) {
                return false;
            } else if (!this.light.matches(p_52618_, $$4)) {
                return false;
            } else if (!this.block.matches(p_52618_, $$4)) {
                return false;
            } else {
                return this.fluid.matches(p_52618_, $$4);
            }
        }
    }

    public JsonElement serializeToJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject $$0 = new JsonObject();
            if (!this.x.isAny() || !this.y.isAny() || !this.z.isAny()) {
                JsonObject $$1 = new JsonObject();
                $$1.add("x", this.x.serializeToJson());
                $$1.add("y", this.y.serializeToJson());
                $$1.add("z", this.z.serializeToJson());
                $$0.add("position", $$1);
            }

            if (this.dimension != null) {
                DataResult var10000 = Level.RESOURCE_KEY_CODEC.encodeStart(JsonOps.INSTANCE, this.dimension);
                Logger var10001 = LOGGER;
                Objects.requireNonNull(var10001);
                var10000.resultOrPartial(var10001::error).ifPresent((p_52633_) -> {
                    $$0.add("dimension", p_52633_);
                });
            }

            if (this.structure != null) {
                $$0.addProperty("structure", this.structure.location().toString());
            }

            if (this.biome != null) {
                $$0.addProperty("biome", this.biome.location().toString());
            }

            if (this.smokey != null) {
                $$0.addProperty("smokey", this.smokey);
            }

            $$0.add("light", this.light.serializeToJson());
            $$0.add("block", this.block.serializeToJson());
            $$0.add("fluid", this.fluid.serializeToJson());
            return $$0;
        }
    }

    public static LocationPredicate fromJson(@Nullable JsonElement p_52630_) {
        if (p_52630_ != null && !p_52630_.isJsonNull()) {
            JsonObject $$1 = GsonHelper.convertToJsonObject(p_52630_, "location");
            JsonObject $$2 = GsonHelper.getAsJsonObject($$1, "position", new JsonObject());
            MinMaxBounds.Doubles $$3 = Doubles.fromJson($$2.get("x"));
            MinMaxBounds.Doubles $$4 = Doubles.fromJson($$2.get("y"));
            MinMaxBounds.Doubles $$5 = Doubles.fromJson($$2.get("z"));
            ResourceKey var14;
            DataResult var10000;
            Logger var10001;
            if ($$1.has("dimension")) {
                var10000 = ResourceLocation.CODEC.parse(JsonOps.INSTANCE, $$1.get("dimension"));
                var10001 = LOGGER;
                Objects.requireNonNull(var10001);
                var14 = (ResourceKey)var10000.resultOrPartial(var10001::error).map((p_52637_) -> {
                    return ResourceKey.create(Registries.DIMENSION, p_52637_);
                }).orElse((Object)null);
            } else {
                var14 = null;
            }

            ResourceKey<Level> $$6 = var14;
            if ($$1.has("structure")) {
                var10000 = ResourceLocation.CODEC.parse(JsonOps.INSTANCE, $$1.get("structure"));
                var10001 = LOGGER;
                Objects.requireNonNull(var10001);
                var14 = (ResourceKey)var10000.resultOrPartial(var10001::error).map((p_207927_) -> {
                    return ResourceKey.create(Registries.STRUCTURE, p_207927_);
                }).orElse((Object)null);
            } else {
                var14 = null;
            }

            ResourceKey<Structure> $$7 = var14;
            ResourceKey<Biome> $$8 = null;
            if ($$1.has("biome")) {
                ResourceLocation $$9 = new ResourceLocation(GsonHelper.getAsString($$1, "biome"));
                $$8 = ResourceKey.create(Registries.BIOME, $$9);
            }

            Boolean $$10 = $$1.has("smokey") ? $$1.get("smokey").getAsBoolean() : null;
            LightPredicate $$11 = LightPredicate.fromJson($$1.get("light"));
            BlockPredicate $$12 = BlockPredicate.fromJson($$1.get("block"));
            FluidPredicate $$13 = FluidPredicate.fromJson($$1.get("fluid"));
            return new LocationPredicate($$3, $$4, $$5, $$8, $$7, $$6, $$10, $$11, $$12, $$13);
        } else {
            return ANY;
        }
    }

    static {
        ANY = new LocationPredicate(Doubles.ANY, Doubles.ANY, Doubles.ANY, (ResourceKey)null, (ResourceKey)null, (ResourceKey)null, (Boolean)null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
    }

    public static class Builder {
        private MinMaxBounds.Doubles x;
        private MinMaxBounds.Doubles y;
        private MinMaxBounds.Doubles z;
        @Nullable
        private ResourceKey<Biome> biome;
        @Nullable
        private ResourceKey<Structure> structure;
        @Nullable
        private ResourceKey<Level> dimension;
        @Nullable
        private Boolean smokey;
        private LightPredicate light;
        private BlockPredicate block;
        private FluidPredicate fluid;

        public Builder() {
            this.x = Doubles.ANY;
            this.y = Doubles.ANY;
            this.z = Doubles.ANY;
            this.light = LightPredicate.ANY;
            this.block = BlockPredicate.ANY;
            this.fluid = FluidPredicate.ANY;
        }

        public static Builder location() {
            return new Builder();
        }

        public Builder setX(MinMaxBounds.Doubles p_153971_) {
            this.x = p_153971_;
            return this;
        }

        public Builder setY(MinMaxBounds.Doubles p_153975_) {
            this.y = p_153975_;
            return this;
        }

        public Builder setZ(MinMaxBounds.Doubles p_153979_) {
            this.z = p_153979_;
            return this;
        }

        public Builder setBiome(@Nullable ResourceKey<Biome> p_52657_) {
            this.biome = p_52657_;
            return this;
        }

        public Builder setStructure(@Nullable ResourceKey<Structure> p_220593_) {
            this.structure = p_220593_;
            return this;
        }

        public Builder setDimension(@Nullable ResourceKey<Level> p_153977_) {
            this.dimension = p_153977_;
            return this;
        }

        public Builder setLight(LightPredicate p_153969_) {
            this.light = p_153969_;
            return this;
        }

        public Builder setBlock(BlockPredicate p_52653_) {
            this.block = p_52653_;
            return this;
        }

        public Builder setFluid(FluidPredicate p_153967_) {
            this.fluid = p_153967_;
            return this;
        }

        public Builder setSmokey(Boolean p_52655_) {
            this.smokey = p_52655_;
            return this;
        }

        public LocationPredicate build() {
            return new LocationPredicate(this.x, this.y, this.z, this.biome, this.structure, this.dimension, this.smokey, this.light, this.block, this.fluid);
        }
    }
}

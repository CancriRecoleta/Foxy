//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.client.model.generators;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ObjectArrays;
import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.IntStream;
import net.minecraft.client.resources.model.BlockModelRotation;
import org.jetbrains.annotations.Nullable;

public final class ConfiguredModel {
    public static final int DEFAULT_WEIGHT = 1;
    public final ModelFile model;
    public final int rotationX;
    public final int rotationY;
    public final boolean uvLock;
    public final int weight;

    private static IntStream validRotations() {
        return IntStream.range(0, 4).map((i) -> {
            return i * 90;
        });
    }

    public static ConfiguredModel[] allYRotations(ModelFile model, int x, boolean uvlock) {
        return allYRotations(model, x, uvlock, 1);
    }

    public static ConfiguredModel[] allYRotations(ModelFile model, int x, boolean uvlock, int weight) {
        return (ConfiguredModel[])validRotations().mapToObj((y) -> {
            return new ConfiguredModel(model, x, y, uvlock, weight);
        }).toArray((x$0) -> {
            return new ConfiguredModel[x$0];
        });
    }

    public static ConfiguredModel[] allRotations(ModelFile model, boolean uvlock) {
        return allRotations(model, uvlock, 1);
    }

    public static ConfiguredModel[] allRotations(ModelFile model, boolean uvlock, int weight) {
        return (ConfiguredModel[])validRotations().mapToObj((x) -> {
            return allYRotations(model, x, uvlock, weight);
        }).flatMap(Arrays::stream).toArray((x$0) -> {
            return new ConfiguredModel[x$0];
        });
    }

    public ConfiguredModel(ModelFile model, int rotationX, int rotationY, boolean uvLock, int weight) {
        Preconditions.checkNotNull(model);
        this.model = model;
        checkRotation(rotationX, rotationY);
        this.rotationX = rotationX;
        this.rotationY = rotationY;
        this.uvLock = uvLock;
        checkWeight(weight);
        this.weight = weight;
    }

    public ConfiguredModel(ModelFile model, int rotationX, int rotationY, boolean uvLock) {
        this(model, rotationX, rotationY, uvLock, 1);
    }

    public ConfiguredModel(ModelFile model) {
        this(model, 0, 0, false);
    }

    static void checkRotation(int rotationX, int rotationY) {
        Preconditions.checkArgument(BlockModelRotation.by(rotationX, rotationY) != null, "Invalid model rotation x=%d, y=%d", rotationX, rotationY);
    }

    static void checkWeight(int weight) {
        Preconditions.checkArgument(weight >= 1, "Model weight must be greater than or equal to 1. Found: %d", weight);
    }

    JsonObject toJSON(boolean includeWeight) {
        JsonObject modelJson = new JsonObject();
        modelJson.addProperty("model", this.model.getLocation().toString());
        if (this.rotationX != 0) {
            modelJson.addProperty("x", this.rotationX);
        }

        if (this.rotationY != 0) {
            modelJson.addProperty("y", this.rotationY);
        }

        if (this.uvLock) {
            modelJson.addProperty("uvlock", this.uvLock);
        }

        if (includeWeight && this.weight != 1) {
            modelJson.addProperty("weight", this.weight);
        }

        return modelJson;
    }

    public static Builder<?> builder() {
        return new Builder();
    }

    static Builder<VariantBlockStateBuilder> builder(VariantBlockStateBuilder outer, VariantBlockStateBuilder.PartialBlockstate state) {
        return new Builder((models) -> {
            return outer.setModels(state, models);
        }, ImmutableList.of());
    }

    static Builder<MultiPartBlockStateBuilder.PartBuilder> builder(MultiPartBlockStateBuilder outer) {
        return new Builder((models) -> {
            Objects.requireNonNull(outer);
            MultiPartBlockStateBuilder.PartBuilder ret = outer.new PartBuilder(new BlockStateProvider.ConfiguredModelList(models));
            outer.addPart(ret);
            return ret;
        }, ImmutableList.of());
    }

    public static class Builder<T> {
        private ModelFile model;
        private final @Nullable Function<ConfiguredModel[], T> callback;
        private final List<ConfiguredModel> otherModels;
        private int rotationX;
        private int rotationY;
        private boolean uvLock;
        private int weight;

        Builder() {
            this((Function)null, ImmutableList.of());
        }

        Builder(@Nullable Function<ConfiguredModel[], T> callback, List<ConfiguredModel> otherModels) {
            this.weight = 1;
            this.callback = callback;
            this.otherModels = otherModels;
        }

        public Builder<T> modelFile(ModelFile model) {
            Preconditions.checkNotNull(model, "Model must not be null");
            this.model = model;
            return this;
        }

        public Builder<T> rotationX(int value) {
            ConfiguredModel.checkRotation(value, this.rotationY);
            this.rotationX = value;
            return this;
        }

        public Builder<T> rotationY(int value) {
            ConfiguredModel.checkRotation(this.rotationX, value);
            this.rotationY = value;
            return this;
        }

        public Builder<T> uvLock(boolean value) {
            this.uvLock = value;
            return this;
        }

        public Builder<T> weight(int value) {
            ConfiguredModel.checkWeight(value);
            this.weight = value;
            return this;
        }

        public ConfiguredModel buildLast() {
            return new ConfiguredModel(this.model, this.rotationX, this.rotationY, this.uvLock, this.weight);
        }

        public ConfiguredModel[] build() {
            return (ConfiguredModel[])ObjectArrays.concat((ConfiguredModel[])this.otherModels.toArray(new ConfiguredModel[0]), this.buildLast());
        }

        public T addModel() {
            Preconditions.checkNotNull(this.callback, "Cannot use addModel() without an owning builder present");
            return this.callback.apply(this.build());
        }

        public Builder<T> nextModel() {
            return new Builder(this.callback, Arrays.asList(this.build()));
        }
    }
}

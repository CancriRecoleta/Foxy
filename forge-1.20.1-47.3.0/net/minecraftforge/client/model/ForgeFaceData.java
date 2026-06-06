//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.client.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.util.ExtraCodecs;

public record ForgeFaceData(int color, int blockLight, int skyLight, boolean ambientOcclusion, boolean calculateNormals) {
    public static final ForgeFaceData DEFAULT = new ForgeFaceData(-1, 0, 0, true, false);
    public static final Codec<Integer> COLOR;
    public static final Codec<ForgeFaceData> CODEC;

    public ForgeFaceData(int color, int blockLight, int skyLight, boolean ambientOcclusion) {
        this(color, blockLight, skyLight, ambientOcclusion, false);
    }

    public ForgeFaceData(int color, int blockLight, int skyLight, boolean ambientOcclusion, boolean calculateNormals) {
        this.color = color;
        this.blockLight = blockLight;
        this.skyLight = skyLight;
        this.ambientOcclusion = ambientOcclusion;
        this.calculateNormals = calculateNormals;
    }

    @Nullable
    public static ForgeFaceData read(@Nullable JsonElement obj, @Nullable ForgeFaceData fallback) throws JsonParseException {
        return obj == null ? fallback : (ForgeFaceData)CODEC.parse(JsonOps.INSTANCE, obj).getOrThrow(false, JsonParseException::new);
    }

    public int color() {
        return this.color;
    }

    public int blockLight() {
        return this.blockLight;
    }

    public int skyLight() {
        return this.skyLight;
    }

    public boolean ambientOcclusion() {
        return this.ambientOcclusion;
    }

    public boolean calculateNormals() {
        return this.calculateNormals;
    }

    static {
        COLOR = (new ExtraCodecs.EitherCodec(Codec.INT, Codec.STRING)).xmap((either) -> {
            return (Integer)either.map(Function.identity(), (str) -> {
                return (int)Long.parseLong(str, 16);
            });
        }, (color) -> {
            return Either.right(Integer.toHexString(color));
        });
        CODEC = RecordCodecBuilder.create((builder) -> {
            return builder.group(COLOR.optionalFieldOf("color", -1).forGetter(ForgeFaceData::color), Codec.intRange(0, 15).optionalFieldOf("block_light", 0).forGetter(ForgeFaceData::blockLight), Codec.intRange(0, 15).optionalFieldOf("sky_light", 0).forGetter(ForgeFaceData::skyLight), Codec.BOOL.optionalFieldOf("ambient_occlusion", true).forGetter(ForgeFaceData::ambientOcclusion), Codec.BOOL.optionalFieldOf("calculate_normals", false).forGetter(ForgeFaceData::calculateNormals)).apply(builder, ForgeFaceData::new);
        });
    }
}

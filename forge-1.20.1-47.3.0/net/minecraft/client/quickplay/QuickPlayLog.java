//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.quickplay;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class QuickPlayLog {
    private static final QuickPlayLog INACTIVE = new QuickPlayLog("") {
        public void log(Minecraft p_279484_) {
        }

        public void setWorldData(Type p_279348_, String p_279305_, String p_279177_) {
        }
    };
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = (new GsonBuilder()).create();
    private final Path path;
    @Nullable
    private QuickPlayWorld worldData;

    QuickPlayLog(String p_279463_) {
        this.path = Minecraft.getInstance().gameDirectory.toPath().resolve(p_279463_);
    }

    public static QuickPlayLog of(@Nullable String p_279275_) {
        return p_279275_ == null ? INACTIVE : new QuickPlayLog(p_279275_);
    }

    public void setWorldData(Type p_279380_, String p_279427_, String p_279470_) {
        this.worldData = new QuickPlayWorld(p_279380_, p_279427_, p_279470_);
    }

    public void log(Minecraft p_279258_) {
        if (p_279258_.gameMode != null && this.worldData != null) {
            Util.ioPool().execute(() -> {
                try {
                    Files.deleteIfExists(this.path);
                } catch (IOException var3) {
                    IOException $$1 = var3;
                    LOGGER.error("Failed to delete quickplay log file {}", this.path, $$1);
                }

                QuickPlayEntry $$2 = new QuickPlayEntry(this.worldData, Instant.now(), p_279258_.gameMode.getPlayerMode());
                DataResult var10000 = Codec.list(net.minecraft.client.quickplay.QuickPlayLog.QuickPlayEntry.CODEC).encodeStart(JsonOps.INSTANCE, List.of($$2));
                Logger var10002 = LOGGER;
                Objects.requireNonNull(var10002);
                var10000.resultOrPartial(Util.prefix("Quick Play: ", var10002::error)).ifPresent((p_279238_) -> {
                    try {
                        Files.createDirectories(this.path.getParent());
                        Files.writeString(this.path, GSON.toJson(p_279238_));
                    } catch (IOException var3) {
                        IOException $$1 = var3;
                        LOGGER.error("Failed to write to quickplay log file {}", this.path, $$1);
                    }

                });
            });
        } else {
            LOGGER.error("Failed to log session for quickplay. Missing world data or gamemode");
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static record QuickPlayWorld(Type type, String id, String name) {
        public static final MapCodec<QuickPlayWorld> MAP_CODEC = RecordCodecBuilder.mapCodec((p_279181_) -> {
            return p_279181_.group(net.minecraft.client.quickplay.QuickPlayLog.Type.CODEC.fieldOf("type").forGetter(QuickPlayWorld::type), Codec.STRING.fieldOf("id").forGetter(QuickPlayWorld::id), Codec.STRING.fieldOf("name").forGetter(QuickPlayWorld::name)).apply(p_279181_, QuickPlayWorld::new);
        });

        QuickPlayWorld(Type type, String id, String name) {
            this.type = type;
            this.id = id;
            this.name = name;
        }

        public Type type() {
            return this.type;
        }

        public String id() {
            return this.id;
        }

        public String name() {
            return this.name;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static enum Type implements StringRepresentable {
        SINGLEPLAYER("singleplayer"),
        MULTIPLAYER("multiplayer"),
        REALMS("realms");

        static final Codec<Type> CODEC = StringRepresentable.fromEnum(Type::values);
        private final String name;

        private Type(String p_279349_) {
            this.name = p_279349_;
        }

        public String getSerializedName() {
            return this.name;
        }
    }

    @OnlyIn(Dist.CLIENT)
    static record QuickPlayEntry(QuickPlayWorld quickPlayWorld, Instant lastPlayedTime, GameType gamemode) {
        public static final Codec<QuickPlayEntry> CODEC = RecordCodecBuilder.create((p_279196_) -> {
            return p_279196_.group(net.minecraft.client.quickplay.QuickPlayLog.QuickPlayWorld.MAP_CODEC.forGetter(QuickPlayEntry::quickPlayWorld), ExtraCodecs.INSTANT_ISO8601.fieldOf("lastPlayedTime").forGetter(QuickPlayEntry::lastPlayedTime), GameType.CODEC.fieldOf("gamemode").forGetter(QuickPlayEntry::gamemode)).apply(p_279196_, QuickPlayEntry::new);
        });

        QuickPlayEntry(QuickPlayWorld quickPlayWorld, Instant lastPlayedTime, GameType gamemode) {
            this.quickPlayWorld = quickPlayWorld;
            this.lastPlayedTime = lastPlayedTime;
            this.gamemode = gamemode;
        }

        public QuickPlayWorld quickPlayWorld() {
            return this.quickPlayWorld;
        }

        public Instant lastPlayedTime() {
            return this.lastPlayedTime;
        }

        public GameType gamemode() {
            return this.gamemode;
        }
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network.protocol.status;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import net.minecraft.SharedConstants;
import net.minecraft.WorldVersion;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ExtraCodecs;
import net.minecraftforge.network.ServerStatusPing;

public record ServerStatus(Component description, Optional<Players> players, Optional<Version> version, Optional<Favicon> favicon, boolean enforcesSecureChat, Optional<ServerStatusPing> forgeData) {
    public static final Codec<ServerStatus> CODEC = RecordCodecBuilder.create((p_273270_) -> {
        return p_273270_.group(ExtraCodecs.COMPONENT.optionalFieldOf("description", CommonComponents.EMPTY).forGetter(ServerStatus::description), net.minecraft.network.protocol.status.ServerStatus.Players.CODEC.optionalFieldOf("players").forGetter(ServerStatus::players), net.minecraft.network.protocol.status.ServerStatus.Version.CODEC.optionalFieldOf("version").forGetter(ServerStatus::version), net.minecraft.network.protocol.status.ServerStatus.Favicon.CODEC.optionalFieldOf("favicon").forGetter(ServerStatus::favicon), Codec.BOOL.optionalFieldOf("enforcesSecureChat", false).forGetter(ServerStatus::enforcesSecureChat), ServerStatusPing.CODEC.optionalFieldOf("forgeData").forGetter(ServerStatus::forgeData)).apply(p_273270_, ServerStatus::new);
    });

    public ServerStatus(Component description, Optional<Players> players, Optional<Version> version, Optional<Favicon> favicon, boolean enforcesSecureChat, Optional<ServerStatusPing> forgeData) {
        this.description = description;
        this.players = players;
        this.version = version;
        this.favicon = favicon;
        this.enforcesSecureChat = enforcesSecureChat;
        this.forgeData = forgeData;
    }

    public Component description() {
        return this.description;
    }

    public Optional<Players> players() {
        return this.players;
    }

    public Optional<Version> version() {
        return this.version;
    }

    public Optional<Favicon> favicon() {
        return this.favicon;
    }

    public boolean enforcesSecureChat() {
        return this.enforcesSecureChat;
    }

    public Optional<ServerStatusPing> forgeData() {
        return this.forgeData;
    }

    public static record Players(int max, int online, List<GameProfile> sample) {
        private static final Codec<GameProfile> PROFILE_CODEC = RecordCodecBuilder.create((p_272926_) -> {
            return p_272926_.group(UUIDUtil.STRING_CODEC.fieldOf("id").forGetter(GameProfile::getId), Codec.STRING.fieldOf("name").forGetter(GameProfile::getName)).apply(p_272926_, GameProfile::new);
        });
        public static final Codec<Players> CODEC = RecordCodecBuilder.create((p_273295_) -> {
            return p_273295_.group(Codec.INT.fieldOf("max").forGetter(Players::max), Codec.INT.fieldOf("online").forGetter(Players::online), PROFILE_CODEC.listOf().optionalFieldOf("sample", List.of()).forGetter(Players::sample)).apply(p_273295_, Players::new);
        });

        public Players(int max, int online, List<GameProfile> sample) {
            this.max = max;
            this.online = online;
            this.sample = sample;
        }

        public int max() {
            return this.max;
        }

        public int online() {
            return this.online;
        }

        public List<GameProfile> sample() {
            return this.sample;
        }
    }

    public static record Version(String name, int protocol) {
        public static final Codec<Version> CODEC = RecordCodecBuilder.create((p_273157_) -> {
            return p_273157_.group(Codec.STRING.fieldOf("name").forGetter(Version::name), Codec.INT.fieldOf("protocol").forGetter(Version::protocol)).apply(p_273157_, Version::new);
        });

        public Version(String name, int protocol) {
            this.name = name;
            this.protocol = protocol;
        }

        public static Version current() {
            WorldVersion worldversion = SharedConstants.getCurrentVersion();
            return new Version(worldversion.getName(), worldversion.getProtocolVersion());
        }

        public String name() {
            return this.name;
        }

        public int protocol() {
            return this.protocol;
        }
    }

    public static record Favicon(byte[] iconBytes) {
        private static final String PREFIX = "data:image/png;base64,";
        public static final Codec<Favicon> CODEC;

        public Favicon(byte[] iconBytes) {
            this.iconBytes = iconBytes;
        }

        public byte[] iconBytes() {
            return this.iconBytes;
        }

        static {
            CODEC = Codec.STRING.comapFlatMap((p_274795_) -> {
                if (!p_274795_.startsWith("data:image/png;base64,")) {
                    return DataResult.error(() -> {
                        return "Unknown format";
                    });
                } else {
                    try {
                        String s = p_274795_.substring("data:image/png;base64,".length()).replaceAll("\n", "");
                        byte[] abyte = Base64.getDecoder().decode(s.getBytes(StandardCharsets.UTF_8));
                        return DataResult.success(new Favicon(abyte));
                    } catch (IllegalArgumentException var3) {
                        return DataResult.error(() -> {
                            return "Malformed base64 server icon";
                        });
                    }
                }
            }, (p_273258_) -> {
                String var10000 = new String(Base64.getEncoder().encode(p_273258_.iconBytes), StandardCharsets.UTF_8);
                return "data:image/png;base64," + var10000;
            });
        }
    }
}

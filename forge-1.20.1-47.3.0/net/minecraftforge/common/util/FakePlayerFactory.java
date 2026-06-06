//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common.util;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;

public class FakePlayerFactory {
    private static final GameProfile MINECRAFT = new GameProfile(UUID.fromString("41C82C87-7AfB-4024-BA57-13D2C99CAE77"), "[Minecraft]");
    private static final Map<FakePlayerKey, FakePlayer> fakePlayers = Maps.newHashMap();

    public FakePlayerFactory() {
    }

    public static FakePlayer getMinecraft(ServerLevel level) {
        return get(level, MINECRAFT);
    }

    public static FakePlayer get(ServerLevel level, GameProfile username) {
        FakePlayerKey key = new FakePlayerKey(level, username);
        return (FakePlayer)fakePlayers.computeIfAbsent(key, (k) -> {
            return new FakePlayer(k.level(), k.username());
        });
    }

    public static void unloadLevel(ServerLevel level) {
        fakePlayers.entrySet().removeIf((entry) -> {
            return ((FakePlayer)entry.getValue()).level() == level;
        });
    }

    private static record FakePlayerKey(ServerLevel level, GameProfile username) {
        private FakePlayerKey(ServerLevel level, GameProfile username) {
            this.level = level;
            this.username = username;
        }

        public ServerLevel level() {
            return this.level;
        }

        public GameProfile username() {
            return this.username;
        }
    }
}

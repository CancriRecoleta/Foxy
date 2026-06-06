//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.network;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.Type;
import org.apache.commons.lang3.tuple.Pair;

public class ConfigSync {
    public static final ConfigSync INSTANCE;
    private final ConfigTracker tracker;

    private ConfigSync(ConfigTracker tracker) {
        this.tracker = tracker;
    }

    public List<Pair<String, HandshakeMessages.S2CConfigData>> syncConfigs(boolean isLocal) {
        Map<String, byte[]> configData = (Map)((Set)this.tracker.configSets().get(Type.SERVER)).stream().collect(Collectors.toMap(ModConfig::getFileName, (mc) -> {
            try {
                return Files.readAllBytes(mc.getFullPath());
            } catch (IOException var2) {
                IOException e = var2;
                throw new RuntimeException(e);
            }
        }));
        return (List)configData.entrySet().stream().map((e) -> {
            return Pair.of("Config " + (String)e.getKey(), new HandshakeMessages.S2CConfigData((String)e.getKey(), (byte[])e.getValue()));
        }).collect(Collectors.toList());
    }

    public void receiveSyncedConfig(HandshakeMessages.S2CConfigData s2CConfigData, Supplier<NetworkEvent.Context> contextSupplier) {
        if (!Minecraft.getInstance().isLocalServer()) {
            Optional.ofNullable((ModConfig)this.tracker.fileMap().get(s2CConfigData.getFileName())).ifPresent((mc) -> {
                mc.acceptSyncedConfig(s2CConfigData.getBytes());
            });
        }

    }

    static {
        INSTANCE = new ConfigSync(ConfigTracker.INSTANCE);
    }
}

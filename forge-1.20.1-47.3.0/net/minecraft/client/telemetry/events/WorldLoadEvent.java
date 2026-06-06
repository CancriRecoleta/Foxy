//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.telemetry.events;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.telemetry.TelemetryEventSender;
import net.minecraft.client.telemetry.TelemetryEventType;
import net.minecraft.client.telemetry.TelemetryProperty;
import net.minecraft.client.telemetry.TelemetryPropertyMap;
import net.minecraft.client.telemetry.TelemetryProperty.GameMode;
import net.minecraft.client.telemetry.TelemetryProperty.ServerType;
import net.minecraft.world.level.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WorldLoadEvent {
    private boolean eventSent;
    @Nullable
    private TelemetryProperty.GameMode gameMode;
    @Nullable
    private String serverBrand;
    @Nullable
    private final String minigameName;

    public WorldLoadEvent(@Nullable String p_286661_) {
        this.minigameName = p_286661_;
    }

    public void addProperties(TelemetryPropertyMap.Builder p_261869_) {
        if (this.serverBrand != null) {
            p_261869_.put(TelemetryProperty.SERVER_MODDED, !this.serverBrand.equals("vanilla"));
        }

        p_261869_.put(TelemetryProperty.SERVER_TYPE, this.getServerType());
    }

    private TelemetryProperty.ServerType getServerType() {
        if (Minecraft.getInstance().isConnectedToRealms()) {
            return ServerType.REALM;
        } else {
            return Minecraft.getInstance().hasSingleplayerServer() ? ServerType.LOCAL : ServerType.OTHER;
        }
    }

    public boolean send(TelemetryEventSender p_263325_) {
        if (!this.eventSent && this.gameMode != null && this.serverBrand != null) {
            this.eventSent = true;
            p_263325_.send(TelemetryEventType.WORLD_LOADED, (p_286185_) -> {
                p_286185_.put(TelemetryProperty.GAME_MODE, this.gameMode);
                if (this.minigameName != null) {
                    p_286185_.put(TelemetryProperty.REALMS_MAP_CONTENT, this.minigameName);
                }

            });
            return true;
        } else {
            return false;
        }
    }

    public void setGameMode(GameType p_261852_, boolean p_261831_) {
        TelemetryProperty.GameMode var10001;
        switch (p_261852_) {
            case SURVIVAL -> var10001 = p_261831_ ? GameMode.HARDCORE : GameMode.SURVIVAL;
            case CREATIVE -> var10001 = GameMode.CREATIVE;
            case ADVENTURE -> var10001 = GameMode.ADVENTURE;
            case SPECTATOR -> var10001 = GameMode.SPECTATOR;
            default -> throw new IncompatibleClassChangeError();
        }

        this.gameMode = var10001;
    }

    public void setServerBrand(String p_261964_) {
        this.serverBrand = p_261964_;
    }
}

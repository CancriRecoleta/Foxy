package com.github.foxy.client.mixin.minecraft;

import com.github.foxy.client.ClientSessionEvents;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class MixinClientPacketListener {
    // Upstream Foxy (1.21) starts the session at ClientboundLoginPacket.commonPlayerSpawnInfo(),
    // which runs BEFORE Minecraft.setLevel(...). That ordering is load-bearing: setLevel triggers
    // LevelRenderer.allChanged -> foxy$createRenderer, which needs the Foxy instance to already
    // exist. commonPlayerSpawnInfo() does not exist on 1.20.1, and a TAIL inject runs AFTER setLevel,
    // so the renderer was created against a null instance and silently never came up (no distant
    // terrain). Inject right before the setLevel call so the instance is created first. NOTE: this is
    // before Minecraft.player is assigned, so FoxyClientInstance.getBasePath() must not use
    // Minecraft.getConnection() (== player.connection); it reads getCurrentServer() instead.
    @Inject(method = "handleLogin", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;setLevel(Lnet/minecraft/client/multiplayer/ClientLevel;)V", shift = At.Shift.BEFORE))
    private void foxy$init(ClientboundLoginPacket packet, CallbackInfo ci) {
        if (!ClientSessionEvents.inSession) {
            ClientSessionEvents.sessionStart();
        }
    }
}

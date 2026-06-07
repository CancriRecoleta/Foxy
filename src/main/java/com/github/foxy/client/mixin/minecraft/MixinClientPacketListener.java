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
    // ClientboundLoginPacket.commonPlayerSpawnInfo() is 1.20.2+; on 1.20.1 just fire at the end of
    // handleLogin (the world/session is set up by then).
    @Inject(method = "handleLogin", at = @At("TAIL"))
    private void voxy$init(ClientboundLoginPacket packet, CallbackInfo ci) {
        if (!ClientSessionEvents.inSession) {
            ClientSessionEvents.sessionStart();
        }
    }
}

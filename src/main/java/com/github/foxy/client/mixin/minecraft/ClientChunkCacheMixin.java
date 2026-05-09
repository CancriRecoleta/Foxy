package com.github.foxy.client.mixin.minecraft;

import com.github.foxy.client.ICheekyClientChunkCache;
import com.github.foxy.client.config.FoxyConfig;
import com.github.foxy.common.world.service.VoxelIngestService;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(ClientChunkCache.class)
public abstract class ClientChunkCacheMixin implements ICheekyClientChunkCache {
    @Shadow
    volatile ClientChunkCache.Storage storage;

    @Override
    public @Nullable LevelChunk foxy$cheekyGetChunk(int x, int z) {
        var chunk = this.storage.getChunk(this.storage.getIndex(x, z));
        return chunk != null && chunk.getPos().x == x && chunk.getPos().z == z ? chunk : null;
    }

    @Override
    public void foxy$forEachLoadedChunk(Consumer<LevelChunk> consumer) {
        for (int i = 0; i < this.storage.chunks.length(); i++) {
            var chunk = this.storage.getChunk(i);
            if (chunk != null) {
                consumer.accept(chunk);
            }
        }
    }

    @Inject(method = "replaceWithPacketData", at = @At("RETURN"))
    private void foxy$ingestAfterChunkPacket(int x, int z, net.minecraft.network.FriendlyByteBuf buffer,
                                             net.minecraft.nbt.CompoundTag tag,
                                             java.util.function.Consumer<net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData.BlockEntityTagOutput> consumer,
                                             CallbackInfoReturnable<LevelChunk> cir) {
        this.foxy$ingestChunk(cir.getReturnValue());
    }

    @Inject(method = "drop", at = @At("HEAD"))
    private void foxy$captureChunkBeforeUnload(int x, int z, CallbackInfo ci) {
        this.foxy$ingestChunk(this.foxy$cheekyGetChunk(x, z));
    }

    @Unique
    private void foxy$ingestChunk(@Nullable LevelChunk chunk) {
        if (chunk != null && FoxyConfig.CONFIG.ingestEnabled) {
            VoxelIngestService.tryAutoIngestChunk(chunk);
        }
    }
}

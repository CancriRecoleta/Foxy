package com.github.foxy.client.mixin.sodium;

import com.github.foxy.client.config.FoxyConfig;
import com.github.foxy.client.core.FoxyRenderSystem;
import com.github.foxy.client.core.IGetFoxyRenderSystem;
import com.github.foxy.common.world.service.VoxelIngestService;
import com.github.foxy.commonImpl.WorldIdentifier;
import me.jellysquid.mods.sodium.client.gl.device.CommandList;
import me.jellysquid.mods.sodium.client.render.chunk.RenderSection;
import me.jellysquid.mods.sodium.client.render.chunk.RenderSectionFlags;
import me.jellysquid.mods.sodium.client.render.chunk.RenderSectionManager;
import me.jellysquid.mods.sodium.client.render.chunk.compile.executor.ChunkBuilder;
import me.jellysquid.mods.sodium.client.render.chunk.data.BuiltSectionInfo;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.ChunkStatus;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderSectionManager.class, remap = false)
public abstract class RenderSectionManagerMixin {
    @Shadow
    @Final
    private ClientLevel world;

    @Shadow
    @Final
    private ChunkBuilder builder;

    @Unique
    private int foxy$bottomSectionY;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void foxy$init(ClientLevel level, int renderDistance, CommandList commandList, CallbackInfo ci) {
        this.foxy$bottomSectionY = level.getMinSection();
        var renderer = this.foxy$getRenderSystem();
        if (renderer != null) {
            renderer.chunkBoundRenderer.reset();
        }
    }

    @Inject(method = "onChunkAdded", at = @At("HEAD"))
    private void foxy$ingestOnChunkAdded(int x, int z, CallbackInfo ci) {
        this.foxy$ingestChunk(x, z);
    }

    @Inject(method = "onChunkRemoved", at = @At("HEAD"))
    private void foxy$ingestOnChunkRemoved(int x, int z, CallbackInfo ci) {
        this.foxy$ingestChunk(x, z);
    }

    @Unique
    private void foxy$ingestChunk(int x, int z) {
        if (!FoxyConfig.CONFIG.ingestEnabled) {
            return;
        }
        var chunk = this.world.getChunkSource().getChunk(x, z, ChunkStatus.FULL, false);
        if (chunk != null) {
            VoxelIngestService.tryAutoIngestChunk(chunk);
        }
    }

    @Redirect(method = "updateSectionInfo", at = @At(value = "INVOKE", target = "Lme/jellysquid/mods/sodium/client/render/chunk/RenderSection;setInfo(Lme/jellysquid/mods/sodium/client/render/chunk/data/BuiltSectionInfo;)V"))
    private void foxy$updateChunkBounds(RenderSection section, BuiltSectionInfo info) {
        boolean wasBuilt = foxy$hasBlockGeometry(section);
        section.setInfo(info);
        boolean isBuilt = foxy$hasBlockGeometry(section);

        if (wasBuilt == isBuilt) {
            return;
        }

        FoxyRenderSystem renderer = this.foxy$getRenderSystem();
        if (renderer == null) {
            return;
        }

        int x = section.getChunkX();
        int y = section.getChunkY();
        int z = section.getChunkZ();

        if ((wasBuilt || isBuilt) && FoxyConfig.CONFIG.ingestEnabled) {
            var chunk = this.world.getChunkSource().getChunk(x, z, ChunkStatus.FULL, false);
            if (chunk != null) {
                int sectionIndex = y - this.foxy$bottomSectionY;
                if (0 <= sectionIndex && sectionIndex < chunk.getSections().length) {
                    var sectionData = chunk.getSection(sectionIndex);
                    var sectionPos = SectionPos.of(x, y, z);
                    var lightEngine = this.world.getLightEngine();
                    var blockLight = lightEngine.getLayerListener(LightLayer.BLOCK).getDataLayerData(sectionPos);
                    var skyLight = lightEngine.getLayerListener(LightLayer.SKY).getDataLayerData(sectionPos);
                    VoxelIngestService.rawIngest(WorldIdentifier.of(this.world), sectionData, x, y, z, blockLight == null ? null : blockLight.copy(), skyLight == null ? null : skyLight.copy());
                }
            }
        }

        long pos = SectionPos.asLong(x, y, z);
        if (wasBuilt) {
            renderer.chunkBoundRenderer.removeSection(pos);
        } else {
            renderer.chunkBoundRenderer.addSection(pos);
        }
    }

    @Unique
    private FoxyRenderSystem foxy$getRenderSystem() {
        var levelRenderer = Minecraft.getInstance().levelRenderer;
        if (levelRenderer == null) {
            return null;
        }
        return ((IGetFoxyRenderSystem) levelRenderer).Foxy$getRenderSystem();
    }

    @Unique
    private static boolean foxy$hasBlockGeometry(RenderSection section) {
        return (section.getFlags() & (1 << RenderSectionFlags.HAS_BLOCK_GEOMETRY)) != 0;
    }
}

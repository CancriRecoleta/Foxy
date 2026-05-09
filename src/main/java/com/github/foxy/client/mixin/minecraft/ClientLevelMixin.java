package com.github.foxy.client.mixin.minecraft;

import com.github.foxy.client.config.FoxyConfig;
import com.github.foxy.common.world.service.VoxelIngestService;
import com.github.foxy.commonImpl.FoxyInstance;
import com.github.foxy.commonImpl.WorldIdentifier;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Hooks vanilla {@link ClientLevel#setBlocksDirty} to feed live block updates into
 * Foxy's voxel ingest pipeline.
 *
 * <h2>Why this is needed</h2>
 * <p>The other entry points
 * ({@link com.github.foxy.client.mixin.minecraft.ClientChunkCacheMixin
 * ClientChunkCacheMixin}'s chunk-load / chunk-drop hooks and the {@code /foxy import
 * current} command) only ingest at coarse milestones &mdash; chunk packet arrival,
 * chunk eviction, or one-shot disk import. They miss the case where the player
 * destroys a block in an already-loaded chunk: the voxel store keeps a stale block
 * at that voxel until the chunk eventually unloads. This mixin closes that gap by
 * re-ingesting the affected chunk-section whenever vanilla flips a block state.</p>
 *
 * <h2>Border filter</h2>
 * <p>For perf, the ingest is gated on whether the changed block sits on a
 * chunk-section border (any of x / y / z is 0 or 15). Internal-cell changes don't
 * affect adjacent sections' visibility data, so re-ingesting the whole section for
 * every torch-place is wasteful. Border changes are precisely the ones that need
 * the cross-section visibility / occlusion data updated.</p>
 *
 * <h2>Cleanroom adaptation vs upstream</h2>
 * <p>Upstream Voxy uses a {@code WorldIdentifier.of(Level)} call backed by an
 * {@code IWorldGetIdentifier} mixin into {@link Level}. The Foxy port avoids that
 * extra mixin by routing through {@link WorldIdentifier#of(Level)}, which derives
 * a per-dimension identifier from the active {@link FoxyInstance}'s
 * {@code namespace} + {@code biomeSeed} and the supplied level's
 * {@link Level#dimension()}. {@link FoxyInstance#getOrCreate} then lazy-builds a
 * dedicated engine per dimension on first use, so live-update ingest stays
 * routed to the correct dimension even when the player is moving between
 * overworld / nether / end during a single session.</p>
 */
@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin {

    /** Block-aligned section-Y offset; cached to skip a level lookup per ingest. */
    @Unique
    private int foxy$bottomSectionY;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void foxy$captureBottomSectionY(CallbackInfo ci) {
        Level self = (Level) (Object) this;
        // 1.20.1 uses getMinBuildHeight (1.21+ renamed to getMinY); the bottom
        // section index is needed by setBlocksDirty's chunk.getSection lookup.
        this.foxy$bottomSectionY = self.getMinBuildHeight() >> 4;
    }

    @Inject(method = "setBlocksDirty", at = @At("TAIL"))
    private void foxy$ingestOnStateChange(BlockPos pos, BlockState old, BlockState updated, CallbackInfo ci) {
        if (old == updated) return;
        // Skip place-block events; only fire on removals where the gap matters for
        // visibility. (Upstream's TODO: is this really needed for performance? In
        // practice the renderer's frame budget can't absorb an ingest per place.)
        if (!updated.isAir()) return;

        FoxyInstance instance = FoxyInstance.current();
        if (instance == null) return;
        if (!FoxyConfig.CONFIG.ingestEnabled) return;

        Level self = (Level) (Object) this;
        // Per-dimension identifier: keeps the engine, save folder and biomeSeed of
        // the active FoxyInstance, but swaps the level key for the level we're
        // actually observing. WorldIdentifier.of falls back to a default namespace
        // when no instance is active, but we already returned in that case above.
        WorldIdentifier id = WorldIdentifier.of(self);

        int lx = pos.getX() & 15;
        int ly = pos.getY() & 15;
        int lz = pos.getZ() & 15;
        // Internal-cell changes don't affect inter-section visibility; skip.
        if (lx != 0 && lx != 15 && ly != 0 && ly != 15 && lz != 0 && lz != 15) return;

        SectionPos csp = SectionPos.of(pos);
        var chunk = self.getChunk(pos.getX() >> 4, pos.getZ() >> 4, ChunkStatus.FULL, false);
        if (chunk == null) return;

        var section = chunk.getSection(csp.y() - this.foxy$bottomSectionY);
        var lightEngine = self.getLightEngine();
        var blockLight = lightEngine.getLayerListener(LightLayer.BLOCK).getDataLayerData(csp);
        var skyLight = lightEngine.getLayerListener(LightLayer.SKY).getDataLayerData(csp);

        VoxelIngestService.rawIngest(
                id,
                section, csp.x(), csp.y(), csp.z(),
                blockLight == null ? null : blockLight.copy(),
                skyLight == null ? null : skyLight.copy());
    }
}

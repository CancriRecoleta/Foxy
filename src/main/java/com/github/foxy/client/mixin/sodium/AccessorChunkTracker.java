package com.github.foxy.client.mixin.sodium;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import me.jellysquid.mods.sodium.client.render.chunk.map.ChunkTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Read-only access to Embeddium's per-chunk status bitmap.
 *
 * <p>Foxy uses this to know which chunk (x, z) cells the vanilla Embeddium terrain
 * pass has already rendered, so the LOD pipeline can skip drawing on top of those.
 * Without this knowledge, Foxy would either over-render (transparent chunks
 * showing voxel mesh through them) or under-render (gaps where Embeddium owns
 * the cell but its data is stale).</p>
 *
 * <p>The targeted field is package-private in Embeddium 0.3 for 1.20.1; an
 * {@link Accessor} mixin against the legacy {@code me.jellysquid.mods.sodium.*}
 * package is the cleanest way to read it without touching upstream's setters.</p>
 */
@Mixin(value = ChunkTracker.class, remap = false)
public interface AccessorChunkTracker {
    @Accessor("chunkStatus")
    Long2IntOpenHashMap foxy$getChunkStatus();
}

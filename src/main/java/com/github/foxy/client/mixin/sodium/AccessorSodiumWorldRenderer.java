package com.github.foxy.client.mixin.sodium;

import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;
import me.jellysquid.mods.sodium.client.render.chunk.RenderSectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Read-only access to {@link SodiumWorldRenderer#renderSectionManager}.
 *
 * <p>Foxy needs the manager to enumerate built sections and to coordinate its own
 * scheduler with Embeddium's chunk build queue. The field is private in Embeddium
 * so we expose it via an {@link Accessor} mixin instead of using reflection on
 * the hot path.</p>
 */
@Mixin(value = SodiumWorldRenderer.class, remap = false)
public interface AccessorSodiumWorldRenderer {
    @Accessor("renderSectionManager")
    RenderSectionManager foxy$getRenderSectionManager();
}

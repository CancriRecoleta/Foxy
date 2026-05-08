package com.github.foxy.client.core.rendering.section;

/**
 * Marker interface tagging a geometry manager as a meshlet consumer.
 *
 * <h2>What it controls</h2>
 * <p>The mesh-generation service inspects whether the active geometry manager
 * implements this interface; when it does, the generator emits its output as
 * meshlets (small fixed-size primitive batches with per-meshlet bounding info)
 * instead of conventional vertex / index buffers. The interface has no methods
 * because the contract is purely structural &mdash; the meshlet format is shared
 * between emitter and consumer via separate APIs.</p>
 *
 * <p>Cleanroom note: identical role to upstream Voxy's marker interface, with
 * proper English javadoc.</p>
 */
public interface IUsesMeshlets {
}

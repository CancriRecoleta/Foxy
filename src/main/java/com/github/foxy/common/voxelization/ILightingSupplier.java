package com.github.foxy.common.voxelization;

/**
 * Per-voxel light supplier injected into
 * {@link WorldConversionFactory#convert(VoxelizedSection, com.github.foxy.common.world.other.Mapper, net.minecraft.world.level.chunk.PalettedContainer, net.minecraft.world.level.chunk.PalettedContainerRO, ILightingSupplier)
 * WorldConversionFactory.convert}.
 *
 * <p>Implementations return one packed light byte per voxel: the low nibble is sky light
 * (0&ndash;15) and the high nibble is block light (0&ndash;15). Coordinates are
 * section-local in the range {@code [0, 16)} on each axis.</p>
 *
 * <p>The conversion path may call this supplier {@code 16*16*16 = 4096} times per chunk
 * section in tight order, so implementations should avoid heap allocations.</p>
 */
@FunctionalInterface
public interface ILightingSupplier {
    /** Returns the packed (block&lt;&lt;4 | sky) light nibble for voxel (x, y, z). */
    byte supply(int x, int y, int z);
}

package com.github.foxy.common.voxelization;

/**
 * Adapts vanilla chunk-section packed-nibble light data into the
 * {@link ILightingSupplier} interface that {@link WorldConversionFactory} expects.
 *
 * <p>The vanilla {@code BlockLight} and {@code SkyLight} chunk-NBT tags are 2048-byte
 * arrays of packed 4-bit light values (4 bits per voxel &times; 4096 voxels per
 * section). This class merges them so the high nibble of the returned byte is block
 * light and the low nibble is sky light &mdash; the layout that
 * {@link com.github.foxy.common.world.other.Mapper Mapper}'s id format expects.</p>
 *
 * <p>If either array is {@code null} (vanilla omits SkyLight in non-overworld dimensions
 * and sometimes BlockLight on fully-bright sections) the corresponding channel reads as
 * 0, matching vanilla's &quot;unstored = dark&quot; semantics.</p>
 */
public final class LightNibbleReader implements ILightingSupplier {
    private final byte[] blockLight;
    private final byte[] skyLight;

    /**
     * @param blockLight 2048-byte packed-nibble block-light array, or {@code null}
     * @param skyLight   2048-byte packed-nibble sky-light array, or {@code null}
     */
    public LightNibbleReader(byte[] blockLight, byte[] skyLight) {
        if (blockLight != null && blockLight.length != 2048) {
            throw new IllegalArgumentException("BlockLight must be 2048 bytes, got " + blockLight.length);
        }
        if (skyLight != null && skyLight.length != 2048) {
            throw new IllegalArgumentException("SkyLight must be 2048 bytes, got " + skyLight.length);
        }
        this.blockLight = blockLight;
        this.skyLight = skyLight;
    }

    /** Linear voxel index, matching the voxelizer's convention {@code (y << 8) | (z << 4) | x}. */
    private static int packedIndex(int x, int y, int z) {
        return (y << 8) | (z << 4) | x;
    }

    /** Reads the 4-bit value at logical index {@code i} from a packed-nibble array. */
    private static int readNibble(byte[] arr, int i) {
        if (arr == null) return 0;
        int b = arr[i >> 1] & 0xFF;
        return (i & 1) == 0 ? (b & 0x0F) : (b >> 4);
    }

    @Override
    public byte supply(int x, int y, int z) {
        int i = packedIndex(x, y, z);
        int sky = readNibble(this.skyLight, i);
        int block = readNibble(this.blockLight, i);
        // Mapper layout: high nibble = block light, low nibble = sky light;
        // see Mipper.getLightId for the slicing on the read side.
        return (byte) ((block << 4) | sky);
    }
}

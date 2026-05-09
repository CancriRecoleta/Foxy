package com.github.foxy.common.world.other;

import static com.github.foxy.common.world.other.Mapper.withLight;

/**
 * Pure-data 8&rarr;1 mip operator for packed mapping ids.
 *
 * <p>Given the eight child voxels of a {@code 2x2x2} cube, returns one packed id that
 * represents the cube at the next-coarser LOD level. Selection rule:</p>
 * <ol>
 *   <li>If any child is non-air, return the child whose
 *       {@code (shapeTier, opacity, corner)} key is the largest. {@code shapeTier}
 *       prefers a full cube over a stair/slab and a stair/slab over a plant/vine,
 *       so distant slabs no longer materialise as full cubes whenever a real full
 *       cube is sitting next to them. {@code opacity} keeps the existing
 *       leaves-vs-air bias. {@code corner} is the final tiebreak and biases toward
 *       the {@code +x +y +z} child &mdash; the corner most likely to be visible
 *       from a typical down-facing camera.</li>
 *   <li>If all eight children are air, average the per-channel light nibbles and return
 *       an air voxel with that combined light. This keeps mipped sky/torchlight smooth
 *       across LOD transitions.</li>
 * </ol>
 *
 * <h2>Selection key bit layout</h2>
 * <pre>
 *   bits 7..8   shapeTier (0=air, 1=plant, 2=partial cube, 3=full cube)
 *   bits 3..6   opacity   (0..15)
 *   bits 0..2   corner    (0..7)
 * </pre>
 * Total 9 bits, kept as a signed {@code int} where {@code -1} flags "all children
 * are air" and triggers the light-averaging branch.
 *
 * <p>The opacity weighting prevents distant trees and similar leaf canopies from
 * disappearing at higher LODs &mdash; see {@link Mapper.StateEntry}'s opacity override.</p>
 */
public final class Mipper {
    private Mipper() {}

    /** Packs (shapeTier, opacity, corner) into the comparable selection key. */
    private static int key(int shapeTier, int opacity, int corner) {
        return (shapeTier << 7) | (opacity << 3) | corner;
    }

    /**
     * Folds eight child voxels into one parent voxel using the rules described in the
     * class javadoc. Parameter naming convention: {@code Iijk} means the child at corner
     * (x=i, y=j, z=k) where each coord is 0 or 1.
     */
    public static long mip(long I000, long I100, long I001, long I101,
                           long I010, long I110, long I011, long I111,
                           Mapper mapper) {
        int max = -1;
        if (!Mapper.isAir(I111)) {
            int id = Mapper.getBlockId(I111);
            max = key(mapper.getBlockStateShapeTier(id), mapper.getBlockStateOpacity(id), 0b111);
        }
        if (!Mapper.isAir(I110)) {
            int id = Mapper.getBlockId(I110);
            max = Math.max(key(mapper.getBlockStateShapeTier(id), mapper.getBlockStateOpacity(id), 0b110), max);
        }
        if (!Mapper.isAir(I011)) {
            int id = Mapper.getBlockId(I011);
            max = Math.max(key(mapper.getBlockStateShapeTier(id), mapper.getBlockStateOpacity(id), 0b011), max);
        }
        if (!Mapper.isAir(I010)) {
            int id = Mapper.getBlockId(I010);
            max = Math.max(key(mapper.getBlockStateShapeTier(id), mapper.getBlockStateOpacity(id), 0b010), max);
        }
        if (!Mapper.isAir(I101)) {
            int id = Mapper.getBlockId(I101);
            max = Math.max(key(mapper.getBlockStateShapeTier(id), mapper.getBlockStateOpacity(id), 0b101), max);
        }
        if (!Mapper.isAir(I100)) {
            int id = Mapper.getBlockId(I100);
            max = Math.max(key(mapper.getBlockStateShapeTier(id), mapper.getBlockStateOpacity(id), 0b100), max);
        }
        if (!Mapper.isAir(I001)) {
            int id = Mapper.getBlockId(I001);
            max = Math.max(key(mapper.getBlockStateShapeTier(id), mapper.getBlockStateOpacity(id), 0b001), max);
        }
        if (!Mapper.isAir(I000)) {
            int id = Mapper.getBlockId(I000);
            max = Math.max(key(mapper.getBlockStateShapeTier(id), mapper.getBlockStateOpacity(id), 0b000), max);
        }

        if (max != -1) {
            return switch (max & 0b111) {
                case 0 -> I000;
                case 1 -> I001;
                case 2 -> I010;
                case 3 -> I011;
                case 4 -> I100;
                case 5 -> I101;
                case 6 -> I110;
                case 7 -> I111;
                default -> throw new IllegalStateException();
            };
        }

        // All air: smear the light values. Block light is the high nibble, sky light the
        // low nibble; we average each independently so torchlight on one side of an air
        // chunk doesn't bleed sky-light values.
        int blockLight = (Mapper.getLightId(I000) & 0xF0) + (Mapper.getLightId(I001) & 0xF0)
                + (Mapper.getLightId(I010) & 0xF0) + (Mapper.getLightId(I011) & 0xF0)
                + (Mapper.getLightId(I100) & 0xF0) + (Mapper.getLightId(I101) & 0xF0)
                + (Mapper.getLightId(I110) & 0xF0) + (Mapper.getLightId(I111) & 0xF0);
        int skyLight = (Mapper.getLightId(I000) & 0x0F) + (Mapper.getLightId(I001) & 0x0F)
                + (Mapper.getLightId(I010) & 0x0F) + (Mapper.getLightId(I011) & 0x0F)
                + (Mapper.getLightId(I100) & 0x0F) + (Mapper.getLightId(I101) & 0x0F)
                + (Mapper.getLightId(I110) & 0x0F) + (Mapper.getLightId(I111) & 0x0F);
        // Round block-light down (mean of 8 values, divide by 8 then mask the nibble);
        // round sky-light up so vertical sky shafts don't darken at coarser LODs.
        blockLight = (blockLight / 8) & 0xF0;
        skyLight = (int) Math.ceil(skyLight / 8.0);
        return withLight(I111, blockLight | skyLight);
    }
}

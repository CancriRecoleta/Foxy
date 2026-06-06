//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.core;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Set;

public enum Direction8 {
    NORTH(new Direction[]{Direction.NORTH}),
    NORTH_EAST(new Direction[]{Direction.NORTH, Direction.EAST}),
    EAST(new Direction[]{Direction.EAST}),
    SOUTH_EAST(new Direction[]{Direction.SOUTH, Direction.EAST}),
    SOUTH(new Direction[]{Direction.SOUTH}),
    SOUTH_WEST(new Direction[]{Direction.SOUTH, Direction.WEST}),
    WEST(new Direction[]{Direction.WEST}),
    NORTH_WEST(new Direction[]{Direction.NORTH, Direction.WEST});

    private final Set<Direction> directions;
    private final Vec3i step;

    private Direction8(Direction... p_122592_) {
        this.directions = Sets.immutableEnumSet(Arrays.asList(p_122592_));
        this.step = new Vec3i(0, 0, 0);
        Direction[] var4 = p_122592_;
        int var5 = p_122592_.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            Direction $$1 = var4[var6];
            this.step.setX(this.step.getX() + $$1.getStepX()).setY(this.step.getY() + $$1.getStepY()).setZ(this.step.getZ() + $$1.getStepZ());
        }

    }

    public Set<Direction> getDirections() {
        return this.directions;
    }

    public int getStepX() {
        return this.step.getX();
    }

    public int getStepZ() {
        return this.step.getZ();
    }
}

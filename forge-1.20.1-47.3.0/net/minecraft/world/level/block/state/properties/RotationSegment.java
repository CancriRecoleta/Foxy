//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block.state.properties;

import java.util.Optional;
import net.minecraft.core.Direction;
import net.minecraft.util.SegmentedAnglePrecision;

public class RotationSegment {
    private static final SegmentedAnglePrecision SEGMENTED_ANGLE16 = new SegmentedAnglePrecision(4);
    private static final int MAX_SEGMENT_INDEX;
    private static final int NORTH_0 = 0;
    private static final int EAST_90 = 4;
    private static final int SOUTH_180 = 8;
    private static final int WEST_270 = 12;

    public RotationSegment() {
    }

    public static int getMaxSegmentIndex() {
        return MAX_SEGMENT_INDEX;
    }

    public static int convertToSegment(Direction p_249634_) {
        return SEGMENTED_ANGLE16.fromDirection(p_249634_);
    }

    public static int convertToSegment(float p_249057_) {
        return SEGMENTED_ANGLE16.fromDegrees(p_249057_);
    }

    public static Optional<Direction> convertToDirection(int p_250978_) {
        Direction var10000;
        switch (p_250978_) {
            case 0 -> var10000 = Direction.NORTH;
            case 4 -> var10000 = Direction.EAST;
            case 8 -> var10000 = Direction.SOUTH;
            case 12 -> var10000 = Direction.WEST;
            default -> var10000 = null;
        }

        Direction $$1 = var10000;
        return Optional.ofNullable($$1);
    }

    public static float convertToDegrees(int p_250653_) {
        return SEGMENTED_ANGLE16.toDegrees(p_250653_);
    }

    static {
        MAX_SEGMENT_INDEX = SEGMENTED_ANGLE16.getMask();
    }
}

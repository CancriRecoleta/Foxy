//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.event.level;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.Nullable;

public abstract class PistonEvent extends BlockEvent {
    private final Direction direction;
    private final PistonMoveType moveType;

    public PistonEvent(Level world, BlockPos pos, Direction direction, PistonMoveType moveType) {
        super(world, pos, world.getBlockState(pos));
        this.direction = direction;
        this.moveType = moveType;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public BlockPos getFaceOffsetPos() {
        return this.getPos().relative(this.direction);
    }

    public PistonMoveType getPistonMoveType() {
        return this.moveType;
    }

    public @Nullable PistonStructureResolver getStructureHelper() {
        return this.getLevel() instanceof Level ? new PistonStructureResolver((Level)this.getLevel(), this.getPos(), this.getDirection(), this.getPistonMoveType().isExtend) : null;
    }

    public static enum PistonMoveType {
        EXTEND(true),
        RETRACT(false);

        public final boolean isExtend;

        private PistonMoveType(boolean isExtend) {
            this.isExtend = isExtend;
        }
    }

    @Cancelable
    public static class Pre extends PistonEvent {
        public Pre(Level world, BlockPos pos, Direction direction, PistonMoveType moveType) {
            super(world, pos, direction, moveType);
        }
    }

    public static class Post extends PistonEvent {
        public Post(Level world, BlockPos pos, Direction direction, PistonMoveType moveType) {
            super(world, pos, direction, moveType);
        }
    }
}

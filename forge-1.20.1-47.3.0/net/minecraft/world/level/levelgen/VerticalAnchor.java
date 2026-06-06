//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.dimension.DimensionType;

public interface VerticalAnchor {
    Codec<VerticalAnchor> CODEC = ExtraCodecs.xor(net.minecraft.world.level.levelgen.VerticalAnchor.Absolute.CODEC, ExtraCodecs.xor(net.minecraft.world.level.levelgen.VerticalAnchor.AboveBottom.CODEC, net.minecraft.world.level.levelgen.VerticalAnchor.BelowTop.CODEC)).xmap(VerticalAnchor::merge, VerticalAnchor::split);
    VerticalAnchor BOTTOM = aboveBottom(0);
    VerticalAnchor TOP = belowTop(0);

    static VerticalAnchor absolute(int p_158923_) {
        return new Absolute(p_158923_);
    }

    static VerticalAnchor aboveBottom(int p_158931_) {
        return new AboveBottom(p_158931_);
    }

    static VerticalAnchor belowTop(int p_158936_) {
        return new BelowTop(p_158936_);
    }

    static VerticalAnchor bottom() {
        return BOTTOM;
    }

    static VerticalAnchor top() {
        return TOP;
    }

    private static VerticalAnchor merge(Either<Absolute, Either<AboveBottom, BelowTop>> p_158925_) {
        return (VerticalAnchor)p_158925_.map(Function.identity(), (p_209698_) -> {
            return (Record)p_209698_.map(Function.identity(), Function.identity());
        });
    }

    private static Either<Absolute, Either<AboveBottom, BelowTop>> split(VerticalAnchor p_158927_) {
        return p_158927_ instanceof Absolute ? Either.left((Absolute)p_158927_) : Either.right(p_158927_ instanceof AboveBottom ? Either.left((AboveBottom)p_158927_) : Either.right((BelowTop)p_158927_));
    }

    int resolveY(WorldGenerationContext var1);

    public static record Absolute(int y) implements VerticalAnchor {
        public static final Codec<Absolute> CODEC;

        public Absolute(int y) {
            this.y = y;
        }

        public int resolveY(WorldGenerationContext p_158949_) {
            return this.y;
        }

        public String toString() {
            return this.y + " absolute";
        }

        public int y() {
            return this.y;
        }

        static {
            CODEC = Codec.intRange(DimensionType.MIN_Y, DimensionType.MAX_Y).fieldOf("absolute").xmap(Absolute::new, Absolute::y).codec();
        }
    }

    public static record AboveBottom(int offset) implements VerticalAnchor {
        public static final Codec<AboveBottom> CODEC;

        public AboveBottom(int offset) {
            this.offset = offset;
        }

        public int resolveY(WorldGenerationContext p_158942_) {
            return p_158942_.getMinGenY() + this.offset;
        }

        public String toString() {
            return this.offset + " above bottom";
        }

        public int offset() {
            return this.offset;
        }

        static {
            CODEC = Codec.intRange(DimensionType.MIN_Y, DimensionType.MAX_Y).fieldOf("above_bottom").xmap(AboveBottom::new, AboveBottom::offset).codec();
        }
    }

    public static record BelowTop(int offset) implements VerticalAnchor {
        public static final Codec<BelowTop> CODEC;

        public BelowTop(int offset) {
            this.offset = offset;
        }

        public int resolveY(WorldGenerationContext p_158956_) {
            return p_158956_.getGenDepth() - 1 + p_158956_.getMinGenY() - this.offset;
        }

        public String toString() {
            return this.offset + " below top";
        }

        public int offset() {
            return this.offset;
        }

        static {
            CODEC = Codec.intRange(DimensionType.MIN_Y, DimensionType.MAX_Y).fieldOf("below_top").xmap(BelowTop::new, BelowTop::offset).codec();
        }
    }
}
